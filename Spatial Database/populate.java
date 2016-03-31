//package database;


import java.sql.*;
import java.util.*;
import java.io.*;
@SuppressWarnings("deprecation")

 public class populate {
 
	 static Connection conn = null;
	 static ResultSet rs = null;
	 static Statement st = null;
	 //static String query;
	 
		public populate() throws SQLException{
			
			connect2database();
		}
		 
		public void read(String one, String two, String three)
		{		
				
					  File fileone = new File (one);
					  File filetwo = new File (two);
					  File filethree = new File (three);
					  FileInputStream fileInput = null;
					  BufferedInputStream bufferInput = null;
					  DataInputStream dataInput = null;
					  try{
							if(fileone != null)
							{
								fileInput = new FileInputStream(fileone);
								bufferInput = new BufferedInputStream(fileInput);
								dataInput = new DataInputStream(bufferInput);
								while (dataInput.available() != 0)
								{	
									String s1 = dataInput.readLine();
									StringTokenizer stoken = new StringTokenizer(s1, ", ");
									String hydrant_id = null;
									int X = 0,Y = 0;		    	 
									hydrant_id = stoken.nextToken();
									X = Integer.parseInt(stoken.nextToken().trim());
									Y = Integer.parseInt(stoken.nextToken().trim());
									this.insert_into_hydrant(hydrant_id,X,Y);			
									
								}
								System.out.println( "fire_hydrants table has been populated \n" );
							}
							
							if(filetwo != null)
							{
							
								fileInput = new FileInputStream(filetwo);
								bufferInput = new BufferedInputStream(fileInput);
								dataInput = new DataInputStream(bufferInput);
								
								while(dataInput.available() != 0)
								{
									
									int a[][] = new int [100][2];
									int i = 0;
									
									String s1 = dataInput.readLine();
									StringTokenizer stoken = new StringTokenizer(s1, ",");
									String bid = null, bname = null;
									int vert = 0;
									bid = stoken.nextToken();
									bname = stoken.nextToken().trim();
									vert = Integer.parseInt(stoken.nextToken().trim());
								  
									while(stoken.hasMoreTokens()) {
											a[i][0] = Integer.parseInt(stoken.nextToken().trim());
											a[i][1] = Integer.parseInt(stoken.nextToken().trim());
											i++;   	
									 }
									 this.insert_building(bid,bname,vert,a);   	
								}
								 System.out.println( "Buildings table has been populated \n" );
									
							}
							
							if( filethree != null )
							{
								fileInput = new FileInputStream(filethree);
								bufferInput = new BufferedInputStream(fileInput);
								dataInput = new DataInputStream(bufferInput);
								
								while(dataInput.available()!=0)
								{
									
								
								String s1 = dataInput.readLine();
								String fname = null;
								fname = s1;
								this.insert_fire(fname);
								}
								System.out.println( "Fire_Buildings table has been populated \n" );
							}
								
					  }
					  catch (FileNotFoundException e) {
					
						  e.printStackTrace();
					  } 
					  
					  catch (IOException e) {
					
						  e.printStackTrace();
					  }
					  catch(NumberFormatException e)
					  {
						 						  e.printStackTrace();
					  }
				}
 
		 public static void connect2database() throws SQLException
		{
			 System.out.println("Looking for the driver ... ");
			 DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
			 String localhost = "dagobah.engr.scu.edu";
			 String port = "1521:db11g";//"1521"			
			 String J_STRING = "jdbc:oracle:thin:@" + localhost + ":" + port;
			 String NAME = "jshah";
			 String PASSWORD = "00001114028";
					
			 try {
				
				 conn = DriverManager.getConnection (J_STRING, NAME, PASSWORD);
				 System.out.println("Connected.");
				 st = conn.createStatement ();
				 st.executeUpdate("delete from fire_buildings");
				 st.executeUpdate("delete from buildings");
				 st.executeUpdate("delete from fire_hydrants");
				 //System.out.println("All data deleted from all the tables");
				 
			 } 
			 catch (SQLException sqlEx) {
				
				 sqlEx.printStackTrace ();
			 } 
		 }
		 
		 public void insert_into_hydrant(String hydrant_id, int X ,int Y)
		{
			try
			 {
				String hydrant_query=null;
				
					if(!hydrant_id.equals("null")) 
					{
						hydrant_query="insert into fire_hydrants values ('"+hydrant_id+"', mdsys.sdo_geometry(2001,null,mdsys.sdo_point_type("+X+","+Y+",null),null,null))";
					}
					st.executeUpdate(hydrant_query);
			 }
			 catch( Exception e )
			 {  
			
				e.printStackTrace();
			 }
		}
		 public void insert_fire(String fname)
			{
				try
				{
					
					String queryy = null;
					if(!fname.equals("null"))
					{
					
						queryy = "insert into fire_buildings values ('"+fname+"')";
						//System.out.println(queryy);
					}
					st.executeUpdate(queryy);
				}
				catch( Exception e )
				{ 
				
					e.printStackTrace();
				}
			}
		
		  
		 public void insert_building(String bid, String bname, int vertices, int a[][])
		{
				try
				{
					String building_query=null;
					int i = 0;
					int x1 = a[0][0], y1 = a[0][1];
					if(!bid.equals("null")) 
					{
						
						building_query="insert into buildings values ('"+bid+"','"+bname+"','"+vertices+"'";
						building_query+=",mdsys.sdo_geometry(2003,null,null,sdo_elem_info_array(1,1003,1),sdo_ordinate_array(";
						while ( i < vertices )
						{
							building_query+=a[i][0];
							building_query+=",";
							building_query+=a[i][1];
							building_query+=", ";
							i++;
						}
						building_query+=x1;
						building_query+=",";
						building_query+=y1;
						building_query+=")))";
					}
					st.executeUpdate(building_query);

			 }
			 catch( Exception e )
			 { 
				
				 e.printStackTrace();
			 }
		}



public static void main (String [] args)throws SQLException  {
	 
	  populate pop= new populate();
	  

	  String one,two,three;
	  one = args[0];
	  two = args[1];
	  three = args[2];
	  System.out.println(one+two+three);
	  //pop.read(one,two,three);
	 
 
 }
 }

