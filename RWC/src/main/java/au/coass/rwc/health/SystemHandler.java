package au.coass.rwc.health;

import au.coaas.sqem.proto.HostSpecs;
import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

public class SystemHandler {
    public static HostSpecs getSystemStatus(boolean isHeartbeat) {
        HostSpecs.Builder builder = HostSpecs.newBuilder();
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);
        if(isHeartbeat) {
            return builder.setFreeMemory(Runtime.getRuntime().freeMemory())
                    .setCpuUsage(osBean.getSystemCpuLoad()).build();
        }

        return builder.setProcessors(Runtime.getRuntime().availableProcessors())
                .setMaxMemory(Runtime.getRuntime().totalMemory())
                .setOperatingSystem(System.getProperty("os.name"))
                .setFreeMemory(Runtime.getRuntime().freeMemory())
                .setCpuUsage(osBean.getSystemCpuLoad())
                .setArchi("os.arch").build();
    }
}
