package com.libraryerp.dao;

import com.libraryerp.config.DB;
import com.libraryerp.model.Book;
import java.sql.*;
import java.util.*;

public class BookDAO {
    public List<Book> search(String keyword, String dept) throws SQLException {
        List<Book> list=new ArrayList<>();
        String sql="SELECT * FROM books WHERE (title LIKE ? OR author LIKE ? OR isbn LIKE ?) "
                +"AND (?='' OR department_code=?) ORDER BY department_code,title";
        try(Connection c=DB.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            String k="%"+(keyword==null?"":keyword.trim())+"%";
            p.setString(1,k); p.setString(2,k); p.setString(3,k);
            p.setString(4,dept); p.setString(5,dept);
            try(ResultSet r=p.executeQuery()){
                while(r.next()) list.add(new Book(r.getInt("book_id"),r.getString("title"),
                    r.getString("author"),r.getString("isbn"),r.getString("department_code"),
                    r.getString("department_name"),r.getString("rack_no"),r.getString("shelf_no"),
                    r.getInt("total_copies"),r.getInt("available_copies")));
            }
        }
        return list;
    }

    public boolean add(Book b) throws SQLException {
        String sql="INSERT INTO books(title,author,isbn,department_code,department_name,rack_no,shelf_no,total_copies,available_copies) VALUES(?,?,?,?,?,?,?,?,?)";
        try(Connection c=DB.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,b.title()); p.setString(2,b.author()); p.setString(3,b.isbn());
            p.setString(4,b.departmentCode()); p.setString(5,b.departmentName());
            p.setString(6,b.rack()); p.setString(7,b.shelf()); p.setInt(8,b.totalCopies());
            p.setInt(9,b.availableCopies()); return p.executeUpdate()==1;
        }
    }

    public boolean delete(int id) throws SQLException {
        try(Connection c=DB.getConnection(); PreparedStatement p=c.prepareStatement("DELETE FROM books WHERE book_id=?")){
            p.setInt(1,id); return p.executeUpdate()==1;
        }
    }
}
