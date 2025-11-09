package sc2002_grpproject.boundary.company;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CompanyActionHandler {
    private CompanyRepresentative rep;
    private List<Internship> internships;
    private List<InternshipApplication> applications;
    private JFrame parentFrame;

    public CompanyActionHandler(CompanyRepresentative rep, List<Internship> internships,
                                List<InternshipApplication> applications, JFrame parentFrame) {
        this.rep = rep;
        this.internships = internships;
        this.applications = applications;
        this.parentFrame = parentFrame;
    }

    public void createInternship() {
        if (!rep.canCreateMoreInternships(internships)) {
            showError("You have reached the maximum limit of 5 active internship postings.");
            return;
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 255, 240));
        panel.setPreferredSize(new Dimension(600, 500));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField titleField = new JTextField(30);
        titleField.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JTextArea descArea = new JTextArea(4, 30);
        descArea.setFont(new Font("Arial", Font.PLAIN, 13));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        
        JComboBox<String> levelCombo = new JComboBox<>(new String[]{"BASIC", "INTERMEDIATE", "ADVANCED"});
        levelCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        
        String[] majors = {
            "Computer Science", "Data Science & AI", "Computer Engineering", "Information Engineering & Media",
            "Aerospace Engineering", "Chemical & Biomolecular Engineering",
            "Engineering in Electrical & Electronic", "Mathematical Sciences",
            "Communication Studies", "Business", "Social Sciences",
            "Biological Sciences", "Arts"
        };
        JComboBox<String> majorCombo = new JComboBox<>(majors);
        majorCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JSpinner slotsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        slotsSpinner.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JSpinner openingYearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2025, 2030, 1));
        JSpinner openingMonthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        JSpinner openingDaySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        
        JSpinner closingYearSpinner = new JSpinner(new SpinnerNumberModel(2025, 2025, 2030, 1));
        JSpinner closingMonthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        JSpinner closingDaySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 31, 1));
        
        JPanel openingDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        openingDatePanel.setBackground(new Color(240, 255, 240));
        openingDatePanel.add(new JLabel("Year:"));
        openingDatePanel.add(openingYearSpinner);
        openingDatePanel.add(new JLabel("Month:"));
        openingDatePanel.add(openingMonthSpinner);
        openingDatePanel.add(new JLabel("Day:"));
        openingDatePanel.add(openingDaySpinner);
        
        JPanel closingDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        closingDatePanel.setBackground(new Color(240, 255, 240));
        closingDatePanel.add(new JLabel("Year:"));
        closingDatePanel.add(closingYearSpinner);
        closingDatePanel.add(new JLabel("Month:"));
        closingDatePanel.add(closingMonthSpinner);
        closingDatePanel.add(new JLabel("Day:"));
        closingDatePanel.add(closingDaySpinner);
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        JLabel titleHeader = new JLabel("Create New Internship");
        titleHeader.setFont(new Font("Arial", Font.BOLD, 16));
        titleHeader.setForeground(new Color(0, 100, 0));
        panel.add(titleHeader, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Title:"), gbc);
        gbc.gridx = 1;
        panel.add(titleField, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1;
        panel.add(descScroll, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Level:"), gbc);
        gbc.gridx = 1;
        panel.add(levelCombo, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Preferred Major:"), gbc);
        gbc.gridx = 1;
        panel.add(majorCombo, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Number of Slots:"), gbc);
        gbc.gridx = 1;
        panel.add(slotsSpinner, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Opening Date:"), gbc);
        gbc.gridx = 1;
        panel.add(openingDatePanel, gbc);
        row++;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(createLabel("Closing Date:"), gbc);
        gbc.gridx = 1;
        panel.add(closingDatePanel, gbc);

        int result = JOptionPane.showConfirmDialog(parentFrame, panel, 
            "Create Internship", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String desc = descArea.getText().trim();
                String levelStr = (String) levelCombo.getSelectedItem();
                String major = (String) majorCombo.getSelectedItem();
                int slots = (Integer) slotsSpinner.getValue();
                
                int openYear = (Integer) openingYearSpinner.getValue();
                int openMonth = (Integer) openingMonthSpinner.getValue();
                int openDay = (Integer) openingDaySpinner.getValue();
                LocalDate opening = LocalDate.of(openYear, openMonth, openDay);
                
                int closeYear = (Integer) closingYearSpinner.getValue();
                int closeMonth = (Integer) closingMonthSpinner.getValue();
                int closeDay = (Integer) closingDaySpinner.getValue();
                LocalDate closing = LocalDate.of(closeYear, closeMonth, closeDay);

                if (title.isEmpty() || desc.isEmpty()) {
                    showError("Please fill in all fields");
                    return;
                }

                if (closing.isBefore(opening)) {
                    showError("Closing date must be after opening date");
                    return;
                }

                InternshipLevel level = InternshipLevel.valueOf(levelStr);
                Internship newInternship = new Internship(title, desc, rep.getCompanyName(), rep,
                    level, major, slots, opening, closing);
                
                internships.add(newInternship);
                rep.incrementInternshipsCreated();

                showInfo("Internship created successfully!\nID: " + newInternship.getInternshipID() + 
                        "\nStatus: PENDING (awaiting Career Center approval)");

            } catch (Exception e) {
                showError("Error creating internship: " + e.getMessage());
            }
        }
    }

    public void editInternship(String internshipId) {
        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);

        if (selected == null) {
            showError("Internship not found");
            return;
        }

        if (selected.getStatus() == InternshipStatus.APPROVED) {
            showError("Cannot edit approved internships");
            return;
        }
        
        if (selected.getStatus() == InternshipStatus.REJECTED) {
            showError("Cannot edit rejected internships");
            return;
        }
        
        if (selected.getStatus() == InternshipStatus.FILLED) {
            showError("Cannot edit filled internships");
            return;
        }

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBackground(new Color(220, 245, 220));
        
        JTextField titleField = new JTextField(selected.getTitle());
        JTextArea descArea = new JTextArea(selected.getDescription(), 3, 20);
        JScrollPane descScroll = new JScrollPane(descArea);
        JComboBox<String> levelCombo = new JComboBox<>(new String[]{"BASIC", "INTERMEDIATE", "ADVANCED"});
        levelCombo.setSelectedItem(selected.getLevel().toString());
        
        String[] majors = {
            "Computer Science", "Data Science & AI", "Computer Engineering", "Information Engineering & Media",
            "Aerospace Engineering", "Chemical & Biomolecular Engineering",
            "Engineering in Electrical & Electronic", "Mathematical Sciences",
            "Communication Studies", "Business", "Social Sciences",
            "Biological Sciences", "Arts"
        };
        JComboBox<String> majorCombo = new JComboBox<>(majors);
        majorCombo.setSelectedItem(selected.getPreferredMajor());
        majorCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        
        JTextField slotsField = new JTextField(String.valueOf(selected.getSlots()));
        
        panel.add(createLabel("Title:"));
        panel.add(titleField);
        panel.add(createLabel("Description:"));
        panel.add(descScroll);
        panel.add(createLabel("Level:"));
        panel.add(levelCombo);
        panel.add(createLabel("Preferred Major:"));
        panel.add(majorCombo);
        panel.add(createLabel("Slots (1-10):"));
        panel.add(slotsField);

        int result = JOptionPane.showConfirmDialog(parentFrame, panel, 
            "Edit Internship", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String title = titleField.getText().trim();
                String desc = descArea.getText().trim();
                String levelStr = (String) levelCombo.getSelectedItem();
                String major = (String) majorCombo.getSelectedItem();
                int slots = Integer.parseInt(slotsField.getText().trim());

                if (!title.isEmpty()) selected.setTitle(title);
                if (!desc.isEmpty()) selected.setDescription(desc);
                selected.setLevel(InternshipLevel.valueOf(levelStr));
                if (major != null && !major.isEmpty()) selected.setPreferredMajor(major);
                if (slots >= 1 && slots <= 10) selected.setSlots(slots);

                showInfo("Internship updated successfully!");

            } catch (Exception e) {
                showError("Error updating internship: " + e.getMessage());
            }
        }
    }

    public void deleteInternship(String internshipId) {
        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);

        if (selected == null) {
            showError("Internship not found");
            return;
        }

        if (selected.getStatus() == InternshipStatus.APPROVED) {
            showError("Cannot delete approved internships");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "Are you sure you want to delete:\n" + selected.getTitle() + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            applications.removeIf(app -> app.getInternship().getInternshipID().equals(internshipId));
            internships.remove(selected);
            rep.decrementInternshipsCreated();
            showInfo("Internship deleted successfully!");
        }
    }

    public void toggleVisibility(String internshipId) {
        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);

        if (selected == null) {
            showError("Internship not found");
            return;
        }

        if (selected.getStatus() != InternshipStatus.APPROVED) {
            showError("Can only toggle visibility for APPROVED internships");
            return;
        }

        selected.setVisible(!selected.isVisible());
        showInfo("Visibility toggled! New status: " + (selected.isVisible() ? "Visible" : "Hidden"));
    }

    public void processApplication(String appId, boolean approve) {
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (selected == null) {
            showError("Application not found");
            return;
        }

        if (selected.getStatus() != ApplicationStatus.PENDING) {
            showError("Can only process PENDING applications");
            return;
        }

        String action = approve ? "APPROVE" : "REJECT";
        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            action + " application from:\n" + selected.getStudent().getName() + 
            "\nFor: " + selected.getInternship().getTitle() + "?",
            "Confirm " + action, JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (approve) {
                long successfulCount = applications.stream()
                    .filter(app -> app.getInternship().getInternshipID().equals(selected.getInternship().getInternshipID()))
                    .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
                    .count();
                
                long availableSlots = selected.getInternship().getSlots() - successfulCount;
                
                if (availableSlots <= 0) {
                    showError("No available slots for this internship.");
                } else {
                    selected.setStatus(ApplicationStatus.SUCCESSFUL);
                    showInfo("Application approved!");
                }
            } else {
                selected.setStatus(ApplicationStatus.UNSUCCESSFUL);
                showInfo("Application rejected.");
            }
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(0, 80, 0));
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
