package org.glagan.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MessageInputReader {
    protected InputStreamReader reader;
    protected String buffer;
    protected char[] charBuffer;

    public MessageInputReader(InputStream input) {
        this.reader = new InputStreamReader(input);
        this.buffer = "";
        this.charBuffer = new char[65535];
    }

    public int hasCheckSumPattern() {
        int limit = buffer.length();
        for (int i = 0; i < buffer.length(); i++) {
            if (buffer.charAt(i) == (char) 0x1 &&
                    i + 1 < limit && buffer.charAt(i + 1) == '1' &&
                    i + 2 < limit && buffer.charAt(i + 2) == '0' &&
                    i + 3 < limit && buffer.charAt(i + 3) == '=' &&
                    i + 4 < limit && buffer.charAt(i + 4) >= '0' && buffer.charAt(i + 1) <= '9' &&
                    i + 5 < limit && buffer.charAt(i + 5) >= '0' && buffer.charAt(i + 1) <= '9' &&
                    i + 6 < limit && buffer.charAt(i + 6) >= '0' && buffer.charAt(i + 1) <= '9' &&
                    i + 7 < limit && buffer.charAt(i + 7) == (char) 0x1) {
                return i + 7;
            }
        }
        return -1;
    }

    /**
     * Read the input stream until a CheckSum(10) pattern is found and return the
     * packet
     */
    public String read() {
        int checkSumPatternEnd = -1;
        if ((checkSumPatternEnd = hasCheckSumPattern()) > -1) {
            String packet = buffer.substring(0, checkSumPatternEnd);
            buffer = buffer.substring(checkSumPatternEnd + 1);
            return packet;
        }
        int read = 0;
        do {
            try {
                read = reader.read(charBuffer);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            if (read > 0) {
                for (int i = 0; i < read; i++) {
                    buffer += charBuffer[i];
                }
                if ((checkSumPatternEnd = hasCheckSumPattern()) > -1) {
                    String packet = buffer.substring(0, checkSumPatternEnd);
                    buffer = buffer.substring(checkSumPatternEnd + 1);
                    return packet;
                }
            }
        } while (read >= 0);
        return null;
    }
}
