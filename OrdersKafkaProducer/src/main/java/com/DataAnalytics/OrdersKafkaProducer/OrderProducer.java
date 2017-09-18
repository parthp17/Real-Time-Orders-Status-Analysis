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

    enum STATUS{PLACED,APPROVED,INTRANSIT,SHIPPED};

    private static final String TOPIC = "orders2";
    private final static String BOOTSTRAP_SERVERS = "192.168.67.1:9092,192.168.67.129:9092,192.168.67.133:9092";

    private static Producer<Long,String> createProducer(){

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG,"OrdersKafkaProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<Long, String>(props);
    }

    static void runProducer(final int sendMessageCount) throws Exception {

        final Producer<Long,String> ordersProducer = createProducer();
        long time = System.currentTimeMillis();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("in");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        try{
            long index = 0;
            StringBuilder sb = new StringBuilder();
            Random r = new Random();
            r.nextInt();
            while(true){
            	sb.append(dtf.format(LocalDateTime.now()) + ",");
            	sb.append("XXXXX-XXX"+",");
            	sb.append(STATUS.values()[(int)(Math.random()*4)]);
                final ProducerRecord<Long, String> record =
                        new ProducerRecord<>(TOPIC, index,
                                sb.toString());
                RecordMetadata metadata = ordersProducer.send(record).get();
                long elapsedTime = System.currentTimeMillis() - time;
                System.out.printf("sent record(key=%s value=%s) " +
                                "meta(partition=%d, offset=%d) time=%d\n",
                        record.key(), record.value(), metadata.partition(),
                        metadata.offset(), elapsedTime);
                sb.setLength(0);
                //index++;
            }
        }finally {
            ordersProducer.flush();
            ordersProducer.close();
        }
    }




}