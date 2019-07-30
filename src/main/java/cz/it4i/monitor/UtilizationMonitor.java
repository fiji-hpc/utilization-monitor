package cz.it4i.monitor;

import net.imagej.ImageJ;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.parallel.ParallelService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import cz.it4i.parallel.MultipleHostParadigm;

@Plugin(type = Command.class, menuPath = "Plugins>Utilities>Utilization Monitor")
public class UtilizationMonitor implements Command {

	@Parameter(type = ItemIO.INPUT)
    private ImageJ ij;
	
	@Parameter(type = ItemIO.INPUT, required = false)
	private MultipleHostParadigm paradigm;
	
	@Parameter(type = ItemIO.INPUT)
	private ParallelService service;
   
    @Override
    public void run() {    	        
    	if (paradigm == null) {
    		if(service.getParadigm() instanceof MultipleHostParadigm) {
    			paradigm = (MultipleHostParadigm) service.getParadigm();
    		} else {
    			ij.log().error("The utilization-monitor plugin works only with MultipleHostParadigm!");
    			return;
    		}
    	}
    	// Launch JavaFX interface
        MainAppFrame app = new MainAppFrame(ij, paradigm);
        app.setTitle(" Utilization Monitor ");
        app.init();
    }
}
