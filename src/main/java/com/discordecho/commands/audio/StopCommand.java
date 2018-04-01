package com.discordecho.commands.audio;

import com.discordecho.commands.Command;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class StopCommand implements Command {

	public Boolean called(String[] args, GuildMessageReceivedEvent e) {
		return null;
	}

	public void action(String[] args, GuildMessageReceivedEvent e) {
		
	}

	public String usage(String prefix) {
		return prefix + "stop";
	}

	public String description() {
		return "Stops recording the current channel. Does nothing if not already recording.";
	}

	public void executed(boolean success, GuildMessageReceivedEvent e) {
		return;
	}

}