/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hcsws1;

/**
 *
 * @author venolin
 */
//import java.awt.Cursor;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
//import java.io.FileWriter;
//import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import javax.swing.SwingWorker;

        
public class Main extends JFrame {

    
  
   
   JPanel mainPanel;
   
   JMenuBar menuBar;
   JMenu menu;
   JMenuItem menuItem;
   
   JButton beginButton;
   JLabel dirLabel;
   JLabel mainLabel;
   JTextField queryText;
   JTextField dirText;
   JPopupMenu contextPopup;
 
   JTextArea outputArea;
   JScrollPane sp;
   JProgressBar progressBar;

   String dir;
   String query;
   //int progress;
   
  
   String csvFilename = "output.csv";


public void getFileList() {

//    beginButton.setEnabled(false);
//    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    
//    done = false;
//    task = new Task();
//    task.addPropertyChangeListener(this);
//    task.execute();
    
    int databaseCount = 0;
    
    File folder = new File(dir);
//    "ven\\"
File[] listOfFiles = folder.listFiles(new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".db") != name.endsWith("_cube.db");


    }
});

progressBar = new JProgressBar(0, listOfFiles.length -1);
mainPanel.add(progressBar);

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
//        System.out.println(listOfFiles[i].getName());

        conn(dir + listOfFiles[i].getName(),query,listOfFiles[i].getName());
        databaseCount+=1;
//        System.out.println(i);
      }
//      else if (listOfFiles[i].isDirectory()) {
//        System.out.println("Directory " + listOfFiles[i].getName());
//      }  
      
     progressBar.setValue(i);
     progressBar.setStringPainted(true);
     
    }
     
    }


  public void conn(String databaseLocal,String sqlQuery,String databaseName) {
     Connection connection = null;
     ResultSet resultSet = null;
     Statement statement = null;

     try
     {
         
         Class.forName("org.sqlite.JDBC");
         connection = DriverManager.getConnection("jdbc:sqlite:" + databaseLocal);
//         ven\\sample.db
         statement = connection.createStatement();
         resultSet = statement
                 .executeQuery(sqlQuery);
      
         ResultSetMetaData rsmd = resultSet.getMetaData();
                 
         
         
          for (int i2 = 1; i2 < (rsmd.getColumnCount() + 1); i2++) { //Write column names to text area
               
                
               
              
              if (outputArea.getLineCount() < 2) { //Checks if column names have already been written
                  
                  if (i2==1) { //Handles one time write to the output area
                    
                    outputArea.append("Database,");
               
                    }
                    
                outputArea.append(rsmd.getColumnName(i2) + ",");
                }
                
               }

         outputArea.append("\n"); //Line break after EVERY database
         
         while (resultSet.next())
         {
//             writeToDatabase("INSERT INTO counter_types VALUES " + (resultSet.getString(sitename) + resultSet.getString(hb) + resultSet.getString(hi) + resultSet.getString(tray) + resultSet.getString(vcu) + resultSet.getString(occulus) + resultSet.getString(hp) + resultSet.getString(reflector)));
//             System.out.println("INSERT INTO counter_types VALUES " + resultSet.getString("sitename") + resultSet.getString("hb") + resultSet.getString("hi") + resultSet.getString("tray") + resultSet.getString("vcu") + resultSet.getString("occulus") + resultSet.getString("hp") + resultSet.getString("reflector"));
//               writeToDatabase("INSERT INTO counter_types VALUES (" + resultSet.getString("sitename") + "," + resultSet.getString("hb") + "," + resultSet.getString("hi") + "," + resultSet.getString("tray") + "," + resultSet.getString("vcu") + "," + resultSet.getString("oculus") + "," + resultSet.getString("hp") + "," + resultSet.getString("reflector") + ")");
//               System.out.println("INSERT INTO counter_types VALUES (" + "'" + resultSet.getString("sitename") + "'" + "," + resultSet.getString("hb") + "," + resultSet.getString("hi") + "," + resultSet.getString("tray") + "," + resultSet.getString("vcu") + "," + resultSet.getString("oculus") + "," + resultSet.getString("hp") + "," + resultSet.getString("reflector") + ")");
//             System.out.println("INSERT INTO counter_types VALUES " + resultSet.getString("sitename"));
                 
//             outputArea.append("INSERT INTO counter_config VALUES (" + "'" + resultSet.getString("SiteName") + "'" + "," + resultSet.getString("EntranceCounters") + "," +  resultSet.getString("FlowCounters") + "," +  resultSet.getString("CrossOverCounters") + "," +  resultSet.getString("InterLevelCounters") + "," +  resultSet.getString("Undefined") + ");" + System.getProperty("line.separator"));
           outputArea.append(databaseName + ",");
           for (int i = 1; i < (rsmd.getColumnCount() + 1); i++) {
//                    writer.append("DisplayName");
//                    writer.append(',');
//                    writer.append("Age");
//                    writer.append('\n');
                
                outputArea.append(resultSet.getString(i) + ",");
           }
//                outputArea.append("'" + resultSet.getString(rsmd.getColumnCount()) + "'" + ");");
//                outputArea.append(System.getProperty("line.separator"));
          }
     }
     catch (Exception e)
     {
//         e.printStackTrace();
     }
     finally
     {
         try
         {
             resultSet.close();
             statement.close();
             connection.close();
         }
         catch (Exception e)
         {
//             e.printStackTrace();
         }
     }
//     System.out.println("VenTestConnection: " + databaseLocal);
//     System.out.println("VenTestQuery: " + sqlQuery);
    }
   public Main(){
     mainPanel = new JPanel();
     
     menuBar = new JMenuBar();
     
     
     
     beginButton = new JButton("Begin");
     dirLabel = new JLabel("Enter directory here:",JLabel.LEFT);
     mainLabel = new JLabel("Enter query here:",JLabel.LEFT);
     queryText = new JTextField("SELECT SiteName,(SELECT COUNT(*) FROM Groups WHERE GroupType=1) AS [EntranceCounters],(SELECT COUNT(*) FROM Groups WHERE GroupType=0) AS [FlowCounters],(SELECT COUNT(*) FROM Groups WHERE GroupType=3) AS [CrossOverCounters],(SELECT COUNT(*) FROM Groups WHERE GroupType=2) AS [InterLevelCounters],(SELECT COUNT(*) FROM Groups WHERE GroupType=-1) AS [Undefined] FROM SiteInfo",20);
     dirText = new JTextField("C:/NortechSystems/data/",20);
     outputArea = new JTextArea(20,60);
     sp = new JScrollPane(outputArea);
          
     
     
         
     //contextPopup = new JPopupMenu();
     
    menu = new JMenu("File");
     menu.setMnemonic(KeyEvent.VK_F);
     menu.getAccessibleContext().setAccessibleDescription(
       "Provides a list of file options"); //This should display as a tooltip
     menuBar.add(menu);
     
     menuItem = new JMenuItem("Export to .csv",
                         KeyEvent.VK_T);
     menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_1, ActionEvent.ALT_MASK));
     menuItem.getAccessibleContext().setAccessibleDescription(
        "This doesn't really do anything");
     menu.add(menuItem);
     
     menuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           
          saveToFile(csvFilename);
          JOptionPane.showMessageDialog(null,"Saved to " + csvFilename);
         }
     });
     
      menu = new JMenu("Help");
     menu.setMnemonic(KeyEvent.VK_H);
     menu.getAccessibleContext().setAccessibleDescription(
       "venTest");
     menuBar.add(menu);
     
     
      menuItem = new JMenuItem("About",
                         KeyEvent.VK_T);
     menuItem.setAccelerator(KeyStroke.getKeyStroke(
        KeyEvent.VK_2, ActionEvent.ALT_MASK));
     menuItem.getAccessibleContext().setAccessibleDescription(
        "This doesn't really do anything");
     menu.add(menuItem);
     
     
     setJMenuBar(menuBar);
     
     mainPanel.add(dirLabel);
     mainPanel.add(dirText);
     mainPanel.add(mainLabel);
     mainPanel.add(queryText);
     mainPanel.add(beginButton);
     mainPanel.add(sp);
     
         

     this.add(mainPanel);

     menuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(mainPanel,"Creators\nWezi Kauleza\nVenolin Naidoo\n\n10/09/2014\nNortech International (Pty) Ltd.","About",JOptionPane.PLAIN_MESSAGE);
         }
     });
     
     beginButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            outputArea.setText("");
            dir = dirText.getText();
            query = queryText.getText();
            getFileList();
//            conn(dir,query);
            
         }
      });
     
      
//      Action copyAction = queryText.getActionMap().get("copy");
//      Action cutAction = queryText.getActionMap().get("cut");
//      Action pasteAction = queryText.getActionMap().get("paste");
//      Action undoAction = queryText.getActionMap().get("undo");
//      Action selectAllAction = queryText.getActionMap().get("selectAll");
//
//
//      contextPopup.add(undoAction);
//      contextPopup.addSeparator();
//      contextPopup.add(cutAction);
//      contextPopup.add(copyAction);
//      contextPopup.add(pasteAction);
//      contextPopup.addSeparator();
//      contextPopup.add(selectAllAction);
//
//      return;
   }
    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        // TODO code application logic here
        Main first = new Main();
        first.setResizable(true);
        first.setTitle("HCSWS Multiple Database Querier V1.1");
        first.setSize(790, 396);
        first.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        first.setVisible(true);
    

    }

    public void saveToFile(String filename) {
        try {
        FileWriter writer = new FileWriter(csvFilename); //Create csv
          writer.append(outputArea.getText());

          writer.flush();
	  writer.close();
        }
        catch (Exception filewrite)
         {
             filewrite.printStackTrace();
         }
    }
    

}
