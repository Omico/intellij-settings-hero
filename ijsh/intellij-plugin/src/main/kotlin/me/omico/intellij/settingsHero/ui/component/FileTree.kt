// Copyright 2023-2024 Omico
// SPDX-License-Identifier: GPL-3.0-only
package me.omico.intellij.settingsHero.ui.component

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileSystemTree
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import java.awt.Dimension
import javax.swing.JTree

fun Row.fileTree(
    modifier: Cell<JTree>.() -> Unit = {},
    parentDisposable: Disposable,
    fileChooserDescriptor: FileChooserDescriptor,
    popupGroup: (DefaultActionGroup.(fileSystemTree: FileSystemTree) -> Unit)? = null,
    okAction: ((selectedFile: VirtualFile) -> Unit)? = null,
) {
    FileTree(parentDisposable, fileChooserDescriptor)
        .apply {
            if (okAction == null) return@apply
            addOkAction { selectedFile?.let(okAction) }
        }
        .apply {
            if (popupGroup == null) return@apply
            val fileSystemTree = this
            DefaultActionGroup
                .createPopupGroup { "FileTreePopupMenu" }
                .apply { popupGroup(fileSystemTree) }
                .let(::registerMouseListener)
        }
        .tree
        .apply { minimumSize = Dimension(300, minimumSize.height) }
        .let(::cell)
        .apply(modifier)
}

private class FileTree(
    parentDisposable: Disposable,
    fileChooserDescriptor: FileChooserDescriptor,
) : FileSystemTreeImpl(null, fileChooserDescriptor) {
    init {
        Disposer.register(parentDisposable, this)
    }
}
