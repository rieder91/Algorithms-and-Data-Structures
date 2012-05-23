package ads1ss12.pa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 */
public class KMST extends AbstractKMST {
	private int adjacentMatrix[][];
	private int numNodes;
	private int k;
	private int minWeight = Integer.MAX_VALUE;

	@SuppressWarnings("unused")
	private int numEdges; // is never actually used

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

		// Create adjacency matrix
		Iterator<Edge> it = edges.iterator();
		Edge t;
		while (it.hasNext()) {
			t = it.next();
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

		// Performance-Boost xD

		// System.out.println("Adajzent-Matrix: ");
		// print(adjacentMatrix);
		// System.out.println("Gewicht der besten Lšsung: " + minWeight);
	}

	/**
	 * builds the mst with the first seed node being the node with the cheapest
	 * edge
	 */
	public void constructMST() {
		PriorityQueue<Edge> q = new PriorityQueue<Edge>();
		Edge t;

		adjacentMatrix = ToZero(adjacentMatrix);

		// builds a priority queue with the cheapest edge of each node
		for (int i = 0; i < numNodes; i++) {
			t = getEdge(i, 1, adjacentMatrix);
			if (t != null) {
				q.add(new Edge(i, -1, t.weight));
			}
		}

		// builds trees with the first seed being the most desireable node in
		// the queue
		for (int i = 0; i < q.size(); i++) {
			addNodes(null, adjacentMatrix, q.poll().node2, 0);
		}
	}

	/**
	 * recursive method that add all edges to a node beginning with the
	 * cheapest. will eventually enerumate all possible solution
	 * 
	 * @param e
	 *            edge-set that is being updated
	 * @param adj
	 *            adjacency-matrix
	 * @param node
	 *            node that was added last
	 */
	public void addNodes(HashSet<Edge> e, int[][] adj, int node, int cweight) {
		Edge t;
		HashSet<Edge> temp;
		int[][] tadj = cloneAdj(adj);
		int adjc = getAdjCount(node, adj);

		if (e != null) {
			temp = new HashSet<Edge>(e);
		} else {
			temp = new HashSet<Edge>();
		}

		// iterates over all possible edges that can be appended to the node
		for (int i = 0; i < adjc; i++) {
			// get i-th most desireable edge
			t = getEdge(node, i + 1, tadj);

			int w = cweight + t.weight;

			// abort the recursion if the weight of the edge set is
			// higher than the currently known best solution
			if (t != null && w < minWeight) {
				// list of all nodes to check for circles
				ArrayList<Integer> circle = getNodes(temp);
				if ((!circle.contains(t.node2) || !circle.contains(t.node1))) {
					// adds the new edge to the graph and calculates the new
					// weight
					temp.add(new Edge(t.node1, t.node2, t.weight));

					if (getNodeCount(temp) == k) {
						// edge set contains k nodes and is a new best
						// solution
						updateSolution(temp, w);
					} else {
						// recursion to add new edges
						if (getEdge(t.node1, 1, tadj).weight < getEdge(t.node2,
								1, tadj).weight) {
							// node1 has a cheaper edge - we follow it first
							addNodes(temp, removeNode(tadj, t.node1, t.node2),
									t.node1, w);
							addNodes(temp, removeNode(tadj, t.node1, t.node2),
									t.node2, w);
							tadj = cloneAdj(adj);
						} else {
							// node2 has a cheaper edge - we follow it first
							addNodes(temp, removeNode(tadj, t.node1, t.node2),
									t.node2, w);
							addNodes(temp, removeNode(tadj, t.node1, t.node2),
									t.node1, w);
							tadj = cloneAdj(adj);
						}
					}
				}
				if (e != null) {
					temp = new HashSet<Edge>(e);
				} else {
					temp = new HashSet<Edge>();
				}
			} else {
				i = adjc;
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
		System.out.println("New best solution: " + min);
		setSolution(min, minSet);
	}

	/**
	 * Clones an adjacency matrix
	 * 
	 * @param adj
	 *            input matrix
	 * @return cloned matrix
	 */
	public int[][] cloneAdj(int[][] adj) {
		int[][] ret = new int[numNodes][numNodes];
		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {
				ret[i][j] = adj[i][j];
			}
		}
		return ret;
	}

	/**
	 * returns the number of nodes adjacent to @param node
	 * 
	 * @param node
	 *            node
	 * @param adj
	 *            adjacency matrix
	 * @return number of adjacent nodes
	 */
	public int getAdjCount(int node, int[][] adj) {
		int ret = 0;
		for (int i = 0; i < numNodes; i++) {
			if (adj[node][i] != 0) {
				ret++;
			}
		}
		return ret;
	}

	/**
	 * returns the cnt'th most desireable edge (n'th cheapest)
	 * 
	 * @param node
	 *            from-node
	 * @param cnt
	 *            n-th best edge
	 * @param adj
	 *            adjacency matrix
	 * @return n-th cheapest edge
	 */
	public Edge getEdge(int node, int cnt, int[][] adj) {
		PriorityQueue<Edge> q = new PriorityQueue<Edge>();
		for (int i = 0; i < numNodes; i++) {
			if (adj[node][i] != 0) {
				q.add(new Edge(node, i, adj[node][i]));
			}
		}

		Edge ret = null;
		for (int i = 0; i < cnt; i++) {
			ret = q.poll();
		}

		return ret;

	}

	/**
	 * Returns the number of unique nodes in an edge-set
	 * 
	 * @param e
	 *            edge-set
	 * @return number of unique nodes
	 */
	public int getNodeCount(HashSet<Edge> e) {
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		int ret = 0;
		Iterator<Edge> it = e.iterator();
		Edge temp;
		while (it.hasNext()) {
			temp = it.next();
			if (!nodes.contains(temp.node1)) {
				nodes.add(temp.node1);
				ret++;
			}
			if (!nodes.contains(temp.node2)) {
				nodes.add(temp.node2);
				ret++;
			}
		}
		return ret;
	}

	/**
	 * returns an arraylist contains all nodes of the edge set (with duplicates)
	 * 
	 * @param set
	 *            edge-set
	 * @return list of all nodes in the edge-set
	 */
	public ArrayList<Integer> getNodes(HashSet<Edge> set) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		Iterator<Edge> it = set.iterator();
		Edge temp;
		while (it.hasNext()) {
			temp = it.next();
			// we dont care about duplicates - no .contains(...)
			ret.add(temp.node1);
			ret.add(temp.node2);
		}
		return ret;
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
				System.out.print(" " + adj[i][j]);
			}
			System.out.println(" ");
		}
	}

	/**
	 * Removes the edge between two nodes from the adjacency-matrix
	 * 
	 * @param adj
	 *            adjacency matrix
	 * @param node
	 *            node 1
	 * @param tNode
	 *            node 2
	 * @return adjacency matrix without the edge from @param node to @param
	 *         tNode
	 */
	public int[][] removeNode(int[][] adj, int node, int tNode) {
		adj[tNode][node] = 0;
		adj[node][tNode] = 0;
		return adj;
	}

	/**
	 * sets all edges with a cost of 1000 to zero; i assumed that a cost of 100
	 * means unreachable
	 * 
	 * @param adj
	 *            matrix
	 * @return matrix without costs of 1000
	 */
	public int[][] ToZero(int[][] adj) {
		for (int i = 0; i < numNodes; i++) {
			for (int j = 0; j < numNodes; j++) {
				if (adj[i][j] == 1000) {
					adj[i][j] = 0;
				}
			}
		}
		return adj;
	}
}
