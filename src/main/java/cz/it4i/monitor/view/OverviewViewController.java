package cz.it4i.monitor.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Paint;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cz.it4i.monitor.MainAppFrame;
import cz.it4i.monitor.model.NodeInfo;

public class OverviewViewController {
    @FXML
    private TableView<NodeInfo> table;
    
    @FXML
    private TableColumn<NodeInfo, Double> nodeIdColumn;
    
    @FXML
    private TableColumn<NodeInfo, Double> cpuUtilizationColumn;
    
    @FXML
    private TableColumn<NodeInfo, Double> memoryUtilizationColumn;

    // Reference to the main application.
    private MainAppFrame mainAppFrame;

    public OverviewViewController() {
    }

    @FXML
    private void initialize() {
        // Initialize the table with the three columns.
    	this.nodeIdColumn.setCellValueFactory(new PropertyValueFactory<>("nodeId"));
    	
    	this.cpuUtilizationColumn.setCellValueFactory(new PropertyValueFactory<>("cpuUtilization"));
    	
    	this.memoryUtilizationColumn.setCellValueFactory(new PropertyValueFactory<>("memoryUtilization"));
    			
		// Update the cell colors according to utilization:
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
		
    	// Update the cell colors according to utilization:
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
		
		// Table row double-click event:
		table.setRowFactory(tv -> {
		    TableRow<NodeInfo> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (! row.isEmpty() && event.getButton()==MouseButton.PRIMARY 
		             && event.getClickCount() == 2) {
		        	
		            NodeInfo clickedRow = row.getItem();
		            MainAppFrame.selectedNode = clickedRow.getNodeId();
		            MainAppFrame.selectedNodeProperty.set(MainAppFrame.selectedNode);		             		       
		            
		            MainAppFrame.fxPanel.setScene(MainAppFrame.nodeScene);
		        }
		    });
		    return row ;
		});
    	    	
    }

    public void setMainApp(MainAppFrame mainAppFrame) {
        this.mainAppFrame = mainAppFrame;
        
        // Add observable list data to the table
        table.setItems(MainAppFrame.tableData);
    }

    // Helper function that sets the appropriate color according to the utilization level:
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
}