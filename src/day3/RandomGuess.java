package day3;

import java.util.Random;

import static day3.GuessGame.getRandomIntBetween;

public class RandomGuess extends GuessMethod{

    Random RNG = new Random();

    @Override
    public int guess(int min, int max, boolean isSmaller) {
        return min + this.RNG.nextInt(max - min + 1);
    }
}
