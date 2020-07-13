package com.assignment.aws.lambdaFunction.handler;

import java.util.UUID;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

@Component
public class DeleteDataLambdaHandler implements Function<APIGatewayProxyRequestEvent,String> {
	
	private Logger logger = LoggerFactory.getLogger(DeleteDataLambdaHandler.class);
	
	@Override
	public String apply(final APIGatewayProxyRequestEvent event) {
		
		String[] request = event.getBody().split("=");
		logger.info(request[0]);
		logger.info(request[1]);
		logger.info("rating = " + event.getBody());
		
		try {
			final AmazonDynamoDBClient client = new AmazonDynamoDBClient(new EnvironmentVariableCredentialsProvider());
	        client.withRegion(Regions.AP_SOUTH_1); // specify the region you created the table in.
	        DynamoDB dynamoDB = new DynamoDB(client);
	        Table table = dynamoDB.getTable("feedback");
	        String id = request[1];
	        logger.info("Connection established.");
	        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
	    			.withPrimaryKey(new PrimaryKey("id", id));
	        try {
	            System.out.println("Attempting a conditional delete...");
	            table.deleteItem(deleteItemSpec);
	            System.out.println("DeleteItem succeeded");
	        }
	        catch (Exception e) {
	            System.err.println("Unable to delete item: " + id);
	            System.err.println(e.getMessage());
	        }
	        
	        
	        String response = "<!DOCTYPE html><div style=\"text-align:center;\"><H3>Successfully deleted rating for id: "+ id + "</H3><br>Go back to <a href=\"https://capstone-project-1.s3.ap-south-1.amazonaws.com/dashboard.html\">Dashboard</a></div>";
	        
			logger.info("Success");
			return response;
		} catch(Exception e) {
			return "<!DOCTYPE html><h3>Something was not right</h3>";
		}
	}
}
