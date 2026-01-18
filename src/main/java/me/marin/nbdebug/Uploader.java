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
import xyz.duncanruns.jingle.util.HttpClientUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class Uploader {

    private static final int TIMEOUT_MS = 10000;
    private static final Gson GSON = new Gson();

    public static JsonObject uploadSettings() throws IOException {
        HttpPost request = new HttpPost("https://api.mclo.gs/1/log");

        String sb = NBSettings.getSettings() + "\n" +
                    MinecraftOptions.getSensitivity();

        List<NameValuePair> pairs = Collections.singletonList(
                new BasicNameValuePair("content", sb)
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



}
