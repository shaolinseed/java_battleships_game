package play;

import javax.swing.*;
import java.net.URL;

public class MainFrame extends JFrame {

    JFrame frame = new JFrame("Battleships MainFrame");


    public MainFrame(){
        super("Battleships");

        URL iconURL = getClass().getResource("/images/mainWindowLogo.png");
        ImageIcon icon = new ImageIcon(iconURL);
        setIconImage(icon.getImage());


    }



}
