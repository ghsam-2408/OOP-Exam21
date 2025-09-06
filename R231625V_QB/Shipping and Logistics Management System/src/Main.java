
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<Customer> customerList = new ArrayList<>();
    private static List<VehicleDetails> vehicleList = new ArrayList<>();
    private static List<DriverDetails> driverList = new ArrayList<>();
    private static int customerCounter = 1; // Counter for generating customer IDs

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nShipping and Logistics Management System");
            System.out.println("1. Add Customer");
            System.out.println("2. Add Vehicle");
            System.out.println("3. Add Driver");
            System.out.println("4. Request Shipment");
            System.out.println("5. Assign Vehicle and Driver to Shipment");
            System.out.println("6. Update Shipment Status");
            System.out.println("7. Exit");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addCustomer(scanner);
                    break;
                case 2:
                    addVehicle(scanner);
                    break;
                case 3:
                    addDriver(scanner);
                    break;
                case 4:
                    requestShipment(scanner);
                    break;
                case 5:
                    assignVehicleAndDriver(scanner);
                    break;
                case 6:
                    updateShipmentStatus(scanner);
                    break;
                case 7:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void addCustomer(Scanner scanner) {
        String customerId = "customer" + customerCounter++; // Auto-generate Customer ID
        System.out.print("Enter Full Name: ");
        String fullName = scanner.nextLine();
        System.out.print("Enter Contact Info: ");
        String contactInfo = scanner.nextLine();
        System.out.print("Is this a corporate customer? (yes/no): ");
        String type = scanner.nextLine();

        if (type.equalsIgnoreCase("yes")) {
            CorporateCustomer corporateCustomer = new CorporateCustomer(customerId, fullName, contactInfo);
            customerList.add(corporateCustomer);
            System.out.println("Success: Corporate customer added with ID: " + customerId);
        } else {
            System.out.print("Enter National Identity: ");
            String nationalIdentity = scanner.nextLine();
            IndividualCustomer individualCustomer = new IndividualCustomer(customerId, fullName, contactInfo, nationalIdentity);
            customerList.add(individualCustomer);
            System.out.println("Success: Individual customer added with ID: " + customerId);
        }
    }

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

        VehicleDetails vehicle;
        if (type.equalsIgnoreCase("yes")) {
            vehicle = new HeavyTruck(vehicleId, model, capacity);
        } else {
            vehicle = new LightTruck(vehicleId, model, capacity);
        }
        vehicleList.add(vehicle);
        System.out.println("Success: Vehicle added.");
    }

    private static void addDriver(Scanner scanner) {
        System.out.print("Enter Driver ID: ");
        String driverId = scanner.nextLine();
        System.out.print("Enter Driver Name: ");
        String driverName = scanner.nextLine();
        System.out.print("Enter License ID: ");
        String licenseId = scanner.nextLine();
        DriverDetails driverDetails = new DriverDetails(driverId, driverName, licenseId);
        driverList.add(driverDetails);
        System.out.println("Success: Driver added.");
    }

    private static void requestShipment(Scanner scanner) {
        System.out.print("Enter Shipment ID: ");
        String shipmentId = scanner.nextLine();
        System.out.print("Enter Item Description: ");
        String itemDescription = scanner.nextLine();
        System.out.print("Enter Item Count: ");
        int itemCount = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter Origin: ");
        String origin = scanner.nextLine();
        System.out.print("Enter Destination: ");
        String destination = scanner.nextLine();

        System.out.println("Select Customer:");
        for (int i = 0; i < customerList.size(); i++) {
            System.out.println((i + 1) + ". " + customerList.get(i).getFullName());
        }
        int customerIndex = scanner.nextInt() - 1;
        Customer customer = customerList.get(customerIndex);
        ShipmentDetails shipmentDetails = new ShipmentDetails(shipmentId, itemDescription, itemCount, origin, destination, customer);
        customer.addShipment(shipmentDetails);
        System.out.println("Success: Shipment requested.");
    }

    private static void assignVehicleAndDriver(Scanner scanner) {
        System.out.print("Enter Shipment ID to assign vehicle and driver: ");
        String shipmentId = scanner.nextLine();
        ShipmentDetails shipmentDetails = null;

        for (Customer customer : customerList) {
            for (ShipmentDetails s : customer.getShipments()) {
                if (s.getShipmentId().equals(shipmentId)) {
                    shipmentDetails = s;
                    break;
                }
            }
        }

        if (shipmentDetails != null) {
            System.out.println("Select Vehicle:");
            for (int i = 0; i < vehicleList.size(); i++) {
                System.out.println((i + 1) + ". " + vehicleList.get(i).getVehicleModel());
            }
            int vehicleIndex = scanner.nextInt() - 1;
            VehicleDetails vehicle = vehicleList.get(vehicleIndex);

            System.out.println("Select Driver:");
            for (int i = 0; i < driverList.size(); i++) {
                System.out.println((i + 1) + ". " + driverList.get(i).getDriverName());
            }
            int driverIndex = scanner.nextInt() - 1;
            DriverDetails driver = driverList.get(driverIndex);

            try {
                // First, assign the driver to the vehicle
                driver.assignVehicle(vehicle);
                // Then, add the shipment to the vehicle
                vehicle.addShipment(shipmentDetails);
                // Set the assigned vehicle and update the shipment status
                shipmentDetails.setAssignedVehicle(vehicle);
                shipmentDetails.setCurrentStatus("processed");
                System.out.println("Vehicle and Driver assigned to shipment.");
            } catch (LoadExceededException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Shipment not found.");
        }
    }


    private static void updateShipmentStatus(Scanner scanner) {
        System.out.print("Enter Shipment ID to update status: ");
        String shipmentId = scanner.nextLine();
        ShipmentDetails shipmentDetails = null;

        for (Customer customer : customerList) {
            for (ShipmentDetails s : customer.getShipments()) {
                if (s.getShipmentId().equals(shipmentId)) {
                    shipmentDetails = s;
                    break;
                }
            }
        }

        if (shipmentDetails != null) {
            System.out.print("Enter new status (in transit/delivered): ");
            String status = scanner.nextLine();
            shipmentDetails.setCurrentStatus(status);
            System.out.println("Shipment status updated to: " + status);
        } else {
            System.out.println("Shipment not found.");
        }
    }
}
