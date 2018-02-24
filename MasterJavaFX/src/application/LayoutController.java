package application;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import controller.EvolutionaryAlgo;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.animation.*;
import model.*;

public class LayoutController {
	private Main main;
	private EvolutionaryAlgo evAlgo;

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
	@FXML
	private Pane drawPane;

	private int rectangleSize = 8;
	private int paneHeight = Main.GridLength * (rectangleSize + 1) - 1;
	private int paneWidth = Main.GridLength * (rectangleSize + 1) - 1;
	private int currentCrew = 0;
	private int timestep = 0;
	private boolean drawingFinished = false;

	private List<Rectangle> grid = new ArrayList<Rectangle>();

	public LayoutController() {

	}

	public void setMain(Main main) {
		this.main = main;
	}

	public void setEvAlgo(EvolutionaryAlgo evAlgo) {
		this.evAlgo = evAlgo;
	}

	@FXML
	private void handleButton() {
		FireFighterCrew shownCrew = evAlgo.getPopulation().get(currentCrew);

		// TODO: Möchte initial machen
		// initialize Container and DrawingCanvas
		drawPane.setPrefHeight(paneHeight);
		drawPane.setPrefWidth(paneWidth);
		// draw grid initially
		initalizeDrawing();

		/*
		 * //testing FireFighterCrew crew = new FireFighterCrew(); // Crew testen for
		 * (int i = 0; i < Main.CrewSize; i++) { FireFighter fighter = new
		 * FireFighter(); fighter.setStartVertice(50 + (2 * i));
		 * fighter.setCurrentVertice(fighter.getStartVertice()); int[] chain = new
		 * int[Main.TimeInterval]; for (int j = 0; j < Main.TimeInterval; j++) {
		 * chain[j] = 1; } fighter.setChain(chain); crew.getCrew().add(fighter); }
		 * 
		 * 
		 * //draw Crews drawCrew(crew);
		 */

		draw(shownCrew);
		CrewLabel.setText(Integer.toString(shownCrew.getID()));
		FitnessLabel.setText(Integer.toString(shownCrew.getFitness()));

	}

	private void initalizeDrawing() {

		// cells malen -- inital alle rot
		for (int j = 0; j < Main.GridLength; j++) {
			for (int i = 0; i < Main.GridLength; i++) {
				Rectangle rect = new Rectangle();
				rect.setFill(Color.RED);
				rect.setStroke(Color.BLACK);
				rect.setStrokeWidth(0.5);
				rect.setHeight(rectangleSize);
				rect.setWidth(rectangleSize);
				rect.setX((i) * (rectangleSize + 1));
				rect.setY(paneHeight + 1 - (j + 1) * (rectangleSize + 1));

				grid.add(rect);

			}
		}
		drawPane.getChildren().addAll(grid);

	}

	private void draw(FireFighterCrew crew) {
		LongValue prevNanos = new LongValue(System.nanoTime());
		AnimationTimer drawLoop;

		drawLoop = new AnimationTimer() {			

			@Override
			public void handle(long now) {
				System.out.println("handle timer");

				// calculate elapsed time
				double elapsedTime = (now - prevNanos.value) / 1000000000.0;
				

				// more than 1 second, draw next step
				if (elapsedTime >= 0.33) {
					prevNanos.value = now;
					System.out.println("Timer: " + elapsedTime);
					drawCrew(crew);
				}
			}

		};

		int temp;
		// draw initial setup
		for (int i = 0; i < Main.CrewSize; i++) {
			temp = crew.getCrew().get(i).getCurrentVertice();
			grid.get(temp).setFill(Color.BLACK);
		}
		
		
		// start animaition
		drawLoop.start();
		System.out.println("loop started");
		if (drawingFinished) {
			drawLoop.stop();
			System.out.println("loop stopped");
			drawingFinished = false;
		}

	}

	private void drawCrew(FireFighterCrew crew) {
		int temp;

		// timestep aktualisieren
		if (timestep == Main.CrewSize) {
			timestep = 0;
			drawingFinished = true;
			return;
		} else {
			timestep = timestep + 1;
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

		// move every fighter

		fighterloop: for (int j = 0; j < Main.CrewSize; j++) {
			currentVertice = crew.getCrew().get(j).getCurrentVertice();
			tempDirection = crew.getCrew().get(j).getChainIndex(timestep);

			// Randfälle, bleibe stehenn wenn Grid zu Ende//Rand rausnehmen
			// Ecken: 0; GridLength; GridLength^2 - (GridLength);
			// GridLength^2 - 1
			if (currentVertice == 0 + Main.GridLength + 1) {
				if (tempDirection == 3 || tempDirection == 4) {
					continue fighterloop;
				}
			}

			if (currentVertice == Main.GridLength + Main.GridLength - 1) {
				if (tempDirection == 2 || tempDirection == 3) {
					continue fighterloop;
				}
			}

			if (currentVertice == (Main.GridSize - Main.GridLength - Main.GridLength + 1)) {
				if (tempDirection == 1 || tempDirection == 4) {
					continue fighterloop;
				}
			}

			if (currentVertice == (Main.GridSize - 1 - Main.GridLength - 1)) {
				if (tempDirection == 1 || tempDirection == 2) {
					continue fighterloop;
				}
			}

			// Rand des Grids
			// unten
			if (currentVertice < Main.GridLength + Main.GridLength) {
				if (tempDirection == 3) {
					continue fighterloop;
				}
			}

			// oben
			if (currentVertice > (Main.GridSize - Main.GridLength - Main.GridLength)) {
				if (tempDirection == 1) {
					continue fighterloop;
				}
			}

			// links
			if ((currentVertice % Main.GridLength) == 1) {
				if (tempDirection == 4) {
					continue fighterloop;
				}
			}

			// rechts
			if ((currentVertice % Main.GridLength) == (Main.GridLength - 2)) {
				if (tempDirection == 2) {
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

				// malen
				// alten Knoten löschen
				grid.get(currentVertice).setFill(Color.WHITE);
				// neuen Knoten verteidigen
				grid.get(currentVertice + Main.GridLength).setFill(Color.BLACK);

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

				// malen
				// alten Knoten löschen
				grid.get(currentVertice).setFill(Color.WHITE);
				// neuen Knoten verteidigen
				grid.get(currentVertice + 1).setFill(Color.BLACK);

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

				// malen
				// alten Knoten löschen
				grid.get(currentVertice).setFill(Color.WHITE);
				// neuen Knoten verteidigen
				grid.get(currentVertice - Main.GridLength).setFill(Color.BLACK);

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

				// malen
				// alten Knoten löschen
				grid.get(currentVertice).setFill(Color.WHITE);
				// neuen Knoten verteidigen
				grid.get(currentVertice - 1).setFill(Color.BLACK);

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

			// Randfälle! verlassener Knoten liegt am Rand/Ecke
			if (latestVertices.get(k).intValue() == 0) {
				// only check upper and right vertice
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

			}

			if (latestVertices.get(k).intValue() == Main.GridLength) {
				// only check upper and left vertice
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}
			}

			if (latestVertices.get(k).intValue() == (Main.GridSize - Main.GridLength)) {
				// only check lower and right vertice
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}
			}

			if (latestVertices.get(k).intValue() == (Main.GridSize - 1)) {
				// only check lower and left vertice
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
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
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}
			}

			// oben
			if (latestVertices.get(k).intValue() > (Main.GridSize - Main.GridLength)) {
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}

				}
			}

			// links
			if ((latestVertices.get(k).intValue() % Main.GridLength) == 0) {
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}
			}

			// rechts
			if ((latestVertices.get(k).intValue() % Main.GridLength) == (Main.GridLength - 1)) {
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						// malen
						grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
						nonBurningVertices.remove(latestVertices.get(k));
						continue;
					}
				}
			}

			// check if latestvertices has burning neighbours __ kein
			// Randfall!
			if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
				if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
					// malen
					grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
					nonBurningVertices.remove(latestVertices.get(k));
					continue;
				}
			}

			if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
				if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
					// malen
					grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
					nonBurningVertices.remove(latestVertices.get(k));
					continue;
				}
			}
			if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
				if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
					// malen
					grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
					nonBurningVertices.remove(latestVertices.get(k));
					continue;
				}
			}

			if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
				if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
					// malen
					grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
					nonBurningVertices.remove(latestVertices.get(k));
					continue;
				}
			}

		}
		latestVertices.clear();
		defendedVertices.clear();

		/*
		 * //smoother animation try { TimeUnit.SECONDS.sleep(1); } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		nonBurningVertices.clear();
	}

	/*
	 * private void drawCrew(FireFighterCrew crew){ int temp;
	 * 
	 * // draw initial setup for (int i = 0; i < Main.CrewSize; i++) { temp =
	 * crew.getCrew().get(i).getCurrentVertice();
	 * grid.get(temp).setFill(Color.BLACK); }
	 * 
	 * // draw every time step // vertices that do not burn SortedSet<Integer>
	 * nonBurningVertices = new TreeSet(); // Vertices of the last timestep
	 * List<Integer> latestVertices = new ArrayList<>(); // defended vertices
	 * SortedSet<Integer> defendedVertices = new TreeSet();
	 * 
	 * // move fighters (switch case unterscheidung), expand fire int tempDirection,
	 * currentVertice; // for every time step timeloop: for (int i = 0; i <
	 * Main.TimeInterval; i++) {
	 * 
	 * // move every fighter
	 * 
	 * fighterloop: for (int j = 0; j < Main.CrewSize; j++) { currentVertice =
	 * crew.getCrew().get(j).getCurrentVertice(); tempDirection =
	 * crew.getCrew().get(j).getChainIndex(i);
	 * 
	 * // Randfälle, bleibe stehenn wenn Grid zu Ende//Rand rausnehmen // Ecken: 0;
	 * GridLength; GridLength^2 - (GridLength); // GridLength^2 - 1 if
	 * (currentVertice == 0 + Main.GridLength + 1) { if (tempDirection == 3 ||
	 * tempDirection == 4) { continue fighterloop; } }
	 * 
	 * if (currentVertice == Main.GridLength + Main.GridLength - 1) { if
	 * (tempDirection == 2 || tempDirection == 3) { continue fighterloop; } }
	 * 
	 * if (currentVertice == (Main.GridSize - Main.GridLength - Main.GridLength +
	 * 1)) { if (tempDirection == 1 || tempDirection == 4) { continue fighterloop; }
	 * }
	 * 
	 * if (currentVertice == (Main.GridSize - 1 - Main.GridLength - 1)) { if
	 * (tempDirection == 1 || tempDirection == 2) { continue fighterloop; } }
	 * 
	 * // Rand des Grids // unten if (currentVertice < Main.GridLength +
	 * Main.GridLength) { if (tempDirection == 3) { continue fighterloop; } }
	 * 
	 * // oben if (currentVertice > (Main.GridSize - Main.GridLength -
	 * Main.GridLength)) { if (tempDirection == 1) { continue fighterloop; } }
	 * 
	 * // links if ((currentVertice % Main.GridLength) == 1) { if (tempDirection ==
	 * 4) { continue fighterloop; } }
	 * 
	 * // rechts if ((currentVertice % Main.GridLength) == (Main.GridLength - 2)) {
	 * if (tempDirection == 2) { continue fighterloop; } }
	 * 
	 * switch (tempDirection) { // dont move case 0:
	 * defendedVertices.add(crew.getCrew().get(j).getCurrentVertice()); break; // go
	 * north case 1: // Zielknoten besetzt for (int k = 0; k < Main.CrewSize; k++) {
	 * if ((currentVertice + Main.GridLength) ==
	 * crew.getCrew().get(k).getCurrentVertice()) {
	 * defendedVertices.add(crew.getCrew().get(j).getCurrentVertice()); continue
	 * fighterloop; } }
	 * 
	 * // malen // alten Knoten löschen
	 * grid.get(currentVertice).setFill(Color.WHITE); // neuen Knoten verteidigen
	 * grid.get(currentVertice + Main.GridLength).setFill(Color.BLACK);
	 * 
	 * crew.getCrew().get(j).setCurrentVertice(currentVertice + Main.GridLength);
	 * nonBurningVertices.add(currentVertice); latestVertices.add(currentVertice);
	 * defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
	 * 
	 * break; // go east case 2: // Zielknoten besetzt for (int k = 0; k <
	 * Main.CrewSize; k++) { if ((currentVertice + 1) ==
	 * crew.getCrew().get(k).getCurrentVertice()) {
	 * defendedVertices.add(crew.getCrew().get(j).getCurrentVertice()); continue
	 * fighterloop; } }
	 * 
	 * // malen // alten Knoten löschen
	 * grid.get(currentVertice).setFill(Color.WHITE); // neuen Knoten verteidigen
	 * grid.get(currentVertice + 1).setFill(Color.BLACK);
	 * 
	 * crew.getCrew().get(j).setCurrentVertice(currentVertice + 1);
	 * nonBurningVertices.add(currentVertice); latestVertices.add(currentVertice);
	 * defendedVertices.add(crew.getCrew().get(j).getCurrentVertice()); break; // go
	 * south case 3: // Zielknoten besetzt for (int k = 0; k < Main.CrewSize; k++) {
	 * if ((currentVertice - Main.GridLength) ==
	 * crew.getCrew().get(k).getCurrentVertice()) {
	 * defendedVertices.add(crew.getCrew().get(j).getCurrentVertice()); continue
	 * fighterloop; } }
	 * 
	 * // malen // alten Knoten löschen
	 * grid.get(currentVertice).setFill(Color.WHITE); // neuen Knoten verteidigen
	 * grid.get(currentVertice - Main.GridLength).setFill(Color.BLACK);
	 * 
	 * crew.getCrew().get(j).setCurrentVertice(currentVertice - Main.GridLength);
	 * nonBurningVertices.add(currentVertice); latestVertices.add(currentVertice);
	 * defendedVertices.add(crew.getCrew().get(j).getCurrentVertice()); break; // go
	 * west case 4: // Zielknoten besetzt for (int k = 0; k < Main.CrewSize; k++) {
	 * if ((currentVertice - 1) == crew.getCrew().get(k).getCurrentVertice()) {
	 * defendedVertices.add(crew.getCrew().get(j).getCurrentVertice()); continue
	 * fighterloop; } }
	 * 
	 * // malen // alten Knoten löschen
	 * grid.get(currentVertice).setFill(Color.WHITE); // neuen Knoten verteidigen
	 * grid.get(currentVertice - 1).setFill(Color.BLACK);
	 * 
	 * crew.getCrew().get(j).setCurrentVertice(currentVertice - 1);
	 * nonBurningVertices.add(currentVertice); latestVertices.add(currentVertice);
	 * defendedVertices.add(crew.getCrew().get(j).getCurrentVertice()); break;
	 * 
	 * } }
	 * 
	 * // expand fire
	 * 
	 * for (int k = 0; k < latestVertices.size(); k++) { //
	 * listPrinter(nonBurningVertices);
	 * 
	 * // Randfälle! verlassener Knoten liegt am Rand/Ecke if
	 * (latestVertices.get(k).intValue() == 0) { // only check upper and right
	 * vertice if (!nonBurningVertices.contains((latestVertices.get(k).intValue() +
	 * 1))) { if (!defendedVertices.contains((latestVertices.get(k).intValue() +
	 * 1))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() +
	 * Main.GridLength))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() +
	 * Main.GridLength))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * }
	 * 
	 * if (latestVertices.get(k).intValue() == Main.GridLength) { // only check
	 * upper and left vertice if
	 * (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) { //
	 * malen grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() +
	 * Main.GridLength))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() +
	 * Main.GridLength))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } } }
	 * 
	 * if (latestVertices.get(k).intValue() == (Main.GridSize - Main.GridLength)) {
	 * // only check lower and right vertice if
	 * (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) { //
	 * malen grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() -
	 * Main.GridLength))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() -
	 * Main.GridLength))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } } }
	 * 
	 * if (latestVertices.get(k).intValue() == (Main.GridSize - 1)) { // only check
	 * lower and left vertice if
	 * (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) { //
	 * malen grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() -
	 * Main.GridLength))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() -
	 * Main.GridLength))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } } }
	 * 
	 * // Rand des Grids // unten if (latestVertices.get(k).intValue() <
	 * Main.GridLength) { if
	 * (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) { //
	 * malen grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
	 * if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) { //
	 * malen grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() +
	 * Main.GridLength))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() +
	 * Main.GridLength))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } } }
	 * 
	 * // oben if (latestVertices.get(k).intValue() > (Main.GridSize -
	 * Main.GridLength)) { if
	 * (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) { //
	 * malen grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
	 * if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) { //
	 * malen grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() -
	 * Main.GridLength))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() -
	 * Main.GridLength))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; }
	 * 
	 * } }
	 * 
	 * // links if ((latestVertices.get(k).intValue() % Main.GridLength) == 0) { if
	 * (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) { //
	 * malen grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() +
	 * Main.GridLength))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() +
	 * Main.GridLength))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() -
	 * Main.GridLength))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() -
	 * Main.GridLength))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } } }
	 * 
	 * // rechts if ((latestVertices.get(k).intValue() % Main.GridLength) ==
	 * (Main.GridLength - 1)) { if
	 * (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) { //
	 * malen grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() +
	 * Main.GridLength))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() +
	 * Main.GridLength))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() -
	 * Main.GridLength))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() -
	 * Main.GridLength))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } } }
	 * 
	 * // check if latestvertices has burning neighbours __ kein // Randfall! if
	 * (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) { //
	 * malen grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
	 * if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) { //
	 * malen grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } } if
	 * (!nonBurningVertices.contains((latestVertices.get(k).intValue() +
	 * Main.GridLength))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() +
	 * Main.GridLength))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * if (!nonBurningVertices.contains((latestVertices.get(k).intValue() -
	 * Main.GridLength))) { if
	 * (!defendedVertices.contains((latestVertices.get(k).intValue() -
	 * Main.GridLength))) { // malen
	 * grid.get(latestVertices.get(k).intValue()).setFill(Color.RED);
	 * nonBurningVertices.remove(latestVertices.get(k)); continue; } }
	 * 
	 * } latestVertices.clear(); defendedVertices.clear();
	 * 
	 * 
	 * 
	 * } nonBurningVertices.clear(); }
	 * 
	 * 
	 * 
	 * 
	 */

}

class LongValue {
	public long value;

	public LongValue(long i) {
		value = i;
	}
}
