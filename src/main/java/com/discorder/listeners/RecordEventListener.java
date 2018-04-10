/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder.listeners;

import com.discorder.Config;
import com.discorder.ProgramState;
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
    private String botName;

    public RecordEventListener() {
        encodePipeline = new ConcurrentLinkedQueue<>();
        listener = new AudioReceiveListener(encodePipeline);
        task = new WriteAudioTask(encodePipeline);
        botName = Config.getDefaultBotName();
    }

    @Override
    public void onStart(RecordEvent e) {
        logger.info("begin recording");
        if (ProgramState.getProgramState() == ProgramState.State.RECORDING) {
            e.getChannel().sendMessage("I am already recording.").queue();
            return;
        }

        logger.debug("attempt to change nickname");
        
        
        Member me = e.getGuild().getSelfMember();
        e.getGuild().getController().setNickname(me, this.botName + " - RECORDING").queue(
            (x) -> {
                logger.debug("notify text channel");
                e.getChannel().sendMessage("I have begun to record.").queue();
            },
            (x) -> {
                logger.error("nickname change failed, notify channel then quit");
                e.getChannel().sendMessage("I was unable to begin recording, because I could not change my own nickname.").queue();
                return;
            }
        );        
        
        logger.debug("insert audio listeners");
        e.getGuild().getAudioManager().setReceivingHandler(listener);
        
        logger.debug("start stream-to-disk task");
        task.start(Config.getDefaultSaveDestination());
        
        ProgramState.setProgramState(ProgramState.State.RECORDING);
    }

    @Override
    public void onPause(RecordEvent e) {
        logger.info("pause recording - NOT YET IMPLEMENTED");
        if (ProgramState.getProgramState() == ProgramState.State.PAUSED) {
            e.getChannel().sendMessage("I am already paused. I can't do it again.").complete();
            return;
        }
    }

    @Override
    public void onStop(RecordEvent e) {
        logger.info("stop recording");
        if (ProgramState.getProgramState() == ProgramState.State.STOPPED) {
            e.getChannel().sendMessage("I was already not recording, so I will continue to not do so.").complete();
            return;
        }
        
        logger.debug("reset nickname");
        Member me = e.getGuild().getSelfMember();
        e.getGuild().getController().setNickname(me, this.botName).complete();

        ProgramState.setProgramState(ProgramState.State.STOPPED);
        
        logger.debug("notify text channel");
        e.getChannel().sendMessage("I'm no longer recording.").complete();

        logger.debug("remove AudioReceiveListener");
        e.getGuild().getAudioManager().setReceivingHandler(null);

        logger.debug("signal write task finish");
        task.stop();
    }

}
