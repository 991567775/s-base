package cn.ezeyc.edpbase.idgenerator;

import cn.ezeyc.edpcommon.error.ExRuntimeException;

/**
 * SnowflakeIdGenerator：
 *
 * @author: Administrator
 * @date: 2020年11月30日, 0030 17:05:02
 */
public class SnowflakeIdGenerator   {
    private static final long EPOCH = 1514736000000L;
    /**
     * 机器标识位数
     */
    private static final long WORKER_ID_BITS = 4L;
    /**
     * 毫秒内自增位
     */
    private static final long SEQUENCE_BITS = 12L;
    /**
     * 机器ID最大值:16
     */
    private static final long MAX_WORKER_ID = -1L ^ -1L << WORKER_ID_BITS;

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMES_TAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long SEQUENCE_MASK = -1L ^ -1L << SEQUENCE_BITS;

    private static final long WORKER_ID=0;
    /**
     * 并发控制
     */
    private static long sequence = 0L;
    private static long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        workerId = workerId;
    }

    public static synchronized long nextId() {
        long timestamp = currentTimeMillis();
        if (lastTimestamp == timestamp) {
            // 如果上一个timestamp与新产生的相等，则sequence加一(0-4095循环);
            // 对新的timestamp，sequence从0开始
            sequence = sequence + 1 & SEQUENCE_MASK;
            if (sequence == 0) {
                // 重新生成timestamp
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        if (timestamp < lastTimestamp) {
            throw new ExRuntimeException(
                    String.format("clock moved backwards.Refusing to generate id for %d milliseconds",
                            (lastTimestamp - timestamp)));
        }

       lastTimestamp = timestamp;
        return timestamp - EPOCH << TIMES_TAMP_LEFT_SHIFT | WORKER_ID << WORKER_ID_SHIFT | sequence;
    }

    /**
     * 等待下一个毫秒的到来, 保证返回的毫秒数在参数lastTimestamp之后
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }
    public Long nextId(Object entity) {
        return nextId();
    }
    /**
     * 获得系统当前毫秒数
     */
    private static long currentTimeMillis() {
        return System.currentTimeMillis();
    }
//    public static void main(String[] args) {
//        System.out.println(nextId());
//    }


}
