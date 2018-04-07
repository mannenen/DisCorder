package com.discorder.commands.audio;

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
        return Config.getCommandPrefix + "pause";
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
