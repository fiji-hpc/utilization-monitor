
package cz.it4i.monitor;

import java.io.IOException;

import cz.it4i.monitor.model.DataLoader;
import cz.it4i.monitor.view.NodeViewController;
import cz.it4i.monitor.view.OverviewViewController;
import cz.it4i.parallel.MultipleHostParadigm;
import cz.it4i.swing_javafx_ui.JavaFXRoutines;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;
import lombok.Setter;
import net.imagej.ImageJ;

public class MainAppFrame {

	@Getter
	@Setter
	private DataLoader dataLoader;

	@Getter
	@Setter
	private Scene nodeScene;

	@Getter
	@Setter
	private Scene overviewScene;

	// Paradigm related variables:
	private MultipleHostParadigm paradigm;

	private ImageJ ij;

	@Getter
	private Stage stage;

	public MainAppFrame(ImageJ ij, MultipleHostParadigm paradigm) {
		ij.context().inject(this);
		this.ij = ij;
		this.paradigm = paradigm;

		JavaFXRoutines.runOnFxThread(() -> this.stage = createStage(
			" Utilization Monitor "));
	}

	private Stage createStage(String windowTitle) {
		this.stage = new Stage();
		stage.initModality(Modality.NONE);
		stage.setResizable(false);
		stage.setTitle(windowTitle);
		stage.initOwner(null);

		return stage;
	}

	public void close() {
		this.stage.close();
	}

	public void init() {

		// The call to runLater() avoid a mix between JavaFX thread and Swing
		// thread.
		Platform.setImplicitExit(false);
		
		JavaFXRoutines.runOnFxThread(() -> {
			initFX(this.stage);
			this.stage.show();

			// On closing the stage stop updating the utilization-monitor
			// samples.
			stage.setOnCloseRequest((WindowEvent we) -> dataLoader
				.stopUpdatingData());
		});
	}

	public void initFX(Stage newStage) {
		// Get the utilization data every second:
		dataLoader = new DataLoader(paradigm, new RealDataGenerator(), this);
		dataLoader.getDataEverySecond();

		// Load the two scenes:
		GridPane overviewFxml = null;
		GridPane nodeFxml = null;
		try {
			// Load the overview scene:
			FXMLLoader loader = new FXMLLoader();
			// Load the FXML files.
			loader.setLocation(getClass().getResource("/OverviewView.fxml"));
			overviewFxml = loader.load();
			// Give the controller access to the main application.
			OverviewViewController overviewViewController = loader.getController();
			overviewViewController.setMainApp(this);
			overviewViewController.initializeBindings();

			// Load the node scene:
			FXMLLoader newLoader = new FXMLLoader();
			newLoader.setLocation(getClass().getResource("/NodeView.fxml"));
			nodeFxml = newLoader.load();
			NodeViewController nodeViewController = newLoader.getController();
			nodeViewController.setMainApp(this);
			nodeViewController.initializeBindings();
		}
		catch (IOException e) {
			ij.log().error(e.toString());
		}
		overviewScene = new Scene(overviewFxml);
		nodeScene = new Scene(nodeFxml);

		newStage.setScene(overviewScene);
	}
}
