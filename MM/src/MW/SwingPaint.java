package MW;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class SwingPaint {

	JButton randBtn, startBtn, stopBtn, recrystBtn, clearBtn, bordersBtn, clearBordersBtn, inclBtn;
	JTextField inclField, cellField, cellSizeField, inclusionSizeField, probabilityField, MCSField, nucleonsField, energyField;
	JLabel inclLabel, cellLabel, cellSizeLabel, inclusionSizeLabel, inclusionRoundLabel, probabilityLabel, MCSLabel, nucleonsLabel;
	JCheckBox periodicBox, inclusionRoundBox, phase2Box, mooreExtendBox;
	public static JComboBox<String> energyList, energyType;
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem menuItem;
	JList<String> typeList, nucleonTypeList;
	String[] types = {"Cellular Automata", "MonteCarlo"};
	String[] nucleonTypes = {"Constant", "Increasing", "Beginning"};
	DrawArea drawArea;
	EnergyDistribution energyDistribution;
	boolean energyDistributed = false;

	ActionListener actionListener = new ActionListener() {
		Thread thread = new Thread();

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == clearBtn) {
				drawArea.clearTables();
			}
			if (e.getSource() == randBtn) {
				if (Integer.parseInt(cellSizeField.getText()) > 9) {
					cellSizeField.setText(Integer.toString(9));
				}
				drawArea.size = Integer.parseInt(cellSizeField.getText());
				if (drawArea.phase2) {
					drawArea.random(Integer.parseInt(cellField.getText()), 2); //todo lub tu
				} else {
					drawArea.random(Integer.parseInt(cellField.getText()), 0);//todo moze tu byc cos zle
				}
			}
			if (e.getSource() == startBtn) {
				drawArea.isAlive = true;
				if (thread.isAlive()) {
					try {
						thread.join();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				thread = new Thread() {
					@Override
					public void run() {
						drawArea.gameOfLife();
					}
				};
				thread.start();
			}

			if (e.getSource() == stopBtn) {
				drawArea.isAlive = false;
				try {
					thread.join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
			if (e.getSource() == periodicBox) {
				drawArea.periodic = periodicBox.isSelected();
			}
			if (e.getSource() == mooreExtendBox) {
				drawArea.mooreExtend = mooreExtendBox.isSelected();
			}
			if (e.getSource() == inclusionRoundBox) {
				drawArea.inclusionRound = inclusionRoundBox.isSelected();
			}
			if (e.getSource() == phase2Box) {
				drawArea.phase2 = phase2Box.isSelected();
			}
			if (e.getSource() == bordersBtn) {
				drawArea.borders();
			}
			if (e.getSource() == clearBordersBtn) {
				drawArea.clearBorders();
			}
                        if(e.getSource() == inclBtn){
                            drawArea.inclNum=Integer.parseInt(inclField.getText());
                            drawArea.inclAfter();
                        }
			 if (e.getSource() == recrystBtn) {
	                drawArea.isAlive = true;
	                if (thread.isAlive()) {
	                    try {
							thread.join();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	                }
	                thread = new Thread() {
	                    @Override
	                    public void run() {
	                        drawArea.dynamicRecrystallization();
	                    }
	                };
	                thread.start();
	            }
		}
	};

	public void show() {
		JFrame frame = new JFrame("Simple Grain Growth");
		Container content = frame.getContentPane();
		content.setLayout(new BorderLayout());
		drawArea = new DrawArea();

		content.add(drawArea, BorderLayout.CENTER);

		JPanel controls = new JPanel();

		cellLabel = new JLabel("Cells:");
		cellField = new JTextField("150");
		cellSizeLabel = new JLabel("Cell Size:");
		cellSizeField = new JTextField("1");
                inclLabel = new JLabel("Inclusions:");
                inclField = new JTextField("5");
		clearBtn = new JButton("Clear");
		clearBtn.addActionListener(actionListener);
                inclBtn = new JButton("Inclusions After");
                inclBtn.addActionListener(actionListener);
		randBtn = new JButton("Random");
		randBtn.addActionListener(actionListener);
		startBtn = new JButton("Start");
		startBtn.addActionListener(actionListener);
		stopBtn = new JButton("Stop");
		stopBtn.addActionListener(actionListener);
		recrystBtn = new JButton("Recrystallization");
		recrystBtn.addActionListener(actionListener);
		periodicBox = new JCheckBox("Periodic", true);
		periodicBox.addActionListener(actionListener);
		
		mooreExtendBox = new JCheckBox("Extended Moore", false);
		mooreExtendBox.addActionListener(actionListener);
		
		inclusionSizeLabel = new JLabel("Inc size:");
		inclusionSizeField = new JTextField("10");
		inclusionRoundBox = new JCheckBox("Inc Round", false);
		inclusionRoundBox.addActionListener(actionListener);
		probabilityLabel = new JLabel("Probability:");
		probabilityField = new JTextField("10");
		phase2Box = new JCheckBox("Phase 2", false);
		phase2Box.addActionListener(actionListener);
		bordersBtn = new JButton("Borders");
		bordersBtn.addActionListener(actionListener);
		clearBordersBtn = new JButton("clear Borders");
		clearBordersBtn.addActionListener(actionListener);
		MCSLabel = new JLabel("MCS:");
		MCSField = new JTextField("10");
		nucleonsLabel = new JLabel("Nucleons:");
		nucleonsField = new JTextField("10");
		energyField = new JTextField("7");
		
		energyList = new JComboBox<String>();
		energyList.addItem("Cell Energy");
		energyList.addItem("Border Energy");
		energyList.addActionListener(actionListener);
		
		energyType = new JComboBox<String>();
		energyType.addItem("Heterogenous");
		energyType.addItem("Homogenous");
		energyType.addActionListener(actionListener);

		inclusionSizeField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateInclusionSize();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateInclusionSize();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
			}

			void updateInclusionSize() {
				drawArea.inclusionSize = Integer.parseInt(inclusionSizeField.getText());
			}
		});
                
                inclField.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                    public void updateInclField(){
                    drawArea.inclNum= Integer.parseInt(inclField.getText());
                    }
                });

		cellSizeField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateCellSize();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateCellSize();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
			}

			void updateCellSize() {
				drawArea.size = Integer.parseInt(cellSizeField.getText());
			}
		});

		probabilityField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateProbability();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateProbability();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
			}

			void updateProbability() {
				drawArea.rule4Probability = Integer.parseInt(probabilityField.getText());
			}
		});
		
		MCSField.getDocument().addDocumentListener(new DocumentListener() {

			void updateMCS() {
				drawArea.MCS = Integer.parseInt(MCSField.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateMCS();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateMCS();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
			}

			
		});
		
		nucleonsField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateNucleons();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateNucleons();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
			}

			void updateNucleons() {
				drawArea.nucleons = Integer.parseInt(nucleonsField.getText());
			}
		});
		
		energyField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				updateEnergy();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				updateEnergy();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
			}

			void updateEnergy() {
				String selected = (String) energyList.getSelectedItem();
				 if (selected == "Cell Energy") {
					 DrawArea.cellEnergy = Integer.parseInt(energyField.getText());
					 System.out.println(DrawArea.cellEnergy);
				 }
				 if (selected == "Border Energy") {
					 DrawArea.borderEnergy = Integer.parseInt(energyField.getText());
					 System.out.println(DrawArea.borderEnergy);
				 }
			}
		});

		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menuBar.add(menu);
		menuItem = new JMenuItem("Export");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					drawArea.importToFile();
				} catch (FileNotFoundException ex) {
					Logger.getLogger(SwingPaint.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		});
		menu.add(menuItem);
		menuItem = new JMenuItem("Import");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					drawArea.exportFromFile();
				} catch (FileNotFoundException ex) {
					Logger.getLogger(SwingPaint.class.getName()).log(Level.SEVERE, null, ex);
				}
			}

		});
		menu.add(menuItem);
		menuItem = new JMenuItem("exportToBMP");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					drawArea.importToBMP();
				} catch (FileNotFoundException ex) {
					Logger.getLogger(SwingPaint.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});
		menu.add(menuItem);

		menuItem = new JMenuItem("importFromBMP");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					drawArea.exportFromBMP();
				} catch (FileNotFoundException ex) {
					Logger.getLogger(SwingPaint.class.getName()).log(Level.SEVERE, null, ex);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Show energy distribution");
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
					drawArea.distributeEnergy();
					energyDistribution = new EnergyDistribution(drawArea.cells, drawArea.size);
					energyDistribution.show();
					
					energyDistribution.work();
			}
			
		});
		menu.add(menuItem);
		
		typeList = new JList<>(types);
		typeList.setSelectedIndex(0);
		typeList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				int index = typeList.getSelectedIndex();
				if (index == 0) {
					drawArea.MonteCarlo = false;
				}
				if (index == 1) {
					drawArea.MonteCarlo = true;
				}
				
			}
			
		});

		nucleonTypeList = new JList<>(nucleonTypes);
		nucleonTypeList.setSelectedIndex(0);
		nucleonTypeList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int index = nucleonTypeList.getSelectedIndex();
				if (index == 0) {
					drawArea.constant = true;
					drawArea.increasing = false;
					drawArea.beginning = false;
				}
				if (index == 1) {
					drawArea.constant = false;
					drawArea.increasing = true;
					drawArea.beginning = false;
				}
				if (index == 2) {
					drawArea.constant = false;
					drawArea.increasing = false;
					drawArea.beginning = true;
				}
				
			}
		});
		
		
		JPanel simulationTypes = new JPanel();
		simulationTypes.add(typeList);
		
		JPanel nucleonPanel = new JPanel();
		nucleonPanel.add(nucleonTypeList);
		
		JPanel list = new JPanel();
		
		list.add(cellLabel);
		list.add(cellField);
                list.add(mooreExtendBox);
		list.add(periodicBox);
		list.add(phase2Box);
		list.add(cellSizeLabel);
		list.add(cellSizeField);
                list.add(inclLabel);
                list.add(inclField);
		list.add(inclusionSizeLabel);
		list.add(inclusionSizeField);
		list.add(inclusionRoundBox);
		list.add(probabilityLabel);
		list.add(probabilityField);
		list.add(MCSLabel);
		list.add(MCSField);
		list.add(energyList);
		list.add(energyField);
		list.add(energyType);
		list.add(nucleonsLabel);
		list.add(nucleonsField);
		controls.add(clearBtn);
		controls.add(randBtn);
		controls.add(startBtn);
		controls.add(stopBtn);
                controls.add(inclBtn);
		controls.add(bordersBtn);
		controls.add(clearBordersBtn);
		controls.add(recrystBtn);

		content.add(simulationTypes, BorderLayout.WEST);
		content.add(list, BorderLayout.NORTH);
		content.add(controls, BorderLayout.SOUTH);
		content.add(nucleonPanel, BorderLayout.EAST);

		frame.setJMenuBar(menuBar);
		frame.setSize(764, 767); //766
		frame.setResizable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
