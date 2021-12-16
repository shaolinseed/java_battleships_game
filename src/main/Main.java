package main;

import database.DatabaseConnect;
import models.player.Bot;
import models.player.Human;
import play.GUIController;
import play.Game;

import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {

        //create new player - bot, userName is used to store player in DB
        Bot bot = new Bot("timmy");
        //create new player - human, userName is used to store player in DB
        Human human = new Human("human");


        //connect to the database
        DatabaseConnect connection = new DatabaseConnect();
        connection.dbConnect();

        //create a new game and setup the thread
        Game game = new Game(human, bot, connection);
        Thread gameThread = new Thread(game);
        gameThread.start();


        //create the GUIController and setup the thread
        GUIController guiController = new GUIController(human, bot, connection, game);
        Thread GUIThread = new Thread(guiController);
        GUIThread.start();


    }
}
