package com.discordecho.commands.audio;

import com.discordecho.commands.Command;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class RecordCommand implements Command {

	public Boolean called(String[] args, GuildMessageReceivedEvent e) {
		return true;
	}

	public void action(String[] args, GuildMessageReceivedEvent e) {
		
	}

	public String usage(String prefix) {
		return prefix + "record [mp3|wav]";
	}

	public String description() {
		return "Begins recording audio in the current voice channel. Does nothing if already recording.";
	}

	public void executed(boolean success, GuildMessageReceivedEvent e) {
		return;
	}

}