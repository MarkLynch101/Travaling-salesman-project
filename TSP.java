//imports
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//main class
public class TSP {

	public static void main(String[] args) {
		//diplay gui
		GUI gui = new GUI();
	}
	
}

//actionlistener for button press
class buttonActionListener implements ActionListener{
	
	//set up veriables 
	private Consumer<String> consumer;
	private JTextArea input;
	
	//constructor sets veriable values 
    public buttonActionListener(Consumer<String> consumer, JTextArea input){
    	this.consumer = consumer;
    	this.input = input;
    }
    
    //perform action after button press calculate the best order for delvery
    public void actionPerformed(ActionEvent e) {
        
    	//initialise output string
    	String output = "";
    	
    	//get input as string text
    	String text = input.getText();
    	
        //input validation 
        if(text.matches("([0-9]+,.+,[0-9]+,[-.0-9]+,[-.0-9]+\\n?){2,}") == false) {
        	output = "invalid entrie please insure the following:\n"
        			+ "- you must enter more that one address\n"
        			+ "- each adress must contain:\n"
        			+ "	- order number,\n"
        			+ "	- address,\n"
        			+ "	- minutes,\n"
        			+ "	- latitude,\n"
        			+ "	- longitude\n"
        			+ "- they must be presented in this order\n"
        			+ "- and separated by a comma.\n";
        }
        else {
        	//split the string up on new lines 
        	String[] addresses = text.split("\\r?\\n");
        	//new 2d array to hold adress info as a table 
            String[][] addressTable = new String[addresses.length][5];
            
            //loop to put adress info in table
            for(int i = 0; i < addresses.length; i++) {
            	
            	String[] temp = addresses[i].split(",");
            	
            	addressTable[i][0] = temp[0];
            	addressTable[i][1] = temp[1];
            	addressTable[i][2] = temp[2];
            	addressTable[i][3] = temp[3];
            	addressTable[i][4] = temp[4];
            }
            
            System.out.println("inputted order angery mins: " + findAngeryMins(addressTable));
            
            //simalir table for holding the souution 
            String[][] solutionTable = new String[addresses.length][5];
            
           
            
            //set up curent address
            int curentAddress = 0;
            
            //put starting address in the solution table
            solutionTable[0][0] = addressTable[0][0];
            solutionTable[0][1] = addressTable[0][1];
            solutionTable[0][2] = addressTable[0][2];
            solutionTable[0][3] = addressTable[0][3];
            solutionTable[0][4] = addressTable[0][4];
            
            //remove starting address from the address table
            addressTable[0][0] = "0";
        	addressTable[0][1] = "0";
        	addressTable[0][2] = "0";
        	addressTable[0][3] = "0";
        	addressTable[0][4] = "0";
        	
        	//loop through the rest of the addresses
        	for(int i = 1; i < addresses.length; i++) {
        		
        		double smalldist = 999999999.9999999;
                int smalldistaddress = 0;
        		//loop through all adresses and find smallest distence
        		for (int j = 1; j < addresses.length; j++) {
        			
        				double checkdist = findDistance(Double.valueOf(addressTable[curentAddress][3]), Double.valueOf(addressTable[curentAddress][4]), Double.valueOf(addressTable[j][3]), Double.valueOf(addressTable[j][4]));

        				if((addressTable[j][1].compareTo("0") != 0) && (checkdist < smalldist)) {
        			
        				smalldist = checkdist;
        				smalldistaddress = j;
        			}
        		}
        		//add address to solution
        		solutionTable[i][0] = addressTable[smalldistaddress][0];
        		solutionTable[i][1] = addressTable[smalldistaddress][1];
        		solutionTable[i][2] = addressTable[smalldistaddress][2];
        		solutionTable[i][3] = addressTable[smalldistaddress][3];
        		solutionTable[i][4] = addressTable[smalldistaddress][4];
        		//set new curent address
        		curentAddress = smalldistaddress;
        		//remove address from the address table
        		addressTable[smalldistaddress][0] = "0";
        		addressTable[smalldistaddress][1] = "0";
        		addressTable[smalldistaddress][2] = "0";
        		addressTable[smalldistaddress][3] = "0";
        		addressTable[smalldistaddress][4] = "0";
        	}
            
        	//calculate angery mins
        	System.out.println("solution angery mins: " + findAngeryMins(solutionTable));
        	
        	
            //convert soution table to output string 
        	String solution = "";
        	for (int i = 0; i < addresses.length; i++) {
        		solution = solution + solutionTable[i][0] + "," + solutionTable[i][1] + "," + solutionTable[i][2] + "," + solutionTable[i][3] + "," + solutionTable[i][4] + "\n";
        	}
           
        	output = solution;
        }
        
        //return the output to be displayed
        consumer.accept(output);
    }
    
    //method to find distence between long lat from lab 8
    public double findDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
		
		//if points are the same return 0 distance
		if ((latitude1 == latitude2) && (longitude1 == longitude2)) return 0;
		
		//calculate distance using the ‘haversine’ formula
		double theta = longitude1 - longitude2;
		double distance = Math.sin(Math.toRadians(latitude1)) * Math.sin(Math.toRadians(latitude2)) + Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) * Math.cos(Math.toRadians(theta));
		distance = Math.acos(distance);
		distance = Math.toDegrees(distance);
		distance = distance * 60 * 1.1515;//miles 
		distance = distance * 1.609344;//km
		return distance;
	}
    
    //method to find the total angery mins
    public int findAngeryMins(String[][] addresses) {
    	
    	int speed = 1;//  km/min
    	int totaltraveltime = 0;
    	int totalangerytime = 0;
    	
    	//loop through the route
    	for(int i = 1; i < addresses.length; i++) {
    		double dist = findDistance(Double.valueOf(addresses[i-1][3]), Double.valueOf(addresses[i-1][4]), Double.valueOf(addresses[i][3]), Double.valueOf(addresses[i][4]));
    		int traveltime = (int) (dist/speed);
    		totaltraveltime += traveltime;
    		int angerytime = totaltraveltime + Integer.valueOf(addresses[i][2]) - 30;
    		totalangerytime += angerytime;
    	}
    	
    	return totalangerytime;
    }

}

//class for handleing gui componints 
class GUI {
	
	public GUI() {
		
		//lable for input area 
		JLabel inputlabel = new JLabel("please input order data here!");
		inputlabel.setForeground(Color.white);
		inputlabel.setBounds(490, 0, 500, 100);
		
		//input data text area
		JTextArea input = new JTextArea();
		JScrollPane inputscroll = new JScrollPane(input); 
		inputscroll.setBounds(425, 60, 300, 150);
		inputscroll.setBorder(BorderFactory.createLineBorder(Color.black));
		
		//calculate route button
		JButton calculateroute = new JButton("calculate route");
		calculateroute.setBounds(490, 215, 180, 20);
		calculateroute.setFocusable(false);
		
		JLabel outputlabel = new JLabel("read output here!");
		outputlabel.setForeground(Color.white);
		outputlabel.setBounds(520, 210, 500, 100);
		
		//output data text area
		JTextArea output = new JTextArea();
		output.setEditable(false);
		JScrollPane outputscroll = new JScrollPane(output); 
		outputscroll.setBounds(425, 270, 300, 150);
		outputscroll.setBorder(BorderFactory.createLineBorder(Color.black));
		
		//addaction listensr and send info to main tsp class find route and return it to the output with the consumer
		Consumer<String> consumer = (String text) -> {
			if(text.charAt(0) == 'i') {
				output.setForeground(Color.red);//set text to red if input was invalid
			}
			else {
				output.setForeground(Color.black);
			}
			output.setText(text);
		};
		calculateroute.addActionListener(new buttonActionListener(consumer, input));
		
				
		//canvas for map 
		JPanel canvas = new DisplayGraphics();
		
		//main panel
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.add(inputlabel);
		panel.add(inputscroll);
		panel.add(calculateroute);
		panel.add(outputlabel);
		panel.add(outputscroll);
		panel.setBackground(new Color(0,102,255));
		panel.add(canvas);
		
		//main frame
		JFrame frame = new JFrame();
		frame.setSize(750,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setTitle("traveling salesman route calculator");
		frame.add(panel);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

class DisplayGraphics extends JPanel{
	
	//colors used
	Color sea = new Color(0,102,255);
	Color land = new Color(0,153,0);
	
	//constructor set canvas bounds and color
	public DisplayGraphics() {
		setBackground(sea);
		setBounds(55, 40, 308, 400);
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//relitive points used to make the moveing of the map easier 
		int relitiveX = 0;
		int relitiveY = 0;
		
		//arrays of points used for the basic map of the polygon 
		int[] xPoints = {relitiveX+128, relitiveX+260, relitiveX+308, relitiveX+260, relitiveX+272, relitiveX+260, relitiveX+52,relitiveX+0, relitiveX+0, relitiveX+72, relitiveX+44, relitiveX+20, relitiveX+24, relitiveX+112};
		int[] yPoints = {relitiveY+12, relitiveY+0, relitiveY+96, relitiveY+128, relitiveY+184,relitiveY+313, relitiveY+400, relitiveY+344, relitiveY+316, relitiveY+216, relitiveY+216, relitiveY+188, relitiveY+96, relitiveY+104};
		
		//set color and draw land as a ploygon 
		g.setColor(land);
		g.fillPolygon(xPoints, yPoints, xPoints.length);
	}
}

