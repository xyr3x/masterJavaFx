/**
 * 
 */
package application;

import controller.EvolutionaryAlgo;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.FireFighterCrew;

/**
 * @author Moritz
 *	Service class to execute evolutionary Algo in another thread
 */
public class EvolutionaryAlgoService extends Service<FireFighterCrew>{
	private Main main;
	EvolutionaryAlgo evAlgo = new EvolutionaryAlgo();
	LayoutController controller;
	
	public void setEvAlgo(EvolutionaryAlgo evAlgo) {
		this.evAlgo = evAlgo;
	}
	
	public EvolutionaryAlgo getEvAlgo() {
		return evAlgo;
	}
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	public void setController(LayoutController controller) {
		this.controller = controller;
	}

	@Override
	protected Task<FireFighterCrew> createTask() {
		// TODO Auto-generated method stub
		final EvolutionaryAlgo _evAlgo = getEvAlgo();
		
		return new Task<FireFighterCrew>() {

			@Override
			protected FireFighterCrew call() throws Exception {
				FireFighterCrew bestCrew = new FireFighterCrew();
				
				bestCrew = _evAlgo.evAlgo();
				
				return bestCrew;
			}
			
		};
	}
	
	
	private void updateUI(FireFighterCrew bestCrew) {
		//TODO: beste crew updaten
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				controller.setBestCrew(bestCrew);
				
			}
			
		});
	}

}
