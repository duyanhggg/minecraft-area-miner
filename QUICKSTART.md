# Quick Start Guide

Get started with the Minecraft Area Miner mod in 5 minutes!

## Installation

1. **Install Prerequisites**
   - Download and install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.x
   - Download [Fabric API](https://modrinth.com/mod/fabric-api) mod

2. **Install Area Miner**
   - Download `area-miner-1.0.0.jar` from releases
   - Place it in your `.minecraft/mods` folder
   - Launch Minecraft with the Fabric profile

## First Use

### Open the Mining GUI
Press **M** key in-game to open the mining configuration screen.

### Configure Your Mining Area

1. **Set Coordinates**
   - **Min Position**: Starting corner (X, Y, Z)
   - **Max Position**: Ending corner (X, Y, Z)
   - Example: Min (0, 64, 0) to Max (16, 80, 16) = 16x16x16 area

2. **Set Mining Speed**
   - Default: 1.0 (normal speed)
   - Range: 0.1 (slow) to 10.0 (very fast)
   - Higher speed mines more blocks per second

3. **Preview the Area**
   - Click **"Toggle Preview"** button
   - A green box outline will show your mining area
   - Press **V** key anytime to toggle visibility

### Start Mining

1. Click **"Start Mining"** button
2. Mining will begin automatically
3. Blocks will be mined and dropped as items
4. Watch the progress in chat

### Control Mining

- **Pause**: Temporarily stop mining (keeps progress)
- **Stop**: Completely stop and reset mining
- Press **V** to toggle area visualization on/off

## Tips & Tricks

### Getting Coordinates
1. Press **F3** in Minecraft to show debug screen
2. Look at "Block" coordinates (center of screen)
3. Note down the X, Y, Z values for your area corners

### Setting Up a Simple Mine
```
Min Position: Your current location
  - Press F3, note X Y Z
Max Position: 10 blocks away in each direction
  - Add 10 to each coordinate
Speed: 1.0 (start slow until you see how it works)
```

### Example Configurations

**Small Test Area** (good for testing)
- Min: (100, 60, 100)
- Max: (110, 70, 110)
- Speed: 1.0
- Size: 10x10x10 = 1,000 blocks

**Cave Clearing**
- Min: (0, 0, 0)
- Max: (20, 20, 20)
- Speed: 2.0
- Size: 20x20x20 = 8,000 blocks

**Large Excavation**
- Min: (0, 0, 0)
- Max: (64, 32, 64)
- Speed: 5.0
- Size: 64x64x32 = 131,072 blocks

## Keyboard Shortcuts

| Key | Action |
|-----|--------|
| **M** | Open/Close Mining GUI |
| **V** | Toggle Area Visualization |
| **ESC** | Close GUI |

## Safety Tips

⚠️ **Important Warnings:**

1. **Mining is permanent!** Blocks cannot be undone
2. **Start small** - test with a small area first
3. **Check your area** - use Preview before starting
4. **Mining speed** - higher speeds use more server resources
5. **Multiplayer** - Only server operators can mine

## Multiplayer Usage

### On Servers
- You must be a server **operator** (OP level 2+)
- Mining operations are logged on the server
- All players can see the mining effects
- Configuration is per-player

### Becoming an Operator
Ask the server owner to run:
```
/op YourUsername
```

## Configuration Files

After first launch, find configs at:
```
.minecraft/config/area-miner/
├── config.json          # Main settings
└── presets/             # Saved mining areas
    ├── small-cave.json
    └── large-area.json
```

### Saving Presets (Future Feature)
Currently, your last mining area is automatically saved and loaded next time you open the GUI.

## Troubleshooting

### GUI Won't Open
- Make sure you're in-game (not in a menu)
- Check that M key binding isn't conflicting
- Look in Options → Controls → Area Miner

### No Visualization
- Press V to toggle it on
- Check config: `visualizationEnabled: true`
- Look for green box outline at your coordinates

### Mining Not Starting
- **Single Player**: Should work automatically
- **Multiplayer**: You need OP permissions
- Check server console for error messages

### Blocks Not Dropping
- This is normal - blocks drop as items
- Make sure you have inventory space
- Items may scatter around the area

## Advanced Features

### Custom Visualization Color
Edit `.minecraft/config/area-miner/config.json`:
```json
{
  "visualizationColor": 65280,  // 0x00FF00 = Green
  "visualizationAlpha": 0.3     // 30% transparent
}
```

Color values (in decimal):
- Red: 16711680 (0xFF0000)
- Green: 65280 (0x00FF00) 
- Blue: 255 (0x0000FF)
- Yellow: 16776960 (0xFFFF00)
- Cyan: 65535 (0x00FFFF)
- Magenta: 16711935 (0xFF00FF)

### Disable Sounds
Edit config.json:
```json
{
  "enableSounds": false
}
```

## Performance Considerations

### Mining Speed Impact
- Speed 1.0: ~1 block/tick = 20 blocks/second
- Speed 5.0: ~5 blocks/tick = 100 blocks/second
- Speed 10.0: ~10 blocks/tick = 200 blocks/second

### Large Areas
For areas over 100,000 blocks:
- Start with speed 1.0
- Increase gradually if performance is good
- Consider mining in multiple smaller areas

## Getting Help

1. **Check Documentation**
   - README.md - Complete user guide
   - BUILD.md - Build instructions
   - IMPLEMENTATION.md - Technical details

2. **Common Issues**
   - See Troubleshooting section above
   - Check server console logs
   - Verify you have correct permissions

3. **Report Bugs**
   - GitHub Issues: https://github.com/duyanhggg/minecraft-area-miner/issues
   - Include: Minecraft version, mod version, error messages

## What's Next?

Now that you know the basics:
1. ✅ Test with a small area (10x10x10)
2. ✅ Experiment with different speeds
3. ✅ Try the visualization toggle
4. ✅ Save your favorite configurations
5. ✅ Share your experience!

Happy mining! ⛏️
