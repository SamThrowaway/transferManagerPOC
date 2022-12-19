/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package com.mycompany.sample_aws;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
//import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
//import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Regions;
//import com.amazonaws.services.mediastoredata.model.PutObjectRequest;
import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;
//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.concurrent.Executors;

/**
 *
 * @author sam huynh
 */
public class Sample_aws {
    
    public static void uploadFileToS3 (TransferManager transferManagerClient, AmazonS3 s3client) throws AmazonClientException, AmazonServiceException, InterruptedException {

        /* Upload file via s3 client */
        long start = System.currentTimeMillis();
        s3client.putObject(
                "large-object-test-bucket", "100MB_file1", 
                new File("/Users/huynh/Documents/100MB.bin")
        );  
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Upload time from S3Client: " + (elapsed/1000) + " seconds");
        
        /* Upload file via transfer manager */
        Upload upload = transferManagerClient.upload("large-object-test-bucket", "100MB_file2", new File("/Users/huynh/Documents/100MB.bin"));
        start = System.currentTimeMillis();
        upload.waitForCompletion();
        elapsed = System.currentTimeMillis() - start;
        
        System.out.println("Upload time from TransferManagerClient: " + (elapsed/1000) + " seconds");
        transferManagerClient.shutdownNow();
    }
    
    public static void downloadFileFromS3(TransferManager transferManagerClient, AmazonS3 s3client) throws AmazonClientException, AmazonServiceException, InterruptedException {
        
        /* Download via s3 client */
        long start = System.currentTimeMillis();
        s3client.getObject(
                new GetObjectRequest("large-object-test-bucket", "100MB.bin"), // (bucketname, filename)
                new File("/Users/huynh/downloads/downloaded_file2")
        );  
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Download time from S3Client: " + (elapsed/1000) + " seconds");
        
        /* download via transfer manager */
        Download download = transferManagerClient.download("large-object-test-bucket", "100MB.bin", new File("/Users/huynh/downloads/downloaded_file1"));
        start = System.currentTimeMillis();
        download.waitForCompletion();
        elapsed = System.currentTimeMillis() - start;
        
        System.out.println("Download time from TransferManagerClient: " + (elapsed/1000) + " seconds");
        transferManagerClient.shutdownNow();
    }

    public static void main(String[] args) {
        
        AWSCredentials credentials = new BasicAWSCredentials("", "");
        
        ClientConfiguration config = new ClientConfiguration();
        config.setProtocol(Protocol.HTTPS);
        
        AmazonS3 s3client = AmazonS3ClientBuilder.standard()
            .withClientConfiguration(config)
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            .withRegion(Regions.US_EAST_1)
            .build();
        
        TransferManager transferManagerClient = TransferManagerBuilder.standard().withS3Client(s3client).build();
        
        /* Upload */
//        try {
//            uploadFileToS3(transferManagerClient, s3client);
//        } catch (InterruptedException e) {
//            System.err.println("Could not upload file");
//        } 
        
        /* Download */
        try {
            downloadFileFromS3(transferManagerClient, s3client);
        } catch (InterruptedException e) {
            System.err.println("Could not download file");
        }      
    }
}
