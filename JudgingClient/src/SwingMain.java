import javax.swing.*;

import packet.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * The gui for the Client
 * @author Stephen Bussey
 *
 */
public class SwingMain extends JFrame implements Observer
{
	private JLabel problem, file, language;
	private JTextField fileField;
	private JButton selectFile, submit;
	private File submitFile;
	private JTextArea result;
	private Client client = null;
	private JComboBox<String> problemSelect;
	
	private String host;
	private int port;
	
	/**
	 * Initialize the GUI and client socket
	 */
	public SwingMain()
	{
		super ("Programming Judging Client");
		this.setLayout(null);
		this.setSize(500, 400);
	
		problem = new JLabel("Problem:");
		file = new JLabel("File:");
		language = new JLabel("Language:");
		fileField = new JTextField("");
		selectFile = new JButton("Select");
		submit = new JButton("Submit");
		result = new JTextArea();
		problemSelect = new JComboBox<String>();
		
		problem.setLocation(10, 10);
		problem.setSize(70, 25);
		
		problemSelect.setLocation(105, 10);
		problemSelect.setSize(180, 25);
		
		file.setLocation(10, 35);
		file.setSize(70, 25);
		
		fileField.setLocation(105, 35);
		fileField.setSize(300, 25);
		fileField.setEditable(false);
		
		selectFile.setLocation(410, 35);
		selectFile.setSize(80, 25);
		
		language.setLocation(10, 60);
		language.setSize(90, 25);
		
		submit.setSize(100, 25);
		submit.setLocation((this.getWidth()/2)-(submit.getWidth()/2), 85);
		
		result.setSize(this.getWidth() - 10, 250);
		result.setLocation(5, 115);
		result.setBorder(BorderFactory.createLineBorder(Color.black));
		result.setLineWrap(true);
		
		this.add(problem);
		this.add(file);
		this.add(language);
		this.add(fileField);
		this.add(selectFile);
		this.add(submit);
		this.add(result);
		this.add(problemSelect);
		if(!loadProperties())
		{
			result.append("Properties could not be loaded\n");
		}
		
		//When the selectFile button is pressed, bring up a JFileChooser dialog
		selectFile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser(new File("."));
				int returnVal = fc.showOpenDialog(SwingMain.this);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					submitFile = fc.getSelectedFile();
					fileField.setText(submitFile.getAbsolutePath());
				}
			}
		});
		
		
		//When the submit button is pressed, attempt to send a sourcePacket to the server
		submit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				SourcePacket p;
				try {
					p = new SourcePacket(problemSelect.getSelectedItem().toString(), submitFile);
					/*
					 * TODO: Make the client detect when it is no longer connected
					 */
					if(!client.getClientSocket().isConnected() || client.getClientSocket().isClosed())
						result.append("Error: Connection to judge lost\n");
					client.sendPacket(p);
				} catch (FileNotFoundException e) {
					result.append(submitFile.getName() + " could not be found.\n");
				}
			}			
		});

		this.setVisible(true);
		
		//Create the client
		try {
			client = new Client(host, port, SwingMain.this);
		} catch (Exception e) {
			e.printStackTrace();
			result.append("Connection to server could not be opened.\n");
		}
	}
	
	private boolean loadProperties()
	{
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("connectionProperties.prop"));
			host = prop.getProperty("host");
			port = Integer.parseInt(prop.getProperty("port"));
		} catch (Exception e) {
			result.append(e.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * Process when the Server sends packets
	 */
	public void update(Packet re)
	{
		//if a ResultPacket is sent, print out the message in the packet
		if(re instanceof ResultPacket)
			result.append(((ResultPacket) re).getResult().getMessage() + "\n\n");
		//if a ProblemPacket is sent, append all Problem names to the problemSelect container
		else if(re instanceof ProblemPacket)
		{
			ArrayList<String> problemNames = ((ProblemPacket) re).getProblemNames();
			for(String s: problemNames)
			{
				problemSelect.addItem(s);
			}
		}
	}
	

	public static void main(String[] args)
	{
		SwingMain win = new SwingMain();
		win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
