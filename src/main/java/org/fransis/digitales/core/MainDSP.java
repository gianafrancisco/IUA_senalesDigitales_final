package org.fransis.digitales.core;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by francisco on 5/31/16.
 */
public class MainDSP {

    public static void main(String ...arg){

        SourceDataLine sourceDataLine = null;

        Mixer.Info[] info = AudioSystem.getMixerInfo();

        int m = 0;
        for(Mixer.Info i: info){
            System.out.println(m+" "+i.getDescription());



            m++;
        }


        //System.exit(0);

        File fileIn = new File("sample.wav");
        try {
            AudioFormat audioFileFormat = AudioSystem.getAudioFileFormat(fileIn).getFormat();
            audioFileFormat = getAudioFormat();

            //AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
            TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFileFormat, info[4]);
            AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);

            DataLine.Info dataLineInfoOut =
                    new DataLine.Info(
                            SourceDataLine.class,
                            audioFileFormat);
            //SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfoOut);
            //sourceDataLine = AudioSystem.getSourceDataLine(audioFileFormat.getFormat(), info[4]);
            sourceDataLine = AudioSystem.getSourceDataLine(audioFileFormat, info[4]);


            ArrayList<Filter> filter = new ArrayList<Filter>();
            //filter.add(new FIR(Filter.LOW_PASS_1K_44100));
            //Runnable dsp = new DSP(audioInputStream,sourceDataLine, filter, audioFileFormat);
            //dsp.run();


            try {
                int bytesPerFrame =
                        audioFileFormat.getFrameSize();
                if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                    bytesPerFrame = 1;
                }
                int numBytes = 1024 * bytesPerFrame;
                byte[] audioBytes = new byte[numBytes];
                byte[] audioBytesOut = new byte[numBytes];
                double[] audioBytesDouble = new double[numBytes/2];
                try {

                    sourceDataLine.open(audioFileFormat);
                    sourceDataLine.start();
                    targetDataLine.open();
                    targetDataLine.start();
                    while (targetDataLine.read(audioBytes,0,audioBytes.length) != -1) {
                        for(int n = 0, i=0; n < audioBytes.length; n+=2,i++){
                            audioBytesDouble[i] = ((audioBytes[i * 2] & 0xFF) | (audioBytes[i * 2 + 1] << 8)) / 32768.0;
                            for(Filter f: filter){
                                audioBytesDouble[i] = f.apply(audioBytesDouble[i]);
                            }
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
                }finally {
                    sourceDataLine.drain();
                    sourceDataLine.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                sourceDataLine.drain();
                sourceDataLine.close();
            }




        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private static AudioFormat getAudioFormat(){
        float sampleRate = 44100.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        //8,16
        int channels = 1;
        //1,2
        boolean signed = true;
        //true,false
        boolean bigEndian = false;
        //true,false
        return new AudioFormat(sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }

}
