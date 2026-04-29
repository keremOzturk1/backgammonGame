

public class Point {

    int count;
    Player owner;

    // Constructor
    public Point() {
        count = 0;
        owner = null;
    }

    // Check if this point has no pieces
    public boolean isEmpty() {
        return count == 0;
    }

    // Check if this point belongs to the given player
    public boolean isOwnedBy(Player player) {
        return owner == player;
    }

    // Check if this point is blocked for the given player
    public boolean isBlockedFor(Player player) {
        return owner != null && owner != player && count >= 2;
    }

    // Check if this point has exactly one opponent piece
    public boolean canBeHitBy(Player player) {
        return owner != null && owner != player && count == 1;
    }

    // Add one piece to this point
    public void addPiece(Player player) {
        owner = player;
        count++;
    }

    // Remove one piece from this point
    public void removePiece() {
        if (count > 0) {
            count--;
        }

        if (count == 0) {
            owner = null;
        }
    }
}