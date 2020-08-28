package client.core;

import com.google.gson.Gson;
import commons.Command;
import commons.model.Message;
import commons.utils.ChatUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//Proxy receiving/sending messages from/to users
public class BroadcastServerProxy {
    public final int MAX_DATA_SIZE = 2048;

    private InetAddress serverAddress;
    private int serverPort;
    private DatagramSocket clientSocket;
    private String userName;
    private Gson gson = new Gson();
    private ChatListener listener ;
    private ReceiverThread receiver;

    public BroadcastServerProxy(
            InetAddress serverAddress,
            int serverPort,
            DatagramSocket sock,
            String name) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.clientSocket = sock;
        this.userName = name;
        receiver = new ReceiverThread();
        receiver.start();
    }

    public void setChatListener(ChatListener listener) {
        this.listener = listener;
    }
    //Join the group chat
    public void join() {
       Message message = new Message();
        message.setCommand(Command.JOIN);
        message.setFrom(this.userName);
        String jsonMessage = gson.toJson(message);
        byte data[] = jsonMessage.getBytes();
        try {
            DatagramPacket packet =
                    new DatagramPacket(data, data.length, serverAddress, serverPort);
            this.clientSocket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Leave the group chat
    public void leave() {
        Message message = new Message();
        message.setCommand(Command.LEAVE);
        message.setFrom(this.userName);
        String jsonMessage = gson.toJson(message);
        byte data[] = jsonMessage.getBytes();
        try {
            DatagramPacket packet =
                    new DatagramPacket(data, data.length, serverAddress, serverPort);
            this.clientSocket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        receiver.interrupt();
    }
    //When sending a message
    public void send(String strMessage) {
        Message message = new Message();
        message.setCommand(Command.MESSAGE);
        message.setFrom(this.userName);
        message.setMessage(strMessage);

        String jsonMessage = gson.toJson(message);

        byte data[] = jsonMessage.getBytes();
        try {
            DatagramPacket packet =
                    new DatagramPacket(data, data.length, serverAddress, serverPort);
            this.clientSocket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //Process the message code and call the following message according to the Command number
    private void processMessage(Message message) {
        if (listener == null) {
            return;
        }
        if (Command.JOIN == message.getCommand()) {//Join the group chat
            listener.onJoin(message.getFrom());
        } else if (Command.LEAVE == message.getCommand()) {//leave the group chat
            listener.onLeave(message.getFrom());
        } else {
            listener.onMessage(message.getFrom(), message.getMessage());//send message
        }
    }

    class ReceiverThread extends Thread {
        public void run() {
            byte [] buffer = new byte[MAX_DATA_SIZE];
            while (!interrupted()) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, MAX_DATA_SIZE);
                    clientSocket.receive(packet);
                    String msgData = new String(packet.getData());
                    System.out.println("Obtained message \n " + msgData);
                    final Message message = gson.fromJson(msgData.trim(), Message.class);
                    processMessage(message);
                    ChatUtils.clearBuffer(buffer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
