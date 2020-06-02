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
        probP2 = new double[nPlaysP1];

        round = 0;
    }

    public void nextRound(){

    }


}
