package cc.nekocc.cyanchatroomserver.infrastructure.config;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * MyBatisUtil 显然是一个单例工具类，用于简化一些常见的 MyBatis 操作，结合异常处理
 */
public final class MyBatisUtil
{
    private static SqlSessionFactory sql_session_factory_;

    private MyBatisUtil()
    {  }

    /**
     * 在应用启动时调用, 用于初始化SqlSessionFactory。
     */
    public static void init()
    {
        if (sql_session_factory_ == null)
        {
            try
            {
                String resource = "mybatis-config.xml";
                InputStream inputStream = Resources.getResourceAsStream(resource);
                sql_session_factory_ = new SqlSessionFactoryBuilder().build(inputStream);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new RuntimeException("Fatal Error: Failed to initialize MyBatis.", e);
            }
        }
    }

    /**
     * 获取 SqlSessionFactory 实例。
     */
    public static SqlSessionFactory getSqlSessionFactory()
    {
        if (sql_session_factory_ == null)
        {
            init();
        }
        return sql_session_factory_;
    }

    /**
     * 执行一个带返回值的数据库查询操作 (SELECT)。
     * 自动处理 SqlSession 的获取和关闭。
     *
     * @param func 需要执行的数据库操作, R 是返回值类型
     * @return 数据库操作的返回值
     */
    public static <R> R executeQuery(Function<SqlSession, R> func)
    {
        try (SqlSession session = getSqlSessionFactory().openSession())
        {
            return func.apply(session);
        }
    }

    /**
     * 执行一个无返回值的数据库写操作 (INSERT, UPDATE, DELETE)。
     * 自动处理 SqlSession 的获取、事务提交和关闭。
     *
     * @param action 需要执行的数据库操作
     */
    public static void executeUpdate(Consumer<SqlSession> action)
    {
        try (SqlSession session = getSqlSessionFactory().openSession())
        {
            action.accept(session);
            session.commit();
        }
    }
}
