package cgwap.util.shutdown_hook;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * This class is used to call the SystemStartupShutdown methods at the time
 * Tomcat starts or stops.
 * 
 * 
 */
public class ApplicationServletContextListener implements ServletContextListener {

    /**
     * Called at the time Tomcat starts.
     */
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        SystemStartupShutdown.startup();
    }

    /**
     * Called at the time Tomcat stops.
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        SystemStartupShutdown.shutdown();
    }
}
