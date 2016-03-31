//package database;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.lang.*;
import java.lang.reflect.Array;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.Graphics;
import java.io.*;
import java.sql.Connection;




public class hw3 extends JFrame {

	private JPanel contentPane;
	private static JLabel labelimage;
	private static JTextArea textAreaQuery;
	private static Graphics g;
	private JTextField mouseCoordinates;
	static Connection conn = null;
	 static ResultSet rs = null;
	 static ResultSet rs1 = null;
	 static Statement stmt = null;
	 static String query;
	 private int leftClickCounter = 0;
	 private int xpoints[] = new int[40];
	 private int ypoints[] = new int[40];
	 private int buildcount_bc = 0;
	 private int xpt_store[][] = new int[400][400];
	 private int ypt_store[][] = new int[400][400];
	 private String pointstring = null;
	 private static int queryc = 0;
	 private static int rightClick_flag = 0;

	  //Launch application
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					hw3 hw = new hw3();
					hw.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void drawrangeline( int x ,int y , int r , Graphics g)
	{
		g.setColor(Color.RED);
		g.fillRect(x-r/2, y-r/2, r, r);		
		
	}
	
	public static void drawthehydrant( int x ,int y , int r , Graphics g)
	{
		g.setColor(Color.GREEN);
		g.fillRect(x-r/2, y-r/2, r, r);
		
	}
	 public static void connect2database() throws SQLException
	 {
	 	 System.out.println("Looking for Oracle's jdbc-odbc driver ... ");
	 	 DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
	 	 System.out.println("Loaded.");
		         String localhost = "dagobah.engr.scu.edu";
			 String port = "1521:db11g";//"1521"			
			 String JDBC_STRING = "jdbc:oracle:thin:@" + localhost + ":" + port;
			 String USER_NAME = "jshah";
			 String PASSWD = "00001114028";
	 	 try {
	 		
	 		 conn = DriverManager.getConnection (JDBC_STRING, USER_NAME, PASSWD);
	 		 System.out.println("Connected");
	 		 stmt = conn.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
	  		 
	 	 } 
	 	 catch (SQLException sqlEx) {
	 		 sqlEx.printStackTrace ();
	 	 } 
	  }
      
	  
	 public static void hydrantsquery() throws SQLException
	 {
		 
		 int X,Y;
		 String hydrantQuery="SELECT fh.hydrant_location.sdo_point.x, fh.hydrant_location.sdo_point.y FROM fire_hydrants fh";
		 queryc++;
		 textAreaQuery.append("Query"+queryc+" :"+hydrantQuery+'\n');
		 try 
		 {
				rs=stmt.executeQuery(hydrantQuery);
				while(rs.next())
				 { 
					X=Integer.parseInt(rs.getString(1));
					Y=Integer.parseInt(rs.getString(2));
				    
					drawthehydrant(X, Y, 10, g);
				 }
		 }
		 catch (SQLException e1) 
		 {
						e1.printStackTrace();
		 }
	 }
	 
	 public static void buildingsquery() throws SQLException
	 {
		 String buildQuery="SELECT b.building_name,b.vertices,t.X, t.Y FROM  buildings b,TABLE(SDO_UTIL.GETVERTICES(b.build_location)) t";
		 String name=null;
		 int verts,k;
		 int xarray[]= new int[400];
		 int yarray[]= new int[400];
		 queryc++;
		 textAreaQuery.append("Query"+queryc+" :"+buildQuery+'\n');
		 
		 try 
		 {
				rs=stmt.executeQuery(buildQuery);
				while(rs.next())
				 { 
					k=0;
					name=rs.getString(1);
					verts=rs.getInt(2);
					xarray[k]=rs.getInt(3);
					yarray[k]=rs.getInt(4);
					k++;
					while(rs.next() && rs.getString(1).compareTo(name)==0)
					{
						xarray[k]=rs.getInt(3);
						yarray[k]=rs.getInt(4);
						k++;
											
					}
					g.setColor(Color.yellow);
					g.drawPolyline(xarray, yarray, verts+1);
					rs.previous();
				 }
		 }
		 catch (SQLException e1) 
		 {
						e1.printStackTrace();
		 }
	}
				
				
	 
	public static void firesquery() throws SQLException
	{
		String fireQuery="SELECT b.building_name,b.vertices,t.X, t.Y FROM  buildings b,TABLE(SDO_UTIL.GETVERTICES(b.build_location)) t, fire_buildings f WHERE f.fire_build_name = b.building_name";
		int verts, k;
		String name=null;
		int xarray[]= new int[400];
		int yarray[]= new int[400];
		
		 queryc++;
		 textAreaQuery.append("Query"+queryc+" :"+fireQuery+'\n');
		 
		 try 
		 {
				rs=stmt.executeQuery(fireQuery);
				while(rs.next())
				 { 
					k=0;
					name=rs.getString(1);
					verts=rs.getInt(2);
					xarray[k]=rs.getInt(3);
					yarray[k]=rs.getInt(4);
					k++;
					while(rs.next() && rs.getString(1).compareTo(name)==0)
					{
						xarray[k]=rs.getInt(3);
						yarray[k]=rs.getInt(4);
						k++;
											
					}
					g.setColor(Color.red);
					g.drawPolyline(xarray, yarray, verts+1);
					rs.previous();	
				 }
		 }
		 catch (SQLException e1) 
		 {
						e1.printStackTrace();
		 }
	}

	public static void close_buildings_query() throws SQLException
	{
		String close_building_query = "SELECT b1.building_name,b1.vertices,t.X, t.Y FROM  buildings b1, buildings b2, TABLE(SDO_UTIL.GETVERTICES(b1.build_location)) t, fire_buildings f WHERE b2.building_name = f.fire_build_name AND SDO_WITHIN_DISTANCE(b1.build_location, b2.build_location, 'distance = 100') = 'TRUE'";
		int verts,k;
		String name=null;
		int xarray[]= new int[400];
		int yarray[]= new int[400];
	
		 queryc++;
		 textAreaQuery.append("Query"+queryc+" :"+close_building_query+'\n');
		 
		 try 
		 {
				rs=stmt.executeQuery(close_building_query);
				while(rs.next())
				 { 
					k=0;
					name=rs.getString(1);
					verts=rs.getInt(2);
					xarray[k]=rs.getInt(3);
					yarray[k]=rs.getInt(4);
					k++;
					while(rs.next() && rs.getString(1).compareTo(name)==0)
					{
						xarray[k]=rs.getInt(3);
						yarray[k]=rs.getInt(4);
						k++;
					}
					g.setColor(Color.yellow);
					g.drawPolyline(xarray, yarray, verts+1);
					rs.previous();	
				 }
		 }
		 catch (SQLException e1) 
		 {
						e1.printStackTrace();
		 }
	}

	public void get_building(int x1, int x2) throws SQLException
	{
		int b1 = x1;
		int b2 = x2;
		String pointstring = null;
		pointstring = "sdo_geometry(2001,null,mdsys.sdo_point_type("+b1+","+b2+",null),null,null)";
		String get_bquery = "SELECT b.building_name,b.vertices,t.X, t.Y FROM  buildings b, TABLE(SDO_UTIL.GETVERTICES(b.build_location)) t WHERE SDO_ANYINTERACT(b.build_location,"+pointstring+") = 'TRUE'";
		int xarray[]= new int[400];
		int yarray[]= new int[400];
		int verts,k;
		String name=null;
		queryc++;
		textAreaQuery.append("Query"+queryc+" :"+get_bquery+'\n');
		 
		 try 
		 {
				rs=stmt.executeQuery(get_bquery);
				while(rs.next())
				 { 
					k=0;
					name=rs.getString(1);
					verts=rs.getInt(2);
					xarray[k]=rs.getInt(3);
					xpt_store[buildcount_bc][k]= rs.getInt(3);
					yarray[k]=rs.getInt(4);
					ypt_store[buildcount_bc][k] = rs.getInt(4);
					k++;
					while(rs.next() && rs.getString(1).compareTo(name)==0)
					{
						xarray[k]=rs.getInt(3);
						xpt_store[buildcount_bc][k]= rs.getInt(3);
						yarray[k]=rs.getInt(4);
						ypt_store[buildcount_bc][k] = rs.getInt(4);
						k++;
											
					}
					g.setColor(Color.red);
					g.drawPolyline(xarray, yarray, verts+1);
					rs.previous();	
					buildcount_bc++;	
				 }
		 }
		 catch (SQLException e1) 
		 {
						e1.printStackTrace();
		 }
	}
	
	public void getclose_hydrant() throws SQLException
	{
		int p,i,j;
		int X, Y;
		for ( p = 0 ; p < buildcount_bc ; p++ )
		{
				int q=0;
				String polystring="sdo_geometry(2003,null,null,sdo_elem_info_array(1,1003,1),sdo_ordinate_array(";
				polystring+=xpt_store[p][q];
		     	polystring+=",";
		     	polystring+=ypt_store[p][q];
		     	while ( ypt_store[p][q] != 0  )
				{
					polystring+=", ";
					polystring+=xpt_store[p][q];
		     		polystring+=",";
		     		polystring+=ypt_store[p][q];
					q++;
		     	}
				polystring+="))";
				String get_hyd_query = "SELECT fh.hydrant_location.sdo_point.x, fh.hydrant_location.sdo_point.y FROM fire_hydrants fh WHERE SDO_NN(fh.hydrant_location,"+polystring+",'sdo_num_res = 1') = 'TRUE'";
				queryc++;
				textAreaQuery.append("Query"+queryc+" :"+get_hyd_query+'\n');
				try 
				{
					connect2database();
					rs=stmt.executeQuery(get_hyd_query);
					while(rs.next())
					{ 
					
					X=Integer.parseInt(rs.getString(1));
					Y=Integer.parseInt(rs.getString(2));
				    
					drawthehydrant(X, Y, 10, g);
					 }
				}
				catch (SQLException e1) 
				{
						e1.printStackTrace();
				}
			}
			for ( i = 0 ; i < 400 ; i++ )
			{
				for ( j = 0 ; j < 400 ; j++ )
				{
					xpt_store[i][j] = 0;
					ypt_store[i][j] = 0;
				}
			}
	}
	
	
	public hw3() {
		setTitle("Jay Shah, SCU ID : 00001114028");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 1000, 750);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final JCheckBox check_Building = new JCheckBox("Buildings");
		check_Building.setBounds(826, 52, 175, 23);
		contentPane.add(check_Building);
		
		
		final JCheckBox check_fire = new JCheckBox("Buildings on Fire");
		check_fire.setBounds(826, 83, 175, 23);
		contentPane.add(check_fire);
		
		final JCheckBox check_hydrant = new JCheckBox("Hydrants");
		check_hydrant.setBounds(826, 118, 175, 23);
		contentPane.add(check_hydrant);
		
		JLabel lblActiveFeatureType = new JLabel("Active Feature Type");
		lblActiveFeatureType.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblActiveFeatureType.setBounds(830, 11, 144, 34);
		contentPane.add(lblActiveFeatureType);
		
		JLabel lblQuery = new JLabel("Query");
		lblQuery.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblQuery.setBounds(830, 169, 95, 23);
		contentPane.add(lblQuery);
		
		final JRadioButton rdbtnWholeRegion = new JRadioButton("Whole Region");
		rdbtnWholeRegion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if("Whole Region".equals(e.getActionCommand()))
				{
					labelimage.setIcon(new ImageIcon("map.jpg"));
					leftClickCounter = 0;
					
				}
			}
		});
		rdbtnWholeRegion.setBounds(826, 208, 175, 23);
		contentPane.add(rdbtnWholeRegion);
		
		final JRadioButton rdbtnRangeQuery = new JRadioButton("Range Query");
		rdbtnRangeQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if("Range Query".equals(e.getActionCommand()))
				{
					labelimage.setIcon(new ImageIcon("map.jpg"));
					leftClickCounter = 0;
					
				}
			}
		});
		rdbtnRangeQuery.setBounds(826, 241, 175, 23);
		contentPane.add(rdbtnRangeQuery);
		
		final JRadioButton radiobuttonCloseFireQuery = new JRadioButton("Find Neighbor Buildings");
		radiobuttonCloseFireQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if("Find Closest Building".equals(e.getActionCommand()))
				{
					labelimage.setIcon(new ImageIcon("map.jpg"));
					leftClickCounter = 0;
					
				}
			}
		});
		radiobuttonCloseFireQuery.setBounds(826, 274, 175, 23);
		contentPane.add(radiobuttonCloseFireQuery);
		
		final JRadioButton rdbtnCloseHydrant = new JRadioButton("Find Closest Fire Hydrants");
		rdbtnCloseHydrant.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if("Find Closest Hydrant".equals(e.getActionCommand()))
				{
					labelimage.setIcon(new ImageIcon("map.jpg"));
					
				}
			}
		});
		rdbtnCloseHydrant.setBounds(826, 308, 175, 23);
		contentPane.add(rdbtnCloseHydrant);	
		
		ButtonGroup bg= new ButtonGroup();
		bg.add(rdbtnCloseHydrant);
		bg.add(radiobuttonCloseFireQuery);
		bg.add(rdbtnRangeQuery);
		bg.add(rdbtnWholeRegion);
		
		labelimage = new JLabel("");
		labelimage.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(rdbtnRangeQuery.isSelected())
				{
					
					int a1 = e.getX();
					int a2 = e.getY();
					
					if(SwingUtilities.isLeftMouseButton(e))
					{	
					g = labelimage.getGraphics();
					drawrangeline(a1, a2, 5, g);
					xpoints[leftClickCounter]=a1;
					ypoints[leftClickCounter]=a2;
					if(leftClickCounter>0)
					{
						g.drawLine(xpoints[leftClickCounter], ypoints[leftClickCounter], xpoints[leftClickCounter-1], ypoints[leftClickCounter-1]);
					}
					leftClickCounter++;
					}
					if(SwingUtilities.isRightMouseButton(e))
					{
						xpoints[leftClickCounter]=xpoints[0];
						ypoints[leftClickCounter]=ypoints[0];
						g.drawPolyline(xpoints, ypoints, leftClickCounter+1);
						rightClick_flag=1;
						
						
					}
					
				}
			
				if(rdbtnCloseHydrant.isSelected())
				{
					
						
						int a1 = e.getX();
						int a2 = e.getY();
						try {
							connect2database();
							g = labelimage.getGraphics();
							get_building(a1,a2);
			           		
						} catch (SQLException e1) {
							e1.printStackTrace();
						}  
						} 
				}
				
		});
		labelimage.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent arg0) {
				int a1 = arg0.getX();
				int a2 = arg0.getY();
				String s = Integer.toString(a1) +','+ Integer.toString(a2);
				mouseCoordinates.setText(s);
				
			}
		});
		labelimage.setIcon(new ImageIcon("map.jpg"));
		labelimage.setBounds(0, 0, 820, 580);
		contentPane.add(labelimage);
		
		
		mouseCoordinates = new JTextField();
		mouseCoordinates.setText("Mouse Coordinates");
		mouseCoordinates.setBounds(150, 595, 148, 20);
		contentPane.add(mouseCoordinates);
		mouseCoordinates.setColumns(10);
		
		JTextArea txtrMouseCoordinates = new JTextArea();
		txtrMouseCoordinates.setText("Mouse Coordinates");
		txtrMouseCoordinates.setBounds(10, 595, 146, 22);
		contentPane.add(txtrMouseCoordinates);
		

		JButton btnSubmit = new JButton("Submit Query");
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(rdbtnWholeRegion.isSelected())
				{	
					try {
					connect2database();
					g = labelimage.getGraphics();
					
					if(check_Building.isSelected())
						buildingsquery();
					if(check_hydrant.isSelected())
						hydrantsquery();
					if(check_fire.isSelected())
						firesquery();
					if(!check_Building.isSelected() && !check_fire.isSelected() && !check_hydrant.isSelected())
						labelimage.setIcon(new ImageIcon("map.jpg"));
					
					
					}
					catch (SQLException e1) {
					e1.printStackTrace();
					}
				}
				if(rdbtnRangeQuery.isSelected() && rightClick_flag==1)
				{
					try {
						connect2database();
						g = labelimage.getGraphics();
						int k,X,Y,rad,ver;
						String name;
						int xarrayy1[]= new int[400];
						int yarrayy1[]= new int[400];
						String polystring="sdo_geometry(2003,null,null,sdo_elem_info_array(1,1003,1),sdo_ordinate_array(";
		     			for(int i=0;i<leftClickCounter;i++)
						{
		     				polystring+=xpoints[i];
		     				polystring+=",";
		     				polystring+=ypoints[i];
		     				polystring+=", ";
		     			}
		     			polystring+=xpoints[0];polystring+=",";polystring+=ypoints[0];polystring+="))";
		     			
						if (check_hydrant.isSelected())
						{
							String query="Select h.hydrant_location.sdo_point.x, h.hydrant_location.sdo_point.y FROM fire_hydrants h WHERE sdo_relate(h.hydrant_location,";
							query+=polystring;
							query+=",'mask=anyinteract') = 'TRUE'";
							queryc++;
							textAreaQuery.append("Query"+queryc+" :"+query+'\n');
							try 
							{
								connect2database();
								rs=stmt.executeQuery(query);
								while(rs.next())
								{ 
	           					
									X=Integer.parseInt(rs.getString(1));
									Y=Integer.parseInt(rs.getString(2));
	           				    
									drawthehydrant(X, Y, 10, g);
	           					
								}
							}
							catch (SQLException e1) 
							{
	           						e1.printStackTrace();
							}
						}	
						
						if (check_Building.isSelected())
							 {	
								String query2="SELECT b.building_name,b.vertices,t.X, t.Y FROM  buildings b,TABLE(SDO_UTIL.GETVERTICES(b.build_location)) t WHERE sdo_relate(b.build_location,";
								query2+=polystring;
								query2+=",'mask=anyinteract') = 'TRUE'";
								queryc++;
								textAreaQuery.append("Query"+queryc+" :"+query2+'\n');
								try 
								{
									connect2database();
								  rs=stmt.executeQuery(query2);
								  while(rs.next())
								  { 
									k=0;
									name=rs.getString(1);
									ver=rs.getInt(2);
									xarrayy1[k]=rs.getInt(3);
									yarrayy1[k]=rs.getInt(4);
									k++;
									while(rs.next() && rs.getString(1).compareTo(name)==0)
									{
										xarrayy1[k]=rs.getInt(3);
										yarrayy1[k]=rs.getInt(4);
										k++;
															
									}
									g.setColor(Color.yellow);
									g.drawPolyline(xarrayy1, yarrayy1, ver+1);
									rs.previous();
									
								 }	
								}
								catch (SQLException e1) 
								{
											e1.printStackTrace();
								}
						}
						if(check_fire.isSelected())
						{
							String query="SELECT b.building_name,b.vertices,t.X, t.Y FROM  buildings b, fire_buildings f, TABLE(SDO_UTIL.GETVERTICES(b.build_location)) t WHERE b.building_name = f.fire_build_name AND sdo_relate(b.build_location,";
							query+=polystring;
							query+=",'mask=anyinteract') = 'TRUE'";
							queryc++;
							textAreaQuery.append("Query"+queryc+" :"+query+'\n');
							 try 
								 {
										connect2database();
										rs=stmt.executeQuery(query);
										while(rs.next())
										 { 
											k=0;
											name=rs.getString(1);
											ver=rs.getInt(2);
											xarrayy1[k]=rs.getInt(3);
											yarrayy1[k]=rs.getInt(4);
											k++;
											while(rs.next() && rs.getString(1).compareTo(name)==0)
											{
												xarrayy1[k]=rs.getInt(3);
												yarrayy1[k]=rs.getInt(4);
												k++;
																	
											}
											g.setColor(Color.red);
											g.drawPolyline(xarrayy1, yarrayy1, ver+1);
											rs.previous();
												
										 }
								 }
								 catch (SQLException e1) 
								 {
												e1.printStackTrace();
								 }
								}
				}
				catch (SQLException e1) {
						e1.printStackTrace();
				}
				}
				if(radiobuttonCloseFireQuery.isSelected())
				{
					try {
						
						connect2database();
						g = labelimage.getGraphics();
						close_buildings_query();
						firesquery();
						
						}
				catch (SQLException e1) {
						e1.printStackTrace();
				}
				}
				
				if(rdbtnCloseHydrant.isSelected())
				{
					try{
					
						
						g = labelimage.getGraphics();
						getclose_hydrant();
						buildcount_bc=0;
						
					}
					catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
				
			
		});
		btnSubmit.setBounds(822, 557, 152, 23);
		contentPane.add(btnSubmit);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 620, 964, 81);
		contentPane.add(scrollPane);
		
		textAreaQuery = new JTextArea();
		scrollPane.setViewportView(textAreaQuery);
		textAreaQuery.setText("Query Text \n");
		
	}
}

