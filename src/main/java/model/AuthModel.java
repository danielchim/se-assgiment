package model;

import context.ContextState;
import entity.Role;
import entity.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthModel {

    String dbUrl;
    String dbUsername;
    String dbPassword;

    public AuthModel() {
        ContextState.loadProperties();
        dbUrl = ContextState.getProperty("datasource.url");
        dbUsername = ContextState.getProperty("datasource.username");
        dbPassword = ContextState.getProperty("datasource.password");
    }

    public User login(User user) {
        String sql = "SELECT u.idUser, u.name, u.password, r.idRole, r.name AS roleName FROM user u " +
                "INNER JOIN Role r ON u.Role_idRole = r.idRole " +
                "WHERE u.name = ? AND u.password = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPassword());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("idUser");
                    String name = rs.getString("name");
                    int roleId = rs.getInt("idRole");
                    Role role = new Role();
                    user.setIdUser(id);
                    user.setName(name);
                    role.setIdRole(roleId);
                    user.setRole(role);
                    ContextState.setIsLoggedin(true);
                    return user;
                } else {
                    return null; // user not found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // user not found
    }
}
