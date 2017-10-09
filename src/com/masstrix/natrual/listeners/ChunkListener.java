package com.masstrix.natrual.listeners;

import com.masstrix.natrual.NaturalEnvironment;
import com.masstrix.natrual.world.WorldDataHandler;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener implements Listener {

    @EventHandler
    public void on(ChunkUnloadEvent event) {
        WorldDataHandler handler = NaturalEnvironment.getInstance().getChunkData();
        World world = event.getWorld();
        Chunk chunk = event.getChunk();
        handler.unloadChunk(world.getName(), chunk.getX(), chunk.getX());
    }
}
