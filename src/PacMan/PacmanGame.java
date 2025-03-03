package PacMan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * PacmanGame is the main game controller which extends JPanel.
 * It ties together the game board, the Pacman character, ghost entities,
 * and the UI components. All game logic (movement, collision detection,
 * game loop, etc.) is coordinated here while delegating drawing and UI
 * functionality to the other classes.
 */
public class PacmanGame extends JPanel {

    public static final int CELL_SIZE = 30; // Size of each cell in pixels

    // Game state variables
    private boolean gameRunning;
    private Timer pacmanTimer;
    private Timer ghostTimer;
    private int pacmanDirection;
    private int pendingPacmanDirection;
    private int score;
    private int lives;
    private boolean isBlue;

    // Components from other classes
    private GameBoard board;          // Handles grid, walls, pellets, power pellets, ghost respawn point, etc.
    private Pacman pacman;            // Represents the Pacman character
    private ArrayList<Ghost> ghosts;  // Holds all ghost entities
    private GameUI ui;                // Handles UI elements such as buttons, labels, and dialogs

    // Reference to the main frame
    public static JFrame frame;

    public PacmanGame() {
        // Initialize game state variables
        gameRunning = false;
        pacmanDirection = 0;
        pendingPacmanDirection = 0;
        score = 0;
        lives = 3; // Default lives value
        isBlue = false;

        // Initialize the GameBoard (which holds the map and related elements)
        board = new GameBoard(this);

        // Initialize Pacman (only one Pacman is used)
        pacman = new Pacman(new Point(0, 0));

        // Initialize ghost list
        ghosts = new ArrayList<>();

        // Initialize the GameUI, passing a reference to this game controller.
        ui = new GameUI(this);

        // Set up the layout. Add the UI's button panel to the SOUTH.
        setLayout(new BorderLayout());
        add(ui.getButtonPanel(), BorderLayout.SOUTH);
        add(ui.getGameOverLabel());
        add(ui.getWinLabel());

        // Add mouse listener for map editing (placing and removing elements)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameRunning) {
                    int row = e.getY() / CELL_SIZE;
                    int col = e.getX() / CELL_SIZE;
                    Point cell = new Point(col, row);
                    if (e.getButton() == MouseEvent.BUTTON1) { // Left-click
                        if (!board.isOccupied(cell) && board.isInBounds(row, col)) {
                            // Depending on the selected element from the UI, add the element.
                            switch (ui.getSelectedElementIndex()) {
                                case 0: // Pacman
                                    pacman.setPosition(cell);
                                    break;
                                case 1: // Ghost
                                    ghosts.add(new Ghost(cell));
                                    break;
                                case 2: // Wall
                                    board.addWall(cell);
                                    break;
                                case 3: // Pellet
                                    board.addPellet(cell);
                                    break;
                                case 4: // Power Pellet
                                    board.addPower(cell);
                                    break;
                                case 5: // Ghost Respawn Point
                                    board.setGhostRespawnPoint(cell);
                                    break;
                            }
                            repaint();
                        }
                    } else if (e.getButton() == MouseEvent.BUTTON3) { // Right-click
                        if (board.isInBounds(row, col)) {
                            board.removeElement(cell);
                            // Remove Pacman if placed at this cell
                            if (pacman.getPosition().equals(cell)) {
                                pacman.setPosition(new Point(-1, -1));
                            }
                            // Remove any ghost present at this cell
                            for (Iterator<Ghost> it = ghosts.iterator(); it.hasNext();) {
                                Ghost g = it.next();
                                if (g.getPosition().equals(cell)) {
                                    it.remove();
                                }
                            }
                            ui.enablePlayButton(true);
                            ui.setPlayButtonText("Play Now");
                            gameRunning = false;
                            repaint();
                        }
                    }
                }
            }
        });

        // Add key listener to capture Pacman movement commands
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameRunning) {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                        pendingPacmanDirection = KeyEvent.VK_LEFT;
                    } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                        pendingPacmanDirection = KeyEvent.VK_RIGHT;
                    } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) {
                        pendingPacmanDirection = KeyEvent.VK_UP;
                    } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) {
                        pendingPacmanDirection = KeyEvent.VK_DOWN;
                    }
                }
            }
        });
        setFocusable(true);
    }

    // --------------------- UI & Map Methods ---------------------

    /**
     * Opens a dialog to allow the user to resize the game map.
     * The new row and column values are validated and applied.
     */
    public void openResizeDialog() {
        // Create a dialog for inputting the new size
        javax.swing.JDialog resizeDialog = new javax.swing.JDialog(frame, "Resize Game", true);
        resizeDialog.setLayout(new BorderLayout());

        // Create labels and text fields for row and column input
        javax.swing.JLabel rowLabel = new javax.swing.JLabel("Number of Rows (From 5 - 25):");
        javax.swing.JTextField rowTextField = new javax.swing.JTextField(Integer.toString(board.getNumRows()), 10);

        javax.swing.JLabel colLabel = new javax.swing.JLabel("Number of Columns (From 5 - 30):");
        javax.swing.JTextField colTextField = new javax.swing.JTextField(Integer.toString(board.getNumCols()), 10);

        // Create the OK button to apply the new size
        javax.swing.JButton okButton = new javax.swing.JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Retrieve the new row and column values
                int newRow = Integer.parseInt(rowTextField.getText());
                int newCol = Integer.parseInt(colTextField.getText());

                // Validate the new values
                if (newRow > 4 && newCol > 4 && newRow <= 25 && newCol <= 30) {
                    // Update the game size
                    board.setDimensions(newRow, newCol);
                    frame.setSize(CELL_SIZE * board.getNumCols() + 17, CELL_SIZE * board.getNumRows() + 140);
                    frame.setLocationRelativeTo(null);
                    resetGame();
                    repaint();
                    // Close the dialog
                    resizeDialog.dispose();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(resizeDialog, "Invalid input. Please enter the values within range for rows and columns.");
                }
            }
        });

        // Add the components to the dialog
        javax.swing.JPanel inputPanel = new javax.swing.JPanel();
        inputPanel.setLayout(new java.awt.GridLayout(2, 2));
        inputPanel.add(rowLabel);
        inputPanel.add(rowTextField);
        inputPanel.add(colLabel);
        inputPanel.add(colTextField);

        resizeDialog.add(inputPanel, BorderLayout.CENTER);
        resizeDialog.add(okButton, BorderLayout.SOUTH);

        // Set the size and visibility of the resize dialog
        resizeDialog.pack();
        resizeDialog.setLocationRelativeTo(frame);
        resizeDialog.setVisible(true);
    }

    /**
     * Clears the map elements.
     */
    public void clearMap() {
        board.clear();
        ghosts.clear();
        repaint();
    }

    /**
     * Loads the default map layout.
     */
    public void createMap() {
        board.createDefaultMap();
        // Update Pacman's position if a Pacman marker was defined in the map
        Point pacStart = board.getPacmanStart();
        if (pacStart != null) {
            pacman.setPosition(pacStart);
        }
        // Load ghost positions from the map
        List<Point> ghostPoints = board.getGhostPositions();
        ghosts.clear();
        for (Point p : ghostPoints) {
            ghosts.add(new Ghost(p));
        }
        // *** FIX: Auto-resize the window to fit the map size ***
        frame.setSize(CELL_SIZE * board.getNumCols() + 17, CELL_SIZE * board.getNumRows() + 140);
        frame.setLocationRelativeTo(null);
        repaint();
    }

    // --------------------- Game Logic Methods ---------------------

    /**
     * Moves Pacman one cell in the direction indicated by pendingPacmanDirection.
     */
    public void movePacman() {
        Point current = pacman.getPosition();
        int dx = getDX();
        int dy = getDY();
        int nextX = (current.x + dx + board.getNumCols()) % board.getNumCols();
        int nextY = (current.y + dy + board.getNumRows()) % board.getNumRows();
        Point nextCell = new Point(nextX, nextY);
        if (board.isInBounds(nextCell.y, nextCell.x) && !board.isWall(nextCell)) {
            pacman.setPosition(nextCell);
            if (board.isPellet(nextCell)) {
                board.removePellet(nextCell);
                score += 10;
                ui.updateScore(score);
                if (board.noPelletsLeft()) {
                    stopGameLoop();
                    ui.showWin(); 
                    repaint();
                }
            }
            if (board.isPower(nextCell)) {
                board.removePower(nextCell);
                score += 20;
                ui.updateScore(score);
                if (board.noPelletsLeft()) {
                    stopGameLoop();
                    ui.showWin();
                    repaint();
                }
                isBlue = true;
            }
        }
    }

    /**
     * Moves the ghosts toward or away from Pacman based on the current game state.
     * Uses a helper method to avoid merging ghosts.
     */
    public void moveGhosts() {
        if (isBlue) {
            // Ghosts run away from Pacman when blue
            for (Ghost ghost : ghosts) {
                Point nextCell = ghost.findNextCellAwayFromPacman(pacman.getPosition(), board);
                if (nextCell != null && !isGhostCollisionExcluding(ghost, nextCell)) {
                    ghost.moveTo(nextCell, board.getNumCols(), board.getNumRows());
                }
            }
        } else {
            // Ghosts chase Pacman
            for (Ghost ghost : ghosts) {
                Point nextCell = ghost.findNextCellTowardsPacman(pacman.getPosition(), board);
                if (nextCell != null && !isGhostCollisionExcluding(ghost, nextCell)) {
                    ghost.moveTo(nextCell, board.getNumCols(), board.getNumRows());
                }
            }
        }
    }

    // Helper method to prevent ghost merging: checks if any other ghost already occupies nextCell.
    private boolean isGhostCollisionExcluding(Ghost current, Point nextCell) {
        for (Ghost g : ghosts) {
            if (g != current && g.getPosition().equals(nextCell)) {
                return true;
            }
        }
        return false;
    }

    private int getDX() {
        if (pendingPacmanDirection == KeyEvent.VK_LEFT || pendingPacmanDirection == KeyEvent.VK_A) {
            return -1;
        } else if (pendingPacmanDirection == KeyEvent.VK_RIGHT || pendingPacmanDirection == KeyEvent.VK_D) {
            return 1;
        } else {
            return 0;
        }
    }

    private int getDY() {
        if (pendingPacmanDirection == KeyEvent.VK_UP || pendingPacmanDirection == KeyEvent.VK_W) {
            return -1;
        } else if (pendingPacmanDirection == KeyEvent.VK_DOWN || pendingPacmanDirection == KeyEvent.VK_S) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Starts the game loop timers for Pacman and ghost movement.
     */
    public void startGameLoop() {
        pacmanTimer = new Timer(GameBoard.PACMAN_SPEED, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePacman();
                pacmanDirection = pendingPacmanDirection;
                repaint();
            }
        });
        pacmanTimer.start();

        ghostTimer = new Timer(GameBoard.GHOST_SPEED, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveGhosts();
                repaint();
            }
        });
        ghostTimer.start();

        // Start a timer to change the ghost color back to red after 8 seconds
        Timer blueTimer = new Timer(8000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBlue = false;
                repaint();
            }
        });
        blueTimer.start();
    }

    /**
     * Stops the game loop timers.
     */
    public void stopGameLoop() {
        if (pacmanTimer != null && pacmanTimer.isRunning()) {
            pacmanTimer.stop();
        }
        if (ghostTimer != null && ghostTimer.isRunning()) {
            ghostTimer.stop();
        }
    }

    /**
     * Resets the game to its initial state and forces an immediate repaint to clear the board.
     */
    public void resetGame() {
        board.clear();
        ghosts.clear();
        pacman.setPosition(new Point(-1, -1));
        score = 0;
        ui.updateScore(score);
        gameRunning = false;
        repaint();
    }

    /**
     * Checks for collisions between Pacman and ghosts.
     * If Pacman is not blue and collides with a ghost, a game over occurs.
     * If Pacman is blue and collides with a ghost, that ghost is removed and respawned.
     */
    private boolean isCollision() {
        Point pPos = pacman.getPosition();
        for (Ghost ghost : ghosts) {
            if (pPos.equals(ghost.getPosition()) && !isBlue) {
                return true;
            } else if (pPos.equals(ghost.getPosition()) && isBlue) {
                ghosts.remove(ghost);
                scheduleGhostRespawn();
                return false;
            }
        }
        return false;
    }

    /**
     * Schedules the ghost respawn timer.
     */
    private void scheduleGhostRespawn() {
        Timer spawnTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spawnGhost(board.getGhostRespawnPoint());
                repaint();
            }
        });
        spawnTimer.setRepeats(false);
        spawnTimer.start();
    }

    /**
     * Spawns a new ghost at the ghost respawn point.
     */
    private void spawnGhost(Point respawnPoint) {
        ghosts.add(new Ghost(respawnPoint));
    }

    // --------------------- Rendering ---------------------

    /**
     * Overridden paintComponent to draw the game grid, walls, pellets, Pacman, ghosts, etc.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK);
        board.drawGrid(g);
        board.drawWalls(g);
        board.drawPellets(g);
        pacman.draw(g, pendingPacmanDirection, CELL_SIZE);
        board.drawGhostRespawn(g);
        if (isBlue) {
            for (Ghost ghost : ghosts) {
                ghost.drawBlue(g, CELL_SIZE);
            }
        } else {
            for (Ghost ghost : ghosts) {
                ghost.draw(g, CELL_SIZE);
            }
        }
        board.drawPower(g);
        // Reposition labels on each repaint (fix for win text glitch)
        ui.positionLabels(this.getWidth(), this.getHeight());
        if (gameRunning && isCollision()) {
            stopGameLoop();
            ui.showGameOver();
        }
    }

    // --------------------- Getters & Setters ---------------------

    public void setGameRunning(boolean running) {
        this.gameRunning = running;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public GameBoard getBoard() {
        return board;
    }

    public Pacman getPacman() {
        return pacman;
    }

    public ArrayList<Ghost> getGhosts() {
        return ghosts;
    }

    /**
     * Renamed from getUI() to avoid overriding JPanel.getUI()
     */
    public GameUI getGameUI() {
        return ui;
    }

    // --------------------- Main Method ---------------------

    public static void main(String[] args) {
        frame = new JFrame("Custom Pacman Game");
        PacmanGame game = new PacmanGame();
        frame.getContentPane().add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(CELL_SIZE * game.board.getNumCols() + 17, CELL_SIZE * game.board.getNumRows() + 140);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
