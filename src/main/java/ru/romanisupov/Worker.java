package ru.romanisupov;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Worker {
    private final BlockingQueue<Job> jobBlockingQueue;
    private final List<Integer> jobIds;
    private final String tempPath;
    private final String readyPath;
    private final String extension;
    private boolean isRunning;

    public Worker(String tempPath, String readyPath, String extension) {
        this.tempPath = tempPath;
        this.readyPath = readyPath;
        this.extension = extension;
        jobBlockingQueue = new LinkedBlockingQueue<>();
        jobIds = new ArrayList<>();
        isRunning = false;
    }

    private class QueueWorker extends Thread {
        private final Job job;

        private QueueWorker(Job job) {
            this.job = job;
        }

        @Override
        public void run() {
            complete(job);
        }
    }

    public void add(Job job) {
        jobIds.add(job.getId());
        jobBlockingQueue.add(job);
    }

    public JobState getJobState(int id) {
        if (jobIds.contains(id)) {
            return JobState.QUEUED;
        }
        else if (fileExists(readyPath + id + extension)) {
            return JobState.READY;
        }
        else if (fileExists(tempPath + id + extension)) {
            return JobState.PROGRESS;
        }
        return JobState.EEXISTS;
    }

    public String getReadyFilePath(int id) {
        return readyPath + id + extension;
    }

    private boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public void run() {
        isRunning = true;
        while (!jobBlockingQueue.isEmpty()) {
            try {
                Job job = jobBlockingQueue.take();
                jobIds.remove(0);
                QueueWorker queueWorker = new QueueWorker(job);
                queueWorker.start();
                queueWorker.join();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void complete(final Job job) {
        String filePath = tempPath + job.getId() + extension;
        FileWorker.download(job.getUrl(), filePath);
        int[] array = FileWorker.readArrayFromFile(filePath);
        Sorter.sort(array, job.getConcurrency());
        filePath = readyPath + job.getId() + extension;
        try {
            FileWorker.writeArrayToFile(array, filePath);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
