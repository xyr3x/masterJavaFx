/** Author: Moritz Wiemker
 * 	Masterthesis
 *
 *
 * Ablauf Evolution�rer Algorithmus
 *	1. Initialisierung: Die erste Generation von L�sungskandidaten wird (meist zuf�llig) erzeugt.
 *	2. Evaluation: Jedem L�sungskandidaten der Generation wird entsprechend seiner G�te ein Wert der Fitnessfunktion zugewiesen.
 *	3. Durchlaufe die folgenden Schritte, bis ein Abbruchkriterium erf�llt ist:
 *		3.1. Selektion: Auswahl von Individuen f�r die Rekombination
 *		3.2. Rekombination: Kombination der ausgew�hlten Individuen
 *		3.3. Mutation: Zuf�llige Ver�nderung der Nachfahren
 *		3.4. Evaluation: Jedem L�sungskandidaten der Generation wird entsprechend seiner G�te ein Wert der Fitnessfunktion zugewiesen.
 *		3.5. Selektion: Bestimmung einer neuen Generation
 *
 *
 */

package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import application.Main;
import model.*;

public class EvolutionaryAlgo {
	private List<FireFighterCrew> population = new ArrayList<FireFighterCrew>();
	private boolean fighterAtBorder = false;

	private int maxFitness = 0;
	private int optimum = Main.CrewSize + 5;
	private FireFighterCrew bestCrew = new FireFighterCrew();
	private int[] bestSetUp = new int[Main.CrewSize];

	// constructor
	public EvolutionaryAlgo() {

	}

	public FireFighterCrew evAlgo() {
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

			// 3.2. Rekombination
			for (int i = 0; i < Main.RecombinationSize; i++) {
				int parent1 = Main.rnd.nextInt(Main.PopulationSize - Main.RecombinationSize);
				int parent2 = Main.rnd.nextInt(Main.PopulationSize - Main.RecombinationSize);
				int crossOver = Main.rnd.nextInt(Main.TimeInterval);

				FireFighterCrew crew = new FireFighterCrew();

				// recombine parentcrew 1 and parentcrew 2 s.t. every kth
				// new fighter is one point crossover of the kth fighter of the
				// parent crews
				for (int j = 0; j < Main.CrewSize; j++) {
					int chain[] = new int[Main.TimeInterval];
					FireFighter fighter = new FireFighter();
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
		return bestCrew;

	}

	// initalisierung des Problems
	private void initialize() {
		int temp = 0;
		// intialize every individuum of the population

		for (int i = 0; i < Main.PopulationSize; i++) {
			FireFighterCrew crew = new FireFighterCrew();

			// initalize every fighter of the crew
			for (int j = 0; j < Main.CrewSize; j++) {
				FireFighter fighter = new FireFighter();

				// initialize startvertice, check if unique
				int startVertice = Main.rnd.nextInt(Main.GridSize);
				startVertice = uniqueStartVertice(startVertice, crew);

				fighter.setStartVertice(startVertice);
				fighter.setCurrentVertice(startVertice);

				// initialize Chain
				int[] chain = new int[Main.TimeInterval];
				for (int k = 0; k < Main.TimeInterval; k++) {
					chain[k] = Main.rnd.nextInt(5);
				}

				fighter.setChain(chain);
				crew.getCrew().add(fighter);
			}

			crew.setFitness(Main.CrewSize);
			population.add(crew);

		}
	}


	public void calculateFitness(FireFighterCrew crew) {
		// vertices that do not burn
		List<Integer> nonBurningVertices = new ArrayList<>();
		// Vertices of the last timestep
		List<Integer> latestVertices = new ArrayList<>();
		// defended vertices
		SortedSet<Integer> defendedVertices = new TreeSet<>();
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
					crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);
					//defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					break;
				// go north
				case 1:
					// Zielknoten besetzt
					for (int k = 0; k < Main.CrewSize; k++) {
						if ((currentVertice + Main.GridLength) == crew.getCrew().get(k).getCurrentVertice()) {
							crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);
							defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
							continue fighterloop;
						}
					}
					crew.getCrew().get(j).setCurrentVertice(currentVertice + Main.GridLength);
					tempFitness += 1;
					latestVertices.add(currentVertice);
					crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					nonBurningVertices.add(currentVertice);

					break;
				// go east
				case 2:
					// Zielknoten besetzt
					for (int k = 0; k < Main.CrewSize; k++) {
						if ((currentVertice + 1) == crew.getCrew().get(k).getCurrentVertice()) {
							crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);
							defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
							continue fighterloop;
						}
					}

					crew.getCrew().get(j).setCurrentVertice(currentVertice + 1);
					tempFitness += 1;
					latestVertices.add(currentVertice);
					crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					nonBurningVertices.add(currentVertice);
					break;
				// go south
				case 3:
					// Zielknoten besetzt
					for (int k = 0; k < Main.CrewSize; k++) {
						if ((currentVertice - Main.GridLength) == crew.getCrew().get(k).getCurrentVertice()) {
							crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);
							defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
							continue fighterloop;
						}
					}
					crew.getCrew().get(j).setCurrentVertice(currentVertice - Main.GridLength);
					tempFitness += 1;
					latestVertices.add(currentVertice);
					crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					nonBurningVertices.add(currentVertice);
					break;
				// go west
				case 4:
					// Zielknoten besetzt
					for (int k = 0; k < Main.CrewSize; k++) {
						if ((currentVertice - 1) == crew.getCrew().get(k).getCurrentVertice()) {
							crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);
							defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
							continue fighterloop;
						}
					}
					crew.getCrew().get(j).setCurrentVertice(currentVertice - 1);
					tempFitness += 1;
					latestVertices.add(currentVertice);
					crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					nonBurningVertices.add(currentVertice);
					break;

				}
			}

			// expand fire
		
			//all non-burning vertices
			for (int k = 0; k < latestVertices.size(); k++) {
				// listPrinter(nonBurningVertices);
				
				// check if latestvertices has burning neighbours __ kein
				// Randfall! -- Randf�lle bereits abgefangen
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - 1))) {
						nonBurningVertices.remove(latestVertices.get(k));
						tempFitness -= 1;
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + 1))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + 1))) {
						nonBurningVertices.remove(latestVertices.get(k));
						tempFitness -= 1;
						continue;
					}
				}
				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() + Main.GridLength))) {
						nonBurningVertices.remove(latestVertices.get(k));
						tempFitness -= 1;
						continue;
					}
				}

				if (!nonBurningVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
					if (!defendedVertices.contains((latestVertices.get(k).intValue() - Main.GridLength))) {
						nonBurningVertices.remove(latestVertices.get(k));
						tempFitness -= 1;
						continue;
					}
				}

			}
			
			//all remaining non burning vertices
			for (int k = 0; k < nonBurningVertices.size(); k++) {
				if (!nonBurningVertices.contains((nonBurningVertices.get(k).intValue() - 1))) {
					if (!defendedVertices.contains((nonBurningVertices.get(k).intValue() - 1))) {
						nonBurningVertices.remove(k);
						tempFitness -= 1;
						continue;
					}
				}

				if (!nonBurningVertices.contains((nonBurningVertices.get(k).intValue() + 1))) {
					if (!defendedVertices.contains((nonBurningVertices.get(k).intValue() + 1))) {
						nonBurningVertices.remove(k);
						tempFitness -= 1;
						continue;
					}
				}
				if (!nonBurningVertices.contains((nonBurningVertices.get(k).intValue() + Main.GridLength))) {
					if (!defendedVertices.contains((nonBurningVertices.get(k).intValue() + Main.GridLength))) {
						nonBurningVertices.remove(k);
						tempFitness -= 1;
						continue;
					}
				}

				if (!nonBurningVertices.contains((nonBurningVertices.get(k).intValue() - Main.GridLength))) {
					if (!defendedVertices.contains((nonBurningVertices.get(k).intValue() - Main.GridLength))) {
						nonBurningVertices.remove(k);
						tempFitness -= 1;
						continue;
					}
				}			

			}

			// save best fitness
			if (crew.getFitness() < tempFitness) {
				System.out.println("New Fitness: " + nonBurningVertices.size());
				crew.setFitness(tempFitness);
				crew.setBestTimeStep(i);
			}

			latestVertices.clear();
			defendedVertices.clear();
			//safe nonBurningVertices in Timestep i

			Integer[] dummy = nonBurningVertices.toArray(new Integer[nonBurningVertices.size()]);	
			for(int k = 0; k < Main.CrewSize; k++) {
				for (int l = 0; l < nonBurningVertices.size(); l++) {
					crew.setNonBurningVerticesIndex(dummy[l].intValue(), i, k);
				}
			}
		}
		nonBurningVertices.clear();
	}
	
	//TODO: save
	/*
	 * public void calculateFitness(FireFighterCrew crew) {
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

	 * 
	 * 
	 * 
	 */
	
	

	// Hilfsfunktionen
	// getter & and setter
	public List<FireFighterCrew> getPopulation() {
		return population;
	}

	public int uniqueStartVertice(int startVertice, FireFighterCrew crew) {
		// check if startVertice equals already existing startVertice
		for (int i = 0; i < crew.getCrew().size(); i++) {
			if (startVertice == crew.getCrew().get(i).getStartVertice()) {
				startVertice = Main.rnd.nextInt(Main.GridSize);
				uniqueStartVertice(startVertice, crew);
			}
		}
		return startVertice;
	}

	private void listPrinter(List list) {
		System.out.print("List: ");
		for (int i = 0; i < list.size(); i++) {
			System.out.print(list.get(i).toString() + ";");
		}
		System.out.println();
	}

	private void listPrinter(SortedSet set) {
		System.out.print("List: ");
		System.out.println(set.toString());
		System.out.println();
	}

	
	//getter and setter
	public FireFighterCrew getBestCrew() {
		return bestCrew;
	}

	public void setBestCrew(FireFighterCrew bestCrew) {
		this.bestCrew = bestCrew;
	}
	
	
	
	
	

}
