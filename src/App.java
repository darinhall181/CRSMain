import java.sql.*;
import java.util.*;

public class App {
   
    static String connectionUrl = "jdbc:sqlserver://cxp-sql-02\\dah181;"
    + "database=CRS;"
    + "user=sa;"
    + "password=atnBEYX2pP0n3U;"
    + "encrypt=true;"
    + "trustServerCertificate=true;"
    + "loginTimeout=15;";
   
    public static void main(String[] args){

        // print out the options
        System.out.println("1. You are a company that is closing down and would like to delete its catalog from the database.");
        System.out.println("2. You are looking for a camera and would like to see the cheapest and most expensive camera from every brand.");
        System.out.println("3. You are a customer and would like to find a camera based on your preferences.");
        System.out.println("4. You are a customer and would like to know the average price of lenses with a specified aperture.");
        System.out.println("5. You are a company that would like to reduce the prices of your catalog by a specified amount for a sale.");
        System.out.println("6. You are a company that would like to add a new product to the market.");
        System.out.println("7. Exit");

        System.out.println("\nPlease enter the number corresponding to your desired function.");
        Scanner s = new Scanner(System.in);
        int choice = s.nextInt();
       
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
        }
       
        s.close();

    }

    public static void method1(){

        System.out.println("\nWhat is the name of your brand?");
        Scanner scan = new Scanner(System.in);
        String brand = scan.nextLine();
        scan.close();

        ResultSet result = null;

        try(Connection connection = DriverManager.getConnection(connectionUrl)){
            String query1 = "select id from brand where name = ?;";
            PreparedStatement statement1 = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
            statement1.setString(1, brand);
            result = statement1.executeQuery();
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
        System.out.println("What is your budget?");
    }

    public static void method4(){
       
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
        System.out.println("What is the name of your brand?");
        String brand = scan.nextLine();
        System.out.println("Would you like to reduce your prices by a dollar amount or a percentage? Choose the number corresponding to your choice.\n1. Dollar \n2. Percentage");
        int choice = scan.nextInt();

        switch(choice){
            case 1:
                Scanner s = new Scanner(System.in);
                System.out.println("How much would you like to change your prices by? Enter a negative number if you would like to decrease your prices.");
                String dollar_amount = s.nextLine();
                s.close();
                try(Connection connection = DriverManager.getConnection(connectionUrl)){

                    String query1 = "update b set b.price = b.price + ? from body b inner join brand x on b.brand = x.id where x.name = ?;";
                    PreparedStatement statement1 = connection.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
                    statement1.setString(1, dollar_amount);
                    statement1.setString(2, brand);
                    statement1.executeQuery();

                    String query2 = "update lens set lens.price = lens.price + ? where brand = ?;";
                    PreparedStatement statement2 = connection.prepareStatement(query2, Statement.RETURN_GENERATED_KEYS);
                    statement2.setString(1, dollar_amount);
                    statement2.setString(2, brand);
                    statement2.executeQuery();

                    System.out.println("Your prices have been updated accordingly.");

                }
       
                catch(Exception e){
                    e.printStackTrace();
                }

            case 2:
                Scanner s1 = new Scanner(System.in);
                System.out.println("By what percentage would you like to change your prices? Enter a negative number to decrease your prices.");
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
                }
                catch(Exception e){
                    e.printStackTrace();
                }
        }
       
        System.out.println("Your prices have been updated accordingly.");
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
                    case 2:
                        gear_type = "filter";
                    case 3:
                        gear_type = "lens cap";
                    case 4:
                        gear_type = "flash";
                    case 5:
                        gear_type = "adapter";
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
                }
                catch(Exception e){
                    e.printStackTrace();
                }
               
        }
    }

}