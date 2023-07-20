import javax.annotation.processing.SupportedSourceVersion;
import java.io.*;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Scanner;

public class User extends Thread {
    private boolean userType; // true = seller, false = customer
    private String password;
    private String email;
    private String nameOfUser;
    private ArrayList<String> storeName;
    private ArrayList<User> conversationUser;
    private ArrayList<User> blockList;
    private ArrayList<User> invisibleList;

    public User(boolean userType, String email, String password, String nameOfUser) {
        this.userType = userType;
        this.password = password;
        this.nameOfUser = nameOfUser;
        this.email = email;
    }

    public String getNameOfUser() {
        return nameOfUser;
    }

    public boolean isUserType() {
        return userType;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setNameOfUser(String nameOfUser) {this.nameOfUser = nameOfUser;}

    public void setUserType(boolean userType) {
        this.userType = userType;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<User> getConversationUser() { return getConversationUser(); }

    public ArrayList<String> setStoreName() {return storeName;}

    public ArrayList<String> getStoreName() {return storeName;}

    public ArrayList<User> getBlockList() {return blockList;}
    public void setBlockList(ArrayList<User> blockList) {this.blockList = blockList;}

    public ArrayList<User> getInvisibleList() {return invisibleList;}

    public void setInvisibleList(ArrayList<User> invisibleList) {this.invisibleList = invisibleList;}

    public boolean isBlocked(User receiver) {
        return blockList.stream().anyMatch(blockedUser -> blockedUser.equals(receiver)); // checking if receiver is in block list
    }
    public boolean isInvisible(User receiver) {
        return invisibleList.stream().anyMatch(invUser -> invUser.equals(receiver)); // checking if receiver is in invisible list
    }

    public boolean addConversation(User receiver) {
        boolean containsName = false;

        containsName = conversationUser.stream().anyMatch(validUser -> validUser.equals(receiver)); // check if receiver is in conversation list

        if (!containsName) {
            File file = new File(String.format("%s_%s.csv", this.nameOfUser, receiver.getNameOfUser()));
            conversationUser.add(receiver);
        }
        return containsName;
    }

    public void createMessage(User receiver, String message) {
        String senderAddress = String.format("%s_%s.csv", this.nameOfUser, receiver.getNameOfUser());
        String receiverAddress = String.format("%s_%s.csv", receiver.getNameOfUser(), this.nameOfUser);
        try {
            // Assemble message
            ArrayList<String> previousMessage = new ArrayList<>();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String tempMessage = String.format("%s,%s,%s,%s", receiver.getNameOfUser(),
                    this.nameOfUser, dtf.format(now), message);
            // sender file
            File senderFile = new File(senderAddress);
            if (senderFile.exists()) {
                Scanner scan = new Scanner(senderFile);
                while (scan.hasNextLine()) {
                    previousMessage.add(scan.nextLine());
                }
                previousMessage.add(tempMessage);
                PrintWriter fileWriter = new PrintWriter(senderFile);
                previousMessage.forEach((n) -> { // print all previous message in to the file
                    fileWriter.print(n + "\n");
                });
                fileWriter.close();
            } else {
                PrintWriter fileWriter = new PrintWriter(String.format("%s_%s.csv",
                        this.nameOfUser, receiver.getNameOfUser()));
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
                previousMessage.forEach((n) -> { // print all previous message in to the file
                    fileWriter.print(n + "\n");
                });
                fileWriter.close();
            } else {
                PrintWriter fileWriter = new PrintWriter(String.format("%s_%s.csv",
                        this.nameOfUser, receiver.getNameOfUser()));
                fileWriter.println(tempMessage);
                fileWriter.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void editMessage(User receiver, String oldMessage, String time
            , String newMessage) throws NoMessageFoundException {
        String senderAddress = String.format("%s_%s.csv", this.nameOfUser, receiver.getNameOfUser());
        String receiverAddress = String.format("%s_%s.csv", receiver.getNameOfUser(), this.nameOfUser);
        boolean noMessageFound = true;
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
                    noMessageFound = false;
                }

                s = (String.join(",", messageDecomp));
                data.add(s);
                counter++;
            }
            bfr.close();
            PrintWriter pw = new PrintWriter(senderAddress);
            data.forEach((n) -> { pw.print(n + "\n"); }); // print all previous message in to the file
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
            data.forEach((n) -> { pw2.print(n + "\n"); }); // print all previous message in to the file

            pw2.close();

            if (noMessageFound) {
                throw new NoMessageFoundException("Message not found");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMessage(User receiver, String time, String message) throws NoMessageFoundException {
        String senderAddress = String.format("%s_%s.csv", this.nameOfUser, receiver.getNameOfUser());
        File senderFile = new File(senderAddress);
        ArrayList<String[]> messages = new ArrayList<>();
        boolean noMessageFound = true;
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
            if (n[3].equals(message) && n[2].equals(time)) {
                noMessageFound = false;
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

        if (noMessageFound) {
            throw new NoMessageFoundException("Message not found");
        }
    }

    public ArrayList<String[]> display50Message(User receiver){
        String senderAddress = String.format("%s_%s.csv", this.nameOfUser, receiver.getNameOfUser());
        File senderFile = new File(senderAddress);
        ArrayList<String[]> messages = new ArrayList<>();
        boolean noMessageFound = true;
        try (BufferedReader reader = new BufferedReader(new FileReader(senderAddress))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.substring(0, line.indexOf(",") + 1);
                String[] messageData = line.split(",", 3);
                messages.add(messageData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (messages.size() > 50) {
            int elementsToRemove = messages.size() - 50;
            messages.subList(0, elementsToRemove).clear();
        }

        return messages;
    }

    public void sendTxtFile(User receiver, String fileAddress) throws FileNotFoundException {
        File senderFile = new File(fileAddress);
        ArrayList<String> fileMessages = new ArrayList<>();
        boolean noMessageFound = true;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileAddress))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileMessages.add(line);
            }
            fileMessages.forEach( n -> createMessage(receiver,n)); // for each element in fileMessage, use createMessage method
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Invalid address");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


