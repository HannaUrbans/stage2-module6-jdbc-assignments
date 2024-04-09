package jdbc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor

public class SimpleJDBCRepository {

    private CustomDataSource dataSource;

    public SimpleJDBCRepository(CustomDataSource dataSource) {
        this.dataSource = dataSource;
    }

    private static final String createUserSQL = "INSERT INTO public.myusers (firstName, lastName, age) VALUES (?, ?, ?)";
    private static final String updateUserSQL = "UPDATE public.myusers SET firstName=?, lastName=?, age=? WHERE id=?";
    private static final String deleteUser = "DELETE FROM public.myusers WHERE id=?";
    private static final String findUserByIdSQL = "SELECT * FROM public.myusers WHERE id=?";
    private static final String findUserByNameSQL = "SELECT * FROM public.myusers WHERE firstName || ' ' || lastName = ?";
    private static final String findAllUserSQL = "SELECT * FROM public.myusers";

    public Long createUser(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new SQLException("Creating user failed, no ID obtained.");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User findUserById(Long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByIdSQL)) {
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int age = rs.getInt("age");
                // Создание объекта User с использованием полученных данных из ResultSet
                return new User(id, firstName, lastName, age);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public User findUserByName(String userName) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByNameSQL)) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                long id = rs.getLong("id");
                String userFirstName = rs.getString("first_name");
                String userLastName = rs.getString("last_name");
                int age = rs.getInt("age");
                return new User(id, userFirstName, userLastName, age);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(findAllUserSQL)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                int age = rs.getInt("age");
                users.add(new User(id, firstName, lastName, age));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void updateUser(User user) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(updateUserSQL)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(Long userId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(deleteUser)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
