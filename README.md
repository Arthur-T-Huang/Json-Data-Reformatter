# JSON Data Reformatter 

This Java application is designed to process job review data by fetching JSON from a recruitment API, transforming it into a structured company-role-review hierarchy, and posting the reformatted data back to the API. The application specializes in aggregating review data, calculating averages, and organizing information by companies and roles.

## Features

- **API Integration**: Fetches data from and posts results to recruitment API endpoints
- **JSON Transformation**: Converts flat JSON data into nested company-role-review structures
- **Data Aggregation**: Calculates average ratings and pay for each role
- **Review Sorting**: Orders reviews by rating (highest to lowest)
- **Pay-to-Rating Analysis**: Identifies roles with the best pay-to-rating ratios
- **Pretty JSON Output**: Formats output JSON with proper indentation

## Project Structure

```
sandbox2025v2/
├── src/main/java/com/sandbox2025v2/sandbox2025v2/
│   └── App.java                    # Main application file
├── pom.xml                         # Maven dependencies (assumed)
└── README.md                       # This file
```

## Prerequisites

- **Java**: JDK 8 or higher
- **Maven**: For dependency management
- **Internet Connection**: Required for API calls

## Dependencies

The application uses the following external libraries:
- **Gson** (`com.google.gson`): For JSON parsing and generation
- **OkHttp** (`com.squareup.okhttp3`): For HTTP requests

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Arthur-T-Huang/Json-Data-Reformatter.git
   cd Json-Data-Reformatter/sandbox2025v2
   ```

2. **Build the project**:
   ```bash
   mvn compile
   ```

3. **Run the application**:
   ```bash
   mvn exec:java -Dexec.mainClass="com.sandbox2025v2.sandbox2025v2.App"
   ```

   Or compile and run directly:
   ```bash
   javac -cp "path/to/gson.jar:path/to/okhttp.jar" App.java
   java -cp ".:path/to/gson.jar:path/to/okhttp.jar" App
   ```

## How It Works

### Data Flow

1. **Fetch**: Application makes a GET request to retrieve raw JSON data containing roles, reviews, and users
2. **Transform**: Raw data is processed and restructured into a hierarchical format
3. **Aggregate**: Calculates average ratings and pay for each role
4. **Sort**: Orders reviews within each role by rating (highest first)
5. **Analyze**: Determines the role with the best pay-to-rating ratio for each company
6. **Export**: Posts the transformed JSON back to the API

### Input Data Structure

The application expects JSON with three main arrays:
- `roles`: Job roles with company information
- `reviews`: Individual reviews with ratings and pay data
- `users`: User information for review attribution

### Output Data Structure

The application generates a nested JSON structure:
```json
{
  "companies": {
    "CompanyName": {
      "maxRatingToPay": 12345,
      "RoleName": {
        "name": "Software Engineer",
        "id": 1,
        "avgPay": 75.50,
        "avgRating": 4.2,
        "reviews": [
          {
            "user": "John Doe",
            "rating": 5,
            "pay": 80,
            "review": 101
          }
        ]
      }
    }
  }
}



