
package cz.it4i.monitor;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import com.sun.management.OperatingSystemMXBean;

@Plugin(headless = true, type = Command.class, menuPath = "Plugins>Utilities>Utilization Data Collector", visible = true)
public class UtilizationDataCollector implements Command {

	// CPU utilization metrics:
	@Parameter(type = ItemIO.OUTPUT)
	private long uptime;

	@Parameter(type = ItemIO.OUTPUT)
	private double processCpuLoad;

	@Parameter(type = ItemIO.OUTPUT)
	private long processCpuTime;

	@Parameter(type = ItemIO.OUTPUT)
	private double systemCpuLoad;

	@Parameter(type = ItemIO.OUTPUT)
	private double systemLoadAverage;

	@Parameter(type = ItemIO.OUTPUT)
	private int availableProcessors;

	// Memory utilization metrics:
	@Parameter(type = ItemIO.OUTPUT)
	private long totalPhysicalMemorySize;

	@Parameter(type = ItemIO.OUTPUT)
	private long freePhysicalMemorySize;

	@Parameter(type = ItemIO.OUTPUT)
	private double memoryUtilization;

	@Parameter(type = ItemIO.OUTPUT)
	private long committedVirtualMemorySize;

	@Parameter(type = ItemIO.OUTPUT)
	private long totalSwapSpaceSize;

	@Parameter(type = ItemIO.OUTPUT)
	private long freeSwapSpaceSize;

	// Operating System information:
	@Parameter(type = ItemIO.OUTPUT)
	private String name;

	@Parameter(type = ItemIO.OUTPUT)
	private String arch;

	@Parameter(type = ItemIO.OUTPUT)
	private String version;

	// Java Virtual Machine information:
	@Parameter(type = ItemIO.OUTPUT)
	private String vmName;

	@Parameter(type = ItemIO.OUTPUT)
	private String vmVendor;

	@Parameter(type = ItemIO.OUTPUT)
	private String vmVersion;

	@Parameter(type = ItemIO.OUTPUT)
	private String classPath;

	@Override
	public void run() {
		// In milliseconds:
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		uptime = runtimeBean.getUptime();

		// CPU:
		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

		processCpuLoad = osBean.getProcessCpuLoad();
		systemCpuLoad = osBean.getSystemCpuLoad();
		availableProcessors = osBean.getAvailableProcessors();
		processCpuTime = osBean.getProcessCpuTime();
		// System load average method is not supported on Windows, it always outputs
		// "-1":
		systemLoadAverage = osBean.getSystemLoadAverage();

		// Memory:
		// Size measurements are in bytes:
		totalPhysicalMemorySize = osBean.getTotalPhysicalMemorySize();
		freePhysicalMemorySize = osBean.getFreePhysicalMemorySize();
		double usedPhysicalMemory = (double) totalPhysicalMemorySize - (double) freePhysicalMemorySize;

		// Calculate the utilization (in [0.0,1.0] interval):
		memoryUtilization = usedPhysicalMemory / totalPhysicalMemorySize;
		committedVirtualMemorySize = osBean.getCommittedVirtualMemorySize();
		totalSwapSpaceSize = osBean.getTotalSwapSpaceSize();
		freeSwapSpaceSize = osBean.getFreeSwapSpaceSize();

		// Operating System:
		name = osBean.getName();
		arch = osBean.getArch();
		version = osBean.getVersion();

		// Java Virtual Machine:
		classPath = runtimeBean.getClassPath();
		vmName = runtimeBean.getVmName();
		vmVendor = runtimeBean.getVmVendor();
		vmVersion = runtimeBean.getVmVersion();
	}

}
