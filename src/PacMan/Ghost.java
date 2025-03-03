package PacMan;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import javax.swing.ImageIcon;

/**
 * Ghost represents an enemy in the game.
 * It encapsulates its position and provides methods for movement and drawing.
 * The ghost can chase Pacman using A* pathfinding or move away from him when Pacman is empowered.
 */
public class Ghost {

    private Point position;

    // Static images for ghost rendering
    private static Image ghostImage;
    private static Image blueGhost;

    // Load ghost images once for all instances
    static {
        ghostImage = new ImageIcon(Ghost.class.getResource("/resources/ghost.png")).getImage();
        blueGhost = new ImageIcon(Ghost.class.getResource("/resources/blueghost.png")).getImage();
    }

    /**
     * Constructs a Ghost at the given position.
     *
     * @param position the starting position of the ghost.
     */
    public Ghost(Point position) {
        this.position = position;
    }

    /**
     * Returns the current position of the ghost.
     *
     * @return the ghost's current Point.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Sets the ghost's position.
     *
     * @param position the new position for the ghost.
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * Moves the ghost to the specified cell with wrapping based on board dimensions.
     *
     * @param nextCell the destination cell.
     * @param numCols number of columns on the board.
     * @param numRows number of rows on the board.
     */
    public void moveTo(Point nextCell, int numCols, int numRows) {
        int newX = (nextCell.x + numCols) % numCols;
        int newY = (nextCell.y + numRows) % numRows;
        this.position = new Point(newX, newY);
    }

    /**
     * Finds the next cell for the ghost to move toward Pacman using A* pathfinding.
     *
     * @param pacmanPos the current position of Pacman.
     * @param board the current game board.
     * @return the next cell (Point) for the ghost to move into, or null if no path is found.
     */
    public Point findNextCellTowardsPacman(Point pacmanPos, GameBoard board) {
        int numCols = board.getNumCols();
        int numRows = board.getNumRows();
        PriorityQueue<Cell> openList = new PriorityQueue<>(Comparator.comparingInt(Cell::getTotalCost));
        Set<Cell> closedSet = new HashSet<>();
        Map<Point, Cell> cellMap = new HashMap<>();

        Cell startCell = new Cell(position, null, 0, calculateHeuristic(position, pacmanPos));
        openList.offer(startCell);
        cellMap.put(position, startCell);

        while (!openList.isEmpty()) {
            Cell currentCell = openList.poll();
            closedSet.add(currentCell);

            if (currentCell.point.equals(pacmanPos)) {
                // Backtrack to determine the next step from the ghost's position.
                while (currentCell.parentCell != startCell && currentCell.parentCell != null) {
                    currentCell = currentCell.parentCell;
                }
                return currentCell.point;
            }

            // Explore neighbors (non-diagonal only)
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 && dy != 0) {
                        continue; // Skip diagonal movements
                    }
                    Point nextPoint = new Point(currentCell.point.x + dx, currentCell.point.y + dy);
                    int nextX = (nextPoint.x + numCols) % numCols;
                    int nextY = (nextPoint.y + numRows) % numRows;
                    Point wrappedNextPoint = new Point(nextX, nextY);

                    if (!board.isInBounds(wrappedNextPoint.y, wrappedNextPoint.x) || board.isWall(wrappedNextPoint)) {
                        continue;
                    }

                    int costSoFar = currentCell.costSoFar + 1;
                    int heuristic = calculateHeuristic(wrappedNextPoint, pacmanPos);
                    Cell nextCell = new Cell(wrappedNextPoint, currentCell, costSoFar, heuristic);

                    if (closedSet.contains(nextCell)) {
                        continue;
                    }

                    Cell existingCell = cellMap.get(wrappedNextPoint);
                    if (existingCell == null || costSoFar < existingCell.costSoFar) {
                        if (existingCell != null) {
                            openList.remove(existingCell);
                        }
                        openList.offer(nextCell);
                        cellMap.put(wrappedNextPoint, nextCell);
                    }
                }
            }
        }
        return null; // No path found
    }

    /**
     * Finds the next cell for the ghost to move away from Pacman.
     *
     * @param pacmanPos the current position of Pacman.
     * @param board the current game board.
     * @return the next cell (Point) that maximizes the Manhattan distance from Pacman.
     */
    public Point findNextCellAwayFromPacman(Point pacmanPos, GameBoard board) {
        int numCols = board.getNumCols();
        int numRows = board.getNumRows();
        Point nextCell = null;
        int maxDistance = Integer.MIN_VALUE;

        // Check all non-diagonal neighbors
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 && dy != 0) {
                    continue; // Skip diagonal movements
                }
                Point candidate = new Point(position.x + dx, position.y + dy);
                int nextX = (candidate.x + numCols) % numCols;
                int nextY = (candidate.y + numRows) % numRows;
                Point wrappedCandidate = new Point(nextX, nextY);

                if (!board.isInBounds(wrappedCandidate.y, wrappedCandidate.x) || board.isWall(wrappedCandidate)) {
                    continue;
                }
                int distance = calculateDistance(wrappedCandidate, pacmanPos);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    nextCell = wrappedCandidate;
                }
            }
        }
        return nextCell;
    }

    /**
     * Draws the ghost (normal state) on the provided Graphics context.
     *
     * @param g the Graphics context.
     * @param cellSize the size of each cell.
     */
    public void draw(Graphics g, int cellSize) {
        int x = position.x * cellSize + 1;
        int y = position.y * cellSize + 1;
        g.drawImage(ghostImage, x, y, cellSize, cellSize, null);
    }

    /**
     * Draws the ghost in blue state on the provided Graphics context.
     *
     * @param g the Graphics context.
     * @param cellSize the size of each cell.
     */
    public void drawBlue(Graphics g, int cellSize) {
        int x = position.x * cellSize + 1;
        int y = position.y * cellSize + 1;
        g.drawImage(blueGhost, x, y, cellSize, cellSize, null);
    }

    // --------------------- Helper Methods for Pathfinding ---------------------

    /**
     * Calculates the Manhattan heuristic distance between two points.
     *
     * @param current the current point.
     * @param target the target point.
     * @return the Manhattan distance.
     */
    private int calculateHeuristic(Point current, Point target) {
        return Math.abs(current.x - target.x) + Math.abs(current.y - target.y);
    }

    /**
     * Calculates the Manhattan distance between two points.
     *
     * @param p1 the first point.
     * @param p2 the second point.
     * @return the Manhattan distance.
     */
    private int calculateDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    /**
     * Inner class representing a cell used in the A* search algorithm.
     */
    private class Cell {
        Point point;
        Cell parentCell;
        int costSoFar;
        int heuristic;

        Cell(Point point, Cell parentCell, int costSoFar, int heuristic) {
            this.point = point;
            this.parentCell = parentCell;
            this.costSoFar = costSoFar;
            this.heuristic = heuristic;
        }

        int getTotalCost() {
            return costSoFar + heuristic;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Cell other = (Cell) obj;
            return point.equals(other.point);
        }

        @Override
        public int hashCode() {
            return point.hashCode();
        }
    }
}
