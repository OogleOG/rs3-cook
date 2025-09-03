# RS3 Cooking Script - Complete Documentation

## 🎯 Overview

The RS3 Cooking Script is a comprehensive, professional-grade automation tool for RuneScape 3 cooking training. Built for the BotWithUs client, it features intelligent banking, advanced safety systems, comprehensive statistics tracking, and a modern ImGui interface.

## 🚀 Quick Start Guide

### Prerequisites
- BotWithUs client (latest version)
- RuneScape 3 account
- Java 17 or higher
- Raw fish in bank or inventory

### Installation Steps
1. **Download/Clone**: Get the script from the repository
2. **Build**: Run `gradlew.bat build` (Windows) or `./gradlew build` (Linux/Mac)
3. **Auto-Deploy**: JAR automatically copies to BotWithUs scripts folder
4. **Restart**: Restart BotWithUs client
5. **Launch**: Find "RS3 Cooking Script" in Local Scripts

### First Run
1. **Start the script** in BotWithUs
2. **Open interface** - script window appears automatically
3. **Select fish** in "Fish Selection" tab
4. **Choose location** in "Locations" tab
5. **Configure banking** in "Banking" tab
6. **Start cooking** with the "Start Cooking" button

## 📊 Interface Guide

### Main Settings Tab
- **Script Controls**: Start/Stop buttons
- **Current Status**: View script state and selected options
- **Quick Configuration**: Essential settings at a glance
- **Break System**: Enable/disable breaks

### Fish Selection Tab
- **Fish Database**: 40+ fish with detailed information
- **Filter Options**: Search by name, filter by membership
- **Fish Details**: Level requirements, XP values, burn rates
- **Recommendations**: Suggested fish based on your level

### Locations Tab
- **Location Types**: Fires, ranges, bonfires, portable ranges
- **Accessibility**: F2P vs Members locations
- **Distance Info**: Bank proximity and travel efficiency
- **Requirements**: Quest and level requirements

### Banking Tab
- **Bank Selection**: Choose specific bank or auto-detect nearest
- **Preset Support**: Configure bank presets for efficiency
- **Banking Options**: Customize banking behavior
- **Status Display**: Current bank and accessibility

### Statistics Tab
- **Real-time Metrics**: Live XP/hour, success rates, session time
- **Fish Statistics**: Individual fish performance tracking
- **Historical Data**: Today's and weekly statistics
- **Progress Tracking**: Target progress and time limits

### Debug Tab
- **Script Controls**: Advanced start/stop/reset functions
- **System Status**: Current state and diagnostic information
- **Advanced Settings**: Fine-tune timing and behavior
- **Pattern Analysis**: Anti-detection monitoring

## 🐟 Fish Database

### Free-to-Play Fish
| Fish | Level | XP | Burn Level | Notes |
|------|-------|----|-----------|----|
| Shrimps | 1 | 30 | Never | Perfect starter |
| Sardine | 1 | 40 | Never | Good early option |
| Herring | 5 | 50 | Never | Low level training |
| Anchovies | 1 | 30 | Never | Alternative starter |
| Trout | 15 | 70 | 20 | Popular F2P choice |
| Pike | 20 | 80 | 25 | Decent experience |
| Salmon | 25 | 90 | 30 | Good mid-level |
| Tuna | 30 | 100 | 35 | Solid choice |
| Lobster | 40 | 120 | 45 | Popular training |
| Swordfish | 45 | 140 | 50 | High level F2P |

### Members Fish (Selection)
| Fish | Level | XP | Burn Level | Notes |
|------|-------|----|-----------|----|
| Karambwan | 30 | 190 | 35 | Excellent XP |
| Monkfish | 62 | 150 | 67 | Popular choice |
| Shark | 80 | 210 | 85 | High level |
| Rocktail | 93 | 225 | 98 | Elite training |
| Sailfish | 99 | 270 | Never burns | Best XP rates |

## 🏛️ Location Database

### Free-to-Play Locations
| Location | Type | Bank Distance | Notes |
|----------|------|---------------|-------|
| Lumbridge Range | Range | 15 tiles | Best F2P location |
| Varrock East Bank | Range | 8 tiles | Near Grand Exchange |
| Al Kharid Range | Range | 12 tiles | Good alternative |
| Edgeville Range | Range | 10 tiles | Convenient location |
| Grand Exchange Bonfire | Bonfire | 15 tiles | Increased XP rates |

### Members Locations
| Location | Type | Bank Distance | Requirements |
|----------|------|---------------|--------------|
| Rogues' Den | Range | 5 tiles | None |
| Cooking Guild | Range | 3 tiles | Chef's hat or Varrock hard diary |
| Catherby | Range | 8 tiles | None |
| Prifddinas | Range | 6 tiles | Plague's End quest |
| Max Guild | Range | 4 tiles | Max cape |

## 🛡️ Safety Systems

### Anti-Detection Features
- **Randomized Timing**: Variable delays (600-1200ms base)
- **Micro-breaks**: Random short pauses (1-4 seconds, 2% chance)
- **Action Variation**: Randomized click locations
- **Pattern Monitoring**: Real-time behavior analysis

### Break System
- **Configurable Timing**: 30-120 second breaks
- **Random Intervals**: Every 5-15 minutes of activity
- **Activity Simulation**: Realistic break behavior
- **Manual Override**: User-triggered breaks

### Emergency Safety
- **Health Monitoring**: Stops if health < 20%
- **Combat Detection**: Pauses if player enters combat
- **Idle Detection**: Stops after 5 minutes inactivity
- **Failure Limits**: Stops after 5 consecutive failures
- **Session Limits**: Time and XP-based stopping

## 📈 Statistics System

### Real-time Tracking
- **Experience**: Gained XP, XP/hour, level progress
- **Fish Performance**: Cooked, burned, success rates
- **Efficiency**: Banking time, cooking speed
- **Session Data**: Duration, breaks, actions

### Historical Analysis
- **Session Records**: Complete history with JSON export
- **Daily Logs**: Detailed activity logs
- **Performance Trends**: XP rate analysis
- **Data Export**: CSV and JSON formats

### File Structure
```
%USERPROFILE%\BotWithUs\rs3cook\
├── config.json              # User configuration
├── logs\
│   ├── sessions.json         # Session history
│   ├── cooking_2024-01-15.log # Daily logs
│   └── ...
└── exports\                  # Data exports
```

## ⚙️ Configuration System

### Automatic Persistence
- **Settings**: All preferences saved automatically
- **JSON Format**: Human-readable configuration files
- **Profile Support**: Multiple configuration profiles
- **Import/Export**: Share configurations

### Key Settings
- **Fish Selection**: Preferred fish types
- **Location Preferences**: Cooking and banking locations
- **Safety Settings**: Break timing, emergency stops
- **UI Preferences**: Interface customization

## 🔧 Troubleshooting

### Common Issues

#### Script Not Loading
**Symptoms**: Script doesn't appear in BotWithUs
**Solutions**:
1. Check JAR file location: `%USERPROFILE%\BotWithUs\scripts\local\`
2. Verify script.ini is in JAR (should be automatic)
3. Restart BotWithUs completely
4. Check console for error messages

#### Compilation Errors
**Symptoms**: Build fails or errors during compilation
**Solutions**:
1. Ensure Java 17+ is installed
2. Use IntelliJ IDEA for building
3. Run `gradlew clean build` for fresh compilation
4. Check all dependencies are available

#### Banking Issues
**Symptoms**: Script can't bank or withdraw fish
**Solutions**:
1. Ensure raw fish are in bank
2. Check bank preset configuration
3. Verify bank location accessibility
4. Note: Some banking features limited by BotWithUs API

#### Performance Issues
**Symptoms**: Script runs slowly or freezes
**Solutions**:
1. Close other applications
2. Check system resources
3. Enable debug mode for detailed logging
4. Restart BotWithUs client

### Debug Mode
Enable in Debug tab for detailed information:
- **State Tracking**: Monitor script state changes
- **Action Logging**: Log all actions with timestamps
- **Error Details**: Detailed error messages
- **Performance Metrics**: Timing and efficiency data

## 🤝 Development

### Contributing Guidelines
1. **Fork** the repository on GitHub
2. **Create feature branch**: `git checkout -b feature/name`
3. **Follow code standards**: Java 17+, clear documentation
4. **Test thoroughly**: Verify changes work correctly
5. **Submit PR**: With detailed description

### Code Structure
```
src/main/java/net/botwithus/rs3cook/
├── CookingScript.java           # Main script class
├── config/                      # Configuration system
├── data/                        # Fish and location data
├── cooking/                     # Cooking logic
├── banking/                     # Banking system
├── safety/                      # Safety and anti-detection
├── statistics/                  # Statistics and logging
└── ui/                         # User interface
```

### API Limitations
- **Banking API**: Limited by BotWithUs API availability
- **Interface Detection**: Some interfaces may not be accessible
- **Game Updates**: May require updates for new content

## 📄 Legal & Safety

### Disclaimer
This script is provided for educational purposes only. Users are responsible for:
- Compliance with RuneScape's Terms of Service
- Account safety and appropriate usage
- Understanding automation risks
- Responsible and ethical use

### Best Practices
- **Use breaks**: Always enable the break system
- **Monitor sessions**: Don't run unattended for long periods
- **Stay updated**: Keep script and client updated
- **Report issues**: Help improve the script by reporting bugs

### Risk Mitigation
- **Start slowly**: Begin with short sessions
- **Test thoroughly**: Verify script behavior before long runs
- **Use safety features**: Enable all safety systems
- **Stay informed**: Keep up with RuneScape policy changes

---

**For additional support, check the GitHub issues page or BotWithUs community forums.**
