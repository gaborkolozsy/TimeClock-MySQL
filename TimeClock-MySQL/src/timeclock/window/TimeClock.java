/*
 * Copyright (c) 2016, Gábor Kolozsy. All rights reserved.
 * 
 */
package timeclock.window;

import java.awt.CardLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import timeclock.config.Config;
import timeclock.dao.JobRepositoryJDBCImpl;
import timeclock.dao.PayInfoRepositoryJDBCImpl;
import timeclock.dao.PayRepositoryJDBCImpl;
import timeclock.dao.TimeInfoRepositoryJDBCImpl;
import timeclock.dao.UserRepositoryBINImpl;
import timeclock.exception.TimeClockException;
import timeclock.interfaces.JobRepository;
import timeclock.interfaces.PayInfoRepository;
import timeclock.interfaces.PayRepository;
import timeclock.interfaces.TimeInfoRepository;
import timeclock.interfaces.UserRepository;
import timeclock.job.Job;
import timeclock.pay.Pay;
import timeclock.query.PayInfo;
import timeclock.query.TimeInfo;
import timeclock.user.User;

/**
 * Calculates the working time and store this in MySQL database.
 * 
 * @author Kolozsy Gábor
 * @email kolozsygabor@gmail.com
 * @version 1.2.2
 * @see timeclock.job.Job
 * @see timeclock.interfaces.JobRepository
 * @see timeclock.dao.JobRepositoryJDBCImpl
 * @see timeclock.pay.Pay
 * @see timeclock.interfaces.PayRepository
 * @see timeclock.dao.PayRepositoryJDBCImpl
 * @see timeclock.user.User
 * @see timeclock.interfaces.UserRepository
 * @see timeclock.dao.UserRepositoryBINImpl
 * @see timeclock.config.Config
 * @see java.util.regex.Pattern
 * @see java.util.regex.Matcher
 * @see java.util.Date
 * @see java.time.LocalDateTime
 * @see java.time.Instant
 * @see java.time.ZoneId
 * @see java.time.ZoneOffset
 * @see java.sql.DriverManager
 * @see java.sql.Connection
 * @see java.awt.CardLayout
 * @see java.awt.Color
 * @see javax.swing.JOptionPane
 * @see java.io.File
 * @see java.io.IOException
 * @see java.sql.SQLException
 * @see java.io.FileNotFoundException
 */
public class TimeClock extends javax.swing.JFrame {

    private static Connection connection;
    private static boolean open = false;
    private static JobRepository jobRep;
    private static String branch;
    private static String project;
    private static String sPackage;
    private static String sClass;
    private static int jobNumber;
    private static final String DONE = "Done"; 
    private static final String WIP = "WIP";
    private static TimeInfoRepository timeInfoRep;
    private static String hour;
    private static String minute;
    private static String second;
    private static PayRepository payRep;
    private static PayInfoRepository payInfoRep;
    private static UserRepository userRep;
    private static final String USERFILE = "user.bin";
    private static final Config config = new Config();
    private static final String STATUS = "Status";
    private static final String AUTOCONNECT = "AutoConnect";
    private static final String NUMBER = "Number";
    private static final String NEXTACTION = "NextAction";
    private static final String BPPC = "BPPC";
    private static final String COMMENT = "Comment";
    private static final String PPC = "PPC";
    private static Color myBlue;
    
    /**
     * Constructor.
     * 
     * @throws java.io.IOException
     */
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public TimeClock() throws IOException {
        initComponents();
        databaseType.setFocusable(false);
        databaseUserId.setFocusable(false);
        
        if (!new File(USERFILE).exists()) {
            databaseRememberMeBox.setSelected(true);
        }
        
        if (config.fileExists()) {
            if (config.isKey(AUTOCONNECT)) {
                if (config.getValue(AUTOCONNECT).equals("true") && new File(USERFILE).exists()) {
                    autoConnect();
                    if (config.isKey(NEXTACTION)) {
                        if (config.getValue(NEXTACTION).equals("end")) {
                            timeClock.setSelectedComponent(end);
                            if (config.isKey(STATUS) && config.getValue(STATUS).equals(DONE)) {
                                startBranchProjectPkgClassNo.setSelectedItem(startBranchProjectPkgClassNo.getSelectedItem().toString().concat((Integer.parseInt(config.getValue(NUMBER))+1)+""));
                                startLockButton.setEnabled(false);
                                endLockButton.setEnabled(false);
                                addLockButton.setEnabled(false);
                            }
                        } else {
                            timeClock.setSelectedComponent(start);
                            if (!config.isKey(STATUS)) {
                                startBranchProjectPkgClassNo.setSelectedItem(startBranchProjectPkgClassNo.getSelectedItem().toString().concat("1"));
                                startLockButton.setEnabled(false);
                                endLockButton.setEnabled(false);
                                addLockButton.setEnabled(false);
                            } else {
                                if (config.isKey(STATUS) && config.getValue(STATUS).equals(WIP)) {
                                    startBranchProjectPkgClassNo.setSelectedItem(startBranchProjectPkgClassNo.getSelectedItem().toString().concat(config.getValue(NUMBER)));
                                    startButton.setText("Work On");
                                    startLockButton.setEnabled(false);
                                    endLockButton.setEnabled(false);
                                    addLockButton.setEnabled(false);
                                }
                                if (config.isKey(STATUS) && config.getValue(STATUS).equals(DONE)) {
                                    startBranchProjectPkgClassNo.setSelectedItem(startBranchProjectPkgClassNo.getSelectedItem().toString().concat((Integer.parseInt(config.getValue(NUMBER))+1)+""));
                                    startLockButton.setEnabled(false);
                                    endLockButton.setEnabled(false);
                                    addLockButton.setEnabled(false);
                                }
                            }
                        }
                    }
                } else {
                    databaseLockAutoConnectionBox.setText(AUTOCONNECT);
                    databaseLockAutoConnectionBox.setSelected(true);
                    databaseQuitButton.setEnabled(false);
                }
            }
            
            if (config.isKey(NUMBER)) {
                endJobNumber.setSelectedItem(config.getValue(NUMBER));
                timeJobNumber.setSelectedItem(config.getValue(NUMBER));
                payJobNumber.setSelectedItem(config.getValue(NUMBER));
            }
            
            if (config.isKey(BPPC)) { // branch,project,package,class
                String[] bppc = config.getValue(BPPC).split("/");
                for (String bp : bppc) {
                    startBranchProjectPkgClassNo.addItem(bp);
                }
            }
            
            if (config.isKey(COMMENT)) {
                String[] comment = config.getValue(COMMENT).split("/");
                for (String com : comment) {
                    startComment.addItem(com);
                }
            }
            
            if (config.isKey(PPC)) { // project,package,class
                String[] ppc = config.getValue(PPC).split("/");
                for (String pp : ppc) {
                    endProjectPackageClass.addItem(pp);
                    timeProjectPackageClass.addItem(pp);
                    payProjectPackageClass.addItem(pp);
                }
            }
            
        } else {
            databaseLockAutoConnectionBox.setText(AUTOCONNECT);
            databaseLockAutoConnectionBox.setSelected(true);
            databaseQuitButton.setEnabled(false);
        }
        
        myBlue = payQueryButton.getForeground();
        timeQueryButton.setForeground(myBlue);
    }// </editor-fold> 

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        timeClock = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        addBPPCforStartTab = new javax.swing.JTextField();
        addPrefixForCommentByStart = new javax.swing.JTextField();
        addPPCforEndTimePayTabs = new javax.swing.JTextField();
        addQuitButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        addLockButton = new javax.swing.JButton();
        database = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        databaseType = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        databaseUserId = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        databaseUsername = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        databasePassword = new javax.swing.JPasswordField();
        databaseRememberMeBox = new javax.swing.JCheckBox();
        databaseConnectionButton = new javax.swing.JButton();
        databaseQuitButton = new javax.swing.JButton();
        databaseLockAutoConnectionBox = new javax.swing.JCheckBox();
        databaseUpdateButton = new javax.swing.JButton();
        start = new javax.swing.JPanel();
        jLabel61 = new javax.swing.JLabel();
        startTimeAt = new javax.swing.JSpinner();
        jLabel41 = new javax.swing.JLabel();
        startBranchProjectPkgClassNo = new javax.swing.JComboBox();
        jLabel51 = new javax.swing.JLabel();
        startComment = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        startDeveloper = new javax.swing.JComboBox();
        startPaymentForThisJob = new javax.swing.JTextField();
        startCurrency = new javax.swing.JComboBox();
        startDbLinked = new javax.swing.JCheckBox();
        startButton = new javax.swing.JButton();
        startQuitButton = new javax.swing.JButton();
        startDeleteLastBox = new javax.swing.JCheckBox();
        startLockButton = new javax.swing.JButton();
        end = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        endTimeAt = new javax.swing.JSpinner();
        jLabel24 = new javax.swing.JLabel();
        endProjectPackageClass = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        endJobNumber = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        endStatus = new javax.swing.JComboBox();
        endDbLinked = new javax.swing.JCheckBox();
        endButton = new javax.swing.JButton();
        endQuitButton = new javax.swing.JButton();
        endLockButton = new javax.swing.JButton();
        time = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        timeProjectPackageClass = new javax.swing.JComboBox();
        timeJobNumber = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        timeLastJobHour = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        timeLastJobMinute = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        timeLastJobSecond = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        timeAverageHour = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        timeAverageMinute = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        timeAverageSecond = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        timeTotalHour = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        timeTotalMinute = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        timeTotalSecond = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        timeDbLInked = new javax.swing.JCheckBox();
        timeQuitButton = new javax.swing.JButton();
        timeQueryButton = new javax.swing.JButton();
        paymant = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        payProjectPackageClass = new javax.swing.JComboBox();
        payJobNumber = new javax.swing.JComboBox();
        jLabel27 = new javax.swing.JLabel();
        payHourlyPayWas = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        payAverageHourlyPay = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        payTotalPayment = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        payDbLinked = new javax.swing.JCheckBox();
        payQueryButton = new javax.swing.JButton();
        payQuitButton = new javax.swing.JButton();
        databaseUpdate = new javax.swing.JInternalFrame();
        jLabel18 = new javax.swing.JLabel();
        databaseUpadateNewUsername = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        databaseUpdateNewPassword = new javax.swing.JPasswordField();
        jLabel13 = new javax.swing.JLabel();
        databaseUpdateConfirmPassword = new javax.swing.JPasswordField();
        databaseNewLoginUpdateButton = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Time Clock");
        setAlwaysOnTop(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.CardLayout());

        timeClock.setPreferredSize(new java.awt.Dimension(500, 265));

        jLabel21.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("Add Branhc, Project, Package and Class for Start");

        jLabel28.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel28.setText("Add prefix for Comment by Start");

        jLabel32.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("Add Project, Package, Class for all other tab");

        addBPPCforStartTab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                addBPPCforStartTabKeyPressed(evt);
            }
        });

        addPrefixForCommentByStart.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                addPrefixForCommentByStartKeyPressed(evt);
            }
        });

        addPPCforEndTimePayTabs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                addPPCforEndTimePayTabsKeyPressed(evt);
            }
        });

        addQuitButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        addQuitButton.setText("quit");
        addQuitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addQuitButtonActionPerformed(evt);
            }
        });

        addButton.setForeground(new java.awt.Color(32, 64, 171));
        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        addLockButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        addLockButton.setText("lock");
        addLockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLockButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(addBPPCforStartTab)
                    .addComponent(addPrefixForCommentByStart)
                    .addComponent(addPPCforEndTimePayTabs)
                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE))
                .addContainerGap(71, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(102, 102, 102)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(addLockButton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(addQuitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel21)
                .addGap(0, 0, 0)
                .addComponent(addBPPCforStartTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel28)
                .addGap(0, 0, 0)
                .addComponent(addPrefixForCommentByStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(addPPCforEndTimePayTabs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addQuitButton)
                    .addComponent(addButton))
                .addGap(3, 3, 3)
                .addComponent(addLockButton)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        timeClock.addTab("Add", jPanel1);

        jLabel22.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel22.setText("Database type");

        databaseType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Couchdb", "DB2", "IMS DB", "Memcached", "MongoDB", "MySQL", "Neo4J", "OBIEE", "PL/SQL", "PostgreSQL", "Redis", "SQL Certificate", "SQL", "SQLite", " ", " ", " " }));
        databaseType.setSelectedIndex(5);
        databaseType.setToolTipText("");

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel1.setText("User ID.");

        databaseUserId.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100" }));

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel2.setText("Username");

        databaseUsername.setText("user");
        databaseUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseUsernameActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel3.setText("Password");

        databasePassword.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        databasePassword.setText("password");

        databaseRememberMeBox.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        databaseRememberMeBox.setText("remember me");

        databaseConnectionButton.setForeground(new java.awt.Color(255, 0, 0));
        databaseConnectionButton.setText("Connection");
        databaseConnectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseConnectionButtonActionPerformed(evt);
            }
        });

        databaseQuitButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        databaseQuitButton.setText("quit");
        databaseQuitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseQuitButtonActionPerformed(evt);
            }
        });

        databaseLockAutoConnectionBox.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        databaseLockAutoConnectionBox.setText("lock auto connection");
        databaseLockAutoConnectionBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseLockAutoConnectionBoxActionPerformed(evt);
            }
        });

        databaseUpdateButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        databaseUpdateButton.setText("update");
        databaseUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseUpdateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout databaseLayout = new javax.swing.GroupLayout(database);
        database.setLayout(databaseLayout);
        databaseLayout.setHorizontalGroup(
            databaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databaseLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(databaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(databaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(databaseLockAutoConnectionBox, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(databaseRememberMeBox))
                .addGap(18, 18, 18)
                .addGroup(databaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(databaseLayout.createSequentialGroup()
                        .addGroup(databaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(databasePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(databaseUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(databaseType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(databaseUserId, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 160, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, databaseLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(databaseUpdateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, databaseLayout.createSequentialGroup()
                        .addComponent(databaseConnectionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                        .addComponent(databaseQuitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        databaseLayout.setVerticalGroup(
            databaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databaseLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(databaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(databaseType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(databaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(databaseUserId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(databaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(databaseUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(databaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(databasePassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(databaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databaseConnectionButton)
                    .addComponent(databaseRememberMeBox)
                    .addComponent(databaseQuitButton))
                .addGap(3, 3, 3)
                .addGroup(databaseLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(databaseLockAutoConnectionBox)
                    .addComponent(databaseUpdateButton))
                .addContainerGap())
        );

        timeClock.addTab("DB", database);

        jLabel61.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel61.setText("Start at");

        startTimeAt.setModel(new javax.swing.SpinnerDateModel());
        startTimeAt.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        startTimeAt.setEnabled(false);

        jLabel41.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel41.setText("Branch;Project;Package;Class;No");

        startBranchProjectPkgClassNo.setEditable(true);
        startBranchProjectPkgClassNo.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        startBranchProjectPkgClassNo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "source,jext,model,Source,", "source,jext,model,SourceMethod," }));
        startBranchProjectPkgClassNo.setToolTipText("Please, type in the job number,\nafter the last semicolon!");
        startBranchProjectPkgClassNo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jLabel51.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel51.setText("Comment");

        startComment.setEditable(true);
        startComment.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        startComment.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "boolean ", "boolean is", "boolean contains", "int ", "int get", "String ", "String get", "List<String> ", "SourceMethod ", "List<SourceMethod> " }));

        jLabel23.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel23.setText("Developer, Pay for job");

        startDeveloper.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Gabor", "Dani", "Tamas" }));

        startPaymentForThisJob.setText("1666.67");

        startCurrency.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        startCurrency.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "HUF", "EUR", "USD" }));

        startDbLinked.setBackground(new java.awt.Color(32, 64, 171));
        startDbLinked.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        startDbLinked.setForeground(new java.awt.Color(0, 204, 0));
        startDbLinked.setText("DB linked");

        startButton.setForeground(new java.awt.Color(255, 0, 0));
        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        startQuitButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        startQuitButton.setText("quit");
        startQuitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startQuitButtonActionPerformed(evt);
            }
        });

        startDeleteLastBox.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        startDeleteLastBox.setText("delete last");
        startDeleteLastBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startDeleteLastBoxActionPerformed(evt);
            }
        });

        startLockButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        startLockButton.setText("lock");
        startLockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startLockButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout startLayout = new javax.swing.GroupLayout(start);
        start.setLayout(startLayout);
        startLayout.setHorizontalGroup(
            startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(startLayout.createSequentialGroup()
                        .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(startLayout.createSequentialGroup()
                        .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41)
                            .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                                .addComponent(jLabel51, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(startDbLinked, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(startDeleteLastBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, startLayout.createSequentialGroup()
                                    .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(startTimeAt, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(startBranchProjectPkgClassNo, 0, 235, Short.MAX_VALUE)
                                            .addComponent(startComment, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addGap(21, 21, 21))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, startLayout.createSequentialGroup()
                                    .addComponent(startDeveloper, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(startPaymentForThisJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(97, 97, 97)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, startLayout.createSequentialGroup()
                                .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(startCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(startLockButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(startLayout.createSequentialGroup()
                                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(startQuitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(21, 21, 21))))))
        );
        startLayout.setVerticalGroup(
            startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(startLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61)
                    .addComponent(startTimeAt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startBranchProjectPkgClassNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(startComment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(startDeveloper, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startPaymentForThisJob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startCurrency, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startButton)
                    .addComponent(startQuitButton)
                    .addComponent(startDbLinked))
                .addGap(3, 3, 3)
                .addGroup(startLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startLockButton)
                    .addComponent(startDeleteLastBox))
                .addContainerGap())
        );

        timeClock.addTab("Start", start);

        jLabel9.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel9.setText("End at");

        endTimeAt.setModel(new javax.swing.SpinnerDateModel());
        endTimeAt.setEnabled(false);

        jLabel24.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel24.setText("Project;Package;Class");

        endProjectPackageClass.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "jext,model,Source", "jext,model,SourceMethod" }));

        jLabel7.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel7.setText("Job's number");

        endJobNumber.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        endJobNumber.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100" }));
        endJobNumber.setToolTipText("");

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel8.setText("Status");

        endStatus.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        endStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Done", "WIP" }));

        endDbLinked.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        endDbLinked.setForeground(new java.awt.Color(0, 204, 0));
        endDbLinked.setText("DB linked");

        endButton.setForeground(new java.awt.Color(255, 0, 0));
        endButton.setText("End");
        endButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endButtonActionPerformed(evt);
            }
        });

        endQuitButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        endQuitButton.setText("quit");
        endQuitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endQuitButtonActionPerformed(evt);
            }
        });

        endLockButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        endLockButton.setText("lock");
        endLockButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endLockButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout endLayout = new javax.swing.GroupLayout(end);
        end.setLayout(endLayout);
        endLayout.setHorizontalGroup(
            endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, endLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(endLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(endLockButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(endLayout.createSequentialGroup()
                        .addGroup(endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(endDbLinked))
                        .addGap(18, 18, 18)
                        .addGroup(endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(endProjectPackageClass, 0, 309, Short.MAX_VALUE)
                            .addGroup(endLayout.createSequentialGroup()
                                .addGroup(endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(endJobNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(endStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(endTimeAt, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(endLayout.createSequentialGroup()
                                .addComponent(endButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(endQuitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        endLayout.setVerticalGroup(
            endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(endLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(endTimeAt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(7, 7, 7)
                .addGroup(endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(endProjectPackageClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(endJobNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(endStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(endLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(endButton)
                    .addComponent(endDbLinked)
                    .addComponent(endQuitButton))
                .addGap(3, 3, 3)
                .addComponent(endLockButton)
                .addContainerGap())
        );

        timeClock.addTab("End", end);

        jLabel26.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel26.setText("Project;Package;Class;No");

        timeProjectPackageClass.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "jext,model,Source", "jext,model,SourceMethod" }));
        timeProjectPackageClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeProjectPackageClassActionPerformed(evt);
            }
        });

        timeJobNumber.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100" }));
        timeJobNumber.setToolTipText("");
        timeJobNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeJobNumberActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel4.setText("Specified job held");

        timeLastJobHour.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        timeLastJobHour.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel6.setText(":");

        timeLastJobMinute.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        timeLastJobMinute.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel10.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel10.setText(":");

        timeLastJobSecond.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        timeLastJobSecond.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("HH:mm:ss");

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel12.setText("Average time in job");

        timeAverageHour.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        timeAverageHour.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel14.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel14.setText(":");

        timeAverageMinute.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        timeAverageMinute.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel15.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel15.setText(":");

        timeAverageSecond.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        timeAverageSecond.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("HH:mm:ss");

        jLabel20.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel20.setText("Total time in project");

        timeTotalHour.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        timeTotalHour.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel34.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel34.setText(":");

        timeTotalMinute.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        timeTotalMinute.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel17.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel17.setText(":");

        timeTotalSecond.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        timeTotalSecond.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("HH:mm:ss");

        timeDbLInked.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        timeDbLInked.setForeground(new java.awt.Color(0, 204, 0));
        timeDbLInked.setText("DB linked");

        timeQuitButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        timeQuitButton.setText("quit");
        timeQuitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeQuitButtonActionPerformed(evt);
            }
        });

        timeQueryButton.setForeground(new java.awt.Color(32, 64, 171));
        timeQueryButton.setText("Query");
        timeQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeQueryButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout timeLayout = new javax.swing.GroupLayout(time);
        time.setLayout(timeLayout);
        timeLayout.setHorizontalGroup(
            timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(timeLayout.createSequentialGroup()
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(timeProjectPackageClass, 0, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(timeJobNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(timeLayout.createSequentialGroup()
                        .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                                .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(timeDbLInked))
                        .addGap(18, 18, 18)
                        .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(timeLayout.createSequentialGroup()
                                .addComponent(timeQueryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                                .addComponent(timeQuitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(timeLayout.createSequentialGroup()
                                .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(timeLayout.createSequentialGroup()
                                        .addComponent(timeLastJobHour, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)
                                        .addComponent(timeLastJobMinute, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(timeLayout.createSequentialGroup()
                                        .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(timeAverageHour, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                                            .addComponent(timeTotalHour, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(3, 3, 3)
                                        .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(timeLastJobSecond, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(timeLayout.createSequentialGroup()
                                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(3, 3, 3)
                                                    .addComponent(timeAverageMinute, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(3, 3, 3)
                                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addGap(3, 3, 3)
                                                    .addComponent(timeAverageSecond, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(timeLayout.createSequentialGroup()
                                                .addComponent(jLabel34)
                                                .addGap(3, 3, 3)
                                                .addComponent(timeTotalMinute, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(3, 3, 3)
                                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(3, 3, 3)
                                                .addComponent(timeTotalSecond, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(18, 18, 18)
                                        .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        timeLayout.setVerticalGroup(
            timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(timeLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(timeProjectPackageClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(timeJobNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(timeLastJobHour, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(timeLastJobMinute, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(timeLastJobSecond, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11))
                    .addComponent(jLabel10))
                .addGap(12, 12, 12)
                .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(timeAverageHour, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)
                        .addComponent(timeAverageMinute, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(timeAverageSecond, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15)
                        .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel20)
                    .addComponent(timeTotalHour, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34)
                    .addComponent(timeTotalMinute, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(timeTotalSecond, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel17)))
                .addGap(37, 37, 37)
                .addGroup(timeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(timeQueryButton)
                    .addComponent(timeDbLInked)
                    .addComponent(timeQuitButton))
                .addGap(42, 42, 42))
        );

        timeClock.addTab("Time", time);

        jLabel25.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel25.setText("Project;Package;Class;No");

        payProjectPackageClass.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "jext,model,Source", "jext,model,SourceMethod" }));
        payProjectPackageClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payProjectPackageClassActionPerformed(evt);
            }
        });

        payJobNumber.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100" }));
        payJobNumber.setToolTipText("");
        payJobNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payJobNumberActionPerformed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel27.setText("Hourly pay was");

        payHourlyPayWas.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        payHourlyPayWas.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel29.setText("HUF/h");

        jLabel31.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel31.setText("Average hourly pay");

        payAverageHourlyPay.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        payAverageHourlyPay.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel33.setText("HUF/h");

        jLabel30.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel30.setText("Total payment");

        payTotalPayment.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        payTotalPayment.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel35.setText("HUF");

        payDbLinked.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        payDbLinked.setForeground(new java.awt.Color(0, 204, 0));
        payDbLinked.setText("DB linked");

        payQueryButton.setForeground(new java.awt.Color(32, 64, 171));
        payQueryButton.setText("Query");
        payQueryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payQueryButtonActionPerformed(evt);
            }
        });

        payQuitButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        payQuitButton.setText("quit");
        payQuitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payQuitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout paymantLayout = new javax.swing.GroupLayout(paymant);
        paymant.setLayout(paymantLayout);
        paymantLayout.setHorizontalGroup(
            paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paymantLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paymantLayout.createSequentialGroup()
                        .addComponent(payDbLinked)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(payQuitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paymantLayout.createSequentialGroup()
                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(payProjectPackageClass, 0, 1, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(payJobNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(paymantLayout.createSequentialGroup()
                        .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(paymantLayout.createSequentialGroup()
                                .addGap(173, 173, 173)
                                .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(payQueryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(payAverageHourlyPay, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(payHourlyPayWas, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(payTotalPayment, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(paymantLayout.createSequentialGroup()
                                .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE))
                                .addGap(124, 124, 124)
                                .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel29)
                                    .addComponent(jLabel33)
                                    .addComponent(jLabel35))))
                        .addGap(0, 142, Short.MAX_VALUE)))
                .addContainerGap())
        );
        paymantLayout.setVerticalGroup(
            paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paymantLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(payProjectPackageClass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(payJobNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(payHourlyPayWas, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel29))
                    .addComponent(jLabel27))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel33)
                        .addComponent(payAverageHourlyPay, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(payTotalPayment, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel35))
                    .addComponent(jLabel30))
                .addGap(37, 37, 37)
                .addGroup(paymantLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(payQueryButton)
                    .addComponent(payQuitButton)
                    .addComponent(payDbLinked))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        timeClock.addTab("Pay", paymant);

        getContentPane().add(timeClock, "card2");

        databaseUpdate.setClosable(true);
        databaseUpdate.setTitle("Update DB Login");
        databaseUpdate.setPreferredSize(new java.awt.Dimension(150, 75));
        databaseUpdate.setVisible(true);

        jLabel18.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel18.setText("New username");

        databaseUpadateNewUsername.setText("username");

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel5.setText("New password");

        databaseUpdateNewPassword.setText("password");

        jLabel13.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel13.setText("Confirm password");

        databaseUpdateConfirmPassword.setText("password");

        databaseNewLoginUpdateButton.setText("Update");
        databaseNewLoginUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseNewLoginUpdateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout databaseUpdateLayout = new javax.swing.GroupLayout(databaseUpdate.getContentPane());
        databaseUpdate.getContentPane().setLayout(databaseUpdateLayout);
        databaseUpdateLayout.setHorizontalGroup(
            databaseUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databaseUpdateLayout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(databaseUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(databaseUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(databaseUpadateNewUsername)
                    .addComponent(databaseUpdateConfirmPassword)
                    .addComponent(databaseUpdateNewPassword)
                    .addComponent(databaseNewLoginUpdateButton, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
                .addContainerGap(183, Short.MAX_VALUE))
        );
        databaseUpdateLayout.setVerticalGroup(
            databaseUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(databaseUpdateLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(databaseUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(databaseUpadateNewUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(databaseUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(databaseUpdateNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(databaseUpdateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(databaseUpdateConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(22, 22, 22)
                .addComponent(databaseNewLoginUpdateButton)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        getContentPane().add(databaseUpdate, "update");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * ADD.
     * Add Branch,Projekt,Package,Class,Comment for combo boxies by tabs.
     */
    // <editor-fold defaultstate="collapsed" desc="Add item for combo boxes">
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            if (addBPPCforStartTab.getText() != null) {
                if (config.isKey(BPPC)) {
                    String[] bppc = config.getValue(BPPC).split("/");
                    String lastBppc = "";
                    for (String bp : bppc) {
                        lastBppc = bp;
                    }
                    if (!lastBppc.equals(addBPPCforStartTab.getText())) {
                        Pattern pattern = Pattern.compile("\\w+\\,\\w+\\,\\w+\\,\\w+\\,");
                        Matcher matcher = pattern.matcher(addBPPCforStartTab.getText());
                        if (matcher.find()) {
                            config.saveValue(BPPC, config.getValue(BPPC).concat("/").concat(matcher.group()));
                            startBranchProjectPkgClassNo.addItem(matcher.group());
                            afterSave();
                            JOptionPane.showConfirmDialog(rootPane, "New project item added\nto combo box in Stat tab!", "Check", JOptionPane.DEFAULT_OPTION);
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "The correct form is >>>\nBranch,Project,Package,Class,", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "This BPPC already added!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    Pattern pattern = Pattern.compile("\\w+\\,\\w+\\,\\w+\\,\\w+\\,");
                    Matcher matcher = pattern.matcher(addBPPCforStartTab.getText());
                    if (matcher.find()) {
                        config.saveValue(BPPC, matcher.group());
                        startBranchProjectPkgClassNo.addItem(matcher.group());
                        afterSave();
                        JOptionPane.showConfirmDialog(rootPane, "New project item added\nto combo box in Stat tab!", "Check", JOptionPane.DEFAULT_OPTION);
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "The correct form is >>>\nBranch,Project,Package,Class,\nor empty text field!", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Empty BPPC field!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            if (addPrefixForCommentByStart.getText() != null) {
                if (config.isKey(COMMENT)) {
                    String[] comment = config.getValue(COMMENT).split("/");
                    String lastComment = "";
                    for (String com : comment) {
                        lastComment = com;
                    }
                    if (!lastComment.equals(addPrefixForCommentByStart.getText())) {
                        config.saveValue(COMMENT, config.getValue(COMMENT).concat("/").concat(addPrefixForCommentByStart.getText()));
                        startComment.addItem(addPrefixForCommentByStart.getText());
                        afterSave();
                        JOptionPane.showConfirmDialog(rootPane, "New comment item added\nto combo box in Stat tab!", "Check", JOptionPane.DEFAULT_OPTION);
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "This comment already added!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    config.saveValue(COMMENT, addPrefixForCommentByStart.getText());
                    startComment.addItem(addPrefixForCommentByStart.getText());
                    afterSave();
                    JOptionPane.showConfirmDialog(rootPane, "New comment item added\nto combo box in Stat tab!", "Check", JOptionPane.DEFAULT_OPTION);
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Empty Comment field!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            if (addPPCforEndTimePayTabs.getText() != null) {
                if (config.isKey(PPC)) {
                    String[] ppc = config.getValue(PPC).split("/");
                    String lastPpc = "";
                    for (String pp : ppc) {
                        lastPpc = pp;
                    }
                    if (!lastPpc.equals(addPPCforEndTimePayTabs.getText())) {
                        Pattern pattern = Pattern.compile("\\w+\\,\\w+\\,\\w+");
                        Matcher matcher = pattern.matcher(addPPCforEndTimePayTabs.getText());
                        if (matcher.find()) {
                            config.saveValue(PPC, config.getValue(PPC).concat("/").concat(matcher.group()));
                            endProjectPackageClass.addItem(addPPCforEndTimePayTabs.getText());
                            timeProjectPackageClass.addItem(addPPCforEndTimePayTabs.getText());
                            payProjectPackageClass.addItem(addPPCforEndTimePayTabs.getText());
                            afterSave();
                            JOptionPane.showConfirmDialog(rootPane, "New project item added to combo box\nin End, Time and Pay tab!", "Check", JOptionPane.DEFAULT_OPTION);
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "The correct form is >>>\nProject,Package,Class,\nor empty field!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "This PPC already added!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    Pattern pattern = Pattern.compile("\\w+\\,\\w+\\,\\w+");
                    Matcher matcher = pattern.matcher(addPPCforEndTimePayTabs.getText());
                    if (matcher.find()) {
                        config.saveValue(PPC, addPPCforEndTimePayTabs.getText());
                        endProjectPackageClass.addItem(addPPCforEndTimePayTabs.getText());
                        timeProjectPackageClass.addItem(addPPCforEndTimePayTabs.getText());
                        payProjectPackageClass.addItem(addPPCforEndTimePayTabs.getText());
                        afterSave();
                        JOptionPane.showConfirmDialog(rootPane, "New project item added to combo box\nin End, Time and Pay tab!", "Check", JOptionPane.DEFAULT_OPTION);
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "The correct form is >>>\nProject,Package,Class,\nor empty field!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Empty PPC field!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }// </editor-fold>//GEN-LAST:event_addButtonActionPerformed
    
    /**
     * ADD - AFTER SAVE.
     * Set the buttons text and the color.
     */
    // <editor-fold defaultstate="collapsed" desc="After save"> 
    private void afterSave() {
        addButton.setSelected(true);
        addButton.setText("Added");
        addButton.setForeground(Color.GREEN);
        addQuitButton.setSelected(true);
        addQuitButton.setForeground(Color.RED);
    }// </editor-fold> 
    
    /**
     * ADD - ADD BUTTON TEXT AND COLOR RESET.
     * Reset the add button's color and text if item changed.
     */
    // <editor-fold defaultstate="collapsed" desc="Reset the color">
    private void addBPPCforStartTabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addBPPCforStartTabKeyPressed
        if (addButton.getText().equals("Added")) {
            addButton.setSelected(false);
            addButton.setText("Add");
            addButton.setForeground(Color.RED);
            addQuitButton.setSelected(false);
            addQuitButton.setForeground(Color.BLACK);
        }
    }// </editor-fold>//GEN-LAST:event_addBPPCforStartTabKeyPressed
    
    
    /**
     * ADD - ADD BUTTON TEXT AND COLOR RESET.
     * Reset the add button's color and text if item changed.
     */
    // <editor-fold defaultstate="collapsed" desc="Reset the color">
    private void addPrefixForCommentByStartKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addPrefixForCommentByStartKeyPressed
        if (addButton.getText().equals("Added")) {
            addButton.setSelected(false);
            addButton.setText("Add");
            addButton.setForeground(Color.RED);
            addQuitButton.setSelected(false);
            addQuitButton.setForeground(Color.BLACK);
        }
    }// </editor-fold>//GEN-LAST:event_addPrefixForCommentByStartKeyPressed
    
    
    /**
     * ADD - ADD BUTTON TEXT AND COLOR RESET.
     * Reset the add button's color and text if item changed.
     */
    // <editor-fold defaultstate="collapsed" desc="Reset the color">
    private void addPPCforEndTimePayTabsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addPPCforEndTimePayTabsKeyPressed
        if (addButton.getText().equals("Added")) {
            addButton.setSelected(false);
            addButton.setText("Add");
            addButton.setForeground(Color.RED);
            addQuitButton.setSelected(false);
            addQuitButton.setForeground(Color.BLACK);
        }
    }// </editor-fold>//GEN-LAST:event_addPPCforEndTimePayTabsKeyPressed

    /**
     * ADD - LOCK BUTTON UNLOCK.
     * Unlock the add button after added.
     */
    // <editor-fold defaultstate="collapsed" desc="Add unlock">
    private void addLockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLockButtonActionPerformed
        addButton.setEnabled(true);
        addButton.setText("Add");
        addButton.setForeground(myBlue);
        addButton.setSelected(false);
        addLockButton.setText("lock");
        addLockButton.setEnabled(false);
        addQuitButton.setForeground(Color.BLACK);
        addQuitButton.setSelected(false);
    }// </editor-fold>//GEN-LAST:event_addLockButtonActionPerformed

    /**
     * ADD - QUIT.
     */
    // <editor-fold defaultstate="collapsed" desc="Quit">
    private void addQuitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addQuitButtonActionPerformed
        quit();
    }// </editor-fold>//GEN-LAST:event_addQuitButtonActionPerformed

    /**
     * DB - FAST LOGIN 
     * By valid username the valid password will load and connect to database
     * automatic.
     */
    @Deprecated
    // <editor-fold defaultstate="collapsed" desc="Loading password"> 
    private void databaseUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_databaseUsernameActionPerformed
        int id = Integer.parseInt(databaseUserId.getSelectedItem().toString());
        String username = databaseUsername.getText();
        userRep = new UserRepositoryBINImpl();
        try {
            if (userRep.findId(id)) {
                User user = userRep.findByUserName(username);
                databasePassword.setText(user.getPassword());
                databaseConnectionButton.setSelected(true);
                databaseConnectionButton.doClick();
            }
        } catch (TimeClockException | FileNotFoundException e) {
            JOptionPane.showMessageDialog(rootPane, "Access denied!\nWrong Id., Username\nor Password!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }// </editor-fold>//GEN-LAST:event_databaseUsernameActionPerformed
     
    /**
     * DB - CONNECTION "1st" TIME.
     * Connect to MySQL database(save user).
     */
    // <editor-fold defaultstate="collapsed" desc="First time database connection"> 
    private void databaseConnectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_databaseConnectionButtonActionPerformed
        String username = databaseUsername.getText();
        String password = "";
        char[] p = databasePassword.getPassword();
        for (char q : p) {
            password += q;
        }
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/TimeClock", username, password);
            jobRep = new JobRepositoryJDBCImpl(connection);
            payRep = new PayRepositoryJDBCImpl(connection);
            databaseConnectionButton.setSelected(true);
            databaseConnectionButton.setText("Connected");
            databaseConnectionButton.setForeground(Color.GREEN);
            open = true; // need by quit

            if (databaseRememberMeBox.isSelected()) {
                userRep = new UserRepositoryBINImpl();
                int id = Integer.parseInt(databaseUserId.getSelectedItem().toString());
                userRep.save(new User(id, username, password));
                User user = userRep.findById(id);
                if (user.getPassword().equals(password)) {
                    JOptionPane.showMessageDialog(rootPane, "Your Id., Username and\nPassword are saved!", "Save", JOptionPane.INFORMATION_MESSAGE);
                }
                databaseRememberMeBox.setSelected(false);
            }
            
            if (databaseLockAutoConnectionBox.isSelected()) {
                config.saveValue(AUTOCONNECT, "true");
                databaseLockAutoConnectionBox.setText("lock auto connection");
                databaseLockAutoConnectionBox.setSelected(false);
                if (config.isKey(AUTOCONNECT)) {
                    JOptionPane.showMessageDialog(rootPane, "Config file saved/created!", "Config", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
            if (config.isKey(NEXTACTION)) {
                if (config.getValue(NEXTACTION).equals("end")) {
                    timeClock.setSelectedComponent(end);
                } else {
                    timeClock.setSelectedComponent(start);
                }
            } else {
                timeClock.setSelectedComponent(start);
            }
            
            if (!connection.isClosed()) {
                startDbLinked.setSelected(true);
                endDbLinked.setSelected(true);
                timeDbLInked.setSelected(true);
                payDbLinked.setSelected(true);
            }
        } catch (SQLException | TimeClockException | FileNotFoundException e) {
            JOptionPane.showMessageDialog(rootPane, "Access denied!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }// </editor-fold>//GEN-LAST:event_databaseConnectionButtonActionPerformed
    
    /**
     * DB - CONNECTION FROM 2nd TIME.
     * If id valid then auto connect to database.
     */
    // <editor-fold defaultstate="collapsed" desc="Auto connect to database">
    private void autoConnect() {
        int id = Integer.parseInt(databaseUserId.getSelectedItem().toString());
        try {
            if (new File(USERFILE).exists()) {
                userRep = new UserRepositoryBINImpl();
                User user = userRep.findById(id);
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/TimeClock", user.getUserName(), user.getPassword());
                jobRep = new JobRepositoryJDBCImpl(connection);
                payRep = new PayRepositoryJDBCImpl(connection);
                databaseConnectionButton.setSelected(true);
                databaseConnectionButton.setText("Connected");
                databaseConnectionButton.setForeground(Color.GREEN);
                open = true;
                
                if (!connection.isClosed()) {
                    startDbLinked.setSelected(true);
                    endDbLinked.setSelected(true);
                    timeDbLInked.setSelected(true);
                    payDbLinked.setSelected(true);
                }
            } else {
                databaseRememberMeBox.setSelected(true);
            }
        } catch (SQLException | TimeClockException | FileNotFoundException e) {
            JOptionPane.showMessageDialog(rootPane, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }// </editor-fold>
    
    /**
     * DB - LOCK AUTO CONNECTION.
     * Lock the auto connection to the database.
     */
    // <editor-fold defaultstate="collapsed" desc="Lock auto connection">
    private void databaseLockAutoConnectionBoxActionPerformed(java.awt.event.ActionEvent evt) {                                                              
        try {
            config.saveValue(AUTOCONNECT, "false");
            if (!connection.isClosed()) {
                databaseConnectionButton.setEnabled(false);
            }
            databaseQuitButton.setEnabled(true);
            databaseQuitButton.setSelected(true);
            databaseQuitButton.setForeground(Color.RED);
        } catch (IOException | SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }// </editor-fold>                                                                

    /**
     * DB - SHOW INPUT PANEL TO SAVE NEW LOGIN.
     * Show the input panel for new login data.
     */
    // <editor-fold defaultstate="collapsed" desc="Show new data input panel">
    private void databaseUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_databaseUpdateButtonActionPerformed
        int id = Integer.parseInt(databaseUserId.getSelectedItem().toString());
        try {
            if (new File(USERFILE).exists()) {
                userRep = new UserRepositoryBINImpl();
                User user = userRep.findById(id);
                if (user.getPassword().equals(JOptionPane.showInputDialog(rootPane, "Password?"))) {
                    CardLayout cl = (CardLayout) getContentPane().getLayout();
                    cl.show(getContentPane(), "update");
                } else {
                    databaseUpdateButton.setSelected(false);
                    JOptionPane.showMessageDialog(rootPane, "Wrong password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "No saved data!\nYou can't update!", "Error", JOptionPane.ERROR_MESSAGE);
                databaseUpdateButton.setSelected(false);
            }
        } catch (TimeClockException | FileNotFoundException e) {
            JOptionPane.showMessageDialog(rootPane, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }// </editor-fold>//GEN-LAST:event_databaseUpdateButtonActionPerformed

    /**
     * DB - UPDATE LOGIN FILE.
     * If username and/or password changed then saveValue it.
     */
    // <editor-fold defaultstate="collapsed" desc="DB login update">
    private void databaseNewLoginUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_databaseNewLoginUpdateButtonActionPerformed
        String password = "";
        char[] p = databaseUpdateNewPassword.getPassword();
        for (char q : p) {
            password += q;
        }
        
        String cPassword = "";
        char[] cp = databaseUpdateConfirmPassword.getPassword();
        for (char q : cp) {
            cPassword += q;
        }
        
        int id = Integer.parseInt(databaseUserId.getSelectedItem().toString());
        if (password.equals(cPassword)) {
            try {
                userRep = new UserRepositoryBINImpl();
                userRep.update(new User(id, databaseUpadateNewUsername.getText(), password));
                User user = userRep.findById(id);
                if (user.getPassword().equals(password)) {
                    databaseNewLoginUpdateButton.setSelected(true);
                    databaseNewLoginUpdateButton.setText("Up to date");
                    databaseNewLoginUpdateButton.setForeground(Color.GREEN);
                    JOptionPane.showMessageDialog(rootPane, "Your Id., Username and\nPassword are up to date!", "Update", JOptionPane.INFORMATION_MESSAGE);
                }
                databaseUpdate.doDefaultCloseAction();
                timeClock.setSelectedComponent(start);
                databaseUpdateButton.setSelected(false);
            } catch (TimeClockException | FileNotFoundException e) {
                JOptionPane.showMessageDialog(rootPane, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Passwords are not the same!", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }// </editor-fold>//GEN-LAST:event_databaseNewLoginUpdateButtonActionPerformed
    
    /**
     * DB - QUIT.
     */
    // <editor-fold defaultstate="collapsed" desc="Quit">
    private void databaseQuitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_databaseQuitButtonActionPerformed
        quit();
    }// </editor-fold>//GEN-LAST:event_databaseQuitButtonActionPerformed

    /**
     * START - JOB.
     * Insert to database
     * Branch, Project, Package, Class, Job_number, Start_at, Comment, Developer_id.
     */
    // <editor-fold defaultstate="collapsed" desc="Starting the job"> 
    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
        Date date = (Date) startTimeAt.getValue();
        Instant instantDate = date.toInstant();
        LocalDateTime ldt = LocalDateTime.ofInstant(instantDate, ZoneId.systemDefault()); // with millis 
        LocalDateTime startAt = LocalDateTime.ofEpochSecond(ldt.toEpochSecond(ZoneOffset.UTC), 0, ZoneOffset.UTC); // without millis
        String string = startBranchProjectPkgClassNo.getSelectedItem().toString();
        String[] str = string.split(",");
        branch = str[0];
        project = str[1];
        sPackage = str[2];
        sClass = str[3];
        jobNumber = Integer.parseInt(str[4]);
        Job job = new Job(project, sPackage, sClass, jobNumber);
        Job updateJob = new Job(project, sPackage, sClass, jobNumber, startAt);
        Pay pay = new Pay(Double.valueOf(startPaymentForThisJob.getText()), startCurrency.getSelectedItem().toString());
        try {
            if (!startDeleteLastBox.isSelected()) {
                int developerId = jobRep.getDeveloperId(startDeveloper.getSelectedItem().toString());
                if (developerId != 0) { // valid
                    if (jobRep.checkJobNumber(job)) { // not used
                        jobRep.insert(new Job(branch, project, sPackage, sClass, jobNumber, startAt, startComment.getSelectedItem().toString(), developerId));
                        payRep.insert(pay);
                        afterStart(jobNumber);
                    } else {
                        if (!jobRep.isStatusNull(job)) {
                            if (jobRep.checkStatus(job).equals(WIP)) {
                                jobRep.updateStartAt(updateJob);
                                afterStart(jobNumber);
                            } else {
                                JOptionPane.showMessageDialog(rootPane, "This job is already done!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "This job number already use!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(rootPane, "Missing developer!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                jobRep.deleteLastIncorrectObject();
                jobRep.autoIncrement();
                
                payRep.deleteLastIncorrectObject();
                payRep.autoIncrement();
                
                startButton.setEnabled(false);
                startButton.setText("Deleted");
                startDeleteLastBox.setSelected(false);
                startQuitButton.setSelected(true);
                startQuitButton.setForeground(Color.RED);
                startLockButton.setEnabled(true);
                startLockButton.setText("unlock");
                config.saveValue(NEXTACTION, "start");
            }
        } catch (SQLException | IOException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (ArrayIndexOutOfBoundsException ex) {
            JOptionPane.showMessageDialog(rootPane, "Missing the job's number!\nPlease, check the semicolon too!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }// </editor-fold>//GEN-LAST:event_startButtonActionPerformed
    
    /**
     * START - AFTER START.
     * Set buttons text and color, update config file.
     */
    // <editor-fold defaultstate="collapsed" desc="After start"> 
    private void afterStart(int jobNumber) throws IOException {
        startButton.setSelected(true);
        startButton.setForeground(Color.GREEN);
        startButton.setText("Added to DB");
        endJobNumber.setSelectedItem(jobNumber+"");
        timeJobNumber.setSelectedItem(jobNumber+"");
        payJobNumber.setSelectedItem(jobNumber+"");
        JOptionPane.showConfirmDialog(rootPane, "Your job's start data,\nstored in database!", "Check", JOptionPane.DEFAULT_OPTION);
        config.saveValue(NUMBER, jobNumber+""); // save the last job number
        config.saveValue(NEXTACTION, "end");
        JOptionPane.showConfirmDialog(rootPane, "Job's number and next action saved!", "Save", JOptionPane.DEFAULT_OPTION);
        startQuitButton.setSelected(true);
        startQuitButton.setForeground(Color.RED);
    }// </editor-fold> 
    
    /**
     * START - LOCK BUTTON UNLOCK.
     * Unlock the start button after added.
     */
    // <editor-fold defaultstate="collapsed" desc="Start unlock">
    private void startLockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startLockButtonActionPerformed
        startButton.setEnabled(true);
        startButton.setText("Start");
        startButton.setForeground(Color.RED);
        startButton.setSelected(false);
        startLockButton.setText("lock");
        startLockButton.setEnabled(false);
        startQuitButton.setForeground(Color.BLACK);
        startQuitButton.setSelected(false);
    }// </editor-fold>//GEN-LAST:event_startLockButtonActionPerformed
    

    /**
     * START - SELECTED DELETE LAST INSERT BOX.
     * Delete last insert row from database for correction
     */
    // <editor-fold defaultstate="collapsed" desc="Delete last insert row">
    private void startDeleteLastBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startDeleteLastBoxActionPerformed
        startButton.setEnabled(true);
        startButton.setText("Delete");
        startButton.setForeground(Color.RED);
        startButton.setSelected(false);
        startQuitButton.setSelected(false);
        startQuitButton.setForeground(Color.BLACK);
        startLockButton.setEnabled(false);
    }// </editor-fold>//GEN-LAST:event_startDeleteLastBoxActionPerformed

    /**
     * START - QUIT.
     */
    // <editor-fold defaultstate="collapsed" desc="Quit"> 
    private void startQuitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startQuitButtonActionPerformed
        quit();
    }// </editor-fold>//GEN-LAST:event_startQuitButtonActionPerformed
    
    /**
     * END - JOB.
     * Update to database: 
     * End_at, status by Project, Package, Class, Job_number.
     */
    // <editor-fold defaultstate="collapsed" desc="End of job"> 
    private void endButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endButtonActionPerformed
        Date date = (Date) endTimeAt.getValue();
        Instant instantDate = date.toInstant();
        LocalDateTime ldt = LocalDateTime.ofInstant(instantDate, ZoneId.systemDefault()); // with millis 
        LocalDateTime endAt = LocalDateTime.ofEpochSecond(ldt.toEpochSecond(ZoneOffset.UTC), 0, ZoneOffset.UTC); // without millis
        String string = endProjectPackageClass.getSelectedItem().toString();
        String[] str = string.split(",");
        project = str[0];
        sPackage = str[1];
        sClass = str[2];
        jobNumber = Integer.parseInt(endJobNumber.getSelectedItem().toString());
        Job job = new Job(project, sPackage, sClass, jobNumber);
        Job updateJob = new Job(project, sPackage, sClass, jobNumber, endAt, endStatus.getSelectedItem().toString());
        try {
            if (!jobRep.checkJobNumber(job)) { // valid
                if (jobRep.isStatusNull(job)) { // null
                    jobRep.updateEndAtAndStatus(updateJob);
                    jobRep.updateToTime(job);
                    if (endStatus.getSelectedItem().toString().equals(WIP)) {
                        config.saveValue(STATUS, WIP);
                        jobRep.updateInPart(job);
                    }
                    afterEnd();
                } else {
                    if (jobRep.checkStatus(job).equals(WIP)) {
                        jobRep.updateEndAtAndStatus(updateJob);
                        jobRep.updateToTimeByWIP(job);
                        if (endStatus.getSelectedItem().toString().equals(DONE)) {
                            config.saveValue(STATUS, DONE);
                        }
                        afterEnd();
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "This job is already done!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "The job's number is invalid!\nSelect the correct number!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException | IOException | NullPointerException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }// </editor-fold>//GEN-LAST:event_endButtonActionPerformed
    
    /**
     * END - AFTER END.
     * Set the buttons text and color, update config file.
     */
    // <editor-fold defaultstate="collapsed" desc="After end"> 
    private void afterEnd() throws IOException {
        endButton.setSelected(true);
        endButton.setForeground(Color.GREEN);
        endButton.setText("Added to DB");
        JOptionPane.showConfirmDialog(rootPane, "Your job's end data,\nstored in database!", "Check", JOptionPane.DEFAULT_OPTION);
        config.saveValue(NEXTACTION, "start");
        JOptionPane.showConfirmDialog(rootPane, "Next action saved!", "Save", JOptionPane.DEFAULT_OPTION);
        endQuitButton.setSelected(true);
        endQuitButton.setForeground(Color.RED);
    }// </editor-fold>
    
    /**
     * END - LOCK BUTTON UNLOCK.
     * Unlock the end button after added.
     */
    // <editor-fold defaultstate="collapsed" desc="End unlock">
    private void endLockButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endLockButtonActionPerformed
        endButton.setEnabled(true);
        endButton.setText("End");
        endButton.setForeground(Color.RED);
        endButton.setSelected(false);
        endLockButton.setText("lock");
        endLockButton.setEnabled(false);
        endQuitButton.setForeground(Color.BLACK);
        endQuitButton.setSelected(false);
    }// </editor-fold>//GEN-LAST:event_endLockButtonActionPerformed
    
    /**
     * END - QUIT.
     */
    // <editor-fold defaultstate="collapsed" desc="Quit">
    private void endQuitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endQuitButtonActionPerformed
        quit();
    }// </editor-fold>//GEN-LAST:event_endQuitButtonActionPerformed

    /**
     * TIME - QUERY.
     * Query the last, average and total time in specified job or project.
     */
    // <editor-fold defaultstate="collapsed" desc="Time query"> 
    private void timeQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeQueryButtonActionPerformed
        String string = timeProjectPackageClass.getSelectedItem().toString();
        String[] str = string.split(",");
        project = str[0];
        sPackage = str[1];
        sClass = str[2];
        jobNumber = Integer.parseInt(timeJobNumber.getSelectedItem().toString());
        Job job = new Job(project, sPackage, sClass, jobNumber);
        try {
            if (!jobRep.checkJobNumber(job)) { // valid
                if (jobRep.checkStatus(job).equals(DONE)) {
                    timeInfoRep = new TimeInfoRepositoryJDBCImpl(connection);
                    TimeInfo timeInfo = (TimeInfo) timeInfoRep.getInfo(job);
                    
                    String[] toTime = timeInfo.getToTime().split(":");
                    hour = toTime[0];
                    minute = toTime[1];
                    second = toTime[2];
                    timeLastJobHour.setText(String.format("%s", hour));
                    timeLastJobMinute.setText(String.format("%s", minute));
                    timeLastJobSecond.setText(String.format("%s", second));

                    String[] averageTime = timeInfo.getAverageTime().split(":");
                    hour = averageTime[0];
                    minute = averageTime[1];
                    second = averageTime[2];
                    timeAverageHour.setText(String.format("%s", hour));
                    timeAverageMinute.setText(String.format("%s", minute));
                    timeAverageSecond.setText(String.format("%s", second));

                    String[] totalTime = timeInfo.getTotalTime().split(":");
                    hour = totalTime[0];
                    minute = totalTime[1];
                    second = totalTime[2];
                    timeTotalHour.setText(String.format("%s", hour));
                    timeTotalMinute.setText(String.format("%s", minute));
                    timeTotalSecond.setText(String.format("%s", second));
                } else {
                    JOptionPane.showMessageDialog(rootPane, "The job, work in progress!", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
                timeQueryButton.setSelected(true);
                timeQueryButton.setForeground(Color.GREEN);
                timeQuitButton.setSelected(true);
                timeQuitButton.setForeground(Color.RED);
            } else {
                timeQueryButton.setSelected(false);
                timeQueryButton.setForeground(Color.BLACK);
                timeQuitButton.setSelected(false);
                timeQuitButton.setForeground(Color.BLACK);
                JOptionPane.showMessageDialog(rootPane, "The job's number is invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch ( NullPointerException ex) {
            JOptionPane.showMessageDialog(rootPane, "The job, work in progress!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }// </editor-fold>//GEN-LAST:event_timeQueryButtonActionPerformed
    
    /**
     * TIME - QUERY BUTTON COLOR RESET.
     * Reset the query button's color if the item changed.
     */
    // <editor-fold defaultstate="collapsed" desc="Reset the color by new Query">
    private void timeProjectPackageClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeProjectPackageClassActionPerformed
        timeQueryButton.setSelected(false);
        timeQueryButton.setForeground(myBlue);
        timeQuitButton.setSelected(false);
        timeQuitButton.setForeground(Color.BLACK);
    }// </editor-fold>//GEN-LAST:event_timeProjectPackageClassActionPerformed
    
    /**
     * TIME - QUERY BUTTON COLOR RESET.
     * Reset the query button's color if the item changed.
     */
    // <editor-fold defaultstate="collapsed" desc="Reset the color by new Query">
    private void timeJobNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeJobNumberActionPerformed
        timeQueryButton.setSelected(false);
        timeQueryButton.setForeground(myBlue);
        timeQuitButton.setSelected(false);
        timeQuitButton.setForeground(Color.BLACK);
    }// </editor-fold>//GEN-LAST:event_timeJobNumberActionPerformed

    /**
     * TIME - QUIT.
     */
    // <editor-fold defaultstate="collapsed" desc="Quit">
    private void timeQuitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeQuitButtonActionPerformed
        quit();
    }// </editor-fold>//GEN-LAST:event_timeQuitButtonActionPerformed
    
    /**
     * PAY - QUERY.
     * Query the hourly, average and total payment for the specified job
     */
    // <editor-fold defaultstate="collapsed" desc="Pay query">
    private void payQueryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payQueryButtonActionPerformed
        String string = payProjectPackageClass.getSelectedItem().toString();
        String[] str = string.split(",");
        project = str[0];
        sPackage = str[1];
        sClass = str[2];
        jobNumber = Integer.parseInt(payJobNumber.getSelectedItem().toString());
        Job job = new Job(project, sPackage, sClass, jobNumber);
        try {
            if (!jobRep.checkJobNumber(job)) { // valid
                if (jobRep.checkStatus(job).equals(DONE)) {
                    payInfoRep = new PayInfoRepositoryJDBCImpl(connection);
                    PayInfo payInfo = (PayInfo) payInfoRep.getInfo(job);
                    
                    payHourlyPayWas.setText(payInfo.getHourlyPay()+"");
                    payAverageHourlyPay.setText(payInfo.getAverageHourlyPay()+"");
                    payTotalPayment.setText(payInfo.getTotalPayment()+"");
                } else {
                    JOptionPane.showMessageDialog(rootPane, "The job, work in progress!", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
                payQueryButton.setSelected(true);
                payQueryButton.setForeground(Color.GREEN);
                payQuitButton.setSelected(true);
                payQuitButton.setForeground(Color.RED);
            } else {
                payQueryButton.setSelected(false);
                payQueryButton.setForeground(Color.BLACK);
                payQuitButton.setSelected(false);
                payQuitButton.setForeground(Color.BLACK);
                JOptionPane.showMessageDialog(rootPane, "The job's number is invalid!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch ( NullPointerException ex) {
            JOptionPane.showMessageDialog(rootPane, "The job, work in progress!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }// </editor-fold>//GEN-LAST:event_payQueryButtonActionPerformed

    /**
     * PAY - QUERY BUTTON COLOR RESET.
     * Reset the query button's color if the item changed.
     */
    // <editor-fold defaultstate="collapsed" desc="Reset the color by new Query">
    private void payProjectPackageClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payProjectPackageClassActionPerformed
        payQueryButton.setSelected(false);
        payQueryButton.setForeground(myBlue);
        payQuitButton.setSelected(false);
        payQuitButton.setForeground(Color.BLACK);
    }// </editor-fold>//GEN-LAST:event_payProjectPackageClassActionPerformed
     
    
    /**
     * PAY - QUERY BUTTON COLOR RESET.
     * Reset the query button's color if the item changed.
     */
    // <editor-fold defaultstate="collapsed" desc="Reset the color by new Query">
    private void payJobNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payJobNumberActionPerformed
        payQueryButton.setSelected(false);
        payQueryButton.setForeground(myBlue);
        payQuitButton.setSelected(false);
        payQuitButton.setForeground(Color.BLACK);
    }// </editor-fold>//GEN-LAST:event_payJobNumberActionPerformed
     
    /**
     * PAY - QUIT.
     */
    // <editor-fold defaultstate="collapsed" desc="Quit">
    private void payQuitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_payQuitButtonActionPerformed
        quit();
    }// </editor-fold>//GEN-LAST:event_payQuitButtonActionPerformed

    /**
     * Close opened connection and quit from program.
     */
    // <editor-fold defaultstate="collapsed" desc="Quit">
    private void quit() {
        try {
            if (!connection.isClosed() || open) {
                jobRep.close();
                payRep.close();
                
                if (timeInfoRep != null) {
                    timeInfoRep.close();
                }
                
                if (payInfoRep != null) {
                    payInfoRep.close();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(rootPane, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        System.exit(0);
    }// </editor-fold> 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //<editor-fold defaultstate="collapsed" desc="Code">
        
        //</editor-fold>

        /**
         * Create and display the form.
         */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new TimeClock().setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(TimeClock.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }

    /**
     * Variables.
     */
    // <editor-fold defaultstate="collapsed" desc="Variables declaration">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField addBPPCforStartTab;
    private javax.swing.JButton addButton;
    private javax.swing.JButton addLockButton;
    private javax.swing.JTextField addPPCforEndTimePayTabs;
    private javax.swing.JTextField addPrefixForCommentByStart;
    private javax.swing.JButton addQuitButton;
    private javax.swing.JPanel database;
    private javax.swing.JButton databaseConnectionButton;
    private javax.swing.JCheckBox databaseLockAutoConnectionBox;
    private javax.swing.JToggleButton databaseNewLoginUpdateButton;
    private javax.swing.JPasswordField databasePassword;
    private javax.swing.JButton databaseQuitButton;
    private javax.swing.JCheckBox databaseRememberMeBox;
    private javax.swing.JComboBox databaseType;
    private javax.swing.JTextField databaseUpadateNewUsername;
    private javax.swing.JInternalFrame databaseUpdate;
    private javax.swing.JButton databaseUpdateButton;
    private javax.swing.JPasswordField databaseUpdateConfirmPassword;
    private javax.swing.JPasswordField databaseUpdateNewPassword;
    private javax.swing.JComboBox databaseUserId;
    private javax.swing.JTextField databaseUsername;
    private javax.swing.JPanel end;
    private javax.swing.JButton endButton;
    private javax.swing.JCheckBox endDbLinked;
    private javax.swing.JComboBox endJobNumber;
    private javax.swing.JButton endLockButton;
    private javax.swing.JComboBox endProjectPackageClass;
    private javax.swing.JButton endQuitButton;
    private javax.swing.JComboBox endStatus;
    private javax.swing.JSpinner endTimeAt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel payAverageHourlyPay;
    private javax.swing.JCheckBox payDbLinked;
    private javax.swing.JLabel payHourlyPayWas;
    private javax.swing.JComboBox payJobNumber;
    private javax.swing.JComboBox payProjectPackageClass;
    private javax.swing.JButton payQueryButton;
    private javax.swing.JButton payQuitButton;
    private javax.swing.JLabel payTotalPayment;
    private javax.swing.JPanel paymant;
    private javax.swing.JPanel start;
    private javax.swing.JComboBox startBranchProjectPkgClassNo;
    private javax.swing.JButton startButton;
    private javax.swing.JComboBox startComment;
    private javax.swing.JComboBox startCurrency;
    private javax.swing.JCheckBox startDbLinked;
    private javax.swing.JCheckBox startDeleteLastBox;
    private javax.swing.JComboBox startDeveloper;
    private javax.swing.JButton startLockButton;
    private javax.swing.JTextField startPaymentForThisJob;
    private javax.swing.JButton startQuitButton;
    private javax.swing.JSpinner startTimeAt;
    private javax.swing.JPanel time;
    private javax.swing.JLabel timeAverageHour;
    private javax.swing.JLabel timeAverageMinute;
    private javax.swing.JLabel timeAverageSecond;
    private javax.swing.JTabbedPane timeClock;
    private javax.swing.JCheckBox timeDbLInked;
    private javax.swing.JComboBox timeJobNumber;
    private javax.swing.JLabel timeLastJobHour;
    private javax.swing.JLabel timeLastJobMinute;
    private javax.swing.JLabel timeLastJobSecond;
    private javax.swing.JComboBox timeProjectPackageClass;
    private javax.swing.JButton timeQueryButton;
    private javax.swing.JButton timeQuitButton;
    private javax.swing.JLabel timeTotalHour;
    private javax.swing.JLabel timeTotalMinute;
    private javax.swing.JLabel timeTotalSecond;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
}