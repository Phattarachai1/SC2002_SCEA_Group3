package sc2002_grpproject;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StaffMenu {
    public static void show(CareerCenterStaff staff, List<Internship> allInternships, List<CompanyRepresentative> allReps, List<InternshipApplication> allApplications) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Career Center Staff Dashboard ---");
            System.out.println("1. Approve Company Representative Registrations");
            System.out.println("2. Approve Internship Opportunities");
            System.out.println("3. Toggle Internship Visibility");
            System.out.println("4. Generate Reports");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1":
                    approveRegistrations(allReps);
                    break;
                case "2":
                    approveInternships(allInternships);
                    break;
                case "3":
                    toggleVisibility(allInternships);
                    break;
                case "4":
                    generateReports(allInternships);
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void approveRegistrations(List<CompanyRepresentative> allReps) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Pending Company Registrations ---");
        List<CompanyRepresentative> pending = allReps.stream()
            .filter(r -> r.getStatus() == CompanyRepresentative.ApprovalStatus.PENDING)
            .collect(Collectors.toList());

        if (pending.isEmpty()) {
            System.out.println("No pending registrations.");
            return;
        }

        pending.forEach(r -> System.out.printf("ID: %s, Name: %s, Company: %s\n", r.getUserID(), r.getName(), r.getCompanyName()));
        System.out.print("\nEnter User ID (Email) to approve: ");
        String repId = sc.nextLine();

        CompanyRepresentative repToApprove = pending.stream().filter(r -> r.getUserID().equals(repId)).findFirst().orElse(null);
        if (repToApprove != null) {
            repToApprove.setStatus(CompanyRepresentative.ApprovalStatus.APPROVED);
            System.out.println("Representative " + repToApprove.getName() + " approved.");
        } else {
            System.out.println("ID not found.");
        }
    }

    private static void approveInternships(List<Internship> allInternships) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Pending Internship Opportunities ---");
        List<Internship> pending = allInternships.stream()
            .filter(i -> i.getStatus() == Internship.InternshipStatus.PENDING)
            .collect(Collectors.toList());

        if (pending.isEmpty()) {
            System.out.println("No pending internships.");
            return;
        }
        
        pending.forEach(i -> System.out.printf("ID: %s, Title: %s, Company: %s\n", i.getInternshipID(), i.getTitle(), i.getCompanyName()));
        System.out.print("\nEnter Internship ID to approve: ");
        String intId = sc.nextLine();

        Internship intToApprove = pending.stream().filter(i -> i.getInternshipID().equals(intId)).findFirst().orElse(null);
        if (intToApprove != null) {
            intToApprove.setStatus(Internship.InternshipStatus.APPROVED);
            System.out.println("Internship " + intToApprove.getTitle() + " approved.");
        } else {
            System.out.println("ID not found.");
        }
    }
    
    private static void toggleVisibility(List<Internship> allInternships) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Toggle Internship Visibility ---");
        allInternships.stream()
            .filter(i -> i.getStatus() == Internship.InternshipStatus.APPROVED)
            .forEach(i -> System.out.printf("ID: %s, Title: %s, Visible: %s\n", i.getInternshipID(), i.getTitle(), i.isVisible()));

        System.out.print("\nEnter Internship ID to toggle visibility: ");
        String intId = sc.nextLine();

        Internship intToToggle = allInternships.stream().filter(i -> i.getInternshipID().equals(intId)).findFirst().orElse(null);
        if (intToToggle != null) {
            intToToggle.setVisible(!intToToggle.isVisible());
            System.out.println("Visibility for " + intToToggle.getTitle() + " set to " + intToToggle.isVisible());
        } else {
            System.out.println("ID not found.");
        }
    }

    private static void generateReports(List<Internship> allInternships) {
        System.out.println("\n--- Full Internship Report ---");
        System.out.println("Total internships created: " + allInternships.size());
        
        long pending = allInternships.stream().filter(i -> i.getStatus() == Internship.InternshipStatus.PENDING).count();
        long approved = allInternships.stream().filter(i -> i.getStatus() == Internship.InternshipStatus.APPROVED).count();
        long filled = allInternships.stream().filter(i -> i.getStatus() == Internship.InternshipStatus.FILLED).count();
        
        System.out.println("Status Breakdown:");
        System.out.println(" - PENDING: " + pending);
        System.out.println(" - APPROVED: " + approved);
        System.out.println(" - FILLED: " + filled);
        System.out.println("---------------------------");

        allInternships.forEach(i -> System.out.printf("ID: %s, Title: %s, Company: %s, Status: %s, Slots Left: %d\n",
            i.getInternshipID(), i.getTitle(), i.getCompanyName(), i.getStatus(), i.getSlots()));
    }
}
