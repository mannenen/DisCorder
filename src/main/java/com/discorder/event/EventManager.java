/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder.event;

import com.discorder.event.handler.RecordEventHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
public class EventManager {

    private final static Logger logger = LoggerFactory.getLogger(EventManager.class);
    private final ConcurrentLinkedQueue<RecordEvent> eventQueue;
    private final List<RecordEventHandler> handlers;
    private static EventManager instance = null;
    private final ScheduledExecutorService executor;

    private EventManager() {
        this.eventQueue = new ConcurrentLinkedQueue<>();
        this.handlers = new ArrayList<>();

        logger.info("starting record event loop");
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.executor.scheduleWithFixedDelay(this::processEvents, 0, 50, TimeUnit.MILLISECONDS);

    }

    public static EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }

        return instance;
    }

    public synchronized void queueEvent(RecordEvent e) {
        this.eventQueue.add(e);
    }

    public void addListener(RecordEventHandler handler) {
        handlers.add(handler);
    }

    public void removeListener(RecordEventHandler handler) {
        handlers.removeIf(x -> x.equals(handler));
    }

    private void processEvents() {
        if (!this.eventQueue.isEmpty()) {
            try {
                RecordEvent event = this.eventQueue.remove();

                switch (event.getEventType()) {
                    case PAUSE:
                        handlers.forEach(x -> x.onPause(event));
                        break;
                    case START:
                        handlers.forEach(x -> x.onStart(event));
                        break;
                    case STOP:
                        handlers.forEach(x -> x.onStop(event));
                        break;
                    default:
                        logger.warn("got an unknown recordEvent type");
                        break;
                }
            } catch (NoSuchElementException nsee) {
                logger.warn("tried to remove from an empty queue", nsee);
            }
        }
    }
}
