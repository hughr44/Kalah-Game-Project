package KalahGame;

import java.util.Scanner;
import java.net.*;
import java.io.*;

public class Server implements Runnable{
	
	public Server() throws IOException{
		
	}

	public static void main(String[] args) throws IOException {
		Server server = new Server();
		server.run();
	}
	
	public void run(){
		try{
			Scanner myScan = new Scanner(System.in);
	
			System.out.println("connect a client...");		
			ServerSocket ss = new ServerSocket(4542);
			System.out.println("listening on port: " + ss.getLocalPort());
			
			Socket s1 = ss.accept();
			
			System.out.println("client connected...");
			
			InputStreamReader in1 = new InputStreamReader(s1.getInputStream());
			BufferedReader bf1 = new BufferedReader(in1); 
			PrintWriter pr1 = new PrintWriter(s1.getOutputStream());
			
			pr1.println("1");
			pr1.flush();
			
			String inputChoice = "2";
			
			//get the game details from client 1
			String gameDetails = bf1.readLine();
			System.out.println("Game details :" + gameDetails);
			
			if(inputChoice.equals("1")){
				//play against AI
			}
			else{
				//play against another client
				System.out.println("connect a new client...");
				Socket s2 = ss.accept();
				
				System.out.println("client connected...");
				
				InputStreamReader in2 = new InputStreamReader(s2.getInputStream());
				BufferedReader bf2 = new BufferedReader(in2); 
				PrintWriter pr2 = new PrintWriter(s2.getOutputStream());
				
				pr2.println("2");
				pr2.flush();
				
				pr2.println(gameDetails);
				pr2.flush();
				
				boolean isClient1Turn = true;
				String client1Message = "";
				String client2Message = "";
				String outMessage = "";
				boolean gameOver = false;
				
				// start processing messages
				while(!gameOver){
					if(isClient1Turn){
						// read client 1 message
						client1Message = "Waiting";
						System.out.println("Waiting 1");
		                while (client1Message.equals("Waiting")) 
		                { 
		                    try
		                    { 
		                    	client1Message = bf1.readLine(); 
		                    } 
		                    catch(IOException i) 
		                    { 
		                        System.out.println(i); 
		                    } 
		                }
						
						// formulate message to send to client 2, will need to do things to check for multiple moves and things like that
		                System.out.println(client1Message);
						outMessage = client1Message;
						
						// send "OK" to client 1 indicating move recognized and telling it to wait
						pr1.println("OK");
						pr1.flush();
						
						// send message to client 2
						pr2.println(outMessage);
						pr2.flush();
						
						// switch turns
						isClient1Turn = false;
					}
					else{
						// read client 2 message
						client2Message = "Waiting";
						System.out.println("Waiting 2");
		                while (client2Message.equals("Waiting")) 
		                { 
		                    try
		                    { 
		                    	client2Message = bf2.readLine(); 
		                    } 
		                    catch(IOException i) 
		                    { 
		                        System.out.println(i); 
		                    } 
		                }
						
						// formulate message to send to client 1
		                System.out.println(client2Message);
						outMessage = client2Message;
						
						// send "OK" to client 2 indicating move recognized and telling it to wait
						pr2.println("OK");
						pr2.flush();
						
						// send message to client 1
						pr1.println(outMessage);
						pr1.flush();
						
						// switch turns
						isClient1Turn = true;
					}
					
					if(client1Message.equals("WINNER") || client2Message.equals("WINNER")){
						// game is over, do something here
						System.out.println("Game has ended");
						gameOver = true;
					}
					
					if(client1Message.equals("P") || client2Message.equals("P")){
						// pie rule
						System.out.println("Pie rule chosen...");
					}
					
				}
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
	}

}