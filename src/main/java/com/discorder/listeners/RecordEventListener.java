/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder.listeners;

import com.discorder.WriteAudioTask;
import com.discorder.event.RecordEvent;
import com.discorder.event.handler.RecordEventHandler;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.dv8tion.jda.core.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nathan
 */
public class RecordEventListener implements RecordEventHandler {
    
    private final static Logger logger = LoggerFactory.getLogger(RecordEventListener.class);
    private final WriteAudioTask task;
    private final ConcurrentLinkedQueue<byte[]> encodePipeline;
    private final AudioReceiveListener listener;
    
    public RecordEventListener() {
        encodePipeline = new ConcurrentLinkedQueue<>();
        listener = new AudioReceiveListener(encodePipeline);
        task = new WriteAudioTask(encodePipeline);
    }

    @Override
    public void onStart(RecordEvent e) {
        logger.debug("start stream-to-disk task");
        task.start();
        
        logger.debug("attempt to change nickname");
        Member me = e.getGuild().getSelfMember();
        e.getGuild().getController().setNickname(me, me.getNickname() + " - RECORDING").queue();
        
        if (!me.getNickname().contains("RECORDING")) {
            logger.error("nickname change failed, notify channel then quit");
            e.getChannel().sendMessage("I was unable to begin recording, because I could not change my own nickname.").queue();
            e.getChannel().sendMessage("Please make sure I have permission to do that, then try again.").queue();
            return;
        }
        
        logger.debug("insert audio listener");
        e.getGuild().getAudioManager().setReceivingHandler(listener);
        
        logger.debug("notify text channel");
        e.getChannel().sendMessage("I have begun to record.").queue();
    }

    @Override
    public void onPause(RecordEvent e) {
        
    }

    @Override
    public void onStop(RecordEvent e) {
        
    }
    
}
