package no.nordicsemi.android.nrfthingy.sound;

import android.content.Context;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import no.nordicsemi.android.thingylib.decoder.ADPCMDecoder;

/**
 * Abstract class that handles all the communication with the ADPCMD decoder.
 *
 * The event detection method needs to implemented to complete the functionality of the class,
 * the method is provided with a decoded 16-bit PCM byte array to process the data, if an event is
 * detected it returns <code>true<code/> or if no event is detected it returns <code>false<code/>.
 *
 * After a sound recording file is retrieved from an instance of this class, this instance can not
 * accept any new data for decoding and a fresh instance has to be created for further decoding.
 * However, the final resulting sound file can still be retrieved again from a closed instance by
 * calling the <code>getSavedSoundFile()</code> method on the closed instance.
 * */

public abstract class BaseSoundProcessor {
    private ADPCMDecoder decoder;
    private DecoderListener decoderListener;
    private SoundProcessorListener soundListener;
    private File soundFile;
    private final boolean saveFile;
    private boolean processorClosed;
    private ByteBuffer byteBuffer;

    // sample rate is 16000hz, each frame is 512 bytes, so we sample 16 frames for a sound
    // event check every 0.5s
    public static int SAMPLE_RATE = 16000;
    public static int FRAME_SIZE = 131;
    public static int FRAME_SAMPLES = 16;

    /**
     * Context has to be provided as the ADPCMD decoder requires it, while the save file
     * option is for indicating that all of the decoded data should be saved to file on system.
     *
     * @param context - system context
     *
     * @param saveFile - true if all decoded audio should be saved to a .WAV file
     */

    public BaseSoundProcessor(Context context, boolean saveFile) {
        this.saveFile = saveFile;
        this.processorClosed = false;
        this.decoder = new ADPCMDecoder(context, saveFile);
        this.decoderListener = new DecoderListener(this);
        this.decoder.setListener(decoderListener);
        this.byteBuffer = ByteBuffer.allocate(FRAME_SIZE * FRAME_SAMPLES);
        this.soundListener = null;
    }

    /**
     * Method that needs to be completed to indicate an event, if an event is detected the method
     * should return <code>true<code/>, else <cooe>false<code/>.
     *
     * If this method returns true, them the soundlistener that is registered to the class will
     * have its <code>onSoundEvent()<code/> method invoked, if the listener is set.
     *
     * @param data - 16-bit byte stream of PCM data
     *
     * @return <code>true<code/> if an event was detected,<code>false<code/> otherwise
     */

    public abstract boolean eventDetection(final byte[] data);

    /**
     * Used to implement a listener for this class
     */

    public interface SoundProcessorListener {
        public void onSoundEvent(byte[] data);
    }

    private class DecoderListener implements ADPCMDecoder.DecoderListener {

        private BaseSoundProcessor soundProcessor;

        public DecoderListener(BaseSoundProcessor soundProcessor) {
            this.soundProcessor = soundProcessor;
        }

        @Override
        public void onFrameDecoded(byte[] pcm, int frameNumber) {
            int bufferFrame = frameNumber % soundProcessor.FRAME_SAMPLES;
            //modulo check for the number of frames we have
            if (bufferFrame == 0) {
                // when frame is 0, buffer is empty so pass
                if (frameNumber != 0) {

                    if (soundProcessor.eventDetection(this.soundProcessor.getBuffer().array())) {

                        if (this.soundProcessor != null) {
                            this.soundProcessor.soundListener.onSoundEvent(pcm);
                        } else {
                            throw new IllegalStateException("sound event was detected but no soundListener is registered");
                        }

                    }
                    this.soundProcessor.getBuffer().clear();
                }

            }
            soundProcessor.getBuffer().put(pcm, bufferFrame * soundProcessor.FRAME_SIZE, pcm.length);

        }
    }

    public void addPacket(final byte[] packet) throws IllegalStateException {
        this.decoder.add(packet);
    }

    /**
     * Closes this BaseSoundProcessor instance from further use and returns the all of the decoded
     * audio that was supplied to the class as a .WAV file.
     *
     * @return all of the provided audio in the decoded format as a .WAV file
     */

    public File getSoundFile() {
        if (!this.processorClosed) {
            throw new IllegalStateException("Once a sound file has been saved, the same processor can not be used again");
        }

        if (this.saveFile) {
            this.processorClosed = true;
            this.soundFile = this.decoder.save();
            return this.soundFile;
        } else {
            throw new IllegalStateException("Decoder is in non-persistent mode");
        }
    }

    /**
     * Returns the cumulative audio that was provided to the instance of this as a .WAV file
     *
     * @return .WAV file
     */

    public File getSavedSoundFile() {
        if (this.processorClosed) {
            return this.soundFile;
        } else {
            throw new IllegalStateException("Sound processor is not yet closed");
        }
    }

    public void setSoundListener(SoundProcessorListener listener) {
        this.soundListener = listener;
    }

    protected ByteBuffer getBuffer() {
        return this.byteBuffer;
    }


    /**
     * Transforms raw PCM bytes to short values (the provided PCM stream is 16-bit),
     * so that the resulting values could be used in analysis.
     *
     * @param data - raw PCM byte data
     *
     * @return PCM data in short form
     */

    public static ArrayList<Short> toShorts(final byte[] data) {
        ArrayList<Short> list = new ArrayList<>();

        for (int i = 0; i < data.length; i+=2) {
            int b1 = data[i] & 0xff;
            int b2 = data[i + 1] & 0xff;

            list.add((short) (b1 << 8 | b2));
        }

        return list;
    }
}


