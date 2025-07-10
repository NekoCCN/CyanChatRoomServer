package cc.nekocc.cyanchatroomserver.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import java.util.Properties;

public class HikariDataSourceFactory extends UnpooledDataSourceFactory
{
    private static String database_url_;
    private static String database_username_;
    private static String database_password_;
    private static boolean initialized_ = false;

    public static void initialize(String database_url, String database_username, String database_password)
    {
        database_url_ = database_url;
        database_username_ = database_username;
        database_password_ = database_password;

        initialized_ = true;
    }

    public HikariDataSourceFactory()
    {
        if (!initialized_)
        {
            throw new IllegalStateException("HikariDataSourceFactory is not initialized. " +
                    "Please call initialize() with database connection parameters before using it.");
        }

        Properties hikari_properties = new Properties();
        try
        {
            hikari_properties.load(getClass().getClassLoader().getResourceAsStream("hikari.properties"));
        } catch (Exception e)
        {
            throw new RuntimeException("Failed to load HikariCP properties file.", e);
        }

        HikariConfig config = new HikariConfig(hikari_properties);
        this.dataSource = new HikariDataSource(config);
    }
}