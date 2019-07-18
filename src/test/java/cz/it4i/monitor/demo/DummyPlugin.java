
package cz.it4i.monitor.demo;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import cz.it4i.monitor.UtilizationMonitor;
import cz.it4i.parallel.Host;
import cz.it4i.parallel.MultipleHostParadigm;
import cz.it4i.parallel.imagej.server.ImageJServerParadigm;
import cz.it4i.parallel.runners.HPCSettings;
import cz.it4i.parallel.ui.HPCImageJServerRunnerWithUI;
import cz.it4i.parallel.ui.HPCSettingsGui;
import net.imagej.ImageJ;
import net.imagej.ops.math.PrimitiveMath;
import net.imagej.plugins.commands.imglib.RotateImageXY;



@Plugin(headless = true, type = Command.class, menuPath = "Plugins>Utilities>Dummy Plugin")
public class DummyPlugin implements Command {
	@Parameter
	private ImageJ ij;
	
	private static Context context;
	
	@Override
	public void run() {
		
		context = ij.context();
		try ( MultipleHostParadigm paradigm = constructParadigm()) {
//			Map<String, Object> parameters = new HashMap<>();
//			parameters.put("paradigm", paradigm);			
//			ij.command().run(UtilizationMonitor.class, true, parameters );
//			
////			try {
//				//RotateFile.callRemotePlugin(paradigm, 200);
//				this.callSimpleRemotePlugin(paradigm);				
//				
////			} catch (IOException e) {
////				e.printStackTrace();
////			}
		}
	}
	
	// Simple numeric command example:
	public void callSimpleRemotePlugin(MultipleHostParadigm paradigm) {
				List<Map<String, Object>> parametersList = new LinkedList<>();
				Map<String, Object> parameters = new HashMap<>();
				parameters.put("a", 1.0);
				parameters.put("b", 1.0);
				parametersList.add(parameters);				
				paradigm.runAll(PrimitiveMath.DoubleMultiply.class, parametersList);
	}
	
	private static MultipleHostParadigm constructParadigm()
		{
			HashMap<Object, Object> requestData = new HashMap<>();
			HPCSettings settings = null;
			boolean shutDownOnClose = true;
			shutDownOnClose = false;
			settings = HPCSettingsGui.showDialog(context);
			requestData.put(HPCSettings.class, settings);
			final HPCSettings finalHpcSettings = settings;

			final HPCImageJServerRunnerWithUI runner = new HPCImageJServerRunnerWithUI(finalHpcSettings, shutDownOnClose);
			
			MultipleHostParadigm innerParadigm = new ImageJServerParadigm();
			runner.start();
			List<Host> hosts = Host.constructListFromNamesAndCores(runner.getRemoteHosts(), runner.getNCores());			
			innerParadigm.setHosts(hosts);
			innerParadigm.init();		
			System.out.println("Used these hosts: "+hosts.toString());
			
			return innerParadigm;
		}

}
