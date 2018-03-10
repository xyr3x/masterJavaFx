package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import application.Main;
import model.*;

public class EvolutionaryAlgoConnected {
	private List<ConnectedFireFighterCrew> population = new ArrayList<ConnectedFireFighterCrew>();
	private boolean fighterAtBorder = false;

	private int maxFitness = 0;
	// private int optimum = Main.CrewSize + 100;
	private int optimum = 10;
	private ConnectedFireFighterCrew bestCrew = new ConnectedFireFighterCrew();
	private int[] bestSetUp = new int[Main.CrewSize];

	// constructor
	public EvolutionaryAlgoConnected() {

	}

	public ConnectedFireFighterCrew evAlgo() {
		System.out.println("Start");
		// stuff
		int counter = 0;

		// 1. Initialisierung
		initialize();
		System.out.println("Init finished");
		// 2. Evaluation
		for (int i = 0; i < population.size(); i++) {
			calculateFitness(population.get(i));
			if (population.get(i).getFitness() > maxFitness) {
				//maxFitness = population.get(i).getFitness();
				maxFitness = population.get(i).getMaxNonBurningVertices();
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

			// 3.2. Rekombination //TODO: WICHTIG!! Überarbeiten
			for (int i = 0; i < Main.RecombinationSize; i++) {
				int parent1 = Main.rnd.nextInt(Main.PopulationSize - Main.RecombinationSize);
				int parent2 = Main.rnd.nextInt(Main.PopulationSize - Main.RecombinationSize);
				int crossOver = Main.rnd.nextInt(Main.TimeInterval);

				ConnectedFireFighterCrew crew = new ConnectedFireFighterCrew();

				// recombine parentcrew 1 and parentcrew 2 s.t. every kth
				// new fighter is one point crossover of the kth fighter of the
				// parent crews
				// first fighter
				// TODO: Fighter connecten
				ConnectedFireFighter fighter1 = new ConnectedFireFighter();
				fighter1.setStartVertice(population.get(parent1).getCrew().get(0).getStartVertice());
				fighter1.setCurrentVertice(fighter1.getStartVertice());
				int chain1[] = new int[Main.TimeInterval];
				for (int k = 0; k < crossOver; k++) {
					chain1[k] = population.get(parent2).getCrew().get(0).getChainIndex(k);
				}
				fighter1.setChain(chain1);
				crew.getCrew().add(fighter1);

				for (int j = 1; j < Main.CrewSize; j++) {
					int chain[] = new int[Main.TimeInterval];
					ConnectedFireFighter fighter = new ConnectedFireFighter();
					// set start vertice
					fighter.setStartVertice(population.get(parent1).getCrew().get(j).getStartVertice());
					fighter.setPosition(population.get(parent1).getCrew().get(j).getPosition());
					fighter.setCurrentVertice(fighter.getStartVertice());
					fighter.setLeftNeighbour(crew.getCrew().get(j - 1));
					// set chain -- take first from 2nd parent until crossover, then take 2nd part
					// from 1st parent for first fighter and calculate rest
					for (int k = 0; k < crossOver; k++) {
						chain[k] = population.get(parent2).getCrew().get(j).getChainIndex(k);
					}
					fighter.setChain(chain);
					crew.getCrew().add(fighter);

				}
				// first fighter
				for (int k = crossOver; k < Main.TimeInterval; k++) {
					crew.getCrew().get(0).setChainIndex(k, population.get(parent1).getCrew().get(0).getChainIndex(k));
				}

				// construct every other fighter
				for (int j = 1; j < Main.CrewSize; j++) {
					for (int k = crossOver; k < Main.TimeInterval; k++) {
						crew.getCrew().get(j).setChainIndex(k, movementCalculator(crew.getCrew().get(j), k));
					}
				}

				crew.setNewCrew(true);
				crew.setGeneration(counter);
				population.add(crew);

			}

			// 3.3 Mutation
			if (Main.rnd.nextInt(100) < Main.MutationProbability) {

				// numbers??
				int numberOfCrews = Main.rnd.nextInt(Main.PopulationSize / 4);

				for (int i = 0; i < numberOfCrews; i++) {
					// change movement of 1st fighter in timestep, recalculate all other movements
					int crewNumber = Main.rnd.nextInt(Main.PopulationSize);
					int timestep = Main.rnd.nextInt(Main.TimeInterval);
					population.get(crewNumber).getCrew().get(0).setChainIndex(timestep, Main.rnd.nextInt(5));
					population.get(crewNumber).getCrew().get(0).setCurrentVertice(population.get(crewNumber).getCrew().get(0).getStartVertice());

					for (int j = 1; j < Main.CrewSize; j++) {
						for (int k = timestep; k < Main.TimeInterval; k++) {
							population.get(crewNumber).getCrew().get(j).setChainIndex(k,
									movementCalculator(population.get(crewNumber).getCrew().get(j), k));
						}
						population.get(crewNumber).getCrew().get(j).setCurrentVertice(population.get(crewNumber).getCrew().get(j).getStartVertice());
					}

					population.get(crewNumber).setChanged(true);
					population.get(crewNumber).setFitness(Main.CrewSize);
				}

			}

			// 3.4 Evaluation
			for (int i = 0; i < population.size(); i++) {
				if (population.get(i).isChanged() || population.get(i).isNewCrew()) {
					calculateFitness(population.get(i));
					if (population.get(i).getMaxNonBurningVertices() > maxFitness) {
						//maxFitness = population.get(i).getFitness();
						maxFitness = population.get(i).getMaxNonBurningVertices();
						bestCrew = population.get(i);

					}
				}

			}
			System.out.println("Fitness: " + maxFitness);
		}
		Collections.sort(population);
		return bestCrew;

	}

	public void initialize() {
		int[] temp = new int[Main.PopulationSize];
		int tempVertice = 0;

		// intialize every individuum of the population

		PopulationLoop: for (int i = 0; i < Main.PopulationSize; i++) {
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
				// TODO DAUERSCHLEIFE MÖGLICH ---- FIXEN!!!
				int counter = 0;

				Innerloop: while (!finished && counter < 8) {
					// 1 == north, 3 == east, 5 == south, 7 == west
					int tempDirection = Main.rnd.nextInt(8) + 1;
					// int tempDirection = 1;
					switch (tempDirection) {
					// north
					case 1:
						boolean dummy = checkIfValid(tempDirection, tempFighter.getLeftNeighbour().getStartVertice(),
								startVertices);
						if (checkIfValid(tempDirection, tempFighter.getLeftNeighbour().getStartVertice(),
								startVertices)) {
							// vertice is valid, set Startvertice
							tempFighter.setStartVertice(
									tempFighter.getLeftNeighbour().getStartVertice() + Main.GridLength);
							tempFighter.setCurrentVertice(tempFighter.getStartVertice());
							tempFighter.setPositionIndex(1, 0);
							startVertices[j] = tempFighter.getStartVertice();
							finished = true;
							counter = 0;
							break; // PopulationLoop;
						}
						// force restart
						else {
							// try again
							counter += 1;
							continue Innerloop;
						}
						// break;

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
				}

				// Counter = 8; also init fehlgeschlagen, abfangen der dauerschleife
				if (counter == 8) {
					System.out.println("Fail");
					// restart from j = 1
					j = 0;
					counter = 0;
					// break PopulationLoop;
					continue OuterLoop;
				}

				//System.out.println("Start Chain: " + j);
				// initialize Chain
				int[] chain2 = new int[Main.TimeInterval];
				int dummy;
				for (int k = 0; k < Main.TimeInterval; k++) {

					dummy = movementCalculator(tempFighter, k);
					//System.out.println(dummy);
					// Fehler, Movement nicht möglich
					if (dummy == -1) {
						// Movement des Vorgängers neu berechnen
						startVertices[j] = 0;
						j -= 1;
						continue OuterLoop;
					}
					chain2[k] = dummy;
				}

				//System.out.println("Chain initalized");

				tempFighter.setChain(chain2);
				crew.getCrew().add(tempFighter);

			}

			crew.setFitness(Main.CrewSize);
			crew.setGeneration(0);
			population.add(crew);

		}

	}

	// calculate fitness -- possible to move fighters at one point
	public void calculateFitness(ConnectedFireFighterCrew crew) {
		// vertices that do not burn
		Set<Integer> nonBurningVertices = new LinkedHashSet<>();
		// defended vertices
		List<Integer> defendedVertices = new ArrayList<Integer>();
		int tempFitness = crew.getFitness();

		// move fighters (switch case unterscheidung), expand fire
		int tempDirection, currentVertice;
		// for every time step
		timeloop: for (int i = 0; i < Main.TimeInterval; i++) {

			// move every fighter

			fighterloop: for (int j = 0; j < Main.CrewSize; j++) {
				currentVertice = crew.getCrew().get(j).getCurrentVertice();
				tempDirection = crew.getCrew().get(j).getChainIndex(i);

				// Randfälle, bleibe stehenn wenn Grid zu Ende//Rand rausnehmen
				// Ecken: 0; GridLength; GridLength^2 - (GridLength);
				// GridLength^2 - 1
				if (currentVertice == 0 + Main.GridLength + 1) {
					if (tempDirection == 3 || tempDirection == 4) {
						// shift grid and reset
						// shift crew and reset the fighters already active in this timeslot
						while (j > 0) {
							// dont touch actual fighter
							j--;
							if (i == 0) {
								crew.getCrew().get(j).setCurrentVertice(crew.getCrew().get(j).getStartVertice());
							} else {
								crew.getCrew().get(j).setCurrentVertice(crew.getDefendedVerticesIndex(i - 1, j));
							}
						}
						crew.shiftXPositive();
						crew.shiftYPositive();

						// reset nonBurningVertices to timestep before
						nonBurningVertices.clear();
						for (int k = 0; k < Main.CrewSize * Main.CrewSize; k++) {
							if ( i > 0) {
								if (crew.getNonBurningVerticesIndex(i - 1, k) != 0) {
									nonBurningVertices.add(crew.getNonBurningVerticesIndex(i - 1, k));
								}
							}	
						}
						// reset Fitness
						tempFitness = crew.getFitness();
						defendedVertices.clear();

						// update non burning vertices
						List<Integer> shiftList = new ArrayList<>();
						for (Integer k : nonBurningVertices) {
							// x positive
							shiftList.add(k.intValue() + (Main.GridLength / 2));
							// y positive
							shiftList.add(k.intValue() + (Main.GridLength * (Main.GridLength / 2)));
						}
						nonBurningVertices.clear();

						for (Integer k : shiftList) {
							nonBurningVertices.add(k);
						}
						shiftList.clear();

						// reset timer
						i = i - 1;
						continue timeloop;

					}
				}

				if (currentVertice == Main.GridLength + Main.GridLength - 1) {
					if (tempDirection == 2 || tempDirection == 3) {
						// shift grid and reset
						// shift crew and reset the fighters already active in this timeslot
						while (j > 0) {
							// dont touch actual fighter
							j--;
							if (i == 0) {
								crew.getCrew().get(j).setCurrentVertice(crew.getCrew().get(j).getStartVertice());
							} else {
								crew.getCrew().get(j).setCurrentVertice(crew.getDefendedVerticesIndex(i - 1, j));
							}
						}
						crew.shiftXNegative();
						crew.shiftYPositive();

						// reset nonBurningVertices to timestep before
						nonBurningVertices.clear();
						for (int k = 0; k < Main.CrewSize * Main.CrewSize; k++) {
							if ( i > 0) {
								if (crew.getNonBurningVerticesIndex(i - 1, k) != 0) {
									nonBurningVertices.add(crew.getNonBurningVerticesIndex(i - 1, k));
								}
							}	
						}
						// reset Fitness
						tempFitness = crew.getFitness();
						defendedVertices.clear();

						// update non burning vertices
						List<Integer> shiftList = new ArrayList<>();
						for (Integer k : nonBurningVertices) {
							// x negative
							shiftList.add(k.intValue() - (Main.GridLength / 2));
							// y positive
							shiftList.add(k.intValue() + (Main.GridLength * (Main.GridLength / 2)));
						}
						nonBurningVertices.clear();

						for (Integer k : shiftList) {
							nonBurningVertices.add(k);
						}
						shiftList.clear();
						// reset timer
						i = i - 1;
						continue timeloop;

					}
				}

				if (currentVertice == (Main.GridSize - Main.GridLength - Main.GridLength + 1)) {
					if (tempDirection == 1 || tempDirection == 4) {
						// shift grid and reset
						// shift crew and reset the fighters already active in this timeslot
						while (j > 0) {
							// dont touch actual fighter
							j--;
							if (i == 0) {
								crew.getCrew().get(j).setCurrentVertice(crew.getCrew().get(j).getStartVertice());
							} else {
								crew.getCrew().get(j).setCurrentVertice(crew.getDefendedVerticesIndex(i - 1, j));
							}
						}

						crew.shiftXPositive();
						crew.shiftYNegative();

						// reset nonBurningVertices to timestep before
						nonBurningVertices.clear();
						for (int k = 0; k < Main.CrewSize * Main.CrewSize; k++) {
							if ( i > 0) {
								if (crew.getNonBurningVerticesIndex(i - 1, k) != 0) {
									nonBurningVertices.add(crew.getNonBurningVerticesIndex(i - 1, k));
								}
							}	
						}
						// reset Fitness
						tempFitness = crew.getFitness();
						defendedVertices.clear();

						// update non burning vertices
						List<Integer> shiftList = new ArrayList<>();
						for (Integer k : nonBurningVertices) {
							// x positive
							shiftList.add(k.intValue() + (Main.GridLength / 2));
							// y negative
							shiftList.add(k.intValue() - (Main.GridLength * (Main.GridLength / 2)));
						}
						nonBurningVertices.clear();

						for (Integer k : shiftList) {
							nonBurningVertices.add(k);
						}
						shiftList.clear();

						// reset timer
						i = i - 1;
						continue timeloop;

					}
				}

				if (currentVertice == (Main.GridSize - 1 - Main.GridLength - 1)) {
					if (tempDirection == 1 || tempDirection == 2) {
						// shift grid and reset
						// shift crew and reset the fighters already active in this timeslot
						while (j > 0) {
							// dont touch actual fighter
							j--;
							if (i == 0) {
								crew.getCrew().get(j).setCurrentVertice(crew.getCrew().get(j).getStartVertice());
							} else {
								crew.getCrew().get(j).setCurrentVertice(crew.getDefendedVerticesIndex(i - 1, j));
							}
						}

						crew.shiftXNegative();
						crew.shiftYNegative();

						// reset nonBurningVertices to timestep before
						nonBurningVertices.clear();
						for (int k = 0; k < Main.CrewSize * Main.CrewSize; k++) {
							if ( i > 0) {
								if (crew.getNonBurningVerticesIndex(i - 1, k) != 0) {
									nonBurningVertices.add(crew.getNonBurningVerticesIndex(i - 1, k));
								}
							}	
						}
						// reset Fitness
						tempFitness = crew.getFitness();
						defendedVertices.clear();

						// update non burning vertices
						List<Integer> shiftList = new ArrayList<>();
						for (Integer k : nonBurningVertices) {
							// x negative
							shiftList.add(k.intValue() - (Main.GridLength / 2));
							// y negative
							shiftList.add(k.intValue() - (Main.GridLength * (Main.GridLength / 2)));
						}
						nonBurningVertices.clear();

						for (Integer k : shiftList) {
							nonBurningVertices.add(k);
						}
						shiftList.clear();

						// reset timer
						i = i - 1;
						continue timeloop;

					}
				}

				// Rand des Grids
				// unten
				if (currentVertice < Main.GridLength + Main.GridLength) {
					if (tempDirection == 3) {
						// shift grid and reset
						// shift crew and reset the fighters already active in this timeslot
						while (j > 0) {
							// dont touch actual fighter
							j--;
							if (i == 0) {
								crew.getCrew().get(j).setCurrentVertice(crew.getCrew().get(j).getStartVertice());
							} else {
								crew.getCrew().get(j).setCurrentVertice(crew.getDefendedVerticesIndex(i - 1, j));
							}
						}

						crew.shiftYPositive();

						// reset nonBurningVertices to timestep before
						nonBurningVertices.clear();
						for (int k = 0; k < Main.CrewSize * Main.CrewSize; k++) {
							if ( i > 0) {
								if (crew.getNonBurningVerticesIndex(i - 1, k) != 0) {
									nonBurningVertices.add(crew.getNonBurningVerticesIndex(i - 1, k));
								}
							}	
						}
						// reset Fitness
						tempFitness = crew.getFitness();
						defendedVertices.clear();

						// update non burning vertices
						List<Integer> shiftList = new ArrayList<>();
						for (Integer k : nonBurningVertices) {
							// y positive
							shiftList.add(k.intValue() + (Main.GridLength * (Main.GridLength / 2)));
						}
						nonBurningVertices.clear();

						for (Integer k : shiftList) {
							nonBurningVertices.add(k);
						}
						shiftList.clear();

						// reset timer
						i = i - 1;
						continue timeloop;

					}
				}

				// oben
				if (currentVertice > (Main.GridSize - Main.GridLength - Main.GridLength)) {
					if (tempDirection == 1) {
						// shift grid and reset
						// shift crew and reset the fighters already active in this timeslot
						while (j > 0) {
							// dont touch actual fighter
							j--;
							if (i == 0) {
								crew.getCrew().get(j).setCurrentVertice(crew.getCrew().get(j).getStartVertice());
							} else {
								crew.getCrew().get(j).setCurrentVertice(crew.getDefendedVerticesIndex(i - 1, j));
							}
						}
						crew.shiftYNegative();

						// reset nonBurningVertices to timestep before
						nonBurningVertices.clear();
						for (int k = 0; k < Main.CrewSize * Main.CrewSize; k++) {
							if ( i > 0) {
								if (crew.getNonBurningVerticesIndex(i - 1, k) != 0) {
									nonBurningVertices.add(crew.getNonBurningVerticesIndex(i - 1, k));
								}
							}	
						}
						// reset Fitness
						tempFitness = crew.getFitness();
						defendedVertices.clear();

						// update non burning vertices
						List<Integer> shiftList = new ArrayList<>();
						for (Integer k : nonBurningVertices) {
							// y negative
							shiftList.add(k.intValue() - (Main.GridLength * (Main.GridLength / 2)));
						}
						nonBurningVertices.clear();

						for (Integer k : shiftList) {
							nonBurningVertices.add(k);
						}

						/*System.out.print("Shift: Non Burning: ");
						for (Integer k : nonBurningVertices) {
							System.out.print(k.intValue() + "|");
						}
						System.out.println();
						
						System.out.print("Shift: DefendedVertices: ");
						for (int k = 0; k < crew.getCrew().size(); k++) {
							System.out.print(crew.getCrew().get(k).getCurrentVertice() + "|");
						}
						System.out.println();
						
						System.out.print("Shift: StartVertices: ");
						for (int k = 0; k < crew.getCrew().size(); k++) {
							System.out.print(crew.getCrew().get(k).getStartVertice() + "|");
						}
						System.out.println();
						*/

						shiftList.clear();

						// reset timer
						i = i - 1;
						continue timeloop;

					}
				}

				// links
				if ((currentVertice % Main.GridLength) == 1) {
					if (tempDirection == 4) {
						// shift grid and reset
						// shift crew and reset the fighters already active in this timeslot
						while (j > 0) {
							// dont touch actual fighter
							j--;
							if (i == 0) {
								crew.getCrew().get(j).setCurrentVertice(crew.getCrew().get(j).getStartVertice());
							} else {
								crew.getCrew().get(j).setCurrentVertice(crew.getDefendedVerticesIndex(i - 1, j));
							}
						}

						crew.shiftXPositive();

						// reset nonBurningVertices to timestep before
						nonBurningVertices.clear();
						for (int k = 0; k < Main.CrewSize * Main.CrewSize; k++) {
							if ( i > 0) {
								if (crew.getNonBurningVerticesIndex(i - 1, k) != 0) {
									nonBurningVertices.add(crew.getNonBurningVerticesIndex(i - 1, k));
								}
							}	
						}
						// reset Fitness
						tempFitness = crew.getFitness();
						defendedVertices.clear();

						// update non burning vertices
						List<Integer> shiftList = new ArrayList<>();
						for (Integer k : nonBurningVertices) {
							// x positive
							shiftList.add(k.intValue() + (Main.GridLength / 2));
						}
						nonBurningVertices.clear();

						for (Integer k : shiftList) {
							nonBurningVertices.add(k);
						}
						shiftList.clear();

						// reset timer
						i = i - 1;
						continue timeloop;

					}
				}

				// rechts
				if ((currentVertice % Main.GridLength) == (Main.GridLength - 2)) {
					if (tempDirection == 2) {
						// shift grid and reset
						// shift crew and reset the fighters already active in this timeslot
						while (j > 0) {
							// dont touch actual fighter
							j--;
							if (i == 0) {
								crew.getCrew().get(j).setCurrentVertice(crew.getCrew().get(j).getStartVertice());
							} else {
								crew.getCrew().get(j).setCurrentVertice(crew.getDefendedVerticesIndex(i - 1, j));
							}
						}

						crew.shiftXNegative();

						// reset nonBurningVertices to timestep before
						nonBurningVertices.clear();
						for (int k = 0; k < Main.CrewSize * Main.CrewSize; k++) {
							if ( i > 0) {
								if (crew.getNonBurningVerticesIndex(i - 1, k) != 0) {
									nonBurningVertices.add(crew.getNonBurningVerticesIndex(i - 1, k));
								}
							}							
						}
						// reset Fitness
						tempFitness = crew.getFitness();
						defendedVertices.clear();

						// update non burning vertices
						List<Integer> shiftList = new ArrayList<>();
						for (Integer k : nonBurningVertices) {
							// x negative
							shiftList.add(k.intValue() - (Main.GridLength / 2));
						}
						nonBurningVertices.clear();

						for (Integer k : shiftList) {
							nonBurningVertices.add(k);
						}
						shiftList.clear();
						// reset timer
						i = i - 1;
						continue timeloop;

					}
				}

				switch (tempDirection) {
				// dont move
				case 0:
					defendedVertices.add(currentVertice);
					crew.setDefendedVerticesIndex(currentVertice, i, j);
					break;
				// go north
				case 1:
					// besetzte Knoten sind erlaubt
					crew.getCrew().get(j).setCurrentVertice(currentVertice + Main.GridLength);
					nonBurningVertices.add(currentVertice);
					// if vertice already cleared/defended fitness doesnt increase
					if (!nonBurningVertices.contains((Integer) crew.getCrew().get(j).getCurrentVertice())) {
						if (!defendedVertices.contains((Integer) crew.getCrew().get(j).getCurrentVertice())) {
							tempFitness += 1;
						}
					}
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);

					break;
				// go east
				case 2:
					// besetzte Knoten sind erlaubt
					crew.getCrew().get(j).setCurrentVertice(currentVertice + 1);
					nonBurningVertices.add(currentVertice);
					// if vertice already cleared/defended fitness doesnt increase
					if (!nonBurningVertices.contains((Integer) crew.getCrew().get(j).getCurrentVertice())) {
						if (!defendedVertices.contains((Integer) crew.getCrew().get(j).getCurrentVertice())) {
							tempFitness += 1;
						}
					}
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);
					break;
				// go south
				case 3:
					// besetzte Knoten sind erlaubt
					crew.getCrew().get(j).setCurrentVertice(currentVertice - Main.GridLength);
					nonBurningVertices.add(currentVertice);
					// if vertice already cleared/defended fitness doesnt increase
					if (!nonBurningVertices.contains((Integer) crew.getCrew().get(j).getCurrentVertice())) {
						if (!defendedVertices.contains((Integer) crew.getCrew().get(j).getCurrentVertice())) {
							tempFitness += 1;
						}
					}
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);
					break;
				// go west
				case 4:
					// besetzte Knoten sind erlaubt
					crew.getCrew().get(j).setCurrentVertice(currentVertice - 1);
					nonBurningVertices.add(currentVertice);
					// if vertice already cleared/defended fitness doesnt increase
					if (!nonBurningVertices.contains((Integer) crew.getCrew().get(j).getCurrentVertice())) {
						if (!defendedVertices.contains((Integer) crew.getCrew().get(j).getCurrentVertice())) {
							tempFitness += 1;
						}
					}
					defendedVertices.add(crew.getCrew().get(j).getCurrentVertice());
					crew.setDefendedVerticesIndex(crew.getCrew().get(j).getCurrentVertice(), i, j);
					break;

				}

			}

			// expand fire

			// all non-burning vertices
			// save vertices to remove in List, to avoid exception
			List<Integer> removeList = new ArrayList<>();
			for (Integer k : nonBurningVertices) {
				if (!defendedVertices.contains((Integer) k.intValue())) {
					if (!nonBurningVertices.contains((Integer) (k.intValue() - 1))) {
						if (!defendedVertices.contains((Integer) (k.intValue() - 1))) {
							removeList.add(k);
							tempFitness -= 1;
							continue;
						}
					}

					if (!nonBurningVertices.contains((Integer) (k.intValue() + 1))) {
						if (!defendedVertices.contains((Integer) (k.intValue() + 1))) {
							removeList.add(k);
							tempFitness -= 1;
							continue;
						}
					}
					if (!nonBurningVertices.contains((Integer) (k.intValue() + Main.GridLength))) {
						if (!defendedVertices.contains((Integer) (k.intValue() + Main.GridLength))) {
							removeList.add(k);
							tempFitness -= 1;
							continue;
						}
					}

					if (!nonBurningVertices.contains((Integer) (k.intValue() - Main.GridLength))) {
						if (!defendedVertices.contains((Integer) (k.intValue() - Main.GridLength))) {
							removeList.add(k);
							tempFitness -= 1;
							continue;
						}
					}

				}
			}
			// remove objects from list
			/*System.out.print("RemoveList: ");
			for (Integer k : removeList) {
				System.out.print(k.intValue() + "|");
			}
			System.out.println();
			*/

			for (Integer k : removeList) {
				nonBurningVertices.remove(k);
			}

			//System.out.println("TempFitness: " + tempFitness);
			// save best fitness
			if (crew.getFitness() < tempFitness) {
				// System.out.println("New Fitness: " + nonBurningVertices.size());
				crew.setFitness(tempFitness);
				crew.setBestTimeStep(i);
			}


			// save non burning vertices in step i
			int l = 0;
			//System.out.print("NonBurning: ");
			for (Integer k : nonBurningVertices) {
				crew.setNonBurningVerticesIndex(k.intValue(), i, l);
				//System.out.print(k.intValue() + "|");
				l++;
			}
			//System.out.println();
			
			int fitnessTest = 0;
			for(Integer k: nonBurningVertices) {
				if(!defendedVertices.contains(k)) {
					fitnessTest++;
				}
							
			}
			crew.setMaxNonBurningVertices(fitnessTest);
			/*if (tempFitness > (Main.CrewSize + fitnessTest)) {
				System.out.println("-----------------Error------------------");
			}
			*/

			defendedVertices.clear();
		}
		nonBurningVertices.clear();
		crew.setChanged(false);
		crew.setNewCrew(false);
	}

	// Hilfsfunktionen

	// check if movement for fighter 2 in timestep t is possible in Bezug auf linken
	// nachbarn
	// TODO: aufeinander laufen erlauben
	public boolean movementPossible(ConnectedFireFighter fighter2, int timestep) {
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
				possible = true;
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
				possible = true;
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
				possible = true;
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
				possible = true;
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
	public int movementCalculator(ConnectedFireFighter fighter2, int timestep) {

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
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(2, timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(8, timestep + 1);
					return movement2;
				}

				// fighter 1 north
			case 1:
				// Fighter laufen aufeinander
				temp = Main.rnd.nextInt(5);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(9, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 3:
					movement2 = 3;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;

				case 4:
					movement2 = 4;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;
				}

				// return -1;
				// fighter 1 east
			case 2:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(8, timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;
				}

				// figher 1 south
			case 3:
				movement2 = 3;
				fighter2.setPositionIndex(1, timestep + 1);
				return movement2;
			// fighter 1 west
			case 4:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(2, timestep + 1);
					return movement2;

				case 1:
					movement2 = 3;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(1, timestep + 1);
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
					fighter2.setPositionIndex(2, timestep + 1);
					return movement2;

				case 1:
					movement2 = 3;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(2, timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(4, timestep + 1);
					return movement2;
				}
				// fighter 1 east
			case 2:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(2, timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(8, timestep + 1);
					return movement2;
				}
				// figher 1 south
			case 3:
				movement2 = 3;
				fighter2.setPositionIndex(2, timestep + 1);
				return movement2;
			// fighter 1 west
			case 4:
				movement2 = 4;
				fighter2.setPositionIndex(2, timestep + 1);
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
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(2, timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(4, timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(4, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;
				}
				// fighter 1 east
			case 2:
				// Fighter laufen aufeinander
				temp = Main.rnd.nextInt(5);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(9, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 3:
					movement2 = 3;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;

				case 4:
					movement2 = 4;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;
				}
				// return -1;
				// figher 1 south
			case 3:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(2, timestep + 1);
					return movement2;

				case 1:
					movement2 = 3;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;
				}
				// fighter 1 west
			case 4:
				movement2 = 4;
				fighter2.setPositionIndex(3, timestep + 1);
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
					fighter2.setPositionIndex(4, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				movement2 = 1;
				fighter2.setPositionIndex(4, timestep + 1);
				return movement2;
			// fighter 1 east
			case 2:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(4, timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(6, timestep + 1);
					return movement2;
				}
				// figher 1 south
			case 3:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(2, timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(4, timestep + 1);
					return movement2;
				}
				// fighter 1 west
			case 4:
				movement2 = 4;
				fighter2.setPositionIndex(4, timestep + 1);
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
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(4, timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(6, timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				movement2 = 1;
				fighter2.setPositionIndex(5, timestep + 1);
				return movement2;
			// fighter 1 east
			case 2:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(6, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;
				}
				// figher 1 south
			case 3:
				// Fighter laufen aufeinander
				temp = Main.rnd.nextInt(5);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(9, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 3:
					movement2 = 3;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;

				case 4:
					movement2 = 4;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;
				}
				// fighter 1 west
			case 4:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(4, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(5, timestep + 1);
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
					fighter2.setPositionIndex(6, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				movement2 = 1;
				fighter2.setPositionIndex(6, timestep + 1);
				return movement2;
			// fighter 1 east
			case 2:
				movement2 = 2;
				fighter2.setPositionIndex(6, timestep + 1);
				return movement2;
			// figher 1 south
			case 3:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(8, timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(6, timestep + 1);
					return movement2;
				}
				// fighter 1 west
			case 4:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(4, timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(6, timestep + 1);
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
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(8, timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(6, timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(6, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;
				}
				// fighter 1 east
			case 2:
				movement2 = 2;
				fighter2.setPositionIndex(7, timestep + 1);
				return movement2;
			// figher 1 south
			case 3:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(8, timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;
				}
				// fighter 1 west
			case 4:
				// Fighter laufen aufeinander
				temp = Main.rnd.nextInt(5);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(9, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 3:
					movement2 = 3;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;

				case 4:
					movement2 = 4;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;
				}
				// return -1;
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
					fighter2.setPositionIndex(8, timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;
				}
				// fighter 1 north
			case 1:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(8, timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(6, timestep + 1);
					return movement2;
				}
				// fighter 1 east
			case 2:
				movement2 = 2;
				fighter2.setPositionIndex(8, timestep + 1);
				return movement2;
			// figher 1 south
			case 3:
				movement2 = 3;
				fighter2.setPositionIndex(8, timestep + 1);
				return movement2;
			// fighter 1 west
			case 4:
				temp = Main.rnd.nextInt(3);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(2, timestep + 1);
					return movement2;

				case 2:
					movement2 = 4;
					fighter2.setPositionIndex(8, timestep + 1);
					return movement2;
				}
			}
			// fighter übereinander
		case 9:
			switch (movement1) {
			// no movement fighter 1
			case 0:
				temp = Main.rnd.nextInt(5);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(9, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 3:
					movement2 = 3;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;

				case 4:
					movement2 = 4;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;
				}

				// fighter 1 north
			case 1:
				temp = Main.rnd.nextInt(4);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(5, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(9, timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(4, timestep + 1);
					return movement2;

				case 3:
					movement2 = 4;
					fighter2.setPositionIndex(6, timestep + 1);
					return movement2;
				}
				// fighter 1 east
			case 2:
				temp = Main.rnd.nextInt(4);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(7, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(8, timestep + 1);
					return movement2;

				case 2:
					movement2 = 2;
					fighter2.setPositionIndex(9, timestep + 1);
					return movement2;

				case 3:
					movement2 = 3;
					fighter2.setPositionIndex(6, timestep + 1);
					return movement2;
				}
				// figher 1 south
			case 3:
				temp = Main.rnd.nextInt(4);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(1, timestep + 1);
					return movement2;

				case 1:
					movement2 = 2;
					fighter2.setPositionIndex(2, timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(9, timestep + 1);
					return movement2;

				case 3:
					movement2 = 4;
					fighter2.setPositionIndex(8, timestep + 1);
					return movement2;
				}
				// fighter 1 west
			case 4:
				temp = Main.rnd.nextInt(4);
				switch (temp) {
				case 0:
					movement2 = 0;
					fighter2.setPositionIndex(3, timestep + 1);
					return movement2;

				case 1:
					movement2 = 1;
					fighter2.setPositionIndex(2, timestep + 1);
					return movement2;

				case 2:
					movement2 = 3;
					fighter2.setPositionIndex(4, timestep + 1);
					return movement2;

				case 3:
					movement2 = 4;
					fighter2.setPositionIndex(9, timestep + 1);
					return movement2;
				}
			}

			break;
		}
		// nicht erreichbar, da überall return statements		
		return -1;

	}

	private boolean checkIfValid(int direction, int vertice, int[] compareVertices) {

		// check if the vertice in direction of given vertice is valid
		// means: it is not in compareVertices and not out of bounds
		switch (direction) {
		// north
		case 1:
			if ((vertice + Main.GridLength) < Main.GridSize) {
				if (intInArray(vertice + Main.GridLength, compareVertices)) {
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
			if (((vertice + Main.GridLength) < Main.GridSize) && ((vertice % Main.GridLength) <= Main.GridLength - 2)) {
				if (intInArray(vertice + Main.GridLength + 1, compareVertices)) {
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
			if ((vertice % Main.GridLength) <= Main.GridLength - 2) {
				if (intInArray(vertice + 1, compareVertices)) {
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
			if (((vertice % Main.GridLength) <= Main.GridLength - 2) && ((vertice - Main.GridLength) > 0)) {
				if (intInArray(vertice - Main.GridLength - 1, compareVertices)) {
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
			if ((vertice - Main.GridLength) > 0) {
				if (intInArray(vertice - Main.GridLength, compareVertices)) {
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
			if (((vertice - Main.GridLength) > 0) && ((vertice % Main.GridLength) >= 2)) {
				if (intInArray(vertice - Main.GridLength - 1, compareVertices)) {
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
			if ((vertice % Main.GridLength) >= 2) {
				if (intInArray(vertice - 1, compareVertices)) {
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
			if (((vertice % Main.GridLength) >= 2) && ((vertice + Main.GridLength) < Main.GridSize)) {
				if (intInArray(vertice + Main.GridLength - 1, compareVertices)) {
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

	public List<ConnectedFireFighterCrew> getPopulation() {
		return population;
	}

	public void setPopulation(List<ConnectedFireFighterCrew> population) {
		this.population = population;
	}

	public ConnectedFireFighterCrew getBestCrew() {
		return bestCrew;
	}

	public void setBestCrew(ConnectedFireFighterCrew bestCrew) {
		this.bestCrew = bestCrew;
	}

	public int[] getBestSetUp() {
		return bestSetUp;
	}

	public void setBestSetUp(int[] bestSetUp) {
		this.bestSetUp = bestSetUp;
	}

}
