import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.util.ArrayList;

public class BackgammonGUI {

    private Game game;
    private JFrame frame;
    private BoardPanel boardPanel;
    private JPanel movePanel;
    private JLabel turnLabel;
    private JLabel diceLabel;

    private ArrayList<Integer> remainingDiceMoves;
    private ArrayList<GameState> undoStack;
    private ArrayList<MoveOption> currentMoveOptions;
    private int selectedSourcePoint;
    private int originalLeftDie;
    private int originalRightDie;
    private boolean canSwitchDiceOrder;
    private boolean isDoubleTurn;
    private final int NO_SELECTED_SOURCE = -2;

    public BackgammonGUI() {
        game = new Game();

        game.board.initialize(game.player1, game.player2);
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
        boardPanel.setUndoClickAction(() -> undoLastMove());
        boardPanel.setPointClickAction(pointIndex -> handlePointClick(pointIndex));
        frame.add(boardPanel, BorderLayout.CENTER);

        movePanel = new JPanel(new GridLayout(0, 1));

        remainingDiceMoves = new ArrayList<>();
        undoStack = new ArrayList<>();
        currentMoveOptions = new ArrayList<>();
        selectedSourcePoint = NO_SELECTED_SOURCE;
        originalLeftDie = 0;
        originalRightDie = 0;
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
        currentMoveOptions = new ArrayList<>();
        selectedSourcePoint = NO_SELECTED_SOURCE;
        boardPanel.setSelectedPoint(-1);
        boardPanel.setUndoAvailable(false);
        clearMoveHighlights();

        int[] diceMoves = game.dice.getMoves();
        for (int i = 0; i < diceMoves.length; i++) {
            remainingDiceMoves.add(diceMoves[i]);
        }

        isDoubleTurn = game.dice.isDouble();
        canSwitchDiceOrder = false;
        setOriginalDiceValues();

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

        currentMoveOptions = getMoveOptionsForRemainingDice();

        if (currentMoveOptions.isEmpty()) {
            selectedSourcePoint = NO_SELECTED_SOURCE;
            boardPanel.setSelectedPoint(-1);
            clearMoveHighlights();
            remainingDiceMoves.clear();
            updateBoardDisplay();
            endTurn();
            return;
        }

        ArrayList<Integer> highlightedPoints = getHighlightedSourcePoints(currentMoveOptions);
        boardPanel.setHighlightedPoints(highlightedPoints);
        diceLabel.setText("Select a highlighted checker");

        updateBoardDisplay();
        movePanel.revalidate();
        movePanel.repaint();
    }

    private void switchDiceOrder() {
        // Dice order is no longer selected manually.
    }

    private void skipCurrentDice() {
        remainingDiceMoves.clear();
        canSwitchDiceOrder = false;
        selectedSourcePoint = NO_SELECTED_SOURCE;
        boardPanel.setSelectedPoint(-1);
        clearTargetHighlights();
        updateBoardDisplay();
        endTurn();
    }

    private void playMove(Move move) {
        undoStack.add(new GameState(game, remainingDiceMoves, canSwitchDiceOrder, isDoubleTurn));
        boardPanel.setUndoAvailable(true);

        game.board.applyMove(move);
        removeMatchingDiceForMove(move);

        canSwitchDiceOrder = false;
        selectedSourcePoint = NO_SELECTED_SOURCE;
        boardPanel.setSelectedPoint(-1);
        clearTargetHighlights();

        updateBoardDisplay();

        if (game.isGameOver()) {
            showGameOverScreen();
            return;
        }

        showMovesForNextDice();
    }

    private void playMoveOption(MoveOption option) {
        undoStack.add(new GameState(game, remainingDiceMoves, canSwitchDiceOrder, isDoubleTurn));
        boardPanel.setUndoAvailable(true);

        game.board.applyMove(option.getMove());
        removeDiceValuesForOption(option);

        canSwitchDiceOrder = false;
        selectedSourcePoint = NO_SELECTED_SOURCE;
        boardPanel.setSelectedPoint(-1);
        clearTargetHighlights();

        updateBoardDisplay();

        if (game.isGameOver()) {
            showGameOverScreen();
            return;
        }

        showMovesForNextDice();
    }

    private void removeDiceValue(int diceValue) {
        for (int i = 0; i < remainingDiceMoves.size(); i++) {
            if (remainingDiceMoves.get(i) == diceValue) {
                remainingDiceMoves.remove(i);
                return;
            }
        }

        if (!remainingDiceMoves.isEmpty()) {
            remainingDiceMoves.remove(0);
        }
    }

    private void removeDiceValuesForOption(MoveOption option) {
        removeDiceValue(option.getDiceValue());

        if (option.isCombinedMove()) {
            removeDiceValue(option.getSecondDiceValue());
        }
    }

    private void removeMatchingDiceForMove(Move move) {
        MoveOption bestOption = null;

        for (MoveOption option : currentMoveOptions) {
            Move optionMove = option.getMove();

            if (movesRepresentSameAction(optionMove, move)) {
                if (bestOption == null || option.getDiceValue() < bestOption.getDiceValue()) {
                    bestOption = option;
                }
            }
        }

        if (bestOption != null) {
            removeDiceValuesForOption(bestOption);
        } else if (!remainingDiceMoves.isEmpty()) {
            remainingDiceMoves.remove(0);
        }
    }

    private boolean movesRepresentSameAction(Move first, Move second) {
        return first.getFrom() == second.getFrom()
                && first.getTo() == second.getTo()
                && first.isBearOff() == second.isBearOff();
    }

    private void undoLastMove() {
        if (undoStack.isEmpty()) {
            boardPanel.setUndoAvailable(false);
            return;
        }

        GameState previousState = undoStack.remove(undoStack.size() - 1);
        previousState.restore(game);
        remainingDiceMoves = previousState.getRemainingDiceMovesCopy();
        canSwitchDiceOrder = previousState.getCanSwitchDiceOrder();
        isDoubleTurn = previousState.getIsDoubleTurn();
        selectedSourcePoint = NO_SELECTED_SOURCE;
        boardPanel.setSelectedPoint(-1);
        clearTargetHighlights();

        boardPanel.setUndoAvailable(!undoStack.isEmpty());

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

        currentMoveOptions = new ArrayList<>();
        selectedSourcePoint = NO_SELECTED_SOURCE;
        boardPanel.setSelectedPoint(-1);
        boardPanel.setUndoAvailable(false);
        clearMoveHighlights();
        diceLabel.setText("Game Over");

        movePanel.removeAll();

        JLabel gameOverLabel = new JLabel("Game Over - Winner: " + winnerText);
        JButton restartButton = new JButton("Restart Game");
        restartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        restartButton.addActionListener(e -> restartGame());

        movePanel.add(gameOverLabel);
        movePanel.add(restartButton);
        movePanel.revalidate();
        movePanel.repaint();

        updateBoardDisplay();
    }

    private void restartGame() {
        game = new Game();

        game.board.initialize(game.player1, game.player2);
        remainingDiceMoves = new ArrayList<>();
        undoStack.clear();
        currentMoveOptions = new ArrayList<>();
        selectedSourcePoint = NO_SELECTED_SOURCE;
        originalLeftDie = 0;
        originalRightDie = 0;
        canSwitchDiceOrder = false;
        isDoubleTurn = false;

        boardPanel.setSelectedPoint(-1);
        boardPanel.setUndoAvailable(false);
        clearMoveHighlights();
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
        currentMoveOptions = new ArrayList<>();
        selectedSourcePoint = NO_SELECTED_SOURCE;
        boardPanel.setSelectedPoint(-1);
        boardPanel.setUndoAvailable(!undoStack.isEmpty());
        canSwitchDiceOrder = false;
        isDoubleTurn = false;
        clearMoveHighlights();

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
        boolean canReverse = false;
        int remainingCount = 0;

        if (!canRoll && !remainingDiceMoves.isEmpty()) {
            if (isDoubleTurn) {
                remainingCount = remainingDiceMoves.size();
                int usedMoves = 4 - remainingCount;

                if (usedMoves < 2) {
                    leftDie = originalLeftDie;
                }

                if (usedMoves < 4) {
                    rightDie = originalRightDie;
                }
            } else {
                if (hasRemainingDieValue(originalLeftDie)) {
                    leftDie = originalLeftDie;
                }

                if (hasRemainingDieValue(originalRightDie)) {
                    rightDie = originalRightDie;
                }
            }
        }

        boardPanel.setDiceData(leftDie, rightDie, canRoll, canReverse, isDoubleTurn, remainingCount);
        boardPanel.repaint();

        turnLabel.setText("Current player: " + game.currentPlayer.getName());
    }

    private ArrayList<MoveOption> getMoveOptionsForRemainingDice() {
        ArrayList<MoveOption> options = new ArrayList<>();

        for (int i = 0; i < remainingDiceMoves.size(); i++) {
            int diceValue = remainingDiceMoves.get(i);
            ArrayList<Move> validMoves =
                    game.moveValidator.getValidMovesForDice(game.board, game.currentPlayer, diceValue);

            for (Move move : validMoves) {
                options.add(new MoveOption(move, diceValue));
            }
        }

        addCombinedMoveOptions(options);
        return options;
    }

    private void addCombinedMoveOptions(ArrayList<MoveOption> options) {
        if (isDoubleTurn || remainingDiceMoves.size() != 2 || !undoStack.isEmpty()) {
            return;
        }

        int firstDice = remainingDiceMoves.get(0);
        int secondDice = remainingDiceMoves.get(1);

        addCombinedMoveOptionsForOrder(options, firstDice, secondDice);
        addCombinedMoveOptionsForOrder(options, secondDice, firstDice);
    }

    private void addCombinedMoveOptionsForOrder(ArrayList<MoveOption> options, int firstDice, int secondDice) {
        ArrayList<Move> firstMoves =
                game.moveValidator.getValidMovesForDice(game.board, game.currentPlayer, firstDice);

        for (Move firstMove : firstMoves) {
            if (firstMove.isBearOff() || firstMove.isHit()) {
                continue;
            }

            int intermediatePoint = firstMove.getTo();
            int finalPoint = intermediatePoint + (secondDice * game.currentPlayer.getDirection());
            Move combinedMove = new Move(firstMove.getFrom(), finalPoint, game.currentPlayer);

            if (isBearOffTarget(finalPoint)) {
                combinedMove.isBearOff = true;
            }

            if (game.moveValidator.isValidMove(game.board, combinedMove, firstDice + secondDice)) {
                if (!combinedMove.isBearOff()) {
                    Point destination = game.board.getPoint(finalPoint);

                    if (destination.canBeHitBy(game.currentPlayer)) {
                        combinedMove.isHit = true;
                    }
                }

                MoveOption combinedOption = new MoveOption(combinedMove, firstDice, secondDice);

                if (!containsEquivalentMoveOption(options, combinedOption)) {
                    options.add(combinedOption);
                }
            }
        }
    }

    private boolean isBearOffTarget(int targetPoint) {
        if (game.currentPlayer.getDirection() == 1) {
            return targetPoint >= 24;
        }

        return targetPoint < 0;
    }

    private boolean containsEquivalentMoveOption(ArrayList<MoveOption> options, MoveOption candidate) {
        for (MoveOption option : options) {
            Move existingMove = option.getMove();
            Move candidateMove = candidate.getMove();

            if (existingMove.getFrom() == candidateMove.getFrom()
                    && existingMove.getTo() == candidateMove.getTo()
                    && existingMove.isBearOff() == candidateMove.isBearOff()
                    && option.getTotalDiceValue() == candidate.getTotalDiceValue()) {
                return true;
            }
        }

        return false;
    }

    private void setOriginalDiceValues() {
        if (isDoubleTurn) {
            originalLeftDie = game.dice.getFirstDie();
            originalRightDie = game.dice.getFirstDie();
            return;
        }

        int firstDie = game.dice.getFirstDie();
        int secondDie = game.dice.getSecondDie();

        if (firstDie <= secondDie) {
            originalLeftDie = firstDie;
            originalRightDie = secondDie;
        } else {
            originalLeftDie = secondDie;
            originalRightDie = firstDie;
        }
    }

    private boolean hasRemainingDieValue(int diceValue) {
        for (int i = 0; i < remainingDiceMoves.size(); i++) {
            if (remainingDiceMoves.get(i) == diceValue) {
                return true;
            }
        }

        return false;
    }

    private int getSmallestRemainingDie() {
        if (remainingDiceMoves.isEmpty()) {
            return 0;
        }

        int smallest = remainingDiceMoves.get(0);
        for (int i = 1; i < remainingDiceMoves.size(); i++) {
            if (remainingDiceMoves.get(i) < smallest) {
                smallest = remainingDiceMoves.get(i);
            }
        }

        return smallest;
    }

    private int getLargestRemainingDie() {
        if (remainingDiceMoves.isEmpty()) {
            return 0;
        }

        int largest = remainingDiceMoves.get(0);
        for (int i = 1; i < remainingDiceMoves.size(); i++) {
            if (remainingDiceMoves.get(i) > largest) {
                largest = remainingDiceMoves.get(i);
            }
        }

        return largest;
    }

    private ArrayList<Integer> getHighlightedSourcePoints(ArrayList<MoveOption> moveOptions) {
        ArrayList<Integer> highlightedPoints = new ArrayList<>();

        for (MoveOption option : moveOptions) {
            int sourcePoint = option.getMove().getFrom();

            if (!highlightedPoints.contains(sourcePoint)) {
                highlightedPoints.add(sourcePoint);
            }
        }

        return highlightedPoints;
    }

    private void handlePointClick(int pointIndex) {
        if (remainingDiceMoves.isEmpty() || game.isGameOver()) {
            return;
        }

        if (selectedSourcePoint != NO_SELECTED_SOURCE) {
            if (pointIndex == selectedSourcePoint) {
                MoveOption bearOffOption = getBearOffOptionForSelectedPoint();

                if (bearOffOption != null) {
                    playMoveOption(bearOffOption);
                    return;
                }
            }

            MoveOption targetOption = getMoveOptionForSelectedTarget(pointIndex);

            if (targetOption != null) {
                playMoveOption(targetOption);
                return;
            }
        }

        if (isPlayableSourcePoint(pointIndex)) {
            MoveOption bearOffOnlyOption = getBearOffOptionForPoint(pointIndex);

            if (bearOffOnlyOption != null && !hasNormalMoveOptionForPoint(pointIndex)) {
                playMoveOption(bearOffOnlyOption);
                return;
            }

            selectedSourcePoint = pointIndex;
            boardPanel.setSelectedPoint(pointIndex);
            updateTargetHighlightsForSelectedPoint(pointIndex);

            if (pointIndex == -1) {
                diceLabel.setText("Selected: BAR");
            } else {
                diceLabel.setText("Selected point: " + toDisplayPoint(pointIndex));
            }
        }
    }

    private boolean isPlayableSourcePoint(int pointIndex) {
        for (MoveOption option : currentMoveOptions) {
            if (option.getMove().getFrom() == pointIndex) {
                return true;
            }
        }

        return false;
    }

    private void updateTargetHighlightsForSelectedPoint(int pointIndex) {
        ArrayList<Integer> targetPoints = new ArrayList<>();
        ArrayList<Integer> hitTargetPoints = new ArrayList<>();
        boolean canBearOff = false;

        for (MoveOption option : currentMoveOptions) {
            Move move = option.getMove();

            if (move.getFrom() == pointIndex) {
                if (move.isBearOff()) {
                    canBearOff = true;
                } else {
                    int targetPoint = move.getTo();

                    if (!targetPoints.contains(targetPoint)) {
                        targetPoints.add(targetPoint);
                    }

                    if (move.isHit() && !hitTargetPoints.contains(targetPoint)) {
                        hitTargetPoints.add(targetPoint);
                    }
                }
            }
        }

        boardPanel.setTargetPoints(targetPoints);
        boardPanel.setHitTargetPoints(hitTargetPoints);
        boardPanel.setSelectedPointCanBearOff(canBearOff);
    }

    private MoveOption getMoveOptionForSelectedTarget(int targetPoint) {
        MoveOption bestOption = null;

        for (MoveOption option : currentMoveOptions) {
            Move move = option.getMove();

            if (move.getFrom() == selectedSourcePoint && !move.isBearOff() && move.getTo() == targetPoint) {
                if (bestOption == null || option.getTotalDiceValue() < bestOption.getTotalDiceValue()) {
                    bestOption = option;
                }
            }
        }

        return bestOption;
    }

    private MoveOption getBearOffOptionForSelectedPoint() {
        return getBearOffOptionForPoint(selectedSourcePoint);
    }

    private MoveOption getBearOffOptionForPoint(int pointIndex) {
        MoveOption bestOption = null;

        for (MoveOption option : currentMoveOptions) {
            Move move = option.getMove();

            if (move.getFrom() == pointIndex && move.isBearOff()) {
                if (bestOption == null || option.getTotalDiceValue() < bestOption.getTotalDiceValue()) {
                    bestOption = option;
                }
            }
        }

        return bestOption;
    }

    private boolean hasNormalMoveOptionForPoint(int pointIndex) {
        for (MoveOption option : currentMoveOptions) {
            Move move = option.getMove();

            if (move.getFrom() == pointIndex && !move.isBearOff()) {
                return true;
            }
        }

        return false;
    }

    private void clearMoveHighlights() {
        boardPanel.setHighlightedPoints(new ArrayList<>());
        clearTargetHighlights();
    }

    private void clearTargetHighlights() {
        boardPanel.setTargetPoints(new ArrayList<>());
        boardPanel.setHitTargetPoints(new ArrayList<>());
        boardPanel.setSelectedPointCanBearOff(false);
    }

    private int toDisplayPoint(int internalIndex) {
        return internalIndex + 1;
    }
}