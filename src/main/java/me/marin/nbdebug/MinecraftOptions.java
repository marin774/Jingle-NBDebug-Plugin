package me.marin.nbdebug;

import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.instance.OpenedInstance;

import java.util.Locale;
import java.util.Optional;

public class MinecraftOptions {

    public static String getSensitivity() {
        Optional<OpenedInstance> optional = Jingle.getMainInstance();
        if (!optional.isPresent()) {
            return "No Minecraft instance found.";
        }

        OpenedInstance instance = optional.get();

        Optional<String> sensitivityO = instance.optionsTxt.getOption("mouseSensitivity");
        Optional<String> sensitivitySS = instance.standardSettings.getOption("mouseSensitivity");

        StringBuilder sb = new StringBuilder();

        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        if (sensitivityO.isPresent()) {
            try {
                float sensitivity = Float.parseFloat(sensitivityO.get());
                sb.append("options.txt mouseSensitivity: ").append(String.format("%.8f", sensitivity)).append("\n");
            } catch (NumberFormatException e) {
                sb.append("options.txt mouseSensitivity: Invalid (").append(sensitivityO.get()).append(")\n");
            }
        } else {
            sb.append("options.txt mouseSensitivity: Not found\n");
        }

        if (sensitivitySS.isPresent()) {
            try {
                float sensitivity = Float.parseFloat(sensitivitySS.get());
                sb.append("standardsettings.json mouseSensitivity: ").append(String.format("%.8f", sensitivity)).append("\n");
            } catch (NumberFormatException e) {
                sb.append("standardsettings.json mouseSensitivity: Invalid (").append(sensitivitySS.get()).append(")\n");
            }
        } else {
            sb.append("standardsettings.json mouseSensitivity: Not found or disabled\n");
        }

        Locale.setDefault(locale);
        return sb.toString();
    }

}
