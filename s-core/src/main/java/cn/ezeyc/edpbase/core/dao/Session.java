package cn.ezeyc.edpbase.core.dao;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;

public class Session {

    private final Executor executor;
    private final boolean autoCommit;
    private  DataSource dataSource;
    public Session(DataSource dataSource,boolean autoCommit) {
        this.autoCommit = autoCommit;
        executor = new Executor(dataSource,autoCommit);

    }


    //其他查询方法
}
