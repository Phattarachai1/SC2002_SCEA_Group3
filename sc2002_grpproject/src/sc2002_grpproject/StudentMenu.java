package sc2002_grpproject;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StudentMenu {
    public static void show(Student student, List<Internship> allInternships, List<InternshipApplication> allApplications) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Student Dashboard ---");
            System.out.println("1. View Available Internships");
            System.out.println("2. Apply for Internship");
            System.out.println("3. View My Applications");
            System.out.println("4. Logout");
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
                    return; // Return to main loop to logout
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void viewAvailableInternships(Student student, List<Internship> allInternships) {
        System.out.println("\n--- Available Internships ---");
        List<Internship> available = allInternships.stream()
            .filter(i -> i.getStatus() == Internship.InternshipStatus.APPROVED && i.isVisible())
            .filter(i -> i.getPreferredMajor().equalsIgnoreCase(student.getMajor()))
            .filter(i -> {
                if (student.getYearOfStudy() <= 2) {
                    return i.getLevel() == Internship.InternshipLevel.BASIC;
                }
                return true; // Year 3 & 4 can apply for any
            })
            .collect(Collectors.toList());

        if (available.isEmpty()) {
            System.out.println("No internships available for you at the moment.");
        } else {
            available.forEach(i -> System.out.printf("ID: %s, Title: %s, Company: %s, Level: %s\n",
                i.getInternshipID(), i.getTitle(), i.getCompanyName(), i.getLevel()));
        }
    }

    private static void applyForInternship(Student student, List<Internship> allInternships, List<InternshipApplication> allApplications) {
        long currentApplications = allApplications.stream()
            .filter(a -> a.getStudent().equals(student) && a.getStatus() == InternshipApplication.ApplicationStatus.PENDING)
            .count();

        if (currentApplications >= 3) {
            System.out.println("You have reached the maximum of 3 pending applications.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Internship ID to apply for: ");
        String internshipId = sc.nextLine();

        Internship toApply = allInternships.stream()
            .filter(i -> i.getInternshipID().equalsIgnoreCase(internshipId) && i.getStatus() == Internship.InternshipStatus.APPROVED && i.isVisible())
            .findFirst().orElse(null);
        
        if (toApply != null) {
            InternshipApplication newApp = new InternshipApplication(student, toApply);
            allApplications.add(newApp);
            System.out.println("Successfully applied for " + toApply.getTitle());
        } else {
            System.out.println("Internship not found or not available for application.");
        }
    }

    private static void viewMyApplications(Student student, List<InternshipApplication> allApplications) {
        System.out.println("\n--- Your Applications ---");
        List<InternshipApplication> myApps = allApplications.stream()
            .filter(a -> a.getStudent().equals(student))
            .collect(Collectors.toList());

        if (myApps.isEmpty()) {
            System.out.println("You have not applied for any internships.");
        } else {
            myApps.forEach(a -> System.out.printf("ID: %s, Internship: %s, Company: %s, Status: %s\n",
                a.getApplicationId(), a.getInternship().getTitle(), a.getInternship().getCompanyName(), a.getStatus()));
        }
    }
}