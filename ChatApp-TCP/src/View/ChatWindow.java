package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

//Chat window between server and client
public class ChatWindow extends JFrame {
    private JTextArea textArea = new JTextArea();
    private JTextField textField = new JTextField();
    private JButton sendButton = new JButton("Send");

    private DataOutputStream dout;
    private DataInputStream din;

    private Socket socket;

    public ChatWindow(String text, Socket socket) {
        setTitle(text);
        this.socket = socket;
        prepareWindow();
        setupSocket();
    }

    //Setup the socket
    private void setupSocket(){
        try{
            din=new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        new ReceiverThread().start();
    }

    //Initialize chat window
    private void prepareWindow() {
        Container container = getContentPane();
        Box south = Box.createHorizontalBox();
        south.add(textField);
        south.add(sendButton);

        JScrollPane scrollPane = new JScrollPane(textArea);
        container.add(scrollPane, "Center");
        container.add(south, "South");

        setBounds(100, 100, 300, 300);
        setResizable(false);
        addWindowListener(new WindowHandler());
        sendButton.addActionListener(new SendHandler());
    }
    //When message received, set text.
    private void onMessageReceived(String message){
        String appendText = textArea.getText() + "\n"+"Other: "+ message;
        textArea.setText(appendText);
    }

    private class WindowHandler extends WindowAdapter {
        @Override
        //Close window
        public void windowClosing(WindowEvent e) {
            dispose();
        }
    }
    //Sending message handler
    private class SendHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String message = textField.getText();
            String appendedText = textArea.getText() + "\n" + "You: " + message;
            textArea.setText(appendedText);
            try{
                dout.writeUTF(message);
            }catch (Exception exc){
                System.out.println(exc.getMessage());
            }
        }
    }
    //Thread for receiving message
    class ReceiverThread extends Thread{
        @Override
        public void run() {
            while (!interrupted()){
                try{
                    String message = din.readUTF();
                    onMessageReceived(message);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
