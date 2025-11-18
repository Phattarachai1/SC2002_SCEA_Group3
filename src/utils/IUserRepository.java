package sc2002_grpproject.utils;

import java.util.List;
import sc2002_grpproject.entity.*;

/**
 * Interface for user data repository operations.
 * Provides abstraction for loading user data from various sources.
 */
public interface IUserRepository {
    
    /**
     * Loads student data from a data source.
     * 
     * @param filePath the path to the data source
     * @return a list of Student objects
     */
    List<Student> loadStudents(String filePath);
    
    /**
     * Loads career center staff data from a data source.
     * 
     * @param filePath the path to the data source
     * @return a list of CareerCenterStaff objects
     */
    List<CareerCenterStaff> loadStaff(String filePath);
    
    /**
     * Loads company representative data from a data source.
     * 
     * @param filePath the path to the data source
     * @return a list of CompanyRepresentative objects
     */
    List<CompanyRepresentative> loadCompanyReps(String filePath);
}
