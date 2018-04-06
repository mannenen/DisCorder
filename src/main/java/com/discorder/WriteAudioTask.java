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
    
    public WriteAudioTask(ConcurrentLinkedQueue<byte[]> pipeline) {
        this.encodePipeline = pipeline;
        
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }
    
    public void start() {
        this.executor.scheduleAtFixedRate(this::run, 0, 20, TimeUnit.MILLISECONDS);
    }
    
    public void stop() throws InterruptedException {
        this.executor.awaitTermination(60, TimeUnit.SECONDS);
    }

    private void run() {
        logger.debug("samples in encode queue: {}", this.encodePipeline.size());
    }
}
