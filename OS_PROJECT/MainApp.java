import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the intro frame
            JFrame introFrame = new JFrame("CPU Scheduling Simulator");
            introFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            introFrame.setSize(1100, 700);
            introFrame.setLocationRelativeTo(null);
            
            // Set dark theme colors
            Color darkBlue = new Color(15, 23, 42);
            Color pastelAccent = new Color(173, 216, 230);
            
            // Create main panel with border layout
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(darkBlue);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
            
            // Create header panel for logos and titles
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(darkBlue);
            
            // Create logo panel
            JPanel logoPanel = new JPanel(new BorderLayout());
            logoPanel.setBackground(darkBlue);
            logoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
            
            // Load and create logo labels
            JLabel leftLogo = createLogoLabel("ssuet.png", "SSUET Logo");
            JLabel rightLogo = createLogoLabel("sed.png", "SED Logo");
            
            // Add logos to logo panel
            logoPanel.add(leftLogo, BorderLayout.WEST);
            logoPanel.add(rightLogo, BorderLayout.EAST);
            
            // Add center text panel between logos
            JPanel centerTextPanel = new JPanel();
            centerTextPanel.setLayout(new BoxLayout(centerTextPanel, BoxLayout.Y_AXIS));
            centerTextPanel.setBackground(darkBlue);
            
            // Create center labels
            JLabel universityLabel = new JLabel("Sir Syed University Of Engineering & Technology", JLabel.CENTER);
            universityLabel.setFont(new Font("times new roman", Font.BOLD, 25));
            universityLabel.setForeground(Color.lightGray);
            universityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel deptLabel = new JLabel("Software Engineering Department", JLabel.CENTER);
            deptLabel.setFont(new Font("times new roman", Font.BOLD, 25));
            deptLabel.setForeground(Color.lightGray);
            deptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel osLabel = new JLabel("Operating System Project", JLabel.CENTER);
            osLabel.setFont(new Font("times new roman", Font.BOLD, 25));
            osLabel.setForeground(Color.lightGray);
            osLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            centerTextPanel.add(universityLabel);
            centerTextPanel.add(Box.createVerticalStrut(5));
            centerTextPanel.add(deptLabel);
            centerTextPanel.add(Box.createVerticalStrut(5));
            centerTextPanel.add(osLabel);
            
            // Add center text to logo panel
            logoPanel.add(centerTextPanel, BorderLayout.CENTER);
            
            // Create title section panel
            JPanel titleSection = new JPanel();
            titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
            titleSection.setBackground(darkBlue);
            titleSection.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            
            // Create main title label
            JLabel titleLabel = new JLabel("Burst Aware Dynamic Time Quantum RR Simulator", JLabel.CENTER);
            titleLabel.setFont(new Font("times new roman", Font.BOLD, 27));
            titleLabel.setForeground(pastelAccent);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            
            // Add main title to title section
            titleSection.add(titleLabel);
            
            // Add logo panel and title section to header panel
            headerPanel.add(logoPanel, BorderLayout.NORTH);
            headerPanel.add(titleSection, BorderLayout.CENTER);
            
            // Create description text area
            
JTextArea description = new JTextArea();
            description.setText("This application simulates Burst Aware Dynamic RR CPU scheduling Algorithm:\n\n"
                    + "- You can add processes, specify their arrival and burst times, \n" +
                    "- Visualize the scheduling with Gantt chart\n" +
                    "- View the process information including the waiting time, turn around time\n  and the time quantum taken in each round" +
                    "\n\n" +
                    "- You can run regular round robin on the provided time quantum, \n" +
                    "- Compare the results of original & proposed Algorithm.");
            description.setFont(new Font("times new roman", Font.BOLD, 19));
            description.setForeground(pastelAccent);
            description.setBackground(darkBlue);
            description.setEditable(false);
            description.setLineWrap(true);
            description.setWrapStyleWord(true);
        
            // Create button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(darkBlue);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
            
            // Create start button
            JButton startButton = new JButton("Start Scheduling Simulator");
            startButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
            startButton.setForeground(darkBlue);
            startButton.setBackground(pastelAccent);
            startButton.setFocusPainted(false);
            startButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(pastelAccent.darker(), 2),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
            ));
            
            // Add action listener to the button
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Close the intro frame
                    introFrame.dispose();
                    
                    // Open the CPU scheduler application
                    SwingUtilities.invokeLater(() -> {
                        CPUSchedulerGUI scheduler = new CPUSchedulerGUI();
                        scheduler.setVisible(true);
                    });
                }
            });
            
            // Add components to panels
            buttonPanel.add(startButton);
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(description, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            // Add main panel to frame
            introFrame.add(mainPanel);
            
            // Make frame visible
            introFrame.setVisible(true);
        });
    }
    
    // Helper method to create logo labels
    private static JLabel createLogoLabel(String imageName, String altText) {
        try {
            // Load image from src folder using getResource
            java.net.URL imageURL = MainApp.class.getResource("/" + imageName);
            if (imageURL != null) {
                ImageIcon originalIcon = new ImageIcon(imageURL);
                Image originalImage = originalIcon.getImage();
                
                // Scale the image to appropriate size (bigger size)
                Image scaledImage = originalImage.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                
                JLabel logoLabel = new JLabel(scaledIcon);
                logoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return logoLabel;
            } else {
                // If image file doesn't exist, create a placeholder
                JLabel placeholderLabel = new JLabel(altText, JLabel.CENTER);
                placeholderLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                placeholderLabel.setForeground(new Color(173, 216, 230));
                placeholderLabel.setPreferredSize(new Dimension(120, 120));
                placeholderLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(173, 216, 230), 2),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
                ));
                return placeholderLabel;
            }
        } catch (Exception e) {
            // If there's any error loading the image, create a placeholder
            JLabel placeholderLabel = new JLabel(altText, JLabel.CENTER);
            placeholderLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            placeholderLabel.setForeground(new Color(173, 216, 230));
            placeholderLabel.setPreferredSize(new Dimension(120, 120));
            placeholderLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(173, 216, 230), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            return placeholderLabel;
        }
    }
}