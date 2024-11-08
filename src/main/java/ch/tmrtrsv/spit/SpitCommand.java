package ch.tmrtrsv.spit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SpitCommand implements CommandExecutor, TabCompleter {

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cЭта команда может быть выполнена только игроком"));
            return true;
        }

        final Player player = (Player) sender;
        final FileConfiguration configuration = Spit.getInstance().getConfig();

        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!player.hasPermission("spit.reload")) {
                    player.sendMessage(Utils.color(configuration.getString("messages.no_permission")));
                    return true;
                }
                if (args.length != 1) {
                    player.sendMessage(Utils.color(configuration.getString("messages.reload_usage")));
                    return true;
                }
                Spit.getInstance().reloadConfig();
                player.sendMessage(Utils.color(configuration.getString("messages.config_reloaded")));
            } else {
                player.sendMessage(Utils.color(configuration.getString("messages.reload_usage")));
            }
            return true;
        }

        if (!player.hasPermission("spit.spit")) {
            player.sendMessage(Utils.color(configuration.getString("messages.no_permission")));
            return true;
        }

        player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SPIT, 1.0f, 1.0f);
        final double velocityMultiplier = configuration.getDouble("settings.velocity-multiplier");
        final double DISTANCE_THRESHOLD = configuration.getDouble("settings.distance-threshold");
        final double DIRECTION_THRESHOLD = configuration.getDouble("settings.direction-threshold");

        for (final Entity target : player.getNearbyEntities(DISTANCE_THRESHOLD, DISTANCE_THRESHOLD, DISTANCE_THRESHOLD)) {
            if (!(target instanceof Player)) {
                continue;
            }
            final Location playerLocation = player.getLocation();
            final Location targetLocation = target.getLocation();
            final Vector playerHeadDirection = player.getEyeLocation().getDirection();
            final Vector directionToTarget = target.getLocation().subtract(player.getEyeLocation()).toVector();
            final double dotProduct = playerHeadDirection.dot(directionToTarget.normalize());

            if (player.getLocation().distance(target.getLocation()) <= 5.0 && player.hasLineOfSight(target) && dotProduct > DIRECTION_THRESHOLD) {
                final Vector direction = targetLocation.subtract(playerLocation).toVector();
                player.launchProjectile(LlamaSpit.class, direction);
                player.sendMessage(Utils.color(configuration.getString("messages.player_hit").replace("$p", target.getName())));
                target.sendMessage(Utils.color(configuration.getString("messages.player_spat").replace("$p", player.getName())));
            } else {
                player.launchProjectile(LlamaSpit.class).setVelocity(player.getVelocity().multiply(velocityMultiplier));
            }
            return true;
        }
        player.launchProjectile(LlamaSpit.class).setVelocity(player.getVelocity().multiply(velocityMultiplier));
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 1) {
            final List<String> strings = new ArrayList<>();
            strings.add("reload");
            return strings;
        }
        return null;
    }
}