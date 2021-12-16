package play;

import database.DatabaseConnect;
import board.PlayerType;
import models.player.Bot;
import models.player.Human;
import models.player.Player;

public class Game implements Runnable {
    //player objects
    Bot bot;
    Human human;
    //database connection
    DatabaseConnect connection;

    //whether the game needs to be setup
    public static boolean gameSetup = false;

    //whether the bot needs to shoot
    public static boolean botToShoot = false;

    //whether it is the human's turn to shoot (otherwise bots turn)
    public static Boolean humanTurn = true;

    //whether the player has successfully taken their turn
    public static Boolean turnTaken = false;

    //the player who has won (otherwise null)
    public static Player playerWon = null;


    //boolean to track if a game has ended
    public static Boolean gameEnded = false;


    /**
     * Game constructor
     *
     * @param human      - the human player
     * @param bot        - the bot player
     * @param connection - the database connect instance
     */
    public Game(Human human, Bot bot, DatabaseConnect connection) {
        this.human = human;
        this.bot = bot;
        this.connection = connection;
        humanTurn = true;

    }


    /**
     * setUpGame function is called when the user starts a new game
     * it inserts a new player entry in the DB if player is not in DB
     * Also initiates the bots ship formation
     */
    public void setupGame() throws InterruptedException {
        System.out.println(Thread.currentThread().getName());
        //update players in DB if they don't exist there
        updatePlayerInDB();
//        bot.calculateProbabilityMap(bot.getBoard());
        System.out.println("TEST");
        bot.placeRandomShips();

    }



    /**
     * checkPlayerWon method checks if a player has won the game
     * (they have destroyed 6 of their opponents ships)
     *
     * @return Player - returns the player that won the game
     */
    public Player checkPlayerWon() {
        if (bot.getBoard().getShipsDestroyed() == 6) {
            return human;
        } else if (human.getBoard().getShipsDestroyed() == 6) {
            return bot;
        }
        return null;
    }

    /**
     * endGame method is called when the game ends to update the database
     * with the new number of wins of the player that won
     * @param player - the player that won the game
     */

    public void endGame(Player player) {
        //if bot won
        if (player.getBoard().getBoardOwner() == PlayerType.BOT) {
            System.out.println("Bot " + bot.getUserName() + " won the game!");
            connection.updatePlayerWins(bot, connection.findPlayerWins(bot));
        } else if (player.getBoard().getBoardOwner() == PlayerType.HUMAN) {
            System.out.println("Human player " + human.getUserName() + " won the game!");
            connection.updatePlayerWins(human, connection.findPlayerWins(human));
        }

    }


    /**
     * updatePlayerInDB method used to initiate an insert
     * into the database if the current player doesn't already
     * exist in the database
     */

    public void updatePlayerInDB() {
        //human check
        //if player found in db
        if (connection.checkPlayerInDB(human)) {
            System.out.println("Player exists in Database!");
        }
        //if player not found add player to DBN
        else {
            System.out.println("Player does not exist in database...");
            System.out.println("Adding player");
            connection.addPlayerToDB(human);
        }
        //bot check
        //if player found in db
        if (connection.checkPlayerInDB(human)) {
            System.out.println("Player exists in Database!");
        } else {
            System.out.println("Player does not exist in database...");
            System.out.println("Adding player");
            connection.addPlayerToDB(human);
        }

    }

    /**
     * run method acts as "game loop" it is used to trigger the bot
     * firing shots and carry out check to see if player has won the game
     */

    @Override
    public void run() {
        //if game hasn't started set delay to 2000
        int delay = 2000;

        while (!gameEnded) {
            //if game needs to be setup
            if (gameSetup) {
                try {
                    //set the game up
                    setupGame();
                    //lower delay once game is setup
                    delay = 1000;
                    System.out.println("Game has been setup!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gameSetup = false;
            }
            //if the bot needs to shoot
            if (botToShoot) {
                //trigger the bot shooting algorithm
                bot.mediumShootAlgorithm(human.getBoard());
                //reset botToShoot
                botToShoot = false;
            }

            //if a player destroyed 6 ships
            if (checkPlayerWon() != null){
                playerWon = checkPlayerWon();
                //run endGame method to add to database
                endGame(checkPlayerWon());
                gameEnded = true;
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}
