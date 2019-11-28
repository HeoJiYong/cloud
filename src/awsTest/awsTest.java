package awsTest;

import java.util.Scanner;


import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;

import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;

import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;


import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;

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
			System.out.println(" 9. DeleteInstance");
			System.out.println(" 99. quit ");
			System.out.println("------------------------------------------------------------");
			System.out.print("Enter an integer:");

			number = menu.nextInt();
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
				startInstance(inst_id);
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
				ListImages();
				break;
				
			case 9:
				DeleteInstance();
				break;
			case 99:
				System.out.println("Quit.... The Program is shutdowned");
				System.exit(0);
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
                "in region %s\n",
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
	                "with endpoint %s\n",
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
        	try {
        		Scanner keyNameTemp = new Scanner(System.in);
            	String keyName;
            	Scanner imageTemp = new Scanner(System.in);
    			String imageId ; // Basic 32-bit Amazon Linux AMI
    			String instanceType = "t2.micro";
    			int minInstanceCount = 1; // create 1 instance
    			int maxInstanceCount = 1;
    			
    			System.out.print("write your keyName: ");
    			keyName = keyNameTemp.nextLine();
    			System.out.print("write your imageID: ");
    			imageId = imageTemp.nextLine();
    			
    			RunInstancesRequest rir = new RunInstancesRequest();
    			rir.withImageId(imageId).withInstanceType(instanceType).withMinCount(minInstanceCount)
    					.withMaxCount(maxInstanceCount).withKeyName(keyName);
    			RunInstancesResult result = ec2.runInstances(rir);
    			System.out.printf("Succecfully Created The Instance from imageID : %s \n",imageId);
        	}catch(Exception e)
        	{
        		System.out.println("Ther are Unexpected ERROR");
        		System.out.println("This Function is shutdowned");
        		return;
        	}
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
	    
	   //---------------------8. list images----------------
	    public static void ListImages() {
			System.out.println("Loading images......");
			
			DescribeImagesRequest request = new DescribeImagesRequest().withOwners("self");
			DescribeImagesResult Images = ec2.describeImages(request);
			for (Image Im : Images.getImages()) {
				System.out.printf("[AMIid] %s, [AMI Status] %s, [AMIname] %s \n", Im.getImageId(), Im.getState(),
						Im.getName());
			}
	    }
	    
	    //---------------------- 9 DeleteInstance -----------------
	    public static void DeleteInstance()
	    {
	    	System.out.print("Write Instance-ID for Delete : ");
	    	Scanner sdel_id = new Scanner(System.in);
	    	String del_id ;
	    	del_id = sdel_id.nextLine();
	    	try {
	    		TerminateInstancesRequest request = new TerminateInstancesRequest().withInstanceIds(del_id);
				ec2.terminateInstances(request);
				System.out.printf("Successfuly Delete ID : %s\n",del_id);
	    	}catch(Exception e){
	    		System.out.println("! There are Unexcepted Error");
	    		System.out.println("! Failed Delete");
	    	}
	    	
	    	
	    }
}