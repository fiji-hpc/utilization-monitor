
package cz.it4i.monitor.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.parallel.ParallelizationParadigm;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import cz.it4i.monitor.UtilizationMonitor;
import cz.it4i.parallel.utils.TestParadigm;
import net.imagej.ImageJ;



@Plugin(headless = true, type = Command.class, menuPath = "Plugins>Utilities>Dummy Plugin")
public class DummyPlugin implements Command {
	@Parameter
	private ImageJ ij;
	
	@Override
	public void run() {
		final Context context = new Context();
		String fijiExecutable = Config.getFijiExecutable();
		try ( ParallelizationParadigm paradigm = TestParadigm.localImageJServer( fijiExecutable, context ) ) {
			
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("paradigm", paradigm);			
			ij.command().run(UtilizationMonitor.class, true, parameters );
			
			try {
				RotateFile.callRemotePlugin(paradigm, 200);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
