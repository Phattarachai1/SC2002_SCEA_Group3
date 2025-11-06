package sc2002_grpproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    
    public static List<Student> loadStudents(String filePath) {
        List<Student> students = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                // StudentID, Name, Major, Year, Email
                students.add(new Student(data[0], data[1], data[2], Integer.parseInt(data[3]), data[4]));
            }
        } catch (IOException e) {
            System.err.println("Error loading students file: " + e.getMessage());
        }
        return students;
    }

    public static List<CareerCenterStaff> loadStaff(String filePath) {
        List<CareerCenterStaff> staff = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                // StaffID, Name, Role, Department, Email
                staff.add(new CareerCenterStaff(data[0], data[1], data[3]));
            }
        } catch (IOException e) {
            System.err.println("Error loading staff file: " + e.getMessage());
        }
        return staff;
    }

    public static List<CompanyRepresentative> loadCompanyReps(String filePath) {
        List<CompanyRepresentative> reps = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // Skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                // Email, Name, CompanyName, Department, Position
                reps.add(new CompanyRepresentative(data[0], data[1], data[2], data[3], data[4]));
            }
        } catch (IOException e) {
            System.err.println("Error loading company representatives file: " + e.getMessage());
        }
        return reps;
    }
}

    // In this project, company reps and internships are created at runtime.
    // The sample file for company reps is initially empty.