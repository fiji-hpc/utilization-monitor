package cz.it4i.monitor;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.parallel.ParallelizationParadigm;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import net.imagej.ImageJ;

@Plugin(type = Command.class, menuPath = "Plugins>Utilities>Utilization Monitor")
public class UtilizationMonitor implements Command {

	@Parameter(type = ItemIO.INPUT)
    private ImageJ ij;
	
	@Parameter(type = ItemIO.INPUT)
	private ParallelizationParadigm paradigm;
   
    @Override
    public void run() {    	
        // Launch JavaFX interface
        MainAppFrame app = new MainAppFrame(ij, paradigm);
        app.setTitle(" Utilization Monitor ");
        app.init();
    }
}
