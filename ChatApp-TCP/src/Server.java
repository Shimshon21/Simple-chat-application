import View.ChatWindow;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args)throws Exception {
        ServerSocket serverSocket = new ServerSocket(9000);
        while (true){
            System.out.println("Waiting for client");
            Socket socket = serverSocket.accept();
            ChatWindow chatWindow = new ChatWindow("Server:Window",socket);
            chatWindow.setVisible(true);
        }

    }
}
