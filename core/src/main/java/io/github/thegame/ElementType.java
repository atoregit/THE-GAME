package io.github.thegame;

import java.util.HashMap;
import java.util.Map;

public enum ElementType {
    HYDROGEN(20, "strawberry.png"),
    OXYGEN(30, "banana.png"),
    NITROGEN(100, "watermelon.png"),
    CHLORINE(100, "watermelon.png"),
    BROMINE(100, "watermelon.png"),
    SODIUM(50, "orange.png"),
    CALCIUM(60, "apple.png"),
    LITHIUM(100, "watermelon.png"),
    IRON(100, "watermelon.png"),
    ALUMINIUM(100, "watermelon.png"),
    COPPER(100, "watermelon.png");

    public final int value;
    public final String spritePath;

    ElementType(int value, String spritePath) {
        this.value = value;
        this.spritePath = spritePath;
    }

    private static final Map<String, String> compounds = new HashMap<>();

    static {
        // Populate valid compound pairs (both directions)
        compounds.put("HYDROGEN_OXYGEN", "Water (H₂O)");
        compounds.put("OXYGEN_HYDROGEN", "Water (H₂O)");

        compounds.put("HYDROGEN_CHLORINE", "Hydrogen Chloride (HCl)");
        compounds.put("CHLORINE_HYDROGEN", "Hydrogen Chloride (HCl)");

        compounds.put("SODIUM_CHLORINE", "Sodium Chloride (NaCl)");
        compounds.put("CHLORINE_SODIUM", "Sodium Chloride (NaCl)");

        compounds.put("CALCIUM_OXYGEN", "Calcium Oxide (CaO)");
        compounds.put("OXYGEN_CALCIUM", "Calcium Oxide (CaO)");

        compounds.put("HYDROGEN_BROMINE", "Hydrogen Bromide (HBr)");
        compounds.put("BROMINE_HYDROGEN", "Hydrogen Bromide (HBr)");

        compounds.put("LITHIUM_OXYGEN", "Lithium Oxide (Li₂O)");
        compounds.put("OXYGEN_LITHIUM", "Lithium Oxide (Li₂O)");

        compounds.put("IRON_OXYGEN", "Iron Oxide (Fe₂O₃)");
        compounds.put("OXYGEN_IRON", "Iron Oxide (Fe₂O₃)");

        compounds.put("ALUMINIUM_OXYGEN", "Aluminium Oxide (Al₂O₃)");
        compounds.put("OXYGEN_ALUMINIUM", "Aluminium Oxide (Al₂O₃)");

        compounds.put("COPPER_OXYGEN", "Copper Oxide (CuO)");
        compounds.put("OXYGEN_COPPER", "Copper Oxide (CuO)");

        compounds.put("SODIUM_OXYGEN", "Sodium Oxide (Na₂O)");
        compounds.put("OXYGEN_SODIUM", "Sodium Oxide (Na₂O)");

        compounds.put("CALCIUM_CHLORINE", "Calcium Chloride (CaCl₂)");
        compounds.put("CHLORINE_CALCIUM", "Calcium Chloride (CaCl₂)");

        compounds.put("LITHIUM_BROMINE", "Lithium Bromide (LiBr)");
        compounds.put("BROMINE_LITHIUM", "Lithium Bromide (LiBr)");

        compounds.put("ALUMINIUM_CHLORINE", "Aluminium Chloride (AlCl₃)");
        compounds.put("CHLORINE_ALUMINIUM", "Aluminium Chloride (AlCl₃)");

        compounds.put("COPPER_CHLORINE", "Copper(II) Chloride (CuCl₂)");
        compounds.put("CHLORINE_COPPER", "Copper(II) Chloride (CuCl₂)");

        compounds.put("IRON_CHLORINE", "Iron(III) Chloride (FeCl₃)");
        compounds.put("CHLORINE_IRON", "Iron(III) Chloride (FeCl₃)");

        compounds.put("SODIUM_BROMINE", "Sodium Bromide (NaBr)");
        compounds.put("BROMINE_SODIUM", "Sodium Bromide (NaBr)");

        compounds.put("CALCIUM_BROMINE", "Calcium Bromide (CaBr₂)");
        compounds.put("BROMINE_CALCIUM", "Calcium Bromide (CaBr₂)");

        compounds.put("BROMINE_BROMINE", "Bromine Gas (Br₂)"); // Diatomic Bromine
        compounds.put("NITROGEN_NITROGEN", "Nitrogen Gas (N₂)"); // Diatomic Nitrogen
    }

    public static boolean isCompound(ElementType first, ElementType second) {
        return compounds.containsKey(first.name() + "_" + second.name());
    }
}
