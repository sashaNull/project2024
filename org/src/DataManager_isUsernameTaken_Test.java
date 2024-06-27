
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_isUsernameTaken_Test {
    
    @Test (expected = IllegalArgumentException.class)
    public void testNullUsername()
    {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.isUsernameTaken(null);
    }
    
    @Test (expected = IllegalStateException.class)
    public void testNullResponse()
    {
        DataManager dm = new DataManager(new WebClient("localhost", 3001){
            @Override
            public String makeRequest (String resource, Map<String, Object> queryParams){
                return null;
            }
        });
        
        dm.isUsernameTaken("upenn");
    }

    @Test
    public void testTakenUserName()
    {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest (String resource, Map<String, Object> queryParams){
                return "{\"status\":\"taken\"}";
            }

        });

        assertEquals(true, dm.isUsernameTaken("upenn"));
    }

    @Test
    public void testAvailableUserName()
    {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest (String resource, Map<String, Object> queryParams){
                return "{\"status\":\"available\"}";
            }

        });

        assertEquals(false, dm.isUsernameTaken("upenn"));
    }

	@Test(expected = IllegalStateException.class)
    public void testErrorStatus()
    {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest (String resource, Map<String, Object> queryParams){
                return "{\"status\":\"error\"}";
            }

        });
        dm.isUsernameTaken("upenn");
    }

	@Test(expected = IllegalStateException.class)
	public void testInvalidJsonResponse() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "Invalid JSON response";
			}
		});

        dm.isUsernameTaken("upenn");
	}
    
    

}