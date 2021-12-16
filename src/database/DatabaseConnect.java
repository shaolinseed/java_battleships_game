package database;

import models.player.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseConnect {
    static Connection aConnection;

    //arraylist of the all player's names (to add to leaderboard)
    private ArrayList<String> playerNames = new ArrayList<String>();
    //arraylist of the all player's wins (to add to leaderboard)
    private ArrayList<Integer> playerWins = new ArrayList<Integer>();

    public ArrayList<String> getPlayerNames() {
        return playerNames;
    }

    public ArrayList<Integer> getPlayerWins() {
        return playerWins;
    }

    /**
     * dbConnect method is used to setup the connection to the database
     * which is used to keep track of each players number of wins
     */

    public void dbConnect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException x) {
            System.out.println("Cannot find driver class. Check CLASSPATH");
            return;
        }
        try {
            // replace DB_SERVER_URL with the link to the database host
            String filename = "jdbc:mysql://localhost:3306/";
            String database = "game_leaderboard"; // database name

            String userName = "root"; //database username
            String password = ""; //database password

            String databaseConn = filename + database;

            aConnection = DriverManager.getConnection(databaseConn, userName, password);

            System.out.println("MySQL JDBC Driver Registered!");
        } catch (SQLException x) {
            System.out.println("Exception connecting to database:" + x);
            return;
        }
    }

    /**
     * getLeaderBoard method is used to help construct the JTable that holds
     * the leaderboard. It orders the players in descending order based on their
     * number of wins and updates the playerNames and playerWins arraylists
     */
    public void getLeaderBoard() {
        try {
            Statement aStmt = aConnection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            //request all players from players tables ordered by the number of wins (descending order)
            String query = "SELECT * FROM player ORDER BY victories DESC";

            //execute the query
            ResultSet rs = aStmt.executeQuery(query);

            //for each of the findings
            while (rs.next()) {
                //add to playerNames arraylist
                playerNames.add(rs.getString(2));
                //add to playerWins arraylist
                playerWins.add(rs.getInt(3));

                System.out.println(rs.getString(2) + " : " + rs.getInt(3) + " wins");
            }
            rs.close();
            aStmt.close();
        } catch (SQLException x) {
            System.out.println("Exception while executing query:" + x);
        }

    }

    /**
     * clearLeaderBoard method empties playerNames and playerWins arraylists
     */
    public void clearLeaderBoard(){
        playerNames.clear();
        playerWins.clear();
    }

    /**
     * findPlayerWins method is used to retrieve the number of wins os a chosen
     * player from the database.
     * @param player - the chosen player to find the number of wins of
     * @return - wins (int) the number of wins of the specified player
     */

    public int findPlayerWins(Player player) {
        //initiate wins to store to
        int wins = 0;
        //get the player's username
        String name = player.getUserName();
        try {
            Statement aStmt = aConnection.createStatement();


            //create SQL String to query database with
            String query = "SELECT victories FROM player WHERE name='" + name + "'";

            //execute the query
            ResultSet rs = aStmt.executeQuery(query);

           while (rs.next()) {
               wins = rs.getInt(1);
           }

           rs.close();
            aStmt.close();
        } catch (SQLException x) {
            System.out.println("Exception while executing query:" + x);
        }
        return wins;
    }

    /**
     * updatePlayerWins method is used when a player wins a game to u
     * update the relevant player's number of victories in the database
     * @param player - the player to update
     * @param currentWins- the player's current number of wins calculated by
     *                     findPlayerWins() method
     */

    public void updatePlayerWins(Player player, int currentWins){
        //get the player's username
        String name = player.getUserName();
        int updatedWins = currentWins + 1;

        try {
            Statement aStmt = aConnection.createStatement();

            String updateDB = "UPDATE player SET victories = " + updatedWins + " WHERE name ='" + name + "'";

            System.out.println(updateDB);
            System.out.println("attempting update");

            //execute the update
            aStmt.execute(updateDB);
            aStmt.close();
        } catch (SQLException x) {
            System.out.println("Exception while executing query:" + x);
        }

    }

    /**
     * checkPlayerInDB method used to find whether a give player exists
     * in the database by checking if their username already in the database
     * @param player - the player to check
     * @return - returns true if
     */

    public boolean checkPlayerInDB(Player player){
        //get the player's username
        String name = player.getUserName();
        String foundString = "";

        try {
            Statement aStmt = aConnection.createStatement();


            //create SQL String to query database with
            String query = "SELECT name FROM player WHERE name='" + name + "'";


            ResultSet rs = aStmt.executeQuery(query);

            while (rs.next()) {
                foundString = rs.getString(1);
            }

            rs.close();
            aStmt.close();
        } catch (SQLException x) {
            System.out.println("Exception while executing query:" + x);
        }

        if (foundString.equals("")){
            return false;
        }
        else {
            return true;
        }

    }

    /**
     * adPlayerToDB method is used when a player with a username
     * that is not already in the database starts a game. It adds them to the DB
     * @param player - the player to be added to the database
     */

    public void addPlayerToDB(Player player){
        //get the player's username
        String name = player.getUserName();

        try {
            Statement aStmt = aConnection.createStatement();

            //insert player with 0 wins / victories and chosen name into the database
            String updateDB = "INSERT INTO `player` (`id`, `name`, `victories`) VALUES (NULL, '" + name + "', '0')";

            //execute the update
            aStmt.execute(updateDB);
            aStmt.close();
        } catch (SQLException x) {
            System.out.println("Exception while executing query:" + x);
        }

    }



}

