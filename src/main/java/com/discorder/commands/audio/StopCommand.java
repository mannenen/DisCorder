package com.discorder.commands.audio;

import com.discorder.commands.Command;
import com.discorder.Config;
import com.discorder.event.EventManager;
import com.discorder.event.RecordEvent;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopCommand implements Command {
    private final static Logger logger = LoggerFactory.getLogger(StopCommand.class);
    
    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e) {
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        logger.debug("enqueueing record stop event");
        RecordEvent event = new RecordEvent(RecordEvent.EventType.STOP, e.getAuthor(), e.getGuild(), e.getChannel());
        EventManager.getInstance().queueEvent(event);
    }

    @Override
    public String usage() {
        return Config.getCommandPrefix() + "stop";
    }

    @Override
    public String description() {
        return "Stops recording the current channel. Does nothing if not already recording.";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e) {
        return;
    }

}
