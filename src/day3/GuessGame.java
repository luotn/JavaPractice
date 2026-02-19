package day3;

import java.util.Random;

public class GuessGame implements Runnable{

    private final int GAMEINDEX;
    private String RESULT = "-----------\n";
    private final int START;
    private final int END;
    public int GUESSCOUNT;
    private static final Random RNG = new Random();
    private int ANSWER;
    private GuessMethod METHOD;

    public GuessGame(int index, int start, int end, Methods method) {
        this.GAMEINDEX = index;
        this.START = start;
        this.END = end;
        this.ANSWER = this.getRandomIntBetween(this.START, this.END);
        switch (method) {
            case RANDOM ->
                this.METHOD = new RandomGuess();
            case BINARY ->
                this.METHOD = new BinarySearch();
            case null, default ->
                throw new RuntimeException("Guess method '" + method + "' incorrect!");
        }
    }

    @Override
    public void run() {
        this.RESULT += "This is game #" + this.GAMEINDEX + "\n";
        this.RESULT += "[RNG God]: I'm thinking about a number between " + this.START + " and " + this.END + "\n";
        int min = this.START;
        int max = this.END;
        int lastGuess = -1;
        boolean lastGuessWasSmallerThanAnswer = false;
        while (lastGuess != this.ANSWER) {
            if (lastGuess != -1) {
                this.RESULT += "[RNG God]: WRONG! You last guess was ";
                if (this.ANSWER - lastGuess < 0) {
                    this.RESULT += "bigger ";
                    max = lastGuess;
                    lastGuessWasSmallerThanAnswer = false;
                } else {
                    this.RESULT += "smaller ";
                    min = lastGuess;
                    lastGuessWasSmallerThanAnswer = true;
                }
                this.RESULT += "than the number in my mind.\n";
            }
            lastGuess = this.METHOD.guess(min, max, lastGuessWasSmallerThanAnswer);
            this.GUESSCOUNT++;
            this.RESULT += "[NEO]: I choose " + lastGuess + "!\n";
        }
        this.RESULT += "[RNG God]: " + (this.GUESSCOUNT > 1 ?
                "Pathetic! How dare you call yourself the chosen one using " + this.GUESSCOUNT + " tries?!\n" +
                        "Now DIE!!!": "Good, but DIE ANYWAY!!!");
    }

    @Override
    public String toString() {
        return this.RESULT;
    }

    public static int getRandomIntBetween(int min, int max) {
        return min + RNG.nextInt(max - min + 1);
    }
}
