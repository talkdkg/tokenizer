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
package org.xaloon.core.api.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @author vytautas r.
 */
public interface InputStreamContainer extends Serializable {
    /**
     * @return input stream
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Close input stream after complete
     */
    void close();

    /**
     * @return true if there is no input stream found
     */
    boolean isEmpty();

    InputStreamContainerOptions getOptions();

    void setOptions(InputStreamContainerOptions options);

    /**
     * Returns input stream as byte array
     * 
     * @return input stream converted into byte array
     */
    byte[] asByteArray();
}
