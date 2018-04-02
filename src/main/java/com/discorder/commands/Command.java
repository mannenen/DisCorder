package com.discorder.commands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public interface Command {
    Boolean called(String[] args, GuildMessageReceivedEvent e);
    void action(String[] args, GuildMessageReceivedEvent e);
    String usage();
    String description();
    void executed(boolean success, GuildMessageReceivedEvent e);
}
