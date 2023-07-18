import java.io.File;
import java.util.ArrayList;

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

    public void createMessage() {

    }

    public void editMessage() {

    }

    public void deleteMessage(String name) {

    }
}