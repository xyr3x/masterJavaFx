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
 *
 */
public class EvolutionaryAlgoService extends Service<FireFighterCrew>{
	EvolutionaryAlgo evAlgo = new EvolutionaryAlgo();
	
	public void setEvAlgo(EvolutionaryAlgo evAlgo) {
		this.evAlgo = evAlgo;
	}
	
	public EvolutionaryAlgo getEvAlgo() {
		return evAlgo;
	}

	@Override
	protected Task createTask() {
		// TODO Auto-generated method stub
		System.out.println("Task läuft");
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
				
			}
			
		});
	}

}
