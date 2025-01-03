package com.example.week1

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@Composable
fun EditView(navHostController: NavHostController,
             backStackEntry: NavBackStackEntry,
             editViewModel: EditViewModel) {

    val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: -1

    LaunchedEffect(key1 = id) {
        if (id != -1) {
            editViewModel.getContact(id)
        }

    }
    val contact = editViewModel.contactState.value
    var diaryState = editViewModel.diaryState.value

    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider", file
    )

    var ImageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }


    val ImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { pickerUri ->
            if (pickerUri != null) {
                ImageUri = pickerUri
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        ImageUri = uri
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    val scrollState = rememberScrollState()

    Column (modifier = Modifier.padding(bottom = 64.dp, top = 64.dp).fillMaxSize().verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally)
    {

        if (ImageUri == Uri.EMPTY){
            Image(
                painter = painterResource(R.drawable.daramgi),
                "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(250.dp)
            )
        }
        else{
            Image(
                painter = rememberAsyncImagePainter(ImageUri),
                "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(250.dp)
            )
        }



        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                shape = RectangleShape,
                border = BorderStroke(1.dp, Color(0xff8f7a79)),
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                onClick = {

                    val permissionCheckResult =
                        ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        // Request a permission
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {

                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color(0xff8f7a79)
                )

            }

            Spacer(Modifier.padding(4.dp))

            Button(
                shape = RectangleShape,
                border = BorderStroke(1.dp, Color(0xff8f7a79)),
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                onClick = {
                    ImagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = null,
                    tint = Color(0xff8f7a79)
                )
            }

        }

        CustomDivider()

        TextField(
            value = diaryState,
            onValueChange = { editViewModel.updateDiaryText(it) },
            placeholder = { Text("추억을 기록하세요") }, // Placeholder 텍스트
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        HorizontalDivider(
            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
            thickness = 1.dp, // 원하는 두께 설정,
            color = Color(0xFF8F7A79) // 8F7A79 색상
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),

        ){
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff8f7a79), // 버튼 배경색
                    contentColor = Color.White // 텍스트 또는 아이콘 색상
                ),
                shape = RoundedCornerShape(16.dp),
                onClick = {
                    navHostController.popBackStack()
                }
            ){
                Text("취소")
            }

            Spacer(Modifier.weight(1f))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff8f7a79), // 버튼 배경색
                    contentColor = Color.White // 텍스트 또는 아이콘 색상
                ),
                shape = RoundedCornerShape(16.dp),
                onClick = {

                    if(ImageUri == Uri.EMPTY){
                        Toast.makeText(context,"이미지를 추가해주세요", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val fileName = "image_${System.currentTimeMillis()}.jpg"

                        val bitmap = editViewModel.getBitmapFromUri(context, ImageUri)
                        val path = editViewModel.saveBitmapToInternalStorage(context, bitmap, fileName)


                        val newContact = contact.copy(
                            name = contact.name,
                            id = contact.id,
                            phone = contact.phone,
                            images = contact.images + ImageComponent(imageUri = path, diary = diaryState)
                        )
                        editViewModel.updateContact(newContact)
                        navHostController.popBackStack()
                    }


                },

            ) {
                Text("저장")
            }
        }
    }


}

@SuppressLint("SimpleDateFormat")
fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
    return image
}