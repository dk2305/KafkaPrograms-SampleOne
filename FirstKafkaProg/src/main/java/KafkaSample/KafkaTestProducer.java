package KafkaSample;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Scanner;

public class KafkaTestProducer {

    private static Scanner in;

    public static void main(String[] args) {

        if(args.length != 1){
            System.err.println("Please specify 1 parameter");
            System.exit(-1);
        }

        String topicName = args[0];
        in = new Scanner(System.in);
        System.out.println("Enter message (Type exit to quit)");

        // Configure Producer
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer");

        // Create Producer
        Producer  producer = new KafkaProducer<String,String>(properties);

        String line = in.nextLine();
        while (!line.equals("exit")){
            ProducerRecord<String,String> producerRecord = new ProducerRecord<String, String>(topicName,line);
            producer.send(producerRecord);
            line = in.nextLine();
        }
        in.close();
        producer.close();
    }
}
