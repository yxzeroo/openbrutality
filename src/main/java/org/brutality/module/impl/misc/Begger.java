package org.brutality.module.impl.misc;

import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class Begger extends Module {
    private static Begger instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ModeSetting mvpSetting = new ModeSetting("Rank", this, "VIP", Arrays.asList("VIP", "VIP+", "MVP", "MVP+", "MVP++"));
    private final NumberSetting delaySSetting = new NumberSetting("Delay (s)", this, 4.0, 1.0, 60.0, 0.5);
    private long w = 0L;
    private long x = 0L;
    private int count = 0;
    private static final long y = 3000L;
    private static final long z = 60000L;
    private static final List<String> entries = Arrays.asList(
        "can anyone gift me vip pls i cant afford it :(",
        "someone pls gift vip i will be ur best friend forever",
        "i have been playing for 2 years and still no rank pls help",
        "my birthday is today can someone gift vip please",
        "i just want vip so i can have a cool color pls",
        "mom wont buy me vip can someone pls gift it to me",
        "i will do anything for vip please someone be kind",
        "vip is only $7 someone pls gift me it i beg",
        "all my friends have vip and i dont pls gift me",
        "can someone gift me vip i promise ill pay back someday",
        "i got scammed out of my last rank pls someone gift vip",
        "please i just need vip nothing else ill be so grateful",
        "whoever gifts me vip i will follow on all socials fr",
        "pls vip gift i have been begging for weeks now please",
        "no rank is so sad pls someone kind gift me vip",
        "i do bedwars every single day and still have no rank pls",
        "someone please vip gift it would make my whole year i swear",
        "i have 500 wins and no rank please someone gift vip",
        "vip gift pls i have never asked for anything in my life",
        "can the next person who reads this gift me vip pls",
        "i will name my dog after you if you gift vip i promise",
        "pls vip my parents said no for christmas it was so sad",
        "whoever gifts vip becomes lobby 1 legend status immediately",
        "i just want the green name pls someone gift vip today",
        "gift vip and i will carry you in every bedwars game pls"
    );
    private static final List<String> entries2 = Arrays.asList(
        "can someone gift vip+ pls i already have vip but want the plus",
        "vip+ please i do bedwars everyday i deserve it lol pls",
        "whoever gifts vip+ i will carry them in bedwars i promise",
        "pls vip+ gift i see people with it and its so cool",
        "can someone gift me vip+ my parents wont buy ranks at all",
        "i will trade my soul for vip+ gift does anyone wanna help",
        "vip+ pls i have been grinding this game for so long now",
        "someone rich pls gift vip+ i beg you with all my heart",
        "pls pls pls vip+ gift ill never ask again i promise fr",
        "vip+ only like $14 can someone nice gift it pls today",
        "i got vip as birthday gift but really wanted vip+ pls help",
        "vip+ gifter equals legend status forever pls someone do it",
        "can anyone hear me pls vip+ gift im so desperate rn",
        "ill 1v1 anyone for vip+ gift winner gets bragging rights",
        "vip+ please i have been saving up but still not enough coins",
        "someone please gift vip+ i have been on hypixel since 2016",
        "vip+ pls i top frag every game but still look like a noob",
        "can a kind soul gift vip+ i will remember you for life fr",
        "pls vip+ the plus sign means everything to me please help",
        "gift vip+ and i will shoutout you in every lobby i join",
        "vip+ pls i watch hypixel videos daily i deserve it right",
        "whoever gifts vip+ gets my eternal loyalty no questions asked",
        "pls someone gift vip+ my birthday is coming up soon i swear",
        "vip+ gift pls i have positive kdr and still no rank sad",
        "can anyone gift vip+ i am literally the most grateful person"
    );
    private static final List<String> entries3 = Arrays.asList(
        "can someone gift mvp pls it would mean the world to me",
        "mvp gift please i have been playing hypixel for 3 years solid",
        "whoever gifts mvp is literally the best person ever no joke",
        "pls mvp i want the aqua name so bad it looks so cool",
        "can anyone gift mvp i will shoutout on my youtube channel today",
        "mvp pls i have like 1000 hours on bedwars and still nothing",
        "someone gift mvp and i will gift you my loyalty forever fr",
        "pls mvp gift i watch hypixel youtube everyday i deserve it pls",
        "mvp is $30 but i have no money pls someone gift me",
        "i cried when i didnt get mvp for christmas pls someone help",
        "mvp gift equals instant legend you will be famous in this lobby",
        "please mvp i just want the blue name nothing else i swear",
        "can someone rich gift mvp please i promise i will use it well",
        "mvp pls pls pls i have been asking for months now please",
        "gift me mvp and i will be the most grateful person ever",
        "mvp please i have been rejected 100 times but i wont give up",
        "pls mvp gift i will put a shrine of your name in my house",
        "can someone gift mvp i have 2000 bedwars wins and no rank",
        "mvp pls my friend gifted someone else and not me it hurts",
        "gift mvp and become the most respected person in lobby 1 ever",
        "pls mvp i am so close to giving up begging please someone",
        "mvp gift pls i have never missed a day of bedwars in 1 year",
        "can anyone hear my prayers and gift mvp please i am begging",
        "mvp pls my sister has it and wont stop showing me pls help",
        "gift mvp pls i promise i will rep hypixel everywhere i go"
    );
    private static final List<String> entries4 = Arrays.asList(
        "can someone gift mvp+ pls it would change my life fr no joke",
        "mvp+ please i want the plus so i can have a custom color",
        "whoever gifts mvp+ i will remember forever i promise on my life",
        "pls mvp+ gift i have 5000 bedwars games and no rank its sad",
        "mvp+ gift pls i have been manifesting this for 6 months now",
        "can anyone gift mvp+ i will literally cry happy tears no joke",
        "mvp+ pls i watch technoblade videos daily as tribute pls gift",
        "someone gift mvp+ please i have been good all year i promise",
        "pls mvp+ i will name my pet after you if you gift me it",
        "mvp+ gift and i will put your ign in every game description",
        "can someone spare mvp+ for a poor bedwars player pls today",
        "mvp+ please i have been on hypixel since 2015 and still nothing",
        "gift mvp+ and become a legend of lobby 1 forever pls help",
        "i dont ask for much just mvp+ gift pls someone be kind today",
        "mvp+ pls my friend has it and wont stop flexing pls help me",
        "can someone gift mvp+ i have won so many games and deserve it",
        "mvp+ pls i only eat sleep and breathe bedwars please reward me",
        "gift mvp+ and i will 1v1 everyone who disrespects you pls",
        "pls mvp+ the gold plus would complete my entire existence fr",
        "mvp+ gift pls i have submitted 10 bug reports i helped hypixel",
        "can anyone gift mvp+ i have referred 5 friends to this server",
        "mvp+ pls pls pls i am on my knees begging in minecraft rn",
        "gift mvp+ and i will make a youtube video thanking you forever",
        "pls mvp+ i deserve it more than anyone in this lobby trust me",
        "can someone with money gift mvp+ to a dedicated player pls"
    );
    private static final List<String> entries5 = Arrays.asList(
        "can someone gift mvp++ pls it is my lifelong dream no joke fr",
        "mvp++ gift please i have dedicated my entire life to hypixel",
        "whoever gifts mvp++ will be worshipped in this lobby forever",
        "pls mvp++ i have maxed every kit and deserve the best rank",
        "mvp++ gift pls i will write a full song about you i am serious",
        "can anyone gift mvp++ i will be your bedwars slave for life",
        "mvp++ pls i have spent 0 dollars on hypixel after 5 years help",
        "someone rich please gift mvp++ i will promote you everywhere",
        "pls mvp++ gift it is my birthday month i am fully manifesting",
        "mvp++ and i will make a shrine in your honor i genuinely mean it",
        "gift mvp++ pls i top frag every game but still look so poor",
        "can someone gift mvp++ please my dream is the golden name glow",
        "mvp++ pls pls pls i have never wanted anything more in my life",
        "whoever gifts mvp++ becomes the undisputed goat of lobby 1",
        "pls mvp++ gift i know you are out there kind soul please help",
        "mvp++ pls i have reported 50 hackers i have served this server",
        "gift mvp++ and i will dedicate every future win to your name",
        "pls mvp++ the golden glow is all i have ever dreamed about fr",
        "can someone gift mvp++ i have been playing since beta i deserve",
        "mvp++ gift pls i asked santa last christmas and got nothing sad",
        "pls mvp++ i will change my ign to your name if you gift it",
        "gift mvp++ and you will ascend to a higher plane of existence",
        "mvp++ pls i have 10000 hours on hypixel i have earned this pls",
        "can anyone gift mvp++ i will be forever in your debt i promise",
        "pls mvp++ gift i am the most dedicated player in this entire lobby"
    );

    public Begger() {
        super("Begger", 0, Category.MISCELLANEOUS);
        this.handleSettingArray(new Setting[]{this.mvpSetting, this.delaySSetting});
        instance = this;
    }

    private void process() {
        if (this.mc.thePlayer != null) {
            long now = System.currentTimeMillis();
            if (now >= this.x) {
                long l = this.mvpSetting.getText().equals("VIP") ? 3000L : (long)(this.delaySSetting.computeDouble() * 1000.0);
                if (now - this.w >= l) {
                    this.w = now;
                    List list = this.computeList();
                    if (list == null || list.isEmpty()) {
                        return;
                    }

                    String str = (String)list.get(this.count % list.size());
                    this.count++;
                    this.mc.thePlayer.sendChatMessage(str);
                }
            }
        }
    }

    @Generated
    public static Begger getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.w = 0L;
        this.x = 0L;
        this.count = 0;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof ChatEvent) {
                    String text = ((ChatEvent)event).message.getUnformattedText();
                    if (text != null && text.contains("You can't say the same message twice")) {
                        this.x = System.currentTimeMillis() + 60000L;
                        this.count++;
                    }
                }

                if (event instanceof EventRender2D) {
                    this.process();
                }
            }
        }
    }

    private List<String> computeList() {
        String text = this.mvpSetting.getText();
        switch (text) {
            case "VIP":
                return entries;
            case "VIP+":
                return entries2;
            case "MVP":
                return entries3;
            case "MVP+":
                return entries4;
            case "MVP++":
                return entries5;
            default:
                return entries;
        }
    }
}
