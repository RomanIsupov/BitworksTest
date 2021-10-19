package ru.romanisupov;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server extends Thread {
    private HttpServer httpServer;
    private final Worker worker;

    public Server(final Worker worker, final int serverPort) {
        this.worker = worker;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(serverPort), 0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeServer(Worker worker) {
        httpServer.createContext("/", new RequestHandler(worker));
        httpServer.setExecutor(null);
        httpServer.start();
    }

    @Override
    public void run() {
        initializeServer(worker);
    }
}
