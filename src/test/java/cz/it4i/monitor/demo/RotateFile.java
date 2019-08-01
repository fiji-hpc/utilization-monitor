
package cz.it4i.monitor.demo;

import static cz.it4i.parallel.Routines.runWithExceptionHandling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.scijava.Context;
import org.scijava.parallel.ParallelizationParadigm;

import cz.it4i.parallel.Routines;
import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.plugins.commands.imglib.RotateImageXY;

/**
 * Demonstration example showing basic usage of ParalellizationParadigm with
 * ImageJ server started in local system. It downloads a picture (Lena) and
 * rotate it for 170 and 340 degree. Result is stored into directory 'output'
 * located in working directory.
 * 
 * @author koz01
 */
public class RotateFile {

	private static final String OUTPUT_DIRECTORY = "output";

	private final static int step = 170;

	private static DatasetIOService ioService;

	public static void callRemotePlugin(final ParallelizationParadigm paradigm, int numberOfTimesToRun)
			throws IOException {
		if (ioService == null) {
			ioService = new Context().getService(DatasetIOService.class);
		}
		final List<Map<String, Object>> parametersList = initParameters(ioService);

		List<Map<String, Object>> results = null;
		for (int i = 0; i < numberOfTimesToRun; i++) {
			results = paradigm.runAll(RotateImageXY.class, parametersList);
		}

		saveOutputs(parametersList, results);
	}

	public static List<Map<String, Object>> initParameters(DatasetIOService ioServiceLocal) throws IOException {
		final List<Map<String, Object>> parametersList = new LinkedList<>();
		Dataset dataset = ioServiceLocal.open(ExampleImage.lenaAsTempFile().toString());
		for (double angle = step; angle < 360; angle += step) {
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("dataset", dataset);
			parameters.put("angle", angle);
			parametersList.add(parameters);
		}
		return parametersList;
	}

	private static void saveOutputs(List<Map<String, Object>> parametersList, List<Map<String, Object>> results) {
		final Path outputDirectory = prepareOutputDirectory();
		final Iterator<Map<String, Object>> inputIterator = parametersList.iterator();
		for (Map<String, ?> result : results) {
			final Double angle = (Double) inputIterator.next().get("angle");
			final Path outputFile = outputDirectory.resolve("result_" + angle + ".png");
			runWithExceptionHandling(() -> ioService.save((Dataset) result.get("dataset"), outputFile.toString()));
		}
	}

	public static Path prepareOutputDirectory() {
		Path outputDirectory = Paths.get(OUTPUT_DIRECTORY);
		if (!Files.exists(outputDirectory)) {
			Routines.runWithExceptionHandling(() -> Files.createDirectories(outputDirectory));
		}
		return outputDirectory;
	}
}
