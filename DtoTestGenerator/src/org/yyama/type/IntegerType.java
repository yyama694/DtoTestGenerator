package org.yyama.type;

public class IntegerType extends DtoTestGenType {
	private static int val = 0;

	private String value;

	public IntegerType() {
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
		return "Integer act = dto.get" + name + "();";
	}

	@Override
	public String getVerifyStr() {
		String result;
		result = "assertEquals( act, Integer.valueOf(" + value + "));";
		return result;
	}

}
