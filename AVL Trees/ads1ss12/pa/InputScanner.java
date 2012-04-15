package ads1ss12.pa;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

/**
 * Diese Klasse wird zum Einlesen der Testinstanzen benutzt.
 * 
 * <p>
 * <b>WICHTIG:</b> Nehmen Sie keine Aenderungen in dieser Klasse vor. Bei der
 * Abgabe werden diese Aenderungen verworfen und es koennte dadurch passieren,
 * dass Ihr Programm somit nicht mehr korrekt funktioniert.
 * </p>
 */
public final class InputScanner {

	public enum ValueType {
		INVALID, INSERT, REMOVE, FIND
	};

	private BufferedReader input;
	private ValueType type;
	private int value;
	private int numActions;
	private boolean debugRequested;

	public InputScanner() {
		this(System.in);
	}

	public InputScanner(InputStream in) {
		input = new BufferedReader(new InputStreamReader(in));
		type = ValueType.INVALID;
		value = 0;
		numActions = -1;
		debugRequested = false;
	}

	public ValueType getType() {
		return type;
	}

	public int getValue() throws Exception {
		if (type == ValueType.INVALID) {
			throw new Exception("Scanner#getValue() called while "
					+ "scanner was in invalid state.");
		}

		return value;
	}

	public boolean debugRequested() {
		boolean ret = debugRequested;

		debugRequested = false;

		return ret;
	}

	public int getNumActions() {
		return numActions;
	}

	public boolean nextValue() throws IOException {
		String s = nextLine();

		while (s != null && (s.charAt(0) == '#' || type == ValueType.INVALID)) {
			if (s.startsWith("#numActions") && numActions < 0) {
				numActions = Integer.parseInt(s.substring(12));
			} else if (s.startsWith("#insert")) {
				type = ValueType.INSERT;
			} else if (s.startsWith("#remove")) {
				type = ValueType.REMOVE;
			} else if (s.startsWith("#find")) {
				type = ValueType.FIND;
			} else if (s.startsWith("#debug")) {
				debugRequested = true;
			}

			s = nextLine();
		}

		if (s == null) {
			type = ValueType.INVALID;
			value = 0;

			return false;
		}

		value = Integer.parseInt(s);

		return true;
	}

	private String nextLine() throws IOException {
		String s = input.readLine();

		while ("".equals(s))
			s = input.readLine();

		return s;
	}

} // class Scanner
