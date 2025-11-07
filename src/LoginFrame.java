package sc2002_grpproject;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LoginFrame extends JFrame {
    private JTextField userIdField;
    private JPasswordField passwordField;
    private List<User> allUsers;
    private List<Student> students;
    private List<CareerCenterStaff> staff;
    private List<CompanyRepresentative> companyReps;
    private List<Internship> internships;
    private List<InternshipApplication> applications;

    public LoginFrame(List<User> allUsers, List<Student> students, List<CareerCenterStaff> staff,
                     List<CompanyRepresentative> companyReps, List<Internship> internships,
                     List<InternshipApplication> applications) {
        this.allUsers = allUsers;
        this.students = students;
        this.staff = staff;
        this.companyReps = companyReps;
        this.internships = internships;
        this.applications = applications;

        setTitle("Internship Management System - Login");
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with green theme
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(230, 255, 230)); // Very light green background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title with decorative panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(144, 238, 144)); // Light green
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(34, 139, 34), 3),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel titleLabel = new JLabel("INTERNSHIP PLACEMENT SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 100, 0)); // Dark green text
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // Center panel for login form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(245, 255, 250)); // Very light mint
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(144, 238, 144), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User ID
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userIdLabel = new JLabel("User ID (Email):");
        userIdLabel.setForeground(new Color(0, 80, 0));
        userIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(userIdLabel, gbc);

        gbc.gridx = 1;
        userIdField = new JTextField(25);
        userIdField.setFont(new Font("Arial", Font.PLAIN, 16));
        userIdField.setPreferredSize(new Dimension(350, 40));
        userIdField.setMinimumSize(new Dimension(350, 40));
        userIdField.setEditable(true);
        userIdField.setEnabled(true);
        userIdField.setToolTipText("Enter your email address");
        formPanel.add(userIdField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(new Color(0, 80, 0));
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(25);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(350, 40));
        passwordField.setMinimumSize(new Dimension(350, 40));
        passwordField.setEditable(true);
        passwordField.setEnabled(true);
        passwordField.setToolTipText("Enter your password (default: password)");
        formPanel.add(passwordField, gbc);
        
        // Info label
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("<html><center><i>For students/staff: Use your User ID from CSV<br>Default password: 'password'</i></center></html>");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(infoLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(230, 255, 230));

        JButton loginButton = createStyledButton("Login");
        JButton registerButton = createStyledButton("Register (Company Rep)");
        JButton exitButton = createStyledButton("Exit");
        
        loginButton.setToolTipText("Click to login with your credentials");
        registerButton.setToolTipText("New company? Click here to register");
        exitButton.setToolTipText("Close the application");
        
    exitButton.setBackground(new Color(255, 200, 200)); // Light red for exit

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        RoundedButton button = new RoundedButton(text, 20);
        // neutral grey button, larger and round
        button.setBackground(new Color(200, 200, 200));
        button.setForeground(Color.BLACK);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleLogin() {
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (userId.isEmpty() || password.isEmpty()) {
            showError("Please enter both User ID and Password");
            return;
        }

        User user = allUsers.stream()
            .filter(u -> u.getUserID().equals(userId))
            .findFirst()
            .orElse(null);

        if (user == null) {
            showError("User not found. Please check your User ID.");
            return;
        }

        if (!user.checkPassword(password)) {
            showError("Incorrect password. Please try again.");
            return;
        }

        // Check company rep approval status
        if (user instanceof CompanyRepresentative) {
            CompanyRepresentative rep = (CompanyRepresentative) user;
            if (rep.getStatus() == CompanyRepresentative.ApprovalStatus.PENDING) {
                showError("Your account is pending approval by Career Center Staff.");
                return;
            } else if (rep.getStatus() == CompanyRepresentative.ApprovalStatus.REJECTED) {
                showError("Your account has been rejected. Please contact Career Center.");
                return;
            }
        }

        // Open appropriate dashboard
        openDashboard(user);
    }

    private void openDashboard(User user) {
        dispose(); // Close login window

        if (user instanceof Student) {
            new StudentDashboard((Student) user, internships, applications, allUsers, students, 
                               staff, companyReps);
        } else if (user instanceof CompanyRepresentative) {
            new CompanyDashboard((CompanyRepresentative) user, internships, applications, 
                               students, allUsers, staff, companyReps);
        } else if (user instanceof CareerCenterStaff) {
            new StaffDashboard((CareerCenterStaff) user, internships, applications, 
                             students, allUsers, staff, companyReps);
        }
    }

    private void handleRegister() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 255, 240));
        panel.setPreferredSize(new Dimension(550, 400));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel titleLabel = new JLabel("Company Representative Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(0, 100, 0));
        
        JTextField emailField = new JTextField(25);
        emailField.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JTextField nameField = new JTextField(25);
        nameField.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JTextField companyField = new JTextField(25);
        companyField.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JTextField deptField = new JTextField(25);
        deptField.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JTextField posField = new JTextField(25);
        posField.setFont(new Font("Arial", Font.PLAIN, 13));

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Company:"), gbc);
        gbc.gridx = 1;
        panel.add(companyField, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Department:"), gbc);
        gbc.gridx = 1;
        panel.add(deptField, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Position:"), gbc);
        gbc.gridx = 1;
        panel.add(posField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Register New Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String email = emailField.getText().trim();
            String name = nameField.getText().trim();
            String company = companyField.getText().trim();
            String dept = deptField.getText().trim();
            String position = posField.getText().trim();

            if (email.isEmpty() || name.isEmpty() || company.isEmpty()) {
                showError("Please fill in all required fields");
                return;
            }

            boolean exists = allUsers.stream().anyMatch(u -> u.getUserID().equals(email));
            if (exists) {
                showError("This email is already registered.");
                return;
            }

            CompanyRepresentative newRep = new CompanyRepresentative(email, name, company, dept, position);
            companyReps.add(newRep);
            allUsers.add(newRep);

            JOptionPane.showMessageDialog(this, 
                "Registration successful!\nYour account is pending approval.\nYou will be able to login once approved by Career Center Staff.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(0, 80, 0));
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
