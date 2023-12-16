/*
 * ▪  ·▄▄▄▄• ▄▄▄·  ▐ ▄  ▄▄▄·  ▄▄ • ▪   ▄▄· ▄▄▄   ▄▄▄· ·▄▄▄▄▄▄▄▄
 * ██ ▪▀·.█▌▐█ ▀█ •█▌▐█▐█ ▀█ ▐█ ▀ ▪██ ▐█ ▌▪▀▄ █·▐█ ▀█ ▐▄▄·•██
 * ▐█·▄█▀▀▀•▄█▀▀█ ▐█▐▐▌▄█▀▀█ ▄█ ▀█▄▐█·██ ▄▄▐▀▀▄ ▄█▀▀█ ██▪  ▐█.▪
 * ▐█▌█▌▪▄█▀▐█ ▪▐▌██▐█▌▐█ ▪▐▌▐█▄▪▐█▐█▌▐███▌▐█•█▌▐█ ▪▐▌██▌. ▐█▌·
 * ▀▀▀·▀▀▀ • ▀  ▀ ▀▀ █▪ ▀  ▀ ·▀▀▀▀ ▀▀▀·▀▀▀ .▀  ▀ ▀  ▀ ▀▀▀  ▀▀▀
 *
 *
 *    @@@@@
 *    @@* *@@
 *      @@@  @@@
 *         @@@  @@ @@@       @@@@@@@@@@@
 *           @@@@@@@@   @@@@@@@@@@@@@@@@@@@@@
 *            @@@    @@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *               #@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *               #@@@   @@                 @@  @@@@  @@@@
 *                @@@@      @@@      @@@@      @@@@   @@@
 *                @@@@@@                     @@@@@@    @@
 *                 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                  @@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                   @@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                     @@@@@@@@@@@@@@@@@@@@@@@
 *                       @@@@@@@@@@@@@@@@@@@
 *                           @@@@@@@@@@@
 *
 * Copyright (c) 2023 - present | sanguine6660 <sanguine6660@gmail.com>
 * Copyright (c) 2023 - present | izanagicraft.com <contact@izanagicraft.com>
 * Copyright (c) 2023 - present | izanagicraft.com team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.izanagicraft.velocity.maintenance;

import com.google.inject.Inject;
import com.izanagicraft.velocity.maintenance.commands.MaintenanceCommand;
import com.izanagicraft.velocity.maintenance.data.JsonData;
import com.izanagicraft.velocity.maintenance.data.types.MaintenanceData;
import com.izanagicraft.velocity.maintenance.listener.LoginEventListener;
import com.izanagicraft.velocity.maintenance.listener.PingEventListener;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Velocity-Maintenance; com.izanagicraft.velocity.maintenance:VelocityMaintenancePlugin
 *
 * @author <a href="https://github.com/sanguine6660">@sanguine6660</a>
 * @since 16.12.2023
 */
@Plugin(
        id = "velocity-maintenance",
        name = "VelocityMaintenance",
        version = "1.0-SNAPSHOT",
        description = "A customizable maintenance plugin for Velocity.",
        authors = {"sanguine6660"},
        url = "https://github.com/IzanagiCraft/Velocity-Maintenance",
        dependencies = {}
)
public class VelocityMaintenancePlugin {
    private static VelocityMaintenancePlugin instance;

    public static VelocityMaintenancePlugin getInstance() {
        return instance;
    }

    private ProxyServer server;
    private Logger logger;
    private @DataDirectory Path dataDir;
    private File dataFolder;

    private Toml config;

    private JsonData<MaintenanceData> maintenanceData;

    /**
     * Constructor for the Velocity-Maintenance plugin.
     *
     * @param server  The Velocity ProxyServer instance.
     * @param logger  The Logger instance for logging.
     * @param dataDir The data directory for storing plugin data.
     */
    @Inject
    public VelocityMaintenancePlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDir) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataDir.toFile();
        this.dataDir = dataDir;
        if (!this.dataFolder.exists()) {
            // Create the data folder if it doesn't exist.
            this.dataFolder.mkdirs();
        }
        this.onLoad();
    }

    /**
     * Method called during the plugin's initialization phase.
     * You can perform any necessary setup tasks here.
     */
    private void onLoad() {
        instance = this;
    }

    /**
     * Event handler for the ProxyInitializeEvent.
     * Called when the plugin is enabled.
     *
     * @param event The ProxyInitializeEvent instance.
     */
    @Subscribe
    public void onEnable(ProxyInitializeEvent event) {
        this.loadConfigurations();
        this.registerCommandsAndEvents();
    }

    /**
     * Event handler for the ProxyShutdownEvent.
     * Called when the plugin is disabled.
     *
     * @param event The ProxyShutdownEvent instance.
     */
    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        // TODO: Add any tasks to be performed when the plugin is disabled.
    }

    private void registerCommandsAndEvents() {
        EventManager eventManager = getServer().getEventManager();
        eventManager.register(this, new LoginEventListener(this));
        eventManager.register(this, new PingEventListener(this));

        CommandManager commandManager = getServer().getCommandManager();
        commandManager.register(commandManager.metaBuilder("maintenance").build(), new MaintenanceCommand(this));
    }

    private void loadConfigurations() {
        this.saveDefaultConfig();

        try {
            File maintenanceDataFile = new File(this.getDataFolder(), "data/maintenance.json");
            this.maintenanceData = new JsonData<>(MaintenanceData.class, maintenanceDataFile);
            this.maintenanceData.setDefault(MaintenanceData.class, MaintenanceData.getDefault());
            this.maintenanceData.load(true);
            this.maintenanceData.save(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveResource(String resourceName, boolean overwrite) {
        File outputFile = new File(getDataFolder(), resourceName);

        if (!overwrite && outputFile.exists()) {
            return;
        }

        try {
            Files.createDirectories(outputFile.toPath().getParent());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream(resourceName);
             OutputStream outputStream = Files.newOutputStream(outputFile.toPath())) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = resourceStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDefaultConfig() {
        this.saveResource("config.toml", false);
        this.reloadDefaultConfig();
    }

    public void reloadDefaultConfig() {
        File configFile = new File(getDataFolder(), "config.toml");
        config = new Toml();
        config.read(configFile);
    }

    public File getDataFolder() {
        return dataFolder;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public Toml getConfig() {
        return config;
    }

    public JsonData<MaintenanceData> getMaintenanceData() {
        return maintenanceData;
    }
}
