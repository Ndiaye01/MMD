package org.group.mmd;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MmdApplicationTests {

	/*@Test
	public void contextLoads() {
	}*/
	
public static final String REST_SERVICE_URI = "http://localhost:8076/";
    
    /* GET */
    @SuppressWarnings("unchecked")
    @Test
   // @Ignore
    public void listAllUsers(){
        System.out.println("Testing listAllUsers API-----------");
          
        RestTemplate restTemplate = new RestTemplate();
        List<LinkedHashMap<String, Object>> imagesMap = restTemplate.getForObject(REST_SERVICE_URI+"/images", List.class);
          System.out.println("printing here...");
        if(imagesMap!=null){
            for(LinkedHashMap<String, Object> map : imagesMap){
                System.out.println(" FileImage ="+imagesMap.get(1));
            }
        }else{
            System.out.println("No images exist----------");
        }
    }
	

}
