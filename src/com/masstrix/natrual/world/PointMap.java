package com.masstrix.natrual.world;

import com.masstrix.natrual.TemperatureValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * A PointMap handles all the temperature data for a single chunk. The chunk is split down by
 * 3 times to reduce data and increase performance. A point is put in a chunk every 3 blocks.
 * There are a total of 2125 points per chunk, each point is a <code>double</code> value.
 * <br>
 * Since <code>chunk points</code> are only 1/3 of a chunks actual scale.
 */
public class PointMap {

    static final int HEIGHT = 85, WIDTH = 5;

    private int x;
    private int z;

    private double[][][] points = new double[WIDTH + 1][HEIGHT + 1][WIDTH + 1]; // x y z == 2125 points

    public PointMap(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public PointMap(int x, int z, double[][][] data) {
        this.x = x;
        this.z = z;
        setData(data);
    }

    void updateLayers(WorldMap map, int y, int yTo) {
        // TODO
    }

    void setData(double[][][] data) {
        for (int y = 0; y < HEIGHT; y++) {
            for (int xi = 0; xi < WIDTH; xi++) {
                System.arraycopy(data[xi][y], 0, this.points[xi][y], 0, WIDTH);
            }
        }
    }

    /**
     * @return x location of the chunk.
     */
    public int getX() {
        return x;
    }

    /**
     * @return z location of the chunk.
     */
    public int getZ() {
        return z;
    }

    /**
     * Get a points modifier temperature value.
     *
     * @param x x position of the point in the chunk.
     * @param y y position of the point in the chunk.
     * @param z z position of the point in the chunk.
     * @return a points modifier value.
     */
    public double getPoint(int x, int y, int z) {
        return points[getPointRound(x)][getPointRound(y)][getPointRound(z)];
    }

    /**
     * Get a points modifier temperature value.
     *
     * @param x x position of the point in the chunk.
     * @param y y position of the point in the chunk.
     * @param z z position of the point in the chunk.
     * @return a points modifier value.
     */
    public double getPoint(double x, double y, double z) {
        return points
                [getPointRound(x)]
                [getPointRound(y)]
                [getPointRound(z)];
    }

    /**
     * Set a points modifier temperature value. The modifier should be a value of
     * 0.5
     *
     * @param x x position of the point in the chunk.
     * @param y y position of the point in the chunk.
     * @param z z position of the point in the chunk.
     * @param val the new temperature value for the point.
     */
    public void setPoint(int x, int y, int z, double val) {
        points[getPointRound(x)][getPointRound(y)][getPointRound(z)] = val;
    }

    /**
     * Return a data point location from a standard world location.
     *
     * @param val what the ordinal location value is.
     * @return the data point.
     */
    public static int getPointRound(int val) {
        return Math.round(val) / 3;
    }

    /**
     * Return a data point location from a standard world location.
     *
     * @param val what the ordinal location value is.
     * @return the data point.
     */
    public static int getPointRound(double val) {
        return getPointRound((int) (val < 0 ? Math.ceil(val) : Math.floor(val)));
    }

    /**
     * @return all points in the chunk.
     */
    public double[][][] getPoints() {
        return points;
    }

    /**
     * Return the calculated temperature of the 4 closest points to the given x y z location.
     *
     * @param x x world location.
     * @param y y world location.
     * @param z z world location.
     * @return mean value of closest 4 points.
     *
     * @deprecated cannot give a correct value when on edge of chunk. This will be relocated to {@link WorldMap}.
     */
    @Deprecated
    public double getWorldValue(int x, int y, int z) {
        int chunkX = x % 16;
        int chunkZ = z % 16;

        double totalTemp = 0;
        int points = 0;

        int pointX = getPointRound(chunkX);
        int pointZ = getPointRound(chunkZ);
        int pointY = getPointRound(y);

        for (int py = 0; py < 2; py++) {
            if (py + pointY > HEIGHT) continue;
            for (int px = 0; px < 2; px++) {
                if (px + pointX > WIDTH) continue;
                for (int pz = 0; pz < 2; pz++) {
                    if (pz + pointZ > WIDTH) continue;
                    totalTemp += this.points[px + pointX][py + pointY][pz + pointZ];
                    points++;

                    double
                            particleX = (pointX * 3) + (x - chunkX) + (px * 3),
                            particleY = (pointY * 3) + (py * 3),
                            particleZ = (pointZ * 3) + (z - chunkZ) + (pz * 3);

                    Bukkit.getWorld("world").spawnParticle(Particle.FLAME,
                            particleX, particleY, particleZ, 1, 0D, 0D, 0D, 0D);
                }
            }
        }

        return totalTemp / 4;
    }

    /**
     * @see #getWorldValue(int, int, int)
     *
     * @param x x world location.
     * @param y y world location.
     * @param z z world location.
     * @return mean value of closest 4 points.
     */
    public double getWorldValue(double x, double y, double z) {
        return getWorldValue((int) Math.round(x), (int) Math.round(y), (int) Math.round(z));
    }

    /**
     * @see #getWorldValue(int, int, int)
     *
     * @param location what world location to get temperature data for.
     * @return mean value of closest 4 points.
     */
    public double getWorldValue(Location location) {
        return getWorldValue(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Calculate the temperature of a location. This is an absolute location and will
     * gather data from a surrounding 5x5x5 area of the given location and return the
     * mean temperature.
     *
     * @param world what world the location is in.
     * @param x x location.
     * @param y y location.
     * @param z z location.
     * @return the mean temperature value for the given location and world.
     */
    public static double calcTemperature(World world, int x, int y, int z) {
        if (world == null) return 0D;
        int blocks = 0;
        double totalTemperature = 0;
        final int startLoc = -2, endLoc = 3;
        for (int secY = startLoc; secY < endLoc; secY++) {
            for (int secX = startLoc; secX < endLoc; secX++) {
                for (int secZ = startLoc; secZ < endLoc; secZ++) {
                    Block block = world.getBlockAt(x + secY, y + secX, z + secZ);
                    totalTemperature = TemperatureValue.getTemp(block);
                    blocks++;
                }
            }
        }
        return totalTemperature / blocks;
    }

    /**
     * @see #calcTemperature(World, int, int, int)
     *
     * @param location what location the temperature is being calculated for.
     * @return the mean temperature value for the given location and world.
     */
    public synchronized static double calcTemperature(Location location) {
        if (location == null || location.getWorld() == null) return 0D;
        else return calcTemperature(location.getWorld(),
                (int) Math.round(location.getX()),
                (int) Math.round(location.getY()),
                (int) Math.round(location.getZ()));
    }
}
