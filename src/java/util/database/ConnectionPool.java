package cgwap.util.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import cgwap.util.Config;
import cgwap.util.exception_handler.ApplicationException;

/**
 * Handles the database connections. The class uses a thread to produce database connections. It
 * also implements the Singleton pattern to make sure the all clients use the same ConnectionPool.
 * 
 */
public class ConnectionPool {

    private static final Logger       LOGGER   = Logger.getLogger(ConnectionPool.class.getName());

    private static ConnectionPool     instance = new ConnectionPool();

    private BlockingQueue<Connection> connectionsFree;
    private BlockingQueue<Connection> connectionsInUse;

    private ConnectionProducer        connectionProducer;
    private Thread                    creatorThread;

    /**
     * Initialize Connection Pool variables.
     */
    private ConnectionPool() {
        this.connectionsFree = new LinkedBlockingQueue<Connection>();
        this.connectionsInUse = new LinkedBlockingQueue<Connection>();
    }

    /**
     * Implementation of the Singleton pattern.
     * 
     * @return the instance of the ConnectionPool
     */
    public static ConnectionPool getInstance() {
        return instance;
    }

    /**
     * Returns a connection from the pool by using the Singleton pattern to get the current instance
     * of ConnectionPool.
     * 
     * @return valid database connection
     * @throws ApplicationException Thrown when no connection available.
     */
    public static Connection getConnection() throws ApplicationException {
        return getInstance().popConnection();
    }

    /**
     * Collects used connections and adds them back to pool.
     * 
     * @param connection - connection to release
     * @throws ApplicationException 
     */
    public static void releaseConnection(Connection connection) throws ApplicationException {
        getInstance().addConnection(connection);
    }

    /**
     * Starts producer thread.
     */
    public static void startup() {
        getInstance().startProducerThread();
    }

    /**
     * Does destruction work before bean will be destroyed. Closes all
     * connections and stops threads.
     */
    public static void shutdown() {
        ConnectionPool pool = getInstance();
        pool.stopProducerThread();
        pool.closeAllConnections();
    }

    /**
     * Returns a connection from the pool.
     * 
     * @return valid database connection
     * @throws ApplicationException Thrown when no connection could be returned.
     */
    private Connection popConnection() throws ApplicationException {
        Connection connection = null;

        try {
            connection = this.connectionsFree.poll(Config.DB_TIMEOUT, TimeUnit.SECONDS);

            if (connection != null && connection.isValid(0)) {
                this.connectionsInUse.add(connection);
                return connection;
            }
            
            return popConnection();

        } catch (InterruptedException e) {
            throw new ApplicationException("Interrupted on getting connection.", e);
        } catch (SQLException e) {
            throw new ApplicationException("Failed to check connection.", e);
        }
    }

    /**
     * Collects used connections and adds them back to pool.
     * 
     * @param connection - connection to release
     */
    private void addConnection(Connection connection) {
        if (connection != null) {
            this.connectionsInUse.remove(connection);
            
            try {
                if (connection.isValid(0)) {
                    this.connectionsFree.add(connection);
                }
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to check connection.", e);
            }
        }
    }

    private void closeAllConnections() {
        LinkedList<Connection> connectionsToClose = new LinkedList<Connection>();
        this.connectionsFree.drainTo(connectionsToClose);
        this.connectionsInUse.drainTo(connectionsToClose);

        for (Connection connection : connectionsToClose) {
            this.closeConnection(connection);
        }
    }

    private void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Failed to close connection.", e);
        }
    }

    private void startProducerThread() {
        LOGGER.info("Start ConnectionProducer Thread");
        this.connectionProducer = new ConnectionProducer(this.connectionsFree, this.connectionsInUse);
        this.creatorThread = new Thread(this.connectionProducer);
        this.creatorThread.start();
    }

    private void stopProducerThread() {
        LOGGER.info("Stop ConnectionProducer Thread.");
        try {
            this.connectionProducer.terminate();
            this.creatorThread.join();
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Failed to stop ConnectionProducer Thread.", e);
        }
    }

}
