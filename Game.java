public class Game {

    Board board;
    Player player1;
    Player player2;
    Player currentPlayer;
    Dice dice;
    GameState state;
    MoveValidator moveValidator;

    public Game() {
        board = new Board();
        player1 = new Player("Player 1", 1);
        player2 = new Player("Player 2", -1);
        currentPlayer = player1;
        dice = new Dice();
        state = new GameState();
        moveValidator = new MoveValidator();
    }

    public void startGame() {
        board.initialize(player1, player2);
        System.out.println("Backgammon game started.");
        System.out.println(currentPlayer.getName() + " starts the game.");
        board.printBoard();
    }

    public void playTurn() {
        System.out.println("\n" + currentPlayer.getName() + "'s turn.");
        dice.roll();
        System.out.println("Dice: " + dice.getFirstDie() + " - " + dice.getSecondDie());

        int[] diceMoves = dice.getMoves();

        for (int i = 0; i < diceMoves.length; i++) {
            int currentDiceValue = diceMoves[i];
            System.out.println("\nCurrent dice value: " + currentDiceValue);

            java.util.ArrayList<Move> validMoves =
                    moveValidator.getValidMovesForDice(board, currentPlayer, currentDiceValue);

            if (validMoves.isEmpty()) {
                System.out.println("No valid moves available for dice value " + currentDiceValue + ".");
                continue;
            }

            printValidMoves(validMoves);

            Move selectedMove = askPlayerToChooseMove(validMoves);
            board.applyMove(selectedMove);

            String selectedFromText;

            if (selectedMove.getFrom() == -1) {
                selectedFromText = "BAR";
            } else {
                selectedFromText = String.valueOf(selectedMove.getFrom() + 1);
            }

            if (selectedMove.isBearOff()) {
                System.out.println("Move played: Bear off from " + selectedFromText);
            } else {
                System.out.println("Move played: From " + selectedFromText + " to " + (selectedMove.getTo() + 1));
            }

            board.printBoard();
        }

        switchPlayer();
    }

    private void printValidMoves(java.util.ArrayList<Move> validMoves) {
        System.out.println("Valid moves:");

        for (int i = 0; i < validMoves.size(); i++) {
            Move move = validMoves.get(i);
            String fromText;

            if (move.getFrom() == -1) {
                fromText = "BAR";
            } else {
                fromText = String.valueOf(move.getFrom() + 1);
            }

            if (move.isBearOff()) {
                System.out.println((i + 1) + ") Bear off from " + fromText);
            } else {
                System.out.println((i + 1) + ") From " + fromText + " to " + (move.getTo() + 1) +
                        (move.isHit() ? " (hit)" : ""));
            }
        }
    }

    private Move askPlayerToChooseMove(java.util.ArrayList<Move> validMoves) {
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        int choice = -1;

        while (choice < 1 || choice > validMoves.size()) {
            System.out.print("Choose a move number: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                scanner.next();
                System.out.println("Please enter a valid number.");
            }
        }

        return validMoves.get(choice - 1);
    }

    public void switchPlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    public boolean isGameOver() {
        return player1.getBorneOff() == 15 || player2.getBorneOff() == 15;
    }

    public Player getWinner() {
        if (player1.getBorneOff() == 15) {
            return player1;
        }
        if (player2.getBorneOff() == 15) {
            return player2;
        }
        return null;
    }
}