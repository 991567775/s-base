package cn.ezeyc.edpbase.core.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Executor {

    private boolean closed;
    private final boolean autoCommit;
    private Connection conn;
    protected Transaction transaction;

    public Executor(DataSource dataSource, boolean autoCommit) {
        this.autoCommit = autoCommit;
        this.transaction = new TransactionFactory().newTransaction(dataSource,autoCommit);
        try {
            this.conn = this.transaction.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
