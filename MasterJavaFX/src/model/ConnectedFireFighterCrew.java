/** Author: Moritz Wiemker
 * 	Masterthesis
 *
 *
 *
 */

package model;

import java.util.ArrayList;
import java.util.List;

import application.Main;

public class ConnectedFireFighterCrew implements Comparable<ConnectedFireFighterCrew> {
	private int ID;
	// number of non burning vertices
	private int Fitness = 0;
	private List<ConnectedFireFighter> crew = new ArrayList<ConnectedFireFighter>();
	private int[] bestSetup = new int[Main.CrewSize];
	private boolean changed = false;
	private boolean newCrew = false;

	// constructor
	public ConnectedFireFighterCrew() {
		this.ID = Main.CrewID;
		Main.CrewID++;
	}

	// getter and setter
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getFitness() {
		return Fitness;
	}

	public void setFitness(int fitness) {
		Fitness = fitness;
	}

	public List<ConnectedFireFighter> getCrew() {
		return crew;
	}

	public void setCrew(List<ConnectedFireFighter> crew) {
		this.crew = crew;
	}

	public int[] getBestSetup() {
		return bestSetup;
	}

	public void setBestSetup(int[] bestSetup) {
		this.bestSetup = bestSetup;
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public boolean isNewCrew() {
		return newCrew;
	}

	public void setNewCrew(boolean newCrew) {
		this.newCrew = newCrew;
	}

	// shifts for the grid
	// shift in x direction -- half of the grid
	public void shiftXPositive() {
		for (int i = 0; i < crew.size(); i++) {
			crew.get(i).setCurrentVertice(crew.get(i).getCurrentVertice() + (Main.GridLength / 2));
		}
	}

	// shift in x direction -- half of the grid
	public void shiftXNegative() {
		for (int i = 0; i < crew.size(); i++) {
			crew.get(i).setCurrentVertice(crew.get(i).getCurrentVertice() - (Main.GridLength / 2));
		}
	}

	// shift in y direction -- half of the grid
	public void shiftYPositive() {
		for (int i = 0; i < crew.size(); i++) {
			crew.get(i).setCurrentVertice(crew.get(i).getCurrentVertice() + ((Main.GridLength / 2) * Main.GridLength));
		}
	}

	// shift in y direction -- half of the grid
	public void shiftYNegative() {
		for (int i = 0; i < crew.size(); i++) {
			crew.get(i).setCurrentVertice(crew.get(i).getCurrentVertice() - ((Main.GridLength / 2) * Main.GridLength));
		}
	}

	@Override
	public int compareTo(ConnectedFireFighterCrew arg0) {
		if (Fitness < arg0.getFitness()) {
			return 1;
		}

		if (Fitness > arg0.getFitness()) {
			return -1;
		}

		return 0;
	}

}
