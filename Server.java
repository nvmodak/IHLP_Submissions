package Cards;

//A Java program for a Server
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.io.*;

public class Server
{
	//initialize socket and input stream
	private Socket		 socket = null;
	private ServerSocket server = null;
	private DataInputStream in	 = null;
	private DataOutputStream outClientSocket = null;
	
	private Socket		 listSockets[] = new Socket[] {null, null, null};
	private ServerSocket listServer[] = new ServerSocket[] {null, null, null};
	//
	private DataInputStream clientInputStreams[]	 = new DataInputStream[] {null, null, null};
	private DataOutputStream clientOutputStreams[] = new DataOutputStream[] {null, null, null};
	
	private ArrayList<Integer> cardsAvailable = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13));
	private ArrayList<Integer> clientOneSelectionList = new ArrayList<>();
	private ArrayList<Integer> clientTwoSelectionList = new ArrayList<>();
	private ArrayList<Integer> clientThreeSelectionList = new ArrayList<>();
	private int clientOneScore = 0;
	private int clientTwoScore = 0;
	private int clientThreeScore = 0;

	int clientPorts[] = new int[] {5001, 5002, 5003};
	
	private void startListening(int clientNumber)
	{
		int port = clientPorts[clientNumber];
		try {
			this.listServer[clientNumber] = new ServerSocket(port);
		
		System.out.println("Server started");

		System.out.println("Waiting for a client ...");

		this.listSockets[clientNumber] = this.listServer[clientNumber].accept();
		System.out.println("Client accepted");

		// takes input from the client socket
		this.clientInputStreams[clientNumber] = new DataInputStream(
			new BufferedInputStream(this.listSockets[clientNumber].getInputStream()));

		this.clientOutputStreams[clientNumber] = new DataOutputStream(
				this.listSockets[clientNumber].getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void sendDataToClients(int clientNumber, String dataToSend)
	{
		try {
			this.clientOutputStreams[clientNumber].writeUTF(dataToSend);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getDataFromClients(int clientNumber)
	{
		try {
			return this.clientInputStreams[clientNumber].readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	private void startPlaying()
	{
		
		
		
		
		int totalCards_Available = 13;
		for(int iFetchCards = 1; iFetchCards <= 13; iFetchCards++, totalCards_Available -- ) 
		{
			
		    Random random = new Random();
		    int cardIndex = random.nextInt(totalCards_Available);
		    System.out.println("Random Card selected  " + cardIndex);
		    Integer cardSelected = cardsAvailable.get(cardIndex);
		    cardsAvailable.remove(cardIndex);
		    String dataReceived = "";
		    int clientOneCardSelected = 0;
		    int clientTwoCardSelected = 0;
		    int clientThreeCardSelected = 0;
		    System.out.println("Selected card is " + cardSelected);
		    // Send and receive data from client
		    System.out.println("Client 1 dealing");
		    this.sendDataToClients(0, cardSelected.toString());
		    dataReceived = this.getDataFromClients(0);
		    System.out.println(dataReceived);
		    clientOneCardSelected = Integer.parseInt(dataReceived);
		    if(clientOneSelectionList.contains(clientOneCardSelected))
		    {
		    	System.out.println("Got duplicate input from the client " + 0 + "Aborting");
		    	return;
		    }
		    clientOneSelectionList.add(clientOneCardSelected);
		    
		    
		    System.out.println("Client 2 dealing");
		    this.sendDataToClients(1, cardSelected.toString());
		    dataReceived = this.getDataFromClients(1);
		    System.out.println(dataReceived);
		    clientTwoCardSelected = Integer.parseInt(dataReceived);
		    if(clientOneSelectionList.contains(clientTwoCardSelected))
		    {
		    	System.out.println("Got duplicate input from the client " + 0 + "Aborting");
		    	return;
		    }
		    
		    System.out.println("Client 3 dealing");
		    this.sendDataToClients(2, cardSelected.toString());
		    dataReceived = this.getDataFromClients(2);
		    System.out.println(dataReceived);
		    clientThreeCardSelected = Integer.parseInt(dataReceived);
		    if(clientOneSelectionList.contains(clientThreeCardSelected))
		    {
		    	System.out.println("Got duplicate input from the client " + 0 + "Aborting");
		    	return;
		    }   
		    
		    
		    int maxNumber = clientOneCardSelected > clientTwoCardSelected ? clientOneCardSelected : clientTwoCardSelected;
		    maxNumber = maxNumber > clientThreeCardSelected ? maxNumber : clientThreeCardSelected;
		    
		    if(maxNumber == clientOneCardSelected)
		    {
		    	this.clientOneScore += cardSelected;
		    }
		    if(maxNumber == clientTwoCardSelected)
		    {
		    	this.clientTwoScore += cardSelected;
		    }
		    if(maxNumber == clientThreeCardSelected)
		    {
		    	this.clientThreeScore += cardSelected;
		    }
		    
		    
		}
		Integer maxScoreClientNumber = 0;
		Integer maxClientScore = 0;
		if(this.clientOneScore > this.clientTwoScore)
		{
			maxClientScore = this.clientOneScore;
			maxScoreClientNumber = 1;
		}
		else
		{
			maxClientScore = this.clientTwoScore;
			maxScoreClientNumber = 2;
		}
		if(maxClientScore < this.clientThreeScore)
		{
			maxClientScore = this.clientThreeScore;
			maxScoreClientNumber = 3;
		}
		
		this.sendDataToClients(0,  "Winner is Client Number " + maxScoreClientNumber.toString() + "and score" + maxClientScore.toString());
		this.sendDataToClients(1,  "Winner is Client Number " + maxScoreClientNumber.toString() + "and score" + maxClientScore.toString());
		this.sendDataToClients(2,  "Winner is Client Number " + maxScoreClientNumber.toString() + "and score" + maxClientScore.toString());
		
	}
	
	private void closeResources(int clientNumber)
	{
		try {
		
		System.out.println("Server started");

		System.out.println("Waiting for a client ...");


		this.clientOutputStreams[clientNumber].close();
		
		this.clientInputStreams[clientNumber].close();
		
		this.listSockets[clientNumber].close();
		

		this.listServer[clientNumber].close();
		System.out.println("Connection to Client Number " + clientNumber + 1 + " is closed.");
		

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// constructor with port
	public Server(int port)
	{
		// starts server and waits for a connection
		try
		{
			
			for (int iPort = 0; iPort < 3; iPort++) {
				
				
			}
			server = new ServerSocket(port);
			System.out.println("Server started");

			System.out.println("Waiting for a client ...");

			socket = server.accept();
			System.out.println("Client accepted");

			// takes input from the client socket
			in = new DataInputStream(
				new BufferedInputStream(socket.getInputStream()));

			outClientSocket = new DataOutputStream(
	                 socket.getOutputStream());
			String line = "";

			// reads message from client until "Over" is sent
// Get the 
				int totalCards_Available = 13;
				for(int iFetchCards = 1; iFetchCards <= 13; iFetchCards++, totalCards_Available -- ) {
					
					
				    Random random = new Random();
				    int cardIndex = random.nextInt(totalCards_Available);
				    Integer cardSelected = cardsAvailable.get(cardIndex);
				    this.SendDataToClient(outClientSocket, cardSelected.toString());
				    System.out.println("Card Drawing " + iFetchCards +"\n\nServer :- \nSelected Card is " + cardSelected +" of Spades");
				try
				{
					line = in.readUTF();
					System.out.println(line);
					/*
					String[] clientMessage = line.split("_");
					int clientNumber = Integer.parseInt(clientMessage[0]);
					int cardSelectedByClient = Integer.parseInt(clientMessage[1]);  
					System.out.println(cardSelectedByClient);
					System.out.println("\nClient :- \nSelected Card is " + cardSelectedByClient +" of Spades");
					//clientOneSelection.add(cardSelectedByClient);
					if(cardSelected < cardSelectedByClient)
						clientOneScore += cardSelected;
					System.out.println("\nClientScore is " + clientOneScore + "\n\n");*/
					

				}
				catch(IOException i)
				{
					System.out.println(i);
				}
				cardsAvailable.remove(cardIndex);
				}
			System.out.println(clientOneScore);
			System.out.println("Closing connection");

			// close connection
			socket.close();
			in.close();
		}
		catch(IOException i)
		{
			System.out.println(i);
		}
	}

	public Server() {
		// TODO Auto-generated constructor stub
	}

	private void SendDataToClient(DataOutputStream outClientSocket, String dataToSend) {
		// TODO Auto-generated method stub
		try {
			outClientSocket.writeUTF(dataToSend);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		Server serve = new Server();
		serve.startListening(0);
		serve.startListening(1);
		serve.startListening(2);
		
		serve.startPlaying();
		
		serve.closeResources(0);
		serve.closeResources(1);
		serve.closeResources(2);
	}
	
}
