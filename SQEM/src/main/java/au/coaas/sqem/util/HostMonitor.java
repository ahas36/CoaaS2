package au.coaas.sqem.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class HostMonitor {
    private static long lastProcessCpuTime  = 0;

    public static synchronized double getCpuUsage() {
        long processCpuTime = 0;

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] allThreadIds = threadMXBean.getAllThreadIds();
        for (long id : allThreadIds) {
            processCpuTime += threadMXBean.getThreadCpuTime(id);
        }

        // Currently the total processing CPU time is in nanoseconds.
        // double cpuUsage = processCpuTime - lastProcessCpuTime;
        // lastProcessCpuTime = processCpuTime;

        // returns in miliseconds
        // return cpuUsage/1000000;
        return processCpuTime/1000000;
    }
}
