package com.aemstore.core.models.impl;

import com.aemstore.core.models.User;
import com.aemstore.core.models.UserService;
import org.apache.commons.codec.digest.Crypt;
import org.apache.commons.codec.digest.DigestUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component(service = UserService.class)
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private String hashPassword(String plaintextPassword) {
        // Generate a salt
        String salt = Crypt.crypt(plaintextPassword);
        // Hash the password using bcrypt with the generated salt
        return Crypt.crypt(plaintextPassword, salt);
    }
    @Reference(target = "(datasource.name=mysqlDB)")
    private DataSource dataSource;

    @Override
    public boolean userExists(String email) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT COUNT(*) FROM user WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error checking if user exists", e);
        }
        return false;
    }

    @Override
    public boolean createUser(User user) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "INSERT INTO user (email, fname, lname, password_hash) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, user.getEmail());
                stmt.setString(2, user.getFirstname());
                stmt.setString(3, user.getLastname());
                stmt.setString(4, hashPassword(user.getPassword())); // Hash the password
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            LOGGER.error("Error creating user", e);
        }
        return false;
    }
}
