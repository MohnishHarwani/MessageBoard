import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.*;

public class Client {
    public static final String MESSAGE_SYSTEM = "MessageSystem";
    public static final String EXIT = "Exiting";

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        int counter = 0;
        int userInputInt = 0;
        String userInputString = "";
        boolean error = false;
        boolean seller = false;
        boolean logOff = false;
        boolean conversationOff = false;
        String userInfoTemp = "";
        String tempString = "";
        String clientInput = "";
        String clientOutput = "";
        String tempSplit[];
        ArrayList<String> userNameList = new ArrayList<>();
        ArrayList<String> userNameList2 = new ArrayList<>();
        ArrayList<String> storeNameList = new ArrayList<>();
        ArrayList<String> conversationList = new ArrayList<>();
        ArrayList<String> messageList = new ArrayList<>();
        //System.out.println(InetAddress.getLocalHost().getHostName());
        //System.out.println(InetAddress.getLocalHost().getHostAddress());

        // Connect to server
        System.out.println("Establishing connection");
        try {
            Socket socket = new Socket(InetAddress.getLocalHost().getHostName(), 4242);
            System.out.println("Connect successfully");
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // Start program
            do {
                do {
                    error = false;
                    System.out.println("Hello! Would you like to use the program? Type 1 for yes, 0 for no.");
                    try {
                        userInputInt = scan.nextInt();
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

                if (userInputInt == 0) {
                    System.out.println("Exiting");
                    return;
                }

                // Login page
                boolean loggedIn = false;

                do {
                    // login page
                    loggedIn = false;
                    do {
                        error = false;
                        System.out.println("Do you have an account already? Enter 1 for yes, 0 for no.");
                        try {
                            userInputInt = scan.nextInt();
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
                        writer.write("Create account");
                        writer.println();
                        writer.flush();
                        userInfoTemp = "";
                        do {
                            error = false;
                            System.out.println("Are you a buyer or a seller? Enter 1 for seller, 0 for buyer");
                            try {
                                userInputInt = scan.nextInt();
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
                            System.out.println("What is your email?");
                            userInputString = scan.nextLine();
                            if (userInputString == null) {
                                System.out.println(EXIT);
                                return;
                            }
                            if (userInputString.isEmpty()) {
                                System.out.println("Email cannot be empty");
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
                            System.out.println("What is your name?");
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
                        userInfoTemp += userInputString;

                        writer.write(userInfoTemp);
                        writer.println();
                        writer.flush();

                        clientInput = reader.readLine();
                        if (clientInput.equals("User Already exist")) {
                            System.out.println(clientOutput);
                        }
                        // log in
                    } else if (userInputInt == 1) {
                        writer.write("Log in");
                        writer.println();
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

                        writer.write(userInfoTemp);
                        writer.println();
                        writer.flush();

                        clientInput = reader.readLine();
                        if (clientInput.equals("fail")) {
                            System.out.println("Invalid email or password");
                        } else {
                            loggedIn = true;
                        }
                    }
                    clientOutput = (loggedIn) ? "pass" : "not pass";
                    writer.write(clientOutput);
                    writer.println();
                    writer.flush();
                } while (!loggedIn);

                do {
                    // Display
                    seller = true;
                    storeNameList.removeAll(storeNameList);
                    userNameList.removeAll(userNameList);
                    userNameList2.removeAll(userNameList2);
                    clientInput = reader.readLine();
                    if (clientInput.equals("Customer display")) {
                        seller = false;
                        clientInput = reader.readLine();
                        Arrays.stream(clientInput.split(";")) // split all returned customer by ; and store into storeNameList
                                .map(String::trim)
                                .forEach(storeNameList::add);
                        clientInput = reader.readLine();
                        Arrays.stream(clientInput.split(";")) // split all returned customer by ; and store into storeNameList
                                .map(String::trim)
                                .forEach(userNameList2::add);
                    } else {
                        clientInput = reader.readLine();
                        Arrays.stream(clientInput.split(";")) // split all returned customer by ; and store into storeNameList
                                .map(String::trim)
                                .forEach(storeNameList::add);
                    }
                    clientInput = reader.readLine();
                    Arrays.stream(clientInput.split(";")) // split all returned customer by ; and store into userNameList
                            .map(String::trim)
                            .forEach(userNameList::add);
                    counter = 0;
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
                        System.out.printf("My stores: ");

                        if (storeNameList.isEmpty() || storeNameList.get(0).isEmpty()){
                            tempString = "None";
                        } else {
                            tempString = storeNameList.stream().collect(Collectors.joining(","));
                        }
                        System.out.println(tempString);
                    }
                    System.out.printf("Exist conversation: ");
                    if (userNameList.isEmpty() || userNameList.get(0).isEmpty()){
                        tempString = "None";
                    } else {
                        tempString = userNameList.stream().collect(Collectors.joining(","));
                    }
                    System.out.println(tempString);

                    // option
                    do {
                        error = false;
                        System.out.println("Options ->\n0: Select a specific conversation\n" +
                                "1: Search user to create new conversation\n2: Block any user\n" +
                                "3: Account Modification\n4: Unblock any user\n5: Invs any user\n6: UnInvis any user\n" +
                                "7: add store\n8: log off");
                        try {
                            userInputInt = scan.nextInt();
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
                    writer.write(Integer.toString(userInputInt));
                    writer.println();
                    writer.flush();
                    logOff = false;
                    switch (userInputInt) {
                        case 8:
                            System.out.println("logging off");
                            logOff = true;
                            break;
                        case 7:
                            clientInput = reader.readLine();
                            if (clientInput.equals("Not Seller")) {
                                System.out.println("Your are not a seller");
                            } else {
                                storeNameList.removeAll(storeNameList);
                                clientInput = reader.readLine();
                                System.out.print("Stores owned: ");
                                if (!clientInput.isEmpty()) {
                                    Arrays.stream(clientInput.split(";")) // split all returned customer by ; and store into userNameList
                                            .map(String::trim)
                                            .forEach(storeNameList::add);
                                    System.out.println(storeNameList.stream().collect(Collectors.joining(";")));
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
                            break;
                        case 6:
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                System.out.println("No user been invis by you");
                            } else {
                                userNameList.removeAll(userNameList);
                                Arrays.stream(clientInput.split(";")) // split all returned customer by ; and store into userNameList
                                        .map(String::trim)
                                        .forEach(userNameList::add);
                                System.out.println("Invis Users:");
                                counter = 0;
                                while (counter < userNameList.size()) {
                                    System.out.println(userNameList.get(counter));
                                    counter++;
                                }
                            }
                            do {
                                error = false;
                                System.out.println("Enter the user you want to" +
                                        " uninvis email and name separate with comma");
                                userInputString = scan.nextLine();
                                if (userInputString == null) {
                                    System.out.println(EXIT);
                                    return;
                                }
                                if (userInputString.isEmpty()) {
                                    System.out.println("Email and Name cannot be empty");
                                    error = true;
                                }
                                if (!userInputString.contains(",")) {
                                    System.out.println("Invalid input, no comma present");
                                    error = true;
                                }
                            } while (error);
                            writer.write(userInputString);
                            writer.println();
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("fail")) {
                                System.out.println("Incorrect email and name");
                            } else {
                                System.out.println(clientInput);
                            }
                            break;
                        case 5:
                            do {
                                error = false;
                                System.out.println("What is the name of the user you want to invis?");
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
                            writer.write(userInputString);
                            writer.println();
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                System.out.println("No user that contain the name.");
                            } else {
                                userNameList.removeAll(userNameList);
                                Arrays.stream(clientInput.split(";")) // split all returned customer by ; and store into usernameList
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
                                            " invis email and name separate with comma");
                                    userInputString = scan.nextLine();
                                    if (userInputString == null) {
                                        System.out.println(EXIT);
                                        return;
                                    }
                                    if (userInputString.isEmpty()) {
                                        System.out.println("Email and Name cannot be empty");
                                        error = true;
                                    }
                                    if (!userInputString.contains(",")) {
                                        System.out.println("Invalid input, no comma present");
                                        error = true;
                                    }
                                } while (error);
                                writer.write(userInputString);
                                writer.println();
                                writer.flush();
                                clientInput = reader.readLine();
                                if (clientInput.equals("fail")) {
                                    System.out.println("Incorrect email and name");
                                } else {
                                    System.out.println(clientInput);
                                }
                            }
                            break;
                        case 4:
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                System.out.println("No user been blocked by you");
                            } else {
                                userNameList.removeAll(userNameList);
                                Arrays.stream(clientInput.split(";")) // split all returned customer by ; and store into userNameList
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
                                        " unblocks email and name separate with comma");
                                userInputString = scan.nextLine();
                                if (userInputString == null) {
                                    System.out.println(EXIT);
                                    return;
                                }
                                if (userInputString.isEmpty()) {
                                    System.out.println("Email and Name cannot be empty");
                                    error = true;
                                }
                                if (!userInputString.contains(",")) {
                                    System.out.println("Invalid input, no comma present");
                                    error = true;
                                }
                            } while (error);
                            writer.write(userInputString);
                            writer.println();
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("fail")) {
                                System.out.println("Incorrect email and name");
                            } else {
                                System.out.println(clientInput);
                            }
                            break;
                        case 3:
                            do {
                                error = false;
                                System.out.println("Options -> 0: Change name; 1:" +
                                        " Change email; 2: Change password; 3: Delete Account");
                                try {
                                    userInputInt = scan.nextInt();
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
                                case 0:
                                    tempString = "name";
                                    break;
                                case 1:
                                    tempString = "email";
                                    break;
                                case 2:
                                    tempString = "password";
                                    break;
                                case 3:
                                    System.out.println("logging off");
                                    logOff = true;
                                    break;
                            }
                            writer.write(Integer.toString(userInputInt));
                            writer.println();
                            writer.flush();
                            if (userInputInt != 3) {
                                do {
                                    error = false;
                                    System.out.printf("What is your new %s?\n", tempString);
                                    userInputString = scan.nextLine();
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
                            break;
                        case 2:
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
                            writer.write(userInputString);
                            writer.println();
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                System.out.println("No user that contain the name.");
                            } else {
                                userNameList.removeAll(userNameList);
                                Arrays.stream(clientInput.split(";")) // split all returned customer by ; and store into usernameList
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
                                            " block's email and name separate with comma");
                                    userInputString = scan.nextLine();
                                    if (userInputString == null) {
                                        System.out.println(EXIT);
                                        return;
                                    }
                                    if (userInputString.isEmpty()) {
                                        System.out.println("Email and Name cannot be empty");
                                        error = true;
                                    }
                                    if (!userInputString.contains(",")) {
                                        System.out.println("Invalid input, no comma present");
                                        error = true;
                                    }
                                } while (error);
                                writer.write(userInputString);
                                writer.println();
                                writer.flush();
                                clientInput = reader.readLine();
                                if (clientInput.equals("fail")) {
                                    System.out.println("Incorrect email and name");
                                } else {
                                    System.out.println(clientInput);
                                }
                            }
                            break;
                        case 1:
                            do {
                                error = false;
                                System.out.println("What is the name of the user you want to create new conversation?");
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
                            writer.write(userInputString);
                            writer.println();
                            writer.flush();
                            clientInput = reader.readLine();
                            if (clientInput.equals("No result")) {
                                System.out.println("No user that contain the name.");
                            } else {
                                userNameList.removeAll(userNameList);
                                Arrays.stream(clientInput.split(";")) // split all returned customer by ; and store into userNameList
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
                                            "to create new conversation");
                                    userInputString = scan.nextLine();
                                    if (userInputString == null) {
                                        System.out.println(EXIT);
                                        return;
                                    }
                                    if (userInputString.isEmpty()) {
                                        System.out.println("Email and Name cannot be empty");
                                        error = true;
                                    }
                                    if (!userInputString.contains(",")) {
                                        System.out.println("Invalid input, no comma present");
                                        error = true;
                                    }
                                } while (error);
                                writer.write(userInputString);
                                writer.println();
                                writer.flush();
                                clientInput = reader.readLine();
                                if (clientInput.equals("fail")) {
                                    System.out.println("Incorrect email and name");
                                } else {
                                    System.out.println(clientInput);
                                }
                            }
                            break;
                        case 0:
                            clientInput = reader.readLine();
                            if (!clientInput.equals("No conversation")) {
                                conversationList.removeAll(conversationList);
                                Arrays.stream(clientInput.split(";"))
                                        .map(String::trim)
                                        .forEach(conversationList::add); // split all returned customer by ; and store into conversation list
                                System.out.println("Conversation users:");
                                conversationList.forEach(n -> System.out.println(n));
                                do {
                                    error = false;
                                    System.out.println("Enter the user's email and name separate with comma " +
                                            "to enter conversation");
                                    userInputString = scan.nextLine();
                                    if (userInputString == null) {
                                        System.out.println(EXIT);
                                        return;
                                    }
                                    if (userInputString.isEmpty()) {
                                        System.out.println("Email and Name cannot be empty");
                                        error = true;
                                    }
                                    if (!userInputString.contains(",")) {
                                        System.out.println("Invalid input, no comma present");
                                        error = true;
                                    }
                                } while (error);
                                writer.write(userInputString);
                                writer.println();
                                writer.flush();
                                clientInput = reader.readLine();
                                if (clientInput.equals("fail")) {
                                    System.out.println("Incorrect email and name");
                                } else {
                                    do {
                                        conversationOff = false;
                                        clientInput = reader.readLine();
                                        if (clientInput.equals("fail")) {
                                            System.out.println("No message");
                                        } else {
                                            messageList.removeAll(messageList);
                                            Arrays.stream(clientInput.split(";"))
                                                    .map(String::trim)
                                                    .forEach(messageList::add); // split all returned customer by ; and store into conversation list
                                            System.out.println("Messages:");
                                            messageList.forEach(n -> System.out.println(n));
                                        }
                                        do {
                                            error = false;
                                            System.out.println("Options -> 0: New message; " +
                                                    "1: Edit Message; 2: deleteMessage; 3: exit conversation");
                                            try {
                                                userInputInt = scan.nextInt();
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
                                        writer.write(Integer.toString(userInputInt));
                                        writer.println();
                                        writer.flush();
                                        switch (userInputInt) {
                                            case 0:
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
                                                writer.write(userInputString);
                                                writer.println();
                                                writer.flush();
                                                clientInput = reader.readLine();
                                                if (clientInput.equals("blocked")) {
                                                    System.out.println("You are blocked by receiver, cannot send message");
                                                }
                                                break;
                                            case 1:
                                                do {
                                                    error = false;
                                                    System.out.println("Enter the old message, time," +
                                                            " and new message separate with semicolon");
                                                    userInputString = scan.nextLine();
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
                                                } while (error);
                                                writer.write(userInputString);
                                                writer.println();
                                                writer.flush();
                                                clientInput = reader.readLine();
                                                if (clientInput.equals("Message not found")) {
                                                    System.out.println("Input message not found");
                                                }
                                                break;
                                            case 2:
                                                do {
                                                    error = false;
                                                    System.out.println("Enter the time and" +
                                                            " content of the message you want to delete" +
                                                            " separate with semicolon");
                                                    userInputString = scan.nextLine();
                                                    if (userInputString == null) {
                                                        System.out.println(EXIT);
                                                        return;
                                                    }
                                                    if (userInputString.isEmpty()) {
                                                        System.out.println("time and content cannot be empty");
                                                        error = true;
                                                    }
                                                    if (!userInputString.contains(";")) {
                                                        System.out.println("Invalid input, no semicolon present");
                                                        error = true;
                                                    }
                                                } while (error);
                                                writer.write(userInputString);
                                                writer.println();
                                                writer.flush();
                                                clientInput = reader.readLine();
                                                if (clientInput.equals("Message not found")) {
                                                    System.out.println("Input message not found");
                                                }
                                                break;
                                            case 3:
                                                System.out.println("Exit conversation");
                                                conversationOff = true;
                                                break;
                                        }
                                        if (conversationOff) {
                                            break;
                                        }
                                    } while (true);
                                }
                            } else {
                                System.out.println("You have no exist conversation," +
                                        " please add new user to make conversation");
                            }
                    }
                    if (logOff) {
                        break;
                    }
                } while (true);
            } while (true);
            //UnknownHostException | SocketException e
        } catch (UnknownHostException | SocketException e) {
            System.out.println("Given host name and port number cannot" +
                    " establish connection with the server");
        }
    }
}
