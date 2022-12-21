package cn.ezeyc.edpbase.monitor;


import com.zaxxer.hikari.HikariPoolMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfiguration;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import java.lang.management.ManagementFactory;

/**
 * 系统监控
 * @author wz
 */
@Conditional({AutoMonitor.class})
@Import({SchedulingConfiguration.class})
public class Monitor {
    private Logger logger= LoggerFactory.getLogger(Monitor.class);
    @Scheduled(fixedRate = 5000)
    public void hikariMonitor() throws MalformedObjectNameException {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName poolName = new ObjectName("com.zaxxer.hikari:type=Pool (mysql)");
        HikariPoolMXBean poolProxy = JMX.newMXBeanProxy(mBeanServer, poolName, HikariPoolMXBean.class);
        if(poolProxy == null) {
            logger.info("Hikari not initialized,please wait...");
        }else {
            logger.info("HikariPoolState = "
                    + "Active-活动连接数=[" + String.valueOf(poolProxy.getActiveConnections() + "] "
                    + "Idle-空闲连接数=[" + String.valueOf(poolProxy.getIdleConnections() + "] "
                    + "Wait--等待连接数=["+poolProxy.getThreadsAwaitingConnection()+"] "
                    + "Total-总连接数=["+poolProxy.getTotalConnections()+"]")));
        }

    }
}
