package com.duyanhggg.areaminer.mining;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MiningController - Manages mining sessions and block extraction
 * Handles session creation, block list building, and mining operations
 */
public class MiningController {
    
    private static MiningController instance;
    private final Map<UUID, MiningSession> activeSessions;
    private final BukkitScheduler scheduler;
    private final int maxSessionDuration; // in ticks
    private final int blockBatchSize;
    private final int maxBlocksPerSession;
    
    private MiningController(BukkitScheduler scheduler) {
        this.activeSessions = new ConcurrentHashMap<>();
        this.scheduler = scheduler;
        this.maxSessionDuration = 20 * 60 * 60; // 60 minutes in ticks
        this.blockBatchSize = 100; // Process blocks in batches
        this.maxBlocksPerSession = 10000; // Maximum blocks per session
    }
    
    /**
     * Get singleton instance of MiningController
     */
    public static MiningController getInstance(BukkitScheduler scheduler) {
        if (instance == null) {
            instance = new MiningController(scheduler);
        }
        return instance;
    }
    
    /**
     * Get singleton instance (requires prior initialization)
     */
    public static MiningController getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MiningController not initialized. Call getInstance(scheduler) first.");
        }
        return instance;
    }
    
    /**
     * Create a new mining session for a player
     */
    public MiningSession createSession(Player player, Location corner1, Location corner2) {
        if (activeSessions.containsKey(player.getUniqueId())) {
            return activeSessions.get(player.getUniqueId());
        }
        
        MiningSession session = new MiningSession(
            player.getUniqueId(),
            player.getName(),
            corner1,
            corner2,
            System.currentTimeMillis()
        );
        
        activeSessions.put(player.getUniqueId(), session);
        return session;
    }
    
    /**
     * Get active session for a player
     */
    public MiningSession getSession(UUID playerUuid) {
        return activeSessions.get(playerUuid);
    }
    
    /**
     * Get active session for a player by name
     */
    public MiningSession getSession(String playerName) {
        return activeSessions.values().stream()
            .filter(session -> session.getPlayerName().equalsIgnoreCase(playerName))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * End a mining session
     */
    public void endSession(UUID playerUuid) {
        MiningSession session = activeSessions.remove(playerUuid);
        if (session != null) {
            session.cleanup();
        }
    }
    
    /**
     * End a mining session by player name
     */
    public void endSession(String playerName) {
        activeSessions.entrySet().removeIf(entry -> 
            entry.getValue().getPlayerName().equalsIgnoreCase(playerName)
        );
    }
    
    /**
     * Check if player has an active session
     */
    public boolean hasActiveSession(UUID playerUuid) {
        return activeSessions.containsKey(playerUuid);
    }
    
    /**
     * Get all active sessions
     */
    public Collection<MiningSession> getAllActiveSessions() {
        return new ArrayList<>(activeSessions.values());
    }
    
    /**
     * Build a list of mineable blocks within the specified region
     */
    public List<Block> buildBlockList(Location corner1, Location corner2, Set<Material> minableMaterials) {
        return buildBlockList(corner1, corner2, minableMaterials, maxBlocksPerSession);
    }
    
    /**
     * Build a list of mineable blocks within the specified region with block limit
     */
    public List<Block> buildBlockList(Location corner1, Location corner2, 
                                      Set<Material> minableMaterials, int maxBlocks) {
        List<Block> blockList = new ArrayList<>();
        
        if (corner1.getWorld() == null || corner2.getWorld() == null) {
            return blockList;
        }
        
        if (!corner1.getWorld().equals(corner2.getWorld())) {
            return blockList;
        }
        
        // Normalize coordinates
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
        
        // Calculate total volume
        long volume = (long) (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        
        if (volume > 1000000) { // 1 million blocks limit
            Bukkit.getLogger().warning("Area too large for mining: " + volume + " blocks");
            return blockList;
        }
        
        // Iterate through blocks and collect mineable ones
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (blockList.size() >= maxBlocks) {
                        return blockList;
                    }
                    
                    Block block = corner1.getWorld().getBlockAt(x, y, z);
                    
                    if (isMineable(block, minableMaterials)) {
                        blockList.add(block);
                    }
                }
            }
        }
        
        return blockList;
    }
    
    /**
     * Check if a block is mineable based on material set
     */
    private boolean isMineable(Block block, Set<Material> minableMaterials) {
        if (block == null || block.getType() == Material.AIR) {
            return false;
        }
        
        if (minableMaterials.isEmpty()) {
            return false;
        }
        
        return minableMaterials.contains(block.getType());
    }
    
    /**
     * Start mining blocks in a session asynchronously
     */
    public void startMiningSession(MiningSession session, Set<Material> minableMaterials) {
        Player player = Bukkit.getPlayer(session.getPlayerUuid());
        if (player == null) {
            endSession(session.getPlayerUuid());
            return;
        }
        
        // Build block list
        List<Block> blockList = buildBlockList(
            session.getCorner1(),
            session.getCorner2(),
            minableMaterials
        );
        
        session.setTotalBlocks(blockList.size());
        
        // Process blocks in batches
        processMiningBatches(session, blockList, 0);
    }
    
    /**
     * Process mining blocks in batches to avoid server lag
     */
    private void processMiningBatches(MiningSession session, List<Block> blockList, int startIndex) {
        if (startIndex >= blockList.size()) {
            session.markAsCompleted();
            return;
        }
        
        int endIndex = Math.min(startIndex + blockBatchSize, blockList.size());
        
        // Process batch
        for (int i = startIndex; i < endIndex; i++) {
            Block block = blockList.get(i);
            if (block != null && block.getType() != Material.AIR) {
                block.setType(Material.AIR);
                session.incrementBlocksMined();
            }
        }
        
        // Schedule next batch
        if (endIndex < blockList.size()) {
            final int nextStart = endIndex;
            scheduler.scheduleSyncDelayedTask(
                Bukkit.getPluginManager().getPlugins()[0],
                () -> processMiningBatches(session, blockList, nextStart),
                1L // 1 tick delay between batches
            );
        } else {
            session.markAsCompleted();
        }
    }
    
    /**
     * Get statistics for a session
     */
    public Map<String, Object> getSessionStats(UUID playerUuid) {
        MiningSession session = getSession(playerUuid);
        if (session == null) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("player", session.getPlayerName());
        stats.put("totalBlocks", session.getTotalBlocks());
        stats.put("blocksMined", session.getBlocksMined());
        stats.put("progress", String.format("%.2f%%", session.getProgress()));
        stats.put("isCompleted", session.isCompleted());
        stats.put("startTime", new Date(session.getStartTime()));
        stats.put("duration", System.currentTimeMillis() - session.getStartTime());
        
        return stats;
    }
    
    /**
     * Clean up expired sessions
     */
    public void cleanupExpiredSessions() {
        long currentTime = System.currentTimeMillis();
        activeSessions.entrySet().removeIf(entry -> {
            MiningSession session = entry.getValue();
            long sessionAge = currentTime - session.getStartTime();
            return sessionAge > (maxSessionDuration * 50); // Convert ticks to milliseconds
        });
    }
    
    /**
     * Cleanup all sessions
     */
    public void cleanupAll() {
        activeSessions.values().forEach(MiningSession::cleanup);
        activeSessions.clear();
    }
    
    /**
     * Get session count
     */
    public int getActiveSessionCount() {
        return activeSessions.size();
    }
    
    /**
     * Inner class representing a mining session
     */
    public static class MiningSession {
        private final UUID playerUuid;
        private final String playerName;
        private final Location corner1;
        private final Location corner2;
        private final long startTime;
        private volatile int totalBlocks;
        private volatile int blocksMined;
        private volatile boolean completed;
        private final Map<String, Object> metadata;
        
        public MiningSession(UUID playerUuid, String playerName, Location corner1, 
                           Location corner2, long startTime) {
            this.playerUuid = playerUuid;
            this.playerName = playerName;
            this.corner1 = corner1.clone();
            this.corner2 = corner2.clone();
            this.startTime = startTime;
            this.totalBlocks = 0;
            this.blocksMined = 0;
            this.completed = false;
            this.metadata = new ConcurrentHashMap<>();
        }
        
        public UUID getPlayerUuid() {
            return playerUuid;
        }
        
        public String getPlayerName() {
            return playerName;
        }
        
        public Location getCorner1() {
            return corner1;
        }
        
        public Location getCorner2() {
            return corner2;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public int getTotalBlocks() {
            return totalBlocks;
        }
        
        public void setTotalBlocks(int totalBlocks) {
            this.totalBlocks = totalBlocks;
        }
        
        public int getBlocksMined() {
            return blocksMined;
        }
        
        public void incrementBlocksMined() {
            this.blocksMined++;
        }
        
        public void addBlocksMined(int count) {
            this.blocksMined += count;
        }
        
        public double getProgress() {
            if (totalBlocks == 0) {
                return 0.0;
            }
            return (blocksMined / (double) totalBlocks) * 100.0;
        }
        
        public boolean isCompleted() {
            return completed;
        }
        
        public void markAsCompleted() {
            this.completed = true;
        }
        
        public void setMetadata(String key, Object value) {
            metadata.put(key, value);
        }
        
        public Object getMetadata(String key) {
            return metadata.get(key);
        }
        
        public void cleanup() {
            metadata.clear();
        }
        
        @Override
        public String toString() {
            return String.format("MiningSession{player=%s, blocks=%d/%d, progress=%.2f%%}",
                playerName, blocksMined, totalBlocks, getProgress());
        }
    }
}
