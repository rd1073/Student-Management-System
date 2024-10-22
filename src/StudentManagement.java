import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.*;
import java.util.Scanner;

public class StudentManagement {
    // JDBC URL, username, and password
    private static final String URL = "jdbc:postgresql://localhost:5432/recipeapp";
    private static final String USER = "postgres";  // Replace with your PostgreSQL username
    private static final String PASSWORD = "golobull";  // Replace with your PostgreSQL password

    public static void main(String[] args) {
        // Test the connection
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to the PostgreSQL database!");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\nChoose an operation:");
                System.out.println("1. Add Student");
                System.out.println("2. View All Students");
                System.out.println("3. Update Student");
                System.out.println("4. Delete Student");
                System.out.println("5. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (choice) {
                    case 1 -> addStudent(conn, scanner);
                    case 2 -> viewStudents(conn);
                    case 3 -> updateStudent(conn, scanner);
                    case 4 -> deleteStudent(conn, scanner);
                    case 5 -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to PostgreSQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add a student to the database
    private static void addStudent(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter student name: ");
            String name = scanner.nextLine();
            System.out.print("Enter student age: ");
            int age = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter student grade: ");
            String grade = scanner.nextLine();

            String sql = "INSERT INTO student(name, age, grade) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, grade);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Student added successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to add student.");
            e.printStackTrace();
        }
    }

    // View all students from the database
    private static void viewStudents(Connection connection) {
        try {
            String sql = "SELECT * FROM student";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("ID | Name | Age | Grade");
            System.out.println("-----------------------");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String grade = resultSet.getString("grade");

                System.out.printf("%d | %s | %d | %s%n", id, name, age, grade);
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve students.");
            e.printStackTrace();
        }
    }

    // Update a student's details
    private static void updateStudent(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter student ID to update: ");
            int id = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter new name: ");
            String name = scanner.nextLine();
            System.out.print("Enter new age: ");
            int age = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter new grade: ");
            String grade = scanner.nextLine();

            String sql = "UPDATE student SET name = ?, age = ?, grade = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, grade);
            statement.setInt(4, id);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Student updated successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to update student.");
            e.printStackTrace();
        }
    }

    // Delete a student from the database
    private static void deleteStudent(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter student ID to delete: ");
            int id = scanner.nextInt();

            String sql = "DELETE FROM student WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Student deleted successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to delete student.");
            e.printStackTrace();
        }
    }
}

/*public class StudentManagement {
    // JDBC URL, username, and password
    private static final String URL = "jdbc:postgresql://localhost:5432/recipeapp";
    private static final String USER = "postgres";
    private static final String PASSWORD = "golobull";

    public static void main(String[] args) {
        // Test the connection
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Connected to the PostgreSQL database!");
            
        } catch (SQLException e) {
            System.out.println("Error connecting to PostgreSQL: " + e.getMessage());
        }
    }}
   private static void addStudent(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter student name: ");
            String name = scanner.nextLine();
            System.out.print("Enter student age: ");
            int age = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter student grade: ");
            String grade = scanner.nextLine();

            String sql = "INSERT INTO students (name, age, grade) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, grade);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Student added successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to add student.");
            e.printStackTrace();
        }
    }

    private static void viewStudents(Connection connection) {
        try {
            String sql = "SELECT * FROM students";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("ID | Name | Age | Grade");
            System.out.println("-----------------------");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String grade = resultSet.getString("grade");

                System.out.printf("%d | %s | %d | %s%n", id, name, age, grade);
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve students.");
            e.printStackTrace();
        }
    }

    private static void updateStudent(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter student ID to update: ");
            int id = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter new name: ");
            String name = scanner.nextLine();
            System.out.print("Enter new age: ");
            int age = scanner.nextInt();
            scanner.nextLine();  // Consume newline
            System.out.print("Enter new grade: ");
            String grade = scanner.nextLine();

            String sql = "UPDATE students SET name = ?, age = ?, grade = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, grade);
            statement.setInt(4, id);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Student updated successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to update student.");
            e.printStackTrace();
        }
    }

    private static void deleteStudent(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter student ID to delete: ");
            int id = scanner.nextInt();

            String sql = "DELETE FROM students WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Student deleted successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Failed to delete student.");
            e.printStackTrace();
        }
    }
}*/


