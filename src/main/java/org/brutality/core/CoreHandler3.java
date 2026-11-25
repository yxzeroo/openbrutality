package org.brutality.core;

import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public abstract class CoreHandler3 implements ICommand {
    @Override
    public abstract List<String> getCommandAliases();

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender iCommandSender) {
        return true;
    }

    @Override
    public abstract String getCommandName();

    public abstract String getText();

    @Override
    public boolean isUsernameIndex(String[] parts, int n) {
        return false;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender iCommandSender, String[] parts, BlockPos pos) {
        return Collections.emptyList();
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/" + this.getCommandName();
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] parts) throws CommandException {
    }

    public int compareTo(ICommand iCommand) {
        return this.getCommandName().compareTo(iCommand.getCommandName());
    }
}
