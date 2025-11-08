package sc2002_grpproject.boundary;

import sc2002_grpproject.entity.*;
import sc2002_grpproject.enums.Enums.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StudentMenu {

    public static void show(Student student, List<Internship> allInternships, List<InternshipApplication> allApplications) {
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            showNotificationBanner(student, allApplications);
            System.out.println("\n--- Student Dashboard ---");
            System.out.println("1. View Available Internships");
            System.out.println("2. Apply for Internship");
            System.out.println("3. View My Applications");
            System.out.println("4. Accept Internship Placement");
            System.out.println("5. Request Withdrawal");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    viewAvailableInternships(student, allInternships);
                    break;
                case "2":
                    applyForInternship(student, allInternships, allApplications);
                    break;
                case "3":
                    viewMyApplications(student, allApplications);
                    break;
                case "4":
                    acceptPlacement(student, allApplications);
                    break;
                case "5":
                    requestWithdrawal(student, allApplications);
                    break;
                case "6":
                    changePassword(student, sc);
                    return; // This logout only happens AFTER successful password change
                case "7":
                    return; // Return to main loop to logout
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void viewAvailableInternships(Student student, List<Internship> allInternships) {
        System.out.println("\n--- Filter Options ---");
        System.out.println("1. View All Available Internships");
        System.out.println("2. Filter by Internship Level");
        System.out.println("3. Filter by Status");
        System.out.println("4. Filter by Closing Date");
        
        Scanner sc = new Scanner(System.in);
        System.out.print("Choose filter option: ");
        String filterChoice = sc.nextLine();
        
        List<Internship> filteredInternships = allInternships.stream()
    .filter(i -> i.isVisible() 
              && i.getStatus() == InternshipStatus.APPROVED
              && LocalDate.now().isAfter(i.getOpeningDate().minusDays(1))  
              && LocalDate.now().isBefore(i.getClosingDate().plusDays(1))
              && i.getConfirmedPlacements() < i.getSlots())  
    .filter(i -> isEligible(student, i))
    .collect(Collectors.toList());

        
        // Apply additional filters
        switch (filterChoice) {
            case "2": // Filter by Level
                System.out.print("Enter level (BASIC/INTERMEDIATE/ADVANCED): ");
                String levelInput = sc.nextLine().toUpperCase();
                try {
                    InternshipLevel level = InternshipLevel.valueOf(levelInput);
                    filteredInternships = filteredInternships.stream()
                        .filter(i -> i.getLevel() == level)
                        .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid level.");
                }
                break;
            case "3": // Filter by Status
                System.out.print("Enter status (PENDING/APPROVED/REJECTED/FILLED): ");
                String statusInput = sc.nextLine().toUpperCase();
                try {
                    InternshipStatus status = InternshipStatus.valueOf(statusInput);
                    filteredInternships = filteredInternships.stream()
                        .filter(i -> i.getStatus() == status)
                        .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid status.");
                }
                break;
            case "4": // Filter by Closing Date
                System.out.print("Enter closing date (YYYY-MM-DD): ");
                String dateInput = sc.nextLine();
                try {
                    LocalDate filterDate = LocalDate.parse(dateInput);
                    filteredInternships = filteredInternships.stream()
                        .filter(i -> i.getClosingDate().equals(filterDate))
                        .collect(Collectors.toList());
                } catch (Exception e) {
                    System.out.println("Invalid date format.");
                }
                break;
        }
        
        // Sort alphabetically by title
        filteredInternships.sort((i1, i2) -> i1.getTitle().compareToIgnoreCase(i2.getTitle()));
        
        if (filteredInternships.isEmpty()) {
            System.out.println("No internships available matching your criteria.");
        } else {
            for (Internship internship : filteredInternships) {
                System.out.println("\n" + internship.getInternshipID() + " - " + internship.getTitle());
                System.out.println("Company: " + internship.getCompanyName());
                System.out.println("Description: " + internship.getDescription());
                System.out.println("Level: " + internship.getLevel());
                System.out.println("Preferred Major: " + internship.getPreferredMajor());
                System.out.println("Slots: " + internship.getSlots());
                System.out.println("Opening Date: " + internship.getOpeningDate());
                System.out.println("Closing Date: " + internship.getClosingDate());
                System.out.println("Status: " + internship.getStatus());
            }
        }
    }

    private static boolean isEligible(Student student, Internship internship) {
        //Check students major and intership major requirements
        if (!internship.getPreferredMajor().equalsIgnoreCase(student.getMajor())) {
        return false;
        }

        // Check year eligibility for level
        if (student.getYearOfStudy() <= 2 && internship.getLevel() != InternshipLevel.BASIC) {
            return false;
        }
        return true;
    }

    private static void applyForInternship(Student student, List<Internship> allInternships, 
                                          List<InternshipApplication> allApplications) {
        // Check if student already has 3 pending/successful applications
        long activeApplications = allApplications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .filter(app -> app.getStatus() == ApplicationStatus.PENDING 
                        || app.getStatus() == ApplicationStatus.SUCCESSFUL)
            .count();

        if (activeApplications >= 3) {
            System.out.println("You have reached the maximum of 3 active applications.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Internship ID to apply: ");
        String internshipID = sc.nextLine().trim().toUpperCase();

        Internship selectedInternship = allInternships.stream()
            .filter(i -> i.getInternshipID().equals(internshipID))
            .findFirst()
            .orElse(null);

        if (selectedInternship == null) {
            System.out.println("Internship not found.");
            return;
        }

        // Check eligibility
        if (!selectedInternship.isVisible() || selectedInternship.getStatus() != InternshipStatus.APPROVED) {
            System.out.println("This internship is not available for application.");
            return;
        }

        
        if (LocalDate.now().isBefore(selectedInternship.getOpeningDate())) {
            System.out.println("Application period has not opened yet.");
            System.out.println("Opens on: " + selectedInternship.getOpeningDate());
            return;
        }

        if (LocalDate.now().isAfter(selectedInternship.getClosingDate())) {
            System.out.println("Application period has closed.");
            System.out.println("Closed on: " + selectedInternship.getClosingDate());
            return;
        }


        // Check if internship is full (by status OR by confirmed placements reaching slots)
        if (selectedInternship.getStatus() == InternshipStatus.FILLED ||
            selectedInternship.getConfirmedPlacements() >= selectedInternship.getSlots()) {
            System.out.println("This internship is already filled.");
            System.out.println("Slots: " + selectedInternship.getSlots() + 
                            " | Confirmed: " + selectedInternship.getConfirmedPlacements());
            return;
        }


        if (!isEligible(student, selectedInternship)) {
            System.out.println("You are not eligible for this internship level.");
            return;
        }

        // Check if already applied (only block if PENDING or SUCCESSFUL)
        boolean hasActiveApplication = allApplications.stream()
            .anyMatch(app -> app.getStudent().getUserID().equals(student.getUserID()) 
                        && app.getInternship().getInternshipID().equals(internshipID)
                        && (app.getStatus() == ApplicationStatus.PENDING
                            || app.getStatus() == ApplicationStatus.SUCCESSFUL));

        if (hasActiveApplication) {
            System.out.println("You already have an active application for this internship.");
            System.out.println("Please check 'View My Applications' for status.");
            return;
        }

        // Notify student if they reapply after withdrawal/rejection
        boolean hasPreviousApplication = allApplications.stream()
            .anyMatch(app -> app.getStudent().getUserID().equals(student.getUserID()) 
                        && app.getInternship().getInternshipID().equals(internshipID));

        if (hasPreviousApplication) {
            System.out.println("Note: You have previously applied to this internship.");
            System.out.println("Your new application will be reviewed by the company.");
        }


        InternshipApplication newApp = new InternshipApplication(student, selectedInternship);
        allApplications.add(newApp);
        System.out.println("Application submitted successfully! Application ID: " + newApp.getApplicationId());
    }

    private static void viewMyApplications(Student student, List<InternshipApplication> allApplications) {
        List<InternshipApplication> myApplications = allApplications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .collect(Collectors.toList());

        if (myApplications.isEmpty()) {
            System.out.println("You have no applications.");
        } else {
            System.out.println("\n--- My Applications ---");
            for (InternshipApplication app : myApplications) {
                System.out.println("\nApplication ID: " + app.getApplicationId());
                System.out.println("Internship: " + app.getInternship().getTitle());
                System.out.println("Company: " + app.getInternship().getCompanyName());
                System.out.println("Status: " + app.getStatus());
                System.out.println("Withdrawal Requested: " + (app.isWithdrawalRequested() ? "Yes" : "No"));
                System.out.println("Placement Confirmed: " + (app.isPlacementConfirmed() ? "Yes" : "No"));
            }
        }
    }

    private static void acceptPlacement(Student student, List<InternshipApplication> allApplications) {
        List<InternshipApplication> successfulApps = allApplications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
            .filter(app -> !app.isPlacementConfirmed())
            .collect(Collectors.toList());

        if (successfulApps.isEmpty()) {
            System.out.println("You have no successful applications to accept.");
            return;
        }

        System.out.println("\n--- Successful Applications ---");
        for (InternshipApplication app : successfulApps) {
            System.out.println(app.getApplicationId() + " - " + app.getInternship().getTitle() 
                             + " (" + app.getInternship().getCompanyName() + ")");
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Application ID to accept: ");
        String appId = sc.nextLine().trim().toUpperCase();

        InternshipApplication selectedApp = successfulApps.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (selectedApp == null) {
            System.out.println("Application not found.");
            return;
        }

        // Accept the placement
        selectedApp.setPlacementConfirmed(true);
        selectedApp.getInternship().incrementConfirmedPlacements();

        // Withdraw all other applications
        allApplications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .filter(app -> !app.getApplicationId().equals(appId))
            .filter(app -> app.getStatus() != ApplicationStatus.WITHDRAWN)
            .forEach(app -> app.setStatus(ApplicationStatus.WITHDRAWN));

        System.out.println("Placement accepted! All other applications have been withdrawn.");
    }

    private static void requestWithdrawal(Student student, List<InternshipApplication> allApplications) {
        List<InternshipApplication> withdrawableApps = allApplications.stream()
            .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
            .filter(app -> app.getStatus() != ApplicationStatus.WITHDRAWN 
                        && app.getStatus() != ApplicationStatus.UNSUCCESSFUL)
            .filter(app -> !app.isWithdrawalRequested())
            .collect(Collectors.toList());

        if (withdrawableApps.isEmpty()) {
            System.out.println("You have no applications to withdraw.");
            return;
        }

        System.out.println("\n--- Your Applications ---");
        for (InternshipApplication app : withdrawableApps) {
            System.out.println(app.getApplicationId() + " - " + app.getInternship().getTitle() 
                             + " (Status: " + app.getStatus() + ")");
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Application ID to request withdrawal: ");
        String appId = sc.nextLine().trim().toUpperCase();

        InternshipApplication selectedApp = withdrawableApps.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);

        if (selectedApp == null) {
            System.out.println("Application not found.");
            return;
        }

        selectedApp.setWithdrawalRequested(true);
        System.out.println("Withdrawal request submitted. Awaiting Career Center Staff approval.");
    }

    private static void changePassword(Student student, Scanner sc) {
    // Step 1: Verify current password
    System.out.print("Enter current password: ");
    String currentPassword = sc.nextLine();
    
    if (!student.checkPassword(currentPassword)) {
        System.out.println("Incorrect current password. Password change cancelled.");
        return; // Stay logged in, return to menu
    }
    
    // Step 2: Loop until new passwords match
    boolean passwordsMatch = false;
    String newPassword = "";
    
    while (!passwordsMatch) {
        System.out.print("Enter new password: ");
        newPassword = sc.nextLine();
        
        System.out.print("Confirm new password: ");
        String confirmPassword = sc.nextLine();
        
        if (newPassword.equals(confirmPassword)) {
            passwordsMatch = true;
        } else {
            System.out.println("\nPasswords do not match. Please try again.");
            System.out.print("Do you want to continue? (yes/no): ");
            String choice = sc.nextLine().toLowerCase();
            
            if (!choice.equals("yes") && !choice.equals("y")) {
                System.out.println("Password change cancelled.");
                return; // Stay logged in, return to menu
            }
            // If yes, loop continues to retry
        }
    }
    
    // Step 3: Change password successfully
    student.changePassword(newPassword);
    System.out.println("\n========================================");
    System.out.println("Password changed successfully!");
    System.out.println("For security, you will be logged out.");
    System.out.println("Please login again with your new password.");
    System.out.println("========================================");
    
    // Force logout - this return will exit the show() method's while loop
}

private static void showNotificationBanner(Student student, List<InternshipApplication> allApplications) {
    // Check for application status updates
    List<InternshipApplication> studentApps = allApplications.stream()
        .filter(app -> app.getStudent().getUserID().equals(student.getUserID()))
        .collect(Collectors.toList());
    
    long approved = studentApps.stream()
        .filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
        .count();
    long rejected = studentApps.stream()
        .filter(app -> app.getStatus() == ApplicationStatus.UNSUCCESSFUL)
        .count();
    long withdrawn = studentApps.stream()
        .filter(app -> app.getStatus() == ApplicationStatus.WITHDRAWN)
        .count();
    
    if (approved > 0 || rejected > 0 || withdrawn > 0) {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("  🔔 APPLICATION STATUS UPDATES");
        System.out.println("─".repeat(70));
        if (approved > 0) {
            System.out.println("  ✓ " + approved + " application(s) APPROVED");
        }
        if (rejected > 0) {
            System.out.println("  ✗ " + rejected + " application(s) REJECTED");
        }
        if (withdrawn > 0) {
            System.out.println("  ⊗ " + withdrawn + " application(s) WITHDRAWN");
        }
        System.out.println("  → Check 'View My Applications' for details");
        System.out.println("═".repeat(70));
    }
}

}
