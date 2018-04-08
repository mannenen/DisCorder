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
    private final ConcurrentLinkedQueue<byte[]> encodePipeline;
    private final ScheduledExecutorService executor;
    private File audioFile;

    public WriteAudioTask(ConcurrentLinkedQueue<byte[]> pipeline) {
        this.encodePipeline = pipeline;

        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    private boolean initFile(final String dirName) {
        String fileName = new SimpleDateFormatter("yyyy-MM-dd - hh.mm.ss").format(new Date()) + ".mp3";
        Path path = Paths.get(dirName);

        if (Files.notExists(path)) {
            logger.info("target file {} will be created", dirName);
            this.audioFile = Files.createFile(Files.createDirectories(dirName), fileName).toFile();
            return true;
        }

        return false;
    }

    public void start(String fileName) {
        try {
            this.initFile(fileName);
            this.executor.scheduleAtFixedRate(this::run, 0, 20, TimeUnit.MILLISECONDS);
        } except (IOException ioe) {
            logger.error("unable to open audio file for writing", ioe);
        }
    }

    public void stop() {
        while (!this.encodePipeline.isEmpty()) {
            try {
                this.executor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                logger.debug("interrupted waiting for executor to finish", ex);
            }
        }

        try {
            this.audioFile.close();
        } catch (IOException ioe) {
            logger.error("got error during file close, whatever", ioe);
        }
    }

    private void run() {
        logger.debug("samples in encode queue: {}", this.encodePipeline.size());
        byte[] sample = this.encodePipeline.poll();

        
    }
}
