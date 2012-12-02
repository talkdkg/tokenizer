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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TokenizerServletContextListener implements ServletContextListener {
    ServletContext context;

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("Context Created");
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {
        context = contextEvent.getServletContext();
        System.out.println("Context Destroyed");
    }
}