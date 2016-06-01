import javax.sound.sampled.*;

/**
 * Created by francisco on 5/28/16.
 */
public class DataLineMain {

    public static void main (String ...arg) throws LineUnavailableException {

        DataLine.Info info = new DataLine.Info(TargetDataLine.class,getAudioFormat());

        for(Line.Info i: AudioSystem.getTargetLineInfo(info)){
            System.out.println(i);
        };


        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info info1: mixerInfos){
            Mixer m = AudioSystem.getMixer(info1);
            Line.Info[] lineInfos = m.getSourceLineInfo();
            for (Line.Info lineInfo:lineInfos){
                //System.out.println (info1.getName()+"---"+lineInfo);
                //Line line = m.getLine(lineInfo);
                //System.out.println("\t-----"+line);
            }
            lineInfos = m.getTargetLineInfo();
            for (Line.Info lineInfo:lineInfos){
                System.out.println (m+"---"+lineInfo);
                Line line = m.getLine(lineInfo);
                System.out.println("\t-----"+line);

            }

        }


    }

    private static AudioFormat getAudioFormat(){
        float sampleRate = 8000.0F;
        //8000,11025,16000,22050,44100
        int sampleSizeInBits = 8;
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
