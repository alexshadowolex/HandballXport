package alex.handball

import alex.handball.model.NameNormalizer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.OutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class MultiOutputStream(private vararg val streams: OutputStream) : OutputStream() {
    override fun close() = streams.forEach(OutputStream::close)
    override fun flush() = streams.forEach(OutputStream::flush)

    override fun write(b: Int) = streams.forEach {
        it.write(b)
    }

    override fun write(b: ByteArray) = streams.forEach {
        it.write(b)
    }

    override fun write(b: ByteArray, off: Int, len: Int) = streams.forEach {
        it.write(b, off, len)
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
class LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}

/**
 * Parses a String of pattern dd.MM.yyyy to java.time.LocalDate?
 * @return {LocalDate?} on success java.time.LocalDate, null on failure
 */
fun String.toJavaLocalDate(): LocalDate? {
    return try {
        LocalDate.parse(this, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    } catch (e: DateTimeParseException) {
        logger.warn("Caught exception while parsing date $this : ", e)
        null
    }
}


/**
 * Adds quotation marks to the string. If the string is empty, it changes nothing.
 */
fun String.addQuotationMarks() =
    if(this.isNotEmpty()) {
        "\"$this\""
    } else {
        this
    }


/**
 * Parses a java.time.LocalDate? to a string of pattern dd.MM.yyyy
 * @return {String} of pattern dd.MM.yyyy on success, "" on failure
 */
fun LocalDate?.customToString() = this?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: ""