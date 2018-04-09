/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nathan
 */
public class WriteAudioTask {
    private final static Logger logger = LoggerFactory.getLogger(WriteAudioTask.class);
    private final ConcurrentLinkedQueue<byte[]> pipeline;
    private final ScheduledExecutorService executor;
    private File audioFile;
    private FileOutputStream outStream;

    public WriteAudioTask(ConcurrentLinkedQueue<byte[]> pipeline) {
        this.pipeline = pipeline;

        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    private void initFile(final String dirName) {
        String fileName = new SimpleDateFormatter("yyyy-MM-dd - hh.mm.ss").format(new Date()) + ".mp3";
        Path path = Paths.get(dirName);

        if (Files.notExists(path)) {
            logger.info("target file {} will be created", dirName);
            Paths.createDirectories(path);
        }

        this.audioFile = path.resolve(Paths.get(dirName)).toFile();
    }

    public void start(String fileName) {
        try {
            this.initFile(fileName);
            this.outStream = new FileOutputStream(this.audioFile);
            this.executor.scheduleAtFixedRate(this::run, 0, 20, TimeUnit.MILLISECONDS);
        } except (IOException ioe) {
            logger.error("unable to open audio file for writing", ioe);
        }
    }

    public void stop() {
        logger.debug("wait for encode pipeline to empty");
        while (!this.pipeline.isEmpty()) {
            logger.info("encode pipeline not empty, sleeping");
            Thread.currentThread().sleep(1000);
        }

        logger.debug("close output stream");
        this.outStream.close();

        logger.debug("wait for encode thread to terminate");
        this.executor.awaitTermination(5, TimeUnit.SECONDS);
    }

    private void run() {
        logger.debug("samples in encode queue: {}", this.pipeline.size());

        logger.debug("take sample from encode pipeline");
        byte[] sample = this.pipeline.poll();

        logger.debug("write sample to file");
        this.outStream.write(this.audioFile, sample);
    }
}
