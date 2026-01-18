package me.marin.nbdebug;

import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.util.ExceptionUtil;

import java.util.Locale;
import java.util.prefs.Preferences;

public class NBSettings {

    public static String getSettings() {
        StringBuilder sb = new StringBuilder();

        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        sb.append("Ninjabrain Bot settings:\n");

        Preferences pref = Preferences.userRoot().node("ninjabrainbot");

        try {
            for (String key : pref.keys()) {
                String value;
                try {
                    value = pref.get(key, null);
                } catch (Exception ignored) {
                    continue;
                }

                try {
                    int intValue = pref.getInt(key, Integer.MIN_VALUE);
                    if (intValue != Integer.MIN_VALUE || value.equals(String.valueOf(Integer.MIN_VALUE))) {
                        sb.append(String.format("%s: %d", key, intValue)).append("\n");
                        continue;
                    }
                } catch (Exception ignored) {}

                try {
                    float floatValue = pref.getFloat(key, Float.NaN);
                    if (!Float.isNaN(floatValue) || value.equals("NaN")) {
                        sb.append(String.format("%s: %.8f", key, floatValue)).append("\n");
                        continue;
                    }
                } catch (Exception ignored) {}

                // Default to string
                sb.append(String.format("%s: %s", key, value)).append("\n");
            }
        } catch (Exception e) {
            Jingle.log(Level.ERROR, "Failed to get Ninjabrain Bot settings:\n" + ExceptionUtil.toDetailedString(e));
            return null;
        }

        Locale.setDefault(locale);
        return sb.toString();
    }

}
