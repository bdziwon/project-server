/**
 * Created by Bartłomiej Dziwoń on 22.01.2017.
 */
public interface DatabaseSqlInterface {
    public String makeUpdateSql();
    public String makeDeleteSql();
    public String makeInsertSql();
    public int setId(int id);
}
