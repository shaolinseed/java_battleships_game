package main;

import board.SquareState;
import models.ship.Ship;
import models.ship.ShipDirection;
import utils.Utils;

import static board.SquareState.HIT;

public class Actions {
    /**
     * getHitShip is used to find which ship has been hit by a given shot
     * @param row - the row of the fired shot
     * @param col - the colum of the fired shot
     * @param ships - the ships array to check against
     * @return - returns the ship that was successfully hit
     */
    public static Ship getHitShip(int row, int col, Ship[] ships) {
        //check each ship in the ships array
        for (Ship s : ships) {
            if (s.getDirection() == ShipDirection.HORIZONTAL) {
                if (s.getStartRow() == row &&
                        s.getStartCol() <= col &&
                        col < s.getStartCol() + s.getLength()) {
                    return s;
                }
            } else {
                if (s.getStartCol() == col &&
                        s.getStartRow() <= row &&
                        row < s.getStartRow() + s.getLength()) {
                    return s;
                }
            }
        }
        //returns null if no ship was hit
        return null;
    }

    /**
     * getNewSquareShotState method is used to return the state of the "updated"
     * square that has been shot without actually updating it. It helps the updateBoardShot
     * method in the Board clas
     * @param row - the row of the square to check
     * @param col - the column of the square to check
     * @param ships - the ships array to help find the new state of the square
     * @param oldSquareState - the previous squares state before it  has been updated
     * @return - returns the updated squares state
     */

    public static SquareState getNewSquareShotState(int row, int col, Ship[] ships, SquareState oldSquareState) {

        Utils.Console.showMessage("Clicked " + row + "," + col);

        //if the clicked box contains a ship and the square has not already been hit
        if (getHitShip(row, col, ships) != null & oldSquareState != HIT) {
            Utils.Console.showMessage("Hit " + getHitShip(row, col, ships));

            //increment the number of hits on the hit ship
            getHitShip(row, col, ships).setNumberOfHits(getHitShip(row, col, ships).getNumberOfHits() + 1);
            System.out.println(getHitShip(row, col, ships) + " has been shot " + getHitShip(row, col, ships).getNumberOfHits() + " times");

            //if the ship has not been destroyed yet (number of hits is less than the length of the ship)
            if (getHitShip(row, col, ships).getNumberOfHits() < getHitShip(row, col, ships).getLength()) {
                return HIT;
            }
            //if the ship has been destroyed (number of hits is equal to the length)
            else if (getHitShip(row, col, ships).getNumberOfHits() == getHitShip(row, col, ships).getLength()) {
                System.out.println("DESTRUCTION BOI!!!");
                return SquareState.DESTROYED;
            }
        }
        //if ship is missed
        else {
            return SquareState.MISS;
        }
        return null;
    }


    /**
     * shipIsOutOfBounds checks if a ship is within the board space when placing
     * (doesn't need the start row or col as this can be evaluated from the ship)
     * @param ship - the ship to check
     * @return - true if the ship is within the board else false
     */
    public static boolean shipIsOutOfBounds(Ship ship) {
        if (ship.getDirection() == ShipDirection.HORIZONTAL) {
            if (ship.getStartCol() + ship.getLength() > Constants.COLS) {
                return true;
            }
        } else {
            // vertical positioning
            if (ship.getStartRow() + ship.getLength() > Constants.ROWS) {
                return true;
            }

        }
        return false;
    }

    //used for proabability map (unfinished)
//    public static int calculatePossiblePlacements(Ship ship, int startRow, int startCol) {
//        int counter = 0;
//
//        if (startCol + ship.getLength() <= Constants.COLS) {
//            counter++;
//        }
//        if (startRow + ship.getLength() <= Constants.ROWS) {
//            counter++;
//        }
//        return counter;
//    }

    /**
     * shipIsOutOfBounds checks if a ship is within the board space when hovering
     * @param ship - the ship to check
     * @param startRow - the starting row of the ship (where mouse is hovered)
     * @param startCol - the starting row of the ship (where mouse is hovered)
     * @return - true if the ship is within the board else false
     */

    public static boolean shipIsOutOfBounds(Ship ship, int startRow, int startCol) {
        if (ship.getDirection() == ShipDirection.HORIZONTAL) {
            if (startCol + ship.getLength() > Constants.COLS) {
                return true;
            }
        } else {
            // vertical positioning
            if (startRow + ship.getLength() > Constants.ROWS) {
                return true;
            }
        }
        return false;
    }

    /**
     * shipCollidesWithPlacedShips method checks if a ship being placed
     * collides with any of the ships that have already been placed on the board
     * @param shipToPlace - the ship that is being placed
     * @param ships - array of the ships of the board to check
     * @return - true if the ship
     */
    public static boolean shipCollidesWithPlacedShips(Ship shipToPlace, Ship[] ships) {
        for (Ship placedShip : ships) {
            if (!placedShip.isPlaced()) continue;

            //for each square that the ship occupiesc
            for (String square : shipToPlace.getOccupiedSquares()) {
                if (placedShip.getOccupiedSquares().contains(square)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * shipCollidesWithPlaced ships does the same as the above function but is used when
     * user is hovering (as the start row and start col have not been set therefore
     * the start row and start col must be provided to the function as it cannot find that
     * information from the ships array alone
     * @param shipToPlace - the ship being placed
     * @param ships - the ships array of the board
     * @param startRow - the start row (mouse point on board)
     * @param startCol - the start column (mouse point on board)
     * @return - returns true if the ship doesn't collide else false
     */
    public static boolean shipCollidesWithPlacedShips(Ship shipToPlace, Ship[] ships, int startRow, int startCol) {
        for (Ship placedShip : ships) {
            if (!placedShip.isPlaced()) continue;

            shipToPlace.calculateOccupiedSquares(startRow, startCol);

            for (String square : shipToPlace.getOccupiedSquares()) {
                if (placedShip.getOccupiedSquares().contains(square)) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * whichShipCollides method is used to return the ship that collides with ship being placed
     * @param shipToPlace - the ship being placed
     * @param ships - the array of ships of the board
     * @param startRow - the start row of the ship being placed
     * @param startCol - the start column of the ship being placed
     * @return - the ship that the ship being placed collides with
     */
    public static Ship whichShipCollides(Ship shipToPlace, Ship[] ships, int startRow, int startCol) {
        for (Ship placedShip : ships) {
            if (!placedShip.isPlaced()) continue;

            shipToPlace.calculateOccupiedSquares(startRow, startCol);

            for (String square : shipToPlace.getOccupiedSquares()) {
                if (placedShip.getOccupiedSquares().contains(square)) {
                    return placedShip;
                }
            }

        }

        return null;
    }


}
