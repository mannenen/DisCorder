package com.discorder.commands.audio;

import com.discorder.commands.Command;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class RecordCommand implements Command {

        @Override
	public Boolean called(String[] args, GuildMessageReceivedEvent e) {
		return true;
	}

        @Override
	public void action(String[] args, GuildMessageReceivedEvent e) {
		
	}

        @Override
	public String usage(String prefix) {
		return prefix + "record [mp3|wav]";
	}

        @Override
	public String description() {
		return "Begins recording audio in the current voice channel. Does nothing if already recording.";
	}

        @Override
	public void executed(boolean success, GuildMessageReceivedEvent e) {
		return;
	}

}