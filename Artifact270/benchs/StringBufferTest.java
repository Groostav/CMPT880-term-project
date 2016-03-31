import benchmarks.instrumented.java17.lang.StringBuffer;
public class StringBufferTest {

	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer("abc");
		StringBuffer sb1 = new StringBuffer("abc");
		sb.append(sb1);
		sb.insert(0, sb1);
		sb.deleteCharAt(0);
	}
}
