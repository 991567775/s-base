package cn.ezeyc.edpbase.control;

import cn.ezeyc.edpbase.pojo.base.ControlBase;
import com.alibaba.fastjson.JSONObject;
import cn.ezeyc.edpcommon.pojo.ResultBody;
import com.sun.management.OperatingSystemMXBean;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
* 描述：监控
* @author wz
* @date 2021-08-30 16:57:30
*/
public class SysMonitorControl extends ControlBase {

    static SystemInfo systemInfo = new SystemInfo();
    static JSONObject jsonObject=new JSONObject();
    static DecimalFormat decimalFormat= new DecimalFormat("#.##");
    static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    static CentralProcessor processor = systemInfo.getHardware().getProcessor();

    /**
     * showdoc
     * @catalog 通用/服务器监控
     * @title 服务器监控信息
     * @description
     * @method post
     * @url 地址/sysMonitor/list
     * @return {"code":200,"data":{},"message":"操作成功","extra":{},"timestamp":1659584966}
     * @return_param code int 状态码
     * @return_param message string 提示信息
     * @return_param timestamp long 服务器时间戳
     * @return_param extra map 额外信息
     * @return_param data Object 数据
     * @remark
     * @number 1
     */
    public  ResultBody list(){
        try {
            //系统版本
            jsonObject.put("osName",System.getProperty("os.name"));

            // 磁盘使用情况
            File[] files = File.listRoots();
            //总磁盘空间
            jsonObject.put("totalDisk",new DecimalFormat("#.#").format(files[0].getTotalSpace() * 1.0 / 1024 / 1024 / 1024) + "G");
            //可用磁盘空间
            jsonObject.put("freeDisk",new DecimalFormat("#.#").format(files[0].getUsableSpace() * 1.0 / 1024 / 1024 / 1024) + "G");

            // 总的物理内存
            jsonObject.put("totalMemorySize",decimalFormat.format(osmxb.getTotalPhysicalMemorySize() / 1024.0 / 1024 / 1024) + "G");
            // 剩余的物理内存
            jsonObject.put("freeMemorySize",decimalFormat.format(osmxb.getFreePhysicalMemorySize() / 1024.0 / 1024 / 1024) + "G");
            // 已使用的物理内存
            jsonObject.put("usedMemory",decimalFormat.format( (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / 1024.0 / 1024 / 1024) + "G");
            jsonObject.put("cpuCount",Runtime.getRuntime().availableProcessors());
            jsonObject.put("javaHome" , System.getProperty("java.home"));
            jsonObject.put("javaVersion" , System.getProperty("java.version"));
            //各个服务的jvm信息
            // 椎内存使用情况
            MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();

            // 初始的总内存
            jsonObject.put("initTotalMemorySize" ,decimalFormat.format(memoryUsage.getInit() / 1024.0 / 1024  ) + "M" );
            // 最大可用内存
            jsonObject.put("maxMemorySize" , decimalFormat.format(memoryUsage.getMax() / 1024.0 / 1024 ) + "M");
            // 已使用的内存
            jsonObject.put("usedMemorySize" ,decimalFormat.format(memoryUsage.getUsed() / 1024.0 / 1024) + "M");

            ThreadGroup parentThread;
            for (parentThread = Thread.currentThread().getThreadGroup(); parentThread.getParent() != null; parentThread = parentThread.getParent()) {

            }
            jsonObject.put("totalThread" , parentThread.activeCount());
            //服务启动时间
            jsonObject.put("startTime:" , simpleDateFormat.format(new Date(ManagementFactory.getRuntimeMXBean().getStartTime())));
            //cpu 信息
            printlnCpuInfo();
            return ResultBody.success(jsonObject);
        } catch (Exception e) {
            return ResultBody.failed(e.getMessage());
        }
    }
    /**
     * showdoc
     * @catalog 通用/服务器监控
     * @title cpu信息
     * @description
     * @method post
     * @url 地址/sysMonitor/printlnCpuInfo
     * @return {"code":200,"data":{},"message":"操作成功","extra":{},"timestamp":1659584966}
     * @return_param code int 状态码
     * @return_param message string 提示信息
     * @return_param timestamp long 服务器时间戳
     * @return_param extra map 额外信息
     * @return_param data Object 数据
     * @remark
     * @number 1
     */
    private void printlnCpuInfo() throws InterruptedException {

        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        jsonObject.put("cpuUsed" , decimalFormat.format(cSys * 100 / totalCpu)+"%");
        jsonObject.put("cpuWait" , decimalFormat.format(iowait * 100 / totalCpu)+"%");
        jsonObject.put("cpuFree" , decimalFormat.format(idle * 100 / totalCpu)+"%");
    }
}
