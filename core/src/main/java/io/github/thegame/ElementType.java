package io.github.thegame;

import java.util.HashMap;
import java.util.Map;

public enum ElementType {
    HYDROGEN(20, "elements/1.png"),
    OXYGEN(30, "elements/2.png"),
    NITROGEN(100, "elements/3.png"),
    CHLORINE(100, "elements/4.png"),
    BROMINE(100, "elements/5.png"),
    SODIUM(50, "elements/6.png"),
    CALCIUM(60, "elements/7.png"),
    LITHIUM(100, "elements/8.png"),
    IRON(100, "elements/9.png"),
    ALUMINIUM(100, "elements/10.png"),
    COPPER(100, "elements/11.png");

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
        compounds.put("SODIUM_CHLORINE", "Sodium Chloride (NaCl)");
        compounds.put("CHLORINE_SODIUM", "Sodium Chloride (NaCl)");
        compounds.put("CALCIUM_OXYGEN", "Calcium Oxide (CaO)");
        compounds.put("OXYGEN_CALCIUM", "Calcium Oxide (CaO)");
        // Add more compound pairs as needed
    }

    public static boolean isCompound(ElementType first, ElementType second) {
        return compounds.containsKey(first.name() + "_" + second.name());
    }
}



