import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node of a branch and bound tree for a knapsack problem
 * @author zach
 *
 */
public class KnapsackBBNode extends BBNode{

	/* Weight of the objects */
	double[] p;
	
	/* Capacity of the knapsack */
	double K;

        /** Create a leaf */ 
	public KnapsackBBNode(BBNode parent, double[] newA, double newRhs, double[] p, double K) {
		super(parent, newA, newRhs);
		this.p = p;
		this.K = K;
	}
	
	/** Create a root */
	public KnapsackBBNode(double[][] A, double[] rhs, double[] obj, boolean isMinimisation, double[] p, double K) {
		super(A, rhs, obj, isMinimisation);
		this.p = p;
		this.K = K;
	}

	public static void main(String[] args) {
		
		BBTree kTree = exKnapsack();
		kTree.solve();
		kTree.displaySolution();
		
	}

	public static BBTree exKnapsack() {

		double[] p = new double[]{3, 7, 9, 6};
		double K = 17;
		
		double[][] mA = new double[][] {p,
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{0, 0, 0, 1}
        };

		double[] rhs = new double[] {K, 1, 1, 1, 1};
		double[] obj = new double[] {8, 18, 20, 11};
		boolean isMinimization = false;

		return new BBTree(new KnapsackBBNode(mA, rhs, obj, isMinimization, mA[0], K));
	}
	
	/** Generate a cover inequality to cut the optimal solution of the linear relaxation */
	private void generateCut() {
		List<Integer> cover = new ArrayList<Integer>();
        double weightCouver = 0;
        int indice = 0;
		if (this.tableau.bestSolution == null)
			return;
        while(weightCouver < this.K && indice < this.tableau.n){
            if(this.tableau.bestSolution[indice] != 0){
                cover.add(indice);
                weightCouver += this.p[indice];
            }
            indice++;
        }
        if(weightCouver > this.K){
			System.out.print(Utility.genSpace(this.depth) + 
					"Found cover : ");
			for (int i : cover) {
				System.out.print((i+1) + " ");
			}
			System.out.println();
			System.out.print(Utility.genSpace(this.depth) + "Relaxation before the cut : ");
            this.tableau.displaySolution();
			
			this.addCoverCutToTableau(cover);
			this.tableau.applySimplexPhase1And2();
            System.out.print(Utility.genSpace(this.depth) + "Relaxation after the cut : ");
			this.tableau.displaySolution();
        }
	}

    
	@Override
	public void branch(BBTree tree) {

		/* Solve the linear relaxation */
		tableau.applySimplexPhase1And2();
		if(tableau.bestSolution != null){
			int id = -1;
			if(tree.bestObjective == null|| !(tableau.bestObjective < tree.bestObjective)^tableau.isMinimization ){
				for(int i = 0; i <tableau.n; i++){
					if(Utility.isFractional(tableau.bestSolution[i])){
						id = i;
						if(this.depth == 0) System.out.print("root: ");
						System.out.println("x[" + (id+1) + "] = " + Utility.nf.format(tableau.bestSolution[id]));
						break;
					}
					
				}
				
				if(id == -1){
					System.out.print("Integer solution: ");
					tableau.displaySolution();
					tree.bestObjective = tableau.bestObjective;
					tree.bestSolution = tableau.bestSolution;
				}
				else{
					this.generateCut();
					double[] A_left = new double[tableau.n];
					double[] A_right = new double[tableau.n];
					A_left[id] = 1;
					A_right[id] = -1;
					double b_left = Math.floor(tableau.bestSolution[id]);
					double b_right = -Math.ceil(tableau.bestSolution[id]);

					KnapsackBBNode nextNodeLeft = new KnapsackBBNode(this, A_left, b_left,this.p,this.K);
					System.out.print(Utility.genSpace(this.depth+1) + "x[" + (id+1) + "] <= " + Math.floor(tableau.bestSolution[id]) + " : ");
					nextNodeLeft.branch(tree);
					KnapsackBBNode nextNodeRight = new KnapsackBBNode(this,A_right,b_right,this.p,this.K);
					System.out.print(Utility.genSpace(this.depth+1) + "x[" + (id+1) + "] >= " + Math.ceil(tableau.bestSolution[id]) + " : ");
					nextNodeRight.branch(tree);
					
				}
			}
			else{
				System.out.println("Node cut : relaxation worst than bound");
				System.out.println(Utility.genSpace(this.depth) + "(node relaxation : " + Utility.nf.format(this.tableau.bestObjective) + ", best integer solution : " + Utility.nf.format(tree.bestObjective) + ")");
			}
		}
		else{
			System.out.println("Node cut: infeasible solution.");
		}
	}

	/**
	 * Add the cover cut which contain the objects which index are in the list cover
	 * @param cover List of the index of the objects in the cover
	 */
	private void addCoverCutToTableau(List<Integer> cover) {
		
		/* Add the constraints which corresponds to the cover */
		int m = tableau.m + 1;
		int n = tableau.n;

		double[][] newMA = new double[m][];

		for(int cstr = 0; cstr < m - 1; cstr++) {
			newMA[cstr] = new double[n];

			for(int var = 0; var < n; var++)
				newMA[cstr][var] = tableau.A[cstr][var]; 
		}

		newMA[m-1] = new double[n];

		for(int var = 0; var < n; var++)
			if(cover.contains(var))
				newMA[m-1][var] = 1.0;
			else
				newMA[m-1][var] = 0.0;

		double[] newMRhs = new double[m];

		for(int cstr = 0; cstr < m - 1; cstr++)
			newMRhs[cstr] = tableau.b[cstr];

		newMRhs[m - 1] = cover.size() - 1;

		tableau = new Tableau(newMA, newMRhs, tableau.c, tableau.isMinimization);
			
	}
}
