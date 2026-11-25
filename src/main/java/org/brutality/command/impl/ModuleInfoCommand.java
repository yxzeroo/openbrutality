package org.brutality.command.impl;

import org.brutality.Brutality;
import org.brutality.command.Command;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;

public class ModuleInfoCommand extends Command {
    @Override
    public void onCommand(String[] args, String message) {
        String str = message.substring(1).toLowerCase();
        Module module = Brutality.moduleManager.computeModule(str);
        if (module == null) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("B", MessageUtils.text5)
                    + MessageUtils.computeString("] - ", MessageUtils.text9)
                    + MessageUtils.computeString("Module not found!", MessageUtils.text13)
            );
        } else if (module.getCategory() == Category.HIDDEN) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("B", MessageUtils.text5)
                    + MessageUtils.computeString("] - ", MessageUtils.text9)
                    + MessageUtils.computeString("Module is hidden!", MessageUtils.text13)
            );
        } else if (args.length == 0) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("B", MessageUtils.text5)
                    + MessageUtils.computeString("] ", MessageUtils.text9)
                    + MessageUtils.computeString(module.getName(), MessageUtils.text15)
                    + MessageUtils.computeString(
                        " (" + (module.checkState44() ? "ON" : "OFF") + ")", module.checkState44() ? MessageUtils.text11 : MessageUtils.text13
                    )
            );

            for (Setting setting : Brutality.settingsManager.computeList(module)) {
                if (setting instanceof BooleanSetting boolSetting) {
                    ModuleManager.handleString(
                        MessageUtils.computeString("� ", MessageUtils.text7)
                            + MessageUtils.computeString(setting.getName() + ": ", MessageUtils.text8)
                            + MessageUtils.computeString(String.valueOf(boolSetting.isEnabled()), boolSetting.isEnabled() ? MessageUtils.text11 : MessageUtils.text13)
                    );
                } else if (setting instanceof NumberSetting numberSetting) {
                    ModuleManager.handleString(
                        MessageUtils.computeString("� ", MessageUtils.text7)
                            + MessageUtils.computeString(setting.getName() + ": ", MessageUtils.text8)
                            + MessageUtils.computeString(String.valueOf(numberSetting.computeDouble()), MessageUtils.text15)
                    );
                } else if (setting instanceof ModeSetting modeSetting) {
                    ModuleManager.handleString(
                        MessageUtils.computeString("� ", MessageUtils.text7)
                            + MessageUtils.computeString(setting.getName() + ": ", MessageUtils.text8)
                            + MessageUtils.computeString(modeSetting.getText(), MessageUtils.text10)
                    );
                }
            }
        } else {
            if (args.length >= 2) {
                String str2 = args[0].toLowerCase().replace(" ", "");
                String str3 = args[1].toLowerCase();
                Setting setting2 = Brutality.settingsManager
                    .computeList(module)
                    .stream()
                    .filter(setting3 -> setting3.getName().replace(" ", "").equalsIgnoreCase(str2))
                    .findFirst()
                    .orElse(null);
                if (setting2 == null) {
                    ModuleManager.handleString(
                        MessageUtils.computeString("[", MessageUtils.text9)
                            + MessageUtils.computeString("B", MessageUtils.text5)
                            + MessageUtils.computeString("] - ", MessageUtils.text9)
                            + MessageUtils.computeString("Setting not found!", MessageUtils.text13)
                    );
                    return;
                }

                if (setting2 instanceof BooleanSetting boolSetting2) {
                    if (!str3.equalsIgnoreCase("true") && !str3.equalsIgnoreCase("false")) {
                        boolSetting2.setEnabled(!boolSetting2.isEnabled());
                    } else {
                        boolSetting2.setEnabled(Boolean.parseBoolean(str3));
                    }

                    ModuleManager.handleString(
                        MessageUtils.computeString("[", MessageUtils.text9)
                            + MessageUtils.computeString("B", MessageUtils.text5)
                            + MessageUtils.computeString("] - ", MessageUtils.text9)
                            + MessageUtils.computeString(
                                setting2.getName() + " set to: " + boolSetting2.isEnabled(), boolSetting2.isEnabled() ? MessageUtils.text11 : MessageUtils.text13
                            )
                    );
                } else if (setting2 instanceof NumberSetting numberSetting2) {
                    try {
                        double d = Double.parseDouble(str3);
                        if (d >= numberSetting2.getMinValue() && d <= numberSetting2.getMaxValue()) {
                            numberSetting2.handleDouble2(d);
                            ModuleManager.handleString(
                                MessageUtils.computeString("[", MessageUtils.text9)
                                    + MessageUtils.computeString("B", MessageUtils.text5)
                                    + MessageUtils.computeString("] - ", MessageUtils.text9)
                                    + MessageUtils.computeString(setting2.getName() + " set to: " + numberSetting2.computeDouble(), MessageUtils.text15)
                            );
                        } else {
                            String str4 = MessageUtils.computeString("[", MessageUtils.text9);
                            String str5 = MessageUtils.computeString("B", MessageUtils.text5);
                            String str6 = MessageUtils.computeString("] - ", MessageUtils.text9);
                            double maxValue = numberSetting2.getMaxValue();
                            double minValue = numberSetting2.getMinValue();
                            ModuleManager.handleString(
                                str4
                                    + str5
                                    + str6
                                    + MessageUtils.computeString("Value out of range! (" + minValue + " - " + maxValue + ")", MessageUtils.text13)
                            );
                        }
                    } catch (NumberFormatException e) {
                        ModuleManager.handleString(
                            MessageUtils.computeString("[", MessageUtils.text9)
                                + MessageUtils.computeString("B", MessageUtils.text5)
                                + MessageUtils.computeString("] - ", MessageUtils.text9)
                                + MessageUtils.computeString("Invalid number format!", MessageUtils.text13)
                        );
                    }
                } else if (setting2 instanceof ModeSetting modeSetting2) {
                    if (modeSetting2.matchesString(str3)) {
                        modeSetting2.handleString(str3);
                        ModuleManager.handleString(
                            MessageUtils.computeString("[", MessageUtils.text9)
                                + MessageUtils.computeString("B", MessageUtils.text5)
                                + MessageUtils.computeString("] - ", MessageUtils.text9)
                                + MessageUtils.computeString(setting2.getName() + " set to: " + modeSetting2.getText(), MessageUtils.text10)
                        );
                    } else {
                        ModuleManager.handleString(
                            MessageUtils.computeString("[", MessageUtils.text9)
                                + MessageUtils.computeString("B", MessageUtils.text5)
                                + MessageUtils.computeString("] - ", MessageUtils.text9)
                                + MessageUtils.computeString("Invalid mode! Available modes: " + modeSetting2.computeList2(), MessageUtils.text13)
                        );
                    }
                }
            }
        }
    }

    public ModuleInfoCommand() {
        super("ModuleInfo", "Displays the module's settings and toggle state", ".<module>", "module");
    }
}
