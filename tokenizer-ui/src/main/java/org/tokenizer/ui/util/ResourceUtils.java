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
package org.tokenizer.ui.util;

import java.io.File;

import com.vaadin.server.VaadinService;

public class ResourceUtils {
    /**
     * Returns the base directory path for the application if there VaadinService is running, or the current directory
     * if not (the latter case is mainly for testing)
     * 
     * @return
     */
    public static String applicationBasePath() {
        if (VaadinService.getCurrent() != null) {
            return VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
        } else {
            return new File(".").getAbsolutePath();
        }

    }

    /**
     * Returns the base directory for the application if there VaadinService is running, or the current directory if not
     * (the latter case is mainly for testing)
     * 
     * @return
     */
    public static File applicationBaseDirectory() {
        return new File(applicationBasePath());
    }
}
