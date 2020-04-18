/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.cqp.util;

import au.coaas.cqp.proto.CDQLQuery;
import au.coaas.cqp.proto.ListOfString;

/**
 *
 * @author ali
 */
public class AddError {
    public static void add(CDQLQuery.Builder query,String topic,String error) {
        if(query.containsErrors(topic))
        {
            ListOfString los = query.getErrorsOrThrow(topic);
            los = los.toBuilder().addValue(error).build();
            query.putErrors(topic, los);
        }
        else
        {
            ListOfString.Builder los = ListOfString.newBuilder();
            los.addValue(error).build();
            query.putErrors(topic, los.build());
        }
    }
}
