package net.guizhanss.slimefunextension.core.registration;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import javax.annotation.ParametersAreNonnullByDefault;

import net.guizhanss.guizhanlib.slimefun.addon.AddonConfig;
import net.guizhanss.slimefunextension.SlimefunExtension;
import net.guizhanss.slimefunextension.utils.FileUtils;

import org.bukkit.configuration.ConfigurationSection;

/**
 * This class is responsible for loading a module.
 */
public class ModuleLoader {
    @ParametersAreNonnullByDefault
    public static void load(File moduleFolder, String moduleName) {
        new ModuleLoader(moduleFolder, moduleName);
    }

    private final String realName;

    @ParametersAreNonnullByDefault
    private ModuleLoader(File moduleFolder, String moduleName) {
        var matcher = FileUtils.ORDERED_PATTERN.matcher(moduleName);
        if (matcher.find()) {
            // remove the number prefix from the module name
            realName = matcher.replaceAll("").trim();
        } else {
            realName = moduleName;
        }
        SlimefunExtension.logKeyed(Level.INFO, "console.loading-module", realName);

        List<String> files = FileUtils.getYamlFiles(moduleFolder);
        for (String fileName : files) {
            SlimefunExtension.debug("Loading file {0}", fileName);
            AddonConfig file = new AddonConfig(SlimefunExtension.getInstance(),
                "modules" + File.separator + moduleName + File.separator + fileName);

            for (String id : file.getKeys(false)) {
                loadConfig(id, file.getConfigurationSection(id));
            }
        }
    }

    @ParametersAreNonnullByDefault
    private void loadConfig(String id, ConfigurationSection section) {

    }
}
