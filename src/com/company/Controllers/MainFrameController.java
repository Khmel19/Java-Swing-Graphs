package com.company.Controllers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;


public class MainFrameController {
    TableController tableController = new TableController();
    private boolean stop = false;
    private JButton createGraphABt = new JButton("      Draw a graph A ->");
    private JButton createGraphBBt = new JButton("      Draw a graph B ->");
    private JButton stopAllBt = new JButton("Stop all");
    private JLabel xTaskATextTo = new JLabel("    x to -> 10");
    private JLabel functionTaskB = new JLabel("Function B : f(x) = ");
    private JLabel xTaskBText = new JLabel("x is [0, 0.5]");
    private JLabel hTaskBText = new JLabel("h = 0.01");
    private JLabel functionTaskA = new JLabel("   Function A : f(x) = ");
    private JLabel xTaskATextFrom = new JLabel("  x from -> 1");
    private JLabel inputNLabel = new JLabel("input n: ");
    private JTextField inputNField = new JTextField();
    private ImageIcon fuctionBIcon = new ImageIcon(MainFrameController.class.getResource("/com/company/images/functionB.png"));
    private ImageIcon fuctionAIcon = new ImageIcon(MainFrameController.class.getResource("/com/company/images/functionA.png"));
    private JLabel imageFuncALabel = new JLabel(fuctionAIcon);
    private JLabel imageFuncBLabel = new JLabel(fuctionBIcon);
    private Thread threadB = new Thread();
    private Thread threadA = new Thread();
    private JPanel tableBPanel;
    static JPanel functionAPanel;
    private JTable funcBTable;
    private JScrollPane scrollPane;

    public JPanel createInfoPanel() {
        JPanel panel = new JPanel();

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(LEADING)
                        .addComponent(stopAllBt)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(functionTaskA)
                                .addComponent(imageFuncALabel))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(xTaskATextFrom)
                                .addComponent(xTaskATextTo))
                        .addComponent(createGraphABt)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(functionTaskB)
                                .addComponent(imageFuncBLabel))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(xTaskBText)
                                .addComponent(hTaskBText)
                                .addComponent(inputNLabel)
                                .addComponent(inputNField))
                        .addComponent(createGraphBBt))
        );

        layout.linkSize(SwingConstants.HORIZONTAL, functionTaskA, xTaskATextFrom,
                xTaskATextTo, functionTaskB, xTaskBText, hTaskBText,
                inputNLabel, inputNField, imageFuncBLabel, imageFuncALabel);

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(stopAllBt)
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(functionTaskA)
                        .addComponent(imageFuncALabel))
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(xTaskATextFrom)
                        .addComponent(xTaskATextTo))
                .addComponent(createGraphABt)
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(functionTaskB)
                        .addComponent(imageFuncBLabel))
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(xTaskBText)
                        .addComponent(hTaskBText)
                        .addComponent(inputNLabel)
                        .addComponent(inputNField))
                .addComponent(createGraphBBt)
        );
        return panel;
    }

    public JPanel createTableBPanel(JScrollPane scrollPane) {
        JPanel panel = new JPanel();
        scrollPane.setMinimumSize(new Dimension(100, 180));
        panel.setLayout(new BorderLayout());

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    public void action(JFrame mainFrame, JSplitPane splitPaneTableGraph, ChartDraw controller) {
        createGraphABt.addActionListener(e -> {
            stop = false;
            if (threadA.isAlive()) {
                threadA.stop();
            }
            controller.clearPointList("A");
            threadA = new Thread(new Runnable() {
                @Override
                public void run() {
                    int x = 1;
                    while (x <= 10 && !stop) {
                        int F = functionA(x);
                        Point point = new Point(x * 100, F * 10000);
                        controller.addPointToList(point, "A");
                        x += 1;
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException en) {
                            JOptionPane.showMessageDialog(mainFrame, "Ошибка!\nЧто-то пошло не так!");
                        }
                    }
                }

                int functionA(int x) {
                    return 4 * x - 1;
                }
            });

            threadA.start();
            splitPaneTableGraph.setLeftComponent(functionAPanel);

        });

        createGraphBBt.addActionListener(e -> {
            if (splitPaneTableGraph.getLeftComponent() != null) {
                tableBPanel.remove(scrollPane);
            }
            if (inputNField.getText() != null) {
                try {
                    if (Integer.valueOf(inputNField.getText()) instanceof Integer) {
                        funcBTable = tableController.createTable();
                        scrollPane = new JScrollPane(funcBTable);
                        tableBPanel = createTableBPanel(scrollPane);
                        splitPaneTableGraph.setLeftComponent(tableBPanel);
                        stop = false;
                        if (threadB.isAlive()) {
                            threadB.stop();
                        }
                        controller.clearPointList("B");
                        threadB = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                double x = 0;
                                while (x <= 50 && !stop) {
                                    double F = functionB(x);

                                    Point point = new Point((int) x, (int) (F * 100));
                                    controller.addPointToList(point, "B");
                                    funcBTable = tableController.addRow(funcBTable, String.format("%.4f", (x / 100)), String.format("%.4f", (F / 100)));
                                    x += 1;
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException en) {
                                        JOptionPane.showMessageDialog(mainFrame, "Ошибка!\nЧто-то пошло не так!");
                                    }
                                }
                            }

                            private double functionB(double x) {
                                double F = 0;
                                double value = 1;
                                int k = 0;
                                while (value >= 0.01 && k < Integer.valueOf(inputNField.getText())) {
                                    value = (pow(-1, k) * pow(x, 2 * k)) / (factorial(k));
                                    F += value;
                                    k++;
                                }
                                return abs(F);
                            }

                            private double factorial(int n) {
                                int result = 1;
                                for (int i = 1; i <= n; i++) {
                                    result = result * i;
                                }
                                return result;
                            }
                        });

                        threadB.start();
                    }
                } catch (Exception e1) {
                    JOptionPane.showMessageDialog(mainFrame, "Ошибка!\nЧто-то пошло не так!");
                }
            }
        });
        stopAllBt.addActionListener(e -> stop = !stop);
    }
}
