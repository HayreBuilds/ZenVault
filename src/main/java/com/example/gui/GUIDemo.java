package com.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class Banksystem{

}
class Users{

}


public class GUIDemo extends JFrame {
    private static final Color BACKGROUND_COLOR     = new Color(245, 247, 250);  // #F5F7FA — light grayish background
    private static final Color TEXT_COLOR           = new Color(33, 37, 41);     // #212529 — strong dark text
    private static final Color BUTTON_COLOR         = new Color(0, 123, 255);    // #007BFF — Bootstrap primary blue
    private static final Color BUTTON_HOVER         = new Color(0, 105, 217);    // #0069D9 — darker blue hover
    private static final Color SECONDARY_COLOR      = new Color(108, 117, 125);  // #6C757D — muted gray text/button
    private static final Color SECONDARY_HOVER      = new Color(73, 80, 87);     // #495057 — darker hover
    GUIDemo(){
        init();
    }
    public void init(){
        setTitle("jovan Ai Banking");
        setSize(1080, 750);
        setMinimumSize(new Dimension(700,500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        JPanel mainPanel = new JPanel(new BorderLayout(20,20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 123, 255),       // Start color (top)
                        0, getHeight(), new Color(102, 178, 255) // End color (bottom)
                );

                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        headerPanel.setPreferredSize(new Dimension(getWidth(),80));

        JLabel welcomeLabel = new JLabel("Welcome ");
        welcomeLabel.setFont(new Font("Roboto",Font.BOLD,24));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel);

        GridBagLayout gridBagLayout = new GridBagLayout();

        mainPanel.add(headerPanel,BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(gridBagLayout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets= new Insets(15,15,15,15);
        gbc.weightx=1.0;
        gbc.fill=GridBagConstraints.HORIZONTAL;

        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshPanel.setBackground(BACKGROUND_COLOR);

        JButton refreshButton = createStyledButton("Refresh",true);
        refreshPanel.add(refreshButton);

        gbc.gridx=0;
        gbc.gridy=0;
        gbc.fill=GridBagConstraints.NONE;
        gbc.anchor=GridBagConstraints.EAST;
        contentPanel.add(refreshPanel,gbc);


        JPanel infoPanel = new JPanel(gridBagLayout);
        GridBagConstraints gbcInfo = new GridBagConstraints();
        infoPanel.setBackground(BACKGROUND_COLOR);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.gray),
                BorderFactory.createEmptyBorder(20,20,20,20)
        ));

        gbcInfo.gridx=0;
        gbcInfo.gridy=0;
        gbcInfo.insets=new Insets(10,0,10,0);
        gbcInfo.fill=GridBagConstraints.HORIZONTAL;

        JLabel infoTitle = new JLabel("Account Information");
        infoTitle.setFont(new Font("Roboto",Font.BOLD,18));
        infoTitle.setForeground(TEXT_COLOR);

        infoPanel.add(infoTitle,gbcInfo);

        gbcInfo.gridy=1;
        JLabel accountNumber = new JLabel("Account Number:16130894");
        accountNumber.setFont(new Font("Roboto",Font.PLAIN,16));
        infoPanel.add(accountNumber,gbcInfo);

        gbcInfo.gridy=2;
        JLabel balance = new JLabel("Balance :$7000");
        balance.setFont(new Font("Roboto",Font.PLAIN,16));
        infoPanel.add(balance,gbcInfo);

        gbcInfo.gridy=3;
        JLabel loan = new JLabel("Loan :$500");
        loan.setFont(new Font("Roboto",Font.PLAIN,16));
        infoPanel.add(loan,gbcInfo);

        gbc.gridy=1;
        gbc.anchor=GridBagConstraints.CENTER;
        gbc.fill=GridBagConstraints.HORIZONTAL;
        contentPanel.add(infoPanel,gbc);


        JPanel actionsPanel = new JPanel(new GridLayout(2,4,15,15));
        actionsPanel.setBackground(BACKGROUND_COLOR);

        // Action buttons
        JButton depositButton = createStyledButton("Deposit Money", true);
        JButton withdrawButton = createStyledButton("Withdraw Money", true);
        JButton transferButton = createStyledButton("Transfer Money", true);
        JButton loanButton = createStyledButton("Loan Management", true);
        JButton historyButton = createStyledButton("Transaction History", true);
        JButton profileButton = createStyledButton("My Profile", true);
        JButton changePinButton = createStyledButton("Change PIN", true);
        JButton logoutButton = createStyledButton("Logout", false);

        // Add buttons to action cards
        actionsPanel.add(createActionCard("Deposit", depositButton));
        actionsPanel.add(createActionCard("Withdraw", withdrawButton));
        actionsPanel.add(createActionCard("Transfer", transferButton));
        actionsPanel.add(createActionCard("Loan", loanButton));
        actionsPanel.add(createActionCard("History", historyButton));
        actionsPanel.add(createActionCard("Profile", profileButton));
        actionsPanel.add(createActionCard("Change PIN", changePinButton));
        actionsPanel.add(createActionCard("Logout", logoutButton));

        gbc.gridy=2;
        contentPanel.add(actionsPanel,gbc);

        mainPanel.add(contentPanel,BorderLayout.CENTER);

        add(mainPanel);

    }

    public JButton createStyledButton(String text,boolean isPrimary){
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Roboto",Font.BOLD,14));
        button.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));

        if(isPrimary){
            button.setBackground(BUTTON_COLOR);
            button.setForeground(Color.WHITE);

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(BUTTON_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(BUTTON_COLOR);
                }
            });
        }else{
            button.setBackground(Color.decode("#FA5D5E"));
            button.setForeground(Color.WHITE);

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
            button.setBackground(Color.decode("#FC3F41"));
                }

                @Override
                public void mouseExited(MouseEvent e) {
            button.setBackground(Color.decode("#FA5D5E"));
                }
            });
        }


        return button;
    }
    public JPanel createActionCard(String text,JButton button){
        JPanel card = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(20,20,20)),
                BorderFactory.createEmptyBorder(15,15,15,15)
        ));
        card.setBackground(BACKGROUND_COLOR);
        constraints.gridx=0;
        constraints.gridy=0;
        constraints.weightx=1.0;
        constraints.insets=new Insets(5,0,5,0);
        constraints.anchor=GridBagConstraints.CENTER;
        constraints.fill=GridBagConstraints.NONE;

        JLabel label = new JLabel(text);
        label.setFont(new Font("Roboto",Font.BOLD,16));
        card.add(label,constraints);

        constraints.gridy=1;
        constraints.insets=new Insets(0,0,0,0);
        card.add(button,constraints);



        return card;
    }


    public static void main(String[] args) {
        new GUIDemo().setVisible(true);
    }
}
