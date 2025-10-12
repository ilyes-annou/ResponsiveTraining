package com.example.testproject.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.platform.SpatialConfiguration
import androidx.xr.compose.spatial.ContentEdge
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.ExperimentalSubspaceVolumeApi
import androidx.xr.compose.subspace.SpatialColumn
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.SpatialRow
import androidx.xr.compose.subspace.Volume
import androidx.xr.compose.subspace.layout.SpatialAlignment
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.offset
import androidx.xr.compose.subspace.layout.scale
import androidx.xr.compose.subspace.layout.width
import androidx.xr.scenecore.GltfModel
import androidx.xr.scenecore.GltfModelEntity
import coil.compose.AsyncImage
import com.example.testproject.ui.theme.Blue
import com.example.testproject.ui.theme.LightBlue
import com.example.testproject.ui.theme.PaleBlue
import com.example.testproject.viewModel.VideoListViewModel
import java.nio.file.Paths


//Video List view used in all UI modes
@Composable
fun VideoList(vm: VideoListViewModel, onClick: (String) -> Unit){
    Column(modifier = Modifier.padding(8.dp)) {
        //Error Message
        if (vm.videos.isEmpty()) {
            Text("Sorry a problem was encountered, please check your internet connection and try again")
            Button(
                onClick = {vm.loadVideos()},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue,
                    contentColor = White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Autorenew,
                    contentDescription = "Play",
                    tint = White
                )
            }
        }
        //Actual Video List
        else {
            vm.videos.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = PaleBlue, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Image & Title
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = item.thumbnail,
                            contentDescription = "Thumbnail",
                            modifier = Modifier
                                .size(height = 40.dp, width = 50.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = item.title,
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    //Play Button, launches Video Player with corresponding Video url
                    Button(
                        onClick = { onClick(item.url) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play",
                            tint = White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

//2D UI Video List
@Composable
fun VideoListView2D(onNavigateToPlayer: (String) -> Unit, vm: VideoListViewModel, spatialConfiguration: SpatialConfiguration){

    val spacialCapabilities = LocalSpatialCapabilities.current

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding(), verticalArrangement = Arrangement.SpaceBetween,  horizontalAlignment = Alignment.CenterHorizontally) {

        VideoList(vm, onClick ={ url -> onNavigateToPlayer(url) })

        if(spacialCapabilities.isSpatialAudioEnabled) {// Only value found that was false for mobile and true for XR HomeSpace & FullSpace Mode <=> if(isXRDevice)
            //Text("Video List spatial ui ${spacialCapabilities.isSpatialUiEnabled} 3d content ${spacialCapabilities.isContent3dEnabled} environnement ${spacialCapabilities.isAppEnvironmentEnabled} passthrough ${spacialCapabilities.isPassthroughControlEnabled} spatial audio ${spacialCapabilities.isSpatialAudioEnabled} ")
            Button(
                onClick = { vm.switchXRMode(spatialConfiguration) },
                modifier= Modifier.align(Alignment.CenterHorizontally).padding(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightBlue),
            )
            {
                Text("Switch XR Mode", color = White)
            }
        }

    }
}

//3D UI Video List
@Composable
fun VideoListView3D(
    vm: VideoListViewModel,
    spatialConfiguration: SpatialConfiguration
){

    val isVideoLaunched by vm.isVideoLaunched.collectAsState()
    val currentUrl = vm.currentVideoUrl.collectAsState().value

    Subspace {

        SpatialRow(modifier = SubspaceModifier.align(SpatialAlignment.Center)) {
            //Button to switch between FullSpace and HouseSpace modes
            Orbiter(
                position = ContentEdge.Top,
                alignment = Alignment.CenterHorizontally
            )
            {
                Button(
                    onClick = { vm.switchXRMode(spatialConfiguration) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightBlue,
                        contentColor = White
                    )
                )
                {
                    Text("Switch XR Mode")
                }
            }

            //Panel to hold the Video List
            SpatialPanel(SubspaceModifier
                .height(500.dp)
                .width(400.dp)
            )
            {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .padding(5.dp)
                )
                {
                    VideoList(vm, onClick = { url -> vm.showVideoPanelAndFootballField(url) })
                }
            }

            //If the Video is ready, show the Video Player and 3D object
            if (isVideoLaunched && currentUrl != null) {
                SpatialColumn {
                    SpatialPanel(SubspaceModifier.height(200.dp).width(400.dp)) {
                        VideoPlayerScreen(
                            onNavigateToList = { vm.setVideoLaunched(false) },
                            videoUrl = currentUrl,
                            vm = vm,
                            spatialConfiguration = spatialConfiguration
                        )
                    }
                    FootballField()
                }
            }
        }

    }
}

//3D object displayed below 3D video
@OptIn(ExperimentalSubspaceVolumeApi::class)
@Composable
fun FootballField() {
    Log.d("XR", "🧩 ObjectInAVolume Composable lancé")
    val session = checkNotNull(LocalSession.current)
    var gltfModel by remember { mutableStateOf<GltfModel?>(null) }

    //Loads 3d model from GLB file
    LaunchedEffect(Unit) {
        try {
            gltfModel = GltfModel.create(session, Paths.get("models", "football_field.glb"))
        }
        catch(e : Exception){
            Log.e("XR", "GLTFModel loading error : ${e.message}")
        }

    }

    if (gltfModel != null) {

        //3D space to hold the model, offset to be below the video
        Volume(
            modifier = SubspaceModifier
                .offset(0.dp,-100.dp, 0.dp,)
                .scale(0.01f)
        )
        {
            parent ->
            gltfModel?.let {
                model ->
                val entity = GltfModelEntity.create(session, model)
                parent.addChild(entity)
            }
        }

    }
    else {
        Log.d("XR", "3D Model loading")
    }

}

@Preview
@Composable fun PreviewVideoCard(){
    Row(Modifier.padding(5.dp)//margin
        .background(color = PaleBlue, shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 10.dp, bottomStart = 10.dp))
        .padding(3.dp)
        ) {

        Text(
            text = "Item ",
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = {},
            modifier = Modifier.background(color =Color(0xFF1958e6), shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 10.dp, bottomStart = 10.dp))
        ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play"
                )
            }
    }
}
