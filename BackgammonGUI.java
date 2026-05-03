import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

public class BackgammonGUI {

    private Game game;
    private JFrame frame;
    private BoardPanel boardPanel;
    private JPanel movePanel;
    private JLabel turnLabel;
    private JLabel diceLabel;
    private JButton undoButton;

    private ArrayList<Integer> remainingDiceMoves;
    private ArrayList<GameState> undoStack;
    private boolean canSwitchDiceOrder;
    private boolean isDoubleTurn;

    public BackgammonGUI() {
        game = new Game();

        game.board.initializeBearOffTest(game.player1, game.player2);

        frame = new JFrame("Backgammon Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        turnLabel = new JLabel("Current player: " + game.currentPlayer.getName());
        diceLabel = new JLabel("Click dice to roll");
        topPanel.add(turnLabel);
        topPanel.add(diceLabel);
        frame.add(topPanel, BorderLayout.NORTH);

        boardPanel = new BoardPanel(game.board, game.player1, game.player2);
        boardPanel.setDiceClickAction(() -> startTurn());
        boardPanel.setReverseClickAction(() -> switchDiceOrder());
        frame.add(boardPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel actionPanel = new JPanel(new GridLayout(1, 1));

        undoButton = new JButton("↶");
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> undoLastMove());
        actionPanel.add(undoButton);

        rightPanel.add(actionPanel, BorderLayout.NORTH);

        movePanel = new JPanel(new GridLayout(0, 1));
        rightPanel.add(new JScrollPane(movePanel), BorderLayout.CENTER);

        frame.add(rightPanel, BorderLayout.EAST);

        remainingDiceMoves = new ArrayList<>();
        undoStack = new ArrayList<>();
        canSwitchDiceOrder = false;
        isDoubleTurn = false;

        updateBoardDisplay();
    }

    public void show() {
        frame.setVisible(true);
    }

    private void startTurn() {
        if (!remainingDiceMoves.isEmpty()) {
            return;
        }

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

        diceLabel.setText("Dice: " + game.dice.getFirstDie() + " - " + game.dice.getSecondDie());

        updateBoardDisplay();
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

        diceLabel.setText("Current dice: " + diceValue);

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

        updateBoardDisplay();
        movePanel.revalidate();
        movePanel.repaint();
    }

    private void switchDiceOrder() {
        if (!canSwitchDiceOrder || remainingDiceMoves.size() != 2) {
            return;
        }

        int firstDice = remainingDiceMoves.get(0);
        int secondDice = remainingDiceMoves.get(1);

        remainingDiceMoves.set(0, secondDice);
        remainingDiceMoves.set(1, firstDice);

        updateBoardDisplay();
        showMovesForNextDice();
    }

    private void skipCurrentDice() {
        if (!remainingDiceMoves.isEmpty()) {
            remainingDiceMoves.remove(0);
        }

        canSwitchDiceOrder = false;
        updateBoardDisplay();
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

        updateBoardDisplay();
    }

    private void restartGame() {
        game = new Game();

        game.board.initializeBearOffTest(game.player1, game.player2);

        remainingDiceMoves = new ArrayList<>();
        undoStack.clear();
        canSwitchDiceOrder = false;
        isDoubleTurn = false;

        undoButton.setEnabled(false);
        diceLabel.setText("Click dice to roll");
        turnLabel.setText("Current player: " + game.currentPlayer.getName());

        movePanel.removeAll();
        movePanel.revalidate();
        movePanel.repaint();

        updateBoardDisplay();
    }

    private void endTurn() {
        game.switchPlayer();

        remainingDiceMoves = new ArrayList<>();
        undoStack.clear();
        undoButton.setEnabled(false);
        canSwitchDiceOrder = false;
        isDoubleTurn = false;

        movePanel.removeAll();
        movePanel.revalidate();
        movePanel.repaint();

        turnLabel.setText("Current player: " + game.currentPlayer.getName());
        diceLabel.setText("Click dice to roll");
        updateBoardDisplay();
    }

    private void updateBoardDisplay() {
        boardPanel.setGameData(game.board, game.player1, game.player2);

        int leftDie = 0;
        int rightDie = 0;
        boolean canRoll = remainingDiceMoves.isEmpty() && !game.isGameOver();
        boolean canReverse = canSwitchDiceOrder && remainingDiceMoves.size() == 2 && !isDoubleTurn;
        int remainingCount = 0;

        if (!canRoll && !remainingDiceMoves.isEmpty()) {
            leftDie = remainingDiceMoves.get(0);

            if (isDoubleTurn) {
                rightDie = leftDie;
                remainingCount = remainingDiceMoves.size();
            } else if (remainingDiceMoves.size() > 1) {
                rightDie = remainingDiceMoves.get(1);
            }
        }

        boardPanel.setDiceData(leftDie, rightDie, canRoll, canReverse, isDoubleTurn, remainingCount);
        boardPanel.repaint();

        turnLabel.setText("Current player: " + game.currentPlayer.getName());
    }

    private int toDisplayPoint(int internalIndex) {
        return internalIndex + 1;
    }
}