import javax.swing.*;

public class TestGUI {
    private JPanel panel1;
    private JTextField textField1;

    public TestGUI(){
        JFrame frame = new JFrame("ObjectivesGUI");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public JPanel getPanel1(){
        return panel1;
    }
}
