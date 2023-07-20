import javax.annotation.processing.SupportedSourceVersion;
import java.io.*;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Scanner;

public class User extends Thread {
    private boolean userType; // true = seller, false = customer
    private String password;
    private String username;
    private String nameofUser;
    private ArrayList<String> conversationName;
    private ArrayList<String> blockList;

    public User(boolean userType, String password, String nameofUser, String username) {
        this.userType = userType;
        this.password = password;
        this.nameofUser = nameofUser;
        this.username = username;
    }

    public String getNameofUser() {
        return nameofUser;
    }

    public void setUserType(boolean userType) {
        this.userType = userType;
    }

    public boolean isUserType() {
        return userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> returnConversationName() {
        return conversationName;
    }

    public boolean canMakeConversation(User receiver) {
        return receiver.isUserType() == this.userType;
    }

    public boolean isBlocked(User user) {
        int counter = 0;
        while(counter++ < blockList.size()) {
            if (user.getNameofUser().equals(blockList.get(counter)))
                return true;
        }
        return false;
    }

    public boolean checkUniqueUser(User user) {
        return !(user.getUsername().equals(this.username) &&
                user.isUserType() == this.userType && user.getNameofUser().equals(this.nameofUser));
    }

    public boolean addConversation(User receiver) {
        boolean containsName = false;

        for (String a : conversationName) {
            if (a.equals(receiver.getNameofUser()))
                containsName = true;
        }

        if (!containsName) {
            File file = new File(String.format("%s_%s.csv", this.nameofUser, receiver.getNameofUser()));
            conversationName.add(receiver.getNameofUser());
        }
        return containsName;
    }

    public void createMessage(User receiver, String message) {
        String senderAddress = String.format("%s_%s.csv", this.nameofUser, receiver.getNameofUser());
        String receiverAddress = String.format("%s_%s.csv", receiver.getNameofUser(), this.nameofUser);
        try {
            // Assemble message
            ArrayList<String> previousMessage = new ArrayList<>();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String tempMessage = String.format("%s,%s,%s,%s", receiver.getNameofUser(),
                    this.nameofUser, dtf.format(now), message);
            // sender file
            File senderFile = new File(senderAddress);
            if (senderFile.exists()) {
                Scanner scan = new Scanner(senderFile);
                while (scan.hasNextLine()) {
                    previousMessage.add(scan.nextLine());
                }
                previousMessage.add(tempMessage);
                PrintWriter fileWriter = new PrintWriter(senderFile);
                previousMessage.forEach((n) -> {
                    fileWriter.print(n + "\n");
                });
                fileWriter.close();
            } else {
                PrintWriter fileWriter = new PrintWriter(String.format("%s_%s.csv",
                        this.nameofUser, receiver.getNameofUser()));
                fileWriter.println(tempMessage);
                fileWriter.close();
            }
            // receiver file
            File receiverFile = new File(receiverAddress);
            previousMessage.removeAll(previousMessage);
            if (receiverFile.exists()) {
                Scanner scan = new Scanner(receiverFile);
                while (scan.hasNextLine()) {
                    previousMessage.add(scan.nextLine());
                }
                previousMessage.add(tempMessage);
                PrintWriter fileWriter = new PrintWriter(receiverFile);
                previousMessage.forEach((n) -> {
                    fileWriter.print(n + "\n");
                });
                fileWriter.close();
            } else {
                PrintWriter fileWriter = new PrintWriter(String.format("%s_%s.csv",
                        this.nameofUser, receiver.getNameofUser()));
                fileWriter.println(tempMessage);
                fileWriter.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void editMessage(User receiver, String oldMessage, String time
            , String newMessage) throws ConversationNotExistException {
        String senderAddress = String.format("%s_%s.csv", this.nameofUser, receiver.getNameofUser());
        String receiverAddress = String.format("%s_%s.csv", receiver.getNameofUser(), this.nameofUser);
        try {
            // sender file
            ArrayList<String> data = new ArrayList<>();
            FileReader fr = new FileReader(senderAddress);
            BufferedReader bfr = new BufferedReader(fr);
            int counter = 0;
            String s = "";
            String temp = "";
            String[] messageDecomp;

            while (bfr.ready()) {
                temp = bfr.readLine();
                messageDecomp = temp.split(",", 4);
                if (messageDecomp[3].contains(oldMessage) && time.equals(messageDecomp[2])) {
                    messageDecomp[3] = messageDecomp[3].replace(oldMessage, newMessage);
                }

                s = (String.join(",", messageDecomp));
                data.add(s);
                counter++;
            }
            bfr.close();
            PrintWriter pw = new PrintWriter(senderAddress);
            data.forEach((n) -> { pw.print(n + "\n"); });
            pw.close();

            // receiver file
            data.removeAll(data);
            FileReader fr2 = new FileReader(receiverAddress);
            BufferedReader bfr2 = new BufferedReader(fr2);
            counter = 0;

            while (bfr2.ready()) {
                temp = bfr2.readLine();
                messageDecomp = temp.split(",", 4);
                if (messageDecomp[3].contains(oldMessage) && time.equals(messageDecomp[2])) {
                    messageDecomp[3] = messageDecomp[3].replace(oldMessage, newMessage);
                }

                s = (String.join(",", messageDecomp));
                data.add(s);
                counter++;
            }
            bfr2.close();

            PrintWriter pw2 = new PrintWriter(receiverAddress);
            data.forEach((n) -> { pw2.print(n + "\n"); });

            pw2.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMessage(User receiver, String time, String message) throws ConversationNotExistException {
        String senderAddress = String.format("%s_%s.csv", this.nameofUser, receiver.getNameofUser());
        File senderFile = new File(senderAddress);
        ArrayList<String[]> messages = new ArrayList<>();
        if (senderFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(senderAddress))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] messageData = line.split(",", 4);
                    messages.add(messageData);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            ArrayList<String[]> updatedMessages = new ArrayList<>();

            for (String[] n : messages) {
                System.out.print(n[2].equals(time) + " ");
                System.out.println(n[3].equals(message));
                if (!n[3].equals(message) || !n[2].equals(time)) {
                    updatedMessages.add(n);
                }
            }

            try (PrintWriter writer = new PrintWriter(new File(senderAddress))) {
                for (String[] n : updatedMessages) {
                    writer.print(String.join(",", n) + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } else {
            throw new ConversationNotExistException("Message do not exist!");
        }
    }

    public void run() {

    }
}
