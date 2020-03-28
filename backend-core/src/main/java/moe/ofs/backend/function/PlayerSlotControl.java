package moe.ofs.backend.function;

@FunctionalInterface
public interface PlayerSlotControl {
    SlotChangeResult validate(SlotChangeRequest slotChangeRequest);
}
