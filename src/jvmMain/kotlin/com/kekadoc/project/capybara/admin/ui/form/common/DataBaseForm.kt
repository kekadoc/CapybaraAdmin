@file:OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterialApi::class,
)

package com.kekadoc.project.capybara.admin.ui.form.common

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Update
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Composable
fun DatabaseForm(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    columns: Int,
    count: Int,
    getHeader: (column: Int) -> String,
    getText: (index: Int, column: Int) -> String,
    onDelete: (index: Int) -> Unit,
    onEdit: (index: Int) -> Unit,
) {
    val horizontalScrollState = rememberScrollState()
    val scrollAdapter = rememberScrollbarAdapter(horizontalScrollState)

    var deleteIndex: Int? by remember { mutableStateOf(null) }

    deleteIndex?.let { index ->
        DeleteConfirmation(
            onDismissRequest = { deleteIndex = null },
            onConfirmRequest = {
                deleteIndex = null
                onDelete.invoke(index)
            }
        )
    }

    Box(
        modifier = modifier
            .run {
                if (isLoading) background(Color.Black.copy(alpha = 0.33f))
                else this
            },
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) CircularProgressIndicator()
        Column(
            modifier = Modifier,
        ) {
            HorizontalDatabaseContent(
                modifier = Modifier.weight(1f),
                horizontalScrollState = horizontalScrollState,
                columns = columns,
                count = count,
                isEnabled = !isLoading,
                getHeader = getHeader,
                getText = getText,
                onDelete = onDelete,
                onEdit = onEdit,
            )
            HorizontalScrollbar(
                modifier = Modifier.wrapContentSize(),
                adapter = scrollAdapter,
                style = LocalScrollbarStyle.current.copy(
                    thickness = 16.dp,
                )
            )
        }
    }

}

@Composable
private fun HorizontalDatabaseContent(
    modifier: Modifier = Modifier,
    horizontalScrollState: ScrollState,
    columns: Int,
    count: Int,
    isEnabled: Boolean,
    getHeader: (column: Int) -> String,
    getText: (row: Int, column: Int) -> String,
    onDelete: (index: Int) -> Unit,
    onEdit: (index: Int) -> Unit,
) {
    val verticalScrollState = rememberLazyListState()
    Row(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .horizontalScroll(horizontalScrollState),
        ) {
            DatabaseHeaders(
                columns = columns,
                getHeader = getHeader,
            )
            LazyColumn(
                modifier = Modifier
                    .weight(1f),
                state = verticalScrollState,
            ) {
                items(
                    count = count,
                    key = { it },
                ) { index ->
                    DatabaseRow(
                        modifier = Modifier
                            .wrapContentSize()
                            .height(80.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                            ),
                        cellContent = { column ->
                            Cell(
                                modifier = Modifier.fillMaxHeight(),
                                text = getText.invoke(index, column),
                                isEnabled = isEnabled,
                            )
                        },
                        rowIndex = index,
                        columnsCount = columns,
                        isEnabled = isEnabled,
                        onEdit = { onEdit.invoke(index) },
                        onDelete = { onDelete.invoke(index) },
                    )
                }
            }
        }
        VerticalScrollbar(
            modifier = Modifier,
            adapter = rememberScrollbarAdapter(verticalScrollState),
        )
    }
}

@Composable
private fun DatabaseRow(
    rowIndex: Int,
    columnsCount: Int,
    isEnabled: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    cellContent: @Composable (column: Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.width(45.dp),
            text = rowIndex.toString(),
            textAlign = TextAlign.Center,
        )
        IconButton(
            enabled = isEnabled,
            onClick = onEdit,
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.Default.Edit,
                contentDescription = null,
            )
        }
        IconButton(
            enabled = isEnabled,
            onClick = onDelete,
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = null,
            )
        }
        repeat(columnsCount) { column ->
            cellContent.invoke(column)
        }
    }
}

@Composable
private fun DatabaseHeaders(
    columns: Int,
    getHeader: (column: Int) -> String,
) {
    Row(
        modifier = Modifier.height(45.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .width(141.dp)
                .fillMaxHeight()
                .border(1.dp, Color.Black),
            text = "",
        )
        repeat(columns) {
            Cell(
                modifier = Modifier.fillMaxHeight(),
                text = getHeader.invoke(it),
                isEnabled = false,
            )
        }
    }
}

@Composable
private fun Cell(
    modifier: Modifier = Modifier,
    text: String,
    isEnabled: Boolean,
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = LocalSanckBarHostState.current
    Box(
        modifier = Modifier
            .width(120.dp)
            .border(1.dp, Color.Black)
            .padding(4.dp)
            .then(modifier)
            .onClick(enabled = isEnabled) {
                coroutineScope.launch {
                    val selection = StringSelection(text)
                    Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
                    snackbarHostState.showSnackbar("Скопировано")
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier,
            text = text,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun DeleteConfirmation(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = onConfirmRequest) {
                Text(text = "Да")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text(text = "Отмена")
            }
        },
        title = {
            Text("Удаление")
        },
        text = {
            Text("Удаление")
        },
    )
}