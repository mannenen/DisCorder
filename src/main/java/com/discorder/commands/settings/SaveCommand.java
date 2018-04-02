/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder.commands.settings;

import com.discorder.commands.Command;
import com.discorder.Config;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author nathan
 */
public class SaveCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e) {
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        
    }

    @Override
    public String usage() {
        return Config.getCommandPrefix() + "save [location]";
    }

    @Override
    public String description() {
        return "Sets the location where recordings will be stored. Prints the current save location if called without args.";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e) {
        return;
    }
    
}
