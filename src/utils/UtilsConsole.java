package utils;

import java.util.Scanner;

public class UtilsConsole {

    private Scanner input = new Scanner(System.in);

    public void showMessage(Object message) {
        System.out.println(message);
    }

    public void showMessageSingle(Object message) {
        System.out.print(message);
    }

    public String getString(String promptMessage) {
        System.out.print(promptMessage);
        return input.nextLine();
    }

    public char getCharLowerCase(String promptMessage) {
        return getString(promptMessage).toLowerCase().charAt(0);
    }

    public int getInteger(String promptMessage) {
        do {
            try {
                return Integer.parseInt(getString(promptMessage));
            } catch (Exception e) {
                showMessage("Please only type numbers");
            }
        } while (true);
    }

    public float getFloat(String promptMessage) {
        do {
            try {
                return Float.parseFloat(getString(promptMessage));
            } catch (Exception e) {
                showMessage("Please only type numbers");
            }
        } while (true);
    }

}
