import javax.swing.*;
import javax.swing.event.ListDataEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cilent program
 *
 * Purdue University -- CS18000 -- Spring 2022 -- Project 5
 *
 * @author William Yu, yuwl; Lamiya Laxmidhar, llaxmidh; Mohnish Harwani, mharwan; Ben Hartley, hartleyb;
 * @version July 22, 2023
 */

public class Client {
    public static final String EXIT = "Exiting";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final Pattern EMAIL_REGEX_PATTERN = Pattern.compile(EMAIL_PATTERN);
    public static final String NAME_PATTERN = "[A-Z][a-zA-Z]*";
    public static final Pattern NAME_REGEX_PATTERN = Pattern.compile(NAME_PATTERN);
    public static final String EDIT_MESSAGE_FORMAT = ".*;\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2};.*";
    public static final Pattern EEDIT_MESSAGE_PATTERN = Pattern.compile(EDIT_MESSAGE_FORMAT);
    public static final String DELETE_MESSAGE_FORMAT = "\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2};.*";
    public static final Pattern DELETE_MESSAGE_PATTERN = Pattern.compile(DELETE_MESSAGE_FORMAT);
    public static final String GUI_TITLE = "Messager";
    public static final Object LOCK = new Object();
    public static String GUI_PASS = new String( );

    public static void endProgramDialog() {
        JOptionPane.showMessageDialog(null, "Thank you for using Messenger!",
                "Exiting", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void createAccountPage() {
        final int[] roleOutput = {0};
        ArrayList<String> info = new ArrayList<String>();
        int line = 0;
        JFrame frame = new JFrame("Create Account");
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.gridy = line++;
        JLabel email = new JLabel("Email: ");
        panel.add(email, gridBagConstraints);
        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        JTextField emailText = new JTextField("", 20);
        panel.add(emailText, gridBagConstraints);

        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.gridy = line++;
        JLabel password = new JLabel("Password: ");
        panel.add(password, gridBagConstraints);
        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        JTextField passwordText = new JTextField(20);
        panel.add(passwordText, gridBagConstraints);

        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.gridy = line++;
        JLabel role = new JLabel("Role Menu:");
        panel.add(role, gridBagConstraints);
        JMenuBar roleBar = new JMenuBar();
        JMenu roleMenu = new JMenu("Buyer");
        roleMenu.setFont(new Font("Arial", Font.BOLD, 13));
        roleMenu.setForeground(Color.RED);
        JMenuItem buyerItem = new JMenuItem("Buyer");
        JMenuItem sellerItem = new JMenuItem("Seller");
        roleMenu.add(buyerItem);
        roleMenu.add(sellerItem);
        roleBar.add(roleMenu);
        panel.add(roleBar, gridBagConstraints);

        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.gridy = line++;
        JLabel name = new JLabel("Name: ");
        panel.add(name, gridBagConstraints);
        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        JTextField nameText = new JTextField(20);
        panel.add(nameText, gridBagConstraints);

        JButton confirm = new JButton("Confirm");
        gridBagConstraints.gridy = line;
        panel.add(confirm, gridBagConstraints);

        buyerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roleOutput[0] = 0;
                changeMenuText(roleMenu, "Buyer");
                updateMenuBarAppearance(roleBar, Color.RED, Font.BOLD);
            }
        });

        sellerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                roleOutput[0] = 1;
                changeMenuText(roleMenu, "Seller");
                updateMenuBarAppearance(roleBar, Color.BLUE, Font.BOLD);
            }
        });

        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (LOCK) {
                    info.clear();
                    info.add(emailText.getText());
                    info.add(passwordText.getText());
                    info.add(nameText.getText());
                    if (info.stream().noneMatch(String::isEmpty)) {
                        if (createAccountCheck(info).isEmpty()) {
                            GUI_PASS = new String((roleOutput[0] == 1) ? "true" : "false" + "," + info.get(0)
                                    + "," + info.get(1) + "," + info.get(2));

                            LOCK.notify();
                            frame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, String.join("\n",
                                    createAccountCheck(info)), "Errors Found", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please fill all information",
                                "Errors Found", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static ArrayList<String> createAccountCheck(ArrayList<String> info) {
        int line = 0;
        ArrayList<String> errorList = new ArrayList<>();
        if (!EMAIL_REGEX_PATTERN.matcher(info.get(line++)).matches()) {
            errorList.add("Invalid email format");
        }
        String tempString = info.get(++line);
        if (!Arrays.stream(tempString.split("\\s+")).
                allMatch(word -> word.matches(NAME_PATTERN))) {
            errorList.add("Invalid name format." +
                    " Name need to have every part's first letter uppercase");
        }
        return errorList;
    }

    private static void updateMenuBarAppearance(JMenuBar menuBar, Color color, int style) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            menu.setFont(new Font(menu.getFont().getName(), style, menu.getFont().getSize()));
            menu.setForeground(color);
        }
    }

    private static void changeMenuText(JMenu menu, String newText) {
        menu.setText(newText);
    }

    public static void loginPage() {
        ArrayList<String> info = new ArrayList<String>();
        int line = 0;
        JFrame frame = new JFrame("Log in");
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.gridy = line++;
        JLabel email = new JLabel("Email: ");
        panel.add(email, gridBagConstraints);
        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        JTextField emailText = new JTextField("", 20);
        panel.add(emailText, gridBagConstraints);

        gridBagConstraints.anchor = GridBagConstraints.LINE_START;
        gridBagConstraints.gridy = line++;
        JLabel password = new JLabel("Password: ");
        panel.add(password, gridBagConstraints);
        gridBagConstraints.anchor = GridBagConstraints.LINE_END;
        JTextField passwordText = new JTextField(20);
        panel.add(passwordText, gridBagConstraints);

        JButton confirm = new JButton("Confirm");
        gridBagConstraints.gridy = line;
        panel.add(confirm, gridBagConstraints);

        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (LOCK) {
                    info.clear();
                    info.add(emailText.getText());
                    info.add(passwordText.getText());
                    if (info.stream().noneMatch(String::isEmpty)) {
                        if (loginCheck(info).isEmpty()) {
                            GUI_PASS = new String(String.join(",", info));
                            LOCK.notify();
                            frame.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, String.join("\n",
                                    loginCheck(info)), "Errors Found", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please fill all information",
                                "Errors Found", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static ArrayList<String> loginCheck(ArrayList<String> info) {
        ArrayList<String> errorList = new ArrayList<>();
        if (!EMAIL_REGEX_PATTERN.matcher(info.get(0)).matches()) {
            errorList.add("Invalid email format");
        }
        return errorList;
    }

    public static void mainCustomerPage(ArrayList<String> storeName, ArrayList<String> sellerName, ArrayList<String> conversationList) {

    }

    public static void mainSellerPage(ArrayList<String> storeName, ArrayList<String> conversationList) {

    }

    public static void messagePage(ArrayList<String> message) {

    }

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        int counter = 0;
        int userInputInt = 0;
        String userInputString = "";
        boolean error = false;
        boolean seller = false;
        boolean keepOption = true;
        boolean keepConversation = true;
        String userInfoTemp = "";
        String tempString = "";
        String clientInput = "";
        String clientOutput = "";
        ArrayList<String> userNameList = new ArrayList<>();
        ArrayList<String> userNameList2 = new ArrayList<>();
        ArrayList<String> storeNameList = new ArrayList<>();
        ArrayList<String> conversationList = new ArrayList<>();
        ArrayList<String> messageList = new ArrayList<>();
        ArrayList<String> infoList = new ArrayList<>();
        ArrayList<String> errorList = new ArrayList<>();

        // Connect to server
        JOptionPane.showMessageDialog(null, "Establishing connection",
                "Connecting", JOptionPane.INFORMATION_MESSAGE);
        try {
            Socket socket = new Socket(InetAddress.getLocalHost().getHostName(), 4242);
            JOptionPane.showMessageDialog(null, "Connect successfully",
                    GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            Matcher matcher;
            // Start program
            do {
                userInputInt = -1;
                userInputInt = JOptionPane.showConfirmDialog(null,
                        "Hello! Would you like to use the program?", GUI_TITLE, JOptionPane.YES_NO_OPTION);
                if (userInputInt == -1 || userInputInt == 1) {
                    endProgramDialog();
                    return;
                }
                writer.println("Start system");
                writer.flush();

                // Login page
                boolean notloggedIn;

                do {
                    String[] buttonOptions = {"Log in", "Create account"};
                    userInputInt = -1;
                    userInputInt = JOptionPane.showOptionDialog(null, "Choose an option:",
                            "StartPage", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                            null, buttonOptions, buttonOptions[0]
                    );
                    // login page
                    if (userInputInt == -1) {
                        endProgramDialog();
                        return;
                    }
                    notloggedIn = true;

                    error = false;
                    int line = 0;
                    errorList.clear();
                    if (userInputInt == 1) {
                        //create account
                        writer.println("Create account");
                        writer.flush();
                        SwingUtilities.invokeLater(Client::createAccountPage);
                    } else if (userInputInt == 0) {
                        // log in
                        writer.println("Log in");
                        writer.flush();
                        SwingUtilities.invokeLater(Client::loginPage);
                    }
                    synchronized (LOCK) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    writer.println(GUI_PASS);
                    writer.flush();

                    clientInput = reader.readLine();
                    if (clientInput.equals("fail")) {
                        JOptionPane.showMessageDialog(null, "Invalid email or password",
                                GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Successfully logged in",
                                GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                        notloggedIn = false;
                    }
                } while (notloggedIn);

                do {
                    // Display
                    seller = true;
                    storeNameList.clear();
                    userNameList.clear();
                    userNameList2.clear();
                    clientInput = reader.readLine();
                    if (clientInput.equals("Customer display")) {
                        seller = false;
                        clientInput = reader.readLine();
                        Arrays.stream(clientInput.split(";"))
                                .map(String::trim)
                                .forEach(storeNameList::add);
                        clientInput = reader.readLine();
                        Arrays.stream(clientInput.split(";"))
                                .map(String::trim)
                                .forEach(userNameList2::add);

                    } else {
                        clientInput = reader.readLine();
                        Arrays.stream(clientInput.split(";"))
                                .map(String::trim)
                                .forEach(storeNameList::add);
                    }
                    clientInput = reader.readLine();
                    Arrays.stream(clientInput.split(";"))
                            .map(String::trim)
                            .forEach(userNameList::add);
                    counter = 0;
                    clientInput = reader.readLine();
                    System.out.println("\n" + clientInput);
                    if (!seller) {
                        if (!userNameList2.isEmpty() && !userNameList2.get(0).isEmpty()) {
                            while (counter < userNameList2.size()) {
                                System.out.printf("Seller: %s ->" +
                                        " Stores: %s\n", userNameList2.get(counter), storeNameList.get(counter));
                                counter++;
                            }
                        } else {
                            System.out.println("Server contain no seller");
                        }
                    } else {
                        System.out.print("My stores: ");

                        if (storeNameList.isEmpty() || storeNameList.get(0).isEmpty()) {
                            tempString = "None";
                        } else {
                            tempString = String.join(", ", storeNameList);
                        }
                        System.out.println(tempString);
                    }
                    System.out.print("Exist conversation: ");
                    if (userNameList.isEmpty() || userNameList.get(0).isEmpty()) {
                        tempString = "None";
                    } else {
                        tempString = String.join(", ", userNameList);
                    }
                    System.out.println(tempString);

                    // option
                    do {
                        error = false;
                        System.out.println("\nOptions ->\n0: Select a specific conversation\n" +
                                "1: Search user to create new conversation\n2: Block any user\n" +
                                "3: Account Modification\n4: Unblock any user\n5: Invisible any user\n" +
                                "6: Indivisibles any user\n" + "7: add store\n8: log off");
                        try {
                            userInputInt = Integer.parseInt(scan.next());
                            if (userInputInt < 0 || userInputInt > 8) {
                                System.out.println("Invalid input");
                                error = true;
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid input");
                            error = true;
                        }
                    } while (error);
                    scan.nextLine();
                    writer.println(Integer.toString(userInputInt));
                    writer.flush();
                    keepOption = true;
                    switch (userInputInt) {
                        case 8 -> {
                            JOptionPane.showMessageDialog(null, "Logging off",
                                    GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                            keepOption = false;
                        }
                        case 7 -> {
                            clientInput = reader.readLine();
                            if (clientInput.equals("Not seller")) {
                                JOptionPane.showMessageDialog(null, "Your are not a seller",
                                        GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                storeNameList.clear();
                                clientInput = reader.readLine();
                                if (!clientInput.isEmpty()) {
                                    Arrays.stream(clientInput.split(";"))
                                            .map(String::trim)
                                            .forEach(storeNameList::add);
                                    JOptionPane.showMessageDialog(null, String.format(
                                                    "Stores you own: %s", String.join(";", storeNameList)),
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            "You don't have any store", GUI_TITLE,
                                            JOptionPane.INFORMATION_MESSAGE);
                                }
                                do {
                                    error = false;
                                    userInputString = JOptionPane.showInputDialog(null,
                                            "Enter the store you want to add",
                                            GUI_TITLE, JOptionPane.QUESTION_MESSAGE);
                                    if (userInputString == null) {
                                        System.out.println(EXIT);
                                        return;
                                    }
                                    if (userInputString.isEmpty()) {
                                        JOptionPane.showMessageDialog(null,
                                                "Store name cannot be empty", GUI_TITLE,
                                                JOptionPane.INFORMATION_MESSAGE);
                                        error = true;
                                    }
                                } while (error);
                                writer.println(userInputString);
                                writer.flush();
                                JOptionPane.showMessageDialog(null,
                                        "Successfully added a store", GUI_TITLE,
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                        case 6 -> {
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                JOptionPane.showMessageDialog(null,
                                        "No user been invisible by you", GUI_TITLE,
                                        JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                userNameList.clear();
                                Arrays.stream(clientInput.split(";"))
                                        .map(String::trim)
                                        .forEach(userNameList::add);
                                JOptionPane.showMessageDialog(null, String.format("Invisible Users: %s",
                                                String.join(";", userNameList)),
                                        GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                userInputString = (String) JOptionPane.showInputDialog(null,
                                        "Select an user to indivisibles from the list ", GUI_TITLE,
                                        JOptionPane.QUESTION_MESSAGE, null, userNameList.toArray(),
                                        userNameList.get(0));
                                if (userInputString == null) {
                                    endProgramDialog();
                                    return;
                                }
                                writer.println(userInputString);
                                writer.flush();
                                clientInput = reader.readLine();
                                if (!clientInput.equals("fail")) {
                                    JOptionPane.showMessageDialog(null,
                                            String.format("Successfully indivisible %s with %s",
                                                    userInputString.substring(userInputString.indexOf(",") + 1),
                                                    userInputString.substring(0, userInputString.indexOf(","))),
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            String.format("Fail to indivisible %s with %s",
                                                    userInputString.substring(userInputString.indexOf(",") + 1),
                                                    userInputString.substring(0, userInputString.indexOf(","))),
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }
                        case 5 -> {
                            do {
                                error = false;
                                userInputString = JOptionPane.showInputDialog(null,
                                        "What is the name of the user you want to invisible?",
                                        GUI_TITLE, JOptionPane.QUESTION_MESSAGE);
                                if (userInputString == null) {
                                    endProgramDialog();
                                    return;
                                }
                                if (userInputString.isEmpty()) {
                                    JOptionPane.showMessageDialog(null,
                                            "Name cannot be empty",
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                    error = true;
                                }
                            } while (error);
                            writer.println(userInputString);
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                JOptionPane.showMessageDialog(null,
                                        "No user that contain the name",
                                        GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                userNameList.clear();
                                Arrays.stream(clientInput.split(";"))
                                        .map(String::trim)
                                        .forEach(userNameList::add);
                                userInputString = (String) JOptionPane.showInputDialog(null,
                                        "Please select an user from the list ", GUI_TITLE,
                                        JOptionPane.QUESTION_MESSAGE, null, userNameList.toArray(),
                                        userNameList.get(0));
                                if (userInputString == null) {
                                    endProgramDialog();
                                    return;
                                }
                                writer.println(userInputString);
                                writer.flush();
                                clientInput = reader.readLine();
                                if (!clientInput.equals("fail")) {
                                    JOptionPane.showMessageDialog(null,
                                            String.format("Successfully invisible %s with %s",
                                                    userInputString.substring(userInputString.indexOf(",") + 1),
                                                    userInputString.substring(0, userInputString.indexOf(","))),
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            String.format("Fail to invisible %s with %s",
                                                    userInputString.substring(userInputString.indexOf(",") + 1),
                                                    userInputString.substring(0, userInputString.indexOf(","))),
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }
                        case 4 -> {
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                JOptionPane.showMessageDialog(null, "No user been blocked by you",
                                        GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                userNameList.clear();
                                Arrays.stream(clientInput.split(";"))
                                        .map(String::trim)
                                        .forEach(userNameList::add);
                                JOptionPane.showMessageDialog(null, String.format("Blocked Users: %s",
                                                String.join(";", userNameList)),
                                        GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                userInputString = (String) JOptionPane.showInputDialog(null,
                                        "Select an user to unblocks from the list ", GUI_TITLE,
                                        JOptionPane.QUESTION_MESSAGE, null, userNameList.toArray(),
                                        userNameList.get(0));
                                if (userInputString == null) {
                                    endProgramDialog();
                                    return;
                                }
                                writer.println(userInputString);
                                writer.flush();
                                clientInput = reader.readLine();
                                if (!clientInput.equals("fail")) {
                                    JOptionPane.showMessageDialog(null,
                                            String.format("Successfully unblocked %s with %s",
                                                    userInputString.substring(userInputString.indexOf(",") + 1),
                                                    userInputString.substring(0, userInputString.indexOf(","))),
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            String.format("Fail to unblock %s with %s",
                                                    userInputString.substring(userInputString.indexOf(",") + 1),
                                                    userInputString.substring(0, userInputString.indexOf(","))),
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                }
                            }

                        }
                        case 3 -> {
                            error = false;
                            String[] accountModificationText = {"0: Change name", "1: Change email",
                                    "2: Change password", "3: Delete Account"};
                            userInputString = (String) JOptionPane.showInputDialog(null,
                                    "Please select option from the list ", GUI_TITLE,
                                    JOptionPane.QUESTION_MESSAGE, null, accountModificationText, null);
                            if (tempString == null) {
                                endProgramDialog();
                                return;
                            }
                            switch (userInputString) {
                                case "0: Change name" -> {tempString = "name"; userInputInt = 0;}
                                case "1: Change email" -> {tempString = "email"; userInputInt = 1;}
                                case "2: Change password" -> {tempString = "password"; userInputInt = 2;}
                                case "3: Delete Account" -> {
                                    System.out.println("logging off");
                                    keepOption = false;
                                    userInputInt = 3;
                                }
                            }
                            writer.println(Integer.parseInt(userInputString));
                            writer.flush();
                            if (Integer.parseInt(userInputString) != 3) {
                                do {
                                    error = false;
                                    userInputString = JOptionPane.showInputDialog(null,
                                            String.format("What is the new %s", tempString),
                                            GUI_TITLE, JOptionPane.QUESTION_MESSAGE);
                                    if (tempString.equals("name") || tempString.equals("email")) {
                                        if (tempString.equals("name")) {
                                            matcher = NAME_REGEX_PATTERN.matcher(userInputString);
                                            if (!matcher.matches()) {
                                                JOptionPane.showMessageDialog(null,
                                                        "Invalid name format",
                                                        GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                                error = true;
                                            }
                                        }
                                        if (tempString.equals("email")) {
                                            matcher = EMAIL_REGEX_PATTERN.matcher(userInputString);
                                            if (!matcher.matches()) {
                                                JOptionPane.showMessageDialog(null,
                                                        "Invalid email format",
                                                        GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                                error = true;
                                            }
                                        }
                                    }
                                    if (userInputString == null) {
                                        System.out.println(EXIT);
                                        return;
                                    }
                                    if (userInputString.isEmpty()) {
                                        JOptionPane.showMessageDialog(null,
                                                String.format("%s cannot be empty", tempString),
                                                GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                        error = true;
                                    }
                                } while (error);
                                writer.println(userInputString);
                                writer.flush();
                            }
                            clientInput = reader.readLine();
                            if (clientInput.equals("User have no conversation")) {
                                JOptionPane.showMessageDialog(null,
                                        String.format("User have no conversation to modify", tempString),
                                        GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                clientInput = reader.readLine();
                            }
                            System.out.println(clientInput);
                        }
                        case 2 -> {
                            do {
                                error = false;
                                userInputString = JOptionPane.showInputDialog(null,
                                        "What is the name of the user you want to block?",
                                        GUI_TITLE, JOptionPane.QUESTION_MESSAGE);
                                if (userInputString == null) {
                                    endProgramDialog();
                                    return;
                                }
                                if (userInputString.isEmpty()) {
                                    JOptionPane.showMessageDialog(null,
                                            "Name cannot be empty",
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                    error = true;
                                }
                            } while (error);
                            writer.println(userInputString);
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                JOptionPane.showMessageDialog(null,
                                        "No user that contain the name",
                                        GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                userNameList.clear();
                                Arrays.stream(clientInput.split(";"))
                                        .map(String::trim)
                                        .forEach(userNameList::add);

                                userInputString = (String) JOptionPane.showInputDialog(null,
                                        "Please select an user from the list ", GUI_TITLE,
                                        JOptionPane.QUESTION_MESSAGE, null, userNameList.toArray(),
                                        userNameList.get(0));
                                if (userInputString == null) {
                                    endProgramDialog();
                                    return;
                                }
                                writer.println(userInputString);
                                writer.flush();
                                clientInput = reader.readLine();
                                if (!clientInput.equals("fail")) {
                                    JOptionPane.showMessageDialog(null,
                                            String.format("Successfully blocked %s with %s",
                                                    userInputString.substring(userInputString.indexOf(",") + 1),
                                                    userInputString.substring(0, userInputString.indexOf(","))),
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            String.format("Fail to block %s with %s",
                                                    userInputString.substring(userInputString.indexOf(",") + 1),
                                                    userInputString.substring(0, userInputString.indexOf(","))),
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }
                        case 1 -> {
                            do {
                                error = false;
                                userInputString = JOptionPane.showInputDialog(null,
                                        "What is the name of the user you want" +
                                                " to create new conversation? (Search)",
                                        GUI_TITLE, JOptionPane.QUESTION_MESSAGE);
                                if (userInputString == null) {
                                    System.out.println(EXIT);
                                    return;
                                }
                                if (userInputString.isEmpty()) {
                                    JOptionPane.showMessageDialog(null,
                                            "Name cannot be empty",
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                    error = true;
                                }
                            } while (error);
                            writer.println(userInputString);
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                JOptionPane.showMessageDialog(null,
                                        "No user that contain the name",
                                        GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                userNameList.clear();
                                Arrays.stream(clientInput.split(";"))
                                        .map(String::trim)
                                        .forEach(userNameList::add);
                                userInputString = (String) JOptionPane.showInputDialog(null,
                                        "Please select an user from the list to create conversation", GUI_TITLE,
                                        JOptionPane.QUESTION_MESSAGE, null, userNameList.toArray(),
                                        userNameList.get(0));
                                if (userInputString == null) {
                                    endProgramDialog();
                                    return;
                                }
                                writer.println(userInputString);
                                writer.flush();
                                clientInput = reader.readLine();
                                if (!clientInput.equals("fail")) {
                                    JOptionPane.showMessageDialog(null,
                                            String.format("Successfully start a conversation to %s with %s",
                                                    userInputString.substring(userInputString.indexOf(",") + 1),
                                                    userInputString.substring(0, userInputString.indexOf(","))),
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            String.format("Fail to start a conversation to %s with %s",
                                                    userInputString.substring(userInputString.indexOf(",") + 1),
                                                    userInputString.substring(0, userInputString.indexOf(","))),
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        }
                        case 0 -> {
                            clientInput = reader.readLine();
                            if (!clientInput.equals("No conversation")) {
                                conversationList.clear();
                                Arrays.stream(clientInput.split(";"))
                                        .map(String::trim)
                                        .forEach(conversationList::add);

                                userInputString = (String) JOptionPane.showInputDialog(null,
                                        "Please select an user from the list to create conversation", GUI_TITLE,
                                        JOptionPane.QUESTION_MESSAGE, null, conversationList.toArray(), null);
                                writer.println(userInputString);
                                writer.flush();
                                clientInput = reader.readLine();
                                if (clientInput.equals("fail")) {
                                    JOptionPane.showMessageDialog(null,
                                            "Incorrect email and name",
                                            GUI_TITLE, JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    do {
                                        keepConversation = true;
                                        clientInput = reader.readLine();
                                        if (clientInput.equals("fail")) {
                                            System.out.println("No message");
                                        } else {
                                            messageList.clear();
                                            Arrays.stream(clientInput.split(";"))
                                                    .map(String::trim)
                                                    .forEach(messageList::add);
                                            System.out.println("Messages:");
                                            messageList.forEach(System.out::println);
                                        }
                                        String[] accountModificationText = {"0: New message", "1: Edit Message",
                                                "2: DeleteMessage", "3: Exit conversation"};
                                        userInputString = (String) JOptionPane.showInputDialog(null,
                                                "Please select option from the list", GUI_TITLE,
                                                JOptionPane.QUESTION_MESSAGE, null, accountModificationText, null);
                                        switch (userInputString) {
                                            case "0: New message" -> userInputInt = 0;
                                            case "1: Edit Message" -> userInputInt = 1;
                                            case "2: DeleteMessage" -> userInputInt = 2;
                                            case "3: Exit conversation" -> {
                                                keepConversation = false;
                                                userInputInt = 3;
                                            }
                                        }
                                        writer.println(userInputInt);
                                        writer.flush();
                                        switch (userInputInt) {
                                            case 0 -> {
                                                do {
                                                    error = false;
                                                    System.out.println("New message content");
                                                    userInputString = scan.nextLine();
                                                    if (userInputString == null) {
                                                        System.out.println(EXIT);
                                                        return;
                                                    }
                                                    if (userInputString.isEmpty()) {
                                                        System.out.println("Content cannot be empty");
                                                        error = true;
                                                    }
                                                } while (error);
                                                writer.println(userInputString);
                                                writer.flush();
                                                clientInput = reader.readLine();
                                                if (clientInput.equals("blocked")) {
                                                    System.out.println("You are blocked by receiver," +
                                                            " cannot send message");
                                                }
                                            }
                                            case 1 -> {
                                                do {
                                                    error = false;
                                                    System.out.println("Enter the old message, time," +
                                                            " and new message separate with semicolon " +
                                                            "ex:oldmessage;time;newMessage");
                                                    userInputString = scan.nextLine();
                                                    matcher = EEDIT_MESSAGE_PATTERN.matcher(userInputString);
                                                    if (userInputString == null) {
                                                        System.out.println(EXIT);
                                                        return;
                                                    }
                                                    if (userInputString.isEmpty()) {
                                                        System.out.println("Email and Name cannot be empty");
                                                        error = true;
                                                    }
                                                    if (!userInputString.contains(";")) {
                                                        System.out.println("Invalid input, no semicolon present");
                                                        error = true;
                                                    }
                                                    if (!matcher.matches()) {
                                                        System.out.println("Invalid format");
                                                        error = true;
                                                    }
                                                } while (error);
                                                writer.println(userInputString);
                                                writer.flush();
                                                clientInput = reader.readLine();
                                                if (clientInput.equals("Message not found")) {
                                                    System.out.println("Input message not found");
                                                }
                                            }
                                            case 2 -> {
                                                do {
                                                    error = false;
                                                    System.out.println("Enter the time and" +
                                                            " content of the message you want to delete" +
                                                            " separate with semicolon ex:time;content");
                                                    userInputString = scan.nextLine();
                                                    matcher = DELETE_MESSAGE_PATTERN.matcher(userInputString);
                                                    if (userInputString == null) {
                                                        System.out.println(EXIT);
                                                        return;
                                                    }
                                                    if (userInputString.isEmpty()) {
                                                        System.out.println("time and content cannot be empty");
                                                        error = true;
                                                    }
                                                    if (!matcher.matches()) {
                                                        System.out.println("Invalid format");
                                                        error = true;
                                                    }
                                                } while (error);
                                                writer.println(userInputString);
                                                writer.flush();
                                                clientInput = reader.readLine();
                                                if (clientInput.equals("Message not found")) {
                                                    System.out.println("Input message not found");
                                                }
                                            }
                                            case 3 -> {
                                                System.out.println("Exit conversation");
                                                keepConversation = false;
                                            }
                                        }
                                    } while (keepConversation);
                                }
                            } else {
                                System.out.println("You have no exist conversation," +
                                        " please add new user to make conversation");
                            }
                        }
                    }
                } while (keepOption);
            } while (true);
        } catch (UnknownHostException | SocketException e) {
            JOptionPane.showMessageDialog(null, "Given host name and port number cannot" +
                    " establish connection with the server", "Disconnected", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}