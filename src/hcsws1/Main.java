/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hcsws1;

/**
 *
 * @author venolin
 */

import java.awt.BorderLayout;
import java.awt.Cursor;
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
import java.util.Properties;
import javax.swing.SwingWorker;

        
public class Main extends JFrame {

    
  
   
   JPanel mainPanel;
   
   //Menu Objects
   JMenuBar menuBar;
   JMenu menu,submenu;
   JRadioButtonMenuItem rbMenuItem;
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

   //Variables
   
   String query;
   String csvFilename = "output.csv";
   //String configData = "test";
   
   //config.properties Variables
   //Variables assigned ONLY for reference purposes. Variables are not necessary as properties object decared below may be referenced directly
   public static String dir = "C:/NortechSystems/data/";
   public static String theme = "java";
   
   public static Properties defaultProps = new Properties();
   
   int progress;

    
   
    
   class BackgroundProcess extends SwingWorker<Integer, Object> { //modifies the SwingWorker class
       
        @Override
       public Integer doInBackground() {
           beginButton.setEnabled(false);
           setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
           
           
          
           
           
           return getFileList();
           
       }

        @Override
       protected void done() { //this runs when the background task completes. done() is a SwingWorker keyword
           try {
               beginButton.setEnabled(true);
               setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
           } catch (Exception ignore) {
           }
       }
   }
  

   
public Integer getFileList() {

    
    
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

    progressBar.setMaximum(listOfFiles.length - 1);

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
//        System.out.println(listOfFiles[i].getName());

        conn(dir + listOfFiles[i].getName(),query,listOfFiles[i].getName());
        databaseCount+=1;
        
        progressBar.setValue(i);
        progressBar.setStringPainted(true);
//        System.out.println(i);
      }
//      else if (listOfFiles[i].isDirectory()) {
//        System.out.println("Directory " + listOfFiles[i].getName());
//      }  
      
     
     
    }
     return databaseCount;
     
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
     dirLabel = new JLabel("DB Directory:",JLabel.LEFT);
     mainLabel = new JLabel("Query:",JLabel.LEFT);
     queryText = new JTextField("SELECT SiteName,(SELECT COUNT(*) FROM Groups WHERE GroupType=1) AS [EntranceCounters],(SELECT COUNT(*) FROM Groups WHERE GroupType=0) AS [FlowCounters],(SELECT COUNT(*) FROM Groups WHERE GroupType=3) AS [CrossOverCounters],(SELECT COUNT(*) FROM Groups WHERE GroupType=2) AS [InterLevelCounters] FROM SiteInfo",20);
     dirText = new JTextField(dir,20);
     outputArea = new JTextArea(20,59);
     sp = new JScrollPane(outputArea);
     
    
          
     progressBar = new JProgressBar();
     
         
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
     
      menu = new JMenu("Settings");
     menu.setMnemonic(KeyEvent.VK_S);
     menu.getAccessibleContext().setAccessibleDescription(
       "venTest");
     menuBar.add(menu);
     
          
     //menu.addSeparator();
        submenu = new JMenu("Theme");
        submenu.setMnemonic(KeyEvent.VK_S);
        menu.add(submenu);
    
        ButtonGroup group = new ButtonGroup();
        
        rbMenuItem = new JRadioButtonMenuItem("Java");
        
        if (defaultProps.getProperty("theme").equals("java")) {
           rbMenuItem.setSelected(true);
           
        }      
        
        rbMenuItem.setMnemonic(KeyEvent.VK_R);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        
        rbMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(mainPanel,"Configuration saved. Please restart the app for the changes to take effect.","Restart required",JOptionPane.PLAIN_MESSAGE);
            theme = "java";
            defaultProps.setProperty("theme", "java");
           writeToConfiguration();
            //write to configuration file here
         }
        });
        
        
        rbMenuItem = new JRadioButtonMenuItem("System");
        
        if (defaultProps.getProperty("theme").equals("system")) {
           rbMenuItem.setSelected(true);
           
        }  
        
        rbMenuItem.setMnemonic(KeyEvent.VK_R);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        
        rbMenuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(mainPanel,"Configuration saved. Please restart the app for the changes to take effect.","Restart required",JOptionPane.PLAIN_MESSAGE);
           theme = "system";
           defaultProps.setProperty("theme", "system");
           writeToConfiguration();
           
           
           //write to configuration file here
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
        KeyEvent.VK_3, ActionEvent.ALT_MASK));
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
     
     
     this.add(progressBar, BorderLayout.PAGE_END); //JFrame is the highest level container followed by JPanel. Self reference to the active JFrame using the keyword 'this'.
     
     this.add(mainPanel);

     menuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(mainPanel,"<html><u>Authors</u></html>\nWezi Kauleza\nVenolin Naidoo (venolin1@gmail.com)\n\n10/09/2014\nThis application makes use of an open source library created by The SQLite Consortium.","About",JOptionPane.PLAIN_MESSAGE); //html used to format text in JOptionPane
         }
     });
     
     beginButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            outputArea.setText("");
            dir = dirText.getText();
            query = queryText.getText();
            //getFileList();
           
            
            
            
            (new BackgroundProcess()).execute();
           
            
            
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
   
   public static void changeThemeToSystem() {
       
       
//           SwingUtilities.updateComponentTreeUI(frame); //Code may be used to refresh theme without closing and reopening app, but impractical
//           frame.pack();
       
      try {
            // Set System L&F
        UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
        } 
        catch (UnsupportedLookAndFeelException e) {
       // handle exception
        }
        catch (ClassNotFoundException e) {
       // handle exception
        }
        catch (InstantiationException e) {
       // handle exception
        }
        catch (IllegalAccessException e) {
       // handle exception
        }

        //new Main(); //Create and show the GUI. 
   }

    public static void main(String[] args) {
        //Page load
        
        readFromConfig(); //On page load, retrieve from the config.properties file
        
        
        
        if (theme.equals("system")) { //Using == here won't work 'cause the value retrieved from the properties file isn't the same as a string with the same value. .equals compares values whereas == compares objects.
          //JOptionPane.showMessageDialog(null, "theme=system");  
          changeThemeToSystem(); }
          else {
                  //JOptionPane.showMessageDialog(null, "theme!=system"); 
                  
        }
        
        
        
        Main first = new Main();
        first.setResizable(false);
        first.setTitle("HCSWS Multiple Database Querier V1.1");
        first.setSize(720, 470);
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
    
    public void writeToConfiguration() { //Writes to the configuration file
        
        
        try {
            
       
        FileWriter out = new FileWriter("config.properties");
        defaultProps.store(out, "---No Comment---");
        out.close();
         } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void readFromConfig() {
        
                
        try {
            
          FileReader in = new FileReader("config.properties");  
         
          //FileInputStream in = new FileInputStream("config.propeties"); //FileNotFoundException thrown???
          defaultProps.load(in);
          in.close(); 
          
          theme=defaultProps.getProperty("theme");
          dir=defaultProps.getProperty("directory");
          
          
        } catch (Exception e) {
            System.out.println(e);
        }
       
    }
    
}
