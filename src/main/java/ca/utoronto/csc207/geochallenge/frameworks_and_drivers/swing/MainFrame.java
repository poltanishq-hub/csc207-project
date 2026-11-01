package ca.utoronto.csc207.geochallenge.frameworks_and_drivers.swing;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;

/** Initial Swing shell. Keep gameplay and scoring logic out of this class. */
public final class MainFrame extends JFrame {
    public MainFrame() {
        super("GeoGuessr Daily Challenge");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JLabel heading = new JLabel("GeoGuessr Daily Challenge", SwingConstants.CENTER);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 26.0f));

        JLabel status = new JLabel(
                "Maven + Swing are working. The five-round game will be built through use cases.",
                SwingConstants.CENTER);

        content.add(heading, BorderLayout.CENTER);
        content.add(status, BorderLayout.SOUTH);
        setContentPane(content);
    }
}
