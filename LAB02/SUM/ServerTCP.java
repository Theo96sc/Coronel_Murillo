/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author JoseLuis
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Integer.parseInt;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * A TCP server that runs on port 9090. When a client connects, it sends the
 * client a message, then closes the connection with that client. Arguably just
 * about the simplest server you can write.
 */
public class ServerTCP {

    /**
     * Application method to run the server runs in an infinite loop listening
     * on port 9898. When a connection is requested, it spawns a new thread to
     * do the servicing and immediately returns to listening. The server keeps a
     * unique client number for each client that connects just to show
     * interesting logging messages. It is certainly not necessary to do this.
     */
    public static void main(String[] args) throws Exception {
        System.out.println("The sum server is running.");
        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(9898);
        try {
            while (true) {
                new Sum(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * A private thread to handle capitalization requests on a particular
     * socket. The client terminates the dialogue by sending a single line
     * containing only a period.
     */
    private static class Sum extends Thread {

        private Socket socket;
        private int clientNumber;

        public Sum(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }

        /**
         * Services this thread's client by first sending the client a welcome
         * message then repeatedly reading strings and sending back the
         * capitalized version of the string.
         */
        public void run() {
            try {
                // Decorate the streams so we can send characters
                // and not just bytes.  Ensure output is flushed
                // after every newline.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                // Send a welcome message to the client.
                out.println("Hello, you are client #" + clientNumber + ".");
                out.println("Enter a line with only a period to quit\n");
                // Get messages from the client, line by line; return them
                // capitalized
                while (true) {
                    String input = in.readLine();
                    if (input == null || input.equals(".")) {
                        break;
                    }
                    StringTokenizer t = new StringTokenizer(input, " ");
                    int a= parseInt(t.nextToken());
                    int b= parseInt(t.nextToken());
                    System.out.println("\tCLIENTE: " + clientNumber);
                    System.out.println("*Peticion SUMA  " + a + " + " + b + "\n");
                    int suma =  a + b;
                    out.println("El resultado es " + suma);
                }
            } catch (IOException e) {
                log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket, what's going on?");
                }
                log("Connection with client# " + clientNumber + " closed");
            }
        }

        /**
         * Logs a simple message. In this case we just write the message to the
         * server applications standard output.
         */
        private void log(String message) {
            System.out.println(message);
        }
    }
}