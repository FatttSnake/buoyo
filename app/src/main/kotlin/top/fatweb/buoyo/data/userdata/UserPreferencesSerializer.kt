package top.fatweb.buoyo.data.userdata

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import top.fatweb.buoyo.data.UserPreferences
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class UserPreferencesSerializer @Inject constructor() : Serializer<UserPreferences> {
    override val defaultValue: UserPreferences = UserPreferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserPreferences =
        try {
            UserPreferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException(
                message = "Cannot read proto.",
                cause = exception
            )
        }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        t.writeTo(output)
    }
}
