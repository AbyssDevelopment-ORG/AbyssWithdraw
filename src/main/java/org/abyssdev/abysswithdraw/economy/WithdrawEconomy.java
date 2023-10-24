package org.abyssdev.abysswithdraw.economy;

import lombok.Getter;
import net.abyssdev.abysslib.builders.ItemBuilder;
import net.abyssdev.abysslib.command.AbyssCommand;
import net.abyssdev.abysslib.config.AbyssConfig;
import net.abyssdev.abysslib.economy.provider.Economy;
import net.abyssdev.abysslib.economy.registry.impl.DefaultEconomyRegistry;
import net.abyssdev.abysslib.nbt.NBTUtils;
import net.abyssdev.abysslib.placeholder.PlaceholderReplacer;
import net.abyssdev.abysslib.text.MessageCache;
import net.abyssdev.abysslib.utils.Utils;
import org.abyssdev.abysswithdraw.AbyssWithdraw;
import org.abyssdev.abysswithdraw.command.WithdrawCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public final class WithdrawEconomy {

    private final String key;
    private final MessageCache messageCache;
    private final ItemBuilder builder;

    private final AbyssCommand<AbyssWithdraw, Player> command;
    private final Economy economy;

    public WithdrawEconomy(final AbyssWithdraw plugin, final AbyssConfig config, final String key) {
        this.key = key;
        this.economy = DefaultEconomyRegistry.get().getEconomy(config.getString("currencies." + key + ".type"));
        this.command = new WithdrawCommand(
                plugin,
                this,
                config.getString("currencies." + key + ".base-command"),
                config.getStringList("currencies." + key + ".aliases"));

        this.command.register();
        this.messageCache = new MessageCache(config);

        for (final String msgKey : config.getSectionKeys("currencies." + key + ".messages")) {
            this.messageCache.loadMessage("currencies." + key + ".messages." + msgKey);
        }

        this.builder = config.getItemBuilder("currencies." + key + ".withdraw-item");
    }

    public void sendMessage(final Player player, final String message, final PlaceholderReplacer replacer) {
        this.messageCache.sendMessage(player, "currencies." + key + ".messages." + message, replacer == null ? new PlaceholderReplacer() : replacer);
    }

    public ItemStack buildItem(final Player player, final double amount) {
        ItemStack item = this.builder.parse(new PlaceholderReplacer()
                .addPlaceholder("%player%", player.getName())
                .addPlaceholder("%amount%", Utils.format(amount)));

        item = NBTUtils.get().setString(item, "WITHDRAW_CURRENCY", this.key);
        item = NBTUtils.get().setDouble(item, "WITHDRAW_AMOUNT", amount);

        return item;
    }

}
