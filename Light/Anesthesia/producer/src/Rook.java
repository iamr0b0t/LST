import java.io.*; 
import java.util.Map;
import java.util.HashMap;

import java.util.Base64;
import java.io.UnsupportedEncodingException;

import java.awt.Robot;
import java.awt.MouseInfo;
import java.awt.event.InputEvent;


import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;

public class Rook {

  private static String dataFile;
  private static int coordX;
  private static int coordY;
  private static int delayAction;
  private static int delayXAction;
  private static int xMapDrift;
  private static int yMapDrift;
  private static int windowDelay;
  private static boolean DEBUG;

  public static void main(String[] args) throws Exception{



     CommandLineParser parser = new DefaultParser();

     Options options = new Options();

     options.addOption("h", "help", false, "display usage");
     options.addOption("v", "verbose", false, "verbose run info");
     options.addOption(
         Option.builder("f")
           .longOpt( "file" )
           .desc( "Absolute path to file"  )
           .hasArg()
           .argName( "FILE" )
           .required(false)
           .build()
        );
     options.addOption(
         Option.builder("x")
           .longOpt( "coordX" )
           .desc( "X coordinate (pixel offset)"  )
           .hasArg()
           .argName( "X" )
           .required(false)
           .build()
        );
     options.addOption(
         Option.builder("y")
           .longOpt( "coordY" )
           .desc( "Y coordinate (pixel offset)"  )
           .hasArg()
           .argName( "Y" )
           .required(false)
           .build()
        );
     options.addOption(
         Option.builder("a")
           .longOpt( "delay-aaction" )
           .desc( "Delay Between Atomic Mouse Event (ms)"  )
           .hasArg()
           .argName( "DELAYA" )
           .required(false)
           .build()
        );
     options.addOption(
         Option.builder("A")
           .longOpt( "delay-caction" )
           .desc( "Delay Between Mouse Clicks (ms)"  )
           .hasArg()
           .argName( "DELAYXA" )
           .required(false)
           .build()
        );
     options.addOption(
         Option.builder("d")
           .longOpt( "x-drift" )
           .desc( "X coordinate drift tolerance (pixels). Pixels between centers of tiles in image map on X")
           .hasArg()
           .argName( "XDRIFT" )
           .required(false)
           .build()
        );
     options.addOption(
         Option.builder("D")
           .longOpt( "y-drift" )
           .desc( "Y coordinate drift tolerance (pixels). Pixels between centers of tiles in image map on Y")
           .hasArg()
           .argName( "YDRIFT" )
           .required(false)
           .build()
        );
     options.addOption(
         Option.builder("w")
           .longOpt( "window-delay" )
           .desc( "Delay Target Window Focus (ms)")
           .hasArg()
           .argName( "DELAYW" )
           .required(false)
           .build()
        );

    try {

        CommandLine cmd = parser.parse( options, args);
        if(cmd.hasOption("h")) {
            System.out.println();
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "Rook", options );
            return;
        }
        if(cmd.hasOption("v")) {
            DEBUG = true;
        }
        if(cmd.hasOption("f")) {
            dataFile = cmd.getOptionValue("f");
        }
        if(cmd.hasOption("x")) {
            coordX = Integer.parseInt(cmd.getOptionValue("x"));
        }
        if(cmd.hasOption("y")) {
            coordY = Integer.parseInt(cmd.getOptionValue("y"));
        }
        if(cmd.hasOption("a")) {
            delayAction = Integer.parseInt(cmd.getOptionValue("a"));
        }
        if(cmd.hasOption("A")) {
            delayXAction = Integer.parseInt(cmd.getOptionValue("A"));
        }
        if(cmd.hasOption("d")) {
            xMapDrift = Integer.parseInt(cmd.getOptionValue("d"));
        }
        if(cmd.hasOption("D")) {
            yMapDrift = Integer.parseInt(cmd.getOptionValue("D"));
        }
        if(cmd.hasOption("w")) {
            windowDelay = Integer.parseInt(cmd.getOptionValue("w"));
        }

    }catch(ParseException pe){
        System.err.println("Unable to parse Options: " + pe.getMessage());
        System.exit(2);
    }


    /* ------------------------------------------------- */

	 byte[] inputDataBytes=null;
	 try {
	 	InputStream inputStream = new FileInputStream(dataFile);
		long fileSize = new File(dataFile).length();
 
		inputDataBytes = new byte[(int) fileSize];
		inputStream.read(inputDataBytes);
 
    } catch (IOException ex) {
       System.err.println("File Issue");
       ex.printStackTrace();
       System.exit(2);
    }


    // Check if we are running from console
    Console c = System.console();
    if (c == null) {
       System.err.println("Should run from the console.");
       System.exit(3);
    }

    String b64data="";
    /* 
    int delayAction = 5; // milliseconds
    int delayXAction = 5; // milliseconds
    int xMapDrift = 47; // pixels between centers of tiles in image map on X
    int yMapDrift = 47; // pixels between centers of tiles in image map on Y
    */

    Map <Character, Integer> hm = new HashMap<Character, Integer>();

    Character[][] charsetTbl = {
			{'A','B','C','D','E','F','G','H'},
			{'I','J','K','L','M','N','O','P'},
			{'Q','R','S','T','U','V','W','X'},
			{'Y','Z','a','b','c','d','e','f'},
			{'g','h','i','j','k','l','m','n'},
			{'o','p','q','r','s','t','u','v'},
			{'w','x','y','z','0','1','2','3'},
			{'4','5','6','7','8','9','+','/'},
			{'='}
		};

	 Integer[][] charsetMap = {
			{11,12,13,14,15,16,17,18},
			{21,22,23,24,25,26,27,28},
			{31,32,33,34,35,36,37,38},
			{41,42,43,44,45,46,47,48},
			{51,52,53,54,55,56,57,58},
			{61,62,63,64,65,66,67,68},
			{71,72,73,74,75,76,77,78},
			{81,82,83,84,85,86,87,88},
			{91}
		};

	 for ( int rowi = 0; rowi < charsetTbl.length; rowi++ ){
	 		for ( int coli = 0; coli < charsetTbl[rowi].length; coli++ ){
				hm.put(charsetTbl[rowi][coli], charsetMap[rowi][coli]);
	 		}
	 }

	 // Encode using basic encoder
	 b64data = Base64.getEncoder().encodeToString(inputDataBytes);

     if ( DEBUG ){
	    System.out.println("Base64 Encoded String (Basic) :" + b64data);
     }

     Robot r = new Robot();
	 //r.setAutoDelay(2);
     r.setAutoWaitForIdle(true);

	 r.delay(windowDelay); // wait  m-seconds to make the target window active. Otherwise robot looses 
						 // first mouse click to focus the target window.

 
     /* 
	 // Macbook keyboard layout
	 Keyboard keyboard = new Keyboard(r);

	 // Simulate typing
	 keyboard.type(new String(Base64.getDecoder().decode(b64data)));
	 System.exit(4);
     */ 

	 int row, col, xPixel, yPixel;
	 for (int i = 0; i < b64data.length(); i++){
		 Character ckey = b64data.charAt(i);        
		 if(hm.containsKey(ckey)){
			Integer cvalue = hm.get(ckey);
			row = cvalue / 10;	// which row in the matrix
			col = cvalue % 10;   // which column in the matrix	
			xPixel = coordX + ((col-1) * xMapDrift); // mouse X (origin) + offset from row into colum of a tile 
																	// adjust first column has no offset 
			yPixel = coordY + ((row-1) * yMapDrift); // mouse Y (origin) + offset from row into row of a tile 
																	// adjust first row has no offset 
		 	System.out.println(ckey + ":" + cvalue  
						+ " (Row: " + row + ", Col: " + col + ")" 
						+ " [X:" + xPixel + " Y:" + yPixel + " ]" );
			 r.mouseMove(xPixel,yPixel);
			 r.delay(delayAction);
			 r.mousePress( InputEvent.BUTTON1_DOWN_MASK );
			 r.delay(delayAction);
			 r.mouseRelease( InputEvent.BUTTON1_DOWN_MASK );

			 r.delay(delayXAction); // Wait between actions
		 }
	 }
  }
}
    
