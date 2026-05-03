public class Player {

    String name;
    int direction; // +1 or -1
    int barCount;  // number of pieces on the bar
    int borneOff;  // number of pieces borne off

    public Player(String name, int direction) {
        this.name = name;
        this.direction = direction;
        this.barCount = 0;
        this.borneOff = 0;
    }

    public boolean hasPiecesOnBar() {
        return barCount > 0;
    }

    public void addToBar() {
        barCount++;
    }

    public void removeFromBar() {
        if (barCount > 0) {
            barCount--;
        }
    }

    public void bearOff() {
        borneOff++;
    }

    public String getName() {
        return name;
    }

    public int getDirection() {
        return direction;
    }

    public int getBarCount() {
        return barCount;
    }

    public int getBorneOff() {
        return borneOff;
    }

    public void setBarCount(int barCount) {
        this.barCount = barCount;
    }

    public void setBorneOff(int borneOff) {
        this.borneOff = borneOff;
    }
}