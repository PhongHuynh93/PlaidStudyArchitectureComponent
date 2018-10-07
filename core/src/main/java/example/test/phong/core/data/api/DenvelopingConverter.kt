package example.test.phong.core.data.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 * a [retrofit2.Converter.Factory] which removes unwanted wrapping envelops from API responses
 */
@Singleton
class DenvelopingConverter @Inject constructor(val gson: Gson) : Converter.Factory() {
    override fun responseBodyConverter(
            type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {

        val payloadName = getPayloadName(annotations) ?: return null

        val adapter = gson.getAdapter(TypeToken.get(type!!))
        return Converter<ResponseBody, Any> { it ->
            it.use outerUse@{ body ->
                gson.newJsonReader(body.charStream()).use innerUse@ { jsonReader ->
                    jsonReader.beginObject()
                    while (jsonReader.hasNext()) {
                        if (payloadName == jsonReader.nextName()) {
                            return@outerUse adapter.read(jsonReader)
                        } else {
                            jsonReader.skipValue()
                        }
                    }
                    return@outerUse null
                }
            }
        }
    }


    private fun getPayloadName(annotations: Array<Annotation>?): String? {
        annotations?.let {
            for (annotation in it) {
                if (annotation is EnvelopePayload) {
                    return annotation.value
                }
            }
        }
        return null
    }
}