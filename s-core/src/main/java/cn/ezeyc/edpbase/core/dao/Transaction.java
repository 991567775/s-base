package cn.ezeyc.edpbase.core.dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Transaction {

    protected Connection connection;
    protected DataSource dataSource;
    protected boolean autoCommit;

    public Transaction(Connection connection, boolean autoCommit) {
        this.connection = connection;
        this.autoCommit = autoCommit;
    }

    public Transaction(DataSource dataSource, boolean autoCommit) {
        this.dataSource = dataSource;
        this.autoCommit = autoCommit;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null) {
            openConnection();
        }
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    public void commit() throws SQLException {
        connection.commit();
    }


    public void rollback() throws SQLException {
        connection.rollback();
    }


    public void close() throws SQLException {
        if (connection != null) {
            /**若是返回连接池需重置*/
            resetAutoCommit();
            connection.close();
        }
    }

    protected void resetAutoCommit() {
        try {
            if (!connection.getAutoCommit()) {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void openConnection() throws SQLException {
        connection = dataSource.getConnection();
        setDesiredAutoCommit(autoCommit);
    }

    protected void setDesiredAutoCommit(boolean desiredAutoCommit) {
        try {
            if (connection.getAutoCommit() != desiredAutoCommit) {
                connection.setAutoCommit(desiredAutoCommit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }



    public boolean isAutoCommit() {
        return autoCommit;
    }


    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }
}
