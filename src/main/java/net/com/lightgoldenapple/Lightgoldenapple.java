package net.com.lightgoldenapple;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class Lightgoldenapple extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Register the event
        Bukkit.getPluginManager().registerEvents(this, this);

        // Create the crafting recipe for the Light Golden Apple
        createLightGoldenAppleRecipe();
        getLogger().info("LightGoldenApple plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("LightGoldenApple plugin has been disabled!");
    }

    private void createLightGoldenAppleRecipe() {
        // Create the custom Light Golden Apple item
        ItemStack lightGoldenApple = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta meta = lightGoldenApple.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§eLight Golden Apple");

            // Apply custom lore or tags if needed (for identification)
            lightGoldenApple.setItemMeta(meta);
        }

        // Define the recipe for crafting the Light Golden Apple
        NamespacedKey key = new NamespacedKey(this, "light_golden_apple");
        ShapedRecipe recipe = new ShapedRecipe(key, lightGoldenApple);

        recipe.shape(" G ", "GAG", " G ");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('A', Material.APPLE);

        // Register the recipe
        Bukkit.addRecipe(recipe);
    }


    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        // Check if the player consumed a Light Golden Apple
        ItemStack item = event.getItem();
        ItemStack itemToRemove = new ItemStack(Material.GOLDEN_APPLE, 1);
        ItemMeta meta = itemToRemove.getItemMeta();
        if (item.getType() == Material.GOLDEN_APPLE &&
                item.getItemMeta() != null &&
                "§eLight Golden Apple".equals(item.getItemMeta().getDisplayName())) {
                itemToRemove.setItemMeta(meta);
            // Cancel the event to prevent the default Golden Apple effects (Regeneration II, Absorption)
            event.setCancelled(false);
            Bukkit.getScheduler().runTaskLater(this, () -> {
                // Remove one Light Golden Apple from the player's inventory
                event.setCancelled(true);
                event.getPlayer().removePotionEffect(PotionEffectType.REGENERATION); // Remove any existing Regen effect
                event.getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);    // Remove any existing Absorption effect

            }, 1L);

            event.setCancelled(false);

            Bukkit.getScheduler().runTaskLater(this, () -> {
                Player player = event.getPlayer();
                ItemStack heldItem = player.getInventory().getItemInMainHand();

                if (heldItem != null && heldItem.getType() != Material.AIR) {
                    int currentAmount = heldItem.getAmount();

                    if (currentAmount > 1) {
                        heldItem.setAmount(currentAmount - 1);
                    } else {
                        player.getInventory().setItemInMainHand(null);
                    }
                }
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 0)); // Regen for 10 seconds
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1600, 0));  // Absorption for 1:20 minutes
            }, 1L);// 20 ticks = 1 second
        }
    }
}
