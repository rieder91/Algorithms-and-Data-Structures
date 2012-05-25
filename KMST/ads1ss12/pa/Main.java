package ads1ss12.pa;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/**
 * Diese Klasse enth&auml;lt nur die {@link #main main()}-Methode zum Starten
 * des Programms, sowie {@link #printDebug(String)} und
 * {@link #printDebug(Object)} zum Ausgeben von Debug Meldungen.
 * 
 * <p>
 * <b>WICHTIG:</b> Nehmen Sie keine &Auml;nderungen in dieser Klasse vor. Bei
 * der Abgabe werden diese &Auml;nderungen verworfen und es k&ouml;nnte dadurch
 * passieren, dass Ihr Programm somit nicht mehr korrekt funktioniert.
 * </p>
 */
public class Main {

	/**
	 * Der Name der Datei, aus der die Testinstanz auszulesen ist. Ist <code>
	 * null</code>, wenn von {@link System#in} eingelesen wird.
	 */
//	private static String fileName = null;
	private static String fileName = "tests/input/0004";
//	private static String fileName = "tests/input/0005"; // hardcore

	/** Der abgeschnittene Pfad */
	private static String choppedFileName;

	/**
	 * Mit diesem flag kann verhindert werden, dass der Thread nach Ablauf der
	 * Zeit beendet wird.
	 */
	private static boolean dontStop = true;

	/** Test flag f&uuml;r Laufzeit Ausgabe */
	private static boolean test = true;

	/** Debug flag f&uuml;r zus&auml;tzliche Debug Ausgaben */
	private static boolean debug = false;

	/** Die Anzahl der Knoten */
	private static Integer numNodes;

	/** Die Anzahl der Kanten */
	private static Integer numEdges;

	/** Der Wert k */
	private static Integer k;

	/** Der Schwellwert f&uuml;r die gelbe Schranke */
	private static Integer threshold;

	/**
	 * Liest die Daten einer Testinstanz ein und &uuml;bergibt sie an die
	 * entsprechenden Methoden der k-MST Implementierung.
	 * 
	 * <p>
	 * Wenn auf der Kommandozeile die Option <code>-d</code> angegeben wird,
	 * werden s&auml;mtliche Strings, die an {@link Main#printDebug(String)}
	 * &uuml;bergeben werden, ausgegeben.
	 * </p>
	 * 
	 * <p>
	 * Der erste String in <code>args</code>, der <em>nicht</em> mit <code>-d
	 * </code>, <code>-t</code>, oder <code>-s</code> beginnt, wird als der Pfad
	 * zur Datei interpretiert, aus der die Testinstanz auszulesen ist. Alle
	 * nachfolgenden Parameter werden ignoriert. Wird kein Dateiname angegeben,
	 * wird die Testinstanz &uuml;ber {@link System#in} eingelesen.
	 * </p>
	 * 
	 * @param args
	 *            Die von der Kommandozeile &uuml;bergebenen Argumente. Die
	 *            Option <code>-d</code> aktiviert debug-Ausgaben &uuml;ber
	 *            {@link #printDebug(String)}, <code>-t</code> gibt
	 *            zus&auml;tzlich Dateiname und Laufzeit aus und <code>-s</code>
	 *            verhindert, dass Ihr Algorithmus nach 30 Sekunden beendet
	 *            wird. Der erste andere String wird als Dateiname
	 *            interpretiert.
	 */
	public static void main(String[] args) {

		Scanner is = processArgs(args);

		SecurityManager oldsm = null;
		try {
			oldsm = System.getSecurityManager();
			SecurityManager sm = new ADS1SecurityManager();
			System.setSecurityManager(sm);
		} catch (SecurityException e) {
			bailOut("Error: could not set security manager: " + e);
		}

		try {
			run(readInput(is));
			// Security Manager ruecksetzen
			System.setSecurityManager(oldsm);
		} catch (SecurityException se) {
			bailOut("Unerlaubter Funktionsaufruf: \"" + se.toString() + "\"");
		} catch (NumberFormatException e) {
			bailOut("Falsches Inputformat: \"" + e.toString() + "\"");
		} catch (Exception e) {
			e.printStackTrace();
			bailOut("Ausnahme \"" + e.toString() + "\"");
		}

	}

	/**
	 * Liest einen Testfall vom &uuml;bergebenen {@link Scanner} ein.
	 * 
	 * @param is
	 *            Der {@link Scanner}
	 * @return die Menge der Kanten im gegebenen Graphen
	 * @throws Exception
	 *             falls der Testfall nicht der Spezifikation entspricht.
	 */
	protected static Set<Edge> readInput(Scanner is) throws Exception {

		numNodes = Integer.valueOf(is.nextLine());
		numEdges = Integer.valueOf(is.nextLine());

		k = Integer.valueOf(is.nextLine());
		if (k <= 0) {
			throw new Exception("k negativ!");
		}

		threshold = Integer.valueOf(is.nextLine());

		Set<Edge> e = new HashSet<Edge>((int)(numEdges/0.75));

		for (int edge_counter = 0; (is.hasNext()); edge_counter++) {

			String val[] = is.nextLine().split(" ");
			if (val.length != 4) {
				throw new Exception("Fehlerhafte Zeile (Kante #" + edge_counter
						+ ")");
			}

			int numEdge = Integer.valueOf(val[0]);
			int node1 = Integer.valueOf(val[1]);
			int node2 = Integer.valueOf(val[2]);
			int weight = Integer.valueOf(val[3]);

			if (numEdge != edge_counter) {
				throw new Exception("Fehlerhafte Zeile (Kante #" + edge_counter
						+ ")");
			}

			if (!(0 <= node1 && node1 < numNodes)) {
				throw new Exception("Knoten 1 fehlerhaft (Kante #"
						+ edge_counter + ")");
			}
			if (!(0 <= node2 && node2 < numNodes)) {
				throw new Exception("Knoten 1 fehlerhaft (Kante #"
						+ edge_counter + ")");
			}
			if (weight < 0) {
				throw new Exception("Gewicht negativ (Kante #" + edge_counter
						+ ")");
			}

			e.add(new Edge(node1, node2, weight));
		}

		if (e.size() != numEdges) {
			throw new Exception("Nicht genuegend Kanten gefunden!");
		}

		return e;
	}

	/**
	 * Startet Ihre k-MST Implementierung mit einen Testfall und
	 * &uuml;berpr&uuml;ft danach Ihre L&ouml;sung.
	 * 
	 * @param edges
	 *            Kantenmege des Graphen
	 * 
	 * @throws Exception
	 *             Signalisiert eine Ausnahme
	 */
	@SuppressWarnings("deprecation")
	protected static void run(Set<Edge> edges) throws Exception {
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis();
		long offs = end - start;
		long timeout = 30000; // 30 seconds

		chopFileName();

		AbstractKMST kmst = new KMST(numNodes, numEdges, new HashSet<Edge>(
				edges), k);

		// create kmst thread
		Thread thread = new Thread(kmst, "k-MST Thread");
		// start kmst thread
		thread.start();

		if (dontStop)
			thread.join(0);
		else {
			// kill kmst thread after at most timeout millisecs
			thread.join(timeout);
			if (thread.isAlive())
				thread.stop();
		}

		// store end time
		end = System.currentTimeMillis();

		AbstractKMST.BnBSolution sol = kmst.getSolution();
		Set<Edge> solution = sol.getBestSolution();
		int upper_bound = sol.getUpperBound();

		// destoy user object
		kmst = null;

		// check solution

		if (solution == null) {
			bailOut("keine gueltige Loesung!");
		}

		// check edges
		if (!edges.containsAll(solution)) {
			bailOut("Loesung enthaelt Kanten die nicht im Ursprungsgraph vorhanden sind!");
		}

		// check tree
		checkTree(solution, k);

		// check weight
		int weight = 0;

		for (Edge e : solution) {
			weight += e.weight;
		}

		if (weight != upper_bound) {
			bailOut("Die obere Grenze muss immer gleich der aktuell besten Loesung sein!");
		}

		// Ergebnis ausgeben
		StringBuffer msg = new StringBuffer(test ? choppedFileName + ": " : "");

		long sum = end - start - offs;

		printDebug("Loesung: " + solution);
		if (upper_bound > threshold)
			bailOut("zu schlechte Loesung: Ihr Ergebnis " + upper_bound
					+ " liegt ueber dem Schwellwert (" + threshold + ")");

		msg.append("Ihr Wert ist unter dem Schwellwert\n");
		msg.append(upper_bound);

		if (test)
			msg.append(", Zeit: "
					+ (sum > 1000 ? sum / 1000 + "s" : sum + "ms"));

		System.out.println();
		System.out.println(msg.toString());
	}

	/**
	 * Diese Methode &uuml;berprüft ob eine Lösung einen gültigen Baum mit exakt
	 * k Knoten darstellt.
	 * 
	 * @param solution
	 *            Die Lösung die überprüft werden soll
	 * @param k
	 *            Anzahl der erforderlichen Knoten.
	 */
	private static void checkTree(Set<Edge> solution, int k) {
		// check tree
		Map<Integer, Set<Integer>> adjlist = new HashMap<Integer, Set<Integer>>();
		Set<Integer> nodes = new HashSet<Integer>();

		// create adjlist
		for (Edge e : solution) {
			nodes.add(e.node1);
			nodes.add(e.node2);
			Set<Integer> s = null;
			if (e.node1 < e.node2) {
				if (adjlist.containsKey(e.node1)) {
					s = adjlist.get(e.node1);
				} else {
					s = new HashSet<Integer>();
					adjlist.put(e.node1, s);
				}
				s.add(e.node2);
			} else {
				if (adjlist.containsKey(e.node2)) {
					s = adjlist.get(e.node2);
				} else {
					s = new HashSet<Integer>();
					adjlist.put(e.node2, s);
				}
				s.add(e.node1);
			}
		}

		// check overall node count
		if (nodes.size() != k) {
			bailOut("Der K-MST enthaelt nicht genau k Knoten!");
		}
		// check reachable nodes
		nodes = new HashSet<Integer>();
		// we stored only edges (n1,n2) in the adjlist if n1<n2
		// so if we start at the minimal element we should reach all nodes
		collectNodes(Collections.min(adjlist.keySet()), adjlist, nodes);
	}

	/**
	 * Diese Methode durchwandert den Baum, überprüft Kreisfreiheit und sammelt
	 * alle Knoten.
	 * 
	 * @param key
	 *            Der aktuelle Knoten
	 * @param adjlist
	 *            Die Adjazenzliste des Baums
	 * @param nodes
	 *            Die gesammelten Knoten.
	 */
	private static void collectNodes(int key,
			Map<Integer, Set<Integer>> adjlist, Set<Integer> nodes) {
		nodes.add(key);

		if (adjlist.containsKey(key)) {
			for (Integer c : adjlist.get(key)) {
				if (nodes.contains(c)) {
					// loop check
					bailOut("Loesung ist nicht kreisfrei!");
				}
				collectNodes(c, adjlist, nodes);
			}
		}

	}

	/**
	 * &Ouml;ffnet die Eingabedatei und gibt einen {@link Scanner} zur&uuml;ck,
	 * der von ihr liest. Falls kein Dateiname angegeben wurde, wird von
	 * {@link System#in} gelesen.
	 * 
	 * @return Einen {@link Scanner} der von der Eingabedatei liest.
	 */
	private static Scanner openInputFile() {
		if (fileName != null)
			try {
				return new Scanner(new FileInputStream(fileName));
			} catch (NoSuchElementException e) {
				bailOut("\"" + fileName + "\" is empty");
			} catch (Exception e) {
				bailOut("could not open \"" + fileName + "\" for reading");
			}

		return new Scanner(System.in);

	}

	/**
	 * Interpretiert die Parameter, die dem Programm &uuml;bergeben wurden und
	 * gibt einen {@link Scanner} zur&uuml;ck, der von der Testinstanz liest.
	 * 
	 * @param args
	 *            Die Eingabeparameter
	 * @return Einen {@link Scanner} der von der Eingabedatei liest.
	 */
	protected static Scanner processArgs(String[] args) {
		for (String a : args) {
			if (a.equals("-s")) {
				dontStop = true;
			} else if (a.equals("-t")) {
				test = true;
			} else if (a.equals("-d")) {
				debug = test = true;
			} else {
				fileName = a;

				break;
			}
		}

		return openInputFile();
	}

	/**
	 * Gibt die Meldung <code>msg</code> aus und beendet das Programm.
	 * 
	 * @param msg
	 *            Die Meldung die ausgegeben werden soll.
	 */
	private static void bailOut(String msg) {
		System.out.println();
		System.err.println((test ? choppedFileName + ": " : "") + "ERR " + msg);
		System.exit(1);
	}

	/**
	 * Generates a chopped String representation of the filename.
	 */
	private static void chopFileName() {
		if (fileName == null) {
			choppedFileName = "System.in";
			return;
		}

		int i = fileName.lastIndexOf(File.separatorChar);

		if (i > 0)
			i = fileName.lastIndexOf(File.separatorChar, i - 1);
		if (i == -1)
			i = 0;

		choppedFileName = ((i > 0) ? "..." : "") + fileName.substring(i);
	}

	/**
	 * Gibt eine debugging Meldung aus. Wenn das Programm mit <code>-d</code>
	 * gestartet wurde, wird <code>msg</code> zusammen mit dem Dateinamen der
	 * Inputinstanz ausgegeben, ansonsten macht diese Methode nichts.
	 * 
	 * @param msg
	 *            Text der ausgegeben werden soll.
	 */
	public static synchronized void printDebug(String msg) {
		if (!debug)
			return;

		System.out.println(choppedFileName + ": DBG " + msg);
	}

	/**
	 * Gibt eine debugging Meldung aus. Wenn das Programm mit <code>-d</code>
	 * gestartet wurde, wird <code>msg</code> zusammen mit dem Dateinamen der
	 * Inputinstanz ausgegeben, ansonsten macht diese Methode nichts.
	 * 
	 * @param msg
	 *            Object das ausgegeben werden soll.
	 */
	public static void printDebug(Object msg) {
		printDebug(msg.toString());
	}

	/**
	 * The constructor is private to hide it from JavaDoc.
	 * 
	 */
	private Main() {
	}

}
