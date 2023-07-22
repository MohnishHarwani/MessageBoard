import javax.sound.midi.SysexMessage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Dictionary;
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

            ArrayList<String> tempUsers = new ArrayList<String>();
            try (BufferedReader reader = new BufferedReader(new FileReader("userinfo.csv"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    tempUsers.add(line);
                }

                tempUsers.add(String.format("%s,%s,%s,%s",(user.isUserType()) ? "true" : "false",
                        user.getEmail(), user.getPassword(), user.getNameOfUser()));
                PrintWriter pw = new PrintWriter("userinfo.csv");
                tempUsers.forEach((n) -> { pw.print(n + "\n"); }); // print all previous message in to the file
                pw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new UserExistException("User Already exist");
        }
    }

    public static void addAction(String Action, User user2) {
        try (BufferedReader reader = new BufferedReader(new FileReader("userAction.csv"))) {
            ArrayList<String> tempAction = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                tempAction.add(line);
            }
            tempAction.add(String.format("%s,%s,%s", currentUser.toString(), Action, user2.toString()));
            PrintWriter pw = new PrintWriter("userAction.csv");
            tempAction.forEach((n) -> { pw.print(n + "\n"); }); // print all previous message in to the file
            pw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void userAddStore(String storeName) {
        try (BufferedReader reader = new BufferedReader(new FileReader("userAction.csv"))) {
            ArrayList<String> tempAction = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                tempAction.add(line);
            }
            tempAction.add(String.format("%s,%s,%s", currentUser.toString(), "store", storeName));
            PrintWriter pw = new PrintWriter("userAction.csv");
            tempAction.forEach((n) -> { pw.print(n + "\n"); }); // print all previous message in to the file
            pw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void removeAction(String Action, User user2) {
        try (BufferedReader reader = new BufferedReader(new FileReader("userAction.csv"))) {
            ArrayList<String> tempAction = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.equals(String.format("%s,%s,%s", currentUser.toString(), Action, user2.toString()))) {
                    tempAction.add(line);
                }
            }
            PrintWriter pw = new PrintWriter("userAction.csv");
            tempAction.forEach((n) -> { pw.print(n + "\n"); }); // print all previous message in to the file
            pw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void replaceAllConversationName(String modifier) throws NoMessageFoundException {
        String currentDirectory = System.getProperty("user.dir");
        File directory = new File(currentDirectory);
        File[] files = directory.listFiles((dir, name) -> name.contains(currentUser.getNameOfUser()));
        if (files != null && files.length > 0) {
            for (File file : files) {
                try {
                    String fileName = file.getName();
                    BufferedReader infoReader = new BufferedReader(new FileReader(fileName));
                    ArrayList<String> tempMessage = new ArrayList<String>();
                    String line;
                    while ((line = infoReader.readLine()) != null) {
                        tempMessage.add(line);
                    }
                    infoReader.close();
                    tempMessage.replaceAll(s -> s.contains(currentUser.getNameOfUser()) ?
                            s.replace(currentUser.getNameOfUser(), modifier) : s);
                    PrintWriter pwInfo = new PrintWriter(fileName);
                    tempMessage.forEach((n) -> { pwInfo.print(n + "\n"); }); // print all previous message in to the file
                    pwInfo.close();
                    String newFileName = fileName.replace(currentUser.getNameOfUser(), modifier);
                    File newFile = new File(directory, newFileName);
                    boolean success = file.renameTo(newFile);
                    if (success) {
                        System.out.println("Renamed: " + fileName + " -> " + newFileName);
                    } else {
                        System.out.println("Failed to rename: " + fileName);
                    }
                } catch (IOException e) {
                    throw new NoMessageFoundException("User have no conversation");
                }
            }
        }
    }

    public static void accountModification(String modifier, int action) {
        try {
            BufferedReader infoReader = new BufferedReader(new FileReader("userinfo.csv"));
            BufferedReader actionReader = new BufferedReader(new FileReader("userAction.csv"));
            ArrayList<String> tempInfo = new ArrayList<String>();
            ArrayList<String> tempAction = new ArrayList<String>();
            String line;
            while ((line = infoReader.readLine()) != null) {
                tempInfo.add(line);
            }
            while ((line = actionReader.readLine()) != null) {
                tempAction.add(line);
            }
            infoReader.close();
            actionReader.close();
            switch (action) {
                case 0:
                    tempInfo.replaceAll(s -> s.contains(currentUser.getNameOfUser()) ?
                            s.replace(currentUser.getNameOfUser(), modifier) : s);
                    tempAction.replaceAll(s -> s.contains(currentUser.getNameOfUser()) ?
                            s.replace(currentUser.getNameOfUser(), modifier) : s);
                    break;
                case 1:
                    tempInfo.replaceAll(s -> s.contains(currentUser.getEmail()) ?
                            s.replace(currentUser.getEmail(), modifier) : s);
                    tempAction.replaceAll(s -> s.contains(currentUser.getNameOfUser()) ?
                            s.replace(currentUser.getEmail(), modifier) : s);
                    break;
                case 2:
                    tempInfo.replaceAll(s -> s.contains(currentUser.getPassword()) ?
                            s.replace(currentUser.getPassword(), modifier) : s);
                    break;
                case 3:
                    tempInfo.removeIf(s -> s.contains(currentUser.getNameOfUser()));
                    tempAction.removeIf(s -> s.contains(currentUser.getNameOfUser()));
                    break;
            }

            PrintWriter pwInfo = new PrintWriter("userInfo.csv");
            tempInfo.forEach((n) -> { pwInfo.print(n + "\n"); }); // print all previous message in to the file
            pwInfo.close();

            PrintWriter pwAction = new PrintWriter("userAction.csv");
            tempAction.forEach((n) -> { pwAction.print(n + "\n"); }); // print all previous message in to the file
            pwAction.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
        removeAction("block",user);
    }

    public static void unInvisUser(User user) {
        ArrayList<User> tempUserList = new ArrayList<>();
        tempUserList = currentUser.getInvisibleList();
        tempUserList.removeIf(n -> n.equals(user)); // remove user from currentUser's invisiblelist if any user in blocklist is equal to input user
        currentUser.setInvisibleList(tempUserList);
        removeAction("invis",user);
    }

    public static Optional<User> exactPerson(ArrayList<User> listOfUser, String email, String name) {
        return listOfUser.stream()
                .filter(user -> user.getEmail().equals(email) && user.getNameOfUser().equals(name)).findFirst();
        // check if input email and name combination exist in the input list of user
    }

    public static ArrayList<User> searchValidUser(String searchingUsername) {
        ArrayList<User> userContainSearch = new ArrayList<>();
        users.stream().filter(n -> n.isUserType() != currentUser.isUserType())
                .forEach(userContainSearch::add); // remove same type user as currentUser
        userContainSearch.removeIf(n -> !n.getNameOfUser().toLowerCase().contains(searchingUsername.toLowerCase()));
        userContainSearch.removeIf(n -> n.isInvisible(currentUser));
        // check if any user's name contain the searching String
        return userContainSearch;
    }

    public static ArrayList<User> allVisibleStore() {
        ArrayList<User> tempVisibleUser = users;
        tempVisibleUser.removeIf(user -> user.isInvisible(currentUser));
        // remove sellers from list if it is invisible
        tempVisibleUser.removeIf(user -> !user.isUserType());
        // remove all customer type from the list
        return tempVisibleUser;
    }

    public static ArrayList<User> currentVisibleConversationUser() {
        ArrayList<User> tempVisibleUser = currentUser.getConversationUser();
        tempVisibleUser.removeIf(user -> user.isInvisible(currentUser));
        // remove customer from list if it is invisible
        return tempVisibleUser;
    }

    public static String displayMessage(ArrayList<String[]> messages, int amount) {
        ArrayList<String> tempMessages = new ArrayList<>();
        String messagsCombined;
        int startIndex = 0;
        if (amount >= messages.size()) {
            startIndex = 0;
        } else {
            startIndex = messages.size() - amount;
        }
        messages.subList(startIndex, messages.size())
                .stream()
                .limit(amount)
                .forEach(array -> tempMessages.add(String.join("-", array)));
        messagsCombined = tempMessages.stream().collect(Collectors.joining(";"));
        return messagsCombined;
        // starting from start index, output number of amount message and join the message with -
    }

    public static void main(String[] args) throws IOException {
        Optional<User> tempUser;
        Optional<User> tempUser2;
        try {
            //read previous User data
            try {
                users = new ArrayList<User>();
                File file = new File("userInfo.csv");
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bfr.readLine()) != null) {
                    String[] a = line.split(",", 4);
                    users.add(new User(Boolean.parseBoolean(a[0]), a[1], a[2], a[3]));
                }
                bfr.close();
            } catch (FileNotFoundException e) {
                File file = new File("userInfo.csv");
                file.createNewFile();
            }

            try {
                int counter = 0;
                File file = new File("userAction.csv");
                BufferedReader bfr = new BufferedReader(new FileReader(file));
                String line;
                while ((line = bfr.readLine()) != null) {
                    String[] a = line.split(",", 3);
                    tempUser = exactPerson(users, a[0].substring(0, a[0].indexOf("-")),
                            a[0].substring(a[0].indexOf("-") + 1));
                    if (tempUser.isPresent()) {
                        if (a[1].equals("store")) {
                            tempUser.get().addStore(a[2]);
                        } else {
                            tempUser2 = exactPerson(users, a[2].substring(0, a[2].indexOf("-")),
                                    a[2].substring(a[2].indexOf("-") + 1));
                            if (tempUser2.isPresent()) {
                                switch (a[1]) {
                                    case "block":
                                        tempUser.get().addBlockUser(tempUser2.get());
                                        break;
                                    case "invis":
                                        tempUser.get().addInvis(tempUser2.get());
                                        break;
                                    case "chat":
                                        tempUser.get().addConversation(tempUser2.get());
                                        tempUser2.get().addConversation(tempUser.get());
                                        break;
                                }
                            }
                        }
                    }
                }
                bfr.close();
            } catch (FileNotFoundException e) {
                File file = new File("userAction.csv");
                file.createNewFile();
            }
            ServerSocket serverSocket = new ServerSocket(4242);
            Socket socket = serverSocket.accept();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            ArrayList<User> tempUserList = new ArrayList<>();
            ArrayList<User> tempUserList2 = new ArrayList<>();
            User tempReceiver;

            String tempString = "";
            String serverInput = "";
            String serverOutput = "";
            String tempSplit[];

            boolean conversationOff = false;

            // Server login
            do {
                serverInput = reader.readLine();
                if (serverInput.equals("Create account")) {
                    try {
                        serverInput = reader.readLine();
                        tempSplit = serverInput.split(",", 4);
                        addUser(new User(Boolean.parseBoolean(tempSplit[0]), tempSplit[1], tempSplit[2], tempSplit[3]));
                        writer.println(SUCCESS);
                        writer.flush();
                    } catch (UserExistException e) {
                        writer.println(e.getMessage());
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
            //System.out.println("log in success");
            boolean seller = currentUser.isUserType();

            while (socket.isConnected()) {
                do {
                    // display
                    if (seller) {
                        writer.println("Seller display");
                        writer.flush();
                        serverOutput = currentUser.getStoreName().stream().collect(Collectors.joining(";"));
                        // combine returned Arraylist's user and send to client as a string
                    } else {
                        writer.println("Customer display");
                        writer.flush();
                        // seller store string
                        serverOutput = allVisibleStore().stream()
                                .map(user -> user.getStoreName().stream().collect(Collectors.joining(",")))
                                .map(storeNames -> storeNames.isEmpty() ? "None" : storeNames)
                                .collect(Collectors.joining(";")); // combine returned Arraylist's user and send to client as a string it use , to combine stores and use ; to separate each seller
                        writer.println(serverOutput);
                        writer.flush();
                        //serverOutput = allVisibleStore().stream().map(s -> ";" + s.getNameOfUser()).collect(Collectors.joining());
                        serverOutput = allVisibleStore().stream().map(User::getNameOfUser).collect(Collectors.joining(";"));
                    }
                    writer.println(serverOutput);
                    writer.flush();
                    serverOutput = currentVisibleConversationUser().stream().map(User::getNameOfUser)
                            .collect(Collectors.joining(";"));
                    writer.println(serverOutput);
                    writer.flush();

                    //operation
                    int typeOfOperation;
                    boolean endProgram = false;

                    serverInput = reader.readLine();
                    typeOfOperation = Integer.parseInt(serverInput);

                    endProgram = false;
                    switch (typeOfOperation) {
                        case 8:
                            endProgram = true;
                            break;
                        case 7:
                            if (!currentUser.isUserType()) {
                                writer.println("Not seller");
                                writer.flush();
                            } else {
                                writer.println("seller");
                                writer.flush();
                                serverOutput = currentUser.getStoreName().stream().collect(Collectors.joining(";"));
                                writer.write(serverOutput);
                                writer.println();
                                writer.flush();
                                serverInput = reader.readLine();
                                currentUser.addStore(serverInput);
                                userAddStore(serverInput);
                            }
                            break;
                        case 6:
                            tempUserList.removeAll(tempUserList);
                            tempUserList = currentUser.getInvisibleList();
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
                                addAction("invis",tempUser.get());
                                writer.write(SUCCESS);
                            } else {
                                writer.write(FAIL);
                            }
                            writer.println();
                            writer.flush();
                            break;
                        case 4:
                            tempUserList.removeAll(tempUserList);
                            tempUserList = currentUser.getBlockList();
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
                                    tempString = reader.readLine();
                                    try {
                                        replaceAllConversationName(tempString);
                                    } catch (NoMessageFoundException e) {
                                        writer.println(e.getMessage());
                                        writer.flush();
                                    }
                                    accountModification(tempString, Integer.parseInt(serverInput));
                                    currentUser.setNameOfUser(tempString);
                                    break;
                                case 1:
                                    tempString = reader.readLine();
                                    accountModification(tempString, Integer.parseInt(serverInput));
                                    currentUser.setEmail(tempString);
                                    break;
                                case 2:
                                    tempString = reader.readLine();
                                    accountModification(tempString, Integer.parseInt(serverInput));
                                    currentUser.setPassword(tempString);
                                    break;
                                case 3:
                                    accountModification("aa", Integer.parseInt(serverInput));
                                    users.removeIf(n -> currentUser.equals(n)); // remove user from the server user list which is deleting a user
                                    endProgram = true;
                                    break;
                            }
                            writer.println(SUCCESS);
                            writer.flush();
                            break;
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
                                addAction("block",tempUser.get());
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
                                addAction("chat", tempUser.get());
                                writer.write(SUCCESS);
                            } else {
                                writer.write(FAIL);
                            }
                            writer.println();
                            writer.flush();
                            break;
                        case 0:
                            tempUserList = currentUser.getConversationUser();
                            serverOutput = currentUser.getConversationUser().stream()
                                    .map(user -> user.getEmail() + "," + user.getNameOfUser())
                                    .collect(Collectors.joining(";"));
                            if (!tempUserList.isEmpty()) {
                                writer.println(serverOutput);
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
                                        try {
                                            serverOutput = displayMessage(currentUser.display50Message(tempReceiver), 50);
                                            writer.println(serverOutput);
                                        } catch (NoPreviousMessageException e) {
                                            writer.println(FAIL);
                                        }
                                        writer.flush();
                                        serverInput = reader.readLine();
                                        switch (Integer.parseInt(serverInput)) {
                                            case 0:
                                                serverInput = reader.readLine();
                                                if (!tempReceiver.isBlocked(currentUser)) {
                                                    if (serverInput.contains(".txt")) {
                                                        currentUser.sendTxtFile(tempReceiver, serverInput);
                                                        writer.println(SUCCESS);
                                                    } else {
                                                        currentUser.createMessage(tempReceiver, serverInput);
                                                        writer.println(SUCCESS);
                                                    }
                                                } else {
                                                    writer.println("blocked");
                                                }
                                                writer.flush();
                                                break;
                                            case 1:
                                                serverInput = reader.readLine();
                                                tempSplit = serverInput.split(";", 3);
                                                try {
                                                    currentUser.editMessage(tempReceiver, tempSplit[0], tempSplit[1], tempSplit[2]);
                                                } catch (NoMessageFoundException e) {
                                                    writer.println(e.getMessage());
                                                    writer.flush();
                                                }
                                                writer.println(SUCCESS);
                                                writer.flush();
                                                break;
                                            case 2:
                                                serverInput = reader.readLine();
                                                tempSplit = serverInput.split(";", 2);
                                                try {
                                                    currentUser.deleteMessage(tempReceiver, tempSplit[0], tempSplit[1]);
                                                } catch (NoMessageFoundException e) {
                                                    writer.println(e.getMessage());
                                                    writer.flush();
                                                }
                                                writer.println(SUCCESS);
                                                writer.flush();
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
                                    writer.println(FAIL);
                                    writer.flush();
                                }
                                break;
                            } else {
                                writer.println("No conversation");
                                writer.flush();
                            }
                    }
                    if (endProgram) {
                        break;
                    }
                } while (true);
            }
            //RuntimeException | SocketException e
        } catch (RuntimeException | SocketException e) {
            System.out.printf("%s disconnect\n", currentUser.toString());
        }
    }
}
