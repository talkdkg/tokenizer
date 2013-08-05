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
package org.xaloon.core.api.image;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

/**
 * @author vytautas r.
 */
public interface ImageResizer extends Serializable {
    /**
     * @param is
     * @param newWidth
     * @param newHeight
     * @param isFixed
     * @return resized image as input stream
     * @throws IOException
     */
    InputStream resize(InputStream is, int newWidth, int newHeight, boolean isFixed) throws IOException;

    /**
     * @param url
     * @param newWidth
     * @param newHeight
     * @param isFixed
     * @return resized image as input stream
     * @throws IOException
     */
    InputStream resize(URL url, int newWidth, int newHeight, boolean isFixed) throws IOException;
}
