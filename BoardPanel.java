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

public class BoardPanel extends JPanel {

    private Board board;
    private Player player1;
    private Player player2;

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
    }

    public void setGameData(Board board, Player player1, Player player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        repaint();
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
        drawPockets(g2, boardX + frameThickness, innerY, pocketWidth, innerH, boardX + boardW - frameThickness - pocketWidth);
        drawHinges(g2, barX, boardY, barWidth, boardH);

        int pointHeight = innerH / 2 - 16;
        drawPoints(g2, leftX, rightX, innerY, innerH, leftHalfW, rightHalfW, pointHeight);
        drawCheckers(g2, leftX, rightX, innerY, innerH, leftHalfW, rightHalfW);
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

        g2.setColor(new Color(48, 25, 16));
        int slotH = 24;
        int gap = 18;
        int startY = y + 70;

        for (int i = 0; i < 6; i++) {
            int slotY = startY + i * (slotH + gap);
            if (slotY + slotH < y + h - 25) {
                g2.fillRoundRect(x + 9, slotY, w - 18, slotH, 8, 8);
            }
        }
    }

    private void drawHinges(Graphics2D g2, int barX, int boardY, int barWidth, int boardH) {
        int hingeW = barWidth - 6;
        int hingeH = 54;
        int hingeX = barX + 3;
        int centerY = boardY + boardH / 2;
        int gapFromCenter = 58;

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