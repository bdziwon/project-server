package util;

import util.interfaces.DatabaseSqlInterface;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Issue implements Cloneable, DatabaseSqlInterface, Serializable {


    private int id              = -1;
    private int projectId       = -1;
    private String title        = "Brak tytułu";
    private String description  = "Brak opisu";
    private String priority     = "ZWYKŁY";

    public Issue(){

    }

    public Issue(int id, int projectId, String title, String description, String priority){
        this.id=id;
        this.projectId=projectId;
        this.title=title;
        this.description=description;
        this.priority=priority;
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

    public String getPriority(){
        return this.priority;
    }

    public void setPriority(String priority){
        this.priority=priority;
    }

    public int getId() {
        return id;
    }

    @Override
    public String makeUpdateSql() {
        String sql =
                "UPDATE issue " +
                        "SET " +
                        "title = '"+getTitle()+"', " +
                        "description = '"+getDescription()+"', " +
                        "priority = '"+getPriority()+"' "+
                        "WHERE id = "+getId();
        return sql;
    }

    @Override
    public String makeDeleteSql() {
        String sql =
                "DELETE FROM issue WHERE id="+getId();
        return sql;
    }

    @Override
    public String makeInsertSql() {
        String sql =
                "INSERT INTO issue(id_project,title,description,priority) " +
                        "VALUES ('"+getProjectId()+"','"+getTitle()+"','"+getDescription()+"','"+getPriority()+"')";

        return sql;
    }

    @Override
    public String makeSelectSql() {
        String sql =
                "SELECT * FROM issue WHERE id = "+getId();
        return sql;
    }

    @Override
    public int setId(int id) {
        this.id = id;
        return this.id;
    }

    @Override
    public Issue resultSetToObject(ResultSet resultSet) {
        Issue issue = null;
        try {
            issue = new Issue();
            issue.setId(resultSet.getInt(1));
            issue.setProjectId(resultSet.getInt(2));
            issue.setTitle(resultSet.getString(3));
            issue.setDescription(resultSet.getString(4));
            issue.setPriority(resultSet.getString(5));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return issue;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}
