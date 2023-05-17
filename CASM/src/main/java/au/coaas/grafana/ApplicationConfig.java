/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.grafana;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author ali
 */
@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(RequestFilter.class);
        resources.add(CorsFilter.class);
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(AuthenticationInterface.class);
        resources.add(QueryInterface.class);
        resources.add(EntityInterface.class);
        resources.add(GenericInterface.class);
        resources.add(SemanticInterface.class);
        resources.add(ServiceInterface.class);
        resources.add(SituationInterface.class);
        resources.add(SubscriptionInterface.class);
        resources.add(GrafanaInterface.class);
        resources.add(LogInterface.class);
        resources.add(ConsumerInterface.class);
        resources.add(StatisticsInterface.class);
        resources.add(EventInterface.class);
    }
}
