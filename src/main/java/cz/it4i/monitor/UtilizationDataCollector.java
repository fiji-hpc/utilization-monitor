
package cz.it4i.monitor;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@SuppressWarnings("restriction")
@Plugin(headless = true, type = Command.class, menuPath = "Plugins>Utilities>Utilization Data Collector", visible = false)
public class UtilizationDataCollector implements Command {

	// CPU utilization metrics:
	@Parameter(type = ItemIO.OUTPUT)
	private long uptime;

	@Parameter(type = ItemIO.OUTPUT)
	private double processCpuLoad;

	@Parameter(type = ItemIO.OUTPUT)
	private double systemCpuLoad;

	@Parameter(type = ItemIO.OUTPUT)
	private double systemLoadAverage;

	// Memory utilization metrics:
	@Parameter(type = ItemIO.OUTPUT)
	private double totalPhysicalMemorySize;

	@Parameter(type = ItemIO.OUTPUT)
	private double freePhysicalMemorySize;
	
	@Parameter(type = ItemIO.OUTPUT)
	private double memoryUtilization;
	
	@Override
	public void run() {		
		// In milliseconds:
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		uptime = runtimeBean.getUptime();

		// CPU:
		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
			OperatingSystemMXBean.class);
		
		processCpuLoad = osBean.getProcessCpuLoad();
		
		systemCpuLoad = osBean.getSystemCpuLoad();
		
		// The bellow functionality is not supported on Windows, it always outputs "-1":
		systemLoadAverage = osBean.getSystemLoadAverage();
		
		// Memory:		
		// Bellow measurements are in bytes:
		totalPhysicalMemorySize = osBean.getTotalPhysicalMemorySize();
		
		freePhysicalMemorySize = osBean.getFreePhysicalMemorySize();
		
		double usedPhysicalMemory = totalPhysicalMemorySize - freePhysicalMemorySize;
		
		// Calculate the utilization (in [0.0,1.0] interval):
		memoryUtilization = usedPhysicalMemory/totalPhysicalMemorySize;
	}

}
