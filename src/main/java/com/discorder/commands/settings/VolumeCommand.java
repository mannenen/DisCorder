package com.discorder.commands.settings;

import com.discorder.commands.Command;
import com.discorder.Config;
import net.dv8tion.jda.core.entities.TextChannel;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VolumeCommand implements Command {
    private final static Logger logger = LoggerFactory.getLogger(VolumeCommand.class);

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        TextChannel tc = e.getChannel();
        if (args.length != 1) {
            String prefix = Config.getCommandPrefix();
            tc.sendMessage(usage()).queue();
            return;
        }

        try{
            int num = Integer.parseInt(args[0]);

            if (num > 0 && num <= 100) {
                double percent = (double) num / 100.0;
                Config.setDefaultVolume(percent);

                tc.sendMessage("Volume set to " + num + "% for next recording!").queue();

            } else {
                String prefix = Config.getCommandPrefix();
                tc.sendMessage(usage()).queue();
                return;
            }

        } catch (NumberFormatException ex) {
            logger.error("bad param given to volume command", ex);
            String prefix = Config.getCommandPrefix();
            tc.sendMessage(usage()).queue();
            return;
        }
    }

    @Override
    public String usage() {
        String prefix = Config.getCommandPrefix();
        return prefix + "volume [1-100]";
    }

    @Override
    public String description() {
        return "Sets the percentage volume to record at, from 1-100%";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }
}
