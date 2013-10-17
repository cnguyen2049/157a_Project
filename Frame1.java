package de.vogella.mysql.first;
import java.awt.*; 
import java.awt.event.*;
import javax.swing.*;
public class Frame1 extends JFrame implements ActionListener
{
  JLabel answer = new JLabel("Options");
  JPanel pane = new JPanel(); 
  JButton pressme = new JButton("Rent Car");
  JButton returnCar = new JButton("Return Car");
  Frame1()
  {
    super("Demo"); setBounds(100,100,300,200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Container con = this.getContentPane();
    con.add(pane); pressme.setMnemonic('P');
    pressme.addActionListener(this);
    returnCar.addActionListener(this);
    pane.add(answer); pane.add(pressme); pane.add(returnCar); pressme.requestFocus();
    setVisible(true); 
  }
  public void actionPerformed(ActionEvent event)
  {
    
    if (event.getSource() == pressme)
    {
      answer.setText("Rent Car");
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
      else if(event.getSource() == returnCar){
          JTextField driverID = new JTextField(5);

          JPanel myPanel = new JPanel();
          myPanel.add(Box.createHorizontalStrut(15)); 
          myPanel.add(new JLabel("Driver ID"));
          myPanel.add(driverID);

          int result = JOptionPane.showConfirmDialog(null, myPanel, 
                   "Please Enter Name Values", JOptionPane.OK_CANCEL_OPTION);
          if (result == JOptionPane.OK_OPTION) {
        	  int number = Integer.parseInt(driverID.getText());
        	  System.out.println(number);
        	  setVisible(true);  
    }
      }
      else{
    	 System.out.println("Error");
      }
    }
 
  
  public static void main(String args[]) {new Frame1();}
}