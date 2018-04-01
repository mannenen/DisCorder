package com.discordecho.listeners;

import static java.lang.Thread.sleep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;

import com.discordecho.DiscordEcho;
import com.discordecho.commands.CommandHandler;
import com.discordecho.configuration.ServerSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;


public class EventListener extends ListenerAdapter {


    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        DiscordEcho.serverSettings.put(e.getGuild().getId(), new ServerSettings(e.getGuild()));
        DiscordEcho.writeSettingsJson();
        System.out.format("Joined new server '%s', connected to %s guilds\n", e.getGuild().getName(), e.getJDA().getGuilds().size());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        DiscordEcho.serverSettings.remove(e.getGuild().getId());
        DiscordEcho.writeSettingsJson();
        System.out.format("Left server '%s', connected to %s guilds\n", e.getGuild().getName(), e.getJDA().getGuilds().size());
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
        if(e.getMember() == null || e.getMember().getUser() == null || e.getMember().getUser().isBot())
            return;

        VoiceChannel biggestChannel = DiscordEcho.biggestChannel(e.getGuild().getVoiceChannels());

        if (e.getGuild().getAudioManager().isConnected()) {

            int newSize = DiscordEcho.voiceChannelSize(e.getChannelJoined());
            int botSize = DiscordEcho.voiceChannelSize(e.getGuild().getAudioManager().getConnectedChannel());
            ServerSettings settings  = DiscordEcho.serverSettings.get(e.getGuild().getId());
            int min = settings.autoJoinSettings.get(e.getChannelJoined().getId());
            
            if (newSize >= min && botSize < newSize) {  //check for tie with old server
                if (DiscordEcho.serverSettings.get(e.getGuild().getId()).autoSave)
                    DiscordEcho.writeToFile(e.getGuild());  //write data from voice channel it is leaving

                DiscordEcho.joinVoiceChannel(e.getChannelJoined(), false);
            }

        } else {
            if (biggestChannel != null) {
                DiscordEcho.joinVoiceChannel(e.getChannelJoined(), false);
            }
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent e) {
        if(e.getMember() == null || e.getMember().getUser() == null || e.getMember().getUser().isBot())
            return;

        int min = DiscordEcho.serverSettings.get(e.getGuild().getId()).autoLeaveSettings.get(e.getChannelLeft().getId());
        int size = DiscordEcho.voiceChannelSize(e.getChannelLeft());

        if (size <= min && e.getGuild().getAudioManager().getConnectedChannel() == e.getChannelLeft()) {

            if (DiscordEcho.serverSettings.get(e.getGuild().getId()).autoSave)
                DiscordEcho.writeToFile(e.getGuild());  //write data from voice channel it is leaving

            DiscordEcho.leaveVoiceChannel(e.getGuild().getAudioManager().getConnectedChannel());

            VoiceChannel biggest = DiscordEcho.biggestChannel(e.getGuild().getVoiceChannels());
            if (biggest != null) {
                DiscordEcho.joinVoiceChannel(biggest, false);
            }
        }
    }

    @Override
    public void onGuildVoiceMove (GuildVoiceMoveEvent e) {
        if(e.getMember() == null || e.getMember().getUser() == null || e.getMember().getUser().isBot())
            return;

        //Check if bot needs to join newly joined channel
        VoiceChannel biggestChannel = DiscordEcho.biggestChannel(e.getGuild().getVoiceChannels());

        if (e.getGuild().getAudioManager().isConnected()) {

            int newSize = DiscordEcho.voiceChannelSize(e.getChannelJoined());
            int botSize = DiscordEcho.voiceChannelSize(e.getGuild().getAudioManager().getConnectedChannel());
            ServerSettings settings  = DiscordEcho.serverSettings.get(e.getGuild().getId());
            int min = settings.autoJoinSettings.get(e.getChannelJoined().getId());

            if (newSize >= min && botSize < newSize) {  //check for tie with old server
                if (DiscordEcho.serverSettings.get(e.getGuild().getId()).autoSave)
                    DiscordEcho.writeToFile(e.getGuild());  //write data from voice channel it is leaving

                DiscordEcho.joinVoiceChannel(e.getChannelJoined(), false);
            }

        } else {
            if (biggestChannel != null) {
                DiscordEcho.joinVoiceChannel(biggestChannel, false);
            }
        }

        //Check if bot needs to leave old channel
        int min = DiscordEcho.serverSettings.get(e.getGuild().getId()).autoLeaveSettings.get(e.getChannelLeft().getId());
        int size = DiscordEcho.voiceChannelSize(e.getChannelLeft());

        if (size <= min && e.getGuild().getAudioManager().getConnectedChannel() == e.getChannelLeft()) {

            if (DiscordEcho.serverSettings.get(e.getGuild().getId()).autoSave)
                DiscordEcho.writeToFile(e.getGuild());  //write data from voice channel it is leaving

            DiscordEcho.leaveVoiceChannel(e.getGuild().getAudioManager().getConnectedChannel());;

            VoiceChannel biggest = DiscordEcho.biggestChannel(e.getGuild().getVoiceChannels());
            if (biggest != null) {
                DiscordEcho.joinVoiceChannel(e.getChannelJoined(), false);
            }
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e){
        if(e.getMember() == null || e.getMember().getUser() == null || e.getMember().getUser().isBot())
            return;

        String prefix = DiscordEcho.serverSettings.get(e.getGuild().getId()).prefix;
        //force help to always work with "!" prefix
        if (e.getMessage().getContent().startsWith(prefix) || e.getMessage().getContent().startsWith("!help")) {
            CommandHandler.handleCommand(CommandHandler.parser.parse(e.getMessage().getContent().toLowerCase(), e));
        }
    }

    @Override
    public void onReady(ReadyEvent e){
        try {
            System.out.format("ONLINE: Connected to %s guilds!\n", e.getJDA().getGuilds().size(), e.getJDA().getVoiceChannels().size());

            Gson gson = new Gson();

            FileReader fileReader = new FileReader("settings.json");
            BufferedReader buffered = new BufferedReader(fileReader);

            Type type = new TypeToken<HashMap<String, ServerSettings>>(){}.getType();

            DiscordEcho.serverSettings = gson.fromJson(fileReader, type);

            if (DiscordEcho.serverSettings == null)
                DiscordEcho.serverSettings = new HashMap<>();

            buffered.close();
            fileReader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        for (Guild g : e.getJDA().getGuilds()) {    //validate settings files
            if (!DiscordEcho.serverSettings.containsKey(g.getId())) {
                DiscordEcho.serverSettings.put(g.getId(), new ServerSettings(g));
                DiscordEcho.writeSettingsJson();
            }
        }

        File dir = new File("/var/www/html/");
        if (!dir.exists())
            dir = new File("recordings/");

        for (File f : dir.listFiles()) {
            if (f.getName().substring(f.getName().lastIndexOf('.'), f.getName().length()).equals(".mp3")) {
                new Thread (() -> {

                    try { sleep(1000 * 60 * 30); } catch (Exception ex){}

                    f.delete();
                    System.out.println("\tDeleting file " + f.getName() + "...");

                }).start();
            }
        }

        //check for servers to join
        for (Guild g : e.getJDA().getGuilds()) {
            VoiceChannel biggest = DiscordEcho.biggestChannel(g.getVoiceChannels());
            if (biggest != null) {
                DiscordEcho.joinVoiceChannel(DiscordEcho.biggestChannel(g.getVoiceChannels()), false);
            }
        }
    }
}
