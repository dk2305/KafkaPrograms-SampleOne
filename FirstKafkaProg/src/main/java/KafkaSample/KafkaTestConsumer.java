package KafkaSample;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class KafkaTestConsumer {

    private static Scanner in;
    private static boolean stop = false;

    public static void main(String[] args) throws InterruptedException {
        if(args.length != 2) {
            System.err.printf("Usage : %s <topicName> <groupId>",
                    KafkaTestConsumer.class.getSimpleName());
            System.exit(-1);
        }

        in = new Scanner(System.in);
        String topicName = args[0];
        String groupId = args[1];

        ConsumerThread consumerRunnable = new ConsumerThread(topicName,groupId);
        consumerRunnable.start();
        String line = "";
        while(!line.equals("exit")){
            line = in.next();
        }

        consumerRunnable.getKafkaConsumer().wakeup();
        System.out.println("Stooping consumer...");
        consumerRunnable.join();

    }

    private static class ConsumerThread extends Thread{

        private String topicName;
        private String groupId;
        private KafkaConsumer<String,String> kafkaConsumer;

        public ConsumerThread(String topicName, String groupId) {
            this.topicName = topicName;
            this.groupId = groupId;
        }

        public void run(){
            // Consumer Properties
            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer");
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,groupId);

            // Create kafka consumer
            kafkaConsumer = new KafkaConsumer<String, String>(properties);
            kafkaConsumer.subscribe(Arrays.asList(topicName));

            //Start Processing
            try{
                while(true){
                    ConsumerRecords<String,String> records = kafkaConsumer.poll(100);
                    for(ConsumerRecord<String,String> record : records){
                        System.out.println(record.value());
                    }
                }
            }catch(WakeupException ex){
                System.out.println("Exception Caught "+ ex.getMessage());
            }finally {
                kafkaConsumer.close();
                System.out.println("After closing KafkaConsumer");
            }
        }

        public KafkaConsumer<String,String> getKafkaConsumer() {
            return this.kafkaConsumer;
        }
    }
}
