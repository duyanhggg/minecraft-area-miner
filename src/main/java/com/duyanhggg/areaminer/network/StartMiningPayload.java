package com.duyanhggg.areaminer.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Network packet payload for initiating mining operations in the Area Miner mod.
 * This packet is sent to start mining in a specified area.
 */
public class StartMiningPayload implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, StartMiningPayload> CODEC = 
        StreamCodec.of(StartMiningPayload::toNetwork, StartMiningPayload::new);
    
    public static final ResourceLocation ID = new ResourceLocation("areaminer", "start_mining");
    
    private final int x;
    private final int y;
    private final int z;
    private final int radius;
    private final boolean includeFluids;
    
    /**
     * Creates a new StartMiningPayload with the specified mining parameters.
     *
     * @param x the x-coordinate of the mining area center
     * @param y the y-coordinate of the mining area center
     * @param z the z-coordinate of the mining area center
     * @param radius the radius of the mining area
     * @param includeF fluids if true, mining will also extract fluids
     */
    public StartMiningPayload(int x, int y, int z, int radius, boolean includeF) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.includeF = includeF;
    }
    
    /**
     * Creates a StartMiningPayload from a network buffer.
     *
     * @param buf the network buffer to read from
     */
    public StartMiningPayload(FriendlyByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.radius = buf.readInt();
        this.includeF = buf.readBoolean();
    }
    
    /**
     * Writes this payload to a network buffer.
     *
     * @param buf the network buffer to write to
     */
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.radius);
        buf.writeBoolean(this.includeF);
    }
    
    @Override
    public ResourceLocation id() {
        return ID;
    }
    
    // Getters
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }
    
    public int getRadius() {
        return radius;
    }
    
    public boolean isIncludeFluids() {
        return includeF;
    }
}
