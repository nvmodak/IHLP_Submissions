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
	 private String convertToCardsString(Integer cardSelected)
	 {
		 String card = "";
		 switch (cardSelected) {
		case 1: 
			card = "Ace";
			break;
		case 11: 
			card = "Jack";
			break;
		case 12: 
			card = "Queen";
			break;
		case 13: 
			card = "King";
			break;
		
		default:
			card = cardSelected.toString();
		}
		 return card;
	 }
	 private Integer convertToCardsInteger(String cardStringRecv)
	 {
		 Integer card;
		 switch (cardStringRecv) {
		case "Ace": 
			card = 1;
			break;
		case "Jack": 
			card = 11;
			break;
		case "Queen": 
			card = 12;
			break;
		case "King": 
			card = 13;
			break;
		
		default:
			card = Integer.parseInt(cardStringRecv);
		}
		 return card;
	 }
	 
	 private void printClientOutput(String dataRecvd, int clientNumber)
	 {
		 String cardType = "";
		 switch (clientNumber) {
		case 1: 
			cardType ="Hearts";
			break;
		case 2: 
			cardType ="Diamonds";
			break;
		case 3: 
			cardType ="Clubs";
			break;
		}
		 System.out.println("Server has received " + dataRecvd +" of " + cardType + " from client " + clientNumber);
	 }
	private void startPlaying()
	{
		int totalCards_Available = 13;
		for(int iFetchCards = 1; iFetchCards <= 13; iFetchCards++, totalCards_Available -- ) 
		{
		    System.out.println("\n\n**************");
		    System.out.println("    Round :- " + iFetchCards +"    ");
		    System.out.println("**************\n\n");
		    Random random = new Random();
		    int cardIndex = random.nextInt(totalCards_Available);
		    Integer cardSelected = cardsAvailable.get(cardIndex);
		    cardsAvailable.remove(cardIndex);
		    String card = convertToCardsString(cardSelected);
		    String dataReceived = "";
		    int clientOneCardSelected = 0;
		    int clientTwoCardSelected = 0;
		    int clientThreeCardSelected = 0;
		    System.out.println("Server has selected card is " + card + " of Spades");

		    this.sendDataToClients(0, card);
		    dataReceived = this.getDataFromClients(0);
		    this.printClientOutput(dataReceived, 1);
		    //clientOneCardSelected = Integer.parseInt(dataReceived);
		    clientOneCardSelected = this.convertToCardsInteger(dataReceived);
		    if(clientOneSelectionList.contains(clientOneCardSelected))
		    {
		    	System.out.println("Got duplicate input from the client " + 1 + "Aborting");
		    	return;
		    }
		    clientOneSelectionList.add(clientOneCardSelected);
		    
		    
		  
		    this.sendDataToClients(1, cardSelected.toString());
		    dataReceived = this.getDataFromClients(1);
		    this.printClientOutput(dataReceived, 2);
		    //clientTwoCardSelected = Integer.parseInt(dataReceived);
		    clientTwoCardSelected = this.convertToCardsInteger(dataReceived);
		    if(clientTwoSelectionList.contains(clientTwoCardSelected))
		    {
		    	System.out.println("Got duplicate input from the client " + 2 + "Aborting");
		    	return;
		    }
		    clientTwoSelectionList.add(clientTwoCardSelected);
		    
		
		    this.sendDataToClients(2, cardSelected.toString());
		    dataReceived = this.getDataFromClients(2);
		    this.printClientOutput(dataReceived, 3);
		    //clientThreeCardSelected = Integer.parseInt(dataReceived);
		    clientThreeCardSelected = this.convertToCardsInteger(dataReceived);
		    if(clientThreeSelectionList.contains(clientThreeCardSelected))
		    {
		    	System.out.println("Got duplicate input from the client " + 3 + "Aborting");
		    	return;
		    }   
		    clientThreeSelectionList.add(clientThreeCardSelected);
		    
		    int maxNumber = clientOneCardSelected > clientTwoCardSelected ? clientOneCardSelected : clientTwoCardSelected;
		    maxNumber = maxNumber > clientThreeCardSelected ? maxNumber : clientThreeCardSelected;
		    
		    if(maxNumber == clientOneCardSelected)
		    {
		    	System.out.println("Client 1 has max score in round " + iFetchCards);
		    	this.clientOneScore += cardSelected;
		    }
		    if(maxNumber == clientTwoCardSelected)
		    {
		    	System.out.println("Client 2 has max score in round " + iFetchCards);
		    	this.clientTwoScore += cardSelected;
		    }
		    if(maxNumber == clientThreeCardSelected)
		    {
		    	System.out.println("Client 2 has max score in round " + iFetchCards);
		    	this.clientThreeScore += cardSelected;
		    }
		    
		    System.out.println("Scores of clients are as follows ");
		    System.out.println("Client 1 :- " + this.clientOneScore);
		    System.out.println("Client 2 :- " + this.clientTwoScore);
		    System.out.println("Client 3 :- " + this.clientThreeScore);
		    
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
		
		ArrayList<Integer> winningClients = new ArrayList<>();
	    if(maxClientScore == this.clientOneScore)
	    {
	    	winningClients.add(1);
	    }
	    if(maxClientScore == this.clientTwoScore)
	    {
	    	winningClients.add(2);
	    }
	    if(maxClientScore == this.clientThreeScore)
	    {
	    	winningClients.add(3);
	    }
		
		String moreThanOneClient = winningClients.size() > 1 ? "are" : "is";
		String endAdvertiseWinner = "Winner " + moreThanOneClient + " Client Number ";
		String EndAdv = "";
		for (Integer integer : winningClients) {
			EndAdv += integer + "  ";
			}
		endAdvertiseWinner += EndAdv;
		endAdvertiseWinner += "and score is " + maxClientScore;
		
		System.out.println("\n\n" + endAdvertiseWinner + "\n\n");
		
		this.sendDataToClients(0,endAdvertiseWinner);
		this.sendDataToClients(1,endAdvertiseWinner);
		this.sendDataToClients(2,endAdvertiseWinner);
		
	}
	
	private int getMaxNumber(int val1, int val2)
	{
		if (val1 > val2) {
			return 0;
		}
		else if (val1 < val2) {
			return 1;
		}
		else
			return -1;
	}
	
	private void closeResources(int clientNumber)
	{
		try {

		this.clientOutputStreams[clientNumber].close();
		
		this.clientInputStreams[clientNumber].close();
		
		this.listSockets[clientNumber].close();
		

		this.listServer[clientNumber].close();
		System.out.println("Connection to Client Number " + (clientNumber + 1) + " is closed.");
		

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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