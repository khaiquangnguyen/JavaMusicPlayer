/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2015
 *
 * Name: Khoi Le & Khai Nguyen
 * Date: Oct 29, 2015
 * Time: 5:31:44 PM
 *
 * Project: csci205_hw
 * Package: hw03.model
 * File: AudioUIModel
 * Description:
 *
 * ****************************************
 */
package hw03.Model;

import hw03.Utility.AudioProcessing;
import hw03.Utility.DFTProcessing;
import javax.sound.sampled.AudioFormat;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;

/**
 * The model class. Act as the underlying model for the AudioView.
 *
 * @author khainguyen
 */
public class AudioUIModel {
    //the Audio Processing varible to store the audio data
    private AudioProcessing audio;
    //store the information of the audio
    private AudioFormat audioFormat;
    //the range of volume change
    private BoundedRangeModel volumeRange;
    //the range of reverb
    private BoundedRangeModel reverbRange;
    //the range of sample to down sample
    private BoundedRangeModel sampleRange;
    //the range of delay
    private BoundedRangeModel delayRange;
    //the range of decay
    private BoundedRangeModel decayRange;
    //the volume to change to
    private double newVolume;
    //the new sample rate after down sample
    private int sampleSkippingFactor;
    //the delay time
    private double delayTime;
    //the decay rate
    private double decay;
    //the dft of the audio
    private DFTProcessing dftData;
    //the reverb amount
    private int reverb;
    //progress bar for audio
    private BoundedRangeModel progressBar;

    /**
     * Constructor. Initiate all the rangeModel for sliders and default values.
     *
     * @author khainguyen
     */
    public AudioUIModel() {
        this.decay = 0;
        this.delayTime = 0;
        this.newVolume = 100;
        this.sampleSkippingFactor = 2;
        this.volumeRange = new DefaultBoundedRangeModel((int) this.newVolume,
                                                        0,
                                                        -100, 100);
        this.decayRange = new DefaultBoundedRangeModel(0, 0, 0, 1000);
        this.delayRange = new DefaultBoundedRangeModel();
        this.reverbRange = new DefaultBoundedRangeModel(6, 0, 4, 8);
        this.sampleRange = new DefaultBoundedRangeModel(2, 0, 2, 10);
        this.progressBar = new DefaultBoundedRangeModel(0, 0, 0,
                                                        100);

    }

    public BoundedRangeModel getProgressBar() {
        return progressBar;
    }

    public int getReverb() {
        return reverb;
    }

    public void setReverb(int reverb) {
        this.reverb = reverb;
    }

    public AudioProcessing getAudio() {
        return audio;
    }

    public void setAudio(AudioProcessing audio) {
        this.audio = audio;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
    }

    public BoundedRangeModel getVolumeRange() {
        return volumeRange;
    }

    public void setVolumeRange(BoundedRangeModel volumeRange) {
        this.volumeRange = volumeRange;
    }

    public BoundedRangeModel getReverbRange() {
        return reverbRange;
    }

    public void setReverbRange(BoundedRangeModel reverbRange) {
        this.reverbRange = reverbRange;
    }

    public BoundedRangeModel getSampleRange() {
        return sampleRange;
    }

    public void setSampleRange(BoundedRangeModel sampleRange) {
        this.sampleRange = sampleRange;
    }

    public BoundedRangeModel getDelayRange() {
        return delayRange;
    }

    public void setDelayRange(BoundedRangeModel delayRange) {
        this.delayRange = delayRange;
    }

    public BoundedRangeModel getDecayRange() {
        return decayRange;
    }

    public void setDecayRange(BoundedRangeModel decayRange) {
        this.decayRange = decayRange;
    }

    public double getNewVolume() {
        return newVolume;
    }

    public void setNewVolume(double newVolume) {
        this.newVolume = newVolume;
    }

    public int getSampleSkippingFactor() {
        return sampleSkippingFactor;
    }

    public void setSampleSkippingFactor(int sampleSkippingFactor) {
        this.sampleSkippingFactor = sampleSkippingFactor;
    }

    public double getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(double delayTime) {
        this.delayTime = delayTime;
    }

    public double getDecay() {
        return decay;
    }

    public void setDecay(double decay) {
        this.decay = decay;
    }

    public DFTProcessing getDftData() {
        return dftData;
    }

    public void setDftData(DFTProcessing dftData) {
        this.dftData = dftData;
    }

}
