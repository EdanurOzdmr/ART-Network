package art;

public class ART_Example {

	private static final int n = 3; // f1 katmanındaki işlem elemanı sayısı
	private static final int m = 3; // f2 katmanındaki işlem elemanı sayısı
	private static final double SIMILARITY = 0.8;
	private static final int PATTERNS = 3; // Makine
	private static final int TRAINING_PATTERNS = 3; // Parça
	private static int pattern[][] = null;

	private static double fw[][] = null; // İleriye doğru ağırlık.
	private static double bw[][] = null; // Geriye doğru ağırlık.

	private static int f1a[] = null;
	private static int f1b[] = null;
	private static double f2[] = null;

	private static void initialize() {
		pattern = new int[][] { { 1, 0, 1 }, { 1, 1, 0 }, { 0, 0, 1 } };

		// İleriye doğru ağırlıkların başlangıç değerleri
		System.out.println("Initial weight values");
		fw = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				fw[i][j] = 1.0 / (1.0 + n);
				System.out.print(fw[i][j] + ", ");
			}
			System.out.print("\n");
		}

		System.out.println();

		// Geriye doğru ağırlıkların başlangıç matrisi.
		bw = new double[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				bw[i][j] = 1.0;
				System.out.print(bw[i][j] + ", ");
			}
			System.out.print("\n");
		}
		System.out.println();

		f1a = new int[n];
		f1b = new int[n];
		f2 = new double[m];

		return;
	}

	private static void ART1() {
		int inputSum = 0;
		int activationSum = 0;
		int f2Max = 0;
		boolean reset = true;

		System.out.println("Begin ART:\n");
		for (int vecNum = 0; vecNum < PATTERNS; vecNum++) {
			System.out.println("Vector: " + vecNum + "\n");

			// F2 katmanı aktivasyonlarını 0.0 olarak başlatma
			for (int i = 0; i < m; i++) {
				f2[i] = 0.0;
			}

			// Girdi setinden örneğin ağa gösterilmesi
			for (int i = 0; i < n; i++) {
				f1a[i] = pattern[vecNum][i];
			}

			// Girdi örüntüsü toplamını hesaplama.
			inputSum = vectorSum(f1a);
			System.out.println("InputSum (s1) = " + inputSum + "\n");

			// F1 katmanındaki her düğüm için aktivasyonları hesaplama.

			for (int i = 0; i < n; i++) {
				f1b[i] = f1a[i];
			}

			// F2 katmanındaki her düğüm için net girişi hesaplama.
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					f2[i] += fw[i][j] * (double) f1a[j];
				}
				System.out.print(String.format("%.1f", f2[i]));
				System.out.println();
			}
			System.out.println();

			reset = true;
			while (reset == true) {
				// F2 düğümlerinin en büyük değerini belirleme.
				f2Max = maximum(f2);

				// s2
				for (int i = 0; i < n; i++) {
					System.out.println(f1b[i] + " * " + String.format("%.1f", bw[f2Max][i]) + " = "
							+ String.format("%.1f", f1b[i] * bw[f2Max][i]));
					f1b[i] = f1a[i] * (int) (bw[f2Max][i]);

				}

				// Girdi modelinin toplamını hesapla.
				activationSum = vectorSum(f1b);
				System.out.println("ActivationSum (S(2)) = " + activationSum + "\n");

				reset = conformityTest(activationSum, inputSum, f2Max);

			}

			if (vecNum < TRAINING_PATTERNS) {
				updateWeights(activationSum, f2Max);
			}

			System.out.println("Vector " + vecNum + " belongs to cluster " + f2Max + "\n");

		} 
		return;
	}

	private static int vectorSum(int[] nodeArray) {
		int sum = 0;

		// Girdi örüntüsü toplamını hesaplama.
		for (int i = 0; i < n; i++) {
			sum += nodeArray[i];
		}

		return sum;
	}

	private static void updateWeights(int activationSum, int f2Max) {
		// İleriye doğru ağırlıkları güncelleme.
		for (int i = 0; i < n; i++) {
			fw[f2Max][i] = ((double) f1b[i]) / (0.5 + (double) activationSum);
		}

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(String.format("%.1f", fw[i][j]) + ", ");
			}
			System.out.println();
		}
		System.out.println();

		// Geriye doğru ağırlıkları güncelleme.
		for (int i = 0; i < n; i++) {
			bw[f2Max][i] = f1b[i];
		}

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(String.format("%.1f", bw[i][j]) + ", ");
			}
			System.out.println();
		}
		System.out.println();

		return;
	}

	// Uygunluk testi
	private static boolean conformityTest(int activationSum, int inputSum, int f2Max) {
		if ((double) activationSum / (double) inputSum >= SIMILARITY) {
			return false;
		} else {
			f2[f2Max] = -1.0;
			return true;
		}
	}

	// Kazanan elemanın seçilmesi
	private static int maximum(double[] nodeArray) {
		int winner = 0;
		boolean foundNewWinner = false;
		boolean done = false;

		while (!done) {
			foundNewWinner = false;
			for (int i = 0; i < m; i++) {
				if (i != winner) {
					if (nodeArray[i] > nodeArray[winner]) {
						winner = i;
						foundNewWinner = true;
					}
				}
			}

			if (foundNewWinner == false) {
				done = true;
			}
		}
		return winner;
	}

	private static void printResults() {
		System.out.println("Final weight values:");
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(String.format("%.1f", fw[i][j]) + ", ");
			}
			System.out.print("\n");
		}
		System.out.println();

		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(String.format("%.1f", bw[i][j]) + ", ");
			}
			System.out.print("\n");
		}

		return;
	}

	public static void main(String[] args) {
		initialize();
		ART1();
		printResults();
		return;
	}

}
