public class MoveOption {

    private Move move;
    private int diceValue;
    private int secondDiceValue;
    private boolean combinedMove;

    public MoveOption(Move move, int diceValue) {
        this.move = move;
        this.diceValue = diceValue;
        this.secondDiceValue = 0;
        this.combinedMove = false;
    }

    public MoveOption(Move move, int diceValue, int secondDiceValue) {
        this.move = move;
        this.diceValue = diceValue;
        this.secondDiceValue = secondDiceValue;
        this.combinedMove = true;
    }

    public Move getMove() {
        return move;
    }

    public int getDiceValue() {
        return diceValue;
    }

    public int getSecondDiceValue() {
        return secondDiceValue;
    }

    public boolean isCombinedMove() {
        return combinedMove;
    }

    public int getTotalDiceValue() {
        if (combinedMove) {
            return diceValue + secondDiceValue;
        }

        return diceValue;
    }

    public boolean usesSameSourceAs(MoveOption other) {
        return move.getFrom() == other.getMove().getFrom();
    }

    public boolean hasSameTargetAs(MoveOption other) {
        return move.getTo() == other.getMove().getTo() && move.isBearOff() == other.getMove().isBearOff();
    }
}