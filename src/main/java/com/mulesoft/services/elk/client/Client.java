package com.mulesoft.services.elk.client;


import com.mulesoft.services.elk.pojos.Config;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public final class Client {
    private static final Logger LOGGER = StatusLogger.getLogger();

    private static final Map<String, Client> clientByLoggerName = new HashMap<>();

    Config config;
    RestHighLevelClient client;

    boolean bulkMode;
    BulkRequest bulkRequest;
    long bulkRequestCreationTime;
    ScheduledExecutorService bulkFlushTimeOutCheckerExecutor;


    //Life cycle and configuration
    public static Client getInstance(Config config) throws IOException {
        Client client = clientByLoggerName.get(config.getName());
        if (client==null)
            clientByLoggerName.put(config.getName(), client = new Client(config));
        return client;
    }

    Client(Config config) throws IOException {
        this.config = config;
        startup();
    }

    void startup() throws IOException {
        try {
            shutdown();
        } catch (Exception e) {

        }

        HttpHost host = new HttpHost(config.getServer().getHost(), config.getServer().getPort(), config.getServer().getProtocol());

        RestClientBuilder clientBuilder = RestClient.builder(host);
        basicAuthentication(clientBuilder);

        client = new RestHighLevelClient(clientBuilder);

        if (!client.indices().exists(new GetIndexRequest().indices(config.getIndex()), RequestOptions.DEFAULT))
            client.indices().create(new CreateIndexRequest(config.getIndex()), RequestOptions.DEFAULT);
    }

    void basicAuthentication(RestClientBuilder builder) {
        if (config.getUsername()!=null && config.getUsername().trim().length()>0 && config.getPassword()!=null && config.getPassword().trim().length()>0) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("user", "password"));
            builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
    }

    void shutdown() throws IOException {
        try {
            if (bulkRequest!=null)
                sendBulkRequest();
            stopBulkFlushTimeOutChecker();
        } finally {
            if (client!=null)
                client.close();
        }
    }

    //Store function implementations
    public void storeJsonDocument(String document, boolean closeBatch) throws IOException {
        storeDocument(
                new IndexRequest(config.getIndex(), "doc", null).source(document, XContentType.JSON),
                closeBatch
        );
    }
    public void storeMapDocument(Map<String, Object> document, boolean closeBatch) throws IOException {
        storeDocument(
                new IndexRequest(config.getIndex(), "doc", null).source(document),
                closeBatch
        );
    }
    public void storeXContentDocument(XContentBuilder document, boolean closeBatch) throws IOException {
        storeDocument(
                new IndexRequest(config.getIndex(), "doc", null).source(document),
                closeBatch
        );
    }
    synchronized void storeDocument(IndexRequest indexRequest, boolean closeBatch) throws IOException {
        if (bulkMode || closeBatch) {
            //Bulk
            if (bulkRequest == null) {
                if (!bulkMode) {
                    bulkMode = true;
                }
                bulkRequest = new BulkRequest();
                bulkRequestCreationTime = System.currentTimeMillis();
            }

            bulkRequest.add(indexRequest);

            if (closeBatch)
                sendBulkRequest();
        } else
            //Single
            client.index(indexRequest, RequestOptions.DEFAULT);
    }


    private synchronized void sendBulkRequest() throws IOException {
        try {
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } finally {
            bulkRequest = null;
        }
    }


    //Bulk buffer flush timeout management
    private void startBulkFlushTimeOutChecker() {
        bulkFlushTimeOutCheckerExecutor = Executors.newSingleThreadScheduledExecutor();
        bulkFlushTimeOutCheckerExecutor.scheduleAtFixedRate(
            () -> {
                //Timeout check based on bulk request creation time. Check is scheduled every 5 seconds
                if (bulkRequest!=null)
                    try {
                        sendBulkRequest();
                    } catch (IOException e) {
                        LOGGER.error("Error sending log to ELK during the bulk request execution",e);
                    }
            },
            5,
            5,
            TimeUnit.SECONDS);
    }
    private void stopBulkFlushTimeOutChecker() {
        if (bulkFlushTimeOutCheckerExecutor!=null && !bulkFlushTimeOutCheckerExecutor.isShutdown())
            bulkFlushTimeOutCheckerExecutor.shutdown();
    }
}
