package cz.it4i.monitor.view;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;

import cz.it4i.monitor.MainAppFrame;

public class NodeViewController {
    @FXML
    private Label nodeLabel;
    
    @FXML
    private Label totalPhysicalMemorySizeLabel;
    
    @FXML
    private Label availableProcessorsLabel;
    
    @FXML
    private LineChart<Double, Double> cpuUtilizationLineChart;
    
    @FXML
    private LineChart<Double, Double> memoryUtilizationLineChart;
        
    
    @FXML
    private NumberAxis xAxisCpuUtilization;
    
    @FXML
    private NumberAxis yAxisCpuUtilization;
    
    @FXML
    private NumberAxis xAxisMemoryUtilization;
    
    @FXML
    private NumberAxis yAxisMemoryUtilization;
    
    private MainAppFrame mainAppFrame;

    public NodeViewController() {
    	this.xAxisCpuUtilization = new NumberAxis();
    	this.yAxisCpuUtilization = new NumberAxis();
    	this.xAxisMemoryUtilization = new NumberAxis();
    	this.yAxisMemoryUtilization = new NumberAxis();
    }

    @FXML
    private void initialize() {
    	nodeLabel.textProperty().bind(MainAppFrame.selectedNodeProperty.asString());
    	
    	totalPhysicalMemorySizeLabel.textProperty().bind(MainAppFrame.totalPhysicalMemorySizeProperty.asString());
    	availableProcessorsLabel.textProperty().bind(MainAppFrame.availableProcessorsProperty.asString());
    	
    	setupAxis(xAxisCpuUtilization, yAxisCpuUtilization, "CPU utilization (%) ");
    	setupAxis(xAxisMemoryUtilization, yAxisMemoryUtilization, "Memory utilization (%) ");
    	
    	cpuUtilizationLineChart.setTitle("CPU utilization (%) over uptime (s).");
    	memoryUtilizationLineChart.setTitle("Memory utilization (%) over uptime (s).");
    	
    	cpuUtilizationLineChart.setStyle("CHART_COLOR_1: LightSkyBlue ;");
    	memoryUtilizationLineChart.setStyle("CHART_COLOR_1: SlateBlue ;");
    	
    	cpuUtilizationLineChart.setData(MainAppFrame.cpuObservableDataSeries);
    	memoryUtilizationLineChart.setData(MainAppFrame.memoryObservableDataSeries);
    }

    @FXML
    private void handleOverview() {
        MainAppFrame.fxPanel.setScene(MainAppFrame.overviewScene);
    }
    
    public void setMainApp(MainAppFrame mainAppFrame) {
        this.mainAppFrame = mainAppFrame;
    }
    
    private void setupAxis(NumberAxis xAxis, NumberAxis yAxis, String name) {
    	xAxis.setForceZeroInRange(false);
    	xAxis.setLabel("uptime (s)");    	
    	xAxis.setForceZeroInRange(false);
    	
    	yAxis.setLabel(name);
		yAxis.setAutoRanging(false);
		yAxis.setLowerBound(0);
		yAxis.setUpperBound(100);
		yAxis.setTickUnit(10);
		yAxis.setAnimated(false);
    }
}