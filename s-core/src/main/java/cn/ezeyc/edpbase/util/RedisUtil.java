package cn.ezeyc.edpbase.util;

import com.alibaba.fastjson.JSONObject;
import cn.ezeyc.edpcommon.annotation.framework.configuration;
import cn.ezeyc.edpcommon.annotation.framework.value;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.*;

import java.util.*;

/**
 * @author wz
 */
@configuration
public class RedisUtil {
    @value("edp.redis.ip")
    private  String ip;
    @value("edp.redis.port")
    private  Integer port;
    @value("edp.redis.db")
    private  Integer db;
    @value("edp.redis.pwd")
    private  String pwd;
    /**
     * 非切片客户端链接
     */
    private Jedis jedis;
    /**
     * 非切片链接池
     */
    private JedisPool jedisPool;
    private JedisCluster jedisCluster ;

    public void init(){
        initialPool();
    }

    /**
     * 初始化连接池
     */
    private void initialPool() {
        GenericObjectPoolConfig jedisPoolConfig = new GenericObjectPoolConfig();
        // 设置最大对象数
        jedisPoolConfig.setMaxTotal(20);
        // 最大能够保持空闲状态的对象数
        jedisPoolConfig.setMaxIdle(10);
        // 设置
        // 超时时间
        jedisPoolConfig.setMaxWaitMillis(10000);
        // 在获取连接的时候检查有效性, 默认false
        jedisPoolConfig.setTestOnBorrow(true);
        // 在返回Object时, 对返回的connection进行validateObject校验
        jedisPoolConfig.setTestOnReturn(true);
        if(StringUtils.isNotBlank(pwd)){
            jedisPool = new JedisPool(jedisPoolConfig, ip, port,10000,pwd);
        }else{
            jedisPool = new JedisPool(jedisPoolConfig, ip, port);
        }
//        initialClusterlPool();
    }

    /**
     * 初始化切片池
     */
    private void initialClusterlPool() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(20);
        config.setMaxIdle(10);
        config.setMinIdle(5);
        Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
        jedisClusterNodes.add(new HostAndPort(ip, port));
        jedisCluster =   new JedisCluster(jedisClusterNodes, 6000, 5000, 10, "artisan", config);
        // 池基本配置
//        GenericObjectPoolConfig<JedisSharding> config = new GenericObjectPoolConfig();
//        config.setMaxTotal(20);
//        config.setMaxIdle(5);
//        config.setMaxWaitMillis(1000L);
//        config.setTestOnBorrow(false);
//
//        // slave链接
//        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
//         JedisShardInfo master = new JedisShardInfo(ip, port, "master");
//         if(StringUtils.isNotBlank(pwd)){
//             master.setPassword(pwd);
//         }
//        shards.add(master);
//         //构造池
//        shardedJedisPool = new ShardedJedisPool(config, shards);
    }

    /**
     * 从连接池中获取一个ShardedJedis对象
     */
    public  Jedis getJedis() {
        return jedisPool.getResource();
    }

    /**
     * 通过String类型key获取String类型Value
     *
     * @param key 键
     * @return 值
     */
    public  String get(String key) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return jedis.get(key);
        }
    }
    /**
     * 通过字节数组类型key获取字节数组类型Value
     *
     * @param key 键
     * @return 值
     */
    public  byte[] get(byte[] key) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return jedis.get(key);
        }
    }
    /**
     * 通过object key获取object Value
     * 实际两者序列化存入byte[]
     * @param key 键
     * @return 值
     */
    public  Object getNormal(Object key) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return SerializeUtil.unSerialize(jedis.get(SerializeUtil.serialize(key)));
        }
    }
    /**
     * 通过string key获取object value
     *  实际两者序列化存入string
     * @param key 键
     * @return 值
     */
    public  Object getByString(String key) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            final String s = jedis.get(key);
            if(s!=null){
                return SerializeUtil.unSerializeString(s);
            }
            return null;
        }
    }

    /**
     * 设置String类型key和String类型value
     *
     * @param key 键
     * @param value 值
     * @return
     */
    public  String set(String key, String value) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return jedis.set(key, value);
        }

    }

    /**
     * 多少秒后过期
     * @param key
     * @param value
     * @param time
     * @return
     */
    public  String set(String key, String value,int time) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            String set = jedis.set(key, value);
            jedis.expire(key, time);
            return set;
        }

    }
    public  String set(String key, Object value,int time) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            String set = jedis.set(key, String.valueOf(value));
            jedis.expire(key, time);
            return set;
        }

    }
    public  String set(String key, Object value) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            String set = jedis.set(key, String.valueOf(value));
            return set;
        }

    }
    /**
     * 设置Object类型key和object value
     *实际两者序列化存入byte[]
     * @param key 键
     * @param value 值
     * @return
     */
    public  String set(Object key, Object value) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return jedis.set(SerializeUtil.serialize(key), SerializeUtil.serialize(value));
        }

    }
    /**
     * 设置String类型key和Object value
     *实际两者序列化存入string
     * @param key 键
     * @param value 值
     * @return
     */
    public  String setByString(String key, Object value) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return jedis.set(key,SerializeUtil.serializeString(value));
        }

    }
    public  String setToMap(String key, Object value) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return jedis.set(key,SerializeUtil.serializeString(JSONObject.parseObject(JSONObject.toJSONString(value), Map.class)));
        }

    }
    /**
     * 设置字节数组类型key和字节数组类型value
     *
     * @param key 键
     * @param value 值
     * @return
     */
    public  String set(byte[] key, byte[] value) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return jedis.set(key, value);
        }

    }

    /**
     * 删除指定String类型key
     */
    public  Long del(String key) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return jedis.del(key);
        }
    }
    /**
     * 删除指定前缀key  (存入的key为string类型)
     */
    public  void delBySuffix(String key) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            final Set<String> keys = jedis.keys(key+"*");
            for(String s:keys){
                jedis.del(s);
            }
        }
    }

    /**
     * 左侧放入集合
     *
     * @param key 键
     * @param values 值集合
     * @return
     */
    public  Long lpush(String key, String... values) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return jedis.lpush(key, values);
        }
    }

    /**
     * 左侧弹出一个元素
     *
     * @param key 指定键
     * @return 左侧第一个元素
     */
    public  String lpop(String key) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return jedis.lpop(key);
        }
    }

    /**
     * 右侧放入集合
     *
     * @param key 键
     * @param values 值集合
     * @return
     */
    public  Long rpush(String key, String... values) {
        try (
               Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return jedis.rpush(key, values);
        }
    }

    /**
     * 右侧弹出一个元素
     *
     * @param key 指定键
     * @return 右侧第一个元素
     */
    public  String rpop(String key) {
        try (
                Jedis jedis = jedisPool.getResource();
        ) {
            jedis.select(db);
            return jedis.rpop(key);
        }
    }

    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public  Boolean exists(String key) {
        try  (

                Jedis jedis = jedisPool.getResource();
        ){
            jedis.select(db);
            return  jedis.exists(key) ;
        }
    }
    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public  Boolean exists(Object key) {
        try  (

                Jedis jedis = jedisPool.getResource();
        ){
            jedis.select(db);
            return  jedis.exists(SerializeUtil.serialize(key)) ;
        }
    }
}
