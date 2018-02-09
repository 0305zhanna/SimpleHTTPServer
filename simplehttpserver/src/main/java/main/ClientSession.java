package main;

import http.HttpRequest;
import http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientSession implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientSession.class);
    private Socket socket;

    public ClientSession(Socket socket) {
        this.socket = socket;
        logger.debug("Connected a client {}", socket);
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            HttpRequest req = new HttpRequest(br);
            HttpResponse resp = new HttpResponse(req);

            try (BufferedOutputStream os = new BufferedOutputStream(socket.getOutputStream())) {
                resp.write(os);
            }

        } catch (IOException e) {
            logger.debug("", e);
        }
        logger.debug("Thread stopped");
    }
}
