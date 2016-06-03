package org.fransis.digitales.core;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Created by francisco on 26/05/2016.
 */
public class ReadFileAudio {

    private SourceDataLine sourceDataLine;
    private String filename;
    private DataLine.Info dataLineInfo;
    private AudioFileFormat audioFileFormat;
    private FIR fir = null;

    public void setFir(FIR fir) {
        this.fir = fir;
    }

    public ReadFileAudio(String filename) {
        this.filename = filename;
    }

    public static void main(String ...arg){
        ReadFileAudio audio = new ReadFileAudio("sample3.wav");
        audio.setFir(new FIR(Filter.HIGH_PASS_10K_44100));
        audio.play();
    }

    public void play(){
        File fileIn = new File(filename);
        try {

            AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(fileIn);

            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(fileIn);
            int bytesPerFrame =
                    audioInputStream.getFormat().getFrameSize();
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                bytesPerFrame = 1;
            }
            int numBytes = 1024 * bytesPerFrame;
            byte[] audioBytes = new byte[numBytes];
            byte[] audioBytesOut = new byte[numBytes];
            double[] audioBytesDouble = new double[numBytes/2];
            try {
                DataLine.Info dataLineInfoOut =
                        new DataLine.Info(
                                SourceDataLine.class,
                                audioFileFormat.getFormat());
                sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfoOut);
                //sourceDataLine = (SourceDataLine) AudioSystem.getLine(Port.Info.LINE_OUT);

                sourceDataLine.open(audioFileFormat.getFormat());
                sourceDataLine.start();

                while (audioInputStream.read(audioBytes) != -1) {
                    for(int n = 0, i=0; n < audioBytes.length; n+=2,i++){
                        audioBytesDouble[i] = ((audioBytes[i * 2] & 0xFF) | (audioBytes[i * 2 + 1] << 8)) / 32768.0;
                        audioBytesDouble[i] = fir.apply(audioBytesDouble[i]);
                        audioBytesDouble[i] *= 32768.0;
                        audioBytesOut[n] = (byte)((short)audioBytesDouble[i] & 0x00FF);
                        audioBytesOut[n + 1] = (byte)(((short)audioBytesDouble[i] & 0xFF00)>>8);

                    }
                    sourceDataLine.write(audioBytesOut,0,audioBytesOut.length);

                }
                sourceDataLine.drain();
                sourceDataLine.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
