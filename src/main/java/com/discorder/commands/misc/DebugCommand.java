/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder.commands.misc;

import com.discorder.Config;
import com.discorder.commands.Command;
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

/**
 *
 * @author nathan
 */
public class DebugCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e) {
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length == 0) {
            e.getChannel().sendMessage(this.plainDebugMessage()).queue();
            return;
        }
        
        List<String> argList = Arrays.asList(args);
        if (argList.contains("logpcm")) {
            e.getChannel().sendMessage(this.logPCM()).queue();
        }
        
    }

    @Override
    public String usage() {
        return Config.getCommandPrefix() + "debug [logpcm]";
    }

    @Override
    public String description() {
        return "Prints debug information in the current text channel.";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e) {
        
    }
    
    private String plainDebugMessage() {
        return "";
    }
    
    private String logPCM() {
        return "";
    }
    
}
