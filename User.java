import java.io.*;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Scanner;

public class User extends Thread {
    private boolean userType; // true = seller, false = customer
    private String password;
    private String username;
    private String name;
    private ArrayList<String> conversationName;
    private ArrayList<String> blockList;

    public User(boolean userType, String password, String name, String username) {
        this.userType = userType;
        this.password = password;
        this.name = name;
        this.username = username;
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
            if (user.getName().equals(blockList.get(counter)))
                return true;
        }
        return false;
    }

    public boolean checkUniqueUser(User user) {
        return !(user.getUsername().equals(this.username) &&
                user.isUserType() == this.userType && user.getName().equals(this.name));
    }

    public boolean addConversation(User receiver) {
        boolean containsName = false;

        for (String a : conversationName) {
            if (a.equals(receiver.getName()))
                containsName = true;
        }

        if (!containsName) {
            File file = new File(String.format("%s_%s.csv", this.name, receiver.getName()));
            conversationName.add(receiver.getName());
        }

        return containsName;
    }

    public String fileName(String name) {
        String tempAddress = String.format("%s_%s.csv", this.name, name);
        File file = new File(tempAddress);
        if (file.exists()) {
            return tempAddress;
        }
        return "File not find";
    }

    public void createMessage(User receiver, String message) {
        File csvFile = new File(fileName(receiver.getName()));
        try {
            ArrayList<String> previousMessage = new ArrayList<>();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String tempMessage = String.format("%s,%s,%s,%s", receiver.getName(),
                    this.name, dtf.format(now), message);

            if (!fileName(receiver.getName()).equals("File not find")) {
                Scanner scan = new Scanner(csvFile);
                while (scan.hasNextLine()) {
                    previousMessage.add(scan.nextLine());
                }
                previousMessage.add(tempMessage);
                PrintWriter fileWriter = new PrintWriter(csvFile);
                previousMessage.forEach((n) -> {
                    fileWriter.print(n + "\n");
                });
                fileWriter.close();
            } else {
                PrintWriter fileWriter = new PrintWriter(String.format("%s_%s.csv", this.name, name));
                fileWriter.println(tempMessage);
                fileWriter.close();
            }
            duplicateConversation(receiver);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void EditMessage(User receiver, String oldMessage, String newMessage) throws ConversationNotExistException {
        if (!fileName(receiver.getName()).equals("File not find")) {
            ArrayList<String> data = new ArrayList<>();
            try {
                FileReader fr = new FileReader(fileName(receiver.getName()));
                BufferedReader bfr = new BufferedReader(fr);
                int counter = 0;
                String s = "";
                String temp = "";
                String[] messageDecomp;

                while (bfr.ready()) {
                    temp = bfr.readLine();
                    messageDecomp = temp.split(",", 4);
                    if (messageDecomp[3].contains(oldMessage)) {
                        messageDecomp[3] = messageDecomp[3].replace(oldMessage, newMessage);
                    }

                    s = (String.join(",", messageDecomp));
                    /*
                    while (counter < 4) {
                        s += (counter == 3) ? messageDecomp[counter] : messageDecomp[counter] + ",";
                        counter++;
                    }
                     */
                    data.add(s);
                }
                bfr.close();

                PrintWriter pw = new PrintWriter(fileName(receiver.getName()));
                data.forEach((n) -> { pw.print(n + "\n"); });

                pw.close();
                duplicateConversation(receiver);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new ConversationNotExistException("Message do not exist!");
        }
    }

    public void deleteMessage(User receiver, String message) throws ConversationNotExistException {
        ArrayList<String[]> messages = new ArrayList<>();
        if (!fileName(receiver.getName()).equals("File not find")) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName(receiver.getName())))) {
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
                if (!n[3].equals(message)) {
                    updatedMessages.add(n);
                }
            }

            try (PrintWriter writer = new PrintWriter(new File(fileName(receiver.getName())))) {
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

    public void duplicateConversation(User receiver) {
        try {
            ArrayList<String> conversationMessage = new ArrayList<>();
            String line = "";
            FileReader senderFile = new FileReader(fileName(receiver.getName()));
            BufferedReader reader = new BufferedReader(senderFile);
            while ((line = reader.readLine()) != null) {
                conversationMessage.add(line);
            }
            File file = new File(String.format("%s_%s.csv", receiver.getName(), this.name));
            PrintWriter pw = new PrintWriter(file);
            conversationMessage.forEach((n) -> { pw.print(n + "\n"); });
            pw.close();

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    public void run() {

    }
}
