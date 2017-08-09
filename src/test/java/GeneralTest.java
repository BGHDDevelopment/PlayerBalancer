import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeneralTest {
    @Test
    public void test() throws Exception {
        //Nothing to test
    }

    public String checkVersion() {
        try {
            final HttpURLConnection con = (HttpURLConnection)new URL("http://www.spigotmc.org/api/general.php").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.getOutputStream().write("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=10788".getBytes("UTF-8"));
            final String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            if (version.length() <= 7) {
                return version;
            }
        }
        catch (Exception ignored) {}

        return null;
    }
}
