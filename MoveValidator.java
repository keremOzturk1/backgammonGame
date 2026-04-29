import java.util.ArrayList;

public class MoveValidator {

    // Check if a single move is valid
    public boolean isValidMove(Board board, Move move, int diceValue) {
        int from = move.getFrom();
        int to = move.getTo();
        Player player = move.getPlayer();

        // Check board bounds
        if (from < 0 || from >= 24 || to < 0 || to >= 24) {
            return false;
        }

        Point source = board.getPoint(from);
        Point destination = board.getPoint(to);

        // Source point must belong to the player
        if (source.isEmpty() || !source.isOwnedBy(player)) {
            return false;
        }

        // Destination point cannot be blocked by opponent
        if (destination.isBlockedFor(player)) {
            return false;
        }

        // Move distance must match dice value
        int expectedTo = from + (diceValue * player.getDirection());
        if (to != expectedTo) {
            return false;
        }

        return true;
    }

    // Generate all valid moves for one dice value
    public ArrayList<Move> getValidMovesForDice(Board board, Player player, int diceValue) {
        ArrayList<Move> validMoves = new ArrayList<>();

        for (int from = 0; from < 24; from++) {
            int to = from + (diceValue * player.getDirection());

            Move move = new Move(from, to, player);

            if (isValidMove(board, move, diceValue)) {
                Point destination = board.getPoint(to);

                if (destination.canBeHitBy(player)) {
                    move.isHit = true;
                }

                validMoves.add(move);
            }
        }

        return validMoves;
    }

    // Generate all valid moves for all dice values
    public ArrayList<Move> getAllValidMoves(Board board, Player player, Dice dice) {
        ArrayList<Move> allMoves = new ArrayList<>();
        int[] moves = dice.getMoves();

        for (int i = 0; i < moves.length; i++) {
            ArrayList<Move> validMovesForDice = getValidMovesForDice(board, player, moves[i]);
            allMoves.addAll(validMovesForDice);
        }

        return allMoves;
    }
}
