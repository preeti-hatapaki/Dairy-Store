import java.util.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

class Product {
    String name;
    int quantity;
    double price;
    LocalDate expiryDate;

    Product(String name, int quantity, double price, LocalDate expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
    }

    double getDiscountedPrice() {
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
        if (daysUntilExpiry <= 1) {
            return price * 0.50; // 50% off on the last day
        } else if (daysUntilExpiry == 2) {
            return price * 0.75; // 25% off if 2 days left
        } else if (daysUntilExpiry == 3) {
            return price * 0.90; // 10% off if 3 days left
        } else {
            return price; // No discount if more than 3 days left
        }
    }
}

class Vendor {
    String username;
    String password;
    List<Product> inventory;

    Vendor(String username, String password) {
        this.username = username;
        this.password = password;
        this.inventory = new ArrayList<>();
    }

    void addProduct(Product product) {
        inventory.add(product);
    }

    void updateInventory(String productName, int additionalQuantity, LocalDate expiryDate, double price) {
        for (Product product : inventory) {
            if (product.name.equalsIgnoreCase(productName) && product.expiryDate.equals(expiryDate)) {
                product.quantity += additionalQuantity;
                return;
            }
        }
        Product newProduct = new Product(productName, additionalQuantity, price, expiryDate);
        inventory.add(newProduct);
    }

    List<Product> getInventory() {
        return inventory;
    }

    void displayInventory() {
        System.out.println("Current Inventory:");
        for (Product product : inventory) {
            System.out.println("Product: " + product.name +
                    ", Quantity: " + product.quantity +
                    ", Price: " + product.price +
                    ", Expiry Date: " + product.expiryDate);
        }
    }

    boolean sellProduct(String productName, int quantity, LocalDate expiryDate) {
        for (Iterator<Product> iterator = inventory.iterator(); iterator.hasNext(); ) {
            Product product = iterator.next();
            if (product.name.equalsIgnoreCase(productName) && product.expiryDate.equals(expiryDate)) {
                if (product.quantity >= quantity) {
                    product.quantity -= quantity;
                    if (product.quantity == 0) {
                        iterator.remove();
                    }
                    return true; // Product sold successfully
                }
                break; // Product found but not enough quantity
            }
        }
        return false; // Product not found or insufficient quantity
    }
}

class User {
    String username;
    String password;
    List<Product> cart;

    User(String username, String password) {
        this.username = username;
        this.password = password;
        this.cart = new ArrayList<>();
    }

    void addToCart(Product product) {
        cart.add(product);
    }

    void removeFromCart(int index) {
        if (index >= 0 && index < cart.size()) {
            cart.remove(index);
        } else {
            System.out.println("Invalid index. Product not removed from cart.");
        }
    }

    double checkout() {
        double total = 0.0;
        for (Product product : cart) {
            total += product.getDiscountedPrice() * product.quantity;
        }
        return total;
    }

    void displayCart() {
        System.out.println("Cart Items:");
        int index = 1;
        for (Product product : cart) {
            System.out.println(index++ + ". Product: " + product.name +
                    ", Quantity: " + product.quantity +
                    ", Expiry Date: " + product.expiryDate +
                    ", Price: " + product.price +
                    ", Discounted Price: " + product.getDiscountedPrice());
        }
    }
}

public class DairyProductSystem {
    private Map<String, Vendor> vendors;
    private Map<String, User> users;

    public DairyProductSystem() {
        vendors = new HashMap<>();
        users = new HashMap<>();
    }

    public static void main(String[] args) {
        DairyProductSystem system = new DairyProductSystem();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("1. Vendor Register");
            System.out.println("2. Vendor Login");
            System.out.println("3. User Register");
            System.out.println("4. User Login");
            System.out.println("5. Exit");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    system.vendorRegister(sc);
                    break;
                case 2:
                    system.vendorLogin(sc);
                    break;
                case 3:
                    system.userRegister(sc);
                    break;
                case 4:
                    system.userLogin(sc);
                    break;
                case 5:
                    System.exit(0);
            }
        }
    }

    private void vendorRegister(Scanner sc) {
        System.out.println("Enter username:");
        String username = sc.next();
        System.out.println("Enter password:");
        String password = sc.next();

        if (vendors.containsKey(username)) {
            System.out.println("Vendor already exists!");
        } else {
            vendors.put(username, new Vendor(username, password));
            System.out.println("Vendor registered successfully.");
        }
    }

    private void vendorLogin(Scanner sc) {
        System.out.println("Enter username:");
        String username = sc.next();
        System.out.println("Enter password:");
        String password = sc.next();

        Vendor vendor = vendors.get(username);
        if (vendor != null && vendor.password.equals(password)) {
            vendorMenu(vendor, sc);
        } else {
            System.out.println("Invalid credentials!");
        }
    }

    private void userRegister(Scanner sc) {
        System.out.println("Enter username:");
        String username = sc.next();
        System.out.println("Enter password:");
        String password = sc.next();

        if (users.containsKey(username)) {
            System.out.println("User already exists!");
        } else {
            users.put(username, new User(username, password));
            System.out.println("User registered successfully.");
        }
    }

    private void userLogin(Scanner sc) {
        System.out.println("Enter username:");
        String username = sc.next();
        System.out.println("Enter password:");
        String password = sc.next();

        User user = users.get(username);
        if (user != null && user.password.equals(password)) {
            userMenu(user, sc);
        } else {
            System.out.println("Invalid credentials!");
        }
    }

    private void vendorMenu(Vendor vendor, Scanner sc) {
        while (true) {
            System.out.println("1. Add Product");
            System.out.println("2. Update Inventory");
            System.out.println("3. View Inventory");
            System.out.println("4. Logout");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    addProductToInventory(vendor, sc);
                    break;
                case 2:
                    updateInventory(vendor, sc);
                    break;
                case 3:
                    vendor.displayInventory();
                    break;
                case 4:
                    return;
            }
        }
    }

    private void addProductToInventory(Vendor vendor, Scanner sc) {
        System.out.println("Enter product name:");
        String name = sc.next();
        System.out.println("Enter product quantity:");
        int quantity = sc.nextInt();
        System.out.println("Enter product price:");
        double price = sc.nextDouble();
        System.out.println("Enter expiry date (yyyy-mm-dd):");
        String dateStr = sc.next();
        LocalDate expiryDate = LocalDate.parse(dateStr);

        Product product = new Product(name, quantity, price, expiryDate);
        vendor.addProduct(product);
        System.out.println("Product added to inventory.");
    }

    private void updateInventory(Vendor vendor, Scanner sc) {
        System.out.println("Enter product name to update:");
        String productName = sc.next();
        System.out.println("Enter additional quantity:");
        int additionalQuantity = sc.nextInt();
        System.out.println("Enter expiry date (yyyy-mm-dd):");
        String dateStr = sc.next();
        LocalDate expiryDate = LocalDate.parse(dateStr);
        System.out.println("Enter product price:");
        double price = sc.nextDouble();

        vendor.updateInventory(productName, additionalQuantity, expiryDate, price);
        System.out.println("Inventory updated successfully.");
    }

    private void userMenu(User user, Scanner sc) {
        while (true) {
            System.out.println("1. View Products");
            System.out.println("2. Add Product to Cart");
            System.out.println("3. Remove Product from Cart");
            System.out.println("4. View Cart");
            System.out.println("5. Checkout");
            System.out.println("6. Logout");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    displayProducts();
                    break;
                case 2:
                    addProductToCart(user, sc);
                    break;
                case 3:
                    removeProductFromCart(user, sc);
                    break;
                case 4:
                    user.displayCart();
                    break;
                case 5:
                    checkout(user, sc);
                    break;
                case 6:
                    return;
            }
        }
    }

    private void displayProducts() {
        int index = 1;
        System.out.println("Available Products:");
        for (Vendor vendor : vendors.values()) {
            for (Product product : vendor.getInventory()) {
                System.out.println(index++ + ". Product: " + product.name +
                        ", Expiry Date: " + product.expiryDate +
                        ", Price: " + product.price +
                        ", Discounted Price: " + product.getDiscountedPrice());
            }
        }
    }

    private void addProductToCart(User user, Scanner sc) {
        displayProducts();
        System.out.println("Enter the product number to add to cart:");
        int productNumber = sc.nextInt();
        int currentIndex = 1;
        boolean found = false;

        for (Vendor vendor : vendors.values()) {
            for (Product product : vendor.getInventory()) {
                if (currentIndex == productNumber) {
                    System.out.println("Enter quantity to add to cart:");
                    int quantity = sc.nextInt();
                    if (quantity <= product.quantity) {
                        Product cartProduct = new Product(product.name, quantity, product.price, product.expiryDate);
                        user.addToCart(cartProduct);
                        System.out.println("Product added to cart.");
                    } else {
                        System.out.println("Insufficient quantity available.");
                    }
                    found = true;
                    break;
                }
                currentIndex++;
            }
            if (found) break;
        }

        if (!found) {
            System.out.println("Invalid product number.");
        }
    }

    private void removeProductFromCart(User user, Scanner sc) {
        user.displayCart();
        if (user.cart.isEmpty()) {
            System.out.println("Cart is empty. Nothing to remove.");
            return;
        }

        System.out.println("Enter the index of the product to remove:");
        int indexToRemove = sc.nextInt() - 1;
        if (indexToRemove >= 0 && indexToRemove < user.cart.size()) {
            user.removeFromCart(indexToRemove);
            System.out.println("Product removed from cart.");
        } else {
            System.out.println("Invalid index. Product not removed from cart.");
        }
    }

    private void checkout(User user, Scanner sc) {
        user.displayCart();
        double total = user.checkout();
        System.out.println("Total amount to pay: " + total);
        System.out.println("Confirm purchase? (yes/no)");
        String confirmation = sc.next();

        if (confirmation.equalsIgnoreCase("yes")) {
            for (Product product : user.cart) {
                for (Vendor vendor : vendors.values()) {
                    if (vendor.sellProduct(product.name, product.quantity, product.expiryDate)) {
                        break;
                    }
                }
            }
            user.cart.clear();
            System.out.println("Purchase successful.");
        } else {
            System.out.println("Purchase cancelled.");
        }
    }
}
