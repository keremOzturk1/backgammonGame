public class Board {

    Point[] points;

    // Constructor
    public Board() {
        points = new Point[24];
        for (int i = 0; i < 24; i++) {
            points[i] = new Point();
        }
    }

    // Initial setup of backgammon
    public void initialize(Player player1, Player player2) {
        // Clear board
        for (int i = 0; i < 24; i++) {
            points[i].count = 0;
            points[i].owner = null;
        }

        // Standard backgammon setup for Player 1
        points[0].count = 2;
        points[0].owner = player1;

        points[11].count = 5;
        points[11].owner = player1;

        points[16].count = 3;
        points[16].owner = player1;

        points[18].count = 5;
        points[18].owner = player1;

        // Standard backgammon setup for Player 2
        points[23].count = 2;
        points[23].owner = player2;

        points[12].count = 5;
        points[12].owner = player2;

        points[7].count = 3;
        points[7].owner = player2;

        points[5].count = 5;
        points[5].owner = player2;
    }

    public Point getPoint(int index) {
        return points[index];
    }

    public void movePiece(int from, int to, Player player) {
        Point destination = points[to];

        if (from == -1) {
            player.removeFromBar();
        } else {
            Point source = points[from];
            source.removePiece();
        }

        // Handle hit
        if (destination.canBeHitBy(player)) {
            destination.owner.addToBar();
            destination.count = 0;
            destination.owner = null;
        }

        destination.addPiece(player);
    }

    // Debug print
    public void printBoard() {
        for (int i = 0; i < 24; i++) {
            String ownerName = "empty";

            if (points[i].owner != null) {
                ownerName = points[i].owner.getName();
            }

            System.out.println("Point " + (i + 1) + ": " + points[i].count + " pieces, owner: " + ownerName);
        }
    }
}