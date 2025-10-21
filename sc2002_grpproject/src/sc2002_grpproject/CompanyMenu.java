package sc2002_grpproject;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CompanyMenu {
    public static void show(CompanyRepresentative rep, List<Internship> allInternships, List<InternshipApplication> allApplications, List<Student> allStudents) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Company Representative Dashboard ---");
            System.out.println("1. Create Internship Opportunity");
            System.out.println("2. View My Internship Postings");
            System.out.println("3. View and Process Applications");
            System.out.println("4. Logout");
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
                    processApplications(rep, allInternships, allApplications);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void createInternship(CompanyRepresentative rep, List<Internship> allInternships) {
        long existingPostings = allInternships.stream()
            .filter(i -> i.getCompanyRepInCharge().equals(rep)).count();
        if (existingPostings >= 5) {
            System.out.println("You cannot create more than 5 internship opportunities.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Internship Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Level (BASIC, INTERMEDIATE, ADVANCED): ");
        Internship.InternshipLevel level = Internship.InternshipLevel.valueOf(sc.nextLine().toUpperCase());
        System.out.print("Enter Preferred Major: ");
        String major = sc.nextLine();
        System.out.print("Enter Number of Slots (max 10): ");
        int slots = Integer.parseInt(sc.nextLine());

        Internship newInternship = new Internship(title, rep.getCompanyName(), rep, level, major, slots);
        allInternships.add(newInternship);
        System.out.println("Internship created and pending approval from Career Center.");
    }

    private static void viewMyPostings(CompanyRepresentative rep, List<Internship> allInternships) {
        System.out.println("\n--- Your Internship Postings ---");
        allInternships.stream()
            .filter(i -> i.getCompanyRepInCharge().equals(rep))
            .forEach(i -> System.out.printf("ID: %s, Title: %s, Status: %s, Visible: %s\n",
                i.getInternshipID(), i.getTitle(), i.getStatus(), i.isVisible()));
    }

    private static void processApplications(CompanyRepresentative rep, List<Internship> allInternships, List<InternshipApplication> allApplications) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Internship ID to view applications for: ");
        String internshipId = sc.nextLine();

        Internship internship = allInternships.stream()
            .filter(i -> i.getInternshipID().equalsIgnoreCase(internshipId) && i.getCompanyRepInCharge().equals(rep))
            .findFirst().orElse(null);

        if (internship == null) {
            System.out.println("Internship not found.");
            return;
        }

        List<InternshipApplication> relevantApps = allApplications.stream()
            .filter(a -> a.getInternship().equals(internship) && a.getStatus() == InternshipApplication.ApplicationStatus.PENDING)
            .collect(Collectors.toList());
        
        if (relevantApps.isEmpty()) {
            System.out.println("No pending applications for this internship.");
            return;
        }

        System.out.println("\n--- Pending Applications for " + internship.getTitle() + " ---");
        relevantApps.forEach(a -> System.out.printf("App ID: %s, Student: %s, Major: %s, Year: %d\n",
            a.getApplicationId(), a.getStudent().getName(), a.getStudent().getMajor(), a.getStudent().getYearOfStudy()));

        System.out.print("\nEnter Application ID to process: ");
        String appId = sc.nextLine();
        System.out.print("Approve or Reject? (A/R): ");
        String decision = sc.nextLine();

        InternshipApplication appToProcess = relevantApps.stream()
            .filter(a -> a.getApplicationId().equalsIgnoreCase(appId))
            .findFirst().orElse(null);

        if (appToProcess != null) {
            if (decision.equalsIgnoreCase("A")) {
                appToProcess.setStatus(InternshipApplication.ApplicationStatus.SUCCESSFUL);
                internship.decreaseSlots();
                System.out.println("Application approved.");
            } else {
                appToProcess.setStatus(InternshipApplication.ApplicationStatus.UNSUCCESSFUL);
                System.out.println("Application rejected.");
            }
        } else {
            System.out.println("Application ID not found.");
        }
    }
}
