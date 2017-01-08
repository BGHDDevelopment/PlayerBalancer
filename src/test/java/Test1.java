import me.jaimemartz.lobbybalancer.connection.ProviderType;
import org.junit.Test;

public class Test1 {
    //@Test
    public void test1() {
        for (int i = 0; i <= 500; i++) {
            int port = (int) Math.floor(Math.random() * (0xFFFF + 1));
            System.out.println(port);

            if (port < 0 || port > 0xFFFF) {
                throw new IllegalArgumentException("port out of range:" + port);
            }
        }
    }

    @Test
    public void test2() {
        for (ProviderType provider : ProviderType.values()) {
            System.out.println(String.format("Provider %s: %s", provider.name(), provider.getDescription()));
        }
    }
}
