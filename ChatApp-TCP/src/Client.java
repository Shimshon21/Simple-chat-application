import View.ChatWindow;

import java.net.Socket;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost",9000);
        ChatWindow chatWindow = new ChatWindow("Client:Window",socket);
        chatWindow.setVisible(true);

    }
}
