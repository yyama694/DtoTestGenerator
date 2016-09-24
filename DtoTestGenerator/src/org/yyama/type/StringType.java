package org.yyama.type;

public class StringType extends DtoTestGenType {
	private static String C = "abcdefghijklmnopqrstuvwxyz";
	private static int pos = 0;

	private String value;

	public StringType() {
		this.value = "\"" + C.charAt(pos++) + "\"";
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
		return "String act = dto.get" + name + "();";
	}

	@Override
	public String getVerifyStr() {
		String result;
		result = "assertEquals(" + value + ", act);";
		if (pos >= 26)
			pos = 0;
		return result;
	}

}
