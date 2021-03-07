import com.jaimemartz.playerbalancer.utils.HastebinPaste;
import org.junit.Test;

import java.net.URL;
import static org.junit.Assert.*;

public class HastebinPasteTest {
    @Test
    public void test() throws Exception {
        HastebinPaste paste = new HastebinPaste("https://haste.zneix.eu/",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed " +
                        "iaculis, sapien et vehicula tristique, diam libero bibendum " +
                        "nunc, et rutrum nisl nulla quis diam. Cras ipsum enim, molestie" +
                        " eget bibendum nec, porta quis ex. Nunc ac sem lorem. Duis eget" +
                        " vestibulum libero. Phasellus vitae venenatis arcu, ac volutpat " +
                        "sem. Nunc porttitor lacus nulla, vitae dictum justo porta at. " +
                        "Aliquam erat volutpat. Vestibulum aliquet eget diam eget commodo." +
                        " Integer facilisis ipsum sit amet sem pharetra ultrices. Nulla diam" +
                        " orci, posuere malesuada ante non, elementum vehicula libero."
        );

        URL pasteUrl = paste.paste();
        assertNotNull(pasteUrl);
    }
}
