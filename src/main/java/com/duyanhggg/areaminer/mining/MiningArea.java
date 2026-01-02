package com.duyanhggg.areaminer.mining;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MiningArea represents a defined rectangular mining region in a Minecraft world.
 * It manages the boundaries, mining operations, and state of a mining area.
 */
public class MiningArea {
    
    private String id;
    private String name;
    private World world;
    private Location cornerOne;
    private Location cornerTwo;
    private UUID owner;
    private Set<UUID> members;
    private boolean enabled;
    private long createdAt;
    private long lastModified;
    private Map<String, Object> metadata;
    
    /**
     * Constructor for creating a new MiningArea
     */
    public MiningArea(String id, String name, World world, Location cornerOne, Location cornerTwo, UUID owner) {
        this.id = id;
        this.name = name;
        this.world = world;
        this.cornerOne = cornerOne.clone();
        this.cornerTwo = cornerTwo.clone();
        this.owner = owner;
        this.members = new HashSet<>();
        this.members.add(owner);
        this.enabled = true;
        this.createdAt = System.currentTimeMillis();
        this.lastModified = System.currentTimeMillis();
        this.metadata = new HashMap<>();
    }
    
    /**
     * Get the unique identifier of this mining area
     */
    public String getId() {
        return id;
    }
    
    /**
     * Set the unique identifier of this mining area
     */
    public void setId(String id) {
        this.id = id;
        updateModificationTime();
    }
    
    /**
     * Get the name of this mining area
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the name of this mining area
     */
    public void setName(String name) {
        this.name = name;
        updateModificationTime();
    }
    
    /**
     * Get the world this mining area is in
     */
    public World getWorld() {
        return world;
    }
    
    /**
     * Set the world this mining area is in
     */
    public void setWorld(World world) {
        this.world = world;
        updateModificationTime();
    }
    
    /**
     * Get the first corner location
     */
    public Location getCornerOne() {
        return cornerOne.clone();
    }
    
    /**
     * Set the first corner location
     */
    public void setCornerOne(Location cornerOne) {
        this.cornerOne = cornerOne.clone();
        updateModificationTime();
    }
    
    /**
     * Get the second corner location
     */
    public Location getCornerTwo() {
        return cornerTwo.clone();
    }
    
    /**
     * Set the second corner location
     */
    public void setCornerTwo(Location cornerTwo) {
        this.cornerTwo = cornerTwo.clone();
        updateModificationTime();
    }
    
    /**
     * Get the owner UUID of this mining area
     */
    public UUID getOwner() {
        return owner;
    }
    
    /**
     * Set the owner UUID of this mining area
     */
    public void setOwner(UUID owner) {
        this.owner = owner;
        if (!this.members.contains(owner)) {
            this.members.add(owner);
        }
        updateModificationTime();
    }
    
    /**
     * Get all members with access to this mining area
     */
    public Set<UUID> getMembers() {
        return new HashSet<>(members);
    }
    
    /**
     * Add a member to this mining area
     */
    public void addMember(UUID member) {
        this.members.add(member);
        updateModificationTime();
    }
    
    /**
     * Remove a member from this mining area
     */
    public void removeMember(UUID member) {
        if (!member.equals(owner)) {
            this.members.remove(member);
            updateModificationTime();
        }
    }
    
    /**
     * Check if a UUID is a member of this mining area
     */
    public boolean isMember(UUID member) {
        return this.members.contains(member);
    }
    
    /**
     * Check if this mining area is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Set whether this mining area is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        updateModificationTime();
    }
    
    /**
     * Get the creation timestamp in milliseconds
     */
    public long getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Get the last modification timestamp in milliseconds
     */
    public long getLastModified() {
        return lastModified;
    }
    
    /**
     * Update the last modification timestamp to current time
     */
    private void updateModificationTime() {
        this.lastModified = System.currentTimeMillis();
    }
    
    /**
     * Get metadata value by key
     */
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
    
    /**
     * Set metadata value by key
     */
    public void setMetadata(String key, Object value) {
        this.metadata.put(key, value);
        updateModificationTime();
    }
    
    /**
     * Remove metadata value by key
     */
    public void removeMetadata(String key) {
        this.metadata.remove(key);
        updateModificationTime();
    }
    
    /**
     * Get all metadata
     */
    public Map<String, Object> getAllMetadata() {
        return new HashMap<>(metadata);
    }
    
    /**
     * Clear all metadata
     */
    public void clearMetadata() {
        this.metadata.clear();
        updateModificationTime();
    }
    
    /**
     * Check if a location is within this mining area
     */
    public boolean isLocationWithin(Location location) {
        if (location == null || location.getWorld() != this.world) {
            return false;
        }
        
        int minX = Math.min(cornerOne.getBlockX(), cornerTwo.getBlockX());
        int maxX = Math.max(cornerOne.getBlockX(), cornerTwo.getBlockX());
        int minY = Math.min(cornerOne.getBlockY(), cornerTwo.getBlockY());
        int maxY = Math.max(cornerOne.getBlockY(), cornerTwo.getBlockY());
        int minZ = Math.min(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
        int maxZ = Math.max(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
        
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }
    
    /**
     * Check if a block is within this mining area
     */
    public boolean isBlockWithin(Block block) {
        return block != null && isLocationWithin(block.getLocation());
    }
    
    /**
     * Check if a player is within this mining area
     */
    public boolean isPlayerWithin(Player player) {
        return player != null && isLocationWithin(player.getLocation());
    }
    
    /**
     * Get all blocks within this mining area
     */
    public List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<>();
        
        if (world == null) {
            return blocks;
        }
        
        int minX = Math.min(cornerOne.getBlockX(), cornerTwo.getBlockX());
        int maxX = Math.max(cornerOne.getBlockX(), cornerTwo.getBlockX());
        int minY = Math.min(cornerOne.getBlockY(), cornerTwo.getBlockY());
        int maxY = Math.max(cornerOne.getBlockY(), cornerTwo.getBlockY());
        int minZ = Math.min(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
        int maxZ = Math.max(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
        
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        
        return blocks;
    }
    
    /**
     * Get blocks of a specific type within this mining area
     */
    public List<Block> getBlocksByType(String materialType) {
        if (world == null) {
            return new ArrayList<>();
        }
        
        return getAllBlocks().stream()
                .filter(block -> block.getType().name().equalsIgnoreCase(materialType))
                .collect(Collectors.toList());
    }
    
    /**
     * Get the volume of this mining area in cubic blocks
     */
    public long getVolume() {
        int width = Math.abs(cornerTwo.getBlockX() - cornerOne.getBlockX()) + 1;
        int height = Math.abs(cornerTwo.getBlockY() - cornerOne.getBlockY()) + 1;
        int depth = Math.abs(cornerTwo.getBlockZ() - cornerOne.getBlockZ()) + 1;
        
        return (long) width * height * depth;
    }
    
    /**
     * Get the center location of this mining area
     */
    public Location getCenter() {
        int centerX = (cornerOne.getBlockX() + cornerTwo.getBlockX()) / 2;
        int centerY = (cornerOne.getBlockY() + cornerTwo.getBlockY()) / 2;
        int centerZ = (cornerOne.getBlockZ() + cornerTwo.getBlockZ()) / 2;
        
        return new Location(world, centerX, centerY, centerZ);
    }
    
    /**
     * Get the minimum corner (with smallest coordinates)
     */
    public Location getMinimumCorner() {
        int minX = Math.min(cornerOne.getBlockX(), cornerTwo.getBlockX());
        int minY = Math.min(cornerOne.getBlockY(), cornerTwo.getBlockY());
        int minZ = Math.min(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
        
        return new Location(world, minX, minY, minZ);
    }
    
    /**
     * Get the maximum corner (with largest coordinates)
     */
    public Location getMaximumCorner() {
        int maxX = Math.max(cornerOne.getBlockX(), cornerTwo.getBlockX());
        int maxY = Math.max(cornerOne.getBlockY(), cornerTwo.getBlockY());
        int maxZ = Math.max(cornerOne.getBlockZ(), cornerTwo.getBlockZ());
        
        return new Location(world, maxX, maxY, maxZ);
    }
    
    /**
     * Get the size vector of this mining area
     */
    public Vector getSize() {
        int width = Math.abs(cornerTwo.getBlockX() - cornerOne.getBlockX()) + 1;
        int height = Math.abs(cornerTwo.getBlockY() - cornerOne.getBlockY()) + 1;
        int depth = Math.abs(cornerTwo.getBlockZ() - cornerOne.getBlockZ()) + 1;
        
        return new Vector(width, height, depth);
    }
    
    /**
     * Expand the mining area by a given amount in all directions
     */
    public void expand(int amount) {
        Location min = getMinimumCorner();
        Location max = getMaximumCorner();
        
        min.subtract(amount, amount, amount);
        max.add(amount, amount, amount);
        
        this.cornerOne = min;
        this.cornerTwo = max;
        updateModificationTime();
    }
    
    /**
     * Contract the mining area by a given amount in all directions
     */
    public void contract(int amount) {
        expand(-amount);
    }
    
    /**
     * Move the mining area to a new location
     */
    public void move(Vector offset) {
        this.cornerOne.add(offset);
        this.cornerTwo.add(offset);
        updateModificationTime();
    }
    
    /**
     * Check if two mining areas overlap
     */
    public boolean overlaps(MiningArea other) {
        if (other == null || !this.world.equals(other.world)) {
            return false;
        }
        
        Location thisMin = getMinimumCorner();
        Location thisMax = getMaximumCorner();
        Location otherMin = other.getMinimumCorner();
        Location otherMax = other.getMaximumCorner();
        
        return thisMin.getBlockX() <= otherMax.getBlockX() &&
                thisMax.getBlockX() >= otherMin.getBlockX() &&
                thisMin.getBlockY() <= otherMax.getBlockY() &&
                thisMax.getBlockY() >= otherMin.getBlockY() &&
                thisMin.getBlockZ() <= otherMax.getBlockZ() &&
                thisMax.getBlockZ() >= otherMin.getBlockZ();
    }
    
    /**
     * Create a deep copy of this mining area
     */
    public MiningArea copy() {
        MiningArea copy = new MiningArea(
                this.id + "_copy",
                this.name + " (Copy)",
                this.world,
                this.cornerOne.clone(),
                this.cornerTwo.clone(),
                this.owner
        );
        
        // Copy all members
        this.members.forEach(copy::addMember);
        
        // Copy settings
        copy.enabled = this.enabled;
        copy.createdAt = this.createdAt;
        copy.lastModified = System.currentTimeMillis();
        
        // Deep copy metadata
        this.metadata.forEach((key, value) -> {
            if (value instanceof String) {
                copy.setMetadata(key, ((String) value));
            } else if (value instanceof Number) {
                copy.setMetadata(key, value);
            } else if (value instanceof Boolean) {
                copy.setMetadata(key, value);
            } else if (value instanceof List<?>) {
                copy.setMetadata(key, new ArrayList<>((List<?>) value));
            } else if (value instanceof Map<?, ?>) {
                copy.setMetadata(key, new HashMap<>((Map<?, ?>) value));
            } else {
                copy.setMetadata(key, value);
            }
        });
        
        return copy;
    }
    
    /**
     * Convert this mining area to a string representation
     */
    @Override
    public String toString() {
        return String.format(
                "MiningArea{id='%s', name='%s', world='%s', corner1=%s, corner2=%s, owner=%s, enabled=%b, members=%d}",
                id, name, world != null ? world.getName() : "null",
                cornerOne, cornerTwo, owner, enabled, members.size()
        );
    }
    
    /**
     * Compare two mining areas for equality
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        MiningArea other = (MiningArea) obj;
        return Objects.equals(this.id, other.id) &&
                Objects.equals(this.world, other.world) &&
                Objects.equals(this.cornerOne, other.cornerOne) &&
                Objects.equals(this.cornerTwo, other.cornerTwo);
    }
    
    /**
     * Get the hash code for this mining area
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, world, cornerOne, cornerTwo);
    }
}
