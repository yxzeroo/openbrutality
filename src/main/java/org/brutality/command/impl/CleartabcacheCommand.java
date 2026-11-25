package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.mellow.Mellow;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class CleartabcacheCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        Mellow.lookupMap.clear();
        ModuleManager.handleString(
            MessageUtils.computeString("[", MessageUtils.text9)
                + MessageUtils.computeString("F", MessageUtils.text12)
                + MessageUtils.computeString("] ", MessageUtils.text9)
                + MessageUtils.computeString("Tab cache has been wiped", MessageUtils.text11)
        );
    }

    public CleartabcacheCommand() {
        super("cleartabcache", "Clear tablist cache", ".cleartabcache");
    }
}
