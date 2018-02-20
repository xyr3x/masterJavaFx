package model;

import application.Main;
import controller.EvolutionaryAlgoConnected;

public class tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Tester");
		
		EvolutionaryAlgoConnected evAlgo = new EvolutionaryAlgoConnected();
		evAlgo.initialize();
		
		for(int i = 0; i < Main.PopulationSize; i++) {

			System.out.print("Crew " + i + ": ");
			for (int j = 0; j < Main.CrewSize; j++) {
				System.out.print(evAlgo.getPopulation().get(i).getCrew().get(j).getStartVertice() + "|");
				
			}
			System.out.println();
		}
	}

}
