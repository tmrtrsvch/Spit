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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SpitCommand implements CommandExecutor, TabCompleter {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cЭта команда может быть выполнена только игроком"));
            return true;
        }

        final Player player = (Player) sender;
        final FileConfiguration config = Spit.getInstance().getConfig();

        if (!player.hasPermission("spit.cooldown.bypass")) {
            long currentTime = System.currentTimeMillis();
            long cooldownTime = config.getInt("settings.cooldown") * 1000L;
            Long lastUsed = cooldowns.get(player.getUniqueId());

            if (lastUsed != null && (currentTime - lastUsed) < cooldownTime) {
                long remainingTime = (cooldownTime - (currentTime - lastUsed)) / 1000;
                player.sendMessage(Utils.color(config.getString("messages.spit_cooldown").replace("%cooldown%", String.valueOf(remainingTime))));
                return true;
            }
            cooldowns.put(player.getUniqueId(), currentTime);
        }

        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!player.hasPermission("spit.reload")) {
                    player.sendMessage(Utils.color(config.getString("messages.no_permission")));
                    return true;
                }
                if (args.length != 1) {
                    player.sendMessage(Utils.color(config.getString("messages.reload_usage")));
                    return true;
                }
                Spit.getInstance().reloadConfig();
                player.sendMessage(Utils.color(config.getString("messages.config_reloaded")));
            } else {
                player.sendMessage(Utils.color(config.getString("messages.reload_usage")));
            }
            return true;
        }

        if (!player.hasPermission("spit.use")) {
            player.sendMessage(Utils.color(config.getString("messages.no_permission")));
            return true;
        }

        player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SPIT, 1.0f, 1.0f);

        final double velocityMultiplier = config.getDouble("settings.velocity-multiplier");
        final double DISTANCE_THRESHOLD = config.getDouble("settings.distance-threshold");
        final double DIRECTION_THRESHOLD = config.getDouble("settings.direction-threshold");

        boolean hasTarget = false;

        for (final Entity target : player.getNearbyEntities(DISTANCE_THRESHOLD, DISTANCE_THRESHOLD, DISTANCE_THRESHOLD)) {
            if (!(target instanceof Player)) {
                continue;
            }
            final Location playerLocation = player.getLocation();
            final Location targetLocation = target.getLocation();
            final Vector playerHeadDirection = player.getEyeLocation().getDirection();
            final Vector directionToTarget = targetLocation.subtract(player.getEyeLocation()).toVector();
            final double dotProduct = playerHeadDirection.dot(directionToTarget.normalize());

            if (dotProduct > DIRECTION_THRESHOLD && player.hasLineOfSight(target)) {
                final Vector direction = targetLocation.subtract(playerLocation).toVector().normalize().multiply(velocityMultiplier);
                LlamaSpit spit = player.launchProjectile(LlamaSpit.class);
                spit.setVelocity(direction);

                player.sendMessage(Utils.color(config.getString("messages.player_hit").replace("$p", target.getName())));
                target.sendMessage(Utils.color(config.getString("messages.player_spat").replace("$p", player.getName())));
                hasTarget = true;
                break;
            }
        }

        if (!hasTarget) {
            Vector spitDirection = player.getEyeLocation().getDirection().normalize().multiply(velocityMultiplier);
            LlamaSpit spit = player.launchProjectile(LlamaSpit.class);
            spit.setVelocity(spitDirection);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 1) {
            final List<String> strings = new ArrayList<>();
            strings.add("reload");
            return strings;
        }
        return null;
    }
}