package scripts.mining;

import java.text.DecimalFormat;

import com.runemate.game.api.hybrid.util.calculations.Random;
import com.runemate.game.api.script.Execution;

public class ReflexAgent {
	
	public static int resets = 0;
	private static double[] coeffiecients;
	private static long start;
	private static int seed = 210;
	
	public static void reinitialize(){
		reinitialize(seed);
	}
	public static void reinitialize(int seed){
		resets++;
		ReflexAgent.seed = seed;
		start = System.currentTimeMillis();
		
		int[] points = new int[14];

		points[0] = seed;
		for (int i = 1; i < points.length; i++) {
			int y = Random.nextInt(-20,45) + points[i-1];
			points[i] = y > 400 ? 400 : (y < 100 ? 100 : y);
		}
		
		Matrix a = new Matrix(points.length, points.length);
		Matrix b = new Matrix(points.length, 1);
		
		for (int row = 0; row < points.length; row++) {
			for (int col = 0; col < points.length; col++) {
				a.element[row][col] = Math.pow(row, col);
			}
			b.element[row][0] = points[row];
		}
		
		Matrix x = Matrix.divide(b, a);
		coeffiecients  = new double[points.length]; 

		for (int i = 0; i < points.length; i++) {
			coeffiecients[i] = x.element[i][0];
		}
	}
	
	public static void initialize(int seed){
		reinitialize(seed);
		resets = 0;
	}
		
	public static void delay() {
		if(seed != -1){
			Execution.delay(getReactionTime());
		}
		
	}
	
	public static int getReactionTime() {
		if(seed != -1){
			int value = applyPolynomial((System.currentTimeMillis() - start) / 3600000.0);
			return value >= 0 ? value : -1;
		}else{
			return Random.nextInt(100,400);
		}
	}
	
	public static int applyPolynomial(double x){
		double delay = 0;
		
		for (int i = 0; i < coeffiecients.length; i++) {
			delay += coeffiecients[i] * Math.pow(x+3, i);
		}
		
		return (int)delay;
	}
	
	private static class Matrix{
		public int rows, columns;
		public double[][] element;	// the array containing the matrix

		public Matrix(int r, int c) {
			//  creates an empty r by c matrix
			rows = r;
			columns = c;
			element = new double[rows][columns];
		}

		public Matrix(double d) {
			//  creates a 1x1 matrix of double d
			rows = 1;
			columns = 1;
			element = new double[1][1];
			element[0][0] = d;
		}

		public Matrix(Matrix m) {
			//  creates a new replicate of m
			rows = m.rows;
			columns = m.columns;
			element = new double[rows][columns];
			int i, j;
			for (i=0; i<rows; i++) {
				for (j=0; j<columns; j++) {
					element[i][j] = m.element[i][j];
				}
			}
		}

		public Matrix(int r, int c, char code) {
			// contsructor: creates an  r by c special matrix
			rows = r;
			columns = c;
			element = new double[rows][columns];
			int i, j;
			if ((code == 'i') || (code == 'I')){
				// make an identity matrix
				for (i = 0; i < r; i++) {
					if (i < c) {
						element[i][i] = 1;
					}
				}
			}
			else if ((code == 'h') || (code == 'H')){
				// make a Hilbert matrix
				for (i = 0; i < r; i++) {
					for (j=0; j<c; j++) {
						element[i][j] = 1/((double)i+(double)j+1);
					}
				}
			}
			else if ((code == 'r') || (code == 'R')){
				// make a random matrix with entries uniform in [0, 1]
				for (i = 0; i < r; i++) {
					for (j=0; j<c; j++) {
						element[i][j] = Math.random();
					}
				}
			}
		}

		public Matrix transpose() {
			// returns the transpose of this matrix object
			Matrix t = new Matrix(columns, rows);
			int i, j;
			for (i = 0; i<rows; i++) {
				for (j = 0; j<columns; j++) {
					t.element[j][i] = this.element[i][j];
				}
			}
			return t;
		}

		public static Matrix add(Matrix m1, Matrix m2){
			// Return the matrix m = m1 + m2
			Matrix m=new Matrix(m1.rows,m1.columns);
			if ((m1.rows == m2.rows)&&(m1.columns==m2.columns)) {
				int i,j;
				for (i=0; i<m.rows; i++) {
					for (j=0; j<m.columns; j++) {
						m.element[i][j] = m1.element[i][j] + m2.element[i][j]; 
					}
				}
			}
			return m;
		}

		public static Matrix multiply(double d, Matrix m1){
			// Return the matrix m = d*m1
			Matrix m=new Matrix(m1.rows,m1.columns);
			int i,j;
			for (i=0; i<m.rows; i++) {
				for (j=0; j<m.columns; j++) {
					m.element[i][j] = d * m1.element[i][j];
				}
			}
			return m;
		}

		public static Matrix multiply(Matrix m1, Matrix m2){
			/* Matrix-Matrix or Matrix-vector product
			       returns m=m1*m2
			       m1 can be a 1x1 Matrix for scalar-Matrix product
			 */
			Matrix m = new Matrix(0);
			if (m1.columns == m2.rows) {
				// matrix product
				double sum = 0;
				int k = 0;
				m = new Matrix(m1.rows,m2.columns);
				int i,j;
				for (i=0; i<m.rows; i++) {
					for (k=0; k<m2.columns; k++) {
						for (j=0; j<m1.columns; j++) {
							sum = sum + m1.element[i][j] * m2.element[j][k];
						}
						m.element[i][k] = sum;
						sum = 0;
					}
				}
			}
			else if ((m1.columns == 1)&&(m1.rows == 1)) {
				// scalar-vector product
				m = new Matrix(m2.rows,m2.columns);
				int i,j;
				for (i=0; i<m.rows; i++) {
					for (j=0; j<m.columns; j++) {
						m.element[i][j] = m1.element[0][0] * m2.element[i][j];
					}
				}
			}
			return m;
		}

		public static Matrix divide(Matrix m1, Matrix m2) {
			/* Returns m1/m2. If m2 is a 1x1 matrix, then this is
			       just matrix/scalar. If m2 is a square, invertible
			       matrix and m1 is a vector (a matrix with one column), 
			       divide returns inverse(m2)*m1, using the
			       Householder QR algorithm.
			 */
			Matrix m = new Matrix(0);
			if ((m2.columns == 1)&&(m2.rows == 1)) {
				// vector-scalar division
				m=new Matrix(m1.rows,m1.columns);
				int i,j;
				for (i=0; i<m.rows; i++) {
					for (j=0; j<m.columns; j++) {
						m.element[i][j] = m1.element[i][j] / m2.element[0][0];
					}
				}
			}
			else if ((m2.columns == m2.rows)&&
					(m1.columns == 1)&&(m1.rows == m2.rows)) {
				/* Solve a general, dense, non-singular linear 
				 system Ax=b via QR, where A=m2, b=m1, and x is returned. */
				m=new Matrix(m2.rows,1);
				Matrix Q=m2.Q();
				Matrix R=m2.R();
				Matrix b=multiply(Q.transpose(),m1);
				int i,j;
				double sum = 0;
				m.element[m.rows-1][0] =
						b.element[m.rows-1][0]/R.element[m.rows-1][m.rows-1];
				i=m.rows-1;
				while(i >= 0) {
					sum = 0;
					j = m.rows-1;
					while(j>=i+1) {
						sum = sum + R.element[i][j]*m.element[j][0];
						j--;
					}
					m.element[i][0] = (b.element[i][0]-sum)/R.element[i][i];
					i--;
				}
			}
			return m;
		}

		public Matrix sub(int r1, int r2, int c1, int c2) {
			// returns the submatrix (r1:r2,c1:c2) (Moeler notation)
			// requires r2>=r1, c2>=c1
			Matrix A = new Matrix(r2 - r1 + 1, c2 - c1 + 1);
			int i, j;
			for (i = r1; i<=r2; i++) {
				for (j = c1; j<=c2; j++) {
					A.element[i - r1][j - c1] = this.element[i][j];
				}
			}
			return A;
		}

		public double norm() {
			/* returns the Frobenius norm (Matrix), or Euclidean norm (Vector)
			       This is the default norm for a Matrix object. Use the Norm
			       class for different norms.
			 */
			double l = 0;
			int i, j;
			for (i = 0; i<rows; i++) {
				for (j = 0; j<columns; j++) {
					l = l + this.element[i][j] * this.element[i][j];
				}
			}
			l = Math.pow(l, 0.5);
			return l;
		}

		public Matrix  Q() {
			/*	returns the 'Q' in the QR-decomposition of this matrix object
				using Householder reflections, without column pivoting
			 */
			Matrix P = new Matrix(rows, rows, 'I');
			Matrix A = new Matrix(this);
			Matrix AA, PP;
			int i, j;
			Matrix v;

			for(j = 0; j<columns; j++) {
				v = A.sub(0,A.rows-1, j, j);
				if (j>0) {
					for (i = 0; i<j; i++) {
						v.element[i][0] = 0;
					}
				}
				v.element[j][0] = v.element[j][0] + v.norm() * sign(v.element[j][0]);
				double r = (double) -2 / (v.norm() * v.norm() );
				AA = new Matrix(A);
				A = multiply(v.transpose(),A);
				A = multiply(v,A);
				A = multiply(r,A);
				A = add(AA,A);
				PP = new Matrix(P);
				P = multiply(v.transpose(),P);
				P = multiply(v,P);
				P = multiply(r,P);
				P = add(PP,P);
			}
			return P.transpose();
		}

		public Matrix  R() {
			/*	returns the 'R' in the QR-decomposition of this matrix object
				using Householder reflections, without column pivoting
			 */
			Matrix P = new Matrix(rows, rows, 'I');
			Matrix A = new Matrix(this);
			Matrix AA, PP;
			int i, j;
			Matrix v;

			for(j = 0; j<columns; j++) {
				v = A.sub(0,A.rows-1, j, j);
				if (j>0) {
					for (i = 0; i<j; i++) {
						v.element[i][0] = 0;
					}
				}
				v.element[j][0] = v.element[j][0] + v.norm() * sign(v.element[j][0]);
				double r = (double) -2 / (v.norm() * v.norm() );
				AA = new Matrix(A);
				A = multiply(v.transpose(),A);
				A = multiply(v,A);
				A = multiply(r,A);
				A = add(AA,A);
				PP = new Matrix(P);
				P = multiply(v.transpose(),P);
				P = multiply(v,P);
				P = multiply(r,P);
				P = add(PP,P);
			}
			return A;
		}

		public String toString() {
			/*Return a string representation of this matrix with 'd'
			      displayed digits*/
			String newln = System.getProperty("line.separator");  
			String outPut = new String();
			String num = new String();
			int i, j;
			for (i=0; i<this.rows; i++) {
				for (j=0; j<this.columns; j++) {
					num = new DecimalFormat("#.######").format(this.element[i][j]);
					outPut = outPut + num + (char) 9;
				}
				outPut = outPut + newln;
			}
			return outPut;
		}

		// The following methods are used internally by the Matrix class:

		double sign(double d) {
			// returns the sign of the supplied double-precision argument
			double s = 1;
			if (d<0) { s = -1; }
			return s;
		}	
	}
}
