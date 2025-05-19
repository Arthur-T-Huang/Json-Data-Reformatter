package com.sandbox2025v2.sandbox2025v2;
import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//executes the program
public class App {
  public static void main(String[] args) throws IOException {
    Converter convert = new Converter();
    Exporter export = new Exporter();
    export.post(convert.getAndConvert());
  }
}

//represents a data class
class Data {
  ArrayList<Roles> roles;
  ArrayList<Reviews> reviews;
  ArrayList<Users> users;

  public Data(ArrayList<Roles> roles, ArrayList<Reviews> reviews, ArrayList<Users> users) {
    this.roles = roles;
    this.reviews = reviews;
    this.users = users;
  }

  // method that finds the average rating for a given roleId
  public double averageRating(int roleId) {
    double average = 0;
    int counter = 0;
    for (Reviews item : this.reviews) {
      if (item.roleId == roleId) {
        average = average + item.overallScore;
        counter = counter + 1;
      }
    }
    if (counter == 0) {
      counter = 1;
    }
    double number = average / counter;
    double numberRounded = Math.round(number * 100.0) / 100.0;
    return numberRounded;

  }

  //method that finds the average pay of a role
  public double averagePay(int roleId) {
    double average = 0;
    int counter = 0;
    for (Reviews item : this.reviews) {
      if (item.roleId == roleId) {
        average = average + item.hourlyPay;
        counter = counter + 1;
      }
    }
    if (counter == 0) {
      counter = 1;
    }
    double number = average / counter;
    double numberRounded = Math.round(number * 100.0) / 100.0;
    return numberRounded;

  }

  //creates a list of companies 
  public ArrayList<Company> createCompanies() {
    ArrayList<String> seenCompanies = new ArrayList<String>();
    ArrayList<Company> companies = new ArrayList<Company>();
    for (Roles item : this.roles) {
      if (!seenCompanies.contains(item.company)) {
        companies.add(new Company(item.company, this.createRoleFinals(item.company)));
        seenCompanies.add(item.company);
      }

    }
    return companies;
  }

  //creates a list of roles that is structured in the desired output format
  public ArrayList<RoleFinal> createRoleFinals(String companyName) {
    ArrayList<Integer> seenRoles = new ArrayList<Integer>();
    ArrayList<RoleFinal> roles = new ArrayList<RoleFinal>();
    for (Roles item : this.roles) {
      if (!seenRoles.contains(item.roleId) && item.company.equals(companyName)) {
        roles
            .add(new RoleFinal(item.role, item.roleId, this.createReviewFinals(item.roleId), this));
        seenRoles.add(item.roleId);
      }
    }
    return roles;
  }
  
  //creates a list of reviews that is structured in the desired output format
  public ArrayList<ReviewFinal> createReviewFinals(int roleId) {
    ArrayList<ReviewFinal> reviews = new ArrayList<ReviewFinal>();
    ArrayList<Integer> seenReviews = new ArrayList<Integer>();
    for (Reviews item : this.reviews) {
      if (!seenReviews.contains(item.ratingId) && item.roleId == roleId) {
        this.addInOrder(item, reviews);
      }
    }
    
    return reviews;
    
    
  }
  
  //adds reviews in order to the list of reviews based on their rating
  //from highest to lowest rating
  public void addInOrder(Reviews input, ArrayList<ReviewFinal> output) {
    boolean added = false;
    for (int i = 0; i < output.size(); i ++) {
      if (output.get(i).rating < input.overallScore ) {
        output.add(i, new ReviewFinal(this.findUser(input.userId), input.overallScore, input.hourlyPay, input.ratingId));
        added = true;
        break;
      }
    }
    if(!added) {
      output.add(new ReviewFinal(this.findUser(input.userId), input.overallScore, input.hourlyPay, input.ratingId));
    }
    
  }

  //returns the name of user of the given user ID
  public String findUser(int userId) {
    String user = "";
    for (Users item : this.users) {
      if (item.userId == userId) {
        return item.nameReturn(userId);
      }
    }
    return user;
  }

}

//represents a role
class Roles {
  String role;
  int roleId;
  String company;

  public Roles(String role, int roleId, String company) {
    this.role = role;
    this.roleId = roleId;
    this.company = company;
  }

}

//represents a review
class Reviews {
  int roleId;
  int ratingId;
  int overallScore;
  int hourlyPay;
  int userId;

  public Reviews(int roleId, int ratingId, int overallScore, int hourlyPay, int userId) {
    this.roleId = roleId;
    this.ratingId = ratingId;
    this.overallScore = overallScore;
    this.hourlyPay = hourlyPay;
    this.userId = userId;
  }
}

//represents a user
class Users {
  String name;
  int userId;
  ArrayList<Integer> reviews;

  public Users(String name, int userId, ArrayList<Integer> reviews) {
    this.name = name;
    this.userId = userId;
    this.reviews = reviews;

  }

  // returns the name of the user if the userId matches the given Id
  //returns "user not found" if the id does not match
  public String nameReturn(int userId) {
    if (this.userId == userId) {
      return this.name;
    }
    else {
      return "user not found";
    }
  }
}

//represents a company
class Company {
  String name;
  ArrayList<RoleFinal> roleFinal;

  public Company(String name, ArrayList<RoleFinal> roleFinal) {
    this.name = name;
    this.roleFinal = roleFinal;
  }

}

//represents a role in the final desired format
class RoleFinal {
  String name;
  int id;
  double avgPay;
  double avgRating;
  ArrayList<ReviewFinal> reviews;

  public RoleFinal(String name, int id, ArrayList<ReviewFinal> reviewFinal, Data data) {
    this.name = name;
    this.id = id;
    this.avgPay = data.averagePay(id);
    this.avgRating = data.averageRating(id);
    this.reviews = reviewFinal;

  }

}

//represents a review in the final desired format
class ReviewFinal {
  String user;
  int rating;
  int pay;
  int review;

  public ReviewFinal(String user, int rating, int pay, int reviewNum) {
    this.user = user;
    this.rating = rating;
    this.pay = pay;
    this.review = reviewNum;
  }
}

//represents a class used to convert json into java
class Converter {
  
  OkHttpClient client = new OkHttpClient().newBuilder().build();
  MediaType mediaType = MediaType.parse("text/plain");
  
  //takes in a list of companies, maps out each company and its role,
  //and converts the output into a json string
  public static String convertToJson(ArrayList<Company> companies) {
    HashMap<String, HashMap<String, RoleFinal>> companiesMap = new HashMap<>();

    for (Company company : companies) {
      HashMap<String, RoleFinal> rolesMap = new HashMap<>();
      for (RoleFinal role : company.roleFinal) {
        rolesMap.put(role.name, role);
      }
      companiesMap.put(company.name, rolesMap);
    }

    HashMap<String, Object> finalMap = new HashMap<>();
    finalMap.put("companies", companiesMap);

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    return gson.toJson(finalMap);

  }
  
  //handles the get request, maps the retrived json to the initial set of java classes
  //converts the data to the second set of java classes that matches the desired format
  //converts the final form data into a json string.
  //throws IOE exception if there is an error with the GET request attempt
  public String getAndConvert() throws IOException {

    Request request = new Request.Builder().url(
        "https://recruitment.sandboxnu.com/api/eyJkYXRhIjp7ImNoYWxsZW5nZSI6IkNvb3BlciIsImVtYWlsIjoiaHVhbmcuYXJ0aEBub3J0aGVhc3Rlcm4uZWR1IiwiZHVlRGF0ZSI6IjIwMjUtMDUtMTNUMDU6MDA6MDAuMDAwWiJ9LCJoYXNoIjoid0s3VmZ4RWdETFVxOXJaWTVuTSJ9")
        .method("GET", null).build();
    Response response = client.newCall(request).execute();
    if (!response.isSuccessful()) {
      throw new IOException("Unexpected code " + response);
    }
    String responseString = response.body().string();
    Reader reader = new StringReader(responseString);
    

    // conversion to java and re-conversion back to a string
    Gson gson = new Gson();
    Data dataFinal = gson.fromJson(reader, Data.class);
    ArrayList<Company> companies = dataFinal.createCompanies();
    String json = Converter.convertToJson(companies);
    System.out.println(json);
    return json;
  }
  

  
}

//class used for exporting the result back to the original link
class Exporter {
  
  OkHttpClient client = new OkHttpClient().newBuilder().build();
  MediaType mediaType = MediaType.parse("text/plain");
  
  //takes in a json string input, converts is back to json, and sends
  //the result back to the original link via a  POST request
  //throws IOE exception if the post content does not match the expected result
  public void post(String json) throws IOException {
    // post request
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body2 = RequestBody.create(json, mediaType);
    Request request2 = new Request.Builder().url(
        "https://recruitment.sandboxnu.com/api/eyJkYXRhIjp7ImNoYWxsZW5nZSI6IkNvb3BlciIsImVtYWlsIjoiaHVhbmcuYXJ0aEBub3J0aGVhc3Rlcm4uZWR1IiwiZHVlRGF0ZSI6IjIwMjUtMDUtMTNUMDU6MDA6MDAuMDAwWiJ9LCJoYXNoIjoid0s3VmZ4RWdETFVxOXJaWTVuTSJ9")
        .method("POST", body2).build();
    Response response2 = client.newCall(request2).execute();
    if (!response2.isSuccessful()) {
      throw new IOException("Unexpected code " + response2);
    }
  }
  
}