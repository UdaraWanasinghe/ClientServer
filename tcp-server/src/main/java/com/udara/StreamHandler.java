package com.udara;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamHandler {
    private InputStream mInputStream;
    private OutputStream mOutputStream;

    public StreamHandler(InputStream in, OutputStream out) {
        this.mInputStream = in;
        this.mOutputStream = out;
    }

    public String readLine() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        while (true) {
            if (mInputStream.available() > 0) {
                byte[] buffer = new byte[1024];
                int read = mInputStream.read(buffer);

                String str = new String(buffer, 0, read);

                int nLineIndex = -1; // exclusive
                for (int i = 0; i < str.length(); i++) {
                    if (str.charAt(i) == '\n' || str.charAt(i) == '\r') {
                        nLineIndex = i;
                    }
                }

                if (nLineIndex == -1) {
                    stringBuilder.append(str);

                } else {
                    stringBuilder.append(str, 0, nLineIndex);
                    return stringBuilder.toString();
                }
            }
        }
    }

    public String[] readUntilPattern(String pattern) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Pattern mPattern = Pattern.compile(pattern);

        while (true) {
            if (mInputStream.available() > 0) {
                byte[] buffer = new byte[1024];
                int read = mInputStream.read(buffer);

                String str = new String(buffer, 0, read);

                stringBuilder.append(str);

                String string = stringBuilder.toString();
                Matcher matcher = mPattern.matcher(string);

                List<String> matchList = new ArrayList<>();

                while (matcher.find()) {
                    matchList.add(matcher.group());
                }

                if (matchList.size() > 0) {
                    matchList.add(string);
                    return matchList.toArray(new String[0]);
                }
            }
        }
    }

    public void writeLine(String line) throws IOException {
        mOutputStream.write(line.getBytes());
    }

    public void close() throws IOException {
        mInputStream.close();
        mOutputStream.close();
    }
}