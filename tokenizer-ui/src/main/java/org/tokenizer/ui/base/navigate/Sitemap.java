/*
 * Copyright 2013 Tokenizer Inc.
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
package org.tokenizer.ui.base.navigate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.tokenizer.ui.util.BasicForest;


/**
 * Encapsulates the site layout. Individual "virtual pages" are represented by {@link SitemapNode} instances. This map
 * is usually built by an implementation of {@link SitemapProvider}, and is one of the fundamental building blocks of
 * the application, as it maps out pages, URLs and Views.
 * <p>
 * <p>
 * Because of it use as such a fundamental building block, an instance of this class has to be created early in the
 * application start up process. It is better therefore not to introduce dependencies into this class, otherwise the
 * design, and ordering of construction, of Guice modules starts to get complicated.
 * <p>
 * <p>
 * A potential solution for dependencies can be seen in {@link SitemapURIConverter}, which acts as an intermediary
 * between this class and {@link URIFragmentHandler} implementations, thus avoiding the creation of dependencies here.
 * <p>
 * <p>
 * Uses LinkedHashMap to hold the site map itself, to retain insertion order<br>
 * 
 * @author David Sowerby 19 May 2013
 * 
 */
public class Sitemap extends BasicForest<SitemapNode> {

	private int nextNodeId = 0;
	private int errors = 0;
	private final Map<StandardPageKeys, String> standardPages = new HashMap<>();
	private String report;
	// Uses LinkedHashMap to retain insertion order
	private final Map<String, String> redirects = new LinkedHashMap<>();

	public String url(SitemapNode node) {
		StringBuilder buf = new StringBuilder(node.getUrlSegment());
		prependParent(node, buf);
		return buf.toString();
	}

	private void prependParent(SitemapNode node, StringBuilder buf) {
		SitemapNode parentNode = getParent(node);
		if (parentNode != null) {
			buf.insert(0, "/");
			buf.insert(0, parentNode.getUrlSegment());
			prependParent(parentNode, buf);
		}
	}

	/**
	 * creates a SiteMapNode and appends it to the map according to the {@code url} given, then returns it. If a node
	 * already exists at that location it is returned. If there are gaps in the structure, nodes are created to fill
	 * them (the same idea as forcing directory creation on a file path). An empty (not null) url is allowed. This
	 * represents the site base url without any further qualification.
	 * 
	 * @param toUrl
	 * @return
	 */
	public SitemapNode append(String url) {

		if (url.equals("")) {
			SitemapNode node = new SitemapNode();
			node.setUrlSegment(url);
			addNode(node);
			return node;
		}
		SitemapNode node = null;
		String[] segments = StringUtils.split(url, "/");
		List<SitemapNode> nodes = getRoots();
		SitemapNode parentNode = null;
		for (int i = 0; i < segments.length; i++) {
			node = findNodeBySegment(nodes, segments[i], true);
			addChild(parentNode, node);
			nodes = getChildren(node);
			parentNode = node;
		}

		return node;
	}

	private SitemapNode findNodeBySegment(List<SitemapNode> nodes, String segment, boolean createIfAbsent) {
		SitemapNode foundNode = null;
		for (SitemapNode node : nodes) {
			if (node.getUrlSegment().equals(segment)) {
				foundNode = node;
				break;
			}
		}

		if ((foundNode == null) && (createIfAbsent)) {
			foundNode = new SitemapNode();
			foundNode.setUrlSegment(segment);

		}
		return foundNode;
	}

	@Override
	public void addNode(SitemapNode node) {
		if (node.getId() == 0) {
			node.setId(nextNodeId());
		}
		super.addNode(node);
	}

	@Override
	public void addChild(SitemapNode parentNode, SitemapNode childNode) {
		// super allows null parent
		if (parentNode != null) {
			if (parentNode.getId() == 0) {
				parentNode.setId(nextNodeId());
			}
		}
		if (childNode.getId() == 0) {
			childNode.setId(nextNodeId());
		}
		super.addChild(parentNode, childNode);
	}

	public String standardPageURI(StandardPageKeys pageKey) {
		return standardPages.get(pageKey);
	}

	private int nextNodeId() {
		nextNodeId++;
		return nextNodeId;
	}

	public Map<StandardPageKeys, String> getStandardPages() {
		return standardPages;
	}

	public boolean hasErrors() {
		return errors > 0;
	}

	public int getErrors() {
		return errors;
	}

	public void error() {
		errors++;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getReport() {
		return report;
	}

	/**
	 * If the {@code page} has been redirected, return the page it has been redirected to, otherwise, just return
	 * {@code page}
	 * 
	 * @param page
	 * @return
	 */
	public String getRedirectFor(String page) {
		return redirects.get(page);
	}

	public Map<String, String> getRedirects() {
		return redirects;
	}

	/**
	 * Returns a list of {@link SitemapNode} matching the {@code segments} provided. If there is an incomplete match (a
	 * segment cannot be found) then:
	 * <ol>
	 * <li>if {@code allowPartialPath} is true a list of nodes is returned correct to the longest path possible.
	 * <li>if {@code allowPartialPath} is false an empty list is returned
	 * 
	 * @param segments
	 * @return
	 */

	public List<SitemapNode> nodeChainForSegments(List<String> segments, boolean allowPartialPath) {
		List<SitemapNode> nodeChain = new ArrayList<>();
		int i = 0;
		String currentSegment = null;
		List<SitemapNode> nodes = getRoots();
		boolean segmentNotFound = false;
		SitemapNode node = null;
		while ((i < segments.size()) && (!segmentNotFound)) {
			currentSegment = segments.get(i);
			node = findNodeBySegment(nodes, currentSegment, false);
			if (node != null) {
				nodeChain.add(node);
				nodes = getChildren(node);
				i++;
			} else {
				segmentNotFound = true;
			}

		}
		if (segmentNotFound && !allowPartialPath) {
			nodeChain.clear();
		}
		return nodeChain;
	}

	public List<String> urls() {
		List<String> list = new ArrayList<>();
		for (SitemapNode node : getAllNodes()) {
			list.add(url(node));
		}
		return list;
	}
}
