package board;

import javax.accessibility.Accessible;
import javax.swing.JButton;
import java.awt.*;

public class Square extends JButton implements Accessible {
    //state of the square, e.g. EMPTY, MISS
    private SquareState squareState;

    /**
     * Square constructor - new squares should be set to empty
     * and painted accordingly
     */
    public Square() {
        this.setSquareState(SquareState.EMPTY);
        this.setEnabled(true);
        this.setFont(new Font("TimesNewRoman", Font.PLAIN, 14));
        this.paintSquare();
    }

    public SquareState getSquareState() {
        return this.squareState;
    }

    public void setSquareState(SquareState squareState) {
        this.squareState = squareState;

    }

    /**
     * paintSquare is used to paint the square the correct
     * colour based on its square state
     *
     */
    public void paintSquare() {
        Color c = null;
        switch (this.squareState) {
            case EMPTY:
                c = Color.WHITE;
                break;
            case HIT:
                c = Color.ORANGE;
                break;
            case MISS:
                c = Color.gray;
                break;
            case DESTROYED:
                c = Color.RED;
                break;
        }

        this.setBackground(c);
    }


    /**
     * paintSquare used when the colour needed is known without
     * calculating the squarestate
     * @param color - the color to paint the square
     */
    public void paintSquare(Color color) {
        this.setBackground(color);
    }


}




