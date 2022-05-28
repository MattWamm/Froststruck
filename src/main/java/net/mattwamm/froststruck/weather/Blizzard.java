package net.mattwamm.froststruck.weather;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Random;

import static net.minecraft.client.render.WorldRenderer.getLightmapCoordinates;

public class Blizzard{
    public static boolean blizzardActive = false;
    private static float[] floats1;
    private static float[] floats2;
    private static final Identifier SNOW = new Identifier("textures/environment/snow.png");
    private static float blizzardGradientPrev;
    private static float blizzardGradient;
    public static void setBlizzardGradient(float rainGradient) {
        float f;
        blizzardGradientPrev = f = MathHelper.clamp(rainGradient, 0.0f, 1.0f);
        blizzardGradient = f;
    }
    public static float getBlizzardGradient(float f) {
        return MathHelper.lerp(f, blizzardGradientPrev, blizzardGradient);
    }
    public static void blizzardInit() {
        floats1 = new float[1024];
        floats2 = new float[1024];
        for(int i = 0; i < 32; ++i) {
            for(int j = 0; j < 32; ++j) {
                float f = (float)(j - 16);
                float g = (float)(i - 16);
                float h = MathHelper.sqrt(f * f + g * g);
                floats1[i << 5 | j] = -g / h;
                floats2[i << 5 | j] = f / h;
            }
        }
    }
    public static void renderBlizzard(LightmapTextureManager manager, float f, double d, double e, double g,int ticks) {
        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();
        float h = getBlizzardGradient(f);
        if (!(h <= 0.0F)) {
            manager.enable();
            World world = client.world;
            int i = MathHelper.floor(d);
            int j = MathHelper.floor(e);
            int k = MathHelper.floor(g);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.disableCull();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            int l = 5;
            if (MinecraftClient.isFancyGraphicsOrBetter()) {
                l = 10;
            }
            RenderSystem.depthMask(MinecraftClient.isFabulousGraphicsOrBetter());
            int m = -1;
            float n = (float) ticks + f;
            RenderSystem.setShader(GameRenderer::getParticleShader);            //add my own
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (int o = k - l; o <= k + l; ++o) {
                for (int p = i - l; p <= i + l; ++p) {
                    int q = (o - k + 16) * 32 + p - i + 16;
                    double r = (double) floats1[q] * 0.5;
                    double s = (double) floats2[q] * 0.5;
                    mutable.set(p, e, o);
                    int t = world.getTopY(Heightmap.Type.MOTION_BLOCKING, p, o);
                    int u = j - l;
                    int v = j + l;
                    if (u < t) {
                        u = t;
                    }
                    if (v < t) {
                        v = t;
                    }
                    int w = t;
                    if (t < j) {
                        w = j;
                    }
                    if (u != v) {
                        Random random = new Random(p * p * 3121 + p * 45238971 ^ o * o * 418711 + o * 13761);
                        mutable.set(p, u, o);
                        float y;
                        float ac;
                        if (m != 1) {
                            if (m >= 0) {
                                tessellator.draw();
                            }
                            m = 1;
                            RenderSystem.setShaderTexture(0, SNOW);
                            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
                        }
                        float ae = -((float) (ticks & 511) + f) / 512.0F;
                        y = (float) (random.nextDouble() + (double) n * 1 * (double) ((float) random.nextGaussian()));
                        float af = (float) (random.nextDouble() + (double) (n * (float) random.nextGaussian()) * 0.001);
                        double ag = (double) p + 0.5 - d;
                        double ah = (double) o + 0.5 - g;
                        ac = (float) Math.sqrt(ag * ag + ah * ah) / (float) l;
                        float ai = ((1.0F - ac * ac) * 0.3F + 0.5F) * h;
                        mutable.set(p, w, o);
                        int aj = getLightmapCoordinates(world, mutable);
                        int ak = aj >> 16 & '\uffff';
                        int al = aj & '\uffff';
                        int am = (ak * 3 + 240) / 4;
                        int an = (al * 3 + 240) / 4;



                        float fogStuff = 4;
                        float fogDistance = 5;


                        bufferBuilder.vertex((double) p - d - r + 0.5, (double) v - e, (double) o - g - s + 0.5).texture(0.0F + y, (float) u * 0.25F + ae + af).color(1.0F, 1.0F, 1.0F, ai).light(an, am).next();
                        bufferBuilder.vertex((double) p - d + r + 0.5, (double) v - e, (double) o - g + s + 0.5).texture(0.25F + y, (float) u * 0.25F + ae + af).color(1.0F, 1.0F, 1.0F, ai).light(an, am).next();
                        bufferBuilder.vertex((double) p - d + r + 0.5, (double) u - e, (double) o - g + s + 0.5).texture(0.25F + y, (float) v * 0.25F + ae + af).color(1.0F, 1.0F, 1.0F, ai).light(an, am).next();
                        bufferBuilder.vertex((double) p - d - r + 0.5, (double) u - e, (double) o - g - s + 0.5).texture(0.0F + y, (float) v * 0.25F + ae + af).color(1.0F, 1.0F, 1.0F, ai).light(an, am).next();
                        RenderSystem.setShaderFogStart(fogStuff);
                        RenderSystem.setShaderFogEnd(fogDistance);
                        RenderSystem.setShaderFogShape(FogShape.SPHERE);
                        BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_SKY, 3.0F, true);
                    }
                }
            }

            if (m >= 0) {
                tessellator.draw();
            }

            RenderSystem.enableCull();
            RenderSystem.disableBlend();
            manager.disable();
        }
    }


}
