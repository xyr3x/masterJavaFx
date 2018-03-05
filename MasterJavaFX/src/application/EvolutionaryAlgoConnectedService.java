package application;

import controller.EvolutionaryAlgoConnected;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.ConnectedFireFighterCrew;

/**
 * @author Moritz
 *	Service class to execute connected evolutionary Algo in another thread
 */
public class EvolutionaryAlgoConnectedService extends Service<ConnectedFireFighterCrew>{
	private Main main;
	EvolutionaryAlgoConnected evAlgo = new EvolutionaryAlgoConnected();
	LayoutController controller;
	
	public void setEvAlgo(EvolutionaryAlgoConnected evAlgo) {
		this.evAlgo = evAlgo;
	}
	
	public EvolutionaryAlgoConnected getEvAlgo() {
		return evAlgo;
	}
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	public void setController(LayoutController controller) {
		this.controller = controller;
	}

	protected Task<ConnectedFireFighterCrew> createTask() {
		// TODO Auto-generated method stub
		final EvolutionaryAlgoConnected _evAlgo = getEvAlgo();
		return new Task<ConnectedFireFighterCrew>() {

			@Override
			protected ConnectedFireFighterCrew call() throws Exception {
				ConnectedFireFighterCrew bestCrew = new ConnectedFireFighterCrew();
				bestCrew = _evAlgo.evAlgo();
				return bestCrew;
			}
			
		};
	}
	
	
	private void updateUI(ConnectedFireFighterCrew bestCrew) {
		//TODO: beste crew updaten
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				controller.setBestCrewConnected(bestCrew);
				
			}
			
		});
	}
}
