import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<Customer> customerList = new ArrayList<>();
    private static List<VehicleDetails> vehicleList = new ArrayList<>();
    private static List<DriverDetails> driverList = new ArrayList<>();
    private static CustomerDAO customerDAO;
    private static VehicleDAO vehicleDAO;
    private static DriverDAO driverDAO;
    private static ShipmentDAO shipmentDAO;

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/Details";
        String user = "sam";
        String pass = "sam2408";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Connected to database!");

            // Create DAO objects - this will also create tables if they don't exist
            customerDAO = new CustomerDAO(conn);
            vehicleDAO = new VehicleDAO(conn);
            driverDAO = new DriverDAO(conn);
            shipmentDAO = new ShipmentDAO(conn);
            System.out.println("Database tables initialized successfully!");

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n=== Shipping and Logistics Management System ===");
                System.out.println("1. Add Customer");
                System.out.println("2. View All Customers");
                System.out.println("3. Update Customer");
                System.out.println("4. Delete Customer");
                System.out.println("5. Add Vehicle");
                System.out.println("6. View All Vehicles");
                System.out.println("7. Update Vehicle");
                System.out.println("8. Delete Vehicle");
                System.out.println("9. Add Driver");
                System.out.println("10. View All Drivers");
                System.out.println("11. Update Driver");
                System.out.println("12. Delete Driver");
                System.out.println("13. Request Shipment");
                System.out.println("14. View All Shipments");
                System.out.println("15. Assign Vehicle and Driver to Shipment");
                System.out.println("16. Update Shipment Status");
                System.out.println("17. Delete Shipment");
                System.out.println("18. Exit");
                System.out.print("Choose option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1: addCustomer(scanner); break;
                    case 2: viewAllCustomers(); break;
                    case 3: updateCustomer(scanner); break;
                    case 4: deleteCustomer(scanner); break;
                    case 5: addVehicle(scanner); break;
                    case 6: viewAllVehicles(); break;
                    case 7: updateVehicle(scanner); break;
                    case 8: deleteVehicle(scanner); break;
                    case 9: addDriver(scanner); break;
                    case 10: viewAllDrivers(); break;
                    case 11: updateDriver(scanner); break;
                    case 12: deleteDriver(scanner); break;
                    case 13: requestShipment(scanner); break;
                    case 14: viewAllShipments(); break;
                    case 15: assignVehicleAndDriver(scanner); break;
                    case 16: updateShipmentStatus(scanner); break;
                    case 17: deleteShipment(scanner); break;
                    case 18:
                        System.out.println("Exiting...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            }
        } catch (Exception ex) {
            System.out.println("Database connection or initialization failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Customer CRUD Operations
    private static void addCustomer(Scanner scanner) {
        System.out.print("Enter Full Name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter Contact Info (10 digits): ");
        String contactInfo = scanner.nextLine();

        // Validate contact info before proceeding
        if (!contactInfo.matches("\\d{10}")) {
            System.out.println("Error: Contact info must be exactly 10 digits. Please try again.");
            return;
        }

        System.out.print("Is this a corporate customer? (yes/no): ");
        String type = scanner.nextLine();

        try {
            // Get next customer ID from database
            String customerId = customerDAO.getNextCustomerId();

            if (type.equalsIgnoreCase("yes")) {
                CorporateCustomer corporateCustomer = new CorporateCustomer(customerId, fullName, contactInfo);
                customerDAO.insertCustomer(corporateCustomer);
                customerList.add(corporateCustomer);
                System.out.println("Success: Corporate customer added with ID: " + customerId + " (DB ID: " + corporateCustomer.getId() + ")");
            } else {
                System.out.print("Enter National Identity: ");
                String nationalIdentity = scanner.nextLine();
                IndividualCustomer individualCustomer = new IndividualCustomer(customerId, fullName, contactInfo, nationalIdentity);
                customerDAO.insertCustomer(individualCustomer);
                customerList.add(individualCustomer);
                System.out.println("Success: Individual customer added with ID: " + customerId + " (DB ID: " + individualCustomer.getId() + ")");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error adding customer to database: " + e.getMessage());
        }
    }

    private static void viewAllCustomers() {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            if (customers.isEmpty()) {
                System.out.println("No customers found.");
                return;
            }

            System.out.println("\n=== All Customers ===");
            for (Customer customer : customers) {
                System.out.println("ID: " + customer.getCustomerId() +
                                 " | Name: " + customer.getFullName() +
                                 " | Contact: " + customer.getContactInfo() +
                                 " | Type: " + (customer instanceof CorporateCustomer ? "Corporate" : "Individual"));
            }
        } catch (Exception e) {
            System.out.println("Error retrieving customers: " + e.getMessage());
        }
    }

    private static void updateCustomer(Scanner scanner) {
        System.out.print("Enter Customer ID to update: ");
        String customerId = scanner.nextLine();

        try {
            Customer customer = customerDAO.selectCustomerById(customerId);
            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }

            System.out.println("Current Name: " + customer.getFullName());
            System.out.print("Enter new Full Name (or press Enter to keep current): ");
            String newName = scanner.nextLine();
            if (!newName.trim().isEmpty()) {
                customer.setFullName(newName);
            }

            System.out.println("Current Contact: " + customer.getContactInfo());
            System.out.print("Enter new Contact Info (10 digits, or press Enter to keep current): ");
            String newContact = scanner.nextLine();
            if (!newContact.trim().isEmpty()) {
                if (!newContact.matches("\\d{10}")) {
                    System.out.println("Error: Contact info must be exactly 10 digits. Update cancelled.");
                    return;
                }
                customer.setContactInfo(newContact);
            }

            customerDAO.updateCustomer(customer);
            System.out.println("Success: Customer updated with ID: " + customerId);

        } catch (IllegalArgumentException e) {
            System.out.println("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error updating customer: " + e.getMessage());
        }
    }

    private static void deleteCustomer(Scanner scanner) {
        System.out.print("Enter Customer ID to delete: ");
        String customerId = scanner.nextLine();

        try {
            customerDAO.deleteCustomer(customerId);
            System.out.println("Success: Customer deleted with ID: " + customerId);
        } catch (Exception e) {
            System.out.println("Error deleting customer: " + e.getMessage());
        }
    }

    // Vehicle CRUD Operations
    private static void addVehicle(Scanner scanner) {
        System.out.print("Enter Vehicle ID: ");
        String vehicleId = scanner.nextLine();
        System.out.print("Enter Model: ");
        String model = scanner.nextLine();
        System.out.print("Enter Capacity: ");
        double capacity = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        System.out.print("Is this a heavy vehicle? (yes/no): ");
        String type = scanner.nextLine();

        try {
            VehicleDetails vehicle;
            if (type.equalsIgnoreCase("yes")) {
                vehicle = new HeavyTruck(vehicleId, model, capacity);
            } else {
                vehicle = new LightTruck(vehicleId, model, capacity);
            }

            vehicleDAO.insertVehicle(vehicle);
            vehicleList.add(vehicle);
            System.out.println("Success: Vehicle added with ID: " + vehicleId + " (DB ID: " + vehicle.getId() + ")");
        } catch (Exception e) {
            System.out.println("Error adding vehicle to database: " + e.getMessage());
        }
    }

    private static void viewAllVehicles() {
        try {
            List<VehicleDetails> vehicles = vehicleDAO.getAllVehicles();
            if (vehicles.isEmpty()) {
                System.out.println("No vehicles found.");
                return;
            }

            System.out.println("\n=== All Vehicles ===");
            for (VehicleDetails vehicle : vehicles) {
                System.out.println("ID: " + vehicle.getVehicleId() +
                                 " | Model: " + vehicle.getVehicleModel() +
                                 " | Capacity: " + vehicle.getCapacity() +
                                 " | Type: " + (vehicle instanceof HeavyTruck ? "Heavy" : "Light"));
            }
        } catch (Exception e) {
            System.out.println("Error retrieving vehicles: " + e.getMessage());
        }
    }

    private static void updateVehicle(Scanner scanner) {
        System.out.print("Enter Vehicle ID to update: ");
        String vehicleId = scanner.nextLine();

        try {
            VehicleDetails vehicle = vehicleDAO.selectVehicleById(vehicleId);
            if (vehicle == null) {
                System.out.println("Vehicle not found.");
                return;
            }

            System.out.println("Current Model: " + vehicle.getVehicleModel());
            System.out.print("Enter new Model: ");
            String newModel = scanner.nextLine();

            System.out.println("Current Capacity: " + vehicle.getCapacity());
            System.out.print("Enter new Capacity: ");
            double newCapacity = scanner.nextDouble();
            scanner.nextLine();

            // Create updated vehicle object and update in database
            VehicleDetails updatedVehicle;
            if (vehicle instanceof HeavyTruck) {
                updatedVehicle = new HeavyTruck(vehicleId, newModel, newCapacity);
            } else {
                updatedVehicle = new LightTruck(vehicleId, newModel, newCapacity);
            }

            vehicleDAO.updateVehicle(updatedVehicle);
            System.out.println("Success: Vehicle updated with ID: " + vehicleId);

        } catch (Exception e) {
            System.out.println("Error updating vehicle: " + e.getMessage());
        }
    }

    private static void deleteVehicle(Scanner scanner) {
        System.out.print("Enter Vehicle ID to delete: ");
        String vehicleId = scanner.nextLine();

        try {
            vehicleDAO.deleteVehicle(vehicleId);
            System.out.println("Success: Vehicle deleted with ID: " + vehicleId);
        } catch (Exception e) {
            System.out.println("Error deleting vehicle: " + e.getMessage());
        }
    }

    // Driver CRUD Operations
    private static void addDriver(Scanner scanner) {
        System.out.print("Enter Driver ID: ");
        String driverId = scanner.nextLine();
        System.out.print("Enter Driver Name: ");
        String driverName = scanner.nextLine();
        System.out.print("Enter License ID: ");
        String licenseId = scanner.nextLine();

        try {
            DriverDetails driverDetails = new DriverDetails(driverId, driverName, licenseId);
            driverDAO.insertDriver(driverDetails);
            driverList.add(driverDetails);
            System.out.println("Success: Driver added with ID: " + driverId + " (DB ID: " + driverDetails.getId() + ")");
        } catch (Exception e) {
            System.out.println("Error adding driver to database: " + e.getMessage());
        }
    }

    private static void viewAllDrivers() {
        try {
            List<DriverDetails> drivers = driverDAO.getAllDrivers();
            if (drivers.isEmpty()) {
                System.out.println("No drivers found.");
                return;
            }

            System.out.println("\n=== All Drivers ===");
            for (DriverDetails driver : drivers) {
                System.out.println("ID: " + driver.getDriverId() +
                                 " | Name: " + driver.getDriverName() +
                                 " | License: " + driver.getLicenseId() +
                                 " | Available: " + (driver.isAvailable() ? "Yes" : "No"));
            }
        } catch (Exception e) {
            System.out.println("Error retrieving drivers: " + e.getMessage());
        }
    }

    private static void updateDriver(Scanner scanner) {
        System.out.print("Enter Driver ID to update: ");
        String driverId = scanner.nextLine();

        try {
            DriverDetails driver = driverDAO.selectDriverById(driverId);
            if (driver == null) {
                System.out.println("Driver not found.");
                return;
            }

            System.out.println("Current Name: " + driver.getDriverName());
            System.out.print("Enter new Driver Name: ");
            String newName = scanner.nextLine();

            System.out.println("Current License: " + driver.getLicenseId());
            System.out.print("Enter new License ID: ");
            String newLicense = scanner.nextLine();

            System.out.println("Current Availability: " + (driver.isAvailable() ? "Available" : "Not Available"));
            System.out.print("Is driver available? (yes/no): ");
            boolean newAvailability = scanner.nextLine().equalsIgnoreCase("yes");

            // Create updated driver object and update in database
            DriverDetails updatedDriver = new DriverDetails(driverId, newName, newLicense);
            updatedDriver.setAvailable(newAvailability);

            driverDAO.updateDriver(updatedDriver);
            System.out.println("Success: Driver updated with ID: " + driverId);

        } catch (Exception e) {
            System.out.println("Error updating driver: " + e.getMessage());
        }
    }

    private static void deleteDriver(Scanner scanner) {
        System.out.print("Enter Driver ID to delete: ");
        String driverId = scanner.nextLine();

        try {
            driverDAO.deleteDriver(driverId);
            System.out.println("Success: Driver deleted with ID: " + driverId);
        } catch (Exception e) {
            System.out.println("Error deleting driver: " + e.getMessage());
        }
    }

    // Shipment CRUD Operations
    private static void requestShipment(Scanner scanner) {
        System.out.print("Enter Shipment ID: ");
        String shipmentId = scanner.nextLine();
        System.out.print("Enter Item Description: ");
        String itemDescription = scanner.nextLine();
        System.out.print("Enter Item Count: ");
        int itemCount = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Origin: ");
        String origin = scanner.nextLine();
        System.out.print("Enter Destination: ");
        String destination = scanner.nextLine();

        try {
            // Load customers from database
            List<Customer> databaseCustomers = customerDAO.getAllCustomers();

            if (databaseCustomers.isEmpty()) {
                System.out.println("No customers found in database. Please add a customer first.");
                return;
            }

            System.out.println("Select Customer from database:");
            for (int i = 0; i < databaseCustomers.size(); i++) {
                Customer customer = databaseCustomers.get(i);
                System.out.println((i + 1) + ". ID: " + customer.getCustomerId() +
                                 " - Name: " + customer.getFullName() +
                                 " - Type: " + (customer instanceof CorporateCustomer ? "Corporate" : "Individual"));
            }

            System.out.print("Enter customer number: ");
            int customerIndex = scanner.nextInt() - 1;
            scanner.nextLine(); // Consume newline

            if (customerIndex < 0 || customerIndex >= databaseCustomers.size()) {
                System.out.println("Invalid customer selection.");
                return;
            }

            Customer selectedCustomer = databaseCustomers.get(customerIndex);
            ShipmentDetails shipmentDetails = new ShipmentDetails(shipmentId, itemDescription, itemCount, origin, destination, selectedCustomer);

            shipmentDAO.insertShipment(shipmentDetails);
            System.out.println("Success: Shipment requested with ID: " + shipmentId +
                             " for customer: " + selectedCustomer.getFullName() +
                             " (DB ID: " + shipmentDetails.getId() + ")");

        } catch (Exception e) {
            System.out.println("Error creating shipment: " + e.getMessage());
        }
    }

    private static void viewAllShipments() {
        try {
            List<ShipmentDetails> shipments = shipmentDAO.getAllShipments();
            if (shipments.isEmpty()) {
                System.out.println("No shipments found.");
                return;
            }

            System.out.println("\n=== All Shipments ===");
            for (ShipmentDetails shipment : shipments) {
                System.out.println("ID: " + shipment.getShipmentId() +
                                 " | Description: " + shipment.getItemDescription() +
                                 " | Count: " + shipment.getItemCount() +
                                 " | Origin: " + shipment.getOrigin() +
                                 " | Destination: " + shipment.getDestination() +
                                 " | Status: " + shipment.getCurrentStatus() +
                                 " | Customer: " + shipment.getCustomer().getFullName());
            }
        } catch (Exception e) {
            System.out.println("Error retrieving shipments: " + e.getMessage());
        }
    }

    private static void deleteShipment(Scanner scanner) {
        System.out.print("Enter Shipment ID to delete: ");
        String shipmentId = scanner.nextLine();

        try {
            // Get assigned driver ID before deletion to make them available again
            String assignedDriverId = shipmentDAO.getAssignedDriverId(shipmentId);

            shipmentDAO.deleteShipment(shipmentId);

            // If there was an assigned driver, make them available again
            if (assignedDriverId != null) {
                driverDAO.updateDriverAvailability(assignedDriverId, true);
                System.out.println("Driver " + assignedDriverId + " is now available again.");
            }

            System.out.println("Success: Shipment deleted with ID: " + shipmentId);
        } catch (Exception e) {
            System.out.println("Error deleting shipment: " + e.getMessage());
        }
    }

    private static void assignVehicleAndDriver(Scanner scanner) {
        System.out.print("Enter Shipment ID to assign vehicle and driver: ");
        String shipmentId = scanner.nextLine();

        try {
            // Check if shipment exists in database
            ShipmentDetails shipmentDetails = shipmentDAO.selectShipmentById(shipmentId);
            if (shipmentDetails == null) {
                System.out.println("Shipment not found in database.");
                return;
            }

            System.out.print("Enter Vehicle ID: ");
            String vehicleId = scanner.nextLine();

            // Check if vehicle exists
            if (!vehicleDAO.checkVehicleExists(vehicleId)) {
                System.out.println("Vehicle not found in database.");
                return;
            }

            // Check if vehicle is available
            if (!shipmentDAO.isVehicleAvailable(vehicleId)) {
                System.out.println("Vehicle is currently assigned to another shipment.");
                return;
            }

            // Check vehicle capacity
            double vehicleCapacity = vehicleDAO.getVehicleCapacity(vehicleId);
            double currentLoad = shipmentDAO.getVehicleCurrentLoad(vehicleId);
            if (currentLoad + shipmentDetails.getItemCount() > vehicleCapacity) {
                System.out.println("Vehicle capacity exceeded. Current load: " + currentLoad +
                                 ", Capacity: " + vehicleCapacity + ", Requested: " + shipmentDetails.getItemCount());
                return;
            }

            System.out.print("Enter Driver ID: ");
            String driverId = scanner.nextLine();

            // Check if driver exists
            if (!driverDAO.checkDriverExists(driverId)) {
                System.out.println("Driver not found in database.");
                return;
            }

            // Check if driver is available
            if (!shipmentDAO.isDriverAvailable(driverId)) {
                System.out.println("Driver is not available.");
                return;
            }

            // Assign vehicle and driver
            shipmentDAO.assignVehicleAndDriver(shipmentId, vehicleId, driverId);

            // Update driver availability
            driverDAO.updateDriverAvailability(driverId, false);

            System.out.println("Success: Vehicle " + vehicleId + " and Driver " + driverId + " assigned to shipment " + shipmentId);

        } catch (Exception e) {
            System.out.println("Error assigning vehicle and driver: " + e.getMessage());
        }
    }

    private static void updateShipmentStatus(Scanner scanner) {
        System.out.print("Enter Shipment ID to update status: ");
        String shipmentId = scanner.nextLine();

        try {
            // Check if shipment exists
            ShipmentDetails shipmentDetails = shipmentDAO.selectShipmentById(shipmentId);
            if (shipmentDetails == null) {
                System.out.println("Shipment not found in database.");
                return;
            }

            System.out.println("Current status: " + shipmentDetails.getCurrentStatus());
            System.out.print("Enter new status (in transit/delivered): ");
            String newStatus = scanner.nextLine();

            // Validate status
            if (!newStatus.equals("in transit") && !newStatus.equals("delivered")) {
                System.out.println("Invalid status. Please enter 'in transit' or 'delivered'.");
                return;
            }

            // Update status in database
            shipmentDAO.updateShipmentStatus(shipmentId, newStatus);

            // If delivered, make driver available again
            if (newStatus.equals("delivered")) {
                String assignedDriverId = shipmentDAO.getAssignedDriverId(shipmentId);
                if (assignedDriverId != null) {
                    driverDAO.updateDriverAvailability(assignedDriverId, true);
                    System.out.println("Driver " + assignedDriverId + " is now available again.");
                }
            }

            System.out.println("Success: Shipment status updated to: " + newStatus);

        } catch (Exception e) {
            System.out.println("Error updating shipment status: " + e.getMessage());
        }
    }
}
