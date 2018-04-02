package com.discorder.commands.misc;

import java.awt.Color;

import com.discorder.commands.Command;
import com.discorder.commands.CommandHandler;
import com.discorder.Config;
import java.util.Set;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;


public class HelpCommand implements Command {

    @Override
    public Boolean called(String[] args, GuildMessageReceivedEvent e){
        return true;
    }

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        TextChannel tc = e.getChannel();
        if (args.length != 0) {
            tc.sendMessage(usage()).queue();
            return;
        }

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("Nathan Mann", "https://github.com/mannenen/DisCorder/", e.getJDA().getSelfUser().getAvatarUrl());
        embed.setColor(Color.GREEN);
        embed.setTitle("Based on ajm1996/DiscordEcho");
        embed.setDescription("Records channel audio");
        embed.setThumbnail("http://www.freeiconspng.com/uploads/information-icon-5.png");
        embed.setFooter("Replace brackets [] with item specified. Vertical bar | means 'or', either side of bar is valid choice.", "https://github.com/mannenen/DisCorder/");
        embed.addBlankField(false);

        Set<String> cmds = CommandHandler.commands.keySet();
        cmds.stream().filter(cmd -> !(cmd.equalsIgnoreCase("help"))).forEachOrdered(cmd -> {
            Command command = CommandHandler.commands.get(cmd);
            String prefix = Config.getCommandPrefix();

            embed.addField(command.usage(), command.description(), true);
        });
        
        e.getChannel().sendMessage(embed.build()).queue();
    }

    @Override
    public String usage() {
        return Config.getCommandPrefix() + "help";
    }

    @Override
    public String description() {
        return "Shows all commands and their usages";
    }

    @Override
    public void executed(boolean success, GuildMessageReceivedEvent e){
        return;
    }
}
