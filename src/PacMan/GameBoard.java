package PacMan;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * GameBoard handles the map layout and all elements on the board including walls,
 * pellets, power pellets, and the ghost respawn point. It also provides methods for
 * drawing the grid and the board elements.
 */
public class GameBoard {

    public static final int PACMAN_SPEED = 100;
    public static final int GHOST_SPEED = 160;

    private int numRows;
    private int numCols;
    private ArrayList<Point> walls;
    private ArrayList<Point> pellets;
    private ArrayList<Point> power;
    private Point ghostRespawnPoint;
    private Point pacmanStart;
    private ArrayList<Point> ghostPositions;

    private Image wallImage;
    private Image pelletImage;
    private Image powerImage;
    private Image ghostRespawnImage;

    // Observer used during drawing (typically the PacmanGame panel)
    private JPanel observer;

    /**
     * Constructs a GameBoard with default dimensions and loads resources.
     *
     * @param observer the component used as the image observer during drawing.
     */
    public GameBoard(JPanel observer) {
        this.observer = observer;
        // Default dimensions before map is loaded
        numRows = 15;
        numCols = 15;
        walls = new ArrayList<>();
        pellets = new ArrayList<>();
        power = new ArrayList<>();
        ghostPositions = new ArrayList<>();
        ghostRespawnPoint = new Point(0, 0);
        pacmanStart = null;

        // Load images from resources
        wallImage = new ImageIcon(getClass().getResource("/resources/wall.png")).getImage();
        pelletImage = new ImageIcon(getClass().getResource("/resources/pellet.png")).getImage();
        powerImage = new ImageIcon(getClass().getResource("/resources/power.png")).getImage();
        ghostRespawnImage = new ImageIcon(getClass().getResource("/resources/respawn.png")).getImage();
    }

    // --------------------- Dimension & State Methods ---------------------

    /**
     * Returns the number of rows on the board.
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * Returns the number of columns on the board.
     */
    public int getNumCols() {
        return numCols;
    }
    
    public void removePellet(Point cell) {
        pellets.remove(cell);
    }

    public void removePower(Point cell) {
        power.remove(cell);
    }


    /**
     * Sets new dimensions for the board.
     *
     * @param rows the new number of rows.
     * @param cols the new number of columns.
     */
    public void setDimensions(int rows, int cols) {
        this.numRows = rows;
        this.numCols = cols;
    }

    /**
     * Checks if the given row and column are within the board bounds.
     *
     * @param row the row index.
     * @param col the column index.
     * @return true if in bounds, false otherwise.
     */
    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < numRows && col >= 0 && col < numCols;
    }

    /**
     * Checks if a cell is occupied by a wall, pellet, or power pellet.
     *
     * @param cell the cell to check.
     * @return true if occupied, false otherwise.
     */
    public boolean isOccupied(Point cell) {
        return walls.contains(cell) || pellets.contains(cell) || power.contains(cell);
    }

    /**
     * Checks if the cell contains a wall.
     *
     * @param cell the cell to check.
     * @return true if it's a wall, false otherwise.
     */
    public boolean isWall(Point cell) {
        return walls.contains(cell);
    }

    /**
     * Checks if the cell contains a pellet.
     *
     * @param cell the cell to check.
     * @return true if it's a pellet, false otherwise.
     */
    public boolean isPellet(Point cell) {
        return pellets.contains(cell);
    }

    /**
     * Checks if the cell contains a power pellet.
     *
     * @param cell the cell to check.
     * @return true if it's a power pellet, false otherwise.
     */
    public boolean isPower(Point cell) {
        return power.contains(cell);
    }

    /**
     * Adds a wall at the specified cell.
     *
     * @param cell the cell to add a wall.
     */
    public void addWall(Point cell) {
        walls.add(cell);
    }

    /**
     * Adds a pellet at the specified cell.
     *
     * @param cell the cell to add a pellet.
     */
    public void addPellet(Point cell) {
        pellets.add(cell);
    }

    /**
     * Adds a power pellet at the specified cell.
     *
     * @param cell the cell to add a power pellet.
     */
    public void addPower(Point cell) {
        power.add(cell);
    }

    /**
     * Sets the ghost respawn point to the specified cell.
     *
     * @param cell the new ghost respawn point.
     */
    public void setGhostRespawnPoint(Point cell) {
        ghostRespawnPoint = cell;
    }

    /**
     * Removes any element (wall, pellet, or power pellet) at the specified cell.
     *
     * @param cell the cell from which to remove elements.
     */
    public void removeElement(Point cell) {
        walls.remove(cell);
        pellets.remove(cell);
        power.remove(cell);
    }

    /**
     * Clears all board elements.
     */
    public void clear() {
        walls.clear();
        pellets.clear();
        power.clear();
        pacmanStart = null;
        ghostPositions.clear();
    }

    /**
     * Creates the default map layout.
     * Sets dimensions, clears existing elements, and parses the default map layout.
     */
    public void createDefaultMap() {
        clear();
        // Set default dimensions for the map
        numRows = 20;
        numCols = 28;
        // Define the map layout (each character represents an element)
        String[] mapLayout = {
            "############################",
            "#............##............#",
            "#O####.#####.##.#####.####O#",
            "#.####.#####.##.#####.####.#",
            "#..........................#",
            "#.####.##.########.##.####.#",
            "#......##....##....##......#",
            "######.#####.##.#####.######",
            "######.##..........##.######",
            "######.##.##....##.##.######",
            ".......#....GRG.....#.......",
            "######.##.##....##.##.######",
            "######.##..........##.######",
            "######.##.########.##.######",
            "#............##............#",
            "#.####.#####.##.#####.####.#",
            "#.####.#####.##.#####.####.#",
            "#O..##.......P........##..O#",
            "#.##########.##.##########.#",
            "#..........................#",
            "############################"
        };

        // Parse the map layout and add elements accordingly
        for (int row = 0; row < numRows + 1; row++) {
            String line = mapLayout[row];
            for (int col = 0; col < numCols; col++) {
                char element = line.charAt(col);
                Point p = new Point(col, row);
                if (element == '#') {
                    walls.add(p);
                } else if (element == '.') {
                    pellets.add(p);
                } else if (element == 'P') {
                    pacmanStart = p;
                } else if (element == 'G') {
                    ghostPositions.add(p);
                } else if (element == 'O') {
                    power.add(p);
                } else if (element == 'R') {
                    ghostRespawnPoint = p;
                }
            }
        }
    }

    /**
     * Returns the starting position for Pacman as defined in the map.
     *
     * @return the starting Point for Pacman.
     */
    public Point getPacmanStart() {
        return pacmanStart;
    }

    /**
     * Returns the list of ghost positions as defined in the map.
     *
     * @return a list of Points representing ghost positions.
     */
    public List<Point> getGhostPositions() {
        return ghostPositions;
    }

    /**
     * Returns the ghost respawn point.
     *
     * @return the Point of the ghost respawn.
     */
    public Point getGhostRespawnPoint() {
        return ghostRespawnPoint;
    }

    /**
     * Checks if there are no pellets or power pellets left on the board.
     *
     * @return true if no pellets or power pellets remain, false otherwise.
     */
    public boolean noPelletsLeft() {
        return pellets.isEmpty() && power.isEmpty();
    }

    // --------------------- Drawing Methods ---------------------

    /**
     * Draws the grid lines on the board.
     *
     * @param g the Graphics context.
     */
    public void drawGrid(Graphics g) {
        g.setColor(Color.GRAY);
        int cellSize = PacmanGame.CELL_SIZE;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int x = col * cellSize + 1;
                int y = row * cellSize + 1;
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }

    /**
     * Draws the walls on the board.
     *
     * @param g the Graphics context.
     */
    public void drawWalls(Graphics g) {
        int cellSize = PacmanGame.CELL_SIZE;
        for (Point wall : walls) {
            int x = wall.x * cellSize + 1;
            int y = wall.y * cellSize + 1;
            g.drawImage(wallImage, x, y, cellSize, cellSize, observer);
        }
    }

    /**
     * Draws the pellets on the board.
     *
     * @param g the Graphics context.
     */
    public void drawPellets(Graphics g) {
        int cellSize = PacmanGame.CELL_SIZE;
        for (Point pellet : pellets) {
            int x = pellet.x * cellSize + cellSize / 2 + 1;
            int y = pellet.y * cellSize + cellSize / 2 + 1;
            g.drawImage(pelletImage, x - 5, y - 5, 10, 10, observer);
        }
    }

    /**
     * Draws the power pellets on the board.
     *
     * @param g the Graphics context.
     */
    public void drawPower(Graphics g) {
        int cellSize = PacmanGame.CELL_SIZE;
        for (Point pow : power) {
            int x = pow.x * cellSize + cellSize / 2 + 1;
            int y = pow.y * cellSize + cellSize / 2 + 1;
            g.drawImage(powerImage, x - cellSize / 4, y - cellSize / 4, cellSize / 2, cellSize / 2, observer);
        }
    }

    /**
     * Draws the ghost respawn point on the board.
     *
     * @param g the Graphics context.
     */
    public void drawGhostRespawn(Graphics g) {
        if (ghostRespawnPoint != null) {
            int cellSize = PacmanGame.CELL_SIZE;
            int x = ghostRespawnPoint.x * cellSize + 1;
            int y = ghostRespawnPoint.y * cellSize + 1;
            g.drawImage(ghostRespawnImage, x, y, cellSize, cellSize, observer);
        }
    }
}
