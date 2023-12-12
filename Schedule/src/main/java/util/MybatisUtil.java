package util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

public final class MybatisUtil {
    public static SqlSessionFactory getSqlSessionFactory() {
        try {
            InputStream is = Resources.getResourceAsStream("mybatis-config.xml");
            return new SqlSessionFactoryBuilder().build(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void sqlRunner(String filePath) {
        try (Reader resourceAsReader = Resources.getResourceAsReader(filePath);
             Connection connection = getSqlSessionFactory().openSession(true).getConnection()) {
            new ScriptRunner(connection).runScript(resourceAsReader);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}