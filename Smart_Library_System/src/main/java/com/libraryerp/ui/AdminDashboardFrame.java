package com.libraryerp.ui;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {
    public AdminDashboardFrame(){
        setTitle("Smart Library ERP - Admin Dashboard");
        setSize(700,450);setLocationRelativeTo(null);setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel p=new JPanel(new GridLayout(4,1,15,15));
        p.setBorder(BorderFactory.createEmptyBorder(35,100,35,100));
        JButton books=new JButton("Manage Books");
        JButton reports=new JButton("Reports");
        JButton students=new JButton("Student Management");
        JButton logout=new JButton("Logout");
        p.add(books);p.add(students);p.add(reports);p.add(logout);add(p);
        books.addActionListener(e->new ManageBooksFrame().setVisible(true));
        students.addActionListener(e->JOptionPane.showMessageDialog(this,"Student management can be extended with CRUD operations."));
        reports.addActionListener(e->new ReportsFrame().setVisible(true));
        logout.addActionListener(e->{new LoginFrame().setVisible(true);dispose();});
    }
}
