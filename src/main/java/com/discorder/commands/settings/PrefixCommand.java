package com.discorder.commands.settings;

import com.discorder.DisCorder;
import com.discorder.commands.Command;
import com.discorder.configuration.Config;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class PrefixCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args[0].length() != 1 || args.length != 1) {
            String prefix = Config.getCommandPrefix();
            DisCorder.sendMessage(e.getChannel(), usage(prefix));
            return;
        }

        Config.setCommandPrefix(args[0]);

        DisCorder.sendMessage(e.getChannel(), "Command prefix now set to " + args[0]);
    }

    @Override
    public String usage(String prefix) {
        return prefix + "prefix [character]";
    }

    @Override
    public String description() {
        return "Sets the prefix for each command to avoid conflict with other bots (Default is '!')";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }
}
