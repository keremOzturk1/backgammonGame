

public class Move {

    int from;
    int to;
    Player player;
    boolean isHit;
    boolean isBearOff;

    // Constructor
    public Move(int from, int to, Player player) {
        this.from = from;
        this.to = to;
        this.player = player;
        this.isHit = false;
        this.isBearOff = false;
    }

    // Constructor with special move information
    public Move(int from, int to, Player player, boolean isHit, boolean isBearOff) {
        this.from = from;
        this.to = to;
        this.player = player;
        this.isHit = isHit;
        this.isBearOff = isBearOff;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isHit() {
        return isHit;
    }

    public boolean isBearOff() {
        return isBearOff;
    }
}