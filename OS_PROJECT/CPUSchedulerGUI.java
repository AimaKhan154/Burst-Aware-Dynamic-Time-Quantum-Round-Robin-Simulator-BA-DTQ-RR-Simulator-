import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


public class CPUSchedulerGUI extends JFrame {

    private DefaultTableModel processTableModel;
    private JTable processTable;
    private JLabel avgWTLabel, avgTATLabel;
    private List<Process> processes;
    private JPanel ganttPanel;
    private JScrollPane ganttScroll;
    private List<String> ganttChartProcesses;
    private List<Double> ganttChartTimes;
    private List<Double> quantumPerRound; // Store quantum per round

    private JButton roundRobinButton; // Added for RR

    public CPUSchedulerGUI() {
        setTitle("CPU Scheduling Algorithm - Burst Aware DTQ Round Robin");
        setSize(1100, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        Color darkBlue = new Color(15, 23, 42);
        Color pastelText = new Color(240, 240, 255);
        Color pastelAccent = new Color(173, 216, 230);
        Color tableBg = new Color(20, 30, 50);
        Color headerBg = new Color(35, 50, 80);

        getContentPane().setBackground(darkBlue);

        // Top panel for process input
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(pastelAccent, 3),
                "Process Input",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 17),
                pastelAccent
        ));
        inputPanel.setBackground(darkBlue);

        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time"};
        processTableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(processTableModel);
        processTable.setFont(new Font("Segoe UI", Font.BOLD, 15));
        processTable.setBackground(tableBg);
        processTable.setForeground(pastelText);
        processTable.setGridColor(pastelAccent);
        processTable.setRowHeight(24);
        processTable.setFillsViewportHeight(true);

        JTableHeader header = processTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBackground(darkBlue);
        header.setForeground(pastelAccent);
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createLineBorder(pastelAccent));

        JScrollPane scrollPane = new JScrollPane(processTable);
        scrollPane.setPreferredSize(new Dimension(400, 290));
        scrollPane.getViewport().setBackground(darkBlue);
        scrollPane.setBorder(BorderFactory.createLineBorder(darkBlue));

        // Buttons to add/remove processes
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 90, 10));
        btnPanel.setBackground(darkBlue);

        JButton addBtn = new JButton("Add Process");
        JButton removeBtn = new JButton("Remove Selected");

        styleButton(addBtn, pastelAccent, darkBlue);
        styleButton(removeBtn, pastelAccent, darkBlue);
        addBtn.setPreferredSize(new Dimension(130, 40));
        removeBtn.setPreferredSize(new Dimension(180, 40));
        btnPanel.add(addBtn);
        btnPanel.add(removeBtn);
        addBtn.addActionListener(e -> addProcessRow());
        removeBtn.addActionListener(e -> removeSelectedRow());

        inputPanel.add(scrollPane, BorderLayout.CENTER);
        inputPanel.add(btnPanel, BorderLayout.SOUTH);

        // Center panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(darkBlue);

        // Calculate Button
        JButton calcBtn = new JButton("Calculate Schedule");
        calcBtn.setFont(new Font("Arial", Font.BOLD, 14));
        styleButton(calcBtn, pastelAccent, darkBlue);
        calcBtn.addActionListener(e -> calculateAndDisplay());
        calcBtn.setPreferredSize(new Dimension(190, 30));

        // Gantt Panel
        ganttPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGanttChart(g);
            }
        };
        ganttPanel.setPreferredSize(new Dimension(900, 100));
        ganttPanel.setBackground(new Color(20, 30, 50));
        ganttPanel.setLayout(null);

        ganttScroll = new JScrollPane(ganttPanel);
        ganttScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(pastelAccent, 3),
                "Gantt Chart",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 17),
                pastelAccent
        ));
        ganttScroll.setBackground(darkBlue);

        // Labels for averages
        avgWTLabel = new JLabel("Average Waiting Time: ");
        avgTATLabel = new JLabel("Average Turnaround Time: ");
        styleLabels(pastelAccent);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(darkBlue);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        avgWTLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        avgTATLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        statsPanel.add(avgWTLabel);
        statsPanel.add(Box.createVerticalStrut(10));
        statsPanel.add(avgTATLabel);
        statsPanel.setPreferredSize(new Dimension(300, 60));
        statsPanel.setBackground(tableBg);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(darkBlue);
        bottomPanel.add(statsPanel, BorderLayout.WEST);
        bottomPanel.add(calcBtn, BorderLayout.EAST);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));

        // Initialize the RR button and add
        roundRobinButton = new JButton("Run Round Robin");
        styleButton(roundRobinButton, pastelAccent, darkBlue);
        roundRobinButton.setPreferredSize(new Dimension(170, 30));
        roundRobinButton.addActionListener(e -> runRoundRobinPrompt());
        bottomPanel.add(roundRobinButton, BorderLayout.WEST); // Added to bottom panel

        add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(ganttScroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleLabels(Color color) {
        Font font = new Font("Arial", Font.BOLD, 16);
        avgWTLabel.setFont(font);
        avgTATLabel.setFont(font);
        avgWTLabel.setForeground(color);
        avgTATLabel.setForeground(color);
    }

    private void styleButton(JButton btn, Color fg, Color bg) {
        btn.setBackground(bg.darker());
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("ARIAL", Font.BOLD, 15));
        btn.setBorder(BorderFactory.createLineBorder(fg));
    }

    private void addProcessRow() {
        processTableModel.addRow(new Object[]{"P" + (processTableModel.getRowCount() + 1), 0, 1});
    }

    private void removeSelectedRow() {
        int selectedRow = processTable.getSelectedRow();
        if (selectedRow != -1) {
            processTableModel.removeRow(selectedRow);
        }
    }

    private void calculateAndDisplay() {
        processes = new ArrayList<>();
        for (int i = 0; i < processTableModel.getRowCount(); i++) {
            String id = (String) processTableModel.getValueAt(i, 0);
            double at, bt;
            try {
                at = Double.parseDouble(processTableModel.getValueAt(i, 1).toString());
                bt = Double.parseDouble(processTableModel.getValueAt(i, 2).toString());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input at row " + (i + 1));
                return;
            }
            processes.add(new Process(id, at, bt));
        }

        if (processes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No processes to schedule!");
            return;
        }

        // Initialize quantum per round list
        quantumPerRound = new ArrayList<>();
        runScheduling();
        ganttPanel.repaint();

        double totalTAT = 0, totalWT = 0;
        for (Process p : processes) {
            totalTAT += p.tat;
            totalWT += p.wt;
        }

        avgWTLabel.setText(String.format("Average Waiting Time: %.2f", totalWT / processes.size()));
        avgTATLabel.setText(String.format("Average Turnaround Time: %.2f", totalTAT / processes.size()));

        showProcessDetails(quantumPerRound);
    }

    private void runScheduling() {
        for (Process p : processes) {
            p.reset();
        }

        processes.sort(Comparator.comparingDouble(p -> p.at));
        double currentTime = 0;
        ganttChartProcesses = new ArrayList<>();
        ganttChartTimes = new ArrayList<>();
        ganttChartTimes.add(0.0);
        int completed = 0;
        int n = processes.size();

        while (completed < n) {
            List<Process> readyQueue = new ArrayList<>();
            for (Process p : processes) {
                if (p.at <= currentTime && !p.completed) {
                    readyQueue.add(p);
                }
            }

            if (readyQueue.isEmpty()) {
                double nextAt = Double.MAX_VALUE;
                for (Process p : processes) {
                    if (!p.completed && p.at > currentTime && p.at < nextAt) {
                        nextAt = p.at;
                    }
                }
                ganttChartProcesses.add("Idle");
                ganttChartTimes.add(nextAt);
                currentTime = nextAt;
                continue;
            }
// Calculate sum of inverse burst times (sumWi)
            double sumWi = 0;
            for (Process p : readyQueue) {
                sumWi += 1.0 / p.remainingBt;// sumWi = Σ(1 / remainingBt)
            }
            // Dynamic quantum formula
            double tq = Math.ceil(readyQueue.size() / sumWi);
            quantumPerRound.add(tq); // Store quantum for this round

            readyQueue.sort(Comparator.comparingDouble(p -> p.remainingBt));

            for (Process p : readyQueue) {
                ganttChartProcesses.add(p.id);
                if (p.remainingBt <= tq) {
                    currentTime += p.remainingBt;
                    ganttChartTimes.add(currentTime);
                    p.remainingBt = 0;
                    p.completed = true;
                    p.ct = currentTime;
                    completed++;
                } else {
                    currentTime += tq;
                    ganttChartTimes.add(currentTime);
                    p.remainingBt -= tq;
                }
            }
        }

        for (Process p : processes) {
            p.tat = p.ct - p.at;
            p.wt = p.tat - p.bt;
        }
    }

    private void drawGanttChart(Graphics g) {
        if (ganttChartProcesses == null || ganttChartProcesses.isEmpty()) return;

        int width = ganttPanel.getWidth();
        int height = ganttPanel.getHeight();
        double totalTime = ganttChartTimes.get(ganttChartTimes.size() - 1);
        int chartStartX = 50;
        int chartY = height / 3;
        int barHeight = 40;
        double scale = (double) (width - 2 * chartStartX) / totalTime;

        int x = chartStartX;
        for (int i = 0; i < ganttChartProcesses.size(); i++) {
            String proc = ganttChartProcesses.get(i);
            double startTime = ganttChartTimes.get(i);
            double endTime = ganttChartTimes.get(i + 1);
            int barWidth = (int) ((endTime - startTime) * scale);

            Color color = proc.equals("Idle") ? Color.GRAY : getColorForProcess(proc);
            g.setColor(color);
            g.fillRect(x, chartY, barWidth, barHeight);
            g.setColor(Color.BLACK);
            g.drawRect(x, chartY, barWidth, barHeight);

            FontMetrics fm = g.getFontMetrics();
            int labelWidth = fm.stringWidth(proc);
            g.setColor(Color.white);
            g.drawString(proc, x + (barWidth - labelWidth) / 2, chartY + (barHeight + fm.getAscent()) / 2 - 2);
            x += barWidth;
        }

        g.setColor(Color.WHITE);
        for (int i = 0; i < ganttChartTimes.size(); i++) {
            double time = ganttChartTimes.get(i);
            int markerX = (int) (chartStartX + time * scale);
            g.drawLine(markerX, chartY + barHeight + 5, markerX, chartY + barHeight + 15);
            String timeStr = String.format("%.1f", time);
            g.drawString(timeStr, markerX - 10, chartY + barHeight + 30);
        }
    }

    private Color getColorForProcess(String processId) {
        int hash = processId.hashCode();
        Random rand = new Random(hash);
        return new Color(rand.nextInt(200) + 20, rand.nextInt(200) + 20, rand.nextInt(200) + 20);
    }

    private void showProcessDetails(List<Double> quantumPerRound) {
        // Calculate total TAT and WT for averages
        double totalTAT = 0;
        double totalWT = 0;
        for (Process p : processes) {
            totalTAT += p.tat;
            totalWT += p.wt;
        }
        double avgTAT = processes.isEmpty() ? 0 : totalTAT / processes.size();
        double avgWT = processes.isEmpty() ? 0 : totalWT / processes.size();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s%-8s%-8s%-8s%-8s%-8s\n", "ID", "AT", "BT", "CT", "TAT", "WT"));
        for (Process p : processes) {
            sb.append(String.format("%-8s%-8.1f%-8.1f%-8.1f%-8.1f%-8.1f\n",
                    p.id, p.at, p.bt, p.ct, p.tat, p.wt));
        }

        sb.append("\n");
        sb.append(String.format("Average WT: %.2f    |    Average TAT: %.2f\n\n", avgWT, avgTAT));

        // Append Quantum per Round
        sb.append("Quantum per Round:\n");
        for (int i=0; i<quantumPerRound.size(); i++) {
            sb.append(String.format("Round %d: %.2f\n", i + 1, quantumPerRound.get(i)));
        }

        // Create JTextArea for data
        JTextArea detailArea = new JTextArea(sb.toString());
        detailArea.setEditable(false);
        detailArea.setBackground(new Color(20, 30, 50));
        detailArea.setForeground(Color.white);
        detailArea.setFont(new Font("Consolas", Font.BOLD, 14));
        detailArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(detailArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(35, 50, 80), 2));

        // Create frame for process details
        JFrame detailFrame = new JFrame("Process Details");
        detailFrame.setSize(640, 500);
        detailFrame.setLocationRelativeTo(this);
        detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailFrame.getContentPane().setBackground(new Color(20, 30, 50));
        detailFrame.setLayout(new BorderLayout());

        // Main heading
        JLabel mainHeading = new JLabel("Process Details", JLabel.CENTER);
        mainHeading.setFont(new Font("Serif", Font.BOLD, 16));
        mainHeading.setForeground(new Color(240, 240, 255));
        mainHeading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Algorithm name just above the table
        JLabel algoNameLabel = new JLabel("Burst Aware Dynamic Time Quantum Round Robin:", JLabel.CENTER);
        algoNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        algoNameLabel.setForeground(new Color(240, 240, 255));
        algoNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Panel for top part
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(20, 30, 50));
        topPanel.add(mainHeading, BorderLayout.NORTH);
        topPanel.add(algoNameLabel, BorderLayout.SOUTH);

        // Show the frame
        JFrame detailsFrame = new JFrame("Process Details");
        detailsFrame.setSize(640, 500);
        detailsFrame.setLocationRelativeTo(this);
        detailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailsFrame.getContentPane().setBackground(new Color(20, 30, 50));
        detailsFrame.setLayout(new BorderLayout());

        detailsFrame.add(topPanel, BorderLayout.NORTH);
        detailsFrame.add(scrollPane, BorderLayout.CENTER);
        detailsFrame.setVisible(true);
    }

    // Added methods for Round Robin
    private void runRoundRobinPrompt() {
        String input = JOptionPane.showInputDialog(this, "Enter Time Quantum:", "Round Robin Quantum", JOptionPane.PLAIN_MESSAGE);
        if (input != null) {
            try {
                double quantum = Double.parseDouble(input);
                runRoundRobinSchedule(quantum);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input for quantum!");
            }
        }
    }

    private void runRoundRobinSchedule(double quantum) {
        List<Process> rrProcesses = new ArrayList<>();
        for (int i = 0; i < processTableModel.getRowCount(); i++) {
            String id = (String) processTableModel.getValueAt(i, 0);
            double at, bt;
            try {
                at = Double.parseDouble(processTableModel.getValueAt(i, 1).toString());
                bt = Double.parseDouble(processTableModel.getValueAt(i, 2).toString());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input at row " + (i + 1));
                return;
            }
            rrProcesses.add(new Process(id, at, bt));
        }

        Queue<Process> queue = new LinkedList<>();
        double currentTime = 0;
        int completed = 0;
        double totalWT = 0, totalTAT = 0;

        rrProcesses.sort(Comparator.comparingDouble(p -> p.at));

        int i = 0;
        while (i < rrProcesses.size() && rrProcesses.get(i).at <= currentTime) {
            queue.add(rrProcesses.get(i));
            i++;
        }

        while (completed < rrProcesses.size()) {
            if (queue.isEmpty()) {
                currentTime = rrProcesses.get(i).at;
                queue.add(rrProcesses.get(i));
                i++;
                continue;
            }

            Process currentProc = queue.poll();
            double execTime = Math.min(quantum, currentProc.remainingBt);
            currentTime += execTime;
            currentProc.remainingBt -= execTime;

            while (i < rrProcesses.size() && rrProcesses.get(i).at <= currentTime) {
                queue.add(rrProcesses.get(i));
                i++;
            }

            if (currentProc.remainingBt > 0) {
                queue.add(currentProc);
            } else {
                currentProc.ct = currentTime;
                currentProc.tat = currentProc.ct - currentProc.at;
                currentProc.wt = currentProc.tat - currentProc.bt;
                totalWT += currentProc.wt;
                totalTAT += currentProc.tat;
                completed++;
            }
        }

        processes = rrProcesses;
        // After scheduling, display the results in a new window
        showRoundRobinResults(processes, quantum);
    }

    private void showRoundRobinResults(List<Process> processList, double quantum) {
        // Calculate total TAT and WT for averages
        double totalTAT = 0;
        double totalWT = 0;
        for (Process p : processList) {
            totalTAT += p.tat;
            totalWT += p.wt;
        }
        double avgTAT = processList.isEmpty() ? 0 : totalTAT / processList.size();
        double avgWT = processList.isEmpty() ? 0 : totalWT / processList.size();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s%-8s%-8s%-8s%-8s%-8s\n", "ID", "AT", "BT", "CT", "TAT", "WT"));
        for (Process p : processList) {
            sb.append(String.format("%-8s%-8.1f%-8.1f%-8.1f%-8.1f%-8.1f\n",
                    p.id, p.at, p.bt, p.ct, p.tat, p.wt));
        }

        sb.append("\n");
        sb.append(String.format("Average WT: %.2f    |    Average TAT: %.2f\n\n", avgWT, avgTAT));
        sb.append("Quantum used: ").append(quantum).append("\n\n");

        // Create JTextArea for data
        JTextArea detailArea = new JTextArea(sb.toString());
        detailArea.setEditable(false);
        detailArea.setBackground(new Color(20, 30, 50));
        detailArea.setForeground(Color.white);
        detailArea.setFont(new Font("Consolas", Font.BOLD, 14));
        detailArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(detailArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(35, 50, 80), 2));

        // Create frame for process results
        JFrame resultFrame = new JFrame("Round Robin Process Results");
        resultFrame.setSize(640, 500);
        resultFrame.setLocationRelativeTo(this);
        resultFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        resultFrame.getContentPane().setBackground(new Color(20, 30, 50));
        resultFrame.setLayout(new BorderLayout());

        // Top label
        JLabel heading = new JLabel("Round Robin Algorithm Results", JLabel.CENTER);
        heading.setFont(new Font("Serif", Font.BOLD, 16));
        heading.setForeground(new Color(240, 240, 255));
        heading.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Add components
        resultFrame.add(heading, BorderLayout.NORTH);
        resultFrame.add(scrollPane, BorderLayout.CENTER);
        resultFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CPUSchedulerGUI().setVisible(true));
    }

    class Process {
        String id;
        double at, bt, remainingBt, ct, tat, wt;
        boolean completed;

        Process(String id, double at, double bt) {
            this.id = id;
            this.at = at;
            this.bt = bt;
            this.remainingBt = bt;
            this.completed = false;
        }

        void reset() {
            this.remainingBt = bt;
            this.ct = 0;
            this.tat = 0;
            this.wt = 0;
            this.completed = false;
        }
    }}