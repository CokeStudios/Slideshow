package teaconmc.slides;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;

public final class ProjectorData {

    public static final class Entry {

        final DynamicTexture texture;
        final RenderType renderType;

        public Entry(NativeImage image, TextureManager manager) {
            this.texture = new DynamicTexture(image);
            this.renderType = RenderType.getText(manager.getDynamicTextureLocation("slide_show", this.texture));
        }
    }

    static final Cache<String, Entry> CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(20, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<String, Entry>() {
                @Override
                public void onRemoval(RemovalNotification<String, Entry> notification) {
                    notification.getValue().texture.close();
                }
            }).build();

    static final ExecutorService WORKER = Executors.newSingleThreadExecutor();

    public static RenderType getRenderType(String location, TextureManager manager) {
        Entry entry = CACHE.getIfPresent(location);
        if (entry == null) {
            WORKER.submit(() -> {
                try {
                    NativeImage image = NativeImage.read(new URL(location).openStream());
                    Minecraft.getInstance().deferTask(() -> CACHE.put(location, new Entry(image, manager)));
                } catch (Exception ignored) {
                    // maybe log this?
                }
            });
            return null;
        }
        return entry.renderType;
    }
    
}