package com.example.shoplylist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
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
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        purchases = withContext(Dispatchers.IO) {
            db.purchaseDao().getAll()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
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
            Text("Add Item to shop list")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(purchases) { purchase ->
                PurchaseItem(purchase)
            }
        }
    }

}

@Composable
fun PurchaseItem(purchase: Purchase) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(purchase.name, style = MaterialTheme.typography.titleSmall)
        }
    }
}