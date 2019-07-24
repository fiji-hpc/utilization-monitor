package cz.it4i.monitor.view;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import cz.it4i.monitor.MainAppFrame;

public class NodeViewController {
    @FXML
    private Label nodeLabel;
    
    @FXML
    private Label totalPhysicalMemorySizeLabel;
    
    @FXML
    private Label availableProcessorsLabel;
    
    @FXML
    private Label systemCpuLoadLabel;
    
    @FXML
    private Label memoryUtilizationLabel;
    
    @FXML
    private Label freePhysicalMemorySizeLabel;
    
    @FXML
    private Label committedVirtualMemorySizeLabel;
    
    @FXML
    private Label totalSwapSpaceSizeLabel;
    
    @FXML
    private Label freeSwapSpaceSizeLabel;
    
    @FXML
    private Label processCpuLoadLabel;
    
    @FXML
    private Label processCpuTimeLabel;
    
    @FXML
    private Label systemLoadAverageLabel;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private Label archLabel;
    
    @FXML
    private Label versionLabel;
    
    @FXML
    private Label vmVendorLabel;
    
    @FXML
    private Label vmNameLabel;
    
    @FXML
    private Label vmVersionLabel;    
    
    @FXML
    private LineChart<Double, Double> cpuUtilizationLineChart;
    
    @FXML
    private LineChart<Double, Double> memoryUtilizationLineChart;
    
    @FXML
    private LineChart<Double, Double> swapUtilizationLineChart;
    
    @FXML
    private LineChart<Double, Double> processLoadLineChart;
    
    @FXML
    private LineChart<Double, Double> averageLoadLineChart;
    
    @FXML
    private ListView<String> classPathListView = new ListView<String>();
    
    @FXML
    private NumberAxis xAxisCpuUtilization;
    
    @FXML
    private NumberAxis yAxisCpuUtilization;
    
    @FXML
    private NumberAxis xAxisMemoryUtilization;
    
    @FXML
    private NumberAxis yAxisMemoryUtilization;
    
    @FXML
    private NumberAxis xAxisSwapUtilization;
    
    @FXML
    private NumberAxis yAxisSwapUtilization;
    
    @FXML
    private NumberAxis xAxisProcessLoad;
    
    @FXML
    private NumberAxis yAxisProcessLoad;
    
    @FXML
    private NumberAxis xAxisAverageLoad;
    
    @FXML
    private NumberAxis yAxisAverageLoad;
    
    private MainAppFrame mainAppFrame;
    
    private final String format = "%.2f";
  
    public NodeViewController() {
    }

    @FXML
    private void initialize() {
    	nodeLabel.textProperty().bind(MainAppFrame.selectedNodeProperty.asString());
    	
    	totalPhysicalMemorySizeLabel.textProperty().bind(MainAppFrame.totalPhysicalMemorySizeProperty.asString(format));
    	availableProcessorsLabel.textProperty().bind(MainAppFrame.availableProcessorsProperty.asString());
    	systemCpuLoadLabel.textProperty().bind(MainAppFrame.systemCpuLoadProperty.asString(format));
    	memoryUtilizationLabel.textProperty().bind(MainAppFrame.memoryUtilizationProperty.asString(format));
    	freePhysicalMemorySizeLabel.textProperty().bind(MainAppFrame.freePhysicalMemorySizeProperty.asString(format));
    	committedVirtualMemorySizeLabel.textProperty().bind(MainAppFrame.committedVirtualMemorySizeProperty.asString(format));
    	totalSwapSpaceSizeLabel.textProperty().bind(MainAppFrame.totalSwapSpaceSizeProperty.asString(format));
    	freeSwapSpaceSizeLabel.textProperty().bind(MainAppFrame.freeSwapSpaceSizeProperty.asString(format));
    	processCpuLoadLabel.textProperty().bind(MainAppFrame.processCpuLoadProperty.asString(format));
    	processCpuTimeLabel.textProperty().bind(MainAppFrame.processCpuTimeProperty.asString(format));
    	systemLoadAverageLabel.textProperty().bind(MainAppFrame.systemLoadAverageProperty.asString(format));
    	nameLabel.textProperty().bind(MainAppFrame.nameProperty);
    	archLabel.textProperty().bind(MainAppFrame.archProperty);
    	versionLabel.textProperty().bind(MainAppFrame.versionProperty);
    	vmVendorLabel.textProperty().bind(MainAppFrame.vmVendorProperty);
    	vmNameLabel.textProperty().bind(MainAppFrame.vmNameProperty);
    	vmVersionLabel.textProperty().bind(MainAppFrame.vmVersionProperty);
    	
    	classPathListView.setItems(MainAppFrame.classPathObservableList);
    	    	
    	setupAxis(xAxisCpuUtilization, yAxisCpuUtilization, "CPU utilization (%) ", false);
    	setupAxis(xAxisMemoryUtilization, yAxisMemoryUtilization, "Memory utilization (%) ", false);
    	setupAxis(xAxisSwapUtilization, yAxisSwapUtilization, "Swap utilization (%)", false);
    	setupAxis(xAxisProcessLoad, yAxisProcessLoad, "Process Load (%)", false);
    	setupAxis(xAxisAverageLoad, yAxisAverageLoad, "System Average Load (%)", true);
    	
    	cpuUtilizationLineChart.setTitle("CPU utilization (%) over uptime (s).");
    	memoryUtilizationLineChart.setTitle("Memory utilization (%) over uptime (s).");
    	swapUtilizationLineChart.setTitle("Swap utilization (%) over uptime (s).");
    	processLoadLineChart.setTitle("Process load (%) over process time (s).");
    	averageLoadLineChart.setTitle("System Average Load (%) over uptime (s).");
    	
    	cpuUtilizationLineChart.setStyle("CHART_COLOR_1: LightSkyBlue ;");
    	memoryUtilizationLineChart.setStyle("CHART_COLOR_1: SlateBlue ;");
    	swapUtilizationLineChart.setStyle("CHART_COLOR_1: Green ;");
    	processLoadLineChart.setStyle("CHART_COLOR_1: Gold ;");
    	averageLoadLineChart.setStyle("CHART_COLOR_1: Violet ;");
    	
    	cpuUtilizationLineChart.setData(MainAppFrame.cpuObservableDataSeries);
    	memoryUtilizationLineChart.setData(MainAppFrame.memoryObservableDataSeries);
    	swapUtilizationLineChart.setData(MainAppFrame.swapObservableDataSeries);
    	processLoadLineChart.setData(MainAppFrame.processObservableDataSeries);
    	averageLoadLineChart.setData(MainAppFrame.averageObservableDataSeries);
    	
    	addCopyContextMenu();
	}
    
    // Add copy context menu to each ListView cell:
    private void addCopyContextMenu() {
    	classPathListView.setCellFactory(lv -> {
        	ListCell<String> cell = new ListCell<>();

        	ContextMenu contextMenu = new ContextMenu();

        	MenuItem editItem = new MenuItem();
        	editItem.textProperty().set("Copy");;
        	editItem.setOnAction(event -> {
        		String item = cell.getItem();
        		// Copy to clipboard:
        		final Clipboard clipboard = Clipboard.getSystemClipboard();
        		final ClipboardContent content = new ClipboardContent();
        		content.putString(item);
        		clipboard.setContent(content);
        	});
        	contextMenu.getItems().addAll(editItem);

        	cell.textProperty().bind(cell.itemProperty());

        	cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
        		if (isNowEmpty) {
        			cell.setContextMenu(null);
        		} else {
        			cell.setContextMenu(contextMenu);
        		}
        	});
        	return cell ;
    	});		
	}

	@FXML
    private void handleOverview() {
        MainAppFrame.fxPanel.setScene(MainAppFrame.overviewScene);
    }
    
    public void setMainApp(MainAppFrame mainAppFrame) {
        this.mainAppFrame = mainAppFrame;
    }
    
    private void setupAxis(NumberAxis xAxis, NumberAxis yAxis, String name, boolean isAutoRanging) {
    	xAxis.setForceZeroInRange(false);
    	xAxis.setLabel("uptime (s)");    	
    	xAxis.setForceZeroInRange(false);
    	
    	yAxis.setLabel(name);
		yAxis.setAutoRanging(isAutoRanging);
		yAxis.setLowerBound(0);
		yAxis.setUpperBound(100);
		yAxis.setTickUnit(10);
		yAxis.setAnimated(false);
    }
}