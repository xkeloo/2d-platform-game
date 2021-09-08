import java.awt.*;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class Main {

    public static void main(String[] args) {
        MainFrame frame = new MainFrame();

        frame.setSize(1000, 700);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((int)(screenSize.getWidth()/2 - frame.getSize().getWidth()/2), (int)(screenSize.getHeight()/2 - frame.getSize().getHeight()/2));

        frame.setResizable(false);
        frame.setTitle("Projekt JTP");
        frame.setVisible(true);

        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
