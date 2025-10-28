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
    private static List<CompanyRepresentative> companyReps;
    private static List<Internship> internships = new ArrayList<>();
    private static List<InternshipApplication> applications = new ArrayList<>();
    private static List<User> allUsers;
    private static User currentUser = null;

    public static void main(String[] args) {
        // Load initial data
        students = DataManager.loadStudents("sample_student_list.csv");
        staff = DataManager.loadStaff("sample_staff_list.csv");
        companyReps = DataManager.loadCompanyReps("sample_company_representative_list.csv");

        // Combine all users into one list for authentication
        allUsers = new ArrayList<>();
        allUsers.addAll(students);
        allUsers.addAll(staff);
        allUsers.addAll(companyReps);

        Scanner sc = new Scanner(System.in);
        
        System.out.println("========================================");
        System.out.println("  INTERNSHIP PLACEMENT MANAGEMENT SYSTEM");
        System.out.println("========================================");

        while (true) {
            if (currentUser == null) {
                showLoginMenu(sc);
            } else {
                showDashboard();
            }
        }
    }

    private static void showLoginMenu(Scanner sc) {
        System.out.println("\n--- Login Menu ---");
        System.out.println("1. Login");
        System.out.println("2. Register (Company Representative)");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
        String choice = sc.nextLine();

        switch (choice) {
            case "1":
                login(sc);
                break;
            case "2":
                registerCompanyRep(sc);
                break;
            case "3":
                System.out.println("Exiting system. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option.");
        }
    }

    private static void login(Scanner sc) {
        System.out.print("Enter User ID: ");
        String userId = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        User user = allUsers.stream()
            .filter(u -> u.getUserID().equals(userId))
            .findFirst()
            .orElse(null);

        if (user == null) {
            System.out.println("User not found. Please check your User ID.");
            return;
        }

        if (!user.checkPassword(password)) {
            System.out.println("Incorrect password. Please try again.");
            return;
        }

        // Additional check for Company Representatives - must be approved
        if (user instanceof CompanyRepresentative) {
            CompanyRepresentative rep = (CompanyRepresentative) user;
            if (rep.getStatus() == CompanyRepresentative.ApprovalStatus.PENDING) {
                System.out.println("Your account is pending approval by Career Center Staff.");
                return;
            } else if (rep.getStatus() == CompanyRepresentative.ApprovalStatus.REJECTED) {
                System.out.println("Your account has been rejected. Please contact Career Center.");
                return;
            }
        }

        currentUser = user;
        System.out.println("Login successful! Welcome, " + user.getName());
    }

    private static void registerCompanyRep(Scanner sc) {
        System.out.println("\n--- Company Representative Registration ---");
        System.out.print("Enter your company email: ");
        String email = sc.nextLine();

        // Check if email already exists
        boolean exists = allUsers.stream()
            .anyMatch(u -> u.getUserID().equals(email));

        if (exists) {
            System.out.println("This email is already registered.");
            return;
        }

        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        System.out.print("Enter company name: ");
        String companyName = sc.nextLine();
        System.out.print("Enter department: ");
        String department = sc.nextLine();
        System.out.print("Enter position: ");
        String position = sc.nextLine();

        CompanyRepresentative newRep = new CompanyRepresentative(email, name, companyName, 
                                                                 department, position);
        companyReps.add(newRep);
        allUsers.add(newRep);

        System.out.println("Registration successful! Your account is pending approval.");
        System.out.println("You will be able to login once approved by Career Center Staff.");
    }

    private static void showDashboard() {
        if (currentUser instanceof Student) {
            StudentMenu.show((Student) currentUser, internships, applications);
            currentUser = null; // Logout after menu
        } else if (currentUser instanceof CompanyRepresentative) {
            CompanyMenu.show((CompanyRepresentative) currentUser, internships, applications, students);
            currentUser = null; // Logout after menu
        } else if (currentUser instanceof CareerCenterStaff) {
            StaffMenu.show((CareerCenterStaff) currentUser, companyReps, internships, 
                          applications, students);
            currentUser = null; // Logout after menu
        }
    }
}
