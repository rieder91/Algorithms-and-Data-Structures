package ads1ss12.pa;

import java.util.ArrayList;

/**
 * Eine Basis f&uuml;r die AVL-Baum Implementierung.
 * 
 * <p>
 * <b>WICHTIG:</b> Nehmen Sie keine &Auml;nderungen in dieser Klasse vor. Bei
 * der Abgabe werden diese &Auml;nderungen verworfen und es k&ouml;nnte dadurch
 * passieren, dass Ihr Programm somit nicht mehr korrekt funktioniert.
 * </p>
 */
public abstract class AbstractAvlTree {

	/**
	 * Der Wurzel-Knoten des AVL-Baums. Bei einem leeren Baum gilt
	 * <code>root == null</code>.
	 */
	protected AvlNode root;

	/**
	 * Erzeugt einen neuen leeren AVL-Baum.
	 * 
	 * Der Verweis auf die {@link #root Wurzel} des neuen Baumes wird auf
	 * <code>null</code> gesetzt.
	 */
	public AbstractAvlTree() {
		root = null;
	}

	/**
	 * F&uuml;gt ein Element mit dem Schl&uuml;ssel <code>k</code> ein.
	 * 
	 * <p>
	 * Nach der Einf&uuml;geoperation muss nat&uuml;rlich wieder ein korrekter
	 * AVL-Baum vorliegen.
	 * </p>
	 * 
	 * <p>
	 * Sie m&uuml;ssen diese Methode in {@link AvlTree#insert} implementieren.
	 * </p>
	 * 
	 * @param k
	 *            Der Schl&uuml;ssel der eingef&uuml;gt werden soll. Falls der
	 *            Schl&uuml;ssel <code>k</code> bereits im Baum enthalten ist,
	 *            soll diese Methode einfach nichts machen.
	 */
	public abstract void insert(int k);

	/**
	 * Entfernt den Knoten mit Schl&uuml;ssel <code>k</code> falls er existiert.
	 * 
	 * <p>
	 * Existiert im AVL-Baum kein Knoten mit Schl&uuml;ssel <code>k</code>, soll
	 * <code>remove()</code> einfach nichts machen.
	 * </p>
	 * 
	 * <p>
	 * Nach dem Entfernen muss sichergestellt sein, dass es sich bei dem
	 * resultierenden Baum immer noch um einen AVL-Baum handelt, und dass
	 * {@link AbstractAvlTree#root root} auf die tats&auml;chliche Wurzel des
	 * Baums zeigt!
	 * </p>
	 * 
	 * <p>
	 * Sie m&uuml;ssen diese Methode in {@link AvlTree#remove} implementieren.
	 * </p>
	 * 
	 * @param k
	 *            Der Schl&uuml;ssel dessen Knoten entfernt werden soll. Ist der
	 *            Wert nicht im Baum enthalten soll diese Methoden nichts
	 *            machen.
	 */
	abstract public void remove(int k);

	/**
	 * F&uuml;hrt eine Links-Rotation beim Knoten <code>n</code> durch.
	 * 
	 * 
	 * @param n
	 *            Der Knoten bei dem die Rotation durchgef&uuml;hrt werden soll.
	 * 
	 * @return Die <em>neue</em> Wurzel des rotierten Teilbaums.
	 */
	abstract public AvlNode rotateLeft(AvlNode n);

	/**
	 * F&uuml;hrt eine Rechts-Rotation beim Knoten <code>n</code> durch.
	 * 
	 * 
	 * @param n
	 *            Der Knoten bei dem die Rotation durchgef&uuml;hrt werden soll.
	 * 
	 * @return Die <em>neue</em> Wurzel des rotierten Teilbaums.
	 */
	abstract public AvlNode rotateRight(AvlNode n);

	/**
	 * Berechnet die Inorder-Durchmusterung dieses AVL-Baums.
	 * 
	 * @return Eine ArrayList die die <code>AvlNode</code>s in der Reihenfolge
	 *         einer Inorder-Durchmusterung enth&auml;lt.
	 */
	final protected ArrayList<AvlNode> inorder() {
		ArrayList<AvlNode> ret = new ArrayList<AvlNode>();

		inorder(root, ret);

		return ret;
	}

	/**
	 * Funktion zur rekursiven Berechnung der Inorder-Durchmusterung.
	 * 
	 * @param n
	 *            Der aktuelle AvlNode
	 * @param io
	 *            Die ArrayList die am Ende die Inorder-Durchmusterung enthalten
	 *            soll
	 */
	final protected void inorder(AvlNode n, ArrayList<AvlNode> io) {
		if (n == null)
			return;

		inorder(n.left, io);

		io.add(n);

		inorder(n.right, io);
	}

} // class AbstractAvlTree