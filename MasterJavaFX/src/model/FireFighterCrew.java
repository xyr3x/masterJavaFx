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

public class FireFighterCrew implements Comparable<FireFighterCrew> {
	private int ID;
	//number of non burning vertices
	private int Fitness = 0;
	private int maxNonBurningVertices;
	private List<FireFighter> crew = new ArrayList<FireFighter>();
	private int bestTimeStep;
	private int generation;
	private boolean changed = false;
	private boolean newCrew = false;
	//save the defended vertices of all timesteps
	private int[][] defendedVertices = new int[Main.TimeInterval][Main.CrewSize];
	//save the non burning vertices of all timesteps
	private int[][] nonBurningVertices = new int[Main.TimeInterval][Main.CrewSize * Main.CrewSize];


	//constructor
	public FireFighterCrew(){
		this.ID = Main.CrewID;
		Main.CrewID++;
	}

	//getter and setter
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
	public List<FireFighter> getCrew() {
		return crew;
	}
	public void setCrew(List<FireFighter> crew) {
		this.crew = crew;
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
		
	public int getBestTimeStep() {
		return bestTimeStep;
	}

	public void setBestTimeStep(int bestTimeStep) {
		this.bestTimeStep = bestTimeStep;
	}
	
	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public int[][] getDefendedVertices() {
		return defendedVertices;
	}
	
	public int getDefendedVerticesIndex(int index1, int index2) {
		return defendedVertices[index1][index2];
	}

	public void setDefendedVertices(int[][] defendedVertices) {
		this.defendedVertices = defendedVertices;
	}
	
	public void setDefendedVerticesIndex(int value, int index1, int index2) {
		defendedVertices[index1][index2] = value;
	}

	public int[][] getNonBurningVertices() {
		return nonBurningVertices;
	}
	
	public int getNonBurningVerticesIndex(int index1, int index2) {
		return nonBurningVertices[index1][index2];
	}

	public void setNonBurningVertices(int[][] nonBurningVertices) {
		this.nonBurningVertices = nonBurningVertices;
	}
	
	public void setNonBurningVerticesIndex(int value, int index1, int index2) {
		nonBurningVertices[index1][index2] = value;
	}
	
	public int getMaxNonBurningVertices() {
		return maxNonBurningVertices;
	}

	public void setMaxNonBurningVertices(int maxNonBurningVertices) {
		this.maxNonBurningVertices = maxNonBurningVertices;
	}

	@Override
	public int compareTo(FireFighterCrew arg0) {
		if(Fitness < arg0.getFitness()){
			return 1;
		}

		if(Fitness > arg0.getFitness()){
			return -1;
		}

		return 0;
	}

}
