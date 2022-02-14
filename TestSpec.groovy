package com.mcqhubb.service

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.apigateway.model.EndpointConfiguration
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.AmazonS3Exception
import com.amazonaws.services.s3.model.PutObjectRequest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.shaded.okhttp3.RequestBody

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3
import spock.lang.Specification

@SpringBootTest
class TestSpec extends Specification{

    static LocalStackContainer s3Container = new LocalStackContainer().withServices(LocalStackContainer.Service.S3).withReuse(true);

    def setup(){
        s3Container.start();

        //this is used to get local s3 bucket's important field that created in container via localstack..
        //it is used here only for testing purpose.. refer 43-50 no. lines property setting method..
         String accessKeyId = s3Container.getDefaultCredentialsProvider().getCredentials().getAWSAccessKeyId()
         String secretKey =  s3Container.getDefaultCredentialsProvider().getCredentials().getAWSSecretKey()
         String endpoint = s3Container.getEndpointConfiguration(S3).getServiceEndpoint()
         String region = s3Container.getEndpointConfiguration(S3).getSigningRegion()

    }


    /**
     * Use this method to override all the aws related property from yml file
      */
//    @DynamicPropertySource
//    public static void prop(DynamicPropertyRegistry registry){
//        registry.add("aws.accessKey",s3Container.getDefaultCredentialsProvider().getCredentials().getAWSAccessKeyId())
//        registry.add("aws.secretKey",s3Container.getDefaultCredentialsProvider().getCredentials().getAWSSecretKey())
//        registry.add("aws.region",s3Container.getEndpointConfiguration(S3).getSigningRegion())
//        registry.add("aws.secretKey",s3Container.getEndpointConfiguration(S3).getServiceEndpoint())
//
//    }


    def "two plus two should equal four"() {
        setup:
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(s3Container.getEndpointConfiguration(S3)).withCredentials(s3Container.getDefaultCredentialsProvider())
                .build();
        String bucket_name = "bucketom"
        s3.createBucket(bucket_name);
        try {
            if (s3.doesBucketExistV2(bucket_name)) {
                System.out.format(">>>>>>>>>>>>>>Bucket %s already exists.\n", bucket_name);

            } else {
                try {
                    bucket_name = s3.createBucket(bucket_name);
                    System.out.format(">>>>>>>>>>Bucket %s Created successfully .\n", bucket_name);

                } catch (AmazonS3Exception e) {
                    System.out.println("<<<<<<<<<<<<<<<<<<<<<******"+ e.getMessage() +"*******>>>>>>>>>>>>>>>>>>>>");
                }
            }

            println ">???????????? Saving...."

            def res = s3.putObject(bucket_name,"test.txt",new File("C:\\Users\\a\\IdeaProjects\\mcqhubb\\src\\main\\resources\\test.txt"))

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>"+ " File Upload Successfull "+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            println res

        } catch(Exception e){
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>"+ " Exceptoin Occered while creating bucket "+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>"+ " Exceptoin Occered while creating bucket "+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        }


        when:
        int result = 2 + 2

        then:
        result == 4
    }


    // use this to ovverride aws bean defined in pollig config.. here it should point towards local s3 bucket
//    @TestConfiguration
//    public static class beanTest{
//        @Bean
//        @Primary
//
//    }
}
