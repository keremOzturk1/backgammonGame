import java.util.Random;

public class Dice {

    int d1;
    int d2;
    Random random;

    // Constructor
    public Dice() {
        random = new Random();
    }

    // Roll the dice
    public void roll() {
        d1 = random.nextInt(6) + 1;
        d2 = random.nextInt(6) + 1;
    }

    // Check if dice are equal
    public boolean isDouble() {
        return d1 == d2;
    }

    // Get first die
    public int getFirstDie() {
        return d1;
    }

    // Get second die
    public int getSecondDie() {
        return d2;
    }

    // Return moves as array
    public int[] getMoves() {
        if (isDouble()) {
            return new int[]{d1, d1, d1, d1};
        }
        return new int[]{d1, d2};
    }
}

