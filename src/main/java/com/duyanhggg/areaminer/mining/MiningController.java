package com.duyanhggg.areaminer.mining;

import com.duyanhggg.areaminer.AreaMiner;
import com.duyanhggg.areaminer.config.ConfigManager;
import com.duyanhggg.areaminer.network.NetworkHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class MiningController {
    private static final Map<UUID, MiningSession> sessions = new HashMap<>();
    private static int tickCounter = 0;
    
    public static void initialize() {
        ServerTickEvents.END_SERVER_TICK.register(MiningController::onServerTick);
    }
    
    private static void onServerTick(MinecraftServer server) {
        tickCounter++;
        
        // Process mining sessions
        Iterator<Map.Entry<UUID, MiningSession>> iterator = sessions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, MiningSession> entry = iterator.next();
            MiningSession session = entry.getValue();
            
            if (session.isActive()) {
                session.tick(server);
            }
            
            if (session.isComplete()) {
                iterator.remove();
            }
        }
    }
    
    public static MiningSession getSession(UUID playerId) {
        return sessions.get(playerId);
    }
    
    public static MiningSession createSession(ServerPlayerEntity player, MiningArea area, float speed) {
        UUID playerId = player.getUuid();
        MiningSession session = new MiningSession(player, area, speed);
        sessions.put(playerId, session);
        return session;
    }
    
    public static void stopSession(UUID playerId) {
        MiningSession session = sessions.get(playerId);
        if (session != null) {
            session.stop();
        }
    }
    
    public static void pauseSession(UUID playerId) {
        MiningSession session = sessions.get(playerId);
        if (session != null) {
            session.pause();
        }
    }
    
    public static void resumeSession(UUID playerId) {
        MiningSession session = sessions.get(playerId);
        if (session != null) {
            session.resume();
        }
    }
    
    public static void removeSession(UUID playerId) {
        sessions.remove(playerId);
    }
    
    public static boolean hasActiveSession(UUID playerId) {
        MiningSession session = sessions.get(playerId);
        return session != null && session.isActive();
    }
    
    public static class MiningSession {
        private final ServerPlayerEntity player;
        private final MiningArea area;
        private final float speed;
        private final List<BlockPos> blocksToMine;
        private int currentIndex;
        private boolean active;
        private boolean paused;
        private boolean complete;
        private int blocksMined;
        private long startTime;
        private float miningProgress;
        
        public MiningSession(ServerPlayerEntity player, MiningArea area, float speed) {
            this.player = player;
            this.area = area;
            this.speed = Math.max(0.1f, Math.min(10f, speed));
            this.blocksToMine = new ArrayList<>();
            this.currentIndex = 0;
            this.active = false;
            this.paused = false;
            this.complete = false;
            this.blocksMined = 0;
            this.startTime = System.currentTimeMillis();
            this.miningProgress = 0;
            
            // Build list of blocks to mine
            buildBlockList();
        }
        
        private void buildBlockList() {
            BlockPos min = area.getMinPos();
            BlockPos max = area.getMaxPos();
            
            int minX = Math.min(min.getX(), max.getX());
            int maxX = Math.max(min.getX(), max.getX());
            int minY = Math.min(min.getY(), max.getY());
            int maxY = Math.max(min.getY(), max.getY());
            int minZ = Math.min(min.getZ(), max.getZ());
            int maxZ = Math.max(min.getZ(), max.getZ());
            
            ServerWorld world = player.getServerWorld();
            
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();
                        
                        if (!state.isAir() && area.canMineBlock(block)) {
                            blocksToMine.add(pos);
                        }
                    }
                }
            }
        }
        
        public void start() {
            this.active = true;
            this.paused = false;
            AreaMiner.LOGGER.info("Mining session started for player {} with {} blocks to mine", 
                player.getName().getString(), blocksToMine.size());
        }
        
        public void stop() {
            this.active = false;
            this.complete = true;
            AreaMiner.LOGGER.info("Mining session stopped for player {}, mined {} blocks", 
                player.getName().getString(), blocksMined);
        }
        
        public void pause() {
            this.paused = true;
        }
        
        public void resume() {
            this.paused = false;
        }
        
        public void tick(MinecraftServer server) {
            if (!active || paused || complete) {
                return;
            }
            
            // Calculate how many blocks to mine this tick based on speed
            miningProgress += speed;
            int blocksThisTick = (int) miningProgress;
            miningProgress -= blocksThisTick;
            
            ServerWorld world = player.getServerWorld();
            
            for (int i = 0; i < blocksThisTick && currentIndex < blocksToMine.size(); i++) {
                BlockPos pos = blocksToMine.get(currentIndex);
                BlockState state = world.getBlockState(pos);
                
                if (!state.isAir()) {
                    // Break the block
                    world.breakBlock(pos, true, player);
                    blocksMined++;
                    
                    // Spawn particles
                    world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
                }
                
                currentIndex++;
            }
            
            // Check if mining is complete
            if (currentIndex >= blocksToMine.size()) {
                complete();
            }
            
            // Send progress update to client every 20 ticks
            if (tickCounter % 20 == 0) {
                NetworkHandler.sendMiningProgressToClient(player, getProgress());
            }
        }
        
        private void complete() {
            this.active = false;
            this.complete = true;
            
            // Play completion sound
            player.getServerWorld().playSound(
                null,
                player.getBlockPos(),
                SoundEvents.ENTITY_PLAYER_LEVELUP,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
            );
            
            AreaMiner.LOGGER.info("Mining session completed for player {}, mined {} blocks in {} ms", 
                player.getName().getString(), blocksMined, System.currentTimeMillis() - startTime);
        }
        
        public boolean isActive() {
            return active && !complete;
        }
        
        public boolean isPaused() {
            return paused;
        }
        
        public boolean isComplete() {
            return complete;
        }
        
        public int getBlocksMined() {
            return blocksMined;
        }
        
        public int getTotalBlocks() {
            return blocksToMine.size();
        }
        
        public float getProgress() {
            if (blocksToMine.isEmpty()) {
                return 1.0f;
            }
            return (float) blocksMined / blocksToMine.size();
        }
        
        public long getElapsedTime() {
            return System.currentTimeMillis() - startTime;
        }
        
        public MiningArea getArea() {
            return area;
        }
    }
}
