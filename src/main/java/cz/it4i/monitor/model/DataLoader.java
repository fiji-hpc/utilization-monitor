
package cz.it4i.monitor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cz.it4i.monitor.DataGenerator;
import cz.it4i.monitor.MainAppFrame;
import cz.it4i.parallel.MultipleHostParadigm;
import cz.it4i.swing_javafx_ui.JavaFXRoutines;
import cz.it4i.swing_javafx_ui.SimpleDialog;
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

	private SimpleIntegerProperty selectedNodeProperty =
		new SimpleIntegerProperty();

	private SimpleIntegerProperty availableProcessorsProperty =
		new SimpleIntegerProperty();

	private SimpleDoubleProperty totalPhysicalMemorySizeProperty =
		new SimpleDoubleProperty();

	private SimpleDoubleProperty freePhysicalMemorySizeProperty =
		new SimpleDoubleProperty();

	private SimpleDoubleProperty systemCpuLoadProperty =
		new SimpleDoubleProperty();

	private SimpleDoubleProperty memoryUtilizationProperty =
		new SimpleDoubleProperty();

	private SimpleDoubleProperty committedVirtualMemorySizeProperty =
		new SimpleDoubleProperty();

	private SimpleDoubleProperty totalSwapSpaceSizeProperty =
		new SimpleDoubleProperty();

	private SimpleDoubleProperty freeSwapSpaceSizeProperty =
		new SimpleDoubleProperty();

	private SimpleDoubleProperty processCpuLoadProperty =
		new SimpleDoubleProperty();

	private SimpleDoubleProperty processCpuTimeProperty =
		new SimpleDoubleProperty();

	private SimpleDoubleProperty systemLoadAverageProperty =
		new SimpleDoubleProperty();

	private SimpleStringProperty nameProperty = new SimpleStringProperty();

	private SimpleStringProperty archProperty = new SimpleStringProperty();

	private SimpleStringProperty versionProperty = new SimpleStringProperty();

	private SimpleStringProperty vmNameProperty = new SimpleStringProperty();

	private SimpleStringProperty vmVendorProperty = new SimpleStringProperty();

	private SimpleStringProperty vmVersionProperty = new SimpleStringProperty();

	private ObservableList<String> classPathObservableList = FXCollections
		.observableArrayList();

	private ObservableList<XYChart.Series<Double, Double>> cpuObservableDataSeries =
		FXCollections.observableArrayList();

	private ObservableList<XYChart.Series<Double, Double>> memoryObservableDataSeries =
		FXCollections.observableArrayList();

	private ObservableList<XYChart.Series<Double, Double>> swapObservableDataSeries =
		FXCollections.observableArrayList();

	private ObservableList<XYChart.Series<Double, Double>> processObservableDataSeries =
		FXCollections.observableArrayList();

	private ObservableList<XYChart.Series<Double, Double>> averageObservableDataSeries =
		FXCollections.observableArrayList();

	private ObservableList<NodeInfo> tableData = FXCollections
		.observableArrayList();

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
	private MultipleHostParadigm paradigm;

	private DataGenerator dataGenerator;

	private MainAppFrame mainAppFrame;

	// Change data generator to use real or fake data, fake data are useful in
	// order to test the GUI.
	public DataLoader(MultipleHostParadigm paradigm, DataGenerator dataGenerator,
		MainAppFrame mainAppFrame)
	{
		this.paradigm = paradigm;
		this.dataGenerator = dataGenerator;
		this.mainAppFrame = mainAppFrame;
	}

	public void getDataEverySecond() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(() -> Platform.runLater(
			this::updateObservables), 0, 1, TimeUnit.SECONDS);
	}

	public void updateObservables() {
		this.runMonitor();

		// Update the table row data:
		for (int index = 0; index < this.tableData.size(); index++) {
			this.tableData.set(index, this.nodeInfoList.get(index));
		}

		// Update the data series in the charts:
		int historySize = this.nodeInfoList.get(selectedNode)
			.getNumberOfItemsInHistory();
		Series<Double, Double> cpuSeries = new Series<>();
		Series<Double, Double> memorySeries = new Series<>();
		Series<Double, Double> swapSeries = new Series<>();
		Series<Double, Double> processSeries = new Series<>();
		Series<Double, Double> averageSeries = new Series<>();
		for (int time = 0; time < historySize; time++) {
			NodeInfo selectedNodeInfo = this.nodeInfoList.get(selectedNode);

			// Set the observable properties:
			long totalPhysicalMemorySize = ((Number) selectedNodeInfo
				.getDataFromHistory(time, "totalPhysicalMemorySize")).longValue();
			totalPhysicalMemorySizeProperty.set(totalPhysicalMemorySize * Math.pow(10,
				-9));

			Integer availableProcessors = (Integer) selectedNodeInfo
				.getDataFromHistory(time, "availableProcessors");
			availableProcessorsProperty.set(availableProcessors.intValue());

			long totalSwapSpaceSize = ((Number) selectedNodeInfo.getDataFromHistory(
				time, "totalSwapSpaceSize")).longValue();
			totalSwapSpaceSizeProperty.set(totalSwapSpaceSize * Math.pow(10, -9));

			long freeSwapSpaceSize = ((Number) selectedNodeInfo.getDataFromHistory(
				time, "freeSwapSpaceSize")).longValue();
			freeSwapSpaceSizeProperty.set(freeSwapSpaceSize * Math.pow(10, -9));

			long committedVirtualMemorySize = ((Number) selectedNodeInfo
				.getDataFromHistory(time, "committedVirtualMemorySize")).longValue();
			committedVirtualMemorySizeProperty.set(committedVirtualMemorySize * Math
				.pow(10, -9));

			Double processCpuLoad = (Double) selectedNodeInfo.getDataFromHistory(time,
				"processCpuLoad");
			processCpuLoadProperty.set(processCpuLoad * 100);

			long processCpuTime = ((Number) selectedNodeInfo.getDataFromHistory(time,
				"processCpuTime")).longValue();
			processCpuTimeProperty.set(processCpuTime * Math.pow(10, -9));

			Double systemLoadAverage = (Double) selectedNodeInfo.getDataFromHistory(
				time, "systemLoadAverage");
			systemLoadAverageProperty.set(systemLoadAverage);

			String name = (String) selectedNodeInfo.getDataFromHistory(time, "name");
			nameProperty.set(name);

			String arch = (String) selectedNodeInfo.getDataFromHistory(time, "arch");
			archProperty.set(arch);

			String version = (String) selectedNodeInfo.getDataFromHistory(time,
				"version");
			versionProperty.set(version);

			String vmName = (String) selectedNodeInfo.getDataFromHistory(time,
				"vmName");
			vmNameProperty.set(vmName);

			String vmVendor = (String) selectedNodeInfo.getDataFromHistory(time,
				"vmVendor");
			vmVendorProperty.set(vmVendor);

			String vmVersion = (String) selectedNodeInfo.getDataFromHistory(time,
				"vmVersion");
			vmVersionProperty.set(vmVersion);

			// Get the class path and spit it in lines:
			String classPath = (String) selectedNodeInfo.getDataFromHistory(time,
				"classPath");
			List<String> tempClassPathList = Arrays.asList(classPath.split(
				"\\s*;\\s*"));
			for (int index = 0; index < tempClassPathList.size(); index++) {
				String aPath = tempClassPathList.get(index);
				if (index < classPathObservableList.size()) {
					classPathObservableList.set(index, aPath);
				}
				else {
					classPathObservableList.add(aPath);
				}
			}

			cpuSeries.setName("CPU Utilization");
			memorySeries.setName("Memory Utilization");
			swapSeries.setName("Swap Utilization");
			processSeries.setName("Process Load");
			averageSeries.setName("System Load Average");
			long uptime = ((Number) selectedNodeInfo.getDataFromHistory(time,
				"uptime")).longValue();
			Double cpuUtilization = (Double) selectedNodeInfo.getDataFromHistory(time,
				"systemCpuLoad");
			systemCpuLoadProperty.set(cpuUtilization * 100);
			Double memoryUtilization = (Double) selectedNodeInfo.getDataFromHistory(
				time, "memoryUtilization");
			memoryUtilizationProperty.set(memoryUtilization * 100);
			long freePhysicalMemorySize = ((Number) selectedNodeInfo
				.getDataFromHistory(time, "freePhysicalMemorySize")).longValue();
			freePhysicalMemorySizeProperty.set(freePhysicalMemorySize / Math.pow(10,
				9));

			cpuSeries.getData().add(new XYChart.Data<>(uptime / 1000.0,
				cpuUtilization * 100));
			memorySeries.getData().add(new XYChart.Data<>(uptime / 1000.0,
				memoryUtilization * 100));
			swapSeries.getData().add(new XYChart.Data<>(uptime / 1000.0,
				(totalSwapSpaceSize - freeSwapSpaceSize) / (double) totalSwapSpaceSize *
					100));
			processSeries.getData().add(new XYChart.Data<>(processCpuTime * Math.pow(
				10, -9), processCpuLoad * 100));
			averageSeries.getData().add(new XYChart.Data<>(uptime / 1000.0,
				systemLoadAverage * 100));
		}
		if (cpuObservableDataSeries.isEmpty()) {
			cpuObservableDataSeries.add(cpuSeries);
			memoryObservableDataSeries.add(memorySeries);
			swapObservableDataSeries.add(swapSeries);
			processObservableDataSeries.add(processSeries);
			averageObservableDataSeries.add(averageSeries);
		}
		else {
			cpuObservableDataSeries.set(0, cpuSeries);
			memoryObservableDataSeries.set(0, memorySeries);
			swapObservableDataSeries.set(0, swapSeries);
			processObservableDataSeries.set(0, processSeries);
			averageObservableDataSeries.set(0, averageSeries);
		}
	}

	private void runMonitor() {
		List<Map<String, Object>> allData;

		// Get the data:
		allData = dataGenerator.generate(paradigm);

		// If there were no data returned then the paradigm is no longer inactive.
		if (allData.isEmpty()) {
			// Stop collecting data and close the utilization-monitor:
			stopUpdatingData();
			JavaFXRoutines.runOnFxThread(() -> SimpleDialog.showError(
				"Paradigm is not active",
				"Please close this error dialog, activate a paradigm and try again."));
			mainAppFrame.close();
			return;
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

	public void stopUpdatingData() {
		scheduledExecutorService.shutdownNow();
	}
}
