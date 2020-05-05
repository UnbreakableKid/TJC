package lp;

import play.NormalFormGame;
import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.problems.LinearProgram;

import java.util.ArrayList;

public class IteratedDominance {

    public static void solveDomination(NormalFormGame game) {
        int i = 0;
        int j = 0;

        boolean changed = true;

        do{

            while (i < game.pRow.length && !game.pRow[i])
                i++;

            while (j < game.pCol.length && !game.pCol[j])
                j++;

            if(j < game.pCol.length) {

                if (dominatedColumn(j, game)) {
                    game.pCol[j] = false;
                    changed = true;
                    i = 0;
                    j = 0;
                } else {
                    changed = false;
                    j++;
                }
            }

            if(i < game.pRow.length) {

                if (dominatedRow(i, game)) {
                    game.pRow[i] = false;
                    changed = true;
                    i = 0;
                    j = 0;
                } else {
                    changed = false;
                    i++;
                }
            }

        } while (i < (game.pRow.length) || j < (game.pCol.length) || changed);

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


        int numRows = iRow.size();
        int namColumns = jCol.size();

        if(numRows == 0 || namColumns == 0)
            return false;

        double[] c = new double[namColumns];

        for(int j = 0; j <namColumns; j++)
            c[j] = 1.0;


        double[] b = new double[numRows];

        for (int i = 0; i < numRows; i++)
            b[i] = game.u2[iRow.get(i)][jDom];


        double[][] A = new double[numRows][namColumns];
        double minUtil = 0;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < namColumns; j++) {
                A[i][j] = game.u2[iRow.get(i)][jCol.get(j)];
                if(A[i][j] < minUtil)
                    minUtil = A[i][j];
            }
        }

        double[] lb = new double[namColumns];
        for (int j = 0; j < namColumns; j++)
            lb[j] = 0.0;


        if(minUtil < 0){
            for (int i = 0; i < numRows; i++) {
                b[i] = b[i] - minUtil;
                for (int j = 0; j < namColumns; j++)
                    A[i][j] = A[i][j] - minUtil;

            }
        }

        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);

        for (int i = 0; i < numRows; i++)
            lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[i], b[i], "c"+i));

        lp.setLowerbound(lb);


        double[] x = LinearProgramming.solveLP(lp);

        if(x != null)
            return (Math.round(lp.evaluate(x) * 100.0) / 100.0) < 1.0;

        return false;
    }

    //as seen in class by prof
    public static boolean dominatedRow(int iDom, NormalFormGame game){
        //region add active rows and columns
        ArrayList<Integer> iRow = new ArrayList<>();
        for (int i = 0; i < game.nRow; i++) {
            if(game.pRow[i] && i != iDom)
                iRow.add(i);
        }

        ArrayList<Integer> jCol = new ArrayList<>();
        for (int j = 0; j < game.nCol; j++) {
            if(game.pCol[j])
                jCol.add(j);
        }
        //endregion

        int nRows = iRow.size();
        int nCols = jCol.size();

        if(nRows == 0 || nCols == 0)
            return false;

        // set P terms to one
        double[] c = new double[nRows];
        for (int i = 0; i < nRows; i++) {
            c[i] = 1.0;
        }

        // set constraints independent term to
        // utilities of row to dominate
        double[] b = new double[nCols];
        for (int j = 0; j < nCols; j++) {
            b[j] = game.u1[iDom][jCol.get(j)];
        }

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
        for (int i = 0; i < nRows; i++) {
            lb[i] = 0.0;
        }

        if(minUtil < 0){
            for (int j = 0; j < nCols; j++) {
                b[j] = b[j] - minUtil;
                for (int i = 0; i < nRows; i++) {
                    A[j][i] = A[j][i] - minUtil;
                }
            }
        }

        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);
        for (int j = 0; j < nCols; j++) {
            lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[j], b[j], "c"+j));
        }
        lp.setLowerbound(lb);

        double[] x = LinearProgramming.solveLP(lp);

        if(x != null){
            return (Math.round(lp.evaluate(x) * 100.0) / 100.0) < 1.0;
        }

        return false;
    }

}
