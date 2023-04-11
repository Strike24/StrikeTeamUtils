package ml.itsstrike.striketeamutils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class GlowManager {
    public static List<Player> glowingPlayers = new ArrayList<>();
    public static void setGlowing(Player player, boolean glowing) {
        if (glowing) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
            if (!glowingPlayers.contains(player)) {
                glowingPlayers.add(player);
            }
        } else {
            player.removePotionEffect(PotionEffectType.GLOWING);
            glowingPlayers.remove(player);
        }
    }
}
