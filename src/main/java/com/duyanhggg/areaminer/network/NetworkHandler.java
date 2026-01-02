package com.duyanhggg.areaminer.network;

import com.duyanhggg.areaminer.AreaMiner;
import com.duyanhggg.areaminer.mining.MiningArea;
import com.duyanhggg.areaminer.mining.MiningController;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class NetworkHandler {
    public static final Identifier START_MINING_ID = Identifier.of(AreaMiner.MOD_ID, "start_mining");
    public static final Identifier STOP_MINING_ID = Identifier.of(AreaMiner.MOD_ID, "stop_mining");
    public static final Identifier PAUSE_MINING_ID = Identifier.of(AreaMiner.MOD_ID, "pause_mining");
    public static final Identifier RESUME_MINING_ID = Identifier.of(AreaMiner.MOD_ID, "resume_mining");
    public static final Identifier MINING_PROGRESS_ID = Identifier.of(AreaMiner.MOD_ID, "mining_progress");
    
    public static void registerServerPackets() {
        // Register payload types
        PayloadTypeRegistry.playC2S().register(StartMiningPayload.ID, StartMiningPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(StopMiningPayload.ID, StopMiningPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PauseMiningPayload.ID, PauseMiningPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ResumeMiningPayload.ID, ResumeMiningPayload.CODEC);
        
        // Register server receivers
        ServerPlayNetworking.registerGlobalReceiver(StartMiningPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            
            context.server().execute(() -> {
                // Check permissions
                if (!player.hasPermissionLevel(2)) {
                    AreaMiner.LOGGER.warn("Player {} attempted to start mining without permission", player.getName().getString());
                    return;
                }
                
                MiningArea area = new MiningArea(
                    payload.minX(), payload.minY(), payload.minZ(),
                    payload.maxX(), payload.maxY(), payload.maxZ()
                );
                
                MiningController.MiningSession session = MiningController.createSession(player, area, payload.speed());
                session.start();
                
                AreaMiner.LOGGER.info("Player {} started mining area", player.getName().getString());
            });
        });
        
        ServerPlayNetworking.registerGlobalReceiver(StopMiningPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            context.server().execute(() -> {
                MiningController.stopSession(player.getUuid());
                AreaMiner.LOGGER.info("Player {} stopped mining", player.getName().getString());
            });
        });
        
        ServerPlayNetworking.registerGlobalReceiver(PauseMiningPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            context.server().execute(() -> {
                MiningController.pauseSession(player.getUuid());
            });
        });
        
        ServerPlayNetworking.registerGlobalReceiver(ResumeMiningPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            context.server().execute(() -> {
                MiningController.resumeSession(player.getUuid());
            });
        });
    }
    
    public static void registerClientPackets() {
        // Register payload types for client
        PayloadTypeRegistry.playS2C().register(MiningProgressPayload.ID, MiningProgressPayload.CODEC);
        
        // Register client receivers
        ClientPlayNetworking.registerGlobalReceiver(MiningProgressPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                // Update client-side progress display
                // This will be handled by the GUI
            });
        });
    }
    
    public static void sendMiningProgressToClient(ServerPlayerEntity player, float progress) {
        ServerPlayNetworking.send(player, new MiningProgressPayload(progress));
    }
    
    // Payload records
    public record StartMiningPayload(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, float speed) implements CustomPayload {
        public static final CustomPayload.Id<StartMiningPayload> ID = new CustomPayload.Id<>(START_MINING_ID);
        public static final PacketCodec<RegistryByteBuf, StartMiningPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, StartMiningPayload::minX,
            PacketCodecs.INTEGER, StartMiningPayload::minY,
            PacketCodecs.INTEGER, StartMiningPayload::minZ,
            PacketCodecs.INTEGER, StartMiningPayload::maxX,
            PacketCodecs.INTEGER, StartMiningPayload::maxY,
            PacketCodecs.INTEGER, StartMiningPayload::maxZ,
            PacketCodecs.FLOAT, StartMiningPayload::speed,
            StartMiningPayload::new
        );
        
        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
    
    public record StopMiningPayload() implements CustomPayload {
        public static final CustomPayload.Id<StopMiningPayload> ID = new CustomPayload.Id<>(STOP_MINING_ID);
        public static final PacketCodec<RegistryByteBuf, StopMiningPayload> CODEC = PacketCodec.unit(new StopMiningPayload());
        
        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
    
    public record PauseMiningPayload() implements CustomPayload {
        public static final CustomPayload.Id<PauseMiningPayload> ID = new CustomPayload.Id<>(PAUSE_MINING_ID);
        public static final PacketCodec<RegistryByteBuf, PauseMiningPayload> CODEC = PacketCodec.unit(new PauseMiningPayload());
        
        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
    
    public record ResumeMiningPayload() implements CustomPayload {
        public static final CustomPayload.Id<ResumeMiningPayload> ID = new CustomPayload.Id<>(RESUME_MINING_ID);
        public static final PacketCodec<RegistryByteBuf, ResumeMiningPayload> CODEC = PacketCodec.unit(new ResumeMiningPayload());
        
        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
    
    public record MiningProgressPayload(float progress) implements CustomPayload {
        public static final CustomPayload.Id<MiningProgressPayload> ID = new CustomPayload.Id<>(MINING_PROGRESS_ID);
        public static final PacketCodec<RegistryByteBuf, MiningProgressPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, MiningProgressPayload::progress,
            MiningProgressPayload::new
        );
        
        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
