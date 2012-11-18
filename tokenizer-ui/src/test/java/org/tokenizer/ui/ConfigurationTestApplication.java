package org.tokenizer.ui;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.tokenizer.ui.views.TaskConfigurationComponent;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.ApplicationServlet;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Window;

public class ConfigurationTestApplication extends Application {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void init() {
        final Window mainWindow = new Window("UI Test Application");
        TabSheet tabsheet = new TabSheet();
        tabsheet.addTab(new TaskConfigurationComponent(), "Configuration", null);
        mainWindow.addComponent(tabsheet);
        setMainWindow(mainWindow);
    }

    public static void main(String[] args) throws Exception {
        startInEmbeddedJetty();
    }

    public static Server startInEmbeddedJetty() throws Exception {
        Server server = new Server(8888);
        ServletContextHandler handler = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        ServletHolder servletHolder = handler.addServlet(
                ApplicationServlet.class, "/*");
        servletHolder.setInitParameter("application",
                ConfigurationTestApplication.class.getName());
        server.setHandler(handler);
        server.start();
        return server;
    }
}
