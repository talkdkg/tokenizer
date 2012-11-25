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

import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.vaadin.appfoundation.authentication.util.PasswordUtil;
import org.vaadin.appfoundation.authentication.util.UserUtil;
import org.vaadin.appfoundation.persistence.facade.FacadeFactory;

public class TokenizerServletContextListener implements ServletContextListener {
    ServletContext context;

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("Context Created");
        // Create your translation files and load them into the i18n servlet:
        // File file = new File(path);
        // InternationalizationServlet.loadTranslations(file);
        try {
            // Register facade
            FacadeFactory.registerFacade("tokenizer-ui", true);
            // Set the salt for passwords
            Properties prop = new Properties();
            prop.setProperty("password.salt", "pfew4‰‰#fawef@53424fsd");
            PasswordUtil.setProperties(prop);
            // Set the properties for the UserUtil
            prop.setProperty("password.length.min", "4");
            prop.setProperty("username.length.min", "4");
            UserUtil.setProperties(prop);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {
        context = contextEvent.getServletContext();
        System.out.println("Context Destroyed");
    }
}