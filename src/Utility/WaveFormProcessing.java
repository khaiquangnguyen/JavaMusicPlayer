/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2015
 *
 * Name: Khoi Le & Khai Nguyen
 * Date: Oct 7, 2015
 * Time: 10:14:30 AM
 *
 * Project: csci205_hw
 * Package: hw01
 * File: WaveFormProcessing
 * Description:
 *
 * ****************************************
 */
package hw03.Utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * process the waveform, either through reading in a wav file or create a pure
 * tone
 *
 * @author kqn001
 */
public class WaveFormProcessing {

    //the default sample rate for the pure tone
    private static int sampleRate = 44100;

    //the default duration for the pure tone
    private int duration = 10;

    //the array which represents the PCM data of the sound
    private byte[] soundArray;

    //the default bitdepth of the pure tone created
    private static final int BIT_DEPTH = Short.MAX_VALUE;

    //the AudioFormat object, which contains the informations of the wave
    private AudioFormat audioFormat = this.audioFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            WaveFormProcessing.sampleRate, 16, 1,
            2, WaveFormProcessing.sampleRate,
            true);
    //the length of the wave
    private long length = sampleRate * duration;

    /**
     * read in a wav file using path and get the informations and PCM data out
     * of it
     *
     * @param fileName - the name of the file
     * @throws UnsupportedAudioFileException - the fiel type can't be read in
     * @throws java.io.FileNotFoundException - the file does not exist
     * @throws IOException - the input/output system has problem
     * @author kqn001
     */
    public WaveFormProcessing(String fileName) throws FileNotFoundException, UnsupportedAudioFileException, IOException {
        File file = new File(fileName);
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            this.audioFormat = audioStream.getFormat();
            this.length = audioStream.getFrameLength();
            this.soundArray = new byte[audioStream.available()];
            audioStream.read(soundArray);
            audioStream.close();
        }
    }

    /**
     * read in a wav file and get the informations and PCM data out of it
     *
     * @param file - the file to be read in
     * @throws UnsupportedAudioFileException - the fiel type can't be read in
     * @throws java.io.FileNotFoundException - the file does not exist
     * @throws IOException - the input/output system has problem
     * @author kqn001
     */
    public WaveFormProcessing(File file) throws FileNotFoundException, UnsupportedAudioFileException, IOException {
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            this.audioFormat = audioStream.getFormat();
            this.length = audioStream.getFrameLength();
            this.soundArray = new byte[audioStream.available()];
            audioStream.read(soundArray);
            audioStream.close();
        }
    }

    /**
     * create a pure tone with the input frequency, amplitude and type of tone
     *
     * @param freq - the frequency of the pure tone
     * @param amp - the amplitute of the pure tone
     * @param duration - the duration of the sound
     * @param type - the type of the tone, chose from SINE, SQUARE, SAWTOOTH
     * @author kqn001
     */
    public WaveFormProcessing(double freq, double amp, int duration,
                              ToneType type) {
        this.duration = duration;
        this.length = sampleRate * duration;
        if (type == ToneType.SINE) {
            createWaveSine(freq, amp);
        } else if (type == ToneType.SQUARE) {
            createWaveSquare(freq, amp);
        } else if (type == ToneType.SAWTOOTH) {
            createWaveSawTooth(freq, amp);
        }
    }

    /**
     * create a pure tone with the input frequency, amplitude and type of tone
     *
     * @param freq - the frequency of the pure tone
     * @param amp - the amplitute of the pure tone
     * @param type - the type of the tone, chose from SINE, SQUARE, SAWTOOTH
     * @author kqn001
     */
    public WaveFormProcessing(double freq, double amp,
                              ToneType type) {
        if (type == ToneType.SINE) {
            createWaveSine(freq, amp);
        } else if (type == ToneType.SQUARE) {
            createWaveSquare(freq, amp);
        } else if (type == ToneType.SAWTOOTH) {
            createWaveSawTooth(freq, amp);
        }
    }

    /**
     * create a sine waveform with a predefined frequency and amplitude
     *
     * This code was based on information found at www.en.wikipedia.com
     *
     * @see <a href = "https://en.wikipedia.org/wiki/Waveform">
     * https://en.wikipedia.org/wiki/Waveform </a>
     *
     * @param freq - the frequency of the wave form
     * @param amp - the amplitude of the wave form
     * @author kqn001
     */
    private byte[] createWaveSine(double freq, double amp) {
        int numSample = sampleRate * duration;
        short[] sample;
        sample = new short[numSample];
        for (int i = 0; i < numSample; i++) {
            sample[i] = (short) (BIT_DEPTH * amp * Math.sin(
                                 freq * 2 * Math.PI * i / (sampleRate)));
        }
        this.soundArray = ProcessingUtility.shortToByteArray(sample, true);
        return this.soundArray;
    }

    /**
     * create a square waveform with a predefined frequency and amplitude
     *
     * This code was based on information found at www.stackoverflow.com
     *
     * @see
     * <a href = "http://stackoverflow.com/questions/3986248/generating-sine-square-triangle-sawtooth-audio-signals-using-androids-audiot">
     * http://stackoverflow.com/questions/3986248/generating-sine-square-triangle-sawtooth-audio-signals-using-androids-audiot</a>
     *
     * @param freq - the frequency of the wave form
     * @param amp - the amplitude of the wave form
     * @author kqn001
     */
    private byte[] createWaveSquare(double freq, double amp) {
        int numSample = sampleRate * duration;
        short[] sample;
        sample = new short[numSample];
        for (int i = 0; i < numSample; i++) {
            sample[i] = (short) (BIT_DEPTH * amp * Math.sin(
                                 freq * 2 * Math.PI * i / (sampleRate)));
            if (sample[i] > 0) {
                sample[i] = (short) (BIT_DEPTH * amp);
            } else if (sample[i] < 0) {
                sample[i] = (short) (BIT_DEPTH * -amp);
            }
        }
        this.soundArray = ProcessingUtility.shortToByteArray(sample, true);
        return this.soundArray;
    }

    /**
     * create a sawtooth waveform with a predefined frequency and amplitude the
     *
     * This code was based on information found at www.michaelkrzyzaniak.com
     *
     * @see
     * <a href = "http://michaelkrzyzaniak.com/AudioSynthesis/2_Audio_Synthesis/1_Basic_Waveforms/5_Sawtooth_Wave/">
     * http://michaelkrzyzaniak.com/AudioSynthesis/2_Audio_Synthesis/1_Basic_Waveforms/5_Sawtooth_Wave/</a>
     *
     *
     * @param freq - the frequency of the wave form
     * @param amp - the amplitude of the wave form
     * @author kqn001
     */
    private byte[] createWaveSawTooth(double freq, double amp) {
        int numSample = sampleRate * duration;
        short[] sample = new short[numSample];
        int period = (int) (sampleRate / freq);
        for (int i = 0; i < numSample; i++) {
            sample[i] = (short) (amp * BIT_DEPTH * (2 * (i % period) / (double) period - 1));
        }
        this.soundArray = ProcessingUtility.shortToByteArray(sample, true);
        return this.soundArray;
    }

    /**
     * get the byte array
     *
     * @return - an byte array represents the PCM data of the sound wave
     */
    public byte[] getByteArray() {
        return this.soundArray;
    }

    /**
     * get the audio format
     *
     * @return - the audioformat object which contains the information of the
     * sound wave
     * @author kqn001
     */
    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    /**
     * get the length of the sound wave
     *
     * @return - the length of the sound wave
     * @author kqn001
     */
    public long getLength() {
        return length;
    }
}
