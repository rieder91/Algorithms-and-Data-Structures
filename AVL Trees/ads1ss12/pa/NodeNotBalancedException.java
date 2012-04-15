package ads1ss12.pa;

public class NodeNotBalancedException extends Exception {

	private static final long serialVersionUID = 1749760658474818391L;

	public NodeNotBalancedException(AvlNode node, int h1, int h2) {
		super("Der Knoten mit dem Schluessel " + node.key
				+ " ist nicht balanciert! (" + h1 + " vs " + h2 + ")");
	}
}
