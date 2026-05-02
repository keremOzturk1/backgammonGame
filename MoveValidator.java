import java.util.ArrayList;

public class MoveValidator {

    // Check if a single move is valid
    public boolean isValidMove(Board board, Move move, int diceValue) {
        int from = move.getFrom();
        int to = move.getTo();
        Player player = move.getPlayer();

        // Check destination bounds
        if (to < 0 || to >= 24) {
            return false;
        }

        Point destination = board.getPoint(to);

        // If player has pieces on the bar, only bar entry moves are allowed
        if (player.hasPiecesOnBar()) {
            if (from != -1) {
                return false;
            }

            int expectedEntryPoint;

            if (player.getDirection() == 1) {
                expectedEntryPoint = diceValue - 1;
            } else {
                expectedEntryPoint = 24 - diceValue;
            }

            if (to != expectedEntryPoint) {
                return false;
            }

            return !destination.isBlockedFor(player);
        }

        // Normal moves must start from a valid board point
        if (from < 0 || from >= 24) {
            return false;
        }

        Point source = board.getPoint(from);

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

        if (player.hasPiecesOnBar()) {
            int entryPoint;

            if (player.getDirection() == 1) {
                entryPoint = diceValue - 1;
            } else {
                entryPoint = 24 - diceValue;
            }

            Move barMove = new Move(-1, entryPoint, player);

            if (isValidMove(board, barMove, diceValue)) {
                Point destination = board.getPoint(entryPoint);

                if (destination.canBeHitBy(player)) {
                    barMove.isHit = true;
                }

                validMoves.add(barMove);
            }

            return validMoves;
        }

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