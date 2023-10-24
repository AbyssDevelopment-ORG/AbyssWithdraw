package org.abyssdev.abysswithdraw;

import lombok.Getter;
import net.abyssdev.abysslib.config.AbyssConfig;
import net.abyssdev.abysslib.patterns.registry.Registry;
import net.abyssdev.abysslib.patterns.registry.impl.EclipseRegistry;
import net.abyssdev.abysslib.plugin.AbyssPlugin;
import org.abyssdev.abysswithdraw.economy.WithdrawEconomy;
import org.abyssdev.abysswithdraw.listener.InteractListener;

/**
 * The abyss withdraw main class
 *
 * @author Relocation
 */
@Getter
public final class AbyssWithdraw extends AbyssPlugin {

    private final AbyssConfig config = this.getAbyssConfig("config");
    private final Registry<String, WithdrawEconomy> economyRegistry = new EclipseRegistry<>();

    @Override
    public void onEnable() {
        this.loadEcon();

        new InteractListener(this);
    }

    private void loadEcon() {
        for (final String key : this.config.getSectionKeys("currencies")) {
            this.economyRegistry.register(key, new WithdrawEconomy(this, config, key));
        }
    }

}