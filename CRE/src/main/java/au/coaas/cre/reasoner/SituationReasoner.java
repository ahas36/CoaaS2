package au.coaas.cre.reasoner;

import au.coaas.cqp.proto.RegionDescription;
import au.coaas.cqp.proto.SituationDescription;
import au.coaas.cqp.proto.WeightedAttributeDescription;
import au.coaas.cre.proto.AttributeValue;
import au.coaas.cre.proto.CREResponse;
import au.coaas.cre.proto.ReasoningResponse;
import au.coaas.cre.proto.SituationInferenceRequest;
import au.coaas.sqem.proto.SQEMResponse;
import com.hp.hpl.jena.sparql.resultset.JSONObjectResult;
import csiro.perccom.csto.util.CstoUtil;
import ltu.ecstra.context.exception.InvalidParameterConEx;
import ltu.ecstra.context.exception.ReasoningErrorConEx;
import ltu.ecstra.context.result.ReasoningResult;
import ltu.ecstra.context.situation.BasicSituationSpace;
import ltu.ecstra.context.state.AxiswiseContextState;
import ltu.ecstra.range.RangeException;
import org.json.JSONObject;

import java.util.Iterator;

public class SituationReasoner {
    public static CREResponse infer(SituationInferenceRequest request) {

        CREResponse.Builder response = CREResponse.newBuilder();

        try {
            for (SituationDescription situationDescription : request.getSituationDescriptionsList()) {
                String situationName = CstoUtil.convertToAlnum(situationDescription.getSituationName().replaceAll("\\s+", ""));
//		List<AttributeDescription> attributeDescriptions = situationDescription.getAttributes();
                BasicSituationSpace situationSpace = new BasicSituationSpace(situationName);
                Iterator<WeightedAttributeDescription> itrAttributeDescriptions = situationDescription.getAttributesList().iterator();
//		System.out.println(attributes.get(0));
                // add attributes to situation space
                while (itrAttributeDescriptions.hasNext()) {
                    WeightedAttributeDescription weightedAttributeDescription = itrAttributeDescriptions.next();
                    String attrName = CstoUtil.convertToAlnum(weightedAttributeDescription.getAttribute().getAttributeName().replaceAll("\\s+", ""));

                    situationSpace.addRangeBasedAxis(attrName, weightedAttributeDescription.getWeight(), BasicSituationSpace.B_NO_CONTRIBUTION);

                    Iterator<RegionDescription> itrRegionDescriptions = weightedAttributeDescription.getAttribute().getRegionsList().iterator();
                    while (itrRegionDescriptions.hasNext()) {
                        RegionDescription region = itrRegionDescriptions.next();
                        try {
                            situationSpace.addNumericRange(attrName, region.getRegionValue(), region.getRegionBelief());
                        } catch (RangeException e) {
                            System.out.println(attrName + " not added!");
                        }
                    }
//			System.out.print(attrName+":"+attribute.getValue());
                }

                //update context state
                AxiswiseContextState contextState = new AxiswiseContextState();
                Iterator<AttributeValue> itrAttributeValues = request.getAttributeValuesList().iterator();
                while (itrAttributeValues.hasNext()) {
                    AttributeValue attributeValue = itrAttributeValues.next();
                    String attrName = CstoUtil.convertToAlnum(attributeValue.getAttributeName().replaceAll("\\s+", ""));
                    contextState.addAxisState(attrName, attributeValue.getValue());
                }

                ReasoningResult reasoningResult = situationSpace.reason(contextState);
                response.addBody(ReasoningResponse.newBuilder().setConfidence(Double.valueOf(reasoningResult.getResult())).setSituationTitle(situationName).build());
            }
            // prepare vars for ECSTRA

            return response.setStatus("200").build();
        } catch (Exception e) {
            e.printStackTrace();
            return response.setStatus("500").build();
        }

    }
}
