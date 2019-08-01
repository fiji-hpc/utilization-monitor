
package cz.it4i.monitor.demo;

import java.io.IOException;
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
import cz.it4i.parallel.fst.utils.RemoteTestParadigm;
import cz.it4i.parallel.fst.utils.TestFSTRPCParadigm;
import net.imagej.ImageJ;
import net.imagej.ops.math.PrimitiveMath;

@Plugin(headless = true, type = Command.class, menuPath = "Plugins>Utilities>Dummy Plugin")
public class DummyPlugin implements Command {
	@Parameter
	private ImageJ ij;

	@Parameter
	private Context context;

	@Override
	public void run() {
		try (MultipleHostParadigm paradigm = constructParadigm()) {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("paradigm", paradigm);
			ij.command().run(UtilizationMonitor.class, true, parameters);

			try {
				RotateFile.callRemotePlugin(paradigm, 200);
				// this.callSimpleRemotePlugin(paradigm);

			} catch (IOException e) {
				e.printStackTrace();
			}
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

	private MultipleHostParadigm constructParadigm() {
		RemoteTestParadigm testParadigm = TestFSTRPCParadigm.hpcFSTRPCServer(context);

		MultipleHostParadigm innerParadigm = (MultipleHostParadigm) testParadigm.getParadigm();
		List<Host> hosts = Host.constructListFromNamesAndCores(testParadigm.getRemoteHosts(), testParadigm.getNCores());
		System.out.println("Used these hosts: " + hosts.toString());

		return innerParadigm;
	}

	public static void main(final String... args) throws Exception {
		// create the ImageJ application context with all available services
		final ImageJ ij = new ImageJ();
		ij.ui().showUI();

		// invoke the plugin
		ij.command().run(DummyPlugin.class, true);
	}
}
