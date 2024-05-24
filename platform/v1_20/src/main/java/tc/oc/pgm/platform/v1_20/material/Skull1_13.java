package tc.oc.pgm.platform.v1_20.material;

import org.bukkit.Material;

import java.util.Set;

public class Skull1_13 extends MaterialData1_13 {
    public final boolean matchAny;

    public Skull1_13(Material material, boolean matchAny) {
        super(material, null,
            Set.of(
                Material.SKELETON_SKULL,
                Material.PLAYER_HEAD,
                Material.PIGLIN_HEAD,
                Material.WITHER_SKELETON_SKULL,
                Material.CREEPER_HEAD
            )
        );
        this.matchAny = matchAny;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Skull1_13)) return false;
        return matchAny && ((Skull1_13) other).matchAny;
    }
}
