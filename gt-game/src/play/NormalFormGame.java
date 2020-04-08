package play;

import java.util.ArrayList;
import java.util.List;

public class NormalFormGame {
    public List<String> rowActions; 	// actions of player 1
    public List<String> colActions; 	// actions of player 2
    public int nRow;					// number of actions of player 1
    public int nCol;					// number of actions of player 2
    public boolean[] pRow;				// if pRow[i]==false than action i of player 1 is not considered
    public boolean[] pCol;				// if pCol[j]==false than action j of player 2 is not considered
    public double[][] u1;				// utility matrix of player 1
    public double[][] u2;				// utility matrix of player 2
    public double[] strategy2;
    public double[] strategy1;


    public NormalFormGame() {
    }

    public NormalFormGame(int[][] M1, int[][] M2, String[] labelsP1, String[] labelsP2) {
        /*
         * Constructor of a NormalFormGame with data obtained from the API
         */
        nRow = labelsP1.length;
        rowActions = new ArrayList<String>();
        pRow = new boolean[nRow];
        for (int i = 0; i<nRow; i++) {
            rowActions.add(labelsP1[i].substring(labelsP1[i].lastIndexOf(':')+1));
            pRow[i] = true;
        }
        nCol = labelsP2.length;
        colActions = new ArrayList<String>();
        pCol = new boolean[nCol];
        for (int j = 0; j<nCol; j++) {
            colActions.add(labelsP2[j].substring(labelsP2[j].lastIndexOf(':')+1));
            pCol[j] = true;
        }
        u1 = new double[nRow][nCol];
        u2 = new double[nRow][nCol];
        for (int i = 0; i<nRow; i++) {
            for (int j = 0; j<nCol; j++) {
                u1[i][j] = M1[i][j];
                u2[i][j] = M2[i][j];
            }
        }
    }

    public void showGame() {
        /*
         * Prints the game in matrix form. The names of the actions are shortened to the first letter
         */
        System.out.print("****");
        for (int j = 0; j<nCol; j++)  if (pCol[j])
            System.out.print("***********");
        System.out.println();
        System.out.print("  ");
        for (int j = 0; j<nCol; j++)  if (pCol[j]) {
            if (colActions.size()>0) {
                System.out.print("      ");
                System.out.print(colActions.get(j).substring(0,1));
                System.out.print("    ");
            }
            else {
                System.out.print("\t");
                System.out.print("Col " +j);
            }
        }
        System.out.println();
        for (int i = 0; i<nRow; i++) if (pRow[i]) {
            if (rowActions.size()>0) System.out.print(rowActions.get(i).substring(0,1)+ ": ");
            else System.out.print("Row " +i+ ": ");
            for (int j = 0; j<nRow; j++)  if (pCol[j]) {
                String fs = String.format("| %3.0f,%3.0f", u1[i][j], u2[i][j]);
                System.out.print(fs+"  ");
            }
            System.out.println("|");
        }
        System.out.print("****");
        for (int j = 0; j<nCol; j++)  if (pCol[j])
            System.out.print("***********");
        System.out.println();
    }

    public boolean doNash2x2(){
        ArrayList<Integer> iRow = new ArrayList<>();
        for (int i = 0; i < nRow; i++) {
            if(pRow[i])
                iRow.add(i);
        }
        ArrayList<Integer> iCol = new ArrayList<>();
        for (int i = 0; i < nCol; i++) {
            if(pCol[i])
                iCol.add(i);
        }
        int n1 = iRow.size();
        int n2 = iCol.size();
        if((n1 != 2) || (n2 != 2)) return  false;

        int r0 = iRow.get(0);
        int r1 = iRow.get(1);
        int c0 = iCol.get(0);
        int c1 = iCol.get(1);

        if((u1[r0][c0] + u1[r1][c1] - u1[r0][c1] - u1[r1][c0]) == 0.0)
            return false;
        double p = (u1[r1][c1] - u1[r0][c1])/ (u1[r0][c0] + u1[r1][c1] - u1[r0][c1] - u1[r1][c0]);
        System.out.println("p = "  + p);
        for (int i = 0; i < nCol; i++) {
            strategy2[i] = 0.0;
        }
        strategy2[c0] = p;
        strategy2[c1] = 1-p;

        System.out.println("Strategy 2 " );
        for (int i = 0; i < strategy2.length ; i++) {

            System.out.println(" " + strategy2[i]);
            
        }
        System.out.println();

        if((u2[r0][c0]+u2[r1][c1] - u2[r0][c1] - u2[r1][c0]) == 0.0)
            return false;
        double q = (u2[r1][c1] - u2[r1][c0])/ (u2[r0][c0]+u2[r1][c1] - u2[r0][c1] - u2[r1][c0]);
        System.out.println("q = " +   q);

        for (int i = 0; i <nRow ; i++) {

            strategy1[r0] = q;
            strategy1[r1] = 1-q;
            
        }
        System.out.println("Strategy 1 " );
        for (int i = 0; i < strategy1.length ; i++) {

            System.out.println(" " + strategy1[i]);

        }

        return  true;
    }
    

}
