package au.coaas.situations;

import au.coaas.cqc.proto.CQCServiceGrpc;
import au.coaas.cqc.proto.EventRequest;
import au.coaas.grpc.client.CQCChannel;
import au.coaas.cqc.proto.CdqlResponse;

import fish.payara.cloud.connectors.kafka.api.OnRecord;
import fish.payara.cloud.connectors.kafka.api.KafkaListener;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import javax.ejb.MessageDriven;
import javax.ejb.ActivationConfigProperty;

import java.util.logging.Logger;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "topics",
                propertyValue = "event"),
        @ActivationConfigProperty(propertyName = "pollInterval",
                propertyValue = "300"),
        @ActivationConfigProperty(propertyName = "retryBackoff",
                propertyValue = "1000"),
        @ActivationConfigProperty(propertyName = "autoCommitInterval",
                propertyValue = "100"),
        @ActivationConfigProperty(propertyName = "clientId",
                propertyValue = "PayaraMicroMessenger"),
        @ActivationConfigProperty(propertyName = "groupIdConfig",
                propertyValue = "test-consumer-group"),
        @ActivationConfigProperty(propertyName = "bootstrapServersConfig",
                propertyValue = "docker.host.internal:9092"),
        @ActivationConfigProperty(propertyName = "keyDeserializer",
                propertyValue = "org.apache.kafka.common.serialization.StringDeserializer"),
        @ActivationConfigProperty(propertyName = "valueDeserializer",
                propertyValue = "org.apache.kafka.common.serialization.StringDeserializer")
    })

public class EventListner implements KafkaListener {

    private static Logger log = Logger.getLogger(EventListner.class.getName());

    @OnRecord(topics = {"event"})
    public CdqlResponse handelEvent(ConsumerRecord record){
        String eventString = record.value().toString();
        String provider = "";

        CQCServiceGrpc.CQCServiceBlockingStub stub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        return stub.handleSituation(EventRequest.newBuilder()
                .setEvent(eventString).setProvider(provider).build());
    }
}
