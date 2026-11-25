package org.brutality.module.impl.misc.skindenicker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;

public final class SkindenickerHandler {
    public static final Set<String> itemSet = new HashSet<>();

    public static void process() {
        try {
            File file = new File(Minecraft.getMinecraft().mcDataDir, "brutality/denick_skin_hashes.txt");
            if (file.isFile()) {
                handleInputStream(new FileInputStream(file));
            }

            InputStream resourceAsStream = SkindenickerHandler.class.getResourceAsStream("/assets/minecraft/brutality/denick_skin_hashes.txt");
            if (resourceAsStream != null) {
                handleInputStream(resourceAsStream);
            }
        } catch (Exception ignored) {
        }
    }

    private SkindenickerHandler() {
    }

    private static void handleInputStream(InputStream in) throws Exception {
        String str;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            while ((str = reader.readLine()) != null) {
                String str2 = str.trim();
                if (!str2.isEmpty() && !str2.startsWith("#")) {
                    itemSet.add(str2);
                }
            }
        }
    }

    static {
        Collections.addAll(
            itemSet,
            "4c7b0468044bfecacc43d00a3a69335a834b73937688292c20d3988cae58248d",
            "3b60a1f6d562f52aaebbf1434f1de147933a3affe0e764fa49ea057536623cd3",
            "19875bb4ac8e7e68c122fdf22bf99abeb4326b96c58ec21d4c5b64cc7a12a5",
            "dd2f967eee43908cda7854df9eb7263637573fd10e498dcdf5d60e9ebc80a1e5",
            "21c44f6b47eadd6720ddc1a14dc4502bd6ccee6542efb74e2b07adb65479cc5",
            "7162726e3b3a7f9c515749a18723ee4439dadd26c0d60e07dea0f2267c6f40a7",
            "10e62bc629872c7d91c2f2edb9643b7455e7238a8c9b4074f1c5312ef162ba22",
            "4336ff82b3d2d7b9081fec5adec2943329531c605b657c11b35231c13a0b8571",
            "7f73526b1a9379be41301cfb74c55270186fbaca63df6949ce3d626e79304d92",
            "8e42e588e1d09ce03c79463e94a7664304f688caf4c617dbcbca64a635bbe79",
            "989bc66d66ff0513507bcb7aa27f4e7e2af24337c4e7c4786c4630839966fdf7",
            "5841684ec6a1f8ba919046857dac9175514fef18a2c9395dc3e341b9f5e778ac",
            "68d98f9a56d4c0ab805c6805756171f4a2cdbf5fa8ce052a4bf4f86411fb080",
            "e2629467cf544e79ed148d3d3842876f2d8841898b4b2e6f60dfc1e02f1179f3",
            "542a699fe314b9f747eed89b9cae23fdefc27684f6c13dc4a29f5d057cc12d",
            "907fcce8c3a8d337a6aff226847e3cc7c9bb6bd02f43be1b7c71b3dcd244e11",
            "9ad4ffb57819e7ff633f60b5901e44a3570573ad4d453075b72ae2cbd23c8a6d",
            "fd22ca2c137a6ecf6a4366eb1f2c8a6b173220b295abc1ae13cedf93dabdbf3c",
            "b77b40e51c7562e523efb0c0f94a616da97c1f485fd8b4a4ac9fb37561812",
            "ff167fcbb98ecd6377905add5c15459e3fb815a0b7c9142e8037a818945630e",
            "fe74c2edd110608e523ce6b6b31f528cb38a122941ede2d68d61ba52bd6802e",
            "f3e399b37b4fba7d2fd0ed14f8a6820131d6c9a355282704c59796f092677542",
            "52ff1cf6537438f4aff7c2cdcdc84cca8f42f9aa264913827981ace5876f71",
            "e32379103e70b548a716a1c8d477b611c44c6d1925e978f1164362f22564b9",
            "ff642bf35a3e622515bb1f20356534fc3f24316b3eb170f477f336ec26edf9d6",
            "4e5223efd125cba238eea12334f9e856718842281d7f865db3c6d577ed5e084",
            "f813e90a33dce8bca6a6c688683706498e1f2aaec8530c481c2a80faa82ddebc",
            "f3a01fd5a6267170ff7c6526d3a9ee4ee4403e036955f902c9414c064d9449be",
            "3a3d66c09223d0899b896487505fda388584941ec946534d2a9e277133fe02e",
            "81895e92fc1552fe1dcd531f6775e1266146b75812c58795a1b0a3bef922b79f",
            "f911f9329b391d8b6e30d55ab9f20608d620d682c9327514b5ab9d1b7ed360d3",
            "fd41e45153cb159af3d2b3d0e4210969fc4a6402a327c5ab6d1f6981ceae7929",
            "8b1ce430410e2416c1c0ae1ad2b91621e128b9c8a46e32dfee9a6e777eab6cf8",
            "6e0b2d4281ad9a090037ef0985fc0667e43c4762c083d34daaa6cd05d5e53055"
        );
    }
}
