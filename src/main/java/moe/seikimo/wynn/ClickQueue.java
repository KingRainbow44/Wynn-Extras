package moe.seikimo.wynn;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class ClickQueue {
    private final Deque<ClickType> queue
            = new ConcurrentLinkedDeque<>();
    private long ticks = 0;

    public ClickQueue() {
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    /**
     * Called every client tick.
     *
     * @param client The Minecraft client.
     */
    private void onClientTick(MinecraftClient client) {
        var player = client.player;
        if (player == null) return;

        if (this.queue.isEmpty()) {
            this.ticks = 0;
            return;
        }

        if (--this.ticks > 0) return;
        this.ticks = ModConfig.get().getCaster().getDelay();

        var next = this.queue.poll();
        if (next == ClickType.LEFT) {
            this.leftClick(client);
        } else if (next == ClickType.RIGHT) {
            this.rightClick(client);
        }
    }

    /**
     * Performs a left click action.
     *
     * @param client The Minecraft client.
     */
    private void leftClick(MinecraftClient client) {
        var player = client.player;
        if (player == null) return;

        player.swingHand(Hand.MAIN_HAND);
    }

    /**
     * Performs a right click action.
     *
     * @param client The Minecraft client.
     */
    private void rightClick(MinecraftClient client) {
        var interactionManager = client.interactionManager;
        if (interactionManager == null) return;

        var world = client.world;
        if (world == null) return;

        var player = client.player;
        if (player == null) return;

        interactionManager.sendSequencedPacket(world, id -> new PlayerInteractBlockC2SPacket(
                Hand.MAIN_HAND,
                new BlockHitResult(
                        player.getPos(),
                        player.getHorizontalFacing(),
                        player.getBlockPos().add(0, 1, 0),
                false
                ),
                id
        ));
    }

    /**
     * @return Whether the queue is empty.
     */
    public boolean isEmpty() {
        return this.queue.isEmpty();
    }

    /**
     * Adds a click to the queue.
     *
     * @param type The type of click.
     */
    public void click(ClickType type) {
        this.queue.offer(type);
    }
}
