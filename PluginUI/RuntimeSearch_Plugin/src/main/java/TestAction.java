import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;


public class TestAction implements ToolWindowFactory {

    private JButton refreshToolWindowButton;
    private JButton hideToolWindowButton;
    private JLabel currentDate;
    private JLabel currentTime;
    private JLabel timeZone;
    private JPanel myToolWindowContent;
    private ToolWindow myToolWindow;

    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        myToolWindow = toolWindow;
        myToolWindowContent = new JPanel();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        myToolWindowContent.setLayout(new GridBagLayout());
        JLabel label = new JLabel("Search for a string:");
        JTextField searchBar = new JTextField(20);
        myToolWindowContent.add(label);
        myToolWindowContent.add(searchBar);

        JButton myButton = new JButton("Find Next");
        myToolWindowContent.add(myButton);

        Content content = contentFactory.createContent(myToolWindowContent, "Test Tool Action", false);
        System.out.println(myToolWindowContent);
        toolWindow.getContentManager().addContent(content);


//        toolWindow.getComponent().add(new TestGUI().getPanel1());

    }
}