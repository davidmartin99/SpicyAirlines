package com.spicyairlines.app.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme
import com.spicyairlines.app.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasePantalla(
    onBack: (() -> Unit)? = null,
    onPerfilClick: (() -> Unit)? = null,
    snackbarHostState: SnackbarHostState? = null, // ✅ Añadido aquí
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.logo_avion_2),
                            contentDescription = "Icono SpicyAirlines",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.logo_letras_2),
                            contentDescription = "Texto SpicyAirlines",
                            modifier = Modifier
                                .height(28.dp)
                        )
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Atrás",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                actions = {
                    if (onPerfilClick != null) {
                        IconButton(onClick = onPerfilClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.user_circle),
                                contentDescription = "Perfil",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = {
            snackbarHostState?.let { SnackbarHost(it) }
                ?: SnackbarHost(remember { SnackbarHostState() })
        } // ✅ Ahora Scaffold puede mostrar Snackbars si se los pasas
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            content(padding)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasePantallaPreview() {
    AppTheme {
        BasePantalla(
            onBack = { /* acción volver */ },
            onPerfilClick = { /* acción perfil */ }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Este es el contenido de ejemplo.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {}) {
                    Text("Botón de prueba")
                }
            }
        }
    }
}
