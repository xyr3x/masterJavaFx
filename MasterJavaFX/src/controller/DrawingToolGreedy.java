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
	
	
	
	
	//SAVE OF OLD LAYOUTCONTROLLER
	/*
	 * package application;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import model.*;

public class LayoutController {
	private Main main;

	@FXML
	private Label FitnessLabel;
	@FXML
	private Label CrewLabel;
	@FXML
	private Label GenerationLabel;
	@FXML
	private Button StartButton;
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private Canvas drawCanvas;

	private int rectangleSize = 8;
	private int canvasHeight = Main.GridLength * (rectangleSize + 1) - 1;
	private int canvasWidth = Main.GridLength * (rectangleSize + 1) - 1;

	private Group root = new Group();
	private boolean fighterAtBorder = false;

	public void setMain(Main main) {
		this.main = main;
	}

	@FXML
	private void handleButton() throws InterruptedException {
		// initialize Container and DrawingCanvas
		drawCanvas.setHeight(canvasHeight);
		drawCanvas.setWidth(canvasWidth);

		GraphicsContext gc = drawCanvas.getGraphicsContext2D();
		// draw grid initially
		initalizeDrawing(gc);
		
		for(int i = 0; i < 15; i++){
			gc.setFill(Color.YELLOW);
			// feld 0
			gc.fillRect(0, 0, rectangleSize, rectangleSize);
			
			Thread.sleep(1000);
			gc.setFill(Color.GREEN);
			// feld 0
			gc.fillRect(0, 0, rectangleSize, rectangleSize);
		}
		
		

	}

	private void initalizeDrawing(GraphicsContext gc) {
		gc.setLineWidth(1);
		gc.setStroke(Color.BLACK);

		// grid malen
		for (int i = 0; i < Main.GridLength; i++) {
			// vertikal
			gc.strokeLine((i + 1) * (rectangleSize + 1), 0, (i + 1) * (rectangleSize + 1),
					Main.GridLength * (rectangleSize + 1) - 1);
			// horizontal
			gc.strokeLine(0, (i + 1) * (rectangleSize + 1), Main.GridLength * (rectangleSize + 1) - 1,
					(i + 1) * (rectangleSize + 1));

		}

		// cells malen -- inital alle rot
		gc.setFill(Color.RED);
		for (int i = 0; i < Main.GridLength; i++) {
			for (int j = 0; j < Main.GridLength; j++) {
				// feld 0
				gc.fillRect(0, 0, rectangleSize, rectangleSize);
				// reihe 0
				gc.fillRect((i + 1) * (rectangleSize + 1), 0, rectangleSize, rectangleSize);
				// spalte 0
				gc.fillRect(0, (i + 1) * (rectangleSize + 1), rectangleSize, rectangleSize);
				// alle anderen
				gc.fillRect((i + 1) * (rectangleSize + 1), (j + 1) * (rectangleSize + 1), rectangleSize, rectangleSize);
			}
		}

		FireFighterCrew crew = new FireFighterCrew();
		// Crew testen
		for (int i = 0; i < Main.CrewSize; i++) {
			FireFighter fighter = new FireFighter();
			fighter.setStartVertice(50 + (2 * i));
			fighter.setCurrentVertice(fighter.getStartVertice());
			int[] chain = new int[Main.TimeInterval];
			for (int j = 0; j < Main.TimeInterval; j++) {
				chain[j] = 1;
			}
			fighter.setChain(chain);
			crew.getCrew().add(fighter);
		}

		drawCrew(crew, gc);

	}

	private void drawCrew(FireFighterCrew crew, GraphicsContext gc) {
		int temp, spalte, zeile, spalteNeu, zeileNeu;

		gc.setFill(Color.BLACK);
		// draw initial setup
		for (int i = 0; i < Main.CrewSize; i++) {
			temp = crew.getCrew().get(i).getCurrentVertice();
			spalte = temp % Main.GridLength;
			zeile = temp / Main.GridLength;
			
			//wichtiger move
			gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);

		}

		// draw every time step
		// vertices that do not burn
		SortedSet<Integer> nonBurningVertices = new TreeSet();
		// Vertices of the last timestep
		List<Integer> latestVertices = new ArrayList<>();
		// defended vertices
		SortedSet<Integer> defendedVertices = new TreeSet();		
		
		// move fighters (switch case unterscheidung), expand fire
		int tempDirection, currentVertice;
		// for every time step
		timeloop: for (int i = 0; i < Main.TimeInterval; i++) {
			
			

			// move every fighter

			fighterloop: for (int j = 0; j < Main.CrewSize; j++) {
				currentVertice = crew.getCrew().get(j).getCurrentVertice();
				spalte = currentVertice % Main.GridLength;
				zeile = currentVertice / Main.GridLength;
				tempDirection = crew.getCrew().get(j).getChainIndex(i);

				// Randfälle, bleibe stehenn wenn Grid zu Ende//Rand rausnehmen
				// Ecken: 0; GridLength; GridLength^2 - (GridLength);
				// GridLength^2 - 1
				if (currentVertice == 0 + Main.GridLength + 1) {
					if (tempDirection == 3 || tempDirection == 4) {
						fighterAtBorder = true;
						continue fighterloop;
					}
				}

				if (currentVertice == Main.GridLength + Main.GridLength - 1) {
					if (tempDirection == 2 || tempDirection == 3) {
						fighterAtBorder = true;
						continue fighterloop;
					}
				}

				if (currentVertice == (Main.GridSize - Main.GridLength - Main.GridLength + 1)) {
					if (tempDirection == 1 || tempDirection == 4) {
						fighterAtBorder = true;
						continue fighterloop;
					}
				}

				if (currentVertice == (Main.GridSize - 1 - Main.GridLength - 1)) {
					if (tempDirection == 1 || tempDirection == 2) {
						fighterAtBorder = true;
						continue fighterloop;
					}
				}

				// Rand des Grids
				// unten
				if (currentVertice < Main.GridLength + Main.GridLength) {
					if (tempDirection == 3) {
						fighterAtBorder = true;
						continue fighterloop;
					}
				}

				// oben
				if (currentVertice > (Main.GridSize - Main.GridLength - Main.GridLength)) {
					if (tempDirection == 1) {
						fighterAtBorder = true;
						continue fighterloop;
					}
				}

				// links
				if ((currentVertice % Main.GridLength) == 1) {
					if (tempDirection == 4) {
						fighterAtBorder = true;
						continue fighterloop;
					}
				}

				// rechts
				if ((currentVertice % Main.GridLength) == (Main.GridLength - 2)) {
					if (tempDirection == 2) {
						fighterAtBorder = true;
						continue fighterloop;
					}
				}

				switch (tempDirection) {
				// dont move
				case 0:
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					break;
				// go north
				case 1:
					// Zielknoten besetzt
					for (int k = 0; k < Main.CrewSize; k++) {
						if ((currentVertice + Main.GridLength) == crew.getCrew().get(k).getCurrentVertice()) {
							defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
							continue fighterloop;
						}
					}
					
					//malen
					spalteNeu = (currentVertice + Main.GridLength) % Main.GridLength;
					zeileNeu = (currentVertice + Main.GridLength) / Main.GridLength;
					//alten Knoten löschen
					gc.setFill(Color.WHITE);
					gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
					//neuen Knoten defenden
					gc.setFill(Color.BLACK);
					gc.fillRect((spalteNeu) * (rectangleSize + 1), canvasHeight + 1 - ((zeileNeu + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
					
					
					crew.getCrew().get(j).setCurrentVertice(currentVertice + Main.GridLength);
					nonBurningVertices.add(currentVertice);
					latestVertices.add(currentVertice);
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());

					break;
				// go east
				case 2:
					// Zielknoten besetzt
					for (int k = 0; k < Main.CrewSize; k++) {
						if ((currentVertice + 1) == crew.getCrew().get(k).getCurrentVertice()) {
							defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
							continue fighterloop;
						}
					}
					
					//malen
					spalteNeu = (currentVertice + 1) % Main.GridLength;
					zeileNeu = (currentVertice + 1) / Main.GridLength;
					//alten Knoten löschen
					gc.setFill(Color.WHITE);
					gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
					//neuen Knoten defenden
					gc.setFill(Color.BLACK);
					gc.fillRect((spalteNeu) * (rectangleSize + 1), canvasHeight + 1 - ((zeileNeu + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
					

					crew.getCrew().get(j).setCurrentVertice(currentVertice + 1);
					nonBurningVertices.add(currentVertice);
					latestVertices.add(currentVertice);
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					break;
				// go south
				case 3:
					// Zielknoten besetzt
					for (int k = 0; k < Main.CrewSize; k++) {
						if ((currentVertice - Main.GridLength) == crew.getCrew().get(k).getCurrentVertice()) {
							defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
							continue fighterloop;
						}
					}
					
					//malen
					spalteNeu = (currentVertice - Main.GridLength) % Main.GridLength;
					zeileNeu = (currentVertice - Main.GridLength) / Main.GridLength;
					//alten Knoten löschen
					gc.setFill(Color.WHITE);
					gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
					//neuen Knoten defenden
					gc.setFill(Color.BLACK);
					gc.fillRect((spalteNeu) * (rectangleSize + 1), canvasHeight + 1 - ((zeileNeu + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
					
					
					crew.getCrew().get(j).setCurrentVertice(currentVertice - Main.GridLength);
					nonBurningVertices.add(currentVertice);
					latestVertices.add(currentVertice);
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					break;
				// go west
				case 4:
					// Zielknoten besetzt
					for (int k = 0; k < Main.CrewSize; k++) {
						if ((currentVertice - 1) == crew.getCrew().get(k).getCurrentVertice()) {
							defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
							continue fighterloop;
						}
					}
					
					//malen
					spalteNeu = (currentVertice - 1) % Main.GridLength;
					zeileNeu = (currentVertice - 1) / Main.GridLength;
					//alten Knoten löschen
					gc.setFill(Color.WHITE);
					gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
					//neuen Knoten defenden
					gc.setFill(Color.BLACK);
					gc.fillRect((spalteNeu) * (rectangleSize + 1), canvasHeight + 1 - ((zeileNeu + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
					
					crew.getCrew().get(j).setCurrentVertice(currentVertice - 1);
					nonBurningVertices.add(currentVertice);
					latestVertices.add(currentVertice);
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					break;

				}
			}

			// expand fire

			for (int k = 0; k < latestVertices.size(); k++) {
				// listPrinter(nonBurningVertices);
				
				spalte = (latestVertices.get(k).intValue()) % Main.GridLength;
				zeile = (latestVertices.get(k).intValue()) / Main.GridLength;
				gc.setFill(Color.RED);
				
				// Randfälle! verlassener Knoten liegt am Rand/Ecke
				if (latestVertices.get(k).intValue() == 0) {
					// only check upper and right vertice
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {							
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

				}

				if (latestVertices.get(k).intValue() == Main.GridLength) {
					// only check upper and left vertice
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}
				}

				if (latestVertices.get(k).intValue() == (Main.GridSize - Main.GridLength)) {
					// only check lower and right vertice
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}
				}

				if (latestVertices.get(k).intValue() == (Main.GridSize - 1)) {
					// only check lower and left vertice
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}
				}

				// Rand des Grids
				// unten
				if (latestVertices.get(k).intValue() < Main.GridLength) {
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}
				}

				// oben
				if (latestVertices.get(k).intValue() > (Main.GridSize - Main.GridLength)) {
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}

					}
				}

				// links
				if ((latestVertices.get(k).intValue() % Main.GridLength) == 0) {
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}
				}

				// rechts
				if ((latestVertices.get(k).intValue() % Main.GridLength) == (Main.GridLength - 1)) {
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
							//malen							
							gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
							nonBurningVertices.remove(latestVertices.get(k));
							continue;
						}
					}
				}

				// check if latestvertices has burning neighbours __ kein
				// Randfall!
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
						//malen							
						gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
						//malen							
						gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						//malen							
						gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						//malen							
						gc.fillRect((spalte) * (rectangleSize + 1), canvasHeight + 1 - ((zeile + 1) * (rectangleSize + 1)), rectangleSize, rectangleSize);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

			}
		

			latestVertices.clear();
			defendedVertices.clear();
			
			//smoother animation
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		nonBurningVertices.clear();
	}
	*/

}

