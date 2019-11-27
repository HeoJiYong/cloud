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
		String inst_id;
		
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

			/*
			 * 	check list
			 * 	2, 4, 6
			 * 
			 * must be implemented list
			 * 8(list images)
			 * 
			 * */
			switch (number) {
			case 1:		//list
				listInstances();
				break;
				
			case 2:		//available zone
				availablezones();
				break;
				
			case 3:		//start
				System.out.print("Write instance ID : ");
				inst_id = id_string.nextLine();
				//startInstance(i-01ab24c24bba59b0b);
				startInstance("inst_id");
				break;
				
			case 4:		//availabe regions
				availableregions();
				break;
				
			case 5:		//stop
				System.out.print("Write instance ID : ");
				inst_id = id_string.nextLine();
				stopInstance(inst_id);
				break;
				
			case 6:		//create instance
				createinstance();
				break;
				
			case 7:		//reboot
				System.out.print("Write instance ID : ");
				inst_id = id_string.nextLine();
				RebootInstance(inst_id);
				break;
				
			case 8:		//list images
				break;
				
			case 99:
				break;
			}
		}
	}
	
	//---------------------1. Create Instance---------------
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
	
	//---------------------2. availablezone---------------
	public static void availablezones()
    {
        DescribeAvailabilityZonesResult zones_response =
            ec2.describeAvailabilityZones();

        for(AvailabilityZone zone : zones_response.getAvailabilityZones()) {
            System.out.printf(
                "Found availability zone %s " +
                "with status %s " +
                "in region %s",
                zone.getZoneName(),
                zone.getState(),
                zone.getRegionName());
        }
    }
	
	//---------------------3. startInstance---------------
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

	  //---------------------4. available regions---------------
	    public static void availableregions()
	    {
	        DescribeRegionsResult regions_response = ec2.describeRegions();
	
	        for(Region region : regions_response.getRegions()) {
	            System.out.printf(
	                "Found region %s " +
	                "with endpoint %s",
	                region.getRegionName(),
	                region.getEndpoint());
	        }
	    }
	    
	  //---------------------5. stopInstance---------------
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
	    
	  //---------------------6. Create Instance---------------
        public static void createinstance()
        {
            final String USAGE =
                "To run this example, supply an instance name and AMI image id\n" +
                "Ex: CreateInstance <instance-name> <ami-image-id>\n";

            if (args.length != 2) {
                System.out.println(USAGE);
                System.exit(1);
            }
            Scanner S_name = new Scanner(System.in);
            Scanner S_ami_id = new Scanner(System.in);
            String name;
            String ami_id;
            
            System.out.print("Write name : ");
            name = S_name.nextLine();
            System.out.print("Write ami_id : ");
            ami_id = S_ami_id.nextLine();
            
            RunInstancesRequest run_request = new RunInstancesRequest()
                .withImageId(ami_id)
                .withInstanceType(InstanceType.T1Micro)
                .withMaxCount(1)
                .withMinCount(1);

            RunInstancesResult run_response = ec2.runInstances(run_request);

            String reservation_id = run_response.getReservation().getInstances().get(0).getInstanceId();

            Tag tag = new Tag()
                .withKey("Name")
                .withValue(name);

            CreateTagsRequest tag_request = new CreateTagsRequest()
                .withTags(tag);

            CreateTagsResult tag_response = ec2.createTags(tag_request);

            System.out.printf(
                "Successfully started EC2 instance %s based on AMI %s",
                reservation_id, ami_id);
        }
	    
	  //---------------------7. RebootInstance----------------
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
	    
	   //---------------------8. RebootInstance----------------
	   
	
}