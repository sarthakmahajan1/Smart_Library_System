package com.libraryerp.ui;

import com.libraryerp.dao.*;
import com.libraryerp.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final String studentId;
    private final BookDAO bookDAO=new BookDAO();
    private final LibraryDAO libraryDAO=new LibraryDAO();
    private final JTable books=new JTable();
    private final JTable issued=new JTable();
    private final JComboBox<String> dept=new JComboBox<>(new String[]{"","A","B","C","D","E","F"});
    private final JTextField search=new JTextField();
    private final JLabel fine=new JLabel("Outstanding Fine: ₹0.00");

    public DashboardFrame(String studentId){
        this.studentId=studentId;
        setTitle("Smart Library ERP - Student Dashboard ("+studentId+")");
        setSize(1000,650);setLocationRelativeTo(null);setDefaultCloseOperation(EXIT_ON_CLOSE);
        JTabbedPane tabs=new JTabbedPane();
        tabs.add("Search Books",searchPanel());
        tabs.add("My Books",myBooksPanel());
        tabs.add("Fine / Payment",finePanel());
        add(tabs); refreshBooks(); refreshIssued(); refreshFine();
    }

    private JPanel searchPanel(){
        JPanel root=new JPanel(new BorderLayout(8,8));
        JPanel top=new JPanel(new GridLayout(1,4,8,8));
        top.add(new JLabel("Keyword:"));top.add(search);top.add(dept);
        JButton s=new JButton("Search");top.add(s);root.add(top,BorderLayout.NORTH);
        books.setModel(new DefaultTableModel(new Object[]{"ID","Title","Author","Dept","Rack","Shelf","Available"},0));
        root.add(new JScrollPane(books),BorderLayout.CENTER);
        JPanel bottom=new JPanel();
        JButton issue=new JButton("Issue Selected Book");bottom.add(issue);root.add(bottom,BorderLayout.SOUTH);
        s.addActionListener(e->refreshBooks());issue.addActionListener(e->issueSelected());
        return root;
    }

    private JPanel myBooksPanel(){
        JPanel p=new JPanel(new BorderLayout(8,8));
        issued.setModel(new DefaultTableModel(new Object[]{"Issue ID","Book ID","Title","Issue Date","Due Date","Renewals","Status"},0));
        p.add(new JScrollPane(issued),BorderLayout.CENTER);
        JPanel b=new JPanel();JButton renew=new JButton("Renew Selected");JButton ret=new JButton("Return Selected");
        b.add(renew);b.add(ret);p.add(b,BorderLayout.SOUTH);
        renew.addActionListener(e->renewSelected());ret.addActionListener(e->returnSelected());return p;
    }

    private JPanel finePanel(){
        JPanel p=new JPanel(new FlowLayout(FlowLayout.LEFT,15,20));
        JButton pay=new JButton("Pay One Fine");p.add(fine);p.add(pay);
        pay.addActionListener(e->{try{if(libraryDAO.payOneFine(studentId))JOptionPane.showMessageDialog(this,"Fine paid. Transaction saved in ERP.");else JOptionPane.showMessageDialog(this,"No unpaid fine.");refreshFine();}catch(Exception x){show(x);}});
        return p;
    }

    private void refreshBooks(){
        try{
            List<Book> list=bookDAO.search(search.getText(),(String)dept.getSelectedItem());
            DefaultTableModel m=(DefaultTableModel)books.getModel();m.setRowCount(0);
            for(Book b:list)m.addRow(new Object[]{b.id(),b.title(),b.author(),b.departmentCode(),b.rack(),b.shelf(),b.availableCopies()});
        }catch(Exception e){show(e);}
    }

    private void refreshIssued(){
        try{
            List<IssuedBook> list=libraryDAO.myBooks(studentId);
            DefaultTableModel m=(DefaultTableModel)issued.getModel();m.setRowCount(0);
            for(IssuedBook b:list)m.addRow(new Object[]{b.issueId(),b.bookId(),b.title(),b.issueDate(),b.dueDate(),b.renewalCount(),b.status()});
        }catch(Exception e){show(e);}
    }

    private void refreshFine(){try{fine.setText(String.format("Outstanding Fine: ₹%.2f",libraryDAO.outstandingFine(studentId)));}catch(Exception e){show(e);}}

    private void issueSelected(){
        int r=books.getSelectedRow();if(r<0){JOptionPane.showMessageDialog(this,"Select a book.");return;}
        try{int id=(int)books.getValueAt(r,0);if(bookDAO.search("", "").stream().noneMatch(b->b.id()==id)){return;}
            if(libraryDAO.issue(id,studentId))JOptionPane.showMessageDialog(this,"Book issued successfully. Due date is 14 days from today.");
            else JOptionPane.showMessageDialog(this,"Book cannot be issued. Check availability or existing issue.");
            refreshBooks();refreshIssued();
        }catch(Exception e){show(e);}
    }

    private void renewSelected(){
        int r=issued.getSelectedRow();if(r<0){JOptionPane.showMessageDialog(this,"Select an issued book.");return;}
        try{boolean ok=libraryDAO.renew((int)issued.getValueAt(r,0),studentId);
            JOptionPane.showMessageDialog(this,ok?"Renewed for 14 days.":"Renewal denied: overdue, already returned, or maximum 2 renewals reached.");
            refreshIssued();refreshFine();
        }catch(Exception e){show(e);}
    }

    private void returnSelected(){
        int r=issued.getSelectedRow();if(r<0){JOptionPane.showMessageDialog(this,"Select an issued book.");return;}
        try{boolean ok=libraryDAO.returnBook((int)issued.getValueAt(r,0),studentId);
            JOptionPane.showMessageDialog(this,ok?"Book returned successfully.":"Return failed.");
            refreshBooks();refreshIssued();refreshFine();
        }catch(Exception e){show(e);}
    }

    private void show(Exception e){JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);}
}
