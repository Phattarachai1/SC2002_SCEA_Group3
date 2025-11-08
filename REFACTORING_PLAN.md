# Boundary Layer Refactoring Plan - Option 3 (Hybrid Approach)

## Overview
This document outlines the complete refactoring of the boundary layer to reduce file sizes and improve maintainability.

## Current Structure Problems
- **StaffDashboard.java**: 974 lines (Very Long)
- **CompanyDashboard.java**: 824 lines (Very Long)
- **StudentDashboard.java**: 632 lines (Long)
- **StaffMenu.java**: 607 lines (Long)
- **CompanyMenu.java**: 584 lines (Long)

## New Directory Structure

```
src/boundary/
├── dashboard/                    [NEW - Main coordinator classes]
│   ├── StudentDashboard.java    (~200 lines)
│   ├── StaffDashboard.java      (~250 lines)
│   └── CompanyDashboard.java    (~230 lines)
│
├── panels/                       [NEW - UI component panels]
│   ├── student/
│   │   ├── InternshipBrowsePanel.java      (~200 lines) - Browse & filter internships
│   │   └── ApplicationManagementPanel.java (~180 lines) - Manage applications
│   │
│   ├── staff/
│   │   ├── RepApprovalPanel.java           (~150 lines) - Approve/reject company reps
│   │   ├── InternshipApprovalPanel.java    (~200 lines) - Approve/reject internships
│   │   ├── WithdrawalApprovalPanel.java    (~120 lines) - Handle withdrawal requests
│   │   └── ReportPanel.java                (~150 lines) - Generate reports
│   │
│   └── company/
│       ├── PostingManagementPanel.java     (~220 lines) - Create/manage postings
│       └── ApplicationReviewPanel.java     (~200 lines) - Review student applications
│
├── helpers/                      [NEW - Utility classes]
│   ├── TableStyleHelper.java    (~120 lines) - Table styling & renderers
│   └── UIHelper.java            (~90 lines)  - Button creation, dialogs
│
├── LoginFrame.java              (unchanged - 276 lines)
├── RoundedButton.java           (unchanged - 55 lines)
├── StudentMenu.java             (to be removed - functionality moved to dashboard)
├── StaffMenu.java               (to be removed - functionality moved to dashboard)
└── CompanyMenu.java             (to be removed - functionality moved to dashboard)
```

## Refactoring Steps

### Phase 1: Create Helper Classes ✓ COMPLETED
1. ✓ Create `TableStyleHelper.java` - Common table styling and renderers
2. ✓ Create `UIHelper.java` - Common UI utilities (buttons, dialogs)

### Phase 2: Extract Student Panels
3. Create `InternshipBrowsePanel.java`
   - Extract from `StudentDashboard.createInternshipPanel()`
   - Move filter logic and table management
   - Methods: constructor, refreshTable(), applyFilters(), getSelectedInternship()

4. Create `ApplicationManagementPanel.java`
   - Extract from `StudentDashboard.createApplicationPanel()`
   - Move application table and action handlers
   - Methods: constructor, refreshTable(), acceptPlacement(), requestWithdrawal()

5. Refactor `StudentDashboard.java`
   - Remove panel creation methods
   - Keep only: constructor, logout(), changePassword()
   - Instantiate and coordinate panels
   - Reduced to ~200 lines

### Phase 3: Extract Staff Panels
6. Create `RepApprovalPanel.java`
   - Extract from `StaffDashboard.createCompanyRepPanel()`
   - Methods: refreshTable(), authorizeRep(), rejectRep()

7. Create `InternshipApprovalPanel.java`
   - Extract from `StaffDashboard.createInternshipPanel()`
   - Methods: refreshTable(), approveInternship(), rejectInternship(), applyFilters()

8. Create `WithdrawalApprovalPanel.java`
   - Extract from `StaffDashboard.createWithdrawalPanel()`
   - Methods: refreshTable(), approveWithdrawal()

9. Create `ReportPanel.java`
   - Extract from `StaffDashboard.createReportPanel()`
   - Methods: showStudentsReport(), showInternshipsReport(), showApplicationsReport(), showCompaniesReport()

10. Refactor `StaffDashboard.java`
    - Remove panel creation methods
    - Keep only: constructor, logout(), changePassword()
    - Instantiate and coordinate panels
    - Reduced to ~250 lines

### Phase 4: Extract Company Panels
11. Create `PostingManagementPanel.java`
    - Extract from `CompanyDashboard.createPostingPanel()`
    - Methods: refreshTable(), createPosting(), editPosting(), toggleVisibility()

12. Create `ApplicationReviewPanel.java`
    - Extract from `CompanyDashboard.createApplicationPanel()`
    - Methods: refreshTable(), approveApplication(), rejectApplication()

13. Refactor `CompanyDashboard.java`
    - Remove panel creation methods
    - Keep only: constructor, logout(), changePassword()
    - Instantiate and coordinate panels
    - Reduced to ~230 lines

### Phase 5: Update Package Structure
14. Move dashboard files to `boundary/dashboard/` folder
15. Update all package declarations
16. Update all imports in:
    - MainAppGUI.java
    - LoginFrame.java
    - All dashboard files
    - All panel files

### Phase 6: Testing
17. Compile all files
18. Run MainAppGUI
19. Test all functionality

## Key Design Decisions

### Panel Communication
Panels communicate with dashboard via:
1. **Constructor injection**: Pass required data and callbacks
2. **Public methods**: Dashboard can call panel methods (e.g., `refresh()`)
3. **Callback interfaces**: Panels notify dashboard of actions

Example:
```java
public class InternshipBrowsePanel extends JPanel {
    private OnInternshipSelectedListener listener;
    
    public interface OnInternshipSelectedListener {
        void onApplyClicked(Internship internship);
    }
    
    public InternshipBrowsePanel(List<Internship> internships, 
                                  Student student,
                                  OnInternshipSelectedListener listener) {
        // Panel implementation
    }
}
```

### Shared State Management
- Dashboards hold the main data lists (internships, applications, etc.)
- Panels receive references and can modify them
- Dashboards coordinate refreshes across panels when data changes

### Benefits
1. **Maintainability**: Files are 150-250 lines instead of 600-900 lines
2. **Testability**: Each panel can be tested independently
3. **Reusability**: Panels could be reused in different contexts
4. **Clarity**: Each class has a single, clear responsibility
5. **Collaboration**: Multiple developers can work on different panels

### Impact on Class Diagram
- **Before**: 8 large classes
- **After**: 3 dashboard coordinators + 8 panel components + 2 helpers = 13 classes
- **Complexity**: More classes, but each is simpler and has clearer responsibilities
- **Relationships**: Composition (Dashboard contains Panels), Dependency (Panels use Helpers)

## Implementation Status

✅ Phase 1: Helper classes created
⏳ Phase 2-6: Pending

## Next Steps

Would you like me to:
1. **Continue with full implementation** (create all panel classes and refactor dashboards)
2. **Create one complete example** (e.g., fully refactor StudentDashboard as a template)
3. **Provide detailed code for each panel** (you implement manually)

Please confirm how you'd like to proceed.
