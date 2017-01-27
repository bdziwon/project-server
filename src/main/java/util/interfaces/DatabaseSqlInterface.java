package util.interfaces;

import java.sql.ResultSet;

/**
 * Created by Bartłomiej Dziwoń on 22.01.2017.
 */
public interface DatabaseSqlInterface {
    public String makeUpdateSql();
    public String makeDeleteSql();
    public String makeInsertSql();
    public String makeSelectSql();
    public Object resultSetToObject(ResultSet resultSet);
    public int setId(int id);
}
