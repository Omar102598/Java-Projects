import javax.swing.*;
import java.util.*;
import javax.swing.border.EmptyBorder;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;



public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		
		final File[] fileToSend = new File[1];
		
		JFrame jframe = new JFrame("Client");
		jframe.setSize(450, 450);
		jframe.setLayout(new BoxLayout(jframe.getContentPane(), BoxLayout.Y_AXIS));
		//jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
		
		JLabel title = new JLabel("File Sender");
		title.setFont(new Font("Arial", Font.BOLD, 25));
		title.setBorder(new EmptyBorder(20, 0, 10, 0));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JLabel jfilename = new JLabel("Choose file to send.");
		jfilename.setFont(new Font("Arial", Font.BOLD, 20));
		jfilename.setBorder(new EmptyBorder(50, 0, 0, 0));
		jfilename.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JPanel jbutton = new JPanel();
		jbutton.setBorder(new EmptyBorder(75, 0, 10, 0));
		
		JButton sendfile = new JButton("Send File");
		sendfile.setPreferredSize(new Dimension(150, 75));
		sendfile.setFont(new Font("Arial", Font.BOLD, 20));
		
		JButton choosefile = new JButton("Choose File");
		choosefile.setPreferredSize(new Dimension(150, 75));
		choosefile.setFont(new Font("Arial", Font.BOLD, 20));
		
		jbutton.add(sendfile);
		jbutton.add(choosefile);
		
		choosefile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
				jFileChooser.setDialogTitle("Choose a file to send");
				
				if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					fileToSend[0] = jFileChooser.getSelectedFile();
					jfilename.setText("The file you want to send is: " + fileToSend[0].getName());
				}
			}
		});
		
		//create socket to connect to port 1234
		try (Socket socket = new Socket("localhost", 1234)) {
			//create output stream and printwriter
			OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
 
            //scanner to take in user input
            Scanner in = new Scanner(System.in);
            String text;
			
            //if user presses send file
			sendfile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (fileToSend[0] == null) {
						jfilename.setText("Please choose a file first");
					}
					else {
						try {
							//create file and data input streams
							FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
							DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
						    
							//file name string & name byte buffer
							String fileName = fileToSend[0].getName();
							byte[] fileNameBytes = fileName.getBytes();
							
							//bytebuffer for file contents
							byte[] fileContentBytes = new byte[(int)fileToSend[0].length()];
							fileInputStream.read(fileContentBytes); //read file content bytes
							
							// write to server file name bytes and length
							dataOutputStream.writeInt(fileNameBytes.length);
							dataOutputStream.write(fileNameBytes);
							
							//write size of byte to acknowledge EOF
							dataOutputStream.writeInt(fileContentBytes.length);
							dataOutputStream.write(fileContentBytes);
							dataOutputStream.flush();
						}
						catch (UnknownHostException error) {
							System.out.println("Server not found: " + error.getMessage());
						}
						catch (IOException ex) {
							 
						    System.out.println("I/O error: " + ex.getMessage());
						}
					}
				}
				
			});

			jframe.add(title);
			jframe.add(jfilename);
			jframe.add(jbutton);
			jframe.setVisible(true);
			
			do {
                //read in user input
                text = in.nextLine();
                writer.println(text);
                writer.flush();
                
                //create input stream and buffer
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                
                //read server message
                String ServerMessage = reader.readLine();
                
                //display server message
                System.out.println("Server: " + ServerMessage);
                
                //exit if user enters quit
            } while (!text.equalsIgnoreCase("quit"));
 
            socket.close();
            writer.close();
            in.close();
            System.exit(0);
		
		}
		
	}
	
}
