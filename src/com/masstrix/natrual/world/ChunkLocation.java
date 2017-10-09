package com.masstrix.natrual.world;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class ChunkLocation {

    private int x, z;

    public ChunkLocation(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(x).append(z).toHashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof ChunkLocation) {
            ChunkLocation chunk = (ChunkLocation) o;
            return chunk.getX() == this.x && chunk.getZ() == this.z;
        }
        return false;
    }
}
