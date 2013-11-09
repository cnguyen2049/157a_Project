package de.vogella.mysql.first;
import java.awt.*; 
import java.awt.event.*;
import javax.swing.*;
public class Frame1 extends JFrame implements ActionListener
{
  JLabel answer = new JLabel("Select");
  JPanel pane = new JPanel(); 
  JButton oldUser = new JButton("Login");
  JButton admin = new JButton("Admin");
  JButton newUser = new JButton("New User");
  Frame1()
  {
    super("Demo"); setBounds(100,100,300,200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container con = this.getContentPane();
    con.add(pane); //pressme.setMnemonic('P');
    admin.addActionListener(this);
    oldUser.addActionListener(this);
    newUser.addActionListener(this);
    pane.add(answer); pane.add(admin); pane.add(oldUser);
    pane.add(newUser);
    setVisible(true); 
  }
  public void actionPerformed(ActionEvent event)
  {
    
    if (event.getSource() == newUser)
    {
      //Frame1.this.dispose();
      //setVisible(false);
      answer.setText("User");
      JTextField nameField = new JTextField(10);
      JTextField driverID = new JTextField(5);
      JTextField age = new JTextField(5);
      JTextField creditCard = new JTextField(7);

      JPanel myPanel = new JPanel();
      myPanel.add(new JLabel("Enter Name:"));
      myPanel.add(nameField);
      myPanel.add(new JLabel("Age:"));
      myPanel.add(age);
      myPanel.add(new JLabel("CC"));
      myPanel.add(creditCard);
      myPanel.add(new JLabel("Driver ID"));
      myPanel.add(driverID);

      int result = JOptionPane.showConfirmDialog(null, myPanel, 
               "Please Enter Name Values", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
    	  String Name = nameField.getText();
    	  int number = Integer.parseInt(driverID.getText());
         System.out.println(Name);
         System.out.println(number);
      setVisible(true);  
      
    }
    }
    else if(event.getSource()== oldUser){
    	Frame1.this.dispose();
    	JTextField driverID = new JTextField(10);
    	JPanel myPanel = new JPanel();
        myPanel.add(Box.createHorizontalStrut(15)); 
        myPanel.add(new JLabel("Driver License"));
        myPanel.add(driverID);

        int result = JOptionPane.showConfirmDialog(null, myPanel, 
                 "Please Enter Name Values", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
      	  int number = Integer.parseInt(driverID.getText());
      	  System.out.println(number);
      	  setVisible(true);  
    }
    }
      else if(event.getSource() == admin){
    	  Frame1.this.dispose();
          JTextField adminID = new JTextField(10);
          JPanel myPanel = new JPanel();
          myPanel.add(Box.createHorizontalStrut(15)); 
          myPanel.add(new JLabel("Admin ID"));
          myPanel.add(adminID);
          
          int result = JOptionPane.showConfirmDialog(null, myPanel, 
                   "Please Enter Name Values", JOptionPane.OK_CANCEL_OPTION);
          if (result == JOptionPane.OK_OPTION) {
        	  if(adminID.getText().length()<5){
        		  JOptionPane.showMessageDialog(null, "Wrong values", "Error Message", JOptionPane.OK_OPTION);
        	  }
        	  int number = Integer.parseInt(adminID.getText());
        	  System.out.println(number);
        	  setVisible(true);  
    }
      }
      else{
    	 System.out.println("Error");
      }
    }
  public void close() {
	    WindowEvent winClosingEvent = new WindowEvent( this, WindowEvent.WINDOW_CLOSING );
	    Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent( winClosingEvent );
	}
  
  public static void main(String args[]) {new Frame1();}
}