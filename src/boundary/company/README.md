# Company Dashboard Components

This package contains the refactored Company Representative dashboard components.

## Files

### 1. CompanyDashboard.java (238 lines)
**Main dashboard frame for company representatives.**

**Responsibilities:**
- Initialize the main window
- Create and manage top panel (welcome, logout, change password buttons)
- Display notifications (pending applications, internship approvals)
- Manage tabbed pane with Internship and Application panels
- Handle password change and logout functionality

**Key Methods:**
- `CompanyDashboard(...)` - Constructor that sets up the UI
- `createNotificationPanel()` - Creates notification alerts
- `changePassword()` - Handles password change dialog
- `logout()` - Handles logout and returns to login screen

---

### 2. InternshipTablePanel.java (308 lines)
**Displays and manages the internship table.**

**Responsibilities:**
- Display all internships created by this company rep
- Provide filtering by Status, Level, and Visibility
- Render table with color-coded rows based on status
- Handle button actions (Create, Edit, Delete, Toggle Visibility, Refresh)

**Key Methods:**
- `InternshipTablePanel(...)` - Constructor
- `refresh()` - Reloads all internships into the table
- `refreshWithFilters(...)` - Reloads with filter criteria applied
- `createFilterPanel()` - Creates the filter UI
- `createTable()` - Initializes the JTable with custom renderer
- `createButtonPanel()` - Creates action buttons

**Table Columns:**
- ID, Title, Level, Major, Slots, Available, Confirmed, Opening, Closing, Status, Visible

**Color Coding:**
- Green: APPROVED internships
- Yellow: PENDING internships
- Red: REJECTED internships

---

### 3. ApplicationTablePanel.java (305 lines)
**Displays and manages the application table.**

**Responsibilities:**
- Display all applications to this company's internships
- Provide filtering by Internship Name, Status, Year, Confirmed status
- Render table with color-coded rows based on application status
- Handle button actions (Approve, Reject, Refresh)
- Sort PENDING applications to the top

**Key Methods:**
- `ApplicationTablePanel(...)` - Constructor
- `refresh()` - Reloads all applications into the table
- `refreshWithFilters(...)` - Reloads with filter criteria applied
- `createFilterPanel()` - Creates the filter UI
- `createTable()` - Initializes the JTable
- `createButtonPanel()` - Creates action buttons
- `applyRowColors()` - Sets custom cell renderer for status colors

**Table Columns:**
- App ID, Internship, Student, Major, Year, Status, Confirmed

**Color Coding:**
- Green: SUCCESSFUL applications
- Yellow: PENDING applications
- Red: UNSUCCESSFUL applications

---

### 4. CompanyActionHandler.java (370 lines)
**Handles all business logic and actions for company representatives.**

**Responsibilities:**
- Validate and create new internships
- Edit existing PENDING internships
- Delete PENDING internships (with cascade delete of applications)
- Toggle visibility of APPROVED internships
- Process (approve/reject) student applications
- Show error and info dialogs

**Key Methods:**

#### `createInternship()`
- Shows dialog to create new internship
- Validates:
  - Company hasn't reached 5 active internship limit
  - All required fields are filled
  - Closing date is after opening date
- Creates Internship object and adds to list
- Increments company rep's internship counter

#### `editInternship(String internshipId)`
- Shows dialog to edit internship details
- Only allows editing of PENDING internships
- Can modify: Title, Description, Level, Preferred Major, Slots

#### `deleteInternship(String internshipId)`
- Only allows deletion of PENDING internships
- Cascade deletes all applications to this internship
- Decrements company rep's internship counter
- Shows confirmation dialog

#### `toggleVisibility(String internshipId)`
- Only works for APPROVED internships
- Toggles between Visible and Hidden
- Hidden internships don't show to students

#### `processApplication(String appId, boolean approve)`
- Processes PENDING applications
- If approving:
  - Checks available slots
  - Sets status to SUCCESSFUL if slots available
  - Shows error if no slots available
- If rejecting:
  - Sets status to UNSUCCESSFUL
- Shows confirmation dialog

#### `showError(String message)` & `showInfo(String message)`
- Display feedback dialogs to the user

---

## Data Flow

### Creating an Internship:
```
User clicks "Create Internship" in InternshipTablePanel
   ↓
InternshipTablePanel → CompanyActionHandler.createInternship()
   ↓
ActionHandler shows dialog, validates, creates internship
   ↓
InternshipTablePanel.refresh() updates the table
```

### Approving an Application:
```
User clicks "Approve Selected" in ApplicationTablePanel
   ↓
ApplicationTablePanel → CompanyActionHandler.processApplication(appId, true)
   ↓
ActionHandler validates, checks slots, updates status
   ↓
ApplicationTablePanel.refresh() updates the table
```

## Dependencies

### External Packages:
- `sc2002_grpproject.entity.*` - Entity classes (Internship, Application, etc.)
- `sc2002_grpproject.enums.Enums.*` - Enum types (Status, Level, etc.)
- `sc2002_grpproject.boundary.RoundedButton` - Custom button component
- `sc2002_grpproject.boundary.LoginFrame` - For logout navigation
- `javax.swing.*` - GUI components
- `java.awt.*` - Layout managers and colors

### Internal Dependencies:
- CompanyDashboard uses InternshipTablePanel and ApplicationTablePanel
- Both panels use CompanyActionHandler
- All components share the same data lists (internships, applications)

## Usage Example

```java
// In LoginFrame.java
CompanyDashboard dashboard = new CompanyDashboard(
    companyRep,           // The logged-in company representative
    internships,          // Shared list of all internships
    applications,         // Shared list of all applications
    students,             // List of all students
    allUsers,             // List of all users
    staff,                // List of career center staff
    companyReps           // List of all company reps
);
```

The dashboard will automatically:
1. Initialize the UI components
2. Create the action handler
3. Set up the panels
4. Load and display data
5. Handle all user interactions

## Testing

### Unit Testing ActionHandler:
```java
@Test
public void testCreateInternship() {
    // Setup
    CompanyRepresentative mockRep = createMockRep();
    List<Internship> internships = new ArrayList<>();
    List<InternshipApplication> applications = new ArrayList<>();
    JFrame mockFrame = new JFrame();
    
    CompanyActionHandler handler = new CompanyActionHandler(
        mockRep, internships, applications, mockFrame
    );
    
    // Test
    handler.createInternship();
    
    // Verify
    assertEquals(1, internships.size());
}
```

### Integration Testing:
```java
@Test
public void testFullWorkflow() {
    // Create dashboard
    CompanyDashboard dashboard = new CompanyDashboard(...);
    
    // Simulate user actions
    // 1. Create internship
    // 2. Student applies
    // 3. Company approves
    // 4. Verify status changes
}
```

## Backup

The original monolithic CompanyDashboard.java (1096 lines) is preserved as:
- `CompanyDashboard.java.old`

This backup is kept for reference and can be deleted once the refactored version is fully tested and accepted.

## Future Enhancements

Potential improvements:
1. Add export to CSV functionality
2. Add search functionality in tables
3. Add bulk approval/rejection of applications
4. Add email notification integration
5. Add analytics dashboard (charts showing application trends)
6. Add internship templates for quick creation

## Related Documentation

- See `../../../REFACTORING_COMPLETE.md` for full refactoring summary
- See `../../../REFACTORING_VISUAL_GUIDE.md` for visual diagrams
- See `../../../DESIGN_CONSIDERATIONS.md` for original design notes
