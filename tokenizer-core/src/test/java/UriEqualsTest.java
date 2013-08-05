/*
 * TOKENIZER CONFIDENTIAL 
 * 
 * Copyright © 2013 Tokenizer Inc. All rights reserved. 
 * 
 * NOTICE: All information contained herein is, and remains the property of Tokenizer Inc. 
 * The intellectual and technical concepts contained herein are proprietary to Tokenizer Inc. 
 * and may be covered by U.S. and Foreign Patents, patents in process, and are 
 * protected by trade secret or copyright law. 
 * 
 * Dissemination of this information or reproduction of this material is strictly 
 * forbidden unless prior written permission is obtained from Tokenizer Inc.
 */
import java.net.URI;
import java.net.URISyntaxException;


public class UriEqualsTest {
  public static void main(String[] args) throws URISyntaxException {
    
    String url = "http://www.tokenizer.ca";
    
    URI uri1 = new URI(url);
    URI uri2 = new URI(url+":80");
    
    
    System.out.println(uri1.equals(uri2));
    
    
  }
}
