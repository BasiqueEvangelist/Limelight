package io.wispforest.limelight.api.builtin.wiki;

import com.mojang.serialization.Codec;
import io.wispforest.endec.Endec;
import io.wispforest.limelight.impl.Limelight;
import io.wispforest.owo.serialization.CodecUtils;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

/**
 * A serializable wiki source type.
 *
 * @param endec the endec describing how to serialize this source
 * @param <T> the type of wiki source to use
 * @apiNote If you don't want to use endecs, you can convert a codec into one via {@link io.wispforest.owo.serialization.CodecUtils#toCodec(Endec)}
 */
public record WikiSourceType<T extends WikiSource>(Endec<T> endec) {
    public static final Registry<WikiSourceType<?>> REGISTRY = FabricRegistryBuilder.<WikiSourceType<?>>createSimple(RegistryKey.ofRegistry(Limelight.id("wiki_source_type"))).buildAndRegister();

    /**
     * @param codec the codec describing how to serialize this source.
     */
    public WikiSourceType(Codec<T> codec) {
        this(CodecUtils.toEndec(codec));
    }
}
