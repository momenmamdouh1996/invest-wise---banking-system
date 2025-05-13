package InvestWise;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Represents a user in the banking application, storing personal information.
 */
class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userID;
    private String name;
    private String email;
    private String password;

    /**
     * Constructs a new User with the specified details.
     *
     * @param userID   the unique identifier for the user
     * @param name     the user's full name
     * @param email    the user's email address
     * @param password the user's password
     */
    public User(int userID, String name, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Gets the user's ID.
     *
     * @return the user's unique identifier
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Gets the user's email address.
     *
     * @return the user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the user's password.
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the user's name.
     *
     * @return the user's full name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of the user.
     *
     * @return a string containing the user's ID, name, and email
     */
    @Override
    public String toString() {
        return "User{ID=" + userID + ", name=" + name + ", email=" + email + "}";
    }
}

/**
 * Represents a financial asset owned by a user.
 */
class Asset implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userID;
    private String type;
    private String name;
    private double value;

    /**
     * Constructs a new Asset with the specified details.
     *
     * @param userID the ID of the user owning the asset
     * @param type   the type of asset (e.g., Stock, Real Estate)
     * @param name   the name of the asset
     * @param value  the monetary value of the asset
     */
    public Asset(int userID, String type, String name, double value) {
        this.userID = userID;
        this.type = type;
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the user ID associated with the asset.
     *
     * @return the user ID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Gets the name of the asset.
     *
     * @return the asset's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of the asset.
     *
     * @return the asset's type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the value of the asset.
     *
     * @return the asset's monetary value
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the value of the asset.
     *
     * @param value the new monetary value
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns a string representation of the asset.
     *
     * @return a string containing the asset's type, name, and value
     */
    @Override
    public String toString() {
        return "[" + type + "] " + name + ": $" + value;
    }
}

/**
 * Interface for managing user operations such as creation and login.
 */
interface IUserManager {
    /**
     * Creates a new user with the specified details.
     *
     * @param name     the user's full name
     * @param email    the user's email address
     * @param password the user's password
     * @return the created User object
     * @throws Exception if the email already exists or an error occurs
     */
    User createUser(String name, String email, String password) throws Exception;

    /**
     * Logs in a user with the specified credentials.
     *
     * @param email    the user's email address
     * @param password the user's password
     * @return the logged-in User object
     * @throws Exception if the credentials are invalid or an error occurs
     */
    User login(String email, String password) throws Exception;
}

/**
 * Interface for persistence operations, handling data storage and retrieval.
 */
interface IPersistenceMechanism {
    /**
     * Validates if an email is unique.
     *
     * @param email the email to validate
     * @return true if the email is unique, false otherwise
     * @throws Exception if an error occurs during validation
     */
    boolean validateData(String email) throws Exception;

    /**
     * Loads all users from storage.
     *
     * @return a list of all User objects
     * @throws Exception if an error occurs during loading
     */
    List<User> loadAllUsers() throws Exception;

    /**
     * Loads all assets from storage.
     *
     * @return a list of all Asset objects
     * @throws Exception if an error occurs during loading
     */
    List<Asset> loadAllAssets() throws Exception;

    /**
     * Saves a user to storage.
     *
     * @param user the User object to save
     * @throws Exception if an error occurs during saving
     */
    void saveUser(User user) throws Exception;

    /**
     * Saves a list of assets to storage.
     *
     * @param assets the list of Asset objects to save
     * @throws Exception if an error occurs during saving
     */
    void saveAssets(List<Asset> assets) throws Exception;

    /**
     * Overwrites the asset storage with the provided list.
     *
     * @param allAssets the list of Asset objects to overwrite
     * @throws Exception if an error occurs during overwriting
     */
    void overwriteAssets(List<Asset> allAssets) throws Exception;
}

/**
 * Implements persistence operations using file-based serialization.
 */
class FilePersistence implements IPersistenceMechanism {
    private static final String USERS_FILE = "users.ser";
    private static final String ASSETS_FILE = "assets.ser";

    /**
     * Loads all users from the users file.
     *
     * @return a list of all User objects
     * @throws Exception if an error occurs during file reading
     */
    @Override
    public List<User> loadAllUsers() throws Exception {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        }
    }

    /**
     * Loads all assets from the assets file.
     *
     * @return a list of all Asset objects
     * @throws Exception if an error occurs during file reading
     */
    @Override
    public List<Asset> loadAllAssets() throws Exception {
        File file = new File(ASSETS_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Asset>) ois.readObject();
        }
    }

    /**
     * Validates if an email is unique among existing users.
     *
     * @param email the email to validate
     * @return true if the email is unique, false if it already exists
     * @throws Exception if an error occurs during validation
     */
    @Override
    public boolean validateData(String email) throws Exception {
        for (User user : loadAllUsers()) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Saves a user to the users file.
     *
     * @param user the User object to save
     * @throws Exception if an error occurs during file writing
     */
    @Override
    public void saveUser(User user) throws Exception {
        List<User> users = loadAllUsers();
        users.add(user);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        }
    }

    /**
     * Saves a list of assets to the assets file.
     *
     * @param newAssets the list of Asset objects to save
     * @throws Exception if an error occurs during file writing
     */
    @Override
    public void saveAssets(List<Asset> newAssets) throws Exception {
        List<Asset> allAssets = loadAllAssets();
        allAssets.addAll(newAssets);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ASSETS_FILE))) {
            oos.writeObject(allAssets);
        }
    }

    /**
     * Overwrites the assets file with the provided list.
     *
     * @param allAssets the list of Asset objects to overwrite
     * @throws Exception if an error occurs during file writing
     */
    @Override
    public void overwriteAssets(List<Asset> allAssets) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ASSETS_FILE))) {
            oos.writeObject(allAssets);
        }
    }
}

/**
 * Manages user-related operations such as creation and login.
 */
class UserManager implements IUserManager {
    private IPersistenceMechanism persistence = new FilePersistence();

    /**
     * Creates a new user with the specified details.
     *
     * @param name     the user's full name
     * @param email    the user's email address
     * @param password the user's password
     * @return the created User object
     * @throws Exception if the email already exists or an error occurs
     */
    @Override
    public User createUser(String name, String email, String password) throws Exception {
        if (!persistence.validateData(email)) {
            throw new Exception("Email already exists!");
        }
        int userID = 1000 + new Random().nextInt(9000);
        User newUser = new User(userID, name, email, password);
        persistence.saveUser(newUser);
        return newUser;
    }

    /**
     * Logs in a user with the specified credentials.
     *
     * @param email    the user's email address
     * @param password the user's password
     * @return the logged-in User object
     * @throws Exception if the credentials are invalid or an error occurs
     */
    @Override
    public User login(String email, String password) throws Exception {
        for (User user : persistence.loadAllUsers()) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new Exception("Invalid email or password!");
    }
}

/**
 * Main application class for the InvestWise banking system.
 */
public class BankingApp {
    private static Scanner sc = new Scanner(System.in);
    private static IPersistenceMechanism persistence = new FilePersistence();
    private static IUserManager userManager = new UserManager();

    /**
     * Main entry point for the banking application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Welcome to InvestWise ===");
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            try {
                if (choice == 1) {
                    signUp();
                } else if (choice == 2) {
                    login();
                } else if (choice == 3) {
                    break;
                } else {
                    System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    /**
     * Clears all user and asset data by deleting storage files.
     */
    static void clearAllData() {
        new File("users.ser").delete();
        new File("assets.ser").delete();
        System.out.println("All user and asset data cleared.");
    }

    /**
     * Handles the user sign-up process.
     *
     * @throws Exception if an error occurs during user creation
     */
    static void signUp() throws Exception {
        String name;
        String email;
        String password;

        while (true) {
            System.out.print("Enter name: ");
            name = sc.nextLine().trim();
            if (!name.isEmpty()) {
                break;
            }
            System.out.println("Name cannot be empty.");
        }

        while (true) {
            System.out.print("Enter email: ");
            email = sc.nextLine().trim();
            if (!email.isEmpty()) {
                break;
            }
            System.out.println("Email cannot be empty.");
        }

        while (true) {
            System.out.print("Enter password: ");
            password = sc.nextLine().trim();
            if (!password.isEmpty()) {
                break;
            }
            System.out.println("Password cannot be empty.");
        }

        User user = userManager.createUser(name, email, password);
        System.out.println("Sign-up successful! Your ID: " + user.getUserID());
        login();
    }

    /**
     * Handles the user login process.
     *
     * @throws Exception if an error occurs during login
     */
    static void login() throws Exception {
        String email;
        String password;
        System.out.println("=== Login ===");

        while (true) {
            System.out.print("Enter email: ");
            email = sc.nextLine().trim();
            if (!email.isEmpty()) {
                break;
            }
            System.out.println("Email cannot be empty.");
        }

        while (true) {
            System.out.print("Enter password: ");
            password = sc.nextLine().trim();
            if (!password.isEmpty()) {
                break;
            }
            System.out.println("Password cannot be empty.");
        }

        User user = userManager.login(email, password);
        System.out.println("Welcome, " + user.getName());
        System.out.println("Your ID: " + user.getUserID());
        userDashboard(user);
    }

    /**
     * Displays the user dashboard and handles user interactions.
     *
     * @param user the logged-in User object
     * @throws Exception if an error occurs during dashboard operations
     */
    static void userDashboard(User user) throws Exception {
        while (true) {
            System.out.println("\n=== Dashboard ===");
            System.out.println("1. Display Portfolio");
            System.out.println("2. Add Asset");
            System.out.println("3. Remove Asset");
            System.out.println("4. Edit Asset");
            System.out.println("5. Calculate Zakat");
            System.out.println("6. Export Financial Report");
            System.out.println("7. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                displayPortfolio(user);
            } else if (choice == 2) {
                addAsset(user);
            } else if (choice == 3) {
                removeAsset(user);
            } else if (choice == 4) {
                editAsset(user);
            } else if (choice == 5) {
                calculateZakat(user);
            } else if (choice == 6) {
                exportFinancialReport(user);
            } else if (choice == 7) {
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    /**
     * Displays the user's portfolio of assets.
     *
     * @param user the User object whose portfolio is displayed
     * @throws Exception if an error occurs during asset retrieval
     */
    static void displayPortfolio(User user) throws Exception {
        List<Asset> allAssets = persistence.loadAllAssets();
        System.out.println("\n=== Portfolio for " + user.getName() + " ===");
        boolean found = false;
        for (Asset asset : allAssets) {
            if (asset.getUserID() == user.getUserID()) {
                System.out.println(asset);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No assets found.");
        }
    }

    /**
     * Allows the user to add a predefined asset to their portfolio.
     *
     * @param user the User object adding the asset
     * @throws Exception if an error occurs during asset addition
     */
    static void addAsset(User user) throws Exception {
        System.out.println("\nChoose asset to add:");
        List<Asset> predefinedAssets = Arrays.asList(
            new Asset(user.getUserID(), "Stock", "Nvidia", 17000),
            new Asset(user.getUserID(), "Real Estate", "Vacation Home", 400000),
            new Asset(user.getUserID(), "Gold", "Gold Necklace", 7000),
            new Asset(user.getUserID(), "Crypto", "Ethereum", 3000),
            new Asset(user.getUserID(), "Stock", "Apple Inc", 15000),
            new Asset(user.getUserID(), "Stock", "Tesla", 10000),
            new Asset(user.getUserID(), "Real Estate", "Rental Apartment", 250000),
            new Asset(user.getUserID(), "Gold", "Gold Bar", 18000),
            new Asset(user.getUserID(), "Crypto", "Bitcoin", 12000)
        );
        for (int i = 0; i < predefinedAssets.size(); i++) {
            System.out.println((i + 1) + ". " + predefinedAssets.get(i));
        }
        System.out.print("Choose asset: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice >= 1 && choice <= predefinedAssets.size()) {
            List<Asset> selected = Collections.singletonList(predefinedAssets.get(choice - 1));
            persistence.saveAssets(selected);
            System.out.println("Asset added.");
        } else {
            System.out.println("Invalid selection.");
        }
        System.out.println("Updated Portfolio.");
        displayPortfolio(user);
    }

    /**
     * Allows the user to remove an asset from their portfolio.
     *
     * @param user the User object removing the asset
     * @throws Exception if an error occurs during asset removal
     */
    static void removeAsset(User user) throws Exception {
        List<Asset> allAssets = persistence.loadAllAssets();
        List<Asset> userAssets = new ArrayList<>();

        System.out.println("\nSelect asset to remove:");
        int index = 1;
        for (Asset asset : allAssets) {
            if (asset.getUserID() == user.getUserID()) {
                System.out.println(index + ". " + asset);
                userAssets.add(asset);
                index++;
            }
        }

        if (userAssets.isEmpty()) {
            System.out.println("No assets to remove.");
            return;
        }

        System.out.print("Enter number to remove: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice >= 1 && choice <= userAssets.size()) {
            allAssets.remove(userAssets.get(choice - 1));
            persistence.overwriteAssets(allAssets);
            System.out.println("Asset removed.");
        } else {
            System.out.println("Invalid choice.");
        }
        System.out.println("Updated Portfolio.");
        displayPortfolio(user);
    }

    /**
     * Allows the user to edit the value of an existing asset.
     *
     * @param user the User object editing the asset
     * @throws Exception if an error occurs during asset editing
     */
    static void editAsset(User user) throws Exception {
        List<Asset> allAssets = persistence.loadAllAssets();
        List<Asset> userAssets = new ArrayList<>();

        System.out.println("\nSelect asset to edit value:");
        int index = 1;
        for (Asset asset : allAssets) {
            if (asset.getUserID() == user.getUserID()) {
                System.out.println(index + ". " + asset);
                userAssets.add(asset);
                index++;
            }
        }

        if (userAssets.isEmpty()) {
            System.out.println("No assets to edit.");
            return;
        }

        System.out.print("Enter number to edit: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice >= 1 && choice <= userAssets.size()) {
            Asset selectedAsset = userAssets.get(choice - 1);

            System.out.print("Enter new value for " + selectedAsset.getName() + ": ");
            double newValue = sc.nextDouble();
            sc.nextLine();

            selectedAsset.setValue(newValue);
            persistence.overwriteAssets(allAssets);
            System.out.println("Asset value updated.");
        } else {
            System.out.println("Invalid selection.");
        }
        System.out.println("Updated Portfolio.");
        displayPortfolio(user);
    }

    /**
     * Calculates and displays the zakat due on the user's assets.
     *
     * @param user the User object whose zakat is calculated
     * @throws Exception if an error occurs during asset retrieval
     */
    static void calculateZakat(User user) throws Exception {
        List<Asset> allAssets = persistence.loadAllAssets();
        double zakatTotal = 0.0;
        System.out.println("\n=== Zakat Calculation ===");
        for (Asset asset : allAssets) {
            if (asset.getUserID() == user.getUserID()) {
                double zakat = asset.getValue() * 0.025; // 2.5%
                System.out.printf("%s: $%.2f x 2.5%% = $%.2f\n", asset.getName(), asset.getValue(), zakat);
                zakatTotal += zakat;
            }
        }
        System.out.printf("Total Zakat Due: $%.2f\n", zakatTotal);
    }

    /**
     * Exports a financial report of the user's assets to a file.
     * Note: The method contains a redundant loop that duplicates console output.
     * Consider refactoring to remove the duplicate loop for better efficiency.
     *
     * @param user the User object whose report is exported
     * @throws Exception if an error occurs during file writing
     */
    static void exportFinancialReport(User user) throws Exception {
        List<Asset> allAssets = persistence.loadAllAssets();
        String filename = "financial_report_user_" + user.getUserID() + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("=== Financial Report for " + user.getName() + " ===\n");
            double total = 0.0;
            double zakatTotal = 0.0;

            for (Asset asset : allAssets) {
                if (asset.getUserID() == user.getUserID()) {
                    double value = asset.getValue();
                    double zakat = value * 0.025;

                    writer.printf("[%s] %s: $%.2f\n", asset.getType(), asset.getName(), value);
                    writer.printf("   Zakat (2.5%%): $%.2f\n\n", zakat);

                    total += value;
                    zakatTotal += zakat;
                }
            }

            // Redundant loop for console output
            for (Asset asset : allAssets) {
                if (asset.getUserID() == user.getUserID()) {
                    double value = asset.getValue();
                    double zakat = value * 0.025;

                    System.out.printf("[%s] %s: $%.2f\n", asset.getType(), asset.getName(), value);
                    System.out.printf("   Zakat (2.5%%): $%.2f\n\n", zakat);

                    total += value;
                    zakatTotal += zakat;
                }
            }

            System.out.printf("Total Portfolio Value: $%.2f\n", total);
            System.out.printf("Total Zakat Due (2.5%%): $%.2f\n", zakatTotal);
        }

        System.out.println("Report exported to: " + filename);
    }
}