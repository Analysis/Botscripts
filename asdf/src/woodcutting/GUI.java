package woodcutting;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

public class GUI {
	
	private JFrame frmWoodcutterBot;
	private MainClass ctx;

	/**
	 * Launch the application.
	 */
	public void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI(ctx);
					window.frmWoodcutterBot.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public GUI(MainClass main) {
		this.ctx = main;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmWoodcutterBot = new JFrame();
		frmWoodcutterBot.setTitle("Woodcutter Bot");
		frmWoodcutterBot.setBounds(100, 100, 250, 230);
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctx.setStartScript(true);
				frmWoodcutterBot.setVisible(false);
			}
		});
		btnStart.setBackground(Color.LIGHT_GRAY);
		
		cmbTrees = new JComboBox<>();
		cmbTrees.setModel(new DefaultComboBoxModel<>(new String[] {"Tree", "Oak", "Willow", "Yew"}));
		cmbTrees.setSelectedIndex(0);
		
		cmbLocation = new JComboBox<>();
		cmbLocation.setModel(new DefaultComboBoxModel<>(new String[] {"Draynor", "Lumbridge", "Varrock", "Edgeville", "Grand Exchange"}));
		
		JLabel lblTree = new JLabel("Tree:");
		
		JLabel lblLocation = new JLabel("Location:");
		
		cmbMethod = new JComboBox<>();
		cmbMethod.setModel(new DefaultComboBoxModel<>(new String[] {"Bank", "Drop"}));
		
		lblMethod = new JLabel("Method:");
		GroupLayout groupLayout = new GroupLayout(frmWoodcutterBot.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblTree)
						.addComponent(lblLocation)
						.addComponent(lblMethod))
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(11)
							.addComponent(cmbTrees, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
								.addComponent(cmbLocation, 0, 126, Short.MAX_VALUE)
								.addComponent(cmbMethod, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
					.addContainerGap(14, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblTree)
						.addComponent(cmbTrees, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblLocation)
						.addComponent(cmbLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(cmbMethod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblMethod))
					.addGap(20)
					.addComponent(btnStart)
					.addContainerGap())
		);
		frmWoodcutterBot.getContentPane().setLayout(groupLayout);
	}
	
	public String getTreeType()
	{
		return cmbTrees.getSelectedItem().toString();
	}
	
	public int getLocation()
	{
		return cmbLocation.getSelectedIndex();
	}
	
	public String getMethod()
	{
		return cmbMethod.getSelectedItem().toString();
	}
	
	public void setVisible(boolean b) {
		frmWoodcutterBot.setVisible(true);		
	}
	
	private JComboBox<String> cmbTrees;
	private JComboBox<String> cmbLocation;
	private JComboBox<String> cmbMethod;
	private JLabel lblMethod;
}
