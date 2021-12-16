package models.player;

import board.PlayerType;
import models.ship.Ship;
import models.ship.ShipDirection;

public class Human extends Player {

    public Human(String userName) {
        super(userName);
        getBoard().setBoardOwner(PlayerType.HUMAN);
    }

    /**
     * initShipDirection method sets all ships to have
     * a direction of horizantal
     */
    public void initShipDirection(){
        for (Ship ship : getShips()){
            ship.setDirection(ShipDirection.HORIZONTAL);
        }
    }

}
