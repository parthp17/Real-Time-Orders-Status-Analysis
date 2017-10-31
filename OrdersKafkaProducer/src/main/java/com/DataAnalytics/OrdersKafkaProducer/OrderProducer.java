package com.DataAnalytics.OrdersKafkaProducer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Random;

/**
 * Created by Parth on 9/14/2017.
 */
public class OrderProducer {

    //Type of Order Statuses
    enum STATUS{PLACED,APPROVED,INTRANSIT,SHIPPED};

    //Name of the topic
    private static final String TOPIC = "orders2";

    //Kafka multi broker cluster to achieve scalability
    private final static String BOOTSTRAP_SERVERS = "192.168.67.1:9092,192.168.67.129:9092,192.168.67.133:9092";
    
    //returns the instance of the KafkaProducer
    private static Producer<Long,String> createProducer(){
        //initailize the properties of the kafkaproducer
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG,"OrdersKafkaProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<Long, String>(props);
    }


    static void runProducer(final int sendMessageCount) throws Exception {
        final Producer<Long,String> ordersProducer = createProducer(); //creates the kakfa-producer
        long startingTime = System.currentTimeMillis();
        // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        // System.out.println("in");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        try{
            long index = 0;
            StringBuilder sb = new StringBuilder();
            // Random random = new Random();
            // random.nextInt();
            while(true){
                //StringBuilder will have string like: "2007-12-03T10:15:30,XXXXX-XXX,PLACED"
            	sb.append(dtf.format(LocalDateTime.now()) + ",");
            	sb.append("XXXXX-XXX"+",");
            	sb.append(STATUS.values()[(int)(Math.random()*4)]); //pick the value from the available list of statuses
                //produce an instance of the record
                final ProducerRecord<Long, String> record =
                        new ProducerRecord<>(TOPIC, index,
                                sb.toString());
                
                //will be sent to Kafka
                RecordMetadata metadata = ordersProducer.send(record).get();

                System.out.printf("sent a record of (key=%s value=%s) meta(partition=%d, offset=%d) time=%d\n",
                        record.key(), record.value(), metadata.partition(),
                        metadata.offset(), System.currentTimeMillis() - startingTime);

                sb.setLength(0);
                index++;
            }
        }finally {
            ordersProducer.flush();
            ordersProducer.close();
        }
    }




}