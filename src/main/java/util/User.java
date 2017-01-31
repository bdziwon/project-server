package util;

import util.interfaces.DatabaseSqlInterface;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;


public class User implements DatabaseSqlInterface, Serializable {

    private int    id       = -1;
    private String name     = "pusto";
    private String surname  = "pusto";
    private String jobTitle = "PROGRAMISTA";

    public User(){

    }

    public User(int id, String name, String surname, String jobTitle){
        this.id=id;
        this.surname=surname;
        this.jobTitle=jobTitle;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getJobTitle() {
        return this.jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @Override
    public String makeUpdateSql() {
        String sql =
                "UPDATE user_t " +
                        "SET " +
                        "name = '" + getName() + "', " +
                        "surname = '" + getSurname() + "', " +
                        "jobTitle = '" + getJobTitle() + "' " +
                        "WHERE id = " + getId();
        return sql;
    }

    @Override
    public String makeDeleteSql() {
        String sql =
                "DELETE FROM user_t WHERE id=" + getId();
        return sql;
    }

    @Override
    public String makeInsertSql() {
        String sql =
                "INSERT INTO user_t(name,surname,jobTitle) " +
                        "VALUES ('" + getName() + "','" + getSurname() + "','" + getJobTitle() + "')";

        return sql;
    }

    @Override
    public String makeSelectSql() {
        String sql =
                "SELECT * FROM user_t WHERE id = " + getId();
        return sql;
    }

    @Override
    public int setId(int id) {
        this.id = id;
        return this.id;
    }

    @Override
    public User resultSetToObject(ResultSet resultSet) {
        User user = null;
        try {
            user = new User();
            user.setId(resultSet.getInt(1));
            user.setName(resultSet.getString(2));
            user.setSurname(resultSet.getString(3));
            user.setJobTitle(resultSet.getString(4));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public String toString() {
        return name + " " + surname + ", " + jobTitle;
    }
}
