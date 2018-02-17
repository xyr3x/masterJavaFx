/** Author: Moritz Wiemker
 * 	Masterthesis
 *
 *	ConnectedFirefighter is the instantiation of the AbstractFireFighter with two pointers added
 *	The pointers point to the left and the right neighbour of the fighter
 *
 */

package model;

import application.Main;

public class ConnectedFireFighter extends AbstractFireFighter{
	//left and right neighbour
	private ConnectedFireFighter leftNeighbour;
	private ConnectedFireFighter rightNeighbour;

	//position im bezug auf linken nachbar. Werte 1-8 im Uhrzeigersinn, dabei 1 oberhalb und 8 links oberhalb
	private int[] position = new int[Main.TimeInterval + 1];
	
	// constructor
	public ConnectedFireFighter() {
		super();
		for (int i = 0; i < Main.TimeInterval + 1; i++) {
			position[i] = 0;
		}
	}

	// getter and setter
	public ConnectedFireFighter getLeftNeighbour() {
		return leftNeighbour;
	}

	public void setLeftNeighbour(ConnectedFireFighter leftNeighbour) {
		this.leftNeighbour = leftNeighbour;
	}

	public ConnectedFireFighter getRightNeighbour() {
		return rightNeighbour;
	}

	public void setRightNeighbour(ConnectedFireFighter rightNeighbour) {
		this.rightNeighbour = rightNeighbour;
	}

	public int[] getPosition() {
		return position;
	}
	
	public void setPosition(int[] position) {
		this.position = position;
	}
	
	public int getPositionIndex(int index) {
		return position[index];
	}
	
	public void setPositionIndex(int position, int index) {
		this.position[index] = position;
	}

}
