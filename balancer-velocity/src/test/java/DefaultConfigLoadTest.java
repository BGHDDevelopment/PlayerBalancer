import com.google.common.reflect.TypeToken;
import com.jaimemartz.playerbalancer.velocity.settings.SettingsHolder;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class DefaultConfigLoadTest {
    private URL file;

    @Before
    public void before() throws IOException {
        file = getClass().getResource("velocity.conf");
    }

    @Test
    public void test() throws IOException, ObjectMappingException {
        HoconConfigurationLoader loader = HoconConfigurationLoader
                .builder()
                .setURL(file)
                .build();

        CommentedConfigurationNode node = loader.load();
        SettingsHolder settings = node.getValue(TypeToken.of(SettingsHolder.class));

        System.out.println(settings);
    }
}
