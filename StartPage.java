import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class StartPage implements  Runnable {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new StartPage());
    }

    @Override
    public void run() {
        JFrame frame = new JFrame("User Info");
        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;

        JLabel username = new JLabel("Username");
        panel.add(username, gridBagConstraints);
        JTextField nameText = new JTextField(20);
        gridBagConstraints.gridx = 1;
        panel.add(nameText, gridBagConstraints);
        JLabel nameLabel = new JLabel();
        nameLabel.setVisible(false);
        panel.add(nameLabel, gridBagConstraints);

        JLabel password = new JLabel("Password");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        panel.add(password, gridBagConstraints);
        JTextField emailText = new JTextField(20);
        gridBagConstraints.gridx = 1;
        panel.add(emailText, gridBagConstraints);
        JLabel emailLabel = new JLabel();
        emailLabel.setVisible(false);
        panel.add(emailLabel, gridBagConstraints);

        JButton login = new JButton("Login");
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        panel.add(login, gridBagConstraints);

        // EDIT
        JButton createAccount = new JButton("Create Account");
        gridBagConstraints.gridx = 4;
        panel.add(createAccount, gridBagConstraints);

        frame.add(panel);
        frame.setSize(500,400);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // SAVE BUTTON LISTENER
        login.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameText.setVisible(false);
                emailText.setVisible(false);
                nameLabel.setText(nameText.getText());
                emailLabel.setText(emailText.getText());
                nameLabel.setVisible(true);
                emailLabel.setVisible(true);
            }
        });

        createAccount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameText.setVisible(false);
                emailText.setVisible(false);
                nameLabel.setText(nameText.getText());
                emailLabel.setText(emailText.getText());
                nameLabel.setVisible(true);
                emailLabel.setVisible(true);
            }
        });



    }

}
