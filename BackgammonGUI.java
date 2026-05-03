import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

public class BackgammonGUI {

    private Game game;
    private JFrame frame;
    private BoardPanel boardPanel;
    private JPanel movePanel;
    private JTextArea infoArea;
    private JLabel turnLabel;
    private JLabel diceLabel;
    private JButton rollButton;
    private JButton undoButton;

    private ArrayList<Integer> remainingDiceMoves;
    private ArrayList<GameState> undoStack;
    private boolean canSwitchDiceOrder;
    private boolean isDoubleTurn;

    public BackgammonGUI() {
        game = new Game();

        game.board.initialize(game.player1, game.player2);

        frame = new JFrame("Backgammon Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        turnLabel = new JLabel("Current player: " + game.currentPlayer.getName());
        diceLabel = new JLabel("Dice: -");
        topPanel.add(turnLabel);
        topPanel.add(diceLabel);
        frame.add(topPanel, BorderLayout.NORTH);

        boardPanel = new BoardPanel(game.board, game.player1, game.player2);
        frame.add(boardPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel actionPanel = new JPanel(new GridLayout(2, 1));

        rollButton = new JButton("Roll Dice");
        rollButton.addActionListener(e -> startTurn());
        actionPanel.add(rollButton);

        undoButton = new JButton("Undo Last Move");
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> undoLastMove());
        actionPanel.add(undoButton);

        rightPanel.add(actionPanel, BorderLayout.NORTH);

        movePanel = new JPanel(new GridLayout(0, 1));
        rightPanel.add(new JScrollPane(movePanel), BorderLayout.CENTER);

        infoArea = new JTextArea(8, 24);
        infoArea.setEditable(false);
        rightPanel.add(new JScrollPane(infoArea), BorderLayout.SOUTH);

        frame.add(rightPanel, BorderLayout.EAST);

        remainingDiceMoves = new ArrayList<>();
        undoStack = new ArrayList<>();
        canSwitchDiceOrder = false;
        isDoubleTurn = false;

        updateBoardDisplay();
        updateInfo("Game started. Click Roll Dice to begin.");
    }

    public void show() {
        frame.setVisible(true);
    }

    private void startTurn() {
        if (game.isGameOver()) {
            showGameOverScreen();
            return;
        }

        game.dice.roll();
        remainingDiceMoves = new ArrayList<>();
        undoStack.clear();
        undoButton.setEnabled(false);

        int[] diceMoves = game.dice.getMoves();
        for (int i = 0; i < diceMoves.length; i++) {
            remainingDiceMoves.add(diceMoves[i]);
        }

        isDoubleTurn = game.dice.isDouble();
        canSwitchDiceOrder = !isDoubleTurn && remainingDiceMoves.size() == 2;

        rollButton.setEnabled(false);
        diceLabel.setText("Dice: " + game.dice.getFirstDie() + " - " + game.dice.getSecondDie());

        updateInfo(game.currentPlayer.getName() + " rolled " +
                game.dice.getFirstDie() + " - " + game.dice.getSecondDie());

        showMovesForNextDice();
    }

    private void showMovesForNextDice() {
        movePanel.removeAll();

        if (game.isGameOver()) {
            showGameOverScreen();
            return;
        }

        if (remainingDiceMoves.isEmpty()) {
            endTurn();
            return;
        }

        int diceValue = remainingDiceMoves.get(0);

        ArrayList<Move> validMoves =
                game.moveValidator.getValidMovesForDice(game.board, game.currentPlayer, diceValue);

        String extraInfo;

        if (isDoubleTurn) {
            extraInfo = "\nDouble moves remaining: " + remainingDiceMoves.size();
        } else {
            extraInfo = "";
        }

        updateInfo("Current player: " + game.currentPlayer.getName() +
                "\nDice: " + game.dice.getFirstDie() + " - " + game.dice.getSecondDie() +
                "\nCurrent dice value: " + diceValue + extraInfo);

        if (validMoves.isEmpty()) {
            JButton skipButton = new JButton("No valid moves for " + diceValue + " - Skip");
            skipButton.addActionListener(e -> skipCurrentDice());
            movePanel.add(skipButton);
        } else {
            for (Move move : validMoves) {
                String fromText;

                if (move.getFrom() == -1) {
                    fromText = "BAR";
                } else {
                    fromText = String.valueOf(toDisplayPoint(move.getFrom()));
                }

                String moveText;

                if (move.isBearOff()) {
                    moveText = "Bear off from " + fromText;
                } else {
                    moveText = "From " + fromText +
                            " to " + toDisplayPoint(move.getTo());
                }

                if (move.isHit()) {
                    moveText += " (hit)";
                }

                JButton moveButton = new JButton(moveText);
                moveButton.addActionListener(e -> playMove(move));
                movePanel.add(moveButton);
            }
        }

        if (canSwitchDiceOrder && remainingDiceMoves.size() == 2) {
            JButton switchButton = new JButton("Switch dice order: use " + remainingDiceMoves.get(1) + " first instead");
            switchButton.addActionListener(e -> switchDiceOrder());
            movePanel.add(switchButton);
        }

        movePanel.revalidate();
        movePanel.repaint();
    }

    private void switchDiceOrder() {
        int firstDice = remainingDiceMoves.get(0);
        int secondDice = remainingDiceMoves.get(1);

        remainingDiceMoves.set(0, secondDice);
        remainingDiceMoves.set(1, firstDice);

        showMovesForNextDice();
    }

    private void skipCurrentDice() {
        if (!remainingDiceMoves.isEmpty()) {
            remainingDiceMoves.remove(0);
        }

        canSwitchDiceOrder = false;
        showMovesForNextDice();
    }

    private void playMove(Move move) {
        undoStack.add(new GameState(game, remainingDiceMoves, canSwitchDiceOrder, isDoubleTurn));
        undoButton.setEnabled(true);

        game.board.applyMove(move);

        if (!remainingDiceMoves.isEmpty()) {
            remainingDiceMoves.remove(0);
        }

        canSwitchDiceOrder = false;

        updateBoardDisplay();

        if (game.isGameOver()) {
            showGameOverScreen();
            return;
        }

        showMovesForNextDice();
    }

    private void undoLastMove() {
        if (undoStack.isEmpty()) {
            undoButton.setEnabled(false);
            return;
        }

        GameState previousState = undoStack.remove(undoStack.size() - 1);
        previousState.restore(game);
        remainingDiceMoves = previousState.getRemainingDiceMovesCopy();
        canSwitchDiceOrder = previousState.getCanSwitchDiceOrder();
        isDoubleTurn = previousState.getIsDoubleTurn();

        undoButton.setEnabled(!undoStack.isEmpty());

        updateBoardDisplay();
        showMovesForNextDice();
    }

    private void showGameOverScreen() {
        Player winner = game.getWinner();
        String winnerText = "Unknown";

        if (winner == game.player1) {
            winnerText = "Brown";
        } else if (winner == game.player2) {
            winnerText = "Green";
        }

        rollButton.setEnabled(false);
        undoButton.setEnabled(false);
        diceLabel.setText("Game Over");

        movePanel.removeAll();

        JLabel gameOverLabel = new JLabel("Game Over - Winner: " + winnerText);
        JButton restartButton = new JButton("Restart Game");
        restartButton.addActionListener(e -> restartGame());

        movePanel.add(gameOverLabel);
        movePanel.add(restartButton);
        movePanel.revalidate();
        movePanel.repaint();

        updateInfo("Game Over\nWinner: " + winnerText);
    }

    private void restartGame() {
        game = new Game();

        game.board.initialize(game.player1, game.player2);

        remainingDiceMoves = new ArrayList<>();
        undoStack.clear();
        canSwitchDiceOrder = false;
        isDoubleTurn = false;

        rollButton.setEnabled(true);
        undoButton.setEnabled(false);
        diceLabel.setText("Dice: -");
        turnLabel.setText("Current player: " + game.currentPlayer.getName());

        movePanel.removeAll();
        movePanel.revalidate();
        movePanel.repaint();

        updateBoardDisplay();
        updateInfo("Game restarted. Click Roll Dice to begin.");
    }

    private void endTurn() {
        game.switchPlayer();

        remainingDiceMoves = new ArrayList<>();
        undoStack.clear();
        undoButton.setEnabled(false);
        canSwitchDiceOrder = false;
        isDoubleTurn = false;

        rollButton.setEnabled(true);
        movePanel.removeAll();
        movePanel.revalidate();
        movePanel.repaint();

        turnLabel.setText("Current player: " + game.currentPlayer.getName());
        diceLabel.setText("Dice: -");
        updateInfo("Turn ended. " + game.currentPlayer.getName() + " should roll dice.");
    }

    private void updateBoardDisplay() {
        boardPanel.setGameData(game.board, game.player1, game.player2);
        boardPanel.repaint();

        turnLabel.setText("Current player: " + game.currentPlayer.getName());
        if (game.dice.getFirstDie() != 0 && game.dice.getSecondDie() != 0) {
            diceLabel.setText("Dice: " + game.dice.getFirstDie() + " - " + game.dice.getSecondDie());
        }
    }

    private int toDisplayPoint(int internalIndex) {
        return internalIndex + 1;
    }

    private void updateInfo(String message) {
        infoArea.setText(message + "\n\n" +
                "Dice: " + game.dice.getFirstDie() + " - " + game.dice.getSecondDie() + "\n" +
                "Player 1: Brown checkers\n" +
                "Player 2: Green checkers\n\n" +
                game.player1.getName() + " bar: " + game.player1.getBarCount() +
                " | borne off: " + game.player1.getBorneOff() + "\n" +
                game.player2.getName() + " bar: " + game.player2.getBarCount() +
                " | borne off: " + game.player2.getBorneOff());
    }
}