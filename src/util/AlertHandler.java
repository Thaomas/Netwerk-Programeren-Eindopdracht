package util;

import javafx.scene.control.Alert;


public class AlertHandler {

    /**
     * Helper Function to display an error message whenever a possible error could occur.
     * The method has been made static in order to allow access from all necessary classes and to prevent clutter.
     * @param alertType The type of message to display, error, warnings.
     * @param title The title of the alert box.
     * @param header The header inside of the alert box.
     * @param context The message to display on the bottom of the box.
     */
    public static void show(Alert.AlertType alertType, String title, String header, String context) {
        Alert infoDialog = new Alert(alertType);
        infoDialog.setTitle(title);
        infoDialog.setHeaderText(header);
        infoDialog.setContentText(context);
        infoDialog.showAndWait();
    }
}
