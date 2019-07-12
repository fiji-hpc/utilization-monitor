
package cz.it4i.imagej;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

@SuppressWarnings("restriction")
@Plugin(headless = true, type = Command.class, menuPath = "Plugins>Utilities>CPU Load")
public class CpuLoadExplorer implements Command {

	@Parameter(type = ItemIO.OUTPUT)
	private long uptime;

	@Parameter(type = ItemIO.OUTPUT)
	private double processCpuLoad;

	@Parameter(type = ItemIO.OUTPUT)
	private double systemCpuLoad;

	@Parameter(type = ItemIO.OUTPUT)
	private double systemLoadAverage;

	@Override
	public void run() {
		// In milliseconds:
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		uptime = runtimeBean.getUptime();

		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
			OperatingSystemMXBean.class);
		
		processCpuLoad = osBean.getProcessCpuLoad();
		
		systemCpuLoad = osBean.getSystemCpuLoad();
		
		// The bellow functionality is not supported on Windows, it always outputs "-1":
		systemLoadAverage = osBean.getSystemLoadAverage();
	}

}
