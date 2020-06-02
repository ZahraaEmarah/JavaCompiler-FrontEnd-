import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.awt.event.ActionEvent;

public class gui {

	private JFrame frame;
	LexicalRules lexicalRules;
	readCFG CFG;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					gui window = new gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public gui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 718, 741);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(153, 0, 51));
		panel.setBounds(0, 0, 702, 702);
		frame.getContentPane().add(panel);

		JTextArea textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.BOLD, 17));
		textArea.setBackground(Color.PINK);
		textArea.setBounds(47, 107, 300, 410);

		JTextArea textArea_1 = new JTextArea();
		textArea_1.setEditable(false);
		textArea_1.setFont(new Font("Monospaced", Font.BOLD, 17));
		textArea_1.setBackground(Color.PINK);
		textArea_1.setBounds(355, 106, 300, 410);
		panel.setLayout(null);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(45, 106, 300, 410);
		panel.add(scrollPane);

		JScrollPane scrollPane_1 = new JScrollPane(textArea_1);
		scrollPane_1.setBounds(355, 106, 300, 410);
		panel.add(scrollPane_1);

		JTextArea textArea_2 = new JTextArea();
		textArea_2.setEditable(false);
		textArea_2.setFont(new Font("Monospaced", Font.BOLD, 17));
		textArea_2.setBounds(45, 625, 608, 40);
		textArea_2.setBackground(Color.PINK);

		JScrollPane scrollPane_2 = new JScrollPane(textArea_2);
		scrollPane_2.setBounds(45, 598, 610, 67);
		panel.add(scrollPane_2);

		JLabel lblLabel = new JLabel("Java Compiler");
		lblLabel.setBounds(45, 21, 610, 42);
		lblLabel.setForeground(Color.LIGHT_GRAY);
		lblLabel.setFont(new Font("Traditional Arabic", Font.PLAIN, 35));
		lblLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblLabel);

		JLabel lblNewLabel = new JLabel("Your code:");
		lblNewLabel.setBounds(45, 74, 160, 27);
		lblNewLabel.setForeground(Color.LIGHT_GRAY);
		lblNewLabel.setFont(new Font("Traditional Arabic", Font.BOLD, 22));
		panel.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Bytecode:");
		lblNewLabel_1.setBounds(361, 74, 137, 27);
		lblNewLabel_1.setForeground(Color.LIGHT_GRAY);
		lblNewLabel_1.setFont(new Font("Traditional Arabic", Font.BOLD, 22));
		panel.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Console:");
		lblNewLabel_2.setBounds(45, 571, 131, 27);
		lblNewLabel_2.setForeground(Color.LIGHT_GRAY);
		lblNewLabel_2.setFont(new Font("Traditional Arabic", Font.BOLD, 23));
		panel.add(lblNewLabel_2);

		JButton btnCompile = new JButton("Compile");
		btnCompile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String string = textArea.getText();
				System.out.println(string);

				try (PrintWriter out = new PrintWriter("program.txt")) {
					out.println(string);
					out.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				lexicalRules = new LexicalRules();
				try {
					// call the read file then it will do everything
					lexicalRules.readFile();
					CFG = new readCFG();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				textArea_1.setText("");
				try {
					BufferedReader in = new BufferedReader(new FileReader("bytecode.txt"));
					String line;
					line = in.readLine();
					while(line != null){
						  textArea_1.append(line + "\n");
						  line = in.readLine();
						}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		});
		btnCompile.setBackground(Color.LIGHT_GRAY);
		btnCompile.setFont(new Font("Tw Cen MT", Font.PLAIN, 18));
		btnCompile.setBounds(248, 542, 212, 27);
		panel.add(btnCompile);
	}
}
