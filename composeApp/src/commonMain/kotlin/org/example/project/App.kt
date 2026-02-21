package org.example.project

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.project.api.*

sealed class Pantalla {
    object ListaProvincias : Pantalla()
    data class ListaCiudades(val provincia: Provincias) : Pantalla()
    data class VistaClima(val ciudad: Municipio, val nombreProvincia: String) : Pantalla()
}

@Composable
fun App() {
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.ListaProvincias) }
    var provincias by remember { mutableStateOf<List<Provincias>>(emptyList()) }
    var municipios by remember { mutableStateOf<List<Municipio>>(emptyList()) }
    var datosClima by remember { mutableStateOf<WeatherDetail?>(null) }
    var busquedasRecientes by remember { mutableStateOf<List<Pair<Municipio, String>>>(emptyList()) }
    
    var loading by remember { mutableStateOf(false) }
    var textoBusqueda by remember { mutableStateOf("") }
    var esCelsius by remember { mutableStateOf(true) }
    
    // Disparadores para carga de datos
    var provinciaParaCargar by remember { mutableStateOf<Provincias?>(null) }
    var ciudadParaCargar by remember { mutableStateOf<Pair<Municipio, String>?>(null) }

    // Cargar provincias al inicio
    LaunchedEffect(Unit) {
        loading = true
        try {
            provincias = ApiExample.fetchProvincias().Provincias
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            loading = false
        }
    }
    
    // Cargar municipios cuando cambia provinciaParaCargar
    LaunchedEffect(provinciaParaCargar) {
        val provincia = provinciaParaCargar ?: return@LaunchedEffect
        loading = true
        try {
            municipios = ApiExample.fetchMunicipios(provincia.CODPROV).municipios
            pantallaActual = Pantalla.ListaCiudades(provincia)
            textoBusqueda = ""
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            loading = false
            provinciaParaCargar = null // Resetear disparador
        }
    }
    
    // Cargar clima cuando cambia ciudadParaCargar
    LaunchedEffect(ciudadParaCargar) {
        val (ciudad, provinciaNombre) = ciudadParaCargar ?: return@LaunchedEffect
        loading = true
        try {
            datosClima = ApiExample.fetchWeather(ciudad.CODPROV, ciudad.CODIGOINE)
            if (!busquedasRecientes.any { it.first.CODIGOINE == ciudad.CODIGOINE }) {
                busquedasRecientes = (listOf(ciudad to provinciaNombre) + busquedasRecientes).take(5)
            }
            pantallaActual = Pantalla.VistaClima(ciudad, provinciaNombre)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            loading = false
            ciudadParaCargar = null // Resetear disparador
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF64B5F6),
            secondary = Color(0xFF81C784),
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E1E)
        )
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                // Cabecera
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pantallaActual !is Pantalla.ListaProvincias) {
                        IconButton(onClick = {
                            pantallaActual = when (pantallaActual) {
                                is Pantalla.ListaCiudades -> Pantalla.ListaProvincias
                                is Pantalla.VistaClima -> Pantalla.ListaProvincias
                                else -> Pantalla.ListaProvincias
                            }
                            textoBusqueda = ""
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                    Text(
                        text = "MeteoPol",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        textAlign = if (pantallaActual is Pantalla.ListaProvincias) TextAlign.Center else TextAlign.Start
                    )
                }

                // Contenido Principal
                Box(modifier = Modifier.weight(1f)) {
                    when (val pantalla = pantallaActual) {
                        is Pantalla.ListaProvincias -> {
                            PantallaListaProvincias(
                                provincias = provincias,
                                loading = loading,
                                textoBusqueda = textoBusqueda,
                                alCambiarBusqueda = { textoBusqueda = it },
                                busquedasRecientes = busquedasRecientes,
                                alSeleccionarProvincia = { provincia ->
                                    provinciaParaCargar = provincia
                                },
                                alSeleccionarReciente = { ciudad, provinciaNombre ->
                                    ciudadParaCargar = ciudad to provinciaNombre
                                }
                            )
                        }
                        is Pantalla.ListaCiudades -> {
                            PantallaListaCiudades(
                                nombreProvincia = pantalla.provincia.NOMBRE_PROVINCIA,
                                ciudades = municipios,
                                loading = loading,
                                textoBusqueda = textoBusqueda,
                                alCambiarBusqueda = { textoBusqueda = it },
                                alSeleccionarCiudad = { ciudad ->
                                    ciudadParaCargar = ciudad to pantalla.provincia.NOMBRE_PROVINCIA
                                }
                            )
                        }
                        is Pantalla.VistaClima -> {
                            PantallaDetalleClima(
                                datos = datosClima,
                                loading = loading,
                                esCelsius = esCelsius,
                                alCambiarUnidad = { esCelsius = !esCelsius }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaListaProvincias(
    provincias: List<Provincias>,
    loading: Boolean,
    textoBusqueda: String,
    alCambiarBusqueda: (String) -> Unit,
    busquedasRecientes: List<Pair<Municipio, String>>,
    alSeleccionarProvincia: (Provincias) -> Unit,
    alSeleccionarReciente: (Municipio, String) -> Unit
) {
    Column {
        if (busquedasRecientes.isNotEmpty()) {
            Text("Búsquedas Recientes", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                busquedasRecientes.forEach { (ciudad, prov) ->
                    SuggestionChip(
                        onClick = { alSeleccionarReciente(ciudad, prov) },
                        label = { Text(ciudad.NOMBRE) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = alCambiarBusqueda,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar provincia...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val filtradas = provincias.filter { it.NOMBRE_PROVINCIA.contains(textoBusqueda, ignoreCase = true) }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtradas) { provincia ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { alSeleccionarProvincia(provincia) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            provincia.NOMBRE_PROVINCIA,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaListaCiudades(
    nombreProvincia: String,
    ciudades: List<Municipio>,
    loading: Boolean,
    textoBusqueda: String,
    alCambiarBusqueda: (String) -> Unit,
    alSeleccionarCiudad: (Municipio) -> Unit
) {
    Column {
        Text("Ciudades de $nombreProvincia", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = alCambiarBusqueda,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar ciudad...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val filtradas = ciudades.filter { it.NOMBRE.contains(textoBusqueda, ignoreCase = true) }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtradas) { ciudad ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { alSeleccionarCiudad(ciudad) },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            ciudad.NOMBRE,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaDetalleClima(
    datos: WeatherDetail?,
    loading: Boolean,
    esCelsius: Boolean,
    alCambiarUnidad: () -> Unit
) {
    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (datos != null) {
        val valorTemp = datos.temperatura_actual.toDoubleOrNull() ?: 0.0
        val tempAMostrar = if (esCelsius) {
            "${datos.temperatura_actual}°C"
        } else {
            val f = (valorTemp * 9/5) + 32
            "${f.toInt()}°F"
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), Color.Transparent)))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = datos.municipio.NOMBRE,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = datos.estado_cielo.description,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = tempAMostrar,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                TextButton(onClick = alCambiarUnidad) {
                    Text(if (esCelsius) "Cambiar a Fahrenheit" else "Cambiar a Celsius")
                }
                
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 24.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                
                // Información integrada
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DetalleFila(Icons.Default.WaterDrop, "Humedad", "${datos.humedad}%")
                    DetalleFila(Icons.Default.WbSunny, "Salida del sol", datos.amanecer)
                    DetalleFila(Icons.Default.WbTwilight, "Puesta del sol", datos.ocaso)
                }
            }
        }
    }
}

@Composable
fun DetalleFila(icono: androidx.compose.ui.graphics.vector.ImageVector, etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
