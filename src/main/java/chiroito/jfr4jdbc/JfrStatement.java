package chiroito.jfr4jdbc;

import chiroito.jfr4jdbc.event.CancelEvent;
import chiroito.jfr4jdbc.event.StatementEvent;

import java.sql.*;

public class JfrStatement implements Statement {

    private final EventFactory factory;
    protected final Statement jdbcStatement;
    private final int statementId;
    private StringBuilder batchSql;

    public JfrStatement(Statement s) {
        this(s, EventFactory.getDefaultEventFactory());
    }

    public JfrStatement(Statement s, EventFactory factory) {
        super();
        this.jdbcStatement = s;
        this.statementId = System.identityHashCode(s);
        this.factory = factory;
    }

    protected StatementEvent createEvent(String sql) {

        StatementEvent event = this.factory.createStatementEvent();
        try {

            event.setSql(sql);
            if (this.jdbcStatement != null) {
                // Add Statement Info.
                event.setStatementId(this.statementId);
                event.setPoolable(this.jdbcStatement.isPoolable());
                event.setClosed(this.jdbcStatement.isClosed());
                event.setStatementClass(this.jdbcStatement.getClass());

                // Add Connection Info.
                Connection con = this.jdbcStatement.getConnection();
                if (con != null) {
                    event.setConnectionId(System.identityHashCode(con));
                    event.setAutoCommit(con.getAutoCommit());
                }
            }
        } catch (SQLException e) {
        }

        return event;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {

        StatementEvent event = this.createEvent(sql);
        event.begin();

        ResultSet rs = null;
        try {
            rs = this.jdbcStatement.executeQuery(sql);
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }

        return new JfrResultSet(rs);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {

        StatementEvent event = this.createEvent("");
        event.begin();

        ResultSet rs;
        try {
            rs = this.jdbcStatement.getResultSet();
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }

        return new JfrResultSet(rs);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {

        StatementEvent event = this.createEvent("getGeneratedKeys");
        event.begin();

        ResultSet rs;
        try {
            rs = this.jdbcStatement.getGeneratedKeys();
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }

        return new JfrResultSet(rs);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {

        StatementEvent event = this.createEvent(sql);
        event.begin();

        int result;
        try {
            result = this.jdbcStatement.executeUpdate(sql);
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }

        return result;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {

        StatementEvent event = this.createEvent(sql);
        event.begin();

        int result;
        try {
            result = this.jdbcStatement.executeUpdate(sql, autoGeneratedKeys);
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }

        return result;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {

        StatementEvent event = this.createEvent(sql);
        event.begin();

        int result;
        try {
            result = this.jdbcStatement.executeUpdate(sql, columnIndexes);
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }

        return result;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {

        StatementEvent event = this.createEvent(sql);
        event.begin();

        int result;
        try {
            result = this.jdbcStatement.executeUpdate(sql, columnNames);
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }

        return result;
    }

    @Override
    public boolean execute(String sql) throws SQLException {

        StatementEvent event = this.createEvent(sql);
        event.begin();

        boolean result;
        try {
            result = this.jdbcStatement.execute(sql);
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }

        return result;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {

        StatementEvent event = this.createEvent(sql);
        event.begin();

        boolean result;
        try {
            result = this.jdbcStatement.execute(sql, autoGeneratedKeys);
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }

        return result;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {

        StatementEvent event = this.createEvent(sql);
        event.begin();

        boolean result;
        try {
            result = this.jdbcStatement.execute(sql, columnIndexes);
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }

        return result;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {

        StatementEvent event = this.createEvent(sql);
        event.begin();

        boolean result;
        try {
            result = this.jdbcStatement.execute(sql, columnNames);
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }

        return result;
    }

    @Override
    public int[] executeBatch() throws SQLException {

        String sql = (this.batchSql == null) ? "" : this.batchSql.toString();

        StatementEvent event = this.createEvent(sql);
        event.begin();

        int[] result;
        try {
            result = this.jdbcStatement.executeBatch();
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }

        return result;
    }

    @Override
    public void cancel() throws SQLException {

        CancelEvent event = factory.createCancelEvent();
        event.setConnectionId(System.identityHashCode(this.getConnection()));
        event.setStatementId(this.statementId);
        event.begin();

        try {
            this.jdbcStatement.cancel();
        } catch (SQLException | RuntimeException e) {
            throw e;
        } finally {
            event.commit();
        }
    }

    @Override
    public void addBatch(String sql) throws SQLException {

        if (this.batchSql == null) {
            this.batchSql = new StringBuilder();
        }
        this.batchSql.append(sql);
        this.batchSql.append(";");

        this.jdbcStatement.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        this.batchSql = null;
        this.jdbcStatement.clearBatch();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.jdbcStatement.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.jdbcStatement.isWrapperFor(iface);
    }

    @Override
    public void close() throws SQLException {
        this.jdbcStatement.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return this.jdbcStatement.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        this.jdbcStatement.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return this.jdbcStatement.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        this.jdbcStatement.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        this.jdbcStatement.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return this.jdbcStatement.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {

        this.jdbcStatement.setQueryTimeout(seconds);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {

        return this.jdbcStatement.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {

        this.jdbcStatement.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {

        this.jdbcStatement.setCursorName(name);
    }

    @Override
    public int getUpdateCount() throws SQLException {

        return this.jdbcStatement.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return this.jdbcStatement.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

        this.jdbcStatement.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {

        return this.jdbcStatement.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        this.jdbcStatement.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return this.jdbcStatement.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return this.jdbcStatement.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {

        return this.jdbcStatement.getResultSetType();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.jdbcStatement.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return this.jdbcStatement.getMoreResults(current);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {

        return this.jdbcStatement.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.jdbcStatement.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        this.jdbcStatement.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return this.jdbcStatement.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        this.jdbcStatement.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return this.jdbcStatement.isCloseOnCompletion();
    }

    @Override
    public long getLargeUpdateCount() throws SQLException {
        return this.jdbcStatement.getLargeUpdateCount();
    }

    @Override
    public void setLargeMaxRows(long max) throws SQLException {
        this.jdbcStatement.setLargeMaxRows(max);
    }

    @Override
    public long getLargeMaxRows() throws SQLException {
        return this.jdbcStatement.getLargeMaxRows();
    }

    @Override
    public long[] executeLargeBatch() throws SQLException {
        return this.jdbcStatement.executeLargeBatch();
    }

    @Override
    public long executeLargeUpdate(String sql) throws SQLException {
        return this.jdbcStatement.executeLargeUpdate(sql);
    }

    @Override
    public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return this.jdbcStatement.executeLargeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return this.jdbcStatement.executeLargeUpdate(sql, columnIndexes);
    }

    @Override
    public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException {
        return this.jdbcStatement.executeLargeUpdate(sql, columnNames);
    }

    @Override
    public String enquoteLiteral(String val) throws SQLException {
        return this.jdbcStatement.enquoteLiteral(val);
    }

    @Override
    public String enquoteIdentifier(String identifier, boolean alwaysQuote) throws SQLException {
        return this.jdbcStatement.enquoteIdentifier(identifier, alwaysQuote);
    }

    @Override
    public boolean isSimpleIdentifier(String identifier) throws SQLException {
        return this.jdbcStatement.isSimpleIdentifier(identifier);
    }

    @Override
    public String enquoteNCharLiteral(String val) throws SQLException {
        return this.jdbcStatement.enquoteNCharLiteral(val);
    }
}
