package PacMan;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * GameUI handles the user interface components for the Pacman game.
 * It creates and manages buttons, labels, and dialogs for controlling the game,
 * and provides methods to update the UI based on game events.
 */
public class GameUI {

    private PacmanGame game;          // Reference to the main game controller
    private JPanel buttonPanel;       // Main panel that holds all UI components

    private JButton playButton;
    private JButton playAgainButton;
    private JButton loadMapButton;
    private JButton resizeButton;
    private JButton clearButton;
    private JButton infoButton;
    private JComboBox<String> elementSelector;
    private JLabel gameOverLabel;
    private JLabel winLabel;
    private JLabel scoreLabel;

    /**
     * Constructs the GameUI and initializes all UI components.
     *
     * @param game the main PacmanGame instance.
     */
    public GameUI(PacmanGame game) {
        this.game = game;
        createUIComponents();
    }

    /**
     * Creates and lays out all UI components.
     */
    private void createUIComponents() {
        // Initialize buttons and labels with original styling and text
        playButton = new JButton("Play Now");
        playButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        playButton.setBackground(Color.YELLOW);
        playButton.setForeground(Color.BLACK);
        playButton.setOpaque(true);
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);

        playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        playAgainButton.setBackground(Color.YELLOW);
        playAgainButton.setForeground(Color.BLACK);
        playAgainButton.setOpaque(true);
        playAgainButton.setBorderPainted(false);
        playAgainButton.setFocusPainted(false);
        playAgainButton.setVisible(false);

        loadMapButton = new JButton("Load Default Map");
        loadMapButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        loadMapButton.setBackground(Color.BLUE);
        loadMapButton.setForeground(Color.YELLOW);
        loadMapButton.setOpaque(true);
        loadMapButton.setBorderPainted(false);
        loadMapButton.setFocusPainted(false);

        resizeButton = new JButton("Resize");
        resizeButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        resizeButton.setBackground(Color.GRAY);
        resizeButton.setForeground(Color.CYAN);

        clearButton = new JButton("Clear Board");
        clearButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 15));
        clearButton.setBackground(Color.RED);
        clearButton.setForeground(Color.BLACK);

        infoButton = new JButton("i");
        infoButton.setFont(new java.awt.Font("Times New Roman", java.awt.Font.ITALIC, 22));
        infoButton.setPreferredSize(new Dimension(40, 40));
        infoButton.setBackground(Color.LIGHT_GRAY);
        infoButton.setForeground(Color.BLACK);

        elementSelector = new JComboBox<>(new String[]{"Pacman", "Ghost", "Wall", "Pellet", "Power Pellet", "Ghost Respawn Point"});
        elementSelector.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        elementSelector.setForeground(Color.BLACK);
        elementSelector.setBackground(Color.WHITE);
        elementSelector.setOpaque(true);
        elementSelector.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(new java.awt.Font("Showcard Gothic", java.awt.Font.BOLD, 30));
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setVisible(false);
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setVerticalAlignment(SwingConstants.CENTER);

        winLabel = new JLabel("You Win!");
        winLabel.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 30));
        winLabel.setForeground(Color.GREEN);
        winLabel.setVisible(false);
        winLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winLabel.setVerticalAlignment(SwingConstants.CENTER);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 20));
        scoreLabel.setForeground(Color.WHITE);

        // Add action listeners to the buttons
        addActionListeners();

        // Create sub-panels to layout buttons and labels similar to the original layout
        JPanel buttonPanel1 = new JPanel();
        buttonPanel1.setLayout(new BoxLayout(buttonPanel1, BoxLayout.Y_AXIS));
        buttonPanel1.add(playButton);
        buttonPanel1.add(elementSelector);
        buttonPanel1.add(playAgainButton);
        buttonPanel1.add(clearButton);
        buttonPanel1.setBackground(Color.BLACK);

        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.setLayout(new BoxLayout(buttonPanel2, BoxLayout.Y_AXIS));
        buttonPanel2.add(scoreLabel);
        buttonPanel2.add(loadMapButton);
        buttonPanel2.add(resizeButton);
        buttonPanel2.setBackground(Color.BLACK);

        JPanel infoButtonPanel = new JPanel();
        infoButtonPanel.setLayout(new BoxLayout(infoButtonPanel, BoxLayout.Y_AXIS));
        infoButtonPanel.add(infoButton);
        infoButtonPanel.setBackground(Color.BLACK);

        // Create the bottom panel using GridBagLayout as in the original code
        JPanel bottomButtonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 5, 0, 0);
        bottomButtonPanel.add(buttonPanel1, gbc);

        gbc.gridx = 1;
        bottomButtonPanel.add(buttonPanel2, gbc);

        gbc.gridx = 2;
        bottomButtonPanel.add(infoButtonPanel, gbc);
        bottomButtonPanel.setBackground(Color.BLACK);

        // Create the main button panel that holds bottom buttons and overlay labels
        buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(bottomButtonPanel, BorderLayout.SOUTH);
        // Add the gameOver and win labels to the center (they will be positioned later)
        buttonPanel.add(gameOverLabel, BorderLayout.CENTER);
        buttonPanel.add(winLabel, BorderLayout.CENTER);
    }

    /**
     * Adds action listeners to the UI components to connect UI actions with game logic.
     */
    private void addActionListeners() {
        // Play button action: start the game if conditions are met.
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Check that a Pacman, ghost, and pellet have been placed.
                if (game.getPacman().getPosition() != null && 
                    game.getPacman().getPosition().x >= 0 &&
                    game.getGhosts().size() > 0 &&
                    !game.getBoard().noPelletsLeft()) {
                    game.setGameRunning(true);
                    playButton.setEnabled(false);
                    clearButton.setEnabled(false);
                    resizeButton.setEnabled(false);
                    loadMapButton.setEnabled(false);
                    infoButton.setEnabled(false);
                    playButton.setText("Game in progress...");
                    game.requestFocusInWindow();
                    game.startGameLoop();
                } else {
                    JOptionPane.showMessageDialog(game, "Place at least one Pacman, Ghost, and Pellet to Play!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

     // Play Again button action: reset the game.
        playAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.resetGame();
                playButton.setEnabled(true);
                clearButton.setEnabled(true);
                resizeButton.setEnabled(true);
                loadMapButton.setEnabled(true);
                infoButton.setEnabled(true);
                playButton.setText("Play Now");
                playAgainButton.setVisible(false);
                gameOverLabel.setVisible(false);
                winLabel.setVisible(false);
                game.requestFocusInWindow();
                // *** Fix: Immediately repaint to clear the board visually ***
                game.repaint();
            }
        });


        // Load Map button action: load the default map.
        loadMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.createMap();
                game.repaint();
            }
        });

        // Resize button action: open the resize dialog.
        resizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.openResizeDialog();
            }
        });

        // Clear button action: clear the board.
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.clearMap();
            }
        });

        // Info button action: open the info dialog.
        infoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog infoDialog = new JDialog(PacmanGame.frame, "How to Play", true);
                infoDialog.setSize(500, 600);
                infoDialog.setLocationRelativeTo(game);
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
                        "<li>Select an element and left click to place it down. You must place at least one Pacman, one Ghost, and one Pellet to start playing.</li>" +
                        "<li>Right click over an element to delete it.</li>" +
                        "<li>The Load Default Map button loads a provided balanced map to play on.</li>" +
                        "<li>The Resize button allows you to resize the map within the given parameters.</li>" +
                        "<li>The Clear Board button clears every element on the board.</li>" +
                        "</ul>" +
                        "<h2>The Elements</h2>" +
                        "<ul>" +
                        "<li>Pacman: Your character. Only one can be placed.</li>" +
                        "<li>Ghost: They chase you. You can place multiple ghosts.</li>" +
                        "<li>Pellet: Adds 10 points; must be eaten to win.</li>" +
                        "<li>Power Pellet: Adds 20 points and turns ghosts blue temporarily.</li>" +
                        "<li>Ghost Respawn Point: Where ghosts respawn after being eaten.</li>" +
                        "</ul>" +
                        "</body></html>";
                JLabel infoLabel = new JLabel(infoText);
                infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                infoLabel.setVerticalAlignment(SwingConstants.CENTER);
                infoDialog.add(infoLabel);
                infoDialog.setVisible(true);
            }
        });
    }

    /**
     * Returns the main panel containing all UI components.
     *
     * @return the button panel.
     */
    public JPanel getButtonPanel() {
        return buttonPanel;
    }

    /**
     * Returns the index of the selected element from the combo box.
     *
     * @return the selected element index.
     */
    public int getSelectedElementIndex() {
        return elementSelector.getSelectedIndex();
    }

    /**
     * Updates the score label with the current score.
     *
     * @param score the current score.
     */
    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    /**
     * Positions the game over and win labels to fill the game area.
     *
     * @param width  the width of the game area.
     * @param height the height of the game area.
     */
    public void positionLabels(int width, int height) {
        gameOverLabel.setBounds(0, 0, width, height);
        winLabel.setBounds(0, 0, width, height);
    }

    /**
     * Displays the game over message and shows the play again button.
     */
    public void showGameOver() {
        gameOverLabel.setVisible(true);
        playAgainButton.setVisible(true);
    }

    /**
     * Displays the win message and shows the play again button.
     */
    public void showWin() {
        winLabel.setVisible(true);
        playAgainButton.setVisible(true);
    }

    /**
     * Enables or disables the Play button.
     *
     * @param enable true to enable, false to disable.
     */
    public void enablePlayButton(boolean enable) {
        playButton.setEnabled(enable);
    }

    /**
     * Sets the text of the Play button.
     *
     * @param text the new text.
     */
    public void setPlayButtonText(String text) {
        playButton.setText(text);
    }
    
    // Getters for the labels
    public JLabel getGameOverLabel() {
        return gameOverLabel;
    }

    public JLabel getWinLabel() {
        return winLabel;
    }

}
