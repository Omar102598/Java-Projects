import java.util.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class QuickSort extends JPanel{
	private static final long serialVersionUID = 1L;
	private final int WIDTH = 1000, HEIGHT = WIDTH * 9 /16;
	private final int SIZE = 200;
	private final float BAR_WIDTH = (float)WIDTH / SIZE;
	private int[] bar_height = new int[SIZE];
	private SwingWorker<Void, Void> shuffler, sorter;
	private int current_index, traversing_index;
	
	private QuickSort () {
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		initBarHeight();
		initSorter();
		initShuffle();
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(Color.CYAN);
		Rectangle2D.Double bar;
		for (int i = 0; i < SIZE; i++) {
			bar = new Rectangle2D.Double(i*BAR_WIDTH, 0, BAR_WIDTH, bar_height[i]);
			g2d.fill(bar);
		}
		
		g2d.setColor(Color.GREEN);
		bar = new Rectangle2D.Double(current_index*BAR_WIDTH, 0, BAR_WIDTH, bar_height[current_index]);
		g2d.fill(bar);
		
		g2d.setColor(Color.RED);
		bar = new Rectangle2D.Double(traversing_index*BAR_WIDTH, 0, BAR_WIDTH, bar_height[traversing_index]);
		g2d.fill(bar);
	}
	
	private void initSorter() {
		sorter = new SwingWorker<>() {
			@Override
			public Void doInBackground() throws InterruptedException{
				quickSorter(bar_height, 0, bar_height.length - 1); 
				return null;
			}
		};
		
	}
	
	
	public int partition(int arr[], int low, int high) throws InterruptedException {
		int pivot = bar_height[high];
		traversing_index = (low-1); 
		for (int current_index=low; current_index<high; current_index++) {
			if (arr[current_index] <= pivot) {
				traversing_index++;
				int temp = arr[traversing_index];
				arr[traversing_index] = arr[current_index];
				arr[current_index] = temp;
				Thread.sleep(10);
				repaint();

			}
		}
		// swap arr[i+1] and arr[high] (or pivot)
		int temp = arr[traversing_index+1];
		arr[traversing_index+1] = arr[high];
		arr[high] = temp;
		return traversing_index+1;
		}
	
	private void quickSorter( int arr[], int low, int high) throws InterruptedException {
		if (low < high) {
			int pi = partition(arr, low, high);
			quickSorter(arr, low, pi-1);
			Thread.sleep(10);
			repaint();
			quickSorter(arr, pi+1, high);
			Thread.sleep(10);
			repaint();

		}
	}
	
	private void initShuffle() {
		shuffler = new SwingWorker<>() {
			@Override
			public Void doInBackground() throws InterruptedException{
				int middle = SIZE / 2;
				for(int i = 0, j = middle; i < middle; i++, j++) {
					int random_index = new Random().nextInt(SIZE);
					swap(i, random_index);
					
					random_index = new Random().nextInt(SIZE);
					swap(j, random_index);
					Thread.sleep(10);;
					repaint();
				}
				
				return null;
			}
			
			@Override
			public void done() {
				super.done();
				sorter.execute();
			}
		};
		shuffler.execute();
	}
	
	private void initBarHeight() {
		float interval = (float)HEIGHT / SIZE;
		
		for (int i = 0; i < SIZE; i++) {
			bar_height[i] = (int) (i * interval);
		}
	}
	
	private void swap(int indexA, int indexB) {
		int temp = bar_height[indexA];
		bar_height[indexA] = bar_height[indexB];
		bar_height[indexB] = temp;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Quick Sort Visualizer");
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setContentPane(new QuickSort());
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});

	}

}


	
	
	
	
