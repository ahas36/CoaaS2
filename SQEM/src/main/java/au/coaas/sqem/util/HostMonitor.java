package au.coaas.sqem.util;

import com.sun.management.OperatingSystemMXBean;
import static sun.management.ManagementFactoryHelper.getOperatingSystemMXBean;

public class HostMonitor {
    private static long lastProcessCpuTime  = 0;

    public static synchronized double getCpuUsage() {
        long processCpuTime = 0;
        if (getOperatingSystemMXBean() instanceof OperatingSystemMXBean)
            processCpuTime = ((OperatingSystemMXBean) getOperatingSystemMXBean()).getProcessCpuTime();

        double cpuUsage = processCpuTime - lastProcessCpuTime;
        lastProcessCpuTime = processCpuTime;

        // returns in miliseconds
        return cpuUsage/1000;
    }
}
