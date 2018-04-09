/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nathan
 */
public final class Config {
    private final static Logger logger = LoggerFactory.getLogger(Config.class);
    private final static Preferences prefs = Preferences.userRoot().node(Config.class.getName());

    protected Config() {
        // if it's the first time, check and see if we have any config saved
        try {
            if (prefs.keys().length == 0) {
                logger.debug("no user-defined preferences stored, loading default values");
                loadDefaultPreferences();
            }
        } catch (BackingStoreException bse) {
            logger.warn("backing store unavailable while checking preferences", bse);
        }
    }

    public static Path getDefaultSaveDestination() {
        return Paths.get(System.getProperty("user.home"), "recordings");
    }

    public static void setDefaultSaveDestination(Path folder) {
        prefs.set("bot.default.savedir", folder.toString());
    }

    public static void setCommandPrefix(String prefix) {
        prefs.put("bot.command.prefix", prefix);
    }

    public static String getCommandPrefix() {
        return prefs.get("bot.command.prefix", "/");
    }

    public static String getDefaultBotName() {
        return prefs.get("bot.default.name", "DisCorder");
    }

    public static void setDefaultBotName(String name) {
        prefs.set("bot.defaultName", name);
    }

    public void restoreDefaults() {
        String nodeName = this.prefs.name();
        try {
            this.clearPreferences(nodeName);
        } catch (BackingStoreException bse) {
            logger.warn("backing store unavailable while trying to clear preferences", bse);
        }

        this.loadDefaultPreferences();
    }

    // recursively deletes preferences
    private void clearPreferences(String nodeName) throws BackingStoreException {
        Preferences node = prefs.node(nodeName);
        String[] childNodeNames = node.childrenNames();

        node.clear();
        for (String childNodeName : childNodeNames) {
            clearPreferences(childNodeName);
        }
    }

    private void loadDefaultPreferences() {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
            Properties defaults = new Properties();
            defaults.load(is);

            for (String key : defaults.stringPropertyNames()) {
                prefs.put(key, defaults.getProperty(key));
            }

            prefs.put("bot.default.name", "DisCorder");
            prefs.put("bot.default.savedir", Config.getDefaultSaveDestination());
        } catch (IOException ioe) {
            logger.error("unable to load default preferences, is config.properties missing?", ioe);
        }
    }

}
