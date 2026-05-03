import java.util.ArrayList;

public class MoveValidator {

    // Check if a single move is valid
    public boolean isValidMove(Board board, Move move, int diceValue) {
        int from = move.getFrom();
        int to = move.getTo();
        Player player = move.getPlayer();

        // If player has pieces on the bar, only bar entry moves are allowed
        if (player.hasPiecesOnBar()) {
            if (from != -1) {
                return false;
            }

            if (to < 0 || to >= 24) {
                return false;
            }

            Point destination = board.getPoint(to);
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

        // Normal and bear-off moves must start from a valid board point
        if (from < 0 || from >= 24) {
            return false;
        }

        Point source = board.getPoint(from);

        // Source point must belong to the player
        if (source.isEmpty() || !source.isOwnedBy(player)) {
            return false;
        }

        int expectedTo = from + (diceValue * player.getDirection());
        if (to != expectedTo) {
            return false;
        }

        if (isBearOffTarget(player, to)) {
            return isValidBearOff(board, player, from, to);
        }

        // Normal move destination must stay on the board
        if (to < 0 || to >= 24) {
            return false;
        }

        Point destination = board.getPoint(to);

        // Destination point cannot be blocked by opponent
        if (destination.isBlockedFor(player)) {
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

            if (isBearOffTarget(player, to)) {
                move.isBearOff = true;
            }

            if (isValidMove(board, move, diceValue)) {
                if (!move.isBearOff()) {
                    Point destination = board.getPoint(to);

                    if (destination.canBeHitBy(player)) {
                        move.isHit = true;
                    }
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

    private boolean isBearOffTarget(Player player, int to) {
        if (player.getDirection() == 1) {
            return to >= 24;
        }

        return to < 0;
    }

    private boolean isValidBearOff(Board board, Player player, int from, int to) {
        if (!allPiecesInHomeBoard(board, player)) {
            return false;
        }

        if (player.getDirection() == 1) {
            if (to == 24) {
                return true;
            }

            return !hasPlayerPieceBehind(board, player, from);
        }

        if (to == -1) {
            return true;
        }

        return !hasPlayerPieceBehind(board, player, from);
    }

    private boolean allPiecesInHomeBoard(Board board, Player player) {
        if (player.hasPiecesOnBar()) {
            return false;
        }

        for (int i = 0; i < 24; i++) {
            Point point = board.getPoint(i);

            if (point.isOwnedBy(player) && !isHomeBoardIndex(player, i)) {
                return false;
            }
        }

        return true;
    }

    private boolean isHomeBoardIndex(Player player, int index) {
        if (player.getDirection() == 1) {
            return index >= 18 && index <= 23;
        }

        return index >= 0 && index <= 5;
    }

    private boolean hasPlayerPieceBehind(Board board, Player player, int from) {
        if (player.getDirection() == 1) {
            for (int i = 18; i < from; i++) {
                Point point = board.getPoint(i);

                if (point.isOwnedBy(player)) {
                    return true;
                }
            }
        } else {
            for (int i = from + 1; i <= 5; i++) {
                Point point = board.getPoint(i);

                if (point.isOwnedBy(player)) {
                    return true;
                }
            }
        }

        return false;
    }
}