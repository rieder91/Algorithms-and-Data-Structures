package ads1ss12.pa;

/**
 * Diese Klasse kapselt alle Informationen eines Knoten des AVL-Baums.
 * 
 * <p>
 * Die Felder der Klasse ({@link #left}, {@link #right}, {@link #balance} und
 * {@link #key}) k&ouml;nnen alle direkt angesprochen werden.
 * </p>
 * 
 * <p>
 * <b>WICHTIG:</b> Nehmen Sie keine &Auml;nderungen in dieser Klasse vor. Bei
 * der Abgabe werden diese &Auml;nderungen verworfen und es k&ouml;nnte dadurch
 * passieren, dass Ihr Programm somit nicht mehr korrekt funktioniert.
 * </p>
 */
public class AvlNode {

	/**
	 * Das linke Kind des aktuellen Knotens.
	 * 
	 * Ist <code>null</code> wenn der Knoten kein linkes Kind hat.
	 */
	public AvlNode left;

	/**
	 * Das rechte Kind des aktuellen Knotens.
	 * 
	 * Ist <code>null</code> wenn der Knoten kein rechtes Kind hat.
	 */
	public AvlNode right;

	/**
	 * Elternknoten des aktuellen Knotens.
	 * 
	 * Ist <code>null</code> wenn der Knoten die Wurzel des AVL-Baums ist.
	 */
	public AvlNode parent;

	/** Der Schl&uuml;ssel des Knotens. */
	public int key;

	/**
	 * Die Balance des Knotens.
	 * 
	 * <p>
	 * <b>WICHTIG:</b> Sie k&ouml;nnen diese Variable beliebig setzen. Das
	 * Framework &uuml;berpr&uuml;ft den Wert <b>nicht</b>!
	 * </p>
	 */
	public int balance;

	/**
	 * Erzeugt einen neuen Knoten.
	 * 
	 * Der neue Knoten hat den Schl&uuml;ssel <code>k</code> und jeweils einen
	 * <code>null</code>-Verweis als linkes und rechtes Kind.
	 * 
	 * @param k
	 *            Der Schl&uuml;ssel des neuen Knotens.
	 */
	public AvlNode(int k) {
		left = right = parent = null;
		balance = 0;
		key = k;
	}

	/**
	 * Liefert eine String-Repr&auml;sentation des Knotens.
	 * 
	 * @return Eine String-Repr&auml;sentation des Knotens.
	 */
	public String toString() {
		return "" + key;
	}

} // class AvlNode