import client.core.BroadcastServerProxy;
import client.view.ChatFrame;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    public static void main(String[] args) throws IOException {
        DatagramSocket clientSocket = new DatagramSocket();

        String userName = JOptionPane.showInputDialog("Enter your name");

        BroadcastServerProxy broadcastServerProxy =
                new BroadcastServerProxy(
                        InetAddress.getLocalHost(),
                        9000,
                        clientSocket,
                        userName);

        broadcastServerProxy.join();

        ChatFrame frame = new ChatFrame(userName, broadcastServerProxy);
        frame.setVisible(true);
    }
}
