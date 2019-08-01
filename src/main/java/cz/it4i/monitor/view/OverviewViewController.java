package cz.it4i.monitor.view;

import java.text.DecimalFormat;

import cz.it4i.monitor.MainAppFrame;
import cz.it4i.monitor.model.NodeInfo;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Paint;

public class OverviewViewController {
	@FXML
	private TableView<NodeInfo> table;

	@FXML
	private TableColumn<NodeInfo, Double> nodeIdColumn;

	@FXML
	private TableColumn<NodeInfo, Double> cpuUtilizationColumn;

	@FXML
	private TableColumn<NodeInfo, Double> memoryUtilizationColumn;

	@FXML
	private TableColumn<NodeInfo, Double> systemLoadAverageColumn;

	private DecimalFormat format = new DecimalFormat("0.00");
	
	private boolean hasBeenExecutedOnce = false;

	// Reference to the main application.
	private MainAppFrame mainAppFrame;

	public void setMainApp(MainAppFrame mainAppFrame) {
		this.mainAppFrame = mainAppFrame;

		// Add observable list data to the table
		table.setItems(mainAppFrame.getDataLoader().getTableData());
	}

	// Update the cell colors according to utilization and set the double formating:
	private void setColumnStyle(TableColumn<NodeInfo, Double> column) {
		column.setCellFactory(cell -> new TableCell<NodeInfo, Double>() {
			@Override
			protected void updateItem(Double item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					setText(format.format(item * 100));
					setTextFill(Paint.valueOf("black"));
					setStyle("-fx-background-color:" + cellColor(item) + ";");
				}
			}
		});
	}

	// Update the cell colors according to utilization and set the double formating,
	// numeric value can be greater than one:
	private void setAverageLoadColumnStyle(TableColumn<NodeInfo, Double> column) {
		column.setCellFactory(cell -> new TableCell<NodeInfo, Double>() {
			@Override
			protected void updateItem(Double item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
					setStyle("");
				} else {
					setText(format.format(item));
					setTextFill(Paint.valueOf("black"));
					String color = "#ad60bd";
					if (item <= mainAppFrame.getDataLoader().getNumberOfNodes()) {
						color = cellColor(item / mainAppFrame.getDataLoader().getNumberOfNodes());
					}
					setStyle("-fx-background-color: " + color + " ;");
				}
			}
		});
	}

	// Helper method that sets the appropriate color according to the utilization
	// level:
	private String cellColor(Double item) {
		String[] colors = { "#04f700", "#9ff700", "#dff700", "#f7dd00", "#f7b400", "#f78f00", "#f76e00", "#f73300",
				"#f71900", "#f70000" };

		return colors[(int) Math.floor(item * 9)];
	}

	public void initializeBindings() {
		if(hasBeenExecutedOnce) {
			return;
		}
		hasBeenExecutedOnce = true;
		
		// Initialize the table with the four columns.
		this.nodeIdColumn.setCellValueFactory(new PropertyValueFactory<>("nodeId"));
		this.cpuUtilizationColumn.setCellValueFactory(new PropertyValueFactory<>("cpuUtilization"));
		this.memoryUtilizationColumn.setCellValueFactory(new PropertyValueFactory<>("memoryUtilization"));
		this.systemLoadAverageColumn.setCellValueFactory(new PropertyValueFactory<>("systemLoadAverage"));

		// Update the cell colors according to utilization:
		setColumnStyle(cpuUtilizationColumn);
		setColumnStyle(memoryUtilizationColumn);
		setAverageLoadColumnStyle(systemLoadAverageColumn);

		// Table row double-click event:
		table.setRowFactory(tv -> {
			TableRow<NodeInfo> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {

					NodeInfo clickedRow = row.getItem();
					mainAppFrame.getDataLoader().setSelectedNode(clickedRow.getNodeId());
					mainAppFrame.getDataLoader().getSelectedNodeProperty()
							.set(mainAppFrame.getDataLoader().getSelectedNode());

					mainAppFrame.getDataLoader().updateObservables();

					mainAppFrame.getFxPanel().setScene(mainAppFrame.getNodeScene());
				}
			});
			return row;
		});
	}

}