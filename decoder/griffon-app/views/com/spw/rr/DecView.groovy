package com.spw.rr

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.Nonnull
import javax.swing.JTable
import javax.swing.ListSelectionModel
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.util.regex.Matcher
import java.util.regex.Pattern

@ArtifactProviderFor(GriffonView)
class DecView implements FocusListener {
    @MVCMember
    @Nonnull
    FactoryBuilderSupport builder
    @MVCMember
    @Nonnull
    DecModel model

    RRTableModel tableModel
    def completeTable



    private static final Logger log = LoggerFactory.getLogger(DecView.class)

    void initUI() {
        JTable theTable
        RRTableModel modelCopy
        builder.with {
            application(size: [1200, 900], id: 'decWindow',
                    title: 'Decoder List') {

                menuBar() {
                    menu(text: 'File') {
                        menuItem importDetailAction, text: 'Import Details', enabled: bind { model.tableSelectionEnabled }
                    }
                    menu(text: 'Window') {
                        menuItem mainAction

                    }

                    menu(text: 'Filters') {
                        menuItem resetFiltersAction, text: "Display all", enabled: bind { model.enableResetFilters }
                        menuItem speedWithAction, text: '    With Speed Profile', enabled: bind { model.filterSpeed }
                        menuItem speedWithoutAction, text: '    Without Speed Profile', enabled: bind { model.filterNoSpeed }
                        menuItem detailsWithAction, text: '    With Imported details', enabled: bind { model.filterDetails }
                        menuItem detailsWithoutAction, text: '    Without Imported Details', enabled: bind { model.filterNoDetails }
                    }

                    menu(text: 'View') {
                        menuItem viewSpeedProfilesAction, text: 'View Speed Profiles', enabled: bind { model.tableSelectionEnabled }
                        menuItem graphSpeedProfileAction, text: 'Graph Speed Profiles', enabled: bind { model.tableSelectionEnabled }
                        menuItem viewCVvaluesAction, text: 'View CV contents', enabled: bind { model.tableSelectionEnabled }
                        menuItem viewDecoderDetailAction, text: 'View Decoder Details', enabled: bind { model.tableSelectionEnabled }
                        menuItem viewSelectedCVAction, text: 'View Selected CV Details', enabled: bind { model.enableCVdetail }
                    }
                    menu(text: 'Help') {
                        menuItem helpAction
                    }

                }
                migLayout(constraints: 'fill')
                def pane = scrollPane(constraints: 'width 1200, height 900, wrap') {
                    theTable = table(model: modelCopy = new RRTableModel((RRBaseModel) model))
                    log.debug("theTable is ${theTable}")
                    log.debug("modelCopy is ${modelCopy}")
                    tableModel = modelCopy
                }
                panel(constraints: "h 16pt") {
                    migLayout(constraints: 'fillx')
                    label("CV's to display ID: ,", constraints: "align left")
                    textField(text: bind("cvDisplay", source: model, mutual: true),
                            id: "cvText",
                            columns: 100)
                }
                //pane.setPreferredSize(new Dimension(1500,600))
                builder.cvText.addFocusListener(this)
            }
        }
        tableModel = modelCopy
        completeTable = theTable
        model.theTable = theTable
        completeTable.setAutoCreateRowSorter(true)
        theTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        theTable.setCellSelectionEnabled(false)
        theTable.setCellSelectionEnabled(false)
        theTable.setRowSelectionAllowed(true)
        theTable.getSelectionModel().addListSelectionListener(new ListenerForTables(model))

    }

    @Override
    void focusGained(FocusEvent e) {

    }

    @Override
    void focusLost(FocusEvent e) {
        log.debug("lost the focus in the CV text field")
        model.enableCVdetail = false
        if (model.tableSelectionEnabled) {
            Pattern regexPattern = Pattern.compile("[\\d]{1,4}(?:\\.[\\d]{1,3}){0,2}(?:\\,\\s{0,2}[\\d]{1,4}(?:\\.[\\d]{1,3}){0,2}){0,15}+\$")
            Matcher match = regexPattern.matcher(model.cvDisplay)
            if (match.matches()) {
                model.enableCVdetail = true
            }
        }
    }
}
