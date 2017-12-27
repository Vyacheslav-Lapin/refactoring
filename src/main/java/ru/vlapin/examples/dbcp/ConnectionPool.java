package ru.vlapin.examples.dbcp;

import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import lombok.val;

import java.io.Closeable;
import java.io.FileInputStream;
import java.sql.*;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionPool {

    private BlockingQueue<Connection> connectionQueue;

    private Properties bundle = new Properties() {
        @SneakyThrows
        Properties load(String address) {
            try (FileInputStream fileInputStream = new FileInputStream(address)) {
                load(fileInputStream);
            }
            return this;
        }
    }.load("./src/test/resources/db.properties");

    ConnectionPool() {
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

    public void dispose() {
        clearConnectionQueue();
    }

    private void clearConnectionQueue() {
        try {
            closeConnectionsQueue(connectionQueue);
        } catch (SQLException e) {
            // logger.log(Level.ERROR, "Error closing the connection.", e);
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

    public void closeConnection(Connection con, Statement st, ResultSet rs) {
        try {
            con.close();
        } catch (SQLException e) {
            // logger.log(Level.ERROR, "Connection isn't return to the pool.");
        }
        try {
            rs.close();
        } catch (SQLException e) {
            // logger.log(Level.ERROR, "ResultSet isn't closed.");
        }
        try {
            st.close();
        } catch (SQLException e) {
            // logger.log(Level.ERROR, "Statement isn't closed.");
        }
    }

    public void closeConnection(Connection con, Statement st) {
        try {
            con.close();
        } catch (SQLException e) {
            // logger.log(Level.ERROR, "Connection isn't return to the pool.");
        }
        try {
            st.close();
        } catch (SQLException e) {
            // logger.log(Level.ERROR, "Statement isn't closed.");
        }
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
            this.connection = c;
            this.connection.setAutoCommit(true);
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