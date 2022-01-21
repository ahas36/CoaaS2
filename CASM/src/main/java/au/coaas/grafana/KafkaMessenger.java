package au.coaas.grafana;

import fish.payara.cloud.connectors.kafka.api.KafkaConnection;
import fish.payara.cloud.connectors.kafka.api.KafkaConnectionFactory;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.resource.ConnectionFactoryDefinition;
import javax.resource.spi.TransactionSupport;
import java.util.logging.Level;
import java.util.logging.Logger;

@ConnectionFactoryDefinition(name = "java:module/env/KafkaConnectionFactory",
        description = "Kafka Connection Factory",
        interfaceName = "fish.payara.cloud.connectors.kafka.KafkaConnectionFactory",
        resourceAdapter = "kafka-rar-0.5.0",
        minPoolSize = 2,
        maxPoolSize = 2,
        transactionSupport = TransactionSupport.TransactionSupportLevel.NoTransaction,
        properties = {
                "bootstrapServersConfig=kafka:9092",
                "clientId=PayaraMicroMessenger"
        })
@Stateless
public class KafkaMessenger {
    @Resource(lookup = "java:module/env/KafkaConnectionFactory")
    KafkaConnectionFactory factory;

    @Schedule(hour = "*", minute = "*", second = "*/5", persistent = false)
    public void sendMessage() {
        try (KafkaConnection conn = factory.createConnection()) {
            conn.send(new ProducerRecord("testing", "Sent from Payara Micro."));
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

}
