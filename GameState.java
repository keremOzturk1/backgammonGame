public class GameState {

    Board board;
    Player currentPlayer;
    Dice dice;

    public GameState() {
        board = null;
        currentPlayer = null;
        dice = null;
    }

    public GameState(Board board, Player currentPlayer, Dice dice) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.dice = dice;
    }

    public Board getBoard() {
        return board;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Dice getDice() {
        return dice;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setDice(Dice dice) {
        this.dice = dice;
    }
}
