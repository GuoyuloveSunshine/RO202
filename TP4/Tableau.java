
public class Tableau {

	/** Number of variables */
	public int n;

	/** Number of constraints */
	public int m;

	public double[][] A;
	public double[] b;
	public double[] c;

	/** Current base; null if none has currently been defined */
	public int[] basis;

	/** Vector of size n which contains the best solution currently found (or null if none has been currently found) */
	public double[] bestSolution;

	/** Best value of the objective currently found */
	public double bestObjective;

	/** True if the tableau corresponds to an optimal solution */
	public boolean isMinimization;

	/** True if the tableau should be printed at each iteration of the algorithm */
	public static final boolean DISPLAY_SIMPLEX_LOGS = false;

	/**
	 * Create a tableau (the input array are duplicated)
	 * @param A
	 * @param b
	 * @param c
	 * @param isMinimization
	 */
	public Tableau(double[][] A, double[] b, double[] c, boolean isMinimization) {

		n = c.length;
		m = A.length;

		this.A = Utility.copyArray(A);
		this.b = Utility.copyArray(b);
		this.c = Utility.copyArray(c);
		this.isMinimization = isMinimization;

		basis = null;
		bestSolution = null;
		bestObjective = 0.0;
	}

	public static Tableau ex1() {

		double[][] A = new double[][] {
			{1, -1},
			{0, 1},
			{8, 5}
		};

		double[] c = new double[]{2, 1};
		double[] b = new double[]{4, 8, 56};

		return new Tableau(A, b, c, false); 
	}

	public static Tableau ex2() {

		double[][] A = new double[][] {
			{1, -2, 1, -1, 0, 0},
			{0, 1, 3, 0, 1, 0}, 
			{2, 0, 1, 2, 0, 1}
		};

		double[] c = new double[]{2, -3, 5, 0, 0, 0};
		double[] b = new double[]{4, 6, 7};

		return new Tableau(A, b, c, true); 
	}

	public static void main(String[] args) {

		/* Si le problème n'est pas sous forme normale, il faut le transformer */
		boolean normalForm = false;
		System.out.println("test");

		/* Si on résout un problème sous forme normale */
		if(normalForm) {

			/**** 1st case - PL Ax = b and a basis is provided (no slack variable added to the problem) */
			Tableau t1 = ex2();
			t1.basis = new int[] {3, 4, 5};
			t1.applySimplex();
		}
		
		/* Si on résout un problème qui n'est pas sous forme normale */
		else {

			/**** 2nd case - PL Ax <= b, add slack variable and use them as a basis */
			Tableau t2 = ex1();
			t2.addSlackAndSolve();
			t2.displaySolution();
		}
	}

	/**
	 * Create a new tableau with 1 slack variable for each constraint and solve
	 * @param t
	 */
	public void addSlackAndSolve() {

		/* Create a new tableau in which one slack variable is added for each constraint, 
		 * and select the slack variables as a base */
		Tableau tSlack = tableauWithSlack();

		/* Apply the simplex algorithm on the tableau with stack */
		tSlack.applySimplex();

		/* Get the solution obtained stack tableau and put it in t */
		setSolution(tSlack);

	}

	/**
	 * Apply the simplex algorithm
	 */
	public void applySimplex() {

		/* Display the initial array */
		if(DISPLAY_SIMPLEX_LOGS) {
			System.out.println("Initial array: ");
			display();
		}
		
		/* Perturbate each value of b to avoid dividing by zero whenever there are degenerations */
		double eps = 1E-7;
		
		for(int i = 0; i < m; i++) {
			b[i] += eps;
			eps *= 0.1;
		}

		/* While the basic solution can be improved, perform a pivot step */
		while(pivot()) {
			if(DISPLAY_SIMPLEX_LOGS)
				display();
		}

		/* Display the final array */
		if(DISPLAY_SIMPLEX_LOGS) {
			System.out.println("Final array");
			display();
		}

	}

	/**
	 * Perform a pivot. A basis must have been selected.
	 * @return True if a new basis has been found; false if an optimal solution is reached.
	 */
	public boolean pivot() {

		/* 1) Canonical form 
		 *   Set the tableau in canonical form (so that the basis matrix has 1 on its diagonal and 0 everywhere else)
		 *
		 * Description des variables à utiliser :
		 * - A[][] : matrice des contraintes (taille m * n)
		 * - b[] : coefficients du membre de droite (taille m)
		 * - c[] : coefficients de l'objectif (taille n)
		 * - bestObjective : valeur de l'objectif de la solution courante
		 * - basis[] : indice des variables dans la base courante (taille m)
		 *     - basis[  0] : indice de la première variable dans la base ;
		 *     - basis[m-1] : indice de la dernière variable de la base.
		 *
		 * Pseudo-code:
		 *
		 * l1 - Pour chaque contrainte i (i.e., pour chaque ligne i de A)
		 *       l2 - Utiliser une combinaison linéaire de la contrainte i pour fixer à 1 le coefficient en ligne i et en colonne basis[i]
		 *       l3 - Utiliser une combinaison linéaire de la contrainte i et des autres contraintes pour fixer les autres coefficients de la colonne basis[i] à 0
		 *       l4 - Utiliser une combinaison linéaire de la contrainte i et de c pour fixer c[basis[i]] à 0
		 *
		 * Remarques :
		 * - dans l2 et l3, ne pas oublier de mettre à jour b
		 * - dans l4, ne pas oublier de mettre à jour bestObjective
		 *  		    
		 */

		/* For each variable in the base (remark: the variable at index baseId of the base is associated with constraint baseId) */
		for(int baseId = 0; baseId < m; baseId++) {

			/* Get the coefficient in the column of its corresponding base variable */
			double baseCoefficient = A[baseId][basis[baseId]];

			/* For each coefficient of this constraint */
			for(int c = 0; c < n; c++)
				
				/* Divide by the coefficient (enable to get 1 in the column of its corresponding base variable) */
				A[baseId][c] /= baseCoefficient;
			
			/* Also divide the RHS */
			b[baseId] /= baseCoefficient;

			/* For each constraint (i.e., each row of the column basis[baseId]) */
			for(int constraintId = 0; constraintId < m; constraintId++) {

				/* If coefficient [constraintId][baseId] must be set to 0 */
				if(constraintId != baseId) {

					double coefficient = A[constraintId][basis[baseId]];

					/* For each coefficient of this constraint */
					for(int colId = 0; colId < n; colId++) 

						A[constraintId][colId] -= coefficient * A[baseId][colId];

					b[constraintId] -= coefficient * b[baseId];

				} 
			}

			double objectiveCoefficient = c[basis[baseId]];

			/* For each coefficient of the objective */
			for(int colId = 0; colId < n; colId++) 

				c[colId] -= objectiveCoefficient * A[baseId][colId];

			bestObjective += objectiveCoefficient * b[baseId];
		}
		
		if(DISPLAY_SIMPLEX_LOGS) {
			System.out.println("Tableau in canonical form");
			display();
		}

		/* 2 - Get the new base */

		/* 2.1 - Get the variable entering the base 
		 * 
		 * Indication : Trouver la variable ayant le meilleur coût réduit
		 * 
		 * Remarque : 
		 *   - Le traitement n'est pas le même si le problème est une maximisation ou une minimisation (utiliser la variable isMinimization)
		 *   - Comme les calculs machine sont approchés, il faut toujours faire des comparaisons numériques à un epsilon prêt. Par exemple :
		 *   	- si vous voulez tester si a est supérieur à 1, il faut écrire : a > 1 + epsilon (sinon la condition serait vérifiée pour a = 1.00000000001) ;
		 *   	- si vous voulez tester si a est inférieur à 1, il faut écrire : a < 1 - epsilon (sinon la condition serait vérifiée pour a = 0.99999999999).
		 *   
		 */
		int bestEnteringVar = -1;
		double bestReducedCost = 0.0;

		for(int i = 0; i < n; i++)
			if((!isMinimization && c[i] > 1E-6 &&  c[i] > bestReducedCost) 
					|| (isMinimization && c[i] < -1E-6 && c[i] < bestReducedCost )) {
				bestEnteringVar = i;
				bestReducedCost = c[i];
			}

		/* 2.2 - Get the variable leaving the base 
		 * 
		 * Pseudo-code
		 * 	Soit e l'indice de la variable entrant en base (trouvée en 2.1).
		 * 	l1 - Déterminer la contrainte i ayant un coefficient positif ou nul en colonne e qui minimise le ratio b[i] / A[i][e]
		 *      l2 - Mettre à jour la base
		 * 
		 * Remarque : il faut une nouvelle fois faire des comparaisons à epsilon prêt.
		 */
		if(bestEnteringVar != -1) {

			if(DISPLAY_SIMPLEX_LOGS)
				System.out.println("x" + (bestEnteringVar+1) + " enters the base.");
			
			

			int bestLeavingVar = -1;
			double bestRatio = Double.POSITIVE_INFINITY;

			for(int constraintId = 0; constraintId < m; constraintId++) {
				double ratio = b[constraintId] / A[constraintId][bestEnteringVar];

				/* If the ratio is positive and lower than the best known ratio
				 * or if the ratio is null and if the x coefficient is positive or null
				 * (in that last case, the variable cannot be increased)
				 */
				if(ratio > 1E-6 && ratio < bestRatio
						|| Math.abs(ratio) < 1E-6 && A[constraintId][bestEnteringVar] > -1E-6) {
					bestRatio = ratio;
					bestLeavingVar = basis[constraintId];
				}
			}

			if(DISPLAY_SIMPLEX_LOGS)
				System.out.println("x" + (bestLeavingVar+1) + " leaves the base.");

			for(int i = 0; i < m; i++)
				if(basis[i] == bestLeavingVar)
					basis[i] = bestEnteringVar;

		}			

		/* 3 - Retourner vrai si une nouvelle base est trouvée et faux sinon */
		return bestEnteringVar != -1;

	}


	/**
	 * Get the solution of the tableau
	 */
	public void getSolution() {

		bestSolution = new double[n];

		/* For each basic variable, get its value */
		for(int varBase = 0; varBase < m; varBase++) {
			int varId = basis[varBase];
			bestSolution[varId] = b[varBase];
		}
	}

	/**
	 * Set the solution of the current Tableau to the solution of another tableau
	 * (typically, set the solution to the one of the tableau with slack variables after applying the simplex algorithm).
	 * @param tSlack Tableau which contains the solution
	 */
	public void setSolution(Tableau tSlack) {

		/* Get the solution of the target tableau */
		tSlack.getSolution();

		bestSolution = new double[n];

		for(int varId = 0; varId < n; varId++)
			bestSolution[varId] = tSlack.bestSolution[varId];

		bestObjective = tSlack.bestObjective;
	}

	/** Display the current solution */
	public void displaySolution() {

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


	/**
	 * Create a tableau with one slack variable for each constraint and set the corresponding base
	 * (the base contains the slack variables) 
	 * @return A tableau with n+m variables (the n original + m slack variables)
	 */
	public Tableau tableauWithSlack() {

		double[][] ASlack = new double[m][];

		/* For each constraint */
		for(int cstr = 0; cstr < m; cstr++) {

			/* Increase the number of variables, and set the coefficients */
			ASlack[cstr] = new double[n + m];

			for(int col = 0; col < n; col++) {
				ASlack[cstr][col] = A[cstr][col];
			}

			ASlack[cstr][n + cstr] = 1.0;
		}

		/* Increase the number of variables in the objective */
		double[] cSlack = new double[n + m];

		for(int i = 0; i < n; i++)
			cSlack[i] = c[i];

		/* Create a basis with the slack variables */
		int[] basis = new int[m];

		for(int i = 0; i < m; i++)
			basis[i] = i + n;

		Tableau slackTableau = new Tableau(ASlack, b, cSlack, isMinimization);
		slackTableau.basis = basis;

		return slackTableau;
	}

	/** Display the tableau */
	public void display() {

		System.out.print("\nVar.\t");

		for(int i = 0; i < n; i++) {
			System.out.print("x" + (i+1) + "\t");
		}

		String dottedLine = "";
		for(int i = 0; i < n+2; i++)
			dottedLine += "--------";

		dottedLine += "\n";

		System.out.print("  (RHS)\t\n" + dottedLine);

		for(int l = 0; l < m; l++) {

			System.out.print("(C" + (l+1) + ")\t");

			for(int c = 0; c < n; c++) {
				System.out.print(Utility.nf.format(A[l][c]) + "\t");
			}
			System.out.print("| " + Utility.nf.format(b[l]) + "\n");
		}

		System.out.print(dottedLine);
		System.out.print("(Obj)\t");

		for(int i = 0; i < n; i++) {
			System.out.print(Utility.nf.format(c[i]) + "\t");
		}

		System.out.print("|  " + Utility.nf.format(bestObjective) + "\n");

		/* If a solution has been computed */
		if(basis != null) {
			System.out.print(dottedLine);
			getSolution();
			displaySolution();
		}

		System.out.println();

	}

	/**
	 * Create the tableau used for phase 1 of the simplex algorithm and create the corresponding basis
	 * @return A tableau with one additional variable for each negative RHS and objective coefficients which correspond to simplex phase 1)
	 */
	public Tableau tableauPhase1(int negativeRHSCount) {

		Tableau tSlack = tableauWithSlack();

		double[] cPhase1 = new double[tSlack.n + negativeRHSCount];
		double[][] APhase1 = new double[m][];

		int negativeId = 0;

		/* For each constraint */
		for(int i = 0; i < m; i ++) {

			APhase1[i] = new double[tSlack.n + negativeRHSCount];

			for(int j = 0; j < tSlack.n; j++)
				APhase1[i][j] = tSlack.A[i][j];

			/* If the RHS is negative, add a slack variable */
			if(b[i] < -1E-6) {
				APhase1[i][tSlack.n + negativeId] = -1.0;
				cPhase1[tSlack.n + negativeId] = -1;
				negativeId++;
			}
		}

		/* Create the new tableau */
		Tableau sPhase1 = new Tableau(APhase1, b, cPhase1, false);

		/* Set the basis */
		negativeId = 0;

		sPhase1.basis = new int[m];

		for(int i = 0; i < m; i++)
			if(b[i] < -1E-6) {
				sPhase1.basis[i] = tSlack.n + negativeId;
				negativeId++;
			}
			else
				sPhase1.basis[i] = i + n;

		return sPhase1;
	}


	/**
	 * Apply the simplex algorithm phase 1 and 2
	 */
	public void applySimplexPhase1And2() {

		Tableau tSlack = tableauWithSlack();

		/* Count the number of negative RHS */
		int negativeRHS = 0;

		for(int i = 0; i < m; i++)
			if(b[i] < -1E-6)
				negativeRHS++;

		boolean isInfeasible = false;

		/* If the 0 vector is not a solution of the simplex */
		if(negativeRHS > 0) {

			Tableau tPhase1 = tableauPhase1(negativeRHS);

			if(DISPLAY_SIMPLEX_LOGS) {
				System.out.println("\nInitial array: ");
				tPhase1.display();
			}

			while(tPhase1.pivot()) {}

			if(DISPLAY_SIMPLEX_LOGS) {
				System.out.println("Final array");
				tPhase1.display();
			}

			/* If no feasible solution for the original problem is found */
			if(tPhase1.bestObjective < -1E-6)
				isInfeasible = true;

			/* If a feasible solution is found */
			else {

				/* Update matrix x and rhs of the slack tableau */
				for(int cstr = 0; cstr < tSlack.m; cstr++) {

					tSlack.b[cstr] = tPhase1.b[cstr];

					for(int var = 0; var < tSlack.n; var++)
						tSlack.A[cstr][var] = tPhase1.A[cstr][var];
				}

				/* Update the base of the slack tableau */
				tSlack.basis = tPhase1.basis;

				/* Test if all the variables in the base are variables in tSlack (and not variables added in tPhase1) */
				for(int i = 0; i < tSlack.m; i++) {

					/* If base[i] is not in tSlack */
					if(tSlack.basis[i] >= tSlack.n) {

						/* Search a variable which can go into the base instead of base[i] */
						int var = 0;
						boolean found = false;

						/* While such a variable as not been found */
						while(!found && var < tSlack.n) {

							/* If variable var has a non zero coefficient in the corresponding constraint */
							if(Math.abs(tSlack.A[i][var]) > 1E-6) {

								/* If variable var is not already in the base */
								found = true;

								for(int j = 0; j < m; j++)
									if(tSlack.basis[i] == var)
										found = false;

								if(found) {
									tSlack.basis[i] = var;
								}
							}

							var++;
						}
					}
				}
			}
		}

		if(!isInfeasible) {

			if(DISPLAY_SIMPLEX_LOGS) {
				System.out.print("Base: ");

				for(int i = 0; i < tSlack.m; i++)
					System.out.print((tSlack.basis[i]+1) + ", ");
				System.out.println();
			}

			tSlack.applySimplex();
			setSolution(tSlack);
		}
	}


}
