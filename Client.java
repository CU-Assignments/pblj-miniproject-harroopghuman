import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client extends JFrame implements ActionListener {
    JTextField textField;
    JTextArea chatArea;
    JButton sendBtn;

    static Socket socket;
    static DataInputStream din;
    static DataOutputStream dout;

    public Client() {
        setTitle("Client Chat");
        setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        textField = new JTextField();
        sendBtn = new JButton("Send");

        bottomPanel.add(textField, BorderLayout.CENTER);
        bottomPanel.add(sendBtn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        sendBtn.addActionListener(this);
        textField.addActionListener(this);

        setSize(400, 500);
        setLocation(200, 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        connectToServer();
    }

    public void actionPerformed(ActionEvent e) {
        try {
            String msg = textField.getText().trim();
            if (!msg.isEmpty()) {
                chatArea.append("You: " + msg + "\n");
                dout.writeUTF(msg);
                textField.setText("");
            }
        } catch (Exception ex) {
            chatArea.append("Error sending message.\n");
        }
    }

    public void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 6001);
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());

            // Start a thread to read messages from the server
            Thread readThread = new Thread(() -> {
                try {
                    String msgIn = "";
                    while ((msgIn = din.readUTF()) != null) {
                        chatArea.append("Server: " + msgIn + "\n");
                    }
                } catch (IOException e) {
                    chatArea.append("Disconnected from server.\n");
                }
            });
            readThread.start();

        } catch (Exception e) {
            chatArea.append("Unable to connect to server.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}

