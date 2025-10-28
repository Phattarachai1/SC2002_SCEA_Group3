package sc2002_grpproject;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CompanyMenu {

    public static void show(CompanyRepresentative rep, List<Internship> allInternships,
                           List<InternshipApplication> allApplications, List<Student> allStudents) {
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            showNotificationBanner(rep, allInternships, allApplications);
            System.out.println("\n--- Company Representative Dashboard ---");
            System.out.println("1. Create Internship Opportunity");
            System.out.println("2. View My Internship Postings");
            System.out.println("3. Edit Internship Opportunity");
            System.out.println("4. Delete Internship Opportunity");
            System.out.println("5. Toggle Internship Visibility");
            System.out.println("6. View and Process Applications (Pending Only)");
            System.out.println("7. View All Applications History");
            System.out.println("8. Change Password");
            System.out.println("9. Logout");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    createInternship(rep, allInternships);
                    break;
                case "2":
                    viewMyPostings(rep, allInternships);
                    break;
                case "3":
                    editInternship(rep, allInternships);
                    break;
                case "4":
                    deleteInternship(rep, allInternships, allApplications);
                    break;
                case "5":
                    toggleVisibility(rep, allInternships);
                    break;
                case "6":
                    processApplications(rep, allInternships, allApplications);
                    break;
                case "7":
                    viewAllApplications(rep, allInternships, allApplications);
                    break;
                case "8":
                    changePassword(rep, sc);
                    return;
                case "9":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void createInternship(CompanyRepresentative rep, List<Internship> allInternships) {
        if (!rep.canCreateMoreInternships()) {
            System.out.println("You have reached the maximum limit of 5 internship postings.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter internship title: ");
        String title = sc.nextLine();
        System.out.print("Enter internship description: ");
        String description = sc.nextLine();
        System.out.print("Enter internship level (BASIC/INTERMEDIATE/ADVANCED): ");
        String levelInput = sc.nextLine().toUpperCase();
        Internship.InternshipLevel level;
        try {
            level = Internship.InternshipLevel.valueOf(levelInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid level. Defaulting to BASIC.");
            level = Internship.InternshipLevel.BASIC;
        }

        System.out.print("Enter preferred major: ");
        String major = sc.nextLine();
        int slots = 0;
        while (slots <= 0 || slots > 10) {
            System.out.print("Enter number of slots (1-10): ");
            try {
                slots = Integer.parseInt(sc.nextLine());
                if (slots <= 0) {
                    System.out.println("Slots must be greater than 0.");
                } else if (slots > 10) {
                    System.out.println("Maximum 10 slots allowed per internship.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        // Improved date parsing with validation
        LocalDate openingDate = null;
        while (openingDate == null) {
            System.out.print("Enter application opening date (YYYY-MM-DD, e.g., 2025-06-01): ");
            String dateInput = sc.nextLine().trim();
            openingDate = parseDate(dateInput);
            if (openingDate == null) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD (e.g., 2025-06-01)");
            }
        }
        LocalDate closingDate = null;
        while (closingDate == null) {
            System.out.print("Enter application closing date (YYYY-MM-DD, e.g., 2025-12-31): ");
            String dateInput = sc.nextLine().trim();
            closingDate = parseDate(dateInput);
            if (closingDate == null) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD (e.g., 2025-12-31)");
            } else if (closingDate.isBefore(openingDate)) {
                System.out.println("Closing date must be after opening date.");
                closingDate = null;
            }
        }

        Internship newInternship = new Internship(title, description, rep.getCompanyName(), rep,
                level, major, slots, openingDate, closingDate);
        allInternships.add(newInternship);
        rep.incrementInternshipsCreated();
        System.out.println("Internship created successfully! ID: " + newInternship.getInternshipID());
        System.out.println("Status: PENDING (awaiting Career Center approval)");
    }

    // Add this helper method to CompanyMenu class
    private static LocalDate parseDate(String dateInput) {
        try {
            // First try standard ISO format
            return LocalDate.parse(dateInput);
        } catch (Exception e1) {
            try {
                // Try to handle single-digit months/days by adding zeros
                String[] parts = dateInput.split("-");
                if (parts.length == 3) {
                    String year = parts[0];
                    String month = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
                    String day = parts[2].length() == 1 ? "0" + parts[2] : parts[2];
                    return LocalDate.parse(year + "-" + month + "-" + day);
                }
            } catch (Exception e2) {
                // Fall through to return null
            }
        }
        return null;
    }

    private static void viewMyPostings(CompanyRepresentative rep, List<Internship> allInternships) {
        System.out.println("\n--- Filter Options ---");
        System.out.println("1. View All My Postings");
        System.out.println("2. Filter by Status");
        System.out.println("3. Filter by Level");
        System.out.println("4. Filter by Visibility");

        Scanner sc = new Scanner(System.in);
        System.out.print("Choose filter option: ");
        String filterChoice = sc.nextLine();

        List<Internship> myInternships = allInternships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());

        // Apply filters
        switch (filterChoice) {
            case "2": // Filter by Status
                System.out.print("Enter status (PENDING/APPROVED/REJECTED/FILLED): ");
                String statusInput = sc.nextLine().toUpperCase();
                try {
                    Internship.InternshipStatus status = Internship.InternshipStatus.valueOf(statusInput);
                    myInternships = myInternships.stream()
                        .filter(i -> i.getStatus() == status)
                        .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid status.");
                }
                break;
            case "3": // Filter by Level
                System.out.print("Enter level (BASIC/INTERMEDIATE/ADVANCED): ");
                String levelInput = sc.nextLine().toUpperCase();
                try {
                    Internship.InternshipLevel level = Internship.InternshipLevel.valueOf(levelInput);
                    myInternships = myInternships.stream()
                        .filter(i -> i.getLevel() == level)
                        .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid level.");
                }
                break;
            case "4": // Filter by Visibility
                System.out.print("Show visible only? (yes/no): ");
                String visInput = sc.nextLine().trim().toLowerCase();
                boolean showVisible = visInput.equals("yes");
                myInternships = myInternships.stream()
                    .filter(i -> i.isVisible() == showVisible)
                    .collect(Collectors.toList());
                break;
        }

        if (myInternships.isEmpty()) {
            System.out.println("No internship postings found.");
        } else {
            for (Internship internship : myInternships) {
                System.out.println("\n" + internship.getInternshipID() + " - " + internship.getTitle());
                System.out.println("Description: " + internship.getDescription());
                System.out.println("Level: " + internship.getLevel());
                System.out.println("Preferred Major: " + internship.getPreferredMajor());
                System.out.println("Slots: " + internship.getSlots() + " | Confirmed: " + internship.getConfirmedPlacements());
                System.out.println("Opening Date: " + internship.getOpeningDate());
                System.out.println("Closing Date: " + internship.getClosingDate());
                System.out.println("Status: " + internship.getStatus());
                System.out.println("Visible: " + (internship.isVisible() ? "Yes" : "No"));
            }
        }
    }

    private static void editInternship(CompanyRepresentative rep, List<Internship> allInternships) {
        List<Internship> myInternships = allInternships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .filter(i -> i.getStatus() != Internship.InternshipStatus.APPROVED) // Can't edit approved
            .collect(Collectors.toList());
        if (myInternships.isEmpty()) {
            System.out.println("No editable internship postings found. (Approved internships cannot be edited)");
            return;
        }

        System.out.println("\n--- Editable Internships ---");
        for (Internship internship : myInternships) {
            System.out.println(internship.getInternshipID() + " - " + internship.getTitle() +
                " (Status: " + internship.getStatus() + ")");
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Internship ID to edit: ");
        String internshipID = sc.nextLine().trim().toUpperCase();
        Internship selectedInternship = myInternships.stream()
            .filter(i -> i.getInternshipID().equals(internshipID))
            .findFirst()
            .orElse(null);
        if (selectedInternship == null) {
            System.out.println("Internship not found or cannot be edited.");
            return;
        }

        System.out.println("\n--- Edit Internship ---");
        System.out.println("Leave blank to keep current value");
        System.out.print("Enter new title [" + selectedInternship.getTitle() + "]: ");
        String title = sc.nextLine();
        if (!title.isEmpty()) selectedInternship.setTitle(title);
        System.out.print("Enter new description [" + selectedInternship.getDescription() + "]: ");
        String description = sc.nextLine();
        if (!description.isEmpty()) selectedInternship.setDescription(description);
        System.out.print("Enter new level [" + selectedInternship.getLevel() + "]: ");
        String levelInput = sc.nextLine().toUpperCase();
        if (!levelInput.isEmpty()) {
            try {
                selectedInternship.setLevel(Internship.InternshipLevel.valueOf(levelInput));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid level. Keeping current value.");
            }
        }
        System.out.print("Enter new preferred major [" + selectedInternship.getPreferredMajor() + "]: ");
        String major = sc.nextLine();
        if (!major.isEmpty()) selectedInternship.setPreferredMajor(major);
        System.out.print("Enter new number of slots [" + selectedInternship.getSlots() + "] (1-10): ");
        String slotsInput = sc.nextLine();
        if (!slotsInput.isEmpty()) {
            try {
                int newSlots = Integer.parseInt(slotsInput);
                if (newSlots > 0 && newSlots <= 10) {
                    selectedInternship.setSlots(newSlots);
                } else {
                    System.out.println("Invalid slots. Must be between 1-10. Keeping current value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Keeping current value.");
            }
        }
        System.out.print("Enter new opening date [" + selectedInternship.getOpeningDate() + "]: ");
        String openingDateInput = sc.nextLine();
        if (!openingDateInput.isEmpty()) selectedInternship.setOpeningDate(LocalDate.parse(openingDateInput));
        System.out.print("Enter new closing date [" + selectedInternship.getClosingDate() + "]: ");
        String closingDateInput = sc.nextLine();
        if (!closingDateInput.isEmpty()) selectedInternship.setClosingDate(LocalDate.parse(closingDateInput));
        System.out.println("Internship updated successfully!");
    }

    private static void deleteInternship(CompanyRepresentative rep, List<Internship> allInternships,
                                        List<InternshipApplication> allApplications) {
        List<Internship> myInternships = allInternships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .filter(i -> i.getStatus() != Internship.InternshipStatus.APPROVED) // Can't delete approved
            .collect(Collectors.toList());
        if (myInternships.isEmpty()) {
            System.out.println("No deletable internship postings found. (Approved internships cannot be deleted)");
            return;
        }

        System.out.println("\n--- Deletable Internships ---");
        for (Internship internship : myInternships) {
            System.out.println(internship.getInternshipID() + " - " + internship.getTitle() +
                " (Status: " + internship.getStatus() + ")");
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Internship ID to delete: ");
        String internshipID = sc.nextLine().trim().toUpperCase();
        Internship selectedInternship = myInternships.stream()
            .filter(i -> i.getInternshipID().equals(internshipID))
            .findFirst()
            .orElse(null);
        if (selectedInternship == null) {
            System.out.println("Internship not found or cannot be deleted.");
            return;
        }

        System.out.print("Are you sure you want to delete this internship? (yes/no): ");
        String confirm = sc.nextLine().toLowerCase();
        if (confirm.equals("yes")) {
            // Remove associated applications
            allApplications.removeIf(app -> app.getInternship().getInternshipID().equals(internshipID));
            allInternships.remove(selectedInternship);
            rep.decrementInternshipsCreated();
            System.out.println("Internship deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void toggleVisibility(CompanyRepresentative rep, List<Internship> allInternships) {
        List<Internship> myApprovedInternships = allInternships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .filter(i -> i.getStatus() == Internship.InternshipStatus.APPROVED)
            .collect(Collectors.toList());
        if (myApprovedInternships.isEmpty()) {
            System.out.println("No approved internships to toggle visibility.");
            return;
        }

        System.out.println("\n--- Approved Internships ---");
        for (Internship internship : myApprovedInternships) {
            System.out.println(internship.getInternshipID() + " - " + internship.getTitle() +
                " (Visible: " + (internship.isVisible() ? "Yes" : "No") + ")");
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Internship ID to toggle visibility: ");
        String internshipID = sc.nextLine().trim().toUpperCase();
        Internship selectedInternship = myApprovedInternships.stream()
            .filter(i -> i.getInternshipID().equals(internshipID))
            .findFirst()
            .orElse(null);
        if (selectedInternship == null) {
            System.out.println("Internship not found.");
            return;
        }

        selectedInternship.setVisible(!selectedInternship.isVisible());
        System.out.println("Visibility toggled! New status: " +
            (selectedInternship.isVisible() ? "Visible" : "Hidden"));
    }

    private static void processApplications(CompanyRepresentative rep, List<Internship> allInternships,
                                           List<InternshipApplication> allApplications) {
        List<Internship> myInternships = allInternships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());
        List<InternshipApplication> relevantApplications = allApplications.stream()
            .filter(app -> myInternships.stream()
                .anyMatch(i -> i.getInternshipID().equals(app.getInternship().getInternshipID())))
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.PENDING)
            .collect(Collectors.toList());
        if (relevantApplications.isEmpty()) {
            System.out.println("No pending applications for your internships.");
            return;
        }

        // Group applications by internship for better display
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         PENDING APPLICATIONS BY INTERNSHIP");
        System.out.println("=".repeat(60));
        for (Internship internship : myInternships) {
            // Get applications for this specific internship
            List<InternshipApplication> appsForThisInternship = relevantApplications.stream()
                .filter(app -> app.getInternship().getInternshipID().equals(internship.getInternshipID()))
                .collect(Collectors.toList());
            if (!appsForThisInternship.isEmpty()) {
                // Show internship summary first
                System.out.println("\n📋 " + internship.getTitle() + " [" + internship.getInternshipID() + "]");
                System.out.println("   Slots: " + internship.getSlots() +
                    " | Confirmed: " + internship.getConfirmedPlacements() +
                    " | Available: " + (internship.getSlots() - internship.getConfirmedPlacements()));
                System.out.println("   Status: " + internship.getStatus());
                System.out.println("   " + "-".repeat(55));
                // Show each pending application
                for (InternshipApplication app : appsForThisInternship) {
                    System.out.println("\n   Application ID: " + app.getApplicationId());
                    System.out.println("   Student: " + app.getStudent().getName() +
                        " (" + app.getStudent().getUserID() + ")");
                    System.out.println("   Major: " + app.getStudent().getMajor() +
                        " | Year: " + app.getStudent().getYearOfStudy());
                    System.out.println("   Status: " + app.getStatus());
                }
                System.out.println();
            }
        }
        System.out.println("=".repeat(60));
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Application ID to process: ");
        String appId = sc.nextLine().trim().toUpperCase();
        InternshipApplication selectedApp = relevantApplications.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);
        if (selectedApp == null) {
            System.out.println("Application not found.");
            return;
        }

        System.out.print("Approve or Reject? (approve/reject): ");
        String decision = sc.nextLine().trim().toLowerCase();
        if (decision.equals("approve")) {
            selectedApp.setStatus(InternshipApplication.ApplicationStatus.SUCCESSFUL);
            System.out.println("Application approved!");
        } else if (decision.equals("reject")) {
            selectedApp.setStatus(InternshipApplication.ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Application rejected.");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    // NEW METHOD - View All Applications History
    private static void viewAllApplications(CompanyRepresentative rep, List<Internship> allInternships,
                                           List<InternshipApplication> allApplications) {
        List<Internship> myInternships = allInternships.stream()
            .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
            .collect(Collectors.toList());
        
        List<InternshipApplication> relevantApplications = allApplications.stream()
            .filter(app -> myInternships.stream()
                .anyMatch(i -> i.getInternshipID().equals(app.getInternship().getInternshipID())))
            .collect(Collectors.toList());
        
        if (relevantApplications.isEmpty()) {
            System.out.println("No applications for your internships.");
            return;
        }
        
        // Filter options
        System.out.println("\n--- Filter Options ---");
        System.out.println("1. View All Applications");
        System.out.println("2. View PENDING only");
        System.out.println("3. View SUCCESSFUL (Approved) only");
        System.out.println("4. View UNSUCCESSFUL (Rejected) only");
        System.out.println("5. View WITHDRAWN only");
        
        Scanner sc = new Scanner(System.in);
        System.out.print("Choose option: ");
        String filterChoice = sc.nextLine();
        
        switch (filterChoice) {
            case "2":
                relevantApplications = relevantApplications.stream()
                    .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.PENDING)
                    .collect(Collectors.toList());
                break;
            case "3":
                relevantApplications = relevantApplications.stream()
                    .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.SUCCESSFUL)
                    .collect(Collectors.toList());
                break;
            case "4":
                relevantApplications = relevantApplications.stream()
                    .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.UNSUCCESSFUL)
                    .collect(Collectors.toList());
                break;
            case "5":
                relevantApplications = relevantApplications.stream()
                    .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.WITHDRAWN)
                    .collect(Collectors.toList());
                break;
        }
        
        if (relevantApplications.isEmpty()) {
            System.out.println("No applications match the selected filter.");
            return;
        }
        
        // Display summary
        System.out.println("\n" + "=".repeat(70));
        System.out.println("           APPLICATION HISTORY FOR YOUR INTERNSHIPS");
        System.out.println("=".repeat(70));
        
        long pending = allApplications.stream()
            .filter(app -> myInternships.stream().anyMatch(i -> i.getInternshipID().equals(app.getInternship().getInternshipID())))
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.PENDING).count();
        long successful = allApplications.stream()
            .filter(app -> myInternships.stream().anyMatch(i -> i.getInternshipID().equals(app.getInternship().getInternshipID())))
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.SUCCESSFUL).count();
        long unsuccessful = allApplications.stream()
            .filter(app -> myInternships.stream().anyMatch(i -> i.getInternshipID().equals(app.getInternship().getInternshipID())))
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.UNSUCCESSFUL).count();
        long withdrawn = allApplications.stream()
            .filter(app -> myInternships.stream().anyMatch(i -> i.getInternshipID().equals(app.getInternship().getInternshipID())))
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.WITHDRAWN).count();
        
        System.out.println("Summary: Pending(" + pending + ") | Approved(" + successful + 
                          ") | Rejected(" + unsuccessful + ") | Withdrawn(" + withdrawn + ")");
        System.out.println("Showing: " + relevantApplications.size() + " applications\n");
        
        // Display applications
        for (InternshipApplication app : relevantApplications) {
            System.out.println("-".repeat(70));
            System.out.println("Application ID: " + app.getApplicationId());
            System.out.println("Internship: " + app.getInternship().getTitle() + " (" + app.getInternship().getInternshipID() + ")");
            System.out.println("Student: " + app.getStudent().getName() + " (" + app.getStudent().getUserID() + ")");
            System.out.println("Major: " + app.getStudent().getMajor() + " | Year: " + app.getStudent().getYearOfStudy());
            System.out.println("Email: " + app.getStudent().getEmail());
            System.out.println("Status: " + app.getStatus());
            System.out.println("Placement Confirmed: " + (app.isPlacementConfirmed() ? "Yes" : "No"));
            System.out.println("Withdrawal Requested: " + (app.isWithdrawalRequested() ? "Yes" : "No"));
        }
        System.out.println("=".repeat(70));
    }

    private static void changePassword(CompanyRepresentative rep, Scanner sc) {
        // Step 1: Verify current password
        System.out.print("Enter current password: ");
        String currentPassword = sc.nextLine();
        if (!rep.checkPassword(currentPassword)) {
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
                String choice = sc.nextLine().trim().toLowerCase();
                if (!choice.equals("yes") && !choice.equals("y")) {
                    System.out.println("Password change cancelled.");
                    return; // Stay logged in, return to menu
                }
                // If yes, loop continues to retry
            }
        }

        // Step 3: Change password successfully
        rep.changePassword(newPassword);
        System.out.println("\n========================================");
        System.out.println("Password changed successfully!");
        System.out.println("For security, you will be logged out.");
        System.out.println("Please login again with your new password.");
        System.out.println("========================================");
    }

    private static void showNotificationBanner(CompanyRepresentative rep, List<Internship> allInternships,
                                          List<InternshipApplication> allApplications) {
    // Get rep's internships
    List<Internship> myInternships = allInternships.stream()
        .filter(i -> i.getCompanyRepInCharge().getUserID().equals(rep.getUserID()))
        .collect(Collectors.toList());
    
    // Check for pending applications
    long pendingApps = allApplications.stream()
        .filter(app -> myInternships.stream()
            .anyMatch(i -> i.getInternshipID().equals(app.getInternship().getInternshipID())))
        .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.PENDING)
        .count();
    
    // Check internship statuses
    long approvedInternships = myInternships.stream()
        .filter(i -> i.getStatus() == Internship.InternshipStatus.APPROVED)
        .count();
    long rejectedInternships = myInternships.stream()
        .filter(i -> i.getStatus() == Internship.InternshipStatus.REJECTED)
        .count();
    long pendingInternships = myInternships.stream()
        .filter(i -> i.getStatus() == Internship.InternshipStatus.PENDING)
        .count();
    
    if (pendingApps > 0 || approvedInternships > 0 || rejectedInternships > 0 || pendingInternships > 0) {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("  🔔 NOTIFICATIONS");
        System.out.println("─".repeat(70));
        
        if (pendingApps > 0) {
            System.out.println("  📥 " + pendingApps + " new application(s) pending review");
        }
        if (approvedInternships > 0) {
            System.out.println("  ✓ " + approvedInternships + " internship(s) APPROVED by staff");
        }
        if (rejectedInternships > 0) {
            System.out.println("  ✗ " + rejectedInternships + " internship(s) REJECTED by staff");
        }
        if (pendingInternships > 0) {
            System.out.println("  ⏳ " + pendingInternships + " internship(s) awaiting staff approval");
        }
        
        System.out.println("═".repeat(70));
    }
}


}

