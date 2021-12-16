package board;

import main.Actions;
import models.ship.*;
import play.Game;
import play.GUIHelper;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static main.Constants.*;
import static main.Main.lock;

public class Board extends JPanel implements ActionListener {
    //array of Ships related to the current board
    private Ship[] ships;
    //array of Squares to store the state of the board
    private Square[][] state;

    //array of the types of direction ships can be placed in
    private ShipDirection[] directions = {
            ShipDirection.HORIZONTAL, ShipDirection.VERTICAL
    };


    //whether all ships have been placed or not
    private boolean allShipsPlaced = false;

    //the owner of the board
    private PlayerType boardOwner;

    //the current ship being placed index in the ships array
    private int currentShipIndex = 0;

    //stores the number of ships destroyed (if it equals 6 game has ended)
    private int shipsDestroyed = 0;

    //accessor methods (getters and setters)

    public Boolean getAllShipsPlaced() {
        return allShipsPlaced;
    }

    public void setAllShipsPlaced(Boolean allShipsPlaced) {
        this.allShipsPlaced = allShipsPlaced;
    }

    public Square[][] getState() {
        return this.state;
    }

    public int getCurrentShipIndex() {
        return currentShipIndex;
    }


    public PlayerType getBoardOwner() {
        return boardOwner;
    }

    public void setBoardOwner(PlayerType boardOwner) {
        this.boardOwner = boardOwner;
    }

    public ShipDirection[] getDirections() {
        return directions;
    }

    public int getShipsDestroyed() {
        return shipsDestroyed;
    }

    public void setShipsDestroyed(int shipsDestroyed) {
        this.shipsDestroyed = shipsDestroyed;
    }

    /**
     * Board constructor
     * @param ships - the player's ships array
     */
    public Board(Ship[] ships) {
        this.ships = ships;
    }

    /**
     * assembleBoard method initializes the board's state by setting up the 2D array
     * as a GridLayout and then creates a square of empty SquareState to place at each
     * index in the array
     */
    public void assembleBoard() {
        //create a new 10*10 Square array
        this.state = new Square[ROWS][COLS];
        setLayout(new GridLayout(ROWS, COLS));
        lock.lock();
        //for each coordiante in the state array
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                //place a square
                add(setupSquare(i, j));
            }
        }
        lock.unlock();
    }


    /**
     * setupSquare method is used to create squares and place them in the state array.
     * It also adds the necessary event listeners such as a mouse entering, exiting and
     * being clicked
     *
     * @param row - the row to place the square in within the 2d state  array
     * @param col - the column to place the square in within the 2d state array
     * @return - square - the created square
     */

    private Square setupSquare(int row, int col) {
        final String label = row + "" + col;

        Square square = Utils.Swing.createSquare(label, 50, 50);

        state[row][col] = square;

        //add mouse listener to detect when mouse hovers or clicks a square
        square.addActionListener(this);

        square.addMouseListener(new java.awt.event.MouseAdapter() {
            /**
             * mouseEntered method uses the current mouse position (which square the mouse has entered)
             * to calculate which squares need to be colored red or green. This data is then supplied to static hashmap
             * squaresToPaint. The GUIController loop will then detect the change and color the board
             * @param evt - the mouse event
             */
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                //only color hover shadow if in placing ships state
                if (!allShipsPlaced) {
                    //get the coordinate of the square that the mouse has entered
                    String label = square.getText();

                    int row = Integer.parseInt(String.valueOf(label.charAt(0)));
                    int col = Integer.parseInt(String.valueOf(label.charAt(1)));


                    //calculate the squares which the ship takes up from the current mouse placement
                    ships[currentShipIndex].calculateOccupiedSquares(row, col);

                    //for each of the squares that the ship being placed occupies
                    for (String s : ships[currentShipIndex].getOccupiedSquares()) {

                        String combinedCoord = String.valueOf(s.charAt(0)) + String.valueOf(s.charAt(1));

                        lock.lock();
                        //if invalid placement
                        if (Actions.shipIsOutOfBounds(ships[currentShipIndex], row, col) || Actions.shipCollidesWithPlacedShips(ships[currentShipIndex], ships, row, col)) {
                            //update the colorsToPaint hashmap with the RED color value
                            GUIHelper.squaresToPaint.put(combinedCoord, Color.RED);
                        } else {
                            //update the colorsToPaint hashmap with the GREEN color value
                            GUIHelper.squaresToPaint.put(combinedCoord, Color.GREEN);
                        }
                        lock.unlock();

                    }
                }
            }


            /**
             * mouseExited method uses the current mouse position (which square the mouse has exited)
             * to calculate which squares need to "uncolored" from the changes mouseEntered made.
             * This data is then supplied to static hashmap squaresToUnPaint.
             * The GUIController loop will then detect the change and color the board
             * @param evt - the mouse event
             */
            public void mouseExited(java.awt.event.MouseEvent evt) {
                //only uncolor hover shadow if in placing ships state
                if (!allShipsPlaced) {
                    //get the coordinate of the square that the mouse has exited
                    String label = square.getText();

                    int row = Integer.parseInt(String.valueOf(label.charAt(0)));
                    int col = Integer.parseInt(String.valueOf(label.charAt(1)));

                    ships[currentShipIndex].calculateOccupiedSquares(row, col);

                    for (String s : ships[currentShipIndex].getOccupiedSquares()) {
                        int paintSquareRow = Integer.parseInt(String.valueOf(s.charAt(0)));
                        int paintSquareCol = Integer.parseInt(String.valueOf(s.charAt(1)));

                        String combinedCoord = String.valueOf(s.charAt(0)) + String.valueOf(s.charAt(1));


                        lock.lock();
                        //if square state is empty
                        if (state[paintSquareRow][paintSquareCol].getSquareState() == SquareState.EMPTY) {
                            //paint square white
                            GUIHelper.squaresToUnPaint.put(combinedCoord, Color.white);
                        } else {
                            //paint square colour of ship
                            GUIHelper.squaresToUnPaint.put(combinedCoord, Actions.whichShipCollides(ships[currentShipIndex], ships, paintSquareRow, paintSquareCol).getColor());
                        }
                        lock.unlock();

                    }
                }
            }
        });
        return square;
    }


    /**
     * actionPeformed method is called when a player clicks on the board
     * it is used to control the events depending on the state of the game
     * (if in placeShips phase clicking the board will place ships, whereas if in
     * main game loop clicking board will shoot a shot
     * @param e - the ActionEvent that triggered the method
     */
    @Override
    public void actionPerformed(ActionEvent e) {


        //get the square that was clicked
        Square s = (Square) e.getSource();

        //store the coord of square as label
        String label = s.getText();

        //split label into the row and col
        int row = Integer.parseInt(String.valueOf(label.charAt(0)));
        int col = Integer.parseInt(String.valueOf(label.charAt(1)));

        //if in ship placing process
        if (!allShipsPlaced) {
            //if valid ship placement
            if (!Actions.shipIsOutOfBounds(ships[currentShipIndex], row, col) & !Actions.shipCollidesWithPlacedShips(ships[currentShipIndex], ships, row, col)) {
                //update the ship object being placed
                ships[currentShipIndex].setStartRow(row);
                ships[currentShipIndex].setStartCol(col);
                ships[currentShipIndex].setPlaced(true);
                //place the ship (will always be human board when mouse clicked)
                placeShip(ships[currentShipIndex], "human");
                //trigger the GUIHelper to colour the board
                GUIHelper.updateShipPlacedColor = true;
            }
        }
        //if in the main game loop (when players take turns at shooting)
        else {
            lock.lock();
            if (!Game.turnTaken & Game.humanTurn & boardOwner == PlayerType.BOT) {
                //update the board with the relevant shot
                updateBoardShot(row, col);
                System.out.println(row + ":" + col);
            }

            lock.unlock();

        }


    }

    /**
     * updateBoardShot method updates the necessary board if the shot taken is valid,
     * it also sends the relevant square coordinate to the GUIController to paint the correct
     * colour (if a ship is destroyed all squares of that ship should be painted)
     * @param row - the row of the fired shot
     * @param col - the column of the fired shot
     */
    public void updateBoardShot(int row, int col) {
        Boolean validShot = true;

        //state of the square before its state has been updated
        SquareState shotSquareState = state[row][col].getSquareState();

        //find the state of the square after it has been shot
        SquareState updatedSquareState = Actions.getNewSquareShotState(row, col, ships, state[row][col].getSquareState());

        //if the square has already been shot
        if (shotSquareState == SquareState.DESTROYED || shotSquareState == SquareState.HIT || shotSquareState == SquareState.MISS) {
            System.out.println("Invalid shot!");
            validShot = false;
        } else {
            //if we destroy the ship (number of hits equal to length of ship)
            if (updatedSquareState == SquareState.DESTROYED) {
                //for each of the squares
                for (String square : Actions.getHitShip(row, col, ships).getOccupiedSquares()) {
                    int destroyedSquareRow = Integer.parseInt(String.valueOf(square.charAt(0)));
                    int destroyedSquareCol = Integer.parseInt(String.valueOf(square.charAt(1)));
                    //set SquareState to destroyed
                    state[destroyedSquareRow][destroyedSquareCol].setSquareState(SquareState.DESTROYED);
                    //trigger the GUIHelper by adding squares colorClickedSquares array
                    GUIHelper.colorClickedSquares.add(String.valueOf(destroyedSquareRow) + String.valueOf(destroyedSquareCol));
                }
                Actions.getHitShip(row, col, ships).setDestroyed(true);
                updateNumberOfShipsDestroyed();

            } else {
                state[row][col].setSquareState(updatedSquareState);
                GUIHelper.colorClickedSquares.add(String.valueOf(row) + String.valueOf(col));
            }
        }


        //confirm the relevant player has taken their turn
        if (validShot) {
            if (boardOwner == PlayerType.BOT) {
                Game.turnTaken = true;
            } else {
                Game.turnTaken = true;
            }
        }


    }

    /**
     * updateNumberOfShipsDestroyed is called when a ship is
     * successfully destroyed, it scans through the ships array and
     * counts the number of ships destroyed updating the shipsDestroyed attribute
     */

    public void updateNumberOfShipsDestroyed() {
        int counter = 0;
        //for each ship
        for (Ship ship : ships) {
            if (ship.getDestroyed()) {
                //count if destroyed
                counter++;
            }
        }
        //update shipsDestroyed
        shipsDestroyed = counter;

        System.out.println(shipsDestroyed);
    }


    /**
     * placeShip method is used when a player places a ship on the board
     * in a valid location. It updates the board accordingly and triggers
     * the GUIController to paint the necessary squares on the board
     * @param s - the ship being placed
     * @param player - the player that is placing the ship
     */
    public void placeShip(Ship s, String player) {
        //row will be kept constant
        int start = s.getStartRow();

        //if ship is horizantal
        if (s.getDirection() == ShipDirection.HORIZONTAL) {
            //row will be kept constant
            start = s.getStartCol();
        }

        //for each square the ship occupies
        for (int i = start; i < start + s.getLength(); i++) {
            int row, col;

            if (s.getDirection() == ShipDirection.HORIZONTAL) {
                row = s.getStartRow();
                col = i;
            } else {
                row = i;
                col = s.getStartCol();
            }

            String combinedCoord = String.valueOf(row) + String.valueOf(col);

            lock.lock();
            //update each of square
            state[row][col].setSquareState(SquareState.SHIP);
            //if player placing is human
            if (player.equals("human")) {
                //update the relevant static array in GUIhelper to trigger GUIController
                GUIHelper.humanShipSquares.put(combinedCoord, s.getColor());
            }
            //if the player placing is bot
            if (player.equals("bot")) {
                //update the relevant static array in GUIhelper to trigger GUIController
                GUIHelper.botShipSquares.put(combinedCoord, s.getColor());
            }
            lock.unlock();
        }
        //calculate the ships occupied squares once it has been placed
        s.calculateOccupiedSquares(s.getStartRow(), s.getStartCol());
        //update the ship's state to placed
        ships[currentShipIndex].setPlaced(true);
        //increment the currentShipIndex
        currentShipIndex++;
        //if no more ships to place
        if (currentShipIndex > 5) {
            //update allShipsPlaced
            allShipsPlaced = true;
            System.out.println("All ships have been placed!");
        }
    }


    /**
     * *checkValidSquare method finds whether a square is valid by checking
     * if A) it's a square that hasn't been hit and B) within the board space
     * @param  - the row value of board to check
     * @param col - the column value of board to check
     * @return
     */
    public boolean checkValidSquare(int row, int col){
        SquareState squareState = state[row][col].getSquareState();
        //if invalid shot
        if (squareState == SquareState.DESTROYED || squareState == SquareState.HIT || squareState == SquareState.MISS
        || row >= ROWS || col >= COLS || row < 0 || col < 0){
            return false;
        } else {
            return true;
        }


    }





}
