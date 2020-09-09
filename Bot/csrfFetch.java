
package Bot;

import java.util.ArrayList;

 
public class csrfFetch implements Runnable {
     
    private final String     startCookie;
    private final Connection connector;  
    
    private ArrayList<String> cookiesInput;
    
    private String     csrfToken  = ""; 
    private String     pageSource = "";
      
    private boolean botHasFinished = false;
  
    public csrfFetch(String startCookie,ArrayList<String> cookiesInput){
        this.startCookie        = startCookie;  
        this.connector          = new Connection(); 
        this.cookiesInput       = cookiesInput;
    }
    
    public String getCsrf(){
        return csrfToken;
    }
     
    @Override
    public void run() {
    try{
            String currentCookie;
            while(!botHasFinished){ 
            
            pageSource = connector.initateGet("https://mewe.com/api/v3/auth/identify", Mewe.userAgent ,csrfToken,cookiesInput);
            if(cookiesInput != null){
            for(int i = 0 ; i < cookiesInput.size(); i++){
              currentCookie = cookiesInput.get(i); 
              if(currentCookie.contains("csrf-token="))
                csrfToken = currentCookie.split("csrf-token=")[1].split(";")[0];    
             } 
            }
            Thread.sleep(5000); 
            } 
            }catch(Exception fetchTokenException){
                fetchTokenException.printStackTrace();
            }
    
    }
}
