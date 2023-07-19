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

    public void ArrayList<String> EditMessage(String oldMessage, String newMessage, String filename) throws FileNotFoundException {
                ArrayList<String> data = new ArrayList<>();
                try {
                        FileReader fr = new FileReader(filename);
                        BufferedReader bfr = new BufferedReader(fr);
                        int line = 0;
                        String s = "";
                        String temp = "";

                        while (bfr.ready()) {
                                temp = bfr.readLine();
                                if (temp.contains(oldMessage)) {
                                        temp = temp.replace(oldMessage, newMessage);
                                }
                                data.add(temp);
                        }
                        return data;
                } catch(FileNotFoundException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        throw new RuntimeException(e);
                }
                return data;
    }

    public void deleteMessage(String name) {
        // duplicate message issue
        // paste code hereimport java.io.*;
import java.util.*;

public class Methods {

    public static void deleteMessage(String name) {
        String csvFilePath = "messages.csv"; // Replace with the actual path of your CSV file

        List<String[]> messages = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] messageData = line.split(",");
                messages.add(messageData);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return; // Exit the method if an exception occurs during file reading
        }

        List<String[]> updatedMessages = new ArrayList<>();

        for (String[] message : messages) {
            String name1 = message[0];
            String content = message[1];

            if (!name.equals(name1)) {
                updatedMessages.add(message);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
            for (String[] message : updatedMessages) {
                writer.write(String.join(",", message));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return; // Exit the method if an exception occurs during file writing
        }
    }

    // Sample usage of the deleteMessage method
  
    }}
    }
}
