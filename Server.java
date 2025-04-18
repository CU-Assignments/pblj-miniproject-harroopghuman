import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Server extends JFrame implements ActionListener {

    JTextArea chatArea;
    JTextField inputField;
    JButton sendButton;

    DataInputStream din;
    DataOutputStream dout;
    Socket socket;

    public Server() {
        setTitle("Server Chat");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        sendButton.addActionListener(this);
        inputField.addActionListener(this); // Send on Enter key

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);

        startServer();
    }

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(6001);
            chatArea.append("Server started. Waiting for client...\n");

            socket = serverSocket.accept();
            chatArea.append("Client connected!\n");

            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());

            // Thread to read incoming messages from client
            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        String clientMsg = din.readUTF();
                        chatArea.append("Client: " + clientMsg + "\n");
                    }
                } catch (IOException e) {
                    chatArea.append("Connection closed.\n");
                }
            });
            readThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        String msg = inputField.getText().trim();
        if (!msg.isEmpty()) {
            try {
                dout.writeUTF(msg);
                chatArea.append("You: " + msg + "\n");
                inputField.setText("");
            } catch (IOException ex) {
                chatArea.append("Error sending message.\n");
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }
}
