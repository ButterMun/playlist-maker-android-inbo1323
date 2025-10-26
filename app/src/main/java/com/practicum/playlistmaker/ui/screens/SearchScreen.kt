package com.practicum.playlistmaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme


@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Поисковый запрос
    var searchQuery by remember { mutableStateOf(TextFieldValue()) }

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Контент меню
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(0.dp)
                )
                .padding(vertical = 16.dp, horizontal = 16.dp)
        ) {
            // Поисковая строка
            SearchTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                onClearClick = { searchQuery = TextFieldValue() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        // Синяя шапка поверх контента
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color(0xFFFFFFFF))
                .windowInsetsPadding(WindowInsets.statusBars),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBackClick() }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Поиск",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
                    )
                )
            }
        }
    }
}

@Composable
fun SearchTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = {
            Text(
                text = "Поиск",
                color = Color.Gray,
                fontSize = 16.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
        },
        trailingIcon = {
            if (value.text.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear search",
                    modifier = Modifier
                        .clickable { onClearClick() },
                    tint = Color.Gray
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedContainerColor = Color(0xFFF5F5F5),
            cursorColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        shape = RoundedCornerShape(16.dp),


        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    PlaylistMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFFFFFFF)) {
            SearchScreen(
                onBackClick = { }
            )
        }
    }
}