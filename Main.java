
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.*;

public class Main {
    static Connection conn;
    static String currentUser = null;

    public static void main(String[] args) {
        try {
            // connect to SQLite
            conn = DriverManager.getConnection("jdbc:sqlite:students.db");
            // create users table
            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS users (
                    username TEXT PRIMARY KEY,
                    password TEXT
                )
            """);
            // create students table
            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS students (
                    studentID TEXT PRIMARY KEY,
                    firstName TEXT,
                    lastName TEXT,
                    course TEXT,
                    yearLevel TEXT,
                    studentType TEXT
                )
            """);
            SwingUtilities.invokeLater(() -> showLoginScreen());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    static void showLoginScreen() {
        JFrame frame = new JFrame("Asian College SIS - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(320, 250);
        frame.setLocationRelativeTo(null);

        // set window icon
        try {
            ImageIcon logoIcon = new ImageIcon("asian.png");
            frame.setIconImage(logoIcon.getImage());
        } catch(Exception e) {
            System.out.println("Logo not found, skipping icon image.");
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // scaled logo for the login screen
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logo = new ImageIcon("asian.png");
            Image scaledImage = logo.getImage().getScaledInstance(175, 100, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
        } catch(Exception e) {
            System.out.println("Logo not found, skipping label image.");
        }

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(logoLabel, gbc);

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        gbc.gridwidth=1;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx=0; gbc.gridy=1;
        panel.add(userLabel, gbc);
        gbc.gridx=1;
        panel.add(userField, gbc);
        gbc.gridx=0; gbc.gridy=2;
        panel.add(passLabel, gbc);
        gbc.gridx=1;
        panel.add(passField, gbc);
        gbc.gridx=0; gbc.gridy=3;
        panel.add(loginBtn, gbc);
        gbc.gridx=1;
        panel.add(registerBtn, gbc);

        frame.add(panel);
        frame.setVisible(true);

        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in both username and password.");
                return;
            }
            try {
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    currentUser = username;
                    JOptionPane.showMessageDialog(frame, "Login successful!");
                    frame.dispose();
                    showStudentManager();
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid credentials.");
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        registerBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in both username and password.");
                return;
            }
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO users(username,password) VALUES(?,?)");
                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(frame, "User registered successfully.");
            } catch(SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Username already exists.");
            }
        });
    }

    static void showStudentManager() {
        JFrame frame = new JFrame("Asian College SIS - Student Management (User: " + currentUser + ")");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 550);
        frame.setLocationRelativeTo(null);

        // also apply the window icon
        try {
            ImageIcon logoIcon = new ImageIcon("asian_college_logo.png");
            frame.setIconImage(logoIcon.getImage());
        } catch(Exception e) {
            System.out.println("Logo not found for manager window, skipping icon.");
        }

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField idField = new JTextField(10);
        JTextField firstNameField = new JTextField(10);
        JTextField lastNameField = new JTextField(10);
        JTextField courseField = new JTextField(10);
        JTextField yearField = new JTextField(5);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Senior High School","College","Diploma"});

        gbc.gridx=0; gbc.gridy=0; inputPanel.add(new JLabel("ID:"),gbc);
        gbc.gridx=1; inputPanel.add(idField,gbc);
        gbc.gridx=2; inputPanel.add(new JLabel("First Name:"),gbc);
        gbc.gridx=3; inputPanel.add(firstNameField,gbc);
        gbc.gridx=4; inputPanel.add(new JLabel("Last Name:"),gbc);
        gbc.gridx=5; inputPanel.add(lastNameField,gbc);

        gbc.gridx=0; gbc.gridy=1; inputPanel.add(new JLabel("Course:"),gbc);
        gbc.gridx=1; inputPanel.add(courseField,gbc);
        gbc.gridx=2; inputPanel.add(new JLabel("Year Level:"),gbc);
        gbc.gridx=3; inputPanel.add(yearField,gbc);
        gbc.gridx=4; inputPanel.add(new JLabel("Type:"),gbc);
        gbc.gridx=5; inputPanel.add(typeBox,gbc);

        JButton addBtn = new JButton("Add Student");
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton logoutBtn = new JButton("Log Out");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(logoutBtn);

        String[] cols = {"Student ID", "First Name", "Last Name", "Course", "Year Level", "Type"};
        DefaultTableModel model = new DefaultTableModel(cols,0);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(Color.GRAY);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        refreshStudentTable(model);

        addBtn.addActionListener(e->{
            try{
                if(idField.getText().isEmpty() || firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() ||
                   courseField.getText().isEmpty() || yearField.getText().isEmpty()){
                    JOptionPane.showMessageDialog(frame,"Fill in all fields.");
                    return;
                }
                PreparedStatement ps = conn.prepareStatement("INSERT INTO students VALUES(?,?,?,?,?,?)");
                ps.setString(1, idField.getText());
                ps.setString(2, firstNameField.getText());
                ps.setString(3, lastNameField.getText());
                ps.setString(4, courseField.getText());
                ps.setString(5, yearField.getText());
                ps.setString(6, (String)typeBox.getSelectedItem());
                ps.executeUpdate();
                refreshStudentTable(model);
                idField.setText("");
                firstNameField.setText("");
                lastNameField.setText("");
                courseField.setText("");
                yearField.setText("");
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(frame,"Student ID already exists.");
            }
        });

        deleteBtn.addActionListener(e->{
            int row = table.getSelectedRow();
            if(row>=0){
                String id = (String)model.getValueAt(row,0);
                try{
                    conn.prepareStatement("DELETE FROM students WHERE studentID='"+id+"'").executeUpdate();
                    refreshStudentTable(model);
                }catch(SQLException ex){
                    ex.printStackTrace();
                }
            }
        });

        editBtn.addActionListener(e->{
            int row = table.getSelectedRow();
            if(row>=0){
                String id = (String)model.getValueAt(row,0);
                idField.setText((String)model.getValueAt(row,0));
                firstNameField.setText((String)model.getValueAt(row,1));
                lastNameField.setText((String)model.getValueAt(row,2));
                courseField.setText((String)model.getValueAt(row,3));
                yearField.setText((String)model.getValueAt(row,4));
                typeBox.setSelectedItem((String)model.getValueAt(row,5));

                int result = JOptionPane.showConfirmDialog(frame,"Confirm update?","Edit",JOptionPane.YES_NO_OPTION);
                if(result==JOptionPane.YES_OPTION){
                    try{
                        PreparedStatement ps = conn.prepareStatement("""
                            UPDATE students SET firstName=?, lastName=?, course=?, yearLevel=?, studentType=?
                            WHERE studentID=?
                        """);
                        ps.setString(1, firstNameField.getText());
                        ps.setString(2, lastNameField.getText());
                        ps.setString(3, courseField.getText());
                        ps.setString(4, yearField.getText());
                        ps.setString(5, (String)typeBox.getSelectedItem());
                        ps.setString(6, id);
                        ps.executeUpdate();
                        refreshStudentTable(model);
                    }catch(SQLException ex){
                        ex.printStackTrace();
                    }
                }
            }
        });

        logoutBtn.addActionListener(e->{
            frame.dispose();
            showLoginScreen();
        });

        frame.add(inputPanel,BorderLayout.NORTH);
        frame.add(scroll,BorderLayout.CENTER);
        frame.add(buttonPanel,BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    static void refreshStudentTable(DefaultTableModel model){
        try{
            model.setRowCount(0);
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM students");
            while(rs.next()){
                Vector<String> row = new Vector<>();
                for(int i=1;i<=6;i++) row.add(rs.getString(i));
                model.addRow(row);
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
}