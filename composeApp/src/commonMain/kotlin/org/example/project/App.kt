package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.api.ApiExample
import org.example.project.api.UserResponse
import androidx.compose.foundation.layout.*
import org.example.project.api.Post
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search

@Composable
fun App() {
    var posts by remember { mutableStateOf<List<Post>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    var rechargetrigger by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var lastViewedPost by remember { mutableStateOf<Post?>(null) }

    LaunchedEffect(rechargetrigger) {
        loading = true
        try {
            posts = ApiExample.fetchPosts()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            loading = false
        }
    }
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                if (selectedPost != null) {
                    val post = selectedPost!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = "ID: ${post.id}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text("userID: ${post.userId}")
                                Text(
                                    text = "Titulo: ${post.title}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text("${post.body}")
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { selectedPost = null },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Volver")
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().statusBarsPadding()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Lista de Usuarios",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Recargar",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { rechargetrigger = !rechargetrigger }
                                    .padding(8.dp)
                            )
                        }

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Cerca per nom...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true
                        )
                        val filteredPosts = if (searchQuery.isBlank()) {
                            posts
                        } else {
                            posts.filter {
                                it.title.contains(searchQuery, ignoreCase = true)
                            }
                        }

                        if (lastViewedPost != null) {
                            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Text(
                                    text = "Último visto:",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedPost = lastViewedPost
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = lastViewedPost!!.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "(ID: ${lastViewedPost!!.id})",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(filteredPosts) { post ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedPost = post
                                            lastViewedPost = post
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = post.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "ID: ${post.userId}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}