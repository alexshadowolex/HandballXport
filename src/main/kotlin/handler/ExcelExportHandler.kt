package alex.handball.handler


import alex.handball.config.AppConfig
import alex.handball.findUniqueOutputFileName
import alex.handball.logger
import alex.handball.model.Fixture
import alex.handball.normalizeGymName
import alex.handball.normalizeTeamName
import org.apache.poi.common.usermodel.HyperlinkType
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFFont
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ExcelExportHandler (
    private val fixtures: List<Fixture>,
    private val excelColumnIndices: ExcelColumnIndices,
    private val headerNames: List<String>
) {
    private val workbook = HSSFWorkbook()
    private val sheet = workbook.createSheet()
    private val createHelper = workbook.creationHelper

    private val fontName = "Calibri"
    private val cellWidthEnlargementToHaveResultLessSquished = 2

    private var linkStyleFirstRowAfterHeader: HSSFCellStyle? = null
    private var linkStyle: HSSFCellStyle? = null
    private var headerStyle: HSSFCellStyle? = null
    private var contentStyleNotFirstRowNotCenteredNotBold: HSSFCellStyle? = null
    private var contentStyleNotFirstRowCenteredNotBold: HSSFCellStyle? = null
    private var contentStyleFirstRowNotCenteredNotBold: HSSFCellStyle? = null
    private var contentStyleNotFirstRowCenteredBold: HSSFCellStyle? = null
    private var contentStyleFirstRowCenteredNotBold: HSSFCellStyle? = null
    private var contentStyleFirstRowCenteredBold: HSSFCellStyle? = null
    private var contentStyleFirstRowCenteredRed: HSSFCellStyle? = null
    private var contentStyleNotFirstRowCenteredRed: HSSFCellStyle? = null

    init {
        linkStyleFirstRowAfterHeader = createLinkStyle(isFirstRowAfterHeader = true)
        linkStyle = createLinkStyle(isFirstRowAfterHeader = false)
        headerStyle = createHeaderStyle()
        contentStyleNotFirstRowNotCenteredNotBold = createTableContentStyle()
        contentStyleNotFirstRowCenteredNotBold = createTableContentStyle(isCenteredHorizontally = true)
        contentStyleFirstRowNotCenteredNotBold = createTableContentStyle(isFirstRowAfterHeader = true)
        contentStyleNotFirstRowCenteredBold = createTableContentStyle(isCenteredHorizontally = true, isFontWeightBold = true)
        contentStyleFirstRowCenteredNotBold = createTableContentStyle(isFirstRowAfterHeader = true, isCenteredHorizontally = true)
        contentStyleFirstRowCenteredBold = createTableContentStyle(isFirstRowAfterHeader = true, isCenteredHorizontally = true, isFontWeightBold = true)
        contentStyleFirstRowCenteredRed = createTableContentStyle(isFirstRowAfterHeader = true, isCenteredHorizontally = true, isFontColorRed = true)
        contentStyleNotFirstRowCenteredRed = createTableContentStyle(isCenteredHorizontally = true, isFontColorRed = true)
    }

    private fun createLinkStyle(isFirstRowAfterHeader: Boolean): HSSFCellStyle {
        val style = workbook.createCellStyle()
        val font = workbook.createFont()
        font.underline = HSSFFont.U_SINGLE
        font.color = IndexedColors.BLUE.index
        font.fontName = fontName
        style.setFont(font)

        style.alignment = HorizontalAlignment.CENTER
        style.verticalAlignment = VerticalAlignment.CENTER

        style.borderBottom = BorderStyle.THIN
        style.borderLeft = BorderStyle.THIN
        style.borderRight = BorderStyle.THIN
        if(!isFirstRowAfterHeader) {
            style.borderTop = BorderStyle.THIN
        }

        return style
    }


    private fun createHeaderStyle(): HSSFCellStyle {
        val style = workbook.createCellStyle()
        val font = workbook.createFont()

        font.bold = true
        font.fontName = fontName
        style.setFont(font)

        style.alignment = HorizontalAlignment.CENTER
        style.verticalAlignment = VerticalAlignment.CENTER

        style.borderBottom = BorderStyle.MEDIUM
        style.borderLeft = BorderStyle.MEDIUM
        style.borderRight = BorderStyle.MEDIUM
        style.borderTop = BorderStyle.MEDIUM

        return style
    }


    private fun createTableContentStyle(
        isFirstRowAfterHeader: Boolean = false,
        isCenteredHorizontally: Boolean = false,
        isFontWeightBold: Boolean = false,
        isFontColorRed: Boolean = false
    ): HSSFCellStyle {
        val style = workbook.createCellStyle()
        val font = workbook.createFont()

        font.bold = isFontWeightBold
        font.fontName = fontName
        if(isFontColorRed) {
            font.color = IndexedColors.RED.index
        }
        style.setFont(font)

        if(isCenteredHorizontally) {
            style.alignment = HorizontalAlignment.CENTER
        }
        style.verticalAlignment = VerticalAlignment.CENTER

        style.borderBottom = BorderStyle.THIN
        style.borderLeft = BorderStyle.THIN
        style.borderRight = BorderStyle.THIN
        if(!isFirstRowAfterHeader) {
            style.borderTop = BorderStyle.THIN
        }

        return style
    }


    private fun getAmountNeededEmptyRows(): Int {
        return fixtures.map { it.date }.distinct().size - 1
    }


    /**
     * Generates an Excel-Export to an xls-file with the given list of FixturePlanner.
     * The file name is of this pattern: firstDate_lastDate-plannedRefereeFixtures.xls (including a unique identifier.
     * The file gets generated to the directory "output" (which gets created if it does not exist yet).
     * The fixture's data gets written into a row with a certain order.
     * TODO: Make almost everything adjustable
     * TODO: Give opportunity to read input from existing file and write data into that
     * @return String of the generated file name, else null
     */
    fun generateExcelExport(): String? {
        try {
            val formatterForName = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val formatterForCellValue = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val firstDate = fixtures.first().date!!.format(formatterForName)
            val lastDate = fixtures.last().date!!.format(formatterForName)

            val outputDirectory = File(AppConfig.excelExportOutputDirectoryName)
            if (!outputDirectory.exists() && !outputDirectory.isDirectory) {
                outputDirectory.mkdir()
            }

            val outputFileName = findUniqueOutputFileName(
                desiredFileName = firstDate + "_" + lastDate + "-exportierteSpiele.xls",
                directory = outputDirectory.name
            )

            // TODO: outsource to appconfig
            val headerOffset = 1
            val rowOffset = headerOffset + 1
            val columnOffset = 0

            val headerRow = sheet.createRow(headerOffset)
            for(columnIndex in 0..headerNames.lastIndex) {
                val cell = headerRow.createCell(columnIndex + columnOffset)
                val cellData = headerNames[columnIndex]

                cell.setCellValue(cellData)
                cell.cellStyle = headerStyle
                sheet.setColumnWidth(columnIndex + columnOffset, cellData.length * 256)
            }

            var previousFixtureDate: LocalDate? = null
            var currentAmountAlreadyCreatedEmptyRows = 0

            for (rowIndex in 0..fixtures.lastIndex + getAmountNeededEmptyRows()) {
                val fixture = fixtures[rowIndex - currentAmountAlreadyCreatedEmptyRows]
                val isFirstRowAfterHeader = rowIndex == 0
                val isEmptyRow = (previousFixtureDate != null && fixture.date != previousFixtureDate).also {
                    if(it) {
                        currentAmountAlreadyCreatedEmptyRows++
                    }
                }

                previousFixtureDate = fixture.date

                val row = sheet.createRow(rowIndex + rowOffset)

                for(columnIndex in 0..excelColumnIndices.gymNameIndex) {
                    val cell = row.createCell(columnIndex + columnOffset)
                    var cellData = ""
                    var cellStyle: HSSFCellStyle? = null

                    if(isEmptyRow) {
                        cellStyle = contentStyleNotFirstRowNotCenteredNotBold
                    } else {
                        when (columnIndex - columnOffset) {
                            excelColumnIndices.dateIndex -> {
                                cellData = fixture.date!!.format(formatterForCellValue)
                                cellStyle = if(isFirstRowAfterHeader) {
                                    contentStyleFirstRowCenteredNotBold
                                } else {
                                    contentStyleNotFirstRowCenteredNotBold
                                }
                            }

                            excelColumnIndices.timeIndex -> {
                                cellData = fixture.time.replace(" v", "").replace(" h", "")
                                cellStyle = if(isFirstRowAfterHeader) {
                                    contentStyleFirstRowCenteredNotBold
                                } else {
                                    contentStyleNotFirstRowCenteredNotBold
                                }
                            }

                            excelColumnIndices.leagueNameIndex -> {
                                cellData = fixture.league.leagueName
                                cellStyle = if(isFirstRowAfterHeader) {
                                    contentStyleFirstRowCenteredNotBold
                                } else {
                                    contentStyleNotFirstRowCenteredNotBold
                                }
                            }

                            excelColumnIndices.gameNumberIndex -> {
                                cellData = fixture.gameId
                                cellStyle = if(isFirstRowAfterHeader) {
                                    contentStyleFirstRowCenteredNotBold
                                } else {
                                    contentStyleNotFirstRowCenteredNotBold
                                }
                            }

                            excelColumnIndices.homeTeamIndex -> {
                                cellData = fixture.homeTeam.normalizeTeamName()
                                cellStyle = if(isFirstRowAfterHeader) {
                                    contentStyleFirstRowNotCenteredNotBold
                                } else {
                                    contentStyleNotFirstRowNotCenteredNotBold
                                }
                            }

                            excelColumnIndices.guestTeamIndex -> {
                                cellData = fixture.guestTeam.normalizeTeamName()
                                cellStyle = if(isFirstRowAfterHeader) {
                                    contentStyleFirstRowNotCenteredNotBold
                                } else {
                                    contentStyleNotFirstRowNotCenteredNotBold
                                }
                            }

                            excelColumnIndices.gymNameIndex -> {
                                cellData = fixture.gym.gymName.normalizeGymName()
                                if(fixture.gym != EMPTY_GYM) {
                                    val gymHyperLink = createHelper.createHyperlink(HyperlinkType.URL)
                                    gymHyperLink.address = fixture.gym.gymLink
                                    cell.hyperlink = gymHyperLink

                                    cellStyle = if(isFirstRowAfterHeader) {
                                        linkStyleFirstRowAfterHeader
                                    } else {
                                        linkStyle
                                    }
                                } else {
                                    cellStyle = if (isFirstRowAfterHeader) {
                                        contentStyleFirstRowCenteredRed
                                    } else {
                                        contentStyleNotFirstRowCenteredRed
                                    }
                                }
                            }
                        }
                    }

                    if(cellStyle != null) {
                        cell.setCellStyle(cellStyle)
                    }
                    cell.setCellValue(cellData)

                    val desiredNewLength = (cellData.length + cellWidthEnlargementToHaveResultLessSquished) * 256
                    if(sheet.getColumnWidth(columnIndex + columnOffset) < desiredNewLength) {
                        sheet.setColumnWidth(columnIndex + columnOffset, desiredNewLength)
                    }
                }
            }


            val fileOutputStream = FileOutputStream("${outputDirectory.name}\\$outputFileName")
            workbook.write(fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            workbook.close()

            return outputFileName
        } catch (e: Exception) {
            logger.error("Caught exception while generating Excel-export: ", e)
            return null
        }
    }
}


data class ExcelColumnIndices (
    val dateIndex: Int,
    val timeIndex: Int,
    val leagueNameIndex: Int,
    val gameNumberIndex: Int,
    val homeTeamIndex: Int,
    val guestTeamIndex: Int,
    val gymNameIndex: Int
)