package org.yyama;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.some.dto.SomeDto;
import org.yyama.type.DtoTestGenType;
import org.yyama.type.IntegerType;
import org.yyama.type.LongType;
import org.yyama.type.StringType;

public class DtoTestGenerator {
	private static final String NEW_LINE = System.lineSeparator();
	private static final String NEW_LINE_D = System.lineSeparator() + System.lineSeparator();

	public static void main(String... args) throws Exception {
		// Classを取得
		Class<SomeDto> clazz = SomeDto.class;
		String cName = clazz.getSimpleName();

		// 型ごとにテストの文字列を決定する
		List<StringParts> testStrs = new ArrayList<>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {

			// serialVersionUIDは対象外とする。
			if (field.getName().equals("serialVersionUID")) {
				continue;
			}

			StringParts str = new StringParts();
			str.fieldName = field.getName();

			// DtoTestGenTypeの実装クラスを決定する
			DtoTestGenType type = selectType(field.getType().getName());
			if (type == null)
				continue;

			// 値のセット部分の文字列作成
			str.setValueStr = type.getSetValueStr(UpperCaseOnlyFirst(field.getName()));

			// 値のゲット部分の文字列作成
			str.getValueStr = type.getGetValueStr(UpperCaseOnlyFirst(field.getName()));

			// Verifyの文字列作成
			str.verifyStr = type.getVerifyStr();

			// toStringの文字列生成
			str.value = type.getValue();

			testStrs.add(str);
		}

		// 出力ストリームを取得
		FileWriter fw = new FileWriter("out/" + clazz.getSimpleName() + "Test.java");
		BufferedWriter bw = new BufferedWriter(fw);

		// パッケージ宣言の書き込み
		bw.write("package " + clazz.getPackage().getName() + ";" + NEW_LINE_D);

		// import文の書き込み
		bw.write("import static org.junit.Assert.*;" + NEW_LINE_D);
		bw.write("import org.junit.Test;" + NEW_LINE_D);

		// クラス宣言の書き込み
		bw.write("public class " + clazz.getSimpleName() + "Test {" + NEW_LINE_D);

		// プロパティの数ループ
		for (StringParts str : testStrs) {
			// メソッド宣言の作成
			bw.write("@Test" + NEW_LINE);
			bw.write("public void set" + UpperCaseOnlyFirst(str.fieldName) + "でセットした値がget"
					+ UpperCaseOnlyFirst(str.fieldName) + "で取得できること() {" + NEW_LINE_D);

			// Setupの作成
			bw.write("// Setup" + NEW_LINE);
			bw.write(cName + " dto = " + "new " + cName + "();" + NEW_LINE_D);

			// Exerciseの作成
			bw.write("// Exercise" + NEW_LINE);
			bw.write(str.setValueStr + NEW_LINE);
			bw.write(str.getValueStr + NEW_LINE_D);

			// Verifyの作成
			bw.write("// Verify" + NEW_LINE);
			bw.write(str.verifyStr + NEW_LINE_D);

			// メソッドの閉じ括弧
			bw.write("}" + NEW_LINE_D);

		}

		// toStringの書き込み
		// メソッド宣言の作成
		bw.write("@Test" + NEW_LINE);
		bw.write("public void toStringでdtoの情報が表示されること() {" + NEW_LINE_D);

		// Setupの作成
		bw.write("// Setup" + NEW_LINE);
		bw.write(cName + " dto = " + "new " + cName + "();" + NEW_LINE);

		// プロパティの数ループ
		for (StringParts parts : testStrs) {
			bw.write(parts.setValueStr + NEW_LINE);
		}
		bw.write(NEW_LINE);

		// Exerciseの作成
		bw.write("// Exercise" + NEW_LINE);
		bw.write("String act = dto.toString();" + NEW_LINE_D);

		// Verifyの作成
		bw.write("// Verify" + NEW_LINE);
		bw.write("assertEquals(\"" + clazz.getSimpleName() + " [");
		for (int i = 0; i < testStrs.size(); i++) {
			if (i != 0) {
				bw.write(", ");
			}
			bw.write(testStrs.get(i).fieldName + "=" + testStrs.get(i).value.replace("\"", ""));
		}
		bw.write("]\", act);" + NEW_LINE_D);

		// メソッドの閉じ括弧
		bw.write("}" + NEW_LINE_D);

		// クラス宣言の閉じ括弧
		bw.write("}");
		// ストリームをクローズ
		bw.close();

		System.out.println("finish");
	}

	private static DtoTestGenType selectType(String type) {
		switch (type) {
		case "java.lang.String":
			return new StringType();
		case "int":
		case "java.lang.Integer":
			return new IntegerType();
		case "long":
		case "java.lang.Long":
			return new LongType();
		default:
			return null;
		}
	}

	private static String UpperCaseOnlyFirst(String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	private static class StringParts {
		public String fieldName;
		public String getValueStr;
		public String setValueStr;
		public String verifyStr;
		public String value;
	}

}
