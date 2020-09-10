
package Bot;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList; 

public class Mewe{
     
    private String        pageSource   = "";
    public  static String userAgent    = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.83 Safari/537.36";
    
    private Connection    connector;   
    private String        userCookie    = "";
    private csrfFetch     csrfFetcher;
    
    private ArrayList<String> cookies = new ArrayList<String>();
     
    
    public Mewe(String userCookie) {
        this.userCookie         = userCookie;
        this.connector           = new Connection();  
        
        cookies.add(userCookie);
        csrfFetcher = new csrfFetch(userCookie,cookies);
        new Thread(csrfFetcher).start(); 
         
        try{
        do{
         Thread.sleep(1000);  
        }while(!readyToLaunchRequests()); 
        }catch(Exception meWeConstructorException){
            System.out.println("ME_WE_CONSTRUCTOR_EXEPTION");
        }
    }   
 
    public boolean readyToLaunchRequests(){
        return csrfFetcher.getCsrf().length()>0;
    }
    
    public void sendMessageInChat(String groupId,String messageToSend){
         pageSource = connector.initatePost("https://mewe.com/api/v2/chat/thread/"+groupId+"/message",userAgent,"{\"setAsRead\":true,\"message\":\""+messageToSend+"\"}", csrfFetcher.getCsrf(),cookies);
         System.out.println("Sent To Chat , Group Id:  " + groupId+" Message: "+ messageToSend);
    }
    
    public void shareInGroup(String groupId, String mediaID)
    {
        pageSource = connector.initatePost("https://mewe.com/api/v3/group/"+groupId+"/post/"+mediaID+"/reshare/group/"+groupId,userAgent,"{\"newText\":\"\",\"public\":false,\"closeFriends\":false,\"postedByPage\":false,\"emojisPreset\":[]}", csrfFetcher.getCsrf(),cookies);
        System.out.println("Sent Post To Group , Group Id:  " + groupId+" mediaID: "+ mediaID);
    }
    
    //Fetches joined group id's of a user
    public ArrayList<String> getJoinedGroups(){
        ArrayList<String> joinedGroups  = new ArrayList<>(); 
        try{
         int pageGroup = 0;
            
         while(true){
             pageSource = connector.initatePost("https://mewe.com/api/v2/chat/threads",userAgent,"{\"chatType\":\"GroupChat\",\"offset\":"+((20)*pageGroup)+"}",csrfFetcher.getCsrf(),cookies);
             pageGroup ++;
             if(pageSource.equals("/n{\"threads\":[],\"_links\":{\"self\":{\"href\":\"/api/v2/chat/threads\"}}}"))
             break; 
             String threadIds [] = pageSource.split("\"threadId\":\"");
         
             for(int i = 0 ; i <  threadIds.length; i++){
             String tempThreadId = threadIds[i].split("\",\"text\"")[0];   
             if(!joinedGroups.contains(tempThreadId))
             joinedGroups.add(tempThreadId);  
            }
         }
           
         
        }catch(Exception getJoinedException){
            getJoinedException.printStackTrace();
        }
        return joinedGroups;
    }
    //sends request to join a group by it's name
    public void applyToGroup(String groupName){
        pageSource = connector.initatePost("https://mewe.com/api/v2/group/public/"+groupName+"/apply", userAgent, "",csrfFetcher.getCsrf(),cookies);
        System.out.println("Applying To "+ groupName + " -> " + pageSource); 
    }
    
    //scrapes available groups under inputted category
    //returns arraylist of groupnames
    public ArrayList<String> scrapeGroupsInCategory (String category){
        ArrayList<String> scrapedGroups = new ArrayList<String>();   
        try{ 
         
        String groupIdSplit   [];
        String groupId;
        
        String groupNameSplit [];
        String groupName; 
        
        int pagesOfGroups = 10;
        
        for(int i = 0; i <  pagesOfGroups; i ++){
            
        pageSource     = connector.initateGet("https://mewe.com/api/v2/groups/public/"+category+"?topic="+category+"&locale=en&offset="+(i*20)+"&maxResults=20", userAgent ,csrfFetcher.getCsrf(),cookies);
        if(pageSource.equals("{\"groups\":[],\"_links\":{\"self\":{\"href\":\"/api/v2/groups/public/"+category+"\"}}}")){
            System.out.println("Done Scraping! Scraped : " + scrapedGroups.size()); 
            break;
        }
        Thread.sleep(500);  
        groupIdSplit    = pageSource.split("\"id\":\"");
        groupNameSplit  = pageSource.split("\"name\":\""); 
        for(int r = 1; r < groupIdSplit.length;  r ++){
        groupId   = groupIdSplit[r].split("\",\"name\"")[0];
        groupName = groupNameSplit[r].split("\",\"color\"")[0];
        if(groupIdSplit[r].contains("applyQuestions\":[]")){
        groupName = URLEncoder.encode(groupName, StandardCharsets.UTF_8.toString());
        scrapedGroups.add(groupName);
        }
        }
        } 
        }catch(Exception scrapeGroupException){
            scrapeGroupException.printStackTrace();
            System.out.println("SCRAPE_GROUP_EXCEPTION");
        }
        return scrapedGroups;
    }
    
}
