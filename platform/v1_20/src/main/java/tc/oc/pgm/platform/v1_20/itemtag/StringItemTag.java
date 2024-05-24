package tc.oc.pgm.platform.v1_20.itemtag;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.util.inventory.tag.ItemTag;

public class StringItemTag implements ItemTag<String> {
    private static final String TAG_FORMAT = "stringitemtag_%s";
    private static int TAG_COUNTER = 0;
    private final NamespacedKey key;

    public StringItemTag() {
        // Cringe
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("PGM");
        this.key = new NamespacedKey(plugin, String.format(TAG_FORMAT, TAG_COUNTER++));
    }

    @Override
    public @Nullable String get(ItemStack item) {
        if (!item.hasItemMeta()) return null;
        final PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(this.key)) return null;
        return pdc.get(this.key, PersistentDataType.STRING);
    }

    @Override
    public void set(ItemStack item, String value) {
        final ItemMeta itemMeta = item.getItemMeta();
        itemMeta.getPersistentDataContainer().set(this.key, PersistentDataType.STRING, value);
        item.setItemMeta(itemMeta);
    }

    @Override
    public void clear(ItemStack item) {
        if (!item.hasItemMeta()) return;
        item.getItemMeta().getPersistentDataContainer().remove(this.key);
    }
}
