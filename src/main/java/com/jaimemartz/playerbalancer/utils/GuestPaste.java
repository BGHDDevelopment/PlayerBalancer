package com.jaimemartz.playerbalancer.utils;

import lombok.Data;
import lombok.Getter;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Data
public class GuestPaste {
    private final String key;
    private final String code;

    private String name;
    private String format;
    private Expiration expiration;
    private Exposure exposure;

    public GuestPaste(String key, String code) {
        this.key = key;
        this.code = code;
    }

    public String paste() throws Exception {
        HttpPost request = new HttpPost("https://pastebin.com/api/api_post.php");

        List<BasicNameValuePair> params = new LinkedList<>();
        params.add(new BasicNameValuePair("api_dev_key", key));
        params.add(new BasicNameValuePair("api_option", "paste"));
        params.add(new BasicNameValuePair("api_paste_code", code));

        if (name != null) {
            params.add(new BasicNameValuePair("api_paste_name", name));
        }

        if (format != null) {
            params.add(new BasicNameValuePair("api_paste_format", format));
        }

        if (expiration != null) {
            params.add(new BasicNameValuePair("api_paste_expire_date", expiration.getValue()));
        }

        if (exposure != null) {
            params.add(new BasicNameValuePair("api_paste_private", String.valueOf(exposure.getValue())));
        }

        HttpEntity entity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        request.setEntity(entity);

        Response response = new Response();
        return client.execute(request, response);
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

    public static class Response implements ResponseHandler<String> {
        @Override
        public String handleResponse(HttpResponse response) throws IOException {
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                try {
                    return entity != null ? EntityUtils.toString(entity) : "";
                } catch (ParseException | IOException e) {
                    return "Error : " + e.getMessage();
                }
            } else {
                return "Unexpected response status: " + status;
            }
        }
    }

    private static final HttpClient client = HttpClients.createDefault();
}
