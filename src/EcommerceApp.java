import java.sql.*;
import java.util.Scanner;

public class EcommerceApp {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);  // Enable manual transactions

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n1. Add Product");
                System.out.println("2. Update Product Stock");
                System.out.println("3. View All Products");
                System.out.println("4. Create Order");
                System.out.println("5. Cancel Order");
                System.out.println("6. Batch Update Prices");
                System.out.println("7. Add Customer");
                System.out.println("8. View Customers");
                System.out.println("9. Update Customer");
                System.out.println("10. Delete Customer");
                System.out.println("11. Add Seller");
                System.out.println("12. View Sellers");
                System.out.println("13. Update Seller");
                System.out.println("14. Delete Seller");
                System.out.println("15. Exit");
                System.out.print("Choose an option: ");
            
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline
            
                switch (choice) {
                    case 1 -> addProduct(conn, scanner);
                    case 2 -> updateProductStock(conn, scanner);
                    case 3 -> viewProducts(conn);
                    case 4 -> createOrder(conn, scanner);
                    case 5 -> cancelOrder(conn, scanner);
                    case 6 -> batchUpdatePrices(conn, scanner);
                    case 7 -> addCustomer(conn, scanner);
                    case 8 -> viewCustomers(conn);
                    case 9 -> updateCustomer(conn, scanner);
                    case 10 -> deleteCustomer(conn, scanner);
                    case 11 -> addSeller(conn, scanner);
                    case 12 -> viewSellers(conn);
                    case 13 -> updateSeller(conn, scanner);
                    case 14 -> deleteSeller(conn, scanner);
                    case 15 -> {
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid option. Try again.");
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error connecting to PostgreSQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addProduct(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();
        System.out.print("Enter price: ");
        double price = scanner.nextDouble();
        System.out.print("Enter stock quantity: ");
        int stock = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.print("Enter seller ID: ");
        int sellerId = scanner.nextInt();

        String sql = "INSERT INTO products (product_name, price, stock_quantity, seller_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, stock);
            stmt.setInt(4, sellerId);
            stmt.executeUpdate();
            conn.commit();
            System.out.println("Product added successfully.");
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

    private static void updateProductStock(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter product ID: ");
        int productId = scanner.nextInt();
        System.out.print("Enter new stock quantity: ");
        int stock = scanner.nextInt();

        String sql = "UPDATE products SET stock_quantity = ? WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, stock);
            stmt.setInt(2, productId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                conn.commit();
                System.out.println("Product stock updated.");
            } else {
                System.out.println("Product not found.");
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

    private static void viewProducts(Connection conn) throws SQLException {
        String sql = "SELECT * FROM products";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("ID | Name | Price | Stock | Seller ID");
            System.out.println("--------------------------------------");
            while (rs.next()) {
                System.out.printf("%d | %s | %.2f | %d | %d%n",
                        rs.getInt("product_id"), rs.getString("product_name"),
                        rs.getDouble("price"), rs.getInt("stock_quantity"),
                        rs.getInt("seller_id"));
            }
        }
    }

    private static void createOrder(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter customer ID: ");
        int customerId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String sql = "INSERT INTO orders (customer_id, status) VALUES (?, 'Pending') RETURNING order_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int orderId = rs.getInt(1);
                System.out.println("Order created with ID: " + orderId);
            }
        }
    }

    private static void cancelOrder(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter order ID to cancel: ");
        int orderId = scanner.nextInt();

        String sql = "UPDATE orders SET status = 'Cancelled' WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                conn.commit();
                System.out.println("Order cancelled.");
            } else {
                System.out.println("Order not found.");
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

    private static void batchUpdatePrices(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter new price for multiple products.");
        String sql = "UPDATE products SET price = ? WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);  // Start transaction
            for (int i = 0; i < 3; i++) {  // Simulate batch update for 3 products
                System.out.print("Enter product ID: ");
                int productId = scanner.nextInt();
                System.out.print("Enter new price: ");
                double price = scanner.nextDouble();
                stmt.setDouble(1, price);
                stmt.setInt(2, productId);
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
            System.out.println("Prices updated successfully.");
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }
    //crus for customers
    private static void addCustomer(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();
        System.out.print("Enter customer email: ");
        String email = scanner.nextLine();
    
        String sql = "INSERT INTO customers (customer_name, email) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();
            conn.commit();
            System.out.println("Customer added successfully.");
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }
    
    private static void viewCustomers(Connection conn) throws SQLException {
        String sql = "SELECT * FROM customers";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("ID | Name | Email");
            System.out.println("--------------------------");
            while (rs.next()) {
                System.out.printf("%d | %s | %s%n",
                        rs.getInt("customer_id"), rs.getString("customer_name"),
                        rs.getString("email"));
            }
        }
    }
    
    private static void updateCustomer(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter customer ID to update: ");
        int customerId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
    
        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new email: ");
        String email = scanner.nextLine();
    
        String sql = "UPDATE customers SET customer_name = ?, email = ? WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setInt(3, customerId);
    
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                conn.commit();
                System.out.println("Customer updated successfully.");
            } else {
                System.out.println("Customer not found.");
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }
    
    private static void deleteCustomer(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter customer ID to delete: ");
        int customerId = scanner.nextInt();
    
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                conn.commit();
                System.out.println("Customer deleted successfully.");
            } else {
                System.out.println("Customer not found.");
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }

    //crud for sellers
    private static void addSeller(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter seller name: ");
        String name = scanner.nextLine();
        System.out.print("Enter contact information: ");
        String contactInfo = scanner.nextLine();
    
        String sql = "INSERT INTO sellers (seller_name, contact_info) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, contactInfo);
            stmt.executeUpdate();
            conn.commit();
            System.out.println("Seller added successfully.");
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }
    
    private static void viewSellers(Connection conn) throws SQLException {
        String sql = "SELECT * FROM sellers";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("ID | Name | Contact Info");
            System.out.println("--------------------------");
            while (rs.next()) {
                System.out.printf("%d | %s | %s%n",
                        rs.getInt("seller_id"), rs.getString("seller_name"),
                        rs.getString("contact_info"));
            }
        }
    }
    
    private static void updateSeller(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter seller ID to update: ");
        int sellerId = scanner.nextInt();
        scanner.nextLine();  // Consume newline
    
        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new contact information: ");
        String contactInfo = scanner.nextLine();
    
        String sql = "UPDATE sellers SET seller_name = ?, contact_info = ? WHERE seller_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, contactInfo);
            stmt.setInt(3, sellerId);
    
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                conn.commit();
                System.out.println("Seller updated successfully.");
            } else {
                System.out.println("Seller not found.");
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }
    
    private static void deleteSeller(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter seller ID to delete: ");
        int sellerId = scanner.nextInt();
    
        String sql = "DELETE FROM sellers WHERE seller_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sellerId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                conn.commit();
                System.out.println("Seller deleted successfully.");
            } else {
                System.out.println("Seller not found.");
            }
        } catch (SQLException e) {
            conn.rollback();
            e.printStackTrace();
        }
    }
    
    
}
