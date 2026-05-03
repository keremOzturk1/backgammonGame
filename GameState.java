import java.util.ArrayList;

public class GameState {

    private int[] pointCounts;
    private int[] pointOwners; // 0 = empty, 1 = player1, 2 = player2

    private int player1BarCount;
    private int player2BarCount;
    private int player1BorneOff;
    private int player2BorneOff;

    private ArrayList<Integer> remainingDiceMoves;
    private boolean canSwitchDiceOrder;
    private boolean isDoubleTurn;

    public GameState() {
        pointCounts = new int[24];
        pointOwners = new int[24];
        remainingDiceMoves = new ArrayList<>();
        canSwitchDiceOrder = false;
        isDoubleTurn = false;
    }

    public GameState(Game game, ArrayList<Integer> remainingDiceMoves,
                     boolean canSwitchDiceOrder, boolean isDoubleTurn) {
        pointCounts = new int[24];
        pointOwners = new int[24];

        for (int i = 0; i < 24; i++) {
            Point point = game.board.getPoint(i);
            pointCounts[i] = point.count;

            if (point.owner == game.player1) {
                pointOwners[i] = 1;
            } else if (point.owner == game.player2) {
                pointOwners[i] = 2;
            } else {
                pointOwners[i] = 0;
            }
        }

        player1BarCount = game.player1.getBarCount();
        player2BarCount = game.player2.getBarCount();
        player1BorneOff = game.player1.getBorneOff();
        player2BorneOff = game.player2.getBorneOff();

        this.remainingDiceMoves = new ArrayList<>();
        for (int i = 0; i < remainingDiceMoves.size(); i++) {
            this.remainingDiceMoves.add(remainingDiceMoves.get(i));
        }

        this.canSwitchDiceOrder = canSwitchDiceOrder;
        this.isDoubleTurn = isDoubleTurn;
    }

    public void restore(Game game) {
        for (int i = 0; i < 24; i++) {
            Point point = game.board.getPoint(i);
            point.count = pointCounts[i];

            if (pointOwners[i] == 1) {
                point.owner = game.player1;
            } else if (pointOwners[i] == 2) {
                point.owner = game.player2;
            } else {
                point.owner = null;
            }
        }

        game.player1.setBarCount(player1BarCount);
        game.player2.setBarCount(player2BarCount);
        game.player1.setBorneOff(player1BorneOff);
        game.player2.setBorneOff(player2BorneOff);
    }

    public ArrayList<Integer> getRemainingDiceMovesCopy() {
        ArrayList<Integer> copy = new ArrayList<>();

        for (int i = 0; i < remainingDiceMoves.size(); i++) {
            copy.add(remainingDiceMoves.get(i));
        }

        return copy;
    }

    public boolean getCanSwitchDiceOrder() {
        return canSwitchDiceOrder;
    }

    public boolean getIsDoubleTurn() {
        return isDoubleTurn;
    }
}