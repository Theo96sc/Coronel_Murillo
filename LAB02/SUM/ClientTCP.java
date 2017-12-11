
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Trivial TCP client.
 */
public class ClientTCP {

    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Suma");
    private JTextField dataFieldA = new JTextField(20);
    private JTextField dataFieldB = new JTextField(20);
    private JButton continuar = new JButton("Enviar");
    private JTextArea messageArea = new JTextArea(8, 60);

    /**
     * Constructs the client by laying out the GUI and registering a listener
     * with the textfield so that pressing Enter in the listener sends the
     * textfield contents to the server.
     */
    public ClientTCP() {
        // Layout GUI
        messageArea.setEditable(false);
        frame.getContentPane().add(dataFieldA, "East");
        frame.getContentPane().add(dataFieldB, "West");
        frame.getContentPane().add(continuar, "Center");
        frame.getContentPane().add(new JScrollPane(messageArea), "South");

        // Add Listeners
        continuar.addActionListener(new ActionListener() {

            /**
             * Responds to pressing the enter key in the textfield by sending
             * the contents of the text field to the server and displaying the
             * response from the server in the text area. If the response is "."
             * we exit the whole application, which closes all sockets, streams
             * and windows.
             */
            public void actionPerformed(ActionEvent e) {
                out.println(dataFieldA.getText()+" "+dataFieldB.getText());
                String response;
                try {
                    response = in.readLine();
                    if (response == null || response.equals("")) {
                        System.exit(0);
                    }
                } catch (IOException ex) {
                    response = "Error: " + ex;
                }
                messageArea.append(response + "\n");
                dataFieldA.selectAll();;
            }
        });
    }

    /**
     * Implements the connection logic by prompting the end user for the
     * server's IP address, connecting, setting up streams, and consuming the
     * welcome messages from the server. The Capitalizer protocol says that the
     * server sends three lines of text to the client immediately after
     * establishing a connection.
     * @throws java.io.IOException
     */
    public void connectToServer() throws IOException {

        // Get the server address from a dialog box.
        String serverAddress = JOptionPane.showInputDialog(
                frame,
                "Enter IP Address of the Server:",
                "Welcome to the Capitalization Program",
                JOptionPane.QUESTION_MESSAGE);

        // Make connection and initialize streams
        Socket socket = new Socket(serverAddress, 9898);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Consume the initial welcoming messages from the server
        for (int i = 0; i < 3; i++) {
            messageArea.append(in.readLine() + "\n");
        }
    }

    /**
     * Runs the client application.
     */
    public static void main(String[] args) throws Exception {

        ClientTCP client = new ClientTCP();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.pack();
        client.frame.setVisible(true);
        client.connectToServer();
    }

}
