package tc.oc.pgm.platform.v1_20.nms;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.*;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Ints;
import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.NameTagVisibility;
import org.jetbrains.annotations.NotNull;
import tc.oc.pgm.platform.v1_20.NullChunkGenerator;
import tc.oc.pgm.platform.v1_20.material.LegacyMaterialUtils;
import tc.oc.pgm.platform.v1_20.material.MaterialDataProvider1_13;
import tc.oc.pgm.util.nms.EnumPlayerInfoAction;
import tc.oc.pgm.util.nms.material.MaterialData;
import tc.oc.pgm.util.nms.material.MaterialDataProvider;
import tc.oc.pgm.util.nms.material.MaterialDataProviderPlatform;
import tc.oc.pgm.util.nms.reflect.Refl;
import tc.oc.pgm.util.nms.reflect.ReflectionProxy;
import tc.oc.pgm.util.nms.v1_10_12.NMSHacks1_10_12;
import tc.oc.pgm.util.reflect.MinecraftReflectionUtils;

public class NMSHacks1_20 extends NMSHacks1_10_12 {
  public static Refl.IBlockData reflIBlockData = ReflectionProxy.getProxy(Refl.IBlockData.class);
  public static Class<?> enumChatFormatClazz =
      MinecraftReflectionUtils.getNMSClassNew("EnumChatFormat");
  private static final String PGM_NAMESPACE = "pgm";

  @Override
  public Set<Material> getMaterialCollection(ItemMeta itemMeta, String key) {
    Map<String, Object> unhandledTags = refl.getUnhandledTags(itemMeta);
    if (!unhandledTags.containsKey(key)) return EnumSet.noneOf(Material.class);
    EnumSet<Material> materialSet = EnumSet.noneOf(Material.class);
    Object canDestroyList = unhandledTags.get(key);

    for (Object item : (List<Object>) nbtTagList.getListField(canDestroyList)) {
      String blockString = nbtTagString.getString(item);
      Material material = Material.matchMaterial(blockString);
      if (material != null) {
        materialSet.add(material);
      }
    }

    return materialSet;
  }

  @Override
  public boolean canMineBlock(Material material, ItemStack tool) {
    if (!material.isBlock()) {
      throw new IllegalArgumentException("Material '" + material + "' is not a block");
    }

    Object nmsBlock = craftMagicNumbers.getBlock(material);
    Object nmsTool = tool == null ? null : craftMagicNumbers.getItem(tool.getType());

    Object iBlockData = reflBlock.getBlockData(nmsBlock);

    boolean alwaysDestroyable = refl.isAlwaysDestroyable(reflIBlockData.getMaterial(nmsBlock));
    boolean toolCanDestroy = nmsTool != null && refl.canDestroySpecialBlock(nmsTool, iBlockData);
    return nmsBlock != null && (alwaysDestroyable || toolCanDestroy);
  }

  @Override
  public ItemStack craftItemCopy(ItemStack item) {
    if (item.getType().equals(Material.POTION)) {
      item = LegacyMaterialUtils.buildLegacyPotion(item.getDurability(), item.getAmount());
    }

    return craftItemStack.asCraftCopy(item);
  }

  @Override
  public ChunkGenerator nullChunkGenerator() {
    return new NullChunkGenerator();
  }

  @Override
  public void spawnFlagParticles(Player bukkitPlayer, DyeColor dyeColor, Location location) {
    BlockData blockData = woolFromDyeColor(dyeColor).createBlockData();
    bukkitPlayer.spawnParticle(
        Particle.BLOCK_DUST, location.clone().add(0, 56, 0), 50, 0.05f, 24f, 0.05f, 0f, blockData);
  }

  @Override
  public void spawnCritArrowParticles(Player playerBukkit, Location projectileLocation) {
    playerBukkit.spawnParticle(Particle.CRIT, projectileLocation, 1);
  }

  @Override
  protected List<Player> getViewingPlayers(Entity entity) {
    Object entityHandle = refl.getEntityHandle(entity);
    Object nmsWorld = refl.getNmsWorldFromEntity(entityHandle);
    Object chunkSource = refl.getChunkSourceFromNmsWorld(nmsWorld);
    Object chunkMap = refl.getChunkMapFromChunkSource(chunkSource);
    Map entityMap = refl.getEntityMapFromChunkMap(chunkMap);
    Object entityTrackerEntry = entityMap.get(refl.getEntityId(entityHandle));
    Set trackedPlayers = refl.getSeenByFromEntityTracker(entityTrackerEntry);

    List<Player> players = new ArrayList<>();

    for (Object trackedPlayer : trackedPlayers) {
      Player bukkitPlayer = refl.getBukkitPlayer(trackedPlayer);
      players.add(bukkitPlayer);
    }
    return players;
  }

  @Override
  public void spawnColoredArrowParticles(
      Color color, Player playerBukkit, Location projectileLocation) {
    playerBukkit.spawnParticle(
        Particle.REDSTONE, projectileLocation, 1, new Particle.DustOptions(color, 1));
  }

  @Override
  public void spawnPayloadParticles(World world, Location loc, Color color) {
    world.spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(color, 1));
  }

  @Override
  public void spawnPayloadBeamParticles(World world, Location loc, DyeColor color) {
    world.spawnParticle(
        Particle.BLOCK_DUST,
        loc,
        40,
        0.15,
        24.0,
        0.15,
        0.0,
        woolFromDyeColor(color).createBlockData());
  }

  @Override
  public void showExplosionParticle(Location explosion, Player playerBukkit) {
    playerBukkit.spawnParticle(Particle.EXPLOSION_HUGE, explosion, 1);
  }

  @Override
  public void spawnSpawnerParticles(World world, Location location) {
    world.spawnParticle(Particle.FLAME, location, 40, 0, 0.15f, 0, 0);
  }

  @Override
  public MaterialDataProviderPlatform getMaterialDataProvider() {
    return new MaterialDataProvider1_13();
  }

  @Override
  public Set<MaterialData> getBlockStates(Material material) {
    return LegacyMaterialUtils.getSimilarMaterials(material).stream()
        .map(MaterialDataProvider::from)
        .collect(Collectors.toSet());
  }

  @Override
  public Object teamPacket(
      int operation,
      String name,
      String displayName,
      String prefix,
      String suffix,
      boolean friendlyFire,
      boolean seeFriendlyInvisibles,
      NameTagVisibility nameTagVisibility,
      Collection<String> players) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);

    String nameTagVisString = null;
    if (nameTagVisibility != null) {
      switch (nameTagVisibility) {
        case ALWAYS:
          nameTagVisString = "always";
          break;
        case NEVER:
          nameTagVisString = "never";
          break;
        case HIDE_FOR_OTHER_TEAMS:
          nameTagVisString = "hideForOtherTeams";
          break;
        case HIDE_FOR_OWN_TEAM:
          nameTagVisString = "hideForOwnTeam";
          break;
      }
    }

    // https://github.com/dmulloy2/ProtocolLib/blob/e1255edb32ca6308204ed2321974ff0c15925360/src/test/java/com/comphenix/protocol/events/PacketContainerTest.java#L636
    final Optional<InternalStructure> internalStructureOpt = packet.getOptionalStructures().read(0);
    final InternalStructure internalStructure = internalStructureOpt.get();
    packet.getStrings().write(0, name);
    internalStructure
        .getStrings()
        .write(0, nameTagVisString)
        .write(1, "never"); // collide with other players

    internalStructure
        .getChatComponents()
        .write(0, WrappedChatComponent.fromLegacyText(displayName))
        .write(1, WrappedChatComponent.fromLegacyText(prefix))
        .write(2, WrappedChatComponent.fromLegacyText(suffix));

    int flags = 0;
    if (friendlyFire) {
      flags |= 1;
    }
    if (seeFriendlyInvisibles) {
      flags |= 2;
    }
    internalStructure.getIntegers().write(0, flags);

    internalStructure
        .getEnumModifier(ColorNMSWrapper.class, enumChatFormatClazz)
        .write(0, ColorNMSWrapper.GRAY);

    packet.getIntegers().write(0, operation);
    //    internalStructure.getIntegers().write(0, flags);

    packet.getSpecificModifier(Collection.class).write(0, players);
    return packet;
  }

  @Override
  public void postToMainThread(Plugin plugin, boolean priority, Runnable task) {
    Server bukkitServer = plugin.getServer();
    Object nmsServer = refl.getNMSServer(refl.getCraftServerHandle(bukkitServer));
    refl.executeRunnable(nmsServer, task);
  }

  @Override
  public void removeAndAddAllTabPlayers(Player viewer) {
    final List<PlayerInfoData> playerInfoDataList = new ArrayList<>();
    final List<UUID> serverPlayerIds = new ArrayList<>();

    for (Player player : Bukkit.getOnlinePlayers()) {
      if (viewer.canSee(player) || player == viewer) {
        final WrappedGameProfile wrappedGameProfile = WrappedGameProfile.fromPlayer(player);
        playerInfoDataList.add(
            new PlayerInfoData(
                wrappedGameProfile,
                getPing(player),
                EnumWrappers.NativeGameMode.fromBukkit(viewer.getGameMode()),
                WrappedChatComponent.fromLegacyText(player.getPlayerListName())));
        serverPlayerIds.add(wrappedGameProfile.getUUID());
      }
    }

    PacketContainer removePlayerPacket =
        new PacketContainer(PacketType.Play.Server.PLAYER_INFO_REMOVE);
    final List<UUID> profileUUIDs = removePlayerPacket.getSpecificModifier(List.class).read(0);
    profileUUIDs.addAll(serverPlayerIds);
    removePlayerPacket.getSpecificModifier(List.class).write(0, profileUUIDs);
    sendPacket(viewer, removePlayerPacket);

    PacketContainer addPlayerPacket = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
    addPlayerPacket.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
    // ProtocolLib bug -- it considers the actions as a collection, so we actually need to write to
    // the second field
    // that actions field is only present in the non-remove variant, so we need to use the first
    // field for the remove
    // packet
    final int playersFieldIndex = 1;
    addPlayerPacket.getPlayerInfoDataLists().write(playersFieldIndex, playerInfoDataList);
    sendPacket(viewer, addPlayerPacket);
  }

  @Override
  public int countSlots(InventoryView inventoryView) {
    // older versions specifically accounted for removing the armor slots when calling
    // getSize on player inventories, this is not the case anymore 1.9+
    // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/commits/aa008dff0f9bedbe88e1fe79831776b0a52eb90a#src%2Fmain%2Fjava%2Forg%2Fbukkit%2Fcraftbukkit%2Finventory%2FCraftInventoryPlayer.java?f=26
    // replicating this here to remove armor/off-hand slots
    return inventoryView.countSlots() - 5;
  }

  @Override
  public Object entityMetadataPacket(int entityId, Entity entity, boolean complete) {
    PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

    packetContainer.getIntegers().write(0, entityId);

    final List<WrappedDataValue> wrappedEntityMetadata = deepCloneEntityMetadata(entity);
    packetContainer.getDataValueCollectionModifier().write(0, wrappedEntityMetadata);

    return packetContainer;
  }

  protected final List<WrappedDataValue> deepCloneEntityMetadata(final Entity entity) {
    // https://www.spigotmc.org/threads/unable-to-modify-entity-metadata-packet-using-protocollib-1-19-3.582442/#post-4517187
    WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();
    final List<WrappedDataValue> wrappedDataValueList = new ArrayList<>();
    for (final WrappedWatchableObject entry : dataWatcher.getWatchableObjects()) {
      if (entry == null) continue;
      final WrappedDataWatcher.WrappedDataWatcherObject watcherObject = entry.getWatcherObject();
      wrappedDataValueList.add(
          new WrappedDataValue(
              watcherObject.getIndex(), watcherObject.getSerializer(), entry.getRawValue()));
    }
    return wrappedDataValueList;
  }

  @Override
  public @NotNull PacketContainer getHealthMetadataPacket(Player player, float health) {
    PacketContainer metadataPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

    metadataPacket.getIntegers().write(0, player.getEntityId());

    final WrappedDataWatcher.Serializer wrappedSerializer =
        WrappedDataWatcher.Registry.get(Float.class);
    // https://github.com/dmulloy2/ProtocolLib/blob/e1255edb32ca6308204ed2321974ff0c15925360/src/main/java/com/comphenix/protocol/wrappers/WrappedWatchableObject.java#L34
    // health is 9th index for this protocol version
    WrappedDataValue wrappedDataValue = new WrappedDataValue(9, wrappedSerializer, health);

    final List<WrappedDataValue> metadata = deepCloneEntityMetadata(player);
    metadata.add(wrappedDataValue);
    metadataPacket.getDataValueCollectionModifier().write(0, metadata);
    return metadataPacket;
  }

  @Override
  public void addPlayerInfoToPacket(Object packet, Object playerInfoData) {
    PacketContainer packetContainer = (PacketContainer) packet;

    final boolean isPlayerInfoRemove =
        packetContainer.getType() == PacketType.Play.Server.PLAYER_INFO_REMOVE;
    if (isPlayerInfoRemove) {
      // remove packet removes profile UUIDS
      final List<UUID> profileUUIDs = packetContainer.getSpecificModifier(List.class).read(0);
      profileUUIDs.add(((PlayerInfoData) playerInfoData).getProfileId());
      packetContainer.getSpecificModifier(List.class).write(0, profileUUIDs);
    } else {
      // ProtocolLib bug -- it considers the actions as a collection, so we actually need to write
      // to the second field
      // that actions field is only present in the non-remove variant, so we need to use the first
      // field for the remove
      // packet
      final int playersFieldIndex = 1;

      StructureModifier<List<PlayerInfoData>> playerInfoDataListsModifier =
          packetContainer.getPlayerInfoDataLists();
      List<PlayerInfoData> playerInfoDataList = playerInfoDataListsModifier.read(playersFieldIndex);
      playerInfoDataList.add((PlayerInfoData) playerInfoData);
      playerInfoDataListsModifier.write(playersFieldIndex, playerInfoDataList);
    }
  }

  @Override
  public Object spawnPlayerPacket(int entityId, UUID uuid, Location location, Player player) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);

    // ID, velocity XYZ, type
    packet.getIntegers().write(0, entityId).write(1, 0).write(2, 0).write(3, 0).write(4, 122);

    packet
        .getDoubles()
        .write(0, location.getX())
        .write(1, location.getY())
        .write(2, location.getZ());

    packet.getUUIDs().write(0, uuid);

    packet
        .getBytes()
        .write(0, (byte) (int) (location.getYaw() * 256.0F / 360.0F))
        .write(1, (byte) (int) (location.getPitch() * 256.0F / 360.0F))
        .write(2, (byte) (int) (location.getPitch() * 256.0F / 360.0F));

    return packet;
  }

  @Override
  public Object destroyEntitiesPacket(int... entityIds) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);

    // now an int list, rather than array
    packet.getIntLists().write(0, Ints.asList(entityIds));

    return packet;
  }

  @Override
  public boolean playerInfoDataListNotEmpty(Object packet) {
    PacketContainer packetContainer = (PacketContainer) packet;

    StructureModifier<List<PlayerInfoData>> playerInfoDataListsModifier =
        packetContainer.getPlayerInfoDataLists();

    // ProtocolLib bug -- it considers the actions as a collection, so we actually need to write to
    // the second field
    // that actions field is only present in the non-remove variant, so we need to use the first
    // field for the remove
    // packet
    final int fieldIndex =
        (packetContainer.getType() == PacketType.Play.Server.PLAYER_INFO_REMOVE) ? 0 : 1;
    return !playerInfoDataListsModifier.read(fieldIndex).isEmpty();
  }

  @Override
  public Material getMaterialFromBlockState(BlockState blockState) {
    return blockState.getType();
  }

  private static int RECIPE_COUNTER = 0;

  @Override
  public ShapedRecipe createShapedRecipeFromOutput(ItemStack output) {
    return new ShapedRecipe(createNamespacedKey("shapedrecipe_" + RECIPE_COUNTER++), output);
  }

  @Override
  public ShapelessRecipe createShapelessRecipeFromOutput(ItemStack output) {
    return new ShapelessRecipe(createNamespacedKey("shapedrecipe_" + RECIPE_COUNTER++), output);
  }

  @Override
  public ItemFlag asBukkit(tc.oc.pgm.util.nms.item.ItemFlag flag) {
    switch (flag) {
      case HIDE_ENCHANTS:
        return ItemFlag.HIDE_ENCHANTS;
      case HIDE_ATTRIBUTES:
        return ItemFlag.HIDE_ATTRIBUTES;
      case HIDE_UNBREAKABLE:
        return ItemFlag.HIDE_UNBREAKABLE;
      case HIDE_DESTROYS:
        return ItemFlag.HIDE_DESTROYS;
      case HIDE_PLACED_ON:
        return ItemFlag.HIDE_PLACED_ON;
      case HIDE_POTION_EFFECTS:
        return ItemFlag.HIDE_POTION_EFFECTS;
      case HIDE_DYE:
        return ItemFlag.HIDE_DYE;
      case HIDE_ARMOR_TRIM:
        return ItemFlag.HIDE_ARMOR_TRIM;
    }
    return null;
  }

  @Override
  public void setUnbreakable(ItemMeta itemMeta, boolean unbreakable) {
    itemMeta.setUnbreakable(unbreakable);
  }

  @Override
  public void setCollidesWithEntities(Player player, boolean interact) {
    player.setCollidable(interact);
  }

  @Override
  public boolean isUnbreakable(ItemMeta itemMeta) {
    return itemMeta.isUnbreakable();
  }

  private static Set<EnumWrappers.PlayerInfoAction> convertPlayerInfoAction(
      EnumPlayerInfoAction enumPlayerInfoAction) {
    switch (enumPlayerInfoAction) {
      case ADD_PLAYER:
        return ImmutableSet.of(
            EnumWrappers.PlayerInfoAction.ADD_PLAYER, EnumWrappers.PlayerInfoAction.UPDATE_LISTED);
      case UPDATE_GAME_MODE:
        return ImmutableSet.of(
            EnumWrappers.PlayerInfoAction.UPDATE_GAME_MODE,
            EnumWrappers.PlayerInfoAction.UPDATE_LISTED);
      case UPDATE_LATENCY:
        return ImmutableSet.of(
            EnumWrappers.PlayerInfoAction.UPDATE_LATENCY,
            EnumWrappers.PlayerInfoAction.UPDATE_LISTED);
      case UPDATE_DISPLAY_NAME:
        return ImmutableSet.of(
            EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME,
            EnumWrappers.PlayerInfoAction.UPDATE_LISTED);
      case REMOVE_PLAYER:
      default:
        return ImmutableSet.of(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
    }
  }

  @Override
  public Object createPlayerInfoPacket(EnumPlayerInfoAction action) {
    Set<EnumWrappers.PlayerInfoAction> playerInfoActions = convertPlayerInfoAction(action);
    // remove is a diff packet
    PacketContainer packet;
    if (playerInfoActions.contains(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER)) {
      packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO_REMOVE);
    } else {
      packet = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
      packet.getPlayerInfoActions().write(0, playerInfoActions);
    }
    return packet;
  }

  @Override
  public void clearArrowsInPlayer(Player player) {
    WrappedDataWatcher entityWatcher = WrappedDataWatcher.getEntityWatcher(player);
    entityWatcher.setObject(12, (int) 0, true);
  }

  @Override
  public void setPotionParticles(Player player, boolean enabled) {
    WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(player);

    if (enabled) {
      Collection<PotionEffect> activePotionEffects = player.getActivePotionEffects();
      for (PotionEffect potionEffect : activePotionEffects) {
        if (!potionEffect.isAmbient()) {
          dataWatcher.setObject(11, false, true);
          dataWatcher.setObject(10, potionEffect.getType().getId(), true);
          return;
        }
      }
    }
    dataWatcher.setObject(10, (int) 0, true);
    dataWatcher.setObject(11, true, true);
  }

  @Override
  public void spawnFakeArmorStand(
      Player player, int entityId, Location location, org.bukkit.util.Vector velocity) {
    PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
    packet
        .getIntegers()
        .write(0, entityId)
        .write(1, 30) // armor stand
        .write(2, (int) (velocity.getX() * 8000))
        .write(3, (int) (velocity.getY() * 8000))
        .write(4, (int) (velocity.getZ() * 8000));
    packet
        .getDoubles()
        .write(0, location.getX())
        .write(1, location.getY())
        .write(2, location.getZ());

    packet
        .getBytes()
        .write(0, (byte) (int) (location.getYaw() * 256.0F / 360.0F))
        .write(1, (byte) (int) (location.getPitch() * 256.0F / 360.0F))
        .write(2, (byte) (int) (location.getPitch() * 256.0F / 360.0F));

    packet.getUUIDs().write(0, UUID.randomUUID());

    WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
    dataWatcher.setObject(
        new WrappedDataWatcher.WrappedDataWatcherObject(
            0, WrappedDataWatcher.Registry.get(Byte.class)),
        (byte) 0x20);
    // unsure which this is supposed to be, assume its no gravity
    dataWatcher.setObject(
        new WrappedDataWatcher.WrappedDataWatcherObject(
            5, WrappedDataWatcher.Registry.get(Boolean.class)),
        true);
    dataWatcher.setObject(
        new WrappedDataWatcher.WrappedDataWatcherObject(
            15, WrappedDataWatcher.Registry.get(Byte.class)),
        (byte) 0x8);

    packet.getDataWatcherModifier().write(0, dataWatcher);

    sendPacket(player, packet);
  }

  private Material woolFromDyeColor(final DyeColor dyeColor) {
    switch (dyeColor) {
      case WHITE:
        return Material.WHITE_WOOL;
      case ORANGE:
        return Material.ORANGE_WOOL;
      case MAGENTA:
        return Material.MAGENTA_WOOL;
      case LIGHT_BLUE:
        return Material.LIGHT_BLUE_WOOL;
      case YELLOW:
        return Material.YELLOW_WOOL;
      case LIME:
        return Material.LIME_WOOL;
      case PINK:
        return Material.PINK_WOOL;
      case GRAY:
        return Material.GRAY_WOOL;
      case LIGHT_GRAY:
        return Material.LIGHT_GRAY_WOOL;
      case CYAN:
        return Material.CYAN_WOOL;
      case PURPLE:
        return Material.PURPLE_WOOL;
      case BLUE:
        return Material.BLUE_WOOL;
      case BROWN:
        return Material.BROWN_WOOL;
      case GREEN:
        return Material.GREEN_WOOL;
      case RED:
        return Material.RED_WOOL;
      case BLACK:
        return Material.BLACK_WOOL;
      default:
        return Material.WHITE_WOOL;
    }
  }

  protected enum ColorNMSWrapper {
    BLACK,
    DARK_BLUE,
    DARK_GREEN,
    DARK_AQUA,
    DARK_RED,
    DARK_PURPLE,
    GOLD,
    GRAY,
    DARK_GRAY,
    BLUE,
    GREEN,
    AQUA,
    RED,
    LIGHT_PURPLE,
    YELLOW,
    WHITE,
    OBFUSCATED,
    BOLD,
    STRIKETHROUGH,
    UNDERLINE,
    ITALIC,
    RESET
  }

  @Override
  protected void listTagAppend(Object list, Object tag) {
    nbtTagList.addIndexed(list, nbtTagList.size(list), tag);
  }

  // TODO: use plugin instead of string namespace
  private NamespacedKey createNamespacedKey(final String key) {
    return new NamespacedKey(PGM_NAMESPACE, key);
  }
}
