package com.jaimemartz.playerbalancer.utils;

import lombok.Data;
import lombok.Getter;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;

@Data
public final class GuestPaste {
    private final String key;
    private final String code;

    private String name = null;
    private String format = null;
    private Expiration expiration = null;
    private Exposure exposure = null;

    public GuestPaste(String key, String code) {
        this.key = key;
        this.code = code;
    }

    public URL paste() throws Exception {
        URL url = new URL("https://pastebin.com/api/api_post.php");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        List<SimpleEntry<String, String>> params = new LinkedList<>();
        params.add(new SimpleEntry<>("api_dev_key", key));
        params.add(new SimpleEntry<>("api_option", "paste"));
        params.add(new SimpleEntry<>("api_paste_code", code));

        if (name != null) {
            params.add(new SimpleEntry<>("api_paste_name", name));
        }

        if (format != null) {
            params.add(new SimpleEntry<>("api_paste_format", format));
        }

        if (expiration != null) {
            params.add(new SimpleEntry<>("api_paste_expire_date", expiration.value));
        }

        if (exposure != null) {
            params.add(new SimpleEntry<>("api_paste_private", String.valueOf(exposure.value)));
        }

        StringBuilder output = new StringBuilder();
        for (SimpleEntry<String, String> entry : params) {
            if (output.length() > 0)
                output.append('&');

            output.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            output.append('=');
            output.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        con.setDoOutput(true);
        try (DataOutputStream dos = new DataOutputStream(con.getOutputStream())) {
            dos.writeBytes(output.toString());
            dos.flush();
        }

        int status = con.getResponseCode();
        if (status >= 200 && status < 300) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }

                try {
                    return new URL(response.toString());
                } catch (MalformedURLException e) {
                    throw new PasteException(response.toString());
                }
            }
        } else {
            throw new PasteException("Unexpected response code " + status);
        }
    }

    public enum Expiration {
        NEVER("N"),
        TEN_MINUTES("10M"),
        ONE_HOUR("1H"),
        ONE_DAY("1D"),
        ONE_WEEK("1W"),
        TWO_WEEKS("2W"),
        ONE_MONTH("1M"),
        SIX_MONTHS("6M"),
        ONE_YEAR("1Y");

        @Getter
        private final String value;
        Expiration(String value) {
            this.value = value;
        }
    }

    public enum Exposure {
        PUBLIC(0),
        UNLISTED(1),
        PRIVATE(2);

        @Getter
        private final int value;
        Exposure(int value) {
            this.value = value;
        }
    }

    public class PasteException extends Exception {
        public PasteException(String response) {
            super(response);
        }
    }
}
