package com.discorder;

import java.io.BufferedReader;

import javax.security.auth.login.LoginException;

import com.discorder.commands.CommandHandler;
import com.discorder.commands.audio.PauseCommand;
import com.discorder.commands.audio.RecordCommand;
import com.discorder.commands.audio.StopCommand;
import com.discorder.commands.misc.HelpCommand;
import com.discorder.commands.misc.JoinCommand;
import com.discorder.commands.misc.LeaveCommand;
import com.discorder.commands.settings.PrefixCommand;
import com.discorder.event.EventManager;
import com.discorder.listeners.EventListener;
import com.discorder.listeners.RecordEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
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
            // create bot instance
            JDA api = new JDABuilder(AccountType.BOT)
                            .setToken(CLIENT_TOKEN)
                            .addEventListener(new EventListener())
                            .buildBlocking();

            // register commands and their aliases
            registerCommands();

            EventManager.getInstance().addListener(new RecordEventListener());
        } catch (LoginException le) {
            logger.error("error logging in", le);
            System.exit(1);
        } catch (InterruptedException ie) {
            logger.error("thread interrupted during login, possibly due to VM shutdown", ie);
            System.exit(1);
        }
        
        ProgramState.setProgramState(ProgramState.State.STOPPED);
    }

    private static void registerCommands() {
        CommandHandler.commands.put("help", new HelpCommand());

        CommandHandler.commands.put("join", new JoinCommand());
        CommandHandler.commands.put("leave", new LeaveCommand());

        CommandHandler.commands.put("stop", new StopCommand());
        CommandHandler.commands.put("record", new RecordCommand());
        CommandHandler.commands.put("pause", new PauseCommand());

        CommandHandler.commands.put("prefix", new PrefixCommand());
    }
}
