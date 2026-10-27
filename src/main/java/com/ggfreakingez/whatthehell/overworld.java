package com.ggfreakingez.whathehell;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.ChunkProvider;
import cn.nukkit.level.generator.noise.nukkit.SimplexNoise;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CustomOverworldGenerator extends ChunkProvider {

    private ChunkManager level;
    private SimplexNoise noise;
    private long seed;

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public String getName() {
        return "custom_overworld";
    }

    @Override
    public Map<String, Object> getSettings() {
        return Collections.emptyMap();
    }

    @Override
    public void init(ChunkManager level, cn.nukkit.level.generator.noise.nukkit.Random random) {
        this.level = level;
        this.seed = random.getSeed();
        this.noise = new SimplexNoise(random, 4, 0.5f, 0.005f);
    }

    @Override
    public void populateChunk(int chunkX, int chunkZ) {

    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {
        FullChunk chunk = this.level.getChunk(chunkX, chunkZ);
        int baseHeight = 64;
        int maxVariance = 32;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = (chunkX << 4) + x;
                int worldZ = (chunkZ << 4) + z;

                double noiseValue = this.noise.getNoise2D(worldX, worldZ);
                int surfaceHeight = baseHeight + (int) (noiseValue * maxVariance);

                surfaceHeight = Math.clamp(surfaceHeight, 1, 255);

                chunk.setBlockId(x, surfaceHeight, z, 2); 

                for (int y = surfaceHeight - 1; y > surfaceHeight - 4 && y > 0; y--) {
                    chunk.setBlockId(x, y, z, 3); 
                }

                for (int y = surfaceHeight - 4; y > 0; y--) {
                    chunk.setBlockId(x, y, z, 1); 
                }

                chunk.setBlockId(x, 0, z, 7); 

                for (int y = surfaceHeight + 1; y <= baseHeight; y++) {
                    chunk.setBlockId(x, y, z, 9); 
                }
            }
        }
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0, 65, 0);
    }

    @Override
    public List<Vector3f> getSpawnPoints() {
        return List.of(new Vector3f(0, 65, 0));
    }
}
