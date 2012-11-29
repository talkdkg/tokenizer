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

import java.io.File;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.kauriproject.runtime.KauriRuntime;
import org.kauriproject.runtime.KauriRuntimeSettings;
import org.kauriproject.runtime.configuration.ConfManager;
import org.kauriproject.runtime.configuration.ConfManagerImpl;
import org.kauriproject.runtime.repository.ArtifactRepository;
import org.kauriproject.runtime.repository.Maven2StyleArtifactRepository;
import org.springframework.context.ApplicationContext;
import org.tokenizer.core.context.ApplicationContextProvider;
import org.w3c.dom.Document;

import com.vaadin.Application;

public class MyVaadinServlet extends
        org.vaadin.dontpush.server.DontPushOzoneServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_CONF_DIR = "/java/git/tokenizer/conf";
    private KauriRuntime runtime;
    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void init(javax.servlet.ServletConfig servletConfig)
            throws javax.servlet.ServletException {
        super.init(servletConfig);
        ArtifactRepository artifactRepository;
        File maven2Repository = findLocalMavenRepository();
        System.out.println("Using local Maven repository at "
                + maven2Repository.getAbsolutePath());
        artifactRepository = new Maven2StyleArtifactRepository(maven2Repository);
        List<File> confDirs = new ArrayList<File>();
        File confDir = new File(DEFAULT_CONF_DIR).getAbsoluteFile();
        confDirs.add(confDir);
        try {
            KauriRuntimeSettings settings = new KauriRuntimeSettings();
            ConfManager confManager = new ConfManagerImpl(confDirs);
            settings.setConfManager(confManager);
            settings.setRepository(artifactRepository);
            // Disable the servlet connectors: the Servlet container is our
            // server.
            // (assuming we're only interested in HTTP)
            settings.setDisableServerConnectors(true);
            runtime = new KauriRuntime(settings);
            runtime.start();
            applicationContext = ApplicationContextProvider
                    .getApplicationContext();
        } catch (Exception e) {
            throw new KauriServletInitializationException(
                    "Problem creating the Kauri Runtime.", e);
        }
    }

    @Override
    protected Application getNewApplication(HttpServletRequest request)
            throws ServletException {
        MyVaadinApplication app = (MyVaadinApplication) super
                .getNewApplication(request);
        Principal principal = request.getUserPrincipal();
        // TODO: uncomment to enable security
        // if (principal == null)
        // throw new ServletException("Access denied");
        // TODO: or uncomment this:
        // if (request.isUserInRole(SecurityAuthorities.AUTHENTICATED_USER)) {
        // // app.setUserRole(SecurityAuthorities.AUTHENTICATED_USER);
        // } else
        // throw new ServletException("Access denied");
        app.setUser(principal);
        app.setLogoutURL(request.getContextPath() + "/logout.jsp");
        return app;
    }

    // This method is duplicated in RuntimeCliLauncher, so if you modify it
    // here, it is likely useful to copy you modifications there too.
    private File findLocalMavenRepository() {
        String homeDir = System.getProperty("user.home");
        File mavenSettingsFile = new File(homeDir + "/.m2/sett ings.xml");
        if (mavenSettingsFile.exists()) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document document = db.parse(mavenSettingsFile);
                XPath xpath = XPathFactory.newInstance().newXPath();
                org.kauriproject.util.xml.SimpleNamespaceContext nc = new org.kauriproject.util.xml.SimpleNamespaceContext();
                nc.addPrefix("m", "http://maven.apache.org/POM/4.0.0");
                xpath.setNamespaceContext(nc);
                String localRepository = xpath.evaluate(
                        "string(/m:settings/m:localRepository)", document);
                if (localRepository != null && localRepository.length() > 0)
                    return new File(localRepository);
                // Usage of the POM namespace in settings.xml is optional, so
                // also try
                // without namespace
                localRepository = xpath.evaluate(
                        "string(/settings/localRepository)", document);
                if (localRepository != null && localRepository.length() > 0)
                    return new File(localRepository);
            } catch (Exception e) {
                System.err.println("Error reading Maven settings file at "
                        + mavenSettingsFile.getAbsolutePath());
                e.printStackTrace();
                System.exit(1);
            }
        }
        return new File(homeDir + "/.m2/repository");
    }

    @Override
    public void destroy() {
        super.destroy();
        runtime.stop();
    }
}
