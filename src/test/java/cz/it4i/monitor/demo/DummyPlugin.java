
package cz.it4i.monitor.demo;

import java.io.IOException;

import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.parallel.ParallelizationParadigm;
import org.scijava.plugin.Plugin;

import cz.it4i.parallel.utils.TestParadigm;

@Plugin(headless = true, type = Command.class, menuPath = "Plugins>Utilities>Dummy Plugin")
public class DummyPlugin implements Command {
	@Override
	public void run() {
		final Context context = new Context();
		String fijiExecutable = Config.getFijiExecutable();
		try ( ParallelizationParadigm paradigm = TestParadigm.localImageJServer( fijiExecutable, context ) ) {
			try {
				RotateFile.callRemotePlugin(paradigm, 200);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
