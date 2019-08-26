package com.udara;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.LogManager;

import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class StreamHandler {
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    public StreamHandler(InputStream in, OutputStream out) {
        this.mInputStream = in;
        this.mOutputStream = out;
    }

    /**
     * Mirror lines from input stream to output stream This method blocks the
     * thread, should run on new thread to manage concurrency
     * 
     * @param in input stream to mirror
     * @throws IOException input stream fails or output stream fails
     */
    public void mirrorLineToOutput(InputStream in) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();


        while (true) {
            if (in.available() > 0) {
                byte[] buffer = new byte[1024];
                int read = in.read(buffer);

                if(read == -1){
                    break;
                }

                String str = new String(buffer, 0, read);

                for (int i = 0; i < str.length(); i++) {
                    if (str.charAt(i) == '\n' || str.charAt(i) == '\r') {
                        String string = stringBuilder.toString() + str.substring(0, i) + "\n";
                        
                        mOutputStream.write(string.getBytes());

                        System.out.print("New line: " + string);

                        stringBuilder = new StringBuilder();
                        stringBuilder.append(str, i, str.length());
                    }
                }
            }
        }
    }

    public void listenForLine(InputStreamListener listener) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] buffer = new byte[1024];

        while (true) {
            if (mInputStream.available() > 0) {

                int read = mInputStream.read(buffer);

                String str = new String(buffer, 0, read);

                int lineIndex = -1;

                for (int i = 0; i < str.length(); i++) {
                    if (str.charAt(i) == '\n' || str.charAt(i) == '\r') {
                        lineIndex = i;
                    }
                }

                if (lineIndex != -1) {
                    int len = stringBuilder.length();
                    stringBuilder.append(str, 0, lineIndex);

                    listener.onLineReceive(stringBuilder.toString());

                    stringBuilder.delete(0, len + lineIndex + System.lineSeparator().length());
                }

            }
        }
    }

    public static interface InputStreamListener {
        void onLineReceive(String line);
    }
}