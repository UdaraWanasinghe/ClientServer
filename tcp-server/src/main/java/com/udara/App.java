package com.udara;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 */
public class App {
    final static int SERVER_PORT = 4099;
    static boolean CLOSE_FLAG = false;
    final static String LOGGER_NAME = App.class.getName() + ".File";

    public static void main(String[] args) {

        final Logger logger = LogManager.getLogger(LOGGER_NAME);

        try {
            final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            logger.info("Server is listening at port: " + SERVER_PORT);

            final ExecutorService executorService = Executors.newCachedThreadPool();

            CloseListener closeListener = new CloseListener() {

                @Override
                public void onClose() {
                    logger.info("Stopping server");

                    try {
                        executorService.shutdown();
                        serverSocket.close();
                        CLOSE_FLAG = true;

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            };

            while (!CLOSE_FLAG) {
                Socket socket = serverSocket.accept();

                logger.info("New connection: " + socket.getInetAddress().toString());

                executorService.execute(new WorkerTask(socket, closeListener));

            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        logger.info("Server stopped");
    }

    private static class WorkerTask implements Runnable {
        private CloseListener closeListener;

//        private StreamHandler mStreamHandler;

        private Socket socket;

        private Logger logger = LogManager.getLogger(LOGGER_NAME);

        private WorkerTask(Socket socket, CloseListener closeListener) throws IOException {
            this.closeListener = closeListener;
            this.socket = socket;
//            mStreamHandler = new StreamHandler(socket.getInputStream(), socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
//                String[] found = mStreamHandler.readUntilPattern("hello|exit");

//                for (int i = 0; i < found.length - 1; i++) {
//                    if (found[i].equals("hello")) {
//                        mStreamHandler.writeLine("Hello There");
//
//                    } else if (found[i].equals("exit")) {
//                        closeListener.onClose();
//                    }
//                }

//                System.out.println("Received: " + found[found.length - 1]);

                Scanner in = new Scanner(socket.getInputStream());
                String received = in.nextLine().toLowerCase();

                logger.info("Received: " + received);

                if (received.contains("hello")) {
                    BufferedOutputStream out = new
                            BufferedOutputStream(socket.getOutputStream());
                    String message = "Hello There\n";
                    out.write(message.getBytes());
                    out.flush();
                }

                in.close();
                socket.close();

                if (received.contains("exit")) {
                    closeListener.onClose();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    private static interface CloseListener {
        void onClose();
    }
}
