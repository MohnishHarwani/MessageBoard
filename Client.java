import javax.swing.*;
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
    public static final String emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final Pattern emailRegexPattern = Pattern.compile(emailPattern);
    public static final String namePattern = "^[A-Z][a-z]+\\s[A-Z][a-z]+$";
    public static final Pattern nameRegexPattern = Pattern.compile(namePattern);
    public static final String emailCommaName = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}" +
            "\\b,\\s*\\p{Lu}\\p{L}+\\s+\\p{Lu}\\p{L}+";
    public static final Pattern emailCommaNamePattern = Pattern.compile(emailCommaName);
    public static final String editMessageFormat = ".*;\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2};.*";
    public static final Pattern editMessagePattern = Pattern.compile(editMessageFormat);
    public static final String deleteMessageFormat = "\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2};.*";
    public static final Pattern deleteMessagePattern = Pattern.compile(deleteMessageFormat);
    public static final String GUI_TITLE = "Messager";

    // assume the dumbest user possible, 01
    // name check 1 space

    public static void endProgramDialog() {
        JOptionPane.showMessageDialog(null, "Thank you for using Messenger!",
                "Exiting", JOptionPane.INFORMATION_MESSAGE);
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

        // Connect to server
        System.out.println("Establishing connection");
        try {
            Socket socket = new Socket(InetAddress.getLocalHost().getHostName(), 4242);
            System.out.println("Connect successfully");
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            Matcher matcher;
            // Start program
            do {
                userInputInt = -1;
                userInputInt = JOptionPane.showConfirmDialog(null, "Hello! Would you like to use the program?"
                        , GUI_TITLE, JOptionPane.YES_NO_OPTION);
                System.out.println(userInputInt);
                if (userInputInt == -1 || userInputInt == 1) {
                    endProgramDialog();
                    return;
                }

                writer.println("Start system");
                writer.flush();

                // Login page
                boolean notloggedIn;

                do {
                    // login page
                    notloggedIn = true;
                    do {
                        error = false;
                        System.out.println("Do you have an account already? Enter 1 for yes, 0 for no.");
                        try {
                            userInputInt = Integer.parseInt(scan.next());
                            if (userInputInt != 0 && userInputInt != 1) {
                                System.out.println("Invalid input");
                                error = true;
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid input");
                            error = true;
                        }
                    } while (error);
                    scan.nextLine();

                    //create account
                    if (userInputInt == 0) {
                        writer.println("Create account");
                        writer.flush();
                        userInfoTemp = "";
                        do {
                            error = false;
                            System.out.println("Are you a buyer or a seller? Enter 1 for seller, 0 for buyer");
                            try {
                                userInputInt = Integer.parseInt(scan.next());
                                if (userInputInt != 0 && userInputInt != 1) {
                                    System.out.println("Invalid input");
                                    error = true;
                                }
                            } catch (IllegalArgumentException e) {
                                System.out.println("Invalid input");
                                error = true;
                            }
                        } while (error);
                        scan.nextLine();
                        userInfoTemp += (userInputInt == 1) ? "true," : "false,";

                        do {
                            error = false;
                            System.out.println("What is your email? ex:[abc@def.ghi]");
                            userInputString = scan.nextLine();
                            matcher = emailRegexPattern.matcher(userInputString);
                            if (userInputString == null) {
                                System.out.println(EXIT);
                                return;
                            }
                            if (userInputString.isEmpty()) {
                                System.out.println("Email cannot be empty");
                                error = true;
                            }
                            if (!matcher.matches()) {
                                System.out.println("Invalid email format");
                                error = true;
                            }
                        } while (error);
                        userInfoTemp += userInputString + ",";

                        do {
                            error = false;
                            System.out.println("What is your password?");
                            userInputString = scan.nextLine();
                            if (userInputString == null) {
                                System.out.println(EXIT);
                                return;
                            }
                            if (userInputString.isEmpty()) {
                                System.out.println("Password cannot be empty");
                                error = true;
                            }
                        } while (error);
                        userInfoTemp += userInputString + ",";

                        do {
                            error = false;
                            System.out.println("What is your name? ex:[Abc Def]");
                            userInputString = scan.nextLine();
                            matcher = nameRegexPattern.matcher(userInputString);
                            if (userInputString == null) {
                                System.out.println(EXIT);
                                return;
                            }
                            if (userInputString.isEmpty()) {
                                System.out.println("Name cannot be empty");
                                error = true;
                            }
                            if (!matcher.matches()) {
                                System.out.println("Invalid name format");
                                error = true;
                            }
                        } while (error);
                        userInfoTemp += userInputString;
                        writer.println(userInfoTemp);
                        writer.flush();

                        clientInput = reader.readLine();
                        System.out.println(clientInput);
                        if (clientInput.equals("User Already exist")) {
                            System.out.println(clientOutput);
                        }
                        // log in
                    } else if (userInputInt == 1) {
                        writer.println("Log in");
                        writer.flush();
                        userInfoTemp = "";
                        do {
                            error = false;
                            System.out.println("What is your email?");
                            userInputString = scan.nextLine();
                            if (userInputString == null) {
                                System.out.println(EXIT);
                                return;
                            }
                            if (userInputString.isEmpty()) {
                                System.out.println("email cannot be empty");
                                error = true;
                            }
                        } while (error);
                        userInfoTemp += userInputString + ",";
                        do {
                            error = false;
                            System.out.println("What is your password?");
                            userInputString = scan.nextLine();
                            if (userInputString == null) {
                                System.out.println(EXIT);
                                return;
                            }
                            if (userInputString.isEmpty()) {
                                System.out.println("Password cannot be empty");
                                error = true;
                            }
                        } while (error);
                        userInfoTemp += userInputString;
                        writer.println(userInfoTemp);
                        writer.flush();

                        clientInput = reader.readLine();
                        if (clientInput.equals("fail")) {
                            System.out.println("Invalid email or password");
                        } else {
                            notloggedIn = false;
                        }
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
                            System.out.println("logging off");
                            keepOption = false;
                        }
                        case 7 -> {
                            clientInput = reader.readLine();
                            if (clientInput.equals("Not seller")) {
                                System.out.println("Your are not a seller");
                            } else {
                                storeNameList.clear();
                                clientInput = reader.readLine();
                                System.out.print("Stores owned: ");
                                if (!clientInput.isEmpty()) {
                                    Arrays.stream(clientInput.split(";"))
                                            .map(String::trim)
                                            .forEach(storeNameList::add);
                                    System.out.println(String.join(";", storeNameList));
                                } else {
                                    System.out.println("None");
                                }
                                do {
                                    error = false;
                                    System.out.println("Enter the store name you want to add");
                                    userInputString = scan.nextLine();
                                    if (userInputString == null) {
                                        System.out.println(EXIT);
                                        return;
                                    }
                                    if (userInputString.isEmpty()) {
                                        System.out.println("Store name cannot be empty");
                                        error = true;
                                    }
                                } while (error);
                                writer.println(userInputString);
                                writer.flush();
                                System.out.println("Successfully added a store");
                            }
                        }
                        case 6 -> {
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                System.out.println("No user been invisible by you");
                            } else {
                                userNameList.clear();
                                Arrays.stream(clientInput.split(";"))
                                        .map(String::trim)
                                        .forEach(userNameList::add);
                                System.out.println("Invisible Users:");
                                counter = 0;
                                while (counter < userNameList.size()) {
                                    System.out.println(userNameList.get(counter));
                                    counter++;
                                }
                            }
                            do {
                                error = false;
                                System.out.println("Enter the user you want to" +
                                        " indivisibles email and name separate with comma ex:email,name");
                                matcher = emailCommaNamePattern.matcher(userInputString);
                                userInputString = scan.nextLine();
                                if (userInputString == null) {
                                    System.out.println(EXIT);
                                    return;
                                }
                                if (userInputString.isEmpty()) {
                                    System.out.println("Email and Name cannot be empty");
                                    error = true;
                                }
                                if (!matcher.matches()) {
                                    System.out.println("Invalid content format");
                                    error = true;
                                }
                            } while (error);
                            writer.println(userInputString);
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("fail")) {
                                System.out.println("Incorrect email and name");
                            } else {
                                System.out.println(clientInput);
                            }
                        }
                        case 5 -> {
                            do {
                                error = false;
                                System.out.println("What is the name of the user you want to invisible?");
                                userInputString = scan.nextLine();
                                if (userInputString == null) {
                                    System.out.println(EXIT);
                                    return;
                                }
                                if (userInputString.isEmpty()) {
                                    System.out.println("Name cannot be empty");
                                    error = true;
                                }
                            } while (error);
                            writer.println(userInputString);
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                System.out.println("No user that contain the name.");
                            } else {
                                userNameList.clear();
                                Arrays.stream(clientInput.split(";"))
                                        .map(String::trim)
                                        .forEach(userNameList::add);
                                System.out.println("Result Users:");
                                counter = 0;
                                while (counter < userNameList.size()) {
                                    System.out.println(userNameList.get(counter));
                                    counter++;
                                }
                                do {
                                    error = false;
                                    System.out.println("Enter the user you want to" +
                                            " invisible's email and name separate with comma ex:email,name");
                                    matcher = emailCommaNamePattern.matcher(userInputString);
                                    userInputString = scan.nextLine();
                                    if (userInputString == null) {
                                        System.out.println(EXIT);
                                        return;
                                    }
                                    if (userInputString.isEmpty()) {
                                        System.out.println("Email and Name cannot be empty");
                                        error = true;
                                    }
                                    if (!matcher.matches()) {
                                        System.out.println("Invalid content format");
                                        error = true;
                                    }
                                } while (error);
                                writer.println(userInputString);
                                writer.flush();
                                clientInput = reader.readLine();
                                if (clientInput.equals("fail")) {
                                    System.out.println("Incorrect email and name");
                                } else {
                                    System.out.println(clientInput);
                                }
                            }
                        }
                        case 4 -> {
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                System.out.println("No user been blocked by you");
                            } else {
                                userNameList.clear();
                                Arrays.stream(clientInput.split(";"))
                                        .map(String::trim)
                                        .forEach(userNameList::add);
                                System.out.println("Blocked Users:");
                                counter = 0;
                                while (counter < userNameList.size()) {
                                    System.out.println(userNameList.get(counter));
                                    counter++;
                                }
                            }
                            do {
                                error = false;
                                System.out.println("Enter the user you want to" +
                                        " unblocks email and name separate with comma  ex:email,name");
                                userInputString = scan.nextLine();
                                matcher = emailCommaNamePattern.matcher(userInputString);
                                if (userInputString == null) {
                                    System.out.println(EXIT);
                                    return;
                                }
                                if (userInputString.isEmpty()) {
                                    System.out.println("Email and Name cannot be empty");
                                    error = true;
                                }
                                if (!matcher.matches()) {
                                    System.out.println("Invalid content format");
                                    error = true;
                                }
                            } while (error);
                            writer.println(userInputString);
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("fail")) {
                                System.out.println("Incorrect email and name");
                            } else {
                                System.out.println(clientInput);
                            }
                        }
                        case 3 -> {
                            do {
                                error = false;
                                System.out.println("Options -> 0: Change name; 1:" +
                                        " Change email; 2: Change password; 3: Delete Account");
                                try {
                                    userInputInt = Integer.parseInt(scan.next());
                                    if (userInputInt < 0 || userInputInt > 3) {
                                        System.out.println("Invalid input");
                                        error = true;
                                    }
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid input");
                                    error = true;
                                }
                            } while (error);
                            scan.nextLine();
                            switch (userInputInt) {
                                case 0 -> tempString = "name";
                                case 1 -> tempString = "email";
                                case 2 -> tempString = "password";
                                case 3 -> {
                                    System.out.println("logging off");
                                    keepOption = false;
                                }
                            }
                            writer.println(Integer.toString(userInputInt));
                            writer.flush();
                            if (userInputInt != 3) {
                                do {
                                    error = false;
                                    System.out.printf("What is your new %s?\n", tempString);
                                    userInputString = scan.nextLine();
                                    if (tempString.equals("name") || tempString.equals("email")) {
                                        if (tempString.equals("name")) {
                                            matcher = nameRegexPattern.matcher(userInputString);
                                            if (!matcher.matches()) {
                                                System.out.println("Invalid name format");
                                                error = true;
                                            }
                                        }
                                        if (tempString.equals("email")) {
                                            matcher = emailRegexPattern.matcher(userInputString);
                                            if (!matcher.matches()) {
                                                System.out.println("Invalid email format");
                                                error = true;
                                            }
                                        }
                                    }
                                    if (userInputString == null) {
                                        System.out.println(EXIT);
                                        return;
                                    }
                                    if (userInputString.isEmpty()) {
                                        System.out.printf("%s cannot be empty\n", tempString);
                                        error = true;
                                    }
                                } while (error);
                                writer.println(userInputString);
                                writer.flush();
                            }
                            clientInput = reader.readLine();
                            if (clientInput.equals("User have no conversation")) {
                                System.out.println("User have no conversation to modify");
                                clientInput = reader.readLine();
                            }
                            System.out.println(clientInput);
                        }
                        case 2 -> {
                            do {
                                error = false;
                                System.out.println("What is the name of the user you want to block?");
                                userInputString = scan.nextLine();
                                if (userInputString == null) {
                                    System.out.println(EXIT);
                                    return;
                                }
                                if (userInputString.isEmpty()) {
                                    System.out.println("Name cannot be empty");
                                    error = true;
                                }
                            } while (error);
                            writer.println(userInputString);
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                System.out.println("No user that contain the name.");
                            } else {
                                userNameList.clear();
                                Arrays.stream(clientInput.split(";"))
                                        .map(String::trim)
                                        .forEach(userNameList::add);
                                System.out.println("Result Users:");
                                counter = 0;
                                while (counter < userNameList.size()) {
                                    System.out.println(userNameList.get(counter));
                                    counter++;
                                }
                                do {
                                    error = false;
                                    System.out.println("Enter the user you want to" +
                                            " block's email and name separate with comma ex:email,name");
                                    userInputString = scan.nextLine();
                                    matcher = emailCommaNamePattern.matcher(userInputString);
                                    if (userInputString == null) {
                                        System.out.println(EXIT);
                                        return;
                                    }
                                    if (userInputString.isEmpty()) {
                                        System.out.println("Email and Name cannot be empty");
                                        error = true;
                                    }
                                    if (!matcher.matches()) {
                                        System.out.println("Invalid content format");
                                        error = true;
                                    }
                                } while (error);
                                writer.println(userInputString);

                                writer.flush();
                                clientInput = reader.readLine();
                                if (clientInput.equals("fail")) {
                                    System.out.println("Incorrect email and name");
                                } else {
                                    System.out.println(clientInput);
                                }
                            }
                        }
                        case 1 -> {
                            do {
                                error = false;
                                System.out.println("What is the name of the user you want" +
                                        " to create new conversation? (Search)");
                                userInputString = scan.nextLine();
                                if (userInputString == null) {
                                    System.out.println(EXIT);
                                    return;
                                }
                                if (userInputString.isEmpty()) {
                                    System.out.println("Name cannot be empty");
                                    error = true;
                                }
                            } while (error);
                            writer.println(userInputString);
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                System.out.println("No user that contain the name.");
                            } else {
                                userNameList.clear();
                                Arrays.stream(clientInput.split(";"))
                                        .map(String::trim)
                                        .forEach(userNameList::add);
                                System.out.println("Result Users:");
                                counter = 0;
                                while (counter < userNameList.size()) {
                                    System.out.println(userNameList.get(counter));
                                    counter++;
                                }
                                do {
                                    error = false;
                                    System.out.println("Enter the user's email and name separate with comma " +
                                            "to create new conversation ex:email,name");
                                    userInputString = scan.nextLine();
                                    matcher = emailCommaNamePattern.matcher(userInputString);
                                    if (userInputString == null) {
                                        System.out.println(EXIT);
                                        return;
                                    }
                                    if (userInputString.isEmpty()) {
                                        System.out.println("Email and Name cannot be empty");
                                        error = true;
                                    }
                                    if (!matcher.matches()) {
                                        System.out.println("Invalid content format");
                                        error = true;
                                    }
                                } while (error);
                                writer.println(userInputString);
                                writer.flush();
                                clientInput = reader.readLine();
                                if (clientInput.equals("fail")) {
                                    System.out.println("Incorrect email and name");
                                } else {
                                    System.out.println(clientInput);
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
                                System.out.println("Conversation users:");
                                conversationList.forEach(System.out::println);
                                do {
                                    error = false;
                                    System.out.println("Enter the user's email and name separate with comma " +
                                            "to enter conversation ex:email,name");
                                    userInputString = scan.nextLine();
                                    matcher = emailCommaNamePattern.matcher(userInputString);
                                    if (userInputString == null) {
                                        System.out.println(EXIT);
                                        return;
                                    }
                                    if (userInputString.isEmpty()) {
                                        System.out.println("Email and Name cannot be empty");
                                        error = true;
                                    }
                                    if (!matcher.matches()) {
                                        System.out.println("Invalid content format");
                                        error = true;
                                    }
                                } while (error);
                                writer.println(userInputString);
                                writer.flush();
                                clientInput = reader.readLine();
                                if (clientInput.equals("fail")) {
                                    System.out.println("Incorrect email and name");
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
                                        do {
                                            error = false;
                                            System.out.println("Options -> 0: New message; " +
                                                    "1: Edit Message; 2: deleteMessage; 3: exit conversation");
                                            try {
                                                userInputInt = Integer.parseInt(scan.next());
                                                if (userInputInt < 0 || userInputInt > 3) {
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
                                                    matcher = editMessagePattern.matcher(userInputString);
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
                                                    matcher = deleteMessagePattern.matcher(userInputString);
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
            System.out.println("Given host name and port number cannot" +
                    " establish connection with the server");
        }
    }
}