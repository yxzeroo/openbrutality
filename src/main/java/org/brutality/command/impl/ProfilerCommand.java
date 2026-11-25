package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.performance.GetTopSlowestHelper;
import org.brutality.utils.performance.PrintCopyPasteReportHelper;

public class ProfilerCommand extends Command {
    private void handleInt(int n) {
        GetTopSlowestHelper.Entry2[] entry2s = GetTopSlowestHelper.getTopSlowest(n).toArray(new GetTopSlowestHelper.Entry2[0]);
        MessageUtils.x(MessageUtils.text15 + "Top " + n + " Slowest Modules:");

        for (int i = 0; i < entry2s.length; i++) {
            GetTopSlowestHelper.Entry2 entry2 = entry2s[i];
            String str = i == 0 ? MessageUtils.text13 : (i < 3 ? MessageUtils.text7 : MessageUtils.text15);
            String str2 = MessageUtils.text8;
            String str3 = MessageUtils.text16;
            String str4 = MessageUtils.text16;
            String str5 = MessageUtils.text8;
            String str6 = MessageUtils.text11;
            String str7 = MessageUtils.text16;
            MessageUtils.x(
                String.format(
                    "%s%d. " + str7 + "%s: " + str6 + "%.2fms" + str5 + " (" + str4 + "%.3fms avg, " + str3 + "%d calls" + str2 + ")",
                    str,
                    i + 1,
                    entry2.text,
                    entry2.getTotalTime(),
                    entry2.getAmount(),
                    entry2.timestamp
                )
            );
        }

        MessageUtils.x("");
        MessageUtils.x(MessageUtils.text15 + "Top " + n + " Highest Average Time:");
        GetTopSlowestHelper.Entry2[] entry2s2 = GetTopSlowestHelper.getHighestAverage(n).toArray(new GetTopSlowestHelper.Entry2[0]);

        for (int j = 0; j < entry2s2.length; j++) {
            GetTopSlowestHelper.Entry2 entry22 = entry2s2[j];
            String str8 = entry22.getAmount() > 1.0 ? MessageUtils.text13 : MessageUtils.text15;
            String str9 = MessageUtils.text8;
            String str10 = MessageUtils.text16;
            String str11 = MessageUtils.text8;
            String str12 = MessageUtils.text16;
            MessageUtils.x(
                String.format(
                    "%s%d. " + str12 + "%s: " + str8 + "%.3fms avg" + str11 + " (" + str10 + "%d calls" + str9 + ")",
                    MessageUtils.text15,
                    j + 1,
                    entry22.text,
                    entry22.getAmount(),
                    entry22.timestamp
                )
            );
        }
    }

    private int computeInt(String str, int n, int n2, int n3) {
        try {
            int n4 = Integer.parseInt(str);
            if (n4 < n) {
                return n;
            }
            return n4 > n2 ? n2 : n4;
        } catch (NumberFormatException e) {
            return n3;
        }
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length > 0) {
            String str = args[0].toLowerCase();
            if (str.equals("reset")) {
                GetTopSlowestHelper.reset();
                PrintCopyPasteReportHelper.reset();
                MessageUtils.x(MessageUtils.text11 + "[Profiler] " + MessageUtils.text16 + "Statistics reset!");
            } else if (str.equals("report")) {
                MessageUtils.x(MessageUtils.text15 + "=== PERFORMANCE PROFILER REPORT ===");
                this.handleInt(15);
            } else if (str.equals("compact")) {
                String str2 = GetTopSlowestHelper.computeString(10);
                MessageUtils.x(str2);
            } else if (str.equals("top")) {
                int n = args.length > 1 ? this.computeInt(args[1], 1, 20, 5) : 5;
                MessageUtils.x(MessageUtils.text15 + "=== TOP " + n + " SLOWEST MODULES ===");
                GetTopSlowestHelper.Entry2[] entry2s = GetTopSlowestHelper.getTopSlowest(n).toArray(new GetTopSlowestHelper.Entry2[0]);

                for (int i = 0; i < entry2s.length; i++) {
                    GetTopSlowestHelper.Entry2 entry2 = entry2s[i];
                    String str3 = i == 0 ? MessageUtils.text13 : (i < 3 ? MessageUtils.text7 : MessageUtils.text15);
                    String str4 = MessageUtils.text8;
                    String str5 = MessageUtils.text16;
                    String str6 = MessageUtils.text16;
                    String str7 = MessageUtils.text8;
                    String str8 = MessageUtils.text11;
                    String str9 = MessageUtils.text16;
                    MessageUtils.x(
                        String.format(
                            "%s%d. " + str9 + "%s: " + str8 + "%.2fms" + str7 + " (" + str6 + "%.3fms avg, " + str5 + "%d calls" + str4 + ")",
                            str3,
                            i + 1,
                            entry2.text,
                            entry2.getTotalTime(),
                            entry2.getAmount(),
                            entry2.timestamp
                        )
                    );
                }
            } else if (!str.equals("realtime")) {
                if (str.equals("entities")) {
                    MessageUtils.x(MessageUtils.text15 + "=== ENTITY PERFORMANCE ===");
                    int n2 = PrintCopyPasteReportHelper.getCount();
                    int n3 = PrintCopyPasteReportHelper.getCount2();
                    int particleCount = PrintCopyPasteReportHelper.getParticleCount();
                    MessageUtils.x(String.format(MessageUtils.text8 + "Total Players: " + MessageUtils.text16 + "%d", n2));
                    MessageUtils.x(String.format(MessageUtils.text8 + "Visible Players: " + MessageUtils.text16 + "%d", n3));
                    MessageUtils.x(String.format(MessageUtils.text8 + "Particles: " + MessageUtils.text16 + "%d", particleCount));
                    if (n3 > 10) {
                        MessageUtils.x(MessageUtils.text13 + "WARNING: High player count detected! This may cause FPS drops.");
                    }

                    if (particleCount > 100) {
                        MessageUtils.x(MessageUtils.text13 + "WARNING: High particle count detected! This may cause FPS drops.");
                    }
                } else if (str.equals("particles")) {
                    MessageUtils.x(MessageUtils.text15 + "=== PARTICLE PERFORMANCE ===");
                    int particleCount2 = PrintCopyPasteReportHelper.getParticleCount();
                    String str10 = particleCount2 < 50 ? MessageUtils.text11 + "Good" : (particleCount2 < 100 ? MessageUtils.text15 + "Moderate" : MessageUtils.text13 + "HIGH");
                    MessageUtils.x(String.format(MessageUtils.text8 + "Particle Count: " + str10 + " " + MessageUtils.text16 + "%d", particleCount2));
                    if (particleCount2 > 50) {
                        MessageUtils.x(MessageUtils.text8 + "Tip: Reduce particle settings in Video > Particles for better FPS");
                    }
                } else if (str.equals("console")) {
                    MessageUtils.x(MessageUtils.text15 + "Printing detailed report to console...");
                    MessageUtils.x(MessageUtils.text8 + "Check your console/logs for the full report.");
                    PrintCopyPasteReportHelper.printConsoleReport();
                } else if (str.equals("copy")) {
                    MessageUtils.x(MessageUtils.text15 + "Generating copy-paste report for console...");
                    MessageUtils.x(MessageUtils.text8 + "Copy the output from console and paste it for analysis.");
                    PrintCopyPasteReportHelper.printCopyPasteReport();
                } else {
                    MessageUtils.x(MessageUtils.text13 + "Unknown subcommand: " + str);
                    this.process();
                }
            } else {
                boolean enabled = PrintCopyPasteReportHelper.isEnabled();
                if (args.length <= 1 || !args[1].equals("on") && !args[1].equals("true")) {
                    if (args.length <= 1 || !args[1].equals("off") && !args[1].equals("false")) {
                        MessageUtils.x(
                            MessageUtils.text7
                                + "[RealTime Profiler] "
                                + MessageUtils.text16
                                + "Status: "
                                + (enabled ? MessageUtils.text11 + "ENABLED" : MessageUtils.text13 + "DISABLED")
                        );
                        if (enabled) {
                            double d = PrintCopyPasteReportHelper.computeDouble();
                            double d2 = PrintCopyPasteReportHelper.computeDouble3();
                            int n4 = PrintCopyPasteReportHelper.getCount2();
                            int particleCount3 = PrintCopyPasteReportHelper.getParticleCount();
                            String str11 = MessageUtils.text16;
                            String str12 = MessageUtils.text8;
                            String str13 = MessageUtils.text16;
                            MessageUtils.x(String.format(MessageUtils.text8 + "FPS: " + str13 + "%.1f" + str12 + " | Frame: " + str11 + "%.2fms", d, d2));
                            String str14 = MessageUtils.text16;
                            String str15 = MessageUtils.text8;
                            String str16 = MessageUtils.text16;
                            MessageUtils.x(
                                String.format(MessageUtils.text8 + "Visible Players: " + str16 + "%d" + str15 + " | Particles: " + str14 + "%d", n4, particleCount3)
                            );
                        }

                        MessageUtils.x(MessageUtils.text8 + "Usage: .profiler realtime [on|off]");
                    } else {
                        PrintCopyPasteReportHelper.disable();
                        MessageUtils.x(MessageUtils.text13 + "[RealTime Profiler] " + MessageUtils.text16 + "DISABLED");
                    }
                } else {
                    PrintCopyPasteReportHelper.enable();
                    MessageUtils.x(MessageUtils.text11 + "[RealTime Profiler] " + MessageUtils.text16 + "ENABLED - Tracking frame performance");
                }
            }
        } else {
            boolean enabled2 = GetTopSlowestHelper.isEnabled();
            boolean enabled3 = PrintCopyPasteReportHelper.isEnabled();
            int size = GetTopSlowestHelper.computeMap().size();
            MessageUtils.x(MessageUtils.text7 + "[Profiler] " + MessageUtils.text16 + "Status:");
            MessageUtils.x(MessageUtils.text8 + "  Module Profiler: " + (enabled2 ? MessageUtils.text11 + "ENABLED" : MessageUtils.text13 + "DISABLED"));
            MessageUtils.x(MessageUtils.text8 + "  Realtime Profiler: " + (enabled3 ? MessageUtils.text11 + "ENABLED" : MessageUtils.text13 + "DISABLED"));
            String str17 = MessageUtils.text16;
            MessageUtils.x(MessageUtils.text8 + "Modules tracked: " + str17 + size);
            String str18 = MessageUtils.text8;
            String str19 = MessageUtils.text16;
            String text = this.getText();
            String str20 = str19;
            MessageUtils.x(str18 + "Usage: " + str20 + text);
            if (enabled3) {
                double d3 = PrintCopyPasteReportHelper.computeDouble();
                double d4 = PrintCopyPasteReportHelper.computeDouble3();
                int n5 = PrintCopyPasteReportHelper.getCount2();
                int particleCount4 = PrintCopyPasteReportHelper.getParticleCount();
                MessageUtils.x("");
                MessageUtils.x(MessageUtils.text15 + "Real-time Stats:");
                String str21 = MessageUtils.text16;
                String str22 = MessageUtils.text8;
                String str23 = MessageUtils.text16;
                MessageUtils.x(String.format(MessageUtils.text8 + "  FPS: " + str23 + "%.1f" + str22 + " | Frame: " + str21 + "%.2fms", d3, d4));
                String str24 = MessageUtils.text16;
                String str25 = MessageUtils.text8;
                String str26 = MessageUtils.text16;
                MessageUtils.x(String.format(MessageUtils.text8 + "  Players: " + str26 + "%d" + str25 + " | Particles: " + str24 + "%d", n5, particleCount4));
            }

            if (size > 0) {
                MessageUtils.x("");
                MessageUtils.x(MessageUtils.text8 + "Top 3 slowest modules:");
                GetTopSlowestHelper.Entry2[] entry2s2 = GetTopSlowestHelper.getTopSlowest(3).toArray(new GetTopSlowestHelper.Entry2[0]);

                for (int j = 0; j < entry2s2.length; j++) {
                    GetTopSlowestHelper.Entry2 entry22 = entry2s2[j];
                    String str27 = MessageUtils.text8;
                    String str28 = MessageUtils.text16;
                    String str29 = MessageUtils.text8;
                    String str30 = MessageUtils.text13;
                    String str31 = MessageUtils.text16;
                    String str32 = MessageUtils.text15;
                    MessageUtils.x(
                        String.format(
                            "  " + str32 + "%d. " + str31 + "%s: " + str30 + "%.2fms" + str29 + " (" + str28 + "%.3fms avg" + str27 + ")",
                            j + 1,
                            entry22.text,
                            entry22.getTotalTime(),
                            entry22.getAmount()
                        )
                    );
                }
            }
        }
    }

    public ProfilerCommand() {
        super("profiler", "Display performance profiling information", ".profiler [reset|report|compact|top|realtime|entities|particles|console|copy]", "prof");
    }

    private void process() {
        MessageUtils.x(MessageUtils.text7 + "Profiler Command Help:");
        MessageUtils.x(MessageUtils.text8 + ".profiler" + MessageUtils.text16 + " - Show summary");
        MessageUtils.x(MessageUtils.text8 + ".profiler reset" + MessageUtils.text16 + " - Reset statistics");
        MessageUtils.x(MessageUtils.text8 + ".profiler report" + MessageUtils.text16 + " - Full report");
        MessageUtils.x(MessageUtils.text8 + ".profiler compact" + MessageUtils.text16 + " - Compact report");
        MessageUtils.x(MessageUtils.text8 + ".profiler top <n>" + MessageUtils.text16 + " - Top N slowest modules");
        MessageUtils.x(MessageUtils.text8 + ".profiler realtime [on|off]" + MessageUtils.text16 + " - Toggle real-time profiler");
        MessageUtils.x(MessageUtils.text8 + ".profiler entities" + MessageUtils.text16 + " - Show entity info");
        MessageUtils.x(MessageUtils.text8 + ".profiler particles" + MessageUtils.text16 + " - Show particle info");
        MessageUtils.x(MessageUtils.text8 + ".profiler console" + MessageUtils.text16 + " - Print detailed report to console");
        MessageUtils.x(MessageUtils.text8 + ".profiler copy" + MessageUtils.text16 + " - Generate copy-paste report");
    }
}
