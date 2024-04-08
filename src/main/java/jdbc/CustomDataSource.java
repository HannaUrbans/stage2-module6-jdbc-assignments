package jdbc;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver;
    private final String url;
    private final String name;
    private final String password;

    private CustomDataSource(String driver, String url, String name, String password) {
        this.driver = driver;
        this.url = url;
        this.name = name;
        this.password = password;
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load JDBC driver", e);
        }
    }

    public static CustomDataSource getInstance(String driver, String url, String name, String password) {
        if (instance == null) {
            synchronized (CustomDataSource.class) {
                if (instance == null) {
                    instance = new CustomDataSource(driver, url, name, password);
                }
            }
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, name, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        // Заглушка
        return null;
    }
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        // Заглушка
    }
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        // Заглушка
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        // Заглушка
        return 0;
    }
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        // Заглушка
        throw new SQLFeatureNotSupportedException("Method getParentLogger() is not supported");
    }
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("Method unwrap(Class<T>) is not supported");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new SQLException("Method isWrapperFor(Class<?>) is not supported");
    }
}