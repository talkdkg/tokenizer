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
package org.tokenizer.core.http;

//import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.fetcher.http.UserAgent;

public class FetcherUtils {
  
  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(FetcherUtils.class);

  /*
  private static Configuration conf = new Configuration();
  
  static {
    conf.addResource("fetcher-default.xml");
    conf.addResource("fetcher.xml");
  }
  
  public static final UserAgent USER_AGENT = new UserAgent(conf.get(
      "agent.name", ""), conf.get("agent.email", ""),
      conf.get("agent.url", ""), UserAgent.DEFAULT_BROWSER_VERSION, "1.0");
  *
  */
  
  public static final UserAgent USER_AGENT = new UserAgent("Tokenizer", "info@tokenizer.ca", "http://www.tokenizer.ca", UserAgent.DEFAULT_BROWSER_VERSION, "1.0");

  
}
