# Minecraft Area Miner

An advanced Fabric mod for Minecraft 1.21.x+ that provides automated area mining with visual preview, GUI configuration, and multiplayer support.

## Features

### 🎯 Area Mining System
- Define rectangular mining areas with customizable dimensions (X, Y, Z coordinates)
- Visual preview/overlay of mining area in-game (box outline visualization)
- Automatic block breaking within the defined area
- Support for selective block type mining (whitelist/blacklist system)
- Smooth mining animation with proper drop handling
- Configurable mining speed (0.1x to 10x)

### 🎮 In-Game GUI Configuration
- Interactive screen to manage mining operations
- Set mining area position (X, Y, Z min/max coordinates)
- Adjustable area size (width, height, depth)
- Mining speed adjustment
- Start/Stop/Pause controls
- Real-time visual preview showing mining area
- Status display (blocks to mine, area size)
- Automatic configuration saving

### 🌐 Multiplayer Support
- Full compatibility with single-player mode
- Server-side support with proper synchronization
- Permission system for servers (op-only mining)
- Network packet handling for multiplayer scenarios
- Server logging of mining operations

### ⚙️ Configuration & Persistence
- Automatic saving of mining configurations
- Auto-load last configuration on startup
- JSON-based configuration files
- Preset management system

### 🔧 Additional Features
- Keybind to open/close mining GUI (default: M key)
- Mining area visualization toggle (default: V key)
- Sound effects for mining completion
- Particle effects during mining
- Configurable visualization colors and transparency

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.x
2. Download and install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest release of Area Miner
4. Place the `.jar` file in your `mods` folder
5. Launch Minecraft with Fabric

## Usage

### Basic Usage
1. Press `M` to open the mining GUI
2. Enter the minimum and maximum coordinates for your mining area
3. Set the mining speed (1.0 is normal speed)
4. Click "Toggle Preview" to see the area outline
5. Click "Start Mining" to begin automated mining
6. Use "Pause" or "Stop" to control the mining process

### Keybinds
- **M** - Open Mining GUI
- **V** - Toggle Area Visualization

### Server Usage
On multiplayer servers, only operators (OP level 2+) can use the mining feature. This is configurable in the mod settings.

## Building from Source

Requirements:
- Java 21 or higher
- Gradle (included via wrapper)

```bash
# Clone the repository
git clone https://github.com/duyanhggg/minecraft-area-miner.git
cd minecraft-area-miner

# Build the mod
./gradlew build

# The compiled mod will be in build/libs/
```

## Configuration

The mod stores its configuration in `.minecraft/config/area-miner/`:
- `config.json` - Main configuration file
- `presets/` - Saved mining presets

### Configuration Options
- `autoLoadLastConfig` - Automatically load last used configuration
- `enableSounds` - Enable sound effects
- `enableParticles` - Enable particle effects
- `defaultMiningSpeed` - Default mining speed
- `visualizationEnabled` - Enable area visualization
- `visualizationColor` - Color for area outline (hex)
- `visualizationAlpha` - Transparency of area outline
- `requireOpOnServers` - Require OP permission on servers
- `logMiningOperations` - Log mining operations to server console

## Technical Details

### Project Structure
```
src/main/java/com/duyanhggg/areaminer/
├── AreaMiner.java              # Main mod initializer
├── AreaMinerClient.java        # Client-side initializer
├── config/                     # Configuration management
│   ├── ConfigManager.java
│   ├── ModConfig.java
│   └── MiningPreset.java
├── mining/                     # Mining logic
│   ├── MiningArea.java
│   └── MiningController.java
├── gui/                        # GUI screens
│   └── MiningScreen.java
├── network/                    # Network synchronization
│   └── NetworkHandler.java
├── renderer/                   # Visual rendering
│   └── AreaRenderer.java
└── events/                     # Event handlers
    └── ClientEventHandlers.java
```

### Dependencies
- Fabric Loader 0.16.0+
- Fabric API 0.110.0+
- Minecraft 1.21.4
- Java 21+

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

If you encounter any issues or have suggestions, please open an issue on the [GitHub repository](https://github.com/duyanhggg/minecraft-area-miner/issues).

## Credits

Created by duyanhggg