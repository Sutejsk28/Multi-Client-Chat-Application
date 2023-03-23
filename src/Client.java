import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client{

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    JTextField tfMessage;
    JPanel a1;

    public Client(Socket socket, String username){
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException io){
            closeEverything(socket,bufferedWriter,bufferedReader);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                if(messageToSend.equals("bye")) closeEverything(socket,bufferedWriter,bufferedReader);
            }
        } catch (IOException io){
            closeEverything(socket,bufferedWriter,bufferedReader);
        }
    }

    public void listenMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromChat;
                while(socket.isConnected()){
                    try {
                        messageFromChat = bufferedReader.readLine();
                        System.out.println(messageFromChat);
                    } catch (IOException io){
                        closeEverything(socket,bufferedWriter,bufferedReader);
                    }
                }
            }
        }).start();
    }

    public void closeEverything( Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader ){
        try {
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(bufferedWriter != null) {
                bufferedWriter.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException io){
            io.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost",1234);
        Client client = new Client(socket,username);
        client.listenMessage();
        client.sendMessage();
    }

}
