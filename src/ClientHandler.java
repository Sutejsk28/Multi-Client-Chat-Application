import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private Socket socket;
    public static ArrayList<ClientHandler> clientHandlersList = new ArrayList<>();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlersList.add(this);
            System.out.println("New Client has connect");
            broadcastMessage("SERVER: " + this.clientUsername + " has entered the chat");
        }catch (IOException io){
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void broadcastMessage(String message){
        for(ClientHandler clientHandler: clientHandlersList){
            try {
                if (!clientHandler.clientUsername.equals(this.clientUsername)){
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException io){
                closeEverything(socket,bufferedWriter,bufferedReader);
            }
        }
    }

    public void removeClientHandler(){
        clientHandlersList.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat");
    }

    public void closeEverything( Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader ){
        removeClientHandler();
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

    @Override
    public void run() {
        String messageFromClient;

        while(socket.isConnected()){
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }catch (IOException io){
                closeEverything(socket,bufferedWriter,bufferedReader);
                break;
            }
        }
    }
}
