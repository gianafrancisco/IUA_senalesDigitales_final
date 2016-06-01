import javax.sound.sampled.*;
import java.util.List;

/**
 * Created by francisco on 5/31/16.
 */
public class DSP implements Runnable {

    private AudioInputStream audioInputStream;
    private SourceDataLine sourceDataLine;
    private List<Filter> filter;
    private AudioFormat format;

    public DSP(AudioInputStream audioInputStream, SourceDataLine sourceDataLine, List<Filter> filter, AudioFormat format) {
        this.audioInputStream = audioInputStream;
        this.sourceDataLine = sourceDataLine;
        this.filter = filter;
        this.format = format;
    }

    public void run() {
        try {
            int bytesPerFrame =
                    format.getFrameSize();
            if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                bytesPerFrame = 1;
            }
            int numBytes = 1024 * bytesPerFrame;
            byte[] audioBytes = new byte[numBytes];
            byte[] audioBytesOut = new byte[numBytes];
            double[] audioBytesDouble = new double[numBytes/2];
            try {

                sourceDataLine.open(format);
                sourceDataLine.start();

                while (audioInputStream.read(audioBytes) != -1) {
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
    }

    public void addFilter(Filter f){
        this.filter.add(f);
    }

}