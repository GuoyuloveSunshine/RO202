public class Test {
    public static void ex1(){
        double[][] A = new double[][]{
            {-2, 2},
            { 2, 3},
            { 9, -2},
        };
        double[] b = new double[] {7, 18, 36};
        double[] c = new double[] {3, 2};
        boolean isMinimisation = false;

        BBNode root = new BBNode(A, b, c,isMinimisation);
        BBTree tree = new BBTree(root);
        tree.solve();
        tree.displaySolution();
    }
    public static void ex2(){
        double[][] A = new double[][] {
			{3, 7, 9, 6},
			{1, 0, 0, 0},
			{0, 1, 0, 0},
			{0, 0, 1, 0},
			{0, 0, 0, 1}
		};
		double[] rhs = new double[] {17, 1, 1, 1, 1};
		double[] obj = new double[] {8, 18, 20, 11};
		BBNode root = new BBNode(A, rhs, obj, false);
		BBTree tree = new BBTree(root);
		tree.solve();
		tree.displaySolution();
    }
    public static void main(String[] args){
        // ex1();
        ex2();
    }
}
