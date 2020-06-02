package Tournament2;

public class FictitionalPlay {

    int[] freqP1, freqP2;
    double[] probP1, probP2;
    int round;

    public double[] getProbP1() {
        return probP1;
    }

    public double[] getProbP2() {
        return probP2;
    }

    public FictitionalPlay(int nPlaysP1, int nPlaysP2) {
        freqP1 = new int[nPlaysP1];
        freqP2 = new int[nPlaysP2];

        probP1 = new double[nPlaysP1];
        probP2 = new double[nPlaysP2];

        round = 0;
    }

    public void nextRound(int i, int j){

        round++;
        freqP1[i]++;
        freqP2[j]++;

        for (int k = 0; k < probP1.length; k++) {

        probP1[k] = freqP1[k]/(double)round;
        }

        for (int k = 0; k < probP2.length; k++) {

            probP2[k] = freqP2[k]/(double)round;
        }


    }


}
