/**
 * Represents an Edge in a graph.
 * To ease the implementation, the same class is used to represent an arc (when the graph is oriented)
 *
 */
public class Edge implements Comparable<Edge>{
	
	/** Id of the first extremity of the edge in the graph (or id of the origin if it is an arc) */
	public int id1;
	
	/** Id of the second extremity of the edge in the graph (or id of the destination if it is an arc) */
	public int id2;
	
	/** Weight of the edge */
	public double weight;
    
	/**
	 * Constructor
	 * @param id1 Id of the first extremity
	 * @param id2 Id of the second extremity
	 * @param weight Weight of the edge
	 */
    public Edge(int id1, int id2, double weight) {
    	this.id1 = id1;
    	this.id2 = id2;
    	this.weight = weight;
    }

    /** Compare the current edge with another (enable to sort by increasing weight the edges) */
	@Override
	public int compareTo(Edge o) {
		return (int)(this.weight - o.weight);
	}
	
	/** Test if the current edge is equal to an object */
	@Override
	public boolean equals(Object o) {
		
		if(o instanceof Edge){
			Edge e = (Edge)o;
			return e.id1 == id1 && e.id2 == id2 || e.id1 == id2 && e.id1 == id2;
		}
		else
			return false;
	}
	
	/** Enables to display an edge */
	@Override
	public String toString() {
		return "(" + id1 + "-" + id2 + ": " + weight + ")";
	}
	
}
