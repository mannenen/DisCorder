package com.discordecho.commands.misc;

import com.discordecho.DiscordEcho;
import com.discordecho.commands.Command;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class LeaveCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 0) {
            String prefix = DiscordEcho.serverSettings.get(e.getGuild().getId()).prefix;
            DiscordEcho.sendMessage(e.getChannel(), usage(prefix));
            return;
        }

        if (!e.getGuild().getAudioManager().isConnected()) {
            DiscordEcho.sendMessage(e.getChannel(), "I am not in a channel!");
            return;
        }

        //write out previous channel's audio if autoSave is on
        if (DiscordEcho.serverSettings.get(e.getGuild().getId()).autoSave)
            DiscordEcho.writeToFile(e.getGuild());

        DiscordEcho.leaveVoiceChannel(e.getGuild().getAudioManager().getConnectedChannel());

    }

    @Override
    public String usage(String prefix) {
        return prefix + "leave";
    }

    @Override
    public String descripition() {
        return "Force the bot to leave it's current channel";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }
}
