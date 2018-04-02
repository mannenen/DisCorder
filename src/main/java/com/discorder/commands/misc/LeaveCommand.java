package com.discorder.commands.misc;

import com.discorder.commands.Command;
import com.discorder.Config;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LeaveCommand implements Command {
    private final static Logger logger = LoggerFactory.getLogger(LeaveCommand.class);

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        TextChannel channel = e.getChannel();
        VoiceChannel vc = e.getGuild().getSelfMember().getVoiceState().getChannel();
        if (vc == null) {
            logger.info("told to leave VC but not connected to any");
            channel.sendMessage("That was a cool idea, but I'm not connected to any voice channels, so...").queue();
            return;
        }
        
        e.getGuild().getAudioManager().closeAudioConnection();
        channel.sendMessage("OK, I'm out.").queue();
    }

    @Override
    public String usage() {
        return Config.getCommandPrefix() + "leave";
    }

    @Override
    public String description() {
        return "Force the bot to leave it's current channel";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }
}
