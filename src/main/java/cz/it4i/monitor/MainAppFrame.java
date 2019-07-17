package cz.it4i.monitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javax.swing.JFrame;
import net.imagej.ImageJ;

import org.scijava.Context;
import org.scijava.log.LogService;
import org.scijava.parallel.ParallelizationParadigm;
import org.scijava.plugin.Parameter;

import cz.it4i.parallel.MultipleHostParadigm;
import cz.it4i.parallel.utils.TestParadigm;

public class MainAppFrame extends JFrame {

    private ScheduledExecutorService scheduledExecutorService;
    
    private final int MAX_POINTS_ON_CHART = 30;
    
    private Scene nodeScene;
    
    private Scene overviewScene;
    
    private int selectedNode = 0;
    
    private int numberOfNodes = 0;
    
    private SimpleIntegerProperty selectedNodeProperty = new SimpleIntegerProperty(this, "selectedNodeProperty");
    
	// Paradigm related variables:
	final MultipleHostParadigm paradigm;
	
    @Parameter
    private LogService log;

    private ImageJ ij;

    private JFXPanel fxPanel;

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
        
        // When the Swing Frame closes close the paradigm:
        Platform.setImplicitExit(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent evt) {
            	// Do nothing else.
            }    
        });

        // The call to runLater() avoid a mix between JavaFX thread and Swing thread.
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    public void initFX(JFXPanel fxPanel) {
    	nodeScene = createNodeScene();
    	overviewScene = createOverviewScene();
    	
		this.fxPanel.setScene(overviewScene);
		this.setSize(600, 800);
		this.fxPanel.setVisible(true);
    }
    
    private Scene createOverviewScene() {
    	GridPane grid = new GridPane();
		
		TableView<NodeInfo> table = new TableView<NodeInfo>();
		grid.add(table, 0, 0);
		GridPane.setHgrow(table, Priority.ALWAYS);
		GridPane.setVgrow(table, Priority.ALWAYS);
		
		TableColumn<NodeInfo, Double> nodeIdColumn = new TableColumn<NodeInfo, Double>("Node ID");
		nodeIdColumn.setCellValueFactory(new PropertyValueFactory<>("nodeId"));
		
		TableColumn<NodeInfo, Double> cpuUtilizationColumn = new TableColumn<NodeInfo, Double>("CPU Utilization");
		cpuUtilizationColumn.setCellValueFactory(new PropertyValueFactory<>("cpuUtilization"));
		
		TableColumn<NodeInfo, Double> memoryUtilizationColumn = new TableColumn<NodeInfo, Double>("Memory Utilization");
		memoryUtilizationColumn.setCellValueFactory(new PropertyValueFactory<>("memoryUtilization"));
		
		cpuUtilizationColumn.setCellFactory(column -> {
			return new TableCell<NodeInfo, Double>(){
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					
					if(item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						setText(item.toString());
						setTextFill(Paint.valueOf("black"));
						setStyle("-fx-background-color:"+cellColor(item)+";");
					}
				}
			};
		});
		
		memoryUtilizationColumn.setCellFactory(column -> {
			return new TableCell<NodeInfo, Double>(){
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					
					if(item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						setText(item.toString());
						setTextFill(Paint.valueOf("black"));
						setStyle("-fx-background-color: "+cellColor(item)+";");
						
					}
				}
			};
		});		
		
		ObservableList<NodeInfo> data = FXCollections.observableArrayList();
		
		numberOfNodes = 20; //10000;
		for(int i = 0; i < this.numberOfNodes; i++) {
			data.add(new NodeInfo(i, (double)i/this.numberOfNodes, 1.0 - (double)i/this.numberOfNodes));			
		}
		
		// Table row double-click event:
		table.setRowFactory(tv -> {
		    TableRow<NodeInfo> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (! row.isEmpty() && event.getButton()==MouseButton.PRIMARY 
		             && event.getClickCount() == 2) {

		            NodeInfo clickedRow = row.getItem();
		            this.selectedNode = clickedRow.nodeId;
		            this.selectedNodeProperty.set(this.selectedNode);
		            this.fxPanel.setScene(nodeScene);
		        }
		    });
		    return row ;
		});
		
		table.setItems(data);
		table.getColumns().addAll(nodeIdColumn, cpuUtilizationColumn, memoryUtilizationColumn);
		
    	Scene scene = new Scene(grid, 600, 800);
    	return scene;
    }
    
    private Scene createNodeScene() {
		GridPane grid = new GridPane();
		GridPane subGridPane = new GridPane();
		
		// Labels
		Label label = new Label();
		label.setText("Node: ");
		label.setFont(Font.font(25));
		subGridPane.add(label, 0, 1);
		
		Label nodeLabel = new Label();
		nodeLabel.textProperty().bind(selectedNodeProperty.asString());
		nodeLabel.setFont(Font.font(25));		
		subGridPane.add(nodeLabel, 1, 1);
		
		// Overview Button
		Button overviewButton = new Button("Overview");
		subGridPane.add(overviewButton, 0, 0);
		grid.add(subGridPane, 0, 0);
		overviewButton.setOnAction(e -> this.fxPanel.setScene(overviewScene));  
		
		LineChart<Double, Double> cpuUtilizationLineChart = createLineChart("uptime (ms)", "cpu utilization", "CPU Utilization", "LightSkyBlue", "cpu");
		LineChart<Double, Double> memoryUtilizationLineChart = createLineChart("uptime (ms)", "memory utilization", "Memory Utilization", "SlateBlue", "memory");
		
		GridPane.setHgrow(cpuUtilizationLineChart, Priority.ALWAYS);
		GridPane.setHgrow(memoryUtilizationLineChart, Priority.ALWAYS);
		GridPane.setVgrow(cpuUtilizationLineChart, Priority.ALWAYS);
		GridPane.setVgrow(memoryUtilizationLineChart, Priority.ALWAYS);
		grid.add(cpuUtilizationLineChart, 0, 1);	    
	    grid.add(memoryUtilizationLineChart, 0, 2);
	    
	    Scene scene = new Scene(grid, 600, 800);
	    return scene;
    }
    
    private LineChart<Double, Double> createLineChart(String xAxisLabel, String yAxisLabel, String name, String colour, String type) {
		NumberAxis xAxis = new NumberAxis();
		xAxis.setLabel(xAxisLabel);
		xAxis.setForceZeroInRange(false);
		xAxis.setAnimated(false);
		
		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel(yAxisLabel);
		yAxis.setAutoRanging(false);
		yAxis.setLowerBound(0);
		yAxis.setUpperBound(1);
		yAxis.setTickUnit(0.1);
		yAxis.setAnimated(false);					
		
		LineChart<Double, Double> lineChart = new LineChart(xAxis, yAxis);
		lineChart.setStyle("CHART_COLOR_1: "+colour+" ;");
		lineChart.setAnimated(false);
		
		XYChart.Series<Double, Double> dataSeries = new Series<Double, Double>();
		dataSeries.setName(name);
		
		// Put data in the chart periodically:
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(() -> {
			double[] point = runMonitor(type);
			
			Platform.runLater(() -> {
			        dataSeries.getData().add(new XYChart.Data<>(point[0], point[1]));
			        
			        if (dataSeries.getData().size() > MAX_POINTS_ON_CHART)
			        	dataSeries.getData().remove(0);
		    });
		}, 0, 1, TimeUnit.SECONDS);
		lineChart.getData().add(dataSeries);
		
		return lineChart;
    }
    
    private String cellColor(Double item) {
    	String[] colors = {
    			"#04f700", 
    			"#9ff700", 
    			"#dff700", 
    			"#f7dd00", 
    			"#f7b400", 
    			"#f78f00", 
    			"#f76e00",
    			"#f73300",
    			"#f71900", 
    			"#f70000"
    	};
    	
    	return colors[(int)Math.floor(item * 9)];
    }
    
    private double[] runMonitor(String choice){
	 	double[] point = new double[2];
  	   	
		// Get the utilization from the local Fiji server:
	 	HashMap parameters = new HashMap<>();
		List<Map<String, Object>> results = paradigm.runOnHosts(
				UtilizationDataCollector.class.getName(), parameters, paradigm.getHosts());
    	
    	if(choice == "memory") {
	   		point[0] = new Double( (Integer)results.get(0).get("uptime"));
    		point[1] = (Double) results.get(0).get("memoryUtilization");
    		return point;
    	} else {
      	  		
			point[0] = new Double( (Integer)results.get(0).get("uptime"));
    		point[1] = (Double) results.get(0).get("systemCpuLoad");    		
    		return point;    	
    	}
    }
}