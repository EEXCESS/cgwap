package cgwap.util.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import cgwap.util.Config;
import cgwap.util.exception_handler.ApplicationException;

/**
 * Thread providing database connection in the background. The thread is controlled by the
 * ConnectionPool, it is possible to ask for more connections and to stop the thread for system
 * shutdown.
 * 
 * 
 */
public class ConnectionProducer implements Runnable {

    private static final Logger             LOGGER = Logger.getLogger(ConnectionProducer.class.getName());

    private final BlockingQueue<Connection> queue;
    private final BlockingQueue<Connection> queueInUse;
    private volatile boolean                running;

    /**
     * Constructor for a new ConnectionProducer.
     * 
     * @param queue - reference on the held available connections of the connection pool
     * @param queueInUse - reference on the held connections which are currently in use
     */
    public ConnectionProducer(BlockingQueue<Connection> queue, BlockingQueue<Connection> queueInUse) {
        this.queue = queue;
        this.queueInUse = queueInUse;
        this.running = true;
    }

    /**
     * This method keeps track of the current number of Connections in the queue and creates new
     * connections.
     */
    @Override
    public void run() {
        try {
            this.loadDriver();

            while (this.running) {
                if (queue.size() < Config.MIN_CONNECTIONS && queue.size() + queueInUse.size() < Config.MAX_CONNECTIONS) {
                    Connection conn = produce();

                    if (conn != null) {
                        this.queue.put(conn);
                    }
                } 
                Thread.sleep(500);
            }

        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupted while adding connection", e);
        } catch (ApplicationException e) {
            LOGGER.log(Level.WARNING, "Failed to produce Connection", e);
        }
    }

    /**
     * Safely terminates the thread.
     */
    public void terminate() {
        this.running = false;
    }

    private void loadDriver() throws ApplicationException {
        LOGGER.fine("Loading JDBC Driver");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ApplicationException("Failed to load Driver.", e);
        }
    }

    private Connection produce() throws ApplicationException {
        LOGGER.fine("Try to open Database Connection.");
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:postgresql://"
                    + Config.DB_HOST + ":" + Config.DB_PORT + "/" + Config.DB_NAME, Config.DB_USER, Config.DB_PASS);
        } catch (SQLException e) {
            throw new ApplicationException("Failed to create new Connection", e);
        }

        return connection;
    }
}
