package util;

/**
 * Created by Bartłomiej Dziwoń on 21.01.2017.
 */
public class DatabaseServerConnectionInfo {

    private String hostname = null;
    private String port = null;
    private String username = null;
    private String password = "";

    public DatabaseServerConnectionInfo() {
        super();
    }

    public DatabaseServerConnectionInfo(String hostname, String port, String username, String password) {
        this(hostname,port);
        this.username = username;
        this.password = password;
    }

    public DatabaseServerConnectionInfo(String hostname, String port) {
        this.hostname = hostname;
        this.port = port;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String makeDbLink() throws IllegalArgumentException {
        if (getHostname() == null || getPort() == null) {
            throw new IllegalArgumentException("Illegal hostname / port  -> cannot be null");
        }
        String link = "jdbc:oracle:thin:@"+getHostname()+":"+getPort()+":XE";
        System.out.println(link);
        return link;
    }

}
