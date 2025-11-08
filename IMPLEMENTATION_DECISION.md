# Complete Refactoring Implementation Guide

## Status: Student Panels COMPLETED ✓

### ✅ Already Created:
1. `src/boundary/helpers/TableStyleHelper.java`
2. `src/boundary/helpers/UIHelper.java`
3. `src/boundary/panels/student/InternshipBrowsePanel.java`
4. `src/boundary/panels/student/ApplicationManagementPanel.java`
5. `src/boundary/dashboard/StudentDashboard.java` (new refactored version)

### Remaining Work:

Due to the large scope, I recommend a **HYBRID APPROACH**:

## Option 1: Minimal Refactoring (Recommended for Time Efficiency)
**Keep the current files but move them to new structure**

Instead of extracting all panels (which requires 15+ new files), we can:

1. Move existing Dashboard files to `dashboard/` folder
2. Keep the Menu classes as they are (they're controllers, not pure UI)
3. Use the new helper classes in existing dashboards
4. Keep LoginFrame and RoundedButton where they are

### Steps:
```powershell
# 1. Move dashboards to new folder
mv src/boundary/StudentDashboard.java src/boundary/dashboard/StudentDashboard_old.java
mv src/boundary/StaffDashboard.java src/boundary/dashboard/StaffDashboard.java
mv src/boundary/CompanyDashboard.java src/boundary/dashboard/CompanyDashboard.java

# 2. Use the NEW StudentDashboard (already created)
# It's in src/boundary/dashboard/StudentDashboard.java

# 3. Update package declarations in Staff and Company dashboards
# Change: package sc2002_grpproject.boundary;
# To: package sc2002_grpproject.boundary.dashboard;

# 4. Update imports in MainAppGUI
# Change: import sc2002_grpproject.boundary.StudentDashboard;
# To: import sc2002_grpproject.boundary.dashboard.StudentDashboard;

# 5. Update LoginFrame imports similarly

# 6. Compile and test
```

## Option 2: Full Panel Extraction (Time-Intensive but Better Architecture)
Continue creating all panel classes as planned. This requires:

### Staff Panels (4 files - ~600 lines total):
- `InternshipApprovalPanel.java` - Lines 228-720 from StaffDashboard
- `RepApprovalPanel.java` - Lines 162-683 from StaffDashboard
- `WithdrawalApprovalPanel.java` - Lines 380-826 from StaffDashboard  
- `ReportPanel.java` - Lines 429-999 from StaffDashboard

### Company Panels (2 files - ~400 lines total):
- `PostingManagementPanel.java` - From CompanyDashboard
- `ApplicationReviewPanel.java` - From CompanyDashboard

### New Dashboards (3 files - ~600 lines total):
- Refactored StaffDashboard (~250 lines)
- Refactored CompanyDashboard (~200 lines)
- StudentDashboard already done ✓

## **RECOMMENDATION:**

Given we've already successfully refactored StudentDashboard with panels, I suggest:

**Continue with Option 2 for Staff and Company** to maintain consistency.

However, this will require creating **9 more files** (4 staff panels + 2 company panels + 2 dashboards + 1 updated MainAppGUI).

## Current Approach Decision Point:

Would you prefer:

**A)** I create all remaining files now (9 more files, ~15 minutes)
**B)** I provide detailed code templates in this document for you to implement
**C)** We switch to Option 1 (minimal refactoring, just move files and update imports)
**D)** We keep Student fully refactored, but just update Staff/Company dashboards minimally

## My Strong Recommendation: **Option D**

**Why Option D is best:**
- Student dashboard is fully refactored ✓ (shows the pattern)
- Staff & Company keep existing structure (working code)
- Update Staff & Company to use new helpers (cleaner code)
- Move all to dashboard folder (better organization)
- ~80% of benefits with 20% of work

### If you choose Option D, here's what I'll do:

1. Update StaffDashboard to use helpers (keep all logic)
2. Update CompanyDashboard to use helpers (keep all logic)
3. Move all dashboards to dashboard/ folder
4. Update LoginFrame imports
5. Update MainAppGUI imports
6. Compile and test

This gives you:
- ✅ Reduced file sizes (helpers extract ~150 lines)
- ✅ Better organization (dashboard folder)
- ✅ One fully refactored example (Student)
- ✅ Working system immediately
- ✅ Foundation for future refactoring

**Which option do you prefer: A, B, C, or D?**
