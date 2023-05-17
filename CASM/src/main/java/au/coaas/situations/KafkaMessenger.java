package au.coaas.situations;

import fish.payara.cloud.connectors.kafka.api.KafkaConnectionFactory;
import fish.payara.cloud.connectors.kafka.api.KafkaConnection;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.ejb.Stateless;
import javax.annotation.Resource;
import javax.resource.spi.TransactionSupport;
import javax.resource.ConnectionFactoryDefinition;

import java.util.logging.Logger;

@ConnectionFactoryDefinition(name = "java:comp/env/KafkaConnectionFactory",
        description = "Kafka Connection Factory",
        interfaceName = "fish.payara.cloud.connectors.kafka.KafkaConnectionFactory",
        resourceAdapter = "kafka-rar-0.3.0",
        minPoolSize = 200,
        maxPoolSize=2000,
        transactionSupport = TransactionSupport.TransactionSupportLevel.NoTransaction,
        properties = {
                "bootstrapServersConfig=docker.host.internal:9092",
                "clientId=PayaraMicroMessenger"
        })

@Stateless
public class KafkaMessenger {
    private static Logger log = Logger.getLogger(EventListner.class.getName());

    @Resource(lookup = "java:comp/env/KafkaConnectionFactory")
    static KafkaConnectionFactory factory;

    public static void sendMessage(String msg, String topic) {
        try (KafkaConnection conn = factory.createConnection()) {
            conn.send(new ProducerRecord(topic, msg));
        } catch (Exception ex) {
            log.severe("Error when producing kafka message:" + ex.getMessage());
        }
    }
}
