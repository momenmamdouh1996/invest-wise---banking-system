import java.io.*;
import java.util.*;

// ===== User Class =====
class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userID;
    private String name;
    private String email;
    private String password;

    public User(int userID, String name, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getUserID() { return userID; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }

    public String toString() {
        return "User{ID=" + userID + ", name=" + name + ", email=" + email + "}";
    }
}

// ===== Asset Class =====
class Asset implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userID;
    private String type;
    private String name;
    private double value;

    public Asset(int userID, String type, String name, double value) {
        this.userID = userID;
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public int getUserID() { return userID; }
    public String getName() { return name; }
    public String toString() {
        return "[" + type + "] " + name + ": $" + value;
    }
}

// ===== Interfaces =====
interface IUserManager {
    User createUser(String name, String email, String password) throws Exception;
    User login(String email, String password) throws Exception;
}

interface IPersistenceMechanism {
    boolean validateData(String email) throws Exception;
    List<User> loadAllUsers() throws Exception;
    List<Asset> loadAllAssets() throws Exception;
    void saveUser(User user) throws Exception;
    void saveAssets(List<Asset> assets) throws Exception;
    void overwriteAssets(List<Asset> allAssets) throws Exception;
}

// ===== FilePersistence =====
class FilePersistence implements IPersistenceMechanism {
    private static final String USERS_FILE = "users.ser";
    private static final String ASSETS_FILE = "assets.ser";

    public List<User> loadAllUsers() throws Exception {
        File file = new File(USERS_FILE);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        }
    }

    public List<Asset> loadAllAssets() throws Exception {
        File file = new File(ASSETS_FILE);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Asset>) ois.readObject();
        }
    }

    public boolean validateData(String email) throws Exception {
        for (User user : loadAllUsers()) {
            if (user.getEmail().equalsIgnoreCase(email)) return false;
        }
        return true;
    }

    public void saveUser(User user) throws Exception {
        List<User> users = loadAllUsers();
        users.add(user);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
        }
    }

    public void saveAssets(List<Asset> newAssets) throws Exception {
        List<Asset> allAssets = loadAllAssets();
        allAssets.addAll(newAssets);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ASSETS_FILE))) {
            oos.writeObject(allAssets);
        }
    }

    public void overwriteAssets(List<Asset> allAssets) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ASSETS_FILE))) {
            oos.writeObject(allAssets);
        }
    }
}

// ===== UserManager =====
class UserManager implements IUserManager {
    private IPersistenceMechanism persistence = new FilePersistence();

    public User createUser(String name, String email, String password) throws Exception {
        if (!persistence.validateData(email)) throw new Exception("Email already exists!");
        int userID = 1000 + new Random().nextInt(9000);
        User newUser = new User(userID, name, email, password);
        persistence.saveUser(newUser);

        List<Asset> demoAssets = Arrays.asList(
            new Asset(userID, "Stock", "Apple Inc", 15000),
            new Asset(userID, "Stock", "Tesla", 10000),
            new Asset(userID, "Real Estate", "Rental Apartment", 250000),
            new Asset(userID, "Gold", "Gold Bar", 18000),
            new Asset(userID, "Crypto", "Bitcoin", 12000)
        );
        persistence.saveAssets(demoAssets);
        return newUser;
    }

    public User login(String email, String password) throws Exception {
        for (User user : persistence.loadAllUsers()) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        throw new Exception("Invalid email or password!");
    }
}

// ===== Main App =====
public class BankingApp {
    static Scanner sc = new Scanner(System.in);
    static IPersistenceMechanism persistence = new FilePersistence();
    static IUserManager userManager = new UserManager();

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
                if (choice == 1) signUp();
                else if (choice == 2) login();
                else if (choice == 3) break;
                else System.out.println("Invalid option.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

    }
        // manual used
    static void clearAllData() {
        new File("users.ser").delete();
        new File("assets.ser").delete();
        System.out.println("All user and asset data cleared.");
    }
    

    static void signUp() throws Exception {
        String name, email, password;
    
        while (true) {
            System.out.print("Enter name: ");
            name = sc.nextLine().trim();
            if (!name.isEmpty()) break;
            System.out.println("Name cannot be empty.");
        }
    
        while (true) {
            System.out.print("Enter email: ");
            email = sc.nextLine().trim();
            if (!email.isEmpty()) break;
            System.out.println("Email cannot be empty.");
        }
    
        while (true) {
            System.out.print("Enter password: ");
            password = sc.nextLine().trim();
            if (!password.isEmpty()) break;
            System.out.println("Password cannot be empty.");
        }
    
        User user = userManager.createUser(name, email, password);
        System.out.println("Sign-up successful! Your ID: " + user.getUserID());
        login();
    }
    

    static void login() throws Exception {
        String email, password;
        System.out.println("=== Login ===");

    
        while (true) {
            System.out.print("Enter email: ");
            email = sc.nextLine().trim();
            if (!email.isEmpty()) break;
            System.out.println("Email cannot be empty.");
        }
    
        while (true) {
            System.out.print("Enter password: ");
            password = sc.nextLine().trim();
            if (!password.isEmpty()) break;
            System.out.println("Password cannot be empty.");
        }
    
        User user = userManager.login(email, password);
        System.out.println("Welcome, " + user.getName());
        System.out.println("Your ID: " + user.getUserID());
        userDashboard(user);
    }
    
    static void userDashboard(User user) throws Exception {
        while (true) {
            System.out.println("\n=== Dashboard ===");
            System.out.println("1. Display Portfolio");
            System.out.println("2. Add Asset");
            System.out.println("3. Remove Asset");
            System.out.println("4. Edit Asset"); // <-- New option
            System.out.println("5. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();
    
            if (choice == 1) displayPortfolio(user);
            else if (choice == 2) addAsset(user);
            else if (choice == 3) removeAsset(user);
            else if (choice == 4) editAsset(user); // <-- Call edit method
            else if (choice == 5) break;
            else System.out.println("Invalid option.");
        }
    }
    
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
        if (!found) System.out.println("No assets found.");
    }

    static void addAsset(User user) throws Exception {
        System.out.println("\nChoose asset to add:");
        List<Asset> predefinedAssets = Arrays.asList(
            new Asset(user.getUserID(), "Stock", "Nvidia", 17000),
            new Asset(user.getUserID(), "Real Estate", "Vacation Home", 400000),
            new Asset(user.getUserID(), "Gold", "Gold Necklace", 7000),
            new Asset(user.getUserID(), "Crypto", "Ethereum", 3000)
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
        displayPortfolio(user);
    }

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
    }

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
    
            // Replace asset with updated value
            allAssets.remove(selectedAsset);
            Asset updatedAsset = new Asset(user.getUserID(), selectedAsset.toString().split("]")[0].substring(1), selectedAsset.getName(), newValue);
            allAssets.add(updatedAsset);
    
            persistence.overwriteAssets(allAssets);
            System.out.println("Asset value updated.");
        } else {
            System.out.println("Invalid selection.");
        }
    }
    

}
