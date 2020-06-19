
package edu.au.cc.gallery;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;



public class S3 {
	private static final Region region = Region.US_EAST_2;
	private S3Client client;

	public void connect() {
        client = S3Client.builder().region(region).build();
	}

	public void createBucket(String bucketName) {
	CreateBucketRequest createBucketRequest = CreateBucketRequest
        .builder()
        .bucket(bucketName)
        .createBucketConfiguration(CreateBucketConfiguration.builder()
                .locationConstraint(region.id())
                .build())
        .build();
	client.createBucket(createBucketRequest);
	}

	public void putObject(String bucketName, String key, String value){
	PutObjectRequest por = PutObjectRequest.builder()
		.bucket(bucketName)
		.key(key)
		.build();
	client.putObject(por, RequestBody.fromString(value));

	}

	public static void demo() {
	S3 s3 = new S3();
	s3.connect();
	s3.createBucket("this.image.gallery");
        s3.putObject("this.image.gallery", "banana", "yellow");	
	}
 }
