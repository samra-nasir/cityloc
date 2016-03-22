/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package locationdb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author Samra
 */
public class LocationDB {

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     */
    public static Connection connection;
    public static void main(String[] args) throws SQLException, FileNotFoundException {
        // TODO code application logic here
        Scanner s = new Scanner(System.in);
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
       // insertData();
       System.out.println("Select any option from below:\n1. Check Lat/Long\n2. Check nearby Cities");
       int opt = s.nextInt();
       if(opt==1){
           System.out.println("Enter City Name:");
           String city = s.next();
           float a[] = findLoc(city); 
           for(int i=0;i<a.length;i++){
               if(i%2==0)
                    System.out.print("Latitude = " + a[i]);
               else
                    System.out.println("...Longitude = " + a[i]);
           }
       }
       
       else if(opt==2){
            System.out.println("Enter a number to select the option:\n1.Enter city name\n2.Enter Lat/Long");
            int opt1 = s.nextInt();
            String city="";
            String lat="",lon="";
            if(opt1==1){
                System.out.println("Enter City Name to find nearby places:");
                city = s.next();
            }
            else if(opt1==2){
                System.out.println("Enter Latitude:");
                lat = s.next();
                System.out.println("Enter Longitude:");
                lon = s.next();
             }
            else{
                System.out.println("Invalid Option Selected");
                return;
            }
            System.out.println("Enter Number of Neighbours to search:");
            int n = s.nextInt();
            String a[] =findNearBy(city,lat,lon,n);
            for(int i=0;i<a.length;i++){
                System.out.println(a[i]);
            
            }
       }

        
    }
    
    static void insertData() throws SQLException{
        
        PreparedStatement prep_statement;
        
     
        String sql = "Insert into cityloc(locid,country,region,city,postalcode,latitude,longitude) values(?,?,?,?,?,?,?)";
	prep_statement = connection.prepareStatement(sql);

       
       // Inserting data in database
       try{
          
           String a[];
           String place = "C:\\Users\\Samra\\Desktop\\GeoLiteCity-Location.csv";
           BufferedReader br = new BufferedReader(new FileReader(place));
           String line;
           br.readLine();
           br.readLine();
            while( (line=br.readLine()) != null){		// loop for appending the textfield
                
                
                a = line.split(",");
                for(int i =0;i<7;i++)
                    a[i]= a[i].replace("\"","");

                prep_statement.setInt(1,Integer.parseInt(a[0]));
                prep_statement.setString(2, a[1]);
                prep_statement.setString(3, a[2]);
                prep_statement.setString(4, a[3]);
                prep_statement.setString(5, a[4]);
                prep_statement.setFloat(6, Float.parseFloat(a[5]));
                prep_statement.setFloat(7, Float.parseFloat(a[6]));
                System.out.println(a[3]);
                prep_statement.executeUpdate();
                
                }
            }
	catch(Exception e){
            System.out.println("Cannot Read File");
	}
    }
    public static float[] findLoc(String city) throws SQLException{

         int flag = 0;
         int c =0;
         String sql ="SELECT * from cityloc where city = '" + city + "'";
         Statement prep_statement = connection.createStatement();
         ResultSet rs = prep_statement.executeQuery(sql);
         rs.last();
        int rows = rs.getRow();
        rs.beforeFirst();
        float a[] = new float[rows*2];
         
         while (rs.next()){
            float lat = rs.getFloat("latitude");
            float lon = rs.getFloat("longitude");
            
            /*System.out.print("Latitude = " + lat);
            System.out.println("...Longitude = " + lon);*/
            a[c]= lat;
            c++;
            a[c]=lon;
            c++;
            flag += 1;
         }
         if(flag>0)
             System.out.println(flag+ " result(s) found.");
         
         else if(flag==0)
             System.out.println("No result Found");

         return a;
        
    }
    public static String[] findNearBy(String city, String lat, String lon, int n) throws SQLException{
         String a[] = new String[n];
         int x=0;
         if(!city.equals("")){
             String q = "SELECT * from cityloc where city = '"+city+"'";
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(q);
             while(rs.next()){
                 lat = rs.getString("latitude");
                 lon = rs.getString("longitude");
             }
             n++;
         }
         
         String b = "SELECT locid, city,latitude, longitude FROM (SELECT z.locid,z.city,z.latitude, z.longitude,p.radius,p.distance_unit* DEGREES(ACOS(COS(RADIANS(p.latpoint)) * COS(RADIANS(z.latitude)) * COS(RADIANS(p.longpoint - z.longitude)) + SIN(RADIANS(p.latpoint)) * SIN(RADIANS(z.latitude)))) AS distance FROM cityloc AS z JOIN (SELECT  "+lat+"  AS latpoint,  "+lon+" AS longpoint,50000000.0 AS radius,      111.045 AS distance_unit) AS p ON 1=1 WHERE z.latitude BETWEEN p.latpoint  - (p.radius / p.distance_unit) AND p.latpoint  + (p.radius / p.distance_unit) AND z.longitude BETWEEN p.longpoint - (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint)))) AND p.longpoint + (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint))))) AS d WHERE distance <= radius ORDER BY distance LIMIT "+n;
         String c;
         Statement statement = connection.createStatement();
         ResultSet rs = statement.executeQuery(b);
         while(rs.next()){
            
            c=rs.getString("city");
            if(!c.equalsIgnoreCase(city)){
                a[x]=c;
                x++;
            
           /* System.out.print(rs.getString("locid"));
            System.out.print("...");
            System.out.print(c);
            System.out.print("...");
            System.out.print(rs.getString("latitude"));
            System.out.print("...");
            System.out.println(rs.getString("longitude"));*/
            }
         }
        
         return a;
     }


    
}
