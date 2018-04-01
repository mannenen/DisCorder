package com.discorder.commands.settings;

import com.discorder.DisCorder;
import com.discorder.commands.Command;
import com.discorder.configuration.Config;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class SaveLocationCommand implements Command {
    
    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length > 1) {
            String prefix = Config.getCommandPrefix();
            DisCorder.sendMessage(e.getChannel(), usage(prefix));
            return;
        }

        if (args.length == 0) {
            String id = e.getChannel().getId();
            Config.setDefaultTextChannel(id);
            DisCorder.sendMessage(e.getChannel(), "Now defaulting to the " + e.getChannel().getName() + " text channel");

        } else if (args.length == 1) {

            //cut off # in channel name if they included it
            if (args[0].startsWith("#")) {
                args[0] = args[0].substring(1);
            }
            
            if(e.getGuild().getTextChannelsByName(args[0], true).size() == 0) {
                DisCorder.sendMessage(e.getChannel(), "Cannot find specified text channel");
                return;
            }
            String id = e.getGuild().getTextChannelsByName(args[0], true).get(0).getId();
            Config.setDefaultTextChannel(id);
            DisCorder.sendMessage(e.getChannel(), "Now defaulting to the " + e.getGuild().getTextChannelById(id).getName() + " text channel");

        }
    }

    @Override
    public String usage(String prefix) {
        return prefix + "saveLocation | " + prefix + "saveLocation [text channel name]";
    }

    @Override
    public String description() {
        return "Sets the text channel of message or the text channel specified as the default location to send files";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }
}
