package hu.bme.ait.shoppinglist.ui.screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hu.bme.ait.shoppinglist.data.CartDAO
import hu.bme.ait.shoppinglist.data.CartItem
import hu.bme.ait.shoppinglist.data.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(val cartDAO: CartDAO) : ViewModel() {




    fun getAllItems(): Flow<List<CartItem>> {
        return cartDAO.getAllItems()
    }

    fun sortPrice(): Flow<List<CartItem>>{
        return cartDAO.sortPrice()
    }

    fun addItem(cartItem: CartItem) {
        viewModelScope.launch() {
            cartDAO.insert(cartItem)
        }
    }

    fun removeItem(cartItem: CartItem) {
        viewModelScope.launch {
            cartDAO.delete(cartItem)
        }
    }

    fun removeAllItems() {
        viewModelScope.launch {
            cartDAO.deleteAllItems()
        }
    }

    fun updateItem(newCartItem: CartItem) {
        viewModelScope.launch {
            cartDAO.update(newCartItem)
        }
    }

    fun changeItemState(cartItem: CartItem, value: Boolean) {
        val changedItem = cartItem.copy()
        changedItem.isBought = value
        viewModelScope.launch {
            cartDAO.update(changedItem)
        }
    }
}
