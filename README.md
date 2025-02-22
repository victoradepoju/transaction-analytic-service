# Transaction Analytics Project

This project is a Java (Spring Boot) application designed to analyze transaction data from files stored in a folder. It provides various endpoints to calculate metrics such as the highest sales volume in a day, the highest sales value in a day, the most sold product by volume, the highest sales staff by month, and the highest hour of the day by average transaction volume.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Setup and Installation](#setup-and-installation)
3. [Running the Project](#running-the-project)
4. [Endpoints](#endpoints)
5. [Example Usage](#example-usage)

---

## Project Overview

The project is built using **Spring Boot** and provides a REST API to analyze transaction data. The data is stored in files, where each file represents transactions for a specific day. The application reads these files, processes the data, and provides insights through various endpoints.

### Key Features:
- **Highest Sales Volume in a Day**: Calculates the highest total sales volume across all days.
- **Highest Sales Value in a Day**: Finds the highest individual sales value across all days.
- **Most Sold Product by Volume**: Determines the product ID with the highest total quantity sold.
- **Highest Sales Staff by Month**: Identifies the staff ID with the highest total sales volume for each month.
- **Highest Hour by Average Transaction Volume**: Finds the hour of the day with the highest average transaction volume.

---

## Setup and Installation

### Prerequisites

- **Java Development Kit (JDK) 17 or higher**
- **Maven** (for building the project)
- **Git** (for cloning the repository)

### Steps

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/victoradepoju/transaction-analytic-service.git
   cd transaction-analytic
   ```

2. **Build the Project**:
   ```bash
   mvn clean install
   ```

3. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the Application**:
   The application will be running at `http://localhost:8080`.

---

## Running the Project

### Folder Structure for Transaction Files

Place your transaction files in a folder. Each file should represent transactions for a specific day. The files should be in the following format:

```
<transactionId>,<timestamp>,<items>,<saleAmount>
```

Example:
```
1,2025-01-01T12:40:45,[857749:6|804084:5|505009:2],5676.010
2,2025-01-01T09:17:54,[865779:1|10792:9|471499:8],39642.009
```

### Running the Application

1. Start the application using the command:
   ```bash
   mvn spring-boot:run
   ```

2. Use a tool like **Postman** or **cURL** to interact with the API.

---

## Endpoints

The following endpoints are available:

### 1. Analyze Transactions
- **Endpoint**: `POST /api/transaction-analysis/analyze-transactions?folderPath=/path/to/transaction/files`
- **Description**: Analyzes all transaction files in the folder and returns a summary of metrics.
- **Response**:
  ```json
  {
    "highestSalesVolumeInADay": 78050.70,
    "highestSalesValueInADay": 56270.94,
    "mostSoldProductByVolume": "10792",
    "highestSalesStaffByMonth": {
      "2025-01": "2",
      "2025-02": "4"
    },
    "highestHourByAverageTransactionVolume": 12
  }
  ```

### 2. Only Highest Sales Volume in a Day
- **Endpoint**: `POST /api/transaction-analysis/highest-sales-volume?folderPath=/path/to/transaction/files`
- **Description**: Returns the highest total sales volume across all days.
- **Response**:
  ```json
  78050.70
  ```

### 3. Only Highest Sales Value in a Day
- **Endpoint**: `POST /api/transaction-analysis/highest-sales-value?folderPath=/path/to/transaction/files`
- **Description**: Returns the highest individual sales value across all days.
- **Response**: 56270.94
  

### 4. Only Most Sold Product by Volume
- **Endpoint**: `POST /api/transaction-analysis/most-sold-product?folderPath=/path/to/transaction/files`
- **Description**: Returns the product ID with the highest total quantity sold.
- **Response**: 10792

### 5. Only Highest Sales Staff by Month
- **Endpoint**: `POST /api/transaction-analysis/highest-sales-staff-by-month?folderPath=/path/to/transaction/files`
- **Description**: Returns the staff ID with the highest total sales volume for each month.
- **Response**:
  ```json
	"2025-07": "6",
	"2025-05": "8",
	"2025-09": "7",
	"2025-10": "2",
	"2025-06": "2",
	"2025-08": "3",
	"2025-01": "8",
	"2025-02": "5",
	"2025-11": "7",
	"2025-12": "1",
	"2025-04": "3",
	"2025-03": "8"
  ```

### 6. Highest Hour by Average Transaction Volume
- **Endpoint**: `POST /api/transaction-analysis/highest-hour-by-average-volume?folderPath=/path/to/transaction/files`
- **Description**: Returns the hour of the day with the highest average transaction volume.
- **Response**: 12

---

## Example Usage

### Using cURL

For easy testing, a list of transaction files is already provided in the `src/main/resources/transactions` folder of the Spring application. You can use the following absolute path for testing on a Mac (uses appropriate absolute path for windows):

```
/Users/username/your-project-folder/transaction-analytic/src/main/resources/transactions
```

If you want to use your own transaction files, ensure you provide the absolute path to the folder containing the files.

1. **Analyze Transactions**:
   ```bash
   curl -X POST "http://localhost:8080/api/transaction-analysis/analyze-transactions?folderPath=/Users/username/your-project-folder/transaction-analytic/src/main/resources/transactions"
   ```

2. **Highest Sales Volume in a Day**:
   ```bash
   curl -X POST "http://localhost:8080/api/transaction-analysis/highest-sales-volume?folderPath=/Users/username/your-project-folder/transaction-analytic/src/main/resources/transactions"
   ```

3. **Highest Sales Value in a Day**:
   ```bash
   curl -X POST "http://localhost:8080/api/transaction-analysis/highest-sales-value?folderPath=/Users/username/your-project-folder/transaction-analytic/src/main/resources/transactions"
   ```

4. **Most Sold Product by Volume**:
   ```bash
   curl -X POST "http://localhost:8080/api/transaction-analysis/most-sold-product?folderPath=/Users/username/your-project-folder/transaction-analytic/src/main/resources/transactions"
   ```

5. **Highest Sales Staff by Month**:
   ```bash
   curl -X POST "http://localhost:8080/api/transaction-analysis/highest-sales-staff-by-month?folderPath=/Users/username/your-project-folder/transaction-analytic/src/main/resources/transactions"
   ```

6. **Highest Hour by Average Transaction Volume**:
   ```bash
   curl -X POST "http://localhost:8080/api/transaction-analysis/highest-hour-by-average-volume?folderPath=/Users/username/your-project-folder/transaction-analytic/src/main/resources/transactions"
   ```

---


Thank you Moniepoint! 🚀
