package http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger logger = LogManager.getLogger(HttpRequest.class);
    private String method;
    private String url;
    private String version;
    private Map<String, String> headers = new HashMap<>();

    public HttpRequest(BufferedReader br) {
        try {
            //read main url string
            String line = br.readLine();
            parseRequestString(line);

//            System.out.printf("Request: %s%n", line);//logging
            logger.debug("Request: {}",line);

            do {
                line = br.readLine();
                parseHeaderLine(line);
            } while (!"".equals(line));

            headers.forEach((k, v) -> logger.debug("{} {}", k,v));//System.out.printf("%s %s%n", k, v));//logging

        } catch (IOException e) {
            logger.error("",e);
//            e.printStackTrace();//logging
        }

    }

    private void parseHeaderLine(String line) {
        if (line == null || "".equals(line)) return;
        String[] parts = line.split(":\\s+");
        headers.put(parts[0], parts[1]);
    }

    private void parseRequestString(String line) {
        if (line == null ) return;
        String[] parts = line.split("\\s+");
        method = parts[0];
        url = parts[1];
        version = parts[2];
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }
}
