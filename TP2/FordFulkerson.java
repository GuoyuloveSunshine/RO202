import java.util.ArrayList;
import java.util.List;
import java.lang.*;

public class FordFulkerson {

	public static void main(String[] args) {
		example();
		Figure1_1();
		Figure1_2();
	}
	
	/** Function which creates an example graph on which Ford-Fulkerson will be applied */
	public static void example() {
		
		Graph g = new Graph(new String[] {"s", "a", "b", "c", "d", "e", "t"});
		g.addArc("s", "a", 8);
		g.addArc("s", "c", 4);
		g.addArc("s", "e", 6);
		g.addArc("a", "b", 10);
		g.addArc("a", "d", 4);
		g.addArc("b", "t", 8);
		g.addArc("c", "b", 2);
		g.addArc("c", "d", 1);
		g.addArc("d", "t", 6);
		g.addArc("e", "b", 4);
		g.addArc("e", "t", 2);

		/* The weight in graph g are the capacities of the arcs */
		System.out.println("The graph is:\n");
		System.out.println(g.toString());

		/* The weights in graph "flow" are the values of the flow */ 
		Graph flow = fordFulkerson(g, "s", "t");
		System.out.println("The flow is:\n");
		System.out.println(flow.toString());
	}

	public static void Figure1_1() {
		
		Graph g = new Graph(new String[] {"s", "1", "2", "3", "4", "t"});

		g.addArc("s", "1", 16);
		g.addArc("s", "2", 13);
		g.addArc("1", "2", 10);
		g.addArc("2", "1", 4);
		g.addArc("1", "3", 12);
		g.addArc("3", "2", 9);
		g.addArc("2", "4", 14);
		g.addArc("4", "3", 7);
		g.addArc("3", "t", 20);
		g.addArc("4", "t", 4);

		/* The weight in graph g are the capacities of the arcs */
		System.out.println("The graph is:\n");
		System.out.println(g.toString());

		/* The weights in graph "flow" are the values of the flow */ 
		Graph flow = fordFulkerson(g, "s", "t");
		System.out.println("The flow is:\n");
		System.out.println(flow.toString());
		
	}

	public static void Figure1_2() {
		
		Graph g = new Graph(new String[] {"s", "A", "B", "C", "D", "E", "F", "t"});

		g.addArc("s", "A", 10);
		g.addArc("s", "C", 12);
		g.addArc("s", "E", 15);
		g.addArc("A", "B", 9);
		g.addArc("A", "C", 4);
		g.addArc("A", "D", 15);
		g.addArc("B", "D", 15);
		g.addArc("B", "t", 10);
		g.addArc("C", "D", 8);
		g.addArc("C", "E", 4);
		g.addArc("D", "F", 15);
		g.addArc("D", "t", 10);
		g.addArc("E", "F", 16);
		g.addArc("F", "C", 6);
		g.addArc("F", "t", 10);

		/* The weight in graph g are the capacities of the arcs */
		System.out.println("The graph is:\n");
		System.out.println(g.toString());

		/* The weights in graph "flow" are the values of the flow */ 
		Graph flow = fordFulkerson(g, "s", "t");
		System.out.println("The flow is:\n");
		System.out.println(flow.toString());
		
	}
	public static Graph fordFulkerson(Graph g, String sName, String tName) {

		/* Mark of the nodes in the graph 
		 * - mark[i] is equal to +j if the node of index i can be reached by increasing the flow on arc ji;
		 * - mark[i] is equal to -j if the node of index i can be reached by decreasing the flow on arc ij;
		 * - mark[i] is equal to Integer.MAX_VALUE if the node is not marked.
		 */
		int[] mark = new int[g.n];
		
		/* Get the index of nodes s and t */
		int s = g.indexOf(sName);
		int t = g.indexOf(tName);
		
		/* Create a new graph with the same nodes than g, which corresponds to the current flow */
		Graph flow = new Graph(g.nodes);
		/* Get all the arcs of the graph */
		List<Edge> arcs = g.getArcs();
		
		// TODO
		do{
			System.out.println("-----New iteration \n");
			boolean newNodeMarked = false;

			/*See all node as unmarked*/
			for(int i = 0; i < g.n; i++){
				mark[i] = Integer.MAX_VALUE;
			}
			mark[s] = 0;
			do{
				newNodeMarked = false;
				for (int i = 0; i <  g.n ; i++){
					for(int j = 0; j < g.n; j++){
						if(mark[i] < Integer.MAX_VALUE && g.adjacency[i][j] > 0 
							&& mark[j] == Integer.MAX_VALUE && flow.adjacency[i][j] < g.adjacency[i][j]){
								mark[j] = +i;
								newNodeMarked = true;
							}
						if(mark[i] == Integer.MAX_VALUE && g.adjacency[i][j] > 0 
							&& mark[j] < Integer.MAX_VALUE && flow.adjacency[i][j] > 0){
								mark[i] = -j;
								newNodeMarked = true;
							}
						
					}
				}
				// for (int i = 0; i < mark.length ; i++){
				// 	System.out.println("("+i+", "+ mark[i]+")");
				// }
			} while(newNodeMarked && mark[t] == Integer.MAX_VALUE);
			// Ameliorer
			if(mark[t] != Integer.MAX_VALUE){
				double value_ameliorer = Integer.MAX_VALUE;
				int curr = t, pre = Math.abs(mark[t]);
				while(curr != 0){
					if(g.adjacency[pre][curr] != 0){// pre est positive
						value_ameliorer = Math.min(value_ameliorer,g.adjacency[pre][curr]-flow.adjacency[pre][curr]);
					}
					else if(g.adjacency[curr][pre] != 0 ){ // pre est negative
						value_ameliorer = Math.min(value_ameliorer,flow.adjacency[curr][pre]);
					}
					curr = pre;
					pre = Math.abs(mark[curr]);
				}
				// Ameliorer
				curr = t;
				pre = Math.abs(mark[t]);
				while(curr != 0){
					if (g.adjacency[pre][curr] != 0 ) flow.adjacency[pre][curr] += value_ameliorer;
					else if (g.adjacency[curr][pre] != 0 ) flow.adjacency[curr][pre] -= value_ameliorer;
					curr = pre;
					pre = Math.abs(mark[curr]);
				}
			}
			// System.out.println(flow.toString());
		}while(mark[t] != Integer.MAX_VALUE);
		
		return flow;

	}
}
