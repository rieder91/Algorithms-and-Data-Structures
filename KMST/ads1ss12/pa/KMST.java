package ads1ss12.pa;

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
	private HashSet<Integer> usedNodes;

	@SuppressWarnings("unused")
	private int numEdges; // is never actually used

	int[] minCost;

	private static int calls = 0;

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
		this.usedNodes = new HashSet<Integer>();
		this.minCost = new int[k];

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
		// System.out.println(calls);

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
		PriorityQueue<Edge> q2 = new PriorityQueue<Edge>();
		Edge t;
		Edge e;
		int j = 0;

		// builds a priority queue with the cheapest edge of each node
		for (int i = 0; i < numNodes; i++) {
			t = getCheapestEdge(i, adjacentMatrix);
			if (t != null) {
				q.add(new Edge(i, -1, t.weight));
				q2.add(new Edge(i, -1, t.weight));
			}
		}

		while (j < k) {
			e = q2.poll();
			if (j == 0) {
				minCost[0] = e.weight;
			} else {
				minCost[j] = minCost[j - 1] + e.weight;
			}
			j++;
		}

		// builds trees with the first seed being the most desireable node in
		// the queue

		for (int i = 0; i < numNodes; i++) {
			addNodes(null, adjacentMatrix, q.poll().node2, 0, null);
			usedNodes.clear();
		}
	}

	public boolean hasNoCircle(HashSet<Integer> used, int node1, int node2) {
		if (used.contains(node1) && used.contains(node2)) {
			return false;
		}
		return true;
	}

	public void addNodes(HashSet<Edge> e, int[][] adj, int node, int cweight,
			PriorityQueue<Edge> p) {

		calls++;
		Edge t;
		HashSet<Edge> temp;
		int w;
		int newNode = node;

		if (p != null) {
			p = new PriorityQueue<Edge>(p);
		} else {
			p = new PriorityQueue<Edge>();
		}

		if (e != null) {
			temp = new HashSet<Edge>(e);
		} else {
			temp = new HashSet<Edge>(2 * k);
		}

		addToQueue(p, node, adj);

		// iterates over all possible edges that can be appended to the node
		while (!p.isEmpty()) {
			// get i-th most desireable edge
			t = p.poll();
			w = cweight + t.weight;

			if (w < minWeight && w + minCost[k - temp.size() - 2] < minWeight) {

				if (usedNodes.contains(t.node1)) {
					newNode = t.node2;
					node = t.node1;
				} else {
					newNode = t.node1;
					node = t.node2;
				}

				if (hasNoCircle(usedNodes, t.node1, t.node2)
						&& cheapestEdgeToNode(adj, newNode) >= t.weight) {
					temp.add(t);

					if (usedNodes.size() == 0) {
						usedNodes.add(newNode);
						usedNodes.add(node);
					} else {
						usedNodes.add(newNode);
					}

					if (temp.size() + 1 == k) {
						updateSolution(temp, w);
					} else {
						addNodes(temp, adj, newNode, w, p);
						if (e != null) {
							temp = new HashSet<Edge>(e);
							usedNodes.remove(newNode);
						} else {
							temp = new HashSet<Edge>();
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
		// System.out.println(min);
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
		for (int i = 0; i < numNodes; i++) {
			if (adj[i][node] != 0) {
				Edge it = new Edge(node, i, adj[i][node]);
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
		for (Integer n : usedNodes) {
			if (adj[n][node] != 0 && adj[n][node] < ret) {
				ret = adj[n][node];
			}
		}
		return ret;
	}
}