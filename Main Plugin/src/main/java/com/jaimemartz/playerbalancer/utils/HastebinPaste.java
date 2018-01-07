package com.jaimemartz.playerbalancer.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class HastebinPaste {
    private final String site;
    private final String code;

    public HastebinPaste(String site, String code) {
        this.site = site.endsWith("/") ? site : site + "/";
        this.code = code;
    }

    public URL paste() throws Exception {
        URL url = new URL(site + "documents");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        con.setDoOutput(true);
        try (DataOutputStream dos = new DataOutputStream(con.getOutputStream())) {
            dos.writeBytes(code);
            dos.flush();
        }

        int status = con.getResponseCode();
        if (status >= 200 && status < 300) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                JsonObject root = new JsonParser().parse(br.readLine()).getAsJsonObject();
                return new URL(site + root.getAsJsonPrimitive("key").getAsString());
            }
        } else {
            throw new Exception("Unexpected response code " + status);
        }
    }
}
