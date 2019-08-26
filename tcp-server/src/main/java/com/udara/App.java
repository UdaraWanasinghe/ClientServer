package com.udara;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class App {
    final static int SERVER_PORT = 4099;
    static boolean CLOSE_FLAG = false;

    public static void main(String[] args) {

        try {
            final ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            System.out.println("Server is listening at port: " + SERVER_PORT);

            final ExecutorService executorService = Executors.newCachedThreadPool();

            CloseListener closeListener = new CloseListener() {

                @Override
                public void onClose() {
                    System.out.println("Stopping server");
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

                System.out.println("New connection: " + socket.getInetAddress().toString());

                executorService.execute(new WorkerTask(socket, closeListener));

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Server stopped");
    }

    private static class WorkerTask implements Runnable {
        private CloseListener closeListener;

        private StreamHandler mStreamHandler;

        private WorkerTask(Socket socket, CloseListener closeListener) throws IOException {
            this.closeListener = closeListener;

            mStreamHandler = new StreamHandler(socket.getInputStream(), socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                String[] found = mStreamHandler.readUntilPattern("hello|exit");

                for (int i = 0; i < found.length - 1; i++) {
                    if (found[i].equals("hello")) {
                        mStreamHandler.writeLine("Hello There");

                    } else if (found[i].equals("exit")) {
                        closeListener.onClose();
                    }
                }

                System.out.println("Received: " + found[found.length - 1]);

                // Scanner in = new Scanner(socket.getInputStream());
                // String received = in.nextLine().toLowerCase();

                // System.out.println("Received: " + received);

                // if (received.contains("hello")) {
                // BufferedOutputStream out = new
                // BufferedOutputStream(socket.getOutputStream());
                // String message = "Hello There";
                // out.write(message.getBytes());
                // out.flush();
                // }

                // in.close();
                // socket.close();

                // if (received.contains("exit")) {
                // closeListener.onClose();
                // }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
    }

    private static interface CloseListener {
        void onClose();
    }
}
