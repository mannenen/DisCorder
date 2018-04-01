package com.discorder;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import javax.security.auth.login.LoginException;
import javax.sound.sampled.AudioFormat;

import com.discorder.commands.CommandHandler;
import com.discorder.commands.audio.RecordCommand;
import com.discorder.commands.audio.StopCommand;
import com.discorder.commands.misc.HelpCommand;
import com.discorder.commands.misc.JoinCommand;
import com.discorder.commands.misc.LeaveCommand;
import com.discorder.commands.settings.PrefixCommand;
import com.discorder.commands.settings.SaveLocationCommand;
import com.discorder.commands.settings.VolumeCommand;
import com.discorder.listeners.AudioReceiveListener;
import com.discorder.listeners.AudioSendListener;
import com.discorder.listeners.EventListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DisCorder {
    private final static Logger logger = LoggerFactory.getLogger(DisCorder.class);
    private final static String CLIENT_TOKEN = readToken("token");
    
    private static String readToken(String fileName) {
        String token = "";
        
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            token = br.readLine().trim();
        } catch (IOException ioe) {
            logger.error("unable to read/find client auth token, cannot continue", ioe);
            System.exit(1);
        }
        
        return token;
    }

    public static void main(String[] args) {
        try {
            //create bot instance
            JDA api = new JDABuilder(AccountType.BOT)
                            .setToken(CLIENT_TOKEN)
                            .addEventListener(new EventListener())
                            .buildBlocking();
        } catch (LoginException le) {
            logger.error("error logging in", le);
            System.exit(1);
        } catch (InterruptedException ie) {
            logger.error("thread interrupted during login", ie);
            System.exit(1);
        }

        //register commands and their aliases
        CommandHandler.commands.put("help", new HelpCommand());

        CommandHandler.commands.put("join", new JoinCommand());
        CommandHandler.commands.put("leave", new LeaveCommand());

        CommandHandler.commands.put("stop", new StopCommand());
        CommandHandler.commands.put("record", new RecordCommand());

        CommandHandler.commands.put("prefix", new PrefixCommand());
        CommandHandler.commands.put("volume", new VolumeCommand());
        CommandHandler.commands.put("savelocation", new SaveLocationCommand());

    }

    //UTILITY FUNCTIONS

    // send a message to a TextChannel
    @Deprecated
    public static void sendMessage(TextChannel tc, String message) {
        if (tc.canTalk()) {
            tc.sendMessage(message).queue(); 
        } else {
            logger.warn("tried to message channel I'm not allowed into");
        }
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
