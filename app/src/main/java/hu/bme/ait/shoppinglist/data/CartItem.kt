package hu.bme.ait.shoppinglist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "category") val category:String,
    @ColumnInfo(name = "name") val title:String,
    @ColumnInfo(name = "description") val description:String,
    @ColumnInfo(name = "price") val price:Float,
    @ColumnInfo(name = "status") var isBought: Boolean
)
