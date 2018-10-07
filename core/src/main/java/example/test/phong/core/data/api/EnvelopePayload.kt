package example.test.phong.core.data.api

/**
 * an annotation for identifying the payload that we want to extract from an API response wrapped in an envelop object
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnvelopePayload(val value: String = "")
