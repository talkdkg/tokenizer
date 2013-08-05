/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
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
