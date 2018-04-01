package com.discordecho.commands.settings;

import com.discordecho.DiscordEcho;
import com.discordecho.commands.Command;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class PrefixCommand implements Command {

    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args[0].length() != 1 || args.length != 1) {
            String prefix = DiscordEcho.serverSettings.get(e.getGuild().getId()).prefix;
            DiscordEcho.sendMessage(e.getChannel(), usage(prefix));
            return;
        }

        DiscordEcho.serverSettings.get(e.getGuild().getId()).prefix = args[0];
        DiscordEcho.writeSettingsJson();

        DiscordEcho.sendMessage(e.getChannel(), "Command prefix now set to " + args[0]);
    }

    public String usage(String prefix) {
        return prefix + "prefix [character]";
    }

    public String description() {
        return "Sets the prefix for each command to avoid conflict with other bots (Default is '!')";
    }

    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }
}
