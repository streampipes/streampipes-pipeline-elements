package org.streampipes.processors.geo.jvm.window.collect.multipoint;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;
import org.streampipes.sdk.utils.Assets;

public class CollectMultiPointController extends StandaloneEventProcessingDeclarer<CollectMultiPointParameters> {

	protected final static String WKT_TEXT = "wkt_text";
	protected final static String EPSG_CODE = "epsg_code";
	protected final static String UID = "uid";
	protected final static String COLLECT = "collect_multipoint_wkt";
	public final static String EPSG_CODE_COLLECTED_MULTIPOINT = "epsg_code_collected_multipoint";


	protected final static String EPA_NAME = "Collect as MultiPoint";


	@Override
	public DataProcessorDescription declareModel() {
		return ProcessingElementBuilder.create(
				"org.streampipes.processors.geo.jvm.window.collect.multipoint",
				EPA_NAME,
				"Description")

				.category(DataProcessorType.GEO)
				.withAssets(Assets.DOCUMENTATION, Assets.ICON)
				.requiredStream(StreamRequirementsBuilder
						.create()
						.requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
								Labels.from(
										WKT_TEXT,
										"Field with WKT String",
										"Contains wkt"),
								PropertyScope.NONE)
						.requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
								Labels.from(
										EPSG_CODE,
										"EPSG Code",
										"EPSG Code for SRID"),
								PropertyScope.NONE)

						.requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
								Labels.from(
										UID,
										"Unique ID field",
										"UID from geometry"),
								PropertyScope.NONE)
						.build()

				)
				.supportedFormats(SupportedFormats.jsonFormat())
				.supportedProtocols(SupportedProtocols.kafka())
				.outputStrategy(OutputStrategies.append(
						EpProperties.stringEp(
								Labels.from(COLLECT,
										"mutlipoint_wkt",
										"multipoint"),
								COLLECT, SO.Text),
						EpProperties.numberEp(
								Labels.from(
										"EPSG Code Collected MultiPoint",
										"EPSG Code from Collected MultiPoint",
										"EPSG Code for SRID from Collected MultiPoint"),
								EPSG_CODE_COLLECTED_MULTIPOINT, SO.Number)
						)
				)
				.build();
	}

	@Override
	public ConfiguredEventProcessor<CollectMultiPointParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor){


		String wkt = extractor.mappingPropertyValue(WKT_TEXT);
		String epsg = extractor.mappingPropertyValue(EPSG_CODE);
		String uid = extractor.mappingPropertyValue(UID);

		CollectMultiPointParameters params = new CollectMultiPointParameters(graph, wkt, epsg, uid);

		return new ConfiguredEventProcessor<>(params, CollectMultiPoint::new);
	}

}
