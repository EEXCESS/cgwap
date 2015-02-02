package cgwap.util.shutdown_hook;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import cgwap.data_access.CreateScheme;
import cgwap.util.Config;
import cgwap.util.database.ConnectionPool;
import cgwap.util.exception_handler.ApplicationException;

/**
 * This class contains all methods which have to be called at system startup and
 * shutdown. It's
 * methods are called by a ShutdownHook or by a ServletContextListener.
 * 
 * 
 */
public class SystemStartupShutdown {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Performs all methods used by system's startup.
     */
    public static void startup() {
        // Config
        Config.init();

        // Logging

        // Database
        ConnectionPool.startup();
         try {
         CreateScheme.createSchemes();
         } catch (ApplicationException e) {
         e.printStackTrace();
         }

        // shutdown hook
        Thread shutdownHook = new ShutdownHook();
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(shutdownHook);

        // start Maintenance Thread
        // startWatcher();
    }

    /**
     * Performs all methods used by system's shutdown.
     */
    public static void shutdown() {
        ConnectionPool.shutdown();

        scheduler.shutdown();
    }

    private static class ShutdownHook extends Thread {

        @Override
        public void run() {
            SystemStartupShutdown.shutdown();
        }
    }

}
