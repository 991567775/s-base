package cn.ezeyc.edpbase.core.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class SessionFactory {
    @Autowired
    private DataSource dataSource;
    public Session openSession() {
        return new Session(dataSource, false);
    }

    public Session openSession(boolean autoCommit) {
        return new Session(dataSource, autoCommit);
    }
}
