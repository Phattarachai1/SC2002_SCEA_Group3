# SC2002 Internship Placement Management System - GUI Version

## 🚀 How to Run the GUI Application

### Prerequisites
- Java JDK 11 or higher
- Command prompt or terminal

### Running the Application

**IMPORTANT:** This repository contains both console and GUI versions. To run the **GUI version**, follow these steps:

#### Option 1: Using Command Line
```bash
# 1. Navigate to the project directory
cd sc2002_gui

# 2. Compile all Java files
javac -d bin src/*.java

# 3. Run the GUI application (NOT MainApp!)
java -cp bin sc2002_grpproject.MainAppGUI
```

#### Option 2: Using an IDE (Eclipse/IntelliJ)
1. Import the project into your IDE
2. Make sure the `src` folder is marked as source folder
3. **Right-click on `MainAppGUI.java`** (NOT `MainApp.java`)
4. Select "Run As" → "Java Application"

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
sc2002_gui/
├── src/
│   ├── MainAppGUI.java          ← RUN THIS FOR GUI VERSION
│   ├── MainApp.java              ← Console version (old)
│   ├── LoginFrame.java
│   ├── StudentDashboard.java
│   ├── CompanyDashboard.java
│   ├── StaffDashboard.java
│   └── (other Java files...)
├── sample_student_list.csv
├── sample_staff_list.csv
└── sample_company_representative_list.csv
```

## 🎨 GUI Features
- **Color-coded status indicators:**
  - Light grey: Selected row
  - Light orange/yellow: PENDING status
  - Light green: APPROVED/SUCCESSFUL status
  - Light red: REJECTED/UNSUCCESSFUL status
- **Click selection:** Simply click on a row to select it
- **Notification panels:** Shows pending actions at the top
- **User-friendly inputs:** Dropdown menus for majors, date pickers
- **Three role-based dashboards:**
  - Student Dashboard
  - Company Representative Dashboard
  - Career Center Staff Dashboard

## 🔐 Test Accounts

### Students
- Email: `tan001@e.ntu.edu.sg` | Password: `password`
- Email: `ng002@e.ntu.edu.sg` | Password: `password`
- Email: `wong005@e.ntu.edu.sg` | Password: `password`

### Career Center Staff
- Email: `staff001@ntu.edu.sg` | Password: `password`

### Company Representatives
- Email: `rep001@company.com` | Password: `password`

## 📝 Notes
- Default password for all CSV users is: `password`
- Company representatives can register new accounts through the GUI
- CSV files must be in the same directory as the compiled classes

## 🐛 Troubleshooting

### Problem: Console interface appears instead of GUI
**Solution:** You're running `MainApp.java`. Run `MainAppGUI.java` instead.

### Problem: CSV files not found
**Solution:** Make sure the CSV files are in the root directory of the project.

### Problem: Module errors
**Solution:** Remove or ignore `module-info.java` if you're not using Java modules.

---
**Branch:** GUI_v1  
**Last Updated:** November 7, 2025
