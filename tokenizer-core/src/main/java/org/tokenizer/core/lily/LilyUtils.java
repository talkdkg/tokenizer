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
package org.tokenizer.core.lily;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.client.LilyClient;
import org.lilyproject.client.NoServersException;
import org.lilyproject.repository.api.Repository;
import org.lilyproject.repository.api.RepositoryException;
import org.lilyproject.util.zookeeper.ZkConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tokenizer.core.TokenizerConfig;

public class LilyUtils {

	private static final Logger LOG = LoggerFactory.getLogger(LilyUtils.class);

	private static volatile LilyClient lilyClient;
	private static Repository repository;

	public static Repository getRepository() {

		if (repository == null) {
			synchronized (LilyUtils.class) {
				try {
					init();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		return repository;
	}

	private static void init() throws IOException, InterruptedException, KeeperException, ZkConnectException, NoServersException, RepositoryException {
		String connectString = TokenizerConfig.getProperties().getProperty("zookeeper.connectString");
		lilyClient = new LilyClient(connectString, TokenizerConfig.getInt("zookeeper.timeout", 120000));
		repository = lilyClient.getRepository();
	}

	/**
	 * @throws IOException
	 */
	public static synchronized void close() throws IOException {
		if (lilyClient != null)
			lilyClient.close();
		lilyClient = null;
	}

	@Override
	public void finalize() throws Throwable {
		close();
	}

}
