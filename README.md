# RS3 Cooking Script for BotWithUs

A comprehensive RuneScape 3 cooking script that supports all fish types and cooking methods including fires, bonfires, cooking ranges, and portable ranges. Features intelligent banking, walking to nearest banks, and a full ImGui interface.

## Features

### Fish Support
- **All RS3 Fish Types**: Supports every cookable fish in RuneScape 3
- **Smart Cooking**: Automatically handles burn rates and cooking levels
- **Experience Tracking**: Real-time XP/hour calculations and progress tracking

### Cooking Locations
- **Fires**: Regular fires and player-made fires
- **Bonfires**: Enhanced XP rates with bonfire cooking
- **Cooking Ranges**: All cooking ranges throughout RS3
- **Portable Ranges**: Support for portable cooking ranges
- **Auto-Detection**: Automatically detects and uses the best available cooking method

### Banking System
- **Intelligent Banking**: Finds and walks to the nearest bank
- **Preset Support**: Uses bank presets for efficient fish withdrawal
- **Auto-Banking**: Automatically banks cooked fish and withdraws raw fish
- **Smart Inventory Management**: Optimizes inventory space usage

### User Interface
- **ImGui Interface**: Modern, responsive user interface
- **Fish Selection**: Easy fish type selection with filtering
- **Location Preferences**: Configure preferred cooking locations
- **Real-time Statistics**: Live XP/hour, profit, and session statistics
- **Configuration Persistence**: Saves your preferences between sessions

### Safety Features
- **Anti-Detection**: Randomized timing and human-like behavior patterns
- **Break System**: Configurable break intervals for safe botting
- **Fail-safes**: Automatic error recovery and safe stopping
- **Session Limits**: Configurable time and XP limits

## Requirements

- BotWithUs client
- RuneScape 3 membership (for some fish types and locations)
- Java 17 or higher
- Gradle (included via wrapper)

## Installation

1. Clone this repository
2. Build the project: `./gradlew build` (or `gradlew.bat build` on Windows)
3. The compiled JAR and script.ini will be automatically copied to your BotWithUs scripts folder
4. Restart BotWithUs client if it's running
5. The script should now appear in your Local Scripts list

## Usage

1. Start the script in BotWithUs
2. Configure your preferred fish type and cooking location
3. Set up your bank preset with raw fish
4. Start the script and monitor via the ImGui interface

## Configuration

The script saves your preferences automatically, including:
- Selected fish type
- Preferred cooking location
- Banking preferences
- Break intervals
- XP goals

## Contributing

Feel free to submit issues and enhancement requests!
