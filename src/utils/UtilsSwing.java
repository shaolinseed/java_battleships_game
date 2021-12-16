package utils;

import board.Square;

import javax.swing.*;
import java.awt.*;

public class UtilsSwing {

    public void showMessage(Object message) {
        JOptionPane.showMessageDialog(
                null,
                message
        );
    }

    public String getString(String promptMessage) {
        return JOptionPane.showInputDialog(promptMessage);
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

    /**
     * Create a new button
     *
     * @param label      button label
     * @param dimensions Width, Height - in that order
     * @return new button
     */
    public JButton createButton(String label, int... dimensions) {

        JButton button = new JButton();
        button.setText(label);
        button.setAlignmentX(JButton.CENTER);

        int width = 50, height = 10;

        switch (dimensions.length) {
            case 2:
                height = dimensions[1];
            case 1:
                width = dimensions[0];
                break;
        }

        button.setSize(width, height);

        return button;

    }
    public Square createSquare(String label, int... dimensions) {

        Square square = new Square();
        square.setText(label);
        square.setAlignmentX(JButton.CENTER);

        int width = 50, height = 50;

        switch (dimensions.length) {
            case 2:
                height = dimensions[1];
            case 1:
                width = dimensions[0];
                break;
        }

        square.setSize(width, height);

        return square;

    }
//    public addBoardToPanel(Board board){
//
//    }

    public JLabel createLabel(String text, int... dimensions) {

        JLabel label = new JLabel();
        label.setText(text);

        int width = 50, height = 10;

        switch (dimensions.length) {
            case 2:
                height = dimensions[1];
            case 1:
                width = dimensions[0];
                break;
        }

        label.setSize(width, height);

        return label;

    }

    /**
     * Create a new checkbox
     * @param text Label text
     * @param checked Boolean
     * @return new checkbox
     */
    public JCheckBox createCheckBox(String text, boolean... checked) {

        JCheckBox box = new JCheckBox(text);

        if (checked.length == 1) {
            box.setSelected(checked[0]);
        }

        return box;

    }

    /**
     * Create a new radio button
     * @param text Label text
     * @param checked Boolean
     * @return new radio button
     */
    public JRadioButton createRadioButton(String text, boolean... checked) {

        JRadioButton radio = new JRadioButton(text);

        if (checked.length == 1) {
            radio.setSelected(checked[0]);
        }

        return radio;

    }

    /**
     * Create a new text area
     *
     * @param params String Text, Boolean Enabled, Int Width, Int Height
     * @return new text area
     */
    public JTextArea createTextArea(Object... params) {

        String text = "";
        boolean enabled = false;
        int width = 50, height = 10;

        switch (params.length) {
            case 4:
                height = (int) params[3];
            case 3:
                width = (int) params[2];
            case 2:
                enabled = (boolean) params[1];
            case 1:
                text = (String) params[0];
                break;
        }

        JTextArea textArea = new JTextArea();
        textArea.setEnabled(enabled);
        textArea.setText(text);
        textArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        textArea.setSize(width, height);
        textArea.setFont(new Font("TimesNewRoman", Font.PLAIN, 20));
        textArea.setAlignmentY(JTextArea.TOP_ALIGNMENT);
        textArea.setAlignmentX(JTextArea.LEFT_ALIGNMENT);

        return textArea;

    }

    /**
     * Create a new text field
     *
     * @param params String Text, Boolean Enabled, Int Width, Int Height
     * @return new text area
     */
    public JTextField createTextField(Object... params) {

        String text = "";
        boolean enabled = true;
        int width = 50, height = 10;

        switch (params.length) {
            case 4:
                height = (int) params[3];
            case 3:
                width = (int) params[2];
            case 2:
                enabled = (boolean) params[1];
            case 1:
                text = (String) params[0];
                break;
        }

        JTextField textField = new JTextField();
        textField.setEnabled(enabled);
        textField.setText(text);
        textField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        textField.setSize(width, height);
        textField.setFont(new Font("TimesNewRoman", Font.PLAIN, 20));

        return textField;

    }

    /**
     * Create a new list with a set of items
     * @param items Array of items
     * @param <ListItemType> Type of items in array e.g. String
     * @return new list
     */
    public <ListItemType> JList<ListItemType> createList(ListItemType[] items, int... dimensions) {

        JList<ListItemType> list = new JList<>(items);

        int width = 50, height = 10;

        switch (dimensions.length) {
            case 2:
                height = dimensions[1];
            case 1:
                width = dimensions[0];
                break;
        }

        list.setSize(width, height);

        return list;

    }

    /**
     * Create a new combo box with a set of items
     * @param items Array of items
     * @param <ListItemType> Type of items in array e.g. String
     * @return new list
     */
    public <ListItemType> JComboBox<ListItemType> createComboBox(ListItemType[] items, int... dimensions) {

        JComboBox<ListItemType> box = new JComboBox<>(items);

        int width = 50, height = 10;

        switch (dimensions.length) {
            case 2:
                height = dimensions[1];
            case 1:
                width = dimensions[0];
                break;
        }

        box.setSize(width, height);

        return box;

    }

}
