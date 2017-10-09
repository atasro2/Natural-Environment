package com.masstrix.natrual.world;

import com.masstrix.natrual.Logger;
import com.masstrix.natrual.NaturalEnvironment;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class ChunkMapGenerator extends Thread {

    private int x, z;
    private WorldMap world = null;

    public ChunkMapGenerator(WorldMap world, int x, int z) {
        this.world = world;
        this.x = x;
        this.z = z;
        setName("ChunkMapGenerator #" + this.getId());
    }

    @Override
    public void run() {
        if (world == null) return;
        final World WORLD = Bukkit.getWorld(this.world.getName());
        if (WORLD == null) {
            if (NaturalEnvironment.getInstance().isDebugEnabled())
                Logger.info(String.format("Failed to fetch chunk %d %d in world %s", x, z, this.world));
            return;
        }

        long startTime = System.currentTimeMillis();
        double[][][] points = new double[PointMap.WIDTH][PointMap.HEIGHT][PointMap.WIDTH];

        for (int y = 0; y < PointMap.HEIGHT; y++) {
            for (int x = 0; x < PointMap.WIDTH; x++) {
                for (int z = 0; z < PointMap.WIDTH; z++) {
                    final int pX = x, pY = y, pZ = z;
                    Bukkit.getScheduler().callSyncMethod(NaturalEnvironment.getInstance(), () -> {
                        points[pX][pY][pZ] = PointMap.calcTemperature(WORLD, pX, pY, pZ);
                        return false;
                    });
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        this.world.setChunkData(x, z, points);
        long timeSpent = System.currentTimeMillis() - startTime;
        if (NaturalEnvironment.getInstance().isDebugEnabled())
            Logger.info(String.format("Generated chunk [%d %d %s] in %dms", x, z, world.getName(), timeSpent));
    }
}
