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
import java.awt.Dimension;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import static javax.swing.JTable.AUTO_RESIZE_OFF;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

        
public class Main extends JFrame {

    
  
   
   JPanel centerPanel;//JTabbedPane fits in here. Created for sizing purposes due to limited screen resolution on most older computers.
   JPanel topPanel;
   
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
   JScrollPane sp,sp2;
   JProgressBar progressBar;
   JTabbedPane resultTabbedPane;
   
            
   JTable outputTable;           
   
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
   int exceptionOffset; //Fixed an issue that caused an exception to be thrown due to files in the fileList incompatible with the SQL query run.
   
   boolean tableCreated = false;
   DefaultTableModel model;
   
   List<String> columnNamesArrayList = new ArrayList<String>(); //1D ArrayList
   
   List<List<String>> dataArrayList = new ArrayList<List<String>>(); //2D ArrayList
   int index = 0; //Index for the resultSet. Keeps track of how many rows each database has
   
   
    
   class BackgroundProcess extends SwingWorker<Integer, Object> { //modifies the SwingWorker class
       
        @Override
       public Integer doInBackground() {
           beginButton.setEnabled(false);
           setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
           progressBar.setString(null);
           
          
           
           
           return getFileList();
           
       }

        @Override
       protected void done() { //this runs when the background task completes. done() is a SwingWorker keyword
           try {
               beginButton.setEnabled(true);
               setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
               progressBar.setValue(progressBar.getMaximum()); //When task is complete, set progress bar to complete.
                progressBar.setString("Complete");
                
                //progressBar.setStringPainted(true);
//progressBar.setForeground(Color.blue);
//progressBar.setString("10%");
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

    progressBar.setMaximum(listOfFiles.length + 1);

    for (int i = 0; i < listOfFiles.length; i++) {
      if (listOfFiles[i].isFile()) {
//        System.out.println(listOfFiles[i].getName());

        conn(dir + listOfFiles[i].getName(),query,listOfFiles[i].getName(),i);
        
        
        databaseCount+=1;
        
        progressBar.setValue(i);
        progressBar.setStringPainted(true);
//        System.out.println(i);
      }
//      else if (listOfFiles[i].isDirectory()) {
//        System.out.println("Directory " + listOfFiles[i].getName());
//      }  
      

    }
    
    try {
        
        // String [][] data = dataArrayList.toArray(new String[dataArrayList.size()][]);
         
        String[][] data = new String[dataArrayList.size()][]; //Creates and array and initializes it with dataArrayList
        for (int i = 0; i < dataArrayList.size(); i++) { //Adds rows from the dataArrayList to the 2-dimensional array created above.
        List<String> row = dataArrayList.get(i);
            data[i] = row.toArray(new String[row.size()]);
        }
         System.out.println("ArrayList size: " + dataArrayList.size());
        
        System.out.println("No. of rows in data values: " + data.length);
        System.out.println("No. of columns in data values: " + data[0].length);
        
         String [] columnNames = columnNamesArrayList.toArray(new String[columnNamesArrayList.size()]);
         System.out.println("No. of columns in columnNames: " + columnNames.length);
         
         
      
    //System.out.println("Data length: " + data2.length);
    //System.out.println("Column length: " + columnNames2.length);
    
     
        
     if (tableCreated == false) { //Completely rebuilds the table on every query.
        model = new DefaultTableModel(data, columnNames);
        JTable outputTable = new JTable(model);
    outputTable.setAutoResizeMode(AUTO_RESIZE_OFF); //Allows horizontal scrolling and prevents crowded column view when there's too many columns returned
     
    
    sp2 = new JScrollPane(outputTable);
    
    resultTabbedPane.addTab("Table",sp2); //Add scrollpane so that column names will show. Initialization and assignment of object in one step ;). 
    tableCreated=true;
    
    } else {
          
       model.setRowCount(0); //Resets rows
       model.setColumnCount(0); //Resets columns
       
       for (int i = 0; i < columnNames.length; i++) {
           model.addColumn(columnNames[i]);
       }
       
       for (int i = 0; i < data.length; i++) {
           model.addRow(data[i]);
       }
       
       
       model.setRowCount(data.length);
       model.setColumnCount(columnNames.length);
             
    }  
    
    //resultTabbedPane.setEnabledAt(1, true);
    
    System.out.print("Array Row order: "); 
        for (int i=0;i<data[0].length;i++) {
            System.out.print(data[0][i] + ",");
        } 
        
        System.out.print("\nArrayList Row order: "); 
        for (int i=0;i<dataArrayList.get(0).size();i++) {
            System.out.print(dataArrayList.get(0).get(i) + ",");
        }
        System.out.print("\n"); 
    
    dataArrayList.clear(); //Resets ArrayList on every new query begin to prevent retention of old data.
    columnNamesArrayList.clear();
    
    } catch (Exception e)
     {
//         e.printStackTrace();
         System.out.println("Table creation failure: " + e);
     }
    
     exceptionOffset = 0;
     index = 0; //Keeps track of the number of rows in each database's resultset in order to create those rows in the table object
     return databaseCount;
     
    }


  public void conn(String databaseLocal,String sqlQuery,String databaseName,Integer fileNumber) {
     Connection connection = null;
     ResultSet resultSet = null;
     Statement statement = null;

     
     System.out.println("Database index: " + (fileNumber - exceptionOffset));

     try
     {
         
         Class.forName("org.sqlite.JDBC");
         connection = DriverManager.getConnection("jdbc:sqlite:" + databaseLocal);
//         ven\\sample.db
         statement = connection.createStatement();
         resultSet = statement
                 .executeQuery(sqlQuery);
      
         ResultSetMetaData rsmd = resultSet.getMetaData();
                 
         //columnNames = new String[rsmd.getColumnCount() + 1]; //Initializing string array. Added 1 for Database name.
         
          for (int i2 = 1; i2 < (rsmd.getColumnCount() + 1); i2++) { //Write column names to text area
               
                
               
              
              if (outputArea.getLineCount() <= 1) { //Checks if column names have already been written
                  
                  if (i2==1) { //Handles one time write to the output area
                    
                    outputArea.append("Database,");
                    
                     columnNamesArrayList.add(0,"Database");
                    //columnNames[0] = "Database";
                    System.out.println(columnNamesArrayList.get(0));
                    }
                    
                outputArea.append(rsmd.getColumnName(i2) + ",");
                
                
                columnNamesArrayList.add(i2,rsmd.getColumnName(i2));
                //columnNames[i2] = rsmd.getColumnName(i2);
                System.out.println(columnNamesArrayList.get(i2));
                
                }
                
               }

                if (outputArea.getLineCount() <= 1) { //Checks if column names have already been written
                  
                  outputArea.append("\n"); //Line break after column names
                
                }
         
         //data = new String[rsmd.getColumnCount()][fileNumber + 1];
         //System.out.println("Number of columns: " + data.length);
         
         
         
         while (resultSet.next())
         {
//             writeToDatabase("INSERT INTO counter_types VALUES " + (resultSet.getString(sitename) + resultSet.getString(hb) + resultSet.getString(hi) + resultSet.getString(tray) + resultSet.getString(vcu) + resultSet.getString(occulus) + resultSet.getString(hp) + resultSet.getString(reflector)));
//             System.out.println("INSERT INTO counter_types VALUES " + resultSet.getString("sitename") + resultSet.getString("hb") + resultSet.getString("hi") + resultSet.getString("tray") + resultSet.getString("vcu") + resultSet.getString("occulus") + resultSet.getString("hp") + resultSet.getString("reflector"));
//               writeToDatabase("INSERT INTO counter_types VALUES (" + resultSet.getString("sitename") + "," + resultSet.getString("hb") + "," + resultSet.getString("hi") + "," + resultSet.getString("tray") + "," + resultSet.getString("vcu") + "," + resultSet.getString("oculus") + "," + resultSet.getString("hp") + "," + resultSet.getString("reflector") + ")");
//               System.out.println("INSERT INTO counter_types VALUES (" + "'" + resultSet.getString("sitename") + "'" + "," + resultSet.getString("hb") + "," + resultSet.getString("hi") + "," + resultSet.getString("tray") + "," + resultSet.getString("vcu") + "," + resultSet.getString("oculus") + "," + resultSet.getString("hp") + "," + resultSet.getString("reflector") + ")");
//             System.out.println("INSERT INTO counter_types VALUES " + resultSet.getString("sitename"));
                 
//             outputArea.append("INSERT INTO counter_config VALUES (" + "'" + resultSet.getString("SiteName") + "'" + "," + resultSet.getString("EntranceCounters") + "," +  resultSet.getString("FlowCounters") + "," +  resultSet.getString("CrossOverCounters") + "," +  resultSet.getString("InterLevelCounters") + "," +  resultSet.getString("Undefined") + ");" + System.getProperty("line.separator"));
           outputArea.append(databaseName + ",");
           //data[0][fileNumber] = databaseName;
           
            dataArrayList.add(new ArrayList<String>()); //Add to 2D List. Placing this here creates a new list object within the list object, thus increasing the max index 3 in 'dataArrayList.get(3).add(1,"Sagren")' each time the loop is run. Thus a new row is created each loop.
           
           dataArrayList.get(index).add(0, databaseName); //There's no need to account for the fileNumber here as the index includes the file number, think about it ;)
           //dataArrayList.get(0).set(1, databaseName); //Sets to already created object
           //dataArrayList.get(0).add(0, "Venolin");
           //dataArrayList.get(0).add(1, "Govender");
            //dataArrayList.get(0).add(2, "Virusan");
           
           //dataArrayList.get(0).add(0,"Kriben"); //add creates a new column and places data in it.
           //dataArrayList.get(0).add(1,"Venolin");
           
           //dataArrayList.get(3).add(0,"Virusan");
           //dataArrayList.get(3).add(1,"Sagren");
           
            //dataArrayList.get(3).add("Kriben");
           //dataArrayList.get(0).set(2, "Naidoo");
           //System.out.println(data[0][fileNumber]);
           //System.out.println("test: " + dataArrayList.get(0).get(0)); //Works!
           //System.out.println("test: " + dataArrayList.get(0).get(1)); //Works!
            //System.out.println("test: " + dataArrayList.get(0).get(2)); //Works!
           
           //System.out.println("test: " + dataArrayList.get(0).get(0)); //Works!
           //System.out.println("test: " + dataArrayList.get(0).get(1)); //Works!
           //System.out.println("test: " + dataArrayList.get(3).get(0)); //Works!
           //System.out.println("test: " + dataArrayList.get(3).get(1)); //Works!
           
           System.out.println("Database name: " + dataArrayList.get(index).get(0)); //Works!
           for (int i = 1; i < (rsmd.getColumnCount() + 1); i++) {
//                    writer.append("DisplayName");
//                    writer.append(',');
//                    writer.append("Age");
//                    writer.append('\n');
               
               
                outputArea.append(resultSet.getString(i) + ",");
                dataArrayList.get(index).add(i, resultSet.getString(i));
                //data[i][fileNumber] = resultSet.getString(i);
                //dataArrayList.add(new ArrayList<String>()); //Add to 2D List
                //dataArrayList.get(i).set(fileNumber, resultSet.getString(i));
                
                //System.out.println(data[i][fileNumber]);
                //System.out.println("Test: " + dataArrayList.get(i).get(i));
                System.out.println("Value: " + dataArrayList.get(index).get(i-1)); //Works!
                
           }
            
//                outputArea.append("'" + resultSet.getString(rsmd.getColumnCount()) + "'" + ");");
//                outputArea.append(System.getProperty("line.separator"));
           outputArea.append("\n"); //Line break after EVERY row in every
           index++;
          }
     
         //dataArrayList.toArray(data);
         
     }
     catch (Exception e)
     {
         exceptionOffset +=1;
//         e.printStackTrace();
         System.out.println(databaseName + e);
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
             System.out.println(databaseName + e);
         }
     }
//     System.out.println("VenTestConnection: " + databaseLocal);
//     System.out.println("VenTestQuery: " + sqlQuery);
     
     
    }
   public Main(){
       
     
     
     centerPanel = new JPanel();
     topPanel = new JPanel();
         
     menuBar = new JMenuBar();
     
     
     
     beginButton = new JButton("Begin");
     dirLabel = new JLabel("DB Directory:",JLabel.LEFT);
     mainLabel = new JLabel("Query:",JLabel.LEFT);
     queryText = new JTextField("SELECT SiteName,(SELECT COUNT(*) FROM Groups WHERE GroupType=1) AS [EntranceCounters],(SELECT COUNT(*) FROM Groups WHERE GroupType=0) AS [FlowCounters],(SELECT COUNT(*) FROM Groups WHERE GroupType=3) AS [CrossOverCounters],(SELECT COUNT(*) FROM Groups WHERE GroupType=2) AS [InterLevelCounters] FROM SiteInfo",20);
     dirText = new JTextField(dir,20);
     outputArea = new JTextArea(20,59);
     sp = new JScrollPane(outputArea);
     
     
     resultTabbedPane = new JTabbedPane();
     outputTable = new JTable();
    
          
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
           JOptionPane.showMessageDialog(centerPanel,"Configuration saved. Please restart the app for the changes to take effect.","Restart required",JOptionPane.PLAIN_MESSAGE);
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
           JOptionPane.showMessageDialog(centerPanel,"Configuration saved. Please restart the app for the changes to take effect.","Restart required",JOptionPane.PLAIN_MESSAGE);
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
     
     
     topPanel.add(dirLabel);
     topPanel.add(dirText);
     topPanel.add(mainLabel);
     topPanel.add(queryText);
     topPanel.add(beginButton);
     
          
     resultTabbedPane.addTab("CSV",sp); //Adds the sp which in turn adds the outputArea
     centerPanel.add(resultTabbedPane);
     
     
    //resultTabbedPane.setEnabledAt(1, false);
     
     //resultTabbedPane.addTab("Table",outputTable); 
     centerPanel.add(resultTabbedPane);
     
     resultTabbedPane.setPreferredSize(new Dimension(680, 360)); //Forces size of object else object sizes to fit container :(
     
      
     this.add(topPanel, BorderLayout.PAGE_START); 
     
     this.add(progressBar, BorderLayout.PAGE_END); //JFrame is the highest level container followed by JPanel. Self reference to the active JFrame using the keyword 'this'.
     
     this.add(centerPanel, BorderLayout.CENTER);

     menuItem.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
           JOptionPane.showMessageDialog(centerPanel,"<html><u>Authors</u></html>\nWezi Kauleza\nVenolin Naidoo (venolin1@gmail.com)\n\n10/09/2014\nThis application makes use of an open source library created by The SQLite Consortium.","About",JOptionPane.PLAIN_MESSAGE); //html used to format text in JOptionPane
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
        first.setTitle("HCSWS Multiple Database Querier V1.3.1");
        first.setSize(720, 490);
        first.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        first.setVisible(true);
        first.setLocationRelativeTo(null); //On load, the application is placed in the center of the screen
    
        
    }

    public void saveToFile(String filename) {
        try {
        FileWriter writer = new FileWriter(csvFilename); //Create csv
          writer.append(outputArea.getText());
          JOptionPane.showMessageDialog(null,"Saved to " + csvFilename);

          writer.flush();
	  writer.close();
        }
        catch (Exception filewrite)
         {
             filewrite.printStackTrace();
             JOptionPane.showMessageDialog(null, filewrite);
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
    
    public static void checkDirectoryValidity() {
        
                   
    File folder = new File(dir);
//    "ven\\"
    File[] listOfFiles = folder.listFiles(new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".db") != name.endsWith("_cube.db");


    }
});
       
    }
    
}
