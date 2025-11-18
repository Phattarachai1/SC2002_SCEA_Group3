# SC2002 Internship Placement Management System - GUI Version

## 🚀 How to Run the GUI Application

### Prerequisites
- Java JDK 11 or higher
- PowerShell or Command Prompt (Windows) / Terminal (Mac/Linux)

### Running the Application

**IMPORTANT:** This repository contains both console and GUI versions. To run the **GUI version**, follow these steps:

#### Option 1: Using Command Line (PowerShell/CMD)
```powershell
# 1. Navigate to the project directory
cd d:\ntu\sc2002\SC2002_SCEA_Group3-structured_4

# 2. Compile all Java files recursively
javac -d bin -sourcepath src (Get-ChildItem -Path src -Recurse -Filter *.java -Exclude module-info.java | Select-Object -ExpandProperty FullName)

# 3. Run the GUI application (NOT MainApp!)
java -cp bin sc2002_grpproject.main.MainAppGUI
```

#### Option 2: Using an IDE (Eclipse/IntelliJ/VS Code)
1. Import the project into your IDE
2. Make sure the `src` folder is marked as source folder
3. **Right-click on `MainAppGUI.java`** in `src/main/` (NOT `MainApp.java`)
4. Select "Run As" → "Java Application" or "Run Java"

### ⚠️ Common Mistake
If you see a **console-based login interface** like this:
```
INTERNSHIP PLACEMENT SYSTEM
User ID (Email):
Password:
```
You are running the **wrong main class**! You ran `MainApp.java` instead of `MainAppGUI.java`.

### ✅ Correct GUI Login Screen
The correct GUI login screen should show:
- A window titled "Internship Management System - Login"
- Green header with "INTERNSHIP PLACEMENT SYSTEM"
- Text input fields for User ID and Password
- Three buttons: Login, Register (Company Rep), Exit

## 📁 Project Structure
```
SC2002_SCEA_Group3-structured_4/
├── src/
│   ├── main/
│   │   ├── MainAppGUI.java          ← RUN THIS FOR GUI VERSION
│   │   └── MainApp.java              ← Console version (alternative)
│   ├── boundary/
│   │   ├── LoginFrame.java
│   │   ├── RoundedButton.java
│   │   ├── CompanyMenu.java          ← Console menu
│   │   ├── StaffMenu.java            ← Console menu
│   │   ├── StudentMenu.java          ← Console menu
│   │   ├── company/
│   │   │   ├── CompanyDashboard.java
│   │   │   ├── InternshipTablePanel.java
│   │   │   ├── ApplicationTablePanel.java
│   │   │   └── CompanyActionHandler.java
│   │   ├── student/
│   │   │   ├── StudentDashboard.java
│   │   │   ├── InternshipBrowsePanel.java
│   │   │   ├── MyApplicationsPanel.java
│   │   │   └── StudentActionHandler.java
│   │   └── staff/
│   │       ├── StaffDashboard.java
│   │       ├── CompanyRepManagementPanel.java
│   │       ├── InternshipManagementPanel.java
│   │       └── StaffActionHandler.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── StudentController.java
│   │   ├── CompanyController.java
│   │   ├── StaffController.java
│   │   ├── result/
│   │   │   ├── AuthResult.java
│   │   │   ├── PostingResult.java
│   │   │   ├── StudentApplicationResult.java
│   │   │   ├── CompanyApplicationResult.java
│   │   │   ├── ApprovalResult.java
│   │   │   └── PasswordChangeResult.java
│   │   └── stats/
│   │       ├── InternshipStats.java
│   │       └── RepresentativeStats.java
│   ├── entity/
│   │   ├── User.java                 ← Abstract base class
│   │   ├── Student.java
│   │   ├── CompanyRepresentative.java
│   │   ├── CareerCenterStaff.java
│   │   ├── Internship.java
│   │   └── InternshipApplication.java
│   ├── enums/
│   │   ├── ApplicationStatus.java
│   │   ├── ApprovalStatus.java
│   │   ├── InternshipLevel.java
│   │   └── InternshipStatus.java
│   ├── utils/
│   │   ├── DataManager.java
│   │   ├── IUserRepository.java
│   │   ├── InternshipFilterService.java
│   │   └── IInternshipFilter.java
│   ├── exception/
│   └── module-info.java              ← Optional, can be excluded
├── bin/                               ← Compiled classes (generated)
├── sample_student_list.csv
├── sample_staff_list.csv
├── sample_company_representative_list.csv
└── docs/                              ← JavaDoc documentation
```

## 🎨 GUI Features
- **Color-coded status indicators:**
  - Light grey: Selected row
  - Light orange/yellow: PENDING status
  - Light green: APPROVED/SUCCESSFUL status
  - Light red: REJECTED/UNSUCCESSFUL status
- **Click selection:** Simply click on a row to select it
- **Notification panels:** Shows pending actions at the top of each dashboard
- **User-friendly inputs:** Dropdown menus for majors and levels, date pickers for internship dates
- **Three role-based dashboards:**
  - **Student Dashboard:** Browse available internships, apply, manage applications, accept/withdraw
  - **Company Representative Dashboard:** Create/edit internships, manage applications, approve/reject applicants
  - **Career Center Staff Dashboard:** Approve company representatives, approve internships, manage withdrawals, view statistics

## 🏗️ Architecture & Design
- **MVC Pattern:** Clean separation of Model (entity), View (boundary), and Controller
- **Strategy Pattern:** `IInternshipFilter` for flexible filtering
- **Repository Pattern:** `IUserRepository` for data access abstraction
- **Result Objects:** Type-safe return values with success/failure states
- **Package Organization:** Modular structure with `sc2002_grpproject` as base package

## 🔐 Test Accounts

### Students (15 total)
- `tan001@e.ntu.edu.sg` - Tan Wei Ling (Computer Science, Year 2)
- `ng002@e.ntu.edu.sg` - Ng Jia Hao (Data Science & AI, Year 3)
- `lim003@e.ntu.edu.sg` - Lim Yi Xuan (Computer Engineering, Year 4)
- `chong004@e.ntu.edu.sg` - Chong Zhi Hao (Information Engineering & Media, Year 1)
- `wong005@e.ntu.edu.sg` - Wong Shu Hui (Computer Science, Year 3)
- `koh006@e.ntu.edu.sg` - Koh Ming Wei (Computer Science, Year 2)
- `lee007@e.ntu.edu.sg` - Lee Hui Ting (Data Science & AI, Year 1)
- `tan008@e.ntu.edu.sg` - Tan Jun Hao (Computer Engineering, Year 3)
- `chen009@e.ntu.edu.sg` - Chen Xiao Yu (Computer Science, Year 4)
- `ong010@e.ntu.edu.sg` - Ong Wei Jie (Information Engineering & Media, Year 2)
- `lim011@e.ntu.edu.sg` - Lim Kai Xiang (Data Science & AI, Year 3)
- `goh012@e.ntu.edu.sg` - Goh Mei Ling (Computer Engineering, Year 1)
- `teo013@e.ntu.edu.sg` - Teo Yi Wen (Computer Science, Year 4)
- `sim014@e.ntu.edu.sg` - Sim Jie Min (Data Science & AI, Year 2)
- `ang015@e.ntu.edu.sg` - Ang Wei Hao (Computer Engineering, Year 3)

**All student passwords:** `password`

### Career Center Staff
- Email: `staff001@ntu.edu.sg` | Password: `password`

### Company Representatives
- Email: `rep001@company.com` | Password: `password`

## 📝 Notes
- Default password for all CSV users is: `password`
- Company representatives can register new accounts through the GUI
- CSV files must be in the **root directory** of the project (same level as `src/`)
- The system uses in-memory data storage (changes are not persisted to CSV files)
- Company representatives have a limit of **5 active internships**
- Year 1-2 students can only apply to **BASIC** level internships
- Students must match the **preferred major** to be eligible for an internship

## 🔑 Business Rules

### For Students:
- Can browse and apply to approved, visible internships
- Must meet eligibility criteria (year level, major match)
- Can only accept **one** placement
- Can request withdrawal from applications
- Cannot apply to past-deadline or filled internships

### For Company Representatives:
- Must be **approved by staff** before posting internships
- Can create up to **5 active internships** maximum
- Can only edit **PENDING** internships
- Can approve/reject applications for their internships
- Cannot delete internships with existing applications

### For Career Center Staff:
- Approve or reject company representative registrations
- Approve or reject internship postings
- Manage withdrawal requests from students
- View system-wide statistics

## 🐛 Troubleshooting

### Problem: Console interface appears instead of GUI
**Solution:** You're running `MainApp.java`. Run `MainAppGUI.java` from the `src/main/` folder instead.

### Problem: CSV files not found
**Solution:** Make sure the CSV files (`sample_student_list.csv`, `sample_staff_list.csv`, `sample_company_representative_list.csv`) are in the root directory of the project, not inside `src/` or `bin/`.

### Problem: Module errors during compilation
**Solution:** Use the `-Exclude module-info.java` flag when compiling (as shown in the compile command above), or remove/ignore `module-info.java` if you're not using Java modules.

### Problem: "package does not exist" errors
**Solution:** Make sure you're using `-sourcepath src` when compiling and that all files follow the package structure under `src/`.

### Problem: GUI doesn't appear
**Solution:** Ensure you're running from the correct package path: `sc2002_grpproject.main.MainAppGUI` (not just `MainAppGUI`).

### Problem: Compilation takes too long
**Solution:** The PowerShell command compiles all Java files recursively. This is normal for the first compilation. Subsequent compilations will be faster.

---
**Repository:** SC2002_SCEA_Group3  
**Branch:** structured_v3  
**Last Updated:** November 18, 2025
