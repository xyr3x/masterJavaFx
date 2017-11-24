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
	private List<FireFighter> crew = new ArrayList<FireFighter>();
	private int[] bestSetup = new int[Main.CrewSize];
	private boolean changed = false;
	private boolean newCrew = false;


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

	public int[] getBestSetup(){
		return bestSetup;
	}

	public void setBestSetup(int[] bestSetup){
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
