package hahn.learning.dynamodb;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.*;

/**
 * Created by jianghan on 16/4/23.
 */
public class DynamodbSample {

    static String tableName = "ExampleTable";
    static DynamoDB dynamoDB;

    public static void main(String[] args) throws Exception {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("local_db", "local_db");
        AmazonDynamoDBClient client = new AmazonDynamoDBClient(awsCreds);
        client.setEndpoint("http://localhost:8000");
        dynamoDB = new DynamoDB(client);

        PutItemOutcome outcome = putData();
        System.out.println("outcome: " + outcome.getPutItemResult());

        Item item = getData();
        System.out.println(item.toJSONPretty());
        // createExampleTable();
        // listMyTables();
        // getTableInformation();
        // updateExampleTable();
        // deleteExampleTable();
    }

    static Item getData() {
        Table table = dynamoDB.getTable(tableName);
        return table.getItem("Id", 206);
    }

    static PutItemOutcome putData() {
        Table table = dynamoDB.getTable(tableName);
        // Build a list of related items
        List<Number> relatedItems = new ArrayList<>();
        relatedItems.add(341);
        relatedItems.add(472);
        relatedItems.add(649);

        //Build a map of product pictures
        Map<String, String> pictures = new HashMap<>();
        pictures.put("FrontView", "http://example.com/products/206_front.jpg");
        pictures.put("RearView", "http://example.com/products/206_rear.jpg");
        pictures.put("SideView", "http://example.com/products/206_left_side.jpg");

        //Build a map of product reviews
        Map<String, List<String>> reviews = new HashMap<>();

        List<String> fiveStarReviews = new ArrayList<>();
        fiveStarReviews.add("Excellent! Can't recommend it highly enough!  Buy it!");
        fiveStarReviews.add("Do yourself a favor and buy this");
        reviews.put("FiveStar", fiveStarReviews);

        List<String> oneStarReviews = new ArrayList<>();
        oneStarReviews.add("Terrible product!  Do not buy this.");
        reviews.put("OneStar", oneStarReviews);

        // Build the item
        Item item = new Item()
                .withPrimaryKey("Id", 206)
                .withString("Title", "20-Bicycle 206")
                .withString("Description", "206 description")
                .withString("BicycleType", "Hybrid")
                .withString("Brand", "Brand-Company C")
                .withNumber("Price", 500)
                .withStringSet("Color", new HashSet<>(Arrays.asList("Red", "Black")))
                .withString("ProductCategory", "Bike")
                .withBoolean("InStock", true)
                .withNull("QuantityOnHand")
                .withList("RelatedItems", relatedItems)
                .withMap("Pictures", pictures)
                .withMap("Reviews", reviews);

        // Write the item to the table
        PutItemOutcome outcome = table.putItem(item);
        return outcome;
    }

    static void createExampleTable() {
        try {
            ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
            attributeDefinitions.add(new AttributeDefinition()
                    .withAttributeName("Id")
                    .withAttributeType("N"));

            ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
            keySchema.add(new KeySchemaElement()
                    .withAttributeName("Id")
                    .withKeyType(KeyType.HASH)); //Partition key

            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(keySchema)
                    .withAttributeDefinitions(attributeDefinitions)
                    .withProvisionedThroughput(new ProvisionedThroughput()
                            .withReadCapacityUnits(5L)
                            .withWriteCapacityUnits(6L));

            System.out.println("Issuing CreateTable request for " + tableName);
            Table table = dynamoDB.createTable(request);

            System.out.println("Waiting for " + tableName
                    + " to be created...this may take a while...");
            table.waitForActive();

            getTableInformation();

        } catch (Exception e) {
            System.err.println("CreateTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }

    static void listMyTables() {
        TableCollection<ListTablesResult> tables = dynamoDB.listTables();
        Iterator<Table> iterator = tables.iterator();

        System.out.println("Listing table names");

        while (iterator.hasNext()) {
            Table table = iterator.next();
            System.out.println(table.getTableName());
        }
    }

    static void getTableInformation() {
        System.out.println("Describing " + tableName);

        TableDescription tableDescription = dynamoDB.getTable(tableName).describe();
        System.out.format("Name: %s:\n" + "Status: %s \n"
                        + "Provisioned Throughput (read capacity units/sec): %d \n"
                        + "Provisioned Throughput (write capacity units/sec): %d \n",
                tableDescription.getTableName(),
                tableDescription.getTableStatus(),
                tableDescription.getProvisionedThroughput().getReadCapacityUnits(),
                tableDescription.getProvisionedThroughput().getWriteCapacityUnits());
    }

    static void updateExampleTable() {
        Table table = dynamoDB.getTable(tableName);
        System.out.println("Modifying provisioned throughput for " + tableName);

        try {
            table.updateTable(new ProvisionedThroughput()
                    .withReadCapacityUnits(6L).withWriteCapacityUnits(7L));

            table.waitForActive();
        } catch (Exception e) {
            System.err.println("UpdateTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }

    static void deleteExampleTable() {
        Table table = dynamoDB.getTable(tableName);
        try {
            System.out.println("Issuing DeleteTable request for " + tableName);
            table.delete();

            System.out.println("Waiting for " + tableName
                    + " to be deleted...this may take a while...");

            table.waitForDelete();
        } catch (Exception e) {
            System.err.println("DeleteTable request failed for " + tableName);
            System.err.println(e.getMessage());
        }
    }

}
