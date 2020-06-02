package lp;

import play.NormalFormGame;
import scpsolver.constraints.LinearEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.problems.LinearProgram;

import java.util.ArrayList;
import java.util.Arrays;

public class ZeroSum {

    public static double[][] doZeroSum(NormalFormGame game){

        double[] temp1;
        double[] temp2;
        double[] strategy1;
        double[] strategy2;

        System.out.println("Doing p1");
        temp1 = columns(game);
        System.out.println("Doing p2");
        temp2 = rows(game);


        assert temp1 != null;
        strategy1 =  Arrays.copyOf(temp1, temp1.length-1);

        assert temp2 != null;
        strategy2 = Arrays.copyOf(temp2, temp2.length-1);

        return new double[][]{strategy1, strategy2};
    }

    public static double[] rows(NormalFormGame game){
        ArrayList<Integer> iRow = new ArrayList<>();
        for (int i = 0; i < game.nRow; i++)
            if(game.pRow[i])
                iRow.add(i);


        ArrayList<Integer> jCol = new ArrayList<>();
        for (int j = 0; j < game.nCol; j++)
            if(game.pCol[j])
                jCol.add(j);


        int numRows = iRow.size();
        int namColumns = jCol.size();

        if(numRows == 0 || namColumns == 0)
            return null;

        double[] c = new double[namColumns + 1];

        for(int j = 0; j <namColumns; j++)
            c[j] = 0;

        c[namColumns] = 1;
        

        double[] b = new double[numRows+1];

        for (int i = 0; i < numRows; i++)
            b[i] = 0.0;

        b[numRows] = 1;


        double[][] A = new double[numRows+1][namColumns+1];
        double minUtil = 0;

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < namColumns; j++) {
                A[i][j] = game.u1[iRow.get(i)][jCol.get(j)];
                A[numRows][j] = 1;
                if(A[i][j] < minUtil)
                    minUtil = A[i][j];

            A[i][namColumns] = -1;
            }

        }


        double[] lb = new double[namColumns + 1];
        for (int j = 0; j < namColumns + 1; j++)
            lb[j] = 0;
        lb[namColumns] = minUtil;

        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);

        for (int i = 0; i < numRows; i++)
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c"+i));

        lp.addConstraint(new LinearEqualsConstraint(A[numRows], b[numRows], "c"+numRows));
        lp.setLowerbound(lb);

        double[] x = LinearProgramming.solveLP(lp);

        lp.evaluate(x);

        return x;

    }

    public static double[] columns(NormalFormGame game){
        ArrayList<Integer> iRow = new ArrayList<>();
        for (int i = 0; i < game.nRow; i++)
            if(game.pRow[i])
                iRow.add(i);


        ArrayList<Integer> jCol = new ArrayList<>();
        for (int j = 0; j < game.nCol; j++)
            if(game.pCol[j])
                jCol.add(j);


        int numRows = iRow.size();
        int namColumns = jCol.size();

        if(numRows == 0 || namColumns == 0)
            return null;

        double[] c = new double[numRows + 1];

        for(int j = 0; j <numRows; j++)
            c[j] = 0;

        c[numRows] = 1;


        double[] b = new double[namColumns+1];

        for (int i = 0; i < namColumns; i++)
            b[i] = 0.0;

        b[namColumns] = 1;


        double[][] A = new double[namColumns+1][numRows+1];
        double minUtil = 0;

        for (int j = 0; j < namColumns; j++) {
            for (int i = 0; i < numRows; i++) {
                 A[j][i] = game.u2[iRow.get(i)][jCol.get(j)];
                 A[namColumns][i] = 1;
                    if(A[j][i] < minUtil)
                    minUtil = A[j][i];

            A[j][numRows] = -1;
            }

        }

        double[] lb = new double[numRows + 1];
        for (int j = 0; j < numRows + 1; j++)
            lb[j] = 0;

        lb[numRows] = minUtil;

        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);

        for (int i = 0; i < namColumns; i++)
            lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c"+i));

        lp.addConstraint(new LinearEqualsConstraint(A[namColumns], b[namColumns], "c"+namColumns));
        lp.setLowerbound(lb);

        double[] x = LinearProgramming.solveLP(lp);

        System.out.println("here");
        System.out.println(Arrays.toString(x));

        return x;
    }



    public static void main(String[] args) {
        int[][] test1 = new int[2][3];
        test1[0][0] = 30;
        test1[0][1] = -10;
        test1[0][2] = 20;
        test1[1][0] = -10;
        test1[1][1] = 20;
        test1[1][2] = -20;

        int[][] test2 = new int[2][3];
        test2[0][0] = -30;
        test2[0][1] = 10;
        test2[0][2] = -20;
        test2[1][0] = 10;
        test2[1][1] = -20;
        test2[1][2] = 20;


        NormalFormGame test = new NormalFormGame(test1, test2, new String[]{"T", "B"}, new String[]{"L", "M", "R"} );

        test.showGame();
        doZeroSum(test);
    }
}
