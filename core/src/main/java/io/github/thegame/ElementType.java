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



