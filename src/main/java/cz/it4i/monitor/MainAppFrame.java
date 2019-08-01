package cz.it4i.monitor;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;

import cz.it4i.monitor.model.DataLoader;
import cz.it4i.monitor.view.NodeViewController;
import cz.it4i.monitor.view.OverviewViewController;
import cz.it4i.parallel.MultipleHostParadigm;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import lombok.Getter;
import lombok.Setter;
import net.imagej.ImageJ;

public class MainAppFrame {
	
	@Getter
	@Setter
	private DataLoader dataLoader;

	@Getter
	@Setter
	private JFXPanel fxPanel;

	@Getter
	@Setter
	private Scene nodeScene;

	@Getter
	@Setter
	private Scene overviewScene;

	// Paradigm related variables:
	private MultipleHostParadigm paradigm;

	private ImageJ ij;

	private JFrame frame;

	public MainAppFrame(ImageJ ij, MultipleHostParadigm paradigm) {
		ij.context().inject(this);
		this.ij = ij;
		this.paradigm = paradigm;
		this.frame = new JFrame();
		this.frame.setTitle(" Utilization Monitor ");
	}

	/**
	 * Create the JFXPanel that make the link between Swing (IJ) and JavaFX plugin.
	 */
	public void init() {
		fxPanel = new JFXPanel();
		this.frame.add(fxPanel);
		this.frame.setVisible(true);

		// The call to runLater() avoid a mix between JavaFX thread and Swing thread.
		Platform.runLater(() -> initFX(fxPanel));

		this.frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				dataLoader.stopUpdatingData();
			}
		});
	}

	public void initFX(JFXPanel aFxPanel) {
		// Get the utilization data every second:
		dataLoader = new DataLoader(paradigm, false);
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
			// Give the controller access to the main app.
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
		} catch (IOException e) {
			ij.log().error(e.toString());
		}
		overviewScene = new Scene(overviewFxml);
		nodeScene = new Scene(nodeFxml);

		aFxPanel.setScene(overviewScene);
		this.frame.setSize(600, 800);

		// Set a reasonable minimum allowed size:
		Dimension minimum = new Dimension(550, 650);
		this.frame.setMinimumSize(minimum);

		aFxPanel.setVisible(true);
	}
}