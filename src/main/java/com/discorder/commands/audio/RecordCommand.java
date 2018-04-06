package com.discorder.commands.audio;

import com.discorder.commands.Command;
import com.discorder.Config;
import com.discorder.event.EventManager;
import com.discorder.event.RecordEvent;
import com.discorder.listeners.AudioReceiveListener;
import net.dv8tion.jda.core.entities.TextChannel;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordCommand implements Command {

    private final static Logger logger = LoggerFactory.getLogger(RecordCommand.class);
    
    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e) {
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        logger.debug("queueing recordStartEvent");
        RecordEvent event = new RecordEvent(RecordEvent.EventType.START, e.getAuthor(), e.getGuild(), e.getChannel());
        EventManager.getInstance().queueEvent(event);
    }

    @Override
    public String usage() {
        return Config.getCommandPrefix() + "record";
    }

    @Override
    public String description() {
        return "Begins recording audio in the current voice channel. Does nothing if already recording.";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e) {
        return;
    }
}
