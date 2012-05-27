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
 * @version 1.0
 */
public class KMST extends AbstractKMST {
	private ArrayList<Edge>[] edgesFromNode;
	private HashSet<HashSet<Edge>> visited;
	// private int adjacentMatrix[][];
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
		// this.adjacentMatrix = new int[numNodes][numNodes];
		this.numNodes = numNodes;
		this.numEdges = numEdges;
		this.k = k;
		this.visited = new HashSet<HashSet<Edge>>(numEdges);
		this.minSum = new int[k + 2];
		this.edgesFromNode = new ArrayList[numNodes];

		// PriorityQueue for the k cheapest edges
		PriorityQueue<Edge> min = new PriorityQueue<Edge>(numNodes);

		// Create data structures
		for (Edge t : edges) {
			// adjacentMatrix[t.node1][t.node2] = t.weight;
			// adjacentMatrix[t.node2][t.node1] = t.weight;
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

		// i use the sum of the k - |V| cheapest edges to determine if a given
		// graph could ever be better than the minWeight
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

		// Debugging
		// System.out.println("Number of function calls: ");
		// System.out.println("addNodes: " + callsNodes);
		// System.out.println("HasNoCircle: " + callshasnocircle);
		// System.out.println("UpdateSolution: " + callsupdatesolution);
		// System.out.println("addQueue: " + callsAddQueue);
		// System.out.println("Adjazenzmatrix: ");
		// print(adjacentMatrix);
	}

	/**
	 * builds the mst with the first seed node being the node with the smallest
	 * sum of edge-costs
	 */
	public void constructMST() {
		PriorityQueue<Edge> q = new PriorityQueue<Edge>(numNodes);
		int t;

		// adds the sums of all nodes to the PriorityQueue
		for (int i = 0; i < numNodes; i++) {
			t = getBestEdge(i);
			if (t != Integer.MAX_VALUE) {
				q.add(new Edge(i, -1, t));
			}
		}

		// estimate to determine a good upper bound
		// no backtracking
		for (Edge e : q) {
			firstEstimate(new HashSet<Edge>(k), e.node2, 0,
					new PriorityQueue<Edge>(numEdges), new BitSet(numNodes));
		}

		// beginning with the best node it enumerates all possible solutions
		// (branch) and cuts if the graph is useless
		for (Edge e : q) {
			addNodes(null, e.node2, 0, null, new BitSet(numNodes));
		}
	}

	/**
	 * Returns the sum of all edges adjacent to a node
	 * 
	 * @param node
	 *            Node of which the edge-sum is calculated
	 * @return sum of the edge-costs
	 */
	public int getBestEdge(int node) {
		int ret = 0;
		for (Edge e : edgesFromNode[node]) {
			ret += e.weight;
		}
		return ret;

	}

	/**
	 * true if there would be no circle if @param node1 and @param node2 were
	 * added
	 * 
	 * @param used
	 *            bitset of all nodes used in the current solution
	 * @param node1
	 *            Node 1
	 * @param node2
	 *            Node 2
	 * @return true if the two nodes would not create a loop
	 */
	public boolean hasNoCircle(BitSet used, int node1, int node2) {
		// callshasnocircle++;
		if (used.get(node1) && used.get(node2)) {
			return false;
		}
		return true;
	}

	/**
	 * always adds the cheapest edge to a given graph and stops if k nodes are
	 * reached; only checks for circles - no heuristics
	 * 
	 * @param e
	 *            edge-set
	 * @param node
	 *            seed-node
	 * @param cweight
	 *            current weight
	 * @param p
	 *            priorityqueue with all edges to be added
	 * @param used
	 *            bitset of all used nodes in the current solution
	 */
	public void firstEstimate(HashSet<Edge> e, int node, int cweight,
			PriorityQueue<Edge> p, BitSet used) {
		Edge t;
		int w, newNode;
		boolean abort = false, wasEmpty, solutionFound;

		// adds to elements to the edge-queue
		addToQueue(p, node, used, cweight);

		while (!p.isEmpty() && !abort) {
			t = p.poll();

			// if a given node has a higher weight than minWeight we can ignore
			// it entirely
			if (t.weight >= minWeight) {
				edgesFromNode[t.node1].remove(edgesFromNode[t.node1]
						.get(t.node2));
				edgesFromNode[t.node2].remove(edgesFromNode[t.node2]
						.get(t.node1));
			} else {
				w = cweight + t.weight;

				// circle check
				if (hasNoCircle(used, t.node1, t.node2)) {
					// make sure to quit the loop
					abort = true;

					if (used.get(t.node1)) {
						// node1 is already in use => node2 is new
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
						// first edge
						used.set(newNode);
						used.set(node);
						wasEmpty = true;
					} else {
						used.set(newNode);
					}

					// if |V| = k and the solution is better than minWeight, we
					// update our best solution
					if (used.cardinality() == k && w < minWeight) {
						updateSolution(e, w);
					} else {
						// we need to add more edges
						firstEstimate(e, newNode, w, p, used);
					}
					// removes the used nodes
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

	/**
	 * Main algorithm; Starting from a seed node it expands to the cheapest edge
	 * that can be connected to the graph. It cuts the enumeration tree if the
	 * weight is too high. Uses Backtracking.
	 * 
	 * @param e
	 *            edge-set
	 * @param node
	 *            seed-node
	 * @param cweight
	 *            current weight
	 * @param p
	 *            priorityqueue with all edges that can be added to the graph
	 * @param used
	 *            bitset of all used nodes
	 */
	public void addNodes(HashSet<Edge> e, int node, int cweight,
			PriorityQueue<Edge> p, BitSet used) {

		// callsNodes++;

		Edge t;
		HashSet<Edge> temp = new HashSet<Edge>(k);
		int w, newNode, size;
		boolean wasEmpty, solutionFound;

		// clone
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

		// expand node
		addToQueue(p, node, used, cweight);

		while (!p.isEmpty()) {
			t = p.poll();
			w = cweight + t.weight;

			// if the weight of the current graph plus the weight of the (k -
			// |V|) cheapest edges is greater than minWeight, we can abort. we
			// also stop enumerating if the current graph has been expanded
			// before
			if (w + minSum[k - used.cardinality()] < minWeight
					&& !visited.contains(temp)) {
				// circle check
				if (hasNoCircle(used, t.node1, t.node2)) {
					if (used.get(t.node1)) {
						// node1 is part of the graph => node2 is new
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
						// first edge
						used.set(newNode);
						used.set(node);
						wasEmpty = true;
					} else {
						used.set(newNode);
					}

					// number of used nodes
					size = used.cardinality();

					if (size == k) {
						// new best solution found
						updateSolution(temp, w);
						solutionFound = true;
					} else {
						// we need to expand more
						addNodes(temp, newNode, w, p, used);

						// if the graph contains more than 2 and less than k
						// nodes we save it to prevent the repeated enumeration
						// of the same solutions
						if (size >= 2 && size < k) {
							visited.add(temp);
						}

						temp = new HashSet<Edge>(k);
						if (e != null) {
							temp.addAll(e);
						}
					}
					// clear nodes
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

	/**
	 * adds all valid edges from @param node to the priorityqueue
	 * 
	 * @param e
	 *            current todo-edge-list
	 * @param node
	 *            node that is to be expanded
	 * @param used
	 *            bitset of used nodes
	 * @param w
	 *            current weight
	 */
	public void addToQueue(PriorityQueue<Edge> e, int node, BitSet used, int w) {
		// callsAddQueue++;

		// we iterate through all adjacent nodes using the adjacency list
		Edge ite;
		Iterator<Edge> it = edgesFromNode[node].iterator();
		while (it.hasNext()) {
			ite = it.next();
			// if the expaning node == ite.node1 we check if node2 is used to
			// prevent a circle and vice verca; the weight of the current graph
			// + the weight of a possible edge also has to be < minWeight; we
			// also do not allow duplicates in the todo-edge-list
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