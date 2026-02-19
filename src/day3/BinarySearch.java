package day3;

public class BinarySearch extends GuessMethod {
    @Override
    public int guess(int min, int max, boolean isSmaller) {
        if(min == max) return min;
        else if (Math.abs(min - max) > 1) return (int) (Math.round(1.0 * (min - 1 + max) / 2));
        else return isSmaller ? max: min;
    }
}
