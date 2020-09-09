
package Bot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter; 
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HttpsURLConnection;


public class Connection {
    
    private String             siteSource = "";
    private URL                siteURL;
    
    private HttpsURLConnection siteConnector;
    private BufferedReader     siteResponseReader;
    
    private     PrintWriter    sitePrintWriter;  ;
     
    public String initateGet(String pageToOpen,String userAgent,String csrfToken,ArrayList<String> inputCookies){
         try{
         siteSource = ""; 
         siteURL    = new URL(pageToOpen);
  
         siteConnector = (HttpsURLConnection) siteURL.openConnection();  
         
         siteConnector.addRequestProperty("Connection"     , "keep-alive");
         siteConnector.addRequestProperty("Cache-Control"  , "max-age=0");
         siteConnector.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
         siteConnector.addRequestProperty("User-Agent"     , userAgent); 
      
         for (String getCookieString : inputCookies) 
	       siteConnector.addRequestProperty("cookie", getCookieString.split(";", 1)[0]);    
         
         siteConnector.addRequestProperty("Accept"          , "*/*"); 
         siteConnector.addRequestProperty("accept-encoding" , "gzip, deflate, br");
         siteConnector.addRequestProperty("accept-language" , "en-US,en;q=0.9"); 
         siteConnector.addRequestProperty("referer"         , "https://mewe.com/myworld"); 
         siteConnector.addRequestProperty("x-requested-with", "XMLHttpRequest");  
         siteConnector.addRequestProperty("x-csrf-token"    , csrfToken);
        
         siteConnector.setInstanceFollowRedirects(false);
         siteConnector.setDoOutput(true);
         siteConnector.setUseCaches(true);
         siteConnector.setRequestMethod("GET");
         siteConnector.setReadTimeout(20000);

         int responseCode = siteConnector.getResponseCode();
         
         if(responseCode != 200)
         siteResponseReader = new BufferedReader(new InputStreamReader(siteConnector.getErrorStream(),"UTF-8"));
         else
         siteResponseReader = new BufferedReader(new InputStreamReader(siteConnector.getInputStream(),"UTF-8"));
         
        String encoding = siteConnector.getContentEncoding() ;
        if(encoding != null){
        if(encoding.equals("gzip"))
          siteResponseReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(siteConnector.getInputStream())));
        } 
        String response = "";
        
        while ((response = siteResponseReader.readLine()) != null)
          siteSource+=response; 
         siteResponseReader.close(); 
            
         List<String> setCookies = siteConnector.getHeaderFields().get("Set-Cookie");
         
         if(inputCookies != null && setCookies != null){ 
             if(setCookies.size() > 1) //has new keep alive cookies
                inputCookies.clear();  
             inputCookies.addAll(setCookies); 
         } 
         siteConnector.disconnect();     
        }catch(Exception grabPageException){
           grabPageException.printStackTrace();
           siteSource = "ERROR";
        }
        return siteSource;
    }
 
    public String initatePost(String pageToOpen,String userAgent,String postParams,String csrfToken,ArrayList<String> cookies) {

    String postResult = "";
    try {
        siteURL = new URL(pageToOpen); 
        siteConnector = (HttpsURLConnection) siteURL.openConnection(); 
  
        if(cookies != null && cookies.size() > 0){
                 for (String PostCookieString : cookies) {
                     siteConnector.addRequestProperty("Cookie", PostCookieString.split(";", 1)[0]);
	      }
        } 
        siteConnector.setRequestMethod("POST");  
        siteConnector.setRequestProperty("accept"        ,  "application/json, text/javascript, */*; q=0.01"); 
        siteConnector.setRequestProperty("accept-encoding", "gzip, deflate, br");
        siteConnector.setRequestProperty("accept-language", "en-US,en;q=0.9"); 
        
        siteConnector.setRequestProperty("cache-control"     , "no-cache");  
        siteConnector.setRequestProperty("origin",  "https://mewe.com");
        siteConnector.setRequestProperty("referer", "https://mewe.com/myworld");  
        siteConnector.setRequestProperty("content-type","application/json; charset=UTF-8");  
        
        siteConnector.setRequestProperty("user-agent",userAgent);
        siteConnector.setRequestProperty("x-requested-with", "XMLHttpRequest"); 
        siteConnector.setRequestProperty("x-csrf-token", csrfToken);
        
        siteConnector.setConnectTimeout(5000);  
     
        siteConnector.setDefaultUseCaches(false);
        siteConnector.setUseCaches(false);
        siteConnector.setInstanceFollowRedirects(false);
        siteConnector.setDoOutput(true); 
        siteConnector.setDoInput(true); 
        
        sitePrintWriter = new PrintWriter(siteConnector.getOutputStream());
        sitePrintWriter.print(postParams);
        sitePrintWriter.flush();
        
        int responseCode = siteConnector.getResponseCode(); 
        System.out.println(responseCode);
        if(responseCode != 200)
        siteResponseReader = new BufferedReader(new InputStreamReader(siteConnector.getErrorStream(),"UTF-8"));
        else
        siteResponseReader = new BufferedReader(new InputStreamReader(siteConnector.getInputStream(),"UTF-8"));
        
        String encoding = siteConnector.getContentEncoding() ;
        if(encoding != null){
        if(encoding.equals("gzip") && responseCode == 200)
                    siteResponseReader = new BufferedReader( new InputStreamReader(new GZIPInputStream(siteConnector.getInputStream())));
        }
        
        String response = "";
        while ((response = siteResponseReader.readLine()) != null) 
            postResult += "/n" + response;
        
        if(responseCode == 204)
            return "SUCCESS";
    } catch (Exception e) {
        e.printStackTrace();
        return "FAIL";
    }
    finally {
        try {
            if (sitePrintWriter != null) {
                sitePrintWriter.close();
            }
            if (siteResponseReader != null) {
                siteResponseReader.close();
            }
        } catch (Exception PostException) {
            PostException.printStackTrace();
            return "ERROR";
        }
    }
    return postResult;
    }
    
}
