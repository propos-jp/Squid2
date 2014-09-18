package jp.co.and_ex.squid2.observe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by obata on 2014/09/17.
 */
public class SquidParser {
    private SquidReader reader;
    private StringBuffer buffer;

    private static final String TOKEN_GET = "@GET\r\n";
    private static final String TOKEN_SET = "@SET\r\n";
    private static final String TOKEN_OK = "OK\r\n";
    private static final String TOKEN_200 = "200 LINE(S)\r\n";

    public static Pattern getObserve_pattern() {
        return observe_pattern;
    }

    private static Pattern observe_pattern = null;

    static {
        observe_pattern = Pattern.compile("[0-9][0-9][0-9][0-9]\\,[0-9][0-9][0-9][0-9]\\,[0-9][0-9][0-9][0-9]\\r\\n");
    }

    public SquidParser(SquidReader reader) {
        this.reader = reader;
        buffer = new StringBuffer();
    }

    public void putData(String data) {
        if (buffer == null) {
            return;
        }

        buffer.append(data);
        String line = buffer.toString();
        while (true) {
            int i = line.indexOf("\r\n");
            if (i < 0) break;
            String token = line.substring(0, i + 2);
            if (i + 2 == line.length()) {
                line = "";
            } else {
                line = line.substring(i + 2);
            }
            if (TOKEN_GET.equals(token)) {
                reader.tokenGet(token);
            } else if (TOKEN_SET.equals(token)) {
                reader.tokenSet(token);
            } else if (TOKEN_OK.equals(token)) {
                reader.tokenOK(token);
            } else if (TOKEN_200.equals(token)) {
                reader.token200(token);
            } else {
                Matcher m = observe_pattern.matcher(token);
                if (m.find()) {
                    reader.tokenData(token);
                } else {
                    reader.tokenError(token);
                }
            }
        }
        buffer.setLength(0);
        buffer.append(line);
        return;
    }
}


