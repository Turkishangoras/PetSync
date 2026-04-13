package com.example.petsync1.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.petsync1.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showResendEmail by remember { mutableStateOf(false) } // Controls Resend Email Button Visibility

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.petsynclogot1),
                contentDescription = "PetSync Logo",
                modifier = Modifier.size(280.dp),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Sign in to continue.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "EMAIL",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 12.dp)
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Enter email address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "PASSWORD",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 12.dp)
                )
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Enter password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Email/Password login button
            Button(
                onClick = { signInWithEmailPassword(email, password, context, navController, onEmailNotVerified = { showResendEmail = true }) },
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Show error message if email is not verified
            if (showResendEmail) {
                Text(
                    text = "Please verify your email before logging in.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )

                // Resend Email Verification Button
                TextButton(onClick = { resendVerificationEmail(email, context) }) {
                    Text("Resend Verification Email", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                }
            }

            // Forgot Password Button
            TextButton(onClick = { navController.navigate("forgot_password") }) {
                Text("Forgot Password?", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }

            // Navigate to Sign Up Screen
            TextButton(onClick = { navController.navigate("signup") }) {
                Text("Sign up!", fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
        }
    }
}

// Function to sign in using email and password
fun signInWithEmailPassword(
    email: String,
    password: String,
    context: Context,
    navController: NavController,
    onEmailNotVerified: () -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        return
    }

    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null && user.isEmailVerified) {
                    // Proceed to home screen if email is verified
                    Toast.makeText(context, "Sign-in successful!", Toast.LENGTH_SHORT).show()
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    // Email is not verified
                    onEmailNotVerified()
                    Toast.makeText(context, "Email not verified. Please check your email.", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Sign-in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

// Function to resend email verification
fun resendVerificationEmail(@Suppress("unused") email: String, context: Context) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    if (user != null) {
        user.sendEmailVerification()
            .addOnSuccessListener {
                Toast.makeText(context, "Verification email resent. Check your inbox.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to resend verification email.", Toast.LENGTH_SHORT).show()
            }
    } else {
        Toast.makeText(context, "No logged-in user found.", Toast.LENGTH_SHORT).show()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    com.example.petsync1.ui.theme.PetSync1Theme(darkTheme = false) {
        LoginScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreenDark() {
    com.example.petsync1.ui.theme.PetSync1Theme(darkTheme = true) {
        LoginScreen(navController = rememberNavController())
    }
}
