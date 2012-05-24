package ads1ss12.pa;

import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 * @author Thomas Rieder, 1125403
 * @date 2012-05-24 
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

		// Performance-Boost xD

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
	 * cheapest. will eventually enumerate all possible solution;
	 * depth-first-search
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
		int w;
		int adjc = getAdjCount(node, adj);

		if (e != null) {
			temp = new HashSet<Edge>(e);
		} else {
			temp = new HashSet<Edge>(2 * k);
		}

		PriorityQueue<Edge> q = getQueue(node, adj);

		// iterates over all possible edges that can be appended to the node
		for (int i = 0; i < adjc; i++) {
			// get i-th most desireable edge
			t = q.poll();
			w = cweight + t.weight;

			// abort the recursion if the weight of the edge set is
			// higher than the currently known best solution
			if (t != null
					&& w < minWeight
					&& cheapestEdgeToNode(adj, t.node1 == node ? t.node2
							: t.node1, temp) >= t.weight) {
				
				// adds the new edge to the graph and calculates the new
				// weight
				temp.add(t);
				if (getNodeCount(temp) == k) {
					// edge set contains k nodes and is a new best
					// solution
					updateSolution(temp, w);
				} else {
					// recursion to add new edges
					if (getCheapestEdge(t.node1, adj).weight <= getCheapestEdge(
							t.node2, adj).weight) {
						// node1 has a cheaper edge - we follow it first
						adj[t.node1][t.node2] = 0;
						adj[t.node2][t.node1] = 0;
						addNodes(temp, adj, t.node1, w);
						addNodes(temp, adj, t.node2, w);
						adj[t.node1][t.node2] = t.weight;
						adj[t.node2][t.node1] = t.weight;
					} else {
						// node2 has a cheaper edge - we follow it first
						adj[t.node1][t.node2] = 0;
						adj[t.node2][t.node1] = 0;
						addNodes(temp, adj, t.node2, w);
						addNodes(temp, adj, t.node1, w);
						adj[t.node1][t.node2] = t.weight;
						adj[t.node2][t.node1] = t.weight;
					}
				}
				if (e != null) {
					temp = new HashSet<Edge>(e);
				} else {
					temp = new HashSet<Edge>(2 * k);
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
		// System.out.println("New best solution: " + min);
		setSolution(min, minSet);
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
	 * returns the most desireable edge adjacent to @param node
	 * 
	 * @param node
	 *            from-node
	 * @param adj
	 *            adjacency matrix
	 * @return cheapest edge
	 */
	public Edge getCheapestEdge(int node, int[][] adj) {
		Edge ret = null;
		for (int i = 0; i < numNodes; i++) {
			if (adj[node][i] != 0) {
				ret = new Edge(node, i, adj[node][i]);
			}
		}
		return ret;
	}

	/**
	 * returns a priority queue with all nodes adjacent to @param node
	 * @param node node
	 * @param adj adjacency-matrix
	 * @return priority queue of all edges adjacent to @param node
	 */
	public PriorityQueue<Edge> getQueue(int node, int[][] adj) {
		PriorityQueue<Edge> q = new PriorityQueue<Edge>();
		for (int i = 0; i < numNodes; i++) {
			if (adj[node][i] != 0) {
				q.add(new Edge(node, i, adj[node][i]));
			}
		}
		return q;
	}

	/**
	 * Returns the number of unique nodes in an edge-set
	 * 
	 * @param e
	 *            edge-set
	 * @return number of unique nodes
	 */
	public int getNodeCount(HashSet<Edge> e) {
		HashSet<Integer> nodes = new HashSet<Integer>();
		int ret = 0;
		for (Edge temp : e) {
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
	
	
	/**
	 * returns the cheapest node to a given @param node from the edge-set @param e
	 * @param adj adjacency-matrix
	 * @param node target node
	 * @param e edge-set
	 * @return cost of the cheapest node
	 */
	public int cheapestEdgeToNode(int[][] adj, int node, HashSet<Edge> e) {
		int ret = Integer.MAX_VALUE;
		if (node >= 0 && node <= numNodes) {
			for (Edge t : e) {
				if (adj[node][t.node1] < ret && adj[node][t.node1] != 0) {
					ret = adj[node][t.node1];
				}
				if (adj[node][t.node2] < ret && adj[node][t.node2] != 0) {
					ret = adj[node][t.node2];
				}
			}
		}

		return ret;
	}
}
