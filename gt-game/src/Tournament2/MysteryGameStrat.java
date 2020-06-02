package Tournament2;

import gametree.GameNode;
import gametree.GameNodeDoesNotExistException;
import gametree.GameTree;
import lp.IteratedDominance;
import lp.ZeroSum;
import play.NormalFormGame;
import play.PlayStrategy;
import play.Strategy;
import play.exception.InvalidStrategyException;

import java.util.Arrays;
import java.util.Iterator;

public class MysteryGameStrat extends Strategy {

    FictitionalPlay frequencies;


    @Override
    public void execute() throws InterruptedException {
        while (!this.isTreeKnown()) {
            System.err.println("Waiting for game tree to become available.");
            Thread.sleep(1000);
        }
        while (true) {
            PlayStrategy myStrategy = this.getStrategyRequest();
            if (myStrategy == null) //Game was terminated by an outside event
                break;
            boolean playComplete = false;

            GameNode fatherP1 = null;
            GameNode finalP2 = null;
            while (!playComplete) {
                System.out.println("*******************************************************");
                if (myStrategy.getFinalP1Node() != -1) {
                    GameNode finalP1 = this.tree.getNodeByIndex(myStrategy.getFinalP1Node());
                    fatherP1 = null;
                    if (finalP1 != null) {
                        try {
                            fatherP1 = finalP1.getAncestor();
                        } catch (GameNodeDoesNotExistException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.print("Last round as P1: " + showLabel(fatherP1.getLabel()) + "|" + showLabel(finalP1.getLabel()));
                        System.out.println(" -> (Me) " + finalP1.getPayoffP1() + " : (Opp) " + finalP1.getPayoffP2());
                    }
                }
                if (myStrategy.getFinalP2Node() != -1) {
                    finalP2 = this.tree.getNodeByIndex(myStrategy.getFinalP2Node());
                    GameNode fatherP2 = null;
                    if (finalP2 != null) {
                        try {
                            fatherP2 = finalP2.getAncestor();
                        } catch (GameNodeDoesNotExistException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.print("Last round as P2: " + showLabel(fatherP2.getLabel()) + "|" + showLabel(finalP2.getLabel()));
                        System.out.println(" -> (Opp) " + finalP2.getPayoffP1() + " : (Me) " + finalP2.getPayoffP2());
                    }
                }
                // Normal Form Games only!
                GameNode rootNode = tree.getRootNode();
                int n1 = rootNode.numberOfChildren();
                int n2 = rootNode.getChildren().next().numberOfChildren();
                String[] labelsP1 = new String[n1];
                String[] labelsP2 = new String[n2];
                int[][] U1 = new int[n1][n2];
                int[][] U2 = new int[n1][n2];
                Iterator<GameNode> childrenNodes1 = rootNode.getChildren();
                GameNode childNode1;
                GameNode childNode2;
                int i = 0;
                int j = 0;
                while (childrenNodes1.hasNext()) {
                    childNode1 = childrenNodes1.next();
                    labelsP1[i] = childNode1.getLabel();
                    j = 0;
                    Iterator<GameNode> childrenNodes2 = childNode1.getChildren();
                    while (childrenNodes2.hasNext()) {
                        childNode2 = childrenNodes2.next();
                        if (i == 0) labelsP2[j] = childNode2.getLabel();
                        U1[i][j] = childNode2.getPayoffP1();
                        U2[i][j] = childNode2.getPayoffP2();
                        j++;
                    }
                    i++;
                }
                showActions(1, labelsP1);
                showActions(2, labelsP2);
                showUtility(1, U1);
                showUtility(2, U2);


                NormalFormGame game = new NormalFormGame(U1, U2, labelsP1, labelsP2);

                IteratedDominance.solveDomination(game);

                double[][] d;

                double[] strategyP1 = new double[labelsP1.length];
                double[] strategyP2 = new double[labelsP2.length];


                if (checkZeroSum(U1, U2)) {


                    int[][] tempU1 = new int[n1][n2];
                    int[][] tempU2 = new int[n1][n2];

                    for (int k = 0; k < n1; k++) {
                        for (int l = 0; l < n2; l++) {

                            tempU2[k][l] = -U2[k][l];

                        }
                    }
                    for (int k = 0; k < n1; k++) {
                        for (int l = 0; l < n2; l++) {

                            tempU1[k][l] = -U1[k][l];

                        }
                    }


                    NormalFormGame game1 = new NormalFormGame(tempU2, U2, labelsP1, labelsP2);
                    d = ZeroSum.doZeroSum(game1);
                    strategyP1 = d[0];


                    NormalFormGame game2 = new NormalFormGame(U1, tempU1, labelsP1, labelsP2);
                    game2.showGame();
                    d = ZeroSum.doZeroSum(game2);

                    strategyP2 = d[1];

                    for (int z = 0; z < strategyP1.length; z++) myStrategy.put(labelsP1[z], strategyP1[z]);
                    for (int z = 0; z < strategyP2.length; z++) myStrategy.put(labelsP2[z], strategyP2[z]);


                } else {
                    if (frequencies == null) {
                        frequencies = new FictitionalPlay(labelsP1.length, labelsP2.length);
                    }

                    if (myStrategy.isFirstRound()) {

                        int bestStrategyIndexP1 = 0;
                        int bestStrategyIndexP2 = 0;
                        int media;
                        int currentBest;

                        currentBest = Integer.MIN_VALUE;
                        for (int k = 0; k < labelsP1.length; k++) {

                            media = 0;
                            for (int l = 0; l < labelsP2.length; l++) {

                                media += U1[k][l];

                            }

                            media /= labelsP2.length;

                            if (media > currentBest) {
                                bestStrategyIndexP1 = k;
                                currentBest = media;
                            }
                        }

                        currentBest = Integer.MIN_VALUE;
                        for (int k = 0; k < U2.length; k++) {

                            media = 0;

                            for (int l = 0; l < U2[k].length; l++) {

                                media += U2[l][k];

                            }

                            media /= U2[k].length;

                            if (media > currentBest) {
                                bestStrategyIndexP2 = k;
                                currentBest = media;
                            }
                        }

                        strategyP1[bestStrategyIndexP1] = 1;
                        strategyP2[bestStrategyIndexP2] = 1;

                        for (int z = 0; z < strategyP1.length; z++) myStrategy.put(labelsP1[z], strategyP1[z]);
                        for (int z = 0; z < strategyP2.length; z++) myStrategy.put(labelsP2[z], strategyP2[z]);

                    } else {

                        
                        int p1Idx = -1;
                        int p2Idx = -1;

                        for (int k = 0; k < labelsP1.length; k++) {
                            if(fatherP1.getLabel().equals(labelsP1[k]))
                                p1Idx = k;
                        }

                        for (int k = 0; k < labelsP2.length; k++) {
                            if(finalP2.getLabel().equals(labelsP2[k]))
                                p2Idx = k;
                        }
                        
                        frequencies.nextRound(p1Idx, p2Idx);
                        
                        double[][] strategies = game.bestResponses(frequencies.getProbP1(), frequencies.getProbP2());


                        System.out.println("PRINTS");
                        System.out.println(Arrays.toString(strategies[0]));
                        System.out.println(Arrays.toString(strategies[1]));
                        for (int k = 0; k < labelsP1.length; k++) myStrategy.put(labelsP1[k], strategies[0][k]);
                        for (int k = 0; k < labelsP2.length; k++) myStrategy.put(labelsP2[k], strategies[1][k]);

                        showStrategy(1, strategies[0], labelsP1);
                        showStrategy(2, strategies[1], labelsP2);


                    }
                }

                try {
                    this.provideStrategy(myStrategy);
                    playComplete = true;
                } catch (InvalidStrategyException e) {
                    System.err.println("Invalid strategy: " + e.getMessage());

                    e.printStackTrace(System.err);
                }
            }
        }
    }



    private boolean checkZeroSum(int[][] u1, int[][] u2) {
        for (int i = 0; i < u1.length; i++) {
            for (int j = 0; j < u1[i].length; j++) {
                if(u1[i][j] != - u2[i][j]){
                    return false;
                }
            }
        }
        return true;
    }

    public String showLabel(String label) {
        return label.substring(label.lastIndexOf(':') + 1);
    }

    public void showActions(int P, String[] labels) {
        System.out.println("Actions Player " + P + ":");
        for (int i = 0; i < labels.length; i++) System.out.println("   " + showLabel(labels[i]));
    }

    public void showUtility(int P, int[][] M) {
        int nLin = M.length;
        int nCol = M[0].length;
        System.out.println("Utility Player " + P + ":");
        for (int i = 0; i < nLin; i++) {
            for (int j = 0; j < nCol; j++) System.out.print("| " + M[i][j] + " ");
            System.out.println("|");
        }
    }

    public double[] setStrategy(int P, String[] labels, PlayStrategy myStrategy, GameTree t) {
        int n = labels.length;
        double[] strategy = new double[n];

        double beta = myStrategy.probabilityForNextIteration();

        int defectionGain = 1;

        int roundsLeft = myStrategy.getMaximumNumberOfIterations();

        double defectionLost = (2 * beta) / (1- beta);

        System.out.println(roundsLeft);
        System.out.println(defectionLost < defectionGain);



        for (int i = 0; i < n; i++) strategy[i] = 0;
        if (P == 1) {

            if ( myStrategy.getLastRoundOpponentScoreAsP1(t) == 4
                    || myStrategy.getLastRoundOpponentScoreAsP1(t) == 1

                    ||myStrategy.getLastRoundOpponentScoreAsP2(t) == 4
                    || myStrategy.getLastRoundOpponentScoreAsP2(t) == 1

                    || defectionLost < defectionGain
                    || (!myStrategy.isFirstRound() && (myStrategy.getLastRoundOpponentScoreAsP1(t) == 0
                    || myStrategy.getLastRoundOpponentScoreAsP2(t) == 0))
            || roundsLeft <= 1)
            {
                strategy[0] = 0;
                strategy[1] = 1;

            } else {
                strategy[0] = 1;
                strategy[1] = 0;
            }

        } else {
            if (myStrategy.getLastRoundOpponentScoreAsP2(t) == 4
                    || myStrategy.getLastRoundOpponentScoreAsP2(t) == 1

                    || myStrategy.getLastRoundOpponentScoreAsP1(t) == 4
                    || myStrategy.getLastRoundOpponentScoreAsP1(t) == 1
                    || defectionLost < defectionGain
                    || (!myStrategy.isFirstRound() && (myStrategy.getLastRoundOpponentScoreAsP1(t) == 0
                    || myStrategy.getLastRoundOpponentScoreAsP2(t) == 0))
                        || roundsLeft <= 1)

            {
                strategy[0] = 0;
                strategy[1] = 1;

            } else {
                strategy[0] = 1;
                strategy[1] = 0;
            }
        }

        for (int i = 0; i < n; i++) myStrategy.put(labels[i], strategy[i]);
        return strategy;
    }


    public void showStrategy(int P, double[] strategy, String[] labels) {
        System.out.println("Strategy Player " + P + ":");
        for (int i = 0; i < labels.length; i++) System.out.println("   " + strategy[i] + ":" + showLabel(labels[i]));
    }

}

