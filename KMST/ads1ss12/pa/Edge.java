package ads1ss12.pa;

/**
 * Eine Kante des Eingabe-Graphen.
 * 
 * <p>
 * Auf die Knoten der Kante sowie deren Gewicht kann einfach &uuml;ber die
 * &ouml;ffentlichen Variablen {@link #node1}, {@link #node2} und
 * {@link #weight} zugegriffen werden. Allerdings k&ouml;nnen diese Werte nicht
 * ver&auml;ndert werden.
 * </p>
 * 
 * <p>
 * <b>WICHTIG:</b> Nehmen Sie keine &Auml;nderungen an dieser Klasse vor! Diese
 * werden vom Abgabesystem verworfen und es k&ouml;nnte sein, dass Ihr Programm
 * dann nicht mehr korrekt funktioniert.
 * </p>
 */
public class Edge implements Comparable<Edge> {

	/** Der eine Knoten der Kante. */
	public final int node1;
	/** Der andere Knoten der Kante. */
	public final int node2;
	/** Das Gewicht der Kante. */
	public final int weight;

	/**
	 * Erzeugt eine neue Kante mit den Knoten <code>n1</code> und
	 * <code>n2</code> und dem Gewicht <code>w</code>.
	 * 
	 * <p>
	 * Der Knoten mit dem kleineren Index wird {@link #node1} zugewiesen, der
	 * Andere {@link #node2}.
	 * </p>
	 * 
	 * <p>
	 * Das Erzeugen eines neuen Knoten-Objekts hat den Aufwand <i>O(1)</i>.
	 * </p>
	 * 
	 * @param n1
	 *            Der eine Knoten der neuen Kante.
	 * @param n2
	 *            Der andere Knoten der neuen Kante.
	 * @param w
	 *            Das Kantengewicht.
	 */
	public Edge(int n1, int n2, int w) {
		if (n1 < n2) {
			node1 = n1;
			node2 = n2;
		} else {
			node1 = n2;
			node2 = n1;
		}

		weight = w;
	}

	/**
	 * Testet zwei Kanten auf Gleichheit.
	 * 
	 * <p>
	 * Zwei Kanten sind gleich, wenn Ihre Endknoten gleich sind.
	 * </p>
	 * 
	 * <p>
	 * Der Aufwand des Vergleichs ist int <i>O(1)</i>.
	 * </p>
	 * 
	 * @param other
	 *            Die andere Kante mit der verglichen werden soll.
	 * @return <code>true</code> wenn die beiden Kanten gleich sind,
	 *         <code>false</code> sonst.
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Edge) {
			Edge o = (Edge) other;

			return node1 == o.node1 && node2 == o.node2 && weight == o.weight;
		} else {
			return false;
		}
	}

	/**
	 * Liefert einen Hashwert dieses Objekts.
	 * 
	 * <p>
	 * Es wird nur ein sehr einfacher Hashwert berechnet, der aber trotzdem nur
	 * f&uuml;r gleiche Knoten, das selbe Ergebnis liefern sollte.
	 * </p>
	 * 
	 * <p>
	 * Der Aufwand der Hashwertberechnung ist in <i>O(1)</i>.
	 * </p>
	 * 
	 * @return Ein Hashwert f&uuml;r dieses Kanten-Objekt.
	 */
	@Override
	public int hashCode() {
		assert node1 < node2;

		return (node1 << 18) + (node2 << 4) + weight;
	}

	/**
	 * Liefert eine String-Repr&auml;sentation dieser Kante.
	 * 
	 * Der Aufwand dieser Operation ist in <i>O(1)</i>.
	 * 
	 * @return Ein {@link String} der diese Kante beschreibt.
	 */
	@Override
	public String toString() {
		return "(" + node1 + "," + node2 + "," + weight + ")";
	}

	@Override
	public int compareTo(Edge o) {
		int w = this.weight - o.weight;
		if (w != 0)
			return w;
		return this.hashCode() - o.hashCode();
	}

} // class Edge
