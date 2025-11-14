package sc2002_grpproject.utils;

import java.time.LocalDate;
import java.util.List;
import sc2002_grpproject.entity.Internship;
import sc2002_grpproject.enums.InternshipLevel;
import sc2002_grpproject.enums.InternshipStatus;

/**
 * Interface for filtering internship lists based on various criteria.
 * Provides abstraction for internship filtering operations.
 */
public interface IInternshipFilter {
    
    /**
     * Filters internships by level.
     * 
     * @param internships the list of internships to filter
     * @param level the internship level to filter by
     * @return a filtered and sorted list of internships
     */
    List<Internship> filterByLevel(List<Internship> internships, InternshipLevel level);
    
    /**
     * Filters internships by status.
     * 
     * @param internships the list of internships to filter
     * @param status the internship status to filter by
     * @return a filtered and sorted list of internships
     */
    List<Internship> filterByStatus(List<Internship> internships, InternshipStatus status);
    
    /**
     * Filters internships by visibility.
     * 
     * @param internships the list of internships to filter
     * @param visible the visibility flag to filter by
     * @return a filtered and sorted list of internships
     */
    List<Internship> filterByVisibility(List<Internship> internships, boolean visible);
    
    /**
     * Filters internships by company name (case-insensitive partial match).
     * 
     * @param internships the list of internships to filter
     * @param companyName the company name to search for
     * @return a filtered and sorted list of internships
     */
    List<Internship> filterByCompany(List<Internship> internships, String companyName);
    
    /**
     * Filters internships by closing date (before a given date).
     * 
     * @param internships the list of internships to filter
     * @param beforeDate the date to filter by
     * @return a filtered and sorted list of internships
     */
    List<Internship> filterByClosingDate(List<Internship> internships, LocalDate beforeDate);
}
