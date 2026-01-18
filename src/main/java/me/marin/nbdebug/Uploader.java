package me.marin.nbdebug;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.util.ExceptionUtil;
import xyz.duncanruns.jingle.util.HttpClientUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

public class Uploader {

    private static final int TIMEOUT_MS = 10000;
    private static final Gson GSON = new Gson();

    public static JsonObject uploadSettings() throws IOException {
        HttpPost request = new HttpPost("https://api.mclo.gs/1/log");

        List<NameValuePair> pairs = Collections.singletonList(
                new BasicNameValuePair("content", getSettings())
        );
        request.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT_MS)
                .setSocketTimeout(TIMEOUT_MS)
                .build();
        request.setConfig(config);

        HttpResponse response = HttpClientUtil.getHttpClient().execute(request);
        HttpEntity entity = response.getEntity();

        return GSON.fromJson(new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8), JsonObject.class);
    }

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
