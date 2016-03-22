/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package locationdb;

import java.sql.DriverManager;
import java.sql.SQLException;
import static locationdb.LocationDB.connection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Samra
 */
public class LocationDBTest {
    
    public LocationDBTest() {
    }
 

    /**
     * Test of findLoc method, of class LocationDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testFindLoc() throws Exception {
         try{
	
            Class.forName("com.mysql.jdbc.Driver");
	} 
	catch (ClassNotFoundException e){

            System.out.println("JDBC Driver not found!");
            return;
	}
        
       try {
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/citylocation","root","");
       }
       catch (SQLException e) {
            System.out.println("Cannot Connect to database");
            return;
        }
        System.out.println("findLoc");
        String city = "Cairo";
        float[] expResult = {(float)30.0771,(float)31.2859};
        float[] result = LocationDB.findLoc(city);
        assertEquals(expResult[0], result[0], 0);
        assertEquals(expResult[1], result[1], 0);
    }

    /**
     * Test of findNearBy method, of class LocationDB.
     * @throws java.lang.Exception
     */
    @Test
    public void testFindNearBy() throws Exception {
         try{
	
            Class.forName("com.mysql.jdbc.Driver");
	} 
	catch (ClassNotFoundException e){

            System.out.println("JDBC Driver not found!");
            return;
	}
        
       try {
		connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/citylocation","root","");
       }
       catch (SQLException e) {
            System.out.println("Cannot Connect to database");
            return;
        }
        System.out.println("findNearBy");
        String city = "cairo";
        String lat = "";
        String lon = "";
        int n = 4;
        String[] expResult = {"Mound City","Mounds","Charleston","Scott City"};
        String[] result = LocationDB.findNearBy(city, lat, lon, n);
        assertArrayEquals(expResult, result);
        
        city="";
        lat = "37.0404";
        lon = "-89.2008";
        String[] expResult2 = {"Cairo","Mound City","Mounds","Charleston"};
        String[] result2 = LocationDB.findNearBy(city, lat, lon, n);
        assertArrayEquals(expResult2, result2);

    }
    
}
