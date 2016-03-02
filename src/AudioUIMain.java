/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2015
 *
 * Name: Khoi Le & Khai Nguyen
 * Date: Oct 29, 2015
 * Time: 5:30:15 PM
 *
 * Project: csci205_hw
 * Package: hw03
 * File: AudioUIMain
 * Description:
 *
 * ****************************************
 */
package hw03;

import hw03.view.AudioUIView;
import hw03.Model.AudioUIModel;
import hw03.Controller.AudioUIController;

/**
 *
 * @author khainguyen
 */
public class AudioUIMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Window classic look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Window Classic".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AudioUIView.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AudioUIView.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AudioUIView.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AudioUIView.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            AudioUIView theView = new AudioUIView();
            AudioUIModel theModel = new AudioUIModel();
            theView.setVisible(true);
            AudioUIController theController = new AudioUIController(theModel,
                                                                    theView);
        });
    }

}
