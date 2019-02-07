package MW;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class DrawArea extends JComponent {
	private static final long serialVersionUID = 4377506826028456397L;
	int cellsMax = 635;
	int size = 1;
	int cells = cellsMax / size;
	Cell[][] tab = new Cell[cells][cells];
	Cell[][] tab1 = new Cell[cells][cells];
	SwingPaint swingpaint;
	List<Cell> colors;
	static int ID = 0;
	boolean isAlive;
	boolean periodic = true;
	int cellNumber = 0;
	int los = 0;
	int numberRecrystallized = 0;
	private Image image;
	private Graphics2D g2;

	int inclusionSize = 10;
	boolean inclusionRound = false;
	int rule4Probability = 10;

	boolean phase2 = false;
        
    int inclNum=10;

	boolean MonteCarlo = false;
	boolean[][] visited = new boolean[cells][cells];
	int MCSiteration = 0;
	int MCS = 10;

	static int[][] energyTab;
	public static int cellEnergy = 0;
	public static int borderEnergy = 7;

	int nucleons = 10;
	boolean constant = true;
	boolean increasing = false;
	boolean beginning = false;
	int nucleonsPrevious = 0; // for increasing type only
	boolean nucleonsCreated = false; // for beginning type only
	boolean mooreExtend = false;

	public DrawArea() {
		setDoubleBuffered(true);
		createTables();
		colors = new ArrayList<>();
		addMouseListener(new MouseAdapter() {

			int x;
			int y;

			@Override
			public void mousePressed(MouseEvent e) {
				if (!isAlive) {
					x = e.getX() / size;
					y = e.getY() / size;
					if (e.getButton() == 1) {
						for (int i = x - inclusionSize / 2; i < x + inclusionSize / 2; i++) {
							for (int j = y - inclusionSize / 2; j < y + inclusionSize / 2; j++) {
								if (inclusionRound && (i - x) * (i - x) + (j - y) * (j - y) < (inclusionSize / 2)
										* (inclusionSize / 2)) {
									tab[i][j] = new Cell(true, i, j);
									tab1[i][j] = tab[i][j];
								}
								if (!inclusionRound) {
									tab[i][j] = new Cell(true, i, j);
									tab1[i][j] = tab[i][j];

								}
							}
						}
					} else if (e.getButton() == 3 && phase2) {
						if (tab[x][y].ID != -2 || tab[x][y].ID != -1) {
							if (colors.get(tab[x][y].ID).phase == 0) {
								Cell cell;
								cell = (Cell) colors.get(tab[x][y].ID);
								cell.phase = 1;
								colors.set(tab[x][y].ID, cell);
							}
						}
					} else if (e.getButton() == 3 ) {
						if (tab[x][y].ID != -2 && tab[x][y].ID != -1) {
							if (colors.get(tab[x][y].ID).phase == 0) {
								Cell cell;
								cell = (Cell) colors.get(tab[x][y].ID);
								cell.phase = 3;
								cell.color = Color.MAGENTA;
								colors.set(tab[x][y].ID, cell);
							}
						}
					}
					draw();
				}
			}
		});
	}
        
            public void inclAfter(){
            try{
            Random rand= new Random();
            int p=0;
            while(p<inclNum){
                    int x = (rand.nextInt(cells));
					int y = (rand.nextInt(cells));
					if(isBorder(x, y)){
						for (int i = x - inclusionSize / 2; i < x + inclusionSize / 2; i++) {
							for (int j = y - inclusionSize / 2; j < y + inclusionSize / 2; j++) {
								if (inclusionRound && (i - x) * (i - x) + (j - y) * (j - y) < (inclusionSize / 2)
										* (inclusionSize / 2)) {
									tab[i][j] = new Cell(true, i, j);
									tab1[i][j] = tab[i][j];
								}
								if (!inclusionRound) {
									tab[i][j] = new Cell(true, i, j);
									tab1[i][j] = tab[i][j];

								}
							}
						}
					
                        p++;
                   }
            draw();
            }
        }catch(Exception ignored){}}

	@Override
	protected void paintComponent(Graphics g) {
		if (image == null) {
			image = createImage(getSize().width, getSize().height);
			g2 = (Graphics2D) image.getGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			clear();
		}
		g.drawImage(image, 0, 0, null);
	}

	public void clear() {
		createTables();
		image.flush();
		g2.setPaint(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, getSize().width, getSize().height);
		g2.setPaint(Color.BLACK);
		draw();
		repaint();
	}

	public void createTables() {
		for (int i = 0; i < cells; i++) {
			for (int j = 0; j < cells; j++) {
				tab[i][j] = new Cell();
				tab1[i][j] = new Cell();
			}
		}
	}

	private void draw() {
		for (int i = 0; i < cells; i++) {
			for (int j = 0; j < cells; j++) {
				g2.setPaint(tab[i][j].color);
				g2.fillRect(i * size, j * size, size, size);
			}
		}
		repaint();
	}

	public void clearTables() {
		for (int i = 0; i < cells; i++) {
			for (int j = 0; j < cells; j++) {
				energyTab[i][j] = 1;
				if ( tab[i][j].ID != -1 && tab[i][j].ID != -2) {
					if (colors.get(tab[i][j].ID).phase == 3) {
						continue;
					} else {
						tab[i][j].ID = -1;
						tab[i][j].color = Color.WHITE;
						tab1[i][j] = tab[i][j];
					}
				} else {
					tab[i][j].ID = -1;
					tab[i][j].color = Color.WHITE;
					tab1[i][j] = tab[i][j];
				}
			}
		}
		colors = new ArrayList<>();
		ID = 0;
		cellNumber = 0;
		phase2 = false;
		MCSiteration = 0;
		draw();
	}

	public void random(int number, int phase) {
		Random rand = new Random();
		cells = cellsMax / size;
		energyTab = new int[cells][cells];
		
		if(phase2) {
			
			int mynum = rand.nextInt(colors.size()-1);
			System.out.println("sasadasd"+colors.size());
			Color myColor = colors.get(mynum).color;
			MCSiteration = 0;
			
			for (int i = 0; i < cells; i++) {
				for (int j = 0; j < cells; j++) {
					if(tab[i][j].color==myColor) {
						tab[i][j].ID=-2;
						tab[i][j].phase=2;
					}else {
						tab[i][j].ID = -1;
						tab[i][j].color = Color.WHITE;
						
					}
				}
				}
			ID=0;
			cellNumber = 0;
			colors = new ArrayList<>();
		}
		
		if (MonteCarlo) {
			if (phase2)
				MCSiteration = 0;
			for (int i = 0; i < number; i++) {
				colors.add(new Cell(ID++, 0, 0));
				cellNumber++;
			}
			for (int i = 0; i < cells; i++) {
				for (int j = 0; j < cells; j++) {
					if(tab[i][j].ID == -2)
						continue;
					if (tab[i][j].ID == -1 || phase2 && tab[i][j].phase != 1 && tab[i][j].ID != 2) {
						int ID = rand.nextInt(cellNumber);
						if (colors.get(ID).phase == 1)
							continue;
						tab[i][j] = colors.get(ID);
						tab1[i][j] = tab[i][j];
					}
				}
			}
		} else {
			for (int i = 0; i < number; i++) {
				int x = rand.nextInt(cells);
				int y = rand.nextInt(cells);
				if(tab[x][y].ID == -2)
					continue;
				if (tab[x][y].ID == -1 || phase2 && tab[x][y].phase != 1 && tab[x][y].ID != 2) {
					tab[x][y] = new Cell(ID++, x, y);
					tab1[x][y] = tab[x][y];
					colors.add(tab[x][y]);
					cellNumber++;
					Cell cell;
					cell = (Cell) colors.get(tab[x][y].ID);
					cell.phase = phase;
					colors.set(tab[x][y].ID, cell);
				}
			}
		}

		draw();
	}

	public void gameOfLife() {
		Random rand = new Random();
		while (isAlive) {
			int visitedNumber = 0;
			draw();
			if (MonteCarlo && MCSiteration < MCS) {
				while (visitedNumber != cells * cells) {
					int x = rand.nextInt(cells);
					int y = rand.nextInt(cells);
					if (visited[x][y]) {
						continue;
					} else {
						visited[x][y] = true;
						visitedNumber++;
						List<Cell> Neighbours;
						Neighbours = new ArrayList<>();
						for (int i = x - 1; i <= x + 1; i++) {
							for (int j = y - 1; j <= y + 1; j++) {
								
								if (i == x && j == y) {
									continue;
								}
								int tempX = i;
								int tempY = j;
								if (periodic) {
									if (i < 0) {
										tempX = cells - 1;
									}
									if (j < 0) {
										tempY = cells - 1;
									}
									if (i == cells) {
										tempX = 0;
									}
									if (j == cells) {
										tempY = 0;
									}
								} else {
									if (i < 0) {
										continue;
									}
									if (j < 0) {
										continue;
									}
									if (i == cells) {
										continue;
									}
									if (j == cells) {
										continue;
									}
								}
								Neighbours.add(tab[tempX][tempY]);
							}
						}
						int energyBefore = energyCount(Neighbours, tab[x][y].ID);
						Random randomID = new Random();
						int newNeighbour = randomID.nextInt(Neighbours.size());
						int newID = Neighbours.get(newNeighbour).ID;
						int energyAfter = energyCount(Neighbours, newID);
						if (energyAfter >= energyBefore) {
							if (tab[x][y].ID == -2 || Neighbours.get(newNeighbour).ID == -2)
								continue;
							if ( (tab[x][y].phase == 3 || Neighbours.get(newNeighbour).phase == 3))
								continue;
							if (phase2 && (tab[x][y].phase == 1 || Neighbours.get(newNeighbour).phase == 1))
								continue;
							tab[x][y] = Neighbours.get(newNeighbour);
							tab1[x][y] = tab[x][y];
						}
					}
				}
				for (int i = 0; i < cells; i++) {
					for (int j = 0; j < cells; j++) {
						visited[i][j] = false;
					}
				}
				System.out.println("Iteration: " + ++MCSiteration);
			} else {
				for (int i = 0; i < cells; i++) {
					for (int j = 0; j < cells; j++) {
						if ( tab[i][j].phase == 3)
							continue;
						if (!phase2) {
							if (tab[i][j].ID != -1 && tab[i][j].ID != -2) {
								checkNeighbourhood(i, j);
							}
						} else if (phase2 && tab[i][j].phase == 2 && tab[i][j].ID != -2) {
							checkNeighbourhood(i, j);
						}
					}
				}
				
				
				if(isDone()) {
					isAlive=false;
					break;
				}
			}
			if(MonteCarlo && MCSiteration == MCS) {
				MCSiteration=0;
				isAlive=false;
				break;
			}
			
			for (int i = 0; i < cells; i++) {
				for (int j = 0; j < cells; j++) {
					tab[i][j] = tab1[i][j];
				}
			}
			distributeEnergy();
			
		}
	}

	public boolean isDone() {
		for (int i = 0; i < cells; i++) {
			for (int j = 0; j < cells; j++) {
				if(tab[i][j].color==Color.WHITE)
					return false;
			}
		}
		return true;
	}
	
	public int energyCount(List<Cell> Neighbors, int ID) {
		int energy = 0;
		for (Iterator<Cell> iterator = Neighbors.iterator(); iterator.hasNext();) {
			Cell next = iterator.next();
			if (next.ID == ID) {
				energy++;
			}
		}
		return energy;
	}

	// checks the neighbourhood of previosuly found cell.
	// in phase 1 - if surrounding space is empty (ID = -1), check this space for
	// its neighbours, to decide
	// what ID will this cell have.
	// in phase 2 - check if cell doesn't have phase (either 1 or 2)
	public void checkNeighbourhood(int x, int y) {
		Random rand = new Random();
		los = rand.nextInt(2);
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (i == x && j == y) {
					continue;
				}
				int tempX = i;
				int tempY = j;
				if (periodic) {
					if (i < 0) {
						tempX = cells - 1;
					}
					if (j < 0) {
						tempY = cells - 1;
					}
					if (i == cells) {
						tempX = 0;
					}
					if (j == cells) {
						tempY = 0;
					}
				} else {
					if (i < 0) {
						continue;
					}
					if (j < 0) {
						continue;
					}
					if (i == cells) {
						continue;
					}
					if (j == cells) {
						continue;
					}
				}
				if (tab[tempX][tempY].ID == -1
						|| (phase2 && tab[tempX][tempY].phase == 0 && tab[tempX][tempY].ID != -2)) {
					int tmp;
					if(mooreExtend) {
						 tmp = countNeighboursExtend(tempX, tempY);
					} else {
						 tmp = countNeighbours(tempX, tempY);
					}
					if (tmp != -1) {
						tab1[tempX][tempY] = (Cell) colors.get(tmp);
					}
				}
			}
		}
	}

	public int countNeighboursExtend(int x, int y) { //extendeed moore
		int result = -1;
		int temp[] = new int[cellNumber];
		int tempRule2[] = new int[cellNumber];
		int tempRule3[] = new int[cellNumber];
		for (int i = 0; i < cellNumber; i++) {
			temp[i] = 0;
			tempRule2[i] = 0;
			tempRule3[i] = 0;
		}
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (i == x && j == y) {
					continue;
				}
				int tempX = i;
				int tempY = j;
				if (periodic) {
					if (i < 0) {
						tempX = cells - 1;
					}
					if (j < 0) {
						tempY = cells - 1;
					}
					if (i == cells) {
						tempX = 0;
					}
					if (j == cells) {
						tempY = 0;
					}
				} else {
					if (i < 0) {
						continue;
					}
					if (j < 0) {
						continue;
					}
					if (i == cells) {
						continue;
					}
					if (j == cells) {
						continue;
					}
				}
				if (tab[tempX][tempY].ID == -2 || (phase2 && tab[tempX][tempY].phase != 2)) {
					continue;
				}
				if (tab[tempX][tempY].ID != -1 || (phase2 && tab[tempX][tempY].phase == 2)) {
					temp[tab[tempX][tempY].ID]++;
					if (tempX == x) {
						tempRule2[tab[tempX][tempY].ID]++;
					}

					if (tempY == y) {
						tempRule2[tab[tempX][tempY].ID]++;
					}
					if (tempX == (x - 1)) {
						if ((tempY == (y - 1)) || (tempY == (y + 1))) {
							tempRule3[tab[tempX][tempY].ID]++;
						}
					}
					if (tempX == (x + 1)) {
						if ((tempY == (y - 1)) || (tempY == (y + 1))) {
							tempRule3[tab[tempX][tempY].ID]++;
						}
					}
				}
			}
		}
		int tempResult = Rule1(temp);
		if (tempResult != -1) {
			result = tempResult;
		} else {
			tempResult = Rule2(tempRule2);
			if (tempResult != -1) {
				result = tempResult;
			} else {
				tempResult = Rule3(tempRule3);
				if (tempResult != -1) {
					result = tempResult;
				} else {
					Random chance = new Random();
					int probability = chance.nextInt(100);
					if (probability <= rule4Probability) {
						int max = temp[0];
						for (int k = 0; k < cellNumber; k++) {
							if (temp[k] >= max) {
								max = temp[k];
								result = k;
							}
						}
						int ileMax = 0;
						for (int k = 0; k < cellNumber; k++) {
							if (temp[k] == max) {
								ileMax++;
							}
						}
						if (ileMax != 1) {
							int temp1[] = new int[ileMax];
							int iter = 0;
							for (int k = 0; k < cellNumber; k++) {
								if (temp[k] == max) {
									temp1[iter++] = k;
								}
							}
							Random rand = new Random();
							result = temp1[rand.nextInt(ileMax)];
						}
					}
				}
			}
		}
		return result;
	}

	private int Rule1(int[] temp) {
		for (int k = 0; k < cellNumber; k++) {
			if (temp[k] > 4) {
				return k;
			}
		}
		return -1;
	}

	private int Rule2(int[] tempRule2) {
		for (int k = 0; k < cellNumber; k++) {
			if (tempRule2[k] > 2) {
				return k;
			}
		}
		return -1;
	}

	private int Rule3(int[] tempRule3) {
		for (int k = 0; k < cellNumber; k++) {
			if (tempRule3[k] > 2) {
				return k;
			}
		}
		return -1;
	}

	
	public int countNeighbours(int x, int y) {//normal moore
		int result = -1;
		int temp[] = new int[cellNumber];
		
		for (int i = 0; i < cellNumber; i++) {
			temp[i] = 0;
		}
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (i == x && j == y) {
					continue;
				}
				int tempX = i;
				int tempY = j;
				if (periodic) {
					if (i < 0) {
						tempX = cells - 1;
					}
					if (j < 0) {
						tempY = cells - 1;
					}
					if (i == cells) {
						tempX = 0;
					}
					if (j == cells) {
						tempY = 0;
					}
				} else {
					if (i < 0) {
						continue;
					}
					if (j < 0) {
						continue;
					}
					if (i == cells) {
						continue;
					}
					if (j == cells) {
						continue;
					}
				}
				if (tab[tempX][tempY].ID == -2 || (phase2 && tab[tempX][tempY].phase != 2)) {
					continue;
				}
				if (tab[tempX][tempY].ID != -1 || (phase2 && tab[tempX][tempY].phase == 2)) {
					temp[tab[tempX][tempY].ID]++;
					
				}
			}
		}
		
						int max = temp[0];
						for (int k = 0; k < cellNumber; k++) {
							if (temp[k] >= max) {
								max = temp[k];
								result = k;
							}
						}
						int ileMax = 0;
						for (int k = 0; k < cellNumber; k++) {
							if (temp[k] == max) {
								ileMax++;
							}
						}
						if (ileMax != 1) {
							int temp1[] = new int[ileMax];
							int iter = 0;
							for (int k = 0; k < cellNumber; k++) {
								if (temp[k] == max) {
									temp1[iter++] = k;
								}
							}
							Random rand = new Random();
							result = temp1[rand.nextInt(ileMax)];
						}
					
				
		return result;
	}
	
	public boolean isBorder(int x, int y) {
		boolean state = false;
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (i == x && j == y) {
					continue;
				}
				int tempX = i;
				int tempY = j;
				if (periodic) {
					if (i < 0) {
						tempX = cells - 1;
					}
					if (j < 0) {
						tempY = cells - 1;
					}
					if (i == cells) {
						tempX = 0;
					}
					if (j == cells) {
						tempY = 0;
					}
				} else {
					if (i < 0) {
						continue;
					}
					if (j < 0) {
						continue;
					}
					if (i == cells) {
						continue;
					}
					if (j == cells) {
						continue;
					}
				}
				if (tab[tempX][tempY].recristallized || tab[x][y].recristallized) break;
				if (tab[tempX][tempY].ID != tab[x][y].ID) {
					state = true;
				}
			}
		}
		return state;
	}

	public void borders() {
		int bordersCounter = 0;
		int grainsCounter = 0;
		double result = 0.0;
		for (int i = 0; i < cells; i++) {
			for (int j = 0; j < cells; j++) {
				if (isBorder(i, j)) {
					tab1[i][j] = new Cell(true, i, j);
					bordersCounter++;
				} else {
					grainsCounter++;
				}
			}
		}
		for (int i = 0; i < cells; i++) {
			for (int j = 0; j < cells; j++) {
				tab[i][j] = tab1[i][j];
			}
		}
		result = (double) bordersCounter / (double) (bordersCounter + grainsCounter);
		System.out.println("Borders to grains ratio: ");
		System.out.println(result);
		draw();
	}

	public void clearBorders() {
		for (int i = 0; i < cells; i++) {
			for (int j = 0; j < cells; j++) {
				if (tab[i][j].ID != -2) {
					tab1[i][j] = new Cell();
				}
			}
		}
		for (int i = 0; i < cells; i++) {
			for (int j = 0; j < cells; j++) {
				tab[i][j] = tab1[i][j];
			}
		}
		draw();
	}

	public boolean importToFile() throws FileNotFoundException {
		String filename;
		filename = JOptionPane.showInputDialog("filename:");
		PrintWriter save = new PrintWriter(filename + ".txt");
		save.println(cells + " " + size);
		for (int i = 0; i < cells; i++) {
			for (int j = 0; j < cells; j++) {
				save.println(i + " " + j + " " + tab[i][j].ID + " " + tab[i][j].color.getRed() + " "
						+ tab[i][j].color.getGreen() + " " + tab[i][j].color.getBlue());
			}
		}
		save.close();
		return true;
	}

	public boolean exportFromFile() throws FileNotFoundException {
		String filename;
		filename = JOptionPane.showInputDialog("filename:");
		createTables();
		File file = new File(filename + ".txt");
		Scanner from = new Scanner(file);
		cells = from.nextInt();
		size = from.nextInt();
		for (int i = 0; i < cells; i++) {
			for (int j = 0; j < cells; j++) {
				tab[i][j].x = from.nextInt();
				tab[i][j].y = from.nextInt();
				tab[i][j].ID = from.nextInt();
				tab[i][j].color = new Color(from.nextInt(), from.nextInt(), from.nextInt());
				tab[i][j].phase = 0;
			}
		}
		from.close();
		draw();
		return true;
	}

	public boolean importToBMP() throws IOException {
		String filename;
		filename = JOptionPane.showInputDialog("filename:");
		File bmpFile = new File(filename + ".bmp");
		BufferedImage img = (BufferedImage) image;
		RenderedImage rendim = img;
		ImageIO.write(rendim, "bmp", bmpFile);
		return true;
	}

	public boolean exportFromBMP() throws IOException {
		String filename;
		filename = JOptionPane.showInputDialog("filename:");
		File bmpFile = new File(filename + ".bmp");
		BufferedImage tmpImage = ImageIO.read(bmpFile);
		image = tmpImage;
		draw();
		return true;
	}

	public void distributeEnergy() {
		String selectedType = (String) SwingPaint.energyType.getSelectedItem();
		for (int i = 0; i < cells; i++) {
			for (int j = 0; j < cells; j++) {
				
				if(selectedType.equals("Heterogenous")) {
					if (tab[i][j].recristallized) {
						energyTab[i][j] = 0;
						continue;
					}
					else if (isBorder(i, j)) {
						energyTab[i][j] = borderEnergy;
					} else {
						energyTab[i][j] = cellEnergy;
					}
				}else {
					if (tab[i][j].recristallized) {
						energyTab[i][j] = 0;
						continue;
					}
				else {
						energyTab[i][j] = cellEnergy;
					}
				}
			}
		}
	}
	
	public void dynamicRecrystallization() {
		Random rand = new Random();
		int iteration = 0;
		while(isAlive) {
			if (iteration % MCS != 0) {
				iteration++;
				for (int i = 0; i < cells; i++) {
					for (int j = 0; j < cells; j++) {
						if (tab[i][j].recristallized) recrystallizeNeighbors(i, j);
					}
				}
				continue;
			}
			iteration = 0;
			int nucleonsTmp = nucleons;
			if (increasing) nucleonsTmp += nucleons;
			for (int i = 0; i < nucleonsTmp; i++) {
				if (beginning && nucleonsCreated) break;
				int x;
				int y;
				x = rand.nextInt(cells);
				y = rand.nextInt(cells);
				if (!tab[x][y].recristallized) {
					if (cellEnergy == borderEnergy) {
						recrystallize(x,y);
						continue;
					}
					if (!isBorder(x, y)) continue;
					else recrystallize(x,y);
				}
			}
			if (beginning) nucleonsCreated = true;
			if (increasing) nucleonsPrevious = nucleonsTmp;
			for (int i = 0; i < cells; i++) {
				for (int j = 0; j < cells; j++) {
					if (tab[i][j].recristallized) recrystallizeNeighbors(i, j);
				}
			}
			for (int i = 0; i < cells; i++) {
				for (int j = 0; j < cells; j++) {
					tab[i][j] = tab1[i][j];
				}
			}
			draw();
			distributeEnergy();
			iteration++;
		}
	}
	
	public void recrystallize(int x, int y) {
		tab1[x][y] = new Cell(ID++, true);
		tab[x][y] = tab1[x][y];
		cellNumber++;
		colors.add(tab[x][y]);
	}

	public void recrystallizeNeighbors(int x, int y) {
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (i == x && j == y) {
					continue;
				}
				int tempX = i;
				int tempY = j;
				if (periodic) {
					if (i < 0) {
						tempX = cells - 1;
					}
					if (j < 0) {
						tempY = cells - 1;
					}
					if (i == cells) {
						tempX = 0;
					}
					if (j == cells) {
						tempY = 0;
					}
				} else {
					if (i < 0) {
						continue;
					}
					if (j < 0) {
						continue;
					}
					if (i == cells) {
						continue;
					}
					if (j == cells) {
						continue;
					}
				}
				if (!tab1[tempX][tempY].recristallized) {
					tab1[tempX][tempY] = tab[x][y];
				}
			}
		}
	}
	
	int countEnergy(int x, int y) {
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				if (i == x && j == y) {
					continue;
				}
				int tempX = i;
				int tempY = j;
				if (periodic) {
					if (i < 0) {
						tempX = cells - 1;
					}
					if (j < 0) {
						tempY = cells - 1;
					}
					if (i == cells) {
						tempX = 0;
					}
					if (j == cells) {
						tempY = 0;
					}
				} else {
					if (i < 0) {
						continue;
					}
					if (j < 0) {
						continue;
					}
					if (i == cells) {
						continue;
					}
					if (j == cells) {
						continue;
					}
				}
				if (!tab1[tempX][tempY].recristallized) {
					tab1[tempX][tempY] = tab[x][y];
				}
			}
		}
		return 1;
	}
	
	public class Cell {

		int ID;
		Color color;
		public int x;
		public int y;
		public boolean recristallized = false;
		public int phase = 0;

		public Cell(int id, int x, int y) {
			Random rand = new Random();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			this.ID = id;
			this.x = x;
			this.y = y;
			color = new Color(r, g, b);
		}

		public Cell(int id, boolean recristallized) {
			Random rand = new Random();
			float r = rand.nextFloat();
			this.ID = id;
			color = new Color(r, 0, 0);
			this.recristallized = recristallized;
		}

		public Cell() {
			this.ID = -1;
			color = Color.WHITE;
		}

		public Cell(boolean isInclusion, int x, int y) { // for creating inclusions
			this.ID = -2;
			color = Color.BLACK;
			this.x = x;
			this.y = y;
		}
	}

}
