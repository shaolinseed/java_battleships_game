package models.player;

import board.SquareState;
import board.Board;
import board.PlayerType;
import main.Actions;
import main.Constants;
import models.ship.Ship;
import models.ship.ShipDirection;
import play.Axis;

import java.util.ArrayList;
import java.util.Random;

import static board.SquareState.*;
import static main.Constants.*;

public class Bot extends Player {
    //whether a ship is currently found
    private Boolean foundShip;
    //the coords of the currently found ship
    private int currentFoundShipRow;
    private int currentFoundShipCol;

    //used to store the first shot of a currently found ship
    private int firstRowShot = 0;
    private int firstColShot = 0;

    //the axis the found ship is on
    private Axis axisFound;

    //list of the squares to shoot
    private ArrayList<String> squaresToShoot;
    private ArrayList<String> shipHitSquares;


    //create instance of Random class
    Random random = new Random();

//    //array of Squares to store the state of the board
//    private int[][] probabilityMap = new int[ROWS][COLS];
//    ;


    public void setAxisFound(Axis axisFound) {
        this.axisFound = axisFound;
    }


    public void setFoundShip(Boolean foundShip) {
        this.foundShip = foundShip;
    }

    //used for probability map algorithm (didn't have time to implement)
//    private void generateInitialState() {
//        for (int i = 0; i < ROWS; i++) {
//            for (int j = 0; j < COLS; j++) {
//                probabilityMap[i][j] = 0;
//            }
//        }
//    }
//
//    private void displayMap() {
//        for (int i = 0; i < ROWS; i++) {
//            for (int j = 0; j < COLS; j++) {
//                System.out.print(probabilityMap[i][j] + " ");
//            }
//            System.out.println();
//        }
//
//    }

    /**
     * Bot constructor - initalises necessary variables
     *
     * @param userName - the userName of the player can be specified in Main
     */
    public Bot(String userName) {
        super(userName);
        //generate random ships array
        getBoard().setBoardOwner(PlayerType.BOT);
        setFoundShip(false);
        setAxisFound(null);
        squaresToShoot = new ArrayList<String>();
        shipHitSquares = new ArrayList<String>();



    }

    /**
     * initRandomShip method finds a valid random position and location for a given ship
     * it then places the ship on the bot's board
     *
     * @param ship - the ship to be randomly placed
     */
    public void initRandomShip(Ship ship) {
        do {
            //generate random direction
            int directionIndex = random.nextInt(getBoard().getDirections().length);
            ShipDirection direction = getBoard().getDirections()[directionIndex];

            ship.setDirection(direction);

            do {
                int startRow = random.nextInt(Constants.ROWS);
                int startCol = random.nextInt(Constants.COLS);

                ship.setStartRow(startRow);
                ship.setStartCol(startCol);
            } while (Actions.shipIsOutOfBounds(ship));

            ship.calculateOccupiedSquares(ship.getStartRow(), ship.getStartCol());

        } while (Actions.shipCollidesWithPlacedShips(ship, getShips()));

        //place the ship on the bot's board
        getBoard().placeShip(ship, "bot");
        //confirm ship placement
        ship.setPlaced(true);

    }

    /**
     * placeRandomShips method cycles through each ship and places it randomly
     */
    public void placeRandomShips() {
        for (Ship ship : getShips()) {
            initRandomShip(ship);
        }

    }

    /**
     * easyShootAlgorithm - randomly shoots valid squares on the human board,
     * if a ship hit is detected it sets up certain attributes that trigger
     * the mediumBotShoot algorithm
     *
     * @param board - the board to shoot (human's board)
     */
    public void easyShootAlgorithm(Board board) {
        //until valid shot
        boolean validShot = false;
        //initialize row and col (randomly generated coords)
        int row = -1;
        int col = -1;

        SquareState shotSquareState = null;
        //generate a valid random shot at the human's board
        do {
            //generate random coordinates
            row = random.nextInt(Constants.ROWS);
            col = random.nextInt(Constants.COLS);

            //if the chosen square is valid
            if (board.checkValidSquare(row, col)) {
                //update board with the necessary state
                shotSquareState = board.getState()[row][col].getSquareState();
                validShot = true;
                board.updateBoardShot(row, col);
                //if a ship was hit
                if (shotSquareState == SHIP) {
                    //setup the fields needed to perform mediumShootAlgorithm
                    foundShip = true;
                    currentFoundShipRow = row;
                    currentFoundShipCol = col;
                    shipHitSquares.add(String.valueOf(row) + String.valueOf(col));
                    calculateSquaresToShoot(board);
                    firstRowShot = row;
                    firstColShot = col;
                    System.out.println(squaresToShoot + " : the squares to attempt shot at.");
                }

            }
            //until valid shot is made
        } while (!validShot);

    }

    /**
     * mediumShootAlgorithm  method is used when a ship has been found by the bot
     * (bot is in hunting mode). initially it shoots around the square and when the
     * axis is found it shoots shots at the necessary squares on the found axis
     *
     * @param board - the board to shoot at (human's board)
     */

    public void mediumShootAlgorithm(Board board) {
        boolean validShot = false;
        boolean successfulShot = false;
        int row = -1;
        int col = -1;
        SquareState shotSquareState = null;

        if (!foundShip) {
            easyShootAlgorithm(board);

        } else {
            int randomIndex;
            do {
                if (axisFound == Axis.ROW || axisFound == Axis.COL) {
                    System.out.println("calculating 22");
                    calculateSquaresToShootHunter();
                }

                randomIndex = random.nextInt(squaresToShoot.size());
                System.out.println("random index : " + randomIndex);

                System.out.println(squaresToShoot);


                String coordToCheck = String.valueOf(squaresToShoot.get(randomIndex));

                row = Integer.parseInt(String.valueOf(coordToCheck.charAt(0)));
                col = Integer.parseInt(String.valueOf(coordToCheck.charAt(1)));

                System.out.println("_---------------------------");
                System.out.println(coordToCheck.charAt(0));
                System.out.println(coordToCheck.charAt(1));

                System.out.println(row);
                System.out.println(col);

                shotSquareState = board.getState()[row][col].getSquareState();


                int tempRow = Integer.parseInt(String.valueOf(shipHitSquares.get(0).charAt(0)));
                int tempCol = Integer.parseInt(String.valueOf(shipHitSquares.get(0).charAt(1)));


                if (shotSquareState == SHIP) {
                    //update the board as valid shot
                    board.updateBoardShot(row, col);
                    //add the shot to the shipHitSquares
                    shipHitSquares.add(String.valueOf(row) + String.valueOf(col));
                    //remove the shot taken from the squaresToShoot array
                    squaresToShoot.clear();


                    if (row == tempRow) {
                        axisFound = Axis.ROW;
                    }
                    if (col == tempCol) {
                        axisFound = Axis.COL;
                    }
                    validShot = true;
                } else if (shotSquareState == EMPTY) {
                    //update the board as valid shot
                    board.updateBoardShot(row, col);
                    squaresToShoot.remove(randomIndex);
                    validShot = true;
                } else {
                    System.out.println(shotSquareState);
                    System.out.println("invalid shot!");
                }

                //if successfully destroyed a ship
                if (board.getState()[row][col].getSquareState() == DESTROYED) {
                    //set foundShip state to false  resume random shooting
                    foundShip = false;
                    //reset all states and values relating to bot shoot algorithm
                    axisFound = null;
                    firstRowShot = 0;
                    firstColShot = 0;
                    squaresToShoot.clear();
                    shipHitSquares.clear();
                }


            } while (!validShot);

        }
    }

    /**
     * calculateSquaresToShoot method is used when a ship is hit for
     * the first time. It updates the squaresToShoot array with the valid
     * squares to shoot +1/-1 of hit row and +1/-1 of hit col
     */

    public void calculateSquaresToShoot(Board board) {
        System.out.println("running squares to shoot 1....");
        int positiveRow = currentFoundShipRow + 1;
        int negativeRow = currentFoundShipRow - 1;

        int positiveCol = currentFoundShipCol + 1;
        int negativeCol = currentFoundShipCol - 1;

        //add guard incase something goes wrong
        try {
            if (board.checkValidSquare(currentFoundShipRow, positiveCol)) {
                squaresToShoot.add(String.valueOf(currentFoundShipRow) + String.valueOf(positiveCol));
            }
            if (board.checkValidSquare(currentFoundShipRow, negativeCol)) {
                squaresToShoot.add(String.valueOf(currentFoundShipRow) + String.valueOf(negativeCol));
            }
            if (board.checkValidSquare(positiveRow, currentFoundShipCol)) {
                squaresToShoot.add(String.valueOf(positiveRow) + String.valueOf(currentFoundShipCol));
            }
            if (board.checkValidSquare(negativeRow, currentFoundShipCol)) {
                squaresToShoot.add(String.valueOf(negativeRow) + String.valueOf(currentFoundShipCol));
            }
        }
        //if error is found
        catch (Exception e) {
            //empty the attributes
            squaresToShoot.clear();
            shipHitSquares.clear();
            axisFound = null;
            foundShip = false;
            firstColShot = 0;
            firstRowShot = 0;
        }


    }

    /**
     * calculateSquaresToShootHunter method is used when the bot knows the axis
     * that the currently hunted ship is on. It creates an array of the square
     * coordiantes that should be shot (2 either side as max ship size is 4)
     */
    public void calculateSquaresToShootHunter() {
        System.out.println("running squares to shoot 2....");
        //if the row needs to be changed
        if (axisFound == Axis.ROW) {
            int foundRow = Integer.parseInt(String.valueOf(shipHitSquares.get(0).charAt(0)));
            String newSquareToShoot;
            //for each of the squares that have been hit previously
            for (int i = 0; i < shipHitSquares.size(); i++) {
                int previousShotCol = Integer.parseInt(String.valueOf(shipHitSquares.get(i).charAt(1)));

                //if the first shot column was greater than the previous shot column
                if (firstColShot > previousShotCol) {
                    //if within board range
                    if ((firstColShot + 1) < COLS) {
                        newSquareToShoot = String.valueOf(foundRow) + String.valueOf(firstColShot + 1);
                        squaresToShoot.add(newSquareToShoot);

                    }
                    if ((previousShotCol - 1) > 0) {
                        newSquareToShoot = String.valueOf(foundRow) + String.valueOf(previousShotCol - 1);
                        squaresToShoot.add(newSquareToShoot);
                    }
                } else {
                    if ((previousShotCol + 1) < COLS) {
                        newSquareToShoot = String.valueOf(foundRow) + String.valueOf(previousShotCol + 1);
                        squaresToShoot.add(newSquareToShoot);
                    }
                    if ((firstColShot - 1) > 0) {
                        newSquareToShoot = String.valueOf(foundRow) + String.valueOf(firstColShot - 1);
                        squaresToShoot.add(newSquareToShoot);
                    }

                }

            }

        }
        //if the axis to change is column
        else if (axisFound == Axis.COL) {
            int foundCol = Integer.parseInt(String.valueOf(shipHitSquares.get(0).charAt(1)));
            String newSquareToShoot;
            //for each of the squares that have been hit previously
            for (int i = 0; i < shipHitSquares.size(); i++) {
                int previousShotRow = Integer.parseInt(String.valueOf(shipHitSquares.get(i).charAt(0)));

                //if the first shot column was greater than the previous shot column
                if (firstRowShot > previousShotRow) {
                    //if within board range
                    if ((firstRowShot + 1) < COLS) {
                        newSquareToShoot = String.valueOf(firstRowShot + 1) + String.valueOf(foundCol);
                        squaresToShoot.add(newSquareToShoot);

                    }
                    if ((previousShotRow - 1) > 0) {
                        newSquareToShoot = String.valueOf(previousShotRow - 1) + String.valueOf(foundCol);
                        squaresToShoot.add(newSquareToShoot);
                    }
                } else {
                    if ((previousShotRow + 1) < COLS) {
                        newSquareToShoot = String.valueOf(previousShotRow + 1) + String.valueOf(foundCol);
                        squaresToShoot.add(newSquareToShoot);
                    }
                    if ((firstColShot - 1) > 0) {
                        newSquareToShoot = String.valueOf(firstRowShot - 1) + String.valueOf(foundCol);
                        squaresToShoot.add(newSquareToShoot);
                    }

                }

            }
        }

    }


//
//    public void calculateProbabilityMap(Board board) {
//        for (Ship ship : board.getShips()){
//
//
//        }
//        System.out.println(getShips()[0]);
//
//        if (!Actions.shipIsOutOfBounds(getShips()[0], 0, 0)) {
//            System.out.println("NOPE");
//        }
//        System.out.println(Actions.calculatePossiblePlacements(getShips()[0], 0, 0));
//
//
//    }


}
