/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2015
 *
 * Name: Khoi Le & Khai Nguyen
 * Date: Nov 1, 2015
 * Time: 7:41:09 PM
 *
 * Project: csci205_hw
 * Package: hw03
 * File: VerticalScaleComponent
 * Description:
 *
 * ****************************************
 */
package hw03.Utility;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/**
 * Draw the vertical ruler using a custom class extent from Jcomponent4
 *
 * @author khainguyen
 */
public class VerticalScaleComponent extends JComponent {
    private double maxHeight;

    public VerticalScaleComponent(double height, int bits) {
        height -= 18;
        if (bits == 8) {
            this.maxHeight = 127 / (height / 4) * 1.2;
        } else {
            this.maxHeight = 32767 / (height / 2) * 1.2;
        }

    }

    /**
     * Draw the vertical ruler
     *
     * @param g
     * @author khainguyen
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        int midPoint = getHeight() / 2 - 9;
        int distance = (int) ((getHeight() - 18) / (8 * 1.2));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Color.WHITE);
        for (int i = 0; i < 9; i++) {
            g2.drawString(Double.toString((i - 4) * 0.25), 0,
                          midPoint + (4 - i) * distance + g2.getFont().getSize() / 2);
            g2.drawLine(getWidth() - 5, midPoint + (4 - i) * distance,
                        getWidth(),
                        midPoint + (4 - i) * distance);
        }
    }
}
