package PacMan;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.*;
import java.util.PriorityQueue;
import java.awt.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.JOptionPane;


public class PacmanGame extends JPanel {

    private static final int CELL_SIZE = 30; // Size of each cell in pixels
    private static int NUM_ROWS = 15; // Number of rows in the map //20
    private static int NUM_COLS = 15; // Number of columns in the map //28
    private static final int PACMAN_SPEED = 100; // Delay between Pacman movements (in milliseconds)
    private static final int GHOST_SPEED = 160; // Delay between Ghost movements (in milliseconds)

    private ArrayList<Point> walls; // List of wall positions
    private ArrayList<Point> pellets; // List of pellet positions
    private ArrayList<Point> power; // List of pellet positions
    private ArrayList<Point> pacmans; // List of pacman positions
    private ArrayList<Point> ghosts; // List of ghost positions
    private Point ghostRespawnPoint;

    private JButton playButton;
    private JButton playAgainButton;
    private JButton loadMap;
    private JComboBox<String> elementSelector;
    private JLabel gameOverLabel;
    private JLabel scoreLabel;
    private JLabel winLabel;
    private JLabel livesLabel;
    private boolean gameRunning;
    private Timer pacmanTimer;
    private Timer ghostTimer;
    private int pacmanDirection;
    private int pendingPacmanDirection;
    private int score;
    private int lives;
    private boolean isBlue;
    private Image ghostImage;
    private Image blueGhost;
    private Image wallImage;
    private Image ghostRespawn;
    private Image pelletImage;
    private Image powerImage;
    static JFrame frame = new JFrame("Custom Pacman Game");


    public PacmanGame() {
        walls = new ArrayList<>();
        pellets = new ArrayList<>();
        pacmans = new ArrayList<>();
        ghosts = new ArrayList<>();
        power = new ArrayList<>();
        ghostRespawnPoint = new Point(); // Change the coordinates to your desired respawn point
        ghostImage = new ImageIcon(getClass().getResource("/resources/ghost.png")).getImage();
        blueGhost = new ImageIcon(getClass().getResource("/resources/blueghost.png")).getImage();
        wallImage = new ImageIcon(getClass().getResource("/resources/wall.png")).getImage();
        ghostRespawn = new ImageIcon(getClass().getResource("/resources/respawn.png")).getImage();
        pelletImage = new ImageIcon(getClass().getResource("/resources/pellet.png")).getImage();
        powerImage = new ImageIcon(getClass().getResource("/resources/power.png")).getImage();


        
        
        playButton = new JButton("Play Now");
        playButton.setFont(new Font("Arial", Font.BOLD, 16));
        playButton.setBackground(Color.YELLOW);
        playButton.setForeground(Color.BLACK);
        playButton.setOpaque(true);
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);
        add(playButton);

        playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(new Font("Arial", Font.BOLD, 16));
        playAgainButton.setBackground(Color.YELLOW);
        playAgainButton.setForeground(Color.BLACK);
        playAgainButton.setOpaque(true);
        playAgainButton.setBorderPainted(false);
        playAgainButton.setFocusPainted(false);
        playAgainButton.setVisible(false);
        add(playAgainButton);
        
        
        loadMap = new JButton("Load Default Map");
        loadMap.setFont(new Font("Arial", Font.BOLD, 16));
        loadMap.setBackground(Color.BLUE);
        loadMap.setForeground(Color.YELLOW);
        loadMap.setOpaque(true);
        loadMap.setBorderPainted(false);
        loadMap.setFocusPainted(false);
        loadMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createMap();
                repaint();
            }
        });
        add(loadMap);
        

        elementSelector = new JComboBox<>(new String[]{"Pacman", "Ghost", "Wall", "Pellet", "Power Pellet", "Ghost Respawn Point"});
        elementSelector.setFont(new Font("Arial", Font.BOLD, 14));
        elementSelector.setForeground(Color.BLACK);
        elementSelector.setBackground(Color.WHITE);
        elementSelector.setOpaque(true);
        elementSelector.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(elementSelector);

        
        gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(new Font("Showcard Gothic", Font.BOLD, 30));
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setVisible(false);
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setVerticalAlignment(SwingConstants.CENTER);
        add(gameOverLabel);

        winLabel = new JLabel("You Win!");
        winLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        winLabel.setForeground(Color.GREEN);
        winLabel.setVisible(false);
        winLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winLabel.setVerticalAlignment(SwingConstants.CENTER);
        add(winLabel);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 20));
        scoreLabel.setForeground(Color.WHITE);
        add(scoreLabel);
        
        JButton resizeButton = new JButton("Resize");
        resizeButton.setFont(new Font("Arial", Font.BOLD, 16));
        resizeButton.addActionListener(e -> openResizeDialog(frame));
        resizeButton.setBackground(Color.GRAY);
        resizeButton.setForeground(Color.CYAN);
        add(resizeButton);
        
        JButton clearButton = new JButton("Clear Board");
        clearButton.setFont(new Font("Arial", Font.BOLD, 15));
        clearButton.addActionListener(e -> clearMap(frame));
        clearButton.setBackground(Color.RED);
        clearButton.setForeground(Color.BLACK);
        add(clearButton);
        
 
     // Create the info button
        JButton infoButton = new JButton("i");
        infoButton.setFont(new Font("Times New Roman", Font.ITALIC, 22));
        infoButton.setPreferredSize(new Dimension(40, 40));
        //infoButton.setFocusPainted(false);
        infoButton.setBackground(Color.LIGHT_GRAY);
        infoButton.setForeground(Color.BLACK);
        

        // Create the info window
        JDialog infoWindow = new JDialog();
        infoWindow.setTitle("How to Play");
        infoWindow.setSize(500, 600);
        infoWindow.setLocationRelativeTo(null); // Center the window

        String infoText = "<html><body>" +
                "<h2>How to Play</h2>" +
                "<ul>" +
                "<li>Move the Pacman using the arrow keys or WASD.</li>" +
                "<li>Eat all the pellets and power pellets to win the level.</li>" +
                "<li>Avoid the ghosts, or it will be game over.</li>" +
                "<li>Power pellets allow you to eat ghosts temporarily. Once they are eaten they will respawn at the ghost respawn point.</li>" +
                "</ul>" +
                "<h2>Building the Map</h2>" +
                "<ul>" +
                "<li>Select an element and left click to place it down. You must place at Pacman and at least one ghost and pellet to start playing.</li>" +
                "<li>Right click over an element to delete it.</li>" +
                "<li>The Load Default Map button loads a provided balanced map to play on.</li>" +
                "<li>The Resize button allows you to resize the map to whichever size you prefer within the given parameters.</li>" +
                "<li>The Clear Board button clears every element on the board.</li>" +
                "</ul>" +
                "<h2>The Elements</h2>" +
                "<ul>" +
                "<li>Pacman: This is what you will be playing as and only one can be placed.</li>" +
                "<li>Ghost: They will be chasing you down and them you must avoid unless they are blue in which case they can be eaten. You can place as many Ghosts as you want.</li>" +
                "<li>Pellets: They add 10 points to your score and you must eat all of them to win the game.</li>" +
                "<li>Power Pellets: They add 20 points to your score and must also be eaten to win the game. They also turn the ghosts blue for a few seconds making them edible by Pacman.</li>" +
                "<li>Ghost Respawn Point: Place this wherever you want the Ghosts to respawn when they are eaten. It is by default placed in the top left corner.</li>" +
                "</ul>" +
                "</body></html>";

        JLabel infoLabel = new JLabel(infoText);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setVerticalAlignment(SwingConstants.CENTER);
        infoWindow.add(infoLabel);

        // Add action listener to the info button
        infoButton.addActionListener(e -> {
            infoWindow.setVisible(true);
        });
        
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pacmans.size() > 0 && ghosts.size() > 0 && pellets.size() > 0) {  //&& pellets.size() > 0
                    gameRunning = true;
                    playButton.setEnabled(false);
                    clearButton.setEnabled(false);
                    resizeButton.setEnabled(false);
                    loadMap.setEnabled(false);
                    infoButton.setEnabled(false);
                    playButton.setText("Game in progress...");
                    requestFocusInWindow();
                    startGameLoop();
                } else {
                    JOptionPane.showMessageDialog(PacmanGame.this, "Place at least one Pacman, Ghost, and Pellet to Play!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        playAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
                playButton.setEnabled(true);
                clearButton.setEnabled(true);
                resizeButton.setEnabled(true);
                loadMap.setEnabled(true);
                infoButton.setEnabled(true);
                playButton.setText("Play Now");
                playAgainButton.setVisible(false);
                gameOverLabel.setVisible(false);
                winLabel.setVisible(false);
                requestFocusInWindow();
            }
        });
        
        JPanel buttonPanel1 = new JPanel();
        buttonPanel1.setLayout(new BoxLayout(buttonPanel1, BoxLayout.Y_AXIS));
        buttonPanel1.add(playButton);
        buttonPanel1.add(elementSelector);
        buttonPanel1.add(clearButton);
        buttonPanel1.setBackground(Color.BLACK);
        
        // Create a panel for Score, Load Default Map, and Resize buttons
        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.setLayout(new BoxLayout(buttonPanel2, BoxLayout.Y_AXIS));
        buttonPanel2.add(scoreLabel);
        buttonPanel2.add(loadMap);
        buttonPanel2.add(resizeButton);
        buttonPanel2.setBackground(Color.BLACK);

        // Create a panel for the info button
        JPanel infoButtonPanel = new JPanel();
        infoButtonPanel.setLayout(new BoxLayout(infoButtonPanel, BoxLayout.Y_AXIS));
        infoButtonPanel.add(infoButton);
        infoButtonPanel.setBackground(Color.BLACK);

        // Create a panel for the bottom buttons
        JPanel bottomButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 5, 0, 0); // Add some spacing

        // Add buttonPanel1 to bottomButtonPanel
        bottomButtonPanel.add(buttonPanel1, gbc);

        gbc.gridx = 1;

        // Add buttonPanel2 to bottomButtonPanel
        bottomButtonPanel.add(buttonPanel2, gbc);

        gbc.gridx = 2;

        // Add infoButtonPanel to bottomButtonPanel
        bottomButtonPanel.add(infoButtonPanel, gbc);
        bottomButtonPanel.setBackground(Color.BLACK);

        // Add bottomButtonPanel to the main panel
        setLayout(new BorderLayout());
        add(bottomButtonPanel, BorderLayout.SOUTH);
        

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!gameRunning) {
                    int row = e.getY() / CELL_SIZE;
                    int col = e.getX() / CELL_SIZE;
                    Point cell = new Point(col, row);
                    if (e.getButton() == MouseEvent.BUTTON1) { // Left-click
                        if (!isOccupied(cell) && isInBounds(row,col)) {
                            if (elementSelector.getSelectedIndex() == 0) { // Pacman
                                pacmans.clear();
                                pacmans.add(cell);
                            } else if (elementSelector.getSelectedIndex() == 1) { // Ghost
                                ghosts.add(cell);
                            } else if (elementSelector.getSelectedIndex() == 2) { // Wall
                                walls.add(cell);
                            } else if (elementSelector.getSelectedIndex() == 3) { // Pellet
                                pellets.add(cell);
                            } else if (elementSelector.getSelectedIndex() == 4) { // Power Pellet
	                            power.add(cell);
	                        } else if (elementSelector.getSelectedIndex() == 5) { // Ghost Respawn Point
	                            ghostRespawnPoint = cell;
	                        }
                            repaint();
                        }
                    } else if (e.getButton() == MouseEvent.BUTTON3) { // Right-click
                        if (isInBounds(row, col)) {
                            walls.remove(cell);
                            pellets.remove(cell);
                            pacmans.remove(cell);
                            ghosts.remove(cell);
                            power.remove(cell);
                            playButton.setEnabled(true);
                            playButton.setText("Play Now");
                            gameRunning = false;
                            repaint();
                        }
                    }
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameRunning) {
                    int key = e.getKeyCode();
                    if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) { // && pacmanDirection != KeyEvent.VK_RIGHT
                        pendingPacmanDirection = KeyEvent.VK_LEFT;
                    } else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) { //&& pacmanDirection != KeyEvent.VK_LEFT
                        pendingPacmanDirection = KeyEvent.VK_RIGHT;
                    } else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) { //&& pacmanDirection != KeyEvent.VK_DOWN
                        pendingPacmanDirection = KeyEvent.VK_UP;
                    } else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) { //&& pacmanDirection != KeyEvent.VK_UP
                        pendingPacmanDirection = KeyEvent.VK_DOWN;
                    }
                }
            }
        });
        setFocusable(true);
        //add(this, BorderLayout.CENTER);
    }
    private void openResizeDialog(JFrame gameWindow) {
        // Create a dialog for inputting the new size
        JDialog resizeDialog = new JDialog(gameWindow, "Resize Game", true);
        resizeDialog.setLayout(new BorderLayout());

        // Create labels and text fields for row and column input
        JLabel rowLabel = new JLabel("Number of Rows (From 5 - 25):");
        JTextField rowTextField = new JTextField(Integer.toString(NUM_ROWS), 10);

        JLabel colLabel = new JLabel("Number of Columns (From 5 - 30):");
        JTextField colTextField = new JTextField(Integer.toString(NUM_COLS), 10);

        // Create the OK button to apply the new size
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            // Retrieve the new row and column values
            int newRow = Integer.parseInt(rowTextField.getText());
            int newCol = Integer.parseInt(colTextField.getText());

            // Validate the new values
            if (newRow > 4 && newCol > 4 && newRow <= 25 && newCol <= 30) {
                // Update the game size
                NUM_ROWS = newRow;
                NUM_COLS = newCol;
                frame.setSize(CELL_SIZE * NUM_COLS + 17, CELL_SIZE * NUM_ROWS + 140);
                frame.setLocationRelativeTo(null);
                //ghostRespawnPoint = new Point(0, 0);
                resetGame();
                repaint();

                // Close the dialog
                resizeDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(resizeDialog, "Invalid input. Please enter the values within range for rows and columns.");
            }
        });

        // Add the components to the dialog
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(rowLabel);
        inputPanel.add(rowTextField);
        inputPanel.add(colLabel);
        inputPanel.add(colTextField);

        resizeDialog.add(inputPanel, BorderLayout.CENTER);
        resizeDialog.add(okButton, BorderLayout.SOUTH);

        // Set the size and visibility of the resize dialog
        resizeDialog.pack();
        resizeDialog.setLocationRelativeTo(gameWindow);
        resizeDialog.setVisible(true);
    }
    
    private void clearMap(JFrame gameWindow) {
        walls.clear();
        pellets.clear();
        power.clear();
        pacmans.clear();
        ghosts.clear();
        repaint();
    }
    
    private void createMap() {
        walls.clear();
        pellets.clear();
        power.clear();
        pacmans.clear();
        ghosts.clear();
        NUM_ROWS = 20;
        NUM_COLS = 28;
        frame.setSize(CELL_SIZE * NUM_COLS + 17, CELL_SIZE * NUM_ROWS + 140);
        frame.setLocationRelativeTo(null);
        repaint();

        // Define the map layout
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

        // Parse the map layout and add elements
        for (int row = 0; row < NUM_ROWS+1; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                char element = mapLayout[row].charAt(col);
                if (element == '#') {
                    walls.add(new Point(col, row));
                } else if (element == '.') {
                    pellets.add(new Point(col, row));
                } else if (element == 'P') {
                    pacmans.add(new Point(col, row));
                } else if (element == 'G') {
                    ghosts.add(new Point(col, row));
                } else if (element == 'O') {
                    power.add(new Point(col, row));
                } else if (element == 'R') {
                	ghostRespawnPoint = new Point(col, row);
                }
            }
        }
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < NUM_ROWS && col >= 0 && col < NUM_COLS;
    }

    private boolean isOccupied(Point cell) {
        return pacmans.contains(cell) || ghosts.contains(cell) || walls.contains(cell) || pellets.contains(cell);
    }

    private boolean isWall(Point cell) {
        return walls.contains(cell);
    }

    private boolean isPellet(Point cell) {
        return pellets.contains(cell);
    }
    
    private boolean isPower(Point cell) {
        return power.contains(cell);
    }

    private void movePacman(Point pacman, int dx, int dy) {
        int nextX = (pacman.x + dx + NUM_COLS) % NUM_COLS;
        int nextY = (pacman.y + dy + NUM_ROWS) % NUM_ROWS;
        Point nextCell = new Point(nextX, nextY);
    	//Point nextCell = new Point(pacman.x + dx, pacman.y + dy);
        //Point warpCell = new Point(NUM_ROWS - pacman.x , NUM_COLS - pacman.y );
        if (isInBounds(nextCell.y, nextCell.x) && !isWall(nextCell)) {
            pacmans.set(0, nextCell);
            if (isPellet(nextCell)) {
                pellets.remove(nextCell);
                score += 10;
                scoreLabel.setText("Score: " + score);
                if (pellets.isEmpty() && power.isEmpty()) {
                    stopGameLoop();
                    winLabel.setVisible(true);
                    playAgainButton.setVisible(true);
                }
            }
            if (isPower(nextCell)) {
                power.remove(nextCell);
                score += 20;
                scoreLabel.setText("Score: " + score);
                if (pellets.isEmpty() && power.isEmpty()) {
                    stopGameLoop();
                    winLabel.setVisible(true);
                    playAgainButton.setVisible(true);
                }
                isBlue = true;
                
            }
        } 
    }
    
    
    private void moveGhost(Point ghost, int dx, int dy) {
        int nextX = (ghost.x + dx + NUM_COLS) % NUM_COLS;
        int nextY = (ghost.y + dy + NUM_ROWS) % NUM_ROWS;
        Point nextCell = new Point(nextX, nextY);

        if (!isWall(nextCell) && !isGhostCollision(nextCell)) {
            ghosts.set(ghosts.indexOf(ghost), nextCell);
        }
    }

    private boolean isGhostCollision(Point point) {
        for (Point ghost : ghosts) {
            if (ghost.equals(point)) {
                return true;
            }
        }
        return false;
    }


    private void moveGhosts() {
        if (isBlue) {
            moveGhostsAwayFromPacman();
        } else {
            moveGhostsTowardsPacman();
        }
    }

    private void moveGhostsTowardsPacman() {
        for (Point ghost : ghosts) {
            Point pacman = pacmans.get(0);
            Point nextCell = findNextCellTowardsPacman(ghost, pacman);
            if (nextCell != null) {
                int dx = nextCell.x - ghost.x;
                int dy = nextCell.y - ghost.y;
                moveGhost(ghost, dx, dy);
            }
        }
    }

    private void moveGhostsAwayFromPacman() {
        for (Point ghost : ghosts) {
            Point pacman = pacmans.get(0);
            Point nextCell = findNextCellAwayFromPacman(ghost, pacman);
            if (nextCell != null) {
                int dx = nextCell.x - ghost.x;
                int dy = nextCell.y - ghost.y;
                moveGhost(ghost, dx, dy);
            }
        }
    }

    private Point findNextCellTowardsPacman(Point ghost, Point pacman) {
        PriorityQueue<Cell> openList = new PriorityQueue<>(Comparator.comparingInt(Cell::getTotalCost));
        Set<Cell> closedSet = new HashSet<>();
        Map<Point, Cell> cellMap = new HashMap<>();

        Cell startCell = new Cell(ghost, null, 0, calculateHeuristic(ghost, pacman));
        openList.offer(startCell);
        cellMap.put(ghost, startCell);

        while (!openList.isEmpty()) {
            Cell currentCell = openList.poll();
            closedSet.add(currentCell);

            if (currentCell.point.equals(pacman)) {
                // Found a path to Pacman, backtrack to find the next cell to move
                while (currentCell.parentCell != startCell && currentCell.parentCell != null) {
                    currentCell = currentCell.parentCell;
                }
                return currentCell.point;
            }

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 && dy != 0) {
                        // Skip diagonal movements
                        continue;
                    }

                    Point nextPoint = new Point(currentCell.point.x + dx, currentCell.point.y + dy);
                    int nextX = (nextPoint.x + NUM_COLS) % NUM_COLS;
                    int nextY = (nextPoint.y + NUM_ROWS) % NUM_ROWS;
                    Point wrappedNextPoint = new Point(nextX, nextY);

                    if (!isInBounds(wrappedNextPoint.y, wrappedNextPoint.x) || isWall(wrappedNextPoint)) {
                        // If the wrapped cell is outside the bounds or a wall, skip it
                        continue;
                    }

                    int costSoFar = currentCell.costSoFar + 1;
                    int heuristic = calculateHeuristic(wrappedNextPoint, pacman);
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

        return null; // Unable to find a path to Pacman
    }


    private Point findNextCellAwayFromPacman(Point ghost, Point pacman) {
        Point nextCell = null;
        int maxDistance = Integer.MIN_VALUE;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx != 0 && dy != 0) {
                    // Skip diagonal movements
                    continue;
                }

                Point candidateCell = new Point(ghost.x + dx, ghost.y + dy);
                int nextX = (candidateCell.x + NUM_COLS) % NUM_COLS;
                int nextY = (candidateCell.y + NUM_ROWS) % NUM_ROWS;
                Point wrappedCandidateCell = new Point(nextX, nextY);

                if (!isInBounds(wrappedCandidateCell.y, wrappedCandidateCell.x) || isWall(wrappedCandidateCell)) {
                    // If the wrapped cell is outside the bounds or a wall, skip it
                    continue;
                }

                int distance = calculateDistance(wrappedCandidateCell, pacman);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    nextCell = wrappedCandidateCell;
                }
            }
        }

        return nextCell;
    }


    private int calculateHeuristic(Point current, Point target) {
        return Math.abs(current.x - target.x) + Math.abs(current.y - target.y);
    }

    private int calculateDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    class Cell {
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
    }



    private void resetGame() {
        walls.clear();
        pellets.clear();
        pacmans.clear();
        ghosts.clear();
        power.clear();
        ghostRespawnPoint=new Point();
        pacmanDirection = 0;
        pendingPacmanDirection = 0;
        score = 0;
        scoreLabel.setText("Score: 0");
        gameRunning = false;
    }

    private void drawGrid(Graphics g) {
        g.setColor(Color.GRAY);
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                int x = col * CELL_SIZE + 1;
                int y = row * CELL_SIZE + 1;
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private void drawWalls(Graphics g) {
        //g.setColor(Color.BLUE);
        for (Point wall : walls) {
            int x = wall.x * CELL_SIZE + 1;
            int y = wall.y * CELL_SIZE + 1;
            //g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            g.drawImage(wallImage, x, y, CELL_SIZE, CELL_SIZE, this);
        }
    }

    private void drawPellets(Graphics g) {
        //g.setColor(Color.GRAY);
        for (Point pellet : pellets) {
            int x = pellet.x * CELL_SIZE + CELL_SIZE / 2 + 1;
            int y = pellet.y * CELL_SIZE + CELL_SIZE / 2 + 1;
            //g.fillOval(x - 5, y - 5, 10, 10);
            g.drawImage(pelletImage, x - 5, y - 5 , 10, 10, this);
        }
    }
    
    private void drawPower(Graphics g) {
        //g.setColor(Color.GRAY);
        for (Point pow : power) {
            int x = pow.x * CELL_SIZE + CELL_SIZE / 2 + 1;
            int y = pow.y * CELL_SIZE + CELL_SIZE / 2 + 1;
            //g.fillOval(x - CELL_SIZE / 4, y - CELL_SIZE / 4, CELL_SIZE / 2, CELL_SIZE / 2);
            g.drawImage(powerImage, x - CELL_SIZE / 4, y - CELL_SIZE / 4 , CELL_SIZE / 2, CELL_SIZE / 2, this);
        }
    }

    private void drawPacmans(Graphics g) {
        g.setColor(Color.YELLOW);
        for (Point pacman : pacmans) {
        	int x = pacman.x * CELL_SIZE + 1;
        	int y = pacman.y * CELL_SIZE + 1;
        	if (pendingPacmanDirection == KeyEvent.VK_RIGHT) {
                g.fillArc(x, y, CELL_SIZE, CELL_SIZE, 45, 270);
        	} else if (pendingPacmanDirection == KeyEvent.VK_LEFT) {
        		g.fillArc(x, y, CELL_SIZE, CELL_SIZE, 225, 270);
        	} else if (pendingPacmanDirection == KeyEvent.VK_UP) {
        		g.fillArc(x, y, CELL_SIZE, CELL_SIZE, 135, 270);
        	}else if (pendingPacmanDirection == KeyEvent.VK_DOWN) {
        		g.fillArc(x, y, CELL_SIZE, CELL_SIZE, 315, 270);
        	} else {
            	g.fillArc(x, y, CELL_SIZE, CELL_SIZE, 45, 270);
        	}


        }
    }

    private void drawGhosts(Graphics g) {
        //g.setColor(Color.RED);
        for (Point ghost : ghosts) {
            int x = ghost.x * CELL_SIZE + 1;
            int y = ghost.y * CELL_SIZE + 1;
            //g.fillArc(x, y, CELL_SIZE, CELL_SIZE, 0, 180);
            g.drawImage(ghostImage, x, y, CELL_SIZE, CELL_SIZE, this);
        }
    }
    
    private void drawBlueGhosts(Graphics g) {
        //g.setColor(Color.BLUE);
        for (Point ghost : ghosts) {
            int x = ghost.x * CELL_SIZE + 1;
            int y = ghost.y * CELL_SIZE + 1;
            //g.fillArc(x, y, CELL_SIZE, CELL_SIZE, 0, 180);
            g.drawImage(blueGhost, x, y, CELL_SIZE, CELL_SIZE, this);
        }
    }
    private void drawGhostRespawn(Graphics g) {
        //g.setColor(Color.GREEN);
        int x = ghostRespawnPoint.x * CELL_SIZE + 1;
        int y = ghostRespawnPoint.y * CELL_SIZE + 1;
        //g.fillArc(x, y, CELL_SIZE, CELL_SIZE, 0, 180);
        g.drawImage(ghostRespawn, x, y, CELL_SIZE, CELL_SIZE, this);
    }

//    private void drawGameOver(Graphics g) {
//        g.setColor(Color.RED);
//        g.setFont(new Font("Arial", Font.BOLD, 30));
//        g.drawString("Game Over", CELL_SIZE * NUM_COLS / 2 - 100, CELL_SIZE * NUM_ROWS / 2);
//    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK);
        drawGrid(g);
        drawWalls(g);
        drawPellets(g);
        drawPacmans(g);
        drawGhostRespawn(g);
        gameOverLabel.setBounds(0, 0, getWidth(), getHeight());
        winLabel.setBounds(0, 0, getWidth(), getHeight());
        if (isBlue) {
            drawBlueGhosts(g); // Draw blue ghosts if isBlue is true
        } else {
            drawGhosts(g); // Draw red ghosts if isBlue is false
        }
        drawPower(g);
        if (gameRunning && isCollision()) {
            stopGameLoop();
            //drawGameOver(g);
            playAgainButton.setVisible(true);
            gameOverLabel.setVisible(true);
        }
    }

    private boolean isCollision() {
        Point pacman = pacmans.get(0);
        for (Point ghost : ghosts) {
            if (pacman.equals(ghost) && isBlue == false) {
                return true;
            }
            else if (pacman.equals(ghost) && isBlue == true) {
            	ghosts.remove(ghost);
            	scheduleGhostRespawn();
                return false;
            }
        }
        return false;
    }
    
    private void scheduleGhostRespawn() {
        Timer spawnTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spawnGhost(ghostRespawnPoint);
                repaint();
            }
        });
        spawnTimer.setRepeats(false); // Only trigger once
        spawnTimer.start();
    }
    
    private void spawnGhost(Point respawnPoint) {
        ghosts.add(respawnPoint);
    }



    private void startGameLoop() {
        pacmanTimer = new Timer(PACMAN_SPEED, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePacman(pacmans.get(0), getDX(), getDY());
                pacmanDirection = pendingPacmanDirection;
                repaint();
            }
        });
        pacmanTimer.start();

        ghostTimer = new Timer(GHOST_SPEED, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveGhosts();
                repaint();
            }
        });
        ghostTimer.start();
        // Start a timer to change the ghost color back to red after 5 seconds
        Timer blueTimer = new Timer(7000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBlue = false;
                repaint();
            }
        });
        //blueTimer.setRepeats(false); // Only trigger once
        blueTimer.start();
    }

    private void stopGameLoop() {
        if (pacmanTimer != null && pacmanTimer.isRunning()) {
            pacmanTimer.stop();
        }
        if (ghostTimer != null && ghostTimer.isRunning()) {
            ghostTimer.stop();
        }
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

    public static void main(String[] args) {
        //JFrame frame = new JFrame("Custom Pacman Game");
        PacmanGame game = new PacmanGame();
        frame.getContentPane().add(game); //borderlayout
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(CELL_SIZE * NUM_COLS + 17, CELL_SIZE * NUM_ROWS + 140);
        //frame.setBounds(NUM_COLS, NUM_ROWS, CELL_SIZE * NUM_COLS + 20, CELL_SIZE * NUM_ROWS + 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
