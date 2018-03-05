package application;
	
import java.io.IOException;
import java.util.Random;

import controller.DrawingToolGreedy;
import controller.EvolutionaryAlgo;
import controller.EvolutionaryAlgoConnected;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;


public class Main extends Application {
	public static int FighterID = 0;
	public static int CrewID = 0;
	public static int GridLength = 100;
	public static int GridSize = GridLength * GridLength;
	public static int TimeInterval = 20;
	public static Random rnd = new Random(1337);

	public static int CrewSize = 15;
	public static int PopulationSize = 100;
	public static int RecombinationSize = PopulationSize / 2;
	public static int MutationProbability = 0;
	private Stage primaryStage;
	private BorderPane rootLayout;
	EvolutionaryAlgo evAlgo = new EvolutionaryAlgo();
	EvolutionaryAlgoConnected evAlgoConnected = new EvolutionaryAlgoConnected();
	
	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			this.primaryStage = primaryStage;
			this.primaryStage.setTitle("FireFighter");

			initRootLayout();
			showLayout();
			//startAlgo();
			

			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void initRootLayout() {
		try {
			// Load fxml file for RootLayout
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show Scene containing RootLayout
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);

			// Give the controller access to the main app.

			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

		}
	
	
	public void showLayout() {
		try {
			// Load fxml file for LineOverview
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("LayoutNew.fxml"));
			AnchorPane startScreen = (AnchorPane) loader.load();

			// lineOerview in Center of RootLayout
			rootLayout.setCenter(startScreen);

			LayoutController controller = loader.getController();
			controller.setMain(this);
			controller.setEvAlgo(evAlgo);
			controller.setEvAlgoConnected(evAlgoConnected);

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*private void startAlgo(){		
		//evolutionaryAlgo ausführen
		//dafür service aufrufen
		EvolutionaryAlgoService service = new EvolutionaryAlgoService();
		service.setEvAlgo(evAlgo);
		service.setMain(this);
		
		
		
		service.start();
	}*/
	
	public static void main(String[] args) {
		launch(args);
	}
}
