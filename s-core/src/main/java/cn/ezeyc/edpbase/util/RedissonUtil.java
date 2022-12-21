package cn.ezeyc.edpbase.util;

import cn.ezeyc.edpcommon.annotation.framework.configuration;
import cn.ezeyc.edpcommon.annotation.framework.value;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 * @author zewang
 */
@configuration
public class RedissonUtil {
    private static final String LOCK_TITLE = "redisLock_";
    @value("edp.redis.ip")
    private  String ip;
    @value("edp.redis.port")
    private  Integer port;
    @value("edp.redis.db")
    private  Integer db;
    @value("edp.redis.pwd")
    private  String pwd;

    private static Redisson redisson;
    public void init(){
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig .setAddress("redis://"+ip+":"+port).setDatabase(db);
        if(StringUtils.isNotBlank(pwd)){
            singleServerConfig.setPassword(pwd);
        }
        //得到redisson对象
        redisson = (Redisson) Redisson.create(config);
    }

    /**
     * 获取redisson对象的方法
     * @return
     */
    public  Redisson getRedisson(){
        return redisson;
    }

    /**
     * 加锁
     * @param lockName
     * @return
     */
    public  static boolean acquire(String lockName){
        //声明key对象
        String key = LOCK_TITLE + lockName;
        //获取锁对象
        RLock mylock = redisson.getLock(key);
        //加锁，并且设置锁过期时间，防止死锁的产生
        mylock.lock(2, TimeUnit.MINUTES);
        //加锁成功
        return  true;
    }

    /**
     * 锁的释放
     * @param lockName
     */
    public static void release(String lockName){
        //必须是和加锁时的同一个key
        String key = LOCK_TITLE + lockName;
        //获取所对象
        RLock mylock = redisson.getLock(key);
        //释放锁（解锁）
        mylock.unlock();
    }
}
