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
        System.out.println("1. You are a company that is closing down and would like to delete its catalog from the database.");
        System.out.println("2. You are looking for a camera and would like to see the cheapest and most expensive camera from every brand.");
        System.out.println("3. You are a customer and would like to find a camera based on your preferences.");
        System.out.println("4. You are a customer and would like to know the average price of lenses with a specified aperture.");
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

    // method that deletes every model produced by a given company from the database
    public static void method1(){

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

            //
            String brand_id = null;
            while(result.next()){
                brand_id = result.getString(1);
            }

            String query2 = "delete from body where brand = ?;";
            PreparedStatement statement2 = connection.prepareStatement(query2);
            statement2.setString(1, brand_id);
            statement2.execute();

            String query3 = "delete from lens where brand = ?;";
            PreparedStatement statement3 = connection.prepareStatement(query3);
            statement3.setString(1, brand_id);
            statement3.execute();

            String query4 = "delete from brand where name = ?;";
            PreparedStatement statement4 = connection.prepareStatement(query4);
            statement4.setString(1, brand);
            statement4.execute();

        }
        catch(Exception e){
            e.printStackTrace();
        }

        System.out.println("\nYour brand's data has been deleted.");

    }

    public static void method2(){
        System.out.println("\nWould you like to know the cheapest and most expensive camera body from every brand? Type yes to continue.");
        Scanner scan = new Scanner(System.in);
        String answer = scan.nextLine();
        scan.close();

        ResultSet result = null;

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
        else{
            System.out.println("Okay, please choose another option from the menu.");
        }
    }

    public static void method3(){
        Scanner scan = new Scanner(System.in);

        System.out.println("\nWhat is your budget?");
        int budget = scan.nextInt();
        scan.nextLine();

        System.out.println("\n1. Beginner\n2. Advanced\n\nPlease choose your skill level. Type the number corresponding to your choice and press enter.");
        int skill = scan.nextInt();
        scan.nextLine();

        switch(skill){
            case 1:
                System.out.println("\n1. Sports\n2. Portrait\n3. Landscape\n4. Wildlife\n5. Events");
                System.out.println("\nWhat are you mostly going to be shooting? Please type the name of your choice and hit enter.");
                String topic = scan.nextLine();

                String brand1 = null;
                String brand2 = null;
                String brand3 = null;

                int[] brand_ids = new int[3];

                int flend1 = 0;
                int flend2 = 0;


                switch(topic){
                    case "Sports":
                        brand1 = "Canon";
                        brand2 = "Fujifilm";
                        brand3 = "Sony";

                        flend1 = 71;
                        flend2 = 600;

                        try(Connection connection = DriverManager.getConnection(connectionUrl)){
                            ResultSet result = null;
                            String query = "select id from brand where brand.name = ? or brand.name = ? or brand.name = ?;";
                            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                            statement.setString(1, brand1);
                            statement.setString(2, brand2);
                            statement.setString(3, brand3);

                            result = statement.executeQuery();
                            
                            int index = 0;
                            while(result.next()){
                                brand_ids[index] = result.getInt(1);
                                index++;
                            }


                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        
                        break;
                        
                    case "Portrait":
                        brand1 = "Canon";
                        brand2 = "Nikon";
                        brand3 = "Leica";

                        flend1 = 36;
                        flend2 = 70;

                        try(Connection connection = DriverManager.getConnection(connectionUrl)){
                            ResultSet result = null;
                            String query = "select id from brand where brand.name = ? or brand.name = ? or brand.name = ?;";
                            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                            statement.setString(1, brand1);
                            statement.setString(2, brand2);
                            statement.setString(3, brand3);

                            result = statement.executeQuery();
                            
                            int index = 0;
                            while(result.next()){
                                brand_ids[index] = result.getInt(1);
                                index++;
                            }


                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }

                        break;

                    case "Landscape":
                        brand1 = "Fujifilm";
                        brand2 = "Sony";
                        brand3 = "Pentax";

                        flend1 = 0;
                        flend2 = 35;

                        try(Connection connection = DriverManager.getConnection(connectionUrl)){
                            ResultSet result = null;
                            String query = "select id from brand where brand.name = ? or brand.name = ? or brand.name = ?;";
                            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                            statement.setString(1, brand1);
                            statement.setString(2, brand2);
                            statement.setString(3, brand3);

                            result = statement.executeQuery();
                            
                            int index = 0;
                            while(result.next()){
                                brand_ids[index] = result.getInt(1);
                                index++;
                            }


                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }

                        break;

                    case "Wildlife":
                        brand1 = "Olympus";
                        brand2 = "Panasonic";
                        brand3 = "Nikon";

                        flend1 = 200;
                        flend2 = 600;

                        try(Connection connection = DriverManager.getConnection(connectionUrl)){
                            ResultSet result = null;
                            String query = "select id from brand where brand.name = ? or brand.name = ? or brand.name = ?;";
                            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                            statement.setString(1, brand1);
                            statement.setString(2, brand2);
                            statement.setString(3, brand3);

                            result = statement.executeQuery();
                            
                            int index = 0;
                            while(result.next()){
                                brand_ids[index] = result.getInt(1);
                                index++;
                            }


                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }

                        break;

                    case "Events":
                        brand1 = "Canon";
                        brand2 = "GoPro";
                        brand3 = "Sony";

                        flend1 = 0;
                        flend2 = 35;

                        try(Connection connection = DriverManager.getConnection(connectionUrl)){
                            ResultSet result = null;
                            String query = "select id from brand where brand.name = ? or brand.name = ? or brand.name = ?;";
                            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                            statement.setString(1, brand1);
                            statement.setString(2, brand2);
                            statement.setString(3, brand3);

                            result = statement.executeQuery();
                            
                            int index = 0;
                            while(result.next()){
                                brand_ids[index] = result.getInt(1);
                                index++;
                            }


                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }

                        break;

                }

                try(Connection connection = DriverManager.getConnection(connectionUrl)){
                    ResultSet result = null;
                    String query = "select brand.name, body.model, lens.FL_start, lens.FL_end, (body.price + lens.price) as total_price "
                    + "from brand inner join body on brand.id = body.brand inner join body_lens on body.id = body_lens.body_id "
                    + "inner join lens on body_lens.lens_id = lens.id where lens.FL_end >= ? and lens.FL_end <= ? and "
                    + "(lens.price + body.price <= ?) and (brand.id = ? or brand.id = ? or brand.id = ?) order by total_price;";
                    PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    System.out.println(flend1);
                    System.out.println(flend2);
                    statement.setInt(1, flend1);
                    statement.setInt(2, flend2);
                    statement.setInt(3, budget);
                    statement.setInt(4, brand_ids[0]);
                    statement.setInt(5, brand_ids[1]);
                    statement.setInt(6, brand_ids[2]);

                    result = statement.executeQuery();

                    System.out.println("\nHere are all of the camera/lens combinations from cheapest to most expensive that are good for shooting "
                     + topic.toLowerCase() + " that are under your budget.\n");

                    while(result.next()){
                        System.out.println("Brand: " + result.getString(1) + " | Model: " + result.getString(2)
                        + " | Focal Length: " + result.getString(3) + "-" + result.getString(4)
                        + " | Total Price: " + result.getString(5));
                    }
                }

                catch(Exception e){
                    e.printStackTrace();
                }


               

                break;
            case 2:
                System.out.println("\n1. Canon\n2. Fujifilm\n3. Leica\n4. Nikon\n5. Sony\n6. Pentax\n7. Olympus\n8. GoPro\n9. Panasonic");
                System.out.println("\nPlease type the name of the brand above that you would like to use and hit enter.");
                String body_brand = scan.nextLine();

                int brand_id = 0;

                try(Connection connection = DriverManager.getConnection(connectionUrl)){
                    String query = "select * from body inner join brand on body.brand = brand.id "
                    + " inner join body_lens on body_lens.body_id = body.id inner join lens on "
                    + "body_lens.lens_id = lens.id where brand.name = ? and (body.price + lens.price) <= ?;";
                    PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    statement.setString(1, body_brand);
                    statement.setInt(2, budget);
                    ResultSet result = statement.executeQuery();

                    System.out.println("\nHere are all of " + body_brand + "'s camera/lens combos that fit your budget: ");


                    if(!result.next()){
                        System.out.println("\n" + body_brand + " has no models within your budget.");
                    }
                    else{
                        while(result.next()){
                            brand_id = result.getInt(2);
                            System.out.println("\n" + "ID: " + result.getString(1) +
                            " | Model: " + result.getString(3) + " | Focal Length: " + 
                            result.getString(15) + "-" + result.getString(16) + " | Aperture: "
                            + result.getString(18) + " | Price: $" + (result.getInt(4) + result.getInt(14)));
                        }
                    }
                }

                catch(Exception e){
                    e.printStackTrace();
                }

                System.out.println("\n1. New camera and lens\n2. New lens only\n\n"
                +"Are you looking for a new camera and a new lens or a new lens only?"
                +" Please type the number corresponding to your choice.");

                int choice = scan.nextInt();
                scan.nextLine();

                System.out.println("\n1. Wide\n2. Standard\n3. Telephoto\n\nPlease type the number "
                + "corresponding to the type of lens you would like and hit enter.");

                int lens_type = scan.nextInt();
                scan.nextLine();

                int flstart = 0;
                int flend = 0;

                switch(lens_type){
                    case 1:
                        flstart = 0;
                        flend = 35;
                        break;
                    case 2:
                        flstart = 36;
                        flend = 70;
                        break;
                    case 3:
                        flstart = 71;
                        flend = 600;
                        break;
                }

                try(Connection connection = DriverManager.getConnection(connectionUrl)){
                    String query;
                    if(choice == 1){
                        query = "select brand.name, body.model, lens.FL_start, lens.FL_end, (body.price + lens.price) as total"
                        + " from brand inner join body on brand.id = body.brand inner join body_lens on body.id = body_lens.body_id"
                        + " inner join lens on body_lens.lens_id = lens.id where lens.FL_end >= ? and lens.FL_end <= ? and"
                        + " body.price + lens.price <= ? and brand.id = ? order by (body.price + lens.price);";
                    }
                    else{
                        query = "select brand.name, lens.FL_start, lens.FL_end, lens.price"
                        + " from brand inner join lens on body_lens.lens_id = lens.id where lens.FL_end >= ? and lens.FL_end <= ? and"
                        + " lens.price <= ? and brand.id = ? order by lens.price;";
                    }

                    PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    statement.setInt(1, flstart);
                    statement.setInt(2, flend);
                    statement.setInt(3, budget);
                    statement.setInt(4, brand_id);


                    if(choice == 1){
                        System.out.println("\nHere are all of the camera/lens combos that fit your parameters, from cheapest to most expensive.\n");
                    }

                    else{
                        System.out.println("\nHere are all of the lenses that fit your parameters, from cheapest to most expensive.\n");
                    }

                    ResultSet result = statement.executeQuery();

                    if (!result.isBeforeFirst() ) {    
                        System.out.println("There were no products that matched your parameters :( Please try again."); 
                    } 
                    
                    while(result.next()){
                        System.out.println("Brand: " + result.getString(1) + " | Model: "
                        + result.getString(2) + " | Focal Length: " + result.getString(3) + "-"
                        + result.getString(4) + " | Total Price: $" + result.getString(5));
                    }
                }

                catch(Exception e){
                    e.printStackTrace();
                }

                break;
        }
        
        scan.close();
    }

    public static void method4(){
       System.out.println("Here are our picks for the top camera/lens combinations from each camera company!");
    }

    /*
    public static void method4(){
        System.out.println("What aperture would you like to evaluate?");
        Scanner scan = new Scanner(System.in);
        String aperture = scan.nextLine();
        scan.close();

        ResultSet result = null;

        try(Connection connection = DriverManager.getConnection(connectionUrl)){
            String query = "select avg(price) as avg_price\r\n" + //
                    "from lens\r\n" + //
                    "where aperature = ?;";
            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, aperture);
            result = statement.executeQuery();
            while (result.next()) {
                System.out.println("The average price of lenses with aperture " + aperture.toString() + " is $" + result.getString(1));
            }
        }

        catch(Exception e){
            e.printStackTrace();
        }
    }
    */

    public static void method5(){
        Scanner scan = new Scanner(System.in);
        System.out.println("\nWhat is the name of your brand?");
        String brand = scan.nextLine();
        System.out.println("\nWould you like to reduce your prices by a dollar amount or a percentage? Choose the number corresponding to your choice.\n1. Dollar \n2. Percentage");
        int choice = scan.nextInt();


        switch(choice){
            case 1:
                Scanner s = new Scanner(System.in);
                System.out.println("\nHow much would you like to change your prices by? Enter a negative number if you would like to decrease your prices.");
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