package com.discorder.commands.audio;

import com.discorder.commands.Command;
import com.discorder.Config;
import com.discorder.WriteAudioTask;
import com.discorder.listeners.AudioReceiveListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.dv8tion.jda.core.entities.TextChannel;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordCommand implements Command {

    private final static Logger logger = LoggerFactory.getLogger(RecordCommand.class);
    private final AudioReceiveListener receiver;
    
    public RecordCommand(AudioReceiveListener receiver) {
        this.receiver = receiver;
    }

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e) {
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        logger.debug("received record command");
        TextChannel tc = e.getChannel();
        tc.sendMessage("I am recording your every word.").queue();
        
        this.receiver.start();
        
        
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
