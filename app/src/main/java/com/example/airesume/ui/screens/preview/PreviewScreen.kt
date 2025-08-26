package com.example.airesume.ui.screens.preview

import android.app.Application
import android.content.Context
import android.content.Intent // Explicit import
import android.graphics.Canvas // Explicit import
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument // Explicit import
import android.print.PrintManager
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.airesume.ui.components.common.AppButton
import com.example.airesume.ui.components.preview.ResumePreview
import com.example.airesume.utils.ResumePrintDocumentAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    navController: NavController,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    resumeId: Long,
    viewModel: PreviewViewModel = viewModel(
        factory = PreviewViewModel.Factory(
            application = LocalContext.current.applicationContext as Application,
            resumeId = resumeId
        )
    )
) {
    val resume by viewModel.resume.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    /// Helper function to draw wrapped text onto the PDF canvas (needed for share PDF if not using adapter)
    fun drawWrappedText(canvas: Canvas, paint: Paint, text: String, x: Float, y: Float, maxWidth: Float): Float {
        val words = text.split(" ")
        var line = ""
        var yPos = y

        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            val width = paint.measureText(testLine)

            if (width > maxWidth) {
                canvas.drawText(line, x, yPos, paint)
                yPos += paint.textSize + 5f // Line height + small gap
                line = word
            } else {
                line = testLine
            }
        }

        if (line.isNotEmpty()) {
            canvas.drawText(line, x, yPos, paint)
            yPos += paint.textSize + 5f // Line height + small gap for next line
        }

        return yPos
    }

    // Function to initiate the print/save-as-PDF flow
    fun printResume() {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${resume?.title ?: "Resume"}_${System.currentTimeMillis()}"

        resume?.let {
            val printAdapter = ResumePrintDocumentAdapter(context, it)
            printManager.print(jobName, printAdapter, null)
        } ?: run {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Resume data not loaded yet.")
            }
        }
    }

    // Function to generate PDF and save to app-specific storage (for sharing)
    suspend fun generatePdfForSharingTemp(): Uri? = withContext(Dispatchers.IO) {
        if (resume == null) return@withContext null

        var pdfDocumentInstance: PdfDocument? = null
        var pdfOutputStream: OutputStream? = null

        try {
            val currentPdfDocument = PdfDocument()
            pdfDocumentInstance = currentPdfDocument

            val PDF_PAGE_WIDTH = 595 // A4 width
            val PDF_PAGE_HEIGHT = 842 // A4 height
            val PDF_MARGIN = 50f // Margins

            val pageInfo = PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH, PDF_PAGE_HEIGHT, 1).create()

            val currentPage = currentPdfDocument.startPage(pageInfo) // Start the first page
            val canvas = currentPage.canvas
            var yPos = PDF_MARGIN

            val paint = Paint()
            paint.color = Color.BLACK
            paint.textAlign = Paint.Align.LEFT
            val xPos = PDF_MARGIN
            val contentWidth = PDF_PAGE_WIDTH - (2 * PDF_MARGIN)

            // --- Simplified drawing for sharing (might only populate first page heavily) ---
            // If you need multi-page PDF for sharing, you should consider using the PrintDocumentAdapter's
            // logic here as well, writing to a temp file.
            paint.textSize = 24f
            paint.isFakeBoldText = true
            canvas.drawText(resume!!.title, xPos, yPos, paint)
            yPos += 40f

            paint.textSize = 14f
            paint.isFakeBoldText = false
            resume!!.personalInfo.let { info ->
                if (info.fullName.isNotEmpty()) { canvas.drawText("Name: ${info.fullName}", xPos, yPos, paint); yPos += 25f }
                if (info.email.isNotEmpty()) { canvas.drawText("Email: ${info.email}", xPos, yPos, paint); yPos += 25f }
                if (info.phone.isNotEmpty()) { canvas.drawText("Phone: ${info.phone}", xPos, yPos, paint); yPos += 25f }
                if (info.address.isNotEmpty()) { canvas.drawText("Address: ${info.address}", xPos, yPos, paint); yPos += 25f }
                if (info.linkedin.isNotEmpty()) { canvas.drawText("LinkedIn: ${info.linkedin}", xPos, yPos, paint); yPos += 25f }
                if (info.github.isNotEmpty()) { canvas.drawText("GitHub: ${info.github}", xPos, yPos, paint); yPos += 25f }
                if (info.summary.isNotEmpty()) {
                    yPos += 10f // Space before summary header
                    paint.isFakeBoldText = true // Set summary title bold
                    paint.textSize = 16f // Set summary title size
                    canvas.drawText("Professional Summary:", xPos, yPos, paint)
                    yPos += 25f // Space after summary header
                    paint.isFakeBoldText = false // Reset for summary content
                    paint.textSize = 14f // Reset for summary content
                    yPos = drawWrappedText(canvas, paint, info.summary, xPos, yPos, contentWidth)
                }
            }
            // ... continue with experiences, education, skills, each with their own page break logic.
            // Remember to handle new pages if content overflows the current page within this temp generation.
            // The logic from the original multi-page savePdfToUri should work here as is.


            currentPdfDocument.finishPage(currentPage) // Finish the page

            val fileName = "resume_share_${resume!!.id}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            pdfOutputStream = FileOutputStream(file)
            pdfOutputStream.let {
                currentPdfDocument.writeTo(it)
            }

            return@withContext FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        } finally {
            pdfOutputStream?.close()
            pdfDocumentInstance?.close()
        }
    }


    // Function to share the PDF
    suspend fun sharePdf() {
        val pdfUri = generatePdfForSharingTemp()
        if (pdfUri != null) {
            withContext(Dispatchers.Main) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, pdfUri)
                    type = "application/pdf"
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                context.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        "Share Resume PDF"
                    )
                )
            }
        } else {
            withContext(Dispatchers.Main) {
                snackbarHostState.showSnackbar("Failed to generate PDF for sharing")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Resume Preview") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Edit, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            sharePdf()
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (resume != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Display the resume title (on-screen composable)
                Text(
                    text = resume!!.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                // Display the actual resume content (on-screen composable)
                ResumePreview(
                    resume = resume!!,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AppButton(
                        text = "Edit",
                        onClick = { navController.navigate("form/${resume!!.id}") },
                        modifier = Modifier.weight(1f)
                    )
                    AppButton(
                        text = "Print / Save as PDF", // Changed button text
                        onClick = {
                            printResume() // Call the new print function
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}