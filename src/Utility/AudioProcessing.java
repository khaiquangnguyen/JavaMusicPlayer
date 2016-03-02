/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2015
 *
 * Name: Khoi Le & Khai Nguyen
 * Date: Oct 7, 2015
 * Time: 10:04:15 AM
 *
 * Project: csci205_hw
 * Package: hw03
 * File: AudioProcessing
 * Description:
 *
 * ****************************************
 */
package hw03.Utility;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Perform different actions on the sound data.
 *
 *
 * @author kal037
 */
public class AudioProcessing {

    //the number of bits per byte
    private static final int BITS_PER_BYTE = 8;

    //the array representations of sound data
    private byte[] byteArray;

    //the clip object to play the sound
    private Clip clip;

    //The DataLine.Info object to play the sound
    private DataLine.Info dataLineInfo;

    //the audio input stream object to read in the byte array to process
    private AudioInputStream audioInputStream;

    //the format file of the current sound
    private AudioFormat audioFormat;

    // the length of the current sound
    private double audioLength;

    //the length of the current in frame
    private double audioLengthFrame;

    //the length of the current sound in byte
    private double audioLengthByte;
    //current frame. Only used for the pause function
    private long currFrame = 0;
    //loop or play
    private boolean loop = false;
    //pause or not pause
    private boolean pause = false;
//the audio length in microsecond
    private long audioLengthTime = 0;

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    //the current playing frame of the audio file
    public long getCurrFrame() {
        return currFrame;
    }

    //the current time in microsecond of the audio file
    public long getCurrTime() {
        return this.clip.getMicrosecondPosition();
    }

    //the length of the audio in second
    public long getAudioLengthTime() {
        return audioLengthTime;
    }

    /**
     * Constructor
     *
     * @param obj a WaveFormProcessing object
     *
     * @throws UnsupportedAudioFileException - if the audio file is not
     * supported
     * @throws LineUnavailableException - if the audio can't be read in normally
     * @throws IOException - if the audio input can't be read
     * @author kal037
     *
     */
    public AudioProcessing(WaveFormProcessing obj) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        this.byteArray = obj.getByteArray();
        this.audioFormat = obj.getAudioFormat();
        this.audioLength = obj.getLength();
        //Reads the byte[] input as an audio stream
        this.audioInputStream = new AudioInputStream(new DataInputStream(
                new ByteArrayInputStream(this.byteArray)), this.audioFormat,
                                                     (long) this.audioLength);
        //Gets the audio format of the wave
        this.audioFormat = this.audioInputStream.getFormat();
        //Gets a data line to play the audio
        this.dataLineInfo = new DataLine.Info(Clip.class, null);
        //Creates a Clip objecct to play the audio without buffering
        this.clip = (Clip) AudioSystem.getLine(dataLineInfo);
        this.clip.open(this.audioInputStream);
        this.audioLengthFrame = this.audioInputStream.getFrameLength();
        this.audioLengthByte = this.audioLengthFrame * this.audioFormat.getSampleSizeInBits() / 8;
        this.audioLengthTime = this.clip.getMicrosecondLength();

        //
        byte[] testArray = new byte[audioInputStream.available()];
        audioInputStream.read(testArray, 0, 1000);
    }

    /**
     * Plays the audio
     *
     * @author kqn001
     *
     * @throws LineUnavailableException
     * @throws IOException
     * @throws InterruptedException
     * @author kal037
     */
    public void play() throws LineUnavailableException, IOException, InterruptedException {
        //dont do anything if the clip is still active
        if (this.clip.isActive()) {
            return;
        }
        // to update the byte array right after modifying the array
        this.audioInputStream = new AudioInputStream(new DataInputStream(
                new ByteArrayInputStream(this.byteArray)), this.audioFormat,
                                                     (long) this.audioLength);
        //Gets the audio format of the wave
        this.audioFormat = this.audioInputStream.getFormat();
        //Gets a data line to play the audio
        this.dataLineInfo = new DataLine.Info(Clip.class, null);
        //Creates a Clip objecct to play the audio without buffering

        //
        this.clip = (Clip) AudioSystem.getLine(dataLineInfo);
        this.clip.open(this.audioInputStream);

        this.audioLengthTime = this.clip.getMicrosecondLength();
        if (this.pause == true) {
            this.pause = false;
            this.clip.setMicrosecondPosition(currFrame);
        } else {
            this.currFrame = 0;
        }
        if (this.loop) {
            this.clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            this.clip.start();
        }
    }

    /**
     * Pause the audio
     *
     * @author kqn001
     */
    public void pause() {
        if (this.clip.isActive()) {
            this.pause = true;
            this.currFrame = this.clip.getMicrosecondPosition();
            this.clip.stop();
        }
    }

    /**
     * make the audio loop
     *
     * @author kqn001
     */
    public void loop() {
        if (this.clip.isActive()) {
            if (this.loop) {
                this.clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                this.clip.loop(0);
            }
        }
    }

    /**
     * Stops the audio
     *
     * @author kal037
     */
    public void stop() {
        if (this.clip.isActive()) {
            this.currFrame = 0;
            this.clip.stop();
        }
    }

    /**
     * Increase/decrease the volume by volume percent positive volume is
     * increment, negative volume is decrement
     *
     * This code was based on information found at www.stackoverflow.com
     *
     * @see
     * <a = href"http://stackoverflow.com/questions/14485873/audio-change-volume-of-samples-in-byte-array">
     * http://stackoverflow.com/questions/14485873/audio-change-volume-of-samples-in-byte-array
     * <\a>
     *
     * @param volume - the amount of percent to change the volume
     * @author kal037
     */
    public void volumeControl(double volume) {
        //8 bits data
        if (this.audioFormat.getSampleSizeInBits() == 8) {
            for (int i = 0; i < this.byteArray.length; i += 1) {
                int newByte = (int) (this.byteArray[i] & 0xff);
                newByte -= 128;
                newByte = (int) (newByte * Math.max(
                                 (1 + volume / 100), 0.01));
                this.byteArray[i] = (byte) (newByte + 128);
            }
        } //16 bits data
        else {
            //convert to short array
            short[] shortArray = ProcessingUtility.byteToShortArray(
                    this.byteArray,
                    this.audioFormat.isBigEndian());
            //down volume
            for (int i = 0; i < shortArray.length; i++) {
                shortArray[i] = (short) (shortArray[i] * Math.max(
                                         (1 + (double) volume / 100), 0.01));
            }
            //convert back to byte array
            this.byteArray = ProcessingUtility.shortToByteArray(shortArray,
                                                                this.audioFormat.isBigEndian());
        }
    }

    /**
     * Performs down-sampling on data by taking a sample very skip step
     *
     * @param skip - the distance between sample to take
     * @author kal037
     *
     */
    public void downSample(int skip) {
        //stop processing if the new sample rate is higher than the current sample rate
        if (skip < 2 || skip > 10) {
            return;
        }
        byte[] newArray;
        List<Byte> newBytes = new ArrayList<>();
        List<Short> newShorts = new ArrayList<>();
        int channel = this.audioFormat.getChannels();

        // 8 bits
        if (this.audioFormat.getSampleSizeInBits() == 8) {
            //down sample
            for (int i = 0; i < this.byteArray.length - skip; i += skip) {
                //1 channel
                newBytes.add(this.byteArray[i]);
                //2 channels
                if (channel == 2) {
                    newBytes.add(this.byteArray[i + 1]);
                }
            }
            // convert back from the byte list to byte array
            newArray = new byte[newBytes.size()];
            for (int i = 0; i < newArray.length; i++) {
                newArray[i] = newBytes.get(i);
            }
        } //16 bits
        else {
            //convert to short array before down sample
            short[] shortArray = ProcessingUtility.byteToShortArray(
                    this.byteArray,
                    this.audioFormat.isBigEndian());
            //down sample
            for (int i = 0; i < shortArray.length - skip; i += skip) {
                // 1 channel
                newShorts.add(shortArray[i]);
                //2 channels
                if (channel == 2) {
                    newShorts.add(shortArray[i + 1]);
                }
            }
            // convert back from the list to short array
            short[] tempShorts = new short[newShorts.size()];
            for (int i = 0; i < tempShorts.length; i++) {
                tempShorts[i] = newShorts.get(i);
            }
            //convert back from the short array to bytes array
            newArray = ProcessingUtility.shortToByteArray(tempShorts,
                                                          this.audioFormat.isBigEndian());
        }
        //calculate the new sample rate
        double sampleRate = this.audioFormat.getSampleRate() / skip;
        //the new audio Length
        this.audioLength /= skip;
        //create new audio format for the new sound wave
        AudioFormat newFormat = new AudioFormat(this.audioFormat.getEncoding(),
                                                (float) sampleRate,
                                                this.audioFormat.getSampleSizeInBits(),
                                                channel,
                                                this.audioFormat.getFrameSize(),
                                                (float) sampleRate,
                                                this.audioFormat.isBigEndian());
        this.byteArray = newArray;
        this.audioFormat = newFormat;
        //reset the input stream
        this.audioInputStream = new AudioInputStream(new DataInputStream(
                new ByteArrayInputStream(this.byteArray)), this.audioFormat,
                                                     (long) this.audioLength);
        this.audioLengthFrame = this.audioInputStream.getFrameLength();
        this.audioLengthByte = this.audioLengthFrame * this.audioFormat.getSampleSizeInBits() / 8;
    }

    /**
     * Creates a delay effect
     *
     * @param delayTime - delay time in ms. Maximum 1000ms
     * @param decay -how much the echo sound loudness is reduced when echo
     * @author kal037
     */
    public void delay(double delayTime, double decay) {
        //stop execution if the delay time is bigger than 1000 ms
        decay /= 100;
        if (delayTime > 1000) {
            return;
        }
        //convert delay time to second
        delayTime /= 1000;

        // get the number of sample to delay
        int sampleDelay = (int) Math.round(
                this.audioFormat.getSampleRate() * delayTime);

        // 8 bits data
        if (this.audioFormat.getSampleSizeInBits() == 8) {
            //apply decay
            for (int i = 0; i < this.byteArray.length - sampleDelay; i++) {
                int newCurByte = (int) (this.byteArray[i] & 0xff);
                int newDelayByte = (int) (this.byteArray[i + sampleDelay] & 0xff);
                newCurByte -= 128;
                newDelayByte -= 128;
                newDelayByte += (float) newCurByte * decay;
                newDelayByte += 128;
                this.byteArray[i + sampleDelay] = (byte) (newDelayByte);
            }
        } //16 bits data
        else {
            //convert to short array
            short[] shortArray = ProcessingUtility.byteToShortArray(
                    this.byteArray,
                    this.audioFormat.isBigEndian());
            //apply decay
            for (int i = 0; i < shortArray.length - sampleDelay; i++) {
                shortArray[i + sampleDelay] += (short) ((float) shortArray[i] * decay);
            }
            //convert back to byte array
            this.byteArray = ProcessingUtility.shortToByteArray(shortArray,
                                                                this.audioFormat.isBigEndian());

        }
    }

    /**
     * create a reverberation effect by making the sound echo multiple times
     * with increasing decay rates
     *
     * @author kqn001
     * @param echoCount - the numebr of echos to create the reverb effect
     */
    public void reverb(int echoCount) {
        int delayTime = 400;
        // apply ten echos
        for (int x = 0; x <= echoCount; x++) {
            // increase the delay time with each echo
            delayTime += 20;
            //decrease the sound loudness with each echo
            double decay = x * 10;
            // apply the echo
            delay(delayTime, decay);
        }

    }

    /**
     * Prints out the information of the current audio
     *
     * @author kal037
     */
    public void getInfo() {
        System.out.println("AUDIO FORMAT INFORMATION");
        System.out.println("------------------------");
        System.out.println("");
        System.out.println(
                "Number of channels:           " + this.audioFormat.getChannels());
        System.out.println(
                "Bits per sample:              " + this.audioFormat.getSampleSizeInBits());
        System.out.println(
                "Sample rate:                  " + this.audioFormat.getSampleRate());
        System.out.println(
                "Frame rate:                   " + this.audioFormat.getFrameRate());
        System.out.println(
                "Length of waveform in frames: " + this.audioInputStream.getFrameLength());
        System.out.println(
                "Length of waveform in bytes:  " + (this.audioFormat.getSampleSizeInBits() * this.audioInputStream.getFrameLength()) / BITS_PER_BYTE);
        if (this.audioFormat.isBigEndian()) {
            System.out.println(
                    "Byte order:                   big-edian");
        } else {
            System.out.println("Byte order:                   little-edian");
        }
        System.out.println("");
    }

    /**
     * Saves the output as WAV file
     *
     * @param filename - name of the file
     * @throws IOException - the file can't be processed
     * @author kal037
     */
    public void saveFile(String filename) throws IOException {
        File out = new File(filename);
        this.audioInputStream = new AudioInputStream(new DataInputStream(
                new ByteArrayInputStream(this.byteArray)), this.audioFormat,
                                                     this.audioInputStream.getFrameLength());
        AudioSystem.write(this.audioInputStream,
                          AudioFileFormat.Type.WAVE,
                          out);
        System.out.println(out.getAbsolutePath());
    }

    /**
     * get the sample rate of the current data
     *
     * @return - the sample rate
     * @author kal037
     */
    public double getSampleRate() {
        return this.audioFormat.getSampleRate();
    }

    /**
     * check if the audio is running or not
     *
     * @return - true if the audio is running, false otherwise
     */
    public boolean isRunning() {
        return clip.isRunning();
    }

    /**
     * Perform DFT on the current sound data using one of the three DFT methods
     *
     * @author kqn001
     * @param type - the type of DFT transformation, either slow, fast, or
     * multi-threading
     * @param peakCount - the number of peak to be collect
     * @throws hw03.Utility.DFTException - the length of the array is not a power of 2
     */
    public DFTProcessing DFT(DFTType type, int peakCount) throws DFTException {
        //one channel

        DFTProcessing DFT;
        //copy the current byte arary
        byte[] copyArray = new byte[this.byteArray.length];
        System.arraycopy(this.byteArray, 0, copyArray, 0, this.byteArray.length);
        //Proceed to DFT
        //8 bit
        if (this.audioFormat.getSampleSizeInBits() == 8) {
            //fill the array to length of power 2
            this.byteArray = ProcessingUtility.fillToPower2(this.byteArray);
            Complex[] complexArray = new Complex[this.byteArray.length];
            //Convert to complex array
            for (int i = 0; i < complexArray.length; i++) {
                complexArray[i] = new Complex(this.byteArray[i] & 0xff - 128);
            }
            if (peakCount > complexArray.length / 2) {
                throw (new DFTException(" too many peaks!"));
            }
            DFT = new DFTProcessing(complexArray, getSampleRate());

        }//16 bits
        else {
            //convert to short array
            short[] shortArray = ProcessingUtility.byteToShortArray(
                    this.byteArray,
                    this.audioFormat.isBigEndian());
            //fill the array to length of power 2
            shortArray = ProcessingUtility.fillToPower2(shortArray);
            Complex[] complexArray = new Complex[shortArray.length];
            //Convert to complex array
            for (int i = 0; i < complexArray.length; i++) {
                complexArray[i] = new Complex(shortArray[i]);
            }
            DFT = new DFTProcessing(complexArray,
                                    getSampleRate());
            if (peakCount > complexArray.length / 2) {
                throw (new DFTException(" too many peaks!"));
            }
        }

        //perform calculation on DFT
//        DFT.calculateDFT(type);
//        System.out.println();
//        // show all the peaks
//        DFT.showPeaks(peakCount);
        // return the byte array to normal
        this.byteArray = copyArray;
        return DFT;
    }

    /**
     * get the byte array
     *
     * @return -the byte array
     * @author kal037
     */
    public byte[] getByteArray() {
        return byteArray;
    }

    public double getAudioLengthFrame() {
        return audioLengthFrame;
    }

    public double getAudioLengthByte() {
        return audioLengthByte;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    /**
     * Convert the current byte array data to a short array accordingly,
     * depending on the file is 8 bits or 16 bits
     *
     * If the file is 8 bits, then the short array is the same
     *
     * If the file is 16 bits, each item of the short array represents 2 bytes
     * of the bytes array
     *
     * @return - a short array, which represents the audio data.
     */
    public short[] getShortArray() {
        short[] shortArray;
        if (this.audioFormat.getSampleSizeInBits() == 8) {
            shortArray = new short[this.byteArray.length];
            for (int i = 0; i < shortArray.length; i++) {
                shortArray[i] = (short) ((int) this.byteArray[i] & 0xff);
                shortArray[i] -= 127;

            }
        } else {
            shortArray = ProcessingUtility.byteToShortArray(
                    this.byteArray,
                    this.audioFormat.isBigEndian());
        }
        return shortArray;
    }

}
