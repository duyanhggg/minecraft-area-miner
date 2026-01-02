package com.duyanhggg.areaminer.mining;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import java.util.HashSet;
import java.util.Set;

public class MiningArea {
    private BlockPos minPos;
    private BlockPos maxPos;
    private Set<Block> whitelist;
    private Set<Block> blacklist;
    private boolean useWhitelist;
    
    public MiningArea(BlockPos minPos, BlockPos maxPos) {
        this.minPos = minPos;
        this.maxPos = maxPos;
        this.whitelist = new HashSet<>();
        this.blacklist = new HashSet<>();
        this.useWhitelist = false;
    }
    
    public MiningArea(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ));
    }
    
    public BlockPos getMinPos() {
        return minPos;
    }
    
    public void setMinPos(BlockPos minPos) {
        this.minPos = minPos;
    }
    
    public BlockPos getMaxPos() {
        return maxPos;
    }
    
    public void setMaxPos(BlockPos maxPos) {
        this.maxPos = maxPos;
    }
    
    public void setCoordinates(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        this.minPos = new BlockPos(minX, minY, minZ);
        this.maxPos = new BlockPos(maxX, maxY, maxZ);
    }
    
    public int getWidth() {
        return Math.abs(maxPos.getX() - minPos.getX()) + 1;
    }
    
    public int getHeight() {
        return Math.abs(maxPos.getY() - minPos.getY()) + 1;
    }
    
    public int getDepth() {
        return Math.abs(maxPos.getZ() - minPos.getZ()) + 1;
    }
    
    public int getTotalBlocks() {
        return getWidth() * getHeight() * getDepth();
    }
    
    public boolean contains(BlockPos pos) {
        return pos.getX() >= Math.min(minPos.getX(), maxPos.getX()) &&
               pos.getX() <= Math.max(minPos.getX(), maxPos.getX()) &&
               pos.getY() >= Math.min(minPos.getY(), maxPos.getY()) &&
               pos.getY() <= Math.max(minPos.getY(), maxPos.getY()) &&
               pos.getZ() >= Math.min(minPos.getZ(), maxPos.getZ()) &&
               pos.getZ() <= Math.max(minPos.getZ(), maxPos.getZ());
    }
    
    public Set<Block> getWhitelist() {
        return whitelist;
    }
    
    public void addToWhitelist(Block block) {
        whitelist.add(block);
    }
    
    public void removeFromWhitelist(Block block) {
        whitelist.remove(block);
    }
    
    public void clearWhitelist() {
        whitelist.clear();
    }
    
    public Set<Block> getBlacklist() {
        return blacklist;
    }
    
    public void addToBlacklist(Block block) {
        blacklist.add(block);
    }
    
    public void removeFromBlacklist(Block block) {
        blacklist.remove(block);
    }
    
    public void clearBlacklist() {
        blacklist.clear();
    }
    
    public boolean isUseWhitelist() {
        return useWhitelist;
    }
    
    public void setUseWhitelist(boolean useWhitelist) {
        this.useWhitelist = useWhitelist;
    }
    
    public boolean canMineBlock(Block block) {
        if (useWhitelist) {
            return whitelist.isEmpty() || whitelist.contains(block);
        } else {
            return !blacklist.contains(block);
        }
    }
    
    public MiningArea copy() {
        MiningArea copy = new MiningArea(minPos, maxPos);
        copy.whitelist = new HashSet<>(this.whitelist);
        copy.blacklist = new HashSet<>(this.blacklist);
        copy.useWhitelist = this.useWhitelist;
        return copy;
    }
}
