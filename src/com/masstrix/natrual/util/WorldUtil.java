package com.masstrix.natrual.util;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class WorldUtil {

    /**
     * Return all blocks within a certain radius of a location.
     *
     * @param location where the scan will be heald.
     * @param radius the scan radius for all blocks, max of 100.
     * @return all blocks in {@param radius} of {@param location}.
     */
    public static List<Block> getNearbyBlocks(Location location, int radius) {
        if (location == null || radius < 0 || radius > 100) return new ArrayList<>();
        List<Block> blocks = new ArrayList<>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
}
