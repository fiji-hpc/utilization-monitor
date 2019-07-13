package cz.it4i.imagej;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scijava.Context;
import org.scijava.command.Command;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import net.imagej.ImageJ;
import org.scijava.parallel.ParallelizationParadigm;

import cz.it4i.parallel.RunningRemoteServer;
import cz.it4i.parallel.plugins.CpuLoadExplorer;
import cz.it4i.parallel.utils.TestParadigm;

@Plugin(type = Command.class, menuPath = "Plugins>Utilities>Utilization Monitor")
public class UtilizationMonitor implements Command {

	@Parameter
    private ImageJ ij;
   
    @Override
    public void run() {    	
        // Launch JavaFX interface
        MainAppFrame app = new MainAppFrame(ij);
        app.setTitle(" Utilization Monitor ");
        app.init();
    }
}
