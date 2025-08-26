package com.example.airesume.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.util.Log
import com.example.airesume.data.model.Education
import com.example.airesume.data.model.Experience
import com.example.airesume.data.model.Resume
import com.example.airesume.data.model.Skill
import com.example.airesume.data.model.Project
import com.example.airesume.data.model.Certification
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.graphics.createBitmap

// Data class to hold information about each piece of content for layout
data class PageContentItem(
    val pageIndex: Int,
    val startY: Float,
    val type: ContentType,
    val data: Any // Can be Resume, PersonalInfo, Experience, Education, Skill, Project, Certification, String (for header/footer)
)

enum class ContentType {
    TITLE, PERSONAL_INFO, SUMMARY, EXPERIENCE_HEADER, EXPERIENCE_ITEM,
    EDUCATION_HEADER, EDUCATION_ITEM, SKILLS_HEADER, SKILLS_ITEM,
    PROJECTS_HEADER, PROJECT_ITEM,
    CERTIFICATIONS_HEADER, CERTIFICATION_ITEM,
    FOOTER
}

class ResumePrintDocumentAdapter(
    private val context: Context,
    private val resume: Resume
) : PrintDocumentAdapter() {

    private var pageCount: Int = 0
    private val contentLayout: MutableList<PageContentItem> = mutableListOf()
    private val PDF_PAGE_WIDTH = 595
    private val PDF_PAGE_HEIGHT = 842
    private val PDF_MARGIN = 50f

    private val COLOR_PRIMARY_CLASSIC = Color.BLACK
    private val COLOR_PRIMARY_MODERN = Color.rgb(0, 102, 204)
    private val COLOR_PRIMARY_MINIMALIST = Color.DKGRAY

    private fun drawWrappedText(canvas: Canvas, paint: Paint, text: String, x: Float, y: Float, maxWidth: Float): Float {
        val words = text.split(" ")
        var line = ""
        var yPos = y

        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            val width = paint.measureText(testLine)

            if (width > maxWidth) {
                canvas.drawText(line, x, yPos, paint)
                yPos += paint.textSize + 5f
                line = word
            } else {
                line = testLine
            }
        }

        if (line.isNotEmpty()) {
            canvas.drawText(line, x, yPos, paint)
            yPos += paint.textSize + 5f
        }

        return yPos
    }

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        cancellationSignal?.throwIfCanceled()

        val changed = newAttributes != oldAttributes || contentLayout.isEmpty() || oldAttributes.minMargins?.topMils != newAttributes.minMargins?.topMils
        if (changed) {
            calculatePageLayout()
        }

        val jobName = resume.title.ifEmpty { "Resume" }
        val builder = PrintDocumentInfo.Builder(jobName)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(pageCount)

        callback.onLayoutFinished(builder.build(), changed)
        Log.d("ResumePrintAdapter", "Layout finished. Page count: $pageCount, Template: ${resume.templateId}")
    }

    private fun calculatePageLayout() {
        contentLayout.clear()
        pageCount = 0

        val paint = Paint()
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.LEFT
        val contentWidth = PDF_PAGE_WIDTH - (2 * PDF_MARGIN)

        var currentPageIndex = 0
        var currentY = PDF_MARGIN

        fun addNewPageAndResetY(reason: String = "") {
            currentPageIndex++
            currentY = PDF_MARGIN
            Log.d("Layout", "Page break due to: $reason (New Page: $currentPageIndex)")
        }

        // --- Title ---
        paint.textSize = 24f
        paint.isFakeBoldText = true
        val titleHeight = 40f
        if (currentY + titleHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
            addNewPageAndResetY("Title Overflow")
        }
        contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.TITLE, resume.title))
        currentY += titleHeight

        // --- Personal Info ---
        paint.textSize = 14f
        paint.isFakeBoldText = false
        resume.personalInfo.let { info ->
            var personalInfoBlockHeight = 0f
            if (info.fullName.isNotEmpty()) personalInfoBlockHeight += 25f
            if (info.email.isNotEmpty()) personalInfoBlockHeight += 25f
            if (info.phone.isNotEmpty()) personalInfoBlockHeight += 25f
            if (info.address.isNotEmpty()) personalInfoBlockHeight += 25f
            if (info.linkedin.isNotEmpty()) personalInfoBlockHeight += 25f
            if (info.github.isNotEmpty()) personalInfoBlockHeight += 25f

            if (currentY + personalInfoBlockHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                addNewPageAndResetY("Personal Info Overflow")
            }
            contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.PERSONAL_INFO, info))
            currentY += personalInfoBlockHeight

            // Summary
            if (info.summary.isNotEmpty()) {
                val spaceBeforeSummary = 10f
                val summaryTitleHeight = 25f
                val dummyPaint = Paint(paint)
                dummyPaint.textSize = 14f
                val dummyCanvas = Canvas(createBitmap(1, 1))
                val startYForMeasurement = 0f
                val finalYForMeasurement = drawWrappedText(dummyCanvas, dummyPaint, info.summary, 0f, startYForMeasurement, contentWidth)
                val wrappedSummaryContentHeight = finalYForMeasurement - startYForMeasurement

                val totalSummaryBlockHeight = spaceBeforeSummary + summaryTitleHeight + wrappedSummaryContentHeight

                if (currentY + totalSummaryBlockHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                    addNewPageAndResetY("Summary Overflow")
                }
                contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.SUMMARY, info.summary))
                currentY += totalSummaryBlockHeight
            }
        }

        // --- Work Experience ---
        if (resume.experiences.isNotEmpty()) {
            val experienceHeaderHeight = 10f + 30f
            if (currentY + experienceHeaderHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                addNewPageAndResetY("Experience Header Overflow")
            }
            contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.EXPERIENCE_HEADER, "Work Experience"))
            currentY += experienceHeaderHeight

            resume.experiences.forEach { experience ->
                val dummyPaint = Paint(paint)
                dummyPaint.textSize = 14f
                val dummyCanvas = Canvas(createBitmap(1, 1))
                var itemHeight = 0f
                if (experience.jobTitle.isNotEmpty()) itemHeight += 20f
                if (experience.company.isNotEmpty()) itemHeight += 15f
                itemHeight += 20f // Dates
                if (experience.description.isNotEmpty()) {
                    val startYForMeasurement = 0f
                    val finalYForMeasurement = drawWrappedText(dummyCanvas, dummyPaint, experience.description, 0f, startYForMeasurement, contentWidth)
                    itemHeight += finalYForMeasurement - startYForMeasurement
                }
                itemHeight += 20f // Spacing after item

                if (currentY + itemHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                    addNewPageAndResetY("Experience Item Overflow")
                }
                contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.EXPERIENCE_ITEM, experience))
                currentY += itemHeight
            }
        }

        // --- Education ---
        if (resume.educations.isNotEmpty()) {
            val educationHeaderHeight = 10f + 30f
            if (currentY + educationHeaderHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                addNewPageAndResetY("Education Header Overflow")
            }
            contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.EDUCATION_HEADER, "Education"))
            currentY += educationHeaderHeight

            resume.educations.forEach { education ->
                var itemHeight = 0f
                if (education.degree.isNotEmpty()) itemHeight += 15f
                if (education.field.isNotEmpty()) itemHeight += 15f
                if (education.institution.isNotEmpty()) itemHeight += 15f
                itemHeight += 25f // Dates
                if (education.description.isNotEmpty()) {
                    val dummyPaint = Paint(paint)
                    dummyPaint.textSize = 14f
                    val dummyCanvas = Canvas(createBitmap(1, 1))
                    val startYForMeasurement = 0f
                    val finalYForMeasurement = drawWrappedText(dummyCanvas, dummyPaint, education.description, 0f, startYForMeasurement, contentWidth)
                    itemHeight += finalYForMeasurement - startYForMeasurement
                }
                itemHeight += 25f // Spacing after item

                if (currentY + itemHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                    addNewPageAndResetY("Education Item Overflow")
                }
                contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.EDUCATION_ITEM, education))
                currentY += itemHeight
            }
        }

        // --- Projects ---
        if (resume.projects.isNotEmpty()) {
            val projectsHeaderHeight = 10f + 30f
            if (currentY + projectsHeaderHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                addNewPageAndResetY("Projects Header Overflow")
            }
            contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.PROJECTS_HEADER, "Projects"))
            currentY += projectsHeaderHeight

            resume.projects.forEach { project ->
                val dummyPaint = Paint(paint)
                dummyPaint.textSize = 14f
                val dummyCanvas = Canvas(createBitmap(1, 1))
                var itemHeight = 0f
                if (project.name.isNotEmpty()) itemHeight += 20f
                if (project.role.isNotEmpty()) itemHeight += 15f
                if (project.technologies.isNotEmpty()) itemHeight += 15f
                itemHeight += 20f // Dates
                if (project.description.isNotEmpty()) {
                    val startYForMeasurement = 0f
                    val finalYForMeasurement = drawWrappedText(dummyCanvas, dummyPaint, project.description, 0f, startYForMeasurement, contentWidth)
                    itemHeight += finalYForMeasurement - startYForMeasurement
                }
                itemHeight += 20f // Spacing after item

                if (currentY + itemHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                    addNewPageAndResetY("Project Item Overflow")
                }
                contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.PROJECT_ITEM, project))
                currentY += itemHeight
            }
        }

        // --- Certifications ---
        if (resume.certifications.isNotEmpty()) {
            val certificationsHeaderHeight = 10f + 30f
            if (currentY + certificationsHeaderHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                addNewPageAndResetY("Certifications Header Overflow")
            }
            contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.CERTIFICATIONS_HEADER, "Certifications"))
            currentY += certificationsHeaderHeight

            resume.certifications.forEach { certification ->
                var itemHeight = 0f
                if (certification.name.isNotEmpty()) itemHeight += 15f
                if (certification.issuingOrganization.isNotEmpty()) itemHeight += 15f
                itemHeight += 20f // Dates (issueDate - expirationDate)
                if (certification.credentialId.isNotEmpty()) itemHeight += 15f
                if (certification.credentialUrl.isNotEmpty()) itemHeight += 15f
                itemHeight += 20f // Spacing after item

                if (currentY + itemHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                    addNewPageAndResetY("Certification Item Overflow")
                }
                contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.CERTIFICATION_ITEM, certification))
                currentY += itemHeight
            }
        }

        // --- Skills ---
        if (resume.skills.isNotEmpty()) {
            val skillsHeaderHeight = 10f + 30f
            if (currentY + skillsHeaderHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                addNewPageAndResetY("Skills Header Overflow")
            }
            contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.SKILLS_HEADER, "Skills"))
            currentY += skillsHeaderHeight

            resume.skills.forEach { skill ->
                val itemHeight = 20f
                if (currentY + itemHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
                    addNewPageAndResetY("Skill Item Overflow")
                }
                contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.SKILLS_ITEM, skill))
                currentY += itemHeight
            }
        }

        // --- Footer ---
        val footerHeight = 24f
        if (currentY + footerHeight > PDF_PAGE_HEIGHT - PDF_MARGIN) {
            addNewPageAndResetY("Footer Overflow")
        }
        contentLayout.add(PageContentItem(currentPageIndex, currentY, ContentType.FOOTER, resume.lastModified))

        pageCount = currentPageIndex + 1

        Log.d("ResumePrintAdapter", "Final calculated pages: $pageCount, Total content items: ${contentLayout.size}")
    }


    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback
    ) {
        val pdfDocument = PdfDocument()
        var activePage: PdfDocument.Page? = null
        var activeCanvas: Canvas? = null

        val currentPrimaryColor = when (resume.templateId) {
            "Modern" -> COLOR_PRIMARY_MODERN
            "Minimalist" -> COLOR_PRIMARY_MINIMALIST
            else -> COLOR_PRIMARY_CLASSIC
        }

        try {
            val paint = Paint()
            paint.color = Color.BLACK
            paint.textAlign = Paint.Align.LEFT
            val xPos = PDF_MARGIN
            val contentWidth = PDF_PAGE_WIDTH - (2 * PDF_MARGIN)

            fun ensurePageStarted(targetPageIndex: Int) {
                if (activePage?.info?.pageNumber != targetPageIndex + 1) {
                    activePage?.let { pdfDocument.finishPage(it) }
                    activePage = pdfDocument.startPage(PdfDocument.PageInfo.Builder(PDF_PAGE_WIDTH, PDF_PAGE_HEIGHT, targetPageIndex + 1).create())
                    activeCanvas = activePage!!.canvas
                }
            }

            for (item in contentLayout) {
                cancellationSignal?.throwIfCanceled()

                val isPageRequested = pages?.any { it.start <= item.pageIndex && item.pageIndex <= it.end } ?: false
                if (!isPageRequested) {
                    continue
                }

                ensurePageStarted(item.pageIndex)

                val canvas = activeCanvas!!
                var currentTextY = item.startY

                when (item.type) {
                    ContentType.TITLE -> {
                        paint.textSize = 24f
                        paint.isFakeBoldText = true
                        paint.color = currentPrimaryColor
                        canvas.drawText(item.data as String, xPos, currentTextY, paint)
                    }
                    ContentType.PERSONAL_INFO -> {
                        val info = item.data as com.example.airesume.data.model.PersonalInfo
                        paint.textSize = 14f
                        paint.isFakeBoldText = false
                        paint.color = Color.BLACK
                        if (info.fullName.isNotEmpty()) { canvas.drawText("Name: ${info.fullName}", xPos, currentTextY, paint); currentTextY += 25f }
                        if (info.email.isNotEmpty()) { canvas.drawText("Email: ${info.email}", xPos, currentTextY, paint); currentTextY += 25f }
                        if (info.phone.isNotEmpty()) { canvas.drawText("Phone: ${info.phone}", xPos, currentTextY, paint); currentTextY += 25f }
                        if (info.address.isNotEmpty()) { canvas.drawText("Address: ${info.address}", xPos, currentTextY, paint); currentTextY += 25f }
                        if (info.linkedin.isNotEmpty()) { canvas.drawText("LinkedIn: ${info.linkedin}", xPos, currentTextY, paint); currentTextY += 25f }
                        if (info.github.isNotEmpty()) { canvas.drawText("GitHub: ${info.github}", xPos, currentTextY, paint); currentTextY += 25f }
                    }
                    ContentType.SUMMARY -> {
                        val summaryText = item.data as String
                        currentTextY += 10f
                        paint.textSize = 16f
                        paint.isFakeBoldText = true
                        paint.color = currentPrimaryColor
                        canvas.drawText("Professional Summary:", xPos, currentTextY, paint)
                        currentTextY += 25f
                        paint.textSize = 14f
                        paint.isFakeBoldText = false
                        paint.color = Color.BLACK
                        drawWrappedText(canvas, paint, summaryText, xPos, currentTextY, contentWidth)
                    }
                    ContentType.EXPERIENCE_HEADER -> {
                        paint.textSize = 18f
                        paint.isFakeBoldText = true
                        paint.color = currentPrimaryColor
                        canvas.drawText(item.data as String, xPos, currentTextY, paint)
                    }
                    ContentType.EXPERIENCE_ITEM -> {
                        val experience = item.data as Experience
                        paint.textSize = 14f
                        paint.isFakeBoldText = false
                        paint.color = Color.BLACK
                        if (experience.jobTitle.isNotEmpty()) { canvas.drawText(experience.jobTitle, xPos, currentTextY, paint); currentTextY += 20f }
                        if (experience.company.isNotEmpty()) { canvas.drawText(experience.company, xPos, currentTextY, paint); currentTextY += 15f }
                        canvas.drawText("${experience.startDate} - ${experience.endDate}", xPos, currentTextY, paint)
                        currentTextY += 20f
                        if (experience.description.isNotEmpty()) {
                            drawWrappedText(canvas, paint, experience.description, xPos, currentTextY, contentWidth)
                        }
                    }
                    ContentType.EDUCATION_HEADER -> {
                        paint.textSize = 18f
                        paint.isFakeBoldText = true
                        paint.color = currentPrimaryColor
                        canvas.drawText(item.data as String, xPos, currentTextY, paint)
                    }
                    ContentType.EDUCATION_ITEM -> {
                        val education = item.data as Education
                        paint.textSize = 14f
                        paint.isFakeBoldText = false
                        paint.color = Color.BLACK
                        if (education.degree.isNotEmpty()) {
                            canvas.drawText(education.degree, xPos, currentTextY, paint); currentTextY += 15f
                        }
                        if (education.field.isNotEmpty()) {
                            canvas.drawText(education.field, xPos, currentTextY, paint); currentTextY += 15f
                        }
                        if (education.institution.isNotEmpty()) { canvas.drawText(education.institution, xPos, currentTextY, paint); currentTextY += 15f }
                        canvas.drawText("${education.startDate} - ${education.endDate}", xPos, currentTextY, paint)
                        currentTextY += 20f
                        if (education.description.isNotEmpty()) {
                            drawWrappedText(canvas, paint, education.description, xPos, currentTextY, contentWidth)
                        }
                    }
                    ContentType.PROJECTS_HEADER -> {
                        paint.textSize = 18f
                        paint.isFakeBoldText = true
                        paint.color = currentPrimaryColor
                        canvas.drawText(item.data as String, xPos, currentTextY, paint)
                    }
                    ContentType.PROJECT_ITEM -> {
                        val project = item.data as Project
                        paint.textSize = 14f
                        paint.isFakeBoldText = false
                        paint.color = Color.BLACK
                        if (project.name.isNotEmpty()) { canvas.drawText(project.name, xPos, currentTextY, paint); currentTextY += 20f }
                        if (project.role.isNotEmpty()) { canvas.drawText("Role: ${project.role}", xPos, currentTextY, paint); currentTextY += 15f }
                        if (project.technologies.isNotEmpty()) { canvas.drawText("Tech: ${project.technologies}", xPos, currentTextY, paint); currentTextY += 15f }
                        canvas.drawText("${project.startDate} - ${project.endDate}", xPos, currentTextY, paint)
                        currentTextY += 20f
                        if (project.description.isNotEmpty()) {
                            drawWrappedText(canvas, paint, project.description, xPos, currentTextY, contentWidth)
                        }
                    }
                    ContentType.CERTIFICATIONS_HEADER -> {
                        paint.textSize = 18f
                        paint.isFakeBoldText = true
                        paint.color = currentPrimaryColor
                        canvas.drawText(item.data as String, xPos, currentTextY, paint)
                    }
                    ContentType.CERTIFICATION_ITEM -> {
                        val certification = item.data as Certification
                        paint.textSize = 14f
                        paint.isFakeBoldText = false
                        paint.color = Color.BLACK
                        if (certification.name.isNotEmpty()) { canvas.drawText(certification.name, xPos, currentTextY, paint); currentTextY += 15f }
                        if (certification.issuingOrganization.isNotEmpty()) { canvas.drawText("Issued by: ${certification.issuingOrganization}", xPos, currentTextY, paint); currentTextY += 15f }
                        canvas.drawText("Dates: ${certification.issueDate} - ${certification.expirationDate}", xPos, currentTextY, paint)
                        currentTextY += 20f
                        if (certification.credentialId.isNotEmpty()) { canvas.drawText("Credential ID: ${certification.credentialId}", xPos, currentTextY, paint); currentTextY += 15f }
                        if (certification.credentialUrl.isNotEmpty()) { canvas.drawText("Credential URL: ${certification.credentialUrl}", xPos, currentTextY, paint); currentTextY += 15f }
                    }
                    ContentType.SKILLS_HEADER -> {
                        paint.textSize = 18f
                        paint.isFakeBoldText = true
                        paint.color = currentPrimaryColor
                        canvas.drawText(item.data as String, xPos, currentTextY, paint)
                    }
                    ContentType.SKILLS_ITEM -> {
                        val skill = item.data as Skill
                        paint.textSize = 14f
                        paint.isFakeBoldText = false
                        paint.color = Color.BLACK
                        canvas.drawText("${skill.name} (${skill.level})", xPos, currentTextY, paint)
                    }
                    ContentType.FOOTER -> {
                        paint.textSize = 14f
                        paint.textAlign = Paint.Align.RIGHT
                        paint.color = Color.GRAY
                        val lastModified = item.data as Long
                        val footerText = "Last modified: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(lastModified))}"
                        canvas.drawText(footerText, PDF_PAGE_WIDTH - PDF_MARGIN, currentTextY, paint)
                    }
                }
            }

            activePage?.let { pdfDocument.finishPage(it) }

            pdfDocument.writeTo(FileOutputStream(destination.fileDescriptor))
            callback.onWriteFinished(pages)
        } catch (e: Exception) {
            Log.e("ResumePrintAdapter", "Error writing PDF: ${e.message}", e)
            callback.onWriteFailed(e.message)
        } finally {
            pdfDocument.close()
            try {
                destination.close()
            } catch (e: IOException) {
                Log.e("ResumePrintAdapter", "Error closing ParcelFileDescriptor: ${e.message}", e)
            }
        }
    }

    override fun onFinish() {
        Log.d("ResumePrintAdapter", "Print job finished.")
    }
}