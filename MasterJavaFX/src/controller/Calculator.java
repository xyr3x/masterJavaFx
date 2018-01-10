package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import application.Main;
import model.FireFighterCrew;

public class Calculator {

//TODO: überarbeiten
	public void calculateFitness(FireFighterCrew crew) {
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

				// Randfälle! verlassener Knoten liegt am Rand/Ecke
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

}
