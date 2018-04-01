package com.discordecho;

import static java.lang.Thread.sleep;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.security.auth.login.LoginException;
import javax.sound.sampled.AudioFormat;

import com.discordecho.commands.CommandHandler;
import com.discordecho.commands.audio.RecordCommand;
import com.discordecho.commands.audio.StopCommand;
import com.discordecho.commands.misc.HelpCommand;
import com.discordecho.commands.misc.JoinCommand;
import com.discordecho.commands.misc.LeaveCommand;
import com.discordecho.commands.settings.AutoJoinCommand;
import com.discordecho.commands.settings.AutoLeaveCommand;
import com.discordecho.commands.settings.PrefixCommand;
import com.discordecho.commands.settings.SaveLocationCommand;
import com.discordecho.commands.settings.VolumeCommand;
import com.discordecho.configuration.ServerSettings;
import com.discordecho.listeners.AudioReceiveListener;
import com.discordecho.listeners.AudioSendListener;
import com.discordecho.listeners.EventListener;
import com.google.gson.Gson;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;

public class DiscordEcho {
    //contains the id of every guild that we are connected to and their corresponding ServerSettings object
    public static HashMap<String, ServerSettings> serverSettings = new HashMap<>();

    public static void main(String[] args) {
        try {
            //read the bot's token from a file name "token" in the main directory
            FileReader fr = new FileReader("shark_token");
            BufferedReader br = new BufferedReader(fr);
            String token = br.readLine();

            //create bot instance
            JDA api = new JDABuilder(AccountType.BOT).setToken(token).addEventListener(new EventListener()).buildBlocking();
        } catch (LoginException e) {
            //If anything goes wrong in terms of authentication, this is the exception that will represent it
            e.printStackTrace();
        } catch (InterruptedException e) {
            //Due to the fact that buildBlocking is a blocking method, one which waits until JDA is fully loaded,
            // the waiting can be interrupted. This is the exception that would fire in that situation.
            //As a note: in this extremely simplified example this will never occur. In fact, this will never occur unless
            // you use buildBlocking in a thread that has the possibility of being interrupted (async thread usage and interrupts)
            e.printStackTrace();
        } catch (RateLimitedException e) {
            //The login process is one which can be ratelimited. If you attempt to login in multiple times, in rapid succession
            // (multiple times a second), you would hit the ratelimit, and would see this exception.
            //As a note: It is highly unlikely that you will ever see the exception here due to how infrequent login is.
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //register commands and their aliases
        CommandHandler.commands.put("help", new HelpCommand());

        CommandHandler.commands.put("join", new JoinCommand());
        CommandHandler.commands.put("leave", new LeaveCommand());

        CommandHandler.commands.put("stop", new StopCommand());
        CommandHandler.commands.put("record", new RecordCommand());

        CommandHandler.commands.put("autojoin", new AutoJoinCommand());
        CommandHandler.commands.put("autoleave", new AutoLeaveCommand());

        CommandHandler.commands.put("prefix", new PrefixCommand());
        CommandHandler.commands.put("volume", new VolumeCommand());
        CommandHandler.commands.put("savelocation", new SaveLocationCommand());

    }

    //UTILITY FUNCTIONS

    // send a message to a TextChannel
    public static void sendMessage(TextChannel tc, String message) {

    }

    @Deprecated
    public static void writeToFile(Guild guild) {

    }

    @Deprecated
    public static void joinVoiceChannel(VoiceChannel channel, boolean b) {

    }

    @Deprecated
    public static void leaveVoiceChannel(VoiceChannel channel) {

    }

    @Deprecated
    public static void writeSettingsJson() {
        
    }

    //generate a random string of 13 length with a namespace of around 2e23
    public static String getPJSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 13) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        
        return salt.toString();
    }

    //encode the passed array of PCM (uncompressed) audio to mp3 audio data
    public static byte[] encodePcmToMp3(byte[] pcm) {
        LameEncoder encoder = new LameEncoder(new AudioFormat(48000.0f, 16, 2, true, true), 128, MPEGMode.STEREO,
                Lame.QUALITY_HIGHEST, false);
        ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
        byte[] buffer = new byte[encoder.getPCMBufferSize()];

        int bytesToTransfer = Math.min(buffer.length, pcm.length);
        int bytesWritten;
        int currentPcmPosition = 0;
        while (0 < (bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer))) {
            currentPcmPosition += bytesToTransfer;
            bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);

            mp3.write(buffer, 0, bytesWritten);
        }

        encoder.close();

        return mp3.toByteArray();
    }

    //kill off the audio handlers and clear their memory for the given guild
    public static void killAudioHandlers(Guild g) {
        AudioReceiveListener ah = (AudioReceiveListener) g.getAudioManager().getReceiveHandler();
        if (ah != null) {
            ah.canReceive = false;
            ah.compVoiceData = null;
            g.getAudioManager().setReceivingHandler(null);
        }

        AudioSendListener sh = (AudioSendListener) g.getAudioManager().getSendingHandler();
        if (sh != null) {
            sh.canProvide = false;
            sh.voiceData = null;
            g.getAudioManager().setSendingHandler(null);
        }

        System.out.println("Destroyed audio handlers for " + g.getName());
        System.gc();
    }
}
