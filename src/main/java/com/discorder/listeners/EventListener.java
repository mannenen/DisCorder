package com.discorder.listeners;

import com.discorder.commands.CommandHandler;
import com.discorder.Config;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EventListener extends ListenerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(EventListener.class);

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        logger.info("Joined new server {}", e.getGuild().getName());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        logger.info("Left server {}", e.getGuild().getName());
    }
    
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
        logger.info("successfully joined voice channel {}", e.getChannelJoined().getName());
        e.getGuild().getAudioManager().setSendingHandler(new AudioSendListener());
    }

    @Override
    public void onReady(ReadyEvent e) {
        logger.info("DisCorder is ready!");

    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        Member sender = e.getMember();
        if(sender == null || sender.getUser() == null || sender.getUser().isBot())
            return;

        String prefix = Config.getCommandPrefix();
        String messageRaw = e.getMessage().getContentRaw();
        //force help to always work with "!" prefix
        if (messageRaw.startsWith(prefix) || messageRaw.startsWith("!help")) {
            CommandHandler.handleCommand(CommandHandler.parser.parse(messageRaw.toLowerCase(), e));
        }
    }
}
