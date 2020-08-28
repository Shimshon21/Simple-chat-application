package server.core;

import com.google.gson.Gson;
import commons.Command;
import commons.model.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class MessageBroadcastService {
    DatagramSocket serverDgSocket;
    Set<ClientEndpoint> clients = new HashSet<ClientEndpoint>();
    Gson gson = new Gson();

    public MessageBroadcastService(DatagramSocket dgSock) {
        this.serverDgSocket = dgSock;
    }

    public void handleMessage(Message message, InetAddress address, int port) {
        if (Command.JOIN == message.getCommand()) {
            ClientEndpoint client = new ClientEndpoint(address, port);
            if (clients.add(client)) {
                System.out.println("Added; Clients : " + clients);
            }
        } else if (Command.LEAVE == message.getCommand()) {
            ClientEndpoint client = new ClientEndpoint(address, port);
            if (clients.remove(new ClientEndpoint(address, port))) {
                System.out.println("Removed; Clients : " + clients);
            }
        }

        broadcastMessage(message);
    }

    private void broadcastMessage(Message message) {

        String jsonText = gson.toJson(message);
        byte[] data = jsonText.getBytes();

        for (ClientEndpoint client : clients) {
            DatagramPacket packet =
                    new DatagramPacket(data, data.length,
                            client.getAddress(), client.getPort());
            try {
                serverDgSocket.send(packet);
                System.out.println("Send message to client " + client);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ClientEndpoint {
        InetAddress address;
        int port;

        public ClientEndpoint(InetAddress address, int port) {
            super();
            this.address = address;
            this.port = port;
        }

        public InetAddress getAddress() {
            return address;
        }
        public void setAddress(InetAddress address) {
            this.address = address;
        }
        public int getPort() {
            return port;
        }
        public void setPort(int port) {
            this.port = port;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((address == null) ? 0 : address.hashCode());
            result = prime * result + port;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ClientEndpoint other = (ClientEndpoint) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (address == null) {
                if (other.address != null)
                    return false;
            } else if (!address.equals(other.address))
                return false;
            if (port != other.port)
                return false;
            return true;
        }

        private MessageBroadcastService getOuterType() {
            return MessageBroadcastService.this;
        }

        @Override
        public String toString() {
            return "ClientEndpoint [address=" + address + ", port=" + port + "]";
        }

    }
}
