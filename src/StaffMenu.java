package sc2002_grpproject;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StaffMenu {

    public static void show(CareerCenterStaff staff, List<CompanyRepresentative> allCompanyReps,
                           List<Internship> allInternships, List<InternshipApplication> allApplications,
                           List<Student> allStudents) {
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            showNotificationBanner(allCompanyReps, allInternships, allApplications);
            System.out.println("\n--- Career Center Staff Dashboard ---");
            System.out.println("1. Authorize/Reject Company Representatives");
            System.out.println("2. Approve/Reject Internship Opportunities");
            System.out.println("3. Approve/Reject Withdrawal Requests");
            System.out.println("4. View Internship Opportunities (with Filters)");
            System.out.println("5. Generate Reports");
            System.out.println("6. View All Applications (All Statuses)");
            System.out.println("7. View All Company Representatives");
            System.out.println("8. Change Password");
            System.out.println("9. Logout");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    authorizeCompanyReps(allCompanyReps);
                    break;
                case "2":
                    approveInternships(allInternships);
                    break;
                case "3":
                    approveWithdrawals(allApplications);
                    break;
                case "4":
                    viewInternshipsWithFilters(allInternships);
                    break;
                case "5":
                    generateReports(allCompanyReps, allInternships, allApplications, allStudents);
                    break;
                case "6":
                    viewAllApplications(allApplications);
                    break;
                case "7":
                    viewAllCompanyRepresentatives(allCompanyReps);
                    break;
                case "8":
                    changePassword(staff, sc);
                    return;
                case "9":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void authorizeCompanyReps(List<CompanyRepresentative> allCompanyReps) {
        List<CompanyRepresentative> pendingReps = allCompanyReps.stream()
            .filter(rep -> rep.getStatus() == CompanyRepresentative.ApprovalStatus.PENDING)
            .collect(Collectors.toList());
        
        if (pendingReps.isEmpty()) {
            System.out.println("No pending company representative registrations.");
            return;
        }

        System.out.println("\n--- Pending Company Representatives ---");
        for (CompanyRepresentative rep : pendingReps) {
            System.out.println("\nUser ID: " + rep.getUserID());
            System.out.println("Name: " + rep.getName());
            System.out.println("Company: " + rep.getCompanyName());
            System.out.println("Department: " + rep.getDepartment());
            System.out.println("Position: " + rep.getPosition());
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter User ID to process: ");
        String userId = sc.nextLine().trim();
        
        CompanyRepresentative selectedRep = pendingReps.stream()
            .filter(rep -> rep.getUserID().equals(userId))
            .findFirst()
            .orElse(null);
        
        if (selectedRep == null) {
            System.out.println("Company representative not found.");
            return;
        }

        System.out.print("Approve or Reject? (approve/reject): ");
        String decision = sc.nextLine().trim().toLowerCase();
        
        if (decision.equals("approve")) {
            selectedRep.setStatus(CompanyRepresentative.ApprovalStatus.APPROVED);
            System.out.println("Company representative approved!");
        } else if (decision.equals("reject")) {
            selectedRep.setStatus(CompanyRepresentative.ApprovalStatus.REJECTED);
            System.out.println("Company representative rejected.");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void approveInternships(List<Internship> allInternships) {
        List<Internship> pendingInternships = allInternships.stream()
            .filter(i -> i.getStatus() == Internship.InternshipStatus.PENDING)
            .collect(Collectors.toList());
        
        if (pendingInternships.isEmpty()) {
            System.out.println("No pending internship opportunities.");
            return;
        }

        System.out.println("\n--- Pending Internship Opportunities ---");
        for (Internship internship : pendingInternships) {
            System.out.println("\nInternship ID: " + internship.getInternshipID());
            System.out.println("Title: " + internship.getTitle());
            System.out.println("Company: " + internship.getCompanyName());
            System.out.println("Description: " + internship.getDescription());
            System.out.println("Level: " + internship.getLevel());
            System.out.println("Preferred Major: " + internship.getPreferredMajor());
            System.out.println("Slots: " + internship.getSlots());
            System.out.println("Opening Date: " + internship.getOpeningDate());
            System.out.println("Closing Date: " + internship.getClosingDate());
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Internship ID to process: ");
        String internshipId = sc.nextLine().trim().toUpperCase();
        
        Internship selectedInternship = pendingInternships.stream()
            .filter(i -> i.getInternshipID().equals(internshipId))
            .findFirst()
            .orElse(null);
        
        if (selectedInternship == null) {
            System.out.println("Internship not found.");
            return;
        }

        System.out.print("Approve or Reject? (approve/reject): ");
        String decision = sc.nextLine().trim().toLowerCase();
        
        if (decision.equals("approve")) {
            selectedInternship.setStatus(Internship.InternshipStatus.APPROVED);
            System.out.println("Internship opportunity approved!");
        } else if (decision.equals("reject")) {
            selectedInternship.setStatus(Internship.InternshipStatus.REJECTED);
            System.out.println("Internship opportunity rejected.");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void approveWithdrawals(List<InternshipApplication> allApplications) {
        List<InternshipApplication> withdrawalRequests = allApplications.stream()
            .filter(app -> app.isWithdrawalRequested())
            .collect(Collectors.toList());
        
        if (withdrawalRequests.isEmpty()) {
            System.out.println("No pending withdrawal requests.");
            return;
        }

        System.out.println("\n--- Pending Withdrawal Requests ---");
        for (InternshipApplication app : withdrawalRequests) {
            System.out.println("\nApplication ID: " + app.getApplicationId());
            System.out.println("Student: " + app.getStudent().getName() + " (" + app.getStudent().getUserID() + ")");
            System.out.println("Internship: " + app.getInternship().getTitle());
            System.out.println("Company: " + app.getInternship().getCompanyName());
            System.out.println("Current Status: " + app.getStatus());
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Application ID to process: ");
        String appId = sc.nextLine().trim().toUpperCase();
        
        InternshipApplication selectedApp = withdrawalRequests.stream()
            .filter(app -> app.getApplicationId().equals(appId))
            .findFirst()
            .orElse(null);
        
        if (selectedApp == null) {
            System.out.println("Application not found.");
            return;
        }

        System.out.print("Approve withdrawal? (yes/no): ");
        String decision = sc.nextLine().trim().toLowerCase();
        
        if (decision.equals("yes")) {
            selectedApp.setStatus(InternshipApplication.ApplicationStatus.WITHDRAWN);
            selectedApp.setWithdrawalRequested(false);
            // If student had confirmed placement, decrement count
            if (selectedApp.isPlacementConfirmed()) {
                selectedApp.getInternship().decrementConfirmedPlacements();
                selectedApp.setPlacementConfirmed(false);
            }
            System.out.println("Withdrawal approved.");
        } else if (decision.equals("no")) {
            selectedApp.setWithdrawalRequested(false);
            System.out.println("Withdrawal request rejected.");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void viewInternshipsWithFilters(List<Internship> allInternships) {
        System.out.println("\n--- Filter Options ---");
        System.out.println("1. View All Internships");
        System.out.println("2. Filter by Status");
        System.out.println("3. Filter by Level");
        System.out.println("4. Filter by Company");
        
        Scanner sc = new Scanner(System.in);
        System.out.print("Choose filter option: ");
        String filterChoice = sc.nextLine();
        
        List<Internship> filteredInternships = allInternships;
        
        switch (filterChoice) {
            case "2": // Filter by Status
                System.out.print("Enter status (PENDING/APPROVED/REJECTED/FILLED): ");
                String statusInput = sc.nextLine().toUpperCase();
                try {
                    Internship.InternshipStatus status = Internship.InternshipStatus.valueOf(statusInput);
                    filteredInternships = allInternships.stream()
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
                    filteredInternships = allInternships.stream()
                        .filter(i -> i.getLevel() == level)
                        .collect(Collectors.toList());
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid level.");
                }
                break;
            case "4": // Filter by Company
                System.out.print("Enter company name: ");
                String company = sc.nextLine();
                filteredInternships = allInternships.stream()
                    .filter(i -> i.getCompanyName().toLowerCase().contains(company.toLowerCase()))
                    .collect(Collectors.toList());
                break;
        }
        
        if (filteredInternships.isEmpty()) {
            System.out.println("No internships found matching the criteria.");
        } else {
            System.out.println("\n--- Internship Opportunities ---");
            for (Internship internship : filteredInternships) {
                System.out.println("\n" + internship.getInternshipID() + " - " + internship.getTitle());
                System.out.println("Company: " + internship.getCompanyName());
                System.out.println("Description: " + internship.getDescription());
                System.out.println("Level: " + internship.getLevel());
                System.out.println("Preferred Major: " + internship.getPreferredMajor());
                System.out.println("Slots: " + internship.getSlots() + " | Confirmed: " + internship.getConfirmedPlacements());
                System.out.println("Opening Date: " + internship.getOpeningDate());
                System.out.println("Closing Date: " + internship.getClosingDate());
                System.out.println("Status: " + internship.getStatus());
                System.out.println("Visible: " + (internship.isVisible() ? "Yes" : "No"));
            }
            System.out.println("\nTotal: " + filteredInternships.size() + " internships");
        }
    }

    private static void generateReports(List<CompanyRepresentative> allCompanyReps,
                                       List<Internship> allInternships,
                                       List<InternshipApplication> allApplications,
                                       List<Student> allStudents) {
        System.out.println("\n--- Report Options ---");
        System.out.println("1. Company Representative Report");
        System.out.println("2. Internship Report");
        System.out.println("3. Application Report");
        System.out.println("4. Student Report");
        
        Scanner sc = new Scanner(System.in);
        System.out.print("Choose report: ");
        String choice = sc.nextLine();
        
        switch (choice) {
            case "1":
                generateCompanyReport(allCompanyReps);
                break;
            case "2":
                generateInternshipReport(allInternships);
                break;
            case "3":
                generateApplicationReport(allApplications);
                break;
            case "4":
                generateStudentReport(allStudents, allApplications);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private static void generateCompanyReport(List<CompanyRepresentative> allCompanyReps) {
        System.out.println("\n=== COMPANY REPRESENTATIVE REPORT ===");
        long pending = allCompanyReps.stream()
            .filter(rep -> rep.getStatus() == CompanyRepresentative.ApprovalStatus.PENDING)
            .count();
        long approved = allCompanyReps.stream()
            .filter(rep -> rep.getStatus() == CompanyRepresentative.ApprovalStatus.APPROVED)
            .count();
        long rejected = allCompanyReps.stream()
            .filter(rep -> rep.getStatus() == CompanyRepresentative.ApprovalStatus.REJECTED)
            .count();
        
        System.out.println("Total Representatives: " + allCompanyReps.size());
        System.out.println("Pending: " + pending);
        System.out.println("Approved: " + approved);
        System.out.println("Rejected: " + rejected);
    }

    private static void generateInternshipReport(List<Internship> allInternships) {
        System.out.println("\n=== INTERNSHIP REPORT ===");
        long pending = allInternships.stream()
            .filter(i -> i.getStatus() == Internship.InternshipStatus.PENDING)
            .count();
        long approved = allInternships.stream()
            .filter(i -> i.getStatus() == Internship.InternshipStatus.APPROVED)
            .count();
        long rejected = allInternships.stream()
            .filter(i -> i.getStatus() == Internship.InternshipStatus.REJECTED)
            .count();
        long filled = allInternships.stream()
            .filter(i -> i.getStatus() == Internship.InternshipStatus.FILLED)
            .count();
        
        System.out.println("Total Internships: " + allInternships.size());
        System.out.println("Pending: " + pending);
        System.out.println("Approved: " + approved);
        System.out.println("Rejected: " + rejected);
        System.out.println("Filled: " + filled);
    }

    private static void generateApplicationReport(List<InternshipApplication> allApplications) {
        System.out.println("\n=== APPLICATION REPORT ===");
        long pending = allApplications.stream()
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.PENDING)
            .count();
        long successful = allApplications.stream()
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.SUCCESSFUL)
            .count();
        long unsuccessful = allApplications.stream()
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.UNSUCCESSFUL)
            .count();
        long withdrawn = allApplications.stream()
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.WITHDRAWN)
            .count();
        long confirmed = allApplications.stream()
            .filter(app -> app.isPlacementConfirmed())
            .count();
        
        System.out.println("Total Applications: " + allApplications.size());
        System.out.println("Pending: " + pending);
        System.out.println("Successful: " + successful);
        System.out.println("Unsuccessful: " + unsuccessful);
        System.out.println("Withdrawn: " + withdrawn);
        System.out.println("Placements Confirmed: " + confirmed);
    }

    private static void generateStudentReport(List<Student> allStudents, List<InternshipApplication> allApplications) {
        System.out.println("\n=== STUDENT REPORT ===");
        System.out.println("Total Students: " + allStudents.size());
        
        long studentsWithApplications = allApplications.stream()
            .map(app -> app.getStudent().getUserID())
            .distinct()
            .count();
        
        long studentsWithPlacements = allApplications.stream()
            .filter(app -> app.isPlacementConfirmed())
            .map(app -> app.getStudent().getUserID())
            .distinct()
            .count();
        
        System.out.println("Students with Applications: " + studentsWithApplications);
        System.out.println("Students with Confirmed Placements: " + studentsWithPlacements);
    }

    // NEW METHOD - View All Applications
    private static void viewAllApplications(List<InternshipApplication> allApplications) {
        if (allApplications.isEmpty()) {
            System.out.println("No applications in the system.");
            return;
        }
        
        // Filter options
        System.out.println("\n--- Filter Options ---");
        System.out.println("1. View All Applications");
        System.out.println("2. View PENDING only");
        System.out.println("3. View SUCCESSFUL only");
        System.out.println("4. View UNSUCCESSFUL (Rejected) only");
        System.out.println("5. View WITHDRAWN only");
        System.out.println("6. View Confirmed Placements only");
        
        Scanner sc = new Scanner(System.in);
        System.out.print("Choose option: ");
        String filterChoice = sc.nextLine();
        
        List<InternshipApplication> filtered = allApplications;
        
        switch (filterChoice) {
            case "2":
                filtered = allApplications.stream()
                    .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.PENDING)
                    .collect(Collectors.toList());
                break;
            case "3":
                filtered = allApplications.stream()
                    .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.SUCCESSFUL)
                    .collect(Collectors.toList());
                break;
            case "4":
                filtered = allApplications.stream()
                    .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.UNSUCCESSFUL)
                    .collect(Collectors.toList());
                break;
            case "5":
                filtered = allApplications.stream()
                    .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.WITHDRAWN)
                    .collect(Collectors.toList());
                break;
            case "6":
                filtered = allApplications.stream()
                    .filter(app -> app.isPlacementConfirmed())
                    .collect(Collectors.toList());
                break;
        }
        
        if (filtered.isEmpty()) {
            System.out.println("No applications match the selected filter.");
            return;
        }
        
        // Display summary
        System.out.println("\n" + "=".repeat(70));
        System.out.println("               ALL APPLICATIONS IN SYSTEM");
        System.out.println("=".repeat(70));
        
        long pending = allApplications.stream()
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.PENDING).count();
        long successful = allApplications.stream()
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.SUCCESSFUL).count();
        long unsuccessful = allApplications.stream()
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.UNSUCCESSFUL).count();
        long withdrawn = allApplications.stream()
            .filter(app -> app.getStatus() == InternshipApplication.ApplicationStatus.WITHDRAWN).count();
        
        System.out.println("Summary: Pending(" + pending + ") | Approved(" + successful + 
                          ") | Rejected(" + unsuccessful + ") | Withdrawn(" + withdrawn + ")");
        System.out.println("Showing: " + filtered.size() + " applications\n");
        
        // Display applications
        for (InternshipApplication app : filtered) {
            System.out.println("-".repeat(70));
            System.out.println("Application ID: " + app.getApplicationId());
            System.out.println("Student: " + app.getStudent().getName() + " (" + app.getStudent().getUserID() + ")");
            System.out.println("Major: " + app.getStudent().getMajor() + " | Year: " + app.getStudent().getYearOfStudy());
            System.out.println("Internship: " + app.getInternship().getTitle() + " (" + app.getInternship().getInternshipID() + ")");
            System.out.println("Company: " + app.getInternship().getCompanyName());
            System.out.println("Status: " + app.getStatus());
            System.out.println("Placement Confirmed: " + (app.isPlacementConfirmed() ? "Yes" : "No"));
            System.out.println("Withdrawal Requested: " + (app.isWithdrawalRequested() ? "Yes" : "No"));
        }
        System.out.println("=".repeat(70));
    }

    // NEW METHOD - View All Company Representatives
    private static void viewAllCompanyRepresentatives(List<CompanyRepresentative> allCompanyReps) {
        if (allCompanyReps.isEmpty()) {
            System.out.println("No company representatives in the system.");
            return;
        }
        
        // Filter options
        System.out.println("\n--- Filter Options ---");
        System.out.println("1. View All Representatives");
        System.out.println("2. View PENDING only");
        System.out.println("3. View APPROVED only");
        System.out.println("4. View REJECTED only");
        
        Scanner sc = new Scanner(System.in);
        System.out.print("Choose option: ");
        String filterChoice = sc.nextLine();
        
        List<CompanyRepresentative> filtered = allCompanyReps;
        
        switch (filterChoice) {
            case "2":
                filtered = allCompanyReps.stream()
                    .filter(rep -> rep.getStatus() == CompanyRepresentative.ApprovalStatus.PENDING)
                    .collect(Collectors.toList());
                break;
            case "3":
                filtered = allCompanyReps.stream()
                    .filter(rep -> rep.getStatus() == CompanyRepresentative.ApprovalStatus.APPROVED)
                    .collect(Collectors.toList());
                break;
            case "4":
                filtered = allCompanyReps.stream()
                    .filter(rep -> rep.getStatus() == CompanyRepresentative.ApprovalStatus.REJECTED)
                    .collect(Collectors.toList());
                break;
        }
        
        if (filtered.isEmpty()) {
            System.out.println("No company representatives match the selected filter.");
            return;
        }
        
        // Display summary
        System.out.println("\n" + "=".repeat(70));
        System.out.println("            ALL COMPANY REPRESENTATIVES IN SYSTEM");
        System.out.println("=".repeat(70));
        
        long pending = allCompanyReps.stream()
            .filter(rep -> rep.getStatus() == CompanyRepresentative.ApprovalStatus.PENDING).count();
        long approved = allCompanyReps.stream()
            .filter(rep -> rep.getStatus() == CompanyRepresentative.ApprovalStatus.APPROVED).count();
        long rejected = allCompanyReps.stream()
            .filter(rep -> rep.getStatus() == CompanyRepresentative.ApprovalStatus.REJECTED).count();
        
        System.out.println("Summary: Pending(" + pending + ") | Approved(" + approved + 
                          ") | Rejected(" + rejected + ")");
        System.out.println("Showing: " + filtered.size() + " representatives\n");
        
        // Display representatives
        for (CompanyRepresentative rep : filtered) {
            System.out.println("-".repeat(70));
            System.out.println("User ID: " + rep.getUserID());
            System.out.println("Name: " + rep.getName());
            System.out.println("Company: " + rep.getCompanyName());
            System.out.println("Department: " + rep.getDepartment());
            System.out.println("Position: " + rep.getPosition());
            System.out.println("Status: " + rep.getStatus());
            System.out.println("Internships Created: " + rep.getInternshipsCreated() + "/5");
        }
        System.out.println("=".repeat(70));
    }

    private static void changePassword(CareerCenterStaff staff, Scanner sc) {
        // Step 1: Verify current password
        System.out.print("Enter current password: ");
        String currentPassword = sc.nextLine();
        if (!staff.checkPassword(currentPassword)) {
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
        staff.changePassword(newPassword);
        System.out.println("\n========================================");
        System.out.println("Password changed successfully!");
        System.out.println("For security, you will be logged out.");
        System.out.println("Please login again with your new password.");
        System.out.println("========================================");
    }

    private static void showNotificationBanner(List<CompanyRepresentative> allCompanyReps,
                                          List<Internship> allInternships,
                                          List<InternshipApplication> allApplications) {
    // Count pending items
    long pendingReps = allCompanyReps.stream()
        .filter(rep -> rep.getStatus() == CompanyRepresentative.ApprovalStatus.PENDING)
        .count();
    
    long pendingInternships = allInternships.stream()
        .filter(i -> i.getStatus() == Internship.InternshipStatus.PENDING)
        .count();
    
    long pendingWithdrawals = allApplications.stream()
        .filter(app -> app.isWithdrawalRequested())
        .count();
    
    if (pendingReps > 0 || pendingInternships > 0 || pendingWithdrawals > 0) {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("  🔔 PENDING ACTIONS REQUIRED");
        System.out.println("─".repeat(70));
        
        if (pendingReps > 0) {
            System.out.println("  👥 " + pendingReps + " company representative(s) awaiting authorization");
        }
        if (pendingInternships > 0) {
            System.out.println("  📋 " + pendingInternships + " internship(s) awaiting approval");
        }
        if (pendingWithdrawals > 0) {
            System.out.println("  🔄 " + pendingWithdrawals + " withdrawal request(s) pending");
        }
        
        System.out.println("═".repeat(70));
    }
}

}
