package client.view;

import client.core.BroadcastServerProxy;
import client.core.ChatListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    JTextArea textArea = new JTextArea();
    JTextField textField = new JTextField();
    JButton btnSend = new JButton("Send");

    BroadcastServerProxy broadcastServerProxy;

    public ChatFrame(String title, BroadcastServerProxy broadcastServerProxy) {
        setTitle(title);
        this.broadcastServerProxy = broadcastServerProxy;
        this.broadcastServerProxy.setChatListener(new ChatMessageListener());
        prepareFrame();
    }

    private void prepareFrame() {
        Container content = getContentPane();

        Box south = Box.createHorizontalBox();
        south.add(textField);
        south.add(btnSend);

        JScrollPane scrollPane = new JScrollPane(textArea);
        content.add(scrollPane, "Center");
        content.add(south, "South");

        setBounds(100, 100, 300, 300);
        setResizable(false);
        addWindowListener(new WindowHandler());
        btnSend.addActionListener(new SendHandler());
    }

    class WindowHandler extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            broadcastServerProxy.leave();
            dispose();
        }
    }

    class SendHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            broadcastServerProxy.send(textField.getText());
        }
    }

    class ChatMessageListener implements ChatListener {

        public void onJoin(String userName) {
            String message = userName + " joined";
            String appendedText = textArea.getText() + "\n" + message;
            textArea.setText(appendedText);
        }

        public void onLeave(String userName) {
            String message = userName + " left";
            String appendedText = textArea.getText() + "\n" + message;
            textArea.setText(appendedText);
        }

        public void onMessage(String userName, String message) {
            String strMessage = userName + ":" + message;
            String appendedText = textArea.getText() + "\n" + strMessage;
            textArea.setText(appendedText);
        }

    }
}
