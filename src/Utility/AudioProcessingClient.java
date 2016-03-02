/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2015
 *
 * Name: Khoi Le & Khai Nguyen
 * Date: Oct 8, 2015
 * Time: 2:34:19 AM
 *
 * Project: csci205_hw
 * Package: hw01
 * File: AudioProcessingClient
 * Description:
 *
 * ****************************************
 */
package hw03.Utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * The client for the audio processing program. The program can aither open a
 * .wav file or generate a completely new pure tone for processing The program
 * can increase or decrease the volume of the file, down samples, add a
 * delay/echo effect or add an reverberate efefct to the existing sound The
 * program can save the sound file to a new file at the current working
 * directory Handles the UI of the program
 *
 * @author kqn001
 */
public class AudioProcessingClient {

    private static AudioProcessing audioProcessing;

    /**
     * Show and do different audio processing options (Adjust volume, add echo)
     * to the sound data
     *
     * @param audioProcessing - the AudioProcessing object that the program is
     * working on
     * @author kqn001 & kal037
     * @throws java.io.IOException - the audio file can't be read in
     */
    public static void subMenuProcessAudio(AudioProcessing audioProcessing) throws IOException {
        while (true) {
            try {
                //Reads input
                Scanner in = new Scanner(System.in);
                int input;
                System.out.println("                     DSP ACTIONS");
                System.out.println("                       *-----*");
                System.out.println();

                System.out.println("-1- To adjust volume, press 1.");
                System.out.println("-2- To add delay, press 2.");
                System.out.println("-0- To go back, press 0.");
                System.out.println();
                System.out.print("->> ");
                input = in.nextInt();

                //Changes volume
                if (input == 1) {
                    double volume;
                    System.out.println("                      VOLUME");
                    System.out.println("                      *-----*");
                    System.out.println();
                    System.out.println(
                            "Enter the percentage you want to increase/decrease your volume by.");
                    System.out.println(
                            "(Positive value to increase; negative value to decrease)");
                    System.out.println();
                    System.out.print("->> ");
                    volume = in.nextDouble();
                    audioProcessing.volumeControl(volume);
                    System.out.println();
                    System.out.println("Volume changed.");
                    System.out.println();

                } //Adds delay
                else if (input == 2) {
                    double delayTime;
                    double decay;
                    //Checks if the values are negative
                    System.out.println("                    DEPLAY/ECHO");
                    System.out.println("                      *-----*");
                    System.out.println();
                    while (true) {
                        //Gets delay time in miliseconds
                        System.out.print(
                                "Enter the delay time in milisecond between 0 and 1000ms: ");
                        delayTime = in.nextDouble();
                        if (delayTime >= 0 && delayTime <= 1000) {
                            break;
                        } else {
                            System.out.println("Please enter a valid value.");
                        }
                    }
                    while (true) {
                        //Gets decay percentage
                        System.out.print(
                                "Enter the decay percentage: ");
                        decay = in.nextDouble();
                        if (decay >= 0 && decay <= 100) {
                            break;
                        } else {
                            System.out.println("Please enter a valid value.");
                        }
                    }
                    audioProcessing.delay(delayTime, decay);
                    System.out.println();
                    System.out.println("Delay added.");
                    System.out.println();
                } else if (input == 0) {
                    break;
                } else {
                    System.out.println(
                            "Option " + input + " doesn't exist! Please input again!");
                    System.out.println();
                }

            } catch (InputMismatchException e) {
                System.out.println("Wrong input! Please input again!");
                System.out.println();
            }

        }
    }

    /**
     * Downsample option for the sound data
     *
     * @param audioProcessing - the AudioProcessing object that the program is
     * working on
     *
     * @author kqn001 & kal037
     */
    public static void subMenuDownSample(AudioProcessing audioProcessing) {
        Scanner in = new Scanner(System.in);
        int input;
        while (true) {
            System.out.println("                      DOWN SAMPLE");
            System.out.println("                        *-----*");
            System.out.println();
            System.out.print("Enter the desired skipping factor[2- 10]: ");
            input = in.nextInt();
            if (input > 10 || input < 2) {
                System.out.println("Please enter a valid skipping factor.");
                System.out.println(
                        "Current sample rate: " + audioProcessing.getSampleRate());
            } else {
                break;
            }
        }
        audioProcessing.downSample(input);
        System.out.println("");
        System.out.println("File down-sampled.");
        System.out.println("");
    }

    /**
     * Save option for the file
     *
     * @param audioProcessing - the AudioProcessing object that the program is
     * working on
     *
     * @author kqn001 & kal037
     * @throws java.io.FileNotFoundException - the file doesn't exist
     */
    public static void subMenuSaveFile(AudioProcessing audioProcessing) throws FileNotFoundException, IOException {
        Scanner in = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("               FILE SELECTION");
                System.out.println("                  *------*");
                System.out.print(
                        "Enter the name you want to save your WAV audio file as filename.wav: ");
                String filename = in.next();
                audioProcessing.saveFile(filename);
                System.out.println();
                System.out.println("File saved.");
                System.out.println();
                break;
            } catch (FileNotFoundException e) {
                System.out.println();
                System.out.println(
                        "Wrong file name format. Please input again!");
                System.out.println();
            } catch (IOException e) {
                System.out.println();
                System.out.println(
                        "Wrong file name format. Please input again!");
                System.out.println();

            }
        }

    }

    /**
     * Perform DFT on the current sound
     *
     * @param audioProcessing - the object which will be transformed
     * @throws DFTException - the length of the sound array is not a power of 2
     * @author kqn001
     */
    public static void subMenuDFT(AudioProcessing audioProcessing) throws DFTException {
        Scanner in = new Scanner(System.in);
        double input;
        int peak;
        while (true) {
            try {
                System.out.println();
                System.out.println("                        DFT");
                System.out.println("                      *-----*");
                System.out.println();
                System.out.println(
                        "-1- for slow DFT, press 1.");
                System.out.println(
                        "-2- For a faster FFT, press 2.");
                System.out.println(
                        "-3- For multithreading FFT, press 3.");
                System.out.println("-0- To go back, press 0.");
                System.out.println();
                System.out.print("->> ");
                input = in.nextInt();
                System.out.println();
                System.out.print("Enter the number of peaks value: ");
                peak = in.nextInt();
                if (input == 0) {
                    break;
                } else if (input == 1) {
                    System.out.println("Performing slow DFT...");
                    audioProcessing.DFT(DFTType.SLOW, peak);
                } else if (input == 2) {
                    System.out.println("Performing faster FFT...");
                    audioProcessing.DFT(DFTType.FAST, peak);
                } else if (input == 3) {
                    System.out.println(
                            "Performing fastest multithreading FFT...");
                    audioProcessing.DFT(DFTType.MULTI, peak);
                } else {
                    System.out.println(
                            "This option doesn't exist. Please try again!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Wrong input! Please input again!");
                System.out.println();

            } catch (DFTException e) {
                System.out.println(
                        "The input sound's length or number of peaks is invalid. Please try again!");
                System.out.println();
            }
        }

    }

    /**
     * Show all possible options for the sound data
     *
     * @param wave - the WaveFormProcessing object, which is the source of sound
     * data
     * @throws InputMismatchException - the input can't be parsed
     * @throws IOException - the input can't be read in
     * @throws UnsupportedAudioFileException - the audio file was not supported
     * @throws LineUnavailableException - the audio file can't be read
     * @throws InterruptedException - always thrown when interrupt the
     * multithreading
     * @author kqn001
     * @throws hw03.Utility.DFTException - the length of the input array is not a power
     * of 2
     */
    public static void subMenu(WaveFormProcessing wave) throws InputMismatchException, IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException, DFTException {

        boolean newSound = true;
        while (true) {
            try {
                Scanner in = new Scanner(System.in);
                int input;
//                audioProcessing = new AudioProcessing(wave);
                if (audioProcessing == null || newSound == true) {
                    audioProcessing = new AudioProcessing(wave);
                    newSound = false;
                }
                System.out.println("                      OPTIONS");
                System.out.println("                      *-----*");
                System.out.println();
                System.out.println(
                        "-1- To play the sound or interrupt the current sound, press 1.");
                System.out.println(
                        "-2- To get the wave's information, press 2.");
                System.out.println("-3- To downsample the wave, press 3.");
                System.out.println(
                        "-4- To process the audio (adjust volume, add delay), press 4.");
                System.out.println(
                        "-5- To create a reverberation effect, press 5.");
                System.out.println("-6- To save the audio, press 6.");
                System.out.println(
                        "-7- To perform DFT on the current wavelength, press 7.");
                System.out.println("-0- To go back, press 0.");
                System.out.println();
                System.out.print("->> ");
                input = in.nextInt();
                System.out.println();
                //Plays sound
                if (input == 1) {
                    audioProcessing.play();

                    while (true) {
                        Thread.sleep(1);
                        System.out.print(
                                "Please inter \"back\" to return to main menu: ");
                        String inputString = in.next();
                        if (inputString.equalsIgnoreCase("back")) {
                            audioProcessing.stop();
                            break;
                        }
//                        if (!audioProcessing.isRunning()) {
//                            break;
//                        }
                    }
                } //Gets file's info
                else if (input == 2) {
                    audioProcessing.getInfo();
                } //Processes the file
                else if (input == 4) {
                    subMenuProcessAudio(audioProcessing);
                } //Downsamples the file
                else if (input == 3) {
                    subMenuDownSample(audioProcessing);
                } //Adds reverb
                else if (input == 5) {
                    audioProcessing.reverb(5);
                    System.out.println(
                            "Succesfully created a reverb effect!");
                    System.out.println();
                } //Saves the new file
                else if (input == 6) {
                    subMenuSaveFile(audioProcessing);
                } else if (input == 7) {
                    subMenuDFT(audioProcessing);
                } else if (input == 0) {
                    break;
                } else {
                    System.out.println(
                            "Option " + input + " doesn't exist! Please input again!");
                    System.out.println();
                }

            } catch (InputMismatchException e) {
                System.out.println("Wrong input! Please input again!");
                System.out.println();

            }
        }
    }

    /**
     * generate pure sound option
     *
     * @return - the wave object, which contains the sound data, or null of
     * nothing was created
     * @throws InputMismatchException - the input can't eb parase
     * @author kqn001
     */
    public static WaveFormProcessing pureSoundProcssing() throws InputMismatchException {
        while (true) {
            try {
                double freq;
                double amp;
                int input;
                Scanner in = new Scanner(System.in);
                WaveFormProcessing wave;
                System.out.println("                   SOUND CREATION");
                System.out.println("                      *-----*");
                System.out.println();
                System.out.println(
                        "-1- For a beautiful sine wave sound, press 1.");
                System.out.println(
                        "-2- For a awesome square wave sound, press 2.");
                System.out.println(
                        "-3- For a badass sawtooh wave sound, press 3.");
                System.out.println("-0- To go back, press 0.");
                System.out.println();
                System.out.print("->> ");
                input = in.nextInt();
                System.out.println();
                //Catches option input error
                if (input >= 1 && input <= 3) {
                    System.out.print(
                            "Please enter your desired frequency(between 0 and 22050) : ");
                    freq = in.nextDouble();
                    System.out.print(
                            "Please enter your desired amplitude (between 0 and 1): ");
                    amp = in.nextDouble();
                    if ((amp >= 0) && (amp <= 1) && (freq > 0) && (freq < 22050)) {
                        switch (input) {
                            //Creates a sine wave
                            case 1: {
                                wave = new WaveFormProcessing(freq,
                                                              amp,
                                                              ToneType.SINE);

                                return wave;
                            }
                            //Creates a square wave
                            case 2: {
                                wave = new WaveFormProcessing(freq,
                                                              amp,
                                                              ToneType.SQUARE);
                                return wave;
                            }
                            //Creates a sawtooth wave
                            case 3: {
                                wave = new WaveFormProcessing(freq,
                                                              amp,
                                                              ToneType.SAWTOOTH);
                                return wave;
                            }

                        }
                    } else {
                        System.out.println(
                                "Wrong input! Please input again!");
                        System.out.println();
                    }
                } else if (input == 0) {
                    break;
                } else {
                    System.out.println(
                            "Option " + input + " doesn't exist! Please input again!");
                    System.out.println();
                }
            } catch (InputMismatchException e) {
                System.out.println("Wrong input! Please input again!");
                System.out.println();

            }
        }
        return null;
    }

    /**
     * Show different audio input options (read from file, generate sound) for
     * the program
     *
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException - the file doesn't exist
     * @throws javax.sound.sampled.UnsupportedAudioFileException - the audio
     * file type was not supported
     * @throws javax.sound.sampled.LineUnavailableException - the audio file
     * can't be read properly
     * @throws java.lang.InterruptedException - is always thrown when interrupt
     * the multi-threading
     * @author kqn001
     * @throws hw03.Utility.DFTException - the length of the input array is not a power
     * of 2
     */
    public static void main(String[] args) throws InputMismatchException, FileNotFoundException, IOException, UnsupportedAudioFileException, LineUnavailableException, InterruptedException, DFTException {

        while (true) {
            try {
                WaveFormProcessing wave;
                Scanner in = new Scanner(System.in);
                int input;
                System.out.println("                   SOUND SELECTION");
                System.out.println("                      *-----*");
                System.out.println();
                System.out.println();
                System.out.println(
                        "-1- To read from a wav file, press 1.");
                System.out.println(
                        "-2- To use our awesome sound generator, press 2.");
                System.out.println("-0- To quit, press 0.");
                System.out.println();
                System.out.print("->> ");
                input = in.nextInt();
                System.out.println();

                //Reads in .wav file
                if (input == 1) {
                    while (true) {
                        try {
                            System.out.print(
                                    "Please input your awesome *.wav sound file with full path, or 0 to go back: ");
                            String fileName = in.next();
                            System.out.println();
                            if (!fileName.equals("0")) {
                                wave = new WaveFormProcessing(fileName);
                                System.out.println(
                                        "Sound file readed successfully!");
                                System.out.println();
                                subMenu(wave);
                                break;
                            } else {
                                break;
                            }
                        } catch (FileNotFoundException e) {
                            System.out.println("The file doesn't exist!");
                            System.out.println();
                        } catch (UnsupportedAudioFileException e) {
                            System.out.println(
                                    "This file type can't be processed");
                            System.out.println();
                        } catch (IOException | LineUnavailableException e) {
                            System.out.println(
                                    "This file can't be processed. Please input again!");
                            System.out.println();
                        }
                    }
                } //Generates sound waves
                else if (input == 2) {
                    wave = pureSoundProcssing();
                    if (wave != null) {
                        System.out.println();
                        System.out.println(
                                "Sound wave created successfully!");
                        System.out.println();
                        subMenu(wave);
                    }
                } //Quits the program
                else if (input == 0) {
                    System.out.println("Good bye!");
                    break;
                } else {
                    System.out.println("Wrong input! Please input again!");
                    System.out.println();
                }
            } catch (InputMismatchException e) {
                System.out.println("Wrong input! Please input again!");
                System.out.println();
            }
        }

    }
}
