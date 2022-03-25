public class BBTree {
    public double[] bestSolution;
    public Double bestObjective;
    public BBNode root;

    public BBTree(BBNode aNode){
        this.root = aNode;
        this.bestObjective = 0.0;
		this.bestSolution = null;
    }
    public void solve(){
        this.root.branch(this);
    }
    public void displaySolution(){
        System.out.print("z = " + Utility.nf.format(bestObjective) + ", ");

		String variables = "(";
		String values = "(";
		for(int i = 0; i < bestSolution.length; i++) 
			if(bestSolution[i] != 0.0) {
				variables += "x" + (i+1) + ", ";

				if(Utility.isFractional(bestSolution[i]))
					values += Utility.nf.format(bestSolution[i]) + ", ";
				else
					values += (int)bestSolution[i] + ", ";
			}

		variables = variables.substring(0, Math.max(0, variables.length() - 2));
		values = values.substring(0, Math.max(0, values.length() - 2));
		System.out.println(variables + ") = " + values + ")");
    }
}
