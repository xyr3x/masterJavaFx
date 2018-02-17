package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import application.Main;
import model.*;

public class EvolutionaryAlgoConnected {
	private List<ConnectedFireFighterCrew> population = new ArrayList<ConnectedFireFighterCrew>();
	private boolean fighterAtBorder = false;

	private int maxFitness = 0;
	private int optimum = Main.CrewSize + 5;
	private ConnectedFireFighterCrew bestCrew = new ConnectedFireFighterCrew();
	private int[] bestSetUp = new int[Main.CrewSize];

	// constructor
	public EvolutionaryAlgoConnected() {

	}

	public void evAlgo() {
		System.out.println("Start");
		// stuff
		int counter = 0;

		// 1. Initialisierung
		initialize();

		// 2. Evaluation
		for (int i = 0; i < population.size(); i++) {
			calculateFitness(population.get(i));
			if (population.get(i).getFitness() > maxFitness) {
				maxFitness = population.get(i).getFitness();
			}
		}
		System.out.println("First Value: " + maxFitness);

		// 3. Schleife
		while (maxFitness < optimum) {
			counter++;
			System.out.println("Schleife Nr: " + counter);

			// 3.1 Selektion
			Collections.sort(population);
			for (int i = 0; i < Main.RecombinationSize; i++) {
				// von hinten Elemente rauswerfen, um Indexshift zu vermeiden
				population.remove(Main.PopulationSize - (i + 1));
			}

			// 3.2. Rekombination //TODO: WICHTIG!! �berarbeiten
			for (int i = 0; i < Main.RecombinationSize; i++) {
				int parent1 = Main.rnd.nextInt(Main.PopulationSize - Main.RecombinationSize);
				int parent2 = Main.rnd.nextInt(Main.PopulationSize - Main.RecombinationSize);
				int crossOver = Main.rnd.nextInt(Main.TimeInterval);

				ConnectedFireFighterCrew crew = new ConnectedFireFighterCrew();

				// recombine parentcrew 1 and parentcrew 2 s.t. every kth
				// new fighter is one point crossover of the kth fighter of the
				// parent crews
				for (int j = 0; j < Main.CrewSize; j++) {
					int chain[] = new int[Main.TimeInterval];
					ConnectedFireFighter fighter = new ConnectedFireFighter();
					// set start vertice
					fighter.setStartVertice(population.get(parent1).getCrew().get(j).getStartVertice());
					// set chain
					for (int k = 0; k < crossOver; k++) {
						chain[k] = population.get(parent1).getCrew().get(j).getChainIndex(k);
					}
					for (int k = crossOver; k < Main.TimeInterval; k++) {
						chain[k] = population.get(parent2).getCrew().get(j).getChainIndex(k);
					}

					crew.getCrew().add(fighter);

				}
				crew.setNewCrew(true);
				population.add(crew);

			}

			// 3.3 Mutation
			if (Main.rnd.nextInt(100) < Main.MutationProbability) {

				// numbers??
				int numberOfCrews = Main.rnd.nextInt(Main.PopulationSize);
				int numberOfFighters = Main.rnd.nextInt(Main.CrewSize);
				int numberOfBitflips = Main.rnd.nextInt(Main.TimeInterval / 4);

				for (int i = 0; i < numberOfCrews; i++) {
					for (int j = 0; j < numberOfFighters; j++) {
						for (int k = 0; k < numberOfBitflips; k++) {
							population.get(i).getCrew().get(j).setChainIndex(Main.rnd.nextInt(Main.TimeInterval),
									Main.rnd.nextInt(5));
						}
					}
					population.get(i).setChanged(true);
				}

			}

			// 3.4 Evaluation
			for (int i = 0; i < population.size(); i++) {
				if (population.get(i).isChanged() || population.get(i).isNewCrew()) {
					calculateFitness(population.get(i));
					if (population.get(i).getFitness() > maxFitness) {
						maxFitness = population.get(i).getFitness();
						bestCrew = population.get(i);

					}
				}

			}
			System.out.println("Fitness: " + maxFitness);
		}

	}

	private void initialize() {
		int[] temp = new int[Main.PopulationSize];
		int tempVertice = 0;

		// intialize every individuum of the population

		for (int i = 0; i < Main.PopulationSize; i++) {
			ConnectedFireFighterCrew crew = new ConnectedFireFighterCrew();
			int[] startVertices = new int[Main.CrewSize];

			// init first fighter
			int startVertice = Main.rnd.nextInt(Main.GridSize);
			ConnectedFireFighter fighter1 = new ConnectedFireFighter();
			fighter1.setStartVertice(startVertice);
			fighter1.setCurrentVertice(startVertice);
			startVertices[0] = startVertice;

			// initialize Chain
			int[] chain = new int[Main.TimeInterval];
			for (int k = 0; k < Main.TimeInterval; k++) {
				chain[k] = Main.rnd.nextInt(5);
			}

			fighter1.setChain(chain);
			crew.getCrew().add(fighter1);

			// initalize every other fighter of the crew
			OuterLoop: for (int j = 1; j < Main.CrewSize; j++) {
				boolean finished = false;
				ConnectedFireFighter tempFighter = new ConnectedFireFighter();
				
				// connect to fighter before
				crew.getCrew().get(j - 1).setRightNeighbour(tempFighter);
				tempFighter.setLeftNeighbour(crew.getCrew().get(j - 1));

				// get Startvertice
				// TODO DAUERSCHLEIFE M�GLICH ---- FIXEN!!!
				int counter = 0;

				Innerloop: while (!finished && counter < 8) {
					// 1 == north, 3 == east, 5 == south, 7 == west
					int tempDirection = Main.rnd.nextInt(8) + 1;
					switch (tempDirection) {
					// north
					case 1:
						if (checkIfValid(tempDirection, tempFighter.getLeftNeighbour().getStartVertice(),
								startVertices)) {
							// vertice is valid, set Startvertice
							tempFighter.setStartVertice(
									tempFighter.getLeftNeighbour().getStartVertice() + Main.GridLength);
							tempFighter.setCurrentVertice(tempFighter.getStartVertice());
							tempFighter.setPositionIndex(1, 0);
							startVertices[j] = tempFighter.getStartVertice();
							finished = true;
						}
						// force restart
						else {
							// try again
							counter += 1;
							continue Innerloop;
						}
						break;

					// north east
					case 2:
						if (checkIfValid(tempDirection, tempFighter.getLeftNeighbour().getStartVertice(),
								startVertices)) {
							// vertice is valid, set Startvertice
							tempFighter.setStartVertice(
									tempFighter.getLeftNeighbour().getStartVertice() + Main.GridLength + 1);
							tempFighter.setCurrentVertice(tempFighter.getStartVertice());
							tempFighter.setPositionIndex(2, 0);
							startVertices[j] = tempFighter.getStartVertice();
							finished = true;
						}
						// force restart
						else {
							// try again
							counter += 1;
							continue Innerloop;
						}
						break;

					// east
					case 3:
						if (checkIfValid(tempDirection, tempFighter.getLeftNeighbour().getStartVertice(),
								startVertices)) {
							// vertice is valid, set Startvertice
							tempFighter.setStartVertice(tempFighter.getLeftNeighbour().getStartVertice() + 1);
							tempFighter.setCurrentVertice(tempFighter.getStartVertice());
							tempFighter.setPositionIndex(3, 0);
							startVertices[j] = tempFighter.getStartVertice();
							finished = true;
						}
						// force restart
						else {
							// try again
							counter += 1;
							continue Innerloop;
						}
						break;

					// south east
					case 4:
						if (checkIfValid(tempDirection, tempFighter.getLeftNeighbour().getStartVertice(),
								startVertices)) {
							// vertice is valid, set Startvertice
							tempFighter.setStartVertice(
									tempFighter.getLeftNeighbour().getStartVertice() - Main.GridLength + 1);
							tempFighter.setCurrentVertice(tempFighter.getStartVertice());
							tempFighter.setPositionIndex(4, 0);
							startVertices[j] = tempFighter.getStartVertice();
							finished = true;
						}
						// force restart
						else {
							// try again
							counter += 1;
							continue Innerloop;
						}
						break;

					// south
					case 5:
						if (checkIfValid(tempDirection, tempFighter.getLeftNeighbour().getStartVertice(),
								startVertices)) {
							// vertice is valid, set Startvertice
							tempFighter.setStartVertice(
									tempFighter.getLeftNeighbour().getStartVertice() - Main.GridLength);
							tempFighter.setCurrentVertice(tempFighter.getStartVertice());
							tempFighter.setPositionIndex(5, 0);
							startVertices[j] = tempFighter.getStartVertice();
							finished = true;
						}
						// force restart
						else {
							// try again
							counter += 1;
							continue Innerloop;
						}
						break;

					// south-west
					case 6:
						if (checkIfValid(tempDirection, tempFighter.getLeftNeighbour().getStartVertice(),
								startVertices)) {
							// vertice is valid, set Startvertice
							tempFighter.setStartVertice(
									tempFighter.getLeftNeighbour().getStartVertice() - Main.GridLength - 1);
							tempFighter.setCurrentVertice(tempFighter.getStartVertice());
							tempFighter.setPositionIndex(6, 0);
							startVertices[j] = tempFighter.getStartVertice();
							finished = true;
						}
						// force restart
						else {
							// try again
							counter += 1;
							continue Innerloop;
						}
						break;

					// west
					case 7:
						if (checkIfValid(tempDirection, tempFighter.getLeftNeighbour().getStartVertice(),
								startVertices)) {
							// vertice is valid, set Startvertice
							tempFighter.setStartVertice(tempFighter.getLeftNeighbour().getStartVertice() - 1);
							tempFighter.setCurrentVertice(tempFighter.getStartVertice());
							tempFighter.setPositionIndex(7, 0);
							startVertices[j] = tempFighter.getStartVertice();
							finished = true;
						}
						// force restart
						else {
							// try again
							counter += 1;
							continue Innerloop;
						}
						break;

					// north-west
					case 8:
						if (checkIfValid(tempDirection, tempFighter.getLeftNeighbour().getStartVertice(),
								startVertices)) {
							// vertice is valid, set Startvertice
							tempFighter.setStartVertice(
									tempFighter.getLeftNeighbour().getStartVertice() + Main.GridLength - 1);
							tempFighter.setCurrentVertice(tempFighter.getStartVertice());
							tempFighter.setPositionIndex(8, 0);
							startVertices[j] = tempFighter.getStartVertice();
							finished = true;
						}
						// force restart
						else {
							// try again
							counter += 1;
							continue Innerloop;
						}
						break;
					}
					
					//Counter = 8; also init fehlgeschlagen, abfangen der dauerschleife
					if (counter == 8) {
						//restart from j = 1
						j = 1;
						continue OuterLoop;
					}
				

					// initialize Chain
					int[] chain2 = new int[Main.TimeInterval];
					int dummy;
					for (int k = 0; k < Main.TimeInterval; k++) {
						dummy = movementCalculator(tempFighter, k);
						//Fehler, Movement nicht m�glich
						if(dummy == -1) {
							//Movement des Vorg�ngers neu berechnen
							k -= 1;
							continue;
						}						
						chain2[k] = dummy;
					}

					tempFighter.setChain(chain2);
					crew.getCrew().add(tempFighter);
				}
			}

			crew.setFitness(Main.CrewSize);
			population.add(crew);

		}

	}

	public void calculateFitness(ConnectedFireFighterCrew crew) {
		// vertices that do not burn
		SortedSet<Integer> nonBurningVertices = new TreeSet();
		// Vertices of the last timestep
		List<Integer> latestVertices = new ArrayList<>();
		// defended vertices
		SortedSet<Integer> defendedVertices = new TreeSet();
		int[] bestSetup = new int[Main.CrewSize];
		int tempFitness = crew.getFitness();

		// move fighters (switch case unterscheidung), expand fire
		int tempDirection, currentVertice;
		// for every time step
		timeloop: for (int i = 0; i < Main.TimeInterval; i++) {

			// move every fighter

			fighterloop: for (int j = 0; j < Main.CrewSize; j++) {
				currentVertice = crew.getCrew().get(j).getCurrentVertice();
				tempDirection = crew.getCrew().get(j).getChainIndex(i);

				// Randf�lle, bleibe stehenn wenn Grid zu Ende//Rand rausnehmen
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
					crew.getCrew().get(j).setCurrentVertice(currentVertice + Main.GridLength);
					crew.setFitness(crew.getFitness() + 1);
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

					crew.getCrew().get(j).setCurrentVertice(currentVertice + 1);
					crew.setFitness(crew.getFitness() + 1);
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
					crew.getCrew().get(j).setCurrentVertice(currentVertice - Main.GridLength);
					crew.setFitness(crew.getFitness() + 1);
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
					crew.getCrew().get(j).setCurrentVertice(currentVertice - 1);
					crew.setFitness(crew.getFitness() + 1);
					nonBurningVertices.add(currentVertice);
					latestVertices.add(currentVertice);
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					break;

				}
			}

			// expand fire

			for (int k = 0; k < latestVertices.size(); k++) {
				// listPrinter(nonBurningVertices);

				// Randf�lle! verlassener Knoten liegt am Rand/Ecke
				if (latestVertices.get(k).intValue() == 0) {
					// only check upper and right vertice
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

				}

				if (latestVertices.get(k).intValue() == Main.GridLength) {
					// only check upper and left vertice
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}
				}

				if (latestVertices.get(k).intValue() == (Main.GridSize - Main.GridLength)) {
					// only check lower and right vertice
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}
				}

				if (latestVertices.get(k).intValue() == (Main.GridSize - 1)) {
					// only check lower and left vertice
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}
				}

				// Rand des Grids
				// unten
				if (latestVertices.get(k).intValue() < Main.GridLength) {
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}
				}

				// oben
				if (latestVertices.get(k).intValue() > (Main.GridSize - Main.GridLength)) {
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}

					}
				}

				// links
				if ((latestVertices.get(k).intValue() % Main.GridLength) == 0) {
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}
				}

				// rechts
				if ((latestVertices.get(k).intValue() % Main.GridLength) == (Main.GridLength - 1)) {
					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}

					if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
							nonBurningVertices.remove(latestVertices.get(k));
							crew.setFitness(crew.getFitness() - 1);
							continue;
						}
					}
				}

				// check if latestvertices has burning neighbours __ kein
				// Randfall!
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
						nonBurningVertices.remove(latestVertices.get(k));
						crew.setFitness(crew.getFitness() - 1);
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
						nonBurningVertices.remove(latestVertices.get(k));
						crew.setFitness(crew.getFitness() - 1);
						continue;
					}
				}
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						nonBurningVertices.remove(latestVertices.get(k));
						crew.setFitness(crew.getFitness() - 1);
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						nonBurningVertices.remove(latestVertices.get(k));
						crew.setFitness(crew.getFitness() - 1);
						continue;
					}
				}

			}

			// save best fitness
			if (crew.getFitness() > tempFitness) {
				tempFitness = crew.getFitness();
				for (int k = 0; k < Main.CrewSize; k++) {
					bestSetup[k] = crew.getCrew().get(k).getCurrentVertice();
				}
			}

			latestVertices.clear();
			defendedVertices.clear();
		}
		nonBurningVertices.clear();
		crew.setBestSetup(bestSetup);
	}

	// Hilfsfunktionen

	// check if movement for fighter 2 in timestep t is possible in Bezug auf linken
	// nachbarn
	private boolean movementPossible(ConnectedFireFighter fighter2, int timestep) {
		boolean possible = false;

		ConnectedFireFighter fighter1 = fighter2.getLeftNeighbour();
		int position = fighter2.getPositionIndex(timestep);
		int movement1 = fighter1.getChainIndex(timestep);
		int movement2 = fighter2.getChainIndex(timestep);

		switch (position) {
		case 1:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				if (movement2 == 0 || movement2 == 2 || movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;

				// fighter 1 north
			case 1:
				return possible;
			// fighter 1 east
			case 2:
				if (movement2 == 0 || movement2 == 2 || movement2 == 3) {
					possible = true;
					return possible;
				} else
					return possible;
				// figher 1 south
			case 3:
				if (movement2 == 3) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 west
			case 4:
				if (movement2 == 0 || movement2 == 3 || movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
			}
			break;

		case 2:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				if (movement2 == 0 || movement2 == 3 || movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 north
			case 1:
				if (movement2 == 0 || movement2 == 1 || movement2 == 3) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 east
			case 2:
				if (movement2 == 0 || movement2 == 2 || movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
				// figher 1 south
			case 3:
				if (movement2 == 3) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 west
			case 4:
				if (movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
			}
			break;

		case 3:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				if (movement2 == 0 || movement2 == 1 || movement2 == 3) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 north
			case 1:
				if (movement2 == 0 || movement2 == 1 || movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 east
			case 2:
				return possible;
			// figher 1 south
			case 3:
				if (movement2 == 0 || movement2 == 3 || movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 west
			case 4:
				if (movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
			}
			break;

		case 4:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				if (movement2 == 0 || movement2 == 1 || movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 north
			case 1:
				if (movement2 == 1) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 east
			case 2:
				if (movement2 == 0 || movement2 == 2 || movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
				// figher 1 south
			case 3:
				if (movement2 == 0 || movement2 == 1 || movement2 == 3) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 west
			case 4:
				if (movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
			}
			break;

		case 5:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				if (movement2 == 0 || movement2 == 2 || movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 north
			case 1:
				if (movement2 == 1) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 east
			case 2:
				if (movement2 == 0 || movement2 == 1 || movement2 == 2) {
					possible = true;
					return possible;
				} else
					return possible;
				// figher 1 south
			case 3:
				return possible;
			// fighter 1 west
			case 4:
				if (movement2 == 0 || movement2 == 1 || movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
			}
			break;

		case 6:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				if (movement2 == 0 || movement2 == 1 || movement2 == 2) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 north
			case 1:
				if (movement2 == 1) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 east
			case 2:
				if (movement2 == 2) {
					possible = true;
					return possible;
				} else
					return possible;
				// figher 1 south
			case 3:
				if (movement2 == 0 || movement2 == 1 || movement2 == 3) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 west
			case 4:
				if (movement2 == 0 || movement2 == 2 || movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
			}
			break;

		case 7:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				if (movement2 == 0 || movement2 == 1 || movement2 == 3) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 north
			case 1:
				if (movement2 == 0 || movement2 == 1 || movement2 == 2) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 east
			case 2:
				if (movement2 == 2) {
					possible = true;
					return possible;
				} else
					return possible;
				// figher 1 south
			case 3:
				if (movement2 == 0 || movement2 == 2 || movement2 == 3) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 west
			case 4:
				return possible;
			}
			break;

		case 8:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				if (movement2 == 0 || movement2 == 2 || movement2 == 3) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 north
			case 1:
				if (movement2 == 0 || movement2 == 1 || movement2 == 3) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 east
			case 2:
				if (movement2 == 2) {
					possible = true;
					return possible;
				} else
					return possible;
				// figher 1 south
			case 3:
				if (movement2 == 3) {
					possible = true;
					return possible;
				} else
					return possible;
				// fighter 1 west
			case 4:
				if (movement2 == 0 || movement2 == 2 || movement2 == 4) {
					possible = true;
					return possible;
				} else
					return possible;
			}
			break;
		}
		return possible;

	}

	// returns possible movement for fighter2 in dependence of fighter1
	private int movementCalculator(ConnectedFireFighter fighter2, int timestep) {

		ConnectedFireFighter fighter1 = fighter2.getLeftNeighbour();
		int position = fighter2.getPositionIndex(timestep);
		int movement1 = fighter1.getChainIndex(timestep);
		int temp, movement2;

		switch (position) {
		case 1:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(1 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(2 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(8 , timestep + 1);
					return movement2;
				}

				// fighter 1 north
			case 1:
				// nicht m�glich
				return -1;
			// fighter 1 east
			case 2:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(8 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(1 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(7 , timestep + 1);
					return movement2;
				}

				// figher 1 south
			case 3:
				movement2 = 3;
				fighter2.setPositionIndex(1 , timestep + 1);
				return movement2;
			// fighter 1 west
			case 4:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(2 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 3;
					fighter2.setPositionIndex(3 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(1 , timestep + 1);
					return movement2;
				}
			}
			break;

		case 2:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(2 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 3;
					fighter2.setPositionIndex(3 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(1 , timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(3 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(2 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(4 , timestep + 1);
					return movement2;
				}
				// fighter 1 east
			case 2:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(1 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(2 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(8 , timestep + 1);
					return movement2;
				}
				// figher 1 south
			case 3:
				movement2 = 3;
				fighter2.setPositionIndex(2 , timestep + 1);
				return movement2;
			// fighter 1 west
			case 4:
				movement2 = 4;
				fighter2.setPositionIndex(2 , timestep + 1);
				return movement2;
			}
			break;

		case 3:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(3 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(2 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(4 , timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(4 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(3 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(5 , timestep + 1);
					return movement2;
				}
				// fighter 1 east
			case 2:
				// nicht m�glich
				return -1;
			// figher 1 south
			case 3:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(2 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 3;
					fighter2.setPositionIndex(3 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(1 , timestep + 1);
					return movement2;
				}
				// fighter 1 west
			case 4:
				movement2 = 4;
				fighter2.setPositionIndex(3 , timestep + 1);
				return movement2;
			}
			break;

		case 4:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(4 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(3 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(5 , timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				movement2 = 1;
				fighter2.setPositionIndex(4 , timestep + 1);
				return movement2;
			// fighter 1 east
			case 2:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(5 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(4 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(6 , timestep + 1);
					return movement2;
				}
				// figher 1 south
			case 3:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(3 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(2 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(4 , timestep + 1);
					return movement2;
				}
				// fighter 1 west
			case 4:
				movement2 = 4;
				fighter2.setPositionIndex(4 , timestep + 1);
				return movement2;
			}
			break;

		case 5:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(5 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(4 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(6 , timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				movement2 = 1;
				fighter2.setPositionIndex(5 , timestep + 1);
				return movement2;
			// fighter 1 east
			case 2:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(6 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(7 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(5 , timestep + 1);
					return movement2;
				}
				// figher 1 south
			case 3:
				// nicht m�glich
				return -1;
			// fighter 1 west
			case 4:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(4 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(3 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(5 , timestep + 1);
					return movement2;
				}
			}
			break;

		case 6:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(6 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(7 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(5 , timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				movement2 = 1;
				fighter2.setPositionIndex(6 , timestep + 1);
				return movement2;
			// fighter 1 east
			case 2:
				movement2 = 2;
				fighter2.setPositionIndex(6 , timestep + 1);
				return movement2;
			// figher 1 south
			case 3:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(7 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(8 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(6 , timestep + 1);
					return movement2;
				}
				// fighter 1 west
			case 4:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(5 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(4 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(6 , timestep + 1);
					return movement2;
				}
			}
			break;

		case 7:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(7 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(8 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(6 , timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(6 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(7 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(5 , timestep + 1);
					return movement2;
				}
				// fighter 1 east
			case 2:
				movement2 = 2;
				fighter2.setPositionIndex(7 , timestep + 1);
				return movement2;
			// figher 1 south
			case 3:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(8 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(1 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(7 , timestep + 1);
					return movement2;
				}
				// fighter 1 west
			case 4:
				// nicht m�glich
				return -1;
			}
			break;

		case 8:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(8 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(1 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(7 , timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(7 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(8 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(6 , timestep + 1);
					return movement2;
				}
				// fighter 1 east
			case 2:
				movement2 = 2;
				fighter2.setPositionIndex(8 , timestep + 1);
				return movement2;
			// figher 1 south
			case 3:
				movement2 = 3;
				fighter2.setPositionIndex(8 , timestep + 1);
				return movement2;
			// fighter 1 west
			case 4:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(1 , timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(2 , timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(8 , timestep + 1);
					return movement2;
				}
			}
			break;
		}
		// nicht erreichbar, da �berall return statements
		return -1;

	}

	private boolean checkIfValid(int direction, int vertice, int[] compareVertices) {

		// check if the vertice in direction of given vertice is valid
		// means: it is not in compareVertices and not out of bounds
		switch (direction) {
		// north
		case 1:
			if ((vertice + Main.GridLength) > Main.GridSize) {
				if (intInArray(vertice, compareVertices)) {
					// vertice already defended
					return false;
				}
				return true;
			}
			// out of bounds
			else
				return false;

			// north east
		case 2:
			if (((vertice + Main.GridLength) > Main.GridSize) && ((vertice % Main.GridLength) >= Main.GridLength - 2)) {
				if (intInArray(vertice, compareVertices)) {
					// vertice already defended
					return false;
				}
				return true;
			}
			// out of bounds
			else
				return false;

			// east
		case 3:
			if ((vertice % Main.GridLength) >= Main.GridLength - 2) {
				if (intInArray(vertice, compareVertices)) {
					// vertice already defended
					return false;
				}
				return true;
			}
			// out of bounds
			else
				return false;

			// south east
		case 4:
			if (((vertice % Main.GridLength) >= Main.GridLength - 2) && ((vertice - Main.GridLength) < 0)) {
				if (intInArray(vertice, compareVertices)) {
					// vertice already defended
					return false;
				}
				return true;
			}
			// out of bounds
			else
				return false;

			// south
		case 5:
			if ((vertice - Main.GridLength) < 0) {
				if (intInArray(vertice, compareVertices)) {
					// vertice already defended
					return false;
				}
				return true;
			}
			// out of bounds
			else
				return false;

			// south west
		case 6:
			if (((vertice - Main.GridLength) < 0) && ((vertice % Main.GridLength) <= 1)) {
				if (intInArray(vertice, compareVertices)) {
					// vertice already defended
					return false;
				}
				return true;
			}
			// out of bounds
			else
				return false;

			// west
		case 7:
			if ((vertice % Main.GridLength) <= 1) {
				if (intInArray(vertice, compareVertices)) {
					// vertice already defended
					return false;
				}
				return true;
			}
			// out of bounds
			else
				return false;

			// north west
		case 8:
			if (((vertice % Main.GridLength) <= 1) && ((vertice + Main.GridLength) > Main.GridSize)) {
				if (intInArray(vertice, compareVertices)) {
					// vertice already defended
					return false;
				}
				return true;
			}
			// out of bounds
			else
				return false;

		}
		return false;
	}

	private boolean intInArray(int value, int[] array) {
		for (int i = 0; i < array.length; i++) {
			if (value == array[i]) {
				return true;
			}
		}
		return false;
	}

}
