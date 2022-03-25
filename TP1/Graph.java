import java.util.ArrayList;
import java.util.List;

/**
 * Class which represents a graph.
 * 
 * The number of nodes in the graph is n.
 * 
 * The link of the graph are stored in a n*n matrix attribute called adjacency
 * adjacency[i][j] != 0 if there is a link between i and j.
 * 
 * If adjacency[i][j] == adjacency[i][j]] != 0, there is an edge between i and j.
 * If adjacency[i][j] != 0 and adjacency[i][j] == 0, there is an arc between i and j. 
 *
 */
public class Graph {

	/** Number of nodes in the graph */
	public int n;
	
	/** Name of the nodes in the graph */
	public String[] nodes;
	
	/** Links in the graphs (either edges or arcs) */
	public double[][] adjacency;

	/**
	 * Constructor
	 * @param sNames Name of the nodes
	 */
	public Graph(String[] sNames) {

		nodes = sNames.clone();
		n = nodes.length;
		adjacency = new double[n][n];

	}
	
	/**
	 * Add an edge to the graph
	 * @param edge
	 */
	public void addEdge(Edge edge) {
		addEdge(edge.id1, edge.id2, edge.weight);
	}

	/**
	 * Add an edge to the graph
	 * @param id1 Id of the first node in the matrix adjacency
	 * @param id2 Id of the second node in the matrix adjacency
	 * @param weight Weight of the edge
	 */
	public void addEdge(int id1, int id2, double weight) {
		addArc(id1, id2, weight);
		addArc(id2, id1, weight);
	}

	/**
	 * Add an edge to the graph
	 * @param name1 Name of the first node
	 * @param name2 Name of the second node
	 * @param weight Weight of the edge
	 */
	public void addEdge(String name1, String name2, double weight) {
		addEdge(indexOf(name1), indexOf(name2), weight);
	}

	/**
	 * Add an arc to the graph
	 * @param name1 Name of the origin node
	 * @param name2 Name of the destination node
	 * @param weight Weight of the arc
	 */
	public void addArc(String name1, String name2, double weight) {
		int id1 = indexOf(name1);
		int id2 = indexOf(name2);
		addArc(id1, id2, weight);
	}
	
	/**
	 * Add an arc to the graph
	 * @param id1 Id of the origin node in the adjacency matrix
	 * @param id2 Id of the destination node in the adjacency matrix
	 * @param weight Weight of the arc
	 */
	public void addArc(int id1, int id2, double weight) {
		if(id1 != -1 && id2 != -1) 
			adjacency[id1][id2] = weight;
	}
	
	/**
	 * Get the id of a node in the adjacency matrix from its name
	 * @param sName Name of the node
	 * @return Id of the node
	 */
	public int indexOf(String sName) {
		
		for(int i = 0; i < nodes.length; i++)
			if(nodes[i].equals(sName))
				return i;
		
		return -1;
	}
	
	/**
	 * Get all the arcs in the graph.
	 * @return The arcs.
	 */
	public List<Edge> getArcs(){
		
		List<Edge> result = new ArrayList<Edge>();
		
		for(int i = 0; i < n; ++i)
			for(int j = 0;j < n; ++j)
				if(adjacency[i][j] != 0)
					result.add(new Edge(i, j, adjacency[i][j]));
		
		return result;
	}
	
	/**
	 * Get all the edges in the graph.
	 * @return The edges.
	 */
	public List<Edge> getEdges(){
		
		List<Edge> result = new ArrayList<Edge>();
		
		for(int i = 0; i < n; ++i)
			for(int j = i+1;j < n; ++j)
				if(adjacency[i][j] != 0)
					result.add(new Edge(i, j, adjacency[i][j]));
		
		return result;
	}
	
	/**
	 * Returns a String which represents the graph.
	 * Enables to print a graph.
	 * For example: System.out.println(myGraph);
	 */
	@Override
	public String toString(){
		
		String result = "";
		
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				if(adjacency[i][j] != 0)
					
					/* If there is an edge */
					if(adjacency[i][j] == adjacency[j][i]) {
						/* if we meet this edge for the first time */ 
						if(i < j)
							result += nodes[i] + " - " + nodes[j] + " (" + adjacency[i][j] + ")\n";
					}
		
					/* If there is an arc */
					else
						result += nodes[i] + " - " + nodes[j] + " (" + adjacency[i][j] + ")\n";
		
		return result;
		
	}

	/**
	 * Check if adding an edge to the graph create a cycle
	 * @param edge The added edge
	 * @return True if the addition of the edge create a cycle; false otherwise.
	 */
	public boolean createACycle(Edge edge) {

		boolean cycleDetected = false;

		/* Nodes reached by following edges from e */
		List<Integer> reachedNodes = new ArrayList<Integer>();
		reachedNodes.add(edge.id1);

		if(reachedNodes.contains(edge.id2))
			cycleDetected = true;
		else	
			reachedNodes.add(edge.id2);

		/* Nodes reached by following edges from e which neighbors have not yet been considered */
		List<Integer> nodesToTest = new ArrayList<Integer>();
		nodesToTest.add(edge.id1);
		nodesToTest.add(edge.id2);
		
		List<Edge> reachedEdges = new ArrayList<Edge>();
		reachedEdges.add(new Edge(edge.id1, edge.id2, edge.weight));

		/* While no cycle is detected and we still have nodes to test */
		while(!cycleDetected && nodesToTest.size() > 0) {

			/* Get the next node to test */
			Integer currentNode = nodesToTest.get(0);
			nodesToTest.remove(0);

			/* Test its neighbors */
			int neighborIndex = 0;

			while(!cycleDetected && neighborIndex < n) {
				
				Edge currentEdge = new Edge(currentNode, neighborIndex, 1.0);

				/* If there is an edge */
				if(adjacency[currentNode][neighborIndex] != 0.0 && !reachedEdges.contains(currentEdge)) {

					/* If this node has already been reached, there is a cycle */
					if(reachedNodes.contains(neighborIndex))
						cycleDetected = true;

					/* Otherwise, add the node to the list of reached nodes and the list of nodes to test */
					else {
						reachedNodes.add(neighborIndex);
						reachedEdges.add(currentEdge);
						nodesToTest.add(neighborIndex);
					}
				}
				
				neighborIndex++;
			}

		}

		return cycleDetected;

	}

}
