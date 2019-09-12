package cz.it4i.monitor;

import javax.swing.JOptionPane;

import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.parallel.ParallelService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import cz.it4i.parallel.MultipleHostParadigm;
import net.imagej.ImageJ;

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
		// Check if the utilization monitor should run:
		if (paradigm == null) {
			paradigm = service.getParadigmOfType(MultipleHostParadigm.class);
			if (paradigm != null) {
				if (paradigm.getHosts().isEmpty()) {
					String message = "There are no nodes!";
					ij.log().error(message);
					JOptionPane.showMessageDialog(null, message);
					return;
				}
			} else {
				String message = "The utilization-monitor plugin works only with MultipleHostParadigm!";
				ij.log().error(message);
				JOptionPane.showMessageDialog(null, message);
				return;
			}
		}
		
		// Launch JavaFX interface
		MainAppFrame app = new MainAppFrame(ij, paradigm);
		app.init();
	}
}
