package utils;

import java.util.Random;

public class Utils {
    public static final UtilsConsole Console = new UtilsConsole();
    public static final UtilsSwing Swing = new UtilsSwing();

    public static final Random rng = new Random();

    public static int getRandomInteger(int min, int max) {
        return rng.nextInt((max - min) + 1) + min;
    }
}
