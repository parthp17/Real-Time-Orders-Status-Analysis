package com.DataAnalytics.OrdersKafkaProducer;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	System.out.println("HI");
        if (args.length == 0) {
            OrderProducer.runProducer(5);
        } else {
            OrderProducer.runProducer(Integer.parseInt(args[0]));
        }

    }
}
