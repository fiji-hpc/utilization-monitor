package cz.it4i.monitor;

import java.awt.Dimension;
import java.io.IOException;
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

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.GridPane;
import javax.swing.JFrame;
import net.imagej.ImageJ;

import cz.it4i.monitor.model.NodeInfo;
import cz.it4i.monitor.view.NodeViewController;
import cz.it4i.monitor.view.OverviewViewController;
import cz.it4i.parallel.MultipleHostParadigm;

public class MainAppFrame extends JFrame {
	public static SimpleIntegerProperty selectedNodeProperty = new SimpleIntegerProperty(MainAppFrame.class, "selectedNodeProperty");
	
	public static SimpleIntegerProperty availableProcessorsProperty = new SimpleIntegerProperty(MainAppFrame.class, "availableProcessorsProperty");
	
	public static SimpleDoubleProperty totalPhysicalMemorySizeProperty = new SimpleDoubleProperty(MainAppFrame.class, "totalPhysicalMemorySizeProperty");
	
	public static SimpleDoubleProperty freePhysicalMemorySizeProperty = new SimpleDoubleProperty(MainAppFrame.class, "freePhysicalMemorySizeProperty");
	
	public static SimpleDoubleProperty systemCpuLoadProperty = new SimpleDoubleProperty(MainAppFrame.class, "systemCpuLoadProperty");
	
	public static SimpleDoubleProperty memoryUtilizationProperty = new SimpleDoubleProperty(MainAppFrame.class, "systemCpuLoadProperty");
	
	public static SimpleDoubleProperty committedVirtualMemorySizeProperty = new SimpleDoubleProperty(MainAppFrame.class, "committedVirtualMemorySizeProperty");
	
	public static SimpleDoubleProperty totalSwapSpaceSizeProperty = new SimpleDoubleProperty(MainAppFrame.class, "totalSwapSpaceSizeProperty");
	
	public static SimpleDoubleProperty freeSwapSpaceSizeProperty = new SimpleDoubleProperty(MainAppFrame.class, "freeSwapSpaceSizeProperty");
	
	public static SimpleDoubleProperty processCpuLoadProperty = new SimpleDoubleProperty(MainAppFrame.class, "processCpuLoadProperty");
	
	public static SimpleDoubleProperty processCpuTimeProperty = new SimpleDoubleProperty(MainAppFrame.class, "processCpuTimeProperty");
	
	public static SimpleDoubleProperty systemLoadAverageProperty = new SimpleDoubleProperty(MainAppFrame.class, "systemLoadAverageProperty");
	
	public static SimpleStringProperty nameProperty = new SimpleStringProperty(MainAppFrame.class, "nameProperty");
	
	public static SimpleStringProperty archProperty = new SimpleStringProperty(MainAppFrame.class, "archProperty");
	
	public static SimpleStringProperty versionProperty = new SimpleStringProperty(MainAppFrame.class, "versionProperty");
	
	public static SimpleStringProperty vmNameProperty = new SimpleStringProperty(MainAppFrame.class, "vmNameProperty");
	
	public static SimpleStringProperty vmVendorProperty = new SimpleStringProperty(MainAppFrame.class, "vmVendorProperty");
	
	public static SimpleStringProperty vmVersionProperty = new SimpleStringProperty(MainAppFrame.class, "vmVersionProperty");
	
	public static ObservableList<String> classPathObservableList = FXCollections.observableArrayList();
	
	public static ObservableList<XYChart.Series<Double, Double>> cpuObservableDataSeries = FXCollections.observableArrayList();
	
	public static ObservableList<XYChart.Series<Double, Double>> memoryObservableDataSeries = FXCollections.observableArrayList();
	
	public static ObservableList<XYChart.Series<Double, Double>> swapObservableDataSeries = FXCollections.observableArrayList();
	
	public static ObservableList<XYChart.Series<Double, Double>> processObservableDataSeries = FXCollections.observableArrayList();
	
	public static ObservableList<XYChart.Series<Double, Double>> averageObservableDataSeries = FXCollections.observableArrayList();
	
	public static ObservableList<NodeInfo> tableData = FXCollections.observableArrayList();
	    
    public static JFXPanel fxPanel;
    
    public static int selectedNode = 0;
    
    public static int numberOfNodes = 0;
    
    public static Scene nodeScene;
    
    public static Scene overviewScene;
    
    private static ScheduledExecutorService scheduledExecutorService;
    
    private static List<NodeInfo> nodeInfoList = new ArrayList<NodeInfo>();	    

    private static long fakeTime = 0;

    private static boolean firstTime = true;
    
	// Paradigm related variables:
	static MultipleHostParadigm paradigm;
    
    private GridPane overviewFxml;
    
    private GridPane nodeFxml;

    private ImageJ ij;

    public MainAppFrame(ImageJ ij, MultipleHostParadigm paradigm) {
        ij.context().inject(this);
        this.ij = ij;
        this.paradigm = paradigm;
    }

    /**
     * Create the JFXPanel that make the link between Swing (IJ) and JavaFX plugin.
     */
    public void init() {
        this.fxPanel = new JFXPanel();
        this.add(this.fxPanel);
        this.setVisible(true);
        
        // Get the utilization data every second:
        getDataEverySecond();
        
        // The call to runLater() avoid a mix between JavaFX thread and Swing thread.
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
        
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
               scheduledExecutorService.shutdownNow();
            }
        });
    }

    public void initFX(JFXPanel fxPanel) {
    	try {
    		// Load the overview scene:
    		// Load the FXML files.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainAppFrame.class.getResource("view/OverviewView.fxml"));
			overviewFxml = (GridPane) loader.load();
			
			// Give the controller access to the main app.
			OverviewViewController overviewViewController = loader.getController();
	        overviewViewController.setMainApp(this);
	        
	        // Load the node scene:
	        FXMLLoader newLoader = new FXMLLoader();
	        newLoader.setLocation(MainAppFrame.class.getResource("view/NodeView.fxml"));
			nodeFxml = (GridPane) newLoader.load();
			NodeViewController nodeViewController = newLoader.getController();
			nodeViewController.setMainApp(this);
		} catch (IOException e) {
			e.printStackTrace();
		}    	
        overviewScene = new Scene(overviewFxml);
    	nodeScene = new Scene(nodeFxml);
    	
		this.fxPanel.setScene(overviewScene);
		this.setSize(600, 800);
		
		// Set a reasonable minimum allowed size:
		Dimension minimum = new Dimension(550, 650);
		this.setMinimumSize(minimum);
		
		this.fxPanel.setVisible(true);
    }
    
    public static List<Map<String, Object>> runMonitor() {
    	List<Map<String, Object>> allData = new LinkedList<>();
    	
    	// Enable the line bellow to use the dummy data for testing instead:
    	//allData = MainAppFrame.fakeRunAll();
    	
    	// Get the real data:
    	HashMap parameters = new HashMap<>();
    	allData = paradigm.runOnHosts(
    		UtilizationDataCollector.class.getName(), parameters, paradigm.getHosts());
    	
    	//System.out.println(allData.toString());
    	
    	// Count the number of nodes from the first response:
    	if(firstTime) {
    		numberOfNodes = allData.size();
    		for(int i = 0; i < numberOfNodes; i++)
    		{
    			nodeInfoList.add(new NodeInfo(i));
    		}
    		firstTime = false;
    		
        	// Insert nodes:
        	for(int index = 0; index < MainAppFrame.numberOfNodes; index++) {    		
        		MainAppFrame.tableData.add(new NodeInfo(index));
        		MainAppFrame.nodeInfoList.add(new NodeInfo(index));
    		}
    	}
    	
    	
    	for(int index = 0; index < numberOfNodes; index++) {
    		// Add new data to the history of each node:
    		nodeInfoList.get(index).addToHistory(allData.get(index));
    	}
    	
    	return allData;		
	}

    private void getDataEverySecond() {		
    	scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(() -> {
			Platform.runLater(() -> updateObservables());
		}, 0, 1, TimeUnit.SECONDS);
    }

    public static void updateObservables() {
		MainAppFrame.runMonitor();
		
		// Update the table row data: 
		for(int index = 0; index < MainAppFrame.tableData.size(); index++) {
        	MainAppFrame.tableData.set(index, MainAppFrame.nodeInfoList.get(index));
        }
		
		// Update the data series in the charts periodically:
		int historySize = MainAppFrame.nodeInfoList.get(selectedNode).getNumberOfItemsInHistory();
		Series<Double, Double> cpuSeries = new Series<Double, Double>();
		Series<Double, Double> memorySeries = new Series<Double, Double>();
		Series<Double, Double> swapSeries = new Series<Double, Double>();
		Series<Double, Double> processSeries = new Series<Double, Double>();
		Series<Double, Double> averageSeries = new Series<Double, Double>();
		for(int time = 0; time < historySize; time++) {
			NodeInfo selectedNodeInfo = MainAppFrame.nodeInfoList.get(MainAppFrame.selectedNode);
			
			// Set the observable properties:
			Long totalPhysicalMemorySize = (Long) selectedNodeInfo.getDataFromHistory(
				time, "totalPhysicalMemorySize");
			totalPhysicalMemorySizeProperty.set(totalPhysicalMemorySize/Math.pow(10, 9));
			
			Integer availableProcessors = (Integer)selectedNodeInfo.getDataFromHistory(time, "availableProcessors");
			availableProcessorsProperty.set(availableProcessors.intValue());
			
			Long totalSwapSpaceSize = (Long) selectedNodeInfo
				.getDataFromHistory(time, "totalSwapSpaceSize");
			totalSwapSpaceSizeProperty.set(totalSwapSpaceSize/Math.pow(10, 9));
			
			Long freeSwapSpaceSize = (Long)selectedNodeInfo.getDataFromHistory(time, "freeSwapSpaceSize");
			freeSwapSpaceSizeProperty.set(freeSwapSpaceSize/Math.pow(10, 9));
			
			Long committedVirtualMemorySize = (Long)selectedNodeInfo.getDataFromHistory(time, "committedVirtualMemorySize");
			committedVirtualMemorySizeProperty.set(committedVirtualMemorySize/Math.pow(10, 9));
			
			Double processCpuLoad = (Double)selectedNodeInfo.getDataFromHistory(time, "processCpuLoad");
			processCpuLoadProperty.set(processCpuLoad*100);
			
			Long processCpuTime = (Long) selectedNodeInfo.getDataFromHistory(time,
				"processCpuTime");
			processCpuTimeProperty.set(processCpuTime*Math.pow(10, -9));
			
			Double systemLoadAverage = (Double)selectedNodeInfo.getDataFromHistory(time, "systemLoadAverage");
			systemLoadAverageProperty.set(systemLoadAverage);
			
			String name = (String)selectedNodeInfo.getDataFromHistory(time, "name");
			nameProperty.set(name);
			
			String arch = (String)selectedNodeInfo.getDataFromHistory(time, "arch");
			archProperty.set(arch);
			
			String version = (String)selectedNodeInfo.getDataFromHistory(time, "version");
			versionProperty.set(version);	
			
			String vmName = (String)selectedNodeInfo.getDataFromHistory(time, "vmName");
			vmNameProperty.set(vmName);
			
			String vmVendor = (String)selectedNodeInfo.getDataFromHistory(time, "vmVendor");
			vmVendorProperty.set(vmVendor);
			
			String vmVersion = (String)selectedNodeInfo.getDataFromHistory(time, "vmVersion");
			vmVersionProperty.set(vmVersion);
			
			String classPath = (String)selectedNodeInfo.getDataFromHistory(time, "classPath");
			List<String> tempClassPathList = Arrays.asList(classPath.split("\\s*;\\s*"));
			for(int index = 0; index < tempClassPathList.size(); index++) {
				String aPath = tempClassPathList.get(index);
				if(index < classPathObservableList.size()) {
					classPathObservableList.set(index, aPath);
				} else
				{
					classPathObservableList.add(aPath);
				}
					
			}
			
			cpuSeries.setName("CPU Utilization");
			memorySeries.setName("Memory Utilization");
			swapSeries.setName("Swap Utilization");
			processSeries.setName("Process Load");
			averageSeries.setName("System Load Average");
			Long uptime = (Long)selectedNodeInfo.getDataFromHistory(time, "uptime");
			Double cpuUtilization = (Double)selectedNodeInfo.getDataFromHistory(time, "systemCpuLoad");
			systemCpuLoadProperty.set(cpuUtilization*100);
			Double memoryUtilization = (Double)selectedNodeInfo.getDataFromHistory(time, "memoryUtilization");
			memoryUtilizationProperty.set(memoryUtilization*100);
			Long freePhysicalMemorySize = (Long)selectedNodeInfo.getDataFromHistory(time, "freePhysicalMemorySize");
			freePhysicalMemorySizeProperty.set(((double)freePhysicalMemorySize)/((double)Math.pow(10, 9)));
			
			cpuSeries.getData().add(
					new XYChart.Data<Double, Double>(uptime / 1000.0, cpuUtilization*100)
			);
			memorySeries.getData().add(
					new XYChart.Data<Double, Double>(uptime / 1000.0, memoryUtilization*100)
			);
			swapSeries.getData().add(
					new XYChart.Data<Double, Double>(uptime / 1000.0, (double) ((totalSwapSpaceSize-freeSwapSpaceSize)/(double)totalSwapSpaceSize*100))
			);
			processSeries.getData().add(
					new XYChart.Data<Double, Double>(processCpuTime * Math.pow(10, -9), processCpuLoad*100)
			);
			averageSeries.getData().add(
					new XYChart.Data<Double, Double>(uptime/1000.0, systemLoadAverage*100) 
			);
		}
		if(cpuObservableDataSeries.size() == 0) {
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

	// Fake runAll function that returns mockup data:
    private static List<Map<String, Object>> fakeRunAll() {
    	Random rand = new Random();
    	fakeTime += 1000;
    	int fakeNumberOfNodes = 20;
    	List<Map<String, Object>> allData = new LinkedList<>();
		for(int index = 0; index < fakeNumberOfNodes; index++) {
    		Map<String, Object> aNodeData = new HashMap<String, Object>();
    		aNodeData.put("uptime", fakeTime);
    		aNodeData.put("memoryUtilization", rand.nextDouble());
    		aNodeData.put("systemCpuLoad", rand.nextDouble());
    		aNodeData.put("availableProcessors", 128);
    		aNodeData.put("totalPhysicalMemorySize", 64.0 * Math.pow(10, 9));
    		aNodeData.put("committedVirtualMemorySize", 729411584L);
    		aNodeData.put("totalSwapSpaceSize", 12489793536L);
    		aNodeData.put("freeSwapSpaceSize", 5837094912L);
    		aNodeData.put("processCpuLoad", rand.nextDouble());
    		aNodeData.put("processCpuTime", fakeTime*Math.pow(10, 6));
    		aNodeData.put("systemLoadAverage", rand.nextDouble()*(fakeNumberOfNodes+1));
    		aNodeData.put("name", "Fake-Linux");
    		aNodeData.put("arch", "amd64");
    		aNodeData.put("version", "3.10.0-957.12.2.el7.x86_64");
    		aNodeData.put("vmVendor", "Fake Corporation");
    		aNodeData.put("vmName", "Fake Virtual Machine 64bit");
    		aNodeData.put("vmVersion","fake-1.0");
    		aNodeData.put("classPath", "one;two;three;");
    		allData .add(aNodeData);
    	}
    	return allData;
    }
}