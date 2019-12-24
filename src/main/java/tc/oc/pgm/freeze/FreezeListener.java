package tc.oc.pgm.freeze;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import tc.oc.pgm.AllTranslations;
import tc.oc.pgm.api.Permissions;
import tc.oc.pgm.api.player.event.ObserverInteractEvent;
import tc.oc.pgm.events.PlayerPartyChangeEvent;
import tc.oc.pgm.match.ObservingParty;
import tc.oc.pgm.spawns.events.ObserverKitApplyEvent;

public class FreezeListener implements Listener {
    private FreezeManager freezeManager;

    public FreezeListener(Plugin plugin) {
        this.freezeManager = FreezeManager.get();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private boolean checkIfPlayerIsFrozen(Entity potentialPlayer) {
        if (!(potentialPlayer instanceof Player)) return false;
        return freezeManager.isFrozen((Player) potentialPlayer);
    }

    private boolean checkIfPlayerIsFrozen(Player player) {
        return freezeManager.isFrozen(player);
    }

    @EventHandler
    public void onTeamChange(PlayerPartyChangeEvent event) {
        if (freezeManager.getFreezeModeOfPlayer(event.getPlayer().getBukkit())) return;
        if (!(event.getNewParty() instanceof ObservingParty)) {
            freezeManager.setFreezeMode(event.getPlayer().getBukkit(), false);
            event.getPlayer().getBukkit().sendMessage(ChatColor.AQUA + AllTranslations.get().translate("freeze.exitFreezeMode", event.getPlayer().getBukkit()));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteractEntity(final ObserverInteractEvent event) {
        if(event.getPlayer().isDead()) return;

        if(this.checkIfPlayerIsFrozen(event.getPlayer().getBukkit())) {
            event.setCancelled(true);

        } else {
            if(event.getClickedItem() != null &&
                    event.getClickedItem().getType() == Material.ICE &&
                    event.getPlayer().getBukkit().hasPermission(Permissions.FREEZE) &&
                    event.getClickedPlayer() != null) {
                event.setCancelled(true);
                freezeManager.toggleFreeze(event.getPlayer().getBukkit(), event.getClickedPlayer().getBukkit());
            }
        }
    }

    @EventHandler
    public void giveKit(final ObserverKitApplyEvent event) {
        if(event.getPlayer().getBukkit().hasPermission(Permissions.FREEZE)) {
            freezeManager.assignFreezeItem(event.getPlayer().getBukkit());
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        if(this.checkIfPlayerIsFrozen(event.getPlayer())) {
            Location old = event.getFrom();
            old.setPitch(event.getTo().getPitch());
            old.setYaw(event.getTo().getYaw());
            event.setTo(old);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onVehicleMove(final VehicleMoveEvent event) {
        if(!event.getVehicle().isEmpty() && this.checkIfPlayerIsFrozen(event.getVehicle().getPassenger())) {
            event.getVehicle().setVelocity(new Vector(0, 0, 0));
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        if(this.checkIfPlayerIsFrozen(event.getEntered())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVehicleExit(final VehicleExitEvent event) {
        if(this.checkIfPlayerIsFrozen(event.getExited())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        if(this.checkIfPlayerIsFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if(this.checkIfPlayerIsFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBucketFill(final PlayerBucketFillEvent event) {
        if(this.checkIfPlayerIsFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        if(this.checkIfPlayerIsFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW) // ignoreCancelled doesn't seem to work well here
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if(this.checkIfPlayerIsFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInventoryClick(final InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player) {
            if(this.checkIfPlayerIsFrozen(event.getWhoClicked())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        if(this.checkIfPlayerIsFrozen(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamge(final EntityDamageByEntityEvent event) {
        if(this.checkIfPlayerIsFrozen(event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onVehicleDamage(final VehicleDamageEvent event) {
        if(this.checkIfPlayerIsFrozen(event.getAttacker())) {
            event.setCancelled(true);
        }
    }
}
