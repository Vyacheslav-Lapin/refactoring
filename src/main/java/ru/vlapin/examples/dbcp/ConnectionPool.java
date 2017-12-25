package ru.vlapin.examples.dbcp;

import java.sql.*;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

public class ConnectionPool {
    private BlockingQueue<Connection> connectionQueue;
    private BlockingQueue<Connection> givenAwayConQueue;

    private String driverName;
    private String url;
    private String user;
    private String password;
    private int poolSize;

    private ConnectionPool() {
        DBResourceManager dbResourseManager = DBResourceManager.getInstance();
        this.driverName = dbResourseManager.getValue(DBParameter.DB_DRIVER);
        this.url = dbResourseManager.getValue(DBParameter.DB_URL);
        this.user = dbResourseManager.getValue(DBParameter.DB_USER);

        this.password = dbResourseManager.getValue(DBParameter.DB_PASSWORD);

        try {
            this.poolSize = Integer.parseInt(dbResourseManager
                    .getValue(DBParameter.DB_POLL_SIZE));
        } catch (NumberFormatException e) {
            poolSize = 5;
        }
    }

    public void initPoolData() throws ConnectionPoolException {
        Locale.setDefault(Locale.ENGLISH);

        try {
            Class.forName(driverName);
            givenAwayConQueue = new ArrayBlockingQueue<Connection>(poolSize);
            connectionQueue = new ArrayBlockingQueue<Connection>(poolSize);
            for (int i = 0; i < poolSize; i++) {
                PooledConnection pooledConnection;
                try (Connection connection = DriverManager.getConnection(url, user, password)) {
                    pooledConnection = new PooledConnection(
                            connection);
                }
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
            closeConnectionsQueue(givenAwayConQueue);
            closeConnectionsQueue(connectionQueue);
        } catch (SQLException e) {
            // logger.log(Level.ERROR, "Error closing the connection.", e);
        }
    }

    public Connection takeConnection() throws ConnectionPoolException {
        Connection connection = null;
        try {
            connection = connectionQueue.take();
            givenAwayConQueue.add(connection);
        } catch (InterruptedException e) {
            throw new ConnectionPoolException(
                    "Error connecting to the data source.", e);
        }
        return connection;
    }

    public void closeConnection(Connection con, Statement st, ResultSet rs) {
        try {	con.close();
        } catch (SQLException e) {
            // logger.log(Level.ERROR, "Connection isn't return to the pool.");
        }
        try {	rs.close();
        } catch (SQLException e) {
            // logger.log(Level.ERROR, "ResultSet isn't closed.");
        }
        try {	st.close();
        } catch (SQLException e) {
            // logger.log(Level.ERROR, "Statement isn't closed.");
        }
    }

    public void closeConnection(Connection con, Statement st) {
        try {	con.close();
        } catch (SQLException e) {
            // logger.log(Level.ERROR, "Connection isn't return to the pool.");
        }
        try {	st.close();
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
            if (!givenAwayConQueue.remove(this)) {
                throw new SQLException("Error deleting connection from the given away connections pool.");
            }
            if (!connectionQueue.offer(this)) {
                throw new SQLException("Error allocating connection in the pool.");
            }
        }

        public boolean setShardingKeyIfValid(ShardingKey shardingKey, ShardingKey superShardingKey, int timeout) throws SQLException {
            return this.connection.setShardingKeyIfValid(shardingKey, superShardingKey, timeout);
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            this.connection.setClientInfo(name, value);
        }

        public Savepoint setSavepoint(String name) throws SQLException {
            return this.connection.setSavepoint(name);
        }

        public SQLXML createSQLXML() throws SQLException {
            return this.connection.createSQLXML();
        }

        public boolean isClosed() throws SQLException {
            return this.connection.isClosed();
        }

        public boolean isValid(int timeout) throws SQLException {
            return this.connection.isValid(timeout);
        }

        public void rollback(Savepoint savepoint) throws SQLException {
            this.connection.rollback(savepoint);
        }

        public void endRequest() throws SQLException {
            this.connection.endRequest();
        }

        public boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout) throws SQLException {
            return this.connection.setShardingKeyIfValid(shardingKey, timeout);
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return this.connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public Savepoint setSavepoint() throws SQLException {
            return this.connection.setSavepoint();
        }

        public void rollback() throws SQLException {
            this.connection.rollback();
        }

        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return this.connection.createStruct(typeName, attributes);
        }

        public int getNetworkTimeout() throws SQLException {
            return this.connection.getNetworkTimeout();
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        public DatabaseMetaData getMetaData() throws SQLException {
            return this.connection.getMetaData();
        }

        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            this.connection.setNetworkTimeout(executor, milliseconds);
        }

        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return this.connection.createStatement(resultSetType, resultSetConcurrency);
        }

        public <T> T unwrap(Class<T> iface) throws SQLException {
            return this.connection.unwrap(iface);
        }

        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            this.connection.setClientInfo(properties);
        }

        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return this.connection.prepareStatement(sql);
        }

        public Blob createBlob() throws SQLException {
            return this.connection.createBlob();
        }

        public void abort(Executor executor) throws SQLException {
            this.connection.abort(executor);
        }

        public void setReadOnly(boolean readOnly) throws SQLException {
            this.connection.setReadOnly(readOnly);
        }

        public SQLWarning getWarnings() throws SQLException {
            return this.connection.getWarnings();
        }

        public void setAutoCommit(boolean autoCommit) throws SQLException {
            this.connection.setAutoCommit(autoCommit);
        }

        public void setTransactionIsolation(int level) throws SQLException {
            this.connection.setTransactionIsolation(level);
        }

        public void setCatalog(String catalog) throws SQLException {
            this.connection.setCatalog(catalog);
        }

        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return this.connection.createArrayOf(typeName, elements);
        }

        public String getSchema() throws SQLException {
            return this.connection.getSchema();
        }

        public void clearWarnings() throws SQLException {
            this.connection.clearWarnings();
        }

        public NClob createNClob() throws SQLException {
            return this.connection.createNClob();
        }

        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            this.connection.setTypeMap(map);
        }

        public Statement createStatement() throws SQLException {
            return this.connection.createStatement();
        }

        public void setShardingKey(ShardingKey shardingKey) throws SQLException {
            this.connection.setShardingKey(shardingKey);
        }

        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            this.connection.releaseSavepoint(savepoint);
        }

        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public String nativeSQL(String sql) throws SQLException {
            return this.connection.nativeSQL(sql);
        }

        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return this.connection.getTypeMap();
        }

        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return this.connection.prepareStatement(sql, columnNames);
        }

        public Clob createClob() throws SQLException {
            return this.connection.createClob();
        }

        public int getTransactionIsolation() throws SQLException {
            return this.connection.getTransactionIsolation();
        }

        public boolean isReadOnly() throws SQLException {
            return this.connection.isReadOnly();
        }

        public void setShardingKey(ShardingKey shardingKey, ShardingKey superShardingKey) throws SQLException {
            this.connection.setShardingKey(shardingKey, superShardingKey);
        }

        public boolean getAutoCommit() throws SQLException {
            return this.connection.getAutoCommit();
        }

        public String getCatalog() throws SQLException {
            return this.connection.getCatalog();
        }

        public Properties getClientInfo() throws SQLException {
            return this.connection.getClientInfo();
        }

        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return this.connection.isWrapperFor(iface);
        }

        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return this.connection.prepareStatement(sql, columnIndexes);
        }

        public void setSchema(String schema) throws SQLException {
            this.connection.setSchema(schema);
        }

        public int getHoldability() throws SQLException {
            return this.connection.getHoldability();
        }

        public CallableStatement prepareCall(String sql) throws SQLException {
            return this.connection.prepareCall(sql);
        }

        public void commit() throws SQLException {
            this.connection.commit();
        }

        public String getClientInfo(String name) throws SQLException {
            return this.connection.getClientInfo(name);
        }

        public void beginRequest() throws SQLException {
            this.connection.beginRequest();
        }

        public void setHoldability(int holdability) throws SQLException {
            this.connection.setHoldability(holdability);
        }

        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return this.connection.prepareStatement(sql, autoGeneratedKeys);
        }
    }
}