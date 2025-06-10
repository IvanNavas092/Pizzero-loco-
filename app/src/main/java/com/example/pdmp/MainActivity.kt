package com.example.pdmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PizzeroLocoApp()
        }
    }
    @Composable
    fun PizzeroLocoApp() {
        var screen by remember { mutableStateOf("start") }
        var coins by remember { mutableStateOf(0) } // Gestión de monedas
        var areClientsPatient by remember { mutableStateOf(false) } // Mejora "Clientes más pacientes"
        var hasTimeReducer by remember { mutableStateOf(false) } // Mejora "Reducir tiempo"
        var hasDoubleCoins by remember { mutableStateOf(false) } // Mejora "Doble monedas"
        val selectedIngredients = remember { mutableStateListOf<String>() }

        val allIngredients = listOf("Queso", "Pepperoni", "Champiñones", "Pimientos", "Bacon", "Tomate")
        val requiredIngredients = allIngredients.shuffled().take((1..8).random())

        when (screen) {
            "start" -> StartScreen(
                onStart = { screen = "choose" },
                onShop = { screen = "shop" },
                onInstructions = { screen = "instructions" },
                onExit = { finish() }
            )
            "instructions" -> InstructionsScreen(onBack = { screen = "start" })
            "choose" -> ChooseIngredientsScreen(
                allIngredients = allIngredients,
                requiredIngredients = requiredIngredients,
                selectedIngredients = selectedIngredients,
                onIngredientSelect = { ingredient ->
                    if (!selectedIngredients.contains(ingredient)) {
                        selectedIngredients.add(ingredient)
                    }
                },
                onBake = {
                    if (selectedIngredients.containsAll(requiredIngredients)) {
                        coins += if (hasDoubleCoins) 60 else 30 // Doble monedas si está activa
                        screen = "bake"
                    }
                },
                onClear = { selectedIngredients.clear() },
                onExitGame = { screen = "start" }
            )
            "bake" -> BakeScreen(
                onContinue = {
                    screen = "choose"
                    selectedIngredients.clear()
                },
                onBackToMenu = {
                    screen = "start"
                    selectedIngredients.clear()
                },
                ingredientsCount = selectedIngredients.size,
                areClientsPatient = areClientsPatient,
                hasTimeReducer = hasTimeReducer,
                hasDoubleCoins = hasDoubleCoins // Pasamos el estado a la pantalla de hornear
            )
            "shop" -> ShopScreen(
                coins = coins,
                onItemBuy = { cost ->
                    coins -= cost
                    // Activamos las mejoras cuando se compran
                    when (cost) {
                        30 -> areClientsPatient = true // Mejora "Clientes más pacientes"
                        50 -> hasTimeReducer = true // Mejora "Reducir tiempo"
                        70 -> hasDoubleCoins = true // Mejora "Doble monedas"
                    }
                },
                onBack = { screen = "start" }
            )
        }
    }
    @Composable //Unit --> Void (No devuelve nada)
    fun StartScreen(
        onStart: () -> Unit,
        onShop: () -> Unit,
        onInstructions: () -> Unit,
        onExit: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Text(
                    "Pizzero Loco",
                    fontSize = 56.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .shadow(16.dp, CircleShape)
                        .padding(1.dp),
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(40.dp))
                MenuButton(text = "Jugar", onClick = onStart)
                Spacer(modifier = Modifier.height(16.dp))
                MenuButton(text = "Tienda", onClick = onShop)
                Spacer(modifier = Modifier.height(16.dp))
                MenuButton(text = "Instrucciones", onClick = onInstructions)
                Spacer(modifier = Modifier.height(16.dp))
                MenuButton(text = "Salir", onClick = onExit)
            }
        }
    }

    @Composable
    fun MenuButton(text: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .shadow(12.dp, CircleShape)
                .background(Color.Transparent) // Fondo transparente
                .padding(14.dp),
            shape = RoundedCornerShape(10.dp), //border_radius
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEE9A00))
        ) {
            Text(
                text,
                fontSize = 21.sp,
                color = Color.White,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }

    @Composable
    fun InstructionsScreen(onBack: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Imagen de fondo
            Image(
                painter = painterResource(id = R.drawable.background), // Cambia "background_image" al nombre de tu recurso
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Contenido principal
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Instrucciones",
                        fontSize = 32.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,

                        )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Selecciona los ingredientes correctos y hornea la pizza para ganar monedas. ¡Diviértete!",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EE), // Color del fondo
                            contentColor = Color.White // Color del texto
                        ),
                        shape = RoundedCornerShape(16.dp),

                        modifier = Modifier
                            .fillMaxWidth(0.5f) // Ajustar el tamaño del botón
                            .shadow(8.dp, CircleShape)
                            .background(Color.Transparent) // Fondo transparente
                            .padding(8.dp),
                    ) {
                        Text(
                            "Volver",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
            }
        }
    }


    @Composable
    fun ChooseIngredientsScreen(
        allIngredients: List<String>,
        requiredIngredients: List<String>,
        selectedIngredients: MutableList<String>, //se puede modificar la lista al ser mutable
        onIngredientSelect: (String) -> Unit,
        onBake: () -> Unit,
        onClear: () -> Unit,
        onExitGame: () -> Unit
    ) {
        val isButtonEnabled = selectedIngredients.containsAll(requiredIngredients)
        val isClearButtonEnabled = selectedIngredients.isNotEmpty() // Habilita el botón de limpiar si al menos un ingrediente está seleccionado
        var lastSelectedIngredient by remember { mutableStateOf<String?>(null) } //guarda el estado y puede ser null
        var showWarningDialog by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Botón de volver en la esquina superior izquierda
            IconButton(
                onClick = { showWarningDialog = true },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.left),
                    contentDescription = "Volver",
                    tint = Color.Unspecified
                )
            }

            if (showWarningDialog) {
                AlertDialog(
                    onDismissRequest = { showWarningDialog = false },
                    title = { Text("Advertencia") },
                    text = { Text("Si sales del juego, los datos de la partida se perderán. ¿Deseas continuar?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showWarningDialog = false
                                onExitGame()
                            },
                            modifier = Modifier
                                .shadow(8.dp, CircleShape)
                        ) {
                            Text("Salir del juego", style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showWarningDialog = false },
                            modifier = Modifier
                                .shadow(8.dp, CircleShape)
                        ) {
                            Text("Seguir jugando", style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ))
                        }
                    }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Ingredientes requeridos:",
                        fontSize = 16.sp,
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.poppins_regular)),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    requiredIngredients.forEach { ingredient ->
                        Text(
                            ingredient,
                            fontSize = 20.sp,
                            color = Color.White,
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.poppins_regular)),
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Último ingrediente seleccionado:",
                        fontSize = 18.sp,
                        color = Color.White,
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.poppins_regular)),
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = lastSelectedIngredient ?: "Ninguno",
                        fontSize = 20.sp,
                        color = Color.Cyan,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.poppins_regular))
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.pala_1),
                        contentDescription = "imagen bandeja",
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(500.dp)
                            .shadow(40.dp, CircleShape)
                    )
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(allIngredients) { ingredient -> //iterar sobre 'allIngredients'
                        IngredientButton(
                            ingredient = ingredient,
                            onClick = {
                                onIngredientSelect(ingredient)
                                lastSelectedIngredient = ingredient
                            },
                            modifier = Modifier
                                .shadow(1.dp, CircleShape)
                                .padding(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp)) // Añadimos un espaciado antes de los botones

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón de horno con imagen dinámica
                    Box(
                        modifier = Modifier
                            .size(70.dp) // Reducido el tamaño del botón de horno
                            .clickable(enabled = isButtonEnabled, onClick = onBake)
                    ) {
                        val ovenImage = if (isButtonEnabled) R.drawable.furnace else R.drawable.furnace_disabled
                        Image(
                            painter = painterResource(id = ovenImage),
                            contentDescription = if (isButtonEnabled) "Hornear pizza" else "Horno deshabilitado",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .size(70.dp) // Tamaño ajustado de la imagen de limpiar
                            .clip(CircleShape)
                            .clickable(enabled = isClearButtonEnabled) { // Habilitar solo si al menos un ingrediente está seleccionado
                                onClear()
                                lastSelectedIngredient = null
                            }
                    ) {
                        val cleanImage = if (isClearButtonEnabled) R.drawable.rodillo else R.drawable.rodillo_disabled // Cambiar imagen si está habilitado
                        Image(
                            painter = painterResource(id = cleanImage),
                            contentDescription = "Limpiar ingredientes",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    fun getIngredientImage(ingredient: String): Int {
        return when (ingredient) {
            "Queso" -> R.drawable.cheese
            "Pepperoni" -> R.drawable.pepperoni
            "Champiñones" -> R.drawable.mushroom
            "Pimientos" -> R.drawable.pimiento
            "Bacon" -> R.drawable.bacon
            "Tomate" -> R.drawable.tomate
            else -> R.drawable.cheese
        }
    }

    @Composable
    fun IngredientButton(ingredient: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .clickable(onClick = onClick)
        ) {
            Image(
                painter = painterResource(id = R.drawable.plato),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
            Image(
                painter = painterResource(id = getIngredientImage(ingredient)),
                contentDescription = ingredient,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
                    .shadow(16.dp, CircleShape)
            )
        }
    }

    @Composable
    fun BakeScreen(
        onContinue: () -> Unit,
        onBackToMenu: () -> Unit,
        ingredientsCount: Int,
        areClientsPatient: Boolean, // Recibimos el estado de los clientes pacientes
        hasTimeReducer: Boolean, // Recibimos el estado de la mejora de tiempo
        hasDoubleCoins: Boolean // Nueva mejora de monedas x2
    ) {
        val poppinsRegular = FontFamily(Font(R.font.poppins_regular)) // Fuente Poppins Regular

        var timer by remember { mutableStateOf(ingredientsCount * 10 - if (areClientsPatient) 10 else 0) }
        val isCooking = remember { mutableStateOf(true) }
        val baseCoins = 30 // Monedas base ganadas por cocción exitosa
        val coinsWon = if (hasDoubleCoins) baseCoins * 2 else baseCoins

        LaunchedEffect(timer, isCooking.value) {
            if (isCooking.value && timer > 0) {
                delay(1000L)
                timer -= if (hasTimeReducer) 2 else 1 // Reducir 2 segundos si la mejora está activada
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tiempo restante: $timer s",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontFamily = poppinsRegular,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { if (timer > 0) timer -= if (hasTimeReducer) 2 else 1 },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE), contentColor = Color.White),
                    modifier = Modifier
                        .shadow(8.dp, CircleShape)
                        .background(Color.Transparent)
                ) {
                    Text(
                        text = "Reducir tiempo",
                        color = Color.White,
                        fontFamily = poppinsRegular,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (timer <= 0) {
                    isCooking.value = false
                    Text(
                        text = "¡La pizza está lista! ",
                        fontSize = 24.sp,
                        color = Color.Green,
                        fontFamily = poppinsRegular,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "¡Has ganado $coinsWon PizzaCoins! ",
                        fontSize = 24.sp,
                        color = Color.Green,
                        fontFamily = poppinsRegular,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onContinue,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE), contentColor = Color.White),
                        modifier = Modifier
                            .shadow(8.dp, CircleShape)
                            .background(Color.Transparent)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Seguir",
                            color = Color.White,
                            fontFamily = poppinsRegular,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onBackToMenu,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE), contentColor = Color.White),
                        modifier = Modifier
                            .shadow(8.dp, CircleShape)
                            .background(Color.Transparent)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Volver al Menú",
                            color = Color.White,
                            fontFamily = poppinsRegular,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ShopScreen(coins: Int, onItemBuy: (Int) -> Unit, onBack: () -> Unit) {
        val items = listOf(
            Item("Acelerador de horno", 50, "Acelera el proceso de horneado, ¡un aumento de velocidad increíble!."),
            Item("Clientes más pacientes", 30, "El tiempo de horneado será menos, ¡tardarás menos en cocinar!"),
            Item("Monedas x2", 70, "Duplica las monedas ganadas por cada pizza cocinada.")
        )

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.left),
                    contentDescription = "Volver",
                    modifier = Modifier.size(40.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.coin),
                        contentDescription = "Monedas",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        text = "$coins",
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(items) { item ->
                        ShopItemCard(
                            item = item,
                            coins = coins,
                            onItemBuy = { onItemBuy(item.cost) },
                            modifier = Modifier
                                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Volver", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }


    data class Item(val name: String, val cost: Int, val description: String)

    @Composable
    fun ShopItemCard(item: Item, coins: Int, onItemBuy: () -> Unit, modifier: Modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .shadow(8.dp, RoundedCornerShape(15.dp)),  // Añadimos sombra
            shape = RoundedCornerShape(15.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFB87F5A))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Título del item
                Text(
                    item.name,
                    fontSize = 20.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                )
                // Descripción del item
                Text(
                    item.description,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Botón para comprar
                Button(
                    onClick = onItemBuy,
                    enabled = coins >= item.cost,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(10.dp, RoundedCornerShape(30.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor  = if (coins >= item.cost) Color(0xFF4CAF50) else Color(0xFFBDBDBD)
                    )
                ) {
                    Text(
                        text = "Comprar por ${item.cost} PizzaCoins",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    )
                }
            }
        }
    }

}












