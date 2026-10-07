package com.example.gestionusuariosv2.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

private val MoradoNuevoUsuario = Color(0xFF3F20C8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoUsuarioScreen(

    onVolver: () -> Unit,

    onGuardar: (
        nombreCompleto: String,
        usuario: String,
        correo: String,
        contrasena: String
    ) -> Unit,

    errorMessage: String? = null

) {

    var nombreCompleto by remember {
        mutableStateOf("")
    }

    var usuario by remember {
        mutableStateOf("")
    }

    var correo by remember {
        mutableStateOf("")
    }

    var contrasena by remember {
        mutableStateOf("")
    }

    var mostrarContrasena by remember {
        mutableStateOf(false)
    }

    Scaffold(

        containerColor = Color.White,

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Nuevo usuario",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onVolver
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(

                        containerColor = MoradoNuevoUsuario,

                        titleContentColor = Color.White,

                        navigationIconContentColor =
                            Color.White
                    )
            )
        }

    ) { paddingValues ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)

        ) {

            OutlinedTextField(

                value = nombreCompleto,

                onValueChange = {
                    nombreCompleto = it
                },

                label = {
                    Text("Nombre completo")
                },

                leadingIcon = {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },

                singleLine = true,

                shape = RoundedCornerShape(8.dp),

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(

                value = usuario,

                onValueChange = {
                    usuario = it
                },

                label = {
                    Text("Usuario")
                },

                leadingIcon = {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },

                singleLine = true,

                shape = RoundedCornerShape(8.dp),

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(

                value = correo,

                onValueChange = {
                    correo = it
                },

                label = {
                    Text("Correo electrónico")
                },

                leadingIcon = {

                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null
                    )
                },

                singleLine = true,

                shape = RoundedCornerShape(8.dp),

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            OutlinedTextField(

                value = contrasena,

                onValueChange = {
                    contrasena = it
                },

                label = {
                    Text("Contraseña")
                },

                leadingIcon = {

                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null
                    )
                },

                trailingIcon = {

                    IconButton(
                        onClick = {
                            mostrarContrasena =
                                !mostrarContrasena
                        }
                    ) {

                        Icon(

                            imageVector =
                                if (mostrarContrasena) {

                                    Icons.Default.VisibilityOff

                                } else {

                                    Icons.Default.Visibility
                                },

                            contentDescription =
                                "Mostrar contraseña"
                        )
                    }
                },

                visualTransformation =
                    if (mostrarContrasena) {

                        VisualTransformation.None

                    } else {

                        PasswordVisualTransformation()
                    },

                singleLine = true,

                shape = RoundedCornerShape(8.dp),

                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = errorMessage,
                    color = Color.Red
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(

                onClick = {

                    onGuardar(
                        nombreCompleto,
                        usuario,
                        correo,
                        contrasena
                    )
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),

                shape = RoundedCornerShape(6.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            MoradoNuevoUsuario
                    )

            ) {

                Text(
                    text = "GUARDAR",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}