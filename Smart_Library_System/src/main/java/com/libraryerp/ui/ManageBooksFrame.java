package com.libraryerp.ui;

import com.libraryerp.dao.AdminDAO;
import javax.swing.*;
import java.awt.*;

public class ManageBooksFrame extends JFrame {
    private final JTextField title=new JTextField(), author=new JTextField(), isbn=new JTextField(),
        code=new JTextField(), dept=new JTextField(), rack=new JTextField(), shelf=new JTextField(), total=new JTextField("1");
    public ManageBooksFrame(){
        setTitle("Manage Books");setSize(600,500);setLocationRelativeTo(null);
        JPanel p=new JPanel(new GridLayout(9,2,8,8));p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        String[] labels={"Title","Author","ISBN","Department Code","Department Name","Rack","Shelf","Total Copies",""};
        JTextField[] fields={title,author,isbn,code,dept,rack,shelf,total};
        for(int i=0;i<8;i++){p.add(new JLabel(labels[i]));p.add(fields[i]);}
        JButton add=new JButton("Add Book");p.add(new JLabel());p.add(add);add(p);
        add.addActionListener(e->addBook());
    }
    private void addBook(){
        try{
            int n=Integer.parseInt(total.getText());
            boolean ok=new AdminDAO().addBook(title.getText(),author.getText(),isbn.getText(),code.getText().toUpperCase(),
                    dept.getText(),rack.getText(),shelf.getText(),n);
            JOptionPane.showMessageDialog(this,ok?"Book added.":"Could not add book.");
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
    }
}
