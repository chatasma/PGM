package tc.oc.pgm.util.nms.item;

import org.bukkit.potion.PotionType;

public class PotionTypeUtils {
  public static PotionType potionTypeFromDamageValue(int damageValue) {
    switch (damageValue) {
      case 0:
        return PotionType.WATER;
      case 1:
        return PotionType.REGEN;
      case 2:
        return PotionType.SPEED;
      case 3:
        return PotionType.FIRE_RESISTANCE;
      case 4:
        return PotionType.POISON;
      case 5:
        return PotionType.INSTANT_HEAL;
      case 6:
        return PotionType.NIGHT_VISION;
      case 8:
        return PotionType.WEAKNESS;
      case 9:
        return PotionType.STRENGTH;
      case 10:
        return PotionType.SLOWNESS;
      case 11:
        return PotionType.JUMP;
      case 12:
        return PotionType.INSTANT_DAMAGE;
      case 13:
        return PotionType.WATER_BREATHING;
      case 14:
        return PotionType.INVISIBILITY;
      default:
        return PotionType.INSTANT_HEAL;
    }
  }
}
