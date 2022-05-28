package net.mattwamm.froststruck.entities;

import com.google.common.collect.Lists;
import net.mattwamm.froststruck.registries.EntityRegistry;
import net.mattwamm.froststruck.registries.ItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SledEntity extends Entity {

    private boolean pressingForward;
    private boolean pressingLeft;
    private boolean pressingRight;
    private boolean pressingBack;
    private double sledYaw;
    private double sledPitch;
    private double x;
    private double y;
    private double z;
    private float yawVelocity;
    private double fallVelocity;
    private float velocityDecay;
    private int field_7708;
    private double waterLevel;
    private SledEntity.Location location;
    private SledEntity.Location lastLocation;
    private float ticksUnderwater;
    @Nullable
    private Entity holdingEntity;
    private int holdingEntityId;
    @Nullable
    private NbtCompound leashNbt;

    private static final TrackedData<Integer> SLED_TYPE = DataTracker.registerData(SledEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(SledEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(SledEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(SledEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public SledEntity(EntityType<? extends SledEntity> type, World world) {
        super(type, world);
        this.stepHeight = 0.6f;
    }
    public SledEntity(World world, double x, double y, double z) {
        this(EntityRegistry.SLED, world);
        this.setPosition(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.stepHeight = 0.6f;
    }
    public void setSledType(SledEntity.Type type) {
        this.dataTracker.set(SLED_TYPE, type.ordinal());
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.contains("Type", 8)) {
            this.setSledType(SledEntity.Type.getType(nbt.getString("Type")));
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putString("Type", this.getSledType().getName());
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        //actions: sit, open chest, put chest, take off chest, tie rope to sled
        //depends on: if chest on sled{CAN'T SIT, CAN TAKE OFF CHEST, CAN OPEN CHEST} if chest is off{CAN SIT} can always TIE ROPE TO SLED
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }
        if (this.ticksUnderwater < 60.0f) {
            if (!this.world.isClient) {
                if (this.getHoldingEntity() == player) {
                    this.detachLeash(true, !player.getAbilities().creativeMode);
                    return ActionResult.success(this.world.isClient);
                }
                if(player.isHolding(Items.LEAD))
                {
                    ActionResult actionResult = this.interactWithItem(player, hand);
                    if (actionResult.isAccepted()) {
                        return actionResult;
                    }
                }
                return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private void readLeashNbt() {
        if (this.leashNbt != null && this.world instanceof ServerWorld) {
            if (this.leashNbt.containsUuid("UUID")) {
                UUID uUID = this.leashNbt.getUuid("UUID");
                Entity entity = ((ServerWorld)this.world).getEntity(uUID);
                if (entity != null) {
                    this.attachLeash(entity, true);
                    return;
                }
            } else if (this.leashNbt.contains("X", 99) && this.leashNbt.contains("Y", 99) && this.leashNbt.contains("Z", 99)) {
                BlockPos blockPos = NbtHelper.toBlockPos(this.leashNbt);
                this.attachLeash(LeashKnotEntity.getOrCreate(this.world, blockPos), true);
                return;
            }
            if (this.age > 100) {
                this.dropItem(Items.LEAD);
                this.leashNbt = null;
            }
        }
    }

    public void detachLeash(boolean sendPacket, boolean dropItem) {
        if (this.holdingEntity != null) {
            this.holdingEntity = null;
            this.leashNbt = null;
            if (!this.world.isClient && dropItem) {
                this.dropItem(Items.LEAD);
            }
            if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
                ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, null));
            }
        }
    }
    protected void updateLeash() {
        if (this.leashNbt != null) {
            this.readLeashNbt();
        }
        if (this.holdingEntity == null) {
            return;
        }
        if (!this.isAlive() || !this.holdingEntity.isAlive()) {
            this.detachLeash(true, true);
        }
    }
    private ActionResult interactWithItem(PlayerEntity player, Hand hand) {
        ActionResult actionResult;
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.LEAD) && this.canBeLeashedBy(player)) {
            this.attachLeash(player, true);
            itemStack.decrement(1);
            return ActionResult.success(this.world.isClient);
        }
        return ActionResult.PASS;
    }

    @Nullable
    public Entity getHoldingEntity() {
        if (this.holdingEntity == null && this.holdingEntityId != 0 && this.world.isClient) {
            this.holdingEntity = this.world.getEntityById(this.holdingEntityId);
        }
        return this.holdingEntity;
    }

    public void attachLeash(Entity entity, boolean sendPacket) {
        this.holdingEntity = entity;
        this.leashNbt = null;
        if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, this.holdingEntity));
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
    }
    public boolean canBeLeashedBy(PlayerEntity player) {
        return !this.isLeashed();
    }

    public void setHoldingEntityId(int id) {
        this.holdingEntityId = id;
        this.detachLeash(false, false);
    }

    public boolean isLeashed() {
        return this.holdingEntity != null;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < 2 && !this.isSubmergedIn(FluidTags.WATER);
    }

    @Override
    public boolean isSubmergedInWater() {
        return this.location == SledEntity.Location.UNDER_WATER || this.location == SledEntity.Location.UNDER_FLOWING_WATER;
    }


    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    public static boolean canCollide(Entity entity, Entity other) {
        return (other.isCollidable() || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(DAMAGE_WOBBLE_TICKS, 0);
        this.dataTracker.startTracking(DAMAGE_WOBBLE_SIDE, 1);
        this.dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, 0.0f);
        this.dataTracker.startTracking(SLED_TYPE, SledEntity.Type.OAK.ordinal());
    }

    @Override
    public double getMountedHeightOffset() {
        return 0.2f; // height the player sits at on the sled determined by the model
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (entity instanceof SledEntity) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.pushAwayFrom(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.pushAwayFrom(entity);
        }
    }
    @Override
    public void animateDamage() {
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() * 11.0f);
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    public boolean collidesWith(Entity other) {
        return SledEntity.canCollide(this, other);
    }

    @Override
    public Direction getMovementDirection() {
        return this.getHorizontalFacing().rotateYClockwise();
    }


    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.world.isClient || this.isRemoved()) {
            return true;
        }
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0f);
        this.scheduleVelocityUpdate();
        this.emitGameEvent(GameEvent.ENTITY_DAMAGED, source.getAttacker());
        boolean creative = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity) source.getAttacker()).getAbilities().creativeMode; // check if player is in creative mode to break instantly
        if (creative || this.getDamageWobbleStrength() > 40.0f) {
            if (!creative && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                this.dropItem(this.asItem());
            }
            this.discard();
        }
        return true;
    }
    public void setDamageWobbleSide(int side) {
        this.dataTracker.set(DAMAGE_WOBBLE_SIDE, side);
    }
    public int getDamageWobbleSide() {
        return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
    }
    public void setDamageWobbleTicks(int wobbleTicks) {
        this.dataTracker.set(DAMAGE_WOBBLE_TICKS, wobbleTicks);
    }
    public int getDamageWobbleTicks() {
        return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
    }
    public float getDamageWobbleStrength() {
        return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH).floatValue();
    }
    public void setDamageWobbleStrength(float wobbleStrength) {this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, Float.valueOf(wobbleStrength));}
    public Item asItem() {
        switch (this.getSledType()) {
            default: {
                return ItemRegistry.OAK_SLED;
            }
            case SPRUCE: {
                return ItemRegistry.SPRUCE_SLED;
            }
            case BIRCH: {
                return ItemRegistry.BIRCH_SLED;
            }
            case JUNGLE: {
                return ItemRegistry.JUNGLE_SLED;
            }
            case ACACIA: {
                return ItemRegistry.ACACIA_SLED;
            }
            case DARK_OAK:
                return ItemRegistry.DARK_OAK_SLED;
        }
    }

    @Override
    public void tick() {
        this.lastLocation = this.location;
        this.location = this.checkLocation();
        this.ticksUnderwater = this.location == Location.UNDER_WATER || this.location == Location.UNDER_FLOWING_WATER ? (this.ticksUnderwater += 1.0f) : 0.0f;
        if (!this.world.isClient && this.ticksUnderwater >= 60.0f) {
            this.removeAllPassengers();
        }
        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }
        if (this.getDamageWobbleStrength() > 0.0f) {
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0f);
        }
        this.updateLeash();
        super.tick();
        this.method_7555();
        if (this.isLogicalSideForUpdatingMovement()) {
            this.updateVelocity();
            this.Movement();
            this.move(MovementType.SELF, this.getVelocity());
        }

        this.checkBlockCollision();
        List<Entity> i = this.world.getOtherEntities(this, this.getBoundingBox().expand(0.2f, -0.01f, 0.2f), EntityPredicates.canBePushedBy(this));
        if (!i.isEmpty()) {
            boolean soundEvent = !this.world.isClient && !(this.getPrimaryPassenger() instanceof PlayerEntity);
            for (int vec3d = 0; vec3d < i.size(); ++vec3d) {
                Entity d = i.get(vec3d);
                if (d.hasPassenger(this)) continue;
                if (soundEvent && this.getPassengerList().size() < 2 && !d.hasVehicle() && d.getWidth() < this.getWidth() && d instanceof LivingEntity && !(d instanceof WaterCreatureEntity) && !(d instanceof PlayerEntity)) {
                    d.startRiding(this);
                    continue;
                }
                this.pushAwayFrom(d);
            }
        }
        if(!this.hasPlayerRider()){
            this.velocityDecay = 0.3f;
        }

    }


    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        return this.getFirstPassenger();
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        double e;
        Vec3d vec3d = SledEntity.getPassengerDismountOffset(this.getWidth() * MathHelper.SQUARE_ROOT_OF_TWO, passenger.getWidth(), passenger.getYaw());
        double d = this.getX() + vec3d.x;
        BlockPos blockPos = new BlockPos(d, this.getBoundingBox().maxY, e = this.getZ() + vec3d.z);
        BlockPos blockPos2 = blockPos.down();
        if (!this.world.isWater(blockPos2)) {
            double g;
            ArrayList<Vec3d> list = Lists.newArrayList();
            double f = this.world.getDismountHeight(blockPos);
            if (Dismounting.canDismountInBlock(f)) {
                list.add(new Vec3d(d, (double)blockPos.getY() + f, e));
            }
            if (Dismounting.canDismountInBlock(g = this.world.getDismountHeight(blockPos2))) {
                list.add(new Vec3d(d, (double)blockPos2.getY() + g, e));
            }
            for (EntityPose entityPose : passenger.getPoses()) {
                for (Vec3d vec3d2 : list) {
                    if (!Dismounting.canPlaceEntityAt(this.world, vec3d2, passenger, entityPose)) continue;
                    passenger.setPose(entityPose);
                    return vec3d2;
                }
            }
        }
        return super.updatePassengerForDismount(passenger);
    }


    @Override
    public void onPassengerLookAround(Entity passenger) {
        this.copyEntityData(passenger);
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        float f = 0.0f;
        float g = (float)((this.isRemoved() ? (double)0.01f : this.getMountedHeightOffset()) + passenger.getHeightOffset());
        if (this.getPassengerList().size() > 1) {
            int i = this.getPassengerList().indexOf(passenger);
            f = i == 0 ? 0.2f : -0.6f;
            if (passenger instanceof AnimalEntity) {
                f += 0.2f;
            }
        }
        Vec3d vec3d = new Vec3d(f, 0.0, 0.0).rotateY(-this.getYaw() * ((float)Math.PI / 180) - 1.5707964f);
        passenger.setPosition(this.getX() + vec3d.x, this.getY() + (double)g, this.getZ() + vec3d.z);
        passenger.setYaw(passenger.getYaw() + this.yawVelocity);
        passenger.setHeadYaw(passenger.getHeadYaw() + this.yawVelocity);
        this.copyEntityData(passenger);
        if (passenger instanceof AnimalEntity && this.getPassengerList().size() > 1) {
            int j = passenger.getId() % 2 == 0 ? 90 : 270;
            passenger.setBodyYaw(((AnimalEntity)passenger).bodyYaw + (float)j);
            passenger.setHeadYaw(passenger.getHeadYaw() + (float)j);
        }
    }

    protected void copyEntityData(Entity entity) {
        entity.setBodyYaw(this.getYaw());
        float f = MathHelper.wrapDegrees(entity.getYaw() - this.getYaw());
        float g = MathHelper.clamp(f, -105.0f, 105.0f);
        entity.prevYaw += g - f;
        entity.setYaw(entity.getYaw() + g - f);
        entity.setHeadYaw(entity.getYaw());
    }

    private void Movement()
    {
        if (!this.hasPassengers()) {
            return;
        }
        float f = 0.0f;
        if (this.pressingLeft) {
            this.yawVelocity -= 1.0f;
        }
        if (this.pressingRight) {
            this.yawVelocity += 1.0f;
        }
        if (this.pressingRight != this.pressingLeft && !this.pressingBack) {
            f += 0.005f;
        }
        this.setYaw(this.getYaw() + this.yawVelocity);
        if(this.pressingForward)
            f += 0.004f;
        if (this.pressingBack) {
            f -= 0.005f;
        }
        this.setVelocity(this.getVelocity().add(MathHelper.sin(-this.getYaw() * ((float)Math.PI / 180)) * f, 0.0, MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)) * f));
    }

    private void updateVelocity() {
        double d = -0.04f;
        double e = this.hasNoGravity() ? 0.0 : (double)-0.4f;
        double f = 0.0;
        this.velocityDecay = 0.05f;
        if (this.lastLocation == SledEntity.Location.IN_AIR && this.location != SledEntity.Location.IN_AIR && this.location != SledEntity.Location.ON_LAND) {
            this.waterLevel = this.getBodyY(1.0);
            this.setPosition(this.getX(), (double)(this.method_7544() - this.getHeight()) + 0.101, this.getZ());
            this.setVelocity(this.getVelocity().multiply(1.0, 0.0, 1.0));
            this.fallVelocity = 0.0;
            this.location = SledEntity.Location.IN_WATER;
        } else {
            if (this.location == SledEntity.Location.IN_WATER) {
                this.velocityDecay = 0.2f;
            } else if (this.location == SledEntity.Location.UNDER_FLOWING_WATER) {
                e = -7.0E-4;
                this.velocityDecay = 0.2f;
            } else if (this.location == SledEntity.Location.UNDER_WATER) {
                f = 0.01f;
                this.velocityDecay = 0.45f;
            } else if (this.location == SledEntity.Location.IN_AIR) {
                this.velocityDecay = 0.9f;
            } else if (this.location == SledEntity.Location.ON_LAND) {
                this.velocityDecay = 0.99f;
            }
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.x * (double)this.velocityDecay, vec3d.y + e, vec3d.z * (double)this.velocityDecay);
            this.yawVelocity *= this.velocityDecay;
//            if (f > 0.0) {
//                Vec3d vec3d2 = this.getVelocity();
//                this.setVelocity(vec3d2.x, (vec3d2.y + f * 0.06153846016296973) * 0.75, vec3d2.z);
//            }
        }
    }

    public float method_7544() {
        Box box = this.getBoundingBox();
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.maxY);
        int l = MathHelper.ceil(box.maxY - this.fallVelocity);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        block0: for (int o = k; o < l; ++o) {
            float f = 0.0f;
            for (int p = i; p < j; ++p) {
                for (int q = m; q < n; ++q) {
                    mutable.set(p, o, q);
                    FluidState fluidState = this.world.getFluidState(mutable);
                    if (fluidState.isIn(FluidTags.WATER)) {
                        f = Math.max(f, fluidState.getHeight(this.world, mutable));
                    }
                    if (f >= 1.0f) continue block0;
                }
            }
            if (!(f < 1.0f)) continue;
            return (float)mutable.getY() + f;
        }
        return l + 1;
    }


    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.sledYaw = yaw;
        this.sledPitch = pitch;
        this.field_7708 = 10;
    }


    private void method_7555() {
        if (this.isLogicalSideForUpdatingMovement()) {
            this.field_7708 = 0;
            this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
        }
        if (this.field_7708 <= 0) {
            return;
        }
        double d = this.getX() + (this.x - this.getX()) / (double)this.field_7708;
        double e = this.getY() + (this.y - this.getY()) / (double)this.field_7708;
        double f = this.getZ() + (this.z - this.getZ()) / (double)this.field_7708;
        double g = MathHelper.wrapDegrees(this.sledYaw - (double)this.getYaw());
        this.setYaw(this.getYaw() + (float)g / (float)this.field_7708);
        this.setPitch(this.getPitch() + (float)(this.sledPitch - (double)this.getPitch()) / (float)this.field_7708);
        --this.field_7708;
        this.setPosition(d, e, f);
        this.setRotation(this.getYaw(), this.getPitch());
    }

    private Location checkLocation() {
        SledEntity.Location location = this.getUnderWaterLocation();
        if (location != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return location;
        }
        if (this.checkSledInWater()) {
            return SledEntity.Location.IN_WATER;
        }
        float f = this.method_7548();
        if (f > 0.0f) {
            return SledEntity.Location.ON_LAND;
        }
        return SledEntity.Location.IN_AIR;
    }
    public float method_7548() {
        Box box = this.getBoundingBox();
        Box box2 = new Box(box.minX, box.minY - 0.001, box.minZ, box.maxX, box.minY, box.maxZ);
        int i = MathHelper.floor(box2.minX) - 1;
        int j = MathHelper.ceil(box2.maxX) + 1;
        int k = MathHelper.floor(box2.minY) - 1;
        int l = MathHelper.ceil(box2.maxY) + 1;
        int m = MathHelper.floor(box2.minZ) - 1;
        int n = MathHelper.ceil(box2.maxZ) + 1;
        VoxelShape voxelShape = VoxelShapes.cuboid(box2);
        float f = 0.0f;
        int o = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int p = i; p < j; ++p) {
            for (int q = m; q < n; ++q) {
                int r = (p == i || p == j - 1 ? 1 : 0) + (q == m || q == n - 1 ? 1 : 0);
                if (r == 2) continue;
                for (int s = k; s < l; ++s) {
                    if (r > 0 && (s == k || s == l - 1)) continue;
                    mutable.set(p, s, q);
                    BlockState blockState = this.world.getBlockState(mutable);
                    f += blockState.getBlock().getSlipperiness();
                    ++o;
                }
            }
        }
        return f / (float)o;
    }


    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        this.fallVelocity = this.getVelocity().y;
        if (this.hasVehicle()) {
            return;
        }
        if (onGround) {
            this.velocityDecay = 0.05f;


            if (this.fallDistance > 3.0f) {
                if (this.location != SledEntity.Location.ON_LAND) {
                    this.onLanding();
                    return;
                }
            }
            this.onLanding();
        } else if (!this.world.getFluidState(this.getBlockPos().down()).isIn(FluidTags.WATER) && heightDifference < 0.0) {
            this.fallDistance -= (float)heightDifference;
        }
    }

    @Override
    public void onLanding(){
        if(this.fallDistance > 0.2f)
        {
            //Vec3d currentVelocity = this.getVelocity();
            float f = 0.25f;
            this.setVelocity(MathHelper.sin(-this.getYaw() * ((float)Math.PI / 180)) * f, 0,MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)) * f);
            this.setVelocity(this.getVelocity().add(MathHelper.sin(-this.getYaw() * ((float)Math.PI / 180)) * f, 0.0, MathHelper.cos(this.getYaw() * ((float)Math.PI / 180)) * f));
        }
        this.fallDistance = 0;
    }



    @Nullable
    private SledEntity.Location getUnderWaterLocation() {
        Box box = this.getBoundingBox();
        double d = box.maxY + 0.001;
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.maxY);
        int l = MathHelper.ceil(d);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        boolean bl = false;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int o = i; o < j; ++o) {
            for (int p = k; p < l; ++p) {
                for (int q = m; q < n; ++q) {
                    mutable.set(o, p, q);
                    FluidState fluidState = this.world.getFluidState(mutable);
                    if (!fluidState.isIn(FluidTags.WATER) || !(d < (double)((float)mutable.getY() + fluidState.getHeight(this.world, mutable)))) continue;
                    if (fluidState.isStill()) {
                        bl = true;
                        continue;
                    }
                    return SledEntity.Location.UNDER_FLOWING_WATER;
                }
            }
        }
        return bl ? SledEntity.Location.UNDER_WATER : null;
    }

    private boolean checkSledInWater() {
        Box box = this.getBoundingBox();
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.minY + 0.001);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        boolean bl = false;
        this.waterLevel = -1.7976931348623157E308;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int o = i; o < j; ++o) {
            for (int p = k; p < l; ++p) {
                for (int q = m; q < n; ++q) {
                    mutable.set(o, p, q);
                    FluidState fluidState = this.world.getFluidState(mutable);
                    if (!fluidState.isIn(FluidTags.WATER)) continue;
                    float f = (float)p + fluidState.getHeight(this.world, mutable);
                    this.waterLevel = Math.max((double)f, this.waterLevel);
                    bl |= box.minY < (double)f;
                }
            }
        }
        return bl;
    }

    public SledEntity.Type getSledType() {
        return SledEntity.Type.getType(this.dataTracker.get(SLED_TYPE));
    }
    public static enum Type {
        OAK(Blocks.OAK_PLANKS, "oak"),
        SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"),
        BIRCH(Blocks.BIRCH_PLANKS, "birch"),
        JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"),
        ACACIA(Blocks.ACACIA_PLANKS, "acacia"),
        DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");

        private final String name;
        private final Block baseBlock;

        private Type(Block baseBlock, String name) {
            this.name = name;
            this.baseBlock = baseBlock;
        }

        public String getName() {
            return this.name;
        }
        public Block getBaseBlock() {
            return this.baseBlock;
        }

        public String toString() {
            return this.name;
        }

        public static SledEntity.Type getType(int type) {
            SledEntity.Type[] types = SledEntity.Type.values();
            if (type < 0 || type >= types.length) {
                type = 0;
            }
            return types[type];
        }

        public static SledEntity.Type getType(String name) {
            SledEntity.Type[] types = SledEntity.Type.values();
            for (int i = 0; i < types.length; ++i) {
                if (!types[i].getName().equals(name)) continue;
                return types[i];
            }
            return types[0];
        }
    }
    public void setInputs(boolean pressingLeft, boolean pressingRight,boolean pressingForward , boolean pressingBack) {
        this.pressingLeft = pressingLeft;
        this.pressingRight = pressingRight;
        this.pressingBack = pressingBack;
        this.pressingForward = pressingForward;
    }

    public enum Location {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR;

    }

}
