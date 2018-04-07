/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder.event;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

/**
 *
 * @author nathan
 */
public class RecordEvent {
    public enum EventType {
        START, STOP, PAUSE
    }

    private final EventType type;
    private final Guild guild;
    private final User user;
    private final TextChannel channel;

    public RecordEvent(EventType type, RecordEvent event) {
        this.type = type;
        this.user = event.user;
        this.guild = event.guild;
        this.channel = event.channel;
    }

    public RecordEvent(EventType type, User author, Guild guild, TextChannel channel) {
        this.type = type;
        this.user = author;
        this.guild = guild;
        this.channel = channel;
    }

    public EventType getEventType() {
        return this.type;
    }

    public Guild getGuild() {
        return guild;
    }

    public User getUser() {
        return user;
    }

    public TextChannel getChannel() {
        return this.channel;
    }
}
