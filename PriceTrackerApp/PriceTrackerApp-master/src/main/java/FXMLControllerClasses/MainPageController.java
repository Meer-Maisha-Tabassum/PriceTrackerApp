package FXMLControllerClasses;

import HelperClasses.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.stream.IntStream;

public class MainPageController implements Initializable {

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchProductText;

    @FXML
    private Button showPriceTrendButton;

    @FXML
    private Button logoutButton;

    @FXML
    private TextArea topSellersTextArea;

    @FXML
    private TextArea cheapestSellerTextArea;

    @FXML
    private LineChart<String, Double> trend;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private TreeView<String> categoryTreeView;

    @FXML
    private ChoiceBox<String> itemChoiceBox;

    @FXML
    private ChoiceBox<String> unitChoiceBox;

    @FXML
    private TableView<CartItem> cartTableView;
    @FXML
    private TableColumn<CartItem, String> itemNameColumn;
    @FXML
    private TableColumn<CartItem, String> itemCodeColumn;
    @FXML
    private TableColumn<CartItem, String> unitColumn;
    @FXML
    private TableColumn<CartItem, String> quantityColumn;

    @FXML
    private TextField itemNameTextfield;
    @FXML
    private TextField itemCodeTextfield;
    @FXML
    private TextField quantityTextfield;
    @FXML
    private TextField unitTextfield;

    private Cart cart;

    String name;
    private boolean found;

    String loginUserName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeTreeView();
        this.cart = new Cart();
        this.loginUserName = SharedDataModel.getInstance().getLoggedInUsername();
        fetchAndDisplayUserCart(loginUserName);
        initializeCartTableView();
        setupMouseEventHandler();
    }

    @FXML
    protected void onSearch() {
        try {
            if (!searchProductText.getText().isEmpty()) {
                topSellersTextArea.clear();
                unitChoiceBox.getItems().clear();

                System.out.println(loginUserName);

                Connection connectionPriceCatcher = DriverManager.getConnection("jdbc:mysql://localhost:3306/pricecatcher", "root", "123456");
                Statement statementPriceCatcher = connectionPriceCatcher.createStatement();
                ResultSet resultSetPriceCatcher = statementPriceCatcher.executeQuery("SELECT * FROM `pricecatcher_2023`");

                Connection connectionItem = DriverManager.getConnection("jdbc:mysql://localhost:3306/lookup_item", "root", "123456");
                Statement statementItem = connectionItem.createStatement();
                ResultSet resultSetItem = statementItem.executeQuery("SELECT * FROM lookup_item");

                Set<String> units = new HashSet<>();

                name = searchProductText.getText().trim();

                while (resultSetItem.next()) {
                    // Use fuzzy search to find items with similar names
                    List<String> fuzzyResults = FuzzySearch.fuzzySearch(name, Arrays.asList(resultSetItem.getString("item")), 6);

                    if (!fuzzyResults.isEmpty()) {
                        units.add(resultSetItem.getString("unit"));
                    }
                }
                if (!units.isEmpty()){
                    unitChoiceBox.getItems().addAll(units);
                    found = true;
                }
                else {
                    found = false;
                    topSellersTextArea.setText("Item currently unavailable!");
                }
            }
            else {
                topSellersTextArea.setText("No such item found!!!");
            }

        }
        catch (SQLException e) {
            System.out.println("Something went wrong try again!");
            topSellersTextArea.clear();
            itemChoiceBox.getItems().clear();
        }

    }


    @FXML
    protected void onUnitSearch() {
        try {
            if (!searchProductText.getText().isEmpty()) {
                if (found) {
                    topSellersTextArea.clear();

                    Connection connectionPriceCatcher = DriverManager.getConnection("jdbc:mysql://localhost:3306/pricecatcher", "root", "123456");
                    Statement statementPriceCatcher = connectionPriceCatcher.createStatement();
                    ResultSet resultSetPriceCatcher = statementPriceCatcher.executeQuery("SELECT * FROM `pricecatcher_2023`");

                    ArrayList<PriceRecord> priceRecords = new ArrayList<>();
                    while (resultSetPriceCatcher.next())
                        priceRecords.add(new PriceRecord(resultSetPriceCatcher.getString("date"),
                                Integer.parseInt(String.valueOf(resultSetPriceCatcher.getString("premise_code"))),
                                Integer.parseInt(String.valueOf(resultSetPriceCatcher.getString("item_code"))),
                                Double.parseDouble(String.valueOf(resultSetPriceCatcher.getString("price")))));

                    Connection connectionItem = DriverManager.getConnection("jdbc:mysql://localhost:3306/lookup_item", "root", "123456");
                    Statement statementItem = connectionItem.createStatement();
                    ResultSet resultSetItem = statementItem.executeQuery("SELECT * FROM lookup_item");

                    int itemCodesWithUnit = 0;
                    String unit = unitChoiceBox.getValue();
                    if (Objects.equals(unit, null)) {
                        topSellersTextArea.setText("Please choose a unit!");
                    }
                    else {
                        name = searchProductText.getText().trim();
                        while (resultSetItem.next()){
                            if (name.equals(resultSetItem.getString("item")) && Objects.equals(unit, resultSetItem.getString("unit"))){
                                itemCodesWithUnit = Integer.parseInt(String.valueOf(resultSetItem.getString("item_code")));
                                break;
                            }
                        }
                        if (itemCodesWithUnit != 0) {
                            ArrayList<ItemDetails> itemDetails = new ArrayList<>();
                            Set<ItemDetails> uniqueItemDetails = new HashSet<>();

                            for (PriceRecord priceRecord : priceRecords) {
                                if (priceRecord.getItemCode() == itemCodesWithUnit) {
                                    ItemDetails newItemDetail = new ItemDetails(name, unit, itemCodesWithUnit, priceRecord.getPremiseCode(), priceRecord.getPrice());
                                    //Check if the itemDetails set already contains the new itemDetail
                                    if (uniqueItemDetails.add(newItemDetail)) {
                                        //If the itemDetail is unique, add it to the list
                                        itemDetails.add(newItemDetail);
                                    }
                                }
                            }
                            Collections.sort(itemDetails);

                            ArrayList<SellerDetails> sellerDetails = new ArrayList<>();

                            Connection connectionPremise = DriverManager.getConnection("jdbc:mysql://localhost:3306/lookup_premise", "root", "123456");
                            Statement statementPremise = connectionPremise.createStatement();
                            ResultSet resultSetPremise = statementPremise.executeQuery("SELECT * FROM lookup_premise");


                            while (resultSetPremise.next()) {
                                for (ItemDetails itemDetail : itemDetails) {
                                    if (Integer.parseInt(String.valueOf(resultSetPremise.getString("premise_code")))
                                            == itemDetail.getPremiseCode()) {
                                        boolean isDuplicate = false;
                                        SellerDetails newSeller = new SellerDetails(resultSetPremise.getString("premise"), resultSetPremise.getString("address"),
                                                resultSetPremise.getString("premise_type"), resultSetPremise.getString("state"),
                                                resultSetPremise.getString("district"), itemDetail);

                                        // Check for duplicates before adding
                                        for (SellerDetails existingSeller : sellerDetails) {
                                            if (existingSeller.equals(newSeller)) {
                                                isDuplicate = true;
                                                break;
                                            }
                                        }
                                        if (!isDuplicate) {
                                            sellerDetails.add(newSeller);
                                        }
                                    }
                                }
                            }

                            Collections.sort(sellerDetails);

                            int topCount = Math.min(sellerDetails.size(), 5);
                            IntStream.range(0, topCount).forEach(i -> {
                                topSellersTextArea.appendText((i + 1) + ": " + sellerDetails.get(i).display() + "\n");
                            });

                        }
                        else {
                            topSellersTextArea.setText("No such item found!!!");
                        }
                    }
                }
                else {
                    topSellersTextArea.setText("No such item found!!!");
                }
            }
            else {
                topSellersTextArea.setText("Please enter a item name and search again!");
            }

        }
        catch (SQLException e) {
            System.out.println("Something went wrong try again!");
            topSellersTextArea.clear();
            itemChoiceBox.getItems().clear();
        }

    }


    @FXML
    protected void onPriceTrend() {
        try {
            if (!searchProductText.getText().isEmpty()) {
                if (found) {
                    Connection connectionPriceCatcher = DriverManager.getConnection("jdbc:mysql://localhost:3306/pricecatcher", "root", "123456");
                    Statement statementPriceCatcher = connectionPriceCatcher.createStatement();
                    ResultSet resultSetPriceCatcher = statementPriceCatcher.executeQuery("SELECT * FROM `pricecatcher_2023`");

                    ArrayList<PriceRecord> priceRecords = new ArrayList<>();
                    while (resultSetPriceCatcher.next())
                        priceRecords.add(new PriceRecord(resultSetPriceCatcher.getString("date"),
                                Integer.parseInt(String.valueOf(resultSetPriceCatcher.getString("premise_code"))),
                                Integer.parseInt(String.valueOf(resultSetPriceCatcher.getString("item_code"))),
                                Double.parseDouble(String.valueOf(resultSetPriceCatcher.getString("price")))));

                    priceRecords.sort(PriceRecord.getDateComparator());

                    Connection connectionItem = DriverManager.getConnection("jdbc:mysql://localhost:3306/lookup_item", "root", "123456");
                    Statement statementItem = connectionItem.createStatement();
                    ResultSet resultSetItem = statementItem.executeQuery("SELECT * FROM lookup_item");


                    name = searchProductText.getText().trim();
                    int itemCodesWithUnit = 0;
                    String unit = unitChoiceBox.getValue();
                    if (Objects.equals(unit, null)){
                        topSellersTextArea.setText("Please choose a unit!");
                    }
                    else {
                        name = searchProductText.getText().trim();
                        while (resultSetItem.next()){
                            if (name.equals(resultSetItem.getString("item")) && Objects.equals(unit, resultSetItem.getString("unit"))){
                                itemCodesWithUnit = Integer.parseInt(String.valueOf(resultSetItem.getString("item_code")));
                                break;
                            }
                        }
                        if (itemCodesWithUnit != 0) {
                            ArrayList<Pair<String, Double>> priceTrend = new ArrayList<>();
                            Map<String, List<Double>> dailyPrices = new HashMap<>();


                            for (PriceRecord priceRecord : priceRecords) {
                                String date = priceRecord.getDate();
                                double price = priceRecord.getPrice();
                                if (itemCodesWithUnit == priceRecord.getItemCode())
                                    dailyPrices.computeIfAbsent(date, k -> new ArrayList<>()).add(price);
                            }

                            // Calculate the average price for each date and add it to priceTrend
                            for (Map.Entry<String, List<Double>> entry : dailyPrices.entrySet()) {
                                String date = entry.getKey();
                                List<Double> prices = entry.getValue();

                                // Calculate the average price
                                double averagePrice = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

                                // Add the date and average price to priceTrend
                                priceTrend.add(new Pair<>(date, averagePrice));
                            }

                            // Sort the priceTrend list based on date
                            priceTrend.sort(Comparator.comparing(Pair::getKey));
                            trend.getData().removeAll();
                            Series<String, Double> series = new Series<>();

                            for (Pair<String, Double> stringDoublePair : priceTrend) {
                                series.getData().add(new XYChart.Data<String, Double>(stringDoublePair.getKey(), stringDoublePair.getValue()));
                            }
                            trend.getData().addAll(series);
                            trend.setVisible(true);
                        }
                        else {
                            topSellersTextArea.setText("No such item found!");
                        }
                    }
                }
                else {
                    topSellersTextArea.setText("No such item found!");
                }
            }
            else {
                topSellersTextArea.setText("Please enter a item name and search!");
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void onLogout(ActionEvent event) {
        openPage("/FXML/LoginFXML.fxml", logoutButton);
    }

    @FXML
    protected void onBrowse() {
        try {
            topSellersTextArea.clear();
            Connection connectionPriceCatcher = DriverManager.getConnection("jdbc:mysql://localhost:3306/pricecatcher", "root", "123456");
            Statement statementPriceCatcher = connectionPriceCatcher.createStatement();
            ResultSet resultSetPriceCatcher = statementPriceCatcher.executeQuery("SELECT * FROM `pricecatcher_2023`");

            ArrayList<PriceRecord> priceRecords = new ArrayList<>();
            while (resultSetPriceCatcher.next())
                priceRecords.add(new PriceRecord(resultSetPriceCatcher.getString("date"),
                        Integer.parseInt(String.valueOf(resultSetPriceCatcher.getString("premise_code"))),
                        Integer.parseInt(String.valueOf(resultSetPriceCatcher.getString("item_code"))),
                        Double.parseDouble(String.valueOf(resultSetPriceCatcher.getString("price")))));

            Connection connectionItem = DriverManager.getConnection("jdbc:mysql://localhost:3306/lookup_item", "root", "123456");
            Statement statementItem = connectionItem.createStatement();
            ResultSet resultSetItem = statementItem.executeQuery("SELECT * FROM lookup_item");

            ArrayList<ItemCodeAndUnit> itemCodesAndUnits = new ArrayList<>();

            String name = itemChoiceBox.getValue();

            if (Objects.equals(name, null)) {
                System.out.println("Please choose a item from the category!");
            }
            else {
                while (resultSetItem.next()){
                    if (name.equals(resultSetItem.getString("item"))){
                        itemCodesAndUnits.add(new ItemCodeAndUnit(Integer.parseInt(String.valueOf(resultSetItem.getString("item_code"))), resultSetItem.getString("unit")));
                    }
                }
                ArrayList<ItemDetails> itemDetails = new ArrayList<>();
                Set<ItemDetails> uniqueItemDetails = new HashSet<>();

                for (PriceRecord priceRecord : priceRecords) {
                    for (ItemCodeAndUnit itemCodeAndUnit : itemCodesAndUnits) {
                        if (priceRecord.getItemCode() == itemCodeAndUnit.getItemCode()) {
                            ItemDetails newItemDetail = new ItemDetails(name, itemCodeAndUnit.getUnit(), itemCodeAndUnit.getItemCode(), priceRecord.getPremiseCode(), priceRecord.getPrice());
                            //Check if the itemDetails set already contains the new itemDetail
                            if (uniqueItemDetails.add(newItemDetail)) {
                                //If the itemDetail is unique, add it to the list
                                itemDetails.add(newItemDetail);
                            }
                        }
                    }
                }
                Collections.sort(itemDetails);

                ArrayList<SellerDetails> sellerDetails = new ArrayList<>();

                Connection connectionPremise = DriverManager.getConnection("jdbc:mysql://localhost:3306/lookup_premise", "root", "123456");
                Statement statementPremise = connectionPremise.createStatement();
                ResultSet resultSetPremise = statementPremise.executeQuery("SELECT * FROM lookup_premise");


                while (resultSetPremise.next()) {
                    for (ItemDetails itemDetail : itemDetails) {
                        if (Integer.parseInt(String.valueOf(resultSetPremise.getString("premise_code")))
                                == itemDetail.getPremiseCode()) {
                            boolean isDuplicate = false;
                            SellerDetails newSeller = new SellerDetails(resultSetPremise.getString("premise"), resultSetPremise.getString("address"),
                                    resultSetPremise.getString("premise_type"), resultSetPremise.getString("state"),
                                    resultSetPremise.getString("district"), itemDetail);

                            // Check for duplicates before adding
                            for (SellerDetails existingSeller : sellerDetails) {
                                if (existingSeller.equals(newSeller)) {
                                    isDuplicate = true;
                                    break;
                                }
                            }
                            if (!isDuplicate) {
                                sellerDetails.add(newSeller);
                            }
                        }
                    }
                }

                Collections.sort(sellerDetails);

                IntStream.range(0, sellerDetails.size()).forEach(i -> {
                    topSellersTextArea.appendText((i + 1) + ": " + sellerDetails.get(i).display() + "\n");
                });
            }

        } catch (SQLException e) {
            System.out.println("Something went wrong try again!");
            topSellersTextArea.clear();
            itemChoiceBox.getItems().clear();
        }
    }

    private void initializeTreeView() {
        // Create root item for the TreeView
        TreeItem<String> rootItem = new TreeItem<>("Categories");

        // Populate the TreeView with item categories and groups
        populateTreeView(rootItem);

        // Set the root item for the TreeView
        categoryTreeView.setRoot(rootItem);

        // Allow editing of category and group names
        categoryTreeView.setEditable(false);
        categoryTreeView.setCellFactory(TextFieldTreeCell.forTreeView());

        // Add a selection listener to the TreeView
        categoryTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Update the text area with the items of the selected category
                try {
                    updateItemTextArea(newValue);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void populateTreeView(TreeItem<String> rootItem) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lookup_item", "root", "123456");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM lookup_item");

            Map<String, TreeItem<String>> categoryMap = new HashMap<>();

            while (resultSet.next()) {
                String category = resultSet.getString("item_category");
                String group = resultSet.getString("item_group");

                if (Objects.equals(category, "") && Objects.equals(group, "")) continue;

                TreeItem<String> groupItem = categoryMap.get(group);
                if (groupItem == null) {
                    groupItem = new TreeItem<>(group);
                    categoryMap.put(group, groupItem);
                    rootItem.getChildren().add(groupItem);
                }

                // Check if the category already exists in the group
                boolean categoryExists = groupItem.getChildren().stream()
                        .anyMatch(categoryItem -> categoryItem.getValue().equals(category));

                // If the category doesn't exist, add it to the group
                if (!categoryExists) {
                    TreeItem<String> categoryItem = new TreeItem<>(category);
                    groupItem.getChildren().add(categoryItem);
                }
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void updateItemTextArea(TreeItem<String> selectedCategory) throws SQLException {
        // Clear the text area
        itemChoiceBox.getItems().clear();
        // Get the selected category's items from the database and display them in the text area
        String category = selectedCategory.getValue();

        // Use a PreparedStatement to avoid SQL injection
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lookup_item", "root", "123456")) {
            String query = "SELECT item FROM lookup_item WHERE item_category = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, category);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String itemName = resultSet.getString("item");
                        itemChoiceBox.getItems().add(itemName);
                    }
                }catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onAddToCart() {
        String itemName = itemNameTextfield.getText().trim();
        String itemCodeStr = itemCodeTextfield.getText().trim();
        String unit = unitTextfield.getText().trim();
        String quantityStr = quantityTextfield.getText().trim();

        if (itemName.isEmpty() || itemCodeStr.isEmpty() || unit.isEmpty() || quantityStr.isEmpty()) {
            // Display an error alert for empty fields
            showAlert("Please fill in all the fields.");
        } else {
            try {
                int itemCode = Integer.parseInt(itemCodeStr);
                int quantity = Integer.parseInt(quantityStr);

                // Create a new CartItem
                CartItem cartItem = new CartItem(itemName, itemCode, unit, quantity);

                // Add the item to the cart
                cart.addItem(cartItem);

                // Update the cart TableView
                cartTableView.setItems(FXCollections.observableArrayList(cart.getItems()));

                // Save the cart item to the database
                addToCartDatabase(loginUserName, cartItem);
            } catch (NumberFormatException e) {
                // Handle the case where itemCode or quantity is not a valid integer
                showAlert("Invalid input for itemCode or quantity.");
            }
        }
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    private void addToCartDatabase(String username, CartItem item) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cart_details", "root", "123456")) {
            String query = "INSERT INTO user_cart (user_name, item_name, item_code, unit, quantity) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, item.getItemName());
                preparedStatement.setInt(3, item.getItemCode());
                preparedStatement.setString(4, item.getUnit());
                preparedStatement.setInt(5, item.getQuantity());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // This method is called when the "Update" button is clicked
    @FXML
    protected void onUpdateFromCart() {
        // Get the selected item from the TableView
        CartItem selectedItem = cartTableView.getSelectionModel().getSelectedItem();

        // Get the updated details from the text fields
        String updatedItemName = itemNameTextfield.getText().trim();
        String updatedItemCodeStr = itemCodeTextfield.getText().trim();
        String updatedUnit = unitTextfield.getText().trim();
        String updatedQuantityStr = quantityTextfield.getText().trim();

        if (updatedItemName.isEmpty() || updatedItemCodeStr.isEmpty() || updatedUnit.isEmpty() || updatedQuantityStr.isEmpty()) {
            // Display an error alert for empty fields
            showAlert("Error! Please fill in all the fields.");
        } else {
            try {
                // Update the selected item details
                selectedItem.setItemName(updatedItemName);
                selectedItem.setItemCode(Integer.parseInt(updatedItemCodeStr));
                selectedItem.setUnit(updatedUnit);
                selectedItem.setQuantity(Integer.parseInt(updatedQuantityStr));

                // Display a confirmation dialog
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Update Item");
                alert.setContentText("Are you sure you want to update this item from the cart?");

                // Handle the user's response
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // Update the TableView
                        cartTableView.refresh();

                        // Update the item in the database
                        updateFromCartDatabase(loginUserName, selectedItem);
                    }
                });
            } catch (NumberFormatException e) {
                // Handle the case where itemCode or quantity is not a valid integer
                showAlert("Error! Invalid input for itemCode or quantity.");
            }
        }
    }


    private void updateFromCartDatabase(String username, CartItem item) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cart_details", "root", "123456")) {
            String query = "UPDATE user_cart SET item_name = ?, item_code = ?, unit =?, quantity = ? " +
                    "WHERE user_name = ? AND item_code = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, item.getItemName());
                preparedStatement.setInt(2, item.getItemCode());
                preparedStatement.setString(3, item.getUnit());
                preparedStatement.setInt(4, item.getQuantity());
                preparedStatement.setString(5, username);
                preparedStatement.setInt(6, item.getItemCode()); // Use the item code as a condition

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeFromCartDatabase(String username, CartItem item) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cart_details", "root", "123456")) {
            String query = "DELETE FROM user_cart WHERE user_name = ? AND item_name = ? AND item_code = ? " +
                    " AND unit = ? AND quantity = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, item.getItemName());
                preparedStatement.setInt(3, item.getItemCode());
                preparedStatement.setString(4, item.getUnit());
                preparedStatement.setInt(5, item.getQuantity());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onDeleteFromCart() {
        // Get the selected item from the TableView
        CartItem selectedItem = cartTableView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            // Display a confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Delete Item");
            alert.setContentText("Are you sure you want to delete this item from the cart?");

            // Handle the user's response
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // Remove the item from the cart
                    cart.removeItem(selectedItem);

                    // Update the TableView
                    cartTableView.setItems(FXCollections.observableArrayList(cart.getItems()));

                    // Remove the item from the database
                    removeFromCartDatabase(loginUserName, selectedItem);
                }
            });
        } else {
            // Inform the user that no item is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Item Selected");
            alert.setContentText("Please select an item from the cart.");
            alert.showAndWait();
        }
    }

    private void fetchAndDisplayUserCart(String username) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cart_details", "root", "123456")) {
            String query = "SELECT * FROM user_cart WHERE user_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        // Retrieve data from the result set
                        String itemName = resultSet.getString("item_name");
                        int itemCode = Integer.parseInt(String.valueOf(resultSet.getString("item_code")));
                        String unit = resultSet.getString("unit");
                        int quantity = Integer.parseInt(String.valueOf(resultSet.getString("quantity")));
                        // Create a new CartItem
                        CartItem cartItem = new CartItem(itemName, itemCode, unit, quantity);

                        // Add the item to the cart
                        cart.addItem(cartItem);
                    }

                    // Update the cart TableView
                    cartTableView.setItems(FXCollections.observableArrayList(cart.getItems()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeCartTableView() {
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        // Set up the TableView to display cart items
        cartTableView.setItems(FXCollections.observableArrayList(cart.getItems()));

    }

    @FXML
    void onCheapestSeller(ActionEvent event) {
        try {
            cheapestSellerTextArea.clear();
            cheapestSellerTextArea.setEditable(false);
            Connection connectionPriceCatcher = DriverManager.getConnection("jdbc:mysql://localhost:3306/pricecatcher", "root", "123456");
            Statement statementPriceCatcher = connectionPriceCatcher.createStatement();
            ResultSet resultSetPriceCatcher = statementPriceCatcher.executeQuery("SELECT * FROM `pricecatcher_2023`");

            ArrayList<PriceRecord> priceRecords = new ArrayList<>();
            while (resultSetPriceCatcher.next())
                priceRecords.add(new PriceRecord(resultSetPriceCatcher.getString("date"),
                        Integer.parseInt(String.valueOf(resultSetPriceCatcher.getString("premise_code"))),
                        Integer.parseInt(String.valueOf(resultSetPriceCatcher.getString("item_code"))),
                        Double.parseDouble(String.valueOf(resultSetPriceCatcher.getString("price")))));

            Connection connectionItem = DriverManager.getConnection("jdbc:mysql://localhost:3306/lookup_premise", "root", "123456");
            Statement statementItem = connectionItem.createStatement();
            ResultSet resultSetItem = statementItem.executeQuery("SELECT * FROM lookup_premise");

            CartItem selectedItem = cartTableView.getSelectionModel().getSelectedItem();

            ArrayList<ItemDetails> itemDetails = new ArrayList<>();
            Set<ItemDetails> uniqueItemDetails = new HashSet<>();
            int itemCode = selectedItem.getItemCode();

            for (PriceRecord priceRecord : priceRecords) {
                if (priceRecord.getItemCode() == itemCode) {
                    ItemDetails newItemDetail = new ItemDetails(name, selectedItem.getUnit(), itemCode, priceRecord.getPremiseCode(), priceRecord.getPrice());
                    //Check if the itemDetails set already contains the new itemDetail
                    if (uniqueItemDetails.add(newItemDetail)) {
                        //If the itemDetail is unique, add it to the list
                        itemDetails.add(newItemDetail);
                    }
                }
            }
            Collections.sort(itemDetails);

            ArrayList<SellerDetails> sellerDetails = new ArrayList<>();

            Connection connectionPremise = DriverManager.getConnection("jdbc:mysql://localhost:3306/lookup_premise", "root", "123456");
            Statement statementPremise = connectionPremise.createStatement();
            ResultSet resultSetPremise = statementPremise.executeQuery("SELECT * FROM lookup_premise");


            while (resultSetPremise.next()) {
                for (ItemDetails itemDetail : itemDetails) {
                    if (Integer.parseInt(String.valueOf(resultSetPremise.getString("premise_code")))
                            == itemDetail.getPremiseCode()) {
                        boolean isDuplicate = false;
                        SellerDetails newSeller = new SellerDetails(resultSetPremise.getString("premise"), resultSetPremise.getString("address"),
                                resultSetPremise.getString("premise_type"), resultSetPremise.getString("state"),
                                resultSetPremise.getString("district"), itemDetail);

                        // Check for duplicates before adding
                        for (SellerDetails existingSeller : sellerDetails) {
                            if (existingSeller.equals(newSeller)) {
                                isDuplicate = true;
                                break;
                            }
                        }
                        if (!isDuplicate) {
                            sellerDetails.add(newSeller);
                        }
                    }
                }
            }

            Collections.sort(sellerDetails);

            // Display the cheapest seller information
            if (!sellerDetails.isEmpty()) {
                cheapestSellerTextArea.appendText((1 + ": " + sellerDetails.get(0).display() + "\n"));
                cheapestSellerTextArea.setEditable(false);
            } else {
                showAlert("No seller details found for the selected item.");
            }


        } catch (SQLException e) {
            showAlert("Error! Invalid input for itemCode or quantity.");
        }

    }

    @FXML
    void onCheapestSellerShop(ActionEvent event) {
        try {
            cheapestSellerTextArea.clear();
            // Connect to the "pricecatcher" database
            Connection connectionPriceCatcher = DriverManager.getConnection("jdbc:mysql://localhost:3306/pricecatcher", "root", "123456");
            Statement statementPriceCatcher = connectionPriceCatcher.createStatement();

            // Fetch data from the "pricecatcher_2023" table
            ResultSet resultSetPriceCatcher = statementPriceCatcher.executeQuery("SELECT * FROM `pricecatcher_2023`");

            ArrayList<PriceRecord> priceRecords = new ArrayList<>();
            while (resultSetPriceCatcher.next())
                priceRecords.add(new PriceRecord(
                        resultSetPriceCatcher.getString("date"),
                        Integer.parseInt(resultSetPriceCatcher.getString("premise_code")),
                        Integer.parseInt(resultSetPriceCatcher.getString("item_code")),
                        Double.parseDouble(resultSetPriceCatcher.getString("price"))
                ));

            Connection connectionItem = DriverManager.getConnection("jdbc:mysql://localhost:3306/lookup_premise", "root", "123456");
            Statement statementItem = connectionItem.createStatement();
            ResultSet resultSetItem = statementItem.executeQuery("SELECT * FROM lookup_premise");

            Map<Integer, List<ItemDetails>> premiseItemDetailsMap = new HashMap<>();

            // Iterate through each item in the cart
            for (CartItem selectedItem : cartTableView.getItems()) {
                int itemCode = selectedItem.getItemCode();

                // Iterate through each price record
                for (PriceRecord priceRecord : priceRecords) {
                    if (priceRecord.getItemCode() == itemCode) {
                        ItemDetails newItemDetail = new ItemDetails(
                                selectedItem.getItemName(),  // Replace with the actual item name
                                selectedItem.getUnit(),
                                itemCode,
                                priceRecord.getPremiseCode(),
                                priceRecord.getPrice()
                        );

                        // Group item details by premise code
                        premiseItemDetailsMap.computeIfAbsent(priceRecord.getPremiseCode(), k -> new ArrayList<>()).add(newItemDetail);
                    }
                }
            }

            // Calculate the total price for each premise
            Map<Integer, Double> totalPrices = new HashMap<>();
            for (Map.Entry<Integer, List<ItemDetails>> entry : premiseItemDetailsMap.entrySet()) {
                double totalPrice = entry.getValue().stream().mapToDouble(ItemDetails::getPrice).sum();
                totalPrices.put(entry.getKey(), totalPrice);
            }

            // Find the premise with the lowest total price
            int cheapestPremise = Collections.min(totalPrices.entrySet(), Map.Entry.comparingByValue()).getKey();

            // Display the details of the cheapest premise
            cheapestSellerTextArea.clear();
            cheapestSellerTextArea.appendText("Cheapest Seller: " + getPremiseDetails(cheapestPremise) + "\n");
            cheapestSellerTextArea.setEditable(false);

        } catch (SQLException e) {
            showAlert("Error! Invalid input for itemCode or quantity.");
        }
    }


    // Helper method to get premise details by premise code
    private String getPremiseDetails(int premiseCode) throws SQLException {
        Connection connectionPremise = DriverManager.getConnection("jdbc:mysql://localhost:3306/lookup_premise", "root", "123456");
        Statement statementPremise = connectionPremise.createStatement();
        ResultSet resultSetPremise = statementPremise.executeQuery("SELECT * FROM lookup_premise WHERE premise_code = " + premiseCode);

        if (resultSetPremise.next()) {
            return "Premise: " + resultSetPremise.getString("premise") + "\n" +
                    "Address: " + resultSetPremise.getString("address") + "\n" +
                    "State: " + resultSetPremise.getString("state") + "\n" +
                    "District: " + resultSetPremise.getString("district");
        } else {
            return "Premise details not found";
        }
    }


    private void setupMouseEventHandler() {
        // Set an event handler to detect mouse clicks on TableView rows
        cartTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) { // Check for a single click
                    CartItem selectedItem = cartTableView.getSelectionModel().getSelectedItem();
                    if (selectedItem != null) {
                        itemNameTextfield.setText(selectedItem.getItemName());
                        itemCodeTextfield.setText(String.valueOf(selectedItem.getItemCode()));
                        unitTextfield.setText(selectedItem.getUnit());
                        quantityTextfield.setText(String.valueOf(selectedItem.getQuantity()));
                    }
                }
            }
        });
    }
    private void openPage(String url, Button button) {
        try {
            // Load the FXML file for the main page
            FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
            Parent root = loader.load();

            // Create a new scene with the main page content
            Scene scene = new Scene(root);

            // Get the current stage and set its scene to the new scene
            Stage stage = (Stage) button.getScene().getWindow();
            stage.setScene(scene);

            // Show the main page
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
