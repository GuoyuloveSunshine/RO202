import java.util.ArrayList;
import java.util.List;

public class simon {

	public static void main(String[] args) {

		/* The weight in graph g are the capacities of the arcs */
		Graph g = example2();

		/* The weights in graph "flow" are the values of the flow */ 
		Graph flow = fordFulkerson(g, "s", "t");

		System.out.println(flow);

	}
	
	/** Function which creates an example graph on which Ford-Fulkerson will be applied */
	public static Graph example() {
		
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
		
		return g;
	}
	public static Graph example2() {
		// 28
		Graph g = new Graph(new String[] {"s", "a", "b", "c", "d", "e", "f", "t"});

		g.addArc("s", "a", 10);
		g.addArc("s", "c", 12);
		g.addArc("s", "e", 15);
		g.addArc("a", "b", 9);
		g.addArc("a", "d", 15);
		g.addArc("a", "c", 4);
		g.addArc("b", "t", 10);
		g.addArc("b", "d", 15);
		g.addArc("c", "d", 8);
		g.addArc("c", "e", 4);
		g.addArc("d", "t", 10);
		g.addArc("d", "f", 15);
		g.addArc("e", "f", 16);
		g.addArc("f", "c", 6);
		g.addArc("f", "t", 10);
		
		return g;
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
		// si un nouveau sommet a ete marque ou non
		boolean flag = false;

		do {
			// Retirer toutes les marques
			for (int imark=0; imark<g.n; imark++){
				mark[imark] = Integer.MAX_VALUE;
			}
			mark[s] = -1;
			do{
				flag = false;
				for (int v=0; v<g.n; v++){
					if (mark[v] == Integer.MAX_VALUE){
						for (int v2=0; v2<g.n; v2++){
							if (mark[v2] != Integer.MAX_VALUE){
								boolean com = (g.adjacency[v2][v] != 0 && flow.adjacency[v2][v]<g.adjacency[v2][v]) || (g.adjacency[v][v2] != 0 && flow.adjacency[v][v2]>0);
								if (com)
								{
									mark[v] = v2;
									flag = true;
								}


							}
						}

					}
				}

			}while (flag && mark[t] == Integer.MAX_VALUE);
		
		if (mark[t] != Integer.MAX_VALUE){
			// amelirer le flux
			double res = Double.MAX_VALUE;
			int pre = mark[t], cur = t;
			// chercher le flux maximal
			while (pre != -1){
				if (g.adjacency[pre][cur] != 0)
				{
					double aug = g.adjacency[pre][cur] - flow.adjacency[pre][cur];
					res = aug < res ? aug : res;
				}
				else if (g.adjacency[cur][pre] != 0)
				{
					double dim = flow.adjacency[cur][pre];
					res = dim < res ? dim : res;
				}
				cur = pre;
				pre = mark[cur];
			}
			// ameliorer
			pre = mark[t]; cur = t;
			while (pre != -1){
				if (g.adjacency[pre][cur] != 0)
				{
					flow.adjacency[pre][cur] += res;
				}
				else if (g.adjacency[cur][pre] != 0)
				{
					flow.adjacency[cur][pre] -= res;
				}
				cur = pre;
				pre = mark[cur];
			}

		}

		}while (mark[t] != Integer.MAX_VALUE);
		
		return flow;

	}
}

/*
Couplage : 
On ajoute un sommet 's' et des arcs de ce sommet vers tous les sommets de V1;
De même, on ajoute un autre sommet 't' et des arcs de tous les sommets de V2 vers 't';
Supposons que la capacité pour tous les arcs est 1;
En utilisant l'algorithme de Ford-Fulkerson, on peut trouver le flot maximal,
si le flot maximal = r, on a alors une solution d'ouvrir le coffre;
sinon, on a une solution pour déverrouiller un maximun de serrures.
*/

/*
Chemins disjoints :
Supposons que la capacité pour tous les arcs est 1;
En utilisant l'algorithme de Ford-Fulkerson, on peut trouver le flot maximal,
c'est aussi le nombre maximun de chemins arcs-disjoints entre 's' et 't'

s-a-c-b-d-t ; s-g-b-t ; s-e-h-c-f-t

on construit un nouveau graphe
pour chaque sommet sauf 's' et 't', on le divise en 2 nouveaux sommets; 
par ex pour le sommet 'a', on le divise en sommets'a1' et 'a2';
on ajoute les arcs prédécesseur(a)-a1, a1-a2, a2-successeur(a) dans le nouveau graphe;
on applique Ford-Fulkerson pour obtenir un nombre maximum de chemins sommets-disjoints

s-a-c-b-d-t ;

*/