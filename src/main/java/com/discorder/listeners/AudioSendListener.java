/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.discorder.listeners;

import java.nio.ByteBuffer;
import java.util.Date;
import net.dv8tion.jda.core.audio.AudioSendHandler;

/**
 *
 * @author nathan
 */
public class AudioSendListener implements AudioSendHandler {
    private Date startTime;
    
    public AudioSendListener() {
        startTime = new Date();
    }
    
    @Override
    public boolean canProvide() {
        Date now = new Date();
        return (((now.getTime() - startTime.getTime()) / 1000) < 2);
    }

    @Override
    public byte[] provide20MsAudio() {
        return ByteBuffer.allocate(Integer.BYTES).putInt(0).array();
    }

    @Override
    public boolean isOpus() {
        return false;
    }
    
}
