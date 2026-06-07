# 🎓 YunTech Campus Activity Registration Platform - Modern Redesign

## ✨ Design Overview

A modern, professional JavaFX application for the National Yunlin University of Science and Technology (YunTech) campus activity registration system. The redesign features a sleek, Apple-inspired interface with Material Design principles and modern glassmorphism effects.

## 🎨 Design Features

### Color Palette
- **Primary Green**: #0A5338 (University Primary)
- **Secondary Green**: #2E8B57 (Accent Green)
- **Accent Blue**: #009CF7 (Modern Blue Accents)
- **Background**: #F8FAFC (Light Background)
- **Card White**: #FFFFFF (Clean Cards)

### Modern Design Elements
✅ **Hero Banner** - Large gradient banner with campus activity slogan
✅ **Centered Search Bar** - Modern search interface with sorting options
✅ **Glassmorphism Effects** - Semi-transparent cards with backdrop effects
✅ **Smooth Shadows** - Multi-layered soft shadows for depth
✅ **Responsive Layout** - Adaptive design (optimized for 1920x1080)
✅ **Modern Typography** - Professional font hierarchy and spacing
✅ **Gradient Headers** - Beautiful gradient backgrounds for tables and sections
✅ **Rounded Corners** - 12-20px border radius for modern look
✅ **Smooth Transitions** - Interactive hover effects and state changes
✅ **Professional Status Badges** - Color-coded activity status indicators

## 📁 Project Structure

```
agents-yuntech-activity-registration-platform/
├── java_frontFX/
│   └── JAVAFX/
│       ├── src/
│       │   ├── Main.java              # Application entry point
│       │   ├── Launcher.java          # JavaFX launcher
│       │   ├── style.css              # Modern styling (REDESIGNED)
│       │   ├── controller/            # Controller classes
│       │   ├── service/               # Business logic services
│       │   ├── model/                 # Data models
│       │   ├── view/                  # UI components
│       │   ├── exception/             # Custom exceptions
│       │   └── util/                  # Utility classes
│       ├── bin/                       # Compiled output
│       ├── data/                      # Activity data (CSV)
│       └── images/                    # UI assets
├── javafx-sdk-24.0.1/                 # JavaFX SDK
├── build.ps1                          # PowerShell build script
├── run.bat                            # Windows batch runner
└── README.md                          # This file
```

## 🚀 Getting Started

### Prerequisites
- Java 24+ (Java HotSpot VM)
- JavaFX 24.0.1 SDK
- Windows (or adapt scripts for your OS)

### Building the Project

**Option 1: Using PowerShell (Automatic)**
```powershell
cd "C:\path\to\agents-yuntech-activity-registration-platform"
PowerShell -ExecutionPolicy Bypass -File build.ps1
```

**Option 2: Using Batch File**
Simply run the pre-configured build script in PowerShell or as a scheduled task.

### Running the Application

**Option 1: Using Batch File (Easiest)**
```bash
run.bat
```

**Option 2: Using Command Line**
```bash
java --module-path "javafx-sdk-24.0.1\lib" ^
  --add-modules javafx.controls,javafx.fxml ^
  -cp "java_frontFX\JAVAFX\bin;javafx-sdk-24.0.1\lib\*;java_frontFX\lib\*" ^
  Launcher
```

**Option 3: Using PowerShell**
```powershell
$javaPath = "C:\path\to\agents-yuntech-activity-registration-platform"
cd $javaPath
java --module-path "javafx-sdk-24.0.1\lib" `
  --add-modules javafx.controls,javafx.fxml `
  -cp "java_frontFX\JAVAFX\bin;javafx-sdk-24.0.1\lib\*;java_frontFX\lib\*" `
  Launcher
```

## 🎯 Key Features

### For Students
- 📋 **Activity List** - Browse all campus activities with modern card layout
- 🔍 **Smart Search** - Search activities by name, location, organizer
- 📊 **Sorting Options** - Sort by event time, registration start time
- 📝 **Registration** - Register for campus activities easily
- 👤 **My Registrations** - View and manage registered activities
- 🔐 **Secure Login** - Student authentication system

### For Organizers
- 📂 **Activity Management** - Create, edit, and delete campus activities
- 📊 **Participant Dashboard** - View registration statistics
- 🎯 **Activity Control** - Manage activity details and settings
- 📅 **Schedule Management** - Control activity and registration dates

### Admin Features
- 🔐 **Dual Role Authentication** - Support for student and organizer login
- 💾 **Data Persistence** - CSV-based activity data storage
- 🛡️ **Permission Control** - Role-based access control

## 🎨 UI/UX Components

### Navigation Bar
- Clean white header with soft shadow
- University logo and branding
- Navigation menu with dynamic text updates
- Responsive layout

### Hero Banner
- Gradient background (Green #0A5338 → #2E8B57)
- Large, bold title text
- Descriptive subtitle
- ~280px height for prominent display

### Search Panel
- Centered layout for visual hierarchy
- Semi-transparent background (glassmorphism)
- Enhanced focus states
- Integrated sorting controls

### Activity Cards
- Glassmorphic design with borders
- Hover effects with deeper shadows
- Status badges (Open, Closed, Pending)
- Click to view detailed information

### Tables
- Gradient header background
- Alternating row colors for readability
- Smooth hover effects
- Modern cell styling

### Login Cards
- Centered modal design
- Clean form inputs
- Primary action buttons
- Back navigation option

## 📱 Responsive Behavior

The application is optimized for:
- **Resolution**: 1920x1080 (Full HD)
- **Scaling**: Adaptable to different screen sizes
- **DPI**: Works with standard and high DPI displays

## 🔧 Customization

### Modify Colors
Edit `style.css` and update color values:
```css
/* Primary color */
-fx-background-color: #0a5338;

/* Accent color */
-fx-background-color: #009CF7;
```

### Adjust Typography
Modify font settings in `.root`:
```css
-fx-font-family: "Microsoft JhengHei", "Segoe UI", sans-serif;
-fx-font-size: 13px;
```

### Change Styling
Update CSS classes in `style.css` for buttons, cards, and components.

## 📊 Modern CSS Architecture

The redesigned `style.css` includes:

| Section | Purpose |
|---------|---------|
| Global Settings | Root styles and fonts |
| Navigation | Navbar and menu styling |
| Hero Banner | Large banner with slogan |
| Search Panel | Enhanced search interface |
| Buttons | Primary, secondary, accent, ghost, outline variants |
| Activity Cards | Modern card design with effects |
| Login Components | Login UI styling |
| Detail Pages | Event detail view styling |
| Tables | Professional table design with gradients |
| Status Badges | Color-coded status indicators |
| Admin Sections | Management page styling |

## 🎬 Screenshots Features

The modern design includes:
- Clean, minimal interface
- Professional color scheme
- Soft, layered shadows
- Modern typography
- Consistent spacing and alignment
- Interactive hover effects
- Clear visual hierarchy

## 📝 Notes

- The application uses JavaFX 24.0.1
- All UI is defined in CSS for easy customization
- Support for Chinese characters (UTF-8 encoding)
- File-based data storage (CSV format)
- Responsive and scalable design

## 🛠️ Troubleshooting

### Application won't start
1. Ensure Java 24+ is installed: `java -version`
2. Verify JavaFX SDK path is correct
3. Check classpath includes all JAR files
4. Try running with `--verbose-startup` flag

### CSS not loading
1. Verify `style.css` is in the `bin` directory
2. Check file permissions
3. Rebuild the project with `build.ps1`

### Compilation errors
1. Clean the `bin` directory
2. Run `build.ps1` with PowerShell as Administrator
3. Check Java version compatibility

## 📞 Support

For issues or questions, check:
- Application logs in the console
- CSS styling in `src/style.css`
- Java source code in `src/view/` and `src/service/`

---

**Version**: 1.0 Modern Redesign
**Last Updated**: 2026-06-08
**Technology**: JavaFX 24.0.1 | Java 24 | CSS3
