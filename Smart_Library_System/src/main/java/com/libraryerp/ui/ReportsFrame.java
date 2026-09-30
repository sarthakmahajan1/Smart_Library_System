package com.libraryerp.ui;

import com.libraryerp.config.DB;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReportsFrame extends JFrame {
    private final JTable table=new JTable();
    public ReportsFrame(){
        setTitle("Library Reports");setSize(900,500);setLocationRelativeTo(null);
        JPanel p=new JPanel(new BorderLayout());
        JPanel top=new JPanel();
        JButton issued=new JButton("Issued Books"), overdue=new JButton("Overdue"), fines=new JButton("Fines");
        top.add(issued);top.add(overdue);top.add(fines);p.add(top,BorderLayout.NORTH);p.add(new JScrollPane(table),BorderLayout.CENTER);add(p);
        issued.addActionListener(e->load("SELECT i.issue_id,i.student_id,b.title,i.issue_date,i.due_date,i.status FROM issued_books i JOIN books b ON b.book_id=i.book_id"));
        overdue.addActionListener(e->load("SELECT i.issue_id,i.student_id,b.title,i.due_date,DATEDIFF(CURDATE(),i.due_date) late_days FROM issued_books i JOIN books b ON b.book_id=i.book_id WHERE i.status='ISSUED' AND i.due_date<CURDATE()"));
        fines.addActionListener(e->load("SELECT fine_id,student_id,issue_id,late_days,amount,paid,paid_date FROM fines ORDER BY fine_id DESC"));
    }
    private void load(String sql){
        try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement(sql);ResultSet r=p.executeQuery()){
            ResultSetMetaData md=r.getMetaData();DefaultTableModel m=new DefaultTableModel();
            for(int i=1;i<=md.getColumnCount();i++)m.addColumn(md.getColumnName(i));
            while(r.next()){Object[] row=new Object[md.getColumnCount()];for(int i=0;i<row.length;i++)row[i]=r.getObject(i+1);m.addRow(row);}
            table.setModel(m);
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }
}
