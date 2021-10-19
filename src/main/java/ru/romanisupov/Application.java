package ru.romanisupov;

public class Application {

    public static void main(String[] args) {
        Worker worker = new Worker("/local/temp/", "/local/ready/", ".txt");
        Server server = new Server(worker, 8888);
        server.start();
    }
}
