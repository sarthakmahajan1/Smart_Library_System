package com.libraryerp.dao;

import com.libraryerp.config.DB;
import com.libraryerp.model.IssuedBook;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class LibraryDAO {
    private static final int ISSUE_DAYS=14, MAX_RENEWALS=2, FINE_PER_DAY=5;

    public List<IssuedBook> myBooks(String studentId) throws SQLException {
        List<IssuedBook> out=new ArrayList<>();
        String sql="SELECT i.issue_id,i.book_id,b.title,i.issue_date,i.due_date,i.renewal_count,i.status "
                 +"FROM issued_books i JOIN books b ON b.book_id=i.book_id "
                 +"WHERE i.student_id=? ORDER BY i.status,i.due_date";
        try(Connection c=DB.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,studentId);
            try(ResultSet r=p.executeQuery()){
                while(r.next()) out.add(new IssuedBook(r.getInt(1),r.getInt(2),r.getString(3),
                    r.getDate(4).toLocalDate(),r.getDate(5).toLocalDate(),r.getInt(6),r.getString(7)));
            }
        }
        return out;
    }

    public boolean issue(int bookId,String studentId) throws SQLException {
        Connection c=DB.getConnection();
        try(c){
            c.setAutoCommit(false);
            try(PreparedStatement check=c.prepareStatement("SELECT available_copies FROM books WHERE book_id=? FOR UPDATE")){
                check.setInt(1,bookId);
                try(ResultSet r=check.executeQuery()){
                    if(!r.next() || r.getInt(1)<=0){c.rollback();return false;}
                }
            }
            try(PreparedStatement dup=c.prepareStatement(
                    "SELECT 1 FROM issued_books WHERE student_id=? AND book_id=? AND status='ISSUED'")){
                dup.setString(1,studentId);dup.setInt(2,bookId);
                try(ResultSet r=dup.executeQuery()){if(r.next()){c.rollback();return false;}}
            }
            try(PreparedStatement p=c.prepareStatement(
                    "INSERT INTO issued_books(student_id,book_id,issue_date,due_date) VALUES(?,?,?,?)")){
                p.setString(1,studentId);p.setInt(2,bookId);
                LocalDate now=LocalDate.now();
                p.setDate(3,Date.valueOf(now));p.setDate(4,Date.valueOf(now.plusDays(ISSUE_DAYS)));
                p.executeUpdate();
            }
            try(PreparedStatement p=c.prepareStatement("UPDATE books SET available_copies=available_copies-1 WHERE book_id=?")){
                p.setInt(1,bookId);p.executeUpdate();
            }
            c.commit(); return true;
        } catch(Exception e){try{c.rollback();}catch(Exception ignored){} throw e;}
    }

    public boolean renew(int issueId,String studentId) throws SQLException {
        String q="SELECT due_date,renewal_count,status FROM issued_books WHERE issue_id=? AND student_id=?";
        try(Connection c=DB.getConnection(); PreparedStatement p=c.prepareStatement(q)){
            p.setInt(1,issueId);p.setString(2,studentId);
            try(ResultSet r=p.executeQuery()){
                if(!r.next()) return false;
                LocalDate due=r.getDate(1).toLocalDate();
                int count=r.getInt(2);
                String status=r.getString(3);
                if(!"ISSUED".equals(status)||count>=MAX_RENEWALS||due.isBefore(LocalDate.now())) return false;
                LocalDate newDue=due.plusDays(ISSUE_DAYS);
                try(PreparedStatement u=c.prepareStatement(
                        "UPDATE issued_books SET due_date=?,renewal_count=renewal_count+1 WHERE issue_id=?")){
                    u.setDate(1,Date.valueOf(newDue));u.setInt(2,issueId);return u.executeUpdate()==1;
                }
            }
        }
    }

    public boolean returnBook(int issueId,String studentId) throws SQLException {
        Connection c=DB.getConnection();
        try(c){
            c.setAutoCommit(false);
            int bookId=-1;
            try(PreparedStatement p=c.prepareStatement(
                    "SELECT book_id FROM issued_books WHERE issue_id=? AND student_id=? AND status='ISSUED' FOR UPDATE")){
                p.setInt(1,issueId);p.setString(2,studentId);
                try(ResultSet r=p.executeQuery()){if(!r.next()){c.rollback();return false;}bookId=r.getInt(1);}
            }
            try(PreparedStatement p=c.prepareStatement(
                    "UPDATE issued_books SET status='RETURNED',return_date=? WHERE issue_id=?")){
                p.setDate(1,Date.valueOf(LocalDate.now()));p.setInt(2,issueId);p.executeUpdate();
            }
            try(PreparedStatement p=c.prepareStatement(
                    "UPDATE books SET available_copies=available_copies+1 WHERE book_id=?")){
                p.setInt(1,bookId);p.executeUpdate();
            }
            refreshFine(issueId,studentId,c);
            c.commit();return true;
        }catch(Exception e){try{c.rollback();}catch(Exception ignored){}throw e;}
    }

    public double outstandingFine(String studentId) throws SQLException {
        try(Connection c=DB.getConnection()){
            refreshAllFines(studentId,c);
            try(PreparedStatement p=c.prepareStatement(
                    "SELECT COALESCE(SUM(amount),0) FROM fines WHERE student_id=? AND paid=FALSE")){
                p.setString(1,studentId);
                try(ResultSet r=p.executeQuery()){r.next();return r.getDouble(1);}
            }
        }
    }

    private void refreshAllFines(String studentId,Connection c) throws SQLException {
        String q="SELECT issue_id,due_date,return_date,status FROM issued_books WHERE student_id=?";
        try(PreparedStatement p=c.prepareStatement(q)){
            p.setString(1,studentId);
            try(ResultSet r=p.executeQuery()){
                while(r.next()){
                    int issueId=r.getInt(1);
                    LocalDate due=r.getDate(2).toLocalDate();
                    LocalDate end=r.getDate(3)==null?LocalDate.now():r.getDate(3).toLocalDate();
                    refreshFine(issueId,studentId,c,due,end);
                }
            }
        }
    }

    private void refreshFine(int issueId,String studentId,Connection c) throws SQLException {
        String q="SELECT due_date,return_date FROM issued_books WHERE issue_id=? AND student_id=?";
        try(PreparedStatement p=c.prepareStatement(q)){
            p.setInt(1,issueId);p.setString(2,studentId);
            try(ResultSet r=p.executeQuery()){
                if(r.next()){
                    LocalDate due=r.getDate(1).toLocalDate();
                    LocalDate end=r.getDate(2)==null?LocalDate.now():r.getDate(2).toLocalDate();
                    refreshFine(issueId,studentId,c,due,end);
                }
            }
        }
    }

    private void refreshFine(int issueId,String studentId,Connection c,LocalDate due,LocalDate end) throws SQLException {
        long days=Math.max(0,ChronoUnit.DAYS.between(due,end));
        double amount=days*FINE_PER_DAY;
        String find="SELECT fine_id,paid FROM fines WHERE issue_id=? ORDER BY fine_id DESC LIMIT 1";
        try(PreparedStatement p=c.prepareStatement(find)){
            p.setInt(1,issueId);
            try(ResultSet r=p.executeQuery()){
                if(r.next()){
                    int id=r.getInt(1);boolean paid=r.getBoolean(2);
                    if(!paid){
                        try(PreparedStatement u=c.prepareStatement(
                                "UPDATE fines SET fine_date=?,late_days=?,amount=? WHERE fine_id=?")){
                            u.setDate(1,Date.valueOf(LocalDate.now()));u.setLong(2,days);u.setDouble(3,amount);u.setInt(4,id);u.executeUpdate();
                        }
                    }
                } else if(amount>0) {
                    try(PreparedStatement i=c.prepareStatement(
                            "INSERT INTO fines(issue_id,student_id,fine_date,late_days,amount) VALUES(?,?,?,?,?)")){
                        i.setInt(1,issueId);i.setString(2,studentId);i.setDate(3,Date.valueOf(LocalDate.now()));
                        i.setLong(4,days);i.setDouble(5,amount);i.executeUpdate();
                    }
                }
            }
        }
    }

    public boolean payOneFine(String studentId) throws SQLException {
        Connection c=DB.getConnection();
        try(c){
            c.setAutoCommit(false);
            int fineId=-1;double amount=0;
            try(PreparedStatement p=c.prepareStatement(
                    "SELECT fine_id,amount FROM fines WHERE student_id=? AND paid=FALSE AND amount>0 ORDER BY fine_id LIMIT 1 FOR UPDATE")){
                p.setString(1,studentId);
                try(ResultSet r=p.executeQuery()){if(!r.next()){c.rollback();return false;}fineId=r.getInt(1);amount=r.getDouble(2);}
            }
            String ref="ERP-"+System.currentTimeMillis();
            try(PreparedStatement p=c.prepareStatement(
                    "UPDATE fines SET paid=TRUE,paid_date=? WHERE fine_id=?")){
                p.setDate(1,Date.valueOf(LocalDate.now()));p.setInt(2,fineId);p.executeUpdate();
            }
            try(PreparedStatement p=c.prepareStatement(
                    "INSERT INTO payments(student_id,fine_id,amount,method,transaction_ref) VALUES(?,?,?,?,?)")){
                p.setString(1,studentId);p.setInt(2,fineId);p.setDouble(3,amount);
                p.setString(4,"ERP_SIMULATED");p.setString(5,ref);p.executeUpdate();
            }
            c.commit();return true;
        }catch(Exception e){try{c.rollback();}catch(Exception ignored){}throw e;}
    }
}
