/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private void initFile(final Path dirName) {
        String fileName = new SimpleDateFormat("yyyy-MM-dd - hh.mm.ss").format(new Date()) + ".mp3";

        if (Files.notExists(dirName)) {
            logger.info("target file {} will be created", dirName.toAbsolutePath().toString());
            try {
                Files.createDirectories(dirName);
            } catch (IOException ex) {
                logger.error("unable to create directory to save file", ex);
            }
        }

        this.audioFile = dirName.resolve(Paths.get(fileName)).toFile();
    }

    public void start(Path dirName) {
        this.initFile(dirName);
        try {
            this.outStream = new FileOutputStream(this.audioFile);
        } catch (FileNotFoundException ex) {
            logger.error("caught exception trying to open file output stream", ex);
        }
        this.executor.scheduleAtFixedRate(this::run, 0, 20, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        logger.debug("signal encode thread to terminate");
        try {
            this.executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            logger.error("executor thread in WriteAudioTask interrupted", ex);
        }
        
        logger.debug("close output stream");
        try {
            this.outStream.close();
        } catch (IOException ex) {
            logger.error("encountered an error closing the file output stream", ex);
        }
    }

    private void run() {
        logger.debug("samples in encode queue: {}", this.pipeline.size());

        while (!this.pipeline.isEmpty()) {
            logger.debug("take sample from encode pipeline");
            byte[] sample = this.pipeline.poll();

            logger.debug("write sample to file");
            try {
                this.outStream.write(sample);
            } catch (IOException ex) {
                logger.error("an error occurred while writing this sample to file", ex);
            }
        }
        
    }
}
