package ads1ss12.pa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;

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
	private HashSet<Integer> usedNodes;


	@SuppressWarnings("unused")
	private int numEdges; // is never actually used
	
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
		System.out.println(System.currentTimeMillis());
		constructMST();
		System.out.println(calls);

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
		
		Edge e;
		for (int i = 0; i < q.size(); i++) {
			e = q.poll();
			addNodes(null, adjacentMatrix, e.node2, 0, new PriorityQueue<Edge>(), 2);
			usedNodes = new HashSet<Integer>();
		}
	}
	
	
	public boolean hasCircle(int node1, int node2) {
		return usedNodes.contains(node1) && usedNodes.contains(node2);
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
	public void addNodes(HashSet<Edge> e, int[][] adj, int node, int cweight, PriorityQueue<Edge> p, int cnt) {
		
		calls++;
		Edge t;
		HashSet<Edge> temp;
		int w;
		int newNode = node;
		boolean updateNeeded = false;
		
		p = new PriorityQueue<Edge>(p);

		if (e != null) {
			temp = new HashSet<Edge>(e);
		} else {
			temp = new HashSet<Edge>(2 * k);
		}
		
		addToQueue(p, node, adj);


		// iterates over all possible edges that can be appended to the node
		while(!p.isEmpty()) {
			// get i-th most desireable edge
			t = p.poll();			

			w = cweight + t.weight;
			cnt++;
			// abort the recursion if the weight of the edge set is
			// higher than the currently known best solution
			if (w < minWeight
//					&& w < 1300
//					&& !hasCircle(t.node1, t.node2)
					/* && cheapestEdgeToNode(adj, t.node1 == node ? t.node2
							: t.node1) >= t.weight*/) {
				
				// adds the new edge to the graph and calculates the new
				// weight
				
				temp.add(t);
				int sizeBefore = temp.size();
				
				if(usedNodes.contains(t.node1)) {
					newNode = t.node2;
					node = t.node1;
				} else {
					newNode = t.node1;
					node = t.node2;
				}
				
				if(usedNodes.size() == 0) {
					usedNodes.add(newNode);
					usedNodes.add(node);
				} else {
					usedNodes.add(newNode);
				}

							
//				if(getNodes(temp) != usedNodes.size()) {
//					System.out.println(usedNodes.size());
//					System.out.println("BREAK");
//				}
				updateNeeded = true;

				if (usedNodes.size() == k) {
					// edge set contains k nodes and is a new best
					// solution
					updateSolution(temp, w);
				} else {
					// recursion to add new edges
		
					adj[t.node1][t.node2] = 0;
					adj[t.node2][t.node1] = 0;

					addNodes(new HashSet<Edge>(temp), adj, newNode, w, new PriorityQueue<Edge>(p), cnt);	
							
					adj[t.node1][t.node2] = t.weight;
					adj[t.node2][t.node1] = t.weight;
					
					
					
					
				}
				

				if (e != null) {
					temp = new HashSet<Edge>(e);
					
				} else {
					p = new PriorityQueue<Edge>();

				}	
			} else {		
				p = new PriorityQueue<Edge>();

			}
			
			int asdf = getNodes(temp);
			int jkl = usedNodes.size();
			
			
//			if(sizeBefore < sizeAfter) {
			
			
			if(asdf != jkl) {
				usedNodes.remove(newNode);
			}

			node = newNode;
		}
	}
	
	
	public boolean containsNode(HashSet<Edge> es, int n) {
		for(Edge e : es) {
			if(e.node1 == n  || e.node2 == n) {
				return true;
			}
		}
		return false;
	}
	
	public int getNodes(HashSet<Edge> e) {
		ArrayList<Integer> a = new ArrayList<Integer>();
		for(Edge it : e) {
			if(!a.contains(it.node1)) {
				a.add(it.node1);
			} 
			if(!a.contains(it.node2)) {
				a.add(it.node2);
			}
		}
		return a.size();
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
		System.out.println(min);
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
		Edge ret = new Edge(-1, -1, Integer.MAX_VALUE);
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
//				if(usedNodes.contains(node) == false && usedNodes.contains(i) == false)
					q.offer(new Edge(node, i, adj[node][i]));
			}
		}
		return q;
	}
	
	public void addToQueue(PriorityQueue<Edge> e, int node, int[][] adj) {
		for(int i = 0; i < numNodes; i++) {
			if(adj[node][i] != 0) {
//				if(usedNodes.contains(node) == false && usedNodes.contains(i) == false) {
					Edge it = new Edge(node, i, adj[node][i]);
					if(!e.contains(it)) {
						e.offer(it);
//					}
				}
			}
		}
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
//	public int cheapestEdgeToNode(int[][] adj, int node) {
//		int ret = Integer.MAX_VALUE;
//		for(Integer n : usedNodes) {
//			if(adj[n][node] != 0 && adj[n][node] < ret) {
//				ret = adj[n][node];
//			}
//		}
//		return ret;
//	}
}