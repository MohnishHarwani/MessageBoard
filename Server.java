import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class Server {
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";

    private static ArrayList<User> users;
    private static ArrayList<String> fileNames = new ArrayList<>();
    private static User currentUser;

    public static void addUser(User user) throws UserExistException {
        if (checkUniqueUser(user.getEmail(), user.getNameOfUser())) {
            users.add(user);
        } else {
            throw new UserExistException("User Already exist");
        }
    }

    public static boolean checkUniqueUser(String email, String nameofUser) {
        return !users.stream().anyMatch(validUser -> validUser.getEmail().equals(email) // check if input email and name combination is unique in server users
                && validUser.getNameOfUser().equals(nameofUser));
    }

    public static boolean authenticateUser(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                currentUser = user;
                return true;
            }
        }
        return false;
    }
    public static void unBlockUser(User user) {
        ArrayList<User> tempUserList;
        tempUserList = currentUser.getBlockList();
        tempUserList.removeIf(n -> n.equals(user)); // remove user from currentUser's blocklist if any user in blocklist is equal to input user
        currentUser.setBlockList(tempUserList);
    }

    public static void unInvisUser(User user) {
        ArrayList<User> tempUserList;
        tempUserList = currentUser.getInvisibleList();
        tempUserList.removeIf(n -> n.equals(user)); // remove user from currentUser's invisiblelist if any user in blocklist is equal to input user
        currentUser.setInvisibleList(tempUserList);
    }

    public static Optional<User> exactPerson(ArrayList<User> listOfUser, String email, String name) {
        return listOfUser.stream()
                .filter(user -> user.getEmail().equals(email) && user.getEmail().equals(name)).findFirst();
        // check if input email and name combination exist in the input list of user
    }

    public static ArrayList<User> searchValidUser(String searchingUsername) {
        ArrayList<User> userContainSearch = new ArrayList<>();
        users.stream().filter(n -> n.isUserType() != currentUser.isUserType())
                .forEach(userContainSearch::add); // remove same type user as currentUser
        userContainSearch.removeIf(n -> !n.getNameOfUser().toLowerCase().contains(searchingUsername.toLowerCase()));
        // check if any user's name contain the searching String
        return userContainSearch;
    }

    public static ArrayList<User> allVisibleStore() {
        ArrayList<User> tempVisibleUser = users;
        tempVisibleUser.removeIf(user -> user.isInvisible(currentUser) || currentUser.isInvisible(user));
        // remove sellers from list if it is invisible
        tempVisibleUser.removeIf(user -> !user.isUserType());
        // remove all customer type from the list
        return tempVisibleUser;
    }

    public static ArrayList<User> currentVisibleCustomer() {
        ArrayList<User> tempVisibleUser = currentUser.getConversationUser();
        tempVisibleUser.removeIf(user -> user.isInvisible(currentUser) || currentUser.isInvisible(user));
        // remove customer from list if it is invisible
        return tempVisibleUser;
    }

    public static void displayMessage(ArrayList<String[]> messages, int amount) {
        int startIndex = 0;
        if (amount >= messages.size()) {
            startIndex = 0;
        } else {
            startIndex = messages.size() - amount;
        }
        messages.subList(startIndex, messages.size())
                .stream()
                .limit(amount)
                .forEach(array -> System.out.println(String.join("-", array)));
        // starting from start index, output number of amount message and join the message with -
    }

    public static void main(String[] args) throws IOException {
        try {
            //read previous User data
            try {
                File file = new File("userInfo");
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bfr.readLine()) != null) {
                    String[] a = line.split(",", 4);
                    users.add(new User(Boolean.parseBoolean(a[0]), a[1], a[2], a[3]));
                }
                bfr.close();
            } catch (FileNotFoundException e) {
                File file = new File("userInfo");
                file.createNewFile();
            }
            ServerSocket serverSocket = new ServerSocket(1234);
            Socket socket = serverSocket.accept();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            ArrayList<User> tempUserList = new ArrayList<>();
            ArrayList<User> tempUserList2 = new ArrayList<>();
            User placeHolderUser;
            User tempReceiver;

            String serverInput = "";
            String serverOutput = "";
            String tempSplit[];
            Optional<User> tempUser;
            boolean conversationOff = false;

            // Server login
            do {
                serverInput = reader.readLine();
                if (serverInput.equals("Create account")) {
                    try {
                        serverInput = reader.readLine();
                        tempSplit = serverInput.split(",", 4);
                        addUser(new User(Boolean.parseBoolean(tempSplit[0]), tempSplit[1], tempSplit[2], tempSplit[3]));
                        writer.write(SUCCESS);
                        writer.println();
                        writer.flush();
                    } catch (UserExistException e) {
                        writer.write(e.getMessage());
                        writer.println();
                        writer.flush();
                    }
                } else if (serverInput.equals("Log in")) {
                    serverInput = reader.readLine();
                    tempSplit = serverInput.split(",", 2);
                    if (authenticateUser(tempSplit[0], tempSplit[1])) {
                        writer.write(SUCCESS);
                    } else {
                        writer.write(FAIL);
                    }
                    writer.println();
                    writer.flush();
                }
                serverInput = reader.readLine();
                if (serverInput.equals("pass")) {
                    break;
                }
            } while (true);

            boolean seller = currentUser.isUserType();

            while (socket.isConnected()) {
                // display
                if (seller) {
                    writer.write("Seller display");
                    writer.println();
                    writer.flush();
                    serverOutput = currentVisibleCustomer().stream().map(User::getNameOfUser)
                            .collect(Collectors.joining(";")); // combine returned Arraylist's user and send to client as a string
                } else {
                    writer.write("Customer display");
                    writer.println();
                    writer.flush();
                    //store string
                    serverOutput = allVisibleStore().stream()
                            .map(Users -> Users.getStoreName().stream().collect(Collectors.joining(",")))
                            .collect(Collectors.joining(";")); // combine returned Arraylist's user and send to client as a string it use , tocombine stores and use ; to separate each seller
                    writer.write(serverOutput);
                    writer.println();
                    writer.flush();
                    // name string
                    serverOutput = allVisibleStore().stream().map(User::getNameOfUser)
                            .collect(Collectors.joining(";")); // combine returned Arraylist's user and send to client as a string
                }
                writer.write(serverOutput);
                writer.println();
                writer.flush();

                //operation
                int typeOfOperation = 4;
                boolean endProgram = false;

                while (typeOfOperation != 0) {
                    serverInput = reader.readLine();
                    typeOfOperation = Integer.parseInt(serverInput);

                    switch (typeOfOperation) {
                        case 8:
                            endProgram = true;
                            break;
                        case 7:
                            tempUserList.removeAll(tempUserList);
                            if (currentUser.isUserType()) {
                                writer.write("Not seller");
                                writer.println();
                                writer.flush();
                            } else {
                                writer.println("seller");
                                serverOutput = currentUser.getStoreName().stream().collect(Collectors.joining(";"));
                                writer.write(serverOutput);
                                writer.println();
                                writer.flush();
                                currentUser.addStore(reader.readLine());
                            }
                            break;
                        case 6:
                            tempUserList.removeAll(tempUserList);
                            serverOutput = currentUser.getInvisibleList().stream()
                                    .map(user -> user.getEmail() + "," + user.getNameOfUser())
                                    .collect(Collectors.joining(";")); // get all the user from currentUser's block list and send to client as string
                            serverOutput = (serverInput.isEmpty()) ? "No result" : serverOutput;
                            writer.write(serverOutput);
                            writer.println();
                            writer.flush();
                            serverInput = reader.readLine();
                            tempSplit = serverInput.split(",", 2);
                            tempUser = exactPerson(tempUserList, tempSplit[0], tempSplit[1]);
                            if (tempUser.isPresent()) {
                                unInvisUser(tempUser.get());
                                writer.write(SUCCESS);
                            } else {
                                writer.write(FAIL);
                            }
                            writer.println();
                            writer.flush();
                            break;
                        case 5:
                            tempUserList.removeAll(tempUserList);
                            tempUserList2.removeAll(tempUserList2);
                            tempUserList = searchValidUser(reader.readLine());
                            serverOutput = tempUserList.stream()
                                    .map(user -> user.getEmail() + "," + user.getNameOfUser())
                                    .collect(Collectors.joining(";")); // combine returned Arraylist's user and send to client as a string
                            serverOutput = (serverInput.isEmpty()) ? "No result" : serverOutput;
                            writer.write(serverOutput);
                            writer.println();
                            writer.flush();
                            serverInput = reader.readLine();
                            tempSplit = serverInput.split(",", 2);
                            tempUser = exactPerson(tempUserList, tempSplit[0], tempSplit[1]);
                            if (tempUser.isPresent()) {
                                tempUserList2 = currentUser.getInvisibleList();
                                tempUserList2.add(tempUser.get());
                                currentUser.setInvisibleList(tempUserList2);
                                writer.write(SUCCESS);
                            } else {
                                writer.write(FAIL);
                            }
                            writer.println();
                            writer.flush();
                            break;
                        case 4:
                            tempUserList.removeAll(tempUserList);
                            serverOutput = currentUser.getBlockList().stream()
                                    .map(user -> user.getEmail() + "," + user.getNameOfUser())
                                    .collect(Collectors.joining(";")); // get all the user from currentUser's block list and send to client as string
                            serverOutput = (serverInput.isEmpty()) ? "No result" : serverOutput;
                            writer.write(serverOutput);
                            writer.println();
                            writer.flush();
                            serverInput = reader.readLine();
                            tempSplit = serverInput.split(",", 2);
                            tempUser = exactPerson(tempUserList, tempSplit[0], tempSplit[1]);
                            if (tempUser.isPresent()) {
                                unBlockUser(tempUser.get());
                                writer.write(SUCCESS);
                            } else {
                                writer.write(FAIL);
                            }
                            writer.println();
                            writer.flush();
                            break;
                        case 3:
                            serverInput = reader.readLine();
                            switch (Integer.parseInt(serverInput)) {
                                case 0:
                                    currentUser.setNameOfUser(reader.readLine());
                                    break;
                                case 1:
                                    currentUser.setEmail(reader.readLine());
                                    break;
                                case 2:
                                    currentUser.setPassword(reader.readLine());
                                    break;
                                case 3:
                                    users.removeIf(n -> currentUser.equals(n)); // remove user from the server user list which is deleting a user
                                    break;
                            }
                            writer.write(SUCCESS);
                            writer.println();
                            writer.flush();
                        case 2:
                            tempUserList.removeAll(tempUserList);
                            tempUserList2.removeAll(tempUserList2);
                            tempUserList = searchValidUser(reader.readLine());
                            serverOutput = tempUserList.stream()
                                    .map(user -> user.getEmail() + "," + user.getNameOfUser())
                                    .collect(Collectors.joining(";")); // combine returned Arraylist's user and send to client as a string
                            serverOutput = (serverInput.isEmpty()) ? "No result" : serverOutput;
                            writer.write(serverOutput);
                            writer.println();
                            writer.flush();
                            serverInput = reader.readLine();
                            tempSplit = serverInput.split(",", 2);
                            tempUser = exactPerson(tempUserList, tempSplit[0], tempSplit[1]);
                            if (tempUser.isPresent()) {
                                tempUserList2 = currentUser.getBlockList();
                                tempUserList2.add(tempUser.get());
                                currentUser.setBlockList(tempUserList2);
                                writer.write(SUCCESS);
                            } else {
                                writer.write(FAIL);
                            }
                            writer.println();
                            writer.flush();
                            break;
                        case 1:
                            tempUserList.removeAll(tempUserList);
                            tempUserList2.removeAll(tempUserList2);
                            tempUserList = searchValidUser(reader.readLine());
                            serverOutput = tempUserList.stream()
                                    .map(user -> user.getEmail() + "," + user.getNameOfUser())
                                    .collect(Collectors.joining(";")); // combine returned Arraylist's user and send to client as a string
                            serverOutput = (serverInput.isEmpty()) ? "No result" : serverOutput;
                            writer.write(serverOutput);
                            writer.println();
                            writer.flush();
                            serverInput = reader.readLine();
                            tempSplit = serverInput.split(",", 2);
                            tempUser = exactPerson(tempUserList, tempSplit[0], tempSplit[1]);
                            if (tempUser.isPresent()) {
                                currentUser.addConversation(tempUser.get());
                                writer.write(SUCCESS);
                            } else {
                                writer.write(FAIL);
                            }
                            writer.println();
                            writer.flush();
                            break;
                        case 0:
                            serverOutput = currentUser.getConversationUser().stream()
                                    .map(user -> user.getEmail() + "," + user.getNameOfUser())
                                    .collect(Collectors.joining(";"));
                            writer.write(serverOutput);
                            writer.println();
                            writer.flush();
                            serverInput = reader.readLine();
                            tempSplit = serverInput.split(",", 2);
                            tempUser = exactPerson(tempUserList, tempSplit[0], tempSplit[1]);
                            if (tempUser.isPresent()) {
                                tempReceiver = tempUser.get();
                                writer.write(SUCCESS);
                                writer.println();
                                writer.flush();
                                do {
                                    serverInput = reader.readLine();
                                    displayMessage(currentUser.display50Message(tempReceiver), 50);
                                    switch (Integer.parseInt(serverInput)) {
                                        case 0:
                                            serverInput = reader.readLine();
                                            if (!tempReceiver.isBlocked(currentUser)) {
                                                if (serverInput.contains(".txt")) {
                                                    currentUser.createMessage(tempReceiver, serverInput);
                                                } else {
                                                    currentUser.sendTxtFile(tempReceiver, serverInput);
                                                }
                                            } else {
                                                System.out.println("You are blocked by receiver, cannot send message");
                                            }
                                            break;
                                        case 1:
                                            serverInput = reader.readLine();
                                            tempSplit = serverInput.split(";", 3);
                                            try {
                                                currentUser.editMessage(tempReceiver, tempSplit[0], tempSplit[1], tempSplit[2]);
                                            } catch (NoMessageFoundException e) {
                                                System.out.println(e.getMessage());
                                            }
                                            break;
                                        case 2:
                                            serverInput = reader.readLine();
                                            tempSplit = serverInput.split(";", 2);
                                            try {
                                                currentUser.deleteMessage(tempReceiver, tempSplit[0], tempSplit[1]);
                                            } catch (NoMessageFoundException e) {
                                                System.out.println(e.getMessage());
                                            }
                                            break;
                                        case 3:
                                            conversationOff = true;
                                            break;
                                    }
                                    if (conversationOff) {
                                        break;
                                    }
                                } while (true);
                            } else {
                                writer.write(FAIL);
                                writer.println();
                                writer.flush();
                            }
                            break;
                    }
                    if (endProgram) {
                        break;
                    }
                }
                if (endProgram) {
                    break;
                }
            }
            System.out.println("Thank you for using the Message Board");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
