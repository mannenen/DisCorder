/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder.listeners;

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

        logger.debug("start stream-to-disk task");
        task.start();

        logger.debug("attempt to change nickname");
        Member me = e.getGuild().getSelfMember();
        botName = me.getNickname();
        e.getGuild().getController().setNickname(me, this.botName + " - RECORDING").queue();

        if (!me.getNickname().contains("RECORDING")) {
            logger.error("nickname change failed, notify channel then quit");
            e.getChannel().sendMessage("I was unable to begin recording, because I could not change my own nickname.").queue();
            e.getChannel().sendMessage("Please make sure I have permission to do that, then try again.").queue();
            return;
        }

        logger.debug("notify text channel");
        e.getChannel().sendMessage("I have begun to record.").queue();

        logger.debug("insert audio listener");
        e.getGuild().getAudioManager().setReceivingHandler(listener);

        ProgramState.setProgramState(ProgramState.State.RECORDING);
    }

    @Override
    public void onPause(RecordEvent e) {
        logger.info("pause recording - NOT YET IMPLEMENTED");
        if (ProgramState.getProgramState() == ProgramState.State.PAUSED) {
            e.getChannel().sendMessage("I am already paused. I can't do it again.").queue();
            return;
        }
    }

    @Override
    public void onStop(RecordEvent e) {
        logger.info("stop recording");
        if (ProgramState.getProgramState() == ProgramState.State.STOPPED) {
            e.getChannel().sendMessage("I was already not recording, so I will continue to not do so.").queue();
            return;
        }

        logger.debug("remove AudioReceiveListener");
        e.getGuild().getAudioManager().setReceivingHandler(null);

        logger.debug("signal write task finish");
        task.stop();

        logger.debug("reset nickname");
        Member me = e.getGuild().getSelfMember();
        e.getGuild().getController().setNickname(me, this.botName).queue();

        ProgramState.setProgramState(ProgramState.State.RECORDING);

        logger.debug("notify text channel");
        e.getChannel().sendMessage("I'm no longer recording.").queue();
    }

}
