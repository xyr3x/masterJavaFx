package application;
	
import java.util.Random;

import controller.DrawingToolGreedy;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class Main extends Application {
	public static int FighterID = 0;
	public static int CrewID = 0;
	public static int GridLength = 30;
	public static int GridSize = GridLength * GridLength;
	public static int TimeInterval = 15;
	public static Random rnd = new Random(1337);

	public static int CrewSize = 10;
	public static int PopulationSize = 100;
	public static int RecombinationSize = PopulationSize / 2;
	public static int MutationProbability = 15;
	
	
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			DrawingToolGreedy dt = new DrawingToolGreedy();
			ScrollPane root = new ScrollPane();
			AnchorPane canvasContainer = (AnchorPane) dt.initializeLayout();
			root.getChildrenUnmodifiable().add(canvasContainer);
			
			
			
			//BorderPane root = new BorderPane();
			Scene scene = new Scene(root,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
