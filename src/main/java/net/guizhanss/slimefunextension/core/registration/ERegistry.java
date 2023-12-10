package net.guizhanss.slimefunextension.core.registration;

import net.guizhanss.slimefunextension.SlimefunExtension;

import lombok.Getter;

@Getter
public final class ERegistry {
    private final SlimefunExtension plugin;

    public ERegistry(SlimefunExtension plugin) {
        this.plugin = plugin;
    }
}
