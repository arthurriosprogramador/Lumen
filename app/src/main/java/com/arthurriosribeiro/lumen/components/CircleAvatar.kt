package com.arthurriosribeiro.lumen.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.arthurriosribeiro.lumen.R
import com.arthurriosribeiro.lumen.screens.viewmodel.MainViewModel
import com.arthurriosribeiro.lumen.utils.LocalActivity
import java.io.File


@Composable
fun CircleAvatar(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val accountConfig = viewModel.accountConfig.value
    var imagePath by remember { mutableStateOf(accountConfig?.userImage) }

    val activity = LocalActivity.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val savedPath = viewModel.copyUriToInternalStorage(activity, it)
                savedPath?.let { path ->
                    accountConfig?.id?.let { id ->
                        viewModel.updateUserImage(path, id)
                        imagePath = path
                    }
                }
            }
        }
    )


    Box(
        modifier = modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
            .clickable {
                launcher.launch("image/*")
            },
        contentAlignment = Alignment.Center
    ) {
        val file = imagePath?.let { File(it) }
        if (file?.exists() == true) {
            AsyncImage(
                model = file,
                contentDescription = "Selected image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = stringResource(R.string.user_configuration_add_photo),
                    modifier = Modifier.size(40.dp),
                )
                Text(
                    stringResource(R.string.user_configuration_add_photo),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}