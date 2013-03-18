/*
 * Copyright 2007-2012 Tokenizer Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tokenizer.executor.engine;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import org.apache.nutch.net.URLFilter;
import org.apache.nutch.urlfilter.automaton.AutomatonURLFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.crawler.db.CrawlerRepository;
import org.tokenizer.crawler.db.UrlRecord;
import org.tokenizer.executor.model.api.WritableExecutorModel;
import org.tokenizer.executor.model.configuration.ClassicRobotTaskConfiguration;
import org.tokenizer.executor.model.configuration.TaskConfiguration;
import org.tokenizer.util.zookeeper.ZooKeeperItf;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;

import crawlercommons.fetcher.FetchedResult;
import crawlercommons.fetcher.http.BaseHttpFetcher;
import crawlercommons.fetcher.http.BaseHttpFetcher.RedirectMode;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;

public class ClassicRobotTask extends AbstractTask {

	private static final Logger LOG = LoggerFactory
			.getLogger(ClassicRobotTask.class);
	private final SimpleHttpFetcher httpClient;
	private final BaseHttpFetcher robotFetcher;
	BaseRobotRules robotRules = null;
	private ClassicRobotTaskConfiguration taskConfiguration;
	private static final int DEFAULT_MAX_THREADS = 1024;
	private URLFilter urlFilter;

	public URLFilter getUrlFilter() {
		return urlFilter;
	}

	public ClassicRobotTask(final UUID uuid, final String friendlyName,
			final ZooKeeperItf zk, final TaskConfiguration taskConfiguration,
			final CrawlerRepository crawlerRepository,
			final WritableExecutorModel model, final HostLocker hostLocker) {
		super(uuid, friendlyName, zk, crawlerRepository, model, hostLocker);
		this.taskConfiguration = (ClassicRobotTaskConfiguration) taskConfiguration;
		UserAgent userAgent = new UserAgent(
				this.taskConfiguration.getAgentName(),
				this.taskConfiguration.getEmailAddress(),
				this.taskConfiguration.getWebAddress(),
				UserAgent.DEFAULT_BROWSER_VERSION, "2.1");
		LOG.warn("userAgent: {}", userAgent.getUserAgentString());
		// this.httpClient = new SimpleHttpFetcher(DEFAULT_MAX_THREADS,
		// FetcherUtils.USER_AGENT);
		this.httpClient = new SimpleHttpFetcher(DEFAULT_MAX_THREADS, userAgent);
		httpClient.setSocketTimeout(30000);
		httpClient.setConnectionTimeout(30000);
		httpClient.setRedirectMode(RedirectMode.FOLLOW_NONE);
		httpClient.setDefaultMaxContentSize(1024 * 1024);
		robotFetcher = RobotUtils.createFetcher(userAgent, 1);
		robotFetcher.setDefaultMaxContentSize(1024 * 1024);
		// this validation is needed only initally; to migrate old version of
		// ZK-serialized objects to new version with default config
		if (this.taskConfiguration.getUrlFilterConfig() == null) {
			this.taskConfiguration
					.setUrlFilterConfig(ClassicRobotTaskConfiguration.DEFAULT_URL_FILTER);
		}
		Reader reader = new StringReader(
				this.taskConfiguration.getUrlFilterConfig());
		try {
			this.urlFilter = new AutomatonURLFilter(reader);
		} catch (IllegalArgumentException e) {
			LOG.error("", e);
		} catch (IOException e) {
			LOG.error("", e);
		}
		/*
		 * Thread filterThread = new Thread(uuid + "-" +
		 * taskConfiguration.getImplementationName() + "-" +
		 * taskConfiguration.getName() + "-ULRFilterThread") {
		 * 
		 * @Override public void run() { crawlerRepository.filter(
		 * ((ClassicRobotTaskConfiguration) taskConfiguration) .getHost(),
		 * urlFilter); } }; filterThread.setDaemon(true); filterThread.start();
		 */
		LOG.debug("Instance created");
	}

	/**
	 * Real job is done here
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	@Override
	protected void process() throws InterruptedException, ConnectionException {
		refreshRobotRules();
		UrlRecord home = new UrlRecord("http://" + taskConfiguration.getHost()
				+ "/");
		crawlerRepository.insertIfNotExists(home);
		FetchedResult fetchedResult;

		LOG.debug("Trying Home: {}", home);
		fetchedResult = PersistenceUtils.fetch(home, crawlerRepository,
				robotRules, metricsCache, httpClient,
				taskConfiguration.getHost(), urlFilter);
		LOG.trace("Fetching Home: {} {}", home, fetchedResult);
		if (fetchedResult == null) {
			LOG.error("Can't retrieve homepage! {}", home.getUrl());
		}

		int fetchAttemptCounter = 0;
		int maxResults = 1000;
		List<UrlRecord> urlRecords = crawlerRepository
				.listUrlRecordsByFetchAttemptCounter(
						taskConfiguration.getHost(), fetchAttemptCounter,
						maxResults);
		for (UrlRecord urlRecord : urlRecords) {
			LOG.debug("Trying URL: {}", urlRecord);
			fetchedResult = PersistenceUtils.fetch(urlRecord,
					crawlerRepository, robotRules, metricsCache, httpClient,
					taskConfiguration.getHost(), urlFilter);
			LOG.trace("Fetching Result: {} {}", urlRecord, fetchedResult);
		}
		if (urlRecords == null || urlRecords.size() == 0) {
			// to prevent spin loop in case if collection is empty:
			LOG.warn("no URLs found with httpResponseCode == 0; sleeping 1 hour...");
			Thread.sleep(1 * 3600 * 1000L);
			// refresh homepage
			// fetchedResult = PersistenceUtils.fetch(home, crawlerRepository,
			// robotRules, metricsCache, httpClient,
			// taskConfiguration.getHost(), urlFilter);
			// LOG.warn("homepage fetched: {}, response: {}", home.getUrl(),
			// fetchedResult.getHttpStatus());
			// if (fetchedResult == null) {
			// LOG.error("Can't retrieve homepage! {}", home.getUrl());
			// }
		}
	}

	/**
	 * Should be called few times a day
	 * 
	 * @return
	 */
	private synchronized boolean refreshRobotRules() {
		BaseRobotsParser parser = new SimpleRobotRulesParser();
		try {
			robotRules = RobotUtils.getRobotRules(robotFetcher, parser,
					new URL("http://" + taskConfiguration.getHost()
							+ "/robots.txt"));
		} catch (MalformedURLException e) {
			LOG.error("", e);
			return false;
		}
		return true;
	}

	@Override
	public TaskConfiguration getTaskConfiguration() {
		return this.taskConfiguration;
	}

	@Override
	public void setTaskConfiguration(final TaskConfiguration taskConfiguration) {
		this.taskConfiguration = (ClassicRobotTaskConfiguration) taskConfiguration;
	}

	public static void main(final String[] args) throws Exception {

		String url = "http://reviews.cnet.com/smartphones/htc-droid-dna-verizon/4505-6452_7-35536642.html";

		UserAgent userAgent = new UserAgent("Googlebot", "", "",
				UserAgent.DEFAULT_BROWSER_VERSION, "2.1");
		SimpleHttpFetcher httpClient = new SimpleHttpFetcher(
				DEFAULT_MAX_THREADS, userAgent);
		httpClient.setSocketTimeout(30000);
		httpClient.setConnectionTimeout(30000);
		httpClient.setRedirectMode(RedirectMode.FOLLOW_NONE);
		httpClient.setDefaultMaxContentSize(1024 * 1024);

		FetchedResult fetchedResult = httpClient.get(url, null);

		System.out.println(fetchedResult.toString());

	}

}
