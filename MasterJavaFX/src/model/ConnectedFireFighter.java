/** Author: Moritz Wiemker
 * 	Masterthesis
 *
 *	ConnectedFirefighter is the instantiation of the AbstractFireFighter with two pointers added
 *	The pointers point to the left and the right neighbour of the fighter
 *
 */

package model;


public class ConnectedFireFighter extends AbstractFireFighter{
	//left and right neighbour
	private ConnectedFireFighter leftNeighbour;
	private ConnectedFireFighter rightNeighbour;

	// constructor
	public ConnectedFireFighter() {
		super();
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



}
