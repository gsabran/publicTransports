package other;

public class TextException extends Exception {
	// usually used to exit a loop
	public String s;

	public TextException(String s) {
		this.s = s;
	}
}
