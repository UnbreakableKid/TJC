package lp;

import play.NormalFormGame;
import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.problems.LinearProgram;

import java.util.ArrayList;

public class IteratedDominance {

    public static void solveDomination(NormalFormGame game) {
        int i = 0;
        int j = 0;

        boolean changedRow = true;
        boolean changedCol = true;

        do{

            if(i < game.pRow.length) {
                while (i < game.pRow.length && !game.pRow[i])
                    i++;

                if (dominatedRow(i, game)) {
                    System.out.println("Row " + i + " is dominated!");
                    game.pRow[i] = false;
                    changedRow = true;
                    i = 0;
                    j = 0;
                } else {
                    changedRow = false;
                    i++;
                }
            }
            game.showGame();

            if(j < game.pCol.length) {
                while (j < game.pCol.length && !game.pCol[j])
                    j++;

                if (dominatedColumn(j, game)) {
                    System.out.println("Column " + j + " is dominated!");
                    game.pCol[j] = false;
                    changedCol = true;
                    i = 0;
                    j = 0;
                } else {
                    changedCol = false;
                    j++;
                }
            }
            game.showGame();

        } while (i < (game.pRow.length) || j < (game.pCol.length) || changedRow || changedCol);
    }


    public static boolean dominatedColumn(int jDom, NormalFormGame game){
        ArrayList<Integer> iRow = new ArrayList<>();
        for (int i = 0; i < game.nRow; i++)
            if(game.pRow[i])
                iRow.add(i);


        ArrayList<Integer> jCol = new ArrayList<>();
        for (int j = 0; j < game.nCol; j++)
            if(game.pCol[j] && j!=jDom)
                jCol.add(j);


        int n1 = iRow.size();
        int n2 = jCol.size();

        double[] c = new double[n2];

        for(int j = 0; j <n2; j++)
            c[j] = 1.0;


        double[] b = new double[n1];

        for (int i = 0; i < n1; i++)
            b[i] = game.u2[iRow.get(i)][jDom];


        double[][] A = new double[n1][n2];
        double minUtil = 0;

        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                A[i][j] = game.u2[iRow.get(i)][jCol.get(j)];
                if(A[i][j] < minUtil)
                    minUtil = A[i][j];
            }
        }

        double[] lb = new double[n2];
        for (int j = 0; j < n2; j++)
            lb[j] = 0.0;


        if(minUtil < 0){
            for (int i = 0; i < n1; i++) {
                b[i] = b[i] - minUtil;
                for (int j = 0; j < n2; j++)
                    A[i][j] = A[i][j] - minUtil;

            }
        }

        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);

        for (int i = 0; i < n1; i++)
            lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[i], b[i], "c"+i));

        lp.setLowerbound(lb);

        LinearProgramming.showLP(lp);

        double[] x;

        x = LinearProgramming.solveLP(lp);

        if(x != null)
            return lp.evaluate(x) < 1.0;

        return false;
    }

    //as seen in class by prof
    public static boolean dominatedRow(int iDom, NormalFormGame game){
        ArrayList<Integer> iRow = new ArrayList<>();
        for (int i = 0; i < game.nRow; i++)
            if(game.pRow[i] && i != iDom)
                iRow.add(i);


        ArrayList<Integer> jCol = new ArrayList<>();
        for (int j = 0; j < game.nCol; j++)
            if(game.pCol[j])
                jCol.add(j);


        int nRows = iRow.size();
        int nCols = jCol.size();

        if(nRows == 0 || nCols == 0)
            return false;

        // set P terms to one
        double[] c = new double[nRows];
        for (int i = 0; i < nRows; i++)
            c[i] = 1.0;


        // set constraints independent term to
        // utilities of row to dominate
        double[] b = new double[nCols];
        for (int j = 0; j < nCols; j++)
            b[j] = game.u1[iDom][jCol.get(j)];


        // constraints matrix
        double[][] A = new double[nCols][nRows];
        double minUtil = 0;

        // add utilites to X's
        for (int j = 0; j < nCols; j++) {
            for (int i = 0; i < nRows; i++) {
                A[j][i] = game.u1[iRow.get(i)][jCol.get(j)];
                if(A[j][i] < minUtil){
                    minUtil = A[j][i];
                }
            }
        }

        // Set lower bounds
        double[] lb = new double[nRows];
        for (int i = 0; i < nRows; i++)
            lb[i] = 0.0;


        if(minUtil < 0)
            for (int j = 0; j < nCols; j++) {
                b[j] = b[j] - minUtil;
                for (int i = 0; i < nRows; i++)
                    A[j][i] = A[j][i] - minUtil;

            }


        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);
        for (int j = 0; j < nCols; j++)
            lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[j], b[j], "c"+j));

        lp.setLowerbound(lb);
        double[] x;

        x = LinearProgramming.solveLP(lp);
        if(x != null)
            return lp.evaluate(x) < 1.0;


        //else LinearProgramming.showSolution(x, lp);
        return false;
    }

}
