package ads1ss12.pa;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 * 
 * @author Thomas Rieder, 1125403
 * @date 2012-05-25
 * @version 0.8
 */
public class KMST extends AbstractKMST {
	private ArrayList<Edge>[] edgesFromNode;
	private HashSet<HashSet<Edge>> visited;
	private int adjacentMatrix[][];
	private int[] minSum;
	private int numNodes;
	private int numEdges;
	private int k;
	private int minWeight = Integer.MAX_VALUE;

	// Debugging
	// private long start = System.currentTimeMillis();

	// private int callsNodes = 0;
	// private int callsAddQueue = 0;
	// private int callshasnocircle = 0;
	// private int callsupdatesolution = 0;

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
	@SuppressWarnings("unchecked")
	public KMST(Integer numNodes, Integer numEdges, HashSet<Edge> edges, int k) {
		this.adjacentMatrix = new int[numNodes][numNodes];
		this.numNodes = numNodes;
		this.numEdges = numEdges;
		this.k = k;
		this.visited = new HashSet<HashSet<Edge>>(numEdges);
		this.minSum = new int[k + 2];
		this.edgesFromNode = new ArrayList[numNodes];

		PriorityQueue<Edge> min = new PriorityQueue<Edge>(numNodes);

		// Create data structures
		for (Edge t : edges) {
			adjacentMatrix[t.node1][t.node2] = t.weight;
			adjacentMatrix[t.node2][t.node1] = t.weight;
			if (edgesFromNode[t.node1] == null) {
				edgesFromNode[t.node1] = new ArrayList<Edge>(numNodes);
			}
			if (edgesFromNode[t.node2] == null) {
				edgesFromNode[t.node2] = new ArrayList<Edge>(numNodes);
			}
			edgesFromNode[t.node1].add(t);
			edgesFromNode[t.node2].add(t);
			min.add(t);
		}

		minSum[0] = 0;
		minSum[1] = 0;
		for (int i = 2; i < k + 2; i++) {
			minSum[i] = minSum[i - 1] + min.poll().weight;
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

		// System.out.println("Number of function calls: ");
		// System.out.println("addNodes: " + callsNodes);
		// System.out.println("HasNoCircle: " + callshasnocircle);
		// System.out.println("UpdateSolution: " + callsupdatesolution);
		// System.out.println("addQueue: " + callsAddQueue);
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

		for (int i = 0; i < numNodes; i++) {
			t = getBestEdge(i);
			if (t != Integer.MAX_VALUE) {
				q.add(new Edge(i, -1, t));
			}
		}

		for (Edge e : q) {
			firstEstimate(new HashSet<Edge>(k), e.node2, 0,
					new PriorityQueue<Edge>(numEdges), new BitSet(numNodes));
		}

		for (Edge e : q) {
			addNodes(null, e.node2, 0, null, new BitSet(numNodes));
		}
	}

	public int getBestEdge(int node) {
		int ret = 0;
		for (Edge e : edgesFromNode[node]) {
			ret += e.weight;
		}
		return ret;

	}

	public boolean hasNoCircle(BitSet used, int node1, int node2) {
		// callshasnocircle++;
		if (used.get(node1) && used.get(node2)) {
			return false;
		}
		return true;
	}

	public void firstEstimate(HashSet<Edge> e, int node, int cweight,
			PriorityQueue<Edge> p, BitSet used) {
		Edge t;
		int w, newNode;
		boolean abort = false, wasEmpty, solutionFound;

		addToQueue(p, node, used, cweight);

		while (!p.isEmpty() && !abort) {
			t = p.poll();

			if (t.weight >= minWeight) {
				edgesFromNode[t.node1].remove(edgesFromNode[t.node1]
						.get(t.node2));
				edgesFromNode[t.node2].remove(edgesFromNode[t.node2]
						.get(t.node1));
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

					if (used.cardinality() == k && w < minWeight) {
						updateSolution(e, w);
					} else {
						firstEstimate(e, newNode, w, p, used);
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

	public void addNodes(HashSet<Edge> e, int node, int cweight,
			PriorityQueue<Edge> p, BitSet used) {

		// callsNodes++;

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

		addToQueue(p, node, used, cweight);

		while (!p.isEmpty()) {
			t = p.poll();
			w = cweight + t.weight;

			if (w + minSum[k - used.cardinality()] < minWeight
					&& !visited.contains(temp)) {
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
						addNodes(temp, newNode, w, p, used);

						if (size >= 2 && size < k) {
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
				p.clear();
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
		// callsupdatesolution++;
		minWeight = min;
		// System.out.println(System.currentTimeMillis() - start + "ms - " +
		// min);
		setSolution(min, minSet);
	}

	public void addToQueue(PriorityQueue<Edge> e, int node, BitSet used, int w) {
		// callsAddQueue++;

		Edge ite;
		Iterator<Edge> it = edgesFromNode[node].iterator();
		while (it.hasNext()) {
			ite = it.next();
			if (!used.get(node == ite.node1 ? ite.node2 : ite.node1)
					&& w + ite.weight < minWeight && !e.contains(ite)) {
				e.offer(ite);
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