package com.rohnsha.medbuddyai.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rohnsha.medbuddyai.ui.theme.fontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentationScreen(
    index: Int,
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (index==0) "Data Usage" else "Documentation",
                        fontFamily = fontFamily,
                        fontWeight = FontWeight(600),
                        fontSize = 26.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back Icon"
                        )
                    }
                }
            )
        },
        modifier = Modifier
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.tertiary
    ){ values ->
        LazyColumn(
            modifier = Modifier
                .padding(values)
                .padding(top = 20.dp)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp))
                .padding(top = 30.dp, start = 24.dp, end = 24.dp)
        ) {
            item {
                Text(
                    text = returnDoc(index),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    modifier = Modifier,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}





private fun returnDoc(index: Int): String {
    fun legally(): String{
        return "We are committed to protecting your privacy and securing your data. This policy explains how we handle your information.\n" +
                "We process scan-related activities locally on your device, without transmitting data to our servers or third-party services, unless you choose to use external services. We don't store scan history, chat history, or sensitive information on our cloud servers.\n" +
                "Community posts, usernames, and timestamps are stored on our cloud servers to facilitate community engagement.\n" +
                "API keys for operating Large Language Models are stored locally on your device, not on our cloud servers.\n" +
                "We use your data only for the purposes stated in this policy and to provide the app's functionality. We don't share your data with third parties, except as required by law or with your explicit consent.\n" +
                "We use encryption to protect your data and implement strict access controls to ensure only authorized personnel can access your data.\n" +
                "You have the right to access, delete, and withdraw consent for using your data at any time.\n" +
                "We may update this policy and notify you of significant changes.\n" +
                "If you have questions or concerns, please contact us at `rohnsha0@gmail.com`."
    }

    fun scanResults(): String{
        return "When a user uploads an image for analysis, the app initiates its on-device scanning process. The CNN models, which have been trained on extensive datasets of medical images, work directly on the user's device to interpret the uploaded scan. This local processing ensures that the user's sensitive medical data never leaves their device during the analysis phase.\n" +
                "The CNN models are capable of identifying various patterns and anomalies in the images that might indicate specific health conditions. However, it's important to note that these results are intended for informational purposes only and should not be considered a definitive medical diagnosis.\n" +
                "For users who wish to gain additional insights, SwasthAI offers an option to verify the results with third-party language models (LLMs) such as ChatGPT or Claude. This step is entirely optional, and users must explicitly choose to share their data for this purpose. If selected, only the original image, not the analysis results, are sent to these external services.\n" +
                "By default, no scan data or analysis results are stored in the cloud or transmitted over the internet. This approach prioritizes user privacy and data security, ensuring that users retain full control over their sensitive medical information at all times."
    }

    fun scanQuestions(): String{
        return "After the initial disease detection using CNN models, SwasthAI employs a crucial additional step: asking the user targeted questions based on the detected condition. This feature significantly enhances the accuracy and reliability of the results.\n" +
                "The follow-up questions serve multiple purposes. They help mitigate false positives by cross-verifying the AI's findings with the user's actual symptoms. This process also gathers vital contextual information, often necessary for a more comprehensive health assessment. Additionally, it educates users about the symptoms associated with the detected condition, enabling more informed decisions about seeking professional medical advice.\n" +
                "Based on the user's responses, SwasthAI can provide more tailored guidance, whether it's suggesting lifestyle changes or recommending further medical consultation. This personalized approach adds considerable value to the user experience.\n" +
                "Importantly, this entire process occurs on the user's device, ensuring privacy and data security. No personal health information is transmitted or stored externally unless the user explicitly opts for third-party verification services.\n" +
                "While this two-step process significantly improves accuracy, it's crucial to remember that SwasthAI is not a substitute for professional medical advice. The app's results should be used as a starting point for discussions with healthcare providers, not as definitive diagnoses.\n" +
                "By implementing this question-based refinement, SwasthAI aims to provide a more reliable and comprehensive tool for understanding one's health, always encouraging users to seek professional medical advice for any concerns."
    }

    return when(index){
        0 -> legally()
        1 -> scanQuestions()
        2 -> scanResults()
        else -> ""
    }

}