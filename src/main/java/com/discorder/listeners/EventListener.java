package com.discorder.listeners;

import com.discorder.commands.CommandHandler;
import com.discorder.configuration.Config;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EventListener extends ListenerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(EventListener.class);

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        logger.info("Joined new server '%s'", e.getGuild().getName());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        logger.info("Left server '%s'", e.getGuild().getName());
    }

    @Override
    public void onReady(ReadyEvent e){
        logger.info("DisCorder is ready!");
        
    }
    
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        logger.debug(e.getMessage().getContentRaw());
        if(e.getMember() == null || e.getMember().getUser() == null || e.getMember().getUser().isBot())
            return;

        String prefix = Config.getCommandPrefix();
        //force help to always work with "!" prefix
        if (e.getMessage().getContentRaw().startsWith(prefix) || e.getMessage().getContentRaw().startsWith("!help")) {
            CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContentRaw().toLowerCase(), e));
}
    }
}
