package io.github.thegame;

import java.util.HashMap;
import java.util.Map;

public enum ElementType {
    HYDROGEN(20, "elements/H.png"),
    OXYGEN(30, "elements/O.png"),
    NITROGEN(100, "elements/N.png"),
    CHLORINE(100, "elements/Cl.png"),
    BROMINE(100, "elements/Br.png"),
    SODIUM(50, "elements/Na.png"),
    CALCIUM(60, "elements/Ca.png"),
    LITHIUM(100, "elements/Li.png"),
    IRON(100, "elements/Fe.png"),
    ALUMINIUM(100, "elements/Al.png"),
    COPPER(100, "elements/Cu.png");

    public final int value;
    public final String spritePath;

    ElementType(int value, String spritePath) {
        this.value = value;
        this.spritePath = spritePath;
    }

    public static final Map<String, String> compounds = new HashMap<>();

    static {
        compounds.put("HYDROGEN_OXYGEN", "Water (H₂O)");
        compounds.put("OXYGEN_HYDROGEN", "Water (H₂O)");

        compounds.put("HYDROGEN_HYDROGEN", "Hydrogen (H₂)");

        compounds.put("HYDROGEN_CHLORINE", "Hydrochloric Acid (HCl)");
        compounds.put("CHLORINE_HYDROGEN", "Hydrochloric Acid (HCl)");

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

        compounds.put("ALUMINUM_OXYGEN", "Aluminum Oxide (Al₂O₃)");
        compounds.put("OXYGEN_ALUMINUM", "Aluminum Oxide (Al₂O₃)");

        compounds.put("COPPER_OXYGEN", "Copper Oxide (CuO)");
        compounds.put("OXYGEN_COPPER", "Copper Oxide (CuO)");

        compounds.put("SODIUM_OXYGEN", "Sodium Oxide (Na₂O)");
        compounds.put("OXYGEN_SODIUM", "Sodium Oxide (Na₂O)");

        compounds.put("CALCIUM_CHLORINE", "Calcium Chloride (CaCl₂)");
        compounds.put("CHLORINE_CALCIUM", "Calcium Chloride (CaCl₂)");

        compounds.put("LITHIUM_BROMINE", "Lithium Bromide (LiBr)");
        compounds.put("BROMINE_LITHIUM", "Lithium Bromide (LiBr)");

        compounds.put("ALUMINUM_CHLORINE", "Aluminum Chloride (AlCl₃)");
        compounds.put("CHLORINE_ALUMINUM", "Aluminum Chloride (AlCl₃)");

        compounds.put("COPPER_CHLORINE", "Copper(II) Chloride (CuCl₂)");
        compounds.put("CHLORINE_COPPER", "Copper(II) Chloride (CuCl₂)");

        compounds.put("IRON_CHLORINE", "Iron(III) Chloride (FeCl₃)");
        compounds.put("CHLORINE_IRON", "Iron(III) Chloride (FeCl₃)");

        compounds.put("SODIUM_BROMINE", "Sodium Bromide (NaBr)");
        compounds.put("BROMINE_SODIUM", "Sodium Bromide (NaBr)");

        compounds.put("CALCIUM_BROMINE", "Calcium Bromide (CaBr₂)");
        compounds.put("BROMINE_CALCIUM", "Calcium Bromide (CaBr₂)");

        compounds.put("BROMINE_BROMINE", "Bromine Gas (Br₂)");

        compounds.put("NITROGEN_NITROGEN", "Nitrogen Gas (N₂)");

        compounds.put("NITROGEN_HYDROGEN", "Ammonia (NH₃)");


    }

    public static boolean isCompound(ElementType first, ElementType second) {
        return compounds.containsKey(first.name() + "_" + second.name());
    }
}
