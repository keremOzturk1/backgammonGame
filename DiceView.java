import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

public class DiceView {

    private Rectangle diceArea;
    private Rectangle reverseArea;

    private final Color dieFace = new Color(245, 237, 216);
    private final Color dieBorder = new Color(70, 45, 30);
    private final Color pipColor = new Color(35, 25, 20);
    private final Color glowColor = new Color(255, 210, 95, 95);
    private final Color currentBorder = new Color(255, 220, 125);
    private final Color reverseFill = new Color(120, 75, 45);
    private final Color reverseText = new Color(245, 230, 205);

    public DiceView() {
        diceArea = new Rectangle();
        reverseArea = new Rectangle();
    }

    public void draw(Graphics2D g2, int centerX, int centerY, int leftValue, int rightValue,
                     boolean canRoll, boolean canReverse, boolean isDouble, int remainingMoves) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int dieSize = 48;
        int gap = 20;
        int totalW = (2 * dieSize) + gap;
        int startX = centerX - totalW / 2;
        int diceY = centerY - dieSize / 2;

        diceArea.setBounds(startX - 18, diceY - 20, totalW + 36, dieSize + 58);
        reverseArea.setBounds(centerX - 13, diceY + dieSize / 2 - 13, 26, 26);

        if (canRoll) {
            drawGlow(g2, diceArea.x, diceArea.y, diceArea.width, diceArea.height);
        }

        drawDie(g2, startX, diceY, dieSize, leftValue, !canRoll);
        drawDie(g2, startX + dieSize + gap, diceY, dieSize, rightValue, false);

        if (canRoll) {
            drawRollText(g2, centerX, diceY + dieSize + 28);
        }

        if (canReverse) {
            drawReverseButton(g2);
        }

        if (isDouble && remainingMoves > 0 && !canRoll) {
            drawRemainingMoves(g2, centerX, diceY + dieSize + 28, remainingMoves);
        }
    }

    public boolean isDiceAreaClicked(int x, int y) {
        return diceArea.contains(x, y);
    }

    public boolean isReverseClicked(int x, int y) {
        return reverseArea.contains(x, y);
    }

    private void drawGlow(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(glowColor);
        g2.fillRoundRect(x, y, w, h, 24, 24);

        g2.setColor(new Color(255, 230, 150, 140));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x, y, w, h, 24, 24);
    }

    private void drawDie(Graphics2D g2, int x, int y, int size, int value, boolean isCurrent) {
        g2.setColor(dieFace);
        g2.fillRoundRect(x, y, size, size, 10, 10);

        if (isCurrent) {
            g2.setColor(currentBorder);
            g2.setStroke(new BasicStroke(4));
            g2.drawRoundRect(x - 3, y - 3, size + 6, size + 6, 12, 12);
        }

        g2.setColor(dieBorder);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, size, size, 10, 10);

        if (value >= 1 && value <= 6) {
            drawPips(g2, x, y, size, value);
        }
    }

    private void drawPips(Graphics2D g2, int x, int y, int size, int value) {
        int left = x + size / 4;
        int center = x + size / 2;
        int right = x + (3 * size) / 4;
        int top = y + size / 4;
        int middle = y + size / 2;
        int bottom = y + (3 * size) / 4;
        int pipSize = Math.max(6, size / 7);

        if (value == 1 || value == 3 || value == 5) {
            drawPip(g2, center, middle, pipSize);
        }

        if (value >= 2) {
            drawPip(g2, left, top, pipSize);
            drawPip(g2, right, bottom, pipSize);
        }

        if (value >= 4) {
            drawPip(g2, right, top, pipSize);
            drawPip(g2, left, bottom, pipSize);
        }

        if (value == 6) {
            drawPip(g2, left, middle, pipSize);
            drawPip(g2, right, middle, pipSize);
        }
    }

    private void drawPip(Graphics2D g2, int centerX, int centerY, int size) {
        g2.setColor(pipColor);
        g2.fillOval(centerX - size / 2, centerY - size / 2, size, size);
    }

    private void drawRollText(Graphics2D g2, int centerX, int y) {
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.setColor(new Color(75, 45, 28));
        String text = "ROLL";
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(text, centerX - metrics.stringWidth(text) / 2, y);
    }

    private void drawReverseButton(Graphics2D g2) {
        g2.setColor(reverseFill);
        g2.fillOval(reverseArea.x, reverseArea.y, reverseArea.width, reverseArea.height);

        g2.setColor(new Color(55, 35, 24));
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(reverseArea.x, reverseArea.y, reverseArea.width, reverseArea.height);

        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(reverseText);
        String text = "↔";
        FontMetrics metrics = g2.getFontMetrics();
        int textX = reverseArea.x + reverseArea.width / 2 - metrics.stringWidth(text) / 2;
        int textY = reverseArea.y + reverseArea.height / 2 + metrics.getAscent() / 2 - 3;
        g2.drawString(text, textX, textY);
    }

    private void drawRemainingMoves(Graphics2D g2, int centerX, int y, int remainingMoves) {
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.setColor(new Color(70, 45, 30));
        String text = "Remaining moves: " + remainingMoves;
        FontMetrics metrics = g2.getFontMetrics();
        g2.drawString(text, centerX - metrics.stringWidth(text) / 2, y);
    }
}