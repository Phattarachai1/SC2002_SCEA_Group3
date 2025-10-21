package sc2002_grpproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainApp {
    // Data lists - acting as our in-memory database
    private static List<Student> students;
    private static List<CareerCenterStaff> staff;
    private static List<CompanyRepresentative> companyReps = new ArrayList<>();
    private static List<Internship> internships = new ArrayList<>();
    private static List<InternshipApplication> applications = new ArrayList<>();
    private static List<User> allUsers;
    private static User currentUser = null;

    public static void main(String[] args) {
        // Load initial data
        students = DataManager.loadStudents("sample_student_list.csv");
        staff = DataManager.loadStaff("sample_staff_list.csv");
        
        // Combine all users into one list for authentication
        allUsers = Stream.concat(students.stream(), staff.stream()).collect(Collectors.toList());

        Scanner sc = new Scanner(System.in);
        while (true) {
            if (currentUser == null) {
                showLoginMenu(sc);
            } else {
                showDashboard();
            }
        }
    }

    private static void showLoginMenu(Scanner sc) {
        System.out.println("\n--- Internship Placement Management System ---");
        System.out.println("1. Login");
        System.out.println("2. Register as Company Representative");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        String choice = sc.nextLine();

        switch (choice) {
            case "1":
                handleLogin(sc);
                break;
            case "2":
                handleCompanyRegistration(sc);
                break;
            case "3":
                System.out.println("Exiting application.");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    private static void handleLogin(Scanner sc) {
        System.out.print("Enter User ID: ");
        String userId = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        // Include dynamically added company reps in login check
        List<User> combinedUserList = new ArrayList<>(allUsers);
        combinedUserList.addAll(companyReps);

        for (User user : combinedUserList) {
            if (user.getUserID().equals(userId) && user.checkPassword(password)) {
                if (user instanceof CompanyRepresentative) {
                    CompanyRepresentative rep = (CompanyRepresentative) user;
                    if (rep.getStatus() != CompanyRepresentative.ApprovalStatus.APPROVED) {
                        System.out.println("Your account is pending approval by Career Center Staff.");
                        return;
                    }
                }
                currentUser = user;
                System.out.println("Login successful. Welcome, " + currentUser.getName() + "!");
                return;
            }
        }
        System.out.println("Login failed. Invalid User ID or Password.");
    }
    
    private static void handleCompanyRegistration(Scanner sc) {
        System.out.print("Enter your Email (this will be your User ID): ");
        String email = sc.nextLine();
        System.out.print("Enter your Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Company Name: ");
        String company = sc.nextLine();
        System.out.print("Enter Department: ");
        String dept = sc.nextLine();
        System.out.print("Enter Position: ");
        String pos = sc.nextLine();

        CompanyRepresentative newRep = new CompanyRepresentative(email, name, company, dept, pos);
        companyReps.add(newRep);
        System.out.println("Registration request submitted. You can log in once approved by Career Center Staff.");
    }

    private static void showDashboard() {
        if (currentUser instanceof Student) {
            StudentMenu.show((Student) currentUser, internships, applications);
        } else if (currentUser instanceof CompanyRepresentative) {
            CompanyMenu.show((CompanyRepresentative) currentUser, internships, applications, students);
        } else if (currentUser instanceof CareerCenterStaff) {
            StaffMenu.show((CareerCenterStaff) currentUser, internships, companyReps, applications);
        }
        // After returning from a menu, log the user out
        System.out.println(currentUser.getName() + " has been logged out.");
        currentUser = null; 
    }
}