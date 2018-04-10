package com.discorder.listeners;


import java.io.ByteArrayOutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.sourceforge.lame.lowlevel.LameEncoder;
import net.sourceforge.lame.mp3.Lame;
import net.sourceforge.lame.mp3.MPEGMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AudioReceiveListener implements AudioReceiveHandler
{
    private final static Logger logger = LoggerFactory.getLogger(AudioReceiveListener.class);
    private final ConcurrentLinkedQueue<byte[]> queue;

    public AudioReceiveListener(ConcurrentLinkedQueue<byte[]> encodeQueue) {
        this.queue = encodeQueue;
    }
    
    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public boolean canReceiveUser() {
        return false;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        byte[] pcmData = combinedAudio.getAudioData(1.0);
            
        //byte[] mp3Data = this.encodePcmToMp3(pcmData);

        //this.queue.add(mp3Data);
        
        this.queue.add(pcmData);
    }

    @Override
    public void handleUserAudio(UserAudio userAudio) {
        return;
    }
    
    private byte[] encodePcmToMp3(byte[] pcm) {
        LameEncoder encoder = new LameEncoder(AudioReceiveHandler.OUTPUT_FORMAT, 128, MPEGMode.STEREO,
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
}