package models.ship;

import java.awt.*;
import java.util.ArrayList;

public abstract class Ship {
    //ship's starting coordinates
    private int startRow;
    private int startCol;
    //ship's direction
    private ShipDirection direction;
    //length of the ship (how many squares it occupies)
    private int length;
    //whether the ship has been placed or not
    private boolean placed;
    //the color to paint the ship
    private Color color;
    //array of the coordinates that the ship occupies - e.g. ["11","12","13"]
    private ArrayList<String> occupiedSquares;
    //number of times ship has been successfully hit
    private int numberOfHits;
    //whether the ship is destroyed or not
    private boolean destroyed;

    /**
     * Ship constructor - the length and color are the only unique attributes associated with
     * a ship's type
     * @param length
     * @param color
     */
    public Ship(int length, Color color) {
        setLength(length);
        setColor(color);
    }

    //accessor methods (getters and setters)

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public ShipDirection getDirection() {
        return direction;
    }

    public void setDirection(ShipDirection direction) {
        this.direction = direction;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isPlaced() {
        return placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public ArrayList<String> getOccupiedSquares() {
        return occupiedSquares;
    }

    public void setOccupiedSquares(ArrayList<String> occupiedSquares) {
        this.occupiedSquares = occupiedSquares;
    }

    public int getNumberOfHits() {
        return numberOfHits;
    }

    public void setNumberOfHits(int numberOfHits) {
        this.numberOfHits = numberOfHits;
    }

    public boolean getDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    /**
     * calculateOccupiedSquares method finds the location of squares taken up by a ship (or potential
     * location when human is in placingShip state) and updates the occupiedSquares field of the ship
     * @param startRow - the starting row of the ship to base the calculation on
     * @param startCol - the starting column of the ship to base the calculation on
     */
    public void calculateOccupiedSquares(int startRow, int startCol) {
        int startSquare, stableIndex;
        if (getDirection() == ShipDirection.HORIZONTAL) {
            startSquare = startCol;
            stableIndex = startRow;
        } else {
            startSquare = startRow;
            stableIndex = startCol;
        }

        //initiate occupiedSpaces array
        this.occupiedSquares = new ArrayList<>();
        //for each of the squares in the ship's range
        for (int i = startSquare; i < startSquare + getLength(); i++) {
            StringBuilder box = new StringBuilder();
            if (getDirection() == ShipDirection.HORIZONTAL) {
                box.append(stableIndex);
                box.append(i);
            } else {
                box.append(i);
                box.append(stableIndex);
            }

            if (box.length() < 3){
                this.occupiedSquares.add(box.toString());
            }

        }
    }

    @Override
    public String toString() {
        return String.format(
                "%s [%d, %d, %s]",
                getClass().getName(),
                getStartRow(),
                getStartCol(),
                getDirection()
        );
    }
}
