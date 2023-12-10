package net.guizhanss.slimefunextension;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import org.bukkit.plugin.Plugin;

import io.github.thebusybiscuit.slimefun4.libraries.dough.updater.GitHubBuildsUpdater;

import net.guizhanss.guizhanlib.slimefun.addon.AbstractAddon;
import net.guizhanss.guizhanlib.slimefun.addon.AddonConfig;
import net.guizhanss.guizhanlib.updater.GuizhanBuildsUpdater;
import net.guizhanss.slimefunextension.core.registration.ERegistry;
import net.guizhanss.slimefunextension.core.registration.ModuleLoader;
import net.guizhanss.slimefunextension.core.services.LocalizationService;
import net.guizhanss.slimefunextension.utils.FileUtils;

import org.bstats.bukkit.Metrics;

public final class SlimefunExtension extends AbstractAddon {

    private static final String DEFAULT_LANG = "en-US";

    private LocalizationService localization;
    private boolean debugEnabled = false;
    private AddonConfig config;
    private ERegistry registry;

    public SlimefunExtension() {
        super("ybw0014", "SlimefunExtension", "master", "auto-update");
    }

    @Nonnull
    public static LocalizationService getLocalization() {
        return inst().localization;
    }

    @Nonnull
    public static ERegistry getRegistry() {
        return inst().registry;
    }

    @ParametersAreNonnullByDefault
    public static void logKeyed(Level level, String key, Object... args) {
        Preconditions.checkNotNull(key, "key cannot be null");

        log(level, inst().localization.getString(key), args);
    }

    public static void debug(@Nonnull String message, @Nonnull Object... args) {
        Preconditions.checkNotNull(message, "message cannot be null");

        if (inst().debugEnabled) {
            inst().getLogger().log(Level.INFO, "[DEBUG] " + message, args);
        }
    }

    @Nonnull
    private static SlimefunExtension inst() {
        return getInstance();
    }

    @Override
    public void enable() {
        log(Level.INFO, "====================");
        log(Level.INFO, " Slimefun Extension ");
        log(Level.INFO, "     by ybw0014     ");
        log(Level.INFO, "====================");

        // create folders
        File moduleFolder = new File(getDataFolder(), "modules");
        if (!moduleFolder.exists()) {
            moduleFolder.mkdirs();
        }

        // registry
        registry = new ERegistry(this);

        // config
        config = new AddonConfig(this, "config.yml");
        config.addMissingKeys();

        // debug
        debugEnabled = config.getBoolean("debug", false);

        // localization
        log(Level.INFO, "Loading language...");
        String lang = config.getString("lang", DEFAULT_LANG);
        localization = new LocalizationService(this);
        localization.addLanguage(lang);
        if (!lang.equals(DEFAULT_LANG)) {
            localization.addLanguage(DEFAULT_LANG);
        }
        logKeyed(Level.INFO, "console.loaded-language", lang);

        // modules
        List<String> modules = FileUtils.getFolders(moduleFolder).stream()
            .sorted(FileUtils.COMPARATOR)
            .filter(folder -> !folder.startsWith("_") && !folder.startsWith("."))
            .toList();
        for (String module : modules) {
            ModuleLoader.load(new File(moduleFolder, module), module);
        }

        setupMetrics();
    }

    @Override
    public void disable() {
    }

    private void setupMetrics() {
        new Metrics(this, 114514);
    }

    @Override
    protected void autoUpdate() {
        if (getPluginVersion().startsWith("DEV")) {
            String path = getGithubUser() + "/" + getGithubRepo() + "/" + getGithubBranch();
            new GitHubBuildsUpdater(this, getFile(), path).start();
        } else if (getPluginVersion().startsWith("Build")) {
            try {
                // use updater in lib plugin
                Class<?> clazz = Class.forName("net.guizhanss.guizhanlibplugin.updater.GuizhanUpdater");
                Method updaterStart = clazz.getDeclaredMethod("start", Plugin.class, File.class, String.class, String.class, String.class);
                updaterStart.invoke(null, this, getFile(), getGithubUser(), getGithubRepo(), getGithubBranch());
            } catch (Exception ignored) {
                // use updater in lib
                new GuizhanBuildsUpdater(this, getFile(), getGithubUser(), getGithubRepo(), getGithubBranch()).start();
            }
        }
    }
}
