class Main {

    public static void main(String[] args) {
        Game game = new Game();
        game.startGame();

        while (!game.isGameOver()) {
            game.playTurn();
        }

        Player winner = game.getWinner();

        if (winner != null) {
            System.out.println("Game over. Winner: " + winner.getName());
        }
    }
}
