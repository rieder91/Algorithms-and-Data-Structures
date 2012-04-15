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
 * 
 * @author Thomas Rieder, 1125403
 * 
 */
public class AvlTree extends AbstractAvlTree {

	/**
	 * Der Default-Konstruktor.
	 * 
	 * Ruft einfach nur den Konstruktor der Oberklasse auf.
	 */
	public AvlTree() {
		super();
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
		if (root == null) {
			root = new AvlNode(k);
		} else {
			insert(root, new AvlNode(k));
		}
	}

	private void insert(AvlNode p, AvlNode q) {
		if (q.key < p.key) {
			// links einfügen
			if (p.left != null) {
				insert(p.left, q);
				rebalance(p);
			} else {
				// neuer Knoten links
				p.left = q;
				q.parent = p;
			}
		} else if (q.key > p.key) {
			// rechts einfügen
			if (p.right != null) {
				insert(p.right, q);
				rebalance(p);
			} else {
				// neuer Knoten Rechts
				p.right = q;
				q.parent = p;
			}
		}
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
		if (root != null) {
			remove(root, k);
		}
	}

	private void remove(AvlNode p, int q) {
		if (p != null) {
			if (q < p.key) {
				// links
				remove(p.left, q);
			} else if (q > p.key) {
				// rechts
				remove(p.right, q);
			} else {
				// aktueller Knoten
				AvlNode u = null; // Nachfolgeknoten
				AvlNode v;
				if (p.left == null || p.right == null) {
					u = p;
				} else {
					// Nachfolger als kleinsten Knoten des rechten Teilbaumes
					u = min(p.right);
					p.key = u.key;
				}
				if (u.left != null) {
					v = u.left;
				} else {
					v = u.right;
				}
				if (v != null) {
					// Vorgänger uebernehmen
					v.parent = u.parent;
				}
				if (u.parent == null) {
					// Wurzelknoten
					root = v;
				} else {
					if (u.parent.left != null && u.key == u.parent.left.key) {
						// es war ein linker Vorgaenger
						u.parent.left = v;
					} else {
						// es war ein rechter Vorgaenger
						u.parent.right = v;
					}
				}
				// Balance wiederherstellen
				balance2Root(u);
			}
		}
	}

	/**
	 * Findet den kleinsten Knoten im Baum
	 * 
	 * @param n
	 *            aktueller Knoten (vom Tail Call)
	 * @return kleinster Knoten
	 */
	private AvlNode min(AvlNode n) {
		return n.left == null ? n : min(n.left);
	}

	private void rebalance(AvlNode n) {
		int balance = balance(n);
		if (balance == -2) { // Fall 1
			if (n.left != null && balance(n.left) <= 0) { // Fall 1.1
				rotateRight(n);
			} else { // Fall 1.2
				rotateLeft(n.left);
				rotateRight(n);
			}
		} else if (balance == 2) { // Fall 2
			if (n.right != null && balance(n.right) >= 0) { // Fall 2.1
				rotateLeft(n);
			} else { // Fall 2.2
				rotateRight(n.right);
				rotateLeft(n);
			}
		}
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
		AvlNode v = n.right;
		v.parent = n.parent;
		if (n.parent != null) {
			if (n.parent.left != null && n.parent.left.key == n.key) {
				// es war ein linker Nachfolger
				n.parent.left = v;
			} else {
				// es war ein rechter Nachfolger
				n.parent.right = v;
			}
		} else {
			// es handelt sich um den Wurzelknoten
			root = v;
		}
		n.right = v.left;
		if (n.right != null) {
			n.right.parent = n;
		}
		v.left = n;
		n.parent = v;
		return v;
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
		AvlNode v = n.left;
		v.parent = n.parent;
		if (n.parent != null) {
			if (n.parent.left != null && n.parent.left.key == n.key) {
				// es war ein linker Nachfolger
				n.parent.left = v;
			} else {
				// es war ein rechter Nachfolger
				n.parent.right = v;
			}
		} else {
			// es handelt sich um den Wurzelknoten
			root = v;
		}
		n.left = v.right;
		if (n.left != null) {
			n.left.parent = n;
		}
		v.right = n;
		n.parent = v;
		return v;
	}

	private void balance2Root(AvlNode n) {
		if (n == null) {
			return;
		} else {
			AvlNode p = n.parent;
			// aktueller Knoten
			rebalance(n);
			// Vorgänger
			balance2Root(p);
		}
	}

	// private AvlNode search(AvlNode n, int k) {
	// if (n == null || n.key == k) {
	// return n;
	// }
	// if (n.key < k && n.right != null) {
	// return search(n.right, k);
	// } else if (n.key > k && n.left != null) {
	// return search(n.left, k);
	// }
	// return n;
	// }

	private int height(AvlNode a) {
		if (a == null) {
			return 0;
		} else {
			return Math.max(height(a.left), height(a.right)) + 1;
		}
	}

	public void print() {
		printTree(root);
	}

	private void printTree(AvlNode n) {
		if (n != null) {
			if (n.left != null) {
				System.out.print("[");
				printTree(n.left);
				System.out.print("]");
			}
			System.out.print(n.key);
			if (n.right != null) {
				System.out.print("[");
				printTree(n.right);
				System.out.print("]");
			}
		}
	}

	private int balance(AvlNode a) {
		return a == null ? 0 : height(a.right) - height(a.left);
	}
}
