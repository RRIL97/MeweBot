package Main;

import Bot.Mewe;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;


public class Main {
    
    
    public static void main(String [] args) throws InterruptedException
    {
        String  input         = "";
        String  messageToSend = "";
        String  mediaId       = "";
        
        Scanner inputScanner = new Scanner(System.in);
        
        System.out.println("Input Cookie");
        input = inputScanner.nextLine();
        System.out.println("Input Message To Send In Chat");
        messageToSend = inputScanner.nextLine(); 
        System.out.println("Input Media ID");
        mediaId       = inputScanner.nextLine();
        
        Mewe user = new Mewe(input);
          
        ArrayList<String> activeGroups = user.getJoinedGroups();
         
        for(int i = 0 ; i < activeGroups.size() ; i++){
            System.out.println("Working On Group ID - " +activeGroups.get(i));
            user.sendMessageInChat(activeGroups.get(i), messageToSend);
            user.shareInGroup(activeGroups.get(i), mediaId);
            Thread.sleep(ThreadLocalRandom.current().nextInt(1,5)*1000);
        } 
        }
}
