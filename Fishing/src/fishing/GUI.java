package fishing;

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
	
	private JFrame frmFishingBot;
	private MainClass ctx;

	/**
	 * Launch the application.
	 */
	public void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI(ctx);
					window.frmFishingBot.setVisible(true);
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
		frmFishingBot = new JFrame();
		frmFishingBot.setTitle("Woodcutter Bot");
		frmFishingBot.setBounds(100, 100, 250, 159);
		frmFishingBot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ctx.setStartScript(true);
				frmFishingBot.setVisible(false);
			}
		});
		btnStart.setBackground(Color.LIGHT_GRAY);
		
		cmbLocation = new JComboBox<>();
		cmbLocation.setModel(new DefaultComboBoxModel<>(new String[] {"Lumbridge", "Draynor"}));
		
		JLabel lblLocation = new JLabel("Location:");
		
		cmbMethod = new JComboBox<>();
		cmbMethod.setModel(new DefaultComboBoxModel<>(new String[] {"Bank", "Drop"}));
		
		lblMethod = new JLabel("Method:");
		GroupLayout groupLayout = new GroupLayout(frmFishingBot.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(lblLocation)
						.addComponent(lblMethod))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(btnStart, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
						.addComponent(cmbMethod, 0, 160, Short.MAX_VALUE)
						.addComponent(cmbLocation, 0, 126, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(cmbLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblLocation))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblMethod)
						.addComponent(cmbMethod, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(btnStart)
					.addGap(81))
		);
		frmFishingBot.getContentPane().setLayout(groupLayout);
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
		frmFishingBot.setVisible(true);		
	}
	private JComboBox<String> cmbLocation;
	private JComboBox<String> cmbMethod;
	private JLabel lblMethod;
}
