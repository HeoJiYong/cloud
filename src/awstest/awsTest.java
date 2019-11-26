package awsTest;

import java.util.Scanner;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.RebootInstancesResult;

public class awsTest {

	static AmazonEC2 ec2;
	
	private static void init() throws Exception {
		
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException(
			"Cannot load the credentials from the credential profiles file. " +
			"Please make sure that your credentials file is at the correct " +
			"location (~/.aws/credentials), and is in valid format.",
			e);

		}
		ec2 = AmazonEC2ClientBuilder.standard()
		.withCredentials(credentialsProvider)
		.withRegion("us-east-1") /* check the region at AWS console */
		.build();
	}
	
	public static void main(String[] args) throws Exception {
		init();
		Scanner menu = new Scanner(System.in);
		Scanner id_string = new Scanner(System.in);
		int number = 0;
		while(true)
		{
			System.out.println(" ");
			System.out.println(" ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" Amazon AWS Control Panel using SDK ");
			System.out.println(" ");
			System.out.println(" Cloud Computing, Computer Science Department ");
			System.out.println(" at Chungbuk National University ");
			System.out.println("------------------------------------------------------------");
			System.out.println(" 1. list instance 2. available zones ");
			System.out.println(" 3. start instance 4. available regions ");
			System.out.println(" 5. stop instance 6. create instance ");
			System.out.println(" 7. reboot instance 8. list images ");
			System.out.println(" 99. quit ");
			System.out.println("------------------------------------------------------------");
			System.out.print("Enter an integer:");

			number = menu.nextInt();

			switch (number) {
			case 1:
				listInstances();
				break;
			case 2:
				System.out.println("1 ");
				break;
			case 3:
				startInstance("i-01ab24c24bba59b0b");
				break;
			case 4:

				break;
			case 5:
				stopInstance("i-01ab24c24bba59b0b");
				break;
			case 6:
				break;
			case 7:
				//RebootInstance();
				break;
			case 8:

				break;
			case 99:

				break;
			}
		}

	}
	public static void listInstances()
	{
		System.out.println("Listing instances....");
		boolean done = false;
	
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		while(!done) {
				DescribeInstancesResult response = ec2.describeInstances(request);
					for(Reservation reservation : response.getReservations()) {
						for(Instance instance : reservation.getInstances()) {
							System.out.printf(
									"[id] %s, " +
									"[AMI] %s, " +
									"[type] %s, " +
									"[state] %10s, " +
									"[monitoring state] %s",
									instance.getInstanceId(),
									instance.getImageId(),
									instance.getInstanceType(),
									instance.getState().getName(),
									instance.getMonitoring().getState());
						}
						System.out.println();
					}
			request.setNextToken(response.getNextToken());
	
			if(response.getNextToken() == null) {
				done = true;
			}
		}
	}
	
	    public static void startInstance(String inst_id)
	    {
	 
	        DryRunSupportedRequest<StartInstancesRequest> dry_request =
	            () -> {
	            StartInstancesRequest request = new StartInstancesRequest()
	                .withInstanceIds(inst_id);

	            return request.getDryRunRequest();
	        };

	        DryRunResult dry_response = ec2.dryRun(dry_request);

	        if(!dry_response.isSuccessful()) {
	            System.out.printf(
	                "Failed dry run to start instance %s", inst_id);

	            throw dry_response.getDryRunResponse();
	        }

	        StartInstancesRequest request = new StartInstancesRequest()
	            .withInstanceIds(inst_id);

	        ec2.startInstances(request);

	        System.out.printf("Successfully started instance %s", inst_id);
	    }

	    public static void stopInstance(String inst_id)
	    {
	     
	        DryRunSupportedRequest<StopInstancesRequest> dry_request =
	            () -> {
	            StopInstancesRequest request = new StopInstancesRequest()
	                .withInstanceIds(inst_id);

	            return request.getDryRunRequest();
	        };

	        DryRunResult dry_response = ec2.dryRun(dry_request);

	        if(!dry_response.isSuccessful()) {
	            System.out.printf(
	                "Failed dry run to stop instance %s", inst_id);
	            throw dry_response.getDryRunResponse();
	        }

	        StopInstancesRequest request = new StopInstancesRequest()
	            .withInstanceIds(inst_id);

	        ec2.stopInstances(request);

	        System.out.printf("Successfully stop instance %s", inst_id);
	    }
	    
	    public static void RebootInstance(String inst_id)
	    {
            final String USAGE =
	                "To run this example, supply an instance id\n" +
	                "Ex: RebootInstance <instance_id>\n";
	            if (inst_id.length() == 0) {
	                System.out.println(USAGE);
	                System.exit(1);
	            }
	            String instance_id = inst_id;

	            RebootInstancesRequest request = new RebootInstancesRequest()
	                .withInstanceIds(instance_id);

	            RebootInstancesResult response = ec2.rebootInstances(request);

	            System.out.printf(
	                "Successfully rebooted instance %s", instance_id);
	     }
	
}