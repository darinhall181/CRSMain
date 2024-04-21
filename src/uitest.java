// importing necesary libraries
import java.sql.*;
import java.util.*;

public class uitest {
   
    // connection url allowing access into the database
    static String connectionUrl = "jdbc:sqlserver://cxp-sql-02\\jab525;"
    + "database=CRS;"
    + "user=sa;"
    + "password=1V6FVhAtVO6Uvm;"
    + "encrypt=true;"
    + "trustServerCertificate=true;"
    + "loginTimeout=15;";
   
    public static void main(String[] args){

        // scanner that reads user input to tell which query to execute
        Scanner s = new Scanner(System.in);

       
        // print out the options
        System.out.println("1. You are a customer and would like to find a camera based on your preferences.");
        System.out.println("2. You are looking for a camera and would like to see the cheapest and most expensive camera from every brand.");
        System.out.println("3. You are a company that is closing down and would like to delete its catalog from the database.");
        System.out.println("4. I'm Feeling Lucky");
        System.out.println("5. You are a company that would like to reduce the prices of your catalog by a specified amount for a sale.");
        System.out.println("6. You are a company that would like to add a new product to the market.");
        System.out.println("7. Exit");

        // user prompt
        System.out.println("\nPlease enter the number corresponding to your desired function.");

        // user input stored in choice
        int choice = s.nextInt();

        // different method will be executed depending on the user's choice
        switch(choice){
            case 1:
                method1();
                break;        
            case 2:
                method2();
                break;
            case 3:
                method3();
                break;
            case 4:
                method4();
                break;
            case 5:
                method5();
                break;
            case 6:
                method6();
                break;
            case 7:
                System.out.println("Exiting...");
                System.exit(0);
        }
    

    // scanner instance is closed
    s.close();

    }

    // method that recommends camera/lens combinations within your budget based on your preferences
    public static void method1(){
        // new scanner instance is created
        Scanner scan = new Scanner(System.in);
        // user prompt
        System.out.println("\nWhat is your budget?");
        // budget stores the user's budget
        int budget = scan.nextInt();
        scan.nextLine();

        // menu and prompt for user's skill level
        System.out.println("\n1. Beginner\n2. Advanced\n\nPlease choose your skill level. Type the number corresponding to your choice and press enter.");
        // skill stores the integer corresponding to the user's choice
        int skill = scan.nextInt();
        scan.nextLine();

        // different questions will be asked based on the user's skill level
        // more general questions will be asked for beginners who know less about what they'd want
        switch(skill){

            // beginner
            case 1:

                // menu and prompt to determine photography subject
                // this will determine what brand and focal length to suggest
                System.out.println("\n1. Sports\n2. Portrait\n3. Landscape\n4. Wildlife\n5. Events");
                System.out.println("\nWhat are you mostly going to be shooting? Please type the number corresponding to your choice and hit enter.");
                int choice = scan.nextInt();
                scan.nextLine();

                // these variables will store the brand names that we have determined to work best for each type of photography
                // each type of photography will be assigned three corresponding brands (which may overlap with other types of photography)
                String brand1 = null;
                String brand2 = null;
                String brand3 = null;

                // array to store the id numbers of the three brands
                int[] brand_ids = new int[3];

                // we want to filter by the FL_end attribute of the lens table
                // these variables serve as the upper and lower bounds we want FL_end to fit between
                int flend1 = 0;
                int flend2 = 0;

                // string storing the name of the topic the user wants to shoot
                String topic = null;

                // switch statement that will assign values to the variables defined above based on what the user wants to shoot
                switch(choice){

                    //sports
                    case 1:
                        brand1 = "Canon";
                        brand2 = "Fujifilm";
                        brand3 = "Sony";

                        flend1 = 71;
                        flend2 = 600;

                        topic = "Sports";
                        
                        break;

                    // portrait
                    case 2:
                        brand1 = "Canon";
                        brand2 = "Nikon";
                        brand3 = "Leica";

                        flend1 = 36;
                        flend2 = 70;

                        topic = "Portrait";

                        break;

                    // landscape
                    case 3:
                        brand1 = "Fujifilm";
                        brand2 = "Sony";
                        brand3 = "Pentax";

                        flend1 = 0;
                        flend2 = 35;

                        topic = "Landscape";

                        break;

                    // wildlife
                    case 4:
                        brand1 = "Olympus";
                        brand2 = "Panasonic";
                        brand3 = "Nikon";

                        flend1 = 200;
                        flend2 = 600;

                        topic = "Wildlife";

                        break;

                    // events
                    case 5:
                        brand1 = "Canon";
                        brand2 = "GoPro";
                        brand3 = "Sony";

                        flend1 = 0;
                        flend2 = 35;

                        topic = "Events";

                        break;

                }

                // establish connection to the database
                try(Connection connection = DriverManager.getConnection(connectionUrl)){

                    // result1 will hold the id numbers of the three companies assigned above
                    // query1 below takes the names of the companies defined above and populates the brand_ids array with their id numbers
                    ResultSet result1 = null;
                    String query1 = "select id from brand where brand.name = ? or brand.name = ? or brand.name = ?;";
                    PreparedStatement statement1 = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
                    statement1.setString(1, brand1);
                    statement1.setString(2, brand2);
                    statement1.setString(3, brand3);

                    result1 = statement1.executeQuery();
                    
                    // populating the brand_ids array
                    int index = 0;
                    while(result1.next()){
                        brand_ids[index] = result1.getInt(1);
                        index++;
                    }

                    /*  result2 will store all the possible camera/lens combinations that are within the user's budget and that will work for the user's
                        preferred application
                    */ 
                    ResultSet result2 = null;

                    // the query ensures that only the three defined brands are represented and that the total price of the camera and lens are within the budget
                    // the query also specifies that the lenses have focal lengths within the specified range, and the output is ordered by total price
                    String query2 = "select brand.name, body.model, lens.FL_start, lens.FL_end, (body.price + lens.price) as total_price "
                    + "from brand inner join body on brand.id = body.brand inner join body_lens on body.id = body_lens.body_id "
                    + "inner join lens on body_lens.lens_id = lens.id where lens.FL_end >= ? and lens.FL_end <= ? and "
                    + "(lens.price + body.price <= ?) and (brand.id = ? or brand.id = ? or brand.id = ?) order by total_price;";
                    PreparedStatement statement2 = connection.prepareStatement(query2, Statement.RETURN_GENERATED_KEYS);
                    System.out.println(flend1);
                    System.out.println(flend2);
                    statement2.setInt(1, flend1);
                    statement2.setInt(2, flend2);
                    statement2.setInt(3, budget);
                    statement2.setInt(4, brand_ids[0]);
                    statement2.setInt(5, brand_ids[1]);
                    statement2.setInt(6, brand_ids[2]);

                    result2 = statement2.executeQuery();

                    // summary of the output set
                    System.out.println("\nHere are all of the camera/lens combinations from cheapest to most expensive that are good for shooting "
                     + topic.toLowerCase() + " that are under your budget.\n");

                    // the output set is printed out
                    while(result2.next()){
                        System.out.println("Brand: " + result2.getString(1) + " | Model: " + result2.getString(2)
                        + " | Focal Length: " + result2.getString(3) + "-" + result2.getString(4)
                        + " | Total Price: " + result2.getString(5));
                    }
                }

                catch(Exception e){
                    e.printStackTrace();
                }

                break;

            // advanced
            case 2:
                
                // the user will decide whether they want a camera and a lens or just a lens
                // advanced photographers might already have a camera they like and as such, might only need a new lens to go with it
                System.out.println("\n1. New camera and lens\n2. New lens only\n\n"
                +"Are you looking for a new camera and a new lens or a new lens only?"
                +" Please type the number corresponding to your choice.");

                // answer stores the option the user chose
                int answer = scan.nextInt();
                scan.nextLine();

                // we assume advanced photographers know what brand they want to shoot with so their options are printed out
                System.out.println("\n1. Canon\n2. Fujifilm\n3. Leica\n4. Nikon\n5. Sony\n6. Pentax\n7. Olympus\n8. GoPro\n9. Panasonic");
                System.out.println("\nPlease type the name of the brand above that you would like to use and hit enter.");
                String body_brand = scan.nextLine();

                // brand_id will store the id of the brand identified by the user
                int brand_id = 0;



                try(Connection connection = DriverManager.getConnection(connectionUrl)){

                    // this query will produce a set of all camera/lens combinations or just lenses from the specified brand that are within the budget
                    String query = null;
                    
                    if(answer == 1){
                        query = "select * from body inner join brand on body.brand = brand.id "
                        + " inner join body_lens on body_lens.body_id = body.id inner join lens on "
                        + "body_lens.lens_id = lens.id where brand.name = ? and (body.price + lens.price) <= ?;";
                    }

                    else{
                        query = "select brand.id, brand.name, lens.FL_start, lens.FL_end, lens.aperture, lens.price "
                        + "from brand inner join lens on brand.id = lens.brand where brand.name = ? and lens.price <= ?;";
                    }
                    PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

                    statement.setString(1, body_brand);
                    statement.setInt(2, budget);
                    ResultSet result = statement.executeQuery();

                    if(answer == 1){
                        System.out.println("\nHere are all of " + body_brand + "'s camera/lens combos that fit your budget: ");
                    }

                    else{
                        System.out.println("\nHere are all of " + body_brand + "'s lenses that fit your budget: ");
                    }
                    // this message will appear if there are no combinations that fit the specifications
                    if(!result.isBeforeFirst()){
                        System.out.println("\n" + body_brand + " has no models within your budget.");
                    }

                    // if there are combinations from the specified brand within the budget, they and their specifications will be printed out
                    else{
                        if(answer == 1){
                            while(result.next()){
                                brand_id = result.getInt(2);
                                System.out.println("\n" + "ID: " + result.getString(1) +
                                " | Model: " + result.getString(3) + " | Focal Length: " + 
                                result.getString(15) + "-" + result.getString(16) + " | Aperture: "
                                + result.getString(18) + " | Price: $" + (result.getInt(4) + result.getInt(14)));
                            }
                        }
                        else{
                            while(result.next()){
                                brand_id = result.getInt(1);
                                System.out.println("\n" + "Focal Length: " + result.getString(3) + "-"
                                + result.getString(4) + " | Aperture: " + result.getString(5) 
                                + " | Price: $" + result.getString(6));
                            }
                        }
                    }
                }

                catch(Exception e){
                    e.printStackTrace();
                }

               

                // the user is prompted to input what type of lens they want to buy
                // this will determine by what focal lengths the lenses are filtered by
                System.out.println("\n1. Wide\n2. Standard\n3. Telephoto\n\nPlease type the number "
                + "corresponding to the type of lens you would like and hit enter.");

                // lens_type stores the number corresponding to the type of lens the user chose
                int lens_type = scan.nextInt();
                scan.nextLine();

                // flstart and flend store the upper and lower bounds for the FL_end attribute of the lens table
                int flstart = 0;
                int flend = 0;

                // upper and lower bounds are assigned based on the type of lens chosen
                switch(lens_type){

                    // wide
                    case 1:
                        flstart = 0;
                        flend = 35;
                        break;

                    // standard
                    case 2:
                        flstart = 36;
                        flend = 70;
                        break;

                    // telephoto
                    case 3:
                        flstart = 71;
                        flend = 600;
                        break;
                }

                try(Connection connection = DriverManager.getConnection(connectionUrl)){

                    // a query is created to find camera/lens combinations or indivdual lenses that match the user's parameters
                    String query;

                    // if the user wants both a camera and a lens, we need to return camera/lens combinations
                    if(answer == 1){
                        query = "select brand.name, body.model, lens.FL_start, lens.FL_end, (body.price + lens.price) as total"
                        + " from brand inner join body on brand.id = body.brand inner join body_lens on body.id = body_lens.body_id"
                        + " inner join lens on body_lens.lens_id = lens.id where lens.FL_end >= ? and lens.FL_end <= ? and"
                        + " body.price + lens.price <= ? and brand.id = ? order by (body.price + lens.price);";
                    }

                    // if the user only wants a lens, we only need to return lenses
                    else{
                        query = "select brand.name, lens.FL_start, lens.FL_end, lens.aperture, lens.price"
                        + " from brand inner join lens on lens.brand = brand.id where lens.FL_end >= ? and lens.FL_end <= ? and"
                        + " lens.price <= ? and brand.id = ? order by lens.price;";
                    }

                    // the query is filled with the user's parameters
                    PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    statement.setInt(1, flstart);
                    statement.setInt(2, flend);
                    statement.setInt(3, budget);
                    statement.setInt(4, brand_id);

                    // message to be displayed if the user wants both a camera and a lens
                    if(answer == 1){
                        System.out.println("\nHere are all of the camera/lens combos that fit your parameters, from cheapest to most expensive.\n");
                    }

                    // message to be displayed if the user only wants a lens
                    else{
                        System.out.println("\nHere are all of the lenses that fit your parameters, from cheapest to most expensive.\n");
                    }

                    ResultSet result = statement.executeQuery();

                    // if the result set is empty, a message is printed out
                    if (!result.isBeforeFirst()) {    
                        System.out.println("There were no products that matched your parameters :( Please try again."); 
                    } 
                    
                    // if the user wants a camera and lens and the result set is not empty, then its products are printed out with their specifications
                    if(answer == 1){
                        while(result.next()){
                            System.out.println("Brand: " + result.getString(1) + " | Model: "
                            + result.getString(2) + " | Focal Length: " + result.getString(3) + "-"
                            + result.getString(4) + " | Total Price: $" + result.getString(5));
                        }
                    }

                    // if the user only wants a lens and the result set is not empty, then its products are printed out with their specifications
                    else{
                        while(result.next()){
                            System.out.println("Brand: " + result.getString(1) + " | Focal Length: " + result.getString(2)
                            + "-" + result.getString(3) + " | Aperture: " + result.getString(4) + " | Price: $"
                            + result.getString(5));
                        }
                    }
                }

                catch(Exception e){
                    e.printStackTrace();
                }

                break;
        }
        
        // the scanner is closed
        scan.close();
    }

    // method that returns the cheapest and most expensive camera body from each brand
    public static void method2(){

        // prompt and input for the user to confirm that the correct use case has been chosen
        System.out.println("\nWould you like to know the cheapest and most expensive camera body from every brand? Type yes to continue.");
        Scanner scan = new Scanner(System.in);
        String answer = scan.nextLine();
        scan.close();

        // result will store the output set
        ResultSet result = null;

        // if the user confirms that the use case is correct, a query will be run to find the requested query
        if(answer.equals("yes")){
            try(Connection connection = DriverManager.getConnection(connectionUrl)){
                String query = "select min(body.price) as min_price, max(body.price) as max_price, brand.name\r\n" + //
                        "from body inner join brand on body.brand = brand.id\r\n" + //
                        "group by brand.name;";
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                result = statement.executeQuery();

                while (result.next()) {
                    System.out.println("Brand: " + result.getString(3) + " | Cheapest: " + result.getString(1) + " | Most Expensive: " + result.getString(2));
                }

            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        // if the user says anything besides 'yes' they will be prompted to choose another option
        else{
            System.out.println("Okay, please choose another option from the menu.");
        }
    }

    // method that deletes every model produced by a given company from the database
    public static void method3(){

        // the user is prompted and a scanner records the name of the brand that the user inputs
        System.out.println("\nWhat is the name of your brand?");
        Scanner scan = new Scanner(System.in);
        String brand = scan.nextLine();
        scan.close();

        // instantiating a result set to store the id of the brand based on its name
        ResultSet result = null;

        try(Connection connection = DriverManager.getConnection(connectionUrl)){
            
            // query that returns the id of the brand based on the name given by the user and stores it in the result set
            String query1 = "select id from brand where name = ?;";
            PreparedStatement statement1 = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
            statement1.setString(1, brand);
            result = statement1.executeQuery();

            // brand_id will store the id number of the brand we want to delete from
            String brand_id = null;
            while(result.next()){
                brand_id = result.getString(1);
            }

            // query2 will delete every body from the selected brand id
            // every table with body_id as a foreign key will have its corresponding tuples deleted
            String query2 = "delete from body where brand = ?;";
            PreparedStatement statement2 = connection.prepareStatement(query2);
            statement2.setString(1, brand_id);
            statement2.execute();

            // query3 will delete every lens from the selected brand id
            // every table with lens_id as a foreign key will have its corresponding tuples deleted
            String query3 = "delete from lens where brand = ?;";
            PreparedStatement statement3 = connection.prepareStatement(query3);
            statement3.setString(1, brand_id);
            statement3.execute();

            // query4 will delete the specified brand from the brand table
            String query4 = "delete from brand where name = ?;";
            PreparedStatement statement4 = connection.prepareStatement(query4);
            statement4.setString(1, brand);
            statement4.execute();

        }
        catch(Exception e){
            e.printStackTrace();
        }

        // confirmation that the query worked
        System.out.println("\nYour brand's data has been deleted.");

    }



    public static void method4(){

        System.out.println("\nBelow is a recommended camera & lens combination. Enjoy!");

        try(Connection connection = DriverManager.getConnection(connectionUrl)){
            String query = "select top 1 brand.name, body.model, lens.FL_start, lens.FL_end, lens.aperture, "
            + "body.price + lens.price as total_price from brand inner join body on brand.id = body.brand "
            + "inner join body_lens on body_lens.body_id = body.id inner join lens on "
            + "body_lens.lens_id = lens.id where body.id = (SELECT TOP 1 id FROM body ORDER BY NEWID());";

            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            int fl = 0;

            while(result.next()){
                fl = result.getInt(4);
                System.out.println("\nBrand: " + result.getString(1) + " | Model: " + result.getString(2)
                + " | Focal Length: " + result.getString(3) + "-" + result.getString(4) + " | Aperture: "
                + result.getString(5) + " | Price: $" + result.getString(6));
            }


            String uses = null;

            if(fl >= 0 && fl <= 35){
                uses = "landscapes and events!";
            }
            else if(fl >= 36 && fl <= 70){
                uses = "portraits!";
            }
            else{
                if(fl >= 200){
                    uses = "sports and wildlife!";
                }
                else{
                    uses = "sports!";
                }
            }

            System.out.println("\nThis camera would be best used for shooting " + uses);
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }

    // method that will increase or decrease a brand's prices by a specified amount
    public static void method5(){

        // scan records user input
        Scanner scan = new Scanner(System.in);
        System.out.println("\nWhat is the name of your brand?");
        
        // brand stores the name of the brand
        String brand = scan.nextLine();
        System.out.println("\nWould you like to reduce your prices by a dollar amount or a percentage? "
        + "Choose the number corresponding to your choice.\n1. Dollar \n2. Percentage");
        
        // choice stores whether the user wants to change their prices by a dollar amount or a percentage
        int choice = scan.nextInt();

        // different code will be implemented depending on whether the user chose a dollar amount or percentage
        switch(choice){
            
            // dollar amount
            case 1:
                Scanner s = new Scanner(System.in);
                System.out.println("\nHow much would you like to change your prices by? Enter a negative number if you would like to decrease your prices.");
                
                // dollar_amount stores the amount in dollars that the user wants to change their prices by
                String dollar_amount = s.nextLine();
                s.close();
                try(Connection connection = DriverManager.getConnection(connectionUrl)){

                    String query1 = "update b set b.price = b.price + ? from body b inner join brand x on b.brand = x.id where x.name = ?;";
                    PreparedStatement statement1 = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
                    statement1.setString(1, dollar_amount);
                    statement1.setString(2, brand);
                    statement1.executeQuery();

                    String query2 = "update lens set lens.price = lens.price + ? from lens inner join brand on lens.brand = brand.id where brand.name = ?;";
                    PreparedStatement statement2 = connection.prepareStatement(query2, Statement.RETURN_GENERATED_KEYS);
                    statement2.setString(1, dollar_amount);
                    statement2.setString(2, brand);
                    statement2.executeQuery();

                    if(Integer.parseInt(dollar_amount) > 0){
                        System.out.println("\n" + brand + "'s prices have been increased by $" + dollar_amount + ".");
                    }

                    else{
                        System.out.println("\n" + brand + "'s prices have been decreased by $" + dollar_amount.substring(1) + ".");
                    }
                    break;
                }
       
                catch(Exception e){
                    e.printStackTrace();
                }

            case 2:
                Scanner s1 = new Scanner(System.in);
                System.out.println("\nBy what percentage would you like to change your prices? Enter a negative number to decrease your prices.");
                String percent = s1.nextLine();
                s1.close();
                try(Connection connection = DriverManager.getConnection(connectionUrl)){
                    String query1 = "update b set b.price = b.price * (1 + ? / 100) from body b inner join brand x on b.brand = x.id where x.name = ?;";
                    PreparedStatement statement1 = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
                    statement1.setString(1, percent);
                    statement1.setString(2, brand);
                    statement1.executeQuery();

                    String query2 = "update b set b.price = b.price * (1 + ? / 100) from body b inner join brand x on b.brand = x.id where x.name = ?;";
                    PreparedStatement statement2 = connection.prepareStatement(query2, Statement.RETURN_GENERATED_KEYS);
                    statement2.setString(1, percent);
                    statement2.setString(2, brand);
                    statement2.executeQuery();

                    if(Integer.parseInt(percent) > 0){
                        System.out.println("\n" + brand + "'s prices have been increased by " + percent + "%.");
                    }

                    else{
                        System.out.println("\n" + brand + "'s prices have been decreased by " + percent.substring(1) + "%.");
                    }

                    break;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
        }
        scan.close();




    }

    public static void method6(){

        Scanner scan = new Scanner(System.in);

        System.out.println("\nWhat is the name of your brand?");
        String brand = scan.nextLine();

        System.out.println("\n1. Camera Body");
        System.out.println("2. Lens");
        System.out.println("3. Gear");
        System.out.println("\nWhat type of product would you like to add to the market? Please type the number corresponding to your choice.");

        int choice = scan.nextInt();
        scan.nextLine();

        ResultSet result = null;


        switch(choice){

            case 1:
                System.out.println("\nWhat is the name of the model you are adding?");
                String name = scan.nextLine();
                System.out.println("\nWhat is the price of the model you are adding (in dollars)?");
                int price = scan.nextInt();
                scan.nextLine();
                System.out.println("\nWhat is the id number of the lens that comes with your camera body? Press 'enter' if no lens comes with your camera.");
                String response = scan.nextLine();
                int kit_lens = 0;

                if(!response.equals("")){
                    kit_lens = Integer.parseInt(response);
                }
       

                try(Connection connection = DriverManager.getConnection(connectionUrl)){
                    String query1 = "insert into body(brand, model, price, kit_lens) values((select id from brand where name = ?), ?, ?, ?);";
                    PreparedStatement statement1 = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
                    statement1.setString(1, brand);
                    statement1.setString(2, name);
                    statement1.setInt(3, price);
                    if(response.equals("")){
                        statement1.setNull(4, Types.INTEGER);
                    }
                    else{
                        statement1.setInt(4, kit_lens);
                    }
                    result = statement1.executeQuery();
                    while(result.next()){
                        System.out.println("\nYour camera body has been added to the database. Its id number is: " + result.getString(1));
                    }
                    break;
                   
                }
                catch(Exception e){
                    e.printStackTrace();
                }
           
            case 2:
                System.out.println("\nWhat is the price of your lens?");
                int lens_price = scan.nextInt();
                scan.nextLine();
                System.out.println("\nWhat is the focal length of your lens?");
                String focal_length = scan.nextLine();
                System.out.println("\n1. Prime \n2. Zoom");
                System.out.println("\nWhat type of lens are you adding? Please enter the number corresponding to your choice.");
                String type = null;

                if(scan.nextInt() == 1){
                    type = "prime";
                }
                else{
                    type = "zoom";
                }

                System.out.println("\nWhat is the aperture of your lens?");
                float aperture = scan.nextFloat();

                try(Connection connection = DriverManager.getConnection(connectionUrl)){
                    String query1 = "insert into lens(brand, price, focal_len, type, aperature) values((select id from brand where name = ?), ?, ?, ?, ?);";
                    PreparedStatement statement1 = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
                    statement1.setString(1, brand);
                    statement1.setInt(2, lens_price);
                    statement1.setString(3, focal_length);
                    statement1.setString(4, type);
                    statement1.setFloat(5, aperture);

                    result = statement1.executeQuery();
                    while(result.next()){
                        System.out.println("\nYour lens has been added to the database. Its id number is: " + result.getString(1));
                    }
                    break;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
           
            case 3:
                System.out.println("\nWhat is the price of your gear?");
                int gear_price = scan.nextInt();
                scan.nextLine();
                System.out.println("\n1. Tripod\n2. Filter\n3. Lens Cap\n4. Flash\n5. Adapter");
                System.out.println("\nWhat type of gear are you adding? Please type the number corresponding to the correct gear type.");
                String gear_type = null;

                switch(scan.nextInt()){
                    case 1:
                        gear_type = "tripod";
                        break;
                    case 2:
                        gear_type = "filter";
                        break;
                    case 3:
                        gear_type = "lens cap";
                        break;
                    case 4:
                        gear_type = "flash";
                        break;
                    case 5:
                        gear_type = "adapter";
                        break;
                    
                    
                }

                try(Connection connection = DriverManager.getConnection(connectionUrl)){
                    String query1 = "insert into gear(price, type) values(?, ?);";
                    PreparedStatement statement1 = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
                    statement1.setInt(1, gear_price);
                    statement1.setString(2, gear_type);
                    result = statement1.executeQuery();

                    while(result.next()){
                        System.out.println("\nYour " + gear_type + " has been added to the database. Its id number is: " + result.getString(1));
                    }
                    
                    break;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
               
        }
    }

}