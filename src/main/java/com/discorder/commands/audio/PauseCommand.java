package com.discorder.commands.audio;

import com.discorder.Config;
import com.discorder.commands.Command;
import com.discorder.event.EventManager;
import com.discorder.event.RecordEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class PauseCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e) {
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        RecordEvent event = new RecordEvent(RecordEvent.EventType.PAUSE, e.getAuthor(), e.getGuild(), e.getChannel());
        EventManager.getInstance().queueEvent(event);
    }

    @Override
    public String usage() {
        return Config.getCommandPrefix() + "pause";
    }

    @Override
    public String description() {
        return "Pauses the current recording";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e) {
        return;
    }

}
