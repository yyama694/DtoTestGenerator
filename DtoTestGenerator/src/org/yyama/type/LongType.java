package org.yyama.type;

public class LongType extends DtoTestGenType {
	private static long val = 0;

	private String value;

	public LongType() {
		this.value = String.valueOf(val++);
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String getSetValueStr(String name) {
		return "dto.set" + name + "(" + value + ");";
	}

	@Override
	public String getGetValueStr(String name) {
		return "Long act = dto.get" + name + "();";
	}

	@Override
	public String getVerifyStr() {
		String result;
		result = "assertEquals(Long.valueOf(" + value + "), act);";
		return result;
	}

}
