package com.practicum.playlistmaker.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.screens.playlists.PlaylistViewModel
import com.practicum.playlistmaker.ui.theme.AppColors
import com.practicum.playlistmaker.ui.theme.PlaylistMakerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPlaylistScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: PlaylistViewModel = viewModel(
        factory = PlaylistViewModel.getViewModelFactory()
    )

    var playlistName by remember { mutableStateOf("") }
    var playlistDescription by remember { mutableStateOf("") }

    val duplicateError by viewModel.duplicatePlaylistError.collectAsState()
    val context = LocalContext.current
    var toastShown by remember { mutableStateOf(false) }


    val duplicateErrorText = stringResource(R.string.duplicate_playlist_error)

    LaunchedEffect(duplicateError) {
        if (duplicateError && !toastShown) {
            Toast.makeText(
                context,
                duplicateErrorText,
                Toast.LENGTH_SHORT
            ).show()
            toastShown = true
        } else if (!duplicateError) {
            toastShown = false
        }
    }


    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp)
                .background(color = AppColors.white, shape = RoundedCornerShape(0.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = stringResource(R.string.playlist_icon),
                        modifier = Modifier.size(120.dp),
                        tint = AppColors.gray
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    OutlinedTextField(
                        value = playlistName,
                        onValueChange = { playlistName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        label = {
                            Row {
                                Text(
                                    stringResource(R.string.playlist_name_placeholder),
                                    color = AppColors.gray
                                )
                                Text(
                                    "*",
                                    color = androidx.compose.ui.graphics.Color.Red,
                                    modifier = Modifier.padding(start = 2.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.primaryBlue,
                            unfocusedBorderColor = AppColors.gray,
                            focusedTextColor = AppColors.black,
                            unfocusedTextColor = AppColors.black,
                            focusedContainerColor = AppColors.white,
                            unfocusedContainerColor = AppColors.white
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = playlistDescription,
                        onValueChange = { playlistDescription = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        label = {
                            Text(
                                stringResource(R.string.playlist_description_placeholder),
                                color = AppColors.gray
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.primaryBlue,
                            unfocusedBorderColor = AppColors.gray,
                            focusedTextColor = AppColors.black,
                            unfocusedTextColor = AppColors.black,
                            focusedContainerColor = AppColors.white,
                            unfocusedContainerColor = AppColors.white
                        )
                    )

                    Button(
                        onClick = {
                            if (playlistName.isNotEmpty()) {
                                viewModel.createNewPlaylist(playlistName, playlistDescription)
                                if (!duplicateError) onSaveClick()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = playlistName.isNotEmpty(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.primaryBlue,
                            contentColor = AppColors.white
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.save_playlist),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(AppColors.white),
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
                    contentDescription = stringResource(R.string.back_button),
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBackClick() },
                    tint = AppColors.black
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.new_playlist_title),
                    color = AppColors.black,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewPlaylistPreview() {
    PlaylistMakerTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = AppColors.white) {
            NewPlaylistScreen(
                onBackClick = { },
                onSaveClick = { }
            )
        }
    }
}