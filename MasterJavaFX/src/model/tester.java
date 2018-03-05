package model;

import application.Main;
import controller.EvolutionaryAlgoConnected;

public class tester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Tester");
		
		EvolutionaryAlgoConnected evAlgo = new EvolutionaryAlgoConnected();
		//evAlgo.evAlgo();
		
		ConnectedFireFighterCrew crew = new ConnectedFireFighterCrew();
		int start = 5050;

		ConnectedFireFighter fighter1 = new ConnectedFireFighter();
		ConnectedFireFighter fighter2 = new ConnectedFireFighter();
		ConnectedFireFighter fighter3 = new ConnectedFireFighter();
		ConnectedFireFighter fighter4 = new ConnectedFireFighter();
		ConnectedFireFighter fighter5 = new ConnectedFireFighter();
		
		fighter1.setStartVertice(start);
		fighter1.setCurrentVertice(start);
		
		int[] chain1 = new int[Main.TimeInterval];
		chain1[0] = 0;
		for(int j = 1; j < Main.TimeInterval; j++) {
			chain1[j] = 1;
		}		
		fighter1.setChain(chain1);
		crew.getCrew().add(fighter1);
		
		
		fighter2.setStartVertice(start + Main.GridLength - 1);
		fighter2.setCurrentVertice(start + Main.GridLength - 1);
		
		int[] chain2 = new int[Main.TimeInterval];
		chain2[0] = 0;
		for(int j = 1; j < Main.TimeInterval; j++) {
			chain2[j] = 1;
		}
		fighter2.setChain(chain2);
		crew.getCrew().add(fighter2);
		
		fighter3.setStartVertice(start + Main.GridLength + 1);
		fighter3.setCurrentVertice(start + Main.GridLength + 1);
		
		int[] chain3 = new int[Main.TimeInterval];
		chain3[0] = 0;
		for(int j = 1; j < Main.TimeInterval; j++) {
			chain3[j] = 1;
		}
		fighter3.setChain(chain3);
		crew.getCrew().add(fighter3);
		
		
		fighter4.setStartVertice(start + Main.GridLength);
		fighter4.setCurrentVertice(start + Main.GridLength);
		
		int[] chain4 = new int[Main.TimeInterval];
		chain4[0] = 1;
		for(int j = 1; j < Main.TimeInterval; j++) {
			chain4[j] = 1;
		}
		fighter4.setChain(chain4);
		crew.getCrew().add(fighter4);
		
		
		
		
		
		
		for(int i = 0; i < Main.CrewSize - 4; i++) {
			ConnectedFireFighter fighter = new ConnectedFireFighter();
			fighter.setStartVertice(start + i + 1 -1000);
			fighter.setCurrentVertice(fighter.getStartVertice());
			
			int[] chain = new int[Main.TimeInterval];
			for(int j = 0; j < Main.TimeInterval; j++) {
				chain[j] = 1;
			}
			fighter.setChain(chain);
			crew.getCrew().add(fighter);
		}
		
		evAlgo.calculateFitness(crew);
		
		System.out.println("Crew: " + crew.getCrew().size());
		System.out.println("Fitness : " + crew.getFitness());
		
		System.out.println("Start Vertices");
		for (int j = 0; j < Main.CrewSize; j++) {
			System.out.print(crew.getCrew().get(j).getStartVertice() + "|");
			
		}
		
		System.out.println();
		
		System.out.println("Defended Vertices");
		for (int i = 0; i < Main.TimeInterval; i++) {
			for (int j = 0; j < Main.CrewSize; j++) {
				System.out.print(crew.getDefendedVerticesIndex(i, j) + "|");
				
			}
			System.out.println();
		}
		System.out.println();
		
		System.out.println();
		System.out.println("Non Burning Vertices");
		for (int i = 0; i < Main.TimeInterval; i++) {
			for (int j = 0; j < (Main.CrewSize * Main.CrewSize); j++ ) {
				System.out.print(crew.getNonBurningVerticesIndex(i, j)+ "|");
			}
			System.out.println();
		}
		System.out.println();
		
		/*	System.out.println("Current Vertices");
			for (int j = 0; j < Main.CrewSize; j++) {
				System.out.print(evAlgo.getBestCrew().getCrew().get(j).getCurrentVertice() + "|");
				
			}
			System.out.println();
			System.out.println("Start Vertices");
			for (int j = 0; j < Main.CrewSize; j++) {
				System.out.print(evAlgo.getBestCrew().getCrew().get(j).getStartVertice() + "|");
				
			}
			System.out.println();
			
			System.out.println("Defended Vertices");
			for (int i = 0; i < Main.TimeInterval; i++) {
				for (int j = 0; j < Main.CrewSize; j++) {
					System.out.print(evAlgo.getBestCrew().getDefendedVerticesIndex(i, j) + "|");
					
				}
				System.out.println();
			}
			System.out.println();
			System.out.println("Non Burning Vertices");
			for (int i = 0; i < Main.TimeInterval; i++) {
				for (int j = 0; j < (Main.CrewSize * Main.CrewSize); j++ ) {
					System.out.print(evAlgo.getBestCrew().getNonBurningVerticesIndex(i, j)+ "|");
				}
				System.out.println();
			}
			System.out.println();
		
		
		for (int i = 0; i < Main.CrewSize; i++) {
			System.out.print(evAlgo.getBestSetUp()[i] + "|");
		}
		
		/*evAlgo.initialize();
		
		for(int i = 0; i < Main.PopulationSize; i++) {

			System.out.print("Crew " + i + ": ");
			for (int j = 0; j < Main.CrewSize; j++) {
				System.out.print(evAlgo.getPopulation().get(i).getCrew().get(j).getStartVertice() + "|");
				
			}
			System.out.println();
		}
		*/
		
	}

}
