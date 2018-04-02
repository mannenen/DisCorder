package com.discorder.commands.audio;

import com.discorder.commands.Command;
import com.discorder.Config;
import com.discorder.listeners.AudioReceiveListener;
import net.dv8tion.jda.core.entities.TextChannel;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopCommand implements Command {
    private final static Logger logger = LoggerFactory.getLogger(StopCommand.class);
    private final AudioReceiveListener receiver;
    
    public StopCommand(AudioReceiveListener receiver) {
        this.receiver = receiver;
    }
    
    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e) {
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        logger.debug("received stop command");
        TextChannel tc = e.getChannel();
        
        if (!this.receiver.canReceiveCombined()) {
            tc.sendMessage("It's cool, I was already not recording anyway, so I will continue not recording.").queue();
        } else {
            this.receiver.stop();
            tc.sendMessage("OK, I have stopped recording.").queue();
        }
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
