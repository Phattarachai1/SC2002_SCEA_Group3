package sc2002_grpproject.boundary.student;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentActionHandler {
    private Student student;
    private List<Internship> internships;
    private List<InternshipApplication> applications;
    private JFrame parentFrame;

    public StudentActionHandler(Student student, List<Internship> internships,
                                List<InternshipApplication> applications, JFrame parentFrame) {
        this.student = student;
        this.internships = internships;
        this.applications = applications;
        this.parentFrame = parentFrame;
    }

    public void applyForInternship(String internshipId) {
        // Check if student has already accepted a placement
        boolean hasAcceptedPlacement = applications.stream()
            .anyMatch(app -> app.getStudent().getUserID().equals(student.getUserID())
                          && app.getStatus() == ApplicationStatus.SUCCESSFUL
                          && app.isPlacementConfirmed());
        
        if (hasAcceptedPlacement) {
            showError("You have already accepted a placement and cannot apply to other internships.");
            return;
        }

        long activeApps = applications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .filter(app -> app.getStatus() == ApplicationStatus.PENDING 
                        || app.getStatus() == ApplicationStatus.SUCCESSFUL)
            .count();

        if (activeApps >= 3) {
            showError("You have reached the maximum of 3 active applications.");
            return;
        }

        Internship selected = internships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);

        if (selected == null) {
            showError("Internship not found");
            return;
        }

        boolean hasActiveApp = applications.stream()
            .anyMatch(app -> app.getStudent().getUserID().equals(student.getUserID()) 
                        && app.getInternship().getInternshipID().equals(internshipId)
                        && (app.getStatus() == ApplicationStatus.PENDING
                            || app.getStatus() == ApplicationStatus.SUCCESSFUL));

        if (hasActiveApp) {
            showError("You already have an active application for this internship.");
            return;
        }

        InternshipApplication newApp = new InternshipApplication(student, selected);
        applications.add(newApp);
        
        showInfo("Application submitted successfully!\nApplication ID: " + newApp.getApplicationId());
    }

    public void acceptPlacement(String appId) {
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (selected == null) {
            showError("Application not found");
            return;
        }

        if (selected.getStatus() != ApplicationStatus.SUCCESSFUL) {
            showError("You can only accept SUCCESSFUL applications.");
            return;
        }

        if (selected.isPlacementConfirmed()) {
            showError("This placement is already confirmed.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "Accept placement for:\n" + selected.getInternship().getTitle() + "\n\n" +
            "This will withdraw all your other applications.\nContinue?",
            "Confirm Acceptance", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            selected.setPlacementConfirmed(true);
            selected.getInternship().incrementConfirmedPlacements();

            applications.stream()
                .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
                .filter(app -> !app.getApplicationId().equals(appId))
                .filter(app -> app.getStatus() != ApplicationStatus.WITHDRAWN)
                .forEach(app -> app.setStatus(ApplicationStatus.WITHDRAWN));

            showInfo("Placement accepted! All other applications have been withdrawn.");
        }
    }

    public void requestWithdrawal(String appId) {
        InternshipApplication selected = applications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (selected == null) {
            showError("Application not found");
            return;
        }

        if (selected.getStatus() == ApplicationStatus.WITHDRAWN 
            || selected.getStatus() == ApplicationStatus.UNSUCCESSFUL) {
            showError("Cannot withdraw this application.");
            return;
        }

        if (selected.isWithdrawalRequested()) {
            showError("Withdrawal already requested for this application.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "Request withdrawal for:\n" + selected.getInternship().getTitle() + "\n\n" +
            "This will require staff approval.\nContinue?",
            "Confirm Withdrawal Request", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            selected.setWithdrawalRequested(true);
            showInfo("Withdrawal request submitted.\nAwaiting Career Center Staff approval.");
        }
    }

    public boolean isEligible(Student s, Internship i) {
        if (!i.getPreferredMajor().equalsIgnoreCase(s.getMajor())) {
            return false;
        }
        if (s.getYearOfStudy() <= 2 && i.getLevel() != InternshipLevel.BASIC) {
            return false;
        }
        return true;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(parentFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
