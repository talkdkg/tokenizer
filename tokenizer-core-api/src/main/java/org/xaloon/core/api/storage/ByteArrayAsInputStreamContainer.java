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

/**
 * @author vytautas r.
 */
public class ByteArrayAsInputStreamContainer extends AbstractInputStreamContainer {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private byte[] input;

    /**
     * Construct.
     * 
     * @param input
     */
    public ByteArrayAsInputStreamContainer(byte[] input) {
        this(input, new InputStreamContainerOptions());
    }

    /**
     * Construct.
     * 
     * @param input
     * @param options
     */
    public ByteArrayAsInputStreamContainer(byte[] input, InputStreamContainerOptions options) {
        super(options);
        this.input = input;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public byte[] asByteArray() {
        return input;
    }
}
