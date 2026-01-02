# Build Instructions

## Prerequisites

- Java 21 or higher
- Gradle 8.10.2 or higher  
- Internet connection to download dependencies

## Network Requirements

This project requires access to the following Maven repositories:
- `https://maven.fabricmc.net/` - Fabric Loom and Fabric API
- `https://libraries.minecraft.net/` - Minecraft libraries (via Fabric Loom)
- Maven Central - Standard Java dependencies

## Building the Mod

### Standard Build

```bash
./gradlew build
```

The compiled mod JAR will be located in `build/libs/area-miner-1.0.0.jar`

### Clean Build

```bash
./gradlew clean build
```

### Development Environment Setup

To set up a development environment for testing:

```bash
# Generate IDE run configurations
./gradlew genSources

# For IntelliJ IDEA
./gradlew idea

# For Eclipse
./gradlew eclipse
```

### Running in Development

```bash
# Run the Minecraft client
./gradlew runClient

# Run a dedicated server  
./gradlew runServer
```

## Troubleshooting

### Build Fails with Network Errors

If you encounter errors like "Could not resolve net.fabricmc:fabric-loom" or DNS resolution failures:

1. Check your internet connection
2. Verify you can access https://maven.fabricmc.net/ in a browser
3. Check firewall/proxy settings that might block Maven repository access
4. Try using a VPN if the repository is blocked in your region

### Gradle Daemon Issues

If Gradle behaves unexpectedly:

```bash
# Stop all Gradle daemons
./gradlew --stop

# Run with --no-daemon
./gradlew build --no-daemon
```

### Java Version Issues

Ensure you're using Java 21 or higher:

```bash
java -version
```

If you have multiple Java versions installed, set JAVA_HOME:

```bash
export JAVA_HOME=/path/to/java21
```

## Current Build Status

**Note**: Due to network connectivity restrictions in the current build environment, the automated build cannot access maven.fabricmc.net. The project structure and code are complete and ready to build in an environment with proper internet access to Fabric's Maven repository.

## Manual Build Alternative

If automated builds are not possible due to network restrictions, you can:

1. Download Fabric Loom manually from https://maven.fabricmc.net/
2. Set up a local Maven repository with the required dependencies
3. Modify build.gradle to point to your local repository

## Verification

After a successful build, verify the mod JAR:

```bash
# Check the JAR was created
ls -lh build/libs/

# Verify JAR contents  
jar tf build/libs/area-miner-1.0.0.jar | grep "AreaMiner.class"
```

## Installation

1. Copy `build/libs/area-miner-1.0.0.jar` to your Minecraft `mods` folder
2. Ensure you have Fabric Loader and Fabric API installed
3. Launch Minecraft 1.21.4 with the Fabric profile
