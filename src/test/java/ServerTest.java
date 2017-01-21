import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ServerTest {


    @Test
    public void getPortShouldReturnDefault() {
        Server server = new Server();
        int test = server.getPort(new String[]{"123s"});
        assertThat(test).isEqualTo(server.getDefaultPort());
    }

    @Test
    public void getPortShouldReturnParameter() {
        Server server = new Server();
        int test = server.getPort(new String[]{"4560"});
        assertThat(test).isEqualTo(4560);
    }
}
