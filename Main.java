import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BackgammonGUI gui = new BackgammonGUI();
                gui.show();
            }
        });
    }
}

class BackgammonGUI {

    private Game game;
    private JFrame frame;
    private JButton[] pointButtons;
    private JPanel movePanel;
    private JTextArea infoArea;
    private JLabel turnLabel;
    private JLabel diceLabel;
    private JButton rollButton;

    private int[] currentDiceMoves;
    private int currentDiceIndex;

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

        JPanel boardPanel = new JPanel(new GridLayout(2, 12));
        pointButtons = new JButton[24];

        for (int i = 0; i < 24; i++) {
            pointButtons[i] = new JButton();
            pointButtons[i].setEnabled(false);
        }

        // Top row: points 13 to 24 from left to right.
        for (int pointNumber = 13; pointNumber <= 24; pointNumber++) {
            boardPanel.add(pointButtons[pointNumber - 1]);
        }

        // Bottom row: points 12 to 1 from left to right.
        for (int pointNumber = 12; pointNumber >= 1; pointNumber--) {
            boardPanel.add(pointButtons[pointNumber - 1]);
        }

        frame.add(boardPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());

        rollButton = new JButton("Roll Dice");
        rollButton.addActionListener(e -> startTurn());
        rightPanel.add(rollButton, BorderLayout.NORTH);

        movePanel = new JPanel(new GridLayout(0, 1));
        rightPanel.add(new JScrollPane(movePanel), BorderLayout.CENTER);

        infoArea = new JTextArea(8, 24);
        infoArea.setEditable(false);
        rightPanel.add(new JScrollPane(infoArea), BorderLayout.SOUTH);

        frame.add(rightPanel, BorderLayout.EAST);

        updateBoardDisplay();
        updateInfo("Game started. Click Roll Dice to begin.");
    }

    public void show() {
        frame.setVisible(true);
    }

    private void startTurn() {
        game.dice.roll();
        currentDiceMoves = game.dice.getMoves();
        currentDiceIndex = 0;
        rollButton.setEnabled(false);
        diceLabel.setText("Dice: " + game.dice.getFirstDie() + " - " + game.dice.getSecondDie());

        updateInfo(game.currentPlayer.getName() + " rolled " +
                game.dice.getFirstDie() + " - " + game.dice.getSecondDie());

        showMovesForCurrentDice();
    }

    private void showMovesForCurrentDice() {
        movePanel.removeAll();

        if (currentDiceIndex >= currentDiceMoves.length) {
            endTurn();
            return;
        }

        int diceValue = currentDiceMoves[currentDiceIndex];

        ArrayList<Move> validMoves =
                game.moveValidator.getValidMovesForDice(game.board, game.currentPlayer, diceValue);

        updateInfo("Current player: " + game.currentPlayer.getName() +
                "\nDice: " + game.dice.getFirstDie() + " - " + game.dice.getSecondDie() +
                "\nCurrent dice value: " + diceValue);

        if (validMoves.isEmpty()) {
            JButton skipButton = new JButton("No valid moves for " + diceValue + " - Skip");
            skipButton.addActionListener(e -> {
                currentDiceIndex++;
                showMovesForCurrentDice();
            });
            movePanel.add(skipButton);
        } else {
            for (Move move : validMoves) {
                String moveText = "From " + toDisplayPoint(move.getFrom()) +
                        " to " + toDisplayPoint(move.getTo());

                if (move.isHit()) {
                    moveText += " (hit)";
                }

                JButton moveButton = new JButton(moveText);
                moveButton.addActionListener(e -> playMove(move));
                movePanel.add(moveButton);
            }
        }

        movePanel.revalidate();
        movePanel.repaint();
    }

    private void playMove(Move move) {
        game.board.movePiece(move.getFrom(), move.getTo(), game.currentPlayer);
        currentDiceIndex++;

        updateBoardDisplay();
        showMovesForCurrentDice();
    }

    private void endTurn() {
        game.switchPlayer();

        rollButton.setEnabled(true);
        movePanel.removeAll();
        movePanel.revalidate();
        movePanel.repaint();

        turnLabel.setText("Current player: " + game.currentPlayer.getName());
        diceLabel.setText("Dice: -");
        updateInfo("Turn ended. " + game.currentPlayer.getName() + " should roll dice.");
    }

    private void updateBoardDisplay() {
        for (int i = 0; i < 24; i++) {
            Point point = game.board.getPoint(i);
            int pointNumber = toDisplayPoint(i);
            String checkerSymbol = "";

            if (point.owner != null) {
                if (point.owner == game.player1) {
                    checkerSymbol = repeatSymbol("○", point.count);
                } else {
                    checkerSymbol = repeatSymbol("●", point.count);
                }
            }

            pointButtons[i].setText("<html><center>" + pointNumber +
                    "<br><br>" + checkerSymbol + "</center></html>");
        }

        turnLabel.setText("Current player: " + game.currentPlayer.getName());
        if (game.dice.getFirstDie() != 0 && game.dice.getSecondDie() != 0) {
            diceLabel.setText("Dice: " + game.dice.getFirstDie() + " - " + game.dice.getSecondDie());
        }
    }

    private int toDisplayPoint(int internalIndex) {
        return internalIndex + 1;
    }

    private String repeatSymbol(String symbol, int count) {
        String result = "";

        for (int i = 0; i < count; i++) {
            result += symbol;
        }

        return result;
    }

    private void updateInfo(String message) {
        infoArea.setText(message + "\n\n" +
                "Dice: " + game.dice.getFirstDie() + " - " + game.dice.getSecondDie() + "\n" +
                "Player 1: White checkers\n" +
                "Player 2: Black checkers\n\n" +
                game.player1.getName() + " bar: " + game.player1.getBarCount() +
                " | borne off: " + game.player1.getBorneOff() + "\n" +
                game.player2.getName() + " bar: " + game.player2.getBarCount() +
                " | borne off: " + game.player2.getBorneOff());
    }
}