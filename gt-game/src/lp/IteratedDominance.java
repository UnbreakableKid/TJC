package lp;

import play.NormalFormGame;
import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.problems.LinearProgram;

import java.util.ArrayList;

public class IteratedDominance {

    public static void iteratedSolve(NormalFormGame game){

    }

    public static boolean dominatedColumn(int jDom, NormalFormGame game){
        ArrayList<Integer> iRow = new ArrayList<>();
        for (int i = 0; i < game.nRow; i++) {
            if(game.pRow[i])
                iRow.add(i);
        }

        ArrayList<Integer> jCol = new ArrayList<>();
        for (int j = 0; j < game.nCol; j++) {
            if(game.pCol[j] && j!=jDom)
                jCol.add(j);
        }

        int n1 = iRow.size();
        int n2 = jCol.size();

        double[] c = new double[n2];
        for(int j = 0; j <n2; j++){
            c[j] = 1.0;
        }

        double[] b = new double[n1];
        for (int i = 0; i < n1; i++) {
            b[i] = game.u2[iRow.get(i)][jDom];
        }

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
        for (int j = 0; j < n2; j++) {
            lb[j] = 0.0;
        }
        if(minUtil < 0){
            System.out.println("Negative utilities detected.\n" +
                    "Offsetting utilites so all are positive.");
            for (int i = 0; i < n1; i++) {
                b[i] = b[i] - minUtil;
                for (int j = 0; j < n2; j++) {
                    A[i][j] = A[i][j] - minUtil;
                }
            }
        }

        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);
        for (int i = 0; i < n1; i++) {
            lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[i], b[i], "c"+i));
        }
        lp.setLowerbound(lb);
        LinearProgramming.showLP(lp);
        double[] x = new double[c.length];
        x = LinearProgramming.solveLP(lp);
        if(x != null){
            LinearProgramming.showSolution(x, lp);
            if(lp.evaluate(x)<1.0) return true;
        }
        else LinearProgramming.showSolution(x, lp);
        return false;
    }

    public static boolean dominatedRow(int iDom, NormalFormGame game){
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

        int n2 = iRow.size();
        int n1 = jCol.size();

        double[] c = new double[n2];
        for(int j = 0; j <n2; j++){
            c[j] = 1.0;
        }

        double[] b = new double[n1];
        for (int i = 0; i < n1; i++) {
            b[i] = game.u1[iDom][jCol.get(i)];
        }

        double[][] A = new double[n1][n2];
        double minUtil = 0;

        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                A[i][j] = game.u1[iRow.get(i)][jCol.get(j)];
                if(A[i][j] < minUtil)
                    minUtil = A[i][j];
            }
        }

        double[] lb = new double[n1];
        for (int j = 0; j < n1; j++) {
            lb[j] = 0.0;
        }
        if(minUtil < 0){
            System.out.println("Negative utilities detected.\n" +
                    "Offsetting utilites so all are positive.");
            for (int i = 0; i < n1; i++) {
                b[i] = b[i] - minUtil;
                for (int j = 0; j < n2; j++) {
                    A[i][j] = A[i][j] - minUtil;
                }
            }
        }

        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);
        for (int i = 0; i < n1; i++) {
            lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[i], b[i], "c"+i));
        }
        lp.setLowerbound(lb);
        LinearProgramming.showLP(lp);
        double[] x = new double[c.length];
        x = LinearProgramming.solveLP(lp);
        if(x != null){
            LinearProgramming.showSolution(x, lp);
            if(lp.evaluate(x)<1.0) return true;
        }
        else LinearProgramming.showSolution(x, lp);
        return false;
    }

}
