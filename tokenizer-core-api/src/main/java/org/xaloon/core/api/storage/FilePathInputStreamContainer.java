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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * File system file as input stream
 * 
 * @author vytautas.r
 */
public class FilePathInputStreamContainer extends AbstractInputStreamContainer {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private final String absoluteFilePath;

    /**
     * Construct.
     * 
     * @param absoluteFilePath
     * 
     * @param options
     */
    public FilePathInputStreamContainer(String absoluteFilePath) {
        super(new InputStreamContainerOptions());
        this.absoluteFilePath = absoluteFilePath;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (StringUtils.isEmpty(absoluteFilePath)) {
            throw new IllegalArgumentException("file path was not provided.");
        }
        File file = new File(absoluteFilePath);
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("File does not exist: %s", absoluteFilePath));
        }
        return new FileInputStream(file);
    }

    @Override
    public void close() {
        FileUtils.deleteQuietly(new File(absoluteFilePath));
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}
