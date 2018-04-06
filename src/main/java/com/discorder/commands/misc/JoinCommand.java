package com.discorder.commands.misc;

import com.discorder.commands.Command;
import com.discorder.Config;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JoinCommand implements Command {
    private final static Logger logger = LoggerFactory.getLogger(JoinCommand.class);
    
    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        TextChannel channel = e.getChannel();
        if (!e.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
            logger.error("not allowed to join any voice channels");
            channel.sendMessage("DisCorder does not have permission to join any voice channels!").queue();
            channel.sendMessage("Make sure you selected the correct permissions on your bot page.").queue();
            return;
        }
        
        VoiceChannel vc = e.getMember().getVoiceState().getChannel();
        if (vc == null) {
            String prefix = Config.getCommandPrefix();
            logger.error("user tried to summon bot without being in voice channel");
            channel.sendMessage("You are not in a voice channel, I don't know where to go?").queue();
            return;
        }
        
        AudioManager am = vc.getGuild().getAudioManager();
        if (am.isAttemptingToConnect()) {
            logger.error("tried to join a voice channel while already trying to join a voice channel");
            channel.sendMessage("I'm already trying to join a channel, please wait.").queue();
            return;
        }
        
        logger.debug("opening audio connection");
        am.openAudioConnection(vc);
        channel.sendMessage("Hello, friends. I am here and ready to record.").queue();
        
    }

    @Override
    public String usage() {
        return Config.getCommandPrefix() + "join";
    }

    @Override
    public String description() {
        return "Summons the bot to your current voice channel to make it available to record.";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }
}
