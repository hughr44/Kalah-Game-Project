package KalahGame;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.shape.*;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Scanner;
import java.net.*;
import java.io.*;

public class Driver extends Application {


    GameBoard myBoard = new GameBoard();

    int numOfHoles;

    static String multipleMoves = "";
    // text to show the number of seeds that are in each hole

    ImageView[] NH;
    ImageView[] SH;

    Button[] NorthHole;
    Button[] SouthHole;

    String numSeeds, numHouses, timerLimit, selected, style;

    TextField seedNum,houseNum,timeNum,ipNum;

    private Text NorthScoringWellText;
    private Text SouthScoringWellText;

    Scene scene, homeScene, modeScene, endScreen, loseScreen;

    static Socket s;
    static PrintWriter pr;
    static InputStreamReader in;
    static BufferedReader bf;
    
    static int playerNum = 1; //1 or 2, player number 1 will decide parameters of the game
    
    static int houseNumForP2;
    static int seedNumForP2;
    static int timeLimitForP2;
    static char randForP2;
    ArrayList<Integer> startingBoardForP2;

    
    int timeLimit=0;
    boolean isPlayersTurn = false;
    long lastTimePoll = System.currentTimeMillis();
        
    ToggleButton randSeed;
    Thread serverThread;
    
    boolean pieRule = true;
    boolean pieRuleClicked = false;
    int turn = 0;
    Button pieSwitch;
    
    public static void main(String[] args) throws UnknownHostException, IOException {
        launch(args);
    }
    
    public void startAsServer() throws UnknownHostException, IOException{
    	//Create server
    	serverThread = new Thread(new Server());
    	serverThread.start();
    	// Connect to server as client (uses local host)
    	s = new Socket("localhost", 4542);
    	System.out.println("listening on port: " + s.getLocalPort());
    	
    	pr = new PrintWriter(s.getOutputStream());
    	in = new InputStreamReader(s.getInputStream());
    	bf = new BufferedReader(in);
    	
    	String str = bf.readLine();
    	playerNum = Integer.valueOf(str);
    	System.out.println(playerNum);
    }
    
    public void startAsClient() throws UnknownHostException, IOException{
    	s = new Socket(ipNum.getText(), 4542);
    	
    	System.out.println("listening on port: " + s.getLocalPort());
    	
    	pr = new PrintWriter(s.getOutputStream());
    	in = new InputStreamReader(s.getInputStream());
    	bf = new BufferedReader(in);
    	
    	String str = bf.readLine();
    	playerNum = Integer.valueOf(str);
    	System.out.println(playerNum);
    	String gameDetailsFrom1 = bf.readLine();
    	System.out.println("Game Details: " + gameDetailsFrom1);
		String temp = "";
		int term = 0;
		startingBoardForP2 = new ArrayList<>();
		for(int i=0; i<gameDetailsFrom1.length(); i++){
			if(gameDetailsFrom1.substring(i, i + 1).equals(" ")){
				if(term==0){
					houseNumForP2 = Integer.valueOf(temp);
				}
				else if(term==1){
					seedNumForP2 = Integer.valueOf(temp);
				}
				else if(term==2){
					timeLimitForP2 = Integer.valueOf(temp);
				}
				else if(term==3){
					randForP2 = temp.charAt(0);
				}
				else{
					startingBoardForP2.add(Integer.valueOf(temp));
				}
				temp = "";
				term++;
			}
			else{
				temp = temp + gameDetailsFrom1.substring(i, i + 1);
			}
		}
		startingBoardForP2.add(Integer.valueOf(temp));
		//seedNumForP2 = Integer.valueOf(temp);
		System.out.println(playerNum + ":" + houseNumForP2 + ":" + seedNumForP2 + ":");
    }
    public void startVersusAI(){
    	playerNum = 0;
    }

    public void start(Stage primaryStage){
        // Board Game Scene
        Group root = new Group();
        
        // ---- Start menu scene ----
        BorderPane startPane = new BorderPane();
        startPane.setPadding(new Insets(100,20,20,20));
        startPane.setStyle("-fx-border-color: #9BC4CB;\n"
                + "-fx-border-insets: 8;\n"
                + "-fx-border-width: 3;\n");

        // Home Scene: The Main Page
        // Create Title
        Label title = new Label("Kalah");
        title.setFont(Font.font("Overlock", FontWeight.BOLD, 50));

        // Hbox for the title
        HBox hbHomeTitle = new HBox();
        hbHomeTitle.setAlignment(Pos.CENTER);
        hbHomeTitle.getChildren().add(title);
        hbHomeTitle.setPadding(new Insets(0,0,0,0));

        // Box to hold button
        GridPane buttonGrid = new GridPane();
        //buttonGrid.setSpacing(40);
        buttonGrid.setHgap(40);
        buttonGrid.setVgap(20);
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setPadding(new Insets(0,0,50,0));
        
        // Create and Style Start Button
        Button startButton = new Button("Start versus player");
        startButton.setStyle(
                "-fx-background-color: #9BC4CB;" +
                        "-fx-text-fill: #FFFFFF;" +
                        "-fx-font: 20 Verdana;"
        );
        Button connectButton = new Button("Connect to Game");
        connectButton.setStyle(
                "-fx-background-color: #9BC4CB;" +
                        "-fx-text-fill: #FFFFFF;" +
                        "-fx-font: 20 Verdana;"
        );
        Button startAiButton = new Button("Start versus AI");
        startAiButton.setStyle(
                "-fx-background-color: #9BC4CB;" +
                        "-fx-text-fill: #FFFFFF;" +
                        "-fx-font: 20 Verdana;"
        );

        // Setting width of buttons to all be equal
        title.setAlignment(Pos.CENTER);
        startButton.setMinWidth(100);
        startButton.setMinHeight(30);
        connectButton.setMinWidth(100);
        connectButton.setMinHeight(30);
        startAiButton.setMinWidth(100);
        startAiButton.setMinHeight(30);
        
        Label ipLabel = new Label("IP Address:");
        ipLabel.setMinWidth(30);
        
        ipNum = new TextField();

        ipNum.setPromptText("Enter IP");
        ipNum.setText("localhost");
        // Add label and each button to the pane
        //buttonBox.getChildren().addAll(startButton,connectButton,startAiButton);
        buttonGrid.add(startButton, 0, 0);
        buttonGrid.add(startAiButton, 0, 1);
        buttonGrid.add(connectButton, 0, 2);
        buttonGrid.add(ipLabel, 0, 3);
        buttonGrid.add(ipNum, 1, 3);

        // Add to Pane
        startPane.setCenter(buttonGrid);
        //startPane.setBottom(connectButton);
        //startPane.setRight(startAiButton);
        startPane.setTop(hbHomeTitle);

        //Create Button Actions
        startButton.setOnAction(e->primaryStage.setScene(modeScene)); 
        startAiButton.setOnAction(e->{
        	primaryStage.setScene(modeScene);
        	startVersusAI();
        }); 
        connectButton.setOnAction(new EventHandler<ActionEvent>(){
        	@Override public void handle(ActionEvent e){
        		try {
    				startAsClient();
    			} catch (Exception e1) {
    				e1.printStackTrace();
    			} 
        		
                int intHouses = houseNumForP2;
                int intSeeds = seedNumForP2;
                boolean isRandom;
                if(randForP2 == 'R'){
                	isRandom=true;
                }
                else{
                	isRandom=false;
                }
                timeLimit=timeLimitForP2;
                System.out.println("Houses " + intHouses + "=" + houseNumForP2 + "; Seeds " + intSeeds + "=" + seedNumForP2);
                
                myBoard = new GameBoard(intHouses, intSeeds, isRandom);
                if(isRandom){
                	for(int i=0;i<intHouses;i++){
                		myBoard.NorthHoles.set(i, startingBoardForP2.get(i));
                		myBoard.SouthHoles.set(i, startingBoardForP2.get(i+intHouses));

                	}
                }
                createButtons(primaryStage, root);
                updateBoardText();
                primaryStage.setScene(scene);
                scene.getWindow().centerOnScreen();     // makes sure the scene is centered on the screen
                
                //if player 2, need to wait for player 1 to make move, receive message from server, update board, then make move
                String serverMessage = "Waiting";
                System.out.println("waiting on other player...");
                while (serverMessage.equals("Waiting")) 
                { 
                    try
                    { 
                      	serverMessage = bf.readLine(); 
                    } 
                    catch(IOException i) 
                    { 
                        System.out.println(i); 
                    } 
                }
                System.out.println("Message Received: " + serverMessage);
                	
                //make corresponding move made by other player, update board
                String[] parseMoves = serverMessage.split(" ");
                for(int i=0; i<parseMoves.length; i++){
                  	myBoard.moveSouth(Integer.valueOf(parseMoves[i]));
                }
                
                 
                turn++;               
                if(turn==1&&pieRule){
                	pieSwitch.setVisible(true);
                }
                
                updateBoardText();
                
                
            }
		});

        //startButton.setOnAction(e->primaryStage.setScene(modeScene));    // Starts up mode scene

        // Start homeScene Set the Stage
        homeScene = new Scene(startPane, 500, 500);
        primaryStage.setTitle("Kalah");
        primaryStage.setScene(homeScene);
        primaryStage.show();
        // end of Home Scene


        // ---- Game Mode Screen ----
        GridPane modePane = new GridPane();
        modePane.setPadding(new Insets(100,20,20,20));
        modePane.setStyle("-fx-background-color: #FFFFFF;");
        modePane.setHgap(20);
        modePane.setVgap(20);
        modePane.getColumnConstraints().add(new ColumnConstraints(130));


        // Titles
        Label seedOpts = new Label("Seeds   ");
        Label houseOpts = new Label("Houses ");
        Label pieOpt = new Label("Pie Rule  ");
        Label timeOpt = new Label("Timer    ");
        seedOpts.setFont(Font.font("Overlock", FontWeight.BOLD, 30));
        houseOpts.setFont(Font.font("Overlock", FontWeight.BOLD, 30));
        pieOpt.setFont(Font.font("Overlock", FontWeight.BOLD, 30));
        timeOpt.setFont(Font.font("Overlock", FontWeight.BOLD, 30));

        // Create Text Boxes
        seedNum = new TextField();
        houseNum = new TextField();
        timeNum = new TextField();

        seedNum.setPromptText("Enter Number 1-10");
        houseNum.setPromptText("Enter Number 4-9");
        timeNum.setPromptText("Enter Time Limit");

        houseNum.setMaxWidth(130);
        seedNum.setMaxWidth(130);
        timeNum.setMaxWidth(130);

        // Buttons
        randSeed = new ToggleButton();
        ToggleButton houseDefault = new ToggleButton();
        ToggleButton pieYes = new ToggleButton();
        ToggleButton pieNo = new ToggleButton();
        ToggleButton play = new ToggleButton();

        randSeed.setMaxWidth(130);
        houseDefault.setMaxWidth(130);
        pieYes.setMaxWidth(130);
        pieNo.setMinWidth(130);
        play.setMinWidth(130);

        randSeed.setText("Random");
        houseDefault.setText("Default");
        pieYes.setText("Yes");
        pieNo.setText("No");
        play.setText("Play!");

        style = "-fx-background-color: #9BC4CB;" +
                "-fx-font: 15 Verdana;";
        selected = "-fx-background-color: #51a0a9;" +
                "-fx-font: 15 Verdana; "
                ;

        randSeed.setStyle(style);
        houseDefault.setStyle(style);
        pieYes.setStyle(style);
        pieNo.setStyle(style);
        play.setStyle(style);

        // Button Actions

        randSeed.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (randSeed.isSelected()){
                    randSeed.setStyle(selected);
                } else {
                    randSeed.setStyle(style);
                }
            }
        });
        houseDefault.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (houseDefault.isSelected()){
                    houseDefault.setStyle(selected);
                } else {
                    houseDefault.setStyle(style);
                }

                myBoard.amtOfHoles = 6;
            }
        });
        pieYes.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (pieYes.isSelected()){
                    pieYes.setStyle(selected);
                    pieNo.setStyle(style);
                    pieNo.setSelected(false);
                    pieRule = true;
                } else {
                    pieYes.setStyle(style);
                    pieNo.setStyle(selected);
                    pieNo.setSelected(true);
                    pieRule = false;
                }
            }
        });
        pieNo.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                if (pieNo.isSelected()){
                    pieNo.setStyle(selected);
                    pieYes.setStyle(style);
                    pieYes.setSelected(false);
                    pieRule = false;
                } else {
                    pieNo.setStyle(style);
                    pieYes.setStyle(selected);
                    pieYes.setSelected(true);
                    pieRule = true;
                }
            }
        });
        pieYes.setStyle(selected);
        pieYes.setSelected(true);


        // Add to Grid
        modePane.add(seedOpts, 0,0);
        modePane.add(seedNum, 1,0);
        modePane.add(randSeed, 2,0);

        modePane.add(houseOpts, 0,1);
        modePane.add(houseNum, 1,1);
        modePane.add(houseDefault, 2,1);

        modePane.add(pieOpt, 0,2);
        modePane.add(pieYes, 1,2);
        modePane.add(pieNo, 2,2);

        modePane.add(timeOpt, 0,3);
        modePane.add(timeNum, 1,3);

        modePane.add(play,1,7);

        modePane.setStyle("-fx-border-color: #9BC4CB;\n"
                + "-fx-border-insets: 8;\n"
                + "-fx-border-width: 3;\n");

        // Set the Scene
        modeScene = new Scene(modePane, 500, 500);

        // end of Mode Scene
        

        // ---- End Screen ----
        //Scene endScreen;

        BorderPane endPane = new BorderPane();
        endPane.setPadding(new Insets(100,20,20,20));
        endPane.setStyle("-fx-border-color: #9BC4CB;\n"
                + "-fx-border-insets: 8;\n"
                + "-fx-border-width: 3;\n");

        // Create Title
        Label message = new Label("WINNER");
        message.setFont(Font.font("Overlock", FontWeight.BOLD, 50));

        // Hbox for the Results
        // fix: add method to get winner
        HBox resultsBox = new HBox();
        resultsBox.setAlignment(Pos.CENTER);
        resultsBox.getChildren().add(message);
        resultsBox.setPadding(new Insets(0,0,0,0));

        // Box to hold button
        HBox backButtonBox = new HBox();
        backButtonBox.setSpacing(40);
        backButtonBox.setAlignment(Pos.CENTER);
        backButtonBox.setPadding(new Insets(0,0,20,0));

        // Create and Style Start Button
        /*
        Button backButton = new Button("Start New Game");
        backButton.setStyle(
                "-fx-background-color: #FF6E56;" +
                        "-fx-text-fill: #FFFFFF;" +
                        "-fx-font: 20 Verdana;"
        );
		*/
        // Setting width of buttons to all be equal
        message.setAlignment(Pos.CENTER);
        /*
        backButton.setMinWidth(100);
        backButton.setMinHeight(30);
		*/
        // Add label and each button to the pane
        //backButtonBox.getChildren().add(backButton);

        // Add to Pane
        //endPane.setCenter(backButtonBox);
        endPane.setTop(resultsBox);

        //Create Button Actions
        //backButton.setOnAction(e->primaryStage.setScene(scene));    // Starts up main start page scene

        // Start homeScene Set the Stage
        endScreen = new Scene(endPane, 500, 500);
        primaryStage.setTitle("Kalah");
        // End of End Scene
        
        
        
     // ---- Lose Screen ----
        //Scene loseScreen;

        BorderPane losePane = new BorderPane();
        losePane.setPadding(new Insets(100,20,20,20));
        losePane.setStyle("-fx-border-color: #9BC4CB;\n"
                + "-fx-border-insets: 8;\n"
                + "-fx-border-width: 3;\n");

        // Create Title
        Label losemessage = new Label("LOSER");
        losemessage.setFont(Font.font("Overlock", FontWeight.BOLD, 50));

        // Hbox for the Results
        // fix: add method to get winner
        HBox loseBox = new HBox();
        loseBox.setAlignment(Pos.CENTER);
        loseBox.getChildren().add(losemessage);
        loseBox.setPadding(new Insets(0,0,0,0));

        // Setting width of buttons to all be equal
        losemessage.setAlignment(Pos.CENTER);

        // Add to Pane
        losePane.setTop(loseBox);

        // Start homeScene Set the Stage
        loseScreen = new Scene(losePane, 500, 500);
        
        primaryStage.setTitle("Kalah");
        // End of Lose Scene
        
        

        play.setOnAction(new StartButtonHandler(primaryStage,root));
        
        if(playerNum == 2){
        	startButton.setOnAction(new StartButtonHandler(primaryStage,root));
        }

        scene = new Scene(root, 1000, 400);

        primaryStage.setTitle("Kalah Game");
    }

    public void createButtons(Stage primaryStage, Group root){
        int size = myBoard.amtOfHoles;
        NH = new ImageView[size];
        NorthHole = new Button[size];
        SH = new ImageView[size];
        SouthHole = new Button[size];

        //Create the base board
        Rectangle rectangleBoard = new Rectangle(142.5, 100, 110+65*size, 200);
        rectangleBoard.setFill(javafx.scene.paint.Color.valueOf("#C69C72"));
        
        for(int i=0;i<size;i++){
            // Create north hole images and buttons
        	
            NH[i] = new ImageView(new Image(getClass().getResource("4.png").toExternalForm()));  
            NH[i].setFitHeight(55);
            NH[i].setPreserveRatio(true);
            NorthHole[i]=new Button();
            NorthHole[i].relocate(205+65*(size-i-1),110);
            NorthHole[i].setGraphic(NH[i]);
            NorthHole[i].setStyle(
                    "-fx-background-radius: 5em; " +
                            "-fx-min-width: 55px; " +
                            "-fx-min-height: 55px; " +
                            "-fx-max-width: 55px; " +
                            "-fx-max-height: 55px;" +
                           " -fx-background-color: -fx-outer-border, -fx-inner-border, -fx-body-color;"
            );
            if(playerNum == 2){
            	NorthHole[i].setOnAction(new ButtonHandler(primaryStage, i));
            }

            //Create south hole images and buttons
            SH[i] = new ImageView(new Image(getClass().getResource("4.png").toExternalForm()));
            SH[i].setFitHeight(55);
            SH[i].setPreserveRatio(true);
            SouthHole[i]=new Button();
            SouthHole[i].relocate(205+65*i,220);
            SouthHole[i].setGraphic(SH[i]);
            SouthHole[i].setStyle(
                    "-fx-background-radius: 5em; " +
                            "-fx-min-width: 55px; " +
                            "-fx-min-height: 55px; " +
                            "-fx-max-width: 55px; " +
                            "-fx-max-height: 55px;" +
                            " -fx-background-color: -fx-outer-border, -fx-inner-border, -fx-body-color;"
            );
            if(playerNum == 1){
            	SouthHole[i].setOnAction(new ButtonHandler(primaryStage, i));
            }
            if(playerNum == 0){
            	SouthHole[i].setOnAction(new AiButtonHandler(primaryStage, i));
            }
        }

        // creating ellipse shapes representing each scoring well
        // then creating the text that shows how many seeds are in each well
        Ellipse NorthWell = new Ellipse(180, 200, 20, 85);
        NorthWell.setFill(javafx.scene.paint.Color.WHITE);
        NorthScoringWellText = new Text("0");
        NorthScoringWellText.setX(172.5);
        NorthScoringWellText.setY(205);
        NorthScoringWellText.setFill(javafx.scene.paint.Color.BLACK);
        NorthScoringWellText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        Ellipse SouthWell = new Ellipse(215+65*size, 200, 20, 85);
        SouthWell.setFill(javafx.scene.paint.Color.WHITE);
        SouthScoringWellText = new Text("0");
        SouthScoringWellText.setX(207.5+size*65);
        SouthScoringWellText.setY(205);
        SouthScoringWellText.setFill(javafx.scene.paint.Color.BLACK);
        SouthScoringWellText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        Text southTimerLabel = new Text("");
        southTimerLabel.setY(330);
        southTimerLabel.setX(340);
        Text northTimerLabel = new Text("");
        northTimerLabel.setY(90);
        northTimerLabel.setX(340);
		
		// Create a timeline to update the timer if it's set
		Timeline timeline = new Timeline( new KeyFrame(Duration.ZERO,
					new EventHandler<ActionEvent>(){
					  @Override public void handle(ActionEvent arg0) {
						  // Actual update function
						  if(timeLimit!=0 && (timeLimit*1000-(System.currentTimeMillis()-lastTimePoll)) > 0){
							  long timeLeft= timeLimit*1000-(System.currentTimeMillis()-lastTimePoll);
							  if(!isPlayersTurn){
								  southTimerLabel.setText(""+(timeLeft/1000));
								  northTimerLabel.setText("");
							  }
							  else{
								  northTimerLabel.setText(""+(timeLeft/1000));
								  southTimerLabel.setText("");
							  }
						  }
						  else if(timeLimit!=0){
							  //send message to server that time ran out
							  if(!isPlayersTurn){
								  southTimerLabel.setText("Other Players Time Ran Out... You Won!");
								  northTimerLabel.setText("");
							  }
							  else{
								  northTimerLabel.setText("Your Time Ran Out... You Lose!");
								  southTimerLabel.setText("");
							  }
							  scene.getRoot().setDisable(true);
						  }
					  	}
					}
				), new KeyFrame(Duration.seconds(.1))); //Sets to update every .1 seconds
		
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
		
		if (pieRule){
            pieSwitch = new Button();
            pieSwitch.setVisible(false);
            pieSwitch.setText("Switch");
            pieSwitch.setStyle("-fx-background-color: #9BC4CB;" +
                    "-fx-font: 15 Verdana;");
            pieSwitch.setLayoutX(315);
            pieSwitch.setLayoutY(330);
            root.getChildren().add(pieSwitch);

            // create button handler
            pieSwitch.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    // Pie Rule
                	System.out.println("performing pie rule...");
                	pieRuleClicked = true;
                    //myBoard.pieRule();
                    
                    pr.println("P");
		            pr.flush();
		            
		            // switch this players player number
		            if(playerNum == 1){
		            	playerNum = 2;
		            }
		            if(playerNum == 2){
		            	playerNum = 1;
		            }
		            
		            // wait for other player to make move
		            scene.getRoot().setDisable(true);
				                
				    //stop this player for making more moves until message containing other players move is received
		            
		            String serverMessage = "";
		            System.out.println("waiting for OK...");
		            while (!serverMessage.equals("OK")) { 
		            	try{ 
		            		serverMessage = bf.readLine(); 
		            	} 
		            	catch(IOException i){ 
		            		System.out.println(i); 
		            	} 
		            }
		            System.out.println("Message Received: " + serverMessage);
		            
				    serverMessage = "Waiting";
				    System.out.println("waiting on other player...");
				    while (serverMessage.equals("Waiting")) { 
				        try{ 
				            serverMessage = bf.readLine(); 
				        } 
				        catch(IOException i){ 
				            System.out.println(i); 
				        } 
				    }
				    System.out.println("Message Received: " + serverMessage);
	
				    System.out.println("Parse 1");
				    String[] parseMoves = serverMessage.split(" ");
				    System.out.println("Parse 2");
				                
				    if(playerNum == 1){
				    	for(int i=0; i<parseMoves.length; i++){
				    		System.out.println("Parse 3");
				            myBoard.moveNorth(Integer.valueOf(parseMoves[i]));
				        }
				    }
				    else{
				        for(int i=0; i<parseMoves.length; i++){
				        	System.out.println("Parse 4");
				            myBoard.moveSouth(Integer.valueOf(parseMoves[i]));
				        }
				    }
				    turn++;
				        
			        lastTimePoll=System.currentTimeMillis();
			        isPlayersTurn = false;
			        updateBoardText();
			            
			        scene.getRoot().setDisable(false);
			            
			        for(int i=0;i<size;i++){
			        	// Create north hole images and buttons
			            if(playerNum == 2){
			                NorthHole[i].setOnAction(new ButtonHandler(primaryStage, i));
			                SouthHole[i].setOnAction(null);
			            }
			            if(playerNum == 1){
			                NorthHole[i].setOnAction(null);
			                SouthHole[i].setOnAction(new ButtonHandler(primaryStage, i));
			            }
			        }
                    
                    pieSwitch.setVisible(false);
                }
            });
        }

        // attaching the labels for the sides of the boards
        root.getChildren().addAll(rectangleBoard,NorthWell,SouthWell,NorthScoringWellText,SouthScoringWellText,northTimerLabel,southTimerLabel);
        for(int i=0;i<size;i++){
            root.getChildren().add(NorthHole[i]);
            root.getChildren().add(SouthHole[i]);
        }
        isPlayersTurn = true;
        lastTimePoll=System.currentTimeMillis();
    }

    public void updateBoardText(){
        // sets the text for each hole to be "hole # : # of seeds in the hole"
        // uses the GameBoard accessors to retreive how many seeds are in the hole

        String[] n = new String[NH.length];
        for(int i=0;i<n.length;i++){
            n[i] = myBoard.getNorthHole(i) + ".png";
            NH[i].setImage(new Image(getClass().getResource(n[i]).toExternalForm()));
        }

        String[] s = new String[SH.length];
        for(int i=0;i<s.length;i++){
            s[i] = myBoard.getSouthHole(i) + ".png";
            
            //System.out.println(s[i]);
            
            SH[i].setImage(new Image(getClass().getResource(s[i]).toExternalForm()));
        }

        // sets the text for each score well to be the number of seeds in each well
        // again uses accessors to retrieve how many seeds are in each well
        NorthScoringWellText.setText("" + myBoard.getNorthWell());

        SouthScoringWellText.setText("" + myBoard.getSouthWell());
    }
    class AiButtonHandler implements EventHandler<ActionEvent>{
    	int spot = 0;
    	Stage primaryStage;
        public AiButtonHandler(Stage primary, int n){
        	primaryStage=primary;
            spot=n;
        }
        @Override
        public void handle(ActionEvent event){
        	lastTimePoll=System.currentTimeMillis();
        	isPlayersTurn = false;
			System.out.println("generates:"+MinMaxTree.generateCnt+"\tGetAvailMoves:"+MinMaxTree.getMovesCnt);
			updateBoardText();
			if(myBoard.isGameOver()){
				if(myBoard.southScore()>myBoard.northScore()){
					System.out.println("You win!");
				}
				else{
					System.out.println("You lose");
				}
			}
			if(myBoard.moveSouth(spot)==0){
				turn++;
				updateBoardText();
				if(myBoard.isGameOver()){
					if(myBoard.southScore()>myBoard.northScore()){
						System.out.println("You win!");
						primaryStage.setScene(endScreen);
					}
					else{
						System.out.println("You lose");
						primaryStage.setScene(loseScreen);
					}
				}
				aiTurn();
				updateBoardText();
				isPlayersTurn=true;
			}
			updateBoardText();
		}
    }

    class ButtonHandler  implements EventHandler<ActionEvent>{
        int spot = 0;
        int moveResponse = 0;
        Stage primaryStage;
        public ButtonHandler(Stage primary, int n){
        	primaryStage=primary;
            spot=n;
        }
        @Override
        public void handle(ActionEvent event){
        	if(playerNum == 1){
        		moveResponse = myBoard.moveSouth(spot);
        	}
        	else{
        		moveResponse = myBoard.moveNorth(spot);
        	}

            updateBoardText();
            
            scene.getRoot().setDisable(true);
            PauseTransition pause = new PauseTransition();
	            pause.setOnFinished(e -> { 
	            if(moveResponse == 1){
	            	//go again
	            	System.out.println("go again...");
	            	multipleMoves = multipleMoves + spot + " ";
	            }
	            else if(moveResponse == -1){
	            	//was an invalid move
	            	System.out.println("invalid move...");
	            }
	            else{
	            	//normal valid move, next players turn
	            	//send the move made to the server
	            	//before waiting for oponnents move, check if the game was won
	            	turn++;
		            	
		            String moveMade = "" + spot;
		            	
		            multipleMoves = multipleMoves + spot + " ";
		            System.out.println("Moves list: " + multipleMoves);
		            	
		            pr.println(multipleMoves);
		            pr.flush();
		            
		            if(myBoard.isGameOver()){
	            		// tell server the game was won
		               	//pr.println("WINNER");
		                //pr.flush();
		                primaryStage.setScene(endScreen);
		            }
		                
		            //receive "OK" message from server
		            String serverMessage = "";
		            System.out.println("waiting for OK...");
		            while (!serverMessage.equals("OK")) { 
		            	try{ 
		            		serverMessage = bf.readLine(); 
		            	} 
		                catch(IOException i){ 
		                	System.out.println(i); 
		                } 
		            }
		            System.out.println("Message Received: " + serverMessage);
		                
		            //stop this player for making more moves until message containing other players move is received
		            serverMessage = "Waiting";
		            System.out.println("waiting on other player...");
		            while (serverMessage.equals("Waiting")) { 
		            	try{ 
		            		serverMessage = bf.readLine(); 
		                } 
		                catch(IOException i){ 
		                	System.out.println(i); 
		                } 
		            }
		            System.out.println("Message Received: " + serverMessage);
		            // TODO: add victory/lose check here to end the game
		            //make corresponding move made by other player, update board
		            //int otherPlayersMove = Integer.valueOf(serverMessage);
		            System.out.println("Parse 1");
		            String[] parseMoves = serverMessage.split(" ");
		            System.out.println("Parse 2");
		            
		            System.out.println(":" + parseMoves[0] + ":");
		            
		            if(parseMoves[0].equals("P")){
		            	System.out.println("other player chose pie rule, switching sides...");
		            	if(playerNum == 1){
		            		playerNum = 2;
		            	}
		            	else{
		            		playerNum = 1;
		            	}
		            	pieSwitch.setVisible(false);
		            	
		            	for(int i=0;i<NorthHole.length;i++){
			                // Create north hole images and buttons
			                if(playerNum == 2){
			                	NorthHole[i].setOnAction(new ButtonHandler(primaryStage, i));
			                	SouthHole[i].setOnAction(null);
			                }
			                if(playerNum == 1){
			                	NorthHole[i].setOnAction(null);
			                	SouthHole[i].setOnAction(new ButtonHandler(primaryStage, i));
			                }
			            }
		            	
		            }
		            else if(parseMoves[0].equals("WINNER")){
		            	System.out.println("Sorry you lost...");
		            }
		            else if(playerNum == 1){
		               	for(int i=0; i<parseMoves.length; i++){
		               		System.out.println("Parse 3");
		               		myBoard.moveNorth(Integer.valueOf(parseMoves[i]));
		               	}
		            }
		            else{
		            	for(int i=0; i<parseMoves.length; i++){
		               		System.out.println("Parse 4");
		               		myBoard.moveSouth(Integer.valueOf(parseMoves[i]));
		              	}
		            }
		            turn++;
		            
		            // if pie message from other server is "P", other player chose pie rule, switch sides and make another move
		            if(turn==1&&pieRule){
		            	pieSwitch.setVisible(true);
		            }
		            
		            //if game is over after making oppenents moves, then this player has lost
		            if(myBoard.isGameOver()){
		               	// tell server the game was won
		              	//pr.println("LOSER");
		                //pr.flush();
		                primaryStage.setScene(loseScreen);
		            }
		               
		            multipleMoves = "";
		           	//allow player to make another turn
		        }
	        	lastTimePoll=System.currentTimeMillis();
	        	isPlayersTurn = false;
	            updateBoardText();
	            scene.getRoot().setDisable(false);
	            });
	        
            pause.play();
        }
    }

    //Handler for starting the game
    class StartButtonHandler implements EventHandler<ActionEvent>{

        Stage primaryStage;
        Group root;
        public StartButtonHandler(Stage primary, Group root){
            primaryStage=primary;
            this.root=root;
        }
        public void handle(ActionEvent event){
            // Gather Text from text boxes
        	System.out.println("Setting Board Handler");
        	if(playerNum==1){
        		try {
					startAsServer();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
            int intSeeds, intHouses;
            // Set else based on input, or use default
            if (!seedNum.getText().equals("")) {
                    intSeeds = Integer.parseInt(seedNum.getText());
            } else {
                intSeeds = 4;
            }
            // Set houses based on input, or use default
            if (!houseNum.getText().equals("")) {
                intHouses = Integer.parseInt(houseNum.getText());
            }
            else {
                intHouses = 6;
            }
            
            if (!timeNum.getText().equals("")){
                // gather text
                timeLimit = Integer.parseInt(timeNum.getText());

            }
            boolean isRandom = randSeed.isSelected();
            myBoard = new GameBoard(intHouses, intSeeds, isRandom);
            if(playerNum == 1){
            	String gameDetails = intHouses + " " + intSeeds + " " + timeLimit+" ";
            	if(isRandom){
            		gameDetails += "R";
            	}
            	else{
            		gameDetails += "S";
            	}
            	for(int i=0;i<intHouses;i++){
            		gameDetails+=" "+myBoard.NorthHoles.get(i);
            	}
            	for(int i=0;i<intHouses;i++){
            		gameDetails+=" "+myBoard.SouthHoles.get(i);
            	}
            	System.out.println(playerNum + " " + gameDetails);
            	
                pr.println(gameDetails);
                pr.flush();
            }
            createButtons(primaryStage, root);
            updateBoardText();
            primaryStage.setScene(scene);
            scene.getWindow().centerOnScreen();     // makes sure the scene is centered on the screen
        }
    }
    
    // Has the ai perform it's turn
    public void aiTurn(){
    	MinMaxTree tree = new MinMaxTree(myBoard);
    	if(pieRuleClicked){
    		tree.isNorth = false;
    	}
    	ArrayList<Integer> moves = tree.getBestMove();
    	for(int i=0;i<moves.size();i++){
    		System.out.println("AI moved on:"+moves.get(i));
    		myBoard.moveNorth(moves.get(i));
    	}
    }
}