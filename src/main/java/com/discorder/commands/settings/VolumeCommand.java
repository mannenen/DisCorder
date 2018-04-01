package com.discorder.commands.settings;

import com.discorder.DisCorder;
import com.discorder.commands.Command;
import com.discorder.configuration.Config;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class VolumeCommand implements Command {

    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 1) {
            String prefix = Config.getCommandPrefix();
            DisCorder.sendMessage(e.getChannel(), usage(prefix));
            return;
        }

        try{
            int num = Integer.parseInt(args[0]);

            if (num > 0 && num <= 100) {
                double percent = (double) num / 100.0;
                Config.setDefaultVolume(percent);

                DisCorder.sendMessage(e.getChannel(), "Volume set to " + num + "% for next recording!");

            } else {
                String prefix = Config.getCommandPrefix();
                DisCorder.sendMessage(e.getChannel(), usage(prefix));
                return;
            }

        } catch (Exception ex) {
            String prefix = Config.getCommandPrefix();
            DisCorder.sendMessage(e.getChannel(), usage(prefix));
            return;
        }
    }

    public String usage(String prefix) {
        return prefix + "volume [1-100]";
    }

    public String description() {
        return "Sets the percentage volume to record at, from 1-100%";
    }

    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }
}
