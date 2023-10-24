package org.abyssdev.abysswithdraw.listener;

import net.abyssdev.abysslib.listener.AbyssListener;
import net.abyssdev.abysslib.nbt.NBTUtils;
import net.abyssdev.abysslib.placeholder.PlaceholderReplacer;
import net.abyssdev.abysslib.utils.Utils;
import org.abyssdev.abysswithdraw.AbyssWithdraw;
import org.abyssdev.abysswithdraw.economy.WithdrawEconomy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * The interact listener
 *
 * @author Relocation
 */
public final class InteractListener extends AbyssListener<AbyssWithdraw> {

    /**
     * Constructs a new InteractListener
     *
     * @param plugin The abyss withdraw plugin
     */
    public InteractListener(final AbyssWithdraw plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {

        final Player player = event.getPlayer();
        final ItemStack item = player.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (!NBTUtils.get().contains(item, "WITHDRAW_CURRENCY")) {
            return;
        }

        final Optional<WithdrawEconomy> econOpt = this.plugin.getEconomyRegistry().get(NBTUtils.get().getString(item, "WITHDRAW_CURRENCY"));

        if (!econOpt.isPresent()) {
            return;
        }

        final WithdrawEconomy economy = econOpt.get();
        final double amount = NBTUtils.get().getDouble(item, "WITHDRAW_AMOUNT");

        economy.getEconomy().addBalance(player, amount);
        economy.getMessageCache().sendMessage(player, "messages.redeemed", new PlaceholderReplacer()
                .addPlaceholder("%amount%", Utils.format(amount)));

        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.setItemInHand(new ItemStack(Material.AIR));
        }
    }
}