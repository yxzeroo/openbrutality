package org.brutality.util.contract;

import java.util.Objects;
import net.minecraft.inventory.Slot;

public class Contract {
    private int slotId2;
    private KillsType killsType;
    private String displayName2;
    private boolean flag;
    private String text;

    public KillsType getKillsType() {
        return this.killsType;
    }

    public int getSlotId() {
        return this.slotId2;
    }

    public int getPriority() {
        return this.killsType.getTaskId();
    }

    public String getDisplayName() {
        return this.displayName2;
    }

    public boolean isFlag() {
        return this.flag;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.displayName2, this.flag, this.text, this.killsType, this.slotId2);
    }

    public String computeString() {
        int priority = this.getPriority();
        String str = this.flag ? " (Vile)" : " (No vile)";
        String text = this.text;
        String str2 = this.killsType.name();
        int slotId2 = this.slotId2;
        String str3 = str2;
        String str4 = text;
        String str5 = str;
        int n = priority;
        return "Priority " + n + str5 + ": " + str4 + " (" + str3 + ") (Slot ID: " + slotId2 + ")";
    }

    public Contract(Slot slot) {
        if (!IsVileContractChoiceHelper.matchesSlot(slot)) {
            throw new IllegalArgumentException("Slot is not a contract slot");
        }
        this.displayName2 = slot.getStack().getDisplayName();
        this.flag = IsVileContractChoiceHelper.isVileContractChoice(slot);
        this.text = IsVileContractChoiceHelper.computeString(slot);
        if (this.text == null) {
            throw new IllegalArgumentException("Could not extract task name from contract slot");
        }
        this.killsType = KillsType.computeKillsType(this.text);
        this.slotId2 = slot.slotNumber;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        int slotId2 = this.slotId2;
        KillsType killsType = this.killsType;
        String text = this.text;
        boolean flag = this.flag;
        String displayName2 = this.displayName2;
        return "Contract{displayName='" + displayName2 + "', isVileContract=" + flag + ", taskName='" + text + "', task=" + killsType + ", slotId=" + slotId2 + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && this.getClass() == obj.getClass()) {
            Contract contract = (Contract)obj;
            return this.flag == contract.flag
                && this.slotId2 == contract.slotId2
                && Objects.equals(this.displayName2, contract.displayName2)
                && Objects.equals(this.text, contract.text)
                && this.killsType == contract.killsType;
        }
        return false;
    }
}
