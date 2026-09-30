package com.libraryerp.dao;

import com.libraryerp.config.DB;
import java.sql.*;

public class AuthDAO {
    public boolean studentLogin(String id, String password) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE student_id=? AND password=? AND active=TRUE";
        try (Connection c=DB.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,id); p.setString(2,password);
            try(ResultSet r=p.executeQuery()){ return r.next(); }
        }
    }

    public boolean adminLogin(String id, String password) throws SQLException {
        String sql = "SELECT 1 FROM admins WHERE admin_id=? AND password=?";
        try (Connection c=DB.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,id); p.setString(2,password);
            try(ResultSet r=p.executeQuery()){ return r.next(); }
        }
    }
}
