package com.spw.rr

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor

import javax.annotation.Nonnull
import javax.swing.JTable
import javax.swing.ListSelectionModel

@ArtifactProviderFor(GriffonView)
class HelpView {

    @MVCMember
    @Nonnull
    FactoryBuilderSupport builder

    @MVCMember
    @Nonnull
    HelpModel model

    RRTableModel tableModel
    def completeTable

    void initUI() {
        builder.with {
            application(size: [1200, 900], id: 'helpWindow', title: 'Decoder Help')
            menuBar() {
                menu(text: 'File') {
                    menuItem closeAction, text: 'Close'
                }
                menu(text: 'Help') {
                    menuItem helpAction, text: 'Help'
                }
            }
            migLayout(constraints: 'fill')
            def pane = scrollPane(constraints: 'width 1200, height 900') {

            }
        }
    }
}
