package ml.itsstrike.striketeamutils.listeners;

import ml.itsstrike.striketeamutils.GlowManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class GlowingListener implements Listener {
    @EventHandler
    public void onDeathEvent(PlayerDeathEvent event) {
        if (GlowManager.glowingPlayers.contains(event.getPlayer())) {
            GlowManager.setGlowing(event.getPlayer(), true);
        }
    }

    @EventHandler
    public void onMilkConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.MILK_BUCKET) return;
        if (GlowManager.glowingPlayers.contains(event.getPlayer())) {
            GlowManager.setGlowing(event.getPlayer(), true);
        }
    }
}
