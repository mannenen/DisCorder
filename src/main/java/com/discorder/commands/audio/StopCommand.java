package com.discorder.commands.audio;

import com.discorder.commands.Command;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class StopCommand implements Command {

        @Override
	public Boolean called(String[] args, GuildMessageReceivedEvent e) {
		return null;
	}

        @Override
	public void action(String[] args, GuildMessageReceivedEvent e) {
		
	}

        @Override
	public String usage(String prefix) {
		return prefix + "stop";
	}

        @Override
	public String description() {
		return "Stops recording the current channel. Does nothing if not already recording.";
	}

        @Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		return;
	}

}