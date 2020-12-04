package no.nordicsemi.android.nrfthingy.sound;

import android.content.Context;

import java.util.ArrayList;

/**
 * Triggers the registered listener on detection of average volume exceeding the specified
 * decibel threshold.
 */

public class DecidelSoundProcessor extends BaseSoundProcessor {

    private double decibels;
    private static int MICROPHONE_REFERENCE = 120; // Thingy microphone caps out at 120 dBSPL

    /**
     * Context has to be provided as the ADPCMD decoder requires it, while the save file
     * option is for indicating that all of the decoded data should be saved to file on system.
     *
     * @param context  - system context
     * @param saveFile - true if all decoded audio should be saved to a .WAV file
     * @param decibelLevel - the audio event threshold level
     */
    public DecidelSoundProcessor(Context context, boolean saveFile, double decibelLevel) {
        super(context, saveFile);
        this.decibels = decibelLevel;
    }

    /**
     * Returns true when the average decibel level in the given samples is above
     * the specified threshold.
     *
     * @param data - 16-bit byte stream of PCM data
     *
     * @return returns <code>true<code/> when the audio level is higher than the threshold,
     * <code>false<code/> otherwise
     */

    @Override
    public boolean eventDetection(byte[] data) {

        // get the short values of the samples
        ArrayList<Short> shorts = BaseSoundProcessor.toShorts(data);

        // get the average decibel level across the samples
        long sum = 0;
        for (short sample : shorts) {
            sum += sample;
        }

        long average = sum / shorts.size();

        double decibelLevel = 20 * Math.log10(Math.abs(average) / Short.MAX_VALUE);

        return decibels <= MICROPHONE_REFERENCE + decibelLevel;
    }
}
