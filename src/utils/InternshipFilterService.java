package sc2002_grpproject.utils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import sc2002_grpproject.entity.Internship;
import sc2002_grpproject.enums.InternshipLevel;
import sc2002_grpproject.enums.InternshipStatus;

/**
 * Implementation of IInternshipFilter interface.
 * Provides concrete filtering operations for internship lists.
 */
public class InternshipFilterService implements IInternshipFilter {
    
    /**
     * Filters internships by level.
     * Results are sorted alphabetically by title.
     * 
     * @param internships the list of internships to filter
     * @param level the internship level to filter by
     * @return a filtered and sorted list of internships
     */
    @Override
    public List<Internship> filterByLevel(List<Internship> internships, InternshipLevel level) {
        return internships.stream()
            .filter(i -> i.getLevel() == level)
            .sorted(Comparator.comparing(Internship::getTitle))
            .collect(Collectors.toList());
    }
    
    /**
     * Filters internships by status.
     * Results are sorted alphabetically by title.
     * 
     * @param internships the list of internships to filter
     * @param status the internship status to filter by
     * @return a filtered and sorted list of internships
     */
    @Override
    public List<Internship> filterByStatus(List<Internship> internships, InternshipStatus status) {
        return internships.stream()
            .filter(i -> i.getStatus() == status)
            .sorted(Comparator.comparing(Internship::getTitle))
            .collect(Collectors.toList());
    }
    
    /**
     * Filters internships by visibility.
     * Results are sorted alphabetically by title.
     * 
     * @param internships the list of internships to filter
     * @param visible the visibility flag to filter by
     * @return a filtered and sorted list of internships
     */
    @Override
    public List<Internship> filterByVisibility(List<Internship> internships, boolean visible) {
        return internships.stream()
            .filter(i -> i.isVisible() == visible)
            .sorted(Comparator.comparing(Internship::getTitle))
            .collect(Collectors.toList());
    }
    
    /**
     * Filters internships by company name (case-insensitive partial match).
     * Results are sorted alphabetically by title.
     * 
     * @param internships the list of internships to filter
     * @param companyName the company name to search for
     * @return a filtered and sorted list of internships
     */
    @Override
    public List<Internship> filterByCompany(List<Internship> internships, String companyName) {
        return internships.stream()
            .filter(i -> i.getCompanyName().toLowerCase().contains(companyName.toLowerCase()))
            .sorted(Comparator.comparing(Internship::getTitle))
            .collect(Collectors.toList());
    }
    
    /**
     * Filters internships by closing date (before a given date).
     * Results are sorted alphabetically by title.
     * 
     * @param internships the list of internships to filter
     * @param beforeDate the date to filter by
     * @return a filtered and sorted list of internships
     */
    @Override
    public List<Internship> filterByClosingDate(List<Internship> internships, LocalDate beforeDate) {
        return internships.stream()
            .filter(i -> i.getClosingDate().isBefore(beforeDate.plusDays(1)))
            .sorted(Comparator.comparing(Internship::getTitle))
            .collect(Collectors.toList());
    }
}
