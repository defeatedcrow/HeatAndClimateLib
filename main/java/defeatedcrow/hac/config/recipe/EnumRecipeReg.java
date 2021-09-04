package defeatedcrow.hac.config.recipe;

public enum EnumRecipeReg {

	ADD,
	REPLACE,
	REMOVE;

	public static EnumRecipeReg getFromName(String name) {
		if (name != null)
			for (EnumRecipeReg t : EnumRecipeReg.values()) {
				if (t.name().equalsIgnoreCase(name)) {
					return t;
				}
			}
		return ADD;
	}

}
