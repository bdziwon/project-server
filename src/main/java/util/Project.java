package util;

import util.interfaces.DatabaseSqlInterface;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Project implements DatabaseSqlInterface, Serializable {

    private int              id               = -1;
    private String           title            = "Brak tytułu";
    private String           description      = "Brak opisu";
    private ArrayList<Issue> issues           = new ArrayList<Issue>();
    private ArrayList<User>  users            = new ArrayList<User>();


    public Project(){

    }

    public Project(int id, String title, String description){
        this.id=id;
        this.title=title;
        this.description=description;
    }


    public String getTitle(){
        return this.title;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public int getId() {
        return id;
    }

    public void addIssue(Issue issue) {
        issues.add(issue);
    }

    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public String makeUpdateSql() {
        String sql =
                "UPDATE project " +
                        "SET " +
                        "title = '"+getTitle()+"', " +
                        "description = '"+getDescription()+"' " +
                        "WHERE id = "+getId();
        return sql;
    }

    @Override
    public String makeDeleteSql() {
        String sql =
                "DELETE FROM project WHERE id="+getId();
        return sql;
    }

    public String makeInsertSql() {
        String sql =
                "INSERT INTO project(title, description) " +
                        "VALUES ('"+getTitle()+"','"+getDescription()+"')";

        return sql;
    }

    @Override
    public String makeSelectSql() {
        String sql =
                "SELECT * FROM project WHERE id = "+getId();
        return sql;
    }


    @Override
    public int setId(int id) {
        this.id = id;
        return this.id;
    }

    @Override
    public Project resultSetToObject(ResultSet resultSet) {
        Project project = null;
        try {
            project = new Project();
            project.setId(resultSet.getInt(1));
            project.setTitle(resultSet.getString(2));
            project.setDescription(resultSet.getString(3));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return project;
    }

    public ArrayList<Issue> getIssues() {
        return issues;
    }

    public ArrayList<User> getUsers() {
        return users;
    }
}
