package cz.it4i.imagej;

import net.imagej.ImageJ;
import net.imagej.ops.OpService;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import com.sun.management.OperatingSystemMXBean;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

@Plugin(type = Command.class, menuPath = "Plugins>Utilities>Utilization Explorer")
public class UtilizationMonitor implements Command {

	@Parameter
    private ImageJ ij;
   
    @Override
    public void run() {
        // Launch JavaFX interface
        MainAppFrame app = new MainAppFrame(ij);
        app.setTitle(" Utilization Monitor ");
        app.init();
    }
}
