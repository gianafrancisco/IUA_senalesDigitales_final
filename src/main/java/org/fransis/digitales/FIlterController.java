package org.fransis.digitales;

import org.fransis.digitales.core.DSP;
import org.fransis.digitales.core.FIR;
import org.fransis.digitales.core.Filter;
import org.omg.CORBA.DoubleHolder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RunAs;
import javax.sound.sampled.*;
import javax.ws.rs.POST;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by francisco on 6/1/16.
 */
@RestController
@RequestMapping("linein")
public class FIlterController {

    private Thread dsp;
    private List<Filter> filters = new ArrayList<>();

    @RequestMapping(method = RequestMethod.POST, path = "/signal")
    public HttpEntity<Void> signal(){
        if(dsp != null) dsp.stop();
        dsp = new Thread(runFilter(filters));
        dsp.start();
        return (ResponseEntity.status(HttpStatus.CREATED)).build();
    }

    @RequestMapping(method = RequestMethod.DELETE,path = "/signal" )
    public HttpEntity<Void> signalDelete(){
        if(dsp != null){ dsp.stop(); dsp = null;}
        return (ResponseEntity.status(HttpStatus.OK)).build();
    }

    @RequestMapping(method = RequestMethod.POST,path = "/fir")
    public HttpEntity<Void> fir(@RequestBody List<Double> c){
        double d[] = new double[c.size()];
        for(int i=0;i<c.size();i++){
            d[i] = c.get(i);
        }
        //if(dsp != null) dsp.stop();
        filters.add(new FIR(d));
        return (ResponseEntity.status(HttpStatus.CREATED)).build();
    }

    @RequestMapping(method = RequestMethod.GET,path = "/fir")
    public HttpEntity<List<Filter>> get(){
        return (ResponseEntity.status(HttpStatus.OK)).body(filters);
    }

    @RequestMapping(method = RequestMethod.DELETE,path = "/fir")
    public HttpEntity<Void> delete(){
        if(filters.size() == 0) return (ResponseEntity.status(HttpStatus.NOT_FOUND)).build();
        filters.remove(filters.size() - 1);
        return (ResponseEntity.status(HttpStatus.OK)).build();
    }

    private Runnable runFilter(List<Filter> f){

        SourceDataLine sourceDataLine = null;
        AudioFormat audioFormat = null;
        AudioInputStream audioInputStream  = null;

        Mixer.Info[] info = AudioSystem.getMixerInfo();
        Mixer.Info usbCard = null;

        int m = 0;

        for(Mixer.Info i: info){
            System.out.println(m+" "+i.getDescription());
            if(i.getDescription().contains("Direct Audio Device: USB Audio Device")){
                usbCard = i;
            }
            m++;
        }

        File fileIn = new File("sample.wav");
        try {
            AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(fileIn);
            //audioFormat = audioFileFormat.getFormat();
            audioFormat = getAudioFormat();

            //audioInputStream =
            //        AudioSystem.getAudioInputStream(fileIn);

            TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFormat, usbCard);
            targetDataLine.open();
            targetDataLine.start();
            audioInputStream = new AudioInputStream(targetDataLine);
            //audioInputStream = new AudioInputStream(AudioSystem.getTargetDataLine(audioFormat, info[0]));

            DataLine.Info dataLineInfoOut =
                    new DataLine.Info(
                            SourceDataLine.class,
                            audioFileFormat.getFormat());
            //sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfoOut);
            sourceDataLine = AudioSystem.getSourceDataLine(audioFormat, usbCard);

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
