package org.abyssdev.abysswithdraw.command;

import net.abyssdev.abysslib.command.AbyssCommand;
import net.abyssdev.abysslib.command.context.CommandContext;
import net.abyssdev.abysslib.placeholder.PlaceholderReplacer;
import net.abyssdev.abysslib.utils.Utils;
import org.abyssdev.abysswithdraw.AbyssWithdraw;
import org.abyssdev.abysswithdraw.economy.WithdrawEconomy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The withdraw command for a currency
 *
 * @author Relocation
 */
public final class WithdrawCommand extends AbyssCommand<AbyssWithdraw, Player> {

    private final WithdrawEconomy economy;

    /**
     * Constructs a new WithdrawCommand
     *
     * @param plugin The abyss withdraw plugin
     * @param name The withdraw base command name
     * @param aliases The withdraw command aliases
     */
    public WithdrawCommand(final AbyssWithdraw plugin, final WithdrawEconomy economy, final String name, final List<String> aliases) {
        super(plugin, name, "The base withdraw command for an economy", aliases, Player.class);

        this.economy = economy;
    }

    @Override
    public void execute(final CommandContext<Player> context) {

        final Player player = context.getSender();

        if (context.getArguments().length != 1) {
            this.economy.sendMessage(player, "withdraw-help", null);
            return;
        }

        final double amount = context.asDouble(0);

        if (!this.economy.getEconomy().hasBalance(player, amount)) {
            this.economy.sendMessage(player, "not-enough", new PlaceholderReplacer()
                    .addPlaceholder("%amount%", Utils.format(amount)));

            return;
        }

        final ItemStack item = this.economy.buildItem(player, amount);

        this.economy.getEconomy().withdrawBalance(player, amount);
        this.economy.sendMessage(player, "withdrawn", new PlaceholderReplacer()
                .addPlaceholder("%amount%", Utils.format(amount)));

        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), item);
            return;
        }

        player.getInventory().addItem(item);
    }

}