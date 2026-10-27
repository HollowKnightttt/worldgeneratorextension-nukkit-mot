package com.ggfreakingez.whatthehell;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;

import java.util.Collections;
import java.util.Map;

public class FlatPerlinGenerator extends Generator {

    private FullChunk chunk;
    private final SimplexNoise noise = new SimplexNoise();

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public String getName() {
        return "flatperlin";
    }

    @Override
    public Map<String, Object> getSettings() {
        return Collections.emptyMap();
    }

    @Override
    public void init(FullChunk chunk, NukkitRandom random) {
        this.chunk = chunk;
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        int baseHeight = 64;
        int scale = 16;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = (chunkX << 4) + x;
                int worldZ = (chunkZ << 4) + z;
                
                double noiseValue = noise.eval(worldX / 100.0, worldZ / 100.0);
                int heightOffset = (int) (noiseValue * scale);
                int finalHeight = baseHeight + heightOffset;

                chunk.setBlockId(x, 0, z, Block.BEDROCK);
                for (int y = 1; y < finalHeight - 3; y++) {
                    chunk.setBlockId(x, y, z, Block.STONE);
                }
                for (int y = finalHeight - 3; y < finalHeight - 1; y++) {
                    chunk.setBlockId(x, y, z, Block.DIRT);
                }
                chunk.setBlockId(x, finalHeight - 1, z, Block.GRASS);
            }
        }
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0, 65, 0);
    }
}

class SimplexNoise {
    private static final int[] p = new int[512];
    static {
        int[] permutation = {151,160,137,91,90,15,
        131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
        190, 6,148,247,120,234,75,0,26,56,62,147,222,63,179,161, 30,22,58,215,166,165,
        114,241, 46,245,140,142,228,143,60,119,162,176,228,1,98,120,51,90,44,40,214,
        191,61,115,25,253,174,105,42,123,212,111,25,142,29,19,211,236,79,172,204,142,
        149,152,189,174,85,41,120,4,103,32,68,177,135,133,143,85,250,34,165,12,30,58,
        191,94,11,219,224,50,234,142,208,165,183,74,252,95,193,15,126,169,142,74,121,
        136,167,42,186,51,75,254,15,51,216,223,169,25,160,216,211,172,217,42,200,183,
        250,55,10,153,71,223,24,250,194,142,161,104,147,22,252,58,164,15,53,67,109,52,
        23,48,240,120,210,132,12,164,178,123,173,19,91,8,44,251,142,12,111,168,142,121,
        91,203,89,110,21,95,142,27,169,28,142,125,21,98,114,142,45,204,122,52,42,208,
        1,142,224,43,115,44,142,175,78,181,142,112,142,91,123,21,119,153,227,165,114,
        142,46,110,207,142,118,208,214,242,169,228,102,40,142,13,56,112,142,24,84,106,
        223,142,44,240,43,39,128,230,95,142,67,77,21,142,110,142,20,80,89,142,127,162,
        24,220,142,142,142,43,46,142,202,110,152,142,181,242,142,142,142,61,65,142,161,
        112,131,142,118,207,142,122,41,142,62,11,142,142,142,142,142,142,142,142,142};
        for (int i=0; i < 256 ; i++) {
            p[i] = permutation[i];
            p[256+i] = permutation[i];
        }
    }

    private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
    private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

    public double eval(double xin, double yin) {
        double s = (xin + yin) * F2;
        int i = (int) Math.floor(xin + s);
        int j = (int) Math.floor(yin + s);
        double t = (i + j) * G2;
        double X0 = i - t;
        double Y0 = j - t;
        double x0 = xin - X0;
        double y0 = yin - Y0;
        int i1, j1;
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }
        double x1 = x0 - i1 + G2;
        double y1 = y0 - j1 + G2;
        double x2 = x0 - 1.0 + 2.0 * G2;
        double y2 = y0 - 1.0 + 2.0 * G2;
        int ii = i & 255;
        int jj = j & 255;
        int gi0 = p[ii + p[jj]] % 12;
        int gi1 = p[ii + i1 + p[jj + j1]] % 12;
        int gi2 = p[ii + 1 + p[jj + 1]] % 12;
        double t0 = 0.5 - x0 * x0 - y0 * y0;
        double n0 = t0 < 0 ? 0.0 : t0 * t0 * t0 * t0 * grad(gi0, x0, y0);
        double t1 = 0.5 - x1 * x1 - y1 * y1;
        double n1 = t1 < 0 ? 0.0 : t1 * t1 * t1 * t1 * grad(gi1, x1, y1);
        double t2 = 0.5 - x2 * x2 - y2 * y2;
        double n2 = t2 < 0 ? 0.0 : t2 * t2 * t2 * t2 * grad(gi2, x2, y2);
        return 70.0 * (n0 + n1 + n2);
    }

    private static double grad(int hash, double x, double y) {
        int h = hash & 7;
        double u = h < 4 ? x : y;
        double v = h < 4 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? 2.0 * v : -2.0 * v);
    }
}
