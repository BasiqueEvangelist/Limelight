package io.wispforest.limelight.impl;

import io.wispforest.owo.config.ui.ConfigScreen;
import io.wispforest.limelight.impl.config.LimelightConfig;
import io.wispforest.limelight.impl.config.LimelightConfigScreen;
import io.wispforest.limelight.impl.resource.CalculatorResourceLoader;
import io.wispforest.limelight.impl.builtin.wiki.WikiLoader;
import io.wispforest.limelight.impl.ui.LimelightScreen;
import io.wispforest.limelight.impl.util.LeastRecentlyUsedList;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class Limelight implements ClientModInitializer {
	public static final String MOD_ID = "limelight";

	public static KeyBinding OPEN_LIMELIGHT = new KeyBinding("key.limelight.open", GLFW.GLFW_KEY_LEFT_BRACKET, KeyBinding.MISC_CATEGORY);

	public static final LimelightConfig CONFIG = LimelightConfig.createAndLoad();

	public static final LeastRecentlyUsedList<String> ENTRY_USES = new LeastRecentlyUsedList<>(10);

	@Override
	public void onInitializeClient() {
		KeyBindingHelper.registerKeyBinding(OPEN_LIMELIGHT);

		ExtensionManager.init();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!OPEN_LIMELIGHT.wasPressed()) return;
			if (client.player == null) return;

			client.setScreen(new LimelightScreen());
		});

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(CalculatorResourceLoader.INSTANCE);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(WikiLoader.INSTANCE);

		ConfigScreen.registerProvider("limelight", LimelightConfigScreen::new);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}