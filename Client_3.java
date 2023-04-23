package Cards;

//A Java program for a Client
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Client_3 {
 // initialize socket and input output streams
 private Socket socket = null;
 private DataInputStream input = null;
 private DataOutputStream outSocket = null;
 private DataInputStream inputSocket = null;

 
 //Cards
	private ArrayList<Integer> cardsAvailable = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13));

 // constructor to put ip address and port
 public Client_3(String address, int port)
 {
     // establish a connection
     try {
         socket = new Socket(address, port);
         System.out.println("Connected");

         // takes input from terminal
         input = new DataInputStream(System.in);

         // sends output to the socket
         outSocket = new DataOutputStream(
             socket.getOutputStream());
         
         inputSocket = new DataInputStream(
					new BufferedInputStream(socket.getInputStream()));
     }
     catch (UnknownHostException u) {
         System.out.println(u);
         return;
     }
     catch (IOException i) {
         System.out.println(i);
         return;
     }

     // string to read message from input
     String line = "";

     // keep reading until "Over" is input
 
    	 int totalCards_Available = 13;
			for(int iFetchCards = 1; iFetchCards <= 13; iFetchCards++, totalCards_Available -- ) {
				System.out.println("Turn - " + iFetchCards);
				String dataReceived = this.GetServerInput(inputSocket);
				System.out.println("data received from server " + dataReceived);
				int advertisedCardFromServer = Integer.parseInt(dataReceived);
				System.out.println("Server has Selected Card " + advertisedCardFromServer +" of Spades");
			    Random random = new Random();
			    int cardIndex = random.nextInt(totalCards_Available);
			    Integer cardSelected = cardsAvailable.get(cardIndex);
			    System.out.println("selected Card is " + cardSelected +" of Hearts");
         try {
             //line = input.readLine();
        	 outSocket.writeUTF(cardSelected.toString());
         }
         catch (IOException i) {
             System.out.println(i);
         }
			cardsAvailable.remove(cardIndex);

			}
			
			System.out.println(this.GetServerInput(inputSocket));
   

     // close the connection
     try {
         input.close();
         outSocket.close();
         inputSocket.close();
         socket.close();
     }
     catch (IOException i) {
         System.out.println(i);
     }
 }

 private String GetServerInput(DataInputStream inputStream) {
	// TODO Auto-generated method stub
	 String inputData = "";
	 try {
		inputData = inputStream.readUTF();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
	return inputData;
}

public static void main(String args[])
 {
     Client_3 client = new Client_3("127.0.0.1", 5003);
 }
}