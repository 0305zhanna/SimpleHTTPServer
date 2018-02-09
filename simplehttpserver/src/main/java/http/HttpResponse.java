package http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpResponse {
    private static final Logger logger = LogManager.getLogger(HttpResponse.class);
    private static final String VERSION = "HTTP/1.1";

    byte[] body;

    List<String> headers = new ArrayList<>();

    public HttpResponse(HttpRequest req) {
        final String method = req.getMethod();

        switch (method) {
            case "GET":
                final String url = req.getUrl();
                final Path path = Paths.get(".", url);

                if (!Files.exists(path)) {
                    fillHeaders(HttpStatus.NOT_FOUND);
                    fillBody("<h1>The requested resourse is not found</h1>");
                    return;
                }

                if (Files.isDirectory(path)) {
                    sendFilesList(path);
                    fillHeaders(HttpStatus.OK);
//                    fillBody("<h1>Unsupported operation</h1>");
                    return;
                } else {
                    logger.debug("file found!");
                    //transfer to file
                    sendFile(path);
                }

                break;
            case "POST":
                break;
            default:
                System.out.println("im in default");
                break;
        }
    }

    public void write(OutputStream os) throws IOException {
        // write headers
        headers.forEach(s -> writeString(os, s));
        // write emty line
        writeString(os, "");
        // write body
        if (body != null)
            os.write(body);
//        os.flush();
    }

    private void writeString(OutputStream os, String s) {
        try {
            os.write((s + "\r\n").getBytes(UTF_8));
        } catch (IOException e) {
            logger.debug("", e);
        }
    }

    private void fillHeaders(HttpStatus status) {
        headers.add(VERSION + " " + status);
        headers.add("Server: Simple Java Core November HTTPSERVER");
        headers.add("Connection close");
    }

    private void fillBody(String s) {
        body = s.getBytes(UTF_8);
    }

    private void sendFile(Path path) {
        try {
            body = Files.readAllBytes(path);
            fillHeaders(HttpStatus.OK);
        } catch (IOException e) {
            logger.debug("", e);
            fillHeaders(HttpStatus.SERVER_ERROR);
            fillBody("<h1>Error showing file</h1>");
        }
    }

    private void sendFilesList(Path path) {
        try {
            File file = path.toFile();
            File[] arrFiles = file.listFiles();

            String b = "";
            for (int i = 0; i < arrFiles.length; i++) {
                b += String.format("<p><a href=\"%s\">%s</a></p>%n", arrFiles[i].getPath().substring(1), arrFiles[i].getName());
            }
            fillBody(b);
            fillHeaders(HttpStatus.OK);
        } catch (Exception e) {
            logger.debug("", e);
            fillHeaders(HttpStatus.SERVER_ERROR);
            fillBody("<p>Error showing file</p>");
        }
    }
}
