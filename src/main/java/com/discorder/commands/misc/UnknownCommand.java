package com.discorder.commands.misc;

import com.discorder.commands.Command;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class UnknownCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e) {
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        TextChannel channel = e.getChannel();
        channel.sendMessageFormat("I don't know what you mean by '%s", e.getMessage().getContentRaw()).queue();
        channel.sendMessageFormat("I damn sure don't know what all this means: '%s'", String.join(" ", args)).queue();
    }

    @Override
    public String usage(String prefix) {
        return "";
    }

    @Override
    public String description() {
        return "This is a default response when bot receives an unknown command.";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e) {
        return;
    }
    
}