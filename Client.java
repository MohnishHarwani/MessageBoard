public class Client {
    public static void main(String[] args) {
        User a = new User(true, "1241", "will", "onlymdestiny");
        User b = new User(false, "1241", "billy", "aonlymdestiny");
        a.createMessage(b, "love");
    }
}
