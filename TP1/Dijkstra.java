import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import javax.sound.midi.SysexMessage;

public class Dijkstra {

	public static void main(String[] args) {

		/* Create the oriented graph */
		Graph g = new Graph(new String[] {"Paris", "Hambourg", "Londres", "Amsterdam", "Edimbourg", "Berlin", "Stockholm", "Rana", "Oslo"});
		
		g.addArc("Paris", "Hambourg", 7);
		g.addArc("Paris",  "Londres", 4);
		g.addArc("Paris",  "Amsterdam", 3);
		g.addArc("Hambourg",  "Stockholm", 1);
		g.addArc("Hambourg",  "Berlin", 1);
		g.addArc("Londres",  "Edimbourg", 2);
		g.addArc("Amsterdam",  "Hambourg", 2);
		g.addArc("Amsterdam",  "Oslo", 8);
		g.addArc("Stockholm",  "Oslo", 2);
		g.addArc("Stockholm",  "Rana", 5);
		g.addArc("Berlin",  "Amsterdam", 2);
		g.addArc("Berlin",  "Stockholm", 1);
		g.addArc("Berlin",  "Oslo", 3);
		g.addArc("Edimbourg",  "Oslo", 7);
		g.addArc("Edimbourg",  "Amsterdam", 3);
		g.addArc("Edimbourg",  "Rana", 6);
		g.addArc("Oslo",  "Rana", 2);
		
		/* Apply Dijkstra algorithm to get an arborescence */
		Graph tree = dijkstra(g, "Paris");
		
		System.out.println(tree.toString());

	}
	
	/**
	 * Apply Dijkstra algorithm on a graph
	 * @param g The graph considered
	 * @param origin The starting node of the paths
	 * @return A graph which is an arborescence and represent the shortest paths from the origin to all the other nodes 
	 */
	public static Graph dijkstra(Graph g, String origin) {
		
		/* Get the index of the origin */
		int r = g.indexOf(origin);
		
		/* Next node considered */
		int pivot = r;
		
		/* Create a list that will contain the nodes which have been considered */
		List<Integer> V2 = new ArrayList<>();
		V2.add(r);
		
		
		int[] pred = new int[g.n];
		double[] pi = new double[g.n];
		
		/* Initially, the distance between r and the other nodes is the infinity */
		for(int v = 0; v < g.n; v++)
			if(v != r)
				pi[v] = Double.POSITIVE_INFINITY;

		// Mettre votre code ici
		for(int j = 0; j < g.n; j++){
			for (int y = 0; y < g.n; y++){
				if(g.adjacency[pivot][y] > 0 && !V2.contains(y)){
					if(pi[pivot] + g.adjacency[pivot][y] < pi[y]){
						pi[y] = pi[pivot] + g.adjacency[pivot][y];
						pred[y] = pivot;
					}
				}
			}
			int temp_min = pi.length-1;
			for(int z = 0; z < pi.length; z++){
				if(!V2.contains(z) && pi[z] < pi [temp_min]){
					temp_min = z;
				}
			}
			
			pivot = temp_min;
			V2.add(pivot);
		}

		Graph tree = new Graph(g.nodes);
		for(int i = 0; i < pred.length; i++){
			tree.addArc(pred[i], i, g.adjacency[pred[i]][i]);
			// System.out.print("("+i+", "+pred[i]+")");
		}
		return tree;
	}
}
