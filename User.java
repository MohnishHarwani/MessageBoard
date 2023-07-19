import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class User extends Thread {
    private boolean userType; // true = seller, false = customer
    private String password;
    private String username;
    private String name;
    private ArrayList<String> conversationName;

    public User(boolean userType, String password, String name, String username) {
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

    public boolean addConversation(String name) {
        boolean containsName = false;

        for (String a: conversationName) {
            if (a.equals(name))
                containsName = true;
        }

        if (!containsName) {
            File file = new File(String.format("%s_%s.csv", this.name, name));
            conversationName.add(name);
        }

        return containsName;
    }

    public ArrayList<String> returnConversationName(){
        return conversationName;
    }

    public void createMessage(String name, String message) {
        File csvFile = new File(String.format("%s_%s.csv", this.name, name));
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            FileWriter fileWriter = new FileWriter(csvFile);

            String tempMessage = String.format("%s,%s,%s,%s", name, this.name, dtf.format(now), message);
            fileWriter.write(tempMessage);

            fileWriter.close();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void editMessage() {
        // duplicate message issue
    }

    public void deleteMessage(String name) {
        // duplicate message issue
    }
}
