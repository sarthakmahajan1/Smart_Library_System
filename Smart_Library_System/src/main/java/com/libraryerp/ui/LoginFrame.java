package com.libraryerp.ui;

import com.libraryerp.dao.AuthDAO;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField id=new JTextField();
    private final JPasswordField pass=new JPasswordField();
    public LoginFrame(){
        setTitle("Smart Library ERP - Login");
        setSize(430,260);setLocationRelativeTo(null);setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel p=new JPanel(new GridLayout(4,2,10,10));p.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
        p.add(new JLabel("Student ID:"));p.add(id);
        p.add(new JLabel("Password:"));p.add(pass);
        JButton login=new JButton("Student Login"), admin=new JButton("Admin Login");
        p.add(login);p.add(admin);add(p);
        login.addActionListener(e->student());
        admin.addActionListener(e->admin());
    }
    private void student(){
        try{
            if(new AuthDAO().studentLogin(id.getText(),new String(pass.getPassword()))){
                new DashboardFrame(id.getText()).setVisible(true);dispose();
            }else JOptionPane.showMessageDialog(this,"Invalid student login.");
        }catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);}
    }
    private void admin(){
        try{
            if(new AuthDAO().adminLogin(id.getText(),new String(pass.getPassword()))){
                new AdminDashboardFrame().setVisible(true);dispose();
            }else JOptionPane.showMessageDialog(this,"Invalid admin login.");
        }catch(Exception ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);}
    }
}
