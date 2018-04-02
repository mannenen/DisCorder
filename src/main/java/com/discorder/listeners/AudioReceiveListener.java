package com.discorder.listeners;


import com.discorder.Config;
import com.discorder.SampleContainer;
import com.discorder.WriteAudioTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import javax.sound.sampled.AudioFormat;
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
    private boolean record;
    private final ArrayBlockingQueue<SampleContainer> queue;
    private final WriteAudioTask task;

    public AudioReceiveListener(ArrayBlockingQueue<SampleContainer> encodeQueue) {
        this.record = false;
        this.queue = encodeQueue;
        
        String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".mp3";
        Path outputPath = Paths.get(Config.getDefaultSaveDestination().toString(), fileName);
        this.task = new WriteAudioTask(outputPath, this.queue);
    }
    
    public void start() {
        logger.debug("start recording signal");
        this.record = true;
        
        try {
            this.task.prepareFile();
        } catch (IOException ioe) {
            logger.error("unable to prepare file for writing, stopping recording", ioe);
            this.record = false;
        }
    }
    
    public void stop() {
        logger.debug("stop recording signal");
        this.record = false;
        
        try {
            this.task.finishFile();
        } catch (IOException ioe) {
            logger.error("error while closing file channel", ioe);
        }
        
    }

    @Override
    public boolean canReceiveCombined() {
        return this.record;
    }

    @Override
    public boolean canReceiveUser() {
        return false;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        byte[] pcmData = combinedAudio.getAudioData(1.0);
        byte[] mp3Data = this.encodePcmToMp3(pcmData);
        
        try {
            this.queue.put(new SampleContainer(mp3Data));
        } catch (InterruptedException ex) {
            logger.info("received thread interrupt while adding sample to worker queue");
        }
    }

    @Override
    public void handleUserAudio(UserAudio userAudio) {
        return;
    }
    
    private byte[] encodePcmToMp3(byte[] pcm) {
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
}