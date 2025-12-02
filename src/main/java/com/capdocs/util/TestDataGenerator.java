package com.capdocs.util;

import com.capdocs.dao.*;
import com.capdocs.model.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestDataGenerator {

    private static final ClientDAO clientDAO = new ClientDAO();
    private static final SupplierDAO supplierDAO = new SupplierDAO();
    private static final ProductDAO productDAO = new ProductDAO();
    private static final OrderDAO orderDAO = new OrderDAO();
    private static final Random random = new Random();

    public static void generate() {
        try {
            if (!clientDAO.findAll().isEmpty()) {
                System.out.println("Data already exists. Skipping generation.");
                return;
            }

            System.out.println("Generating Test Data...");

            // 1. Clients
            generateClients();

            // 2. Suppliers
            generateSuppliers();

            // 3. Categories & Products
            generateProducts();

            // 4. Orders (Production)
            generateOrders();

            System.out.println("Test Data Generation Completed.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateClients() throws SQLException {
        String[] names = { "Juan Perez", "Maria Lopez", "Carlos Gomez", "Ana Martinez", "Luis Rodriguez",
                "Sofia Hernandez", "Pedro Diaz", "Laura Sanchez", "Miguel Torres", "Elena Ramirez" };
        for (String name : names) {
            Client client = new Client(0, name, "123456789", "contacto@empresa.com", "Calle Falsa 123", 0.0);
            clientDAO.create(client);
        }
    }

    private static void generateSuppliers() throws SQLException {
        String[] companies = { "Textiles Uno", "Hilos y Mas", "Telas Premium", "Insumos Global", "Moda Suministros" };
        for (String company : companies) {
            Supplier supplier = new Supplier(0, company, "Contacto " + company, "555-0000",
                    "contacto@" + company.replace(" ", "").toLowerCase() + ".com", "Calle Falsa 123",
                    "Proveedor confiable");
            supplierDAO.create(supplier);
        }
    }

    private static void generateProducts() throws SQLException {
        // Categories
        String[] categories = { "Camisetas", "Gorras", "Uniformes", "Bordados" };
        int[] catIds = new int[categories.length];

        // We need to fetch or create categories. Assuming they might not exist or we
        // just create them.
        // ProductDAO doesn't return ID on saveCategory easily in the current code,
        // let's assume we can fetch them.
        // Actually, let's just create them and then fetch all to get IDs.
        for (String cat : categories) {
            productDAO.saveCategory(Category.builder().name(cat).build());
        }
        List<Category> dbCats = productDAO.findAllCategories();

        // Products
        for (Category cat : dbCats) {
            for (int i = 1; i <= 5; i++) {
                Product product = Product.builder()
                        .categoryId(cat.getId())
                        .name(cat.getName() + " Modelo " + i)
                        .baseCost(10.0 * i)
                        .build();
                int prodId = productDAO.saveProduct(product);

                // Variants
                String[] sizes = { "S", "M", "L", "XL" };
                for (String size : sizes) {
                    ProductVariant variant = ProductVariant.builder()
                            .productId(prodId)
                            .size(size)
                            .stockQuantity(random.nextInt(50) + 10)
                            .build();
                    productDAO.saveVariant(variant);
                }
            }
        }
    }

    private static void generateOrders() throws SQLException {
        List<Client> clients = clientDAO.findAll();
        List<Product> products = productDAO.findAllProducts();

        if (clients.isEmpty() || products.isEmpty())
            return;

        // Create some orders with different statuses
        createOrderBatch(clients, products, Order.Status.PENDING, 3);
        createOrderBatch(clients, products, Order.Status.IN_PROCESS, 3);
        createOrderBatch(clients, products, Order.Status.FINISHED, 5);
    }

    private static void createOrderBatch(List<Client> clients, List<Product> products, Order.Status status, int count)
            throws SQLException {
        for (int i = 0; i < count; i++) {
            Client client = clients.get(random.nextInt(clients.size()));
            Product product = products.get(random.nextInt(products.size()));
            List<ProductVariant> variants = productDAO.findVariantsByProductId(product.getId());
            if (variants.isEmpty())
                continue;

            ProductVariant variant = variants.get(random.nextInt(variants.size()));

            int quantity = random.nextInt(5) + 1;
            double price = product.getBaseCost() * 1.5 * quantity;

            Order order = Order.builder()
                    .clientId(client.getId())
                    .status(status)
                    .totalPrice(price)
                    .paidAmount(price) // Fully paid for simplicity
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(10)))
                    .build();

            List<OrderItem> items = new ArrayList<>();
            items.add(OrderItem.builder()
                    .variantId(variant.getId())
                    .quantity(quantity)
                    .techniqueDetails("Estampado Logo")
                    .build());

            orderDAO.createOrder(order, items, null);

            // If status is not PENDING, we need to update it because createOrder sets it to
            // what we passed,
            // but let's ensure consistency if logic changes.
            // Actually createOrder uses the status from the object, so it's fine.
            // However, createOrder creates a transaction. If we want history, we might want
            // older dates.
            // The DAO uses current timestamp for transaction usually, but Order has
            // createdAt.
            // We might need to update the status manually if createOrder defaults to
            // PENDING in DB (it doesn't, it uses object).
        }
    }
}
