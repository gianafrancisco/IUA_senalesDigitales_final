import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by francisco on 5/31/16.
 */
public class MainDSP {

    public static void main(String ...arg){

        File fileIn = new File("sample4.wav");
        try {
            AudioFileFormat audioFileFormat = AudioSystem.getAudioFileFormat(fileIn);

            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(fileIn);

            DataLine.Info dataLineInfoOut =
                    new DataLine.Info(
                            SourceDataLine.class,
                            audioFileFormat.getFormat());
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfoOut);

            ArrayList<Filter> filter = new ArrayList<Filter>();
            filter.add(new FIR());
            Runnable dsp = new DSP(audioInputStream,sourceDataLine, filter, audioFileFormat.getFormat());
            dsp.run();
        }catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
}
