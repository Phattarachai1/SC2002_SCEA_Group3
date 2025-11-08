package sc2002_grpproject.boundary.panels.staff;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import sc2002_grpproject.boundary.helpers.UIHelper;
import sc2002_grpproject.controller.StaffController;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel for generating various reports
 */
public class ReportPanel extends JPanel {
    private final List<Student> students;
    private final List<Internship> internships;
    private final List<InternshipApplication> applications;
    private final List<CompanyRepresentative> companyReps;
    
    public ReportPanel(List<Student> students, List<Internship> internships,
                      List<InternshipApplication> applications, List<CompanyRepresentative> companyReps) {
        this.students = students;
        this.internships = internships;
        this.applications = applications;
        this.companyReps = companyReps;
        
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIHelper.LIGHT_MINT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(createTitlePanel(), BorderLayout.NORTH);
        add(createButtonPanel(), BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UIHelper.LIGHT_MINT);
        
        JLabel titleLabel = new JLabel("System Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(new Color(0, 80, 0));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        JLabel helpLabel = new JLabel("<html><i>Generate various reports to view system statistics and data</i></html>");
        helpLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        helpLabel.setForeground(new Color(100, 100, 100));
        titlePanel.add(helpLabel, BorderLayout.SOUTH);
        
        return titlePanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new GridLayout(4, 2, 20, 15));
        btnPanel.setBackground(UIHelper.LIGHT_MINT);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JButton studentsBtn = UIHelper.createStyledButton("Students Report");
        JButton internshipsBtn = UIHelper.createStyledButton("Internships Report");
        JButton applicationsBtn = UIHelper.createStyledButton("Applications Report");
        JButton companiesBtn = UIHelper.createStyledButton("Companies Report");
        JButton pendingIntBtn = UIHelper.createStyledButton("Pending Internships");
        JButton pendingWithdrawalsBtn = UIHelper.createStyledButton("Pending Withdrawals");
        JButton approvedIntBtn = UIHelper.createStyledButton("Approved Internships");
        JButton fullReportBtn = UIHelper.createStyledButton("Full System Report");
        
        studentsBtn.addActionListener(e -> showStudentsReport());
        internshipsBtn.addActionListener(e -> showInternshipsReport());
        applicationsBtn.addActionListener(e -> showApplicationsReport());
        companiesBtn.addActionListener(e -> showCompaniesReport());
        pendingIntBtn.addActionListener(e -> showPendingInternships());
        pendingWithdrawalsBtn.addActionListener(e -> showPendingWithdrawals());
        approvedIntBtn.addActionListener(e -> showApprovedInternships());
        fullReportBtn.addActionListener(e -> showFullReport());
        
        btnPanel.add(studentsBtn);
        btnPanel.add(internshipsBtn);
        btnPanel.add(applicationsBtn);
        btnPanel.add(companiesBtn);
        btnPanel.add(pendingIntBtn);
        btnPanel.add(pendingWithdrawalsBtn);
        btnPanel.add(approvedIntBtn);
        btnPanel.add(fullReportBtn);
        
        return btnPanel;
    }
    
    private void showStudentsReport() {
        long total = students.size();
        long withPlacements = applications.stream()
            .filter(app -> app.isPlacementConfirmed())
            .map(app -> app.getStudent().getUserID())
            .distinct()
            .count();
        long withoutPlacements = total - withPlacements;
        double placementRate = total > 0 ? (withPlacements * 100.0 / total) : 0;
        
        String report = String.format(
            "STUDENTS REPORT\n\n" +
            "Total Students: %d\n" +
            "With Confirmed Placements: %d\n" +
            "Without Placements: %d\n" +
            "Placement Rate: %.1f%%",
            total, withPlacements, withoutPlacements, placementRate
        );
        showReport("Students Report", report);
    }
    
    private void showInternshipsReport() {
        long total = internships.size();
        long approved = internships.stream().filter(i -> i.getStatus() == InternshipStatus.APPROVED).count();
        long pending = internships.stream().filter(i -> i.getStatus() == InternshipStatus.PENDING).count();
        long rejected = internships.stream().filter(i -> i.getStatus() == InternshipStatus.REJECTED).count();
        
        String report = String.format(
            "INTERNSHIPS REPORT\n\n" +
            "Total Internships: %d\n" +
            "Approved: %d\n" +
            "Pending: %d\n" +
            "Rejected: %d",
            total, approved, pending, rejected
        );
        showReport("Internships Report", report);
    }
    
    private void showApplicationsReport() {
        long total = applications.size();
        long successful = applications.stream().filter(a -> a.getStatus() == ApplicationStatus.SUCCESSFUL).count();
        long pending = applications.stream().filter(a -> a.getStatus() == ApplicationStatus.PENDING).count();
        long unsuccessful = applications.stream().filter(a -> a.getStatus() == ApplicationStatus.UNSUCCESSFUL).count();
        long withdrawn = applications.stream().filter(a -> a.getStatus() == ApplicationStatus.WITHDRAWN).count();
        
        String report = String.format(
            "APPLICATIONS REPORT\n\n" +
            "Total Applications: %d\n" +
            "Successful: %d\n" +
            "Pending: %d\n" +
            "Unsuccessful: %d\n" +
            "Withdrawn: %d",
            total, successful, pending, unsuccessful, withdrawn
        );
        showReport("Applications Report", report);
    }
    
    private void showCompaniesReport() {
        long totalCompanies = companyReps.stream().map(CompanyRepresentative::getCompanyName).distinct().count();
        long authorizedReps = companyReps.stream().filter(r -> r.getStatus() == ApprovalStatus.APPROVED).count();
        long pendingReps = companyReps.stream().filter(r -> r.getStatus() == ApprovalStatus.PENDING).count();
        long rejectedReps = companyReps.stream().filter(r -> r.getStatus() == ApprovalStatus.REJECTED).count();
        long totalPostings = internships.size();
        
        String report = String.format(
            "COMPANIES REPORT\n\n" +
            "Total Companies: %d\n" +
            "Authorized Representatives: %d\n" +
            "Pending Representatives: %d\n" +
            "Rejected Representatives: %d\n" +
            "Total Internship Postings: %d",
            totalCompanies, authorizedReps, pendingReps, rejectedReps, totalPostings
        );
        showReport("Companies Report", report);
    }
    
    private void showPendingInternships() {
        StringBuilder report = new StringBuilder("PENDING INTERNSHIPS\n\n");
        internships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.PENDING)
            .forEach(i -> report.append(String.format("• %s - %s\n", i.getTitle(), i.getCompanyName())));
        
        if (!report.toString().contains("•")) {
            report.append("No pending internships.");
        }
        showReport("Pending Internships", report.toString());
    }
    
    private void showPendingWithdrawals() {
        StringBuilder report = new StringBuilder("PENDING WITHDRAWALS\n\n");
        applications.stream()
            .filter(a -> a.isWithdrawalRequested() && a.getStatus() != ApplicationStatus.WITHDRAWN)
            .forEach(a -> report.append(String.format("• %s - %s (%s)\n",
                a.getStudent().getName(), a.getInternship().getTitle(), a.getApplicationId())));
        
        if (!report.toString().contains("•")) {
            report.append("No pending withdrawal requests.");
        }
        showReport("Pending Withdrawals", report.toString());
    }
    
    private void showApprovedInternships() {
        StringBuilder report = new StringBuilder("APPROVED INTERNSHIPS\n\n");
        internships.stream()
            .filter(i -> i.getStatus() == InternshipStatus.APPROVED)
            .forEach(i -> report.append(String.format("• %s - %s (Slots: %d, Confirmed: %d)\n",
                i.getTitle(), i.getCompanyName(), i.getSlots(), i.getConfirmedPlacements())));
        
        if (!report.toString().contains("•")) {
            report.append("No approved internships.");
        }
        showReport("Approved Internships", report.toString());
    }
    
    private void showFullReport() {
        String report = "FULL SYSTEM REPORT\n\n" +
            "Students: " + students.size() + "\n" +
            "Internships: " + internships.size() + "\n" +
            "Applications: " + applications.size() + "\n" +
            "Companies: " + companyReps.stream().map(CompanyRepresentative::getCompanyName).distinct().count();
        showReport("Full System Report", report);
    }
    
    private void showReport(String title, String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(Color.WHITE);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
