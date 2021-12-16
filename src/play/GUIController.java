package play;

import database.DatabaseConnect;
import board.Board;
import board.PlayerType;
import main.Main;
import models.player.Bot;
import models.player.Human;
import models.player.Player;
import models.ship.ShipDirection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class GUIController extends JFrame implements Runnable, ActionListener {

    //player and game objects
    private Game game;
    private Bot bot;
    private Human human;

    //database connection
    DatabaseConnect connection;

    //the main frame to hold the panels
    MainFrame frame = new MainFrame();

    //the game panel used to display the boards and rotate button
    JPanel gamePanel = new JPanel(new GridBagLayout());

    GridBagConstraints constraints = new GridBagConstraints();

    JButton rotateButton = new JButton("Rotate");

    //the main menu panel
    JPanel mainMenuPanel = new JPanel();
    //play game button for main menu
    JButton playButton = new JButton("Play Game");
    //leaderboard button for main menu
    JButton leaderBoardButton = new JButton("Leaderboard");

    //used to test if players get added to DB on game end
//    JButton gameEndedButton = new JButton("Game ended test");

    //leader board panel
    JPanel leaderBoardPanel = new JPanel();

    //table model for leaderboard table
    DefaultTableModel tableModel = new DefaultTableModel();
    //leaderboard table
    JTable leaderBoardTable = new JTable(tableModel);

    //enable leaderboard scrollin
    JScrollPane scrollPane = new JScrollPane(leaderBoardTable);

    //gam won panel (appears when a player wins a game)
    JPanel gameWonPanel = new JPanel();

    //button to return to main  menu
    JButton returnMainMenuButton = new JButton("Return to main menu");


    //whether bots ships should be painted (used for testing)
    public static boolean paintBotShips;


    //whether the human board is currently displayer
    private boolean humanBoardDisplayed;
    //whether all ships have been coloured on the board
    boolean allShipsColoured;

    //the timer for the never dying loop
    int timer;

    /**
     * GUIController constructor
     *
     * @param human
     * @param bot
     * @param connection
     * @param game
     */

    public GUIController(Human human, Bot bot, DatabaseConnect connection, Game game) {
        this.human = human;
        this.bot = bot;
        this.connection = connection;
        this.game = game;


        humanBoardDisplayed = true;
        allShipsColoured = false;

        //setup the panels
        setupMainMenu();
        setupLeaderBoardPanel();
        frame.add(mainMenuPanel);


        System.out.println("------------------------------");

        updatePanel();


    }

    /**
     * setupMainMenu method is used to contruct the main menu
     */
    public void setupMainMenu() {
        mainMenuPanel.setLayout(new BoxLayout(mainMenuPanel, BoxLayout.PAGE_AXIS));
        mainMenuPanel.setPreferredSize(new Dimension(600, 400));
        //create "padding"
        mainMenuPanel.add(Box.createRigidArea(new Dimension(30, 30)));


        //centre all the buttons
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderBoardButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //set the text attributes
        playButton.setFont(new Font("TimesNewRoman", Font.PLAIN, 16));
        leaderBoardButton.setFont(new Font("TimesNewRoman", Font.PLAIN, 16));


        playButton.setSize(200, 200);

        //add the play game button
        mainMenuPanel.add(playButton);
        //spacer
        mainMenuPanel.add(Box.createVerticalStrut(15));
//        //add the leader board button
        mainMenuPanel.add(leaderBoardButton);

        //testing
//        mainMenuPanel.add(gameEndedButton);
//        gameEndedButton.addActionListener(this);


        //add action listeners
        playButton.addActionListener(this);
        leaderBoardButton.addActionListener(this);
    }


    /**
     * setUpLeaderBoardPanel method used to contruct the leaderboard panel
     * with the title and the JTable of players
     */
    public void setupLeaderBoardPanel() {

        //set layout and alignments
        leaderBoardPanel.setLayout(new BoxLayout(leaderBoardPanel, BoxLayout.PAGE_AXIS));
        leaderBoardTable.setAlignmentX(Component.CENTER_ALIGNMENT);
        returnMainMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        //create title label
        JLabel label = new JLabel("LeaderBoard");
        label.setFont(new Font("TimesNewRoman", Font.PLAIN, 40));

        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        //add the label
        leaderBoardPanel.add(label);

        //setup the table
        leaderBoardTable.setRowHeight(30);
        tableModel.addColumn("Player");
        tableModel.addColumn("Number of wins");


        leaderBoardTable.setVisible(true);
        leaderBoardPanel.add(scrollPane);

        leaderBoardPanel.add(returnMainMenuButton);
        returnMainMenuButton.addActionListener(this);
    }

    /**
     * setupGameWonPanel is used to setup the window when a player
     * wins the game creating custom messages depending on the winner
     *
     * @param playerWon - the player that won the game
     */

    public void setupGameWonPanel(Player playerWon) {
        //setup layout
        gameWonPanel.add(Box.createRigidArea(new Dimension(30, 30)));
        gameWonPanel.setLayout(new BoxLayout(gameWonPanel, BoxLayout.PAGE_AXIS));

        String wonMessage, lostMessage, winsMessage;
        //create the won / lost messages to display
        //if bot wins
        if (playerWon.getBoard().getBoardOwner() == PlayerType.BOT) {
            wonMessage = "Bot: " + bot.getUserName() + " won the game!";
            winsMessage = "They now have " + connection.findPlayerWins(bot) + " wins";
            lostMessage = "Human: " + human.getUserName() + " lost the game!";
        }
        //if human wins
        else {
            wonMessage = "Human: " + human.getUserName() + " won the game!";
            winsMessage = "They now have " + connection.findPlayerWins(human) + " wins";
            lostMessage = "bot: " + bot.getUserName() + " lost the game!";

        }
        //create the labels
        JLabel wonLabel = new JLabel(wonMessage);
        JLabel winsLabel = new JLabel(winsMessage);
        JLabel lostLabel = new JLabel(lostMessage);
        wonLabel.setFont(new Font("TimesNewRoman", Font.PLAIN, 24));
        winsLabel.setFont(new Font("TimesNewRoman", Font.PLAIN, 24));

        lostLabel.setFont(new Font("TimesNewRoman", Font.PLAIN, 20));

        returnMainMenuButton.setSize(200, 100);


        gameWonPanel.add(wonLabel);
        //spacer
        gameWonPanel.add(Box.createVerticalStrut(5));
        gameWonPanel.add(winsLabel);
        //spacer
        gameWonPanel.add(Box.createVerticalStrut(15));

        gameWonPanel.add(lostLabel);
        //spacer
        gameWonPanel.add(Box.createVerticalStrut(15));

        gameWonPanel.add(returnMainMenuButton);
        //action listener for return to main menu button
        returnMainMenuButton.addActionListener(this);
        gameWonPanel.setVisible(true);

    }

    /**
     * updateLeaderBoardTable is used to update the leaderboard Jtable with
     * the existing players ordered by their number of wins
     */
    public void updateLeaderBoardTable() {
        //ensure table is empty before adding to it
        tableModel.setRowCount(0);
        //arrayList with ordered player names
        ArrayList<String> playerNames = connection.getPlayerNames();
        //arrayList with ordered player wins
        ArrayList<Integer> playerWins = connection.getPlayerWins();
        //add each player to the table
        for (int i = 0; i < playerNames.size(); i++) {
            //create the entry array of strings (name | wins)
            String[] entry = {playerNames.get(i), String.valueOf(playerWins.get(i))};
            //execute the entry
            tableModel.addRow(entry);

        }


    }


    /**
     * setupGamePanel method is called when a user first enters a game
     * it sets up the panel to fit the board and rotate button
     * @param board
     */
    public void setupGamePanel(Board board) {
        //set instance variables in the GridBagConstraints instance
        constraints.ipadx = 25;
        constraints.ipady = 25;

        constraints.gridx = 0;
        constraints.gridy = 0;

        gamePanel.add(board, constraints);


        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 1;

//        JLabel label = new JLabel(String.valueOf(human.getBoard().getShipsDestroyed()));
//        JLabel humanShipsDestroyed = new JLabel(human.getBoard().getShips.)


        //rotate button
        gamePanel.add(rotateButton, constraints);
        rotateButton.addActionListener(this);


        //return to main menu button
        gamePanel.add(returnMainMenuButton, constraints);
        returnMainMenuButton.addActionListener(this);

    }

    /**
     * upodatePanel method updates the frame resetting the values
     */
    public void updatePanel() {
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);
    }


    /**
     * run method is called when thread starts up -
     * it should be kept alive until the game closes. It listens
     * for various state changes or static variable changes to trigger
     * other events
     */
    @Override
    public void run() {
        int delay = 500;
        do {
            //if all ships been placed increase timer -as no longer to update hovered squares
            if (!human.getBoard().getAllShipsPlaced()) {
                timer = 1;
            } else {
                timer = 100;
            }
            //if paintBotShips is true
            if (paintBotShips) {
                //paint the placed ships of the bot
                paintPlacedShipSquares(bot);
                paintBotShips = false;

            }
            //if squares need to be unpainted triggered by static array is not being empty
            if (!GUIHelper.squaresToUnPaint.isEmpty()) {
                Main.lock.lock();
                //unpaint the squares
                unPaintSquares();
                Main.lock.unlock();

            }
            //if squares need to be painted triggered by static array is not being empty
            if (!GUIHelper.squaresToPaint.isEmpty()) {
                Main.lock.lock();
                paintHoverSquares();
                Main.lock.unlock();
            }

            //if it is the ships haven't been coloured and the static boolean to indicate ships needing to
            //coloured is true
            if (Game.humanTurn & !allShipsColoured & GUIHelper.updateShipPlacedColor) {
                Main.lock.lock();
                paintPlacedShipSquares(human);
                GUIHelper.updateShipPlacedColor = false;
                Main.lock.unlock();
            }


            //if loop identifies change in colorClickedSquares array
            if (GUIHelper.colorClickedSquares.size() > 0) {
                //if the humanTurn has been changed in actionPeformed function (Board class)
                //change it here, changing after painting allows user to shoot 2 squares in 1 move

                //if it is the humans turn, and they have successfully taken a turn
                if (Game.humanTurn & Game.turnTaken) {
                    //paint the necessary square
                    paintClickedSquare(bot.getBoard());
                    //reset the turn taken to false
                    Game.turnTaken = false;
                    //change to the bot's turn
                    Game.humanTurn = false;
                }
                // if it is the bot's turn and they have successfully taken a turn
                else if (!Game.humanTurn & Game.turnTaken) {
                    //paint the necessary square
                    paintClickedSquare(human.getBoard());
                    //reset the turnTaken to false
                    Game.turnTaken = false;
                    //change to the human's turn
                    Game.humanTurn = true;

                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

            //if all ships have been placed
            if (human.getBoard().getAllShipsPlaced()) {

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                delay = 0;

                //if board is out of sync (human board is displayed when humans turn to shoot)
                if (humanBoardDisplayed & Game.humanTurn) {

                    //remove human board
                    System.out.println("swapping to bot board");
                    gamePanel.remove(human.getBoard());

                    //add bot board
                    setupGamePanel(bot.getBoard());
                    updatePanel();
                    humanBoardDisplayed = false;

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                if (!humanBoardDisplayed & !Game.humanTurn) {
                    System.out.println("swapping to human board");
                    //remove human board
                    gamePanel.remove(bot.getBoard());
                    //add bot board
//                    paintPlacedShipSquares(human);
                    setupGamePanel(human.getBoard());
                    updatePanel();
                    humanBoardDisplayed = true;

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Game.botToShoot = true;
                    System.out.println(Game.botToShoot);


                }

            }
            if (Game.playerWon != null) {
                setupGameWonPanel(Game.playerWon);
                frame.add(gameWonPanel);
                gamePanel.setVisible(false);
                gameWonPanel.setVisible(true);
            }


            try {
                Thread.sleep(timer);
            } catch (Exception e) {
            }

        } while (!Game.gameEnded);

    }

    /**
     * paintHoverSquares method is triggered when a player's mouse leaves a square
     * it unpaints each of the squares that were hovered over prior by accessing
     * the squaresToUnPaint static hashmap
     * w
     */
    public void unPaintSquares() {
        //for each element in the unPaintSquares hash map
        for (Map.Entry<String, Color> set :
                GUIHelper.squaresToUnPaint.entrySet()) {
            int paintSquareRow = Integer.parseInt(String.valueOf(set.getKey().charAt(0)));
            int paintSquareCol = Integer.parseInt(String.valueOf(set.getKey().charAt(1)));

            //paint (unpaint) the necessary squares with the correct color
            human.getBoard().getState()[paintSquareRow][paintSquareCol].paintSquare(set.getValue());
        }
        GUIHelper.squaresToUnPaint.clear();
    }

    /**
     * paintHoverSquares method is triggered when a player hovers over a square
     * it paints each of the squares that the current ship being placed occupies
     * with green if ok placement and red if bad placement
     */
    public void paintHoverSquares() {
        //for each element in the squaresToPaint hash map
        for (Map.Entry<String, Color> set :
                GUIHelper.squaresToPaint.entrySet()) {

            int paintSquareRow = Integer.parseInt(String.valueOf(set.getKey().charAt(0)));
            int paintSquareCol = Integer.parseInt(String.valueOf(set.getKey().charAt(1)));

            //paint the necessary squares with the found colour
            human.getBoard().getState()[paintSquareRow][paintSquareCol].paintSquare(set.getValue());
        }
        //empty the hashmap
        GUIHelper.squaresToPaint.clear();

    }

    /**
     * paintPlacedShipSquares method is used to paint the ships
     * a player has placed on their board
     *
     * @param player - the relevant player to accesss their board
     */
    public void paintPlacedShipSquares(Player player) {
        //hashmap to hold the current players ships
        HashMap<String, Color> tempShipSquares = new HashMap<String, Color>();
        if (player == human) {
            tempShipSquares = GUIHelper.humanShipSquares;
        } else {
            tempShipSquares = GUIHelper.botShipSquares;
        }
        //for each item in the tempShipSquares HashMap
        for (Map.Entry<String, Color> set :
                tempShipSquares.entrySet()) {
            int paintSquareRow = Integer.parseInt(String.valueOf(set.getKey().charAt(0)));
            int paintSquareCol = Integer.parseInt(String.valueOf(set.getKey().charAt(1)));

            player.getBoard().getState()[paintSquareRow][paintSquareCol].paintSquare(set.getValue());
        }

    }

    /**
     * paintClickedSquare is used to retreive the square(s) that should be painted
     * from the static array colorClickedSquares and colour the square
     *
     * @param board - the board to be coloured
     */

    public void paintClickedSquare(Board board) {
        for (int i = 0; i < GUIHelper.colorClickedSquares.size(); i++) {
            int paintSquareRow = Integer.parseInt(String.valueOf(GUIHelper.colorClickedSquares.get(i).charAt(0)));
            int paintSquareCol = Integer.parseInt(String.valueOf(GUIHelper.colorClickedSquares.get(i).charAt(1)));
            board.getState()[paintSquareRow][paintSquareCol].paintSquare();

        }
        //empty the colorClickedSquare
        GUIHelper.colorClickedSquares.clear();

    }


    /**
     * actionPeformed method is used to detect when different buttons are pressed
     * and triggers different actions depending on the button
     *
     * @param e - the action performed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //if the play game button is pressed
        if (e.getSource() == playButton) {
            Game.gameEnded = false;
            setupGamePanel(human.getBoard());
            //assemble each of the boards on this thread (GUI related)
            human.getBoard().assembleBoard();
            bot.getBoard().assembleBoard();
            Game.gameSetup = true;

            //update gui to display the gamePanel
            mainMenuPanel.setVisible(false);
            frame.add(gamePanel);

        }
        //if the leaderboard button is pressed
        else if (e.getSource() == leaderBoardButton) {
            connection.clearLeaderBoard();
            connection.getLeaderBoard();
            leaderBoardPanel.setVisible(true);
            updateLeaderBoardTable();
            connection.findPlayerWins(human);
            mainMenuPanel.setVisible(false);
            frame.add(leaderBoardPanel);
            System.out.println("!!!!");

        }


        //if the rotate ships button is pressed
        else if (e.getSource() == rotateButton) {
            //if ship is currently horizantal
            if (human.getShips()[human.getBoard().getCurrentShipIndex()].getDirection() == ShipDirection.HORIZONTAL) {
                //change direction to vertical
                human.getShips()[human.getBoard().getCurrentShipIndex()].setDirection(ShipDirection.VERTICAL);
            }
            //if ship is currently vertical
            else {
                //change direction to horizantal
                human.getShips()[human.getBoard().getCurrentShipIndex()].setDirection(ShipDirection.HORIZONTAL);
            }


        } else if (e.getSource() == returnMainMenuButton) {
            leaderBoardPanel.setVisible(false);
            mainMenuPanel.setVisible(true);
            gameWonPanel.setVisible(false);
            Game.playerWon = null;
//            frame.add(mainMenuPanel);
        }
        //used for testing
//        else if (e.getSource() == gameEndedButton){
//            setupGameWonPanel(bot);
//            frame.add(gameWonPanel);
//            mainMenuPanel.setVisible(false);
//            gameWonPanel.setVisible(true);
//        }
    }
}
