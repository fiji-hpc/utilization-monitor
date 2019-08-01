package cz.it4i.monitor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cz.it4i.monitor.UtilizationDataCollector;
import cz.it4i.parallel.MultipleHostParadigm;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DataLoader {
	private SimpleIntegerProperty selectedNodeProperty = new SimpleIntegerProperty();

	private SimpleIntegerProperty availableProcessorsProperty = new SimpleIntegerProperty();

	private SimpleDoubleProperty totalPhysicalMemorySizeProperty = new SimpleDoubleProperty();

	private SimpleDoubleProperty freePhysicalMemorySizeProperty = new SimpleDoubleProperty();

	private SimpleDoubleProperty systemCpuLoadProperty = new SimpleDoubleProperty();

	private SimpleDoubleProperty memoryUtilizationProperty = new SimpleDoubleProperty();

	private SimpleDoubleProperty committedVirtualMemorySizeProperty = new SimpleDoubleProperty();

	private SimpleDoubleProperty totalSwapSpaceSizeProperty = new SimpleDoubleProperty();

	private SimpleDoubleProperty freeSwapSpaceSizeProperty = new SimpleDoubleProperty();

	private SimpleDoubleProperty processCpuLoadProperty = new SimpleDoubleProperty();

	private SimpleDoubleProperty processCpuTimeProperty = new SimpleDoubleProperty();

	private SimpleDoubleProperty systemLoadAverageProperty = new SimpleDoubleProperty();

	private SimpleStringProperty nameProperty = new SimpleStringProperty();

	private SimpleStringProperty archProperty = new SimpleStringProperty();

	private SimpleStringProperty versionProperty = new SimpleStringProperty();

	private SimpleStringProperty vmNameProperty = new SimpleStringProperty();

	private SimpleStringProperty vmVendorProperty = new SimpleStringProperty();

	private SimpleStringProperty vmVersionProperty = new SimpleStringProperty();

	private ObservableList<String> classPathObservableList = FXCollections.observableArrayList();

	private ObservableList<XYChart.Series<Double, Double>> cpuObservableDataSeries = FXCollections
			.observableArrayList();

	private ObservableList<XYChart.Series<Double, Double>> memoryObservableDataSeries = FXCollections
			.observableArrayList();

	private ObservableList<XYChart.Series<Double, Double>> swapObservableDataSeries = FXCollections
			.observableArrayList();

	private ObservableList<XYChart.Series<Double, Double>> processObservableDataSeries = FXCollections
			.observableArrayList();

	private ObservableList<XYChart.Series<Double, Double>> averageObservableDataSeries = FXCollections
			.observableArrayList();

	private ObservableList<NodeInfo> tableData = FXCollections.observableArrayList();

	private int numberOfNodes = 0;

	private int selectedNode = 0;

	// Variables used only inside this class:
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private ScheduledExecutorService scheduledExecutorService;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private List<NodeInfo> nodeInfoList = new ArrayList<>();

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private boolean firstTime = true;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Random rand = new Random();

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private long fakeTime = 0;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private boolean testMode = false;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private MultipleHostParadigm paradigm;

	// Test mode uses a fake data generator, used to work on GUI without the need to
	// run remotely.
	public DataLoader(MultipleHostParadigm paradigm, boolean testMode) {
		this.paradigm = paradigm;
		this.testMode = testMode;
	}

	public void getDataEverySecond() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(() -> Platform.runLater(this::updateObservables), 0, 1,
				TimeUnit.SECONDS);
	}

	public void updateObservables() {
		this.runMonitor();

		// Update the table row data:
		for (int index = 0; index < this.tableData.size(); index++) {
			this.tableData.set(index, this.nodeInfoList.get(index));
		}

		// Update the data series in the charts:
		int historySize = this.nodeInfoList.get(selectedNode).getNumberOfItemsInHistory();
		Series<Double, Double> cpuSeries = new Series<>();
		Series<Double, Double> memorySeries = new Series<>();
		Series<Double, Double> swapSeries = new Series<>();
		Series<Double, Double> processSeries = new Series<>();
		Series<Double, Double> averageSeries = new Series<>();
		for (int time = 0; time < historySize; time++) {
			NodeInfo selectedNodeInfo = this.nodeInfoList.get(selectedNode);

			// Set the observable properties:
			Long totalPhysicalMemorySize = (Long) selectedNodeInfo.getDataFromHistory(time, "totalPhysicalMemorySize");
			totalPhysicalMemorySizeProperty.set(totalPhysicalMemorySize * Math.pow(10, -9));

			Integer availableProcessors = (Integer) selectedNodeInfo.getDataFromHistory(time, "availableProcessors");
			availableProcessorsProperty.set(availableProcessors.intValue());

			Long totalSwapSpaceSize = (Long) selectedNodeInfo.getDataFromHistory(time, "totalSwapSpaceSize");
			totalSwapSpaceSizeProperty.set(totalSwapSpaceSize * Math.pow(10, -9));

			Long freeSwapSpaceSize = (Long) selectedNodeInfo.getDataFromHistory(time, "freeSwapSpaceSize");
			freeSwapSpaceSizeProperty.set(freeSwapSpaceSize * Math.pow(10, -9));

			Long committedVirtualMemorySize = (Long) selectedNodeInfo.getDataFromHistory(time,
					"committedVirtualMemorySize");
			committedVirtualMemorySizeProperty.set(committedVirtualMemorySize * Math.pow(10, -9));

			Double processCpuLoad = (Double) selectedNodeInfo.getDataFromHistory(time, "processCpuLoad");
			processCpuLoadProperty.set(processCpuLoad * 100);

			Long processCpuTime = (Long) selectedNodeInfo.getDataFromHistory(time, "processCpuTime");
			processCpuTimeProperty.set(processCpuTime * Math.pow(10, -9));

			Double systemLoadAverage = (Double) selectedNodeInfo.getDataFromHistory(time, "systemLoadAverage");
			systemLoadAverageProperty.set(systemLoadAverage);

			String name = (String) selectedNodeInfo.getDataFromHistory(time, "name");
			nameProperty.set(name);

			String arch = (String) selectedNodeInfo.getDataFromHistory(time, "arch");
			archProperty.set(arch);

			String version = (String) selectedNodeInfo.getDataFromHistory(time, "version");
			versionProperty.set(version);

			String vmName = (String) selectedNodeInfo.getDataFromHistory(time, "vmName");
			vmNameProperty.set(vmName);

			String vmVendor = (String) selectedNodeInfo.getDataFromHistory(time, "vmVendor");
			vmVendorProperty.set(vmVendor);

			String vmVersion = (String) selectedNodeInfo.getDataFromHistory(time, "vmVersion");
			vmVersionProperty.set(vmVersion);

			// Get the class path and spit it in lines:
			String classPath = (String) selectedNodeInfo.getDataFromHistory(time, "classPath");
			List<String> tempClassPathList = Arrays.asList(classPath.split("\\s*;\\s*"));
			for (int index = 0; index < tempClassPathList.size(); index++) {
				String aPath = tempClassPathList.get(index);
				if (index < classPathObservableList.size()) {
					classPathObservableList.set(index, aPath);
				} else {
					classPathObservableList.add(aPath);
				}
			}

			cpuSeries.setName("CPU Utilization");
			memorySeries.setName("Memory Utilization");
			swapSeries.setName("Swap Utilization");
			processSeries.setName("Process Load");
			averageSeries.setName("System Load Average");
			Long uptime = (Long) selectedNodeInfo.getDataFromHistory(time, "uptime");
			Double cpuUtilization = (Double) selectedNodeInfo.getDataFromHistory(time, "systemCpuLoad");
			systemCpuLoadProperty.set(cpuUtilization * 100);
			Double memoryUtilization = (Double) selectedNodeInfo.getDataFromHistory(time, "memoryUtilization");
			memoryUtilizationProperty.set(memoryUtilization * 100);
			Long freePhysicalMemorySize = (Long) selectedNodeInfo.getDataFromHistory(time, "freePhysicalMemorySize");
			freePhysicalMemorySizeProperty.set(((double) freePhysicalMemorySize) / Math.pow(10, 9));

			cpuSeries.getData().add(new XYChart.Data<>(uptime / 1000.0,
				cpuUtilization * 100));
			memorySeries.getData().add(new XYChart.Data<>(uptime / 1000.0,
				memoryUtilization * 100));
			swapSeries.getData().add(new XYChart.Data<>(uptime / 1000.0,
				(totalSwapSpaceSize - freeSwapSpaceSize) / (double) totalSwapSpaceSize *
					100));
			processSeries.getData()
				.add(new XYChart.Data<>(processCpuTime * Math.pow(10, -9),
					processCpuLoad * 100));
			averageSeries.getData().add(new XYChart.Data<>(uptime / 1000.0,
				systemLoadAverage * 100));
		}
		if (cpuObservableDataSeries.isEmpty()) {
			cpuObservableDataSeries.add(cpuSeries);
			memoryObservableDataSeries.add(memorySeries);
			swapObservableDataSeries.add(swapSeries);
			processObservableDataSeries.add(processSeries);
			averageObservableDataSeries.add(averageSeries);
		} else {
			cpuObservableDataSeries.set(0, cpuSeries);
			memoryObservableDataSeries.set(0, memorySeries);
			swapObservableDataSeries.set(0, swapSeries);
			processObservableDataSeries.set(0, processSeries);
			averageObservableDataSeries.set(0, averageSeries);
		}
	}

	private void runMonitor() {
		List<Map<String, Object>> allData;

		// Get the real or fake data depending on mode:
		if (!testMode) {
			Map<String, Object> parameters = new HashMap<>();
			allData = paradigm.runOnHosts(UtilizationDataCollector.class.getName(), parameters, paradigm.getHosts());
		} else {
			allData = fakeRunAll();
		}

		// Count the number of nodes from the first response:
		if (firstTime) {
			numberOfNodes = allData.size();
			for (int i = 0; i < numberOfNodes; i++) {
				nodeInfoList.add(new NodeInfo(i));
			}
			firstTime = false;

			// Insert nodes:
			for (int index = 0; index < this.numberOfNodes; index++) {
				this.tableData.add(new NodeInfo(index));
				this.nodeInfoList.add(new NodeInfo(index));
			}
		}

		// Add new data to the history of each node:
		for (int index = 0; index < numberOfNodes; index++) {
			nodeInfoList.get(index).addToHistory(allData.get(index));
		}
	}

	// Fake runAll function that returns mock-up data:
	private List<Map<String, Object>> fakeRunAll() {
		fakeTime += 1000;
		int fakeNumberOfNodes = 20;
		List<Map<String, Object>> allData = new LinkedList<>();
		for (int index = 0; index < fakeNumberOfNodes; index++) {
			Map<String, Object> aNodeData = new HashMap<>();
			aNodeData.put("uptime", fakeTime);
			double fakeMemoryUtilization = rand.nextDouble();
			aNodeData.put("memoryUtilization", fakeMemoryUtilization);
			aNodeData.put("systemCpuLoad", rand.nextDouble());
			aNodeData.put("availableProcessors", 128);
			aNodeData.put("totalPhysicalMemorySize", 64 * (long) Math.pow(10, 9));
			aNodeData.put("committedVirtualMemorySize", 729411584L);
			aNodeData.put("totalSwapSpaceSize", 12489793536L);
			aNodeData.put("freeSwapSpaceSize", 5837094912L);
			aNodeData.put("processCpuLoad", rand.nextDouble());
			aNodeData.put("processCpuTime", fakeTime * (long) Math.pow(10, 6));
			aNodeData.put("systemLoadAverage", rand.nextDouble() * (fakeNumberOfNodes + 1));
			aNodeData.put("name", "Fake-Linux");
			aNodeData.put("arch", "amd64");
			aNodeData.put("version", "3.10.0-957.12.2.el7.x86_64");
			aNodeData.put("vmVendor", "Fake Corporation");
			aNodeData.put("vmName", "Fake Virtual Machine 64bit");
			aNodeData.put("vmVersion", "fake-1.0");
			aNodeData.put("freePhysicalMemorySize", 16L * (long) Math.pow(10, 9));
			aNodeData.put("classPath", "one;two;three;");
			allData.add(aNodeData);
		}
		return allData;
	}

	public void stopUpdatingData() {
		scheduledExecutorService.shutdownNow();
	}
}
