package me.basiqueevangelist.limelight.impl.ui;

import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.SmallCheckboxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.Insets;
import io.wispforest.owo.ui.core.Sizing;
import io.wispforest.owo.ui.core.Surface;
import me.basiqueevangelist.limelight.api.action.SetSearchTextAction;
import me.basiqueevangelist.limelight.api.action.ToggleAction;
import me.basiqueevangelist.limelight.api.entry.ResultEntry;
import me.basiqueevangelist.limelight.api.action.InvokeAction;
import me.basiqueevangelist.limelight.impl.Limelight;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class ResultEntryComponent extends FlowLayout {
    private final LimelightScreen screen;
    private final ResultEntry entry;
    private final @Nullable SmallCheckboxComponent toggleBox;

    public ResultEntryComponent(LimelightScreen screen, ResultEntry entry) {
        super(Sizing.fill(), Sizing.content(), Algorithm.HORIZONTAL);
        this.screen = screen;
        this.entry = entry;

        padding(Insets.vertical(4));

        var moduleLabel = Components.label(Text.empty()
            .append(entry.module().name())
            .append(" ")
            .formatted(Formatting.DARK_GRAY));

        List<Text> tooltip = new ArrayList<>();

        tooltip.add(entry.module().name().copy().formatted(Formatting.BOLD));

        var desc = entry.module().description();
        if (desc != null) {
            tooltip.add(desc);
        }

        if (MinecraftClient.getInstance().options.advancedItemTooltips) {
            tooltip.add(Text.literal(entry.module().id().toString()).formatted(Formatting.DARK_GRAY));
        }

        moduleLabel.tooltip(tooltip);

        child(moduleLabel);

        child(Components.label(Text.empty()
            .append(entry.text())
            .formatted(Formatting.BLACK)));

        if (entry.action() instanceof ToggleAction toggle) {
            this.toggleBox = Components.smallCheckbox(null);

            this.toggleBox.checked(toggle.getValue());
            this.toggleBox.onChanged().subscribe(toggle::setValue);

            child(Components.spacer().verticalSizing(Sizing.fixed(0)));
            child(this.toggleBox);
        } else {
            this.toggleBox = null;
        }
    }

    public void run() {
        Limelight.ENTRY_USES.bump(entry.entryId());

        switch (entry.action()) {
            case InvokeAction invoke -> {
                if (invoke.closesScreen()) screen.close();
                invoke.run();
            }
            case SetSearchTextAction setSearchText -> {
                // hey guys did you know I love ConcurrentModificationExceptions
                MinecraftClient.getInstance().send(() -> {
                    screen.searchBox.setText(setSearchText.newSearchText());
                    screen.searchBox.root().focusHandler().focus(screen.searchBox, FocusSource.KEYBOARD_CYCLE);
                });
            }
            case ToggleAction ignored -> {
                toggleBox.checked(!toggleBox.checked());
            }
        }
    }

    @Override
    public boolean onMouseDown(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            run();
        }

        return super.onMouseDown(mouseX, mouseY, button);
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            run();
        }

        return super.onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public void onFocusGained(FocusSource source) {
        super.onFocusGained(source);
        surface(Surface.outline(0xFFFFFFFF));
    }

    @Override
    public void onFocusLost() {
        super.onFocusLost();
        surface(Surface.BLANK);
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return true;
    }
}
