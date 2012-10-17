import java.net.URI;
import java.net.URISyntaxException;


public class UriEqualsTest {
  public static void main(String[] args) throws URISyntaxException {
    
    String url = "http://www.tokenizer.ca";
    
    URI uri1 = new URI(url);
    URI uri2 = new URI(url+":80");
    
    
    System.out.println(uri1.equals(uri2));
    
    
  }
}
