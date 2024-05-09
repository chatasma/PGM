package tc.oc.pgm.util.nms.reflect;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public interface Refl {

  @Reflect.CB("inventory.CraftMetaItem.unhandledTags")
  Map<String, Object> getUnhandledTags(ItemMeta item);

  @Reflect.CB("inventory.CraftMetaSkull.profile")
  void setSkullProfile(SkullMeta meta, Object profile);

  @Reflect.CB("entity.CraftPlayer.getHandle()")
  Object getPlayerHandle(Player player);

  @Reflect.CB("entity.CraftEntity.getHandle()")
  Object getEntityHandle(Entity entity);

  @Reflect.NMS("EntityFireball.dirX")
  void setFireballDirX(Object fireball, double amount);

  @Reflect.NMS("EntityFireball.dirY")
  void setFireballDirY(Object fireball, double amount);

  @Reflect.NMS("EntityFireball.dirZ")
  void setFireballDirZ(Object fireball, double amount);

  @Reflect.NMS("world.entity.Entity.dM()")
  @Reflect.NMS("Entity.getWorld()")
  Object getNmsWorldFromEntity(Object handle);

  @Reflect.NMS("world.level.GeneratorAccess.L()")
  Object getChunkSourceFromNmsWorld(Object handle);

  @Reflect.NMS("server.level.ChunkProviderServer.a")
  Object getChunkMapFromChunkSource(Object handle);

  @Reflect.NMS("server.level.PlayerChunkMap.K")
  Map getEntityMapFromChunkMap(Object handle);

  @Reflect.NMS("server.level.PlayerChunkMap$EntityTracker.f")
  Set getSeenByFromEntityTracker(Object handle);

  @Reflect.NMS("server.network.ServerPlayerConnection.p()")
  Object getNmsPlayerFromConnection(Object handle);

  @Reflect.NMS("world.entity.Entity.aj()")
  @Reflect.NMS("Entity.getId()")
  int getEntityId(Object handle);

  @Reflect.NMS("WorldServer.getTracker()")
  Object getEntityTracker(Object worldServer);

  @Reflect.NMS("EntityTracker.trackedEntities")
  Object getTrackedEntities(Object entityTracker);

  @Reflect.NMS(value = "IntHashMap.get()", parameters = int.class)
  Object getIntHashMapMethod(Object intHashMap, int id);

  @Reflect.NMS("EntityTrackerEntry.trackedPlayers")
  Set getTrackedPlayers(Object trackerEntry);

  @Reflect.NMS("server.level.EntityPlayer.getBukkitEntity()")
  @Reflect.NMS("EntityPlayer.getBukkitEntity()")
  Player getBukkitPlayer(Object nmsHandle);

  @Reflect.NMS("EntityPlayer.ping")
  int getPlayerPing(Object handle);

  @Reflect.CB("CraftWorld.getHandle()")
  Object getWorldHandle(World world);

  @Reflect.NMS("world.level.World.X()")
  @Reflect.NMS("World.getTime()")
  long getWorldTime(Object handle);

  @Reflect.CB("CraftServer.getHandle()")
  Object getCraftServerHandle(Server server);

  @Reflect.CB("entity.CraftLivingEntity.getHandle()")
  Object getCraftEntityHandle(LivingEntity entity);

  @Reflect.NMS("EntityLiving.getAbsorptionHearts()")
  float getAbsorptionHearts(Object entity);

  @Reflect.NMS(value = "world.entity.EntityLiving.y()", parameters = float.class)
  @Reflect.NMS(value = "EntityLiving.setAbsorptionHearts()", parameters = float.class)
  void setAbsorptionHearts(Object entity, float hearts);

  @Reflect.NMS("server.players.PlayerList.c()")
  @Reflect.NMS("DedicatedPlayerList.getServer()")
  Object getNMSServer(Object handle);

  @Reflect.NMS(value = "MinecraftServer.a()", parameters = Callable.class)
  void addCallableToMainThread(Object nmsServer, Callable<Object> callable);

  // 1.20
  @Reflect.NMS(value = "server.MinecraftServer.c()", parameters = Runnable.class)
  void executeRunnable(Object nmsServer, Runnable runnable);

  @Reflect.NMS(value = "Item.canDestroySpecialBlock()", parameters = IBlockData.class)
  boolean canDestroySpecialBlock(Object nmsItem, Object blockData);

  @Reflect.NMS("Material.isAlwaysDestroyable()")
  boolean isAlwaysDestroyable(Object material);

  @Reflect.CB("inventory.CraftItemStack")
  interface CraftItemStack {
    @Reflect.StaticMethod(value = "asCraftCopy", parameters = ItemStack.class)
    ItemStack asCraftCopy(ItemStack itemStack);
  }

  @Reflect.NMS("nbt.NBTBase")
  @Reflect.NMS("NBTBase")
  interface NBTBase {}

  @Reflect.NMS("nbt.NBTTagString")
  @Reflect.NMS("NBTTagString")
  interface NBTTagString {

    @Reflect.StaticMethod(value = "a", parameters = String.class)
    @Reflect.Constructor(String.class)
    Object build(String string);

    @Reflect.Method("t_")
    @Reflect.Method("a_")
    @Reflect.Method("c_")
    @Reflect.Method("asString")
    String getString(Object item);
  };

  @Reflect.NMS("nbt.NBTTagList")
  @Reflect.NMS("NBTTagList")
  interface NBTTagList {

    @Reflect.Field("c")
    @Reflect.Field("list")
    Object getListField(Object self);

    @Reflect.Constructor
    Object build();

    @Reflect.Method("size")
    int size(Object self);

    @Reflect.Method(value = "p", parameters = int.class)
    @Reflect.Method(value = "get", parameters = int.class)
    Object get(Object self, int index);

    @Reflect.Method(value = "add", parameters = NBTBase.class)
    void add(Object self, Object value);

    // 1.20
    @Reflect.Method(
        value = "c",
        parameters = {int.class, NBTBase.class})
    void addIndexed(Object self, int idx, Object value);

    @Reflect.Method("isEmpty")
    boolean isEmpty(Object self);
  }

  @Reflect.NMS("nbt.NBTTagCompound")
  @Reflect.NMS("NBTTagCompound")
  interface NBTTagCompound {

    @Reflect.Constructor
    Object build();

    @Reflect.Method(value = "l", parameters = String.class)
    @Reflect.Method(value = "getString", parameters = String.class)
    String getString(Object self, String parameter);

    @Reflect.Method(value = "i", parameters = String.class)
    @Reflect.Method(value = "getLong", parameters = String.class)
    long getLong(Object self, String parameter);

    @Reflect.Method(value = "k", parameters = String.class)
    @Reflect.Method(value = "getDouble", parameters = String.class)
    double getDouble(Object self, String parameter);

    @Reflect.Method(value = "h", parameters = String.class)
    @Reflect.Method(value = "getInt", parameters = String.class)
    int getInt(Object self, String parameter);

    @Reflect.Method(
        value = "a",
        parameters = {String.class, String.class})
    @Reflect.Method(
        value = "setString",
        parameters = {String.class, String.class})
    void setString(Object self, String parameter, String value);

    @Reflect.Method(
        value = "a",
        parameters = {String.class, long.class})
    @Reflect.Method(
        value = "setLong",
        parameters = {String.class, long.class})
    void setLong(Object self, String parameter, long value);

    @Reflect.Method(
        value = "a",
        parameters = {String.class, double.class})
    @Reflect.Method(
        value = "setDouble",
        parameters = {String.class, double.class})
    void setDouble(Object self, String parameter, double value);

    @Reflect.Method(
        value = "a",
        parameters = {String.class, int.class})
    @Reflect.Method(
        value = "setInt",
        parameters = {String.class, int.class})
    void setInt(Object self, String parameter, int value);
  }

  @Reflect.CB("util.CraftMagicNumbers")
  interface CraftMagicNumbers {
    @Reflect.StaticMethod(value = "getItem", parameters = Material.class)
    Object getItem(Material material);

    @Reflect.StaticMethod(value = "getBlock", parameters = Material.class)
    Object getBlock(Material material);
  }

  @Reflect.NMS("Block")
  interface Block {

    @Reflect.StaticMethod(value = "getByName", parameters = String.class)
    Object getBlockByName(String name);

    @Reflect.StaticMethod(value = "getId", parameters = Block.class)
    int getId(Object self);

    @Reflect.Method("getBlockData")
    Object getBlockData(Object self);

    @Reflect.Method(value = "q", parameters = IBlockData.class)
    Object getMaterial(Object self, Object blockData);
  }

  @Reflect.NMS("IBlockData")
  interface IBlockData {
    @Reflect.Method("getMaterial")
    Object getMaterial(Object self);
  }
}
