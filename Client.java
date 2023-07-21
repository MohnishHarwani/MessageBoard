import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
        ArrayList<String> storeNameList = new ArrayList<>();
        ArrayList<String> conversationList = new ArrayList<>();

        // Start program
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

        try {
            // Connect to server
            System.out.println("Establishing connection");
            Socket socket = new Socket("localhost", 1234);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // Login page
            boolean loggedIn = false;
            do {
                do {
                    // login page
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

                        writer.write("Create account");
                        writer.println();
                        writer.flush();

                        writer.write(userInfoTemp);
                        writer.println();
                        writer.flush();

                        clientInput = reader.readLine();
                        if (clientInput.equals("User Already exist")) {
                            System.out.println(clientOutput);
                        } else {
                            loggedIn = true;
                        }
                        // log in
                    } else if (userInputInt == 1) {
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

                        writer.write("Log in");
                        writer.println();
                        writer.flush();

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

                // Display
                seller = true;
                storeNameList.removeAll(storeNameList);
                userNameList.removeAll(userNameList);
                clientInput = reader.readLine();
                if (clientInput.equals("Customer display")) {
                    seller = false;
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
                    while (counter < userNameList.size()) {
                        System.out.printf("Seller: %s ->" +
                                " Stores: %s\n", userNameList.get(counter), storeNameList.get(counter));
                        counter++;
                    }
                } else {
                    while (counter < userNameList.size()) {
                        System.out.println("Customer: +" + userNameList.get(counter));
                        counter++;
                    }
                }

                // option
                do {
                    error = false;
                    System.out.println("Options -> 0:Select a specific conversation;" +
                            " 1: Search user to create new conversation; 2: Block any user;" +
                            " 3: Account Modification; 4: Unblock any user; 5: Invs any user; 6: UnInvis any user;" +
                            " 7: add store; 8: log off");
                    try {
                        userInputInt = scan.nextInt();
                        if (userInputInt < 0 || userInputInt > 5) {
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
                            System.out.println("Your are not seller");
                        } else {
                            storeNameList.removeAll(storeNameList);
                            clientInput = reader.readLine();
                            Arrays.stream(clientInput.split(";")) // split all returned customer by ; and store into userNameList
                                    .map(String::trim)
                                    .forEach(storeNameList::add);
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
                            writer.write(userInputString);
                            writer.println();
                            writer.flush();
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
                                break;
                        }
                        writer.write(Integer.toString(userInputInt));
                        writer.println();
                        writer.flush();
                        if (userInputInt != 3) {
                            do {
                                error = false;
                                System.out.printf("What is your new %s?%n", tempString);
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
                            writer.write(userInputString);
                            writer.println();
                            writer.flush();
                        }
                        System.out.println(reader.readLine());
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
                                    System.out.println("Email and Namecannot be empty");
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
                        conversationList.removeAll(conversationList);
                        Arrays.stream(clientInput.split(";"))
                                .map(String::trim)
                                .forEach(conversationList::add); // split all returned customer by ; and store into conversation list
                        System.out.println("Conversation users:");
                        counter = 0;
                        while (counter < conversationList.size()) {
                            System.out.println(conversationList.get(counter));
                            counter++;
                        }
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
                                        } while (error);
                                        writer.write(userInputString);
                                        writer.println();
                                        writer.flush();
                                        break;
                                    case 2:
                                        do {
                                            error = false;
                                            System.out.println("Enter the time and" +
                                                    " content of the message you want to delete");
                                            userInputString = scan.nextLine();
                                            if (userInputString == null) {
                                                System.out.println(EXIT);
                                                return;
                                            }
                                            if (userInputString.isEmpty()) {
                                                System.out.println("Email and Name cannot be empty");
                                                error = true;
                                            }
                                        } while (error);
                                        writer.write(userInputString);
                                        writer.println();
                                        writer.flush();
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
                        break;
                }
                if (logOff) {
                    break;
                }
            } while (true);
        } catch (UnknownHostException | SocketException e) {
            System.out.println("Given host name and port number cannot" +
                    " establish connection with the server");
        }
    }
}
