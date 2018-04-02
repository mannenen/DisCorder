/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nathan
 */
public class WriteAudioTask implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(WriteAudioTask.class);
    private FileChannel channel;
    private ByteBuffer buffer;
    private final Path fileName;
    private final ArrayBlockingQueue<SampleContainer> queue;
    
    public WriteAudioTask(Path fileName, ArrayBlockingQueue<SampleContainer> encodeQueue) {
        this.fileName = fileName;
        this.queue = encodeQueue;        
    }
    
    public void prepareFile() throws IOException {
        channel = new FileOutputStream(this.fileName.toFile()).getChannel();
    }
    
    public void finishFile() throws IOException {
        while (!this.queue.isEmpty())
            logger.debug("waiting for worker queue to empty, items remaining: %d", this.queue.size());
        channel.close();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                logger.debug("in worker thread, processing items remaining: %d", this.queue.size());
                byte[] sample = this.queue.take().sample;
                buffer = ByteBuffer.wrap(sample);
                channel.write(buffer);
            } catch (InterruptedException ex) {
                logger.info("worker thread received interrupt while retrieving from work queue");
            } catch (IOException ex) {
                logger.error("unable to write sample to file in worked thread");
            }
            logger.debug("work items remaining in queue: %d", this.queue.size());
        }
    }
}
