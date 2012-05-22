package ads1ss12.pa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Klasse zum Berechnen eines k-MST mittels Branch-and-Bound. Hier sollen Sie
 * Ihre L&ouml;sung implementieren.
 */
public class KMST extends AbstractKMST {
	private int adjacentMatrix[][];
	private int numNodes;
//	private int numEdges;
	private int k;
	private int minWeight = Integer.MAX_VALUE;


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
//		this.numEdges = numEdges;
		this.k = k;


		Iterator<Edge> it = edges.iterator();
		Edge t;
		while(it.hasNext()) {
			t = it.next();
			adjacentMatrix[t.node1][t.node2] = t.weight;
			adjacentMatrix[t.node2][t.node1] = t.weight;
		}
	}

	public void print(int[][] adj) {
		for(int i = 0; i < numNodes; i++) {
			for(int j = 0; j < numNodes; j++) {
				System.out.print(" " + adj[i][j]);
			}
			System.out.println(" ");
		}
	}

	public int[][] ToZero(int[][] adj) {
		for(int i = 0; i < numNodes; i++) {
			for(int j = 0; j < numNodes; j++) {
				if(adj[i][j] == 1000) {
					adj[i][j] = 0;
				}
			}
		}
		return adj;
	}

	public void constructMST() {
		adjacentMatrix = ToZero(adjacentMatrix);
		for(int i = 0; i < numNodes; i++) {
			addNodes(null, adjacentMatrix, i);
		}
	}

	public int[][] cloneAdj(int[][] adj) {
		int[][] ret = new int[numNodes][numNodes];
		for(int i = 0; i < numNodes; i++) {
			for(int j = 0; j < numNodes; j++) {
				ret[i][j] = adj[i][j];
			}
		}
		return ret;
	}

	public void updateSolution(HashSet<Edge> minSet, int min) {
		minWeight = min;
		//		System.out.println("Neues Gewicht: " + min);
		setSolution(min, minSet);
	}

	public void addNodes(HashSet<Edge> e, int[][] adj, int node) {
		Edge t;
		HashSet<Edge> temp;
		int[][] tadj = cloneAdj(adj);
		int adjc = getAdjCount(node, adj);


		if(e != null) {
			temp = new HashSet<Edge>(e);
		} else {
			temp = new HashSet<Edge>();
		}

		for(int i = 0; i < adjc; i++) {
			t = getEdge(node, i, tadj);
			if(t != null) {
				ArrayList<Integer> circle = getNodes(temp);
				if((!circle.contains(t.node2) || !circle.contains(t.node1)) && t.weight != 0) {
					temp.add(new Edge(t.node1, t.node2, t.weight));
					int w = getWeight(temp);
					if(w < minWeight) {
						if(getNodeCount(temp) == k) {
							updateSolution(temp, w);
						} else {
							addNodes(temp, removeNode(tadj, t.node1, t.node2), t.node1);
							addNodes(temp, removeNode(tadj, t.node1, t.node2), t.node2);
						}
					}
				}
				if(e != null) {
					temp = new HashSet<Edge>(e);
				} else {
					temp = new HashSet<Edge>();
				}

			}
			// Auskommentiert = es wird nicht alles gesucht, bleibt aber trotzdem unter der Grenze => schneller
//			tadj = cloneAdj(adj);
		}
	}

	public int getNodeCount(HashSet<Edge> e) {
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		int ret = 0;
		Iterator<Edge> it = e.iterator();
		Edge temp;
		while(it.hasNext()) {
			temp = it.next();
			if(!nodes.contains(temp.node1)) {
				nodes.add(temp.node1);
				ret++;
			}
			if(!nodes.contains(temp.node2)) {
				nodes.add(temp.node2);
				ret++;
			}
		}
		return ret;
	}

	public int[][] removeNode(int[][] adj, int node, int tNode) {
		adj[tNode][node] = 0;
		adj[node][tNode] = 0;
		return adj;
	}

	public ArrayList<Integer> getNodes(HashSet<Edge> set) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		Iterator<Edge> it = set.iterator();
		Edge temp;
		while(it.hasNext()) {
			temp = it.next();
			ret.add(temp.node1);
			ret.add(temp.node2);
		}
		return ret;
	}

	public Edge getEdge(int node, int cnt, int[][] adj) {
		cnt++;
		
		PriorityQueue<Edge> q = new PriorityQueue<Edge>();
		for(int i = 0; i < numNodes; i++) {
			if(adj[node][i] != 0) {
				q.add(new Edge(node, i, adj[node][i]));
			}
		}

		Edge ret = null;
		for(int i = 0; i < cnt; i++) {
			ret = q.poll();
		}

		return ret;

	}

	public int getWeight(Set<Edge> e) {
		Iterator<Edge> it = e.iterator();
		int sum = 0;

		while(it.hasNext()) {
			sum += it.next().weight;
		}

		return sum;
	}

	public int getAdjCount(int node, int[][] adj) {
		int ret = 0;
		for(int i = 0; i < numNodes; i++) {
			if(adj[node][i] != 0) {
				ret++;
			}
		}
		return ret;
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
		
//		System.out.println("Adajzent-Matrix: ");
//		print(adjacentMatrix);	
//		System.out.println("Gewicht der besten Lšsung: " + minWeight);
	}

}
