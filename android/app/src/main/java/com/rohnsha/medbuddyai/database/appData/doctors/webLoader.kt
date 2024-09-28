import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Launch
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.ui.theme.BGMain
import com.rohnsha.medbuddyai.ui.theme.ViewDash
import com.rohnsha.medbuddyai.ui.theme.lightTextAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewLoaderSc(url: String, navController: NavHostController) {
    var isLoading by remember { mutableStateOf(true) }

    val currentURL= remember {
        mutableStateOf(url)
    }

    val context= LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    WebUItopBar(
                        url = currentURL.value,
                        navController = navController
                    )},
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back Icon"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentURL.value))
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Launch,
                            contentDescription = "Launch in chrome Icon"
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize(),
        containerColor = BGMain
    ){ values ->
        Column(
            Modifier.padding(values)
        ) {
            //WebUItopBar(url = currentURL.value, navController = navController)

            Box(modifier = Modifier.weight(1f)) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, urlChange: String?, favicon: Bitmap?) {
                                    isLoading = true
                                    urlChange?.let { currentURL.value = it }
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    isLoading = false
                                }
                            }
                            settings.apply {
                                javaScriptEnabled= true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                            }
                        }
                    },
                    update = { webView ->
                        webView.loadUrl(url)
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxSize()
                )
            }
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator(
                        color = ViewDash,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebUItopBar(url: String, navController: NavHostController) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        //modifier = Modifier.padding(start= 8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 21.dp, horizontal = 32.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
            ) {
                Text(
                    text = url,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = lightTextAccent,
                    fontSize = 14.sp
                )
            }
        }
    }
}