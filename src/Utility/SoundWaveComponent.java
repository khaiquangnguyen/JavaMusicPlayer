/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2015
 *
 * Name: Khoi Le & Khai Nguyen
 * Date: Oct 28, 2015
 * Time: 5:28:10 PM
 *
 * Project: csci205_hw
 * Package: hw03
 * File: SoundWaveTest
 * Description:
 *
 * ****************************************
 */
package hw03.Utility;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.Scrollable;

/**
 * Draw the audio and the horizontal ruler.
 *
 * @author Khoi Le & khainguyen
 */
public class SoundWaveComponent extends JComponent implements Scrollable {
    //the number of sample per pixel
    private int samplesPerPixel;
    //the input short array to draw
    private short[] shortArray;
    //the width of the current window
    private final int FRAME_WIDTH = 1000;
    //the number of pixel to draw a time stamp
    private final int STEP = 50;
    // the height of the window
    private int height;
    //the scale to which the audio input data will be reduced
    private double redrawScale = 1;
    //the size of the ruler
    private int rulerSize;
    //the duratio of the audio
    private double duration;

    public SoundWaveComponent(short[] shortArray, int height, int bits,
                              double duration) {
        this.shortArray = shortArray;
        this.samplesPerPixel = Math.round(shortArray.length / (FRAME_WIDTH - 10));
        this.height = height;
        if (bits == 8) {
            this.redrawScale = 127 / (height / 4) * 1.2;
        } else {
            this.redrawScale = 32767 / (height / 2) * 1.2;
        }
        rulerSize = height / 10;
        this.duration = duration;
    }

    public double getRedrawScale() {
        return redrawScale;
    }

    public void setShortArray(short[] shortArray) {
        this.shortArray = shortArray;
    }

    /**
     * Change the number of samples per pixel by change amount to zoom in and
     * out
     *
     * @param change - how much samples per pixel is changed
     * @author kqn001
     */
    public void rescale(int change) {

        if (samplesPerPixel < 10) {
            change /= 5;
        }
        change = (int) (change * (Math.max(Math.log(Math.abs(
                                  samplesPerPixel)),
                                           1)));
        this.samplesPerPixel += change;
        if (this.samplesPerPixel == 0) {
            this.samplesPerPixel += change;
        }
        this.samplesPerPixel = Math.min(this.samplesPerPixel, Math.round(
                                        shortArray.length / (FRAME_WIDTH - 10)));
        this.samplesPerPixel = Math.max(this.samplesPerPixel, -30);
        this.setPreferredSize(new Dimension(
                this.shortArray.length / Math.abs(
                        samplesPerPixel),
                height));
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSamplesPerPixel(int samplesPerPixel) {
        this.samplesPerPixel = samplesPerPixel;
    }

    /**
     * Draw the audio and the ruler.
     *
     * @param g
     * @author Khoi Le & khainguyen
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(40, 40, 40));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Color.WHITE);
        int frame = 0;
        int oldX = 0;
        int oldY = getHeight() / 2;
        if (samplesPerPixel > 0) {
            for (int i = 0; i < shortArray.length - samplesPerPixel; i += samplesPerPixel) {
                short[] array = Arrays.copyOfRange(shortArray, i,
                                                   i + samplesPerPixel);
                if (samplesPerPixel <= 4) {
                    short maxValue = this.findMax(array);
                    int y = (int) (getHeight() / 2 - maxValue / redrawScale);
                    g2.drawLine(oldX, oldY, frame, y);
                    oldX = frame;
                    oldY = y;
                    frame++;
                } else {
                    short maxValue = this.findMax(array);
                    int posY = (int) (getHeight() / 2 - maxValue / redrawScale);
                    int negY = (int) (getHeight() / 2 + maxValue / redrawScale);
                    g2.drawLine(frame, posY, frame, negY);
                    frame++;
                }
            }
        } else if (samplesPerPixel < 0) {
            for (int i = 0; i < shortArray.length; i++) {
                int y = (int) (getHeight() / 2 - shortArray[i] / redrawScale);
                g2.drawLine(oldX, oldY, frame, y);
                int radius = samplesPerPixel / -10;
                g2.fillOval(oldX - radius, oldY - radius, radius * 2, radius * 2);
                oldX = frame;
                oldY = y;
                frame -= samplesPerPixel;

            }
        }

        //Draw ruler
        g2.setColor(new Color(20, 20, 20));
        g2.fillRect(0, 0, getWidth(), rulerSize);
        int timeStep = (int) (50 * (duration / getPreferredSize().width));
        g2.setColor(Color.WHITE);
        double count = 0;
        for (int i = 0; i < getPreferredSize().width; i += STEP) {
            if (count != 0) {
                g2.drawLine(i, rulerSize, i, rulerSize * 3 / 4);
                g2.drawString(String.format("%.4f", count / 1000000),
                              i - String.format("%.4f", count / 1000000).length() * g2.getFont().getSize() / 4,
                              rulerSize / 2);
            }
            count += timeStep;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (samplesPerPixel > 0) {
            return (new Dimension(this.shortArray.length / Math.abs(
                    samplesPerPixel),
                                  height));
        } else {
            return (new Dimension(this.shortArray.length * Math.abs(
                    samplesPerPixel),
                                  height));
        }

    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle rctngl, int i, int i1) {
        return 10;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle rctngl, int i, int i1) {
        return 10;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    /**
     * Find the maximum value in a short array
     *
     * @param array - the short array to find max
     * @return - the maximum value in the array
     * @author Khoi Le
     */
    private short findMax(short[] array) {
        short result = Short.MIN_VALUE;
        for (short i : array) {
            if (i > result) {
                result = i;
            }
        }
        return result;
    }
}
