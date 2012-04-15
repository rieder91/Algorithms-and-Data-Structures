package ads1ss12.pa;

/**
 * AVL-Baum-Klasse die die fehlenden Methoden aus {@link AbstractAvlTree}
 * implementiert.
 * 
 * <p>
 * In dieser Klasse m&uuml;ssen Sie Ihren Code einf&uuml;gen und die Methoden
 * {@link #remove remove()} sowie {@link #rotateLeft rotateLeft()} und
 * {@link #rotateRight rotateRight()} implementieren.
 * </p>
 * 
 * <p>
 * Sie k&ouml;nnen beliebige neue Variablen und Methoden in dieser Klasse
 * hinzuf&uuml;gen. Wichtig ist nur, dass die oben genannten Methoden
 * implementiert werden.
 * </p>
 */
public class AvlTree extends AbstractAvlTree {

	/**
	 * Der Default-Konstruktor.
	 * 
	 * Ruft einfach nur den Konstruktor der Oberklasse auf.
	 */
	public AvlTree() {
		super();
		// TODO: Hier ist der richtige Platz fuer Initialisierungen
	}

	/**
	 * F&uuml;gt ein Element mit dem Schl&uuml;ssel <code>k</code> ein.
	 * 
	 * <p>
	 * Existiert im AVL-Baum ein Knoten mit Schl&uuml;ssel <code>k</code>, soll
	 * <code>insert()</code> einfach nichts machen.
	 * </p>
	 * 
	 * <p>
	 * Nach dem Einf&uuml;gen muss sichergestellt sein, dass es sich bei dem
	 * resultierenden Baum immer noch um einen AVL-Baum handelt, und dass
	 * {@link AbstractAvlTree#root root} auf die tats&auml;chliche Wurzel des
	 * Baums zeigt!
	 * </p>
	 * 
	 * @param k
	 *            Der Schl&uuml;ssel der eingef&uuml;gt werden soll.
	 * @see AbstractAvlTree#insert
	 */
	public void insert(int k) {
		// TODO: Diese Methode ist von Ihnen zu implementieren
	}

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
	 * @param k
	 *            Der Schl&uuml;ssel dessen Knoten entfernt werden soll.
	 * 
	 * @see AbstractAvlTree#root
	 * @see #rotateLeft rotateLeft()
	 * @see #rotateRight rotateRight()
	 */
	public void remove(int k) {
		// TODO: Diese Methode ist von Ihnen zu implementieren
	}

	/**
	 * F&uuml;hrt eine Links-Rotation beim Knoten <code>n</code> durch.
	 * 
	 * 
	 * @param n
	 *            Der Knoten bei dem die Rotation durchgef&uuml;hrt werden soll.
	 * 
	 * @return Die <em>neue</em> Wurzel des rotierten Teilbaums.
	 */
	public AvlNode rotateLeft(AvlNode n) {
		// TODO: Diese Methode ist von Ihnen zu implementieren
		return null;
	}

	/**
	 * F&uuml;hrt eine Rechts-Rotation beim Knoten <code>n</code> durch.
	 * 
	 * 
	 * @param n
	 *            Der Knoten bei dem die Rotation durchgef&uuml;hrt werden soll.
	 * 
	 * @return Die <em>neue</em> Wurzel des rotierten Teilbaums.
	 */
	public AvlNode rotateRight(AvlNode n) {
		// TODO: Diese Methode ist von Ihnen zu implementieren
		return null;
	}

}