# Minecraft Area Miner - Implementation Details

This document provides a comprehensive overview of the implementation of the Area Miner Fabric mod for Minecraft 1.21.x+.

## Project Overview

The Area Miner mod is a fully-featured Fabric mod that provides automated area mining with a rich GUI, visualization, networking support, and extensive configuration options.

## Implementation Summary

### ✅ Completed Features

#### 1. Core Mining System

**MiningArea.java**
- Defines rectangular mining areas with min/max BlockPos coordinates
- Supports whitelist/blacklist filtering for selective block mining
- Calculates area dimensions (width, height, depth) and total block count
- Provides contains() method for efficient block checking

**MiningController.java**
- Manages mining sessions on the server side
- Implements MiningSession inner class for tracking individual mining operations
- Provides start/stop/pause/resume functionality
- Calculates mining progress based on configurable speed (0.1x to 10x)
- Handles block breaking with proper drop handling
- Sends progress updates to clients every second (20 ticks)
- Plays completion sound (ENTITY_PLAYER_LEVELUP)
- Comprehensive server-side logging

Key Features:
- Thread-safe session management using UUID-based HashMap
- Efficient block iteration and breaking
- Speed-based mining with fractional progress tracking
- Automatic session cleanup on completion

#### 2. Visual Rendering System

**AreaRenderer.java**
- Renders 3D box outlines for mining areas
- Uses WorldRenderEvents.AFTER_TRANSLUCENT for proper rendering
- Configurable color and transparency (alpha channel)
- Toggle-able visualization (on/off)
- Proper depth testing and blending for clean visuals
- Matrix transformations for camera-relative positioning

Rendering Details:
- Uses VertexFormat.DrawMode.DEBUG_LINES for efficient line rendering
- Draws 12 edges of the bounding box (4 bottom, 4 top, 4 vertical)
- Handles camera position offset for correct world-space rendering

#### 3. GUI System

**MiningScreen.java**
- Full-featured configuration screen extending Screen
- Input fields for min/max X, Y, Z coordinates (6 fields total)
- Speed input field with validation
- Four action buttons: Start, Stop, Pause, Preview
- Real-time display of total blocks and area dimensions
- Auto-loads last configuration on open
- Saves configuration on mining start
- Preview mode with live area visualization

GUI Layout:
- Centered layout with logical grouping
- Min position on left, max position in middle, speed on right
- Buttons aligned horizontally below inputs
- Status information displayed at bottom
- Proper event handling for keyboard and mouse input

#### 4. Networking & Multiplayer

**NetworkHandler.java**
- Complete packet system using Fabric's CustomPayload API
- Five payload types:
  - StartMiningPayload (C2S): Initiates mining with area and speed
  - StopMiningPayload (C2S): Stops active mining session
  - PauseMiningPayload (C2S): Pauses mining temporarily
  - ResumeMiningPayload (C2S): Resumes paused session
  - MiningProgressPayload (S2C): Updates client with progress

Security & Permissions:
- Server-side permission checks (requires OP level 2+)
- Packet validation and error handling
- Server execution context for thread safety

#### 5. Configuration Management

**ConfigManager.java**
- JSON-based configuration using Gson
- Auto-creates config directory: `.minecraft/config/area-miner/`
- Preset system with save/load functionality
- Preset directory: `.minecraft/config/area-miner/presets/`
- Thread-safe file operations with proper error handling

**ModConfig.java**
- Comprehensive settings:
  - Auto-load last configuration
  - Sound and particle toggle
  - Default mining speed
  - Visualization settings (enabled, color, alpha)
  - Server permission requirements
  - Operation logging toggle
- Stores last used configuration for auto-load

**MiningPreset.java**
- Named presets with complete area configuration
- Supports whitelist/blacklist block lists
- JSON serialization for easy editing

Example presets provided:
- Small Cave Mining (10x10x10)
- Large Area Mining (64x32x64)

#### 6. Client-Side Integration

**AreaMinerClient.java**
- Client mod initializer
- Registers keybindings:
  - M key: Open mining GUI
  - V key: Toggle visualization
- Initializes client event handlers
- Sets up network packet receivers
- Initializes rendering system

**ClientEventHandlers.java**
- Handles client tick events
- Processes keybinding inputs
- Sends actionbar feedback messages
- Opens GUI screen on key press

#### 7. Server-Side Integration

**AreaMiner.java**
- Main mod initializer for server
- Initializes configuration system
- Registers server-side network packets
- Initializes mining controller
- Comprehensive logging

#### 8. Additional Features

**Localization (en_us.json)**
- Complete English translations for:
  - Keybindings
  - GUI elements
  - Status messages
  - Category names

**Mixin Configuration (area-miner.mixins.json)**
- Ready for future mixin additions
- Configured for Java 21 compatibility

**Mod Metadata (fabric.mod.json)**
- Proper mod identification (area-miner)
- Version templating from gradle.properties
- Dual entrypoints (main and client)
- Dependency requirements (Fabric Loader 0.16+, Minecraft ~1.21, Fabric API)
- License and contact information

## Technical Architecture

### Package Structure

```
com.duyanhggg.areaminer/
├── AreaMiner.java              # Server-side mod initializer
├── AreaMinerClient.java        # Client-side mod initializer
├── config/                     # Configuration management
│   ├── ConfigManager.java      # File I/O and preset management
│   ├── ModConfig.java          # Main configuration class
│   └── MiningPreset.java       # Preset data class
├── mining/                     # Core mining logic
│   ├── MiningArea.java         # Area definition and filtering
│   └── MiningController.java   # Session management and execution
├── gui/                        # User interface
│   └── MiningScreen.java       # Main configuration screen
├── network/                    # Multiplayer networking
│   └── NetworkHandler.java     # Packet definitions and handlers
├── renderer/                   # Visual rendering
│   └── AreaRenderer.java       # Area box visualization
└── events/                     # Event handlers
    └── ClientEventHandlers.java # Client tick and input handling
```

### Design Patterns Used

1. **Singleton Pattern**: ConfigManager, MiningController (static instance management)
2. **Factory Pattern**: MiningController.createSession()
3. **Observer Pattern**: Network progress updates, event handlers
4. **Strategy Pattern**: Block filtering (whitelist vs blacklist)
5. **State Pattern**: Mining session states (active, paused, complete)

### Thread Safety

- Server-side operations execute in server thread context
- Network packets properly scheduled using context.server().execute()
- Session management uses concurrent-safe collections
- Configuration file I/O with proper error handling

### Performance Optimizations

1. **Efficient Block Iteration**: Pre-builds block list to avoid repeated lookups
2. **Fractional Progress**: Mines multiple blocks per tick based on speed
3. **Batch Updates**: Sends progress updates every 20 ticks instead of every tick
4. **Conditional Rendering**: Only renders when visualization enabled
5. **Early Returns**: Checks prevent unnecessary processing

## Build Configuration

### Gradle Setup

**gradle.properties**
- Minecraft version: 1.21.4
- Yarn mappings: 1.21.4+build.1
- Fabric Loader: 0.16.9
- Fabric API: 0.110.0+1.21.4
- Mod version: 1.0.0
- Java version: 21

**build.gradle**
- Fabric Loom plugin for Minecraft mod development
- Split source sets (main and client)
- Proper Maven repositories (Fabric, Gradle Plugin Portal)
- Source JAR generation
- Publishing configuration ready

**settings.gradle**
- Plugin management with Fabric repository
- Root project name: area-miner

### Dependencies

Required:
- Fabric Loader 0.16.0+
- Minecraft 1.21.x
- Fabric API (all modules)
- Java 21+

Transitive:
- Gson (via Fabric Loader for config)
- LWJGL (via Minecraft for rendering)
- SLF4J (via Fabric Loader for logging)

## API and Extension Points

### For Mod Developers

The mod is designed to be extensible:

1. **Custom Block Filters**: Extend MiningArea to add custom filtering logic
2. **Mining Algorithms**: Override buildBlockList() in MiningSession for custom patterns
3. **GUI Customization**: Extend MiningScreen for additional controls
4. **Network Extensions**: Add custom payloads for additional features
5. **Rendering**: Modify AreaRenderer for different visualization styles

### Events

The mod subscribes to:
- `ServerTickEvents.END_SERVER_TICK` - Mining execution
- `ClientTickEvents.END_CLIENT_TICK` - Input handling
- `WorldRenderEvents.AFTER_TRANSLUCENT` - Area rendering

## Configuration Files

### Main Config (config.json)

```json
{
  "autoLoadLastConfig": true,
  "enableSounds": true,
  "enableParticles": true,
  "defaultMiningSpeed": 1.0,
  "visualizationEnabled": true,
  "visualizationColor": 65280,
  "visualizationAlpha": 0.3,
  "requireOpOnServers": true,
  "logMiningOperations": true,
  "lastMinX": 0,
  "lastMinY": 64,
  "lastMinZ": 0,
  "lastMaxX": 16,
  "lastMaxY": 80,
  "lastMaxZ": 16,
  "lastSpeed": 1.0,
  "lastUseWhitelist": false
}
```

### Preset Format (presets/*.json)

```json
{
  "name": "Preset Name",
  "minX": 0,
  "minY": 0,
  "minZ": 0,
  "maxX": 10,
  "maxY": 10,
  "maxZ": 10,
  "speed": 1.0,
  "useWhitelist": false,
  "whitelist": [],
  "blacklist": []
}
```

## Security Considerations

1. **Permission Checks**: Server requires OP level 2 for mining operations
2. **Input Validation**: Coordinates and speed values validated before use
3. **Rate Limiting**: Progress updates limited to once per second
4. **Session Isolation**: Each player has separate session (UUID-based)
5. **Server Authority**: All mining logic runs on server, client only sends requests

## Known Limitations

1. **No Undo**: Once mining starts, blocks cannot be restored (by design)
2. **Single Session**: Each player can only have one active mining session
3. **No Cross-Dimension**: Mining area must be in player's current dimension
4. **Memory Usage**: Large areas pre-calculate all block positions
5. **Permission Model**: Fixed OP level requirement (not configurable per-player)

## Future Enhancement Possibilities

1. **Advanced Filtering**: Ore-only mode, tool-specific blocks
2. **Multi-Area**: Support multiple mining areas per player
3. **Scheduling**: Delayed start, scheduled operations
4. **Statistics**: Per-block-type counters, efficiency metrics
5. **Block Patterns**: Customizable mining patterns (spiral, layered, etc.)
6. **Integration**: Fortune support, silk touch compatibility
7. **UI Improvements**: Preset selector in GUI, visual coordinate picker
8. **Performance**: Chunk-based iteration for massive areas

## Testing Recommendations

### Unit Testing
- MiningArea bounds checking
- MiningController session management
- ConfigManager file operations
- Network packet serialization

### Integration Testing
- Client-server synchronization
- Permission enforcement
- Configuration persistence
- GUI interaction

### Performance Testing
- Large area mining (1000+ blocks)
- Multiple concurrent sessions
- Rendering performance with multiple areas
- Memory usage with extreme coordinates

## Compliance

### Minecraft EULA
- No gameplay advantage (automation is optional)
- No pay-to-win features
- Open source (MIT license)

### Fabric Guidelines
- Uses official Fabric API
- Follows Fabric modding conventions
- Proper entrypoint usage
- No ASM/bytecode manipulation (uses Fabric's systems)

## Documentation

- README.md: User-facing documentation with installation and usage
- BUILD.md: Build instructions and troubleshooting
- IMPLEMENTATION.md: This file - technical implementation details
- Code comments: Inline documentation in all source files

## Version History

### v1.0.0 (Current)
- Initial release
- Complete feature set as specified
- Minecraft 1.21.4 support
- Full single-player and multiplayer functionality

## Credits

- **Author**: duyanhggg
- **Framework**: Fabric (FabricMC team)
- **Game**: Minecraft (Mojang Studios)

## License

MIT License - See LICENSE file for full text.
