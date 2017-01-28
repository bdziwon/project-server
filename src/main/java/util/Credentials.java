package util;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Credentials implements Serializable {
    private String login;

    private String password;
    public Credentials(String login, String password) {
        this.login    = login;
        this.password = password;
    }

    public boolean loginContainsUnallowedChars() {
        Pattern pattern = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(login);
        if (matcher.find()) {
            return true;
        }
        return  false;
    }

    public boolean passwordContainsUnallowedChars() {
        Pattern pattern = Pattern.compile("[^a-z0-9_]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    public boolean loginHaveAllowedLength() {
        if (login.length() < 4) {
            return false;
        }
        return true;
    }

    public boolean passwordHaveAllowedLength() {
        if (password.length() < 4) {
            return false;
        }
        return true;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


}
