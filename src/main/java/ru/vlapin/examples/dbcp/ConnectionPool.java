package ru.vlapin.examples.dbcp;

import lombok.experimental.Delegate;
import lombok.extern.log4j.Log4j2;
import lombok.val;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

@Log4j2
public class ConnectionPool implements Supplier<Connection>, Closeable {

    private BlockingQueue<Connection> connectionQueue;

    ConnectionPool() {
        Properties bundle = DBParameter.getLoad("./src/test/resources/db.properties");

        assert bundle.containsKey(DBParameter.DB_USER);
        assert bundle.containsKey(DBParameter.DB_PASSWORD);
        assert bundle.size() >= 4 && bundle.size() <= 5;

        String driverName = (String) bundle.remove(DBParameter.DB_DRIVER);
        String url = (String) bundle.remove(DBParameter.DB_URL);

        String size = (String) bundle.remove(DBParameter.DB_POLL_SIZE);
        int poolSize = size == null ? 5 : Integer.parseInt(size);

        try {
            Class.forName(driverName);
            connectionQueue = new ArrayBlockingQueue<>(poolSize);
            for (int i = 0; i < poolSize; i++) {
                val pooledConnection = new PooledConnection(
                        DriverManager.getConnection(url, bundle));
                connectionQueue.add(pooledConnection);
            }
        } catch (SQLException e) {
            throw new ConnectionPoolException("SQLException in ConnectionPool", e);
        } catch (ClassNotFoundException e) {
            throw new ConnectionPoolException("Can't find database driver class", e);
        }
    }

    @Override
    public void close() {
        try {
            closeConnectionsQueue(connectionQueue);
        } catch (SQLException e) {
             log.error("Error closing the connection.", e);
        }
    }

    /**
     * @throws ConnectionPoolException if Interrupted
     */
    public Connection takeConnection() {
        Connection connection;
        try {
            connection = connectionQueue.take();
        } catch (InterruptedException e) {
            throw new ConnectionPoolException(
                    "Error connecting to the data source.", e);
        }
        return connection;
    }

    /**
     * Alias for {@link #takeConnection()}
     */
    @Override
    public Connection get() {
        return takeConnection();
    }

    private void closeConnectionsQueue(BlockingQueue<Connection> queue)
            throws SQLException {
        Connection connection;
        while ((connection = queue.poll()) != null) {
            if (!connection.getAutoCommit()) {
                connection.commit();
            }
            ((PooledConnection) connection).reallyClose();
        }
    }

    private class PooledConnection implements Connection {
        @Delegate(excludes = Closeable.class)
        private Connection connection;

        public PooledConnection(Connection c) throws SQLException {
            connection = c;
            connection.setAutoCommit(true);
        }

        public void reallyClose() throws SQLException {
            connection.close();
        }

        @Override
        public void close() throws SQLException {
            if (connection.isClosed()) {
                throw new SQLException("Attempting to close closed connection.");
            }
            if (connection.isReadOnly()) {
                connection.setReadOnly(false);
            }
            if (!connectionQueue.offer(this)) {
                throw new SQLException("Error allocating connection in the pool.");
            }
        }
    }
}