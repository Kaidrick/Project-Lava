package moe.ofs.backend.function.slotcontrol;

@FunctionalInterface
public interface PlayerSlotControl {
    SlotChangeResult validate(SlotChangeRequest slotChangeRequest);
}
