package hu.bme.ait.shoppinglist.ui.screen

import android.media.MediaPlayer
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import hu.bme.ait.shoppinglist.R
import hu.bme.ait.shoppinglist.data.CartItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortScreen(
    onBackClick: () -> Unit,
    shoppingViewModel: ShoppingViewModel = hiltViewModel()
) {

    var itemToEdit: CartItem? by rememberSaveable { mutableStateOf(null) }
    var showCautionDialog by rememberSaveable { mutableStateOf(false) }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val add = MediaPlayer.create(context, R.raw.add)

    var shoppingList = shoppingViewModel.sortPrice().collectAsState(emptyList())


    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        context.getString(R.string.sorted_by_price),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                actions = {
                    IconButton(onClick = {
                        onBackClick()
                    })
                    { Icon(Icons.Filled.Home, contentDescription = null) }
                    IconButton(onClick = {
                        showCautionDialog = true
                    })
                    { Icon(Icons.Filled.Delete, contentDescription = null) }
                }
            )
        },
        bottomBar =
            {
                FloatingActionButton(
                    modifier = Modifier.padding(360.dp, 0.dp, 3.dp, 3.dp),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    onClick = { add.start(); showAddDialog = true })
                { Icon(Icons.Filled.AddCircle, contentDescription = null) }
            }) {innerPadding ->

        if (showCautionDialog) {
            SortCautionDialog(shoppingViewModel, onCancel = { showCautionDialog = false })
        }

        if (showAddDialog) {
            SortAddDialog(
                shoppingViewModel,
                itemToEdit,
                onCancel = { showAddDialog = false; itemToEdit = null })
        }


        if (shoppingList.value.isNullOrEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(context.getString(R.string.nothing_here_for_now))
            }
        } else {
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)) {
                items(shoppingList.value) {
                    SortItemCard(
                        it,
                        onItemChecked = { item: CartItem, bool: Boolean ->
                            shoppingViewModel.changeItemState(
                                it,
                                !it.isBought,
                            )
                        },
                        onItemDelete = { it -> shoppingViewModel.removeItem(it) },
                        onItemEdit = { selectedItem ->
                            itemToEdit = selectedItem
                            showAddDialog = true
                        }
                    )

                }
            }
        }
    }
}





@Composable
fun SortItemCard(
    cartItem: CartItem,
    onItemChecked: (CartItem, Boolean) -> Unit,
    onItemDelete: (CartItem) -> Unit,
    onItemEdit: (CartItem) -> Unit
) {

    var context = LocalContext.current
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        modifier = Modifier.padding(5.dp)
    ) {
        var expanded by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .padding(15.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )

        ) {

            Row(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ){
                if (cartItem.category == context.getString(R.string.food) || cartItem.category == context.getString(R.string.empty)) {
                    Image(painterResource(R.drawable.food), modifier = Modifier, contentDescription = null, contentScale = ContentScale.Crop)
                } else if (cartItem.category == context.getString(R.string.clothes)) {
                    Image(painterResource(R.drawable.clothes),  modifier = Modifier, contentDescription = null, contentScale = ContentScale.Crop)
                } else if (cartItem.category == context.getString(R.string.stationaries)) {
                    Image(painterResource(R.drawable.stationaries),  modifier = Modifier, contentDescription = null, contentScale = ContentScale.Crop)
                } else if (cartItem.category == context.getString(R.string.toiletries)) {
                    Image(painterResource(R.drawable.toiletries),  modifier = Modifier, contentDescription = null, contentScale = ContentScale.Crop)
                } else if (cartItem.category == context.getString(R.string.misc)) {
                    Image(painterResource(R.drawable.misc),  modifier = Modifier, contentDescription = null, contentScale = ContentScale.Crop)}
                Spacer(modifier = Modifier.weight(0.1f))
                Text(
                    cartItem.title,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(0.6f),
                    style = if (cartItem.isBought) {
                        TextStyle(
                            textDecoration = TextDecoration.LineThrough
                        )
                    } else {
                        TextStyle(
                            textDecoration = TextDecoration.None
                        )
                    }
                )
                Spacer(modifier = Modifier.weight(0.2f))
                Checkbox(
                    checked = cartItem.isBought,
                    onCheckedChange = { checkboxState ->
                        onItemChecked(cartItem, checkboxState)
                    }
                )
                Spacer(modifier = Modifier.weight(0.05f))
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = context.getString(R.string.delete),
                    modifier = Modifier.clickable {
                        onItemDelete(cartItem)
                    },
                    tint = Color.DarkGray
                )
                Spacer(modifier = Modifier.weight(0.05f))
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = context.getString(R.string.edit),
                    modifier = Modifier.clickable {
                        onItemEdit(cartItem)
                    },
                    tint = Color.DarkGray
                )
            }
            Text("Price: $${cartItem.price}", modifier = Modifier.wrapContentSize())
            Text("Notes: ${cartItem.description}", modifier = Modifier.wrapContentSize())
        }
    }
}













@Composable
fun SortAddDialog(
    viewModel: ShoppingViewModel,
    itemToEdit: CartItem? = null,
    onCancel: () -> Unit
) {

    var context = LocalContext.current
    var title by remember {
        mutableStateOf(
            itemToEdit?.title ?: context.getString(R.string.empty)
        )
    }
    var desc by remember {
        mutableStateOf(
            itemToEdit?.description ?: context.getString(R.string.empty)
        )
    }
    var price by remember {
        mutableFloatStateOf(itemToEdit?.price ?: 0f)
    }

    var category by remember {
        mutableStateOf(itemToEdit?.category ?: context.getString(R.string.empty))
    }


    var userInput by remember {
        mutableStateOf(context.getString(R.string.empty))
    }
    var inputPriceErrorState by remember {
        mutableStateOf(false)
    }

    var inputErrorState by remember {
        mutableStateOf(false)
    }

    var errorText = context.getString(R.string.title_cannot_be_empty)


    Dialog(onDismissRequest = {
        onCancel()
    }
    )
    {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(size = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(15.dp)
            ) {
                Text(
                    if (itemToEdit == null) context.getString(R.string.new_item) else context.getString(R.string.edit_item),
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(context.getString(R.string.item_title)) },
                    value = "$title",
                    onValueChange = {
                        title = it; if (it != context.getString(R.string.empty)) {
                        inputErrorState = false
                    }
                    })
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(context.getString(R.string.item_description))},
                    value = "$desc",
                    onValueChange = { desc = it })
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(context.getString(R.string.item_price)) },
                    value = "$price",
                    onValueChange = {
                        userInput = it
                        try {
                            userInput.toFloat(); inputPriceErrorState = false; price = it.toFloat()
                        } catch (e: Exception) {
                            inputPriceErrorState = true
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    trailingIcon = {
                        if (inputPriceErrorState)
                            Icon(
                                Icons.Filled.Warning,
                                context.getString(R.string.error),
                                tint = MaterialTheme.colorScheme.error
                            )
                    },
                    isError = inputPriceErrorState
                )
                SortSpinnerSample(
                    listOf(context.getString(R.string.food),
                        context.getString(R.string.clothes),
                        context.getString(R.string.stationaries),
                        context.getString(R.string.toiletries), context.getString(R.string.misc)
                    ),
                    preselected = context.getString(R.string.food),
                    onSelectionChanged = { category = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (inputErrorState) {
                        Text(errorText, color = Color.Red)
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = Color.Red)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (inputPriceErrorState) {
                        Text(context.getString(R.string.price_field_only_accepts_numbers), color = Color.Red)
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = Color.Red)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        if (title == context.getString(R.string.empty)) {
                            inputErrorState = true
                        } else inputErrorState = false
                        if (!inputErrorState && !inputPriceErrorState) {
                            if (itemToEdit == null) {
                                viewModel.addItem(
                                    cartItem = CartItem(
                                        title = title,
                                        description = desc,
                                        category = category,
                                        price = price,
                                        isBought = false
                                    )
                                )
                            } else {
                                val edited = itemToEdit.copy(
                                    title = title,
                                    description = desc,
                                    category = category,
                                    price = price,
                                    isBought = false

                                )
                                viewModel.updateItem(
                                    edited
                                )
                            }


                            onCancel()
                        }
                    }) {
                        Text(context.getString(R.string.save))
                    }
                }
            }
        }
    }
}


@Composable
fun SortCautionDialog(
    viewModel: ShoppingViewModel,
    onCancel: () -> Unit,
) {
    var context = LocalContext.current
    val shuffle = MediaPlayer.create(context, R.raw.shuffle)

    Dialog(
        onDismissRequest = {
            onCancel()
        }) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(),
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            shadowElevation = 4.dp,
            border = BorderStroke(3.dp, Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(context.getString(R.string.delete_all_items))
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Button(
                        shape = RoundedCornerShape(2.dp),
                        onClick = { shuffle.start(); viewModel.removeAllItems(); onCancel() }) {
                        Text(context.getString(R.string.confirm))
                    }
                    Button(shape = RoundedCornerShape(2.dp), onClick = { onCancel() }) {
                        Text(context.getString(R.string.cancel))
                    }


                }

            }
        }


    }
}


@Composable
fun SortSpinnerSample(
    list: List<String>,
    preselected: String,
    onSelectionChanged: (myData: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(preselected) }
    var expanded by remember { mutableStateOf(false) } // initial value
    OutlinedCard(
        modifier = modifier.clickable {
            expanded = !expanded
        }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = selected,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Icon(
                Icons.Outlined.ArrowDropDown, null, modifier =
                    Modifier.padding(8.dp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                list.forEach { listEntry ->
                    DropdownMenuItem(
                        onClick = {
                            selected = listEntry
                            expanded = false
                            onSelectionChanged(selected)
                        },
                        text = {
                            Text(
                                text = listEntry,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Start)
                            )
                        },
                    )
                }
            }
        }
    }
}




