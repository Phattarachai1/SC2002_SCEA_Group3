package sc2002_grpproject.boundary.staff;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.*;
import javax.swing.*;
import java.util.List;

/**
 * Handles all career center staff business logic and UI interactions.
 * Manages approval workflows for company representatives and internship postings.
 * 
 * @author SC2002_SCEA_Group3
 * @version 1.0
 */
public class StaffActionHandler {
    private List<Internship> internships;
    private List<InternshipApplication> applications;
    private List<CompanyRepresentative> companyReps;
    private JFrame parentFrame;

    public StaffActionHandler(List<Internship> internships,
                              List<InternshipApplication> applications,
                              List<CompanyRepresentative> companyReps,
                              JFrame parentFrame) {
        this.internships = internships;
        this.applications = applications;
        this.companyReps = companyReps;
        this.parentFrame = parentFrame;
    }

    // ============ Company Representative Actions ============
    
    public void authorizeRepresentative(String userId) {
        CompanyRepresentative rep = companyReps.stream()
            .filter(r -> r.getUserID().equals(userId))
            .findFirst()
            .orElse(null);

        if (rep == null) {
            showError("Representative not found");
            return;
        }

        if (rep.getStatus() == ApprovalStatus.APPROVED) {
            showError("This representative is already authorized");
            return;
        }

        if (rep.getStatus() == ApprovalStatus.REJECTED) {
            showError("Cannot authorize a rejected representative. Rejection is permanent.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "Authorize representative:\n" + rep.getName() + "\n" + rep.getCompanyName() + "?",
            "Confirm Authorization", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            rep.setStatus(ApprovalStatus.APPROVED);
            showSuccess("Company representative authorized successfully!");
        }
    }

    public void rejectRepresentative(String userId) {
        CompanyRepresentative rep = companyReps.stream()
            .filter(r -> r.getUserID().equals(userId))
            .findFirst()
            .orElse(null);

        if (rep == null) {
            showError("Representative not found");
            return;
        }

        if (rep.getStatus() == ApprovalStatus.REJECTED) {
            showError("This representative is already rejected");
            return;
        }

        if (rep.getStatus() == ApprovalStatus.APPROVED) {
            showError("Cannot reject an approved representative");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "Reject representative:\n" + rep.getName() + "\n" + rep.getCompanyName() + "?",
            "Confirm Rejection", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            rep.setStatus(ApprovalStatus.REJECTED);
            showSuccess("Company representative rejected.");
        }
    }

    // ============ Internship Actions ============
    
    public void approveInternship(String internshipId) {
        Internship internship = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);

        if (internship == null) {
            showError("Internship not found");
            return;
        }

        if (internship.getStatus() == InternshipStatus.APPROVED) {
            showError("This internship is already approved");
            return;
        }

        if (internship.getStatus() == InternshipStatus.REJECTED) {
            showError("Cannot approve a rejected internship");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "Approve internship:\n" + internship.getTitle() + "\nCompany: " + 
            internship.getCompanyName() + "?",
            "Confirm Approval", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            internship.setStatus(InternshipStatus.APPROVED);
            showSuccess("Internship approved successfully!");
        }
    }

    public void rejectInternship(String internshipId) {
        Internship internship = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);

        if (internship == null) {
            showError("Internship not found");
            return;
        }

        if (internship.getStatus() == InternshipStatus.REJECTED) {
            showError("This internship is already rejected");
            return;
        }

        if (internship.getStatus() == InternshipStatus.APPROVED) {
            showError("Cannot reject an approved internship");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "Reject internship:\n" + internship.getTitle() + "\nCompany: " + 
            internship.getCompanyName() + "?",
            "Confirm Rejection", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            internship.setStatus(InternshipStatus.REJECTED);
            showSuccess("Internship rejected.");
        }
    }

    // ============ Withdrawal Actions ============
    
    public void approveWithdrawal(String applicationId) {
        InternshipApplication application = applications.stream()
            .filter(app -> app.getApplicationId().equals(applicationId))
            .findFirst()
            .orElse(null);

        if (application == null) {
            showError("Application not found");
            return;
        }

        if (!"PENDING".equals(application.getWithdrawalStatus())) {
            showError("No pending withdrawal request found for this application");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "Approve withdrawal request:\nStudent: " + application.getStudent().getName() +
            "\nInternship: " + application.getInternship().getTitle() + "?",
            "Confirm Withdrawal", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // If placement was confirmed, decrement the confirmed placements
            if (application.isPlacementConfirmed()) {
                application.getInternship().decrementConfirmedPlacements();
            }
            
            application.setStatus(ApplicationStatus.WITHDRAWN);
            application.setWithdrawalStatus("APPROVED");
            showSuccess("Withdrawal approved. Placement slot released.");
        }
    }

    public void rejectWithdrawal(String applicationId) {
        InternshipApplication application = applications.stream()
            .filter(app -> app.getApplicationId().equals(applicationId))
            .findFirst()
            .orElse(null);

        if (application == null) {
            showError("Application not found");
            return;
        }

        if (!"PENDING".equals(application.getWithdrawalStatus())) {
            showError("No pending withdrawal request found for this application");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "Reject withdrawal request:\nStudent: " + application.getStudent().getName() +
            "\nInternship: " + application.getInternship().getTitle() + "?",
            "Confirm Rejection", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Application status remains unchanged
            application.setWithdrawalStatus("REJECTED");
            showSuccess("Withdrawal request rejected. Student will be notified.");
        }
    }

    // ============ Dialog Methods ============
    
    public void showError(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showWarning(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
