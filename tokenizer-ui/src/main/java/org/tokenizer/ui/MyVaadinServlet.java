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
package org.tokenizer.ui;

import org.springframework.context.ApplicationContext;
import org.tokenizer.core.context.ApplicationContextProvider;

public class MyVaadinServlet extends
        org.vaadin.dontpush.server.DontPushOzoneServlet {

    // com.vaadin.terminal.gwt.server.ApplicationServlet {
    private static final long serialVersionUID = 1L;
    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void init(javax.servlet.ServletConfig servletConfig)
            throws javax.servlet.ServletException {
        super.init(servletConfig);
        applicationContext = ApplicationContextProvider.getApplicationContext();
    }

}