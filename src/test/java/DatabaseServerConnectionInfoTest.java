import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;
import util.DatabaseServerConnectionInfo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Created by Bartłomiej Dziwoń on 21.01.2017.
 */
public class DatabaseServerConnectionInfoTest {

    @Test
    public void makeDbLinksShouldReturnProperDbLink() {
        String expected = "jdbc:mysql://localhost:3306/?serverTimezone=Europe/Warsaw";
        DatabaseServerConnectionInfo connectionInfo = new DatabaseServerConnectionInfo();
        connectionInfo.setHostname("localhost");
        connectionInfo.setPort("3306");
        assertThat(connectionInfo.makeDbLink()).isEqualTo(expected);
    }

    @Test
    public void makeDbLinkShouldThrowException() {
        final DatabaseServerConnectionInfo connectionInfo = new DatabaseServerConnectionInfo();
        ThrowableAssert.ThrowingCallable makeDbLinkAction = () -> connectionInfo.makeDbLink();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(makeDbLinkAction);
    }
}
