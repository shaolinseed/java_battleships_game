package play;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GUIHelper {

    //hashmap updated when squares on board need to be painted
    public static HashMap<String, Color> squaresToPaint = new HashMap<String, Color>();

    //hashmap updated when squares on board need to be unpainted (mouse exits square)
    public static HashMap<String, Color> squaresToUnPaint = new HashMap<String, Color>();

    //hashmap to store the human's ship squares
    public static HashMap<String, Color> humanShipSquares = new HashMap<String, Color>();

    //hashmap to store the bot's ship squares
    public static HashMap<String, Color> botShipSquares = new HashMap<String, Color>();

    //arraylist to store squares to be coloured updated when a player shoots the board
    public static ArrayList<String> colorClickedSquares = new ArrayList<String>();

    //boolean to store whether the ships need to be colored or not
    public static boolean updateShipPlacedColor = false;

}
