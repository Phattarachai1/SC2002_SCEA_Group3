package sc2002_grpproject.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sc2002_grpproject.entity.*;

/**
 * Utility class for loading data from CSV files.
 * Provides methods to load students, staff, and company representatives.
 * Implements IUserRepository interface for data access abstraction.
 */
public class DataManager implements IUserRepository {
    
    /**
     * Loads student data from a CSV file.
     * Expected format: StudentID, Name, Major, Year, Email
     * 
     * @param filePath the path to the CSV file
     * @return a list of Student objects
     */
    @Override
    public List<Student> loadStudents(String filePath) {
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

    /**
     * Loads career center staff data from a CSV file.
     * Expected format: StaffID, Name, Role, Department, Email
     * 
     * @param filePath the path to the CSV file
     * @return a list of CareerCenterStaff objects
     */
    @Override
    public List<CareerCenterStaff> loadStaff(String filePath) {
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

    /**
     * Loads company representative data from a CSV file.
     * Expected format: Email, Name, CompanyName, Department, Position
     * 
     * @param filePath the path to the CSV file
     * @return a list of CompanyRepresentative objects
     */
    @Override
    public List<CompanyRepresentative> loadCompanyReps(String filePath) {
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
