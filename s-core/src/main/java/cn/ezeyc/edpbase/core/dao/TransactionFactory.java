package cn.ezeyc.edpbase.core.dao;

import javax.sql.DataSource;
import java.sql.Connection;

public class TransactionFactory {

    public Transaction newTransaction(Connection conn, boolean autoComm) {
        return new Transaction(conn, autoComm);
    }


    public Transaction newTransaction(DataSource dataSource, boolean autoComm) {
        return new Transaction(dataSource, autoComm);
    }
}
