package lp;

import play.NormalFormGame;
import scpsolver.constraints.LinearEqualsConstraint;
import scpsolver.constraints.LinearSmallerThanEqualsConstraint;
import scpsolver.problems.LinearProgram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeneralSum {

    public static void doGeneralSum(NormalFormGame game, boolean once) {

        int counter = 0;


        for (int i = 1; i <= Math.min(game.nRow, game.nCol); i++) {
            System.out.println("Trying " + i + " X " + i );
            boolean found = false;
            List<boolean[]> subset = getSubSets(0, i, game.nRow, game.pRow);

            List<boolean[]> subsetColumns = getSubSets(0, i, game.nCol, game.pCol);

            for (boolean[] rowSubset : subset) {
                for (boolean[] colSubset : subsetColumns) {
                    double[] x = calculateNash(game, i, rowSubset, colSubset);

                    if(x != null){
                        found = true;

                        counter++;

                        System.out.println("RESULT HERE");
                        printStuff(game, x, rowSubset,colSubset );

                        if(once){
                            return;
                        }

                    }

                }
            }
            if(!found)
            System.out.println("False for " + i + " X " + i);

        }
        System.out.println("Found " + counter + " Solutions");
    }

    private static void printStuff(NormalFormGame game, double[] x, boolean[] rowSubset, boolean[] colSubset) {

        int idx = 0;

        System.out.println(Arrays.toString(rowSubset) + " x " +  Arrays.toString(colSubset));

        System.out.println("PLAYER 1");

        for (int i = 0; i < game.nRow; i++) {

            if(rowSubset[i]){
                System.out.println(game.rowActions.get(i) +  " : " +Math.round(x[idx] * 100.0) / 100.0);
                idx++;
            }
            else System.out.println(game.rowActions.get(i) +  " : " + 0);

        }

        System.out.println("PLAYER 2");
        for (int i = 0; i < game.nCol; i++) {

            if(colSubset[i]){
                System.out.println(game.colActions.get(i) +  " : " + Math.round(x[idx] * 100.0) / 100.0);
                idx++;
            }
            else System.out.println(game.colActions.get(i) +  " : " + 0);

        }

    }

    public static  void test(NormalFormGame game){

        boolean[] rowSubset = new boolean[]{true, false, false, false, true, false, false, false, false, false, false, true, false, false, false};

        boolean[] colSubset = new boolean[]{false, false, false, true, false, false, false, false, false, false, true, false, false, false, true};

        double[] x = calculateNash(game, 3, rowSubset,colSubset);

        printStuff(game, x, rowSubset,colSubset );

    }


    public static double[] calculateNash(NormalFormGame game, int subsetSize, boolean[] subsetRows, boolean[] subsetColums) {


        ArrayList<Integer> iRow = new ArrayList<>();
        for (int i = 0; i < game.nRow; i++)
            if (game.pRow[i])
                iRow.add(i);


        ArrayList<Integer> jCol = new ArrayList<>();
        for (int j = 0; j < game.nCol; j++)
            if (game.pCol[j])
                jCol.add(j);


        int numRows = iRow.size();
        int namColumns = jCol.size();

        int nConstraints = numRows + namColumns + 2;
        int nVals = subsetSize * 2 + 2;

        if (numRows == 0 || namColumns == 0)
            return null;

        double[] c = new double[nVals];


        double[] b = new double[nConstraints];

        for (int i = 0; i < nConstraints - 2; i++)
            b[i] = 0.0;

        b[nConstraints - 2] = 1;
        b[nConstraints - 1] = 1;


        double[][] A = new double[nConstraints][nVals];
        double minUtil = 0;

        for (int i = 0; i < numRows; i++) {
            int counter = subsetSize;

            for (int j = 0; j < namColumns; j++) {

                    if (subsetColums[jCol.get(j)]) {
                        double val = game.u1[iRow.get(i)][jCol.get(j)];
                        A[i][counter] = val;
                        counter++;

                        if (minUtil > val)
                            minUtil = val;
                    }
                }
                A[i][nVals - 2] = -1;
            }


        for (int i = 0; i < namColumns; i++) {


            int counter = 0;

                for (int j = 0; j < numRows; j++) {
                    if (subsetRows[iRow.get(j)]) {
                        double val = game.u2[iRow.get(j)][jCol.get(i)];

                        A[i + numRows][counter] = val;

                        counter++;

                        if (minUtil > val)
                            minUtil = val;
                    }


                }

                A[i + numRows][nVals - 1] = -1;
            }


        for (int i = 0; i < subsetSize; i++) {
            A[nConstraints - 2][i] = 1;
            A[nConstraints - 1][subsetSize + i] = 1;

        }

        if (minUtil < 0) {
            System.out.println("LB negativo: " + minUtil);
        }


        double[] lb = new double[nVals];


        lb[nVals - 1] = minUtil;
        lb[nVals - 2] = minUtil;


        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(true);

        int idx = 0;

        for (int i = 0; i < numRows ; i++) {
            if (subsetRows[iRow.get(i)]) {
                lp.addConstraint(new LinearEqualsConstraint(A[i], b[i], "c" + i));
            }
            else
                lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c" + i));

            idx++;

        }

        for (int i = numRows; i < namColumns+numRows; i++) {

            if (subsetColums[jCol.get(i - numRows)])
                lp.addConstraint(new LinearEqualsConstraint(A[i], b[i], "c" + i));
            else
                lp.addConstraint(new LinearSmallerThanEqualsConstraint(A[i], b[i], "c" + i));

            idx++;

        }


        lp.addConstraint(new LinearEqualsConstraint(A[A.length-2], b[b.length-2], "c" +idx));
        idx++;
        lp.addConstraint(new LinearEqualsConstraint(A[A.length-1], b[b.length-1], "c" +idx));



        lp.setLowerbound(lb);

        //LinearProgramming.showLP(lp);

        double[] x = LinearProgramming.solveLP(lp);


        if (x==null)
            return null;

        double[] temp = Arrays.copyOf(x, x.length - 2);

        int counter= 0;
        for (int i = 0; i <temp.length; i++) {


            if (temp[i] > 0.0){
                counter++;
            }
        }


        System.out.println(subsetSize);
        if (counter == subsetSize*2)
            return x;

        return  null;
    }


    public static void main(String[] args) {
        int[][] test1 = new int[3][3];
        test1[0][0] = 0;
        test1[0][1] = 0;
        test1[0][2] = 1;
        test1[1][0] = 0;
        test1[1][1] = 2;
        test1[1][2] = 0;
        test1[2][0] = 4;
        test1[2][1] = 0;
        test1[2][2] = 0;

        int[][] test2 = new int[3][3];
        test2[0][0] = 0;
        test2[0][1] = 0;
        test2[0][2] = 2;
        test2[1][0] = 3;
        test2[1][1] = 0;
        test2[1][2] = 0;
        test2[2][0] = 0;
        test2[2][1] = 2;
        test2[2][2] = 0;


        NormalFormGame test = new NormalFormGame(test1, test2, new String[]{"a", "c", "b"}, new String[]{"C", "B", "A"});

        test.showGame();
        //IteratedDominance.solveDomination(test);


        doGeneralSum(test, true);
    }

    public static List<boolean[]> getSubSets(int j, int support_size, int total_actions, boolean[] p) {
        boolean[] b = new boolean[total_actions];
        List<boolean[]> subset = new ArrayList<>();
        if (support_size == 0) {
            for (int i = 0; i < b.length; i++)
                b[i] = false;

            subset.add(b);

        } else {
            int op = 0;

            for (int i = j; i < p.length; i++)

                if (p[i])
                    op++;

            if (op == support_size) {

                for (int i = 0; i < b.length; i++)
                    if (p[i])
                        b[i] = true;

                subset.add(b);

            } else {
                if (p[j]) {
                    List<boolean[]> s1 = getSubSets(j + 1, support_size - 1, total_actions, p);
                    for (boolean[] booleans : s1) {
                        b = booleans;
                        b[j] = true;
                        subset.add(b);
                    }
                }
                List<boolean[]> s0 = getSubSets(j + 1, support_size, total_actions, p);
                for (boolean[] booleans : s0) {
                    b = booleans;
                    b[j] = false;
                    subset.add(b);
                }
            }
        }
        return subset;
    }

    public static void showSubSet(List<boolean[]> s) {
        int n = s.get(0).length;
        boolean[] b = new boolean[n];
        for (boolean[] booleans : s) {
            b = booleans;
            System.out.print("{");
            for (int j = 0; j < n; j++)
                if (b[j]) System.out.print(" " + 1);
                else System.out.print(" " + 0);
            System.out.println(" }");
        }
    }

}
