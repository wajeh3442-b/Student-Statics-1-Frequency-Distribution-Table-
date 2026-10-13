
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Main extends JFrame {

    private JTextArea inputArea;
    private JTable table;
    private DefaultTableModel model;
    private JLabel summary;

    public Main () {
        setTitle("Frequency Distribution Calculator");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        main.setBackground(new Color(235, 242, 250));
        setContentPane(main);

        JLabel title = new JLabel(
                "FREQUENCY DISTRIBUTION TABLE", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 23));
        title.setForeground(new Color(20, 65, 130));
        main.add(title, BorderLayout.NORTH);

        inputArea = new JTextArea(5, 40);
        inputArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        inputArea.setText(
                "2.2, 4.1, 3.5, 4.5, 3.2, 3.7, 3.0, 2.6,\n" +
                        "3.4, 1.6, 3.1, 3.3, 3.8, 3.1, 4.7, 3.7,\n" +
                        "2.5, 4.3, 3.4, 3.6, 2.9, 3.3, 3.9, 3.1,\n" +
                        "3.3, 3.1, 3.7, 4.4, 3.2, 4.1, 1.9, 3.4,\n" +
                        "4.7, 3.8, 3.2, 2.6, 3.9, 3.0, 4.2, 3.5"
        );

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                "Enter Data (separate values with commas or spaces)"));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);

        JButton calculate = new JButton("Generate Frequency Table");
        calculate.setBackground(new Color(30, 105, 190));
        calculate.setForeground(Color.WHITE);
        calculate.addActionListener(e -> calculateTable());

        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> {
            inputArea.setText("");
            model.setRowCount(0);
            summary.setText("Enter your data and generate the table.");
        });

        JPanel buttons = new JPanel();
        buttons.setBackground(Color.WHITE);
        buttons.add(calculate);
        buttons.add(clear);
        inputPanel.add(buttons, BorderLayout.SOUTH);

        String[] columns = {
                "Class Interval",
                "Class Boundaries",
                "Class Midpoint",
                "Frequency (f)"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 14));

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.add(inputPanel, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        summary = new JLabel("Ready to calculate.");
        summary.setFont(new Font("Arial", Font.BOLD, 14));

        main.add(center, BorderLayout.CENTER);
        main.add(summary, BorderLayout.SOUTH);

        calculateTable();
    }

    private void calculateTable() {
        try {
            String input = inputArea.getText().trim();

            if (input.isEmpty()) {
                throw new IllegalArgumentException(
                        "Please enter numerical values.");
            }

            String[] tokens = input.split("[,;\\s]+");
            double[] values = new double[tokens.length];

            double min = Double.POSITIVE_INFINITY;
            double max = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < tokens.length; i++) {
                values[i] = Double.parseDouble(tokens[i]);

                if (!Double.isFinite(values[i])) {
                    throw new IllegalArgumentException(
                            "Enter only finite numbers.");
                }

                min = Math.min(min, values[i]);
                max = Math.max(max, values[i]);
            }

            // Textbook settings: 7 classes, width 0.5, start 1.5.
            int numberOfClasses = 7;
            double width = 0.5;
            double start = 1.5;
            double offset = 0.05;

            // Ensure all entered data fit the textbook's classes.
            if (min < start || max > start + numberOfClasses * width) {
                throw new IllegalArgumentException(
                        "The values must be between 1.5 and 5.0 " +
                                "for the textbook's seven class intervals.");
            }

            int[] frequency = new int[numberOfClasses];

            for (double value : values) {
                int index = (int) Math.floor(
                        (value - start) / width + 1e-9);

                if (index == numberOfClasses) {
                    index--;
                }

                if (index < 0 || index >= numberOfClasses) {
                    throw new IllegalArgumentException(
                            "A value falls outside the class intervals.");
                }

                frequency[index]++;
            }

            model.setRowCount(0);
            int total = 0;

            for (int i = 0; i < numberOfClasses; i++) {
                double lower = start + i * width;
                double upper = lower + width;
                double midpoint = (lower + upper) / 2.0;

                String interval = String.format(
                        "%.1f-%.1f", lower, upper - 0.1);

                String boundaries = String.format(
                        "%.2f-%.2f", lower - offset, upper - offset);

                model.addRow(new Object[]{
                        interval,
                        boundaries,
                        String.format("%.1f", midpoint),
                        frequency[i]
                });

                total += frequency[i];
            }

            model.addRow(new Object[]{
                    "TOTAL", "-", "-", total
            });

            summary.setText(
                    "Observations: " + values.length
                            + "   |   Minimum: " + min
                            + "   |   Maximum: " + max
                            + "   |   Range: " + String.format("%.1f", max - min)
                            + "   |   Class Width: 0.5"
                            + "   |   Total Frequency: " + total
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numerical values.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main ().setVisible(true);
        });
    }
}