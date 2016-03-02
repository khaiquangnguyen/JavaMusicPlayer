/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2015
 *
 * Name: Khoi Le & Khai Nguyen
 * Date: Oct 29, 2015
 * Time: 10:12:19 PM
 *
 * Project: csci205_hw
 * Package: hw03
 * File: AudioUIController
 * Description:
 *
 * ****************************************
 */
package hw03.Controller;

import hw03.Utility.AudioProcessing;
import hw03.Model.AudioUIModel;
import hw03.view.AudioUIView;
import hw03.Utility.DFTException;
import hw03.Utility.DFTType;
import hw03.Utility.ProcessingUtility;
import hw03.Utility.ToneType;
import hw03.Utility.WaveFormProcessing;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Handle audio player. Play the audio on a different thread from Event Dispatch
 * Thread.
 *
 * @author khainguyen
 */
class audioPlayer extends SwingWorker<Integer, Integer> {

    //the audio file to play
    private AudioProcessing audio;

    /**
     * Constructor
     *
     * @param audio - the audio player to play
     */
    public audioPlayer(AudioProcessing audio) {
        this.audio = audio;
    }

    /**
     * the method used for multi-threading. Play the audio
     *
     * @return - Integer, which return after the file finish playing.
     * @throws Exception - when the file can't be played
     */
    @Override
    protected Integer doInBackground() throws Exception {
        audio.play();
        return 0;
    }

}

/**
 * The Controller class which connects AudioUIModel and AudioUIView. Update
 * AudioUIModel from changes in AudioUIView and vice versa.
 *
 * Act as the listener for buttons and sliders, and also perform actions based
 * on action and changes received
 *
 * @author khainguyen
 */
public class AudioUIController implements ActionListener, ChangeListener {

    //the model
    private AudioUIModel theModel;
    //the view
    private AudioUIView theView;
    //the timer for the audio progress bar
    Timer updateTimer = new Timer(100, this);
    //the audio file to be processed
    private AudioProcessing audioWave;

    /**
     * Constructor Reset and add ActionListener and ChangeListner to components
     * in view.
     *
     * @param theModel - the model to connect
     * @param theView - the view to connect
     * @author khainguyen
     */
    public AudioUIController(AudioUIModel theModel, AudioUIView theView) {
        this.theModel = theModel;
        this.theView = theView;
        theView.getOpen().addActionListener(this);
        theView.getPlayButton().addActionListener(this);
        theView.getCreate().addActionListener(this);
        theView.getExit().addActionListener(this);
        theView.getStopButton().addActionListener(this);
        theView.getVolumeButton().addActionListener(this);
        theView.getReverbButton().addActionListener(this);
        theView.getEchoButton().addActionListener(this);
        theView.getDownSampleButton().addActionListener(this);
        theView.getSaveButton().addActionListener(this);
        theView.getPauseButton().addActionListener(this);
        theView.getLoopToggleButton().addActionListener(this);
        theView.getVolumeSlider().addChangeListener(this);
        theView.getDecaySlider().addChangeListener(this);
        theView.getDelayTimeSlider().addChangeListener(this);
        theView.getReverbSlider().addChangeListener(this);
        theView.getDownSampleSlider().addChangeListener(this);
        theView.getDFTButton().addActionListener(this);
        theView.getShowPeakButton().addActionListener(this);
    }

    /**
     * Update all the informations of the waveform shown in theView.
     *
     * @author khainguyen
     */
    private void updateInfo() {
        //update channel Count
        String numChannel = Integer.toString(
                theModel.getAudioFormat().getChannels());
        theView.getNumChannel().setText(numChannel);
        //update number of bits per sample
        String bitsPerSample = Integer.toString(
                theModel.getAudioFormat().getSampleSizeInBits());
        theView.getNumBitsPerSample().setText(bitsPerSample);
        //update sample rate
        String sampleRate = Double.toString(
                theModel.getAudioFormat().getSampleRate());
        theView.getNumSampleRate().setText(sampleRate);
        // update frame rate
        String frameRate = Double.toString(
                theModel.getAudioFormat().getFrameRate());
        theView.getNumFrameRate().setText(frameRate);
        //update length in frame
        String lengthInFrame = Double.toString(
                theModel.getAudio().getAudioLengthFrame());
        theView.getNumLengthInFrame().setText(lengthInFrame);
        //update length in byte
        String lengthInByte = Double.toString(
                theModel.getAudio().getAudioLengthByte());
        theView.getNumLengthInByte().setText(lengthInByte);
    }

    /**
     * Reset all the components in the view when an new audio data is imported
     *
     * @author khainguyen
     */
    private void updateNewAudio() {
        //eneble all buttons but show peaks
        theView.getShowPeakButton().setEnabled(false);
        theView.getPlayButton().setEnabled(true);
        theView.getPauseButton().setEnabled(true);
        theView.getStopButton().setEnabled(true);
        theView.getLoopToggleButton().setEnabled(true);
        theView.getDownSampleButton().setEnabled(true);
        theView.getReverbButton().setEnabled(true);
        theView.getEchoButton().setEnabled(true);
        theView.getVolumeButton().setEnabled(true);
        theView.getSaveButton().setEnabled(true);
        theView.getDFTButton().setEnabled(true);

        //reset all slider's value in the view
        theView.getDecaySlider().setValue(0);
        theView.getDownSampleSlider().setValue(0);
        theView.getDelayTimeSlider().setValue(0);
        theView.getReverbSlider().setValue(0);
        theView.getVolumeSlider().setValue(0);
        theView.getVolumeSlider().setValue(0);
        //reset all slider value in the model
        theModel.getProgressBar().setValue(0);
        theModel.getSampleRange().setValue(2);
        theModel.getDecayRange().setValue(0);
        theModel.getDelayRange().setValue(0);
        theModel.getReverbRange().setValue(0);
        theModel.getVolumeRange().setValue(0);

        //reset info
        this.updateInfo();
        //draw the wave form
        drawSoundWave();
    }

    /**
     * Open an audio file and import the audio into the program
     *
     * @author khainguyen
     */
    private void openFile() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        while (true) {
            JFileChooser fc = new JFileChooser(".");
            int result = fc.showOpenDialog(null);
            if (result == 1 || result == -1) {
                break;
            }
            if (fc.getSelectedFile() != null) {
                if (theModel.getAudio() != null) {
                    theModel.getAudio().stop();
                }
                audioWave = new AudioProcessing(
                        new WaveFormProcessing(fc.getSelectedFile()));
                theModel.setAudio(audioWave);
                theModel.setAudioFormat(audioWave.getAudioFormat());
                updateNewAudio();
                break;
            } else {
                break;
            }
        }
    }

    /**
     * Draw the sound wave to the View. The screen will be divided by two if the
     * input is two channels.
     *
     * @author khainguyen
     */
    public void drawSoundWave() {
        //clear the current panel
        if (theModel.getAudio() != null) {
            theView.getWaveFormPanel().removeAll();
            theView.revalidate();
            theView.repaint();
            //get the short array
            short[] shortArray = theModel.getAudio().getShortArray();
            //if 1 channel
            theView.getWaveFormPanel().setLayout(new GridLayout(1, 1));
            if (theModel.getAudioFormat().getChannels() == 1) {
                JPanel soundPane = ProcessingUtility.createWavePane(
                        shortArray, theView.getWaveFormPanel().getHeight(),
                        theModel.getAudioFormat().getSampleSizeInBits(),
                        theModel.getAudio().getAudioLengthTime());
                theView.getWaveFormPanel().add(soundPane);
                theView.revalidate();
                theView.repaint();
            } //16 bits
            else if (theModel.getAudioFormat().getChannels() == 2) {
                //get the two channels to short arrays
                short[] firstChannel = new short[shortArray.length / 2];
                short[] secondChannel = new short[shortArray.length / 2];
                for (int i = 0; i < firstChannel.length; i++) {
                    firstChannel[i] = shortArray[i * 2];
                    secondChannel[i] = shortArray[i * 2 + 1];
                }
                //make the wave panel a grid array for easier processing
                theView.getWaveFormPanel().setLayout(new GridLayout(2, 1));
//                draw the first channel
                JPanel firstPane = ProcessingUtility.createWavePane(
                        firstChannel, theView.getWaveFormPanel().getHeight() / 2,
                        theModel.getAudioFormat().getSampleSizeInBits(),
                        theModel.getAudio().getAudioLengthTime());
                theView.getWaveFormPanel().add(firstPane);
//                draw the second channel
                JPanel secondPane = ProcessingUtility.createWavePane(
                        secondChannel,
                        theView.getWaveFormPanel().getHeight() / 2,
                        theModel.getAudioFormat().getSampleSizeInBits(),
                        theModel.getAudio().getAudioLengthTime());
                theView.getWaveFormPanel().add(secondPane);
                theView.revalidate();
                theView.repaint();

            }
        }
    }

    /**
     * Create a new sound wave using inputs from user. The input includes
     * frequency, amplitude and durations
     *
     * @author khainguyen
     */
    private void createSound() {
        while (true) {
            try {
                JTextField frequency = new JTextField(5);
                JTextField amplitude = new JTextField(5);
                JTextField duration = new JTextField(5);
                JComboBox soundType = new JComboBox(ToneType.values());
                JPanel inputJPanel = new JPanel(new GridLayout(4, 2));
                inputJPanel.add(new JLabel("Frequency: "));
                inputJPanel.add(frequency);
                inputJPanel.add(new JLabel("Amplitude: "));
                inputJPanel.add(amplitude);
                inputJPanel.add(new JLabel("Duration: "));
                inputJPanel.add(duration);
                inputJPanel.add(new JLabel("Tone Type: "));
                inputJPanel.add(soundType);
                int input = JOptionPane.showConfirmDialog(null, inputJPanel,
                                                          "CREATE NEW SOUND",
                                                          JOptionPane.OK_CANCEL_OPTION,
                                                          JOptionPane.INFORMATION_MESSAGE
                );
                if (input == JOptionPane.CANCEL_OPTION || input == JOptionPane.CLOSED_OPTION) {
                    break;
                } else {
                    double freq = Double.parseDouble(frequency.getText());
                    double amp = Double.parseDouble(amplitude.getText());
                    int dur = Integer.parseInt(duration.getText());
                    if (freq > 22050 || amp < 0 || amp > 1 || dur == 0) {
                        JOptionPane.showMessageDialog(null,
                                                      "One of the input is incorrect! Please try again!",
                                                      "Input Error",
                                                      JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (theModel.getAudio() != null) {
                            theModel.getAudio().stop();
                        }
                        ToneType toneType = (ToneType) soundType.getSelectedItem();
                        audioWave = new AudioProcessing(
                                new WaveFormProcessing(freq, amp, dur, toneType));
                        theModel.setAudio(audioWave);
                        theModel.setAudioFormat(audioWave.getAudioFormat());
                        updateNewAudio();
                        break;
                    }
                }
            } catch (HeadlessException | NumberFormatException | UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                JOptionPane.showMessageDialog(null,
                                              "The inputs can't be processed! Please try again!",
                                              "Input Error",
                                              JOptionPane.ERROR_MESSAGE);

            }
        }
    }

    /**
     * Get the current time from the audio and return it to update the progress
     * bar
     *
     * @return - the current time of the audio which is playing in second
     *
     * @author khainguyen
     */
    private int getCurrTime() {
        if (theModel.getAudio().isRunning()) {
            int time = ((int) (theModel.getAudio().getCurrTime() % theModel.getAudio().getAudioLengthTime()) / 100000);
            return time;

        } else {
            int time = 0;
            updateTimer.stop();
            return time;
        }
    }

    /**
     * Exit the window by dispose the window and the call system Exit.
     *
     * @author khainguyen
     */
    private void exit() {
        JFrame frame = (JFrame) theView.getPlayButton().getTopLevelAncestor();
        JOptionPane.showMessageDialog(null,
                                      "See ya!",
                                      "Exit",
                                      JOptionPane.INFORMATION_MESSAGE);
        frame.dispose();
        System.exit(0);
    }

    /**
     * Save the new audio File. The name of the file will be prompted by the
     * user.
     *
     * @IOException: when the file can't be saved.
     * @author khainguyen
     */
    private void saveFile() throws IOException {
        while (true) {
            JFileChooser fc = new JFileChooser(".");
            int result = fc.showSaveDialog(null);
            if (result == 1 || result == -1) {
                break;
            } else {
                String fileName = fc.getSelectedFile().getName();
                theModel.getAudio().saveFile(fileName);
                break;

            }
        }
    }

    /**
     * Action listener. React when an action is detected.
     *
     * Update when one of the buttons is pressed.
     *
     * @param e - the action
     * @author khainguyen
     */
    @Override
    public void actionPerformed(ActionEvent e
    ) {
        //open and audio file
        if (e.getSource() == theView.getOpen()) {
            try {
                openFile();
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
                JOptionPane.showMessageDialog(null,
                                              "The file can't be processed! Please try again!",
                                              "File Error",
                                              JOptionPane.ERROR_MESSAGE);
            }
        } //create a new audio
        else if (e.getSource() == theView.getCreate()) {
            createSound();
        } //exit
        else if (e.getSource() == theView.getExit()) {
            exit();
        } //play the sound
        else if (e.getSource() == theView.getPlayButton()) {
            audioPlayer player = new audioPlayer(theModel.getAudio());
            player.execute();
            theView.getProgressBar().setMaximum(
                    (int) (theModel.getAudio().getAudioLengthTime() / 100000));
            updateTimer.start();
        } //stop the sound
        else if (e.getSource() == theView.getStopButton()) {
            theModel.getAudio().stop();
            updateTimer.stop();
            theModel.getProgressBar().setValue(0);
            theView.getProgressBar().setValue(0);

        } //save the file
        else if (e.getSource() == theView.getSaveButton()) {
            try {
                saveFile();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                                              "The file can't be save!PLease try again!",
                                              "Save Error",
                                              JOptionPane.ERROR_MESSAGE);
            }
        } //toggle loop
        else if (e.getSource() == theView.getLoopToggleButton()) {
            theModel.getAudio().setLoop(
                    theView.getLoopToggleButton().isSelected());
            theModel.getAudio().loop();

        }//pause
        else if (e.getSource() == theView.getPauseButton()) {
            theModel.getAudio().pause();
            updateTimer.stop();
        } else if (e.getSource() == updateTimer) {
            theView.getProgressBar().setValue(getCurrTime());
        } // perform down sample
        else if (e.getSource() == theView.getDownSampleButton()) {
            performDownSample();
        } //perform change volume
        else if (e.getSource() == theView.getVolumeButton()) {
            performChangeVolume();
        } //perform reverb
        else if (e.getSource() == theView.getReverbButton()) {
            performReverb();
        } //perform echo
        else if (e.getSource() == theView.getEchoButton()) {
            performEcho();
        } //perform dft
        else if (e.getSource() == theView.getDFTButton()) {
            try {
                performDFT();
            } catch (DFTException ex) {
                JOptionPane.showMessageDialog(null,
                                              "DFT failed!",
                                              "Faied",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (e.getSource() == theView.getShowPeakButton()) {
            showPeaks();
        }
    }

    /**
     * Stop the playing of the current audio file and perform Echo.
     *
     * Reset the echo slider and values in theView after the action is
     * performed.Then redraw the wave.
     *
     * @throws HeadlessException
     * @author khainguyen
     */
    private void performEcho() throws HeadlessException {
        theModel.getAudio().stop();
        updateTimer.stop();
        theModel.getAudio().delay(theModel.getDelayTime(),
                                  theModel.getDecay());
        JOptionPane.showMessageDialog(null,
                                      "The echo effect is changed successfully!",
                                      "Successful",
                                      JOptionPane.INFORMATION_MESSAGE);
        theModel.setDecay(0);
        theModel.setDelayTime(0);
        theModel.getDecayRange().setValue(0);
        theModel.getDelayRange().setValue(0);
        theView.getDecaySlider().setValue(0);
        theView.getDelayTimeSlider().setValue(0);
        drawSoundWave();
    }

    /**
     * Perform DFT on the audio using multi DFT and inform the user after
     * complete
     *
     * @throws DFTException: when DFT can't be performed, mostly due to bad data
     * input.
     * @author khainguyen
     */
    private void performDFT() throws DFTException {
        theModel.setDftData(theModel.getAudio().DFT(DFTType.MULTI, 1));
        theModel.getDftData().calculateDFT(DFTType.FAST);
        JOptionPane.showMessageDialog(null,
                                      "DFT is calculated successfully!",
                                      "Successful",
                                      JOptionPane.INFORMATION_MESSAGE);
        theView.getShowPeakButton().setEnabled(true);

        showPeaks();
    }

    /**
     * Show a number of peaks of the DFT. The number of peaks is inputed by the
     * user.
     *
     * The list of peaks will be print out as a table in the form
     * |peak|frequency|amplitude.
     *
     * @author khainguyen
     */
    private void showPeaks() {
        while (true) {
            JTextField peakCount = new JTextField(5);
            JPanel inputJPanel = new JPanel(new GridLayout(2, 1));
            inputJPanel.add(new JLabel(
                    "Enter number of peaks to calculate:"));
            inputJPanel.add(peakCount);
            int input = JOptionPane.showConfirmDialog(null, inputJPanel,
                                                      "CALCULATE DFT PEAKS",
                                                      JOptionPane.OK_CANCEL_OPTION,
                                                      JOptionPane.INFORMATION_MESSAGE
            );
            if (input == JOptionPane.CANCEL_OPTION || input == JOptionPane.CLOSED_OPTION) {
                break;
            } else {
                int peakInt = Integer.parseInt(peakCount.getText());
                if (peakInt < 1 || peakInt > theModel.getAudioFormat().getSampleRate() / 2) {
                    JOptionPane.showMessageDialog(null,
                                                  "One of the input is incorrect! Please try again!",
                                                  "Input Error",
                                                  JOptionPane.ERROR_MESSAGE);
                } else {
                    double[][] peakList = theModel.getDftData().getPeaks(peakInt);
                    JPanel pane = new JPanel(new GridLayout(peakInt + 1, 3));
                    pane.add(new JLabel("Peak", JLabel.CENTER));
                    pane.add(new JLabel("Frequency", JLabel.CENTER));
                    pane.add(new JLabel("Magnitude", JLabel.CENTER));

                    for (int i = 0; i < peakInt; i++) {
                        pane.add(new JLabel(Integer.toString(i + 1),
                                            JLabel.CENTER));
                        pane.add(new JLabel(String.format("%.2f",
                                                          peakList[0][i]),
                                            JLabel.CENTER));
                        pane.add(new JLabel(String.format("%.0f",
                                                          peakList[1][i]),
                                            JLabel.CENTER));

                    }
                    JScrollPane scrollPane = new JScrollPane(pane);
                    scrollPane.setViewportView(pane);
                    scrollPane.setVerticalScrollBarPolicy(
                            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    scrollPane.setPreferredSize(new Dimension(300, 200));
                    JOptionPane.showConfirmDialog(null, scrollPane,
                                                  "PEAKS",
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  JOptionPane.INFORMATION_MESSAGE
                    );
                    break;
                }
            }
        }
    }

    /**
     * Stop the playing of the current audio file and perform reverb.
     *
     * Reset the reverb slider and values in theView after the action is
     * performed. Then redraw the wave.
     *
     *
     *
     * @throws HeadlessException
     * @author khainguyen
     */
    private void performReverb() throws HeadlessException {
        theModel.getAudio().stop();
        updateTimer.stop();
        theModel.getAudio().reverb(theModel.getReverb());
        JOptionPane.showMessageDialog(null,
                                      "The reverb effect is added successfully!",
                                      "Successful",
                                      JOptionPane.INFORMATION_MESSAGE);
        drawSoundWave();
    }

    /**
     * Stop the playing of the current audio file and perform change volume.
     *
     * Reset the change volume slider and values in theView after the action is
     * performed
     *
     * @throws HeadlessException
     * @author khainguyen
     */
    private void performChangeVolume() throws HeadlessException {
        theModel.getAudio().stop();
        updateTimer.stop();
        theModel.getAudio().volumeControl(theModel.getNewVolume());
        JOptionPane.showMessageDialog(null,
                                      "The volume is changed successfully!",
                                      "Successful",
                                      JOptionPane.INFORMATION_MESSAGE);
        theModel.setNewVolume(0);
        theModel.getVolumeRange().setValue(0);
        theView.getVolumeSlider().setValue(0);
        drawSoundWave();
    }

    /**
     * Stop the playing of the current audio file and perform down sample.
     *
     * Reset the down sample slider and values in theView after the action is
     * performed. Then redraw the wave.
     *
     * @throws HeadlessException
     * @author khainguyen
     */
    private void performDownSample() throws HeadlessException {
        theModel.getAudio().stop();
        updateTimer.stop();
        theModel.getAudio().downSample(theModel.getSampleSkippingFactor());
        theModel.setAudioFormat(theModel.getAudio().getAudioFormat());
        JOptionPane.showMessageDialog(null,
                                      "The sample is decreased successfully!",
                                      "Successful",
                                      JOptionPane.INFORMATION_MESSAGE);
        theModel.getSampleRange().setMaximum(
                (int) theModel.getAudioFormat().getSampleRate());
        theModel.setSampleSkippingFactor(2);
        theModel.getSampleRange().setValue(2);
        theView.getDownSampleSlider().setValue(2);
        updateInfo();
        drawSoundWave();
    }

    /**
     * Change listener. React when a change is detected
     *
     * The changes includes the sliders for change volume, delay, echo, reverb,
     * down sample.
     *
     * @param ce - the change
     * @author khainguyen
     *
     */
    @Override
    public void stateChanged(ChangeEvent ce
    ) {
        //volume changes
        if (ce.getSource() == theView.getVolumeSlider()) {
            theModel.setNewVolume(
                    theView.getVolumeSlider().getValue());
            theView.getVolumeLabel().setText(
                    Integer.toString(
                            (int) theModel.getNewVolume()));
        } //delay time changes
        else if (ce.getSource() == theView.getDelayTimeSlider()) {
            theModel.setDelayTime(
                    theView.getDelayTimeSlider().getValue());
            theView.getDelayTimeLabel().setText(
                    Integer.toString(
                            (int) theModel.getDelayTime()));
        } //decay rate changes
        else if (ce.getSource() == theView.getDecaySlider()) {
            theModel.setDecay(
                    theView.getDecaySlider().getValue());
            theView.getDecayLabel().setText(
                    Integer.toString(
                            (int) theModel.getDecay()));
        } //reverb changes
        else if (ce.getSource() == theView.getReverbSlider()) {
            theModel.setReverb(
                    theView.getReverbSlider().getValue());
            theView.getReverbLabel().setText(
                    Integer.toString(
                            theModel.getReverb()));
        } //downsample changes
        else if (ce.getSource() == theView.getDownSampleSlider()) {
            theModel.setSampleSkippingFactor(
                    theView.getDownSampleSlider().getValue());
            theView.getDownSampleLabel().setText(
                    Integer.toString(
                            (int) theModel.getSampleSkippingFactor()));
        }
    }
}
