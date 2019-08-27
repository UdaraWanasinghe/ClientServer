package com.udara;

import com.udara.StreamHandler.InputStreamListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    final static String HOST = "127.0.0.1";
    final static int PORT = 4099;

    private static Logger logger = LogManager.getLogger(App.class.getName() + ".Client");

    public static void main(String[] args) {
        try {

            Socket socket = new Socket(HOST, PORT);

            logger.info("Connected to: " + socket.getInetAddress().toString());

            Scanner scanner = new Scanner(System.in);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

            String line = scanner.nextLine() + "\n";
            out.write(line.getBytes());
            out.flush();

            logger.info("Sent: " + line);

            line = in.readLine();
            logger.info("Received: " + line);

            scanner.close();
            socket.close();

        } catch (Exception e) {
            logger.error("Unexpected error", e);
        }

    }

    static void myOwn(Socket socket) throws IOException {
        final StreamHandler streamHandler = new StreamHandler(socket.getInputStream(), socket.getOutputStream());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("Input: ");

                    streamHandler.mirrorLineToOutput(System.in);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();

        streamHandler.listenForLine(new InputStreamListener() {
            @Override
            public void onLineReceive(String line) {
                System.out.println(line);
            }
        });
    }
}
