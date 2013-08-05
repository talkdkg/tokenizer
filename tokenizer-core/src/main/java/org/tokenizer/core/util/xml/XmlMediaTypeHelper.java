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
package org.tokenizer.core.util.xml;

import java.util.HashSet;
import java.util.Set;

public class XmlMediaTypeHelper {
    private static Set xmlMediaTypes;
    static {
        xmlMediaTypes = new HashSet();
        xmlMediaTypes.add("text/xml");
        xmlMediaTypes.add("application/xml");
    }

    /**
     * Returns true if the media type is recognized as the media type of some
     * XML format.
     */
    public static boolean isXmlMediaType(String mediaType) {
        return xmlMediaTypes.contains(mediaType) || mediaType.endsWith("+xml");
    }
}
