package com.example.shoplylist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.shoplylist.ui.theme.ShoplyListTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = AppDatabase.getDatabase(this)
        enableEdgeToEdge()
        setContent {
            ShoplyListTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BuyListScreen(db)
                }
            }
        }
    }
}

@Composable
fun BuyListScreen(db: AppDatabase) {

    var name by remember {mutableStateOf("")}
    var purchases by remember {mutableStateOf(listOf<Purchase>())}
    var editPurchase by remember { mutableStateOf<Purchase?>(null) }
    var editName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        purchases = withContext(Dispatchers.IO) {
            db.purchaseDao().getAll()
        }
    }

    Column(modifier = Modifier.padding(32.dp)) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        ) {
            items(purchases) { purchase ->
                PurchaseItem(
                    purchase,
                    onEdit = { edit ->
                        editPurchase = edit
                        editName = edit.name
                    },
                    onSelect = { selected ->
                        purchases = purchases.map {
                            if (it.id == selected.id) it.copy(isSelected = !it.isSelected)
                            else it
                        }

                    },
                    onDelete = { deleted ->
                        scope.launch(Dispatchers.IO) {
                            db.purchaseDao().delete(deleted)
                            val updatedPurchases = db.purchaseDao().getAll()
                            withContext(Dispatchers.Main) {
                                purchases = updatedPurchases
                            }
                        }
                    }
                )
            }
        }


        OutlinedTextField(
            value=name,
            onValueChange = {name = it},
            label = { Text("Purchase Item") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    scope.launch(Dispatchers.IO) {
                        db.purchaseDao().insert(Purchase(name=name))
                        val updatedPurchases = db.purchaseDao().getAll()
                        withContext(Dispatchers.Main) {
                            purchases = updatedPurchases
                            name = ""
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (editPurchase != null) {
            AlertDialog(
                onDismissRequest = {
                    editPurchase = null
                    editName = ""
                },
                title = {
                    Text("Edit Item")
                },
                text = {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = {editName = it},
                        label = { Text("Text")}
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val updatedPurchase = editPurchase!!.copy(name=editName)

                            purchases = purchases.map{
                                if (it.id == updatedPurchase.id) updatedPurchase else it
                            }

                            scope.launch(Dispatchers.IO) {
                                db.purchaseDao().update(updatedPurchase)
                            }

                            editPurchase = null
                            editName = ""
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        editPurchase = null
                        editName = ""
                    }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun PurchaseItem(purchase: Purchase, onEdit: (Purchase)-> Unit, onSelect: (Purchase) -> Unit, onDelete: (Purchase) -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text=purchase.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(2f).fillMaxHeight(),
                textDecoration = if (purchase.isSelected) TextDecoration.LineThrough else null
            )

            IconButton(
                onClick = { onDelete(purchase) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }

            IconButton(
                onClick = {
                    onEdit(purchase)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit"
                )
            }

            Checkbox(
                checked = purchase.isSelected,
                onCheckedChange = {
                    onSelect(purchase)
                }
            )
        }
    }
}

