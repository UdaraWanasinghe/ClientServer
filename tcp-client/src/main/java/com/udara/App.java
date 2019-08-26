package com.udara;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import com.udara.StreamHandler.InputStreamListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * Hello world!
 */
public class App {
    final static String HOST = "127.0.0.1";
    final static int PORT = 4099;

    private static Logger logger;

    public static void main(String[] args) {
        initLogger();

        try {
            Socket socket = new Socket(HOST, PORT);

            logger.info("hello");

            System.out.println("Connected to: " + socket.getInetAddress().toString());

            Scanner scanner = new Scanner(System.in);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

            String line = scanner.nextLine() + "\n";
            out.write(line.getBytes());
            out.flush();

            System.out.print("Sent: " + line);

            line = in.readLine();
            System.out.println("Received: " + line);

            scanner.close();
            socket.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    static void myOwn(Socket socket) throws IOException {
        final StreamHandler streamHandler = new StreamHandler(socket.getInputStream(), socket.getOutputStream());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.print("Input: ");
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

    static void logEvent(){
        logger.info("Event started!");
    }

    static void initLogger(){
        logger = LogManager.getLogger("com.udara.App.File");
    }
}
