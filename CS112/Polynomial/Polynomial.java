package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {
	
	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage format
	 * of the polynomial is:
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * with the guarantee that degrees will be in descending order. For example:
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * which represents the polynomial:
	 * <pre>
	 *      4*x^5 - 2*x^3 + 2*x + 3 
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients and
	 *         degrees read from scanner
	 */
	public static Node read(Scanner sc) 
	throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}
	
	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		Node sum = new Node(0, 0, null);
		Node ptr = sum;
		while (poly1 != null && poly2 != null) 
		{
			if (poly1.term.degree > poly2.term.degree)
			{
				ptr.next = new Node(poly2.term.coeff, poly2.term.degree, null);
				poly2 = poly2.next;
				ptr = ptr.next;
			}
			else if (poly1.term.degree < poly2.term.degree)
			{
				ptr.next = new Node(poly1.term.coeff, poly1.term.degree, null);
				poly1 = poly1.next;
				ptr = ptr.next;
			}
			else if (poly1.term.degree == poly2.term.degree)
			{
				ptr.next = new Node(poly1.term.coeff + poly2.term.coeff, poly1.term.degree, null);
				poly1 = poly1.next;
				poly2 = poly2.next;
				ptr = ptr.next;
			}
		}
		//If poly1 is empty, print rest of poly2
		if (poly1 == null)
		{
			while (poly2 != null)
			{
				ptr.next = new Node(poly2.term.coeff, poly2.term.degree, null);
				poly2 = poly2.next;
				ptr = ptr.next;
			}
		}
		//If poly2 is empty, print rest of poly1
		if (poly2 == null)
		{
			while (poly1 != null)
			{
				ptr.next = new Node(poly1.term.coeff, poly1.term.degree, null);
				poly1 = poly1.next;
				ptr = ptr.next;
			}
		}
		return sum.next;

	}
	
	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input polynomials.
	 * The returned polynomial MUST have all new nodes. In other words, none of the nodes
	 * of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the returned node
	 *         is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		/** COMPLETE THIS METHOD **/
		Node tempProduct = new Node(0, 0, null);
		Node ptr = tempProduct;
		Node temp;
		Node product = new Node(1, -1, null);
		Node ptrp2 = poly2;
		
		//Getting all terms
		while (poly1 != null)
		{
			while (ptrp2 != null)
			{
				ptr.next = new Node((poly1.term.coeff * ptrp2.term.coeff), poly1.term.degree + ptrp2.term.degree, null);
				ptrp2 = ptrp2.next;
				ptr = ptr.next;
			}
			ptrp2 = poly2;
			poly1 = poly1.next;
		}
		//Adding like terms
		ptr = tempProduct;
		while (ptr != null)
		{
			temp = ptr;
			ptr = ptr.next;
			temp.next = null;
			product = add(product, temp);
		}
		return product.next;
	}
		
	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {
		/** COMPLETE THIS METHOD **/
		float total = 0;
		while (poly != null)
		{
			total += poly.term.coeff * (Math.pow(x, poly.term.degree));
			poly = poly.next;
		}
		return total;
	}
	
	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		} 
		
		String retval = poly.term.toString();
		for (Node current = poly.next ; current != null ;
		current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}	
}
