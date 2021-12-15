import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Server {
	
	static ArrayList<MyFile> myFiles = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		int fileId = 0;
		
		JFrame jframe = new JFrame("Server");
		jframe.setSize(400, 400);
		jframe.setLayout(new BoxLayout(jframe.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel jpanel = new JPanel();
		jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
		
		JScrollPane jscroll = new JScrollPane(jpanel);
		jscroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		JLabel title = new JLabel("File Reciever");
		title.setFont(new Font("Arial", Font.BOLD, 25));
		title.setBorder(new EmptyBorder(20, 0, 10, 0));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		jframe.add(title);
		jframe.add(jscroll);
		jframe.setVisible(true);
		
		//create new socket on port 1234
		try (ServerSocket serverSocket = new ServerSocket(1234)){
			
			
			System.out.println("Server is listening on port 1234");
			
			while(true) {
				//accept client connection
				Socket socket = serverSocket.accept();
				System.out.println("New Client Accepted");
				
				//create input stream and buffer
				InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                
                //create output stream, buffer and printwriter
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                Scanner scanner = new Scanner(System.in);
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream()); //data stream for files
 
                String text;
				
				do {
					//read message first from client
					
					text = reader.readLine();
					
					//if user enters this command in console to send a file
					if (text.equalsIgnoreCase("sending file")) {
						//read length of file name from client
						int fileNameLength = dataInputStream.readInt();
						
						if (fileNameLength > 0) {
							//buffer for file name bytes
							byte[] fileNameBytes = new byte[fileNameLength];
							//read file name and fill buffer until end of name
							dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
							String filename = new String(fileNameBytes);
							
							//read file length from client
							int fileContentLength = dataInputStream.readInt();
							
							//System.out.println(fileContentLength);
							
							if (fileContentLength > 0) {
								//buffer for file content
								byte[] fileContentBytes = new byte[fileContentLength];
								
								//read file content and fill buffer until EOF
								dataInputStream.readFully(fileContentBytes, 0, fileContentLength);
								
								JPanel jfileRow = new JPanel();
								jfileRow.setLayout(new BoxLayout(jfileRow, BoxLayout.Y_AXIS));
								
								
								JLabel jfilename = new JLabel(filename);
								jfilename.setFont(new Font("Arial", Font.BOLD, 20));
								jfilename.setBorder(new EmptyBorder(10, 0, 10, 0));
								jfilename.setAlignmentX(Component.CENTER_ALIGNMENT);
								
								//if file is a txt file
									if (getFileExtension(filename).equalsIgnoreCase("txt")) {
										jfileRow.setName(String.valueOf(fileId));
										jfileRow.addMouseListener(getMyMouseListener());
										jfileRow.add(jfilename);
										jpanel.add(jfileRow);
										jframe.validate();
									}
									else {
										jfileRow.setName(String.valueOf(fileId));
										jfileRow.addMouseListener(getMyMouseListener());
										jfileRow.add(jfilename);
										jpanel.add(jfileRow);
										jframe.validate();
									}
								
									//add a myfile object with name, content and extension
								myFiles.add(new MyFile(fileId, filename, fileContentBytes, getFileExtension(filename)));
								fileId++;
							}
						}
					}
					
					// print out client message
		        	System.out.println("Client: " + text);
					String message = scanner.nextLine();
					writer.println(message);
					writer.flush();
				
				//if user enter quits terminate
			} while (!text.equalsIgnoreCase("quit)")); {
				socket.close();
				writer.close();
				reader.close();
				scanner.close();
				break;
			}
		}
			System.exit(0);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	//mouse listener for file name
	public static MouseListener getMyMouseListener() {
		
		return new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				
				JPanel jpanel = (JPanel) e.getSource();
				
				//number of files
				int fileId = Integer.parseInt(jpanel.getName());
				
				//loop through array to display files to send
				for (MyFile myFile: myFiles) {
					if (myFile.getId() == fileId) {
						JFrame jpreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
						jpreview.setVisible(true);
					}
				}
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
	//file downloader
	public static JFrame createFrame(String filename, byte[] filedata, String fileExtension) {
		
		JFrame jframe = new JFrame("File Downloader");
		jframe.setSize(400, 400);
		
		JPanel jpanel = new JPanel();
		jpanel.setLayout(new BoxLayout(jpanel, BoxLayout.Y_AXIS));
		
		JLabel title = new JLabel("File Downloader");
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont(new Font("Arial", Font.BOLD, 25));
		title.setBorder(new EmptyBorder(20, 0, 10, 0));
		
		JLabel prompt = new JLabel("Are you sure you want to download " + filename);
		prompt.setFont(new Font("Arial", Font.BOLD, 20));
		prompt.setBorder(new EmptyBorder(20, 0, 10, 0));
		prompt.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton yes = new JButton("Yes");
		yes.setPreferredSize(new Dimension(150, 75));
		yes.setFont(new Font("Arial", Font.BOLD, 20));
		
		JButton no = new JButton("No");
		no.setPreferredSize(new Dimension(150, 75));
		no.setFont(new Font("Arial", Font.BOLD, 20));
		
		JLabel fileContent = new JLabel();
		fileContent.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JPanel buttons = new JPanel();
		buttons.setBorder(new EmptyBorder(20, 0, 10, 0));
		buttons.add(yes);
		buttons.add(no);
		
		if (fileExtension.equalsIgnoreCase("txt")) {
			fileContent.setText("<html>" + new String(filedata) + "</html");
		}
		else {
			fileContent.setIcon(new ImageIcon(filedata));
		}
		
		// if user presses yes
		yes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File fileToDownload = new File(filename);
				
				try {
					//create new file for receiving end
					FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
					
					//write file contents to new file
					fileOutputStream.write(filedata);
					fileOutputStream.close();
				
					jframe.dispose();
				}
				catch (IOException error){
					error.printStackTrace();
				}
			}
		});
		
		no.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jframe.dispose();
				
			}
		});
		
		jpanel.add(title);
		jpanel.add(prompt);
		jpanel.add(fileContent);
		jpanel.add(buttons);
		
		jframe.add(jpanel);
		
		return jframe;
		
	}
	
	//file extension method
	public static String getFileExtension(String filename) {
		
		int i = filename.lastIndexOf('.');
	
		if (i > 0) {
			return filename.substring(i + 1);
		}
		else {
			return "No Extension Found";
		}
	}

}
