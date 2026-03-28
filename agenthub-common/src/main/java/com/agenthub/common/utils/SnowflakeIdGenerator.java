package com.agenthub.common.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * 雪花算法ID生成器
 *
 * 结构：64位 = 1位符号 + 41位时间戳 + 10位机器ID + 12位序列号
 * - 时间戳：41位，约69年
 * - 机器ID：10位（数据中心ID 5位 + 工作机器ID 5位），支持1024个节点
 * - 序列号：12位，每毫秒可生成4096个ID
 */
@Slf4j
public class SnowflakeIdGenerator {

    /**
     * 开始时间戳（2024-01-01）
     */
    private static final long START_TIMESTAMP = 1704067200000L;

    /**
     * 机器ID占用位数
     */
    private static final long MACHINE_ID_BITS = 10L;

    /**
     * 序列号占用位数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 机器ID最大值
     */
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);

    /**
     * 序列号最大值
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * 机器ID左移位数
     */
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 时间戳左移位数
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;

    /**
     * 机器ID
     */
    private final long machineId;

    /**
     * 序列号
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 单例实例
     */
    private static volatile SnowflakeIdGenerator instance;

    /**
     * 构造函数
     *
     * @param machineId 机器ID（0-1023）
     */
    public SnowflakeIdGenerator(long machineId) {
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("机器ID必须在0-" + MAX_MACHINE_ID + "之间");
        }
        this.machineId = machineId;
        log.info("雪花算法ID生成器初始化完成，机器ID: {}", machineId);
    }

    /**
     * 获取单例实例
     * 默认使用机器IP地址最后一段作为机器ID
     */
    public static SnowflakeIdGenerator getInstance() {
        if (instance == null) {
            synchronized (SnowflakeIdGenerator.class) {
                if (instance == null) {
                    // 默认机器ID从环境变量获取，如果没有则使用IP哈希
                    long machineId = getMachineIdFromEnv();
                    instance = new SnowflakeIdGenerator(machineId);
                }
            }
        }
        return instance;
    }

    /**
     * 从环境变量获取机器ID
     */
    private static long getMachineIdFromEnv() {
        String envMachineId = System.getenv("MACHINE_ID");
        if (envMachineId != null && !envMachineId.isEmpty()) {
            try {
                return Long.parseLong(envMachineId) & MAX_MACHINE_ID;
            } catch (NumberFormatException e) {
                log.warn("环境变量MACHINE_ID解析失败，使用默认值");
            }
        }
        // 使用进程ID作为默认机器ID
        return (ProcessHandle.current().pid() & MAX_MACHINE_ID);
    }

    /**
     * 生成下一个ID
     */
    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        // 时钟回拨检测
        if (currentTimestamp < lastTimestamp) {
            long offset = lastTimestamp - currentTimestamp;
            if (offset <= 5) {
                // 小范围回拨，等待
                try {
                    Thread.sleep(offset);
                    currentTimestamp = System.currentTimeMillis();
                } catch (InterruptedException e) {
                    throw new RuntimeException("时钟回拨等待失败", e);
                }
            } else {
                // 大范围回拨，抛出异常
                throw new RuntimeException("时钟回拨超过5ms，拒绝生成ID");
            }
        }

        if (currentTimestamp == lastTimestamp) {
            // 同一毫秒内，序列号递增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 序列号溢出，等待下一毫秒
                currentTimestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置为0
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        // 组装ID
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence;
    }

    /**
     * 等待直到下一毫秒
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    /**
     * 解析ID的时间戳部分
     */
    public static long parseTimestamp(long id) {
        return (id >> TIMESTAMP_SHIFT) + START_TIMESTAMP;
    }

    /**
     * 解析ID的机器ID部分
     */
    public static long parseMachineId(long id) {
        return (id >> MACHINE_ID_SHIFT) & MAX_MACHINE_ID;
    }

    /**
     * 解析ID的序列号部分
     */
    public static long parseSequence(long id) {
        return id & MAX_SEQUENCE;
    }
}