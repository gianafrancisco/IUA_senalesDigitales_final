package org.fransis.digitales;

import org.fransis.digitales.core.DSP;
import org.fransis.digitales.core.FIR;
import org.fransis.digitales.core.Filter;
import org.springframework.beans.factory.annotation.Value;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by francisco on 6/3/16.
 */
public class FilterApp {


    private Thread dsp;
    private List<Filter> filters = new ArrayList<>();
    private int inputIndex;
    private int outputIndex;
    private SourceDataLine sourceDataLine = null;
    private TargetDataLine targetDataLine = null;


    public static void main(String ...arg){

        if(arg.length == 0){System.exit(0);};

        List<Filter> filters = new ArrayList<>();
        FilterApp app = new FilterApp();

        double coef[] = new double[arg.length-2];
        int i = 2;
        for(; i< arg.length;i++){
            coef[i-2]=Double.parseDouble(arg[i]);
        }
        if(i == 2) {
            filters.add(new FIR());
        }else {
            filters.add(new FIR(coef));
        }
        app.runFilter(Integer.parseInt(arg[0]),Integer.parseInt(arg[1]),filters).run();

    }


    private Runnable runFilter(int input, int output, List<Filter> f){


        AudioFormat audioFormat = null;
        AudioInputStream audioInputStream  = null;

        Mixer.Info[] info = AudioSystem.getMixerInfo();
        Mixer.Info usbCard = null;
        Mixer.Info usbCardOut = null;

        int m = 0;

        for(Mixer.Info i: info){
            System.out.println(m+" "+i.getDescription());
            if(i.getDescription().contains("Direct Audio Device: USB Audio Device")){
                usbCard = i;
            }
            m++;
        }

        usbCard = info[input];
        usbCardOut = info[output];

        File fileIn = new File("sample.wav");
        try {
            AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(fileIn);
            //audioFormat = audioFileFormat.getFormat();
            audioFormat = getAudioFormat();

            //audioInputStream =
            //        AudioSystem.getAudioInputStream(fileIn);

            targetDataLine = AudioSystem.getTargetDataLine(audioFormat, usbCard);
            targetDataLine.open();
            targetDataLine.start();
            audioInputStream = new AudioInputStream(targetDataLine);
            //audioInputStream = new AudioInputStream(AudioSystem.getTargetDataLine(audioFormat, info[0]));

            DataLine.Info dataLineInfoOut =
                    new DataLine.Info(
                            SourceDataLine.class,
                            audioFileFormat.getFormat());
            //sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfoOut);
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat, usbCardOut);

            Runnable dsp = new DSP(audioInputStream,sourceDataLine, f, audioFormat);
            return dsp;
        }catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        return new Runnable() {
            @Override
            public void run() {

            }
        };
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
