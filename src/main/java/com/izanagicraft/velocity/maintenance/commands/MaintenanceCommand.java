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

package com.izanagicraft.velocity.maintenance.commands;

import com.izanagicraft.velocity.maintenance.VelocityMaintenancePlugin;
import com.izanagicraft.velocity.maintenance.messages.MessageUtils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.RawCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Velocity-Maintenance; com.izanagicraft.velocity.maintenance.commands:MaintenanceCommand
 *
 * @author <a href="https://github.com/sanguine6660">@sanguine6660</a>
 * @since 16.12.2023
 */
public class MaintenanceCommand implements RawCommand {
    private final VelocityMaintenancePlugin plugin;

    public MaintenanceCommand(VelocityMaintenancePlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public void execute(Invocation invocation) {

        boolean enablePerm = hasEnablePermission(invocation.source());
        boolean disablePerm = hasDisablePermission(invocation.source());

        if (!enablePerm && !disablePerm) return;

        String error = plugin.getConfig().getString("messages.maintenance-error");

        switch (invocation.arguments()) {
            case "on": {
                try {
                    plugin.getMaintenanceData().get().setEnabled(true);
                    try {
                        plugin.getMaintenanceData().save(true);
                    } catch (IOException e) {
                    }
                    String enabled = plugin.getConfig().getString("messages.maintenance-enabled");
                    MessageUtils.sendPrefixedMessage(invocation.source(), enabled);
                } catch (Exception e) {
                    MessageUtils.sendPrefixedMessage(invocation.source(), error);
                    e.printStackTrace();
                }
                break;
            }

            case "off": {
                try {
                    plugin.getMaintenanceData().get().setEnabled(false);
                    try {
                        plugin.getMaintenanceData().save(true);
                    } catch (IOException e) {
                    }
                    String disabled = plugin.getConfig().getString("messages.maintenance-disabled");
                    MessageUtils.sendPrefixedMessage(invocation.source(), disabled);
                } catch (Exception e) {
                    MessageUtils.sendPrefixedMessage(invocation.source(), error);
                    e.printStackTrace();
                }
                break;
            }
            default:
                String usage = plugin.getConfig().getString("messages.maintenance-usage");
                MessageUtils.sendPrefixedMessage(invocation.source(), usage);
                break;
        }

    }

    private boolean hasEnablePermission(CommandSource source) {
        String permission = String.valueOf(plugin.getConfig().getString("permissions.enable"));
        return source.hasPermission(permission);
    }

    private boolean hasDisablePermission(CommandSource source) {
        String permission = String.valueOf(plugin.getConfig().getString("permissions.disable"));
        return source.hasPermission(permission);
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        String[] args = invocation.arguments().split("\\s+");
        List<String> completions = new ArrayList<>();
        List<String> available = new ArrayList<>();

        switch (args.length) {
            case 1:
                available.add("on");
                available.add("off");
                break;
            default:
                break;
        }

        MessageUtils.copyPartialMatches(args[args.length - 1], available, completions);
        Collections.sort(completions);
        return CompletableFuture.completedFuture(completions);
    }
}
