package commons.utils;

public class ChatUtils {
    public static void clearBuffer(byte[] buffer) {
        for(int i=0; i < buffer.length; i++) {
            buffer[i] = 0;
        }
    }
}
