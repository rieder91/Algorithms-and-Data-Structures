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
 * @version 0.7
 */
public class KMST extends AbstractKMST {
	private int adjacentMatrix[][];
	private int numNodes;
	private int k;
	private int minWeight = Integer.MAX_VALUE;

	private HashSet<HashSet<Edge>> visited;

	private int numEdges;
//	private long start = System.currentTimeMillis();
//	private int callsNodes = 0;
//	private int callsAddQueue = 0;
//	private int callshasnocircle = 0;
//	private int callsupdatesolution = 0;

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

//		System.out.println("Number of function calls: ");
//		System.out.println("addNodes: " + callsNodes);
//		System.out.println("HasNoCircle: " + callshasnocircle);
//		System.out.println("UpdateSolution: " + callsupdatesolution);
//		System.out.println("addQueue: " + callsAddQueue);
		// System.out.println("Adjazenzmatrix: ");
		// print(adjacentMatrix);
		// System.out.println("Gewicht der besten Loesung: " + minWeight);
	}

	/**
	 * builds the mst with the first seed node being the node with the cheapest
	 * edge
	 */
	public void constructMST() {
		PriorityQueue<Edge> q = new PriorityQueue<Edge>(numNodes);
		int t;

		// builds a priority queue with the cheapest edge of each node
		for (int i = 0; i < numNodes; i++) {
			// t = getCheapestEdge(i, adjacentMatrix);
			t = getBestEdge(i, adjacentMatrix);
			if (t != Integer.MAX_VALUE) {
				q.add(new Edge(i, -1, t));
			}
		}

		for (Edge e : q) {
			firstEstimate(new HashSet<Edge>(k), adjacentMatrix, e.node2, 0,
					new PriorityQueue<Edge>(numEdges), new BitSet(numNodes));
		}

		// System.out.println("enum");

		for (Edge e : q) {
			addNodes(null, adjacentMatrix, e.node2, 0, null, new BitSet(
					numNodes));
		}
	}

	public int getBestEdge(int node, int[][] adjacentMatrix) {
		int ret = 0;
		for (int i = 0; i < numNodes; i++) {
			ret += adjacentMatrix[node][i];
		}
		return ret;

	}

	public boolean hasNoCircle(BitSet used, int node1, int node2) {
//		callshasnocircle++;
		if (used.get(node1) && used.get(node2)) {
			return false;
		}
		return true;
	}

	public void firstEstimate(HashSet<Edge> e, int[][] adj, int node,
			int cweight, PriorityQueue<Edge> p, BitSet used) {
		Edge t;
		int w, newNode, size;
		boolean abort = false, wasEmpty, solutionFound;

		addToQueue(p, node, adj, used, cweight);

		while (!p.isEmpty() && !abort) {
			t = p.poll();

			if (t.weight > minWeight) {
				adjacentMatrix[t.node1][t.node2] = 0;
				adjacentMatrix[t.node2][t.node1] = 0;
			} else {
				w = cweight + t.weight;
				if (hasNoCircle(used, t.node1, t.node2)) {
					if (used.get(t.node1)) {
						newNode = t.node2;
						node = t.node1;
					} else {
						newNode = t.node1;
						node = t.node2;
					}

					e.add(t);

					wasEmpty = false;
					solutionFound = false;
					if (used.isEmpty()) {
						used.set(newNode);
						used.set(node);
						wasEmpty = true;
					} else {
						used.set(newNode);
					}
					abort = true;

					size = used.cardinality();

					if (size == k) {
						if (w < minWeight) {
							updateSolution(e, w);
						}
					} else {
						firstEstimate(e, adj, newNode, w, p, used);
					}

					if (!solutionFound) {
						used.clear(newNode);
						if (wasEmpty) {
							used.clear(node);
						}
					}
				}
			}
		}
	}

	public void addNodes(HashSet<Edge> e, int[][] adj, int node, int cweight,
			PriorityQueue<Edge> p, BitSet used) {

//		callsNodes++;

		Edge t;
		HashSet<Edge> temp = new HashSet<Edge>(k);
		int w, newNode, size;
		boolean wasEmpty, solutionFound;

		if (p != null) {
			p = new PriorityQueue<Edge>(p);
		} else {
			p = new PriorityQueue<Edge>(numEdges);
		}

		if (used != null) {
			used = (BitSet) used.clone();
		}

		if (e != null) {
			temp.addAll(e);
		}

		addToQueue(p, node, adj, used, cweight);

		while (!p.isEmpty()) {
			t = p.poll();
			w = cweight + t.weight;

			if (w < minWeight && !visited.contains(temp)) {
				if (hasNoCircle(used, t.node1, t.node2)) {
					if (used.get(t.node1)) {
						newNode = t.node2;
						node = t.node1;
					} else {
						newNode = t.node1;
						node = t.node2;
					}

					temp.add(t);

					wasEmpty = false;
					solutionFound = false;
					if (used.isEmpty()) {
						used.set(newNode);
						used.set(node);
						wasEmpty = true;
					} else {
						used.set(newNode);
					}

					size = used.cardinality();

					if (size == k) {
						updateSolution(temp, w);
						solutionFound = true;

					} else {
						addNodes(temp, adj, newNode, w, p, used);

						if (size >= 2 && size < k ) {
							visited.add(temp);
						}

						temp = new HashSet<Edge>(k);
						if (e != null) {
							temp.addAll(e);
						}
					}
					if (!solutionFound) {
						used.clear(newNode);
						if (wasEmpty) {
							used.clear(node);
						}
					}
				}
			} else {
				p = new PriorityQueue<Edge>(numEdges);
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
//		callsupdatesolution++;
		minWeight = min;
//		System.out.println(System.currentTimeMillis() - start + "ms - " + min);
		setSolution(min, minSet);
	}

	public void addToQueue(PriorityQueue<Edge> e, int node, int[][] adj,
			BitSet used, int w) {
//		callsAddQueue++;
		Edge it;
		for (int i = 0; i < numNodes; i++) {
			if (!used.get(i) && adj[node][i] != 0) {
				it = new Edge(node, i, adj[node][i]);
				if (w + it.weight < minWeight && !e.contains(it)) {
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

}