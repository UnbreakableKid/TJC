package Quiz4;

import com.sun.deploy.util.ArrayUtil;
import lp.LinearProgramming;
import scpsolver.constraints.LinearBiggerThanEqualsConstraint;
import scpsolver.constraints.LinearEqualsConstraint;
import scpsolver.problems.LinearProgram;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class GraphsGame {
    public double[] v;
    public int nPlayers;
    public String[] ids;

    public int[][] coalitions;
    public double[] shapleys;

    public double[][] adjTable;

    public double[]playerVals;

    public GraphsGame(double[] v) {
        this.v = v;
        this.nPlayers = (int) (Math.log(v.length) / Math.log(2));
        setPlayersID();
    }

    public GraphsGame(String filename) throws FileNotFoundException {

        File file;

        file = new File(filename);


        Scanner valuesFileReader = new Scanner(file);

        int nLines = 0;

        while (valuesFileReader.hasNextLine()) {

            valuesFileReader.nextLine();
            nLines++;

        }

        this.nPlayers = (nLines+1);
        setPlayersID();


        this.shapleys = new double[nPlayers];

        this.coalitions = new int[(int) Math.pow(2, nPlayers)][nPlayers];

        int currentLine = 0;

        valuesFileReader = new Scanner(file);

        this.v = new double[coalitions.length];

        double[]players = new double[nPlayers];


        this.adjTable = new double[nPlayers][nPlayers];
        while (currentLine < nLines) {

            String lineRead = valuesFileReader.nextLine();

            String[]bla = lineRead.split(" ");

            for (int i = 0; i <bla.length ; i++) {

                adjTable[currentLine+1][i] = Double.parseDouble(bla[i]);
                adjTable[i][currentLine+1] = Double.parseDouble(bla[i]);
            }

            currentLine++;

        }

        this.playerVals = players;

    }


    public void setPlayersID() {
        int c = 64;
        ids = new String[nPlayers];
        for (int i = nPlayers - 1; i >= 0; i--) {
            c++;
            ids[i] = (String.valueOf((char) c));
        }
    }

    public void showGame() {
        System.out.println("*********** Coalitional Game ***********");
        for (int i = 0; i < v.length; i++) {
            showSet(i);
            System.out.println(" (" + v[i] + ")");
        }
    }

    public void showSet(long v) {
        boolean showPlayerID = true;
//        boolean showPlayerID = false;

        int power;
        System.out.print("{");
        int cnt = 0;
        for (int i = 0; i < nPlayers; i++) {
            power = nPlayers - (i + 1);

            if (showPlayerID) {

                if (inSet(i, v)) {

                    if (cnt > 0)
                        System.out.print(",");
                    cnt++;
                    System.out.print(ids[power]);
                }
            } else {
                if (cnt > 0)
                    System.out.print(",");

                cnt++;
                if (inSet(i, v))
                    System.out.print(1);
                else
                    System.out.print(0);
            }
        }
        System.out.print("}");
    }

    public boolean inSet(int i, long v) {
        int power;
        long vi;
        long div;
        long mod;
        power = nPlayers - (i + 1);
        vi = (long) Math.pow(2, power);
        div = v / vi;
        mod = div % 2;
        return (mod == 1);
    }

//    public ArrayList<Integer> getSet(long v) {
//        ArrayList<Integer> players = new ArrayList<>();
//        int power;
//        long vi;
//        long div;
//        long mod;
//        for (int i = 0; i < nPlayers; i++) {
//            power = nPlayers - (i + 1);
//            vi = (long) Math.pow(2, power);
//            div = v / vi;
//            mod = div % 2;
//            if (mod == 1)
//                players.add(power);
//        }
//        return players;
//    }
//
//    public int permutation(int j, int size, int currentOut, long v0) {
//
//        int x = 0;
//        long value = 0;
//        if (size == 0) {
//            //showSet(v0);
//            x++;
//
//        } else {
//            int op = 0;
//            if (currentOut < j)
//                op = nPlayers - j;
//
//            else
//                op = nPlayers - j - 1;
//
//            if (op == size) {
//                for (int i = j; i < nPlayers; i++) {
//                    if (i != currentOut)
//                        value += (long) Math.pow(2, nPlayers - (i + 1));
//                }
//                v0 = v0 + value;
//                //showSet(v0);
//                x++;
//
//
//            } else {
//                if (j != currentOut)
//                    x = x + permutation(j + 1, size - 1, currentOut, v0 + (long) Math.pow(2, nPlayers - (j + 1)));
//
//                x = x + permutation(j + 1, size, currentOut, v0);
//            }
//        }
//
//        return x;
//    }
//
//    private ArrayList<Integer> doPermutation(int howManyFound, int personToIgnore, int howManyToDo) {
//        ArrayList<String[]> keepr = new ArrayList<>();
//
//        int counter = 0;
//        int k = 0;
//
//        while (counter < howManyFound) {
//
//            String bla = Integer.toBinaryString(k);
//
//            StringBuilder sb = new StringBuilder();
//            for (int x = 0; x < nPlayers; x++) {
//                sb.append(0);
//            }
//            bla = sb.substring(bla.length()) + bla;
//
//            sb = new StringBuilder(bla);
//
//            sb.setCharAt(personToIgnore, '0');
//
//
//            long count = sb.chars().filter(ch -> ch == '1').count();
//            if (count == howManyToDo && !keepr.contains(sb.toString())) {
//                keepr.add(sb.toString().split(""));
//                counter++;
//            }
//            k++;
//        }
//
//
//        for (String[] s : keepr) {
//            coalitions[personToIgnore]
//        }
//        return tmp;
//    }


    public int howManyOnes(int[] list){
        int sum = 0;
        for (int value : list) {
            if (value == 1)
                sum++;
        }

        return sum;
    }

    public long doFactorial(int number) {

        if (number == 0) {
            return 1;
        }
        return number * doFactorial(number - 1);
    }

    public double doShapley(int personToIgnore) {

        double sum = 0;

        long numPlayFact = doFactorial(nPlayers);

        for (int i = 0; i < v.length; i++) {

            if(!inSet(personToIgnore, i)) {
                int numberOfOnes = howManyOnes(coalitions[i]);

                long firstPart = doFactorial(numberOfOnes);

                long secondPart = doFactorial(nPlayers - numberOfOnes - 1);

                int j = i + (int) Math.pow(2, nPlayers - personToIgnore - 1);

                double thirdPart = v[j] - v[i];

                sum += firstPart * secondPart * thirdPart;
            }
        }

        sum /= numPlayFact;

        return sum;

    }

    public void doShapleyForEveryone() {

        for (int i = 0; i < nPlayers; i++) {
            shapleys[i] = Math.round(doShapley(i) * 100.0 )/100.0;
        }

        for (int i = 0; i < shapleys.length ; i++) {

            System.out.println(ids[i] + " : " + shapleys[shapleys.length - i - 1]);

        }

    }
    private void computeColitionalVals(int index) {

        int sum = 0;
        for (int i = 0; i < coalitions[index].length ; i++) {

            if(coalitions[index][i] == 1) {

                for (int j = i+1; j <  coalitions[index].length ; j++) {
                    if (coalitions[index][j] == 1){
                        sum += adjTable[i][j];
                    }
                }
            }
        }

        v[index] = sum;
    }

    public void checkInCore(){

        for (int i = 0; i <v.length; i++) {
            double sum = 0;

            for (int j = 0; j <nPlayers; j++) {

                if( coalitions[i][j] == 1)

                    sum+= shapleys[j];


            }

            if(sum < v[i]){

                System.out.println("NOT IN CORE!!!!!!!!!!!!!!");
                checkEmptyCore();
                return;
            }
        }

        System.out.println("IN CORE!!!!!!!!!!!!!!");

    }

    public void checkEmptyCore(){

        double[] c = new double[nPlayers];

        double[] b = new double[v.length];

        System.arraycopy(v, 0, b, 0, v.length);


        double[][] A = new double[b.length][c.length];

        for (int i = 0; i < b.length; i++) {
            for (int j = c.length-1; j >= 0; j--) {
                A[i][c.length-1-j] = coalitions[i][j];
            }
        }

        double[] lb = new double[nPlayers];


        LinearProgram lp = new LinearProgram(c);
        lp.setMinProblem(false);

        for (int i = 0; i < A.length-1; i++)
            lp.addConstraint(new LinearBiggerThanEqualsConstraint(A[i], b[i], "c"+i));


        lp.addConstraint(new LinearEqualsConstraint(A[A.length-1], b[b.length-1], "c"+(A.length-1)));
        lp.setLowerbound(lb);

//        LinearProgramming.showLP(lp);

        double[] x = LinearProgramming.solveLP(lp);

        if(x != null) {
            System.out.println("CORE ISN'T EMPTY");

            for (int i = 0; i < x.length; i++) {
                System.out.println( ids[i] + " : " + x[i]);
            }
        }
        else
            System.out.println("Core is empty");
    }


    public static void main(String[] args) throws FileNotFoundException {


//        String filename = "Prob3.txt";
//        String filename = "C7.txt";
//        String filename = "C8.txt";
        String filename = "C9.txt";

        GraphsGame c = new GraphsGame(filename);
        //c.showGame();

        for (int i = 0; i < c.coalitions.length; i++) {
            String tempCoalition;
            tempCoalition = Integer.toBinaryString(i);

            String[] split = tempCoalition.split("");

            for (int j = split.length-1, k = c.nPlayers-1; j >= 0; j--, k--) {
                c.coalitions[i][k] = Integer.parseInt(split[j]);
            }

            c.computeColitionalVals(i);

        }


        c.doShapleyForEveryone();
        c.checkInCore();

    }


}



