package ru.romanisupov;

public class Job {
    private final int id;
    private final int concurrency;
    private final String url;

    public Job(int id, int concurrency, String url) {
        this.id = id;
        this.concurrency = concurrency;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public String getUrl() {
        return url;
    }
}
