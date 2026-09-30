package com.libraryerp.dao;

import com.libraryerp.config.DB;
import java.sql.*;

public class AdminDAO {
    public boolean addBook(String title,String author,String isbn,String code,String dept,
                           String rack,String shelf,int total) throws SQLException {
        String sql="INSERT INTO books(title,author,isbn,department_code,department_name,rack_no,shelf_no,total_copies,available_copies) VALUES(?,?,?,?,?,?,?,?,?)";
        try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,title);p.setString(2,author);p.setString(3,isbn);p.setString(4,code);
            p.setString(5,dept);p.setString(6,rack);p.setString(7,shelf);p.setInt(8,total);p.setInt(9,total);
            return p.executeUpdate()==1;
        }
    }
    public boolean deleteBook(int id) throws SQLException {
        try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement("DELETE FROM books WHERE book_id=?")){
            p.setInt(1,id);return p.executeUpdate()==1;
        }
    }
}
