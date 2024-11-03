package ch.tmrtrsv.spit;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Spit extends JavaPlugin {

    private static Spit instance;

    public void onEnable() {
        (Spit.instance = this).saveDefaultConfig();
        this.getCommand("spit").setExecutor(new SpitCommand());
        this.getCommand("spit").setTabCompleter(new SpitCommand());
        this.sendCredit();
    }

    @Override
    public void onDisable() {
        this.sendCredit();
    }

    private void sendCredit() {
        Bukkit.getConsoleSender().sendMessage(Utils.color(""));
        Bukkit.getConsoleSender().sendMessage(Utils.color("&f &#23C100S&#22B900p&#20B100i&#1FA900t &#1C9800v&#1A90001&#198800.&#1780000"));
        Bukkit.getConsoleSender().sendMessage(Utils.color("&f Автор: &#FB3908Т&#FC2B06и&#FD1D04м&#FE0E02у&#FF0000р"));
        Bukkit.getConsoleSender().sendMessage(Utils.color("&f Телеграм: &#008DFF@&#0086FFt&#007FFFm&#0078FFr&#0071FFt&#006BFFr&#0064FFs&#005DFFv&#0056FFc&#004FFFh"));
        Bukkit.getConsoleSender().sendMessage(Utils.color(""));
    }

    public static Spit getInstance() {
        return Spit.instance;
    }
}