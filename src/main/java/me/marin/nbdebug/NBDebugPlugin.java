package me.marin.nbdebug;

import com.google.common.io.Resources;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.JingleAppLaunch;
import xyz.duncanruns.jingle.gui.JingleGUI;
import xyz.duncanruns.jingle.gui.UploadedLogPane;
import xyz.duncanruns.jingle.plugin.PluginEvents;
import xyz.duncanruns.jingle.plugin.PluginManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;

public class NBDebugPlugin {

    private static JButton uploadButton;
    private static final AtomicBoolean uploading = new AtomicBoolean(false);

    public static void main(String[] args) throws IOException {
        JingleAppLaunch.launchWithDevPlugin(args, PluginManager.JinglePluginData.fromString(
                Resources.toString(Resources.getResource(NBDebugPlugin.class, "/jingle.plugin.json"), Charset.defaultCharset())
        ), NBDebugPlugin::initialize);
    }

    public static void initialize() {
        PluginEvents.MAIN_INSTANCE_CHANGED.register(NBDebugPlugin::updateButtonState);

        uploadButton = new JButton("Upload Ninjabrain Bot Settings");
        uploadButton.addActionListener((a) -> {
            uploading.set(true);
            updateButtonState();
            new Thread(() -> {
                try {
                    JsonObject response = Uploader.uploadSettings();
                    if (response.get("success").getAsBoolean()) {
                        String url = response.get("url").getAsString();

                        Jingle.log(Level.INFO, "(NBDebug) Uploaded NinjabrainBot settings to " + url);

                        Object[] options = new Object[]{"Copy URL", "Close"};
                        JEditorPane pane = new UploadedLogPane(url);

                        int button = JOptionPane.showOptionDialog(
                                null,
                                pane,
                                "NBDebug: Uploaded Settings",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                options,
                                null
                        );

                        if (button == 0) {
                            // copy to clipboard
                            StringSelection sel = new StringSelection(url);
                            try {
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(sel, null);
                            } catch (IllegalAccessError e) {
                                JOptionPane.showMessageDialog(null, "Failed to copy to clipboard.", "NBDebug: Copy to Clipboard Failed", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        String error = response.get("error").getAsString();
                        JOptionPane.showMessageDialog(null, String.format("Error while uploading settings:\n%s", error), "NBDebug: Upload Settings Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    Jingle.logError("Failed to upload log:", ex);
                    JOptionPane.showMessageDialog(null, "Error while uploading settings.", "NBDebug: Upload Settings Failed", JOptionPane.ERROR_MESSAGE);
                }

                uploading.set(false);
                updateButtonState();
            }, "log-uploader").start();
        });

        updateButtonState();
        JingleGUI.get().registerQuickActionButton(0, () -> uploadButton);
    }

    private static void updateButtonState() {
        uploadButton.setEnabled(!uploading.get() && Jingle.getMainInstance().isPresent());
    }
}
