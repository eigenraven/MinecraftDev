/*
 * Minecraft Development for IntelliJ
 *
 * https://mcdev.io/
 *
 * Copyright (C) 2024 minecraft-dev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, version 3.0 only.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.mcdev.obfex.facet.config

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.textFieldWithBrowseButton
import com.intellij.ui.table.JBTable
import com.intellij.util.IconUtil
import com.intellij.util.ui.AbstractTableCellEditor
import io.mcdev.obfex.facet.MappingTargetConfig
import io.mcdev.obfex.facet.ObfExFacetConfiguration
import io.mcdev.obfex.formats.MappingsFormatType
import io.mcdev.obfex.mappings.MappingsFile
import io.mcdev.obfex.mappings.MappingsFormatParser
import java.awt.Component
import java.net.URI
import java.net.URISyntaxException
import java.nio.file.Paths
import javax.swing.JComponent
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.table.AbstractTableModel
import javax.swing.table.DefaultTableCellRenderer

class MappingsTable(private val config: ObfExFacetConfiguration) {

    private val state: MutableList<MappingsTableRow> = configToState(config.state.mappingTargets).toMutableList()

    private val model: MappingsFileTableModel = MappingsFileTableModel(state)
    private val table: JBTable = createTable(model)
    val panel: JComponent = createPanel(table, model)

    fun stateToConfig(): List<MappingTargetConfig> {
        if (!table.isEditing) {
            return state.map { MappingTargetConfig(it.type, it.uri?.toString() ?: "") }
        }

        val value = table.cellEditor.cellEditorValue as? URI
        val currentRow = table.editingRow

        return state.mapIndexed { i, row ->
            if (i == currentRow) {
                // TODO file type
                MappingTargetConfig(null, value?.toString() ?: "")
            } else {
                MappingTargetConfig(row.type, row.uri?.toString() ?: "")
            }
        }
    }

    private fun configToState(list: List<MappingTargetConfig>): List<MappingsTableRow> {
        return list.map { value ->
            val (type, uri) = value
            if (type == null && uri.isNullOrBlank()) {
                MappingsTableRow.NULL
            } else {
                MappingsTableRow(type, if (uri.isNullOrBlank()) null else URI.create(uri))
            }
        }
    }

    fun reset() {
        state.clear()
        state.addAll(configToState(config.state.mappingTargets))

        table.clearSelection()
        model.fireTableDataChanged()
    }

    private class MappingsFileTableModel(val state: MutableList<MappingsTableRow>) : AbstractTableModel() {
        override fun getRowCount(): Int = state.size
        override fun getColumnCount(): Int = 1
        override fun getColumnClass(columnIndex: Int): Class<*> = URI::class.java
        override fun getColumnName(column: Int): String = "Mappings Files"

        override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean = true

        override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
            return state[rowIndex]
        }
        override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
            val uri = when (aValue) {
                is URI -> aValue
                is String -> aValue.toUriFromUserInput()
                else -> return
            }
            val type: MappingsFormatType? = uri?.let {
                val path = uri.path
                val matchedParsers = MappingsFormatParser.EP_NAME.extensions.filter { parser ->
                    parser.expectedFileExtensions.any { ext -> path.endsWith(ext) }
                }
                if (matchedParsers.isEmpty()) {
                    null
                } else if (matchedParsers.size == 1) {
                    // TODO Provide a separate optional API method for getting file type, for parsers which don't
                    //  need to do a full parse to determine type
                    // TODO Make a util for this since it's repeated here
                    (matchedParsers.single().parse(toVirtualFile(uri))?.source as? MappingsFile)?.type
                } else {
                    val file = toVirtualFile(uri)
                    val allMatching = matchedParsers.filterNot { parser ->
                        parser.isSupportedFile(file)
                    }
                    if (allMatching.isEmpty()) {
                        null
                    } else if (allMatching.size == 1) {
                        (allMatching.single().parse(toVirtualFile(uri))?.source as? MappingsFile)?.type
                    } else {
                        logger<MappingsFileTableModel>().warn(
                            "Multiple support mappings parsers found for URI: " +
                                "$uri; choosing first registered"
                        )
                        (allMatching.first().parse(toVirtualFile(uri))?.source as? MappingsFile)?.type
                    }
                }
            }
            state[rowIndex] = MappingsTableRow(type, uri)
        }

        private fun toVirtualFile(uri: URI): VirtualFile {
            // This needs to handle http uris as well - probably by downloading to the .idea directory or something maybe
            // need to have an external file manager for handling that, keep track of etags, redownload on change, etc
            TODO()
        }
    }

    private class Editor(
        private val model: MappingsFileTableModel,
        private val state: MutableList<MappingsTableRow> = model.state,
    ) : AbstractTableCellEditor() {
        private val textField = textFieldWithBrowseButton(null, "Mappings Files", fileDesc, null)
        private var row: Int = -1

        override fun getCellEditorValue(): Any? {
            return textField.text.toUriFromUserInput()
        }

        override fun getTableCellEditorComponent(
            table: JTable?,
            value: Any?,
            isSelected: Boolean,
            row: Int,
            column: Int
        ): Component {
            this.row = row
            textField.text = when (value) {
                is URI -> value.toPresentableText()
                else -> ""
            }
            return textField
        }

        override fun stopCellEditing(): Boolean {
            if (row != -1 && state.size > row) {
                val uri = textField.text.toUriFromUserInput()
                state[row] = MappingsTableRow(null, uri)
                model.fireTableCellUpdated(row, 0)
            }
            textField.text = ""
            return super.stopCellEditing()
        }
    }

    private object Renderer : DefaultTableCellRenderer() {
        private fun readResolve(): Any = Renderer

        override fun getTableCellRendererComponent(
            table: JTable?,
            value: Any?,
            isSelected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int
        ): Component {
            val field = JBTextField()
            field.text = when (value) {
                is URI -> value.toPresentableText()
                is String -> value
                else -> ""
            }
            field.isEditable = false

            return field
        }
    }

    companion object {
        private fun createTable(model: MappingsFileTableModel): JBTable {
            val table = JBTable(model)

            table.setShowGrid(true)
            table.setEnableAntialiasing(true)

            table.emptyText.text = "No mappings files"

            table.visibleRowCount = 8

            table.setDefaultRenderer(URI::class.java, Renderer)
            table.setDefaultEditor(URI::class.java, Editor(model))

            table.selectionModel.selectionMode = ListSelectionModel.SINGLE_SELECTION

            return table
        }

        private fun createPanel(table: JBTable, model: MappingsFileTableModel): JComponent {
            val state = model.state

            return ToolbarDecorator.createDecorator(table)
                .disableUpDownActions()
                .setAddAction {
                    val initial = state.size

                    val chosenFiles = FileChooser.chooseFiles(fileDesc, null, null)
                    for (chosenFile in chosenFiles) {
                        state.add(MappingsTableRow(null, chosenFile.toNioPath().toUri()))
                    }
                    model.fireTableRowsInserted(initial, state.lastIndex)
                }
                .setAddIcon(IconUtil.addPackageIcon)
                .setAddActionName("Add File")
                .addExtraAction(object : AnAction(
                    "Add Text",
                    "Create blank entry for manual input",
                    IconUtil.addBlankLineIcon
                ) {
                        override fun actionPerformed(e: AnActionEvent) {
                            state.add(MappingsTableRow.NULL)
                            if (table.isEditing) {
                                table.cellEditor.stopCellEditing()
                            }
                            model.fireTableRowsInserted(state.lastIndex - 1, state.lastIndex)

                            IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown {
                                IdeFocusManager.getGlobalInstance().requestFocus(table, true)

                                table.changeSelection(state.lastIndex, 0, false, false)
                                table.editCellAt(state.lastIndex, 0)
                            }
                        }
                    })
                .setRemoveAction {
                    val selected = table.selectedRow
                    if (selected == -1) {
                        return@setRemoveAction
                    }
                    if (table.isEditing) {
                        table.cellEditor?.stopCellEditing()
                    }

                    state.removeAt(selected)
                    model.fireTableRowsDeleted(selected, selected)

                    IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown {
                        IdeFocusManager.getGlobalInstance().requestFocus(table, true)
                    }
                }
                .setButtonComparator("Add File", "Add Text", "Remove")
                .createPanel()
        }

        private fun URI.toPresentableText(): String {
            if (scheme == "file") {
                return Paths.get(this).toAbsolutePath().toString()
            }
            return toString()
        }

        private fun String.toUriFromUserInput(): URI? {
            if (isBlank()) {
                return null
            }
            val uri = try {
                URI(this)
            } catch (_: URISyntaxException) {
                return null
            }
            if (uri.scheme == null) {
                // plain file path
                return Paths.get(this).toUri()
            }
            return uri
        }

        private val fileDesc = FileChooserDescriptor(true, false, false, false, false, true)
    }
}
