package client.core;

public interface ChatListener {
        void onJoin(String userName);
        void onLeave(String userName);
        void onMessage(String userName, String message);
}
