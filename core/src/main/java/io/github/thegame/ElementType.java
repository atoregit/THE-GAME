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
        // Populate valid compound pairs (both directions)
        compounds.put("HYDROGEN_OXYGEN", "Water (H2O)");
        compounds.put("OXYGEN_HYDROGEN", "Water (H2O)");

        compounds.put("HYDROGEN_HYRDOGEN", "Hydrogen (H2)");

        compounds.put("HYDROGEN_CHLORINE", "Hydrogen Chloride (HCl)");
        compounds.put("CHLORINE_HYDROGEN", "Hydrogen Chloride (HCl)");

        compounds.put("SODIUM_CHLORINE", "Sodium Chloride (NaCl)");
        compounds.put("CHLORINE_SODIUM", "Sodium Chloride (NaCl)");

        compounds.put("CALCIUM_OXYGEN", "Calcium Oxide (CaO)");
        compounds.put("OXYGEN_CALCIUM", "Calcium Oxide (CaO)");

        compounds.put("HYDROGEN_BROMINE", "Hydrogen Bromide (HBr)");
        compounds.put("BROMINE_HYDROGEN", "Hydrogen Bromide (HBr)");

        compounds.put("LITHIUM_OXYGEN", "Lithium Oxide (Li2O)");
        compounds.put("OXYGEN_LITHIUM", "Lithium Oxide (Li2O)");

        compounds.put("IRON_OXYGEN", "Iron Oxide (Fe2O3)");
        compounds.put("OXYGEN_IRON", "Iron Oxide (Fe2O3)");

        compounds.put("ALUMINIUM_OXYGEN", "Aluminium Oxide (Al2O3)");
        compounds.put("OXYGEN_ALUMINIUM", "Aluminium Oxide (Al2O3)");

        compounds.put("COPPER_OXYGEN", "Copper Oxide (CuO)");
        compounds.put("OXYGEN_COPPER", "Copper Oxide (CuO)");

        compounds.put("SODIUM_OXYGEN", "Sodium Oxide (Na2O)");
        compounds.put("OXYGEN_SODIUM", "Sodium Oxide (Na2O)");

        compounds.put("CALCIUM_CHLORINE", "Calcium Chloride (CaCl2)");
        compounds.put("CHLORINE_CALCIUM", "Calcium Chloride (CaCl2)");

        compounds.put("LITHIUM_BROMINE", "Lithium Bromide (LiBr)");
        compounds.put("BROMINE_LITHIUM", "Lithium Bromide (LiBr)");

        compounds.put("ALUMINIUM_CHLORINE", "Aluminium Chloride (AlCl2)");
        compounds.put("CHLORINE_ALUMINIUM", "Aluminium Chloride (AlCl2)");

        compounds.put("COPPER_CHLORINE", "Copper(II) Chloride (CuCl2)");
        compounds.put("CHLORINE_COPPER", "Copper(II) Chloride (CuCl2)");

        compounds.put("IRON_CHLORINE", "Iron(III) Chloride (FeCl3)");
        compounds.put("CHLORINE_IRON", "Iron(III) Chloride (FeCl3)");

        compounds.put("SODIUM_BROMINE", "Sodium Bromide (NaBr)");
        compounds.put("BROMINE_SODIUM", "Sodium Bromide (NaBr)");

        compounds.put("CALCIUM_BROMINE", "Calcium Bromide (CaBr3)");
        compounds.put("BROMINE_CALCIUM", "Calcium Bromide (CaBr3)");

        compounds.put("BROMINE_BROMINE", "Bromine Gas (Br2)"); // Diatomic Bromine
        compounds.put("NITROGEN_NITROGEN", "Nitrogen Gas (N2)"); // Diatomic Nitrogen
    }

    public static boolean isCompound(ElementType first, ElementType second) {
        return compounds.containsKey(first.name() + "_" + second.name());
    }
}
