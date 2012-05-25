package ads1ss12.pa;

import java.util.BitSet;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 * 
 * @author Thomas Rieder, 1125403
 * @date 2012-05-25
 */
public class KMST extends AbstractKMST {
	private int adjacentMatrix[][];
	private int numNodes;
	private int k;
	private int minWeight = Integer.MAX_VALUE;

	private BitSet usedNodes;
	private HashSet<HashSet<Edge>> visited;

	@SuppressWarnings("unused")
	private int numEdges; // is never actually used

	// private long start = System.currentTimeMillis();
	// private int calls = 0;

	/**
	 * Der Konstruktor. Hier ist die richtige Stelle f&uuml;r die
	 * Initialisierung Ihrer Datenstrukturen.
	 * 
	 * @param numNodes
	 *            Die Anzahl der Knoten
	 * @param numEdges
	 *            Die Anzahl der Kanten
	 * @param edges
	 *            Die Menge der Kanten
	 * @param k
	 *            Die Anzahl der Knoten, die Ihr MST haben soll
	 */
	public KMST(Integer numNodes, Integer numEdges, HashSet<Edge> edges, int k) {
		this.adjacentMatrix = new int[numNodes][numNodes];
		this.numNodes = numNodes;
		this.numEdges = numEdges;
		this.k = k;
		this.usedNodes = new BitSet(numNodes);
		this.visited = new HashSet<HashSet<Edge>>(numEdges);

		// Create adjacency matrix
		for (Edge t : edges) {
			adjacentMatrix[t.node1][t.node2] = t.weight;
			adjacentMatrix[t.node2][t.node1] = t.weight;
		}
	}

	/**
	 * Diese Methode bekommt vom Framework maximal 30 Sekunden Zeit zur
	 * Verf&uuml;gung gestellt um einen g&uuml;ltigen k-MST zu finden.
	 * 
	 * <p>
	 * F&uuml;gen Sie hier Ihre Implementierung des Branch-and-Bound Algorithmus
	 * ein.
	 * </p>
	 */
	@Override
	public void run() {
		constructMST();

		// System.out.println("Number of function calls: " + calls);
		// System.out.println("Adjazenzmatrix: ");
		// print(adjacentMatrix);
		// System.out.println("Gewicht der besten Loesung: " + minWeight);
	}

	/**
	 * builds the mst with the first seed node being the node with the cheapest
	 * edge
	 */
	public void constructMST() {
		PriorityQueue<Edge> q = new PriorityQueue<Edge>();
		Edge t;

		// builds a priority queue with the cheapest edge of each node
		for (int i = 0; i < numNodes; i++) {
			t = getCheapestEdge(i, adjacentMatrix);
			if (t != null && t.weight != Integer.MAX_VALUE) {
				q.add(new Edge(i, -1, t.weight));
			}
		}

		for (Edge e : q) {
			firstEstimate(null, adjacentMatrix, e.node2, 0, null);
			usedNodes.clear();
		}

		for (Edge e : q) {
			addNodes(null, adjacentMatrix, e.node2, 0, null);
			usedNodes.clear();
		}
	}

	public boolean hasNoCircle(BitSet used, int node1, int node2) {
		if (used.get(node1) && used.get(node2)) {
			return false;
		}
		return true;
	}

	public void firstEstimate(HashSet<Edge> e, int[][] adj, int node,
			int cweight, PriorityQueue<Edge> p) {
		Edge t;
		HashSet<Edge> temp = new HashSet<Edge>(k);
		int w, newNode, size;
		boolean abort = false;

		if (p != null) {
			p = new PriorityQueue<Edge>(p);
		} else {
			p = new PriorityQueue<Edge>();
		}

		if (e != null) {
			temp.addAll(e);
		}

		addToQueue(p, node, adj);

		while (!p.isEmpty() && !abort) {
			t = p.poll();

			if (t.weight > minWeight) {
				adjacentMatrix[t.node1][t.node2] = 0;
				adjacentMatrix[t.node2][t.node1] = 0;
			} else {
				w = cweight + t.weight;
				size = temp.size();
				if (hasNoCircle(usedNodes, t.node1, t.node2)) {
					if (usedNodes.get(t.node1)) {
						newNode = t.node2;
						node = t.node1;
					} else {
						newNode = t.node1;
						node = t.node2;
					}

					temp.add(t);

					if (usedNodes.isEmpty()) {
						usedNodes.set(newNode);
						usedNodes.set(node);
					} else {
						usedNodes.set(newNode);
					}

					abort = true;

					if (size + 2 == k) {
						if (w < minWeight) {
							updateSolution(temp, w);
						}
					} else {
						firstEstimate(temp, adj, newNode, w, p);
					}
				}
			}
		}
	}

	public void addNodes(HashSet<Edge> e, int[][] adj, int node, int cweight,
			PriorityQueue<Edge> p) {

		// calls++;
		Edge t;
		HashSet<Edge> temp = new HashSet<Edge>(k);
		int w, newNode, size;

		if (p != null) {
			p = new PriorityQueue<Edge>(p);
		} else {
			p = new PriorityQueue<Edge>();
		}

		if (e != null) {
			temp.addAll(e);
		}

		addToQueue(p, node, adj);

		while (!p.isEmpty()) {
			t = p.poll();
			w = cweight + t.weight;

			if (w < minWeight && !visited.contains(temp)) {
				if (hasNoCircle(usedNodes, t.node1, t.node2)) {
					if (usedNodes.get(t.node1)) {
						newNode = t.node2;
						node = t.node1;
					} else {
						newNode = t.node1;
						node = t.node2;
					}

					size = temp.size() + 2;
					temp.add(t);

					if (usedNodes.isEmpty()) {
						usedNodes.set(newNode);
						usedNodes.set(node);
					} else {
						usedNodes.set(newNode);
					}

					if (size == k) {
						updateSolution(temp, w);
					} else {
						addNodes(temp, adj, newNode, w, p);

						if (size == 2) {
							visited.add(temp);
						}

						temp = new HashSet<Edge>(k);

						if (e != null) {
							temp.addAll(e);
							usedNodes.clear(newNode);
						}
					}
				}
			} else {
				p = new PriorityQueue<Edge>();
			}
		}
	}

	/**
	 * updates the best solution
	 * 
	 * @param minSet
	 *            new edge-set
	 * @param min
	 *            new weight
	 */
	public void updateSolution(HashSet<Edge> minSet, int min) {
		minWeight = min;
		// System.out.println(System.currentTimeMillis() - start + "ms - " +
		// min);
		setSolution(min, minSet);
	}

	public Edge getCheapestEdge(int node, int[][] adj) {
		Edge ret = new Edge(-1, -1, Integer.MAX_VALUE);
		for (int i = 0; i < numNodes; i++) {
			if (adj[node][i] != 0 && adj[node][i] < ret.weight) {
				ret = new Edge(node, i, adj[node][i]);
			}
		}
		return ret;
	}

	public void addToQueue(PriorityQueue<Edge> e, int node, int[][] adj) {
		Edge it;
		for (int i = 0; i < numNodes; i++) {
			if (!usedNodes.get(i) && adj[node][i] != 0) {
				it = new Edge(node, i, adj[node][i]);
				if (!e.contains(it)) {
					e.offer(it);
				}
			}
		}
	}

	/**
	 * Prints the adjacency matrix
	 * 
	 * @param adj
	 *            Matrix
	 */
	public void print(int[][] adj) {
		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {
				System.out.print(adj[i][j] + " ");
			}
			System.out.println("");
		}
	}

	public int cheapestEdgeToNode(int[][] adj, int node) {
		int ret = Integer.MAX_VALUE;
		for (int i = 0; i < numNodes; i++) {
			if (usedNodes.get(i) && adj[node][i] != 0 && adj[node][i] < ret) {
				ret = adj[node][i];
			}
		}
		return ret;
	}
}