/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2015
 *
 * Name: Khoi Le & Khai Nguyen
 * Date: Oct 27, 2015
 * Time: 9:20:34 PM
 *
 * Project: csci205_hw
 * Package: hw03
 * File: ProcessingUtility
 * Description:
 *
 * ****************************************
 */
package hw03.Utility;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseWheelEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Keep all the utilities which are class-independent.
 *
 * @author khainguyen
 */
public class ProcessingUtility {

    /**
     * fill an array with 0 until the array has length is a power of 2
     *
     * @param byteArray - the array to be filled
     * @return - the resulted array after filling
     * @author kqn001
     */
    public static byte[] fillToPower2(byte[] byteArray) {
        int length = byteArray.length;
        if (isPowerOf2(length)) {
            return byteArray;
        } else {
            int power = (int) Math.ceil(Math.log(length) / Math.log(2));
            int newLength = (int) Math.pow(2, power);
            byte[] newArray = new byte[newLength];
            System.arraycopy(byteArray, 0, newArray, 0, length);
            return newArray;
        }
    }

    /**
     * fill an array with 0 until the array has length is a power of 2
     *
     * @param shortArray - the array to be filled
     * @return - the resulted array after filling
     * @author kqn001
     */
    public static short[] fillToPower2(short[] shortArray) {
        int length = shortArray.length;
        if (isPowerOf2(length)) {
            return shortArray;
        } else {
            int power = (int) Math.ceil(Math.log(length) / Math.log(2));
            int newLength = (int) Math.pow(2, power);
            short[] newArray = new short[newLength];
            System.arraycopy(shortArray, 0, newArray, 0, length);
            return newArray;
        }
    }

    /**
     * sort a 2-D array according to the value of the first row of the array
     * using super slow way I could find a better way, but it's not really
     * necessary
     *
     * @param inputArray - the array to be sort
     * @return - the output array
     * @author kqn001
     */
    public static double[][] sortArray(double[][] inputArray) {
        for (int i = 0; i < inputArray[0].length - 1; i++) {
            for (int j = i + 1; j < inputArray[0].length; j++) {
                if (inputArray[0][i] < inputArray[0][j]) {
                    for (double[] inputArray1 : inputArray) {
                        double t = inputArray1[i];
                        inputArray1[i] = inputArray1[j];
                        inputArray1[j] = t;
                    }
                }
            }
        }
        return inputArray;
    }

    /**
     * convert an byte array to short array to handle. Only handle 16 bits byte
     * array
     *
     * This code was based on information found at www.stackoverflow.com
     *
     * @see
     * <a href = "http://stackoverflow.com/questions/5625573/byte-array-to-short-array-and-back-again-in-java">
     * http://stackoverflow.com/questions/5625573/byte-array-to-short-array-and-back-again-in-java</a>
     *
     * @param byteArray - the byte array to be converted
     * @param endian - the type of endian the array was converted, true for big,
     * false for little
     * @return - the resulted short array
     * @author kqn001
     */
    public static short[] byteToShortArray(byte[] byteArray, boolean endian) {
        if (endian == true) {
            short[] shortArray = new short[byteArray.length / 2];
            ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(
                    shortArray);
            return shortArray;
        } else {
            short[] shortArray = new short[byteArray.length / 2];
            ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(
                    shortArray);
            return shortArray;
        }
    }

    /**
     * Converts short array to byte array
     *
     * This code was based on information found at www.stackoverflow.com
     *
     * @see
     * <a href = "http://stackoverflow.com/questions/5625573/byte-array-to-short-array-and-back-again-in-java">
     * http://stackoverflow.com/questions/5625573/byte-array-to-short-array-and-back-again-in-java</a>
     *
     * @param shortArray - the short array to be converted
     * @param endian - the type of endian the array was converted, true for big,
     * false for little
     * @return - the resulted byte array
     * @author kqn001
     */
    public static byte[] shortToByteArray(short[] shortArray, boolean endian) {
        if (endian == true) {
            byte[] byteArray = new byte[shortArray.length * 2];
            ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).asShortBuffer().put(
                    shortArray);
            return byteArray;
        } else {
            byte[] byteArray = new byte[shortArray.length * 2];
            ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(
                    shortArray);
            return byteArray;
        }
    }

    /**
     * Check if the number is a power of 2 or not
     *
     * @param number - the number to be check
     * @return - true of the number is a power of 2, false otherwise
     * @author kqn001
     */
    public static boolean isPowerOf2(int number) {
        if (number == 1) {
            return true;
        } else if (number < 1) {
            return false;
        } else if (number % 2 == 1) {
            return false;
        } else {
            return isPowerOf2(number / 2);
        }
    }

    /**
     * create a pane which contains the illustration of the sound wave,
     * represented by shortArray
     *
     * The pane has two components. A VerticalScaleComponent which show the
     * vertical ruler. The second component is a JScrollPane, which contains the
     * illustration of the sound wave.
     *
     * @param shortArray - the array representation of the sound
     * @param height - the height of the pane
     * @param bits - the number of bits of the audio wave
     * @param duration - the duration of the audio wave
     * @return - the panel which contains the illustration of the sound wave
     * @author khainguyen
     *
     */
    public static JPanel createWavePane(short[] shortArray, int height,
                                        int bits, double duration) {
        SoundWaveComponent soundWave = new SoundWaveComponent(shortArray,
                                                              (int) (height - 18),
                                                              bits, duration);
        JScrollPane jsp = new JScrollPane(soundWave);
        jsp.setViewportView(soundWave);
        jsp.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jsp.setOpaque(false);
        jsp.setWheelScrollingEnabled(false);
        jsp.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        soundWave.addMouseWheelListener((MouseWheelEvent mwe) -> {
            int notches = mwe.getWheelRotation();
            double mouseXRelavtive = mwe.getPoint().getX() / soundWave.getPreferredSize().getWidth();
            double distanceToViewOrigin = mwe.getPoint().getX() - jsp.getViewport().getViewPosition().getX();
            soundWave.rescale(notches * 5);
            soundWave.repaint();
            int newX = (int) (soundWave.getPreferredSize().getWidth() * mouseXRelavtive - distanceToViewOrigin);
            jsp.getViewport().setViewPosition(new Point(
                    (int) (newX),
                    0));
            jsp.getViewport().revalidate();
            jsp.getViewport().repaint();
            jsp.getViewport().setViewPosition(new Point(
                    (int) (newX),
                    0));
        });
        JPanel panel = new JPanel(new BorderLayout());
        VerticalScaleComponent verticalRuler = new VerticalScaleComponent(
                panel.getHeight(),
                bits);
        verticalRuler.setPreferredSize(new Dimension(30,
                                                     verticalRuler.getHeight()));
        panel.add(verticalRuler, BorderLayout.WEST);
        panel.add(jsp);
        return panel;

    }

}
