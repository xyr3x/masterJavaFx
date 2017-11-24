package controller;

import application.Main;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import model.*;

/*
 * Moritz; 22.11.17
 * Greedy eine Crew durchgehen und diese darstellen lassen
 */
public class DrawingToolGreedy {

	
	private int rectangleSize = 4;
	private AnchorPane canvasContainer = new AnchorPane();
	private Canvas drawCanvas = new Canvas();
	private Group root = new Group();
	
	
	//initialize layout__draw grid lines, draw burning cells
	public Pane initializeLayout(){
		//initialize Container and DrawingCanvas
		canvasContainer.setPrefSize(800, 600);
		drawCanvas.setHeight(Main.GridLength * (rectangleSize + 1) - 1);
		drawCanvas.setWidth(Main.GridLength * (rectangleSize + 1) - 1);
		
		GraphicsContext gc = drawCanvas.getGraphicsContext2D();
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);
		
		//grid malen
		for(int i = 0; i < Main.GridLength; i++){
			//vertikal
			gc.strokeLine((i+1) * (rectangleSize + 1), 0, (i+1) * (rectangleSize + 1), Main.GridLength * (rectangleSize + 1) - 1);
			//horizontal
			gc.strokeLine(0, (i+1) * (rectangleSize + 1), Main.GridLength * (rectangleSize + 1) - 1, (i+1) * (rectangleSize + 1));
			
		}
		
		
		//root.getChildren().addAll(drawCanvas);
		canvasContainer.getChildrenUnmodifiable().add(drawCanvas);
		return canvasContainer;
	}

	
	//draw all the time steps of the given crew
	public void animateCrew(FireFighterCrew crew){
		
	}
	
	
	
	//getter & setter
	public int getRectangleSize() {
		return rectangleSize;
	}

	public void setRectangleSize(int rectangleSize) {
		this.rectangleSize = rectangleSize;
	}
}
