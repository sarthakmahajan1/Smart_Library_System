package com.libraryerp.dao;

import com.libraryerp.config.DB;
import com.libraryerp.model.Student;
import java.sql.*;

public class StudentDAO {
    public Student find(String id) throws SQLException {
        String sql="SELECT student_id,name,department,semester,email,phone FROM students WHERE student_id=?";
        try(Connection c=DB.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,id);
            try(ResultSet r=p.executeQuery()){
                if(r.next()) return new Student(r.getString(1),r.getString(2),r.getString(3),
                        r.getInt(4),r.getString(5),r.getString(6));
            }
        }
        return null;
    }
}
