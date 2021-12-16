package models.player;

import board.Board;
import models.ship.Battleship;
import models.ship.Cruiser;
import models.ship.Destroyer;
import models.ship.Ship;

public abstract class Player {
    //the player's username
    private String userName;

    //array of the ships of the board (human's or bot's board)
    private Ship[] ships = {
            new Battleship(), new Cruiser(), new Cruiser(),
            new Destroyer(), new Destroyer(), new Destroyer(),
    };

    //create a new Board object ?
    private Board board = new Board(ships);

    public Player(String userName){
        setUserName(userName);
    }

    //accessor methods (getters and setters)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Ship[] getShips() {
        return ships;
    }

    public void setShips(Ship[] ships) {
        this.ships = ships;
    }





}
