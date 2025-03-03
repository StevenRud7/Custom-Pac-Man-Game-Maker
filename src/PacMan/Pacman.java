package PacMan;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;

/**
 * Pacman represents the player's character in the game.
 * It holds the current position of Pacman and provides methods to update
 * and draw Pacman with the correct orientation.
 */
public class Pacman {

    private Point position;

    /**
     * Constructs a Pacman with the specified starting position.
     *
     * @param position the starting position of Pacman.
     */
    public Pacman(Point position) {
        this.position = position;
    }

    /**
     * Returns the current position of Pacman.
     *
     * @return the current Point representing Pacman's position.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Sets Pacman's position to the specified point.
     *
     * @param position the new position for Pacman.
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * Draws Pacman on the provided Graphics context.
     * The drawing orientation depends on the pending movement direction.
     *
     * @param g the Graphics context.
     * @param pendingDirection the key code representing the current movement direction.
     * @param cellSize the size of each cell in the game grid.
     */
    public void draw(Graphics g, int pendingDirection, int cellSize) {
        int x = position.x * cellSize + 1;
        int y = position.y * cellSize + 1;
        g.setColor(Color.YELLOW);
        if (pendingDirection == KeyEvent.VK_RIGHT) {
            g.fillArc(x, y, cellSize, cellSize, 45, 270);
        } else if (pendingDirection == KeyEvent.VK_LEFT) {
            g.fillArc(x, y, cellSize, cellSize, 225, 270);
        } else if (pendingDirection == KeyEvent.VK_UP) {
            g.fillArc(x, y, cellSize, cellSize, 135, 270);
        } else if (pendingDirection == KeyEvent.VK_DOWN) {
            g.fillArc(x, y, cellSize, cellSize, 315, 270);
        } else {
            g.fillArc(x, y, cellSize, cellSize, 45, 270);
        }
    }
}
