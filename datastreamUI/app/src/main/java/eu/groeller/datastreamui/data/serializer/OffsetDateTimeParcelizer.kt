package eu.groeller.datastreamui.data.serializer

import android.os.Parcel
import kotlinx.parcelize.Parceler
import java.time.OffsetDateTime

object OffsetDateTimeParcelizer : Parceler<OffsetDateTime> {
    override fun create(parcel: Parcel): OffsetDateTime {
        return OffsetDateTime.parse(parcel.readString())
    }

    override fun OffsetDateTime.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this.toString())
    }
}
