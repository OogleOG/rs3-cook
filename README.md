# 🍳 RS3 Cooking Script for BotWithUs

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![BotWithUs](https://img.shields.io/badge/BotWithUs-Compatible-blue.svg)](https://botwithus.net/)

A **comprehensive, professional-grade** RuneScape 3 cooking script for the BotWithUs client. Features intelligent banking, advanced safety systems, comprehensive statistics tracking, and a modern ImGui interface.

![Script Interface](https://via.placeholder.com/800x400/2c3e50/ecf0f1?text=RS3+Cooking+Script+Interface)

## ✨ Features

### 🐟 **Complete Fish Database**
- **40+ Fish Types** - From shrimps to sailfish with accurate data
- **Detailed Information** - Cooking levels, XP values, burn rates
- **Smart Recommendations** - Automatic fish suggestions based on your level
- **F2P & Members Support** - Complete coverage for all account types

### 🏛️ **Advanced Location System**
- **Cooking Locations** - Prifddinas & All Portable Range spots
- **Multiple Methods** - Bonfires and Portable Ranges
- **Intelligent Selection** - Automatic best location detection
- **Bank Integration** - Optimized for banking efficiency

### 🏦 **Smart Banking System**
- **40+ Bank Locations** - Comprehensive bank database
- **Nearest Bank Detection** - Automatic pathfinding to closest bank
- **Intelligent Management** - Auto-deposit cooked fish, withdraw raw fish

### 🖥️ **Professional Interface**
- **Modern ImGui Design** - Clean, tabbed interface
- **Real-time Statistics** - Live XP/hour, success rates, session tracking
- **Interactive Configuration** - Easy fish and location selection
- **Progress Monitoring** - Visual progress indicators and estimates

### 🛡️ **Advanced Safety & Anti-Detection**
- **Intelligent Break System** - Configurable, randomized break intervals
- **Anti-Pattern Detection** - Monitors and prevents repetitive behavior
- **Emergency Safety Systems** - Health monitoring, combat detection, fail-safes
- **Human-like Behavior** - Randomized timing and micro-variations

### 📊 **Comprehensive Statistics**
- **Real-time Tracking** - XP rates, fish statistics, efficiency metrics
- **Historical Analysis** - Session history with trend analysis
- **Detailed Logging** - Daily logs with timestamps and performance data
- **Export Capabilities** - JSON and CSV export for external analysis

## 🚀 Quick Start

### Prerequisites
- [BotWithUs Client](https://botwithus.net/) (latest version)
- RuneScape 3 account
- Java 20
- Raw fish in bank or inventory

### Installation
```bash
# Clone the repository
git clone https://github.com/your-username/rs3-cook.git
cd rs3-cook

# Build the project (Windows)
gradlew.bat build

# Build the project (Linux/Mac)
./gradlew build
```

The script will automatically:
- ✅ Compile to JAR format
- ✅ Copy to BotWithUs scripts folder
- ✅ Include all necessary metadata

### First Run
1. **Restart BotWithUs** client
2. **Find "RS3 Cooking Script"** in Local Scripts
3. **Start the script** - interface appears automatically
4. **Configure settings** in the tabbed interface
5. **Click "Start Cooking"** and monitor progress

## 📖 Documentation

- **[User Guide](USER_GUIDE.md)** - Step-by-step usage instructions
- **[Technical Documentation](DOCUMENTATION.md)** - Detailed implementation guide
- **[Changelog](CHANGELOG.md)** - Version history and updates

## 🎯 Supported Content

<details>
<summary><strong>🐟 Fish Types (40+ Supported)</strong></summary>

### Free-to-Play Fish
| Fish | Level | XP | Burn Level | Notes |
|------|-------|----|-----------|----|
| Shrimps | 1 | 30 | Never | Perfect starter |
| Sardine | 1 | 40 | Never | Good early option |
| Herring | 5 | 50 | Never | Low level training |
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
| Sailfish | 99 | 270 | Never | Best XP rates |

</details>

<details>
<summary><strong>🏛️ Cooking Locations (30+ Supported)</strong></summary>

### Free-to-Play Locations
- **Lumbridge Range** - Best F2P location, close bank upstairs
- **Varrock East Bank** - Popular, near Grand Exchange  
- **Al Kharid Range** - Good alternative with nearby bank
- **Edgeville Range** - Convenient location near bank
- **Grand Exchange Bonfire** - Increased XP rates

### Members Locations
- **Rogues' Den** - Excellent bank proximity (5 tiles)
- **Cooking Guild** - Best range location, requires chef's hat
- **Catherby** - Popular for fishing/cooking combination
- **Prifddinas** - High-level location with crystal range
- **Max Guild** - Premium location for maxed players

</details>

## 🛡️ Safety & Compliance

### Built-in Safety Features
- ✅ **Randomized Timing** - Human-like delays and variations
- ✅ **Break System** - Regular breaks with realistic timing
- ✅ **Pattern Avoidance** - Anti-detection monitoring
- ✅ **Emergency Stops** - Health, combat, and failure detection
- ✅ **Session Limits** - Time and XP-based stopping conditions

### Best Practices
- **Always use breaks** - Enable the break system
- **Start with short sessions** - 30-60 minutes initially
- **Monitor regularly** - Check on the script periodically
- **Stay updated** - Keep script and client updated

## 🔧 Troubleshooting

<details>
<summary><strong>Common Issues & Solutions</strong></summary>

### Script Not Appearing
- ✅ Check JAR file is in `%USERPROFILE%\BotWithUs\scripts\local\`
- ✅ Verify `script.ini` is included in JAR
- ✅ Restart BotWithUs client completely
- ✅ Check console for error messages

### Compilation Issues
- ✅ Ensure Java 17+ is installed
- ✅ Use IntelliJ IDEA for building
- ✅ Run `gradlew clean build` for fresh compilation

### Banking Problems
- ✅ Ensure raw fish are in bank
- ✅ Check bank preset configuration
- ✅ Verify bank location accessibility

</details>

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Setup
1. **Fork** the repository
2. **Clone** your fork locally
3. **Create** a feature branch
4. **Make** your changes
5. **Test** thoroughly
6. **Submit** a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## ⚠️ Disclaimer

**Important**: This script is provided for educational purposes only. Users are responsible for:

- ✅ **Compliance** with RuneScape's Terms of Service
- ✅ **Account safety** and appropriate usage
- ✅ **Understanding** automation risks
- ✅ **Responsible use** and ethical behavior

The developers are not responsible for any account actions, bans, or consequences resulting from script usage. Use at your own risk and discretion.

## 🆘 Support

- **📖 Documentation** - Check the guides and documentation
- **🐛 Bug Reports** - Use GitHub Issues for bugs
- **💡 Feature Requests** - Submit enhancement ideas
- **💬 Community** - Join BotWithUs forums for discussion

---

<div align="center">

**⭐ Star this repository if you find it helpful!**

Made with ❤️ for the BotWithUs community

</div>
