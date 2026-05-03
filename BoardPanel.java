import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BoardPanel extends JPanel {

    private Board board;
    private Player player1;
    private Player player2;

    private DiceView diceView;
    private Runnable diceClickAction;
    private Runnable reverseClickAction;
    private int leftDieValue;
    private int rightDieValue;
    private int remainingMoveCount;
    private boolean diceCanRoll;
    private boolean diceCanReverse;
    private boolean diceIsDouble;

    private final Color woodDark = new Color(92, 50, 30);
    private final Color woodMid = new Color(135, 79, 45);
    private final Color woodLight = new Color(176, 112, 66);
    private final Color boardBase = new Color(239, 224, 190);
    private final Color olivePoint = new Color(50, 67, 45);
    private final Color terracottaPoint = new Color(153, 73, 45);
    private final Color orangeChecker = new Color(154, 78, 42);
    private final Color greenChecker = new Color(42, 84, 56);
    private final Color checkerHighlight = new Color(255, 255, 255, 80);

    public BoardPanel(Board board, Player player1, Player player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        setPreferredSize(new Dimension(900, 620));
        setBackground(woodDark);

        diceView = new DiceView();
        diceCanRoll = true;
        diceCanReverse = false;
        diceIsDouble = false;
        leftDieValue = 0;
        rightDieValue = 0;
        remainingMoveCount = 0;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e.getX(), e.getY());
            }
        });
    }

    public void setGameData(Board board, Player player1, Player player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        repaint();
    }

    public void setDiceData(int leftDieValue, int rightDieValue, boolean diceCanRoll,
                            boolean diceCanReverse, boolean diceIsDouble, int remainingMoveCount) {
        this.leftDieValue = leftDieValue;
        this.rightDieValue = rightDieValue;
        this.diceCanRoll = diceCanRoll;
        this.diceCanReverse = diceCanReverse;
        this.diceIsDouble = diceIsDouble;
        this.remainingMoveCount = remainingMoveCount;
        repaint();
    }

    public void setDiceClickAction(Runnable diceClickAction) {
        this.diceClickAction = diceClickAction;
    }

    public void setReverseClickAction(Runnable reverseClickAction) {
        this.reverseClickAction = reverseClickAction;
    }

    private void handleMouseClick(int x, int y) {
        if (diceCanReverse && diceView.isReverseClicked(x, y)) {
            if (reverseClickAction != null) {
                reverseClickAction.run();
            }
            return;
        }

        if (diceCanRoll && diceView.isDiceAreaClicked(x, y)) {
            if (diceClickAction != null) {
                diceClickAction.run();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int outerMargin = 22;
        int frameThickness = 34;
        int barWidth = 42;
        int pocketWidth = 42;

        int boardX = outerMargin;
        int boardY = outerMargin;
        int boardW = width - (2 * outerMargin);
        int boardH = height - (2 * outerMargin);

        drawWoodFrame(g2, boardX, boardY, boardW, boardH, frameThickness);

        int innerX = boardX + frameThickness + pocketWidth;
        int innerY = boardY + frameThickness;
        int innerW = boardW - (2 * frameThickness) - (2 * pocketWidth);
        int innerH = boardH - (2 * frameThickness);

        int leftHalfW = (innerW - barWidth) / 2;
        int rightHalfW = leftHalfW;
        int leftX = innerX;
        int barX = innerX + leftHalfW;
        int rightX = barX + barWidth;

        g2.setColor(boardBase);
        g2.fillRect(leftX, innerY, leftHalfW, innerH);
        g2.fillRect(rightX, innerY, rightHalfW, innerH);

        drawBar(g2, barX, innerY, barWidth, innerH);
        drawPockets(g2, boardX + frameThickness, innerY, pocketWidth, innerH,
                boardX + boardW - frameThickness - pocketWidth);
        drawHinges(g2, barX, boardY, barWidth, boardH);

        int pointHeight = innerH / 2 - 16;
        drawPoints(g2, leftX, rightX, innerY, innerH, leftHalfW, rightHalfW, pointHeight);
        drawCheckers(g2, leftX, rightX, innerY, innerH, leftHalfW, rightHalfW);
        drawBarCheckers(g2, barX, innerY, barWidth, innerH);
        drawBorneOffCheckers(g2, boardX + boardW - frameThickness - pocketWidth, innerY, pocketWidth, innerH);
        drawDiceArea(g2, leftX, innerY, leftHalfW, innerH);
        drawPointNumbers(g2, leftX, rightX, innerY, innerH, leftHalfW, rightHalfW);
    }

    private void drawWoodFrame(Graphics2D g2, int x, int y, int w, int h, int thickness) {
        g2.setColor(woodDark);
        g2.fillRoundRect(x, y, w, h, 18, 18);

        g2.setColor(woodMid);
        g2.fillRoundRect(x + 5, y + 5, w - 10, h - 10, 14, 14);

        g2.setColor(woodDark);
        g2.fillRect(x + thickness, y + thickness, w - (2 * thickness), h - (2 * thickness));

        g2.setColor(woodLight);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x + 7, y + 7, w - 14, h - 14, 12, 12);

        g2.setColor(new Color(65, 34, 21));
        g2.drawRoundRect(x + thickness / 2, y + thickness / 2, w - thickness, h - thickness, 10, 10);
    }

    private void drawBar(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(98, 55, 34));
        g2.fillRect(x, y, w, h);

        g2.setColor(new Color(65, 34, 21));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(x, y, x, y + h);
        g2.drawLine(x + w, y, x + w, y + h);

        g2.setColor(new Color(130, 84, 55));
        for (int i = 0; i < h; i += 32) {
            g2.drawLine(x + 4, y + i, x + w - 4, y + i + 18);
        }
    }

    private void drawPockets(Graphics2D g2, int leftPocketX, int y, int pocketW, int h, int rightPocketX) {
        drawSinglePocket(g2, leftPocketX, y, pocketW, h);
        drawSinglePocket(g2, rightPocketX, y, pocketW, h);
    }

    private void drawSinglePocket(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(102, 58, 36));
        g2.fillRect(x, y, w, h);

        g2.setColor(new Color(128, 78, 48));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(x + 3, y, x + 3, y + h);
        g2.drawLine(x + w - 4, y, x + w - 4, y + h);
    }

    private void drawHinges(Graphics2D g2, int barX, int boardY, int barWidth, int boardH) {
        int hingeW = barWidth - 6;
        int hingeH = 54;
        int hingeX = barX + 3;
        int centerY = boardY + boardH / 2;
        int gapFromCenter = 82;

        drawSingleHinge(g2, hingeX, centerY - gapFromCenter - hingeH, hingeW, hingeH);
        drawSingleHinge(g2, hingeX, centerY + gapFromCenter, hingeW, hingeH);
    }

    private void drawSingleHinge(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(170, 124, 65));
        g2.fillRoundRect(x, y, w, h, 5, 5);

        g2.setColor(new Color(80, 53, 28));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, w, h, 5, 5);
        g2.drawLine(x + w / 2, y + 4, x + w / 2, y + h - 4);

        int screwSize = 5;
        int leftScrewX = x + 6;
        int rightScrewX = x + w - 6 - screwSize;

        for (int i = 0; i < 3; i++) {
            int screwY = y + 8 + i * ((h - 16 - screwSize) / 2);
            drawScrew(g2, leftScrewX, screwY, screwSize);
            drawScrew(g2, rightScrewX, screwY, screwSize);
        }
    }

    private void drawScrew(Graphics2D g2, int x, int y, int size) {
        g2.setColor(new Color(95, 68, 36));
        g2.fillOval(x, y, size, size);

        g2.setColor(new Color(45, 30, 18));
        g2.drawOval(x, y, size, size);
        g2.drawLine(x + 1, y + size / 2, x + size - 2, y + size / 2);
    }

    private void drawPoints(Graphics2D g2, int leftX, int rightX, int y, int h, int leftW, int rightW, int pointH) {
        int pointW = leftW / 6;

        for (int i = 0; i < 6; i++) {
            drawTriangle(g2, leftX + i * pointW, y, pointW, pointH, true, i);
            drawTriangle(g2, rightX + i * pointW, y, pointW, pointH, true, i + 6);
            drawTriangle(g2, leftX + i * pointW, y + h, pointW, pointH, false, i);
            drawTriangle(g2, rightX + i * pointW, y + h, pointW, pointH, false, i + 6);
        }
    }

    private void drawTriangle(Graphics2D g2, int x, int y, int w, int h, boolean top, int colorIndex) {
        Polygon triangle = new Polygon();

        if (top) {
            triangle.addPoint(x, y);
            triangle.addPoint(x + w, y);
            triangle.addPoint(x + w / 2, y + h);
        } else {
            triangle.addPoint(x, y);
            triangle.addPoint(x + w, y);
            triangle.addPoint(x + w / 2, y - h);
        }

        if (colorIndex % 2 == 0) {
            g2.setColor(terracottaPoint);
        } else {
            g2.setColor(olivePoint);
        }

        g2.fillPolygon(triangle);
    }

    private void drawCheckers(Graphics2D g2, int leftX, int rightX, int y, int h, int leftW, int rightW) {
        for (int pointIndex = 0; pointIndex < 24; pointIndex++) {
            Point point = board.getPoint(pointIndex);

            if (point.isEmpty()) {
                continue;
            }

            int displayPoint = pointIndex + 1;
            int[] center = getPointCenter(displayPoint, leftX, rightX, y, h, leftW, rightW);

            int checkerSize = Math.min(leftW / 6, h / 12) - 8;
            int stackGap = Math.max(8, checkerSize - 6);
            boolean topRow = displayPoint >= 13;

            for (int i = 0; i < point.count; i++) {
                int checkerX = center[0] - checkerSize / 2;
                int checkerY;

                if (topRow) {
                    checkerY = y + (i * stackGap);
                } else {
                    checkerY = y + h - checkerSize - (i * stackGap);
                }

                drawChecker(g2, checkerX, checkerY, checkerSize, point.owner);
            }
        }
    }

    private void drawBarCheckers(Graphics2D g2, int barX, int y, int barWidth, int h) {
        int checkerSize = Math.max(24, barWidth - 10);
        int checkerX = barX + (barWidth - checkerSize) / 2;

        if (player1.getBarCount() > 0) {
            int stackTopY = y + h / 2 - checkerSize - 18;
            drawCompressedStack(g2, checkerX, stackTopY, checkerSize, player1.getBarCount(), player1, true);
        }

        if (player2.getBarCount() > 0) {
            int stackTopY = y + h / 2 + 18;
            drawCompressedStack(g2, checkerX, stackTopY, checkerSize, player2.getBarCount(), player2, false);
        }
    }

    private void drawBorneOffCheckers(Graphics2D g2, int rightPocketX, int y, int pocketW, int h) {
        int checkerSize = Math.max(24, pocketW - 12);
        int checkerX = rightPocketX + (pocketW - checkerSize) / 2;

        int upperPocketY = y + h / 4 - checkerSize / 2;
        int lowerPocketY = y + (3 * h) / 4 - checkerSize / 2;

        drawBorneOffPocket(g2, checkerX, upperPocketY, checkerSize, player1.getBorneOff(), player1);
        drawBorneOffPocket(g2, checkerX, lowerPocketY, checkerSize, player2.getBorneOff(), player2);
    }

    private void drawBorneOffPocket(Graphics2D g2, int x, int y, int checkerSize, int count, Player owner) {
        if (count > 0) {
            drawCompressedStack(g2, x, y, checkerSize, count, owner, false);
        }
    }

    private void drawDiceArea(Graphics2D g2, int leftX, int y, int leftW, int h) {
        int centerX = leftX + leftW / 2;
        int centerY = y + h / 2;

        diceView.draw(g2, centerX, centerY, leftDieValue, rightDieValue,
                diceCanRoll, diceCanReverse, diceIsDouble, remainingMoveCount);
    }

    private void drawCompressedStack(Graphics2D g2, int x, int y, int checkerSize, int count, Player owner, boolean upward) {
        if (count <= 0) {
            return;
        }

        int visibleOffset = Math.max(6, checkerSize / 5);
        int maxVisiblePieces = Math.min(count, 8);

        for (int i = maxVisiblePieces - 1; i >= 0; i--) {
            int checkerY;

            if (upward) {
                checkerY = y - (i * visibleOffset);
            } else {
                checkerY = y + (i * visibleOffset);
            }

            drawChecker(g2, x, checkerY, checkerSize, owner);
        }

        if (count > maxVisiblePieces) {
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.setColor(new Color(235, 220, 190));

            String text = "x" + count;
            FontMetrics metrics = g2.getFontMetrics();
            int textX = x + checkerSize / 2 - metrics.stringWidth(text) / 2;
            int textY;

            if (upward) {
                textY = y - (maxVisiblePieces * visibleOffset) - 4;
            } else {
                textY = y + checkerSize + (maxVisiblePieces * visibleOffset) + 12;
            }

            g2.drawString(text, textX, textY);
        }
    }

    private int[] getPointCenter(int displayPoint, int leftX, int rightX, int y, int h, int leftW, int rightW) {
        int pointW = leftW / 6;
        int x;

        if (displayPoint >= 13 && displayPoint <= 18) {
            x = leftX + (displayPoint - 13) * pointW + pointW / 2;
        } else if (displayPoint >= 19 && displayPoint <= 24) {
            x = rightX + (displayPoint - 19) * pointW + pointW / 2;
        } else if (displayPoint >= 7 && displayPoint <= 12) {
            x = leftX + (12 - displayPoint) * pointW + pointW / 2;
        } else {
            x = rightX + (6 - displayPoint) * pointW + pointW / 2;
        }

        return new int[]{x, y + h / 2};
    }

    private void drawChecker(Graphics2D g2, int x, int y, int size, Player owner) {
        if (owner == player1) {
            g2.setColor(orangeChecker);
        } else {
            g2.setColor(greenChecker);
        }

        g2.fillOval(x, y, size, size);

        g2.setColor(new Color(40, 25, 18));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(x, y, size, size);

        g2.setColor(checkerHighlight);
        g2.fillOval(x + size / 5, y + size / 6, size / 3, size / 4);
    }

    private void drawPointNumbers(Graphics2D g2, int leftX, int rightX, int y, int h, int leftW, int rightW) {
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(new Color(235, 220, 190));

        for (int displayPoint = 1; displayPoint <= 24; displayPoint++) {
            int[] center = getPointCenter(displayPoint, leftX, rightX, y, h, leftW, rightW);
            String text = String.valueOf(displayPoint);
            FontMetrics metrics = g2.getFontMetrics();
            int textW = metrics.stringWidth(text);
            boolean topRow = displayPoint >= 13;
            int textY;

            if (topRow) {
                textY = y - 10;
            } else {
                textY = y + h + 20;
            }

            g2.drawString(text, center[0] - textW / 2, textY);
        }
    }
}